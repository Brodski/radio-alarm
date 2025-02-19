package com.example.bskiradioalarm.ui.stationsdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.viewmodels.StationsViewModel

class MenuMainDialog(alarmSettings: AlarmSettings) : DialogFragment() {

//    private val sharedStationsViewModel: StationsViewModel by activityViewModels()
    private val alarmSettings: AlarmSettings = alarmSettings

//    companion object {
//        private const val ARG_ALARM_SETTINGS = "alarmSettings"
//
//        fun newInstance(alarmSettings: String): MenuMainDialog {
//            val fragment = MenuMainDialog()
//            val args = Bundle()
//            args.putString(ARG_ALARM_SETTINGS, alarmSettings)
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val jsonStr: String = arguments?.getString(ARG_ALARM_SETTINGS) ?: "No Data"
//        val alarmSettings: AlarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
        println("!!! MainMenuDialog: $alarmSettings")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Station Options")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val firstButton = Button(requireContext()).apply {
            text = "Choose station"
            setOnClickListener {
                MenuSelectStation(alarmSettings).show(parentFragmentManager, "FormDialog1")
            }
        }

        val secondButton = Button(requireContext()).apply {
            text = "Open Second Dialog"
            setOnClickListener {
//                SecondDialogFragment().show(parentFragmentManager, "SecondDialog")
            }
        }

        layout.addView(firstButton)
        layout.addView(secondButton)

        builder.setView(layout)
        return builder.create()
    }

}
