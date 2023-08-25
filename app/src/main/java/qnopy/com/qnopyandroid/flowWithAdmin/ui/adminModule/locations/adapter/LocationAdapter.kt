package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.databinding.LocationLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnLocationListener
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

class LocationAdapter(
    val locationList: MutableList<Location>,
    val locationListener: OnLocationListener,
    val context: Context, val isFormDefault: Boolean
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(val binding: LocationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(location: Location) = with(binding) {

            ivDeleteLoc.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.delete,
                    R.color.event_start_blue
                )
            )

            tvLocationName.text = location.locationName
            if (location.latitude == null || location.longitude == null || location.latitude.equals(
                    "0.0"
                ) || location.longitude.equals("0.0")
            ) {
                ivMarker.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_no_loc_marker
                    )
                )
            } else {
                ivMarker.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_loc_marker
                    )
                )
            }

            ivMarker.setOnClickListener {
                locationListener.onLocationPinClicked(
                    location,
                    absoluteAdapterPosition
                )
            }

            tvLocationName.setOnClickListener {
                locationListener.onLocationNameClicked(
                    location,
                    absoluteAdapterPosition
                )
            }

            ivDeleteLoc.setOnClickListener {
                locationListener.onLocationDeleteClicked(
                    location,
                    absoluteAdapterPosition, isFormDefault
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding =
            LocationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int = locationList.size
    fun removeDeletedItem(pos: Int) {
        locationList.removeAt(pos)
        notifyItemRemoved(pos)
    }
}