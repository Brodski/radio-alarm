package com.example.bskiradioalarm.ui.stationsdialog


import StationAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.utils.CoolConstantData
import com.example.bskiradioalarm.utils.Scheduler

class StationsUiSelect(alarmSettings: AlarmSettings) : DialogFragment() {

    private val sharedStationsViewModel: StationsViewModel by activityViewModels()
    private val alarmSettings: AlarmSettings = alarmSettings

    private lateinit var scheduler: Scheduler

    private lateinit var stationsLogic: StationsLogic

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scheduler = Scheduler(context)
        stationsLogic = StationsLogic(context)
    }


    @SuppressLint("DialogFragmentCallbacksDetector")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogView = layoutInflater.inflate(R.layout.popup_all_choose_stations, null)

        val listView: ListView = dialogView.findViewById(R.id.listView)

        val alarmsCurrentStation: Station? = alarmSettings.getStation()

        ////////////////////////////////////////
        // ROWS - POPULATE WITH MANY STATIONS //
        ////////////////////////////////////////
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select a station for\n" +  alarmSettings.prettyPrintTime() + " - " + alarmSettings.prettyDays())
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, which -> dialog.dismiss() }
            .setOnCancelListener { println("User dismissed the dialog by tapping outside.") }
            .create()

        dialog.setOnShowListener {
            sharedStationsViewModel.stations.observe(this, Observer { stations: List<Station> ->
                val adapter = StationAdapter(requireContext(),
                    stations,
                    sharedStationsViewModel,
//                    sharedStationsViewModel.stationPreloadedList.size,
                    CoolConstantData.stationPreloadedList.size,
                    ::onStationSelected,
                    ::onPlayStation,
                    ::onDeleteLongPress,
                    alarmSettings,
                    alarmsCurrentStation)
                listView.adapter = adapter
            })
        }

        ////////////////////////
        // NEW STATION BUTTON //
        ////////////////////////
        val newStationBtn: Button = dialogView.findViewById(R.id.newStationButton)
        newStationBtn.setOnClickListener {
            this.showNewStationForm(requireActivity())
        }

        return dialog
    }

    private fun onStationSelected(station: Station, alarmSettings: AlarmSettings) {
        stationsLogic.stationSelected(station, alarmSettings)

    }
    private fun onPlayStation(station: Station, mediaState: String) {
        stationsLogic.playStation(station, mediaState)
    }

    private fun onDeleteLongPress(stationDelete: Station) {
        AlertDialog.Builder(context)
            .setTitle("Remove ${stationDelete.title}?")
            .setMessage("")
            .setPositiveButton("Confirm") { dialog, _ ->

                stationsLogic.deleteStation(stationDelete, sharedStationsViewModel.stations.value)

                sharedStationsViewModel.loadStations()

                dialog.dismiss()
            }
            .setNeutralButton("cancel") { dialog, _ ->
                println("Canceled delete " + stationDelete.title)
                dialog.dismiss()
            }
            .show()

    }

    fun showNewStationForm(context: Context) {
        val formView = LayoutInflater.from(context).inflate(R.layout.popup_new_add_station, null)
        val titleEle: EditText = formView.findViewById(R.id.titleId)
        val urlEle: EditText = formView.findViewById(R.id.urlId)

        AlertDialog.Builder(context)
            .setTitle("New station")
            .setView(formView)
            .setPositiveButton("Confirm") { dialog, _ ->
                val title = titleEle.text.toString().trim()
                val url = urlEle.text.toString().trim()

                println("got: $title $url")
                val newStation: Station = Station(title, url)
                newStation.save()

                sharedStationsViewModel.loadStations()
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}