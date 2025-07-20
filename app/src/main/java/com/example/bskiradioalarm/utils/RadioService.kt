package com.example.bskiradioalarm.utils

import PreferencesManagerSingleton
import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.Optionz
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.ui.wakeup.WakeUpActivity
import java.util.Timer
import java.util.TimerTask

class RadioService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    private var timer: Timer? = null

    private var rampSecond = 30

    companion object {

        var isRunning = false
            private set // stupid syntax, thanks kotlin.

        var currentStreamUrl = ""
            private set

        public const val notificationMusicId = 1
        public const val DEFAULT_RADIO_REF_ID = "CPR_Classical_Id"

        public const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        public const val EXTRA_STREAM_URL = "EXTRA_STREAM_URL"
        public const val EXTRA_STATION_REF_ID = "EXTRA_STATION_REF_ID"
        private const val EXTRA_PREVIEW_2ND_ACTION = "EXTRA_PREVIEW_2ND_ACTION"

        private const val ACTION_ALARM = "ACTION_ALARM"
        private const val ACTION_STATION_PREVIEW = "ACTION_STATION_PREVIEW"
        private const val ACTION_STATION_END = "ACTION_STATION_END"

        private const val PREVIEW_ACTION_PLAY = "PREVIEW_ACTION_PLAY"
        private const val PREVIEW_ACTION_PAUSE = "PREVIEW_ACTION_PAUSE"
//        private const val PREVIEW_ACTION_STOP = "PREVIEW_ACTION_STOP"



        fun startAlarm(context: Context, alarmSettings: AlarmSettings, isSnooze: Boolean = false) {
            println("+++++++(RadioService) startAlarm() +++++++")
            println("+++++++(RadioService) startAlarm() +++++++")
            println("+++++++(RadioService) startAlarm() +++++++")
            println("+++++++(RadioService) alarmSettings.stationRef: " + alarmSettings.stationRef)
            println("+++++++(RadioService) alarmSettings.id: " + alarmSettings.id)

            val radioIntent = Intent(context, RadioService::class.java)
            radioIntent.putExtra(this.EXTRA_STATION_REF_ID, alarmSettings.stationRef)
            radioIntent.putExtra(this.EXTRA_ALARM_ID, alarmSettings.id)
            radioIntent.action = this.ACTION_ALARM
            radioIntent.putExtra("isSnooze", isSnooze) // starting the alarm again b/c of snooze btn.isSnooze isSnooze = true


            context.startForegroundService(radioIntent) // to onStartCommand()
        }

        fun startPreviewStation(context: Context, streamUrl: String) {
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
            context.startForegroundService(radioIntent) // to onStartCommand()
        }

        fun stopMusic(context: Context) {
            println("+++++++(RadioService) stopMusic+++++++")
            val intent = Intent(context, RadioService::class.java)
            context.stopService(intent)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("# # # # # # # START -- RadioService --- START # # # # # # #")
        println("# # # # # # # START -- RadioService --- START # # # # # # #")
//        val streamUrl     = intent?.getStringExtra(RadioService.EXTRA_STREAM_URL) ?: return START_NOT_STICKY
        val alarmId       = intent?.getStringExtra(RadioService.EXTRA_ALARM_ID)
        val stationIdRef  = intent?.getStringExtra(RadioService.EXTRA_STATION_REF_ID) ?: ""
        val previewAction = intent?.getStringExtra(RadioService.EXTRA_PREVIEW_2ND_ACTION)
        val streamUrl     = intent?.getStringExtra(RadioService.EXTRA_STREAM_URL) ?: ""
        val isSnooze      = intent?.getBooleanExtra("isSnooze", false)

        var alarmSettings: AlarmSettings? = AlarmSettings.getAlarmById(alarmId.toString())
        if (alarmSettings == null) {
            println("THIS MOFO IS NULL WTF")
            println("THIS MOFO IS NULL WTF")
            println("THIS MOFO IS NULL WTF")
            println("THIS MOFO IS NULL WTF")
            println("THIS MOFO IS NULL WTF")
        }

        if (alarmSettings == null) {
            alarmSettings = AlarmSettings()
        }
        if (isSnooze == true && !stationIdRef.isNullOrBlank()) {
            alarmSettings?.stationRef = stationIdRef
        }

        println("(RadioService) - onStartCommand EXTRAS: alarmId= " + alarmId)
        println("(RadioService) - onStartCommand EXTRAS: stationIdRef= " + stationIdRef)
        println("(RadioService) - onStartCommand EXTRAS: previewAction= " + previewAction)
        println("(RadioService) - onStartCommand EXTRAS: streamUrl= " + streamUrl)
        println("(RadioService) - onStartCommand EXTRAS: isSnooze= " + isSnooze)
        println("(RadioService) - onStartCommand EXTRAS: alarmSettings?.stationRef = " + alarmSettings?.stationRef)
        println("(RadioService) - onStartCommand EXTRAS: alarmSettings=" + alarmSettings)

        when (intent?.action) {
            RadioService.ACTION_ALARM -> {
                return startAlarmAux(alarmSettings)
            }
            RadioService.ACTION_STATION_PREVIEW -> {
                return startPreviewStationAux(previewAction, streamUrl)
            }

            RadioService.ACTION_STATION_END -> {
                println("(RadioService STOP) 6")
                println("(RadioService STOP) 6")
                println("(RadioService STOP) 6")
                println("(RadioService STOP) 6")
                println("(RadioService STOP) 6")
                stopBetter()
                return START_NOT_STICKY
            }
            else -> {
                return START_NOT_STICKY
            }
        }
    }


    fun startPreviewStationAux(previewAction: String?, streamUrl: String): Int {
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

    private fun startAlarmAux(alarmSettings: AlarmSettings): Int {
        println("(RadioService) startAlarm - Start")
        println("(RadioService) startAlarm - PACKING 4 WakeUpActivity")
        println("(RadioService) startAlarm - PACKING 4 WakeUpActivity")
        println("(RadioService) startAlarm - PACKING 4 WakeUpActivity")
        println("(RadioService) startAlarm - PACKING alarmSettings.stationRef: " + alarmSettings.stationRef)
//        println("(RadioService) startAlarm - PUTTING alarmId INTO EXTRA: " + alarmSettings.id)
//        println("(RadioService) alarmSettings " + alarmSettings)
        val uniqueRequestCode = (0..1000000).random()

        val openIntent = Intent(this, WakeUpActivity::class.java)

        openIntent.putExtra(RadioService.EXTRA_ALARM_ID, alarmSettings.id)
        openIntent.putExtra("uniqueRequestCode", uniqueRequestCode)
        openIntent.putExtra(RadioService.EXTRA_STATION_REF_ID, alarmSettings.stationRef)
        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val openPendingIntent = PendingIntent.getActivity(this, uniqueRequestCode, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CoolConstantData.music_channel_id)
            .setContentTitle("Bski Alarm: ")
            .setContentText("Tap to dismiss")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
//            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(openPendingIntent, true)
            .setSilent(true)
            .build()


        startForeground(RadioService.notificationMusicId, notification)  // to onStartCommand()


        // BAM HERE!!!!!!!
        val station: Station? = Station.getStationById(alarmSettings.stationRef)
        playStream(station!!.url)


        println("(RadioService) startAlarm - STARTING ACTIVITY")
//        wakeUpScreen(context)
        this.startActivity(openIntent)


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

        // Stop action
        val stopIntent = Intent(this, RadioService::class.java)
        stopIntent.action = RadioService.ACTION_STATION_END
        val stopPendingIntent  = PendingIntent.getService(this, 300, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle(if (isPlaying) "Playing" else "Paused")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .addAction(
                if (isPlaying) android.R.drawable.alert_dark_frame else android.R.drawable.ic_media_play, // Apparenlty icons in notif doesnt work anymore :(
//                if (isPlaying) "∥ Pause" else "▶ Play",
                if (isPlaying) "Pause" else "▶ Play",
                if (isPlaying) pausePendingIntent else playPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "✖ Stop",
                stopPendingIntent
            )
            .setOngoing(isPlaying) // Notification stays if music is playing
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()




//        with(NotificationManagerCompat.from(this)) {
//            notificationManager.notify(RadioService.notificationMusicId, notification)
//        }
//        startForeground(RadioService.notificationMusicId, notification)
        startForeground(RadioService.notificationMusicId, notification)  // to onStartCommand()

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(RadioService.notificationMusicId, notification)

        if (isPlaying) {
            isRunning = true
        }
        else {
            isRunning = false
        }
        currentStreamUrl = streamUrl
    }


    // BOOM
    private fun playStream(url: String) {
        val handler = Handler(Looper.getMainLooper())
        val prepareTimeout: Long = 10000L // 10 seconds


        try {
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) playStream: $url")
            println("(RadioService) mediaPlayer: " + mediaPlayer)

            // Prep previous state
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            } else {
                mediaPlayer?.reset()
            }

            val timeoutRunnable: Runnable = Runnable {
                println("MediaPlayer Prepare timed out! Invalid media or URL.")
                stopBetter()
                Toast.makeText(applicationContext, "⚠ Error playing stream!!!", Toast.LENGTH_LONG).show()
            }


            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnErrorListener { _, what, extra ->
                    println("MediaPlayer Error: $what, Extra: $extra")
                    handler.removeCallbacks(timeoutRunnable) // Cancel timeout
                    Toast.makeText(applicationContext, "⚠ Error playing stream!", Toast.LENGTH_LONG).show()
                    stopBetter()
                    true
                }
                setOnPreparedListener {
                    handler.removeCallbacks(timeoutRunnable)
                    setVolume(0f, 0f) // start silent
                    start()
                    doSomething10secondsLater(handler)
                    println("MediaPlayer prepared and started.")
                }
                prepareAsync()
//                prepare()  // synchronous
            }

            // Schedule timeout handling
             handler.postDelayed(timeoutRunnable, prepareTimeout)

            currentStreamUrl = url
            isRunning = true
        } catch (e: Exception) {
            println("MediaPlayer: Unexpected Error: ${e.message}")
            stopBetter()
        }

    }

    private fun rampVolume(stepsRemaining: Int) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var currentMUSICVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (currentMUSICVolume < 3) {
            currentMUSICVolume = 3
        }

        val volumeChunk: Float = currentMUSICVolume.toFloat() / rampSecond
        var volumeNew = volumeChunk * (rampSecond - stepsRemaining) // 0.433 * [ 30 - 29,28,27,26,25 ]

        timer?.cancel()
        timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                println("(RadioService) RAMPING @ " + stepsRemaining + ": " + volumeNew)

                if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                    mediaPlayer!!.setVolume(volumeNew, volumeNew)
                }

                // go next
                if (stepsRemaining == 0) {
                    timer?.cancel()
                    timer = null
                    println("(RadioService) RAMP COMPLETE")
                    return
                }
                else {
                    rampVolume(stepsRemaining - 1)
                }
            }
        }
        var delay = 1000L // 1 seconds
        timer?.schedule(task, delay)

    }

    private fun doSomething10secondsLater(handler: Handler) {
        val steps = 30

        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val muteSecondsX: String? = optionsSharedPreferences.getString(Optionz.MUTE_STORAGE_PREF_KEY, "0")
        var muteSeconds: Long = muteSecondsX?.toLong() ?: 0L

        println("(RadioService) muteSecond: " + muteSeconds)


        handler.postDelayed({
            println("$muteSeconds seconds later")
            rampVolume(rampSecond)
        }, muteSeconds * 1000)
    }

    fun stopBetter(): Int {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        }
//
//        // Stop and release MediaPlayer safely
//        mediaPlayer?.let {
//            if (it.isPlaying) {
//                it.stop()
//            }
//            it.release()
//        }
//        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
        currentStreamUrl = ""
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        println("(RadioService) onDestroy")
        stopBetter()
        super.onDestroy()
    }
}
