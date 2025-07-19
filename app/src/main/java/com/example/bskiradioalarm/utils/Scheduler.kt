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
        val isOn: Boolean = alarmSettings.daysOfWeek[day] == true
        if (!isOn) {
            this.cancelAlarm(alarmSettings, day)
        }
        if (isOn) {
            println("^^^^^^^^^ createAlarmIntent 1  ^^^^^^^^^")
            this.createAlarmIntent(alarmSettings, day)
        }
    }

//    fun testAlarmOnStart() {
//        var alarmSettings = AlarmSettings()
//        var cal = Calendar.getInstance()
//        var day = cal.get(Calendar.DAY_OF_WEEK)
//        var dayName = AlarmSettings.getDayName(day)
//        alarmSettings.daysOfWeek[dayName] = true
//        alarmSettings.hour = cal.get(Calendar.HOUR_OF_DAY)
//        alarmSettings.minute = cal.get(Calendar.MINUTE)
//        println("^^^^^^^^^ createAlarmIntent 2  ^^^^^^^^^")
//        this.createAlarmIntent(alarmSettings, dayName)
//    }

    fun createSnoozeIntent(alarmSettings: AlarmSettings, day: String) {
        this.createAlarmIntent(alarmSettings, day)
    }
    
    fun createAlarmIntent(alarmSettings: AlarmSettings, day: String){
        var isTest = false
        val requestCodeAlarm = alarmSettings.getRequestCode(day)

        // SET TIME
        val alarmAsCal: Calendar = Calendar.getInstance()
        alarmAsCal.set(Calendar.DAY_OF_WEEK, AlarmSettings.getDayAsInt(day))
        alarmAsCal.set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
        alarmAsCal.set(Calendar.MINUTE, alarmSettings.minute)
        alarmAsCal.set(Calendar.SECOND, 0)

        val calendarEpsilon = Calendar.getInstance().apply { add(Calendar.MILLISECOND, 20) }
        if ((alarmAsCal.before(calendarEpsilon) || Calendar.getInstance().timeInMillis == alarmAsCal.timeInMillis)  && !isTest) {
            alarmAsCal.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week if today’s time has passed
        }
        if (isTest) {
            println("TEST: +20 sec")
            val cal2: Calendar = Calendar.getInstance()
            val nowSec: Int = cal2.get(Calendar.SECOND)
            if ( nowSec >= 53) {
                alarmAsCal.add(Calendar.MINUTE, 1 )
            }
            else {
                alarmAsCal.set(Calendar.SECOND, nowSec + 7)
            }
        }

        // BUILD INTENT
        val wakeUpintent = Intent(this.context, WakeUpReceiver::class.java)

        wakeUpintent.putExtra(RadioService.EXTRA_STATION_REF_ID, alarmSettings.stationRef)
        wakeUpintent.putExtra(RadioService.EXTRA_ALARM_ID, alarmSettings.id)
        wakeUpintent.putExtra("requestCode", requestCodeAlarm)
        wakeUpintent.putExtra("wakeEpoch", alarmAsCal.timeInMillis.toString())
        wakeUpintent.putExtra("unique_id", System.currentTimeMillis())
        wakeUpintent.putExtra("snoozeStationRefId", alarmSettings.stationRef)

        println("=====================    BEGIN  =========================")
        println("===== Creating EXTRAS - .EXTRA_STATION_REF_ID: " + alarmSettings.stationRef)
        println("===== Creating EXTRAS - .EXTRA_ALARM_ID: " + alarmSettings.id)
        println("===== Creating EXTRAS - requestCode: " +  requestCodeAlarm)
        println("===== Creating EXTRAS - wakeEpoch: " +  alarmAsCal.timeInMillis.toString())
        println("===== Creating EXTRAS - unique_id: " +  System.currentTimeMillis())
        println("===== Creating EXTRAS - snoozeStationRefId: " +  alarmSettings.stationRef)
        println("===== Creating EXTRAS - snoozeStationRefId: " +  alarmSettings.stationRef)
        println("===== Creating EXTRAS - snoozeStationRefId: " +  alarmSettings.stationRef)
        println("===== Creating EXTRAS - snoozeStationRefId: " +  alarmSettings.stationRef)


//        println("===== Creating Alarm Intent. alarmSettings.id: " + alarmSettings.id )
//        println("===== Creating Alarm Intent. day: " + day )
//        println("===== Creating Alarm Intent. requestCode: $requestCodeAlarm")
//        println("===== Creating Alarm Intent. stationRef: ${alarmSettings.stationRef}")
//        println("===== Creating Alarm Intent. snoozeStationRefId: $snoozeStationRefId")

        val pendingIntent = PendingIntent.getBroadcast(this.context, requestCodeAlarm, wakeUpintent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)

        // SEND INTENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // permission check
            this.context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
        else {
            // send it
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmAsCal.timeInMillis, pendingIntent)
        }
        println("INTENT CREATED set for: ${alarmAsCal.time} ")
        println("=====================    END    =========================")
    }

    // Called by the OS at phone's power-on
    // Also called by MainActivity but I think that is redundant/pointless
    fun setAllAlarms() {
        val alarmsSharedPrefs = PreferencesManagerSingleton.alarmsSharedPrefs
        val allAlarmsMap: LinkedHashMap<String, AlarmSettings> = AlarmSettings.getAllSorted()

        for ((keyId, alarmSettings) in allAlarmsMap) {
            println("(setAllAlarms) --------    id: $keyId     ---------")
            println("(setAllAlarms) --------    stationRef: " + alarmSettings.stationRef)
            for ((dayName, isOn) in alarmSettings.daysOfWeek) {
//                println("$keyId  $dayName $isOn")
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
//            println("INTENT CANCEL DOES NOT EXIT. requestCode: " + requestCode)
        }
        else {
//            println("INTENT CANCELED. requestCode: " + requestCode)
            alarmManager.cancel(possibleIntent) // Cancels the alarm
            possibleIntent.cancel()
        }

    }
}