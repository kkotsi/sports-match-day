package com.example.sports_match_day.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber

/**
 * Created by Kristo on 01-Apr-21
 */
class MessageService : FirebaseMessagingService(), KoinComponent {

    override fun onNewToken(token: String) {
        Timber.d("Successfully registered for notifications")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
       val notificationHandler = get<NotificationHandler>()
        notificationHandler.showNotification(remoteMessage)
    }
}