package com.example.bskiradioalarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bskiradioalarm.databinding.ActivityMainBinding
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.IAlarmShared
import com.example.bskiradioalarm.utils.AlarmReceiver
import com.example.bskiradioalarm.utils.AlarmScheduleIntentHelper
import com.example.bskiradioalarm.utils.CoolConstants
import java.util.Calendar
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        println("MAIN ACTIIVTY")
        val sharedPreferences = this.getSharedPreferences("alarms_setting", Context.MODE_PRIVATE)
        val allAlarmsMap: LinkedHashMap<String, Any?> = AlarmSettings.getAllSorted(sharedPreferences)

        println("@@ Setting alarms for all...: " + allAlarmsMap)
        for ((keyId, value) in allAlarmsMap) {

            val jsonStr = value.toString()
            val alarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
            val alarmScheduleIntentHelper: AlarmScheduleIntentHelper = AlarmScheduleIntentHelper(this)
            alarmScheduleIntentHelper.setAlarm(alarmSettings)
//            setAlarm(alarmSettings)

        }
    }


//    public fun setAlarm(alarmSettings: AlarmSettings) {
//        val intent = Intent(this, AlarmReceiver::class.java)
//        val alarmManager: AlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        intent.putExtra("message", "party hard! " + alarmSettings.id)
//
//        val pendingIntent = PendingIntent.getBroadcast(this, 69, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, alarmSettings.hour)
//            set(Calendar.MINUTE, alarmSettings.minute)
//            set(Calendar.SECOND, 0)
//        }
////        val calendar = Calendar.getInstance().apply {
////            add(Calendar.SECOND, 10) // Trigger in 10 sec
////        }
//        println("Alarm set for: ${calendar.time}")
//        println("Build.VERSION.SDK_INT: ${Build.VERSION.SDK_INT}")
//        println("Build.VERSION_CODES.S: ${Build.VERSION_CODES.S}")
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
//            println("shit way")
//            this.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
//        }
//        else {
//            println("goodway")
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//        }
//    }

    //////////////////////////////
    ////      HARD DELETE     ////
    //////////////////////////////
    private fun hardDelete(sharedPreferences: SharedPreferences) {
        val keysToDelete = sharedPreferences.all.keys.toList()
        val editor = sharedPreferences.edit()
        for (key in keysToDelete) {
            editor.remove(key)
        }
        editor.apply() // Apply all changes at once
    }

}