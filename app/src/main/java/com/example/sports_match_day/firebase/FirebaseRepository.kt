package com.example.sports_match_day.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by Kristo on 08-Mar-21
 */
class FirebaseRepositoryImpl: FirebaseRepository {
    override fun getMatches(
        onComplete: (Any?) -> Unit
    ) {
        FirebaseDatabase.getInstance().reference
            .child("matches")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    FirebaseDatabase.getInstance().reference
                        .child("matches")
                        .removeEventListener(this)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    onComplete.invoke(dataSnapshot.value)
                    FirebaseDatabase.getInstance().reference
                        .child("matches")
                        .removeEventListener(this)
                }
            })
    }
}

interface FirebaseRepository{
    fun getMatches(
        onComplete: (Any?) -> Unit
    )
}

