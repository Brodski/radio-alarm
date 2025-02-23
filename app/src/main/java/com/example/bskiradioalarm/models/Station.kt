package com.example.bskiradioalarm.models

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Station(
    val title: String,
    val url: String,
    var id: String = System.currentTimeMillis().toString(),
) {
    constructor(id: String) : this("", "", id)

    private fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }

    public fun save(stationSharedPreferences: SharedPreferences) {
        val editor: Editor = stationSharedPreferences.edit()
        editor.putString(this.id, this.toJsonStringSerialize())
        editor.apply()
    }

    companion object {

        fun getAllStations(stationsSharedPrefs: SharedPreferences): LinkedHashMap<String, Station> {
            val allEntries: Map<String, *> = stationsSharedPrefs.all

            val sortedMapEntries: LinkedHashMap<String, Station> = allEntries.entries
//                .sortedBy { it.key.toLongOrNull() ?: Long.MAX_VALUE }
                .associateTo(LinkedHashMap()) { it.key to this.toStationDeserialize(it.value.toString()) }
//                .filterValues { true } as LinkedHashMap<String, Station>

            return sortedMapEntries
        }

        public fun toStationDeserialize(jsonString: String): Station {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }
    }
}
