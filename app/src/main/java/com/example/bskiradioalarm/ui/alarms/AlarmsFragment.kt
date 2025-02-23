package com.example.bskiradioalarm.ui.alarms


import PreferencesManagerSingleton
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bskiradioalarm.databinding.FragmentAlarmsBinding
import java.util.*
import android.app.TimePickerDialog
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.ui.stationsdialog.MenuMainDialog
import com.example.bskiradioalarm.viewmodels.StationsViewModel
import com.example.bskiradioalarm.utils.Scheduler
import kotlin.collections.LinkedHashMap

class AlarmsFragment : Fragment() {

    private var _binding: FragmentAlarmsBinding? = null
    private val binding get() = _binding!! // only valid between onCreateView and onDestroyView.

    private lateinit var alarmsSharedPrefs: SharedPreferences
    private lateinit var stationsSharedPrefs: SharedPreferences

    private val alarmSettingsMap = mutableMapOf<String, AlarmSettings>()
    private val uiAlarmsMap = mutableMapOf<String, LinearLayout>()

    private lateinit var scheduler: Scheduler

//    private val sharedStationsViewModel: StationsViewModel by activityViewModels()
//
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        alarmsSharedPrefs = PreferencesManagerSingleton.alarmsSharedPrefs
        stationsSharedPrefs = requireContext().getSharedPreferences("station_setting", Context.MODE_PRIVATE)
        scheduler = Scheduler(requireContext())

        _binding = FragmentAlarmsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.addAlarmButton.setOnClickListener {
            addNewAlarm()
        }

        println("(Alarm-onCreateView) loading alarms from storage ...")
        loadAlarmsFromStorage()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // sharedStationsViewModel.loadStations()
    }

    ///////////////////////////////////////////////
    // TAP NEW ALARM "+" BUTTON 1/2
    ///////////////////////////////////////////////
    private fun addNewAlarm() {
        println("addNewAlarm() ")
        val newAlarmSettings: AlarmSettings = AlarmSettings()
        newAlarmSettings.save(alarmsSharedPrefs)
        addAlarmUi(newAlarmSettings)

    }

    ///////////////////////////////////////////////
    // TAP TIME "9:00" BUTTON
    ///////////////////////////////////////////////
    private fun openClockDialog(alarmSettings: AlarmSettings, isNew: Boolean = false) {
        // Init Clock-dialog & display
        val calendar = Calendar.getInstance()
        val hourUi: Int = alarmSettings.hour
        val minuteUi: Int = alarmSettings.minute

        var timeSelected = false // pro trick

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            android.R.style.Theme_DeviceDefault_Dialog,
            { view, selectedHour, selectedMinute  ->
                println("clicked in clock")
                timeSelected = true
                alarmSettings.hour = selectedHour
                alarmSettings.minute = selectedMinute
            },
            hourUi, minuteUi, false
        )
        timePickerDialog.setOnDismissListener {
            // User clicked though
            if (timeSelected) {
                println("Dialog dismissed AFTER selection")
                alarmSettings.save(alarmsSharedPrefs)
                alarmSettings.updateTime(scheduler)
                updateAlarmUi(alarmSettings)
            }
            // User canceled
            else {
                println("Dialog dismissed WITHOUT selection")
            }
        }
        timePickerDialog.show()
    }

    private fun updateAlarmUi(alarmSettings: AlarmSettings) {
        val container = uiAlarmsMap[alarmSettings.id]
        val hourMinLabel = container?.findViewWithTag<TextView>("alarm_time_text")
        if (hourMinLabel != null) {
            hourMinLabel.text = alarmSettings.prettyPrintTime()
        } else {
            println("WTF NO LABELLLLL")
        }
        println("new clock time: " + hourMinLabel?.text)
    }

    private fun loadAlarmsFromStorage() {
        val allAlarmsMap: LinkedHashMap<String, AlarmSettings> = AlarmSettings.getAllSorted(alarmsSharedPrefs)
        for ((keyId, alarmSettings) in allAlarmsMap) {
//            val jsonStr: String = alarmsSharedPrefs.getString(keyId, "").toString()
//            val alarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
            println("(load) loading alarm to ui: ${alarmSettings.id}")
            addAlarmUi(alarmSettings)
        }
    }

///////////////////////////////////////////////////////////////////////////////////
// ADD NEW ALARM 2/2
///////////////////////////////////////////////////////////////////////////////////
//        +--------+   +----+  +----+  +----+  +----+  +----+  +----+  +----+
//   🔊   | 12:30  |   |Mon |  |Tue |  |Wed |  |Thu |  |Fri |  |Sat |  |Sun |  ❌
//        +--------+   +----+  +----+  +----+  +----+  +----+  +----+  +----+
//                      [ x ]   [   ]    [ x ]   [   ]  [ x ]    [   ]   [ x ]
///////////////////////////////////////////////////////////////////////////////////
    private fun addAlarmUi(alarmSettings: AlarmSettings) {
        // EMPTY  CONTAINER
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
//            setPadding(10, 10, 10, 10)
            setPadding(0, 10, 10, 0)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#F0F0F0")) // Light gray background
        }

        // Station Button (🔊)
        val stationButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_lock_silent_mode_off)
            layoutParams = LinearLayout.LayoutParams(110, 60)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(0, 0, 0, 0)
            setColorFilter(Color.BLACK)
            setOnClickListener { MenuMainDialog(alarmSettings).show(parentFragmentManager, "MenuMainTag")}
//            setOnClickListener { MenuMainDialog.newInstance(alarmSettings.toJsonStringSerialize()).show(parentFragmentManager, "MenuMainTag")}
//            setOnClickListener { showStationDialog(alarmSettings) }
        }

        // TIME 12:30
        val textView = TextView(requireContext()).apply {
            text = alarmSettings.prettyPrintTime()
            textSize = 18f
            setPadding(20, 10, 20, 10)
            setOnClickListener { openClockDialog(alarmSettings)}
            tag = "alarm_time_text"
        }

        // Delete Button (❌)
        val todooo = android.R.drawable.ic_media_play
        val deleteButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(60, 60)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(5, 5, 5, 5)
            setOnClickListener { deleteAndConfirm(alarmSettings, container) }
        }

        container.addView(stationButton)
        container.addView(textView)

        // CHECKBOXES
        val checkBoxStates: BooleanArray = alarmSettings.daysOfWeek.values.toBooleanArray()
        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        for (i in 0 until 7) {
            val checkBoxContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(5, 5, 5, 5)
            }
            val dayName = TextView(requireContext()).apply {
                text = dayLabels[i]
                textSize = 14f
                gravity = Gravity.CENTER
            }
            val checkBox = CheckBox(requireContext()).apply {
                isChecked = checkBoxStates[i]
                setPadding(5, 5, 5, 5)
                setOnCheckedChangeListener { _, isChecked ->
                    checkBoxStates[i] = isChecked
                    onCheckBoxToggled(i, isChecked, alarmSettings)
                }
            }
            checkBoxContainer.addView(dayName)
            checkBoxContainer.addView(checkBox)

            container.addView(checkBoxContainer)
            uiAlarmsMap[alarmSettings.id] = container
        }

        container.addView(deleteButton)

        (container.parent as? ViewGroup)?.removeView(container)

        binding.alarmsContainer.addView(container) // Add to UI
    }

    //////////////////////////////////////
    // CHECKBOX TOGGLE
    //////////////////////////////////////
    private fun onCheckBoxToggled(i: Int, isChecked: Boolean, alarmSettings: AlarmSettings) {
        val dayMap: MutableMap.MutableEntry<String, Boolean> = alarmSettings.daysOfWeek.entries.elementAt(i)
        alarmSettings.daysOfWeek[dayMap.key] = isChecked
        alarmSettings.save(alarmsSharedPrefs)
        val day = dayMap.key
        val isOn = alarmSettings.daysOfWeek[dayMap.key]
        scheduler.setWakeUp2(alarmSettings, day)
    }

    //////////////////////////////////////
    // DELETE ALARM
    //////////////////////////////////////
    private fun deleteAndConfirm(alarmSettings: AlarmSettings, container: LinearLayout) {
        val time = alarmSettings.prettyPrintTime()

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm @ $time")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Delete") { _, _ ->
                (container.parent as? ViewGroup)?.removeView(container)
                uiAlarmsMap.remove(alarmSettings.id)
                alarmSettings.delete(alarmsSharedPrefs, scheduler)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

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

