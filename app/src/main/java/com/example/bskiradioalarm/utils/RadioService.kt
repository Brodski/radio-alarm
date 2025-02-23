package com.example.bskiradioalarm.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.ui.wakeup.WakeUpActivity

class RadioService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {

        var isRunning = false
            private set // stupid syntax, thanks kotlin.

        var currentStreamUrl = ""
            private set

        public const val notificationMusicId = 1
        public const val EXTRA_STREAM_URL = "EXTRA_STREAM_URL"
        private const val EXTRA_PREVIEW_2ND_ACTION = "EXTRA_PREVIEW_2ND_ACTION"
        private const val ACTION_ALARM = "ACTION_ALARM"
        private const val ACTION_STATION_PREVIEW = "ACTION_STATION_PREVIEW"
        private const val PREVIEW_ACTION_PLAY = "PREVIEW_ACTION_PLAY"
        private const val PREVIEW_ACTION_PAUSE = "PREVIEW_ACTION_PAUSE"



        fun startAlarm(context: Context, streamUrl: String) {
            println("+++++++(RadioService) startAlarm+++++++")
            println("+++++++(RadioService) startAlarm+++++++")
            println("+++++++(RadioService) startAlarm+++++++")
            val radioIntent = Intent(context, RadioService::class.java)
            radioIntent.putExtra(this.EXTRA_STREAM_URL, streamUrl)
            radioIntent.action = this.ACTION_ALARM
            context.startForegroundService(radioIntent) // to onStartCommand()
        }

        fun startPreviewStation(context: Context, streamUrl: String) {
            println("+++++++(RadioService) startPreviewStation+++++++")
            println("+++++++(RadioService) startPreviewStation+++++++")
            println("+++++++(RadioService) startPreviewStation+++++++")
            val radioIntent = Intent(context, RadioService::class.java)
            radioIntent.putExtra(this.EXTRA_STREAM_URL, streamUrl)
            if (isRunning && currentStreamUrl == streamUrl){
                radioIntent.putExtra(this.EXTRA_PREVIEW_2ND_ACTION, this.PREVIEW_ACTION_PAUSE)
            }
            else {
                radioIntent.putExtra(this.EXTRA_PREVIEW_2ND_ACTION, this.PREVIEW_ACTION_PLAY)
            }
            radioIntent.action = this.ACTION_STATION_PREVIEW
            context.startForegroundService(radioIntent)
        }

        fun stopMusic(context: Context) {
            println("+++++++(RadioService) stopMusic+++++++")
            println("+++++++(RadioService) stopMusic+++++++")
            println("+++++++(RadioService) stopMusic+++++++")
            println("(RadioService) STOPPING MUSIC")
            val intent = Intent(context, RadioService::class.java)
            context.stopService(intent)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("RadioServicex: " + intent?.getStringExtra(RadioService.EXTRA_STREAM_URL))
        val streamUrl = intent?.getStringExtra(RadioService.EXTRA_STREAM_URL) ?: return START_NOT_STICKY
        val previewAction = intent?.getStringExtra(RadioService.EXTRA_PREVIEW_2ND_ACTION)
        println("(RadioService) - onStartCommand: streamUrl=$streamUrl, previewAction=$previewAction, intent?.action="+intent?.action)
        when (intent?.action) {
            RadioService.ACTION_ALARM -> {
                return startAlarm(streamUrl)
            }
            RadioService.ACTION_STATION_PREVIEW -> {
                return startPreviewStation(previewAction, streamUrl)
            }
            else -> {
                return START_NOT_STICKY
            }
        }
    }


    fun startPreviewStation(previewAction: String?, streamUrl: String): Int {
        println("xxxxxxxxxxxxxxxxxxxxxx")
        println("(RadioService) startPreviewStation - isRunning: "+ RadioService.isRunning)
        println("(RadioService) startPreviewStation - currentStreamUrl: "+ RadioService.currentStreamUrl)
        when(previewAction) {
            RadioService.PREVIEW_ACTION_PLAY -> {
                if (mediaPlayer == null) {
                    println("(RadioService) 1 ")
                    this.playStream(streamUrl)
                    this.startPreviewStationNotif(true, streamUrl)
                }
                else if (RadioService.isRunning && streamUrl != RadioService.currentStreamUrl) {
                    println("(RadioService) 2 ")
                    this.playStream(streamUrl) // will reset
                    this.startPreviewStationNotif(true, streamUrl)
                }
                else if (RadioService.isRunning && streamUrl == RadioService.currentStreamUrl) {

                    println("(RadioService) 3 ")
                    // do nothing
                }
                else if (!RadioService.isRunning && streamUrl == RadioService.currentStreamUrl) {
                    println("(RadioService) 4 ")
                    mediaPlayer?.start()
                    this.startPreviewStationNotif(true, streamUrl)
                    isRunning = true
                }
                else if (!RadioService.isRunning && streamUrl != RadioService.currentStreamUrl) {
                    println("(RadioService) 5")
                    this.playStream(streamUrl)
                    this.startPreviewStationNotif(true, streamUrl)
                }
            }

            RadioService.PREVIEW_ACTION_PAUSE -> {
                mediaPlayer?.pause()
                this.startPreviewStationNotif(false, streamUrl)
                RadioService.isRunning = false
            }
            else ->  return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun startAlarm(streamUrl: String): Int {
        println("(RadioService) startAlarm - Start")
        val openIntent = Intent(this, WakeUpActivity::class.java)
        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("Bski Alarm: ")
            .setContentText("Tap to dismiss")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .addAction(R.drawable.ic_launcher_foreground, "Dismiss", openPendingIntent)
            .setContentIntent(openPendingIntent) // Clicking opens WakeUpActivity
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true) // Allows notification to disappear after click
//            .setOngoing(false)   // Allows swiping away
            .setFullScreenIntent(openPendingIntent, true) // ✅ activity launches even when screen is off
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        startForeground(RadioService.notificationMusicId, notification)
        playStream(streamUrl)
        return START_STICKY
    }
    private fun startPreviewStationNotif(isPlaying: Boolean, streamUrl: String) {
        println("(RadioService) startPreviewStationNotif - isPlaying: $isPlaying")

        // Play action
        val playIntent = Intent(this, RadioService::class.java)
        playIntent.action = RadioService.ACTION_STATION_PREVIEW
        playIntent.putExtra(RadioService.EXTRA_STREAM_URL, streamUrl)
        playIntent.putExtra(RadioService.EXTRA_PREVIEW_2ND_ACTION, RadioService.PREVIEW_ACTION_PLAY)
        val playPendingIntent = PendingIntent.getService(this, 100, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Pause action
        val pauseIntent = Intent(this, RadioService::class.java)
        pauseIntent.action = RadioService.ACTION_STATION_PREVIEW
        pauseIntent.putExtra(RadioService.EXTRA_STREAM_URL, streamUrl)
        pauseIntent.putExtra(RadioService.EXTRA_PREVIEW_2ND_ACTION, RadioService.PREVIEW_ACTION_PAUSE)
        val pausePendingIntent = PendingIntent.getService(this, 200, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle(if (isPlaying) "Playing" else "Paused")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .addAction(
                if (isPlaying) android.R.drawable.alert_dark_frame else android.R.drawable.ic_media_play, // Apparenlty icons in notif doesnt work anymore :(
                if (isPlaying) "∥ Pause" else "▶ Play",
                if (isPlaying) pausePendingIntent else playPendingIntent
            )
            .setOngoing(isPlaying) // Notification stays if music is playing
            .build()

        startForeground(RadioService.notificationMusicId, notification)
        if (isPlaying) {
            isRunning = true
        }
        else {
            isRunning = false
        }
        currentStreamUrl = streamUrl
    }

    fun stopBetter(): Int {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
        currentStreamUrl = ""
        return START_NOT_STICKY
    }

    private fun playStream(url: String) {
        println("(RadioService) playStream: $url")

        // Prep previous state
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        } else {
            mediaPlayer?.reset() // Reset before changing URL
        }

        // Timeout & Bad urls prep
        val prepareTimeout: Long = 10000L // 10s
        val handler: Handler = Handler(Looper.getMainLooper())

        val timeoutRunnable: Runnable = Runnable {
            println("MediaPlayer Prepare timed out! Invalid media or URL.")
            stopBetter()
            Toast.makeText(applicationContext, "⚠ Error playing stream!", Toast.LENGTH_LONG).show()
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnErrorListener { _, what, extra ->
                    println("MusicService Error: $what, Extra: $extra")
                    handler.removeCallbacks(timeoutRunnable) // Cancel timeout
                    Toast.makeText(applicationContext, "⚠ Error playing stream!", Toast.LENGTH_LONG).show()
                    stopBetter()
                    true
                }
                setOnPreparedListener {
                    handler.removeCallbacks(timeoutRunnable)
                    start()
                    println("MediaPlayer MediaPlayer prepared and started.")
                }
                prepareAsync()
//                prepare()  // synchronous
            }

            handler.postDelayed(timeoutRunnable, prepareTimeout)
            currentStreamUrl = url
            isRunning = true
        } catch (e: Exception) {
            println("MusicService: Unexpected Error: ${e.message}")
            Log.e("MusicService", "Unexpected Error: ${e.message}")
//            stopBetter()
        }
    }



    override fun onDestroy() {
        println("(RadioService) onDestroy")
        super.onDestroy()
        mediaPlayer?.release()
//        stopForeground(true)
        stopBetter()
    }
}
