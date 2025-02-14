package com.example.bskiradioalarm

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bskiradioalarm.databinding.ActivityMainBinding
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.utils.Scheduler

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
        // TODO
        // NOT GOOD CODE JUST TESTING
//        val scheduler: Scheduler = Scheduler(this)
//        scheduler.setAllAlarms()
    }


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