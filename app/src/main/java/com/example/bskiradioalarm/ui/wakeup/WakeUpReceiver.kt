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
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.utils.RadioService
import com.example.bskiradioalarm.utils.Scheduler
import java.util.Calendar

class WakeUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // UNPACK INTENT
        val DEFAULT_STATION_ID = RadioService.DEFAULT_RADIO_REF_ID

        val requestCode         = intent.getIntExtra("requestCode", -1)
        val alarmId             = intent.getStringExtra(RadioService.EXTRA_ALARM_ID) ?: "-123"
        val wakeEpochTemp       = intent.getStringExtra("wakeEpoch") ?: "-1"
        val snoozeStationRefId  = intent.getStringExtra("snoozeStationRefId")
        val EXTRA_STATION_REF_ID = intent.getStringExtra(RadioService.EXTRA_STATION_REF_ID)




        val currentAlarmCal: Calendar = Calendar.getInstance().apply { timeInMillis = wakeEpochTemp.toLong() }

        // PLAY RADIO
        println("---------------- ALARM GOING OFF -----------------")
        println("----------- FOUND EXTRAS: alarmId: $alarmId ------------")
        println("----------- FOUND EXTRAS: REQUESTCODE: $requestCode ------------")
        println("----------- FOUND EXTRAS: snoozeStationRefId: $snoozeStationRefId ------------")
        println("----------- FOUND EXTRAS: EXTRA_STATION_REF_ID: $EXTRA_STATION_REF_ID ------------")
        var alarmSettings = AlarmSettings.getAlarmById(alarmId)

        var isSnooze = false
        if (!snoozeStationRefId.toString().isNullOrBlank()) {
            println("---------- alarm triggered by SNOOZE")
            isSnooze = true // alarm triggered by a snooze
        }
        if (alarmSettings == null) {
            alarmSettings = AlarmSettings()
            alarmSettings.stationRef = if (isSnooze) snoozeStationRefId.toString() else DEFAULT_STATION_ID
            println("---------- alarmSettings IS NULL @ ID=  " +alarmId)
            println("---------- alarmSettings using stationRef: " + alarmSettings.stationRef)
        }


        RadioService.startAlarm(context, alarmSettings, isSnooze)

        val alarmIntent = Intent(context, WakeUpActivity::class.java)
        // alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)


//        wakeUpScreen(context)
//
//        context.startActivity(alarmIntent)

        if (!isSnooze) {
            println("----------  SCHEDULING FOR NEXT WEEK")
            repeatNextWeek(context, currentAlarmCal, alarmId)
//            repeatNextWeek(context, currentAlarmCal, stationRefId, alarmId)
        }
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

//    private fun repeatNextWeek(context: Context, currentAlarmCal: Calendar, stationRefId: String, alarmId: String) {
    private fun repeatNextWeek(context: Context, currentAlarmCal: Calendar, alarmId: String) {

        val repeatAlarm: AlarmSettings? = AlarmSettings.getAlarmById(alarmId)

        val nextWake = Calendar.getInstance()
        nextWake.timeInMillis = currentAlarmCal.timeInMillis

        val day: String = AlarmSettings.getDayName(nextWake.get(Calendar.DAY_OF_WEEK))
        val repeatAlarmSettings: AlarmSettings = AlarmSettings()
        repeatAlarmSettings.id = alarmId
        repeatAlarmSettings.hour = nextWake.get(Calendar.HOUR_OF_DAY)
        repeatAlarmSettings.minute = nextWake.get(Calendar.MINUTE)
        repeatAlarmSettings.daysOfWeek[day]= true
        // repeatAlarmSettings.station = Station()
        // repeatAlarmSettings.station?.url = streamUrl
        // repeatAlarmSettings.stationRef = stationRefId

        // println("(repeatWake) nextWake.get(Calendar.DAY_OF_WEEK):" + nextWake.get(Calendar.DAY_OF_WEEK))
        // println("(repeatWake) nextWake.get(Calendar.HOUR_OF_DAY):" + nextWake.get(Calendar.HOUR_OF_DAY))
        // println("(repeatWake) nextWake.get(Calendar.MINUTE):" + nextWake.get(Calendar.MINUTE))
        // println("(repeatWake) nextWake.DAY:" + day)
        // println("(repeatWake) nextWake.time:" + nextWake.time)
        // println("(repeatWake) nextWake.time:" + nextWake.time)
        // println("(repeatWake) nextWake.time:" + nextWake.time)

        val scheduler = Scheduler(context)

        // OLD
//        scheduler.setWakeUp2(repeatAlarmSettings, day) // week will be incremented in setWakeUp2

        // NEW
        if (repeatAlarm == null) {
            println("ABORT!!!!!!!!")
            println("ABORT!!!!!!!!")
            println("ABORT!!!!!!!!")
            println("ABORT!!!!!!!!")
            println("ABORT!!!!!!!!")
            return
        }
        println("REPEATING @ ")
        println("REPEATING @ ")
        println("REPEATING @ ")
        println(repeatAlarm)
        scheduler.setWakeUp2(repeatAlarm, day) // week will be incremented in setWakeUp2
    }

}
