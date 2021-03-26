package com.example.sports_match_day.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by Kristo on 26-Mar-21
 */


/**
 * //https://github.com/firebase/firebase-android-sdk/issues/2385#issuecomment-770370777
 * Temporary fix until firebase fixes the issue with the get() function
 */
suspend inline fun Query.awaitQueryValue() : DataSnapshot = suspendCancellableCoroutine { continuation ->
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            continuation.resume(snapshot)
        }
        override fun onCancelled(error: DatabaseError) {
            continuation.resumeWithException(Throwable(error.message))
        }
    })
}