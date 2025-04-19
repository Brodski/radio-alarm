package com.example.bskiradioalarm.ui.stationsdialog

import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.utils.RadioService
import android.content.Context
import com.example.bskiradioalarm.models.AlarmSettings

class StationsLogic(private val context: Context) {

    fun playStation(station: Station, mediaState: String) {
        println("Tapped X ${station}")
        println("Playing " + station.title + " @ " + station.url)
        if ("playing".equals(mediaState)) {
            RadioService.startPreviewStation(this.context, station.url)
        }
        if ("paused".equals(mediaState)){
            RadioService.startPreviewStation(this.context, station.url)
        }
    }

    fun deleteStation(stationDelete: Station, listStations : List<Station>?) {

        val editor = PreferencesManagerSingleton.stationsSharedPrefs.edit()
        println(" DELETING THIS STATION: " + stationDelete.id)
        editor.remove(stationDelete.id)
        editor.commit()
        println("Removed " + stationDelete.title + ". id: " + stationDelete.id)
    }

    fun stationSelected(station: Station, alarmSettings: AlarmSettings) {
        println("onStationSelected CLICKED station.title $station")
        alarmSettings.station = station
        alarmSettings.stationRef = station.id
        alarmSettings.save()

    }
}