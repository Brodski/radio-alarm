package com.bski.bskiradioalarm.ui.options

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bski.bskiradioalarm.databinding.FragmentOptionsBinding
import com.bski.bskiradioalarm.models.Optionz


class OptionsFragment : Fragment() {

    private var _binding: FragmentOptionsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val notificationsViewModel = ViewModelProvider(this)[OptionsViewModel::class.java]

        _binding = FragmentOptionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val muteView: TextView = binding.muteInfo
        val snoozeView: TextView = binding.snoozeInfo
        val volumeView: TextView = binding.volumeInfo

        // populate textview, alternative style
        updateUiMuteMessage(muteView)
        updateUiSnoozeMessage(snoozeView)
        updateUiVolumeMessage(volumeView)

        // trick to do something after dialog
        // MUTE
        parentFragmentManager.setFragmentResultListener("muteDialogClosed", viewLifecycleOwner) { _, _ ->
            updateUiMuteMessage(muteView)
        }
        // SNOOZE
        parentFragmentManager.setFragmentResultListener("snoozeDialogClosed", viewLifecycleOwner) { _, _ ->
            updateUiSnoozeMessage(snoozeView)
        }
        // VOLUME
        parentFragmentManager.setFragmentResultListener("volumeDialogClosed", viewLifecycleOwner) { _, _ ->
            updateUiVolumeMessage(volumeView)
        }


        // Edit Volume
        var volumeWheelBtn: Button = binding.volumeWheelBtn
        volumeWheelBtn.setOnClickListener {
            OptionsVolumeDialog().show(parentFragmentManager, "volumeShit")
        }

        // Edit Mute button
        var muteWheelBtn: Button = binding.muteWheelBtn
        muteWheelBtn.setOnClickListener {
            OptionsMuteDialog().show(parentFragmentManager, "mutedatshit")
        }

        // Edit Snooze Time
        var snoozeWheelBtn: Button = binding.snoozeWheelBtn
        snoozeWheelBtn.setOnClickListener {
            OptionsSnoozeDialog().show(parentFragmentManager, "snoozeTimerz")
        }

        // Add More Stations Btn
        var loadMoreBtn: Button = binding.loadMoreBtn
        loadMoreBtn.setOnClickListener {
            OptionsAddMoreDialog().show(parentFragmentManager, "addMore")
        }

//        // Edit Alarm Sound
//        var soundSettingsBtn: Button = binding.soundSettingsBtn
//        soundSettingsBtn.setOnClickListener {
//            val intent = Intent(Settings.ACTION_SOUND_SETTINGS)
//            startActivity(intent)
//        }


        return root
    }

    private fun updateUiMuteMessage(muteView: TextView) {
        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val mute_second: String? = optionsSharedPreferences.getString(Optionz.MUTE_STORAGE_PREF_KEY, "0")
        muteView.text = "Muted for $mute_second seconds"
    }

    private fun updateUiSnoozeMessage(snoozeView: TextView) {
        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val snooze_minutes: String? = optionsSharedPreferences.getString(Optionz.SNOOZE_STORAGE_PREF_KEY, "5")
        snoozeView.text = "Snooze for $snooze_minutes minutes"
    }

    private fun updateUiVolumeMessage(volumeView: TextView) {
        val optionsSharedPreferences = PreferencesManagerSingleton.optionsSharedPrefs
        val volume: String? = optionsSharedPreferences.getString(Optionz.VOLUME_STORAGE_PREF_KEY, "7")
        volumeView.text = "Volume: $volume"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}