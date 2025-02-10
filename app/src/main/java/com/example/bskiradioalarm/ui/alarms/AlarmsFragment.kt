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
import com.example.bskiradioalarm.models.AlarmSettings
import java.time.LocalTime
import java.time.format.TextStyle

class AlarmsFragment : Fragment() {

    private var _binding: FragmentAlarmsBinding? = null

    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    private val alarmSettingsList = mutableListOf<AlarmSettings>()
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
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            android.R.style.Theme_DeviceDefault_Dialog,
            ::onTimeSelected,
            hour, minute, false
        )
        timePickerDialog.show()
    }
    private fun onTimeSelected(view: TimePicker, selectedHour: Int, selectedMinute: Int) {
        val uuid = UUID.randomUUID().toString()
        val selectedTime: LocalTime = LocalTime.of(selectedHour, selectedMinute)
        val alarmSettings: AlarmSettings = AlarmSettings()
//        alarmSettings.wakeTime = selectedTime
        alarmSettings.uuid = uuid
        println("here we go !!!!!!")
        var jsonAll = AlarmSettings.savePrepViaString(alarmSettings)
        println("jsonAll")
        println(jsonAll)

        val textView = TextView(requireContext()).apply { // Create a new TextView dynamically
            text = String.format("%02d:%02d", selectedHour, selectedMinute)
            textSize = 18f
            setPadding(20, 10, 20, 10)
            setOnClickListener { showWeekdaySelector(alarmSettings) }
        }
        binding.alarmsContainer.addView(textView) // Add to UI


//        val timeUi = String.format("%02d:%02d", selectedHour, selectedMinute)
//        binding.alarmTextView.text = timeUi  // Update UI
    }

    private fun showWeekdaySelector(alarmSettings: AlarmSettings) {


        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
            for ((day, isOn) in alarmSettings.daysOfWeek) {
                println("$day: $isOn")
//                val dayString = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                val dayString = day
                val checkBox = CheckBox(requireContext()).apply {
                    text = dayString
                    isChecked = isOn
                    setOnCheckedChangeListener { _, isChecked ->
                        alarmSettings.daysOfWeek[day] = isChecked
//                        saveAlarms()
                    }
                }
                addView(checkBox)
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Days for alarmTime")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ -> }
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
//                setOnClickListener { showWeekdaySelector(time) }
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
