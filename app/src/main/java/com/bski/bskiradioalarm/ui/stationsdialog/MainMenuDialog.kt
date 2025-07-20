package com.bski.bskiradioalarm.ui.stationsdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bski.bskiradioalarm.models.AlarmSettings

class MainMenuDialog(alarmSettings: AlarmSettings) : DialogFragment() {

    private val alarmSettings: AlarmSettings = alarmSettings
    private val sharedStationsViewModel: StationsViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedStationsViewModel.loadStations()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Station Options")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val firstButton = Button(requireContext()).apply {
            text = "Choose station"
            setOnClickListener {
                StationsUiSelect(alarmSettings).show(parentFragmentManager, "FormDialog1")
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
