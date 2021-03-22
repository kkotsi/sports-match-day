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
                .get().await()

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
            //If the connection to wifi is briefly lost firebase seems to be unable to reconnect.
            if(t.message == "Client is offline") {
                FirebaseDatabase.getInstance().goOffline()
                FirebaseDatabase.getInstance().goOnline()
            }
            throw t
        }
    }

    override fun removeMatch(matchId: Int) {

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("$matchId")

            .removeValue { error, ref ->
                print("ok")
            }
    }

    override fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>
    ) {
        val firebaseMatch = HashMap<String, Any>()
        firebaseMatch["city"] = city
        firebaseMatch["country"] = country
        firebaseMatch["sportId"] = sportId
        firebaseMatch["id"] = 5

        val listParticipants = mutableListOf<HashMap<String, Any>>()
        participants.forEach {
            val map = hashMapOf<String, Any>()
            map["id"] = it.participant?.id ?: 0
            map["score"] = it.score

            listParticipants.add(map)
        }

        firebaseMatch["participants"] = listParticipants

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("5")
            .setValue(
                firebaseMatch
            ) { error, ref ->
                print("ok")
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

    fun removeMatch(matchId: Int)

    fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>
    )
}

