package com.example.bskiradioalarm

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
//        supportActionBar?.hide()
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

        /////////////////////////////////////////////////////////////
        // Boilerplate above
        // My code below
        /////////////////////////////////////////////////////////////
        println("MAIN ACTIIVTY")
        createNotificationChannel()
        // TODO
        // NOT GOOD CODE JUST TESTING
        val scheduler: Scheduler = Scheduler(this)
//        scheduler.testAlarmOnStart()
        Log.e("BootReceiver", "test123 BOOT ACTION COMPLETED!!!!!!")
//        checkAndRequestOverlayPermission()

//        scheduler.setAllAlarms()
    }

    //////////////////////////////
    ////      INIT NOTIFICATION      ////
    //////////////////////////////
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "music_channel",
                "Music Playback",
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
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


    ///////////////////////////////////////
    ////      REQUEST ALARM POP UP     ////
    ///////////////////////////////////////
    fun checkAndRequestOverlayPermission() {
        println("Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT)
        println("Build.VERSION_CODES.M: " + Build.VERSION_CODES.M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                showOverlayPermissionDialog(this)
            } else {
                Toast.makeText(this, "Overlay permission is already granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showOverlayPermissionDialog(context: Context) {
        val REQUEST_CODE_OVERLAY_PERMISSION = 1234

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Overlay Permission Required")
        builder.setMessage("To continue, please allow this app to display over other apps.")
        builder.setPositiveButton("Grant Permission") { _, _ ->
            showOverlayPermissionDialog(this)
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            context.startActivity(intent)
//            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(context, "Overlay permission is required for this feature.", Toast.LENGTH_LONG).show()
        }

        val dialog = builder.create()
        dialog.show()
    }


}