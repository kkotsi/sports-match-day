package com.example.sports_match_day.firebase

import android.annotation.TargetApi
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Kristo on 01-Apr-21
 */
class NotificationHandler(
    context: Context,
    private val notificationFactory: NotificationFactory
) {

    private var mNotificationId = 0

    private enum class ChannelName {
        DEFAULT
    }

    private var mNotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager


    fun showNotification(message: RemoteMessage) {
        val notification =
            notificationFactory.createNotification(message, ChannelName.DEFAULT.name)
        if (isOreo()) {
            setLastId()
            mNotificationManager?.createNotificationChannel(
                notificationFactory.createNotificationChannel(
                    ChannelName.DEFAULT.name,
                    ChannelName.DEFAULT.name
                )
            )
        }
        message.messageId
        mNotificationManager?.notify(mNotificationId++, notification)
    }

    private fun isOreo(): Boolean {
        return Build.VERSION.SDK_INT >= 26
    }

    @TargetApi(26)
    private fun setLastId() {
        if(mNotificationManager?.activeNotifications?.size ?: 0 > 0){
            mNotificationId = mNotificationManager?.activeNotifications?.last { true }?.id ?: 0
        }
    }
}
