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

    // 9:00am default
    var hour: Int = 9,
    var minute: Int = 0,

    var daysOfWeek: MutableMap<String, Boolean> = linkedMapOf(
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
    public fun save(sharedPreferences: SharedPreferences){
//        println("saving this:\n" + this.toJsonStringSerialize())

        // Save in storage
        val jsonStr: String =  this.toJsonStringSerialize()
        sharedPreferences.edit().putString(this.uuid, jsonStr).apply()

        // Save in memory
//        alarmSettingsMap[this.uuid] = this
    }
    public fun delete(sharedPreferences: SharedPreferences) {
        println("BEFORE---------------")
        for ((uuid, value) in sharedPreferences.all) {
            println(uuid)
        }

        val deleteUuid: String = this.uuid
        sharedPreferences.edit().remove(deleteUuid).apply()

        println("AFTER---------------")
        for ((uuid, value) in sharedPreferences.all) {
            println(uuid)
        }
    }
}






