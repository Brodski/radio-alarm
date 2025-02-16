package com.example.bskiradioalarm.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bskiradioalarm.models.Station

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _stations = MutableLiveData<List<Station>>()


    val stations: LiveData<List<Station>> get() = _stations

    fun loadStations() {
        _stations.value = listOf(
            Station("Station1", "https://www.station1.com"),
            Station("Station2", "https://www.station2.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
            Station("Station3", "https://www.station3.com"),
        )
    }
}