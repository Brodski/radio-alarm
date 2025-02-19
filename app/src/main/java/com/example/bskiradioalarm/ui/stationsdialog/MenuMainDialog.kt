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

class MenuMainDialog : DialogFragment() {

//    private val sharedStationsViewModel: StationsViewModel by activityViewModels()

    companion object {
        private const val ARG_ALARM_SETTINGS = "alarmSettings"

        fun newInstance(alarmSettings: String): MenuMainDialog {
            val fragment = MenuMainDialog()
            val args = Bundle()
            args.putString(ARG_ALARM_SETTINGS, alarmSettings)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val jsonStr: String = arguments?.getString(ARG_ALARM_SETTINGS) ?: "No Data"
        val alarmSettings: AlarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
        println("MainMenuDialog: Received alarmSettings: $alarmSettings")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Station Options")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val firstButton = Button(requireContext()).apply {
            text = "Choose station"
            setOnClickListener {
                MenuSelectStation.newInstance(jsonStr).show(parentFragmentManager, "FormDialog1")
//                FormDialog1().show(parentFragmentManager, "FormDialog1")
//                println("alarmSettings: " + alarmSettings)
//                menuSelectStation.showStationDialog(alarmSettings, dialogView, context, viewLifecycleOwner, sharedStationsViewModel)
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

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
//            val view = inflater.inflate(R.layout.dialog_main_menu, null)
//
//            // Buttons inside the popup
//            val btnForm1 = view.findViewById<Button>(R.id.btnForm1)
//            val btnForm2 = view.findViewById<Button>(R.id.btnForm2)
//            val btnForm3 = view.findViewById<Button>(R.id.btnForm3)
//
//            btnForm1.setOnClickListener {
//                FormDialog1().show(parentFragmentManager, "FormDialog1")
//            }
//            btnForm2.setOnClickListener {
//                FormDialog2().show(parentFragmentManager, "FormDialog2")
//            }
//            btnForm3.setOnClickListener {
//                FormDialog3().show(parentFragmentManager, "FormDialog3")
//            }
//
//            builder.setView(view)
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
//    }
}
