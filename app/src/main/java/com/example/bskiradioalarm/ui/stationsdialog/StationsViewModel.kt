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

//    val DEFAULT_RADIO_ID = "CPR_Classical_Id"


    val stationPreloadedList: List<Station> = CoolConstantData.stationPreloadedList
//    val stationPreloadedList: List<Station> = listOf(
//        Station("CPR Classical", "https://stream1.cprnetwork.org/cpr2_lo", RadioService.DEFAULT_RADIO_REF_ID),
//        Station("KUVO Jazz", "https://kuvo.streamguys1.com/kuvo-mp3-128", "idStation2"),
//        Station("Station3", "https://www.station3.com", "idStation3"),
//        Station("Station4", "https://www.station4.com", "idStation4"),
//        Station("Station5", "https://www.station5.com", "idStation5"),
//        Station("Station6", "https://www.station6.com", "idStation6"),
//        Station("Station7", "https://www.station7.com", "idStation7"),
//        Station("Station8", "https://www.station8.com", "idStation8"),
//        Station("Station9", "https://www.station9.com", "idStation9"),
//        Station("Station10", "https://www.station10.com", "idStation10"),
//        Station("Station11", "https://www.station11.com", "idStation11"),
//        Station("Station12", "https://www.station12.com", "idStation12"),
//        Station("Station13", "https://www.station13.com", "idStation13"),
//        Station("Station14", "https://www.station14.com", "idStation14"),
//        Station("Station15", "https://www.station15.com", "idStation15"),
//        Station("Station16", "https://www.station16.com", "idStation16"),
//    )

    fun loadStations() {
        println("``````````````````loadStations")
        val allStationsStorage: LinkedHashMap<String, Station> = Station.getAllStations()

//        val combinedList: List<Station> = this.stationPreloadedList + allStationsStorage.values
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