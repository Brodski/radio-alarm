package com.example.bskiradioalarm.ui.wakeup

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.utils.RadioService

class WakeUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        println("WAKE UP ACTIVITY")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wake_up) // Simple XML with two buttons

        val btnSnooze = findViewById<Button>(R.id.btnSnooze)
        val btnDismiss = findViewById<Button>(R.id.btnDismiss)

        // TODO close the notification when the app closes
        val notificationId = 1

        btnSnooze.setOnClickListener {
            Toast.makeText(this, "Snoozed!", Toast.LENGTH_SHORT).show()

            stopService(Intent(this, RadioService::class.java)) // Stop radio service

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.cancel(notificationId)

            finish()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish() // Closes the current activity so it won't stay in the back stack
        }

        btnDismiss.setOnClickListener {
            Toast.makeText(this, "Dismissed!", Toast.LENGTH_SHORT).show()
            stopMusic()
            finish()
        }
    }


    private fun stopMusic() {
        // stop Foreground Service too
        val stopIntent = Intent(this, RadioService::class.java)
        stopService(stopIntent) // Stops radio playback
    }
}
