package com.example.bskiradioalarm.utils

import PreferencesManagerSingleton
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.ui.wakeup.WakeUpReceiver

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

    fun testAlarmOnStart() {
        var alarmSettings = AlarmSettings()
        var cal = Calendar.getInstance()
        var day = cal.get(Calendar.DAY_OF_WEEK)
        var dayName = AlarmSettings.getDayName(day)
        alarmSettings.daysOfWeek[dayName] = true
        alarmSettings.hour = cal.get(Calendar.HOUR_OF_DAY)
        alarmSettings.minute = cal.get(Calendar.MINUTE)
        this.createAlarmIntent(alarmSettings, dayName, true)
    }

    fun createAlarmIntent(alarmSettings: AlarmSettings, day: String, isTest: Boolean = false){
        val requestCodeAlarm = alarmSettings.getRequestCode(day)

        // SET TIME
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, AlarmSettings.getDayAsInt(day))
        calendar.set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
        calendar.set(Calendar.MINUTE, alarmSettings.minute)
        calendar.set(Calendar.SECOND, 0)

        val calendarEpsilon = Calendar.getInstance().apply { add(Calendar.MILLISECOND, 20) }
        if ((calendar.before(calendarEpsilon) || Calendar.getInstance().timeInMillis == calendar.timeInMillis)  && !isTest) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week if today’s time has passed
        }
        if (isTest) {
            println("TEST: +20 sec")
            val cal2: Calendar = Calendar.getInstance()
            val nowSec: Int = cal2.get(Calendar.SECOND)
            if ( nowSec >= 53) {
                calendar.add(Calendar.MINUTE, 1 )
            }
            else {
                calendar.set(Calendar.SECOND, nowSec + 7)
            }
        }

        // BUILD INTENT
        val intent = Intent(this.context, WakeUpReceiver::class.java)
        intent.putExtra("requestCode", requestCodeAlarm)
        intent.putExtra("id", alarmSettings.id)
        intent.putExtra("wakeEpoch", calendar.timeInMillis.toString())
        intent.putExtra(RadioService.EXTRA_STREAM_URL, alarmSettings.station?.url)

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
        val alarmsSharedPrefs = PreferencesManagerSingleton.alarmsSharedPrefs
        println("!!! SCHEDULER FRAGMENT: " + alarmsSharedPrefs)
        println("!!! SCHEDULER FRAGMENT: " + alarmsSharedPrefs)
        println("!!! SCHEDULER FRAGMENT: " + alarmsSharedPrefs)
        val allAlarmsMap: LinkedHashMap<String, AlarmSettings> = AlarmSettings.getAllSorted(alarmsSharedPrefs)

        println("@@ Setting alarms for all...: ")
        for ((keyId, alarmSettings) in allAlarmsMap) {
            println("--------------------------------")
            println("--------    $keyId     ---------")
            println("--------------------------------")
//            val jsonStr = value.toString()
//            val alarmSettings: AlarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
            println(alarmSettings)
            for ((dayName, isOn) in alarmSettings.daysOfWeek) {
                println("$keyId  $dayName $isOn")
                if (isOn) {
                    this.createAlarmIntent(alarmSettings, dayName)
                }

            }

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