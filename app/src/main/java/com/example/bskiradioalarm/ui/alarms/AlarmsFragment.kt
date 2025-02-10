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
import android.widget.TimePicker
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.example.bskiradioalarm.models.AlarmSettings

class AlarmsFragment : Fragment() {

    private var _binding: FragmentAlarmsBinding? = null
    private val binding get() = _binding!! // only valid between onCreateView and onDestroyView.

    // TODO load it up
    private val alarmSettingsMap = mutableMapOf<String, AlarmSettings>()
    private val uiAlarmsMap = mutableMapOf<String, LinearLayout>()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sharedPreferences = requireContext().getSharedPreferences("alarms_setting", Context.MODE_PRIVATE)

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

        return root
    }
    private fun addNewAlarm() {
        // Init
        println("addNewAlarm() ")
        val uuid = UUID.randomUUID().toString()
        val newAlarmSettings: AlarmSettings = AlarmSettings()
        newAlarmSettings.uuid = uuid
        newAlarmSettings.save(sharedPreferences, alarmSettingsMap)
        println("start......")
        openClockDialog(newAlarmSettings, isNew = true)
        println("end......")
    }

    private fun openClockDialog(alarmSettings: AlarmSettings, isNew: Boolean = false) {
        val calendar = Calendar.getInstance()
        val hourUi: Int = alarmSettings.hour ?: 9
        val minuteUi: Int = alarmSettings.minute ?: 0

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
            if (timeSelected) { // Completed
                println("Dialog dismissed AFTER selection")
                if (isNew) {
                    addAlarmUi(alarmSettings)
                }
                else {
                    updateAlarmUi(alarmSettings)
                }
//                openWeekdaySelector(alarmSettings)

            }
            else { // Canceled
                println("Dialog dismissed WITHOUT selection")
//                onCancel?.invoke()
            }
        }
        timePickerDialog.show()
    }

    private fun openWeekdaySelector(alarmSettings: AlarmSettings) {

        // Custom pop-up
        val weekdayView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)

            // 7 days
            for ((day, isAlarmOn) in alarmSettings.daysOfWeek) {
                val checkBox = CheckBox(requireContext()).apply {
                    text = day
                    isChecked = isAlarmOn
                    setOnCheckedChangeListener { _, isChecked ->
                        alarmSettings.daysOfWeek[day] = isChecked
//                        alarmSettings.save(sharedPreferences, alarmSettingsMap)
                    }
                }
                addView(checkBox)
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Days for alarmTime")
            .setView(weekdayView)
            .setPositiveButton("OK") { _, _ -> }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onTimeSelected(view: TimePicker, alarmSettings: AlarmSettings) {

        openWeekdaySelector(alarmSettings)
//        val timeUi = String.format("%02d:%02d", selectedHour, selectedMinute)
//        binding.alarmTextView.text = timeUi  // Update UI
    }


    private fun updateAlarmUi(alarmSettings: AlarmSettings) {

        val container = uiAlarmsMap[alarmSettings.uuid]
        val hourMinLabel = container?.findViewWithTag<TextView>("alarm_time_text")
        if (hourMinLabel != null) {
            hourMinLabel.text = String.format("%02d:%02d", alarmSettings.hour, alarmSettings.minute)
        } else {
            println("WTF NO LABELLLLL")
        }
        println("new: " + hourMinLabel?.text)

    }
///////////////////////////////////////////////////////////////////////////////
//        +--------+   +----+  +----+  +----+  +----+  +----+  +----+  +----+
//        | 12:30  |   |Mon |  |Tue |  |Wed |  |Thu |  |Fri |  |Sat |  |Sun |
//        +--------+   +----+  +----+  +----+  +----+  +----+  +----+  +----+
//                      [ x ]   [   ]    [ x ]   [   ]  [ x ]    [   ]   [ x ]
///////////////////////////////////////////////////////////////////////////////
    private fun addAlarmUi(alarmSettings: AlarmSettings) {

        // CONTAINER
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 10, 20, 10)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#F0F0F0")) // Light gray background
        }

        // TIME 08:30
        val textView = TextView(requireContext()).apply {
            text = String.format("%02d:%02d", alarmSettings.hour, alarmSettings.minute)
            textSize = 18f
            setPadding(20, 10, 20, 10)
            setOnClickListener { openClockDialog(alarmSettings)}
            tag = "alarm_time_text"
        }

        // Create the Delete Button (❌)
        val deleteButton = ImageButton(requireContext()).apply {
//            setImageResource(android.R.drawable.ic_delete) // Use a built-in small trash icon
            setImageResource(android.R.drawable.ic_delete) // Use a built-in small trash icon
            layoutParams = LinearLayout.LayoutParams(60, 60) // Explicit size (adjust as needed)
            scaleType = ImageView.ScaleType.CENTER_INSIDE // Keeps the icon within bounds
            setBackgroundColor(Color.TRANSPARENT) // Remove default button background
            setPadding(5, 5, 5, 5) // Minimal padding
            setOnClickListener { showDeleteConfirmationDialog(alarmSettings, container) }
        }


        container.addView(textView)

        // CHECKBOXES
        val checkBoxStates: BooleanArray = BooleanArray(7)
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
                setPadding(5, 5, 5, 5)
                setOnCheckedChangeListener { _, isChecked ->
                    checkBoxStates[i] = isChecked
                    onCheckBoxToggled(i, isChecked)
                }
            }
            checkBoxContainer.addView(dayName)
            checkBoxContainer.addView(checkBox)

            container.addView(checkBoxContainer)
            uiAlarmsMap[alarmSettings.uuid] = container
        }

        container.addView(deleteButton)

        (container.parent as? ViewGroup)?.removeView(container)

        binding.alarmsContainer.addView(container) // Add to UI
    }

    private fun onCheckBoxToggled(i: Int, isChecked: Boolean) {
        println("clicked it:" + i + isChecked)
    }
    private fun showDeleteConfirmationDialog(alarmSettings: AlarmSettings, container: LinearLayout) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Delete") { _, _ ->
                (container.parent as? ViewGroup)?.removeView(container)
                uiAlarmsMap.remove(alarmSettings.uuid)
//                alarmContainerViews.remove(container)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    
//    private fun saveAlarms() {
//        val editor = sharedPreferences.edit()
//        editor.putStringSet("alarm_times", alarmList.toSet())
//
//        for ((time, days) in alarmDaysMap) {
//            val key = "days_$time"
//            editor.putString(key, days.joinToString(",")) // Convert boolean array to comma-separated string
//        }
//
//        editor.apply()
//    }

//    private fun loadSavedAlarms() {
//        alarmList.clear()
//        alarmDaysMap.clear()
//        binding.alarmsContainer.removeAllViews()
//
//        val savedAlarms = sharedPreferences.getStringSet("alarm_times", emptySet()) ?: emptySet()
//        alarmList.addAll(savedAlarms)
//
//        for (time in alarmList) {
//            val daysString = sharedPreferences.getString("days_$time", "false,false,false,false,false,false,false")
//            val daysArray = daysString!!.split(",").map { it.toBoolean() }.toBooleanArray()
//            alarmDaysMap[time] = daysArray
//
//            val textView = TextView(requireContext()).apply {
//                text = time
//                textSize = 18f
//                setPadding(20, 10, 20, 10)
//                setOnClickListener { openWeekdaySelector(time) }
//            }
//
//            binding.alarmsContainer.addView(textView)
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
