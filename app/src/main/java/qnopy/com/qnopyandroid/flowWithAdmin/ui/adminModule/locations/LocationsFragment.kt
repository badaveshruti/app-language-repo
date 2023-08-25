package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentLocationsBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnLocationListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations.adapter.LocationTypeAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.map.MapActivity
import qnopy.com.qnopyandroid.ui.activity.AddLocationActivity
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

@AndroidEntryPoint
class LocationsFragment : Fragment() {

    private lateinit var site: Site

    private lateinit var locationLauncher: ActivityResultLauncher<Intent>
    private lateinit var onMapResultReceived: ActivityResultLauncher<Intent>
    val TAG = "**"

    lateinit var binding: FragmentLocationsBinding
    private val locationsViewModel: LocationsFragmentViewModel by viewModels()

    val mapLocList: HashMap<String, ArrayList<Location>> = hashMapOf()
    lateinit var locationAdapter: LocationTypeAdapter

    var companyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)

        onMapResultReceived =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                setAdapter()
            }

        locationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                } else {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsBinding.inflate(layoutInflater, container, false)

        companyId =
            Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.COMPANYID).toInt()

        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (isShowInfoEnabled) {

            binding.ivInfoAddLoc.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Locations", "All data in QNOPY is grouped by Locations. " +
                                "Locations can be specific spots with exact GPS coordinates, " +
                                "like monitoring wells, or larger areas. To create a new Location, " +
                                "you only need its name. If you’re not sure if you need your data " +
                                "separated by Location, select Default Site Location before " +
                                "collecting data.", "Got it", "",
                        false, context
                    )
                }
            }
        } else {
            binding.ivInfoAddLoc.visibility = View.GONE
        }

        binding.projectLocationRv.apply {
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.addLocationButton.setOnClickListener(AddNewLocationListener())
        binding.addLocationTextView.setOnClickListener(AddNewLocationListener())

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setAdapter()
    }

    inner class LocationListener : OnLocationListener {
        override fun onLocationPinClicked(location: Location, pos: Int) {
            updateLocation(location, pos)
        }

        override fun onLocationNameClicked(location: Location, pos: Int) {
        }

        override fun onLocationDeleteClicked(location: Location, pos: Int, isFormDefault: Boolean) {
            deleteLocation(location, pos, isFormDefault)
        }

        override fun onLocationAssignClicked(location: Location, pos: Int) {

        }
    }

    private fun deleteLocation(location: Location, pos: Int, isFormDefault: Boolean) {
        val deleteDialogBuilder = AlertDialog.Builder(requireContext(), R.style.dialogStyle)

        deleteDialogBuilder.setTitle(activity?.getString(R.string.delete_location))
        deleteDialogBuilder.setMessage(activity?.getString(R.string.do_you_want_to_delete_location))
        deleteDialogBuilder.setCancelable(false)
        deleteDialogBuilder.setNegativeButton(activity?.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        deleteDialogBuilder.setPositiveButton(activity?.getString(R.string.yes)) { dialog, _ ->
            locationsViewModel.deleteLocation(location.locationID)
            locationAdapter.removeDeletedItem(pos, isFormDefault)
            dialog.dismiss()
        }
        val dialog = deleteDialogBuilder.create()

        dialog.show()
    }

    inner class AddNewLocationListener : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent = Intent(requireContext(), AddLocationActivity::class.java)
//            intent.putExtra("MOBILEAPP_ID", appId)
            locationLauncher.launch(intent)
        }
    }

    private fun updateLocation(location: Location, pos: Int) {
        val mapIntent = Intent(requireContext(), MapActivity::class.java)
        mapIntent.putExtra("LOC_ID", location.locationID)
        mapIntent.putExtra("SITE_NAME", site.siteName)
        mapIntent.putExtra("LOCATION_NAME", location.locationName)
//      mapIntent.putExtra("EVENT_ID", SplitLocationAndMapActivity.eventID)
//        mapIntent.putExtra("APP_ID", appId)
        mapIntent.putExtra("PREV_CONTEXT", "Location")
        mapIntent.putExtra("OPERATION", GlobalStrings.TAG_LOCATION)
        mapIntent.putExtra("LATITUDE", location.latitude)
        mapIntent.putExtra("LONGITUDE", location.longitude)

        onMapResultReceived.launch(mapIntent)
    }

    private fun setAdapter() {
        mapLocList.apply {
            clear()
            putAll(locationsViewModel.getAllLocationForSite(site.siteID))
        }

        if (mapLocList.isEmpty()) {
            binding.projectLocationRv.visibility = View.INVISIBLE
            binding.tvNoForms.visibility = View.VISIBLE
        } else {
            binding.projectLocationRv.visibility = View.VISIBLE
            binding.tvNoForms.visibility = View.GONE
        }

        locationAdapter =
            LocationTypeAdapter(mapLocList, LocationListener(), requireContext())
        binding.projectLocationRv.adapter = locationAdapter
    }
}