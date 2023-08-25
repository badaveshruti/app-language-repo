package qnopy.com.qnopyandroid.flowWithAdmin.ui.home

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.clientmodel.MobileApp
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentHomeBinding
import qnopy.com.qnopyandroid.db.*
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnRecentEventListener
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnSiteListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.CreateProjectActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.dialog.StartDataCollectionDialog
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter.SiteAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import qnopy.com.qnopyandroid.ui.activity.CreateNewEventActivity
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity
import qnopy.com.qnopyandroid.ui.locations.LocationActivity
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity
import qnopy.com.qnopyandroid.uicontrols.CustomToast
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.MyRecyclerScroll
import qnopy.com.qnopyandroid.util.Util
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var createProjectLauncher: ActivityResultLauncher<Intent>
    private lateinit var favJob: Job
    private val homeViewModel: HomeFragmentViewModel by viewModels()

    lateinit var binding: FragmentHomeBinding
    lateinit var startDataCollectionDialog: StartDataCollectionDialog
    lateinit var progressDialog: AlertDialog
    lateinit var siteAdapter: SiteAdapter

    lateinit var userId: String

    var siteList: MutableList<Site> = mutableListOf()
    private var homeScreenActivity: HomeScreenActivity? = null

    @Inject
    lateinit var siteDataSource: SiteDataSource

    @Inject
    lateinit var formSitesDataSource: FormSitesDataSource

    @Inject
    lateinit var locationDataSource: LocationDataSource

    @Inject
    lateinit var mobileAppSource: MobileAppDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createProjectLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val siteId = result.data?.getStringExtra(GlobalStrings.KEY_SITE_ID)
                    siteId?.let {
                        val siteData = siteDataSource.getSiteDetails(it, userId)
                        openAdminPanel(siteData)
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeScreenActivity = (activity as HomeScreenActivity)

        userId = Util.getSharedPreferencesProperty(activity, GlobalStrings.USERID)

        binding.greetingMessageTv.text = getGreetingMessage()

        setUpSearchBar()
        setUpFavSwitch()

        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }

        binding.fabAddProject.setOnClickListener {
            CreateProjectActivity.startActivity(requireContext(), createProjectLauncher)
        }

        progressDialog = AlertManager.showQnopyProgressBar(
            requireActivity() as AppCompatActivity,
            getString(R.string.loading)
        )
        
        setHasOptionsMenu(true)
        populateSiteData()
        addObserver()
    }

    override fun onPause() {
        super.onPause()
        favJob.cancel()
    }

    private fun addObserver() {
        favJob = lifecycleScope.launchWhenStarted {
            with(homeViewModel) {
                favStateFlow.collect {
                    when (it) {
                        is ApiState.Loading -> {
                            progressDialog.show()
                        }

                        is ApiState.Success -> {
                            progressDialog.cancel()
                            if (this@HomeFragment::progressDialog.isInitialized) progressDialog.dismiss()
                            val response = it.response as FavouriteProjectResponse
                            if (response.success) {
                                val site = siteList.single { sSite -> sSite.siteID == siteID }
                                updateProjectFavourite(siteID.toString(), userId, favStatus)
                                if (this@HomeFragment::siteAdapter.isInitialized) {
                                    siteAdapter.updateFavStatus(site, favStatus)
                                }
                            }
                        }
                        is ApiState.Failure -> {
                            progressDialog.cancel()
                            if (this@HomeFragment::progressDialog.isInitialized) progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Failed to favourite",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        is ApiState.Empty -> {}
                    }
                }
            }
        }
    }

/*    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuInflater: MenuInflater = inflater
        menuInflater.inflate(R.menu.menu_home_frag, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemSearchProject -> {
                homeScreenActivity?.openSearchProjectScreen()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    private fun populateSiteData() {
        if (siteList.isNotEmpty()) {
            binding.tvSelectProject.visibility = View.VISIBLE
            binding.edtSearchProject.visibility = View.VISIBLE
            binding.rvHome.visibility = View.VISIBLE
            binding.tvNoProjects.visibility = View.GONE

            siteAdapter = SiteAdapter(
                ArrayList(siteList),
                SiteListener(),
                RecentEventCardClicked(),
                requireContext()
            )
            binding.rvHome.apply {
                adapter = siteAdapter
            }
        } else {
            binding.tvNoProjects.visibility = View.VISIBLE
            binding.tvSelectProject.visibility = View.GONE
            binding.edtSearchProject.visibility = View.GONE
            binding.rvHome.visibility = View.GONE
        }

        binding.rvHome.addOnScrollListener(object : MyRecyclerScroll() {
            override fun show() {
                showAnim()
            }

            override fun hide() {
                binding.fabAddProject.animate().translationY(
                    (binding.fabAddProject.height
                            + Util.pxToDp(100)).toFloat()
                ).setInterpolator(AccelerateInterpolator(2f)).start()
            }
        })
    }

    fun showAnim() {
        binding.fabAddProject.animate().translationY(0f)
            .setInterpolator(DecelerateInterpolator(2f)).start()
    }

    override fun onResume() {
        super.onResume()
        siteList.clear()
        siteList.addAll(homeViewModel.getSiteListForUser(userId))
        populateSiteData()

        if (binding.switchFavs.isChecked)
            if (this::siteAdapter.isInitialized) {
                siteAdapter.filterByFavs(true)
            }

        if (binding.edtSearchProject.text.toString().trim().isNotEmpty())
            if (this::siteAdapter.isInitialized) {
                siteAdapter.filter.filter(binding.edtSearchProject.text.toString().trim())
            }
    }

    private fun setUpFavSwitch() {
        binding.switchFavs.setOnCheckedChangeListener { _, isChecked ->
            if (this::siteAdapter.isInitialized) {
                siteAdapter.filterByFavs(isChecked)
            }
        }
    }

    private fun setUpSearchBar() {
        binding.edtSearchProject.addTextChangedListener {
            if (this::siteAdapter.isInitialized) {
                siteAdapter.filter.filter(it.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeScreenActivity).setTitle("Home")
        (requireActivity() as HomeScreenActivity).backButtonVisibility(false)
    }

    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()

        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning!"
            in 12..16 -> "Good Afternoon!"
            in 17..23 -> "Good Evening!"
            else -> ""
        }
    }

    inner class RecentEventCardClicked() : OnRecentEventListener {
        override fun onShowMoreClicked(event: EventData) {
            (requireActivity() as HomeScreenActivity).showEventBottomSheet(event, false)
        }

        override fun onEventClicked(event: EventData) {
            startLocationIntent(event)
        }
    }

    private fun startLocationIntent(event: EventData) {

        if (formSitesDataSource.isAppTypeNoLoc(
                event.mobAppID.toString(),
                event.siteID.toString()
            )
        ) {
            showFormDetailsScreen(event)
            return
        }

        Util.setSharedPreferencesProperty(
            requireActivity(),
            GlobalStrings.CURRENT_SITEID, event.siteID.toString() + ""
        )
        Util.setSharedPreferencesProperty(
            requireActivity(),
            GlobalStrings.CURRENT_SITENAME, event.siteName
        )
        Util.setSharedPreferencesProperty(
            requireActivity(),
            GlobalStrings.CURRENT_APPID,
            event.mobAppID.toString() + ""
        )
        var locationIntent = Intent(requireActivity(), LocationActivity::class.java)
        val isSplitScreenEnabled = Util.getSharedPrefBoolProperty(
            requireActivity(),
            GlobalStrings.ENABLE_SPLIT_SCREEN
        )
        if (Util.isTablet(requireActivity()) && isSplitScreenEnabled) locationIntent = Intent(
            requireActivity(),
            SplitLocationAndMapActivity::class.java
        )

        locationIntent.putExtra("APP_ID", event.mobAppID)
        locationIntent.putExtra("SITE_ID", event.siteID.toString() + "")
        locationIntent.putExtra("SITE_NAME", event.siteName)
        locationIntent.putExtra("EVENT_ID", event.eventID)
        locationIntent.putExtra("fromaddsite", false)
        startActivity(locationIntent)
    }

    //20/10/22 if the siteType is no_loc then the event will show the default location form directly w\o location screen
    //the default location taken is currently in lowest id and very first
    private fun showFormDetailsScreen(event: EventData) {
        var serverEventId = event.eventID

        val locations: ArrayList<Location> =
            locationDataSource.getDefaultLocation(event.siteID, event.mobAppID)

        if (locations.size >= 1) {
            val location = locations[0]
            val locName = location.locationName
            val locationId = location.locationID
            val deviceId = DeviceInfo.getDeviceID(requireContext())
            var userId: String? = "0"
            try {
                userId = Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.USERID)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomeFragment", "Error in parsing Shared preferences for userID:" + e.message)
                val userData = UserDataSource(requireContext())
                val username =
                    Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.USERNAME)

                val newUser = userData.getUser(username)
                if (newUser != null) {
                    userId = newUser.userID.toString() + ""
                }
            }
            if (serverEventId < 0) {
                serverEventId =
                    EventDataSource(requireContext()).getServerEventID(serverEventId.toString())
            }
            val dispAppName = SiteMobileAppDataSource(requireContext())
                .getMobileAppDisplayNameRollIntoApp(event.mobAppID, event.siteID)
            Util.setSharedPreferencesProperty(
                requireContext(),
                GlobalStrings.CURRENT_APPNAME,
                dispAppName
            )
            Util.setSharedPreferencesProperty(
                requireContext(),
                GlobalStrings.CURRENT_LOCATIONID,
                locationId
            )
            Util.setSharedPreferencesProperty(
                requireContext(),
                GlobalStrings.CURRENT_LOCATIONNAME,
                locName
            )
            Util.setSharedPreferencesProperty(
                requireContext(),
                GlobalStrings.SESSION_USERID,
                userId
            )
            Util.setSharedPreferencesProperty(
                requireContext(),
                GlobalStrings.SESSION_DEVICEID,
                deviceId
            )
            val childAppList: List<MobileApp> =
                mobileAppSource.getChildApps(event.mobAppID, event.siteID, locationId)
            val maxApps = childAppList.size
            if (maxApps == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_forms_for_this_location), Toast.LENGTH_SHORT
                ).show()
                return
            }
            val locationDetailIntent = Intent(
                requireActivity(),
                LocationDetailActivity::class.java
            )
            locationDetailIntent.putExtra("EVENT_ID", serverEventId)
            locationDetailIntent.putExtra("LOCATION_ID", locationId)
            locationDetailIntent.putExtra("APP_ID", event.mobAppID)
            locationDetailIntent.putExtra("SITE_ID", event.siteID)
            locationDetailIntent.putExtra(
                "SITE_NAME",
                siteDataSource.getSiteNamefromID(event.siteID)
            )
            locationDetailIntent.putExtra("APP_NAME", dispAppName)
            val cocId: String? = null
            locationDetailIntent.putExtra("COC_ID", cocId)
            locationDetailIntent.putExtra("LOCATION_NAME", locName)
            locationDetailIntent.putExtra(
                "LOCATION_DESC",
                if (location.locationDesc == null) "" else location.locationDesc
            )
            locationDetailIntent.putExtra(GlobalStrings.FORM_DEFAULT, location.formDefault)
            try {
                startActivity(locationDetailIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(
                    "HomeFragment",
                    "no_loc site event creation Error in Redirecting to Details Form:" + e.message
                )
                Toast.makeText(
                    requireContext(),
                    getString(R.string.unable_to_connect_to_server),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    inner class SiteListener : OnSiteListener {

        override fun onAdminSettingsClicked(site: Site) {
            openAdminPanel(site)
        }

        override fun onSiteFavoriteClicked(site: Site, isFavorite: Boolean, pos: Int) {
            showFavAlert(site, isFavorite, pos)
        }

        override fun onNewFormTileClicked(site: Site, form: SSiteMobileApp) {
        }

        override fun onFormTileClick(site: Site, form: SSiteMobileApp, formPosition: Int) {
            if (form.isGetNewForm)
                openAdminPanel(site)
            else {
/*                StartDataCollectionDialog(site, form).show(
                    childFragmentManager,
                    "StartDataCollection"
                )*/

                if (!siteDataSource.isSiteTypeDemo(site.siteID))
                    if (!siteDataSource.isSiteTypeDemo(site.siteID))
                        gotoCreateNewEvent(site, form)
                    else
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_permission_to_create_event),
                            Toast.LENGTH_SHORT
                        ).show()
            }
        }
    }

    private fun openAdminPanel(site: Site) {
        val navigationAction =
            HomeFragmentDirections.actionHomeFragmentToProjectFragment(site)
        requireActivity().findNavController(R.id.navHost).navigate(navigationAction)
    }

    private fun showFavAlert(site: Site, isFavorite: Boolean, pos: Int) {
        val msg =
            "Do you want to ${if (!isFavorite) "un-" else ""}favourite \"${site.siteName}\" project?"

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Favourite")
        builder.setMessage(msg)
        builder.setPositiveButton(
            "Yes"
        ) { dialog: DialogInterface, _: Int ->
            run {
                dialog.cancel()

                if (CheckNetwork.isInternetAvailable(requireContext())) {
                    //saving below value in ViewModel to get to know particular siteId and status
                    // to update in db when response comes
                    homeViewModel.siteID = site.siteID
                    homeViewModel.favStatus = isFavorite

                    homeViewModel.setFavouriteProject(
                        FavouriteProjectRequest(
                            site.siteID.toString(),
                            if (isFavorite) "1" else "0"
                        ), pos
                    )
                } else {
                    CustomToast.showToast(
                        requireActivity(),
                        getString(R.string.bad_internet_connectivity),
                        5
                    )
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun gotoCreateNewEvent(site: Site, form: SSiteMobileApp) {
        val intent = Intent(
            requireContext(),
            CreateNewEventActivity::class.java
        )
        intent.putExtra(GlobalStrings.CURRENT_APPID, form.mobileAppId)
        intent.putExtra(GlobalStrings.CURRENT_SITEID, site.siteID)
        intent.putExtra(GlobalStrings.FORM_NAME, form.display_name)
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, site.siteName)
        intent.putExtra(GlobalStrings.USERID, userId.toInt())
        intent.putExtra(GlobalStrings.EVENT_STAR_DATE, System.currentTimeMillis())
        startActivity(intent)
    }

    fun updateEvent(eventToClose: EventData) {
//        updateAdapter
    }
}