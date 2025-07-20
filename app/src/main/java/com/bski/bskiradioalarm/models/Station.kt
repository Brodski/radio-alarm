package com.bski.bskiradioalarm.models

import PreferencesManagerSingleton
import android.content.SharedPreferences.Editor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Station(
    var title: String,
    var url: String,
    var id: String,
    var epochId: Long? = null,
    ) {
    constructor(title: String, url: String) : this(
        title,
        url,
        System.currentTimeMillis().toString(),
        System.currentTimeMillis()
    )

    constructor(title: String, url: String, epochId: Long) : this(
        title,
        url,
        epochId.toString(),
        epochId
    )

    private fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }

    public fun save() {
        val stationSharedPreferences = PreferencesManagerSingleton.stationsSharedPrefs
        val editor: Editor = stationSharedPreferences.edit()
        editor.putString(this.id, this.toJsonStringSerialize())
        editor.commit()
    }

    companion object {

        fun getAllStations(): LinkedHashMap<String, Station> {
            val allEntries: Map<String, *> = PreferencesManagerSingleton.stationsSharedPrefs.all

//            val sortedMapEntries: LinkedHashMap<String, Station> = allEntries.entries
//                .associateTo(LinkedHashMap()) { it.key to this.toStationDeserialize(it.value.toString()) }

            var counter1 = 0
            for (x in allEntries)
            {
                println("" + counter1 + ": " + x)
                counter1 = counter1 + 1
            }
            println("jaskdfjkalsdjflkjalsdjfljka")
            val sortedMapEntries: LinkedHashMap<String, Station> = allEntries.entries
                .map { it.key to this.toStationDeserialize(it.value.toString()) } // Convert to List<Pair<String, Station>>
                .sortedBy { it.second.epochId }
                .associateTo(LinkedHashMap()) { it.first to it.second }

//            println("CHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPT ")
//            println("CHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPT ")
//            println("CHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPT ")
//            println("CHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPTCHAGPT ")
//            println(sortedMapEntries)
            var counter = 0
            for (x in sortedMapEntries)
            {
                println("" + counter + ": " + x)
                counter = counter + 1
            }
            return sortedMapEntries
        }

        fun toStationDeserialize(jsonString: String): Station {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }

        fun getStationById(stationRefId: String): Station? {
            if (stationRefId == null || stationRefId == "") {
                println("User has not selected a station yet. Will probably eventually default to CPR, but until then, its gonna be empty")
                return null
            }
            try {
                println("--- Getting Station: "+ stationRefId)
                val jsonStr: String = PreferencesManagerSingleton.stationsSharedPrefs.getString(stationRefId, "-1").toString()
                val station: Station = Station.toStationDeserialize(jsonStr)
                return station
            }
            catch (e: Exception) {
                println("Caught an exception: ${e.message}")
                e.printStackTrace()
            }
            return null
        }
    }
}
