package com.example.bskiradioalarm.ui.options

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.Optionz
import com.example.bskiradioalarm.utils.CoolConstantData

class OptionsAddMoreDialog : DialogFragment() {
    private lateinit var viewModel: OptionsViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewModel = ViewModelProvider(requireActivity())[OptionsViewModel::class.java]


        val dialogView = layoutInflater.inflate(R.layout.popup_options_add_more, null)
        val loadMoreBtn: Button = dialogView.findViewById(R.id.loadMoreBtn)

        loadMoreBtn.setOnClickListener {
            println("woo!")
            for (station in CoolConstantData.stationLoadMoreList) {
                if (PreferencesManagerSingleton.stationsSharedPrefs.contains(station.id)) {
                    println("1 ☑ already saved: " + station.id)
                } else {
                    println("1 ❌ missing. will save: " + station.id)
                    station.save()
                }
            }
            dialog?.dismiss();
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add 12 new stations. Classical, Jazz, and News stations.")
            .setMessage("These can be deleted and will not conflict with your pre-existing stations.")
            .setView(dialogView)
//            .setPositiveButton("OK") { dialog, _ ->
//
//
//                parentFragmentManager.setFragmentResult("muteDialogClosed", Bundle())
//                dialog.dismiss()
//            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()



        return dialog
    }
}