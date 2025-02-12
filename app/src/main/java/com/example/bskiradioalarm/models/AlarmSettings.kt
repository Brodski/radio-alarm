package com.example.bskiradioalarm.models

import android.content.SharedPreferences
//import java.time.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
//import java.util.UUID

@Serializable
data class AlarmSettings(
    var id: String = System.currentTimeMillis().toString(),

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

        fun getAllSorted(sharedPreferences: SharedPreferences): LinkedHashMap<String, Any?> {
            val allEntries: Map<String, *> = sharedPreferences.all

//            for (entry in allEntries.entries) {
//                println("Original key: ${entry.key}, Parsed Long: ${entry.key.toLongOrNull()}")
//            }

            val sortedMapEntries: LinkedHashMap<String, Any?> = allEntries.entries
                .sortedBy { it.key.toLongOrNull() ?: Long.MAX_VALUE }
                .associateTo(LinkedHashMap()) { it.key to it.value }

//            for ((key, value) in sortedMapEntries) {
//                println("getAllSorted: " + "$key: $value")
//            }
            return sortedMapEntries
        }
    }
    public fun getAltId(): Int {
        return this.id.takeLast(9).toInt()
        // Int max size    =    2147483647
        // Sys.time = long = 1739286196625
    }
    private fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }

    public fun save(sharedPreferences: SharedPreferences){
        // Save in storage
        val jsonStr: String =  this.toJsonStringSerialize()
        sharedPreferences.edit().putString(this.id, jsonStr).apply()
    }

    public fun delete(sharedPreferences: SharedPreferences) {
        println("BEFORE---------------")
        for ((id, value) in sharedPreferences.all) {
            println(id)
        }

        val deleteId: String = this.id
        sharedPreferences.edit().remove(deleteId).apply()

        println("AFTER---------------")
        for ((id, value) in sharedPreferences.all) {
            println(id)
        }
    }
}