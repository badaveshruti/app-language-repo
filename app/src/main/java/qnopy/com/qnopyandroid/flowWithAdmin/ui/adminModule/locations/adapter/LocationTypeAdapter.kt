package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.databinding.LayoutLocationTypesBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnLocationListener
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

class LocationTypeAdapter(
    val mapLocations: HashMap<String, ArrayList<Location>>,
    val locationListener: OnLocationListener,
    val context: Context
) : RecyclerView.Adapter<LocationTypeAdapter.LocTypeViewHolder>() {

    private var isDefaultExpanded: Boolean = false
    private var isOthersExpanded: Boolean = false

    private lateinit var defaultLocAdapter: LocationAdapter
    private lateinit var otherLocAdapter: LocationAdapter

    inner class LocTypeViewHolder(val binding: LayoutLocationTypesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rvLocations.layoutManager = LinearLayoutManager(context)
            binding.rvLocations.itemAnimator = DefaultItemAnimator()
        }

        fun bind(locationList: java.util.ArrayList<Location>, isFormDefault: Boolean) =
            with(binding) {
                binding.tvLocationType.text = if (isFormDefault) "Default" else "Other"

                if (isDefaultExpanded || isOthersExpanded) {
                    rvLocations.visibility = View.VISIBLE
                    ivCollapse.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.expand_arrow, R.color.dark_gray
                        )
                    )

                    if (isDefaultExpanded) {
                        setDefaultAdapter(binding, locationList)
                    }

                    if (isOthersExpanded) {
                        setOtherAdapter(binding, locationList)
                    }

                } else {
                    rvLocations.visibility = View.GONE
                    ivCollapse.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.arrow_right_enabled, R.color.dark_gray
                        )
                    )
                }

                binding.layoutHeader.setOnClickListener {
                    if (isFormDefault) {
                        isDefaultExpanded = !isDefaultExpanded
                        setExpandIcon(binding, isDefaultExpanded)

                        if (isDefaultExpanded) {
                            setDefaultAdapter(binding, locationList)
                        } else {
                            binding.rvLocations.visibility = View.GONE
                        }
                    } else {
                        isOthersExpanded = !isOthersExpanded
                        setExpandIcon(binding, isOthersExpanded)

                        if (isOthersExpanded) {
                            setOtherAdapter(binding, locationList)
                        } else {
                            binding.rvLocations.visibility = View.GONE
                        }
                    }
                }
            }
    }

    private fun setDefaultAdapter(
        binding: LayoutLocationTypesBinding,
        locationList: java.util.ArrayList<Location>
    ) {

        binding.rvLocations.visibility = View.VISIBLE

        if (!this::defaultLocAdapter.isInitialized) {
            defaultLocAdapter = LocationAdapter(locationList, locationListener, context, true)
            binding.rvLocations.adapter = defaultLocAdapter
        }
    }

    private fun setOtherAdapter(
        binding: LayoutLocationTypesBinding,
        locationList: java.util.ArrayList<Location>
    ) {

        binding.rvLocations.visibility = View.VISIBLE

        if (!this::otherLocAdapter.isInitialized) {
            otherLocAdapter = LocationAdapter(locationList, locationListener, context, false)
            binding.rvLocations.adapter = otherLocAdapter
        }
    }

    private fun setExpandIcon(binding: LayoutLocationTypesBinding, isExpanded: Boolean) {
        if (isExpanded) {
            binding.ivCollapse.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.expand_arrow, R.color.dark_gray
                )
            )
        } else {
            binding.ivCollapse.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.arrow_right_enabled, R.color.dark_gray
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocTypeViewHolder {
        val binding =
            LayoutLocationTypesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocTypeViewHolder, position: Int) {
        if (position == 0)
            mapLocations[GlobalStrings.FORM_DEFAULT]?.let { holder.bind(it, true) }
        else if (position == 1)
            mapLocations[GlobalStrings.NON_FORM_DEFAULT]?.let { holder.bind(it, false) }
    }

    override fun getItemCount(): Int = mapLocations.size

    fun removeDeletedItem(pos: Int, formDefault: Boolean) {
        if (formDefault) {
            defaultLocAdapter.removeDeletedItem(pos)
        } else {
            otherLocAdapter.removeDeletedItem(pos)
        }
    }
}