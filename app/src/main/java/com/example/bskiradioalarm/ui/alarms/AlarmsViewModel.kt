package com.example.bskiradioalarm.ui.alarms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

class AlarmsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    fun updateText(newText: String) {
        _timeDiff.value = newText
    }




    private val _timeDiff = MutableLiveData<String>()
    val timeDiff: LiveData<String> = _timeDiff

    private var startTime: Calendar? = null
    private var timer: Timer? = null

    fun startTimer(start: Calendar?, isUserEventGo: Boolean = true) {
        startTime = null
        startTime = start
        timer?.cancel()
        timer = Timer()
        scheduleNextTick(start, isUserEventGo)
    }

    private fun scheduleNextTick(nextAlarm: Calendar?, isUserEventGo: Boolean = false) {
        val alarmsLogic: AlarmsLogic = AlarmsLogic()
        val task = object : TimerTask() {
            override fun run() {
                val dayHourMin: Triple<Int,Int,Int> = alarmsLogic.convertToTripletIntDiff(nextAlarm)

                var msg = ""
                if (dayHourMin.first == 0) {
                    msg = "Next alarm in ${dayHourMin.second} hours, ${dayHourMin.third} min"
                }
                else if (dayHourMin.first == -1 ) {
                    msg = "No alarms scheduled my man"
                }
                else {
                    msg = "Alarm in ${dayHourMin.first} days, ${dayHourMin.second} hours, ${dayHourMin.third} min"
                }

                println(msg)
                _timeDiff.postValue(msg)

                scheduleNextTick(nextAlarm)
            }
        }
        var delay = 10000L // 10 seconds
        if (isUserEventGo) {
            delay = 0
        }
        timer?.schedule(task, delay)
    }


    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}