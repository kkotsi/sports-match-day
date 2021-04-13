package com.example.sports_match_day.firebase

import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.network.Match
import com.google.firebase.database.DataSnapshot
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
            .orderByChild("id")
            .startAt(offset.toDouble())
            .limitToFirst(count)
            .awaitQueryValue()

        return getMatches(dataSnapshot)
    }

    override suspend fun getMatch(matchId: Int): Match? {
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("match$matchId")
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

    private suspend fun removeByNames(matchIds: List<String>) {
        val removeMatches = mutableMapOf<String, Any?>()
        matchIds.forEach {
            removeMatches["/matches/$it"] = null
        }
        FirebaseDatabase.getInstance().reference
            .updateChildren(removeMatches)
            .await()

    }

   private suspend fun removeByIds(matchIds: List<Int>): List<Match> {
       val removeMatches = mutableMapOf<String, Any?>()
       matchIds.forEach {
           removeMatches["/matches/match$it"] = null
       }
       val matches = mutableListOf<Match>()
       matchIds.forEach {
           getMatch(it)?.let{
               matches.add(it)
           }
       }
       FirebaseDatabase.getInstance().reference
           .updateChildren(removeMatches)
           .await()

       return matches
   }

    override suspend fun removeMatchByAthlete(matchIds: List<Int>): List<Match> {
        return removeByIds(matchIds)
    }

    override suspend fun removeMatchBySquad(matchIds: List<Int>): List<Match> {
        return removeByIds(matchIds)
    }

    override suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        stadium: String,
        date: Long,
        participants: List<Participant>
    ): Int {
        val matchId = getLastId() + 1
        setMatch(matchId, city, country, stadium, sportId, date, participants)
        updateLastId(matchId)
        return matchId
    }

    private fun getMatches(dataSnapshot: DataSnapshot): List<Match>?{
        /**
         * Firebase returns a list of items if the count is > 1
         * if the count is 1, firebase returns a HashMap with key the position of the item
         * and value the actual data.
         */
        if (dataSnapshot.childrenCount > 1) {
            val list = mutableListOf<Match>()
            dataSnapshot.children.forEach { matchDataSnapshot ->

                val genericTypeIndicator =
                object : GenericTypeIndicator<Match>() {}
                val data = matchDataSnapshot.getValue(genericTypeIndicator)

                data?.let {
                    list.add(it)
                }
            }
            return list
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

    override suspend fun removeMatchesBySport(sportId: Int): List<Match>? {
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child("matches")
            .orderByChild("sportId")
            .equalTo(sportId.toDouble())
            .awaitQueryValue()

        removeMatch(dataSnapshot)
        return getMatches(dataSnapshot)
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

        val mapParticipants = HashMap<String, Any>()
        participants.forEach {
            val map = hashMapOf<String, Any>()
            val partId = it.contestant!!.id
            map["participantId"] = partId
            map["score"] = it.score

            mapParticipants["participant$partId"] = map
        }

        firebaseMatch["participants"] = mapParticipants

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("match$id")
            .setValue(
                firebaseMatch
            ).await()
    }

    /**
     * Finds the id from the dataSnapshot and deletes the match.
     */
    private suspend fun removeMatch(dataSnapshot: DataSnapshot) {
        when {
            dataSnapshot is HashMap<*, *> -> {
                val id = dataSnapshot["id"].toString()
                removeByNames(listOf(id))
            }
            dataSnapshot.value is HashMap<*, *> -> {
                removeByNames((dataSnapshot.value as HashMap<String, *>).keys.toList())
            }
        }
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
    suspend fun removeMatchByAthlete(matchIds: List<Int>): List<Match>
    suspend fun removeMatchBySquad(matchIds: List<Int>): List<Match>
    suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        stadium: String,
        date: Long,
        participants: List<Participant>
    ): Int

    suspend fun removeMatchesBySport(sportId: Int): List<Match>?
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

