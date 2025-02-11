package com.example.bskiradioalarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

        for ((uuid, value) in sharedPreferences.all) {

            val jsonStr = value.toString()
            val alarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
            println("@@ Setting alarm for: ")
            println(alarmSettings)
//            setAlarm(this, alarmSettings)

            val myUuid = UUID.fromString(alarmSettings.uuid)

            val mostSigBits = myUuid.mostSignificantBits
            val leastSigBits = myUuid.leastSignificantBits

            println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
            println("UUID: $myUuid")
            println("Most Significant Bits (MSB): $mostSigBits")
            println("Least Significant Bits (LSB): $leastSigBits")

        }
    }


//////////////////////////////
////      HARD DELETE     ////
//////////////////////////////
    private  fun hardDelete() {
        //        val keysToDelete = sharedPreferences.all.keys.toList()
        //        val editor = sharedPreferences.edit()
        //        for (key in keysToDelete) {
        //            editor.remove(key)
        //        }
        //        editor.apply() // Apply all changes at once
    }


}