package com.example.bskiradioalarm.models

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Station(
    val title: String,
    val url: String
) {

    private fun toJsonStringSerialize(): String {
        val json = Json { encodeDefaults = true; prettyPrint = true }
        return json.encodeToString(this)
    }

    companion object {

        public fun saveAStation(stationSharedPreferences: SharedPreferences, station: Station) {
            val editor: Editor = stationSharedPreferences.edit()
            editor.putString(station.title, station.toJsonStringSerialize())
//            editor.putString(station.title, "test123")
            editor.apply()
        }

        fun getAllStations(sharedPreferences: SharedPreferences): LinkedHashMap<String, Any?> {
            val allEntries: Map<String, *> = sharedPreferences.all

            val sortedMapEntries: LinkedHashMap<String, Any?> = allEntries.entries
                //        .sortedBy { it.key.toLongOrNull() ?: Long.MAX_VALUE }
                .associateTo(LinkedHashMap()) { it.key to it.value }

            return sortedMapEntries
        }
    }
}
