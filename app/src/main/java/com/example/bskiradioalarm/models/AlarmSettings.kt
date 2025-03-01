package com.example.bskiradioalarm.models

//import java.time.LocalTime
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

    fun doQoLAlarmCheck(context: Context, view: View) {
        val dayHourMin: Triple<Int,Int,Int> = this.findNextAlarmEvent()
        var nextAlarmMsg = ""
        if (dayHourMin.first == 0) {
            nextAlarmMsg = "Next in ${dayHourMin.second} hours, ${dayHourMin.third} min"
        }
        else {
            nextAlarmMsg = "Next in ${dayHourMin.first} days, ${dayHourMin.second} hours, ${dayHourMin.third} min"
        }

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)

        if (currentVolume == 1) {
            nextAlarmMsg = " \uD83D\uDD07Alarm is SILENT!" + "\nSettings → Sound → Alarm Volume\n" + nextAlarmMsg // 🔊
            val snackbar: Snackbar = Snackbar.make(view, nextAlarmMsg, Snackbar.LENGTH_INDEFINITE)
            val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackbar.setAction("Dismiss") { snackbar.dismiss() }
            textView.maxLines = 5
            snackbar.show()
        }
        else {
            Toast.makeText(context, nextAlarmMsg, Toast.LENGTH_LONG).show()
        }
        println( "Alarm Volume: $currentVolume / $maxVolume")
        println("\uD83D\uDD0A Settings → Sound → Alarm Volume")
    }

    fun findNextAlarmEvent(): Triple<Int, Int, Int> {
        // Create a Calendar instance for next Tuesday at 12:10 PM
        var nowCal = Calendar.getInstance()
        var nextAlert = Calendar.getInstance() // Will set time to 12:10 PM

        nextAlert[Calendar.HOUR_OF_DAY] = this.hour
        nextAlert[Calendar.MINUTE] = this.minute

        println("+=========== START ============+")
        for (i in 1..7) {
            val nextDayNum = nextAlert[Calendar.DAY_OF_WEEK] // 7 = Saturday
            val nextDayName = AlarmSettings.getDayName(nextDayNum) // Saturday
            val nextIsOn = this.daysOfWeek[nextDayName]
            println("nextDayNum: " + nextDayName + " nextDayName: " + nextDayName + " nextIsOn: " +  nextIsOn)

            if (i == 1 && nextIsOn == true && nextAlert.before(nowCal)) { // we set hours and minutes few lines ago
                println("continue...")
                nextAlert.add(Calendar.DAY_OF_MONTH, 1);
                continue
            }
            if (nextIsOn == true){
                println("---> nextIsOn = TRUE")
                println("---> nextIsOn = TRUE")
                println("---> nextIsOn = TRUE")
                break
            }

            nextAlert.add(Calendar.DAY_OF_MONTH, 1);
        }

        println("+=========== END =============+")


//        while (nextAlert[Calendar.DAY_OF_WEEK] != Calendar.TUESDAY) {
//            System.out.println("nextAlert.DAY_OF_WEEK: " + nextAlert[Calendar.DAY_OF_WEEK]);
//            nextAlert.add(Calendar.DAY_OF_MONTH, 1);
//        }

        // Calculate the difference in milliseconds
        val diffMillis = nextAlert.timeInMillis - nowCal.timeInMillis

        // Convert to hours and minutes
        val msInSec = 1000
        val secInMin = 60
        val minInHour = 60
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        val hours = (diffMillis / (1000 * 60 * 60)).toInt() % 24
        val minutes = (diffMillis / (1000 * 60)).toInt() % 60
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        println("days: $days. hours $hours. min $minutes")
        return Triple(days, hours, minutes)
    }

}