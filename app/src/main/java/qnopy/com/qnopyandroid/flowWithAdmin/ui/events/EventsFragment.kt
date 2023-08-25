package qnopy.com.qnopyandroid.flowWithAdmin.ui.events

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ycuwq.calendarview.CalendarView
import com.ycuwq.calendarview.Date
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.job
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.clientmodel.LogDetails
import qnopy.com.qnopyandroid.clientmodel.MobileApp
import qnopy.com.qnopyandroid.databinding.FragmentEventsBinding
import qnopy.com.qnopyandroid.db.*
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFieldEventListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.events.adapter.EventsAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel
import qnopy.com.qnopyandroid.signature.CaptureSignature
import qnopy.com.qnopyandroid.ui.activity.*
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask
import qnopy.com.qnopyandroid.ui.events.SubmittalsFragment
import qnopy.com.qnopyandroid.ui.locations.LocationActivity
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity
import qnopy.com.qnopyandroid.uicontrols.CustomToast
import qnopy.com.qnopyandroid.uiutils.DownloadEventData
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.SharedPref
import qnopy.com.qnopyandroid.util.Util
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EventsFragment : Fragment(), CompoundButton.OnCheckedChangeListener,
    CalendarView.OnDateSelectedListener, DownloadEventListTask.OnEventDownloadListener,
    CoroutineScope by MainScope(), OnTaskCompleted,
    DownloadEventData.DownloadEventDataListener {

    val args by navArgs<EventsFragmentArgs>()

    private lateinit var downloadDataResult: ActivityResultLauncher<Intent>
    private var eventDataToDownload: EventData? = null
    private lateinit var dateSavedToScroll: Date
    private var progressBarDownloadData: androidx.appcompat.app.AlertDialog? = null
    private var closeEvent: Boolean = false
    private lateinit var eventToClose: EventData
    private lateinit var getEventsJob: Job
    private var siteId: String = ""
    private var siteName: String = ""
    private val TAG: String = "EventsFrag"
    private lateinit var selectedDateInDateFormat: Date
    private lateinit var dateSelected: String
    lateinit var binding: FragmentEventsBinding
    private var homeScreenActivity: HomeScreenActivity? = null

    val eventsViewModel: EventsFragmentViewModel by viewModels()

    lateinit var eventAdapter: EventsAdapter

    var listAllEvents: ArrayList<EventData> = ArrayList()
    private var mapEvents: HashMap<String, ArrayList<EventData>> = hashMapOf()
    var mapEventsDates: HashMap<String, Int> = hashMapOf()

    var selectedDay: Calendar = Calendar.getInstance()

    private val CALENDAR_STATE: Boolean = false
    private val ALL_EVENTS_STATE: Boolean = true
    private var LIST_STATE: Boolean = CALENDAR_STATE

    @Inject
    lateinit var siteDataSource: SiteDataSource

    @Inject
    lateinit var mobileAppSource: MobileAppDataSource

    @Inject
    lateinit var formSiteDataSource: FormSitesDataSource

    @Inject
    lateinit var locationDataSource: LocationDataSource

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeScreenActivity).setTitle("Events")
        (requireActivity() as HomeScreenActivity).setSyncMenuIconVisible(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadDataResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (eventDataToDownload != null) {
                        startLocationIntent(eventDataToDownload!!)
                        eventDataToDownload = null
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            if (arguments?.containsKey(GlobalStrings.KEY_SITE_ID)!!)
                siteId = arguments?.getString(GlobalStrings.KEY_SITE_ID).toString()

            if (siteId.isEmpty())
                siteId = args.siteId
        } catch (e: Exception) {
            e.printStackTrace()
        }

        homeScreenActivity = (requireActivity() as HomeScreenActivity)
//        homeScreenActivity?.getSiteIdForProjectUser()?.let { siteId = it }
        homeScreenActivity?.getSiteName()?.let { siteName = it }

        if (siteName.isEmpty() && siteId.isNotEmpty())
            siteName = siteDataSource.getSiteNamefromID(siteId.toInt())

        setUpUI()
    }

    private fun setUpUI() {
        val siteDataSource = SiteDataSource(requireContext())

        if (ScreenReso.isMobile2POINT0)
            binding.startNewEvent.visibility = View.INVISIBLE

        if (homeScreenActivity != null)
            if (homeScreenActivity?.siteIdForProjectUser!!.isNotEmpty()
                && homeScreenActivity?.siteIdForProjectUser != "-1"
            ) if (siteDataSource.isSiteTypeTimeSheet(homeScreenActivity!!.siteIdForProjectUser.toInt()))
                binding.startNewEvent.visibility = View.INVISIBLE

        binding.searchView.doOnTextChanged { text, _, _, _ ->
            eventAdapter.filter.filter(
                text
            )
        }

        eventAdapter = EventsAdapter(
            listAllEvents,
            this@EventsFragment,
            FieldEventListener()
        )

        val itemDecoration =
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvCalendar.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventAdapter
            addItemDecoration(itemDecoration)
        }

        binding.switchCompat.setOnCheckedChangeListener(this)
        binding.startNewEvent.setOnClickListener(OnStartNewEvent())

        setUpCalendar()
    }

    override fun onPause() {
        super.onPause()
        getEventsJob.cancel()
        if (this::selectedDateInDateFormat.isInitialized)
            dateSavedToScroll = selectedDateInDateFormat;
    }

    override fun onResume() {
        super.onResume()

        if (ScreenReso.isMobile2POINT0 && siteId.isNotEmpty()) {
            homeScreenActivity!!.backButtonVisibility(ScreenReso.isMobile2POINT0)
            homeScreenActivity!!.enableHomeMenuItem()
        } else
            homeScreenActivity!!.backButtonVisibility(false)

        addObservers()
        fetchEvents()
    }

    fun fetchEvents() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!EventDataSource(requireActivity()).isEventsDownloadedAlready) {
                refreshEvents()
            } else
                eventsViewModel.getAllEvents(siteId)
        }, 200)
    }

    override fun onDestroyView() {
        getEventsJob.cancel()
        super.onDestroyView()
    }

    private fun addObservers() {
        getEventsJob = lifecycleScope.launchWhenResumed {
            eventsViewModel.getAllEventsFromDb.collect {
                when (it) {
                    is ApiState.Loading -> {
                        homeScreenActivity?.showAlertProgress(getString(R.string.loading))
                    }
                    is ApiState.Failure -> {
                        homeScreenActivity?.cancelAlertProgress()
                        Toast.makeText(
                            requireContext(),
                            requireActivity().getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                        this.coroutineContext.job.cancel()
                    }
                    is ApiState.Success -> {
                        val res =
                            it.response as Triple<HashMap<String, ArrayList<EventData>>,
                                    ArrayList<EventData>, HashMap<String, Int>>

                        mapEvents = res.first
                        listAllEvents = res.second
                        mapEventsDates = res.third

                        binding.calendarView.setMapEventsDates(mapEventsDates)
                        homeScreenActivity?.cancelAlertProgress()

                        if (binding.switchCompat.isChecked)
                            updateRvFor(selectedDay)
                        else
                            updateRvFor(null)

                        if (this@EventsFragment::dateSavedToScroll.isInitialized
                            && dateSavedToScroll.toString() != dateSelected
                        ) {
                            binding.calendarView.scrollToDate(
                                dateSavedToScroll.year, dateSavedToScroll.month,
                                dateSavedToScroll.day, true
                            )
                        }
                    }
                    is ApiState.Empty -> {}
                    else -> {}
                }
            }
        }
    }

    private fun setUpCalendar() {

        if (ScreenReso.isLimitedUser) binding.startNewEvent.visibility = View.INVISIBLE

        binding.calendarLayout.shrink()
        binding.calendarView.apply {
            setBottomTextColor(Color.BLACK)
            setBottomTextSize(40)
            setSelectedItemColor(ContextCompat.getColor(requireContext(), R.color.red))
            setOnDateSelectedListener(this@EventsFragment)
        }
    }

    private fun refreshEvents() {
        if (CheckNetwork.isInternetAvailable(requireActivity())) {
            DownloadEventListTask(
                requireActivity() as AppCompatActivity,
                this@EventsFragment
            ).execute()
        } else {
            Toast.makeText(
                requireActivity(), getString(R.string.bad_internet_connectivity),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDateSelected(date: Date?) {
        val year = date?.year
        val month = date?.month

        binding.tvMonthYr.text = getConvertedDate("$year-$month")

        dateSelected = date.toString()
        date?.let { selectedDateInDateFormat = it }

        updateRvFor(selectedDay)
    }

    private fun showData(listEvents: ArrayList<EventData>) {
        listEvents.sortWith { lhs, rhs ->
            rhs.modificationDate.compareTo(lhs.modificationDate)
        }

        eventAdapter = EventsAdapter(
            listEvents, this@EventsFragment,
            FieldEventListener()
        )
        binding.rvCalendar.adapter = eventAdapter
        eventAdapter.notifyDataSetChanged()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) = with(binding) {
        when (isChecked) {
            true -> {
                //calendar
                LIST_STATE = CALENDAR_STATE;
                calendarView.visibility = View.VISIBLE
                tvMonthYr.visibility = View.VISIBLE
                calendarLayout.setmEnableScroll(true)

                updateRvFor(selectedDay)
            }
            false -> {
                //all events
                LIST_STATE = ALL_EVENTS_STATE
                calendarView.visibility = View.GONE
                tvMonthYr.visibility = View.GONE
                calendarLayout.setmEnableScroll(false)

                updateRvFor(null)
            }
        }
    }

    inner class OnStartNewEvent : View.OnClickListener {
        override fun onClick(v: View?) {

            if (ScreenReso.isProjectUser && siteId.isNotEmpty() && siteId != "-1") {
                if (this@EventsFragment::dateSelected.isInitialized)
                    startApplicationActivity(dateSelected)
            } else {
                val intent = Intent(requireActivity(), SiteActivity::class.java)

                if (this@EventsFragment::dateSelected.isInitialized && dateSelected.isNotEmpty()
                    && LIST_STATE == CALENDAR_STATE
                ) {
                    val milliSec = Util.getTimeInMillisAddingCurrentTime(dateSelected)
                    intent.putExtra(GlobalStrings.EVENT_STAR_DATE, milliSec)
                }

                startActivity(intent)
            }
        }
    }

    fun startApplicationActivity(dateSelected: String) {
        val companyID = Util.getSharedPreferencesProperty(
            requireContext(),
            GlobalStrings.COMPANYID
        ).toInt()

        val appData = SiteMobileAppDataSource(requireContext())
        var siteFormList: List<SSiteMobileApp?> = ArrayList()

        siteFormList = appData.getAllAppsV16(siteId.toInt())

        if (siteFormList.isNotEmpty()) invokeApplicationActivity(
            siteName, siteId.toInt(),
            dateSelected
        ) else Toast.makeText(
            requireContext(), "You don't have any forms to proceed!",
            Toast.LENGTH_SHORT
        ).show()

        Util.setSharedPreferencesProperty(requireContext(), GlobalStrings.CURRENT_SITEID, siteId)
    }

    private fun invokeApplicationActivity(siteName: String, siteID: Int, dateSelected: String) {
        val applicationIntent = Intent(requireContext(), ApplicationActivity::class.java)

        //for calendar to carry selected date
        if (dateSelected.isNotEmpty() && LIST_STATE == CALENDAR_STATE) {
            val milliSec = Util.getTimeInMillisAddingCurrentTime(dateSelected)
            applicationIntent.putExtra(GlobalStrings.EVENT_STAR_DATE, milliSec)
        }
        applicationIntent.putExtra("SITE_NAME", siteName)
        applicationIntent.putExtra("SITE_ID", siteID)
        Util.setSharedPreferencesProperty(
            requireContext(), GlobalStrings.CURRENT_SITEID,
            ""
                    + siteID
        )
        Util.setSharedPreferencesProperty(
            requireContext(),
            GlobalStrings.CURRENT_SITENAME,
            siteName
        )
        Log.i(TAG, "Selected site id:$siteID& siteName:$siteName")
        startActivity(applicationIntent)
    }

    inner class FieldEventListener : OnFieldEventListener {
        override fun onEventClicked(event: EventData, position: Int) {
            val fieldDataSource = FieldDataSource(requireActivity())

            //ask for download data in case event is not created by logged in user
            if (homeScreenActivity?.userID?.toInt() != event.userId &&
                !fieldDataSource.hasFieldDataForEvent(
                    event.siteID.toString() + "",
                    event.eventID.toString() + ""
                )
            ) {
                showDownloadDataAlert(event)
            } else {
                startLocationIntent(event)
            }
        }

        override fun onEventOptionsClicked(event: EventData, position: Int) {
            (requireActivity() as HomeScreenActivity).showEventBottomSheet(event, true)
        }

        override fun onCloseEventClicked(status: Int, event: EventData, position: Int) {
            eventToClose = event
            val fieldDataSource = FieldDataSource(requireActivity())

            val reqDataList = fieldDataSource.getMandatoryFieldList(
                event.mobAppID.toString() + "",
                event.eventID.toString() + "",
                event.siteID.toString() + ""
            )

            if (CalendarFragment.hasRequiredLocationsFields(
                    event.siteID, event.eventID,
                    event.mobAppID, requireContext()
                )
            ) {
                //all operations are done in the condition method
            } else if (reqDataList != null && reqDataList.size > 0 && reqDataList[0].count > 0) {
                CalendarFragment.requiredDataInFormAlert(event, requireContext())
            } else {
                closeEventAlert(event)
            }
        }
    }

    fun showDownloadDataAlert(event: EventData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("There might be data available for this event on server, do you want to download?")
        builder.setPositiveButton(
            "Yes"
        ) { _: DialogInterface?, _: Int -> onDownloadDataClicked(event) }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            startLocationIntent(event)
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_Gray))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun onDownloadDataClicked(event: EventData) {
        val syncDateSource = DataSyncDateSource(requireActivity())
        val timeMillis = syncDateSource.getDataSyncTime(
            event.eventID.toString() + "",
            event.siteID.toString() + ""
        )
        when {
/*            timeMillis != 0L -> {
                AlertManager.showDownloadDataWaitAlert(
                    requireActivity() as AppCompatActivity,
                    requireActivity().getString(R.string.download_data),
                    "Please wait for 00m00s to download data.", timeMillis
                )
            }*/
            CheckNetwork.isInternetAvailable(requireActivity()) -> {
                eventDataToDownload = event
                val intent = Intent(requireActivity(), DownloadYourOwnDataActivity::class.java)
                intent.putExtra("SITE_NAME", event.siteName)
                intent.putExtra("SITEID", event.siteID)
                intent.putExtra("EVENTID", event.eventID)
                intent.putExtra("APP_NAME", event.mobAppName)
                intent.putExtra("PARENTAPPID", event.mobAppID)
                intent.putExtra("REPORT", "TRUE")
                downloadDataResult.launch(intent)
            }
            else -> {
                Toast.makeText(
                    requireActivity(), getString(R.string.please_check_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startLocationIntent(event: EventData) {

        if (formSiteDataSource.isAppTypeNoLoc(event.mobAppID.toString(), event.siteID.toString())) {
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

    private fun updateRvFor(day: Calendar?) {

        if (day == null) {
            showData(listAllEvents)
        } else {
            if (this::dateSelected.isInitialized && mapEvents.containsKey(dateSelected))
                mapEvents[dateSelected]?.let {
                    showData(
                        it
                    )
                } else
                showData(ArrayList())
        }
        eventAdapter.filter.filter(binding.searchView.text.toString())
    }

    private fun getConvertedDate(time: String): String {
        val displayFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val parseFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

        val date = parseFormat.parse(time) ?: time

        return displayFormat.format(date)
    }

    override fun onEventDownloadSuccess() {
        eventsViewModel.getAllEvents(siteId)
    }

    override fun onEventDownloadFailed() {
        CustomToast.showToast(
            requireActivity(),
            requireActivity().getString(R.string.something_went_wrong),
            Toast.LENGTH_SHORT
        )
    }

    private fun closeEventAlert(event: EventData) {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setTitle(R.string.close_end_field_event)
        alertDialogBuilder
            .setMessage(R.string.sure_submit_data_and_close_event)
        alertDialogBuilder.setPositiveButton(
            getString(R.string.yes)
        ) { dialog, _ ->
            dialog.cancel()
            val capture = Util.getSharedPreferencesProperty(
                requireActivity(),
                GlobalStrings.CAPTURE_SIGNATURE
            )
            val captureSignature = capture?.toBoolean() ?: false

            if (captureSignature) {
                val intent = Intent(requireActivity(), CaptureSignature::class.java)
                intent.putExtra("EVENT_ID", event.eventID)
                intent.putExtra("APP_ID", event.mobAppID)
                intent.putExtra("SITE_ID", event.siteID)
                intent.putExtra("CLOSE", "true")
                intent.putExtra("UserID", homeScreenActivity?.userID)
                startActivityForResult(
                    intent,
                    SubmittalsFragment.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
                )
            } else {
                closingEvents(event)
            }
        }
        alertDialogBuilder.setNegativeButton(
            getString(R.string.no)
        ) { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun closingEvents(event: EventData) {
        val eventData = EventDataSource(requireActivity())
        closeEvent = true
        val serverGenerated = eventData.isEventIDServerGenerated(event.eventID)
        val dEvent =
            eventData.getEventById(event.mobAppID, event.siteID, event.eventID.toString() + "")
        if (!serverGenerated) {
            val eventHandler = EventIDGeneratorTask(
                this,
                dEvent,
                homeScreenActivity?.username,
                homeScreenActivity?.password,
                false,
                homeScreenActivity
            )
            eventHandler.execute()
        } else {
            if (CheckNetwork.isInternetAvailable(requireActivity())) {
                uploadFieldDataBeforeEndEvent(event.eventID)
            } else {
                CustomToast.showToast(
                    requireActivity(),
                    getString(R.string.bad_internet_connectivity),
                    5
                )
            }
        }
    }

    private fun uploadFieldDataBeforeEndEvent(eventId: Int) {
        val dataUpload = Intent(requireActivity(), DataSyncActivity::class.java)
        dataUpload.putExtra("USER_NAME", homeScreenActivity?.username)
        dataUpload.putExtra("PASS", homeScreenActivity?.password)
        dataUpload.putExtra("EVENT_ID", eventId)
        dataUpload.putExtra("CLOSE_EVENT", true)
        startActivityForResult(dataUpload, SubmittalsFragment.SYNC_ACTIVITY_REQUEST_CODE)
    }

    override fun onTaskCompleted(response: Any?) {
        val fieldData = FieldDataSource(requireActivity())
        val attachDataSrc = AttachmentDataSource(requireActivity())
        val eventData = EventDataSource(requireActivity())
        val serverGenEventID: Int

        if (response != null) {
            if (response is String) {
                if (response == "SUCCESS") {
                    uploadFieldData()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.unable_to_connect_to_server),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (response is EventResponseModel) {
                GlobalStrings.responseMessage = response.message
                if (response.isSuccess) {
                    serverGenEventID = response.data.eventId
                    setGeneratedEventID(response)
                    fieldData.updateEventID(eventToClose.eventID, serverGenEventID)
                    attachDataSrc.updateEventID(eventToClose.eventID, serverGenEventID)
                    eventData.updateEventID(eventToClose.eventID, response)
                    SampleMapTagDataSource(requireActivity())
                        .updateEventID_SampleMapTag(
                            eventToClose.eventID.toString() + "",
                            serverGenEventID.toString() + ""
                        )

                    //changing client negative eventId to server gen Id so that the event can be closed from server
                    if (eventToClose.eventID < 0) eventToClose.eventID = serverGenEventID
                    if (CheckNetwork.isInternetAvailable(requireActivity())) {
                        if (closeEvent) {
                            uploadFieldDataBeforeEndEvent(eventToClose.eventID)
                        } else {
                            uploadFieldData()
                        }
                    } else {
                        CustomToast.showToast(
                            requireActivity() as Activity,
                            getString(R.string.bad_internet_connectivity),
                            5
                        )
                    }
                } else {
                    if (response.responseCode == HttpStatus.NOT_ACCEPTABLE) {
                        //04-Mar-16
                        Toast.makeText(
                            requireActivity(),
                            GlobalStrings.responseMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (response.responseCode == HttpStatus.NOT_FOUND
                        || response.responseCode == HttpStatus.LOCKED
                        || response.responseCode == HttpStatus.UNAUTHORIZED
                    ) {
                        Util.setDeviceNOT_ACTIVATED(
                            requireActivity() as Activity,
                            homeScreenActivity?.username,
                            homeScreenActivity?.password
                        )
                        //                    Toast.makeText(requireActivity(),GlobalStrings.responseMessage,Toast.LENGTH_LONG).show();
                    }
                    if (response.responseCode == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(
                            requireActivity(),
                            GlobalStrings.responseMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.unable_to_connect_to_server),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun uploadFieldData() {
        val locationSource = LocationDataSource(requireActivity())
        val fieldSource = FieldDataSource(requireActivity())
        val attachDataSource = AttachmentDataSource(requireActivity())

        //CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData()
        fieldSource.checkAndUpdateClientEventInAttachmentData()
        val tempLogsDataSource = TempLogsDataSource(requireActivity())
        val logDetails = LogDetails()

        logDetails.allIds = ""
        logDetails.date = Util.getFormattedDateFromMilliS(
            System.currentTimeMillis(),
            GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN
        )

        logDetails.screenName = "Calendar Screen - sync while closing event"
        logDetails.details = ("Has field data before checking old strings? Rows: "
                + fieldSource.collectDataForSyncUpload().size)
        tempLogsDataSource.insertTempLogs(logDetails)

        val isLocationsAvailableToSync = locationSource.isOfflineLocationsAvailable //24-Mar-17
        val isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync
        val isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync()
        val cocDataSource = CocMasterDataSource(requireActivity())
        val isCoCAvailableToSync = cocDataSource.syncableCOCID.size > 0
        logDetails.details =
            "Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size
        tempLogsDataSource.insertTempLogs(logDetails)
        logDetails.details =
            ("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                    + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                    + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync)
        tempLogsDataSource.insertTempLogs(logDetails)

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync
            && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync
        ) {
            Toast.makeText(
                requireActivity(),
                getString(R.string.no_data_to_sync),
                Toast.LENGTH_LONG
            ).show()
        } else {
            val dataUpload = Intent(requireActivity(), DataSyncActivity::class.java)
            dataUpload.putExtra("USER_NAME", homeScreenActivity?.username)
            dataUpload.putExtra("PASS", homeScreenActivity?.password)
            dataUpload.putExtra("EVENT_ID", eventToClose.eventID)
            startActivity(dataUpload)
        }
    }

    override fun onTaskCompleted() {
    }

    override fun setGeneratedEventID(id: Int) {
    }

    override fun setGeneratedEventID(obj: Any?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SubmittalsFragment.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {
            closingEvents(eventToClose)
        } else if (requestCode == SubmittalsFragment.SYNC_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {
            if (data != null && data.hasExtra("SYNC_FLAG")) {
                val date = System.currentTimeMillis()
                val eventClosed = data.getBooleanExtra("SYNC_FLAG", false)
                var eventEndDate = data.getStringExtra("EVENT_END_DATE")!!.toLong()
                val dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false)
                if (eventEndDate < 1) {
                    eventEndDate = date
                }
                if (dataSynced && eventClosed) {
                    val eventData = EventDataSource(requireActivity())
                    val cp = CompletionPercentageDataSource(requireActivity())
                    eventData.closeEventStatus(
                        eventToClose.mobAppID, eventToClose.siteID,
                        eventEndDate, eventToClose.eventID.toString() + ""
                    )
                    cp.truncatePercentageByRollAppID_And_SiteID(
                        eventToClose.siteID.toString() + "",
                        eventToClose.mobAppID.toString() + ""
                    )
                    SharedPref.resetCamOrMap()
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.event_has_been_closed),
                        Toast.LENGTH_LONG
                    ).show()
                    eventAdapter.updateEvent(eventToClose)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.event_cannot_be_closed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun showDownloadEventProgress() {
        progressBarDownloadData = AlertManager.showQnopyProgressBar(
            requireActivity() as AppCompatActivity,
            "Checking to see if there is data for this event.."
        )
        progressBarDownloadData?.show()
    }

    override fun cancelDownloadEventProgress() {
        if (progressBarDownloadData != null && progressBarDownloadData!!.isShowing) {
            progressBarDownloadData!!.cancel()
        }
    }

    override fun showLocationScreen(event: EventData?) {
        startLocationIntent(event!!)
    }
}