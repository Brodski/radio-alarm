package com.example.bskiradioalarm.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.bskiradioalarm.models.AlarmSettings

import java.util.*

class AlarmScheduleIntentHelper(context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val context: Context = context

    public fun setAlarm(alarmSettings: AlarmSettings) {
        val intent = Intent(this.context, AlarmReceiver::class.java)
        intent.putExtra("message", "party hard! " + alarmSettings.getAltId())

        val pendingIntent = PendingIntent.getBroadcast(this.context, alarmSettings.getAltId(), intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
            set(Calendar.MINUTE, alarmSettings.minute)
            set(Calendar.SECOND, 0)
        }
//        val calendar = Calendar.getInstance().apply {
//            add(Calendar.SECOND, 10) // Trigger in 10 sec
//        }
//        println("Alarm set for: ${calendar.time}")
        println("Current time:   ${System.currentTimeMillis()}")
        println("Alarm set for: (${calendar.timeInMillis}) ${calendar.time} ")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            this.context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
        else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(context, 69, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE) //if no such PendingIntent exists, it returns null without creating a new one.

        alarmManager.cancel(pendingIntent) // Cancels the alarm
        pendingIntent.cancel()

        println("Alarm with requestCode $69 canceled")
    }

    fun printAlarm(context: Context, requestCode: Int) {
        println("Alarm with requestCode $69 is scheduled")
    }
}