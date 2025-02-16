import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bskiradioalarm.R
import com.example.bskiradioalarm.models.Station

class StationAdapter(
    private val context: Context,
    private val stations: List<Station>,
    private val onStationSelected: (Station) -> Unit,
    private val onPlayStation: (Station) -> Unit
    ) : BaseAdapter() {

    override fun getCount(): Int = stations.size
    override fun getItem(position: Int): Any = stations[position]
    override fun getItemId(position: Int): Long = position.toLong() // we need this, stupid that I have to write it -.- yet never call it

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

        val station = stations[position]
        viewHolder.stationUiText.text = station.title

        // Update background
        if (position == selectedIndex) {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.system_neutral1_300))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        // Update play button
        if (position == playIndex) {
            viewHolder.playBtn.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_light)) // Playing
        } else {
            viewHolder.playBtn.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent)) // Not playing
        }

        // Handle selection click
        viewHolder.selectBtn.setOnClickListener {
            selectedIndex = position // Store selected position
            notifyDataSetChanged() // Refresh UI
        }

        // Handle play button click
        viewHolder.playBtn.setOnClickListener {
            playIndex = position // Store playing position
            notifyDataSetChanged() // Refresh UI
            onPlayStation(station)
        }

        return view
    }
}
class ViewHolder(view: View) {
    val stationUiText: TextView = view.findViewById(R.id.itemText)
    val selectBtn: Button = view.findViewById(R.id.selectButton)
    val playBtn: ImageButton = view.findViewById(R.id.toastButton)
}

