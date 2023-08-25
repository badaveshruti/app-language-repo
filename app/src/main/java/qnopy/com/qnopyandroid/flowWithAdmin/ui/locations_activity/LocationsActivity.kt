package qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.databinding.ActivityLocationsBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnLocationClickListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity.models.LocationsDataRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.util.Util

@AndroidEntryPoint
class LocationsActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        val EVENT_NAME = "eventName"
    }

    lateinit var binding: ActivityLocationsBinding
    lateinit var event: EventData
    val viewModel: LocationActivityViewModel by viewModels()

    val locationList: ArrayList<Location> = arrayListOf()
    lateinit var locationAdapter: LocationAdapter

    fun init(event: EventData) = with(binding) {
        locationDetail.dateTv.text = getFormattedDate(event.modificationDate)
        locationDetail.title.text = event.eventName
        locationDetail.formName.text = event.siteName
        locationDetail.projectName.text = event.mobAppName
        locationDetail.userName.text = event.updatedBy?.let { viewModel.getUserById(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Locations"
        backButtonVisibility(true)
        event = intent.extras?.getSerializable(EVENT_NAME) as EventData
        init(event)
        locationAdapter = LocationAdapter(locationList, OnLocationClick())

        binding.navMap.setOnClickListener(this)
        binding.navFilter.setOnClickListener(this)
        binding.navAddLocation.setOnClickListener(this)
        binding.locationRv.adapter = locationAdapter

        binding.searchView.doOnTextChanged { text, _, _, _ ->
            locationAdapter.filter.filter(
                text
            )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getDataForEventLocation(
            LocationsDataRequest(event.siteID, 1448, event.eventID, true, true)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.location_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_folder -> {
                Toast.makeText(this, "Folders", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(viewId: View?) {
        when (viewId) {
            binding.navMap -> {
                Toast.makeText(this, "navMap", Toast.LENGTH_SHORT).show()
            }
            binding.navFilter -> {
                Toast.makeText(this, "navFilter", Toast.LENGTH_SHORT).show()
            }
            binding.navAddLocation -> {
                Toast.makeText(this, "navAddLocation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun backButtonVisibility(isVisible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isVisible)
        supportActionBar?.setDisplayShowHomeEnabled(isVisible)
        supportActionBar?.setHomeButtonEnabled(isVisible);
    }

    private fun getFormattedDate(date: Long): String =
        Util.getFormattedDateFromMilliS(date, " dd \nMMM")

    inner class OnLocationClick : OnLocationClickListener {
        override fun onLocationPinClicked(location: Location, pos: Int) {

        }

        override fun onLocationNameClicked(location: Location, pos: Int) {

        }

        override fun onNextClcikListner(location: Location, pos: Int) {

        }

    }

    val locationJob = lifecycleScope.launchWhenStarted {
        viewModel.locationsState.collect {
            when (it) {
                is ApiState.Loading -> {
                }
                is ApiState.Failure -> {
                    //handle failure
                    Toast.makeText(this@LocationsActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
                is ApiState.Success -> {
                    val res = (it.response as HashMap<String, ArrayList<Location>>)
                    val newLocationList = res.values.toList()[0]
                    locationList.apply {
                        clear()
                        addAll(newLocationList)
                    }
                    locationAdapter = LocationAdapter(locationList, OnLocationClick())
                    binding.locationRv.adapter = locationAdapter
                }
                is ApiState.Empty -> {}
            }
        }
    }
}