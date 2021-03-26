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
        try {
            //Exclude the match with id == offsetId by added 1.0
            val offset = offsetId?.plus(1) ?: 0
            val dataSnapshot = FirebaseDatabase.getInstance().reference
                .child("matches")
                .orderByKey()
                .startAt("$offset")
                //.startAt(offset)
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
        } catch (t: Throwable) {
            //get() randomly closes connection firebase issue
            if (t.message == "Client is offline") {
                FirebaseDatabase.getInstance().goOffline()
                FirebaseDatabase.getInstance().goOnline()
            }
            throw t
        }
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
        date: Long,
        participants: List<Participant>
    ) {
        val firebaseMatch = HashMap<String, Any>()
        val matchId = getLastId() + 1
        firebaseMatch["city"] = city
        firebaseMatch["country"] = country
        firebaseMatch["sportId"] = sportId
        firebaseMatch["date"] = date
        firebaseMatch["id"] = matchId

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
            .child("$matchId")
            .setValue(
                firebaseMatch
            ).await()

        updateLastId(matchId)
    }
}

interface FirebaseRepository {

    /**
     *
     * @param count How many matches will be returned.
     * @param offsetId The last match id.
     */
    suspend fun getMatches(count: Int, offsetId: Int?): List<Match>?

    suspend fun removeMatch(matchId: Int)

    suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>
    )
}

