package com.bski.bskiradioalarm.ui.options

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bski.bskiradioalarm.R
import com.bski.bskiradioalarm.models.Optionz

class OptionsSnoozeDialog : DialogFragment() {
    private lateinit var viewModel: OptionsViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewModel = ViewModelProvider(requireActivity())[OptionsViewModel::class.java]


        val dialogView = layoutInflater.inflate(R.layout.popup_options_snooze, null)
        val numberPicker: NumberPicker = dialogView.findViewById(R.id.numberPicker2)
        val values = (1..20 step 1).toList().map { it.toString() }.toTypedArray()
        println("SNOOZE LOOP ")
        for (element in values) {
            println(element)
        }
        numberPicker.minValue = 1
        numberPicker.maxValue = values.size
        numberPicker.displayedValues = values
        numberPicker.wrapSelectorWheel = true

        // initial Value
        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val snoozeMinutes: String? = optionsSharedPreferences.getString(Optionz.SNOOZE_STORAGE_PREF_KEY, "5")
//        val initIndex = values.indexOf(snoozeMinutes)
//        numberPicker.value = if (initIndex != -1) initIndex + 1 else 5
        numberPicker.value = snoozeMinutes?.toInt() ?: 5

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Snooze (minutes):")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                println("values: " + values)
                println("numberPicker.value: " + numberPicker.value)
//                println("values[numberPicker.value]: " + numberPicker.value)
                val selectedValue = numberPicker.value.toString()

                val editor: SharedPreferences.Editor = optionsSharedPreferences.edit()
                editor.putString(Optionz.SNOOZE_STORAGE_PREF_KEY, selectedValue)
                editor.commit()

                println(selectedValue)
                parentFragmentManager.setFragmentResult("snoozeDialogClosed", Bundle())
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        return dialog
    }
}