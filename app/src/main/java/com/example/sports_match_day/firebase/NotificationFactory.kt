package com.example.sports_match_day.firebase

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.sports_match_day.R
import com.example.sports_match_day.ui.LoginActivity
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Kristo on 01-Apr-21
 */
class NotificationFactory(private var mContext: Context) {

    fun createNotification(message: RemoteMessage, channelId: String?): Notification {
        val openPendingIntent = createOpenPendingIntent()

        return NotificationCompat.Builder(mContext, channelId!!)
            .setSmallIcon(createSmallIcon())
            .setLargeIcon(createLargeIcon())
            .setContentTitle(getTitle(message))
            .setContentIntent(openPendingIntent)
            .setPriority(getPriority())
            .setContentText(getTicker(message))
            .setTicker(getTicker(message))
            .setVibrate(getVibration())
            .setColor(getColor())
            .setLights(getLedColor(), LED_DURATION_ON, LED_DURATION_OFF)
            .setAutoCancel(true)
            .build()
    }

    private fun createLargeIcon(): Bitmap{
        val resourceId: Int = R.drawable.app_icon_large
        return BitmapFactory.decodeResource(mContext.resources, resourceId)
    }

    private fun createOpenPendingIntent(): PendingIntent{
        val intent = Intent(mContext, LoginActivity::class.java)

        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.action = Intent.ACTION_MAIN
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

        return PendingIntent.getActivity(mContext,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @TargetApi(26)
    fun createNotificationChannel(name: String?, channelId: String?): NotificationChannel {
        val notificationChannel =
            NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = getVibration()
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = getLedColor()
        return notificationChannel
    }

    private fun createSmallIcon(): Int {
        return R.drawable.app_icon_small
    }

    private fun getTitle(message: RemoteMessage): String {
        return message.notification?.title ?: mContext.getString(R.string.app_name)
    }

    private fun getTicker(message: RemoteMessage): String {
        return message.notification?.body ?: mContext.getString(R.string.app_name)
    }

    private fun getPriority(): Int {
        return NotificationCompat.PRIORITY_HIGH
    }

    private fun getVibration(): LongArray {
        return longArrayOf(0, 800, 0)
    }

    private fun getColor(): Int {
        return ContextCompat.getColor(mContext, R.color.colorAccent)
    }

    private fun getLedColor(): Int {
        return ContextCompat.getColor(mContext, R.color.colorAccent)
    }

    companion object {
        private const val LED_DURATION_ON = 800
        private const val LED_DURATION_OFF = 800
    }
}