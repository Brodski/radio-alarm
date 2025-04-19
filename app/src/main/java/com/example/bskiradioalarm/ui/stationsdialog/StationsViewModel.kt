package com.example.bskiradioalarm.ui.stationsdialog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.utils.CoolConstantData
import com.example.bskiradioalarm.utils.RadioService

class StationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _stations = MutableLiveData<List<Station>>()
    val selectedStation = MutableLiveData<Station?>()
    private val sharedPreferences: SharedPreferences? = null

    val stations: LiveData<List<Station>> get() = _stations

    fun loadStations() {
        println("``````````````````loadStations")
        val allStationsStorage: LinkedHashMap<String, Station> = Station.getAllStations()

        val combinedList: List<Station> = allStationsStorage.values.toList()

        _stations.value = combinedList
    }

    fun getIndexByTitle(title: String?): Int {
        if (this.stations.value == null) {
            return -1;
        }
        for ((idx, station) in this.stations.value!!.withIndex()) {
            if (title == station.title) {
                println("GOT $idx: $station")
                return idx
            }
        }
        return -1
    }
}