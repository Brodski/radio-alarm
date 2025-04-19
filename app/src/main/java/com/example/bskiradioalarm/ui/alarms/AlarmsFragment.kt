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
import androidx.lifecycle.ViewModelProvider
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.ui.options.OptionsViewModel
import com.example.bskiradioalarm.ui.stationsdialog.MainMenuDialog
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

    private val alarmsLogic = AlarmsLogic()

    private val noAlarmsMessageUi = "No alarms scheduled"

    private lateinit var alarmsViewModel: AlarmsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        val alarmsViewModel = ViewModelProvider(this)[AlarmsViewModel::class.java]
        alarmsViewModel = ViewModelProvider(this)[AlarmsViewModel::class.java]

        alarmsSharedPrefs = PreferencesManagerSingleton.alarmsSharedPrefs
        stationsSharedPrefs = PreferencesManagerSingleton.stationsSharedPrefs
        scheduler = Scheduler(requireContext())

        _binding = FragmentAlarmsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.addAlarmButton.setOnClickListener {
            onAddNewAlarm()
        }

        populateUiWithAlarmsInStorage()

        // Cool "Next Alarm" UI feature
        val messageNextAlarmTextView: TextView = binding.msgNextAlarm
        alarmsViewModel.timeDiff.observe(viewLifecycleOwner) {
            messageNextAlarmTextView.text = it
        }

        val nextAlarm: Calendar? = alarmsLogic.findNextAlarmFromAll()
        alarmsViewModel.startTimer(nextAlarm)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    ///////////////////////////////////////////////
    // Init Populating Stuff
    ///////////////////////////////////////////////
    private fun populateUiWithAlarmsInStorage() {
        val allAlarmsMap: LinkedHashMap<String, AlarmSettings> = AlarmSettings.getAllSorted()
        for ((keyId, alarmSettings) in allAlarmsMap) {
            println("(populating ui with alarms): ${alarmSettings.id}")
            addAlarmUi(alarmSettings)
        }
    }

    ///////////////////////////////////////////////
    // TAP NEW ALARM "+" BUTTON
    ///////////////////////////////////////////////
    private fun onAddNewAlarm() {
        println("addNewAlarm() ")

        val newAlarmSettings: AlarmSettings = AlarmSettings()

        alarmsLogic.addNewAlarm(newAlarmSettings)

        addAlarmUi(newAlarmSettings)
    }

    ///////////////////////////////////////////////
    // CLOCK POPUP
    // TAP TIME "9:00"
    ///////////////////////////////////////////////
    private fun openClockDialog(alarmSettings: AlarmSettings, isNew: Boolean = false) {
        val calendar = Calendar.getInstance()
        val hourUi: Int = alarmSettings.hour
        val minuteUi: Int = alarmSettings.minute

        var timeSelected = false // pro trick

        // CLOCK POPUP //
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            android.R.style.Theme_DeviceDefault_Dialog,
            { view, selectedHour, selectedMinute  -> // EVENT FOR WHEN TimeSet by user
                timeSelected = true
                alarmSettings.hour = selectedHour
                alarmSettings.minute = selectedMinute
            },
            hourUi, minuteUi, false
        )

        // EVENT LISTENERS //
        timePickerDialog.setOnDismissListener {
            // User clicked though
            if (timeSelected) {
                // logic
                alarmsLogic.completeTimeSelect(alarmSettings, scheduler)

                // ui
//                alarmsLogic.doQoLAlarmToast(alarmSettings, requireContext(), requireView())
                this.someUpdateUiShit()

                this.updateAlarmUi(alarmSettings)
            }
            // User canceled
            else {
                println("Dialog dismissed WITHOUT selection")
            }
        }
        timePickerDialog.show()
    }

    private fun someUpdateUiShit() {
        val nextAlarm: Calendar? = alarmsLogic.findNextAlarmFromAll()
        alarmsViewModel.startTimer(nextAlarm)

    }

    private fun updateAlarmUi(alarmSettings: AlarmSettings) {
        val container = uiAlarmsMap[alarmSettings.id]
        val hourMinLabel = container?.findViewWithTag<TextView>("alarm_time_text")
        if (hourMinLabel != null) {
            hourMinLabel.text = alarmSettings.prettyPrintTime()
        }
        println("new clock time: " + hourMinLabel?.text)
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
            setOnClickListener { MainMenuDialog(alarmSettings).show(parentFragmentManager, "MenuMainTag")}
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
        val dayIsOnMap = alarmSettings.daysOfWeek.entries.elementAt(i)

        alarmsLogic.toggleDayOnOff(alarmSettings, scheduler, dayIsOnMap.key, isChecked)

        alarmsLogic.doQoLAlarmToast(alarmSettings, requireContext(), requireView(), dayIsOnMap.key)

        this.someUpdateUiShit()
//        alarmsLogic.toastForSingle(alarmSettings, dayIsOnMap.key)

//        val nextAlarm: Calendar? = alarmsLogic.findNextAlarmFromAll()
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

                // data
                alarmsLogic.deleteAlarm(alarmSettings, scheduler)


                // ui
                (container.parent as? ViewGroup)?.removeView(container)
                uiAlarmsMap.remove(alarmSettings.id)

                this.someUpdateUiShit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

