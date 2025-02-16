package com.example.bskiradioalarm.ui.dashboard

import StationAdapter
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.databinding.FragmentDashboardBinding
import com.example.bskiradioalarm.models.Station

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private lateinit var openDialogButton: Button

    private var selectedItem: String = "Select an Item"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // BUTTON
        binding.openDialogButton.setOnClickListener {
            showItemSelectionDialog()
        }

        dashboardViewModel.loadStations()

    }

    private fun onStationSelected(station: Station) {
        binding.openDialogButton.text = station.title  // Update button text with selected station
    }
    private fun onPlayStation(station: Station) {
//        Toast.makeText(context, "Tapped ${station.title}", Toast.LENGTH_SHORT).show()
        println("Tapped ${station}")
        println("context $context")
    }


    private fun showItemSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.list_stations, null)

        val listView: ListView = dialogView.findViewById(R.id.listView)

        println("showItemSelectionDialog: ")

        dashboardViewModel.stations.observe(viewLifecycleOwner, Observer { stations: List<Station> ->
            val adapter = StationAdapter(requireContext(), stations, ::onStationSelected, ::onPlayStation)
            listView.adapter = adapter
        })

        val dialog =AlertDialog.Builder(requireContext())
            .setTitle("Select a Station")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ -> dialog.dismiss() } // right align
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() } // left align, both Neg and Pos are right aligned and stupid
            .show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Makes it slight wider

        dialog.setOnCancelListener {
            println("User dismissed the dialog by tapping outside.")
        }
    }


//    private fun showItemSelectionDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_list, null)
//        val listView: ListView = dialogView.findViewById(R.id.listView)
//
//        val items = List(30) { "Item ${it + 1}" }
//        val adapter = ItemAdapter(requireContext(), items, ::onStationSelected)
//
//        listView.adapter = adapter
//
//        AlertDialog.Builder(requireContext()) // Using requireContext() inside a Fragment
//            .setTitle("Select an Item")
//            .setView(dialogView)
//            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//    private fun showCustomListDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.list_alert_dialog, null)
//        val listView: ListView = dialogView.findViewById(R.id.dialogListView)
//        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
//        val okButton: Button = dialogView.findViewById(R.id.okButton)
//
//        val dialog = AlertDialog.Builder(requireContext())
//            .setView(dialogView)
//            .create()
//
//        // Load stations from ViewModel
//        dashboardViewModel.stations.observe(viewLifecycleOwner) { stations ->
//            val adapter = StationAdapter(requireContext(), stations, ::onStationSelected)
//            listView.adapter = adapter
//        }
//
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        okButton.setOnClickListener {
//            println("OK Clicked")
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}