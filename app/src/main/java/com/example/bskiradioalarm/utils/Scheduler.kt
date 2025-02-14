package com.example.bskiradioalarm.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract.CalendarAlerts
import android.provider.Settings
import com.example.bskiradioalarm.models.AlarmSettings
import java.time.DayOfWeek

import java.util.*

class Scheduler(context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val context: Context = context

    public fun setWakeUp2(alarmSettings: AlarmSettings, day: String) {
//        println("(setWakeUp2) " + (day.toLowerCase() + alarmSettings.id))
        val isOn: Boolean = alarmSettings.daysOfWeek[day] == true
        if (!isOn) {
            this.cancelAlarm(alarmSettings, day)
        }
        if (isOn) {
            this.createAlarmIntent(alarmSettings, day)
        }
    }

    fun createAlarmIntent(alarmSettings: AlarmSettings, day: String){
        val requestCodeAlarm = alarmSettings.getRequestCode(day)

        // SET TIME
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, AlarmSettings.getDayAsInt(day))
        calendar.set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
        calendar.set(Calendar.MINUTE, alarmSettings.minute)
        calendar.set(Calendar.SECOND, 0)

        val calendarEpsilon = Calendar.getInstance().apply { add(Calendar.SECOND, 20) }
//        println("XalarmSettings:" + alarmSettings)
//        println("calendar.timeInMillis:   " + calendar.timeInMillis)
//        println("calEpsilon.timeInMillis: " + calendarEpsilon.timeInMillis)
        if (calendar.before(calendarEpsilon)) {
            println("ADDING A WEEK!")
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week if today’s time has passed
        }

        // BUILD INTENT
        val intent = Intent(this.context, WakeUpReceiver::class.java)
        intent.putExtra("requestCode", requestCodeAlarm)
        intent.putExtra("id", alarmSettings.id)
        intent.putExtra("wakeEpoch", calendar.timeInMillis.toString())
        intent.putExtra("msg", "USA")

        val pendingIntent = PendingIntent.getBroadcast(this.context, requestCodeAlarm, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)

        // SEND INTENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            this.context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
        else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
        println("INTENT CREATED set for: ${calendar.time} ")
    }

    fun setAllAlarms() {
        val sharedPreferences = this.context.getSharedPreferences("alarms_setting", Context.MODE_PRIVATE)
        val allAlarmsMap: LinkedHashMap<String, Any?> = AlarmSettings.getAllSorted(sharedPreferences)

        println("@@ Setting alarms for all...: ")
        for ((keyId, value) in allAlarmsMap) {
            println("--------------------------------")
            println("--------    $keyId     ---------")
            println("--------------------------------")
            val jsonStr = value.toString()
            val alarmSettings: AlarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)

        }
    }

    fun cancelAlarm(alarmSettings: AlarmSettings, day: String) {
        val intent = Intent(this.context, WakeUpReceiver::class.java)
        val requestCode: Int = alarmSettings.getRequestCode(day)
        val possibleIntent = PendingIntent.getBroadcast(this.context, requestCode, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE) //if no such PendingIntent exists, it returns null without creating a new one.
        if (possibleIntent == null) { // probably shouldnt happen
            println("INTENT CANCEL DOES NOT EXIT. requestCode: " + requestCode)
        }
        else {
            println("INTENT CANCELED. requestCode: " + requestCode)
            alarmManager.cancel(possibleIntent) // Cancels the alarm
            possibleIntent.cancel()
        }

    }
}