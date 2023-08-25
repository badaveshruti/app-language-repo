package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.clientmodel.MobileApp
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentHomeTabBinding
import qnopy.com.qnopyandroid.db.*
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnRecentEventListener
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnSiteListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter.FormTileAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter.RecentEventsAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.project.ProjectFragmentDirections
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import qnopy.com.qnopyandroid.ui.activity.CreateNewEventActivity
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity
import qnopy.com.qnopyandroid.ui.locations.LocationActivity
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import javax.inject.Inject

//this home fragment is shown on site details where we see all tabs home, forms, equipments, etc
@AndroidEntryPoint
class HomeTabFragment : BaseFragment() {

    private lateinit var site: Site

    private var eventsDownloadedOnce: Boolean = false
    private lateinit var userId: String
    private lateinit var binding: FragmentHomeTabBinding

    @Inject
    lateinit var siteDataSource: SiteDataSource

    @Inject
    lateinit var formSitesDataSource: FormSitesDataSource

    @Inject
    lateinit var locationDataSource: LocationDataSource

    @Inject
    lateinit var mobileAppSource: MobileAppDataSource

    private lateinit var downloadDataResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadDataResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                eventsDownloadedOnce = true
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeTabBinding.inflate(inflater, container, false)
        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    private fun showDownloadAllEventsDataAlert() {

        if (site.eventList.isEmpty()) {
            return
        }

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Download")
        builder.setIcon(
            VectorDrawableUtils.getDrawable(
                requireContext(),
                R.drawable.downloadicon,
                R.color.qnopy_teal
            )
        )
        builder.setMessage("Do you want to download all recent events data?")
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            dialog.cancel()

            if (!CheckNetwork.isInternetAvailable(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    R.string.please_check_internet_connection,
                    Toast.LENGTH_SHORT
                ).show()
                return@setPositiveButton
            }

            if (site.eventList.isNotEmpty()) {
                val eventIds = arrayListOf<String>()

                site.eventList.forEach { event ->
                    eventIds.add(event.eventID.toString())
                }

                val intent = Intent(
                    requireContext(),
                    DownloadYourOwnDataActivity::class.java
                )

                intent.putExtra(
                    GlobalStrings.KEY_EVENT_IDS,
                    Util.splitArrayListToString(eventIds)
                )
                downloadDataResult.launch(intent)
            }
        }

        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.cancel()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun setUpUi() {
        userId = Util.getSharedPreferencesProperty(activity, GlobalStrings.USERID)

        setInfoButtons()

        binding.apply {

            layoutChildItems.tvSeeAllEvents.visibility = View.VISIBLE

            layoutChildItems.rvLatestEvents.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            layoutChildItems.rvLatestEvents.itemAnimator = DefaultItemAnimator()

            var spanCount = 3
            if (Util.isTablet(requireContext()))
                spanCount = 5

            layoutChildItems.rvForms.layoutManager =
                GridLayoutManager(context, spanCount)
            layoutChildItems.rvForms.itemAnimator = DefaultItemAnimator()

            layoutChildItems.tvSeeAllEvents.setOnClickListener {
                val navigationAction =
                    ProjectFragmentDirections.actionProjectFragmentToEventsFragment(site.siteID.toString())
                requireActivity().findNavController(R.id.navHost).navigate(navigationAction)
            }

            layoutChildItems.ivDownloadRecentEventsData.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        requireContext(),
                        R.drawable.downloadicon, R.color.colorPrimary
                    )
                )

                setOnClickListener {
                    showDownloadAllEventsDataAlert()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handleRecyclerItems()
    }

    private fun setInfoButtons() {

        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        binding.apply {
            if (!isShowInfoEnabled) {
                layoutChildItems.ivInfoStartNew.visibility = View.GONE
                layoutChildItems.ivInfoContinueWith.visibility = View.GONE
            } else {
                layoutChildItems.ivInfoStartNew.apply {
                    setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.ic_info, R.color.event_start_blue
                        )
                    )

                    setOnClickListener {
                        AlertManager.showNormalAlert(
                            "Start New", "Start filling out a blank form from the list " +
                                    "below. Every time you fill out one single form, that’s considered " +
                                    "one Event. If you’ve just gotten to the site and need to start " +
                                    "taking data, start a new Event for the appropriate form by " +
                                    "tapping it below. You can also swipe to the right to see more " +
                                    "available forms.", "Got it", "",
                            false, context
                        )
                    }
                }

                layoutChildItems.ivInfoContinueWith.apply {
                    setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.ic_info, R.color.event_start_blue
                        )
                    )

                    setOnClickListener {
                        AlertManager.showNormalAlert(
                            "Continue with", "Tap on a form listed " +
                                    "here to go back to a form you’ve already started. You can see who " +
                                    "started the form and on what date. Be sure to check the name and date " +
                                    "of the form before editing past data. You can scroll to the right " +
                                    "to see more past forms.", "Got it", "",
                            false, context
                        )
                    }
                }
            }
        }
    }

    fun handleRecyclerItems() {

        site.eventList.clear()
        site.eventList.addAll(EventDataSource(context).getRecentEvents(site.siteID.toString()))
/*        if (site.eventList.size >= 10)
            site.eventList = ArrayList(site.eventList.slice(0..9))*/

        site.formsList.clear()
        site.formsList.addAll(SiteMobileAppDataSource(context).getAllAppsV16(site.siteID))

        if (site.eventList.isNotEmpty()) {
            binding.layoutChildItems.tvContinueWith.visibility = View.VISIBLE
            binding.layoutChildItems.rvLatestEvents.visibility = View.VISIBLE
            binding.layoutChildItems.tvSeeAllEvents.visibility = View.VISIBLE
            binding.layoutChildItems.ivDownloadRecentEventsData.visibility = View.VISIBLE
            setEventAdapter(site.eventList)
        } else {
            binding.layoutChildItems.tvContinueWith.visibility = View.INVISIBLE
            binding.layoutChildItems.ivInfoContinueWith.visibility = View.INVISIBLE
            binding.layoutChildItems.rvLatestEvents.visibility = View.GONE
            binding.layoutChildItems.tvSeeAllEvents.visibility = View.INVISIBLE
            binding.layoutChildItems.ivDownloadRecentEventsData.visibility = View.INVISIBLE
        }

        if (site.formsList.isNotEmpty()) {
            binding.layoutChildItems.layoutForms.visibility = View.VISIBLE
            setFormsAdapter(site.formsList, site)
        } else {
            binding.layoutChildItems.layoutForms.visibility = View.GONE
        }

        if (site.eventList.isEmpty() && site.formsList.isEmpty())
            binding.tvNoData.visibility = View.VISIBLE
    }

    private fun setFormsAdapter(
        formsList: java.util.ArrayList<SSiteMobileApp>, site: Site
    ) {
        val adapter = FormTileAdapter(site, formsList, SiteListener())
        binding.layoutChildItems.rvForms.adapter = adapter
    }

    private fun setEventAdapter(eventList: ArrayList<EventData>) {
        val adapter = RecentEventsAdapter(eventList, RecentEventCardClicked(), requireContext())
        binding.layoutChildItems.rvLatestEvents.adapter = adapter
    }

    inner class SiteListener : OnSiteListener {

        override fun onAdminSettingsClicked(site: Site) {
        }

        override fun onSiteFavoriteClicked(site: Site, isFavorite: Boolean, pos: Int) {
        }

        override fun onNewFormTileClicked(site: Site, form: SSiteMobileApp) {
        }

        override fun onFormTileClick(site: Site, form: SSiteMobileApp, formPosition: Int) {
            if (!siteDataSource.isSiteTypeDemo(site.siteID)
                || !siteDataSource.isSiteTypeTimeSheet(site.siteID)
            )
                gotoCreateNewEvent(site, form)
            else
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_permission_to_create_event),
                    Toast.LENGTH_SHORT
                ).show()
        }
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

        val locations: java.util.ArrayList<Location> =
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
}