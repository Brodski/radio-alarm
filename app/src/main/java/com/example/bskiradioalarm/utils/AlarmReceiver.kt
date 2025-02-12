package com.example.bskiradioalarm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.bskiradioalarm.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show notification when alarm triggers
        showNotification(context, "Alarm", "Time to wake up!")
        println("ALARM GOING OFF " + intent)
//        println("ALARM GOING OFF: Action - " + intent.action)
//        println("ALARM GOING OFF: Data - " + intent.data)
        println("ALARM GOING OFF: Extras - " + intent.extras?.toString())

//        intent.extras?.keySet()?.forEach { key ->
//            println("ALARM GOING OFF: Extra - $key: " + intent.extras?.get(key))
//        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val channel = NotificationChannel(channelId, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(soundUri, null) // Set sound for the channel
        }
        notificationManager.createNotificationChannel(channel)


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)  // Title of the notification
            .setContentText(message) // Message body
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High-priority to show immediately
            .build()

        notificationManager.notify(1, notification)
    }
}
