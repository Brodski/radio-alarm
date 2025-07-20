package com.example.bskiradioalarm.models

//import java.time.LocalTime
import PreferencesManagerSingleton
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.bskiradioalarm.utils.RadioService
import com.example.bskiradioalarm.utils.Scheduler
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar

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

    var station: Station? = null,

//    var stationRef: String = ""
    var stationRef: String = RadioService.DEFAULT_RADIO_REF_ID

) {

    companion object {

        fun getAllSorted(): LinkedHashMap<String, AlarmSettings> {
            val allEntries: Map<String, *> = PreferencesManagerSingleton.alarmsSharedPrefs.all

            val sortedMapEntries: LinkedHashMap<String, AlarmSettings> = allEntries.entries
                .sortedBy { it.key.toLongOrNull() ?: Long.MAX_VALUE }
                .associateTo(LinkedHashMap()) { it.key to this.toAlarmDeserialize(it.value.toString()) }
//                .filterValues { true } as LinkedHashMap<String, AlarmSettings>
            return sortedMapEntries
        }

        fun getDayAsInt(day: String): Int {
            val intDay = when (day.lowercase()) {
                "sunday"     -> Calendar.SUNDAY    // 1
                "monday"     -> Calendar.MONDAY    // 2
                "tuesday"    -> Calendar.TUESDAY   // 3
                "wednesday"  -> Calendar.WEDNESDAY // 4
                "thursday"   -> Calendar.THURSDAY  // 5
                "friday"     -> Calendar.FRIDAY    // 6
                "saturday"   -> Calendar.SATURDAY  // 7
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

        fun getAlarmById(alarmId: String): AlarmSettings? {
            try {
                val jsonStr: String = PreferencesManagerSingleton.alarmsSharedPrefs.getString(alarmId, "-1").toString()
                if (jsonStr == "-1") {
                    return null
                }
                val alarmSettings = AlarmSettings.toAlarmDeserialize(jsonStr)
                return alarmSettings
            }
            catch (e: Exception) {
                println("Caught an exception: ${e.message}")
                e.printStackTrace()
            }
            return null
        }

    }


    public fun prettyPrintTime(): String {
        val time = String.format("%02d:%02d", this.hour, this.minute)
        return time
    }

    public fun getRequestCode(day: String): Int {
        val idX: String = day.toLowerCase() + this.id
        val hashed: Int = idX.hashCode()
//        println("GET REQUEST CODE: $idX")
        return hashed
    }

    public fun prettyDays(): String {
        var prettyList: MutableList<String> = mutableListOf<String>()
        for ((key, isOn) in this.daysOfWeek) {
            if (!isOn) {
                continue
            }
            prettyList.add(key.take(3))
        }
        val title: String = if (prettyList.isNullOrEmpty()) {"Disabled"} else {prettyList.joinToString(", ")}
        return title
    }

    public fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }

    public fun save(){
        // Save in storage
        val sharedPreferences: SharedPreferences = PreferencesManagerSingleton.alarmsSharedPrefs
        val jsonStr: String =  this.toJsonStringSerialize()
        sharedPreferences.edit().putString(this.id, jsonStr).apply()
    }

    public fun delete(scheduler: Scheduler) {
        for (entry: Map.Entry<String, Boolean> in this.daysOfWeek) {
            val dayKey: String = entry.key
            val isOn: Boolean = entry.value
            println("Delete $dayKey${this.id}")
            scheduler.cancelAlarm(this, dayKey)
        }
        val deleteId: String = this.id
        PreferencesManagerSingleton.alarmsSharedPrefs.edit().remove(deleteId).apply()
    }


    public fun updateTime(scheduler: Scheduler) {
        for (entry: Map.Entry<String, Boolean> in this.daysOfWeek) {
            val dayKey: String = entry.key
            val isOn: Boolean = entry.value
            println("Updating $dayKey${this.id} - $isOn")
            scheduler.cancelAlarm(this, dayKey) // We cancel it or cancel nothing.
            scheduler.setWakeUp2(this, dayKey)  // Then enable it (but only if 'isOn')
        }
    }

    public fun getStation(stationRef: String = this.stationRef): Station? {
        return Station.getStationById(this.stationRef)
    }

    public fun findNextAlarmEvent(): Calendar? {
        var nowCal = Calendar.getInstance()
        nowCal.add(Calendar.MINUTE, 1)
        var nextAlert = Calendar.getInstance()

        nextAlert[Calendar.HOUR_OF_DAY] = this.hour
        nextAlert[Calendar.MINUTE] = this.minute

        var isOnToday = false
        for (i in 1..7) {
            var nextDayNum = nextAlert[Calendar.DAY_OF_WEEK] // eg. 7 = Saturday
            val nextDayName = AlarmSettings.getDayName(nextDayNum) // eg. Saturday
            val nextIsOn = this.daysOfWeek[nextDayName]

//            println("???? nextDayName, nextIsOn:" + nextDayName + ", " + nextIsOn )
//            println("???? nextDayName, nextAlert.time:" + nextAlert.time )
//            println("???? nextDayName, nowCal.time:" + nowCal.time)
            if (i == 1 && nextIsOn == true && nextAlert.before(nowCal)) { // we set hours and minutes few lines ago
//                println("???? CONTINUE b/c is same day, but behind:")
                nextAlert.add(Calendar.DAY_OF_MONTH, 1);
                isOnToday = true
                continue
            }
            if (nextIsOn == true){
                break
            }

            nextAlert.add(Calendar.DAY_OF_MONTH, 1);

            if ( i == 7 && nextIsOn == false && isOnToday == true) {
//                println("???? Alarm must be on in 7 days from now")
                break
            }
            if ( i == 7 && nextIsOn == false) {
//                println("???? all alarms are disabled")
                return null
            }
        }
        return nextAlert
    }

}