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
        timer?.cancel()
        timer = Timer()
        scheduleNextTick(isUserEventGo)
    }

    // TODO This code is copy and pasted @ doQoLAlarmToast
    private fun scheduleNextTick(isUserEventGo: Boolean = false) {
        val alarmsLogic: AlarmsLogic = AlarmsLogic()
        var nextAlarm: Calendar? = alarmsLogic.findNextAlarmFromAll()
        val task = object : TimerTask() {
            override fun run() {
                val dayHourMin: Triple<Int,Int,Int> = alarmsLogic.convertToTripletIntDiff(nextAlarm)

                var msg = ""
                if (dayHourMin.first == 0) {
                    msg = "Alarm in ${dayHourMin.second} hours, ${dayHourMin.third} min"
                }
                else if (dayHourMin.first == -1 ) {
                    msg = "No alarms scheduled"
                }
                else {
                    msg = "Alarm in ${dayHourMin.first} days, ${dayHourMin.second} hours, ${dayHourMin.third} min"
                }

                println(msg)
                _timeDiff.postValue(msg)

                scheduleNextTick()
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