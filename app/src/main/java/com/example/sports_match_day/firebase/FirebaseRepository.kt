package com.example.sports_match_day.firebase

import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.network.Match
import com.google.firebase.database.*


/**
 * Created by Kristo on 08-Mar-21
 */
class FirebaseRepositoryImpl : FirebaseRepository {
    //The last id we downloaded. (Pagination offset)
    private var offset = 0.0
    override fun getMatchesInitial(count: Int, onComplete: (List<Match>?) -> Unit) {
        offset = 0.0
        getMatches(count,onComplete)
    }

    override fun getMatches(count: Int, onComplete: (List<Match>?) -> Unit) {
        FirebaseDatabase.getInstance().reference
            .child("matches")
            .orderByChild("id")
            .startAt(offset)
            .limitToFirst(count)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    FirebaseDatabase.getInstance().reference
                        .child("matches")
                        .removeEventListener(this)
                    //throw error.toException()
                }

                /**
                 * Firebase returns a list of items if the count is > 1
                 * if the count is 1, firebase returns a HashMap with key the position of the item
                 * and value the actual data.
                 */
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.childrenCount > 1) {
                        val genericTypeIndicator = object : GenericTypeIndicator<List<Match>?>() {}
                        val data = dataSnapshot.getValue(genericTypeIndicator)

                        data?.findLast { true }?.let {
                            offset = (it.id + 1).toDouble()
                        }

                        onComplete.invoke(data)
                    } else {
                        val genericTypeIndicator =
                            object : GenericTypeIndicator<HashMap<String, Match>>() {}
                        val data = dataSnapshot.getValue(genericTypeIndicator)

                        val list = mutableListOf<Match>()
                        data?.values?.first()?.let {
                            list.add(it)
                            offset = (it.id + 1).toDouble()
                        }

                        onComplete.invoke(list)
                    }

                    FirebaseDatabase.getInstance().reference
                        .child("matches")
                        .removeEventListener(this)
                }
            })
    }

    override fun removeMatch(matchId: Int) {

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("$matchId")

            .removeValue { error, ref ->
                print("ok")
            }
    }

    override fun addMatch(city: String, country: String, sportId: Int, date: Long, participants: List<Participant>){
        val firebaseMatch = HashMap<String, Any>()
        firebaseMatch["city"] = city
        firebaseMatch["country"] = country
        firebaseMatch["sportId"] = sportId
        firebaseMatch["id"] = 5

        val listParticipants = mutableListOf<HashMap<String,Any>>()
        participants.forEach {
            val map = hashMapOf<String,Any>()
            map["id"] = it.participant?.id ?: 0
            map["score"] = it.score

            listParticipants.add(map)
        }

        firebaseMatch["participants"] = listParticipants

        FirebaseDatabase.getInstance().reference
            .child("matches")
            .child("5")
            .setValue(firebaseMatch
            ) { error, ref ->
                print("ok")
            }
    }
}

interface FirebaseRepository {

    fun getMatchesInitial(
        count: Int,
        onComplete: (List<Match>?) -> Unit
    )

    fun getMatches(
        count: Int,
        onComplete: (List<Match>?) -> Unit
    )

    fun removeMatch(matchId: Int)

    fun addMatch(city: String, country: String, sportId: Int, date: Long, participants: List<Participant>)
}

