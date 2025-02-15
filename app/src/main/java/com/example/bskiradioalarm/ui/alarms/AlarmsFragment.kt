package com.example.bskiradioalarm.ui.alarms


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.utils.CoolConstants
import com.example.bskiradioalarm.utils.Scheduler

class AlarmsFragment : Fragment() {

    private var _binding: FragmentAlarmsBinding? = null
    private val binding get() = _binding!! // only valid between onCreateView and onDestroyView.

    private lateinit var sharedPreferences: SharedPreferences

    // TODO load it up
    private val alarmSettingsMap = mutableMapOf<String, AlarmSettings>()
    private val uiAlarmsMap = mutableMapOf<String, LinearLayout>()

    private lateinit var scheduler: Scheduler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sharedPreferences = requireContext().getSharedPreferences("alarms_setting", Context.MODE_PRIVATE)
        scheduler = Scheduler(requireContext())

        val alarmsViewModel = ViewModelProvider(this).get(AlarmsViewModel::class.java)

        _binding = FragmentAlarmsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        alarmsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.addAlarmButton.setOnClickListener {
            addNewAlarm()
        }

        println("(Alarm-onCreateView) loading alarms from storage ...")
        loadAlarmsFromStorage()
        return root
    }
    ///////////////////////////////////////////////
    // TAP NEW "+" BUTTON 1/2
    ///////////////////////////////////////////////
    private fun addNewAlarm() {
        // Init
        println("addNewAlarm() ")
        val newAlarmSettings: AlarmSettings = AlarmSettings()
        newAlarmSettings.save(sharedPreferences)
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
                alarmSettings.save(sharedPreferences)
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
            hourMinLabel.text = String.format("%02d:%02d", alarmSettings.hour, alarmSettings.minute)
        } else {
            println("WTF NO LABELLLLL")
        }
        println("new clock time: " + hourMinLabel?.text)
    }

    private fun loadAlarmsFromStorage() {
        val allAlarmsMap: LinkedHashMap<String, Any?> = AlarmSettings.getAllSorted(sharedPreferences)
        for ((keyId, value) in allAlarmsMap) {
            val jsonStr: String = sharedPreferences.getString(keyId, "").toString()
            val alarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
            println("(load) loading alarm to ui: ${alarmSettings.id}")
            addAlarmUi(alarmSettings)
        }
    }

///////////////////////////////////////////////////////////////////////////////////
// ADD NEW ALARM 2/2
///////////////////////////////////////////////////////////////////////////////////
//        +--------+   +----+  +----+  +----+  +----+  +----+  +----+  +----+
//        | 12:30  |   |Mon |  |Tue |  |Wed |  |Thu |  |Fri |  |Sat |  |Sun |  ❌
//        +--------+   +----+  +----+  +----+  +----+  +----+  +----+  +----+
//                      [ x ]   [   ]    [ x ]   [   ]  [ x ]    [   ]   [ x ]
///////////////////////////////////////////////////////////////////////////////////
    private fun addAlarmUi(alarmSettings: AlarmSettings) {
        // EMPTY  CONTAINER
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 10)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#F0F0F0")) // Light gray background
        }

        // TIME 12:30
        val textView = TextView(requireContext()).apply {
            text = String.format("%02d:%02d", alarmSettings.hour, alarmSettings.minute)
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
        alarmSettings.save(sharedPreferences)
        val day = dayMap.key
        val isOn = alarmSettings.daysOfWeek[dayMap.key]
        scheduler.setWakeUp2(alarmSettings, day)
    }

    //////////////////////////////////////
    // DELETE ALARM
    //////////////////////////////////////
    private fun deleteAndConfirm(alarmSettings: AlarmSettings, container: LinearLayout) {
        val time = String.format("%02d:%02d", alarmSettings.hour, alarmSettings.minute)

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm @ $time")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Delete") { _, _ ->
                (container.parent as? ViewGroup)?.removeView(container)
                uiAlarmsMap.remove(alarmSettings.id)
                alarmSettings.delete(sharedPreferences, scheduler)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
