package com.example.bskiradioalarm.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bskiradioalarm.models.Station

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _stations = MutableLiveData<List<Station>>()
    private var stationMap: Map<String, Station> = emptyMap()
    val selectedStation = MutableLiveData<Station?>()
    private val sharedPreferences: SharedPreferences? = null


    val stations: LiveData<List<Station>> get() = _stations

    fun loadStations() {
        if (_stations.value != null) {
            println("NOT NULLLL STATIONS")
        }
        val stationList = listOf(
            Station("Station1", "https://www.station1.com"),
            Station("Station2", "https://www.station2.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station4", "https://www.station4.com"),
            Station("Station5", "https://www.station5.com"),
            Station("Station6", "https://www.station6.com"),
            Station("Station7", "https://www.station7.com"),
            Station("Station8", "https://www.station8.com"),
            Station("Station9", "https://www.station9.com"),
            Station("Station10", "https://www.station10.com"),
            Station("Station11", "https://www.station11.com"),
            Station("Station12", "https://www.station12.com"),
            Station("Station13", "https://www.station13.com"),
            Station("Station14", "https://www.station14.com"),
            Station("Station15", "https://www.station15.com"),
            Station("Station16", "https://www.station16.com"),
        )
        
        _stations.value = stationList
        stationMap = stationList.associateBy { it.title }
//        {
//            "Station1" -> Station(title="Station1", url="https://www.station1.com"),
//            "Station2" -> Station(title="Station2", url="https://www.station2.com"),
//            "Station3" -> Station(title="Station3", url="https://www.station3.com")
//        }
    }

    fun getStationByTitle(title: String): Station? {
        return stationMap[title]
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