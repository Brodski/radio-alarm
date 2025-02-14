package com.example.bskiradioalarm.models

import android.content.Context

//I dont care that it's not idiomatic, fight me irl punk
interface IAlarmShared {
    fun setWakeUp(context: Context, alarmSettings: AlarmSettings)
}