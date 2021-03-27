package com.example.sports_match_day.firebase

import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.network.Match
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await

/**
 * Created by Kristo on 08-Mar-21
 */
class FirebaseRepositoryImpl : FirebaseRepository {

    private suspend fun getLastId(): Int {
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child("max_id")
            .awaitQueryValue()

        return dataSnapshot.value.toString().toInt()
    }

    private suspend fun updateLastId(id: Int) {
        FirebaseDatabase.getInstance().reference
            .child("max_id")
            .setValue(id)
            .await()
    }

    override suspend fun getMatches(count: Int, offsetId: Int?): List<Match>? {
        //Exclude the match with id == offsetId by added 1.0
        val offset = offsetId?.plus(1) ?: 0
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child("matches")
            .orderByKey()
            .startAt("$offset")
            .limitToFirst(count)
            .awaitQueryValue()

        /**
         * Firebase returns a list of items if the count is > 1
         * if the count is 1, firebase returns a HashMap with key the position of the item
         * and value the actual data.
         */
        if (dataSnapshot.childrenCount > 1) {
            if (dataSnapshot.value is ArrayList<*>) {
                val genericTypeIndicator = object : GenericTypeIndicator<List<Match>?>() {}
                return dataSnapshot.getValue(genericTypeIndicator)
            } else if (dataSnapshot.value is HashMap<*, *>) {
                val genericTypeIndicator =
                    object : GenericTypeIndicator<HashMap<String, Match>?>() {}
                return dataSnapshot.getValue(genericTypeIndicator)?.values?.toList()
            }
        } else if (dataSnapshot.childrenCount > 0) {
            val genericTypeIndicator =
                object : GenericTypeIndicator<HashMap<String, Match>>() {}
            val data = dataSnapshot.getValue(genericTypeIndicator)

            val list = mutableListOf<Match>()
            data?.values?.first()?.let {
                list.add(it)
            }
            return list
        }

        return null
    }

    override suspend fun getMatch(matchId: Int): Match? {
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("$matchId")
            .awaitQueryValue()

        val genericTypeIndicator =
            object : GenericTypeIndicator<Match>() {}
        return dataSnapshot.getValue(genericTypeIndicator)
    }

    override suspend fun removeMatch(matchId: Int) {

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("$matchId")
            .removeValue()
    }

    override suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        stadium: String,
        date: Long,
        participants: List<Participant>
    ) {
        val matchId = getLastId() + 1
        setMatch(matchId, city, country, stadium, sportId, date, participants)
        updateLastId(matchId)
    }

    override suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>
    ) {
        setMatch(id, city, country, stadium, sportId, date, participants)
    }

    private suspend fun setMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>
    ) {
        val firebaseMatch = HashMap<String, Any>()
        firebaseMatch["city"] = city
        firebaseMatch["country"] = country
        firebaseMatch["sportId"] = sportId
        firebaseMatch["stadium"] = stadium
        firebaseMatch["date"] = date
        firebaseMatch["id"] = id

        val listParticipants = mutableListOf<HashMap<String, Any>>()
        participants.forEach {
            val map = hashMapOf<String, Any>()
            map["id"] = it.contestant!!.id
            map["score"] = it.score

            listParticipants.add(map)
        }

        firebaseMatch["participants"] = listParticipants

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("$id")
            .setValue(
                firebaseMatch
            ).await()
    }
}

interface FirebaseRepository {

    /**
     *
     * @param count How many matches will be returned.
     * @param offsetId The last match id.
     */
    suspend fun getMatches(count: Int, offsetId: Int?): List<Match>?
    suspend fun getMatch(matchId: Int): Match?
    suspend fun removeMatch(matchId: Int)
    suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        stadium: String,
        date: Long,
        participants: List<Participant>
    )

    suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>
    )
}

