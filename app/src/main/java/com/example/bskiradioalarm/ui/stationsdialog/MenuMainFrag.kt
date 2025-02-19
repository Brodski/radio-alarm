package com.example.bskiradioalarm.ui.stationsdialog

import PreferencesManagerSingleton
import StationAdapter
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.viewmodels.StationsViewModel

class MenuMainFrag(alarmSettings: AlarmSettings): DialogFragment() {

    val alarmSettings: AlarmSettings = alarmSettings
//    val dialogView: View = dialogView

    private val sharedStationsViewModel: StationsViewModel by activityViewModels()
    lateinit var stationsSharedPrefs: SharedPreferences
    lateinit var menuSelectStation: MenuSelectStation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stationsSharedPrefs = PreferencesManagerSingleton.stationsSharedPrefs
        menuSelectStation = MenuSelectStation()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = layoutInflater.inflate(R.layout.list_stations, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Station Options")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val firstButton = Button(requireContext()).apply {
            text = "Choose station"
            setOnClickListener {
                println("alarmSettings: " + alarmSettings)
//                menuSelectStation.showStationDialog(alarmSettings, dialogView, context, viewLifecycleOwner, sharedStationsViewModel)
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



    private fun showStationDialog(alarmSettings: AlarmSettings) {
        println("CLICKED ICON STATION: " + alarmSettings)
        val dialogView = layoutInflater.inflate(R.layout.list_stations, null)

        val listView: ListView = dialogView.findViewById(R.id.listView)

        val pos: Int = sharedStationsViewModel.getIndexByTitle(alarmSettings.station?.title)
        println("showStationDialog: " + alarmSettings)
        println("station pos: " + pos)

        sharedStationsViewModel.stations.observe(viewLifecycleOwner, Observer { stations: List<Station> ->
            val adapter = StationAdapter(requireContext(), stations, sharedStationsViewModel, ::onStationSelected, ::onPlayStation, alarmSettings)
            listView.adapter = adapter
        })

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select a Station: " +  alarmSettings.prettyPrintTime())
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, which -> handelConfirm(dialog, which) } // right align
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() } // left align, both Neg and Pos are right aligned and stupid
            .show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Makes it slight wider

        dialog.setOnCancelListener {
            println("User dismissed the dialog by tapping outside.")
        }
    }

    private fun onStationSelected(station: Station, alarmSettings: AlarmSettings) {
        println("station.title $station")
        println("alarmSettings  $alarmSettings")
        println("")
        alarmSettings.station = station
        Station.saveAStation(stationsSharedPrefs, station)

    }
    private fun onPlayStation(station: Station) {
        println("Tapped ${station}")
        println("Playing " + station.title + " @ " + station.url)
        val x: LinkedHashMap<String, Any?> = Station.getAllStations(stationsSharedPrefs)
        println("x")
        println(x)
    }
        fun handelConfirm(dialog: DialogInterface, which: Int){
            println("hello from confirm")
            val selectedStation = sharedStationsViewModel.selectedStation.value
            println("GOT STATION: " + selectedStation)
            println("GOT STATION: " + selectedStation?.title)
            dialog.dismiss()
        }
}