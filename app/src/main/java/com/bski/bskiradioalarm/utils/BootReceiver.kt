package com.bski.bskiradioalarm.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            println("BOOT ACTION COMPLETED!!!!!!")
            println("BOOT ACTION COMPLETED!!!!!!")
            println("BOOT ACTION COMPLETED!!!!!!")
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
            var scheduler: Scheduler = Scheduler(context)
            println("BOOT RECEIVER")
            scheduler.setAllAlarms()
        }
    }
}
