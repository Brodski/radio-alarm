package com.example.bskiradioalarm.ui.wakeup

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.utils.RadioService
import com.example.bskiradioalarm.utils.Scheduler
import java.util.Calendar

class WakeUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wake_up)

        val btnSnooze = findViewById<Button>(R.id.btnSnooze)
        val btnDismiss = findViewById<Button>(R.id.btnDismiss)

        // TODO close the notification when the app closes
        btnSnooze.setOnClickListener {
            snooze()
            Toast.makeText(this, "Snoozed!", Toast.LENGTH_SHORT).show()
        }

        btnDismiss.setOnClickListener {
            Toast.makeText(this, "Dismissed!", Toast.LENGTH_SHORT).show()
            stopMusic()
        }
    }

    override fun onDestroy() {
        stopMusic()
        super.onDestroy()
    }

    private fun snooze() {
        stopMusic()
        var cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 5)
        var day = cal.get(Calendar.DAY_OF_WEEK)
        var dayName = AlarmSettings.getDayName(day)

        var alarmSettings = AlarmSettings()
        alarmSettings.daysOfWeek[dayName] = true
        alarmSettings.hour = cal.get(Calendar.HOUR_OF_DAY)
        alarmSettings.minute = cal.get(Calendar.MINUTE)
        alarmSettings.id = "snoozeId"

        println("SNOOZE! next @ " + alarmSettings)
        var scheduler: Scheduler = Scheduler(this)
        scheduler.createAlarmIntent(alarmSettings, dayName)

    }

    private fun stopMusic() {
//        val notificationId = 1
//        stopService(Intent(this, RadioService::class.java)) // Stop radio service
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.cancel(notificationId)
        RadioService.stopMusic(this)
        finish()
    }
}
