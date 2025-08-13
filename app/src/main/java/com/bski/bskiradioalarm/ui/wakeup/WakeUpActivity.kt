package com.bski.bskiradioalarm.ui.wakeup

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bski.bskiradioalarm.R
import com.bski.bskiradioalarm.models.AlarmSettings
import com.bski.bskiradioalarm.models.Optionz
import com.bski.bskiradioalarm.utils.RadioService
import com.bski.bskiradioalarm.utils.Scheduler
import java.util.Calendar
import kotlin.random.Random


class WakeUpActivity : AppCompatActivity() {

    val SNOOZE_MINUTES = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")


        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            var keyguardManager = getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD  or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        var keyguardManager = this.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager != null && keyguardManager.isKeyguardLocked) {
            keyguardManager.requestDismissKeyguard(this, null)
        }


        setContentView(R.layout.activity_wake_up)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val btnSnooze = findViewById<Button>(R.id.btnSnooze)
        val btnDismiss = findViewById<Button>(R.id.btnDismiss)

        val stationRef = intent.getStringExtra(RadioService.EXTRA_STATION_REF_ID) ?: "-1"
        val alarmId = intent.getStringExtra(RadioService.EXTRA_ALARM_ID) ?: "FAILED_ALARMID"

        println("WAKEUP GOT stationRef=$stationRef")
        println("WAKEUP GOT alarmId=$alarmId")

        btnSnooze.setOnClickListener {

            val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
            val snoozeMinutes: String? = optionsSharedPreferences.getString(Optionz.SNOOZE_STORAGE_PREF_KEY, "5")

            Toast.makeText(this, "Snoozed for $snoozeMinutes minutes", Toast.LENGTH_SHORT).show()
            snooze(alarmId, stationRef)
            stopMusic()
            finish()
        }

        btnDismiss.setOnClickListener {
            stopMusic()
            finish()
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:AlarmLock")


        try {
            wakeLock.acquire(3000) // Keep CPU awake for ~3 seconds
            // startForeground(RadioService.notificationMusicId, notification)
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }

    private fun snooze(alarmId: String, stationRef: String) {
        println(" ````````` SNOOZE `````````` ")
        println(" ````````` SNOOZE `````````` ")
        println(" ````````` SNOOZE `````````` ")

        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val snoozeMinutes: String? = optionsSharedPreferences.getString(Optionz.SNOOZE_STORAGE_PREF_KEY, "5")
        println("(snooze) snoozeMinutes: " + snoozeMinutes)
        var cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, snoozeMinutes?.toInt() ?: 5)
        var day = cal.get(Calendar.DAY_OF_WEEK)
        var dayName = AlarmSettings.getDayName(day)

        var previousAlarm = AlarmSettings.getAlarmById(alarmId)

        var alarmSettings = AlarmSettings()
        alarmSettings.daysOfWeek[dayName] = true
        alarmSettings.hour = cal.get(Calendar.HOUR_OF_DAY)
        alarmSettings.minute = cal.get(Calendar.MINUTE)
        alarmSettings.id = "snoozeId"+ Random.nextInt().toString()
        alarmSettings.stationRef = if (stationRef != "-1") stationRef  else previousAlarm?.stationRef.toString()

//        println("(SNOOZE) next @ " + alarmSettings)
        var scheduler: Scheduler = Scheduler(this)
        println("(snooze) alarm.id: " +         alarmSettings.id)
        println("(snooze) alarm.stationRef: " + alarmSettings.stationRef)
        println("(snooze) alarm.stationRef: " + alarmSettings.stationRef)
        println("(snooze) alarm.stationRef: " + alarmSettings.stationRef)
        println("(snooze) alarm.stationRef: " + alarmSettings.stationRef)
        println("(snooze) alarm.stationRef: " + alarmSettings.stationRef)
        println(" ````````` SNOOZE END `````````` ")
        scheduler.createSnoozeIntent(alarmSettings, dayName)
        return
    }

    private fun stopMusic() {
        RadioService.stopMusic(this)
//        finish()
    }
}
