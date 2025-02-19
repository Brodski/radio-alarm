import android.content.Context
import android.content.SharedPreferences

object PreferencesManagerSingleton {
    private const val ALARM_STORAGE_NAME = "alarms_setting"
    private const val STATION_STORAGE_NAME = "station_setting"
    public lateinit var alarmsSharedPrefs: SharedPreferences
    public lateinit var stationsSharedPrefs: SharedPreferences

    fun init(context: Context) {
        alarmsSharedPrefs = context.getSharedPreferences(ALARM_STORAGE_NAME, Context.MODE_PRIVATE)
        stationsSharedPrefs = context.getSharedPreferences(STATION_STORAGE_NAME, Context.MODE_PRIVATE)
    }

}
