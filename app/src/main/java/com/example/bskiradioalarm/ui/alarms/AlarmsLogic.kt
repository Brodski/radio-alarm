package com.example.bskiradioalarm.ui.alarms

import PreferencesManagerSingleton
import android.content.Context
import android.media.AudioManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.utils.Scheduler
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmsLogic {

    fun addNewAlarm(newAlarmSettings: AlarmSettings) {
        // Empty alarm with nothing
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
    fun toastForSingle(alarmSettings: AlarmSettings, dayName: String) {

        val alertCal = toastForSingleAux(alarmSettings, dayName)

        val dayHourMin: Triple<Int,Int,Int> = this.convertToTripletIntDiff(alertCal)


    }

    fun toastForSingleAux(alarmSettings: AlarmSettings, dayName: String): Calendar? {
        println("vvvvvvvvvv")
        println("    " + alarmSettings.daysOfWeek)
        println("   dayName: " + dayName)
        val dayTapped = alarmSettings.daysOfWeek[dayName]
        println("   dayTapped " + dayTapped)
        if (dayTapped == false) {
            return null
        }
        var nowCal = Calendar.getInstance()
        var tappedAlarmCal = Calendar.getInstance()
        tappedAlarmCal[Calendar.HOUR_OF_DAY] = alarmSettings.hour
        tappedAlarmCal[Calendar.MINUTE] = alarmSettings.minute
//        tappedAlarmCal[Calendar.DAY_OF_WEEK] = AlarmSettings.getDayAsInt(dayName)

        // annoying math to move the calender from today to the next Tuesday or Wednesday or ect
        val today = tappedAlarmCal.get(Calendar.DAY_OF_WEEK)
        val targetDayOfWeek = AlarmSettings.getDayAsInt(dayName)
        var daysToAdd = targetDayOfWeek - today
        if (daysToAdd <= 0) {
            daysToAdd += 7
        }
        tappedAlarmCal.add(Calendar.DAY_OF_MONTH, daysToAdd)

        // math to move if same day
        // idk if I need this
        if (tappedAlarmCal.before(nowCal) && dayTapped == true) {
            println("   ---> isSameDayAsToday and before ")
            tappedAlarmCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(tappedAlarmCal.time)
        println("   " + formattedDate)
        println("^^^^^^^")
        return tappedAlarmCal

    }

    fun toggleDayOnOff(alarmSettings: AlarmSettings, scheduler: Scheduler, dayName: String, isChecked: Boolean) {
        alarmSettings.daysOfWeek[dayName] = isChecked
        alarmSettings.save()
        scheduler.setWakeUp2(alarmSettings, dayName)
    }
    ////////////////////////////////////////
    //  time    M   T   W   Th  F   S   Su
    //  9:00    X   X   X   X   X   ✔️  X
    //  14:15   X   ✔️  X  ✔️   ✔️  X   X
    //  8:15    X   X️   X   X️    ️X   X   X
    //
    // If today is Wednesday, it will return:
    // Triple <Saturday, 9, 0>
    // Triple<Thursday, 14, 15>
    // Triple<-1, -1, -1>
    fun findNextAlarmFromAll(): Calendar? {
        val nextAlarmList = mutableListOf<Calendar?>()
        val allAlarmsMap: LinkedHashMap<String, AlarmSettings> = AlarmSettings.getAllSorted()

        for (alarmSetting: AlarmSettings in allAlarmsMap.values.toList()) {
            var nextTime = alarmSetting.findNextAlarmEvent()
            if (nextTime != null) {
                nextAlarmList.add(nextTime)
            }
        }

        val sortedAlarms = nextAlarmList.sortedBy { it?.timeInMillis }


        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        for (alarmCal in sortedAlarms) {
            if (alarmCal == null) {
                continue
            }
            val formattedDate = formatter.format(alarmCal.time)
            println(formattedDate)
        }
        if (sortedAlarms.size == 0) {
            return null
        }
        val shiiit = sortedAlarms.firstOrNull()
        val formattedDate = formatter.format(shiiit?.time)

        return shiiit



    }



    fun convertToTripletIntDiff(nextAlert: Calendar?): Triple<Int, Int, Int> {
        if (nextAlert == null){
            return Triple(-1, -1, -1)
        }
        var nowCal = Calendar.getInstance()

        val diffMillis = nextAlert.timeInMillis - nowCal.timeInMillis

        val msInSec = 1000
        val secInMin = 60
        val minInHour = 60
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        val hours = (diffMillis / (1000 * 60 * 60)).toInt() % 24
        val minutes = (diffMillis / (1000 * 60)).toInt() % 60
//        println("next alarm: days: $days. hours $hours. min $minutes")
//        println("+=========== END =============+")
        return Triple(days, hours, minutes)
    }



    // THIS IS DISABLED ATM, I just dont like it
    fun doQoLAlarmToast(alarmSettings: AlarmSettings, context: Context, view: View, dayName: String) {
        val tappedAlarmCal: Calendar? = toastForSingleAux(alarmSettings, dayName)
        var nextAlarm = tappedAlarmCal
        val dayHourMin: Triple<Int,Int,Int> = this.convertToTripletIntDiff(nextAlarm)
        println("QOL TOAST - dayHourMin" + dayHourMin)
        //
        // Next Alarm toast
        //
        // TODO, this message duplicated in the ViewModel file
        var nextAlarmMsg = ""
        if (dayHourMin.first == 0) {
            nextAlarmMsg = "Alarm in ${dayHourMin.second} hours, ${dayHourMin.third} min"
        }
        else if (dayHourMin.first == -1 ) {
//            nextAlarmMsg = "Alarm disabled"
            nextAlarmMsg = "${dayName}  disabled"
        }
        else {
            nextAlarmMsg = "Alarm in ${dayHourMin.first} days, ${dayHourMin.second} hours, ${dayHourMin.third} min"
        }
        println(nextAlarmMsg)

        //
        // Audio Toast
        //
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
//            Toast.makeText(context, nextAlarmMsg, Toast.LENGTH_LONG).show()
            Toast.makeText(context, nextAlarmMsg, Toast.LENGTH_SHORT).show()
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

        androidx.appcompat.R.drawable.abc_ic_menu_overflow_material


        androidx.appcompat.R.drawable.abc_ab_share_pack_mtrl_alpha
        androidx.appcompat.R.drawable.test_level_drawable

        androidx.appcompat.R.drawable.abc_scrubber_control_to_pressed_mtrl_000

        androidx.appcompat.R.drawable.tooltip_frame_light

        androidx.appcompat.R.drawable.btn_radio_off_to_on_mtrl_animation

        android.R.drawable.ic_menu_edit

        android.R.drawable.btn_dialog

        android.R.drawable.ic_menu_crop

        android.R.drawable.btn_dropdown

        android.R.drawable.ic_menu_preferences

        android.R.drawable.ic_menu_manage
        android.R.drawable.ic_menu_view
        android.R.drawable.ic_menu_help
        android.R.drawable.ic_dialog_info
        android.R.drawable.ic_menu_info_details

        android.R.drawable.ic_menu_more

    }
}