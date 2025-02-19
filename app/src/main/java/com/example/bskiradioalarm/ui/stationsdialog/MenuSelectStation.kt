package com.example.bskiradioalarm.ui.stationsdialog


import StationAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.viewmodels.StationsViewModel

class MenuSelectStation : DialogFragment() {

    private val sharedStationsViewModel: StationsViewModel by activityViewModels()

    companion object {
        private const val ARG_ALARM_SETTINGS = "alarmSettings"

        fun newInstance(alarmSettings: String): MenuSelectStation {
            val fragment = MenuSelectStation()
            val args = Bundle()
            args.putString(ARG_ALARM_SETTINGS, alarmSettings)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("DialogFragmentCallbacksDetector")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val jsonStr: String = arguments?.getString(ARG_ALARM_SETTINGS) ?: "No Data"
        val alarmSettings: AlarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
        println("MenuSelectStation Received alarmSettings: $alarmSettings")

        val dialogView = layoutInflater.inflate(R.layout.list_stations, null)

//        return AlertDialog.Builder(requireContext())
//            .setView(dialogView)
//            .create()
//

        val listView: ListView = dialogView.findViewById(R.id.listView)

        val pos: Int = sharedStationsViewModel.getIndexByTitle(alarmSettings.station?.title)
        println("showStationDialog: " + alarmSettings)
        println("station pos: " + pos)

//        sharedStationsViewModel.stations.observe(viewLifecycleOwner, Observer { stations: List<Station> ->
//            val adapter = StationAdapter(requireContext(), stations, sharedStationsViewModel, ::onStationSelected, ::onPlayStation, alarmSettings)
//            listView.adapter = adapter
//        })


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select a Station: " +  alarmSettings.prettyPrintTime())
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, which -> handelConfirm(dialog, which) }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setOnCancelListener { println("User dismissed the dialog by tapping outside.") }
            .create()
//            .show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Makes it slight wider
//        dialog.setOnCancelListener { println("User dismissed the dialog by tapping outside.") }
//        val d2 = dialog.create()
        dialog.setOnShowListener {
            sharedStationsViewModel.stations.observe(this, Observer { stations: List<Station> ->
                val adapter = StationAdapter(requireContext(), stations, sharedStationsViewModel, ::onStationSelected, ::onPlayStation, alarmSettings)
                listView.adapter = adapter
            })
        }

        return dialog
//        return activity?.let {
//            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
////            val view = inflater.inflate(R.layout.dialog_form1, null)
//
//            builder.setView(view)
//                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun showStationDialog(alarmSettings: AlarmSettings) {
        println("CLICKED ICON STATION: " + alarmSettings)
    }
    fun handelConfirm(dialog: DialogInterface, which: Int){
        println("hello from confirm")
        val selectedStation = sharedStationsViewModel.selectedStation.value
        println("GOT STATION: " + selectedStation)
        println("GOT STATION: " + selectedStation?.title)
        dialog.dismiss()
    }

    private fun onStationSelected(station: Station, alarmSettings: AlarmSettings) {
        println("station.title $station")
        println("alarmSettings  $alarmSettings")
        println("")
        alarmSettings.station = station
//        Station.saveAStation(stationsSharedPrefs, station)

    }
    private fun onPlayStation(station: Station) {
        println("Tapped ${station}")
        println("Playing " + station.title + " @ " + station.url)
//        val x: LinkedHashMap<String, Any?> = Station.getAllStations(stationsSharedPrefs)
//        println("x")
//        println(x)
    }
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
////            val view = inflater.inflate(R.layout.dialog_form1, null)
//
//            builder.setView(view)
//                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
//    }
}