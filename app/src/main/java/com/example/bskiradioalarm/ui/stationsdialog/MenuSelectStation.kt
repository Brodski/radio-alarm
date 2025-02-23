package com.example.bskiradioalarm.ui.stationsdialog


import PreferencesManagerSingleton
import StationAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.utils.RadioService
import com.example.bskiradioalarm.utils.Scheduler
import com.example.bskiradioalarm.viewmodels.StationsViewModel
import java.util.Calendar

class MenuSelectStation(alarmSettings: AlarmSettings) : DialogFragment() {

    private val sharedStationsViewModel: StationsViewModel by activityViewModels()
    private val alarmSettings: AlarmSettings = alarmSettings

    private lateinit var scheduler: Scheduler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scheduler = Scheduler(context)
    }


//    companion object {
//        private const val ARG_ALARM_SETTINGS = "alarmSettings"
//
//        fun newInstance(alarmSettings: String): MenuSelectStation {
//            val fragment = MenuSelectStation()
//            val args = Bundle()
//            args.putString(ARG_ALARM_SETTINGS, alarmSettings)
//            fragment.arguments = args
//            return fragment
//        }
//    }

    @SuppressLint("DialogFragmentCallbacksDetector")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val jsonStr: String = arguments?.getString(ARG_ALARM_SETTINGS) ?: "No Data"
//        val alarmSettings: AlarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)

//        println("!!! SelectStation  $alarmSettings")

        val dialogView = layoutInflater.inflate(R.layout.list_stations, null)

        val listView: ListView = dialogView.findViewById(R.id.listView)

        val pos: Int = sharedStationsViewModel.getIndexByTitle(alarmSettings.station?.title)


        ////////////////////////////////////////
        // ROWS - POPULATE WITH MANY STATIONS //
        ////////////////////////////////////////
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select a station for\n" +  alarmSettings.prettyPrintTime() + " - " + alarmSettings.prettyDays())
//            .setMessage("Please choose a station from the list below.")
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, which -> handelConfirm(dialog, which) }
//            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setOnCancelListener { println("User dismissed the dialog by tapping outside.") }
            .create()
//            .show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Makes it slight wider

        dialog.setOnShowListener {
//            println("SHOW LISTENER: " + alarmSettings)
            sharedStationsViewModel.stations.observe(this, Observer { stations: List<Station> ->
                val adapter = StationAdapter(requireContext(),
                    stations,
                    sharedStationsViewModel,
                    sharedStationsViewModel.stationPreloadedList.size,
                    ::onStationSelected,
                    ::onPlayStation,
                    ::onDeleteLongPress,
                    alarmSettings)
                listView.adapter = adapter
            })
        }

        ////////////////////////
        // NEW STATION BUTTON //
        ////////////////////////
        val newStationBtn: Button = dialogView.findViewById(R.id.newStationButton)
        newStationBtn.setOnClickListener {
            println("CLICKED BTN")
            showNewStationForm(requireActivity())
        }

        return dialog
    }

    fun handelConfirm(dialog: DialogInterface, which: Int){
//        println("hello from confirm")
//        val selectedStation = sharedStationsViewModel.selectedStation.value
//        println("GOT STATION: " + selectedStation)
//        println("GOT STATION: " + selectedStation?.title)
        dialog.dismiss()
    }

    private fun onStationSelected(station: Station, alarmSettings: AlarmSettings) {
        println("onStationSelected CLICKED station.title $station")
        println("")
        alarmSettings.station = station
        alarmSettings.save(PreferencesManagerSingleton.alarmsSharedPrefs)

    }
    private fun onPlayStation(station: Station, mediaState: String) {
        println("Tapped ${station}")
        println("Playing " + station.title + " @ " + station.url)
//        val calendar = Calendar.getInstance()
//        val minute = calendar.get(Calendar.MINUTE)
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val day = calendar.get(Calendar.DAY_OF_WEEK)
//        val alarmSettings: AlarmSettings = AlarmSettings()
//        alarmSettings.minute = minute
//        alarmSettings.hour = hour
//        alarmSettings.daysOfWeek[AlarmSettings.getDayName(day)] = true

//        println("---PLAYING STATION:")
//        println("---mediaState: $mediaState")
        if ("playing".equals(mediaState)) {
            RadioService.startPreviewStation(requireContext(),station.url)
        }
        if ("paused".equals(mediaState)){
//            RadioService.startPreviewStation(requireContext(),"https://stream1.cprnetwork.org/cpr2_lo")
            RadioService.startPreviewStation(requireContext(),station.url)
        }

    }

    private fun onDeleteLongPress(stationDelete: Station) {
        AlertDialog.Builder(context)
            .setTitle("Remove ${stationDelete.title}?")
            .setMessage("")
            .setPositiveButton("Confirm") { dialog, _ ->
//                AlarmSettings.updateDeletedStation(station, sharedStationsViewModel.stationMap)
                val allAlarmz: LinkedHashMap<String, AlarmSettings> = AlarmSettings.getAllSorted(PreferencesManagerSingleton.alarmsSharedPrefs)
                val allStationz: LinkedHashMap<String, Station> = Station.getAllStations(PreferencesManagerSingleton.stationsSharedPrefs)
                for ((key, valAlarmSettings) in allAlarmz) {
                    println("(allAlarmz) key: " + key)
                    println("(allAlarmz) alarmSettings: " + valAlarmSettings)
                    if (valAlarmSettings.station?.id == stationDelete.id) {
                        println("(allAlarmz) WE HAVE A MATCH: " + valAlarmSettings.station?.id + " " + stationDelete.id)
                        valAlarmSettings.station = AlarmSettings.getDefaultStation(sharedStationsViewModel.stations.value ?: emptyList())
                    }
                }
                for ((key, station) in allStationz) {

                }
                val editor = PreferencesManagerSingleton.stationsSharedPrefs.edit()
                editor.remove(stationDelete.id)
                sharedStationsViewModel.loadStations()
//                alarmSettings.station = null
                dialog.dismiss()
                println("Removed " + stationDelete.title)
            }
            .setNeutralButton("cancel") { dialog, _ ->
                dialog.dismiss()
                println("Canceled delete " + stationDelete.title)
            }
            .show()

    }

    fun showNewStationForm(context: Context) {
        val formView = LayoutInflater.from(context).inflate(R.layout.new_station, null)

        val titleEle: EditText = formView.findViewById(R.id.titleId)
        val urlEle: EditText = formView.findViewById(R.id.urlId)

        AlertDialog.Builder(context)
            .setTitle("New station")
            .setView(formView)
            .setPositiveButton("Confirm") { dialog, _ ->
                val title = titleEle.text.toString()
                val url = urlEle.text.toString()

                println("got: $title $url")
                val newStation: Station = Station(title, url)
                newStation.save(PreferencesManagerSingleton.stationsSharedPrefs)
                sharedStationsViewModel.loadStations()
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}