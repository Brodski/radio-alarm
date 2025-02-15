package com.example.bskiradioalarm.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            println("BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")
            Log.e("BootReceiver", "BOOT ACTION COMPLETED!!!!!!")

            // Reschedule alarms here
//            rescheduleAlarms(context)
        }
    }

    private fun rescheduleAlarms(context: Context) {
        var scheduler: Scheduler = Scheduler(context)
        scheduler.setAllAlarms()
    }
}
