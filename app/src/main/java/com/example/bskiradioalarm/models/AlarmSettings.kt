package com.example.bskiradioalarm.models

import android.content.SharedPreferences
import com.example.bskiradioalarm.utils.Scheduler
//import java.time.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

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

    var station: Station? = null

) {

    companion object {
        fun getAllSorted(alarmsSharedPrefs: SharedPreferences): LinkedHashMap<String, AlarmSettings> {
            val allEntries: Map<String, *> = alarmsSharedPrefs.all

            val sortedMapEntries: LinkedHashMap<String, AlarmSettings> = allEntries.entries
                .sortedBy { it.key.toLongOrNull() ?: Long.MAX_VALUE }
                .associateTo(LinkedHashMap()) { it.key to this.toAlarmDeserialize(it.value.toString()) }
//                .filterValues { true } as LinkedHashMap<String, AlarmSettings>
            return sortedMapEntries
        }
        fun getDayAsInt(day: String): Int {
            val intDay = when (day.lowercase()) {
                "sunday" -> Calendar.SUNDAY       // 1
                "monday" -> Calendar.MONDAY       // 2
                "tuesday" -> Calendar.TUESDAY     // 3
                "wednesday" -> Calendar.WEDNESDAY // 4
                "thursday" -> Calendar.THURSDAY   // 5
                "friday" -> Calendar.FRIDAY       // 6
                "saturday" -> Calendar.SATURDAY   // 7
                else -> throw IllegalArgumentException("Invalid day name: $day")
            }
            return intDay
        }
        fun getDayName(dayInt: Int): String {
            return when (dayInt) {
                Calendar.SUNDAY -> "Sunday"
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                else -> "Unknown"
            }
        }

        fun toAlarmDeserialize(jsonString: String): AlarmSettings {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }
//        fun updateDeletedStation(station: Station) {
//            this.getAllSorted()
//        }
        fun getDefaultStation(listStations: List<Station>): Station? {
            if (listStations.isNullOrEmpty()) {
                println("listStations null wtf")
                println("listStations null wtf")
                println("listStations null wtf")
                println("listStations null wtf")
                println("listStations null wtf")
                println("listStations null wtf")
                return null
            }
            return listStations[0]
        }

    }


    public fun prettyPrintTime(): String {
        val time = String.format("%02d:%02d", this.hour, this.minute)
        return time
    }
    public fun getRequestCode(day: String): Int {
        val idX: String = day.toLowerCase() + this.id
        val hashed: Int = idX.hashCode()
        return hashed
    }
    public fun prettyDays(): String {
        var prettyList: MutableList<String> = mutableListOf<String>()
        for ((key, isOn) in this.daysOfWeek) {
            if (!isOn) {
                continue
            }
            if (key.lowercase() in listOf("thursday", "tuesday", "saturday", "sunday")) {
                prettyList.add(key.take(2))
            }
            else {
                prettyList.add(key.take(1))
            }
        }
        val title: String = if (prettyList.isNullOrEmpty()) {"Disabled"} else {prettyList.joinToString(", ")}
        return title
    }
//    public fun getAltId(): Int {
//        return this.id.takeLast(9).toInt()
//        // Int max size    =    2147483647
//        // Sys.time = long = 1739286196625
//    }

    public fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }

    public fun save(sharedPreferences: SharedPreferences){
        // Save in storage
        val jsonStr: String =  this.toJsonStringSerialize()
        sharedPreferences.edit().putString(this.id, jsonStr).apply()
    }

    public fun delete(sharedPreferences: SharedPreferences, scheduler: Scheduler) {
        for (entry: Map.Entry<String, Boolean> in this.daysOfWeek) {
            val dayKey: String = entry.key
            val isOn: Boolean = entry.value
            println("Delete $dayKey${this.id}")
            scheduler.cancelAlarm(this, dayKey)
        }
        val deleteId: String = this.id
        sharedPreferences.edit().remove(deleteId).apply()
    }


    fun updateTime(scheduler: Scheduler) {
        for (entry: Map.Entry<String, Boolean> in this.daysOfWeek) {
            val dayKey: String = entry.key
            val isOn: Boolean = entry.value
            println("Updating $dayKey${this.id} - $isOn")
            scheduler.cancelAlarm(this, dayKey) // We cancel it or cancel nothing.
            scheduler.setWakeUp2(this, dayKey)  // Then enable it (but only if 'isOn')
        }
    }

}