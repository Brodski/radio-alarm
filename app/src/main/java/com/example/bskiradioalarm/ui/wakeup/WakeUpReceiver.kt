package com.example.bskiradioalarm.ui.wakeup

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.utils.RadioService
import com.example.bskiradioalarm.utils.Scheduler
import java.util.Calendar

class WakeUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("------------ ALARM GOING OFF -------------")
        println(intent)

        // UNPACK INTENT
        val requestCode   = intent.getIntExtra("requestCode", -1)
        val streamUrl           = intent.getStringExtra(RadioService.EXTRA_STREAM_URL) ?: ""
        val id            = intent.getStringExtra("id") ?: "-1"
        val wakeEpochTemp = intent.getStringExtra("wakeEpoch") ?: "-1"
        val wakeCal: Calendar = Calendar.getInstance().apply { timeInMillis = wakeEpochTemp.toLong() }
        val title = "Alarm: $requestCode"

//        showNotification(context, title, wakeCal)

        // PLAY RADIO
//        val streamUrl = "https://stream1.cprnetwork.org/cpr2_lo"

//        val radioIntent = Intent(context, RadioService::class.java)
//        radioIntent.putExtra("STREAM_URL", streamUrl)
//        context.startForegroundService(radioIntent)
        RadioService.startAlarm(context, streamUrl)

        val alarmIntent = Intent(context, WakeUpActivity::class.java)
//        alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        wakeUpScreen(context)
        context.startActivity(alarmIntent)


        println("REPEAT NEXT WEEK = OFF")
        println("REPEAT NEXT WEEK = OFF")
        println("REPEAT NEXT WEEK = OFF")
//        repeatNextWeek(context, wakeCal, id)
    }
    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "BskiAlarmRadio:WakeLock"
        )

        wakeLock.acquire(5000) // Keep the screen on for 5 seconds
    }

    private fun repeatNextWeek(context: Context, currentAlarmCal: Calendar, id: String) {
        val nextWake = Calendar.getInstance()
        nextWake.timeInMillis = currentAlarmCal.timeInMillis

        val day: String = AlarmSettings.getDayName(nextWake.get(Calendar.DAY_OF_WEEK))
        val alarmSettings: AlarmSettings = AlarmSettings()
        alarmSettings.id = id
        alarmSettings.hour = nextWake.get(Calendar.HOUR_OF_DAY)
        alarmSettings.minute = nextWake.get(Calendar.MINUTE)
        alarmSettings.daysOfWeek[day]= true

        println("(repeatWake) nextWake.get(Calendar.DAY_OF_WEEK):" + nextWake.get(Calendar.DAY_OF_WEEK))
        println("(repeatWake) nextWake.get(Calendar.WEEK_OF_YEAR):" + nextWake.get(Calendar.WEEK_OF_YEAR))
        println("(repeatWake) nextWake.get(Calendar.HOUR_OF_DAY):" + nextWake.get(Calendar.HOUR_OF_DAY))
        println("(repeatWake) nextWake.get(Calendar.MINUTE):" + nextWake.get(Calendar.MINUTE))
        println("(repeatWake) nextWake.get(Calendar.SECOND):" + nextWake.get(Calendar.SECOND))
        println("(repeatWake) DAY:" + day)
        println("(repeatWake) Calendar.DAY_OF_WEEK:" + Calendar.DAY_OF_WEEK)
        println("(repeatWake) nextWake.time:" + nextWake.time)
        println(alarmSettings)

        val scheduler = Scheduler(context)
        scheduler.setWakeUp2(alarmSettings, day) // week will be incremented in setWakeUp2


    }


    private fun showNotification(context: Context, title: String, wakeCal: Calendar) {
        val notificationIdTest = 2
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val channel = NotificationChannel(channelId, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(soundUri, null) // Set sound for the channel
        }
        notificationManager.createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)  // Title of the notification
            .setContentText(wakeCal.time.toString()) // Message body
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High-priority to show immediately
            .build()

        notificationManager.notify(notificationIdTest, notification)
    }
}
