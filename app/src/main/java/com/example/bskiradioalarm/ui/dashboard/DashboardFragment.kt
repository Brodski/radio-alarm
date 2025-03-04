package com.example.bskiradioalarm.ui.dashboard

import PreferencesManagerSingleton
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bskiradioalarm.databinding.FragmentDashboardBinding
import com.example.bskiradioalarm.ui.stationsdialog.StationsViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val stationsViewModel: StationsViewModel by viewModels()

    private lateinit var openDialogButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val stationsViewModel = ViewModelProvider(this).get(StationsViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.printAlarms.setOnClickListener {
            PreferencesManagerSingleton.printAllAlarms()
        }
        binding.printStations.setOnClickListener {
            PreferencesManagerSingleton.printAllStations()

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}