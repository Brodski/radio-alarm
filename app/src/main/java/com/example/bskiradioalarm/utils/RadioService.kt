package com.example.bskiradioalarm.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.ui.wakeup.WakeUpActivity

class RadioService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val streamUrl = intent?.getStringExtra("STREAM_URL") ?: return START_NOT_STICKY

        startForegroundNotification()
        playStream(streamUrl)
        return START_STICKY
    }

    private fun playStream(url: String) {
        println("RadioService playStream: $url")
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { start() }
            setOnErrorListener { _, what, extra ->
                Log.e("MusicService", "Error: $what, Extra: $extra")
                stopSelf()
                true
            }
            prepareAsync()
        }
    }

    private fun startForegroundNotification() {
        // Open a notification (helps user got to alarm/radio/app). QOL
        val openIntent = Intent(this, WakeUpActivity::class.java)
        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "music_channel")
            // UI
            .setContentTitle("Bski Alarm: ")
            .setContentText("Tap to dismiss")
            .setSmallIcon(android.R.drawable.ic_media_play)

            // Intents
            .setContentIntent(openPendingIntent) // Clicking opens WakeUpActivity

            .addAction(R.drawable.ic_launcher_foreground, "Dismiss", openPendingIntent)

            // Features
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true) // Allows notification to disappear after click
//            .setOngoing(false)   // Allows swiping away
            .setFullScreenIntent(openPendingIntent, true) // ✅ Ensures the activity launches even when screen is off
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Treated as an alarm

            .build()

        val notificationId = 1
        startForeground(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaPlayer?.release()
        stopForeground(true)
        super.onDestroy()
    }
}
