@file:Suppress("UNCHECKED_CAST")

package com.example.sports_match_day.firebase

import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.network.Match
import com.example.sports_match_day.ui.base.ErrorUserIdNotFound
import com.example.sports_match_day.ui.base.SportsDebugError
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.tasks.await

/**
 * Created by Kristo on 08-Mar-21
 */
class FirebaseRepositoryImpl : FirebaseRepository {

    private suspend fun getLastId(): Int {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child(userId)
            .child("max_id")
            .awaitQueryValue()

        return dataSnapshot.value.toString().toInt()
    }

    private suspend fun updateLastId(id: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        FirebaseDatabase.getInstance().reference
            .child(userId)
            .child("max_id")
            .setValue(id)
            .await()
    }

    override suspend fun getMatches(count: Int, offsetId: Int?): List<Match> {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        if(id.isBlank()){
            throw NullPointerException("The user id cannot be null or black!")
        }

        //Exclude the match with id == offsetId by adding 1.0
        val offset = offsetId?.plus(1) ?: 0
        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child(id)
            .child("matches")
            .orderByChild("id")
            .startAt(offset.toDouble())
            .limitToFirst(count)
            .awaitQueryValue()

        return getMatches(dataSnapshot)
    }

    override suspend fun getMatches(): List<Match> {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        if(id.isBlank()){
            throw NullPointerException("The user id cannot be null or black!")
        }

        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child(id)
            .child("matches")
            .awaitQueryValue()

        return getMatches(dataSnapshot)
    }

    override suspend fun getMatch(matchId: Int): Match? {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        if(id.isBlank()){
            throw NullPointerException("The user id cannot be null or black!")
        }

        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child(id)
            .child("matches")
            .child("match$matchId")
            .awaitQueryValue()

        val genericTypeIndicator =
            object : GenericTypeIndicator<Match>() {}
        return dataSnapshot.getValue(genericTypeIndicator)
    }

    override suspend fun removeMatch(matchId: Int) {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        if(id.isBlank()){
            throw NullPointerException("The user id cannot be null or black!")
        }

        FirebaseDatabase.getInstance().reference
            .child(id)
            .child("matches")
            .child("match$matchId")
            .removeValue()
            .await()
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

       val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
       if(id.isBlank()){
           throw NullPointerException("The user id cannot be null or black!")
       }

       FirebaseDatabase.getInstance().reference
           .child(id)
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
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ): Int {

        if(Prefs.getBoolean(PreferencesKeys.TEST_ERROR, false)){
            Prefs.putBoolean(PreferencesKeys.TEST_ERROR, false)
            throw SportsDebugError("Couldn't save the match. This is a test crash")
        }

        val matchId = getLastId() + 1
        setMatch(matchId, city, country, stadium, sportId, date, participants, stadiumLocation)
        updateLastId(matchId)
        return matchId
    }

    private fun getMatches(dataSnapshot: DataSnapshot): List<Match>{
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
        return mutableListOf()
    }

    override suspend fun removeMatchesBySport(sportId: Int): List<Match> {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        if(id.isBlank()){
            throw NullPointerException("The user id cannot be null or black!")
        }

        val dataSnapshot = FirebaseDatabase.getInstance().reference
            .child(id)
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
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ) {
        setMatch(id, city, country, stadium, sportId, date, participants, stadiumLocation)
    }

    override suspend fun signUp(data: HashMap<String,Match>) {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()

        val userData = hashMapOf<String, Any>()
        userData["matches"] = data
        userData["max_id"] = "100"
        FirebaseDatabase.getInstance().reference
            .child(id)
            .setValue(userData)
            .await()
    }

    private suspend fun setMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ) {
        val firebaseMatch = HashMap<String, Any>()
        firebaseMatch["city"] = city
        firebaseMatch["country"] = country
        firebaseMatch["sportId"] = sportId
        firebaseMatch["stadium"] = stadium
        firebaseMatch["date"] = date
        firebaseMatch["id"] = id
        stadiumLocation?.let{
            val mapStadiumLocation = HashMap<String, Double>()
            mapStadiumLocation["latitude"] = it.latitude
            mapStadiumLocation["longitude"] = it.longitude
            firebaseMatch["stadiumLocation"] = mapStadiumLocation
        }

        val mapParticipants = HashMap<String, Any>()

        //This will fix the weird firebase order.
        var count = participants.size
        participants.forEach {
            val map = hashMapOf<String, Any>()
            val partId = it.contestant!!.id
            map["participantId"] = partId
            map["score"] = it.score

            mapParticipants["participant$count"] = map
            count--
        }

        firebaseMatch["participants"] = mapParticipants

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw ErrorUserIdNotFound()
        if(userId.isBlank()){
            throw NullPointerException("The user id cannot be null or black!")
        }

        FirebaseDatabase.getInstance().reference
            .child(userId)
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
    suspend fun getMatches(count: Int, offsetId: Int?): List<Match>
    suspend fun getMatches(): List<Match>
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
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ): Int

    suspend fun removeMatchesBySport(sportId: Int): List<Match>
    suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: Long,
        participants: List<Participant>,
        stadiumLocation: LatLng?
    )

    suspend fun signUp(data: HashMap<String, Match>)
}

