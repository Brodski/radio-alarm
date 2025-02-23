import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.AlarmSettings
import com.example.bskiradioalarm.models.Station
import com.example.bskiradioalarm.viewmodels.StationsViewModel

class StationAdapter(
    private val context: Context,
    private val stations: List<Station>,
    private val viewModel: StationsViewModel,
    private val hideFirstN: Int,
    private val onStationSelected: (Station, AlarmSettings) -> Unit,
    private val onPlayStation: (Station, String) -> Unit,
    private val onDeleteLongPress: (Station) -> Unit,
    private val alarmSettings: AlarmSettings,
    ) : BaseAdapter() {

    // we need this, stupid that I have to write it -.- yet never call it
    override fun getCount(): Int = stations.size
    override fun getItem(position: Int): Any = stations[position]
    override fun getItemId(position: Int): Long = position.toLong()

    private var selectedIndex: Int = -1
    private var playIndex: Int = -1
    private val playList = mutableListOf<ImageButton>()
    private val viewList = mutableListOf<View>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        if (position < hideFirstN) {
            viewHolder.deleteBtn.visibility = View.GONE
        } else {
            viewHolder.deleteBtn.visibility = View.VISIBLE
        }

        // preloaded UI, highlight selected
        if (selectedIndex == -1) {
            selectedIndex = viewModel.getIndexByTitle(this.alarmSettings.station?.title)
        }


        val station = stations[position]
        viewHolder.stationUiText.text = station.title

        // Update background
        if (position == selectedIndex) {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.system_neutral1_300))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        // Update play button
        // Thing we clicked on is currently NOT playing
        if ((position == playIndex) && viewHolder.playBtn.tag.toString() != "playing") {
            viewHolder.playBtn.setBackgroundColor(ContextCompat.getColor(context, android.R.color.system_accent2_400)) // Playing
            viewHolder.playBtn.setImageResource(android.R.drawable.ic_media_pause)
            viewHolder.playBtn.tag = "playing"
        }
        // Thing we clicked on is currently playing (toggle: playing --> paused)
//        else if ((position == playIndex) && viewHolder.playBtn.tag.toString() == "playing") {
//
//        }
        else {
            viewHolder.playBtn.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent)) // Not playing
            viewHolder.playBtn.setImageResource(android.R.drawable.ic_media_play)
            viewHolder.playBtn.tag = "paused"
        }

        // Selection click
        viewHolder.deleteBtn.setOnClickListener {
            onDeleteLongPress(station)
        }
        viewHolder.selectBtn.setOnClickListener {
            selectedIndex = position
            viewModel.selectedStation.value = station
            notifyDataSetChanged()    // Refresh UI

            onStationSelected(station, alarmSettings)
        }

        // Play button click
        viewHolder.playBtn.setOnClickListener {
            playIndex = position
            notifyDataSetChanged() // Refresh UI

            onPlayStation(station, viewHolder.playBtn.tag as String)
        }

        return view
    }

}
class ViewHolder(view: View) {
    val stationUiText: TextView = view.findViewById(R.id.itemText)
    val selectBtn: Button = view.findViewById(R.id.selectButton)
    val playBtn: ImageButton = view.findViewById(R.id.toastButton)
    val deleteBtn: ImageButton = view.findViewById(R.id.deleteStationBtn)
}

