package com.example.bskiradioalarm.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bskiradioalarm.databinding.FragmentDashboardBinding
import com.example.bskiradioalarm.viewmodels.StationsViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val stationsViewModel: StationsViewModel by viewModels()

    private lateinit var openDialogButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val stationsViewModel = ViewModelProvider(this).get(StationsViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        // BUTTON
////        binding.openDialogButton.setOnClickListener {
////            showItemSelectionDialog()
////        }
////
////        dashboardViewModel.loadStations()
//
//    }
//
//    private fun onStationSelected(station: Station, alarmSettings: AlarmSettings) {
//        binding.openDialogButton.text = station.title  // Update button text with selected station
//    }
//    private fun onPlayStation(station: Station) {
////        Toast.makeText(context, "Tapped ${station.title}", Toast.LENGTH_SHORT).show()
//        println("Tapped ${station}")
//        println("context $context")
//    }
//
//
//    private fun showItemSelectionDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.list_stations, null)
//
//        val listView: ListView = dialogView.findViewById(R.id.listView)
//
//        println("dashboard dialog: " + dashboardViewModel.stations)
//
//        var alarmHack = AlarmSettings()
//        dashboardViewModel.stations.observe(viewLifecycleOwner, Observer { stations: List<Station> ->
//            val adapter = StationAdapter(requireContext(), stations, dashboardViewModel, ::onStationSelected, ::onPlayStation, alarmHack)
//            listView.adapter = adapter
//        })
//
//        val dialog = AlertDialog.Builder(requireContext())
//            .setTitle("Select a Station")
//            .setView(dialogView)
//            .setPositiveButton("Save") { dialog, which -> handelConfirm(dialog, which) }
//            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Makes it slight wider
//
//        dialog.setOnCancelListener {
//            println("User dismissed the dialog by tapping outside.")
//        }
//    }
//    fun handelConfirm(dialog: DialogInterface, which: Int){
//        println("hello from confirm")
//        val selectedStation = dashboardViewModel.selectedStation.value
//        println("GOT STATION: " + selectedStation)
//        println("GOT STATION: " + selectedStation?.title)
//        dialog.dismiss()
//
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}