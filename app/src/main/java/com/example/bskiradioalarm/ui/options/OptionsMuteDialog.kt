package com.example.bskiradioalarm.ui.options

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.Optionz

class OptionsMuteDialog : DialogFragment() {
    private lateinit var viewModel: OptionsViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewModel = ViewModelProvider(requireActivity())[OptionsViewModel::class.java]


        val dialogView = layoutInflater.inflate(R.layout.popup_options_mute, null)
        val numberPicker: NumberPicker = dialogView.findViewById(R.id.numberPicker)
        val values = (0..120 step 10).toList().map { it.toString() }.toTypedArray()

        numberPicker.minValue = 0
        numberPicker.maxValue = values.size - 1
        numberPicker.displayedValues = values
        numberPicker.wrapSelectorWheel = true

        // initial Value
        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val muteSecond: String? = optionsSharedPreferences.getString(Optionz.MUTE_STORAGE_PREF_KEY, "0")
        val initIndex = values.indexOf(muteSecond)
        numberPicker.value = if (initIndex != -1) initIndex else 0

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Start Alarm Muted (seconds):")
            .setMessage("Online stations often play ads for the first x seconds. Work around by starting your alarm muted.")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedValue = values[numberPicker.value]

                val editor: SharedPreferences.Editor = optionsSharedPreferences.edit()
                editor.putString(Optionz.MUTE_STORAGE_PREF_KEY, selectedValue)
                editor.commit()

                println(selectedValue)
                parentFragmentManager.setFragmentResult("muteDialogClosed", Bundle())
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        return dialog
    }
}