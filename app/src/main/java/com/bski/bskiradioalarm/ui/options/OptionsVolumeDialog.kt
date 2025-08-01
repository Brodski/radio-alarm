package com.bski.bskiradioalarm.ui.options

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bski.bskiradioalarm.R
import com.bski.bskiradioalarm.models.Optionz

class OptionsVolumeDialog : DialogFragment() {
    private lateinit var viewModel: OptionsViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        viewModel = ViewModelProvider(requireActivity())[OptionsViewModel::class.java]

        val dialogView = layoutInflater.inflate(R.layout.popup_options_volume, null)
        val numberPicker: NumberPicker = dialogView.findViewById(R.id.numberPicker3)
        val values = (3..max step 1).toList().map { it.toString() }.toTypedArray() // dont let the user go lower than volume=3

        numberPicker.minValue = 0
        numberPicker.maxValue = values.size - 1
        numberPicker.displayedValues = values
        numberPicker.wrapSelectorWheel = true

        println("numberPicker.minValue: " + numberPicker.minValue)
        println("numberPicker.maxValue: " + numberPicker.maxValue)
        println("numberPicker.displayedValues: " + numberPicker.displayedValues)
        println("numberPicker.wrapSelectorWheel: " + numberPicker.wrapSelectorWheel)

        // initial Value
        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val volumeInit: String? = optionsSharedPreferences.getString(Optionz.VOLUME_STORAGE_PREF_KEY, "7")
        val initIndex = values.indexOf(volumeInit)
        numberPicker.value = if (initIndex != -1) initIndex else 7

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Volume")
            .setMessage("(minimum: 3)")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedValue = values[numberPicker.value]

                val editor: SharedPreferences.Editor = optionsSharedPreferences.edit()
                editor.putString(Optionz.VOLUME_STORAGE_PREF_KEY, selectedValue)
                editor.commit()

                println("NEW VOLUME!!!" + selectedValue)
                parentFragmentManager.setFragmentResult("volumeDialogClosed", Bundle())
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        return dialog
    }
}