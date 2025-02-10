package com.example.bskiradioalarm.models

import android.content.SharedPreferences
//import java.time.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.UUID

@Serializable
data class AlarmSettings(
    var uuid: String = UUID.randomUUID().toString(),

    var hour: Int? = null,

    var minute: Int? = null,

    var daysOfWeek: MutableMap<String, Boolean> = mutableMapOf(
        "Monday" to false,
        "Tuesday" to false,
        "Wednesday" to false,
        "Thursday" to false,
        "Friday" to false,
        "Saturday" to false,
        "Sunday" to false
    ),
) {

    companion object {
        fun toAlarmDeserialize(jsonString: String): AlarmSettings {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }
    }
    private fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }
    public fun save(sharedPreferences: SharedPreferences, alarmSettingsMap: MutableMap<String, AlarmSettings>){
//        println("saving this:\n" + this.toJsonStringSerialize())

        // Save in storage
        val jsonStr: String =  this.toJsonStringSerialize()
        sharedPreferences.edit().putString(this.uuid, jsonStr).apply()

        // Save in memory
        alarmSettingsMap[this.uuid] = this
    }
}






