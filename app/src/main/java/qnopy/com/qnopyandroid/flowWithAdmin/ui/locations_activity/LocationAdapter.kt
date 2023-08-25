package qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.databinding.RvLocationLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnLocationClickListener

class LocationAdapter(val locationsList:ArrayList<Location>,val mOnLocationClickListener: OnLocationClickListener):
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(), Filterable {

    private val mFilter:LocationFilter = LocationFilter()
    private val originalLocationList:ArrayList<Location> = arrayListOf()
    init {
        originalLocationList.addAll(locationsList)
    }

    inner class LocationViewHolder(val binding: RvLocationLayoutBinding):RecyclerView.ViewHolder(binding.root){
            fun bind(location: Location) = with(binding){
                tvLocationName.text = location.locationName
                tagLocation.setOnClickListener{mOnLocationClickListener.onLocationPinClicked(location, absoluteAdapterPosition)}
                tvLocationName.setOnClickListener{mOnLocationClickListener.onLocationNameClicked(location, absoluteAdapterPosition)}
                gotoNext.setOnClickListener{mOnLocationClickListener.onNextClcikListner(location, absoluteAdapterPosition)}
                val drawable =
                    if (location.latitude.toDouble() != 0.0 && location.longitude.toDouble() != 0.0)
                    R.drawable.ic_loc_marker
                 else
                     R.drawable.ic_no_loc_marker
                tagLocation.setBackgroundResource(drawable)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = RvLocationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationsList[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int = locationsList.size

    override fun getFilter(): Filter = mFilter

    private inner class LocationFilter:Filter(){
        override fun performFiltering(charSeq: CharSequence?): FilterResults {
            val filteredList = arrayListOf<Location>()
            val result = FilterResults()
            if (charSeq==null || charSeq.toString().isEmpty()){
                filteredList.apply {
                    clear()
                    addAll(originalLocationList)
                }
            }else{
                val pattern = charSeq.toString().lowercase().trim()
                locationsList.forEach {
                    if (it.locationName.lowercase().contains(pattern)){
                        filteredList.add(it)
                    }
                }
            }
            result.apply {
                values = filteredList
                count = filteredList.size
            }
            return result
        }

        override fun publishResults(chaeSeq: CharSequence?, result: FilterResults?) {
            locationsList.apply {
                clear()
                addAll(result?.values as ArrayList<Location>)
                notifyDataSetChanged()
            }
        }

    }
}