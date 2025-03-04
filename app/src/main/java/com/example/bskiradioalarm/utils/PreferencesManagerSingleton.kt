import android.content.Context
import android.content.SharedPreferences
import com.example.bskiradioalarm.models.Station

object PreferencesManagerSingleton {
    private const val ALARM_STORAGE_NAME = "alarms_setting"
    private const val STATION_STORAGE_NAME = "station_setting"
    public lateinit var alarmsSharedPrefs: SharedPreferences
    public lateinit var stationsSharedPrefs: SharedPreferences

    fun init(context: Context) {
        alarmsSharedPrefs = context.getSharedPreferences(ALARM_STORAGE_NAME, Context.MODE_PRIVATE)
        stationsSharedPrefs = context.getSharedPreferences(STATION_STORAGE_NAME, Context.MODE_PRIVATE)
    }

    fun printAllAlarms() {
        println("ALARM PREFERENCE")
        println("ALARM PREFERENCE")
        println("ALARM PREFERENCE")
        for ((key, value) in this.alarmsSharedPrefs.all) {
            println("ALARM Key: $key, Value: $value")
        }
    }

    fun printAllStations() {
        println("STATION PREFERENCE")
        println("STATION PREFERENCE")
        println("STATION PREFERENCE")
        val allStationz:  LinkedHashMap<String, Station> = Station.getAllStations()

        for ((key, value) in allStationz) {
            println("STATION Key: $key, Value: $value")
        }
    }
}
