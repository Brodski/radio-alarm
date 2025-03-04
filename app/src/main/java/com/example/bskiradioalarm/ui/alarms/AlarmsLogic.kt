package com.example.bskiradioalarm.ui.alarms

import android.content.Context
import android.media.AudioManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.utils.Scheduler
import com.google.android.material.snackbar.Snackbar

class AlarmsLogic {

    fun addNewAlarm(newAlarmSettings: AlarmSettings) {
        // Empty alarm with nothing
        println("addNewAlarm() ")
        newAlarmSettings.save()
    }

    fun deleteAlarm(alarmSettings: AlarmSettings, scheduler: Scheduler) {
        alarmSettings.delete(scheduler)
    }

    fun completeTimeSelect(alarmSettings: AlarmSettings, scheduler: Scheduler) {
        println("Dialog dismissed AFTER selection")

        alarmSettings.save()

        alarmSettings.updateTime(scheduler)
    }

    fun toggleDayOnOff(alarmSettings: AlarmSettings, scheduler: Scheduler, dayName: String, isChecked: Boolean) {

        alarmSettings.daysOfWeek[dayName] = isChecked
        alarmSettings.save()
        scheduler.setWakeUp2(alarmSettings, dayName)
    }

    fun doQoLAlarmToast(alarmSettings: AlarmSettings, context: Context, view: View) {
        val dayHourMin: Triple<Int,Int,Int> = alarmSettings.findNextAlarmEvent()

        var nextAlarmMsg = ""
        if (dayHourMin.first == 0) {
            nextAlarmMsg = "Alarm in ${dayHourMin.second} hours, ${dayHourMin.third} min"
        }
        else {
            nextAlarmMsg = "Alarm in ${dayHourMin.first} days, ${dayHourMin.second} hours, ${dayHourMin.third} min"
        }


        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

        if (currentVolume == 0) {
            nextAlarmMsg = "⚠ Alarm is silent! \uD83D\uDD07" + "\nSettings → Sound → Alarm Volume\n" + nextAlarmMsg // ⚠
        }
        if (currentVolume == 1) {
//            nextAlarmMsg = "⚠ Alarm volume low!" + "\nSettings → Sound → Alarm Volume\n" + nextAlarmMsg // 🔊
            nextAlarmMsg = "⚠ Alarm volume low!\n" + nextAlarmMsg // 🔊
        }
        if (currentVolume < 2) {
            val duration = if (currentVolume == 0) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT
            val snackbar: Snackbar = Snackbar.make(view, nextAlarmMsg, duration)
            val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackbar.setAction("Dismiss") { snackbar.dismiss() }
            textView.maxLines = 5
            snackbar.show()
        }
        else {
            Toast.makeText(context, nextAlarmMsg, Toast.LENGTH_LONG).show()
        }
        println( "Alarm Volume: $currentVolume / $maxVolume")
        println("\uD83D\uDD0A Settings → Sound → Alarm Volume")
    }

    private fun nothing() {

        android.R.drawable.ic_delete
        android.R.drawable.stat_notify_error

        android.R.drawable.ic_media_play
        android.R.drawable.ic_media_pause
        android.R.drawable.ic_menu_add
        android.R.drawable.ic_input_add
        android.R.drawable.ic_lock_idle_alarm
        android.R.drawable.alert_dark_frame
        android.R.drawable.alert_light_frame
        android.R.drawable.arrow_down_float
        android.R.drawable.bottom_bar
        android.R.drawable.btn_default
        android.R.drawable.btn_dialog
        android.R.drawable.btn_minus
        android.R.drawable.btn_radio
        android.R.drawable.btn_plus
        android.R.drawable.alert_light_frame
        android.R.drawable.ic_lock_idle_alarm
        android.R.drawable.btn_star
        android.R.drawable.btn_star_big_off
        android.R.drawable.btn_star_big_on
        android.R.drawable.dialog_frame
        android.R.drawable.dialog_holo_dark_frame
        android.R.drawable.edit_text
        android.R.drawable.editbox_background
        android.R.drawable.editbox_dropdown_dark_frame
        android.R.drawable.editbox_dropdown_light_frame

        android.R.drawable.ic_dialog_alert

        android.R.drawable.ic_btn_speak_now

        android.R.drawable.ic_dialog_dialer

        android.R.drawable.ic_dialog_email

        android.R.drawable.ic_dialog_info

        android.R.drawable.ic_dialog_map

        android.R.drawable.ic_input_get

        android.R.drawable.ic_media_rew
        android.R.drawable.ic_menu_day
        android.R.drawable.ic_menu_directions
        android.R.drawable.ic_menu_edit
        android.R.drawable.ic_menu_manage
        android.R.drawable.ic_btn_speak_now

        R.drawable.ic_notifications_black_24dp
        R.drawable.ic_home_black_24dp
        R.drawable.ic_dashboard_black_24dp
        androidx.appcompat.R.drawable.abc_ab_share_pack_mtrl_alpha
        androidx.appcompat.R.drawable.test_level_drawable

        androidx.appcompat.R.drawable.abc_scrubber_control_to_pressed_mtrl_000

        androidx.appcompat.R.drawable.tooltip_frame_light

        androidx.appcompat.R.drawable.btn_radio_off_to_on_mtrl_animation

        android.R.drawable.ic_menu_edit

        android.R.drawable.btn_dialog

    }
}