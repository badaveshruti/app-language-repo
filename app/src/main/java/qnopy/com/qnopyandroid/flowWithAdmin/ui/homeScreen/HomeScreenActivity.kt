package qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel
import qnopy.com.qnopyandroid.clientmodel.*
import qnopy.com.qnopyandroid.customView.CustomButton
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.databinding.ActivityMainBinding
import qnopy.com.qnopyandroid.databinding.BottomsheetReportNamesBinding
import qnopy.com.qnopyandroid.db.*
import qnopy.com.qnopyandroid.flowWithAdmin.ui.events.EventsFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchAllReportByIdResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchReportsById
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchReportsById.FetchReportsByIdResponseListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.ReportsAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.ReportsAdapter.ReportClickedListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.HomeFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.HomeFragmentDirections
import qnopy.com.qnopyandroid.flowWithAdmin.ui.project.ProjectFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.project.ProjectFragmentDirections
import qnopy.com.qnopyandroid.flowWithAdmin.ui.settings.SettingFragment
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.DEvent
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse.AttachmentList
import qnopy.com.qnopyandroid.signature.CaptureSignature
import qnopy.com.qnopyandroid.ui.activity.*
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask
import qnopy.com.qnopyandroid.ui.events.SubmittalsFragment
import qnopy.com.qnopyandroid.ui.locations.LocationActivity
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity
import qnopy.com.qnopyandroid.ui.task.TaskFragment
import qnopy.com.qnopyandroid.ui.task.TaskMapFragment
import qnopy.com.qnopyandroid.ui.task.TasksTabFragment
import qnopy.com.qnopyandroid.uicontrols.CustomToast
import qnopy.com.qnopyandroid.uiutils.CustomAlert
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.SharedPref
import qnopy.com.qnopyandroid.util.Util
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class HomeScreenActivity : ProgressDialogActivity(), OnTaskCompleted,
    DownloadEventListTask.OnEventDownloadListener, CustomAlert.LocationServiceAlertListener {

    private lateinit var reportDataToGenerate: FetchAllReportByIdResponse.Data
    private lateinit var mBottomSheetReportsList: BottomSheetDialog
    private var userRole: Int = 0
    private var trialPeriod: Long = 0L
    private lateinit var trialItem: MenuItem
    private lateinit var menuItemLiveFeed: MenuItem
    private lateinit var menuItemTaskMapView: MenuItem
    private lateinit var menuItemTaskListView: MenuItem
    private lateinit var newTaskItem: MenuItem
    private lateinit var searchProjectItem: MenuItem

    private var eventDataToDownload: EventData? = null
    private var closeEvent: Boolean = false
    private lateinit var eventToClose: EventData
    private val mapTaskIds: HashMap<Int, Int> = HashMap()
    private val cancellationTokenSource = CancellationTokenSource()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var badElf: BadELFGPSTracker
    private lateinit var myBroadCastReceiver: MyBroadCastReceiver
    private var taskAttachmentList: ArrayList<AttachmentList> = ArrayList()
    lateinit var password: String
    lateinit var userGuid: String
    lateinit var username: String
    lateinit var userID: String

    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    var isForcedSyncClicked = false

    @Inject
    lateinit var locationDataSource: LocationDataSource

    @Inject
    lateinit var mobileAppDataSource: MobileAppDataSource

    @Inject
    lateinit var formSiteDataSource: FormSitesDataSource

    @Inject
    lateinit var siteDataSource: SiteDataSource

    @Inject
    lateinit var eventDataSource: EventDataSource

    @Inject
    lateinit var fieldDataSource: FieldDataSource

    @Inject
    lateinit var userDataSource: UserDataSource

    @Inject
    lateinit var metaDataSource: MetaDataSource

    @JvmField
    var siteIdForProjectUser = ""

    @JvmField
    var siteNameProjectUser = ""

    var syncItem: MenuItem? = null

    private lateinit var homeViewModel: HomeScreenViewModel
    private var countMediaSync = 0
    private lateinit var downloadDataResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addMenu()

        ScreenReso.userDetails = userDataSource.currentUserData

        homeViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]
        myBroadCastReceiver = MyBroadCastReceiver()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        downloadDataResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (eventDataToDownload != null) {
                        startLocationIntent(eventDataToDownload!!)
                        eventDataToDownload = null
                    }
                }
            }

        if (intent != null) {
            intent.getStringExtra(GlobalStrings.KEY_SITE_ID)?.let { siteIdForProjectUser = it }
            intent.getStringExtra(GlobalStrings.KEY_SITE_NAME)?.let { siteNameProjectUser = it }
        }

        val userAppType = Util.getSharedPreferencesProperty(this, GlobalStrings.USERAPPTYPE)

        if (userAppType != null) {
            ScreenReso.isLimitedUser =
                userAppType.equals(GlobalStrings.APP_TYPE_LIMITED, ignoreCase = true)
            ScreenReso.isProjectUser =
                userAppType.equals(GlobalStrings.APP_TYPE_PROJECT, ignoreCase = true)
            ScreenReso.isCalendarUser =
                userAppType.equals(GlobalStrings.APP_TYPE_CALENDAR, ignoreCase = true)
            ScreenReso.isMobile2POINT0 =
                userAppType.equals(GlobalStrings.APP_TYPE_MOBILE_2_POINT_0, ignoreCase = true)
        }

        setUpUi()

        trialPeriod =
            Util.getSharedPrefLongProperty(this, GlobalStrings.TRIAL_PERIOD)
        val isTrialOn = trialPeriod != 0L && trialPeriod >= System.currentTimeMillis()

        if (userRole == GlobalStrings.TRIAL_USER && !isTrialOn) {
            showTrialAlert()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this)
            } else getLocation()
        } else getLocation()
    }

    private fun addMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_activity_menu, menu)

                trialItem = menu.findItem(R.id.item_trial)
                setVisibilityTrialUser()

                syncItem = menu.findItem(R.id.sync_data)
                newTaskItem = menu.findItem(R.id.action_new_task)
                searchProjectItem = menu.findItem(R.id.itemSearchProject)
                menuItemTaskListView = menu.findItem(R.id.action_task_view)
                menuItemTaskMapView = menu.findItem(R.id.action_task_map_view)
                menuItemLiveFeed = menu.findItem(R.id.itemLiveFeed)
                setSyncBadge()

                //added this in case when taskMapFrag or taskFrag is loaded, menu is called again and so it refreshes
                val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                try {
                    if (fragment is TasksTabFragment) {
                        if (fragment.fragment is TaskFragment)
                            prepareOptionMenuForTaskFrag()
                        else
                            prepareOptionMenuForTaskMapFrag()
                    } else if (fragment is ProjectFragment)
                        handleLiveFeedItemVisibility(true)

                    searchProjectItem.isVisible = fragment is HomeFragment
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                when (menuItem.itemId) {
                    R.id.sync_data -> {
                        downloadForms()
                    }

                    R.id.itemSearchProject -> {
                        openSearchProjectScreen()
                    }

                    R.id.itemLiveFeed -> {
                        openLiveFeedScreen()
                    }

                    R.id.item_trial -> {
                        showTrialAlert()
                    }

                    R.id.action_new_task -> {
                        try {
                            if (fragment is TasksTabFragment) {
                                fragment.createNewTask()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }

                    R.id.action_task_view -> {
                        try {
                            if (fragment is TasksTabFragment) {
                                menuItemTaskMapView.isVisible = true
                                menuItemTaskListView.isVisible = false
                                fragment.loadTaskFragment(TaskFragment())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }

                    R.id.action_task_map_view -> {
                        try {
                            if (fragment is TasksTabFragment) {
                                menuItemTaskMapView.isVisible = false
                                menuItemTaskListView.isVisible = true
                                fragment.loadTaskFragment(TaskMapFragment())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }

                    android.R.id.home -> {
                        if (siteIdForProjectUser.isEmpty()) {
                            findNavController(R.id.navHost).navigateUp()
                            if (navHostFragment.childFragmentManager.backStackEntryCount == 1) {
                                backButtonVisibility(false)
                            }
                        } else
                            onBackPressed()
                        return true
                    }
                }
                return true
            }
        })
    }

    private fun getUserRole(): Int {
        var userRole = 0
        val uRole = Util.getSharedPreferencesProperty(this, GlobalStrings.USERROLE)
        if (!uRole.isNullOrEmpty()) {
            userRole = uRole.toInt()
        } else {
            val userrole: Int = userDataSource.getUserRolefromID(userID.toInt())
            if (userrole != 0) {
                Util.setSharedPreferencesProperty(
                    this,
                    GlobalStrings.USERROLE,
                    userrole.toString()
                )
                userRole = userrole
            }
        }

        return userRole
    }

    fun getSiteIdForProjectUser(): String = siteIdForProjectUser

    fun getSiteName(): String = siteNameProjectUser

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Request permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // Main code
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            currentLocationTask.addOnCompleteListener { task ->
                var result = ""
                if (task.isSuccessful) {
                    // Task completed successfully
                    val location = task.result
                    GlobalStrings.CURRENT_GPS_LOCATION = location
                    if (location != null) result = "Location (success): " +
                            location.latitude +
                            ", " +
                            location.longitude
                } else {
                    // Task failed with an exception
                    val exception = task.exception
                    result = "Exception thrown: $exception"
                }
                Log.d(TAG, "getCurrentLocation() result: $result")
            }
        } else {
            Log.d(TAG, "Request fine location permission.")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationActivity.LOCATION_PERMISSION_REQUEST_CODE) { // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setTaskBadge() {
        val tdSource = TaskDetailsDataSource(this)
        val count = tdSource.getTaskCountForBadge(siteIdForProjectUser)
        val badge: BadgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.tasksFragment)
        if (count > 0) binding.bottomNavigation.getOrCreateBadge(R.id.tasksFragment).number =
            count else if (badge.isVisible) binding.bottomNavigation.removeBadge(R.id.tasksFragment)
    }

    fun setSettingsBadge() {
        val count = NotificationsDataSource(this)
            .getNotificationCount(userID.toInt())
        val badge: BadgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.settingFragment)
        if (count[1] > 0) binding.bottomNavigation.getOrCreateBadge(R.id.settingFragment).number =
            count[1] else if (badge.isVisible) binding.bottomNavigation.removeBadge(R.id.settingFragment)
    }

    fun setUpUi() {
//        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        userID = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID)
        userRole = getUserRole()

        //added check for isNullOrEmpty as sharedPref may return null or make the default value
        //as empty string in sharedPref and add checks wherever u fetch the string in old and new code
        username = Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME)
        if (username.isNullOrEmpty() && ScreenReso.userDetails != null) {
            username = ScreenReso.userDetails.userName
            Util.setSharedPreferencesProperty(this, GlobalStrings.USERNAME, username)
        }

        userGuid = Util.getSharedPreferencesProperty(this, username)
        if (userGuid.isNullOrEmpty() && ScreenReso.userDetails != null) {
            userGuid = ScreenReso.userDetails.userGuid
            Util.setSharedPreferencesProperty(this, username, userGuid)
        }

        password = Util.getSharedPreferencesProperty(this, GlobalStrings.PASSWORD)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        if (siteIdForProjectUser.isNotEmpty()) {
            backButtonVisibility(true)
        }

        if (ScreenReso.isLimitedUser || ScreenReso.isProjectUser) {
            binding.bottomNavigation.menu.findItem(R.id.settingFragment).isVisible = false
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHost.id) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.bottom_navigation)

        val homeMenuItem = binding.bottomNavigation.menu.findItem(R.id.homeFragment)
        val eventMenuItem = binding.bottomNavigation.menu.findItem(R.id.eventsFragment)
        if (ScreenReso.isMobile2POINT0) {
            homeMenuItem.isChecked = true
            homeMenuItem.isVisible = true
            graph.setStartDestination(R.id.homeFragment)
            navController.setGraph(graph, null)
        } else {
            homeMenuItem.isVisible = false
            eventMenuItem.isVisible = true
            val bundle = Bundle()
            bundle.putSerializable(GlobalStrings.KEY_SITE_ID, siteIdForProjectUser)
            graph.setStartDestination(R.id.eventsFragment)
            navController.setGraph(graph, bundle)
        }

        addObserver()
    }

    public fun enableHomeMenuItem() {
        val homeMenuItem = binding.bottomNavigation.menu.findItem(R.id.homeFragment)
        homeMenuItem.isChecked = true
    }

    private fun addObserver() {

        lifecycleScope.launchWhenStarted {
            homeViewModel.syncMediaResponse.collect {
                when (it) {
                    is ApiState.Loading -> {
                    }

                    is ApiState.Failure -> {
                        Toast.makeText(this@HomeScreenActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }

                    is ApiState.Success -> {
                        val resultModel = it.response as AttachmentTaskResponseModel
                        countMediaSync++

                        if (resultModel.isSuccess) {
                            val attachmentsDataSource =
                                TaskAttachmentsDataSource(this@HomeScreenActivity)
                            attachmentsDataSource.updateDataSyncFlag(
                                resultModel.data.taskId,
                                resultModel.data.fileName,
                                if (resultModel.data.clientTaskAttachmentId == null)
                                    resultModel.data.taskAttachmentId
                                else resultModel.data.clientTaskAttachmentId,
                                resultModel.data.taskAttachmentId
                            )
                        } else {
                            Toast.makeText(
                                this@HomeScreenActivity,
                                "Unable to sync Task Media.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (countMediaSync == taskAttachmentList.size) {
                            countMediaSync = 0
                            cancelAlertProgress()
                            callMetaSync()
                        }
                    }

                    is ApiState.Empty -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.sendReportResponse.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showAlertProgress("Emailing logs please wait..")
                    }

                    is ApiState.Failure -> {
                        cancelAlertProgress()
                        Toast.makeText(
                            this@HomeScreenActivity,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiState.Success -> {
                        cancelAlertProgress()
                        val resultModel = it.response as String
                        val msg: String = if (resultModel == "false") {
                            getString(R.string.unable_to_gen_report)
                        } else {
                            resultModel
                        }
                        showReportAlert(msg)
                    }

                    is ApiState.Empty -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.updateEventNameResponse.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showAlertProgress("Updating name please wait..")
                    }

                    is ApiState.Failure -> {
                        cancelAlertProgress()
                        Toast.makeText(
                            this@HomeScreenActivity,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiState.Success -> {
                        cancelAlertProgress()
                        val resultModel = it.response as EventNameUpdateResponse
                        if (resultModel.success != null && resultModel.success.trim() == "true") {
                            EventDataSource(this@HomeScreenActivity).updateEventNameOnly(
                                resultModel.data.eventId,
                                resultModel.data.eventName
                            )
                            Toast.makeText(
                                this@HomeScreenActivity,
                                getString(R.string.event_name_updated),
                                Toast.LENGTH_SHORT
                            ).show()

                            val fragment =
                                navHostFragment.childFragmentManager.primaryNavigationFragment
                            try {
                                if (fragment is EventsFragment) {
                                    fragment.eventsViewModel.getAllEvents(siteIdForProjectUser)
                                }

                                if (fragment is ProjectFragment) {
                                    fragment.refreshHomeTabOrEvents()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(
                                this@HomeScreenActivity,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is ApiState.Empty -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        trialPeriod =
            Util.getSharedPrefLongProperty(this, GlobalStrings.TRIAL_PERIOD)

        if (ScreenReso.isDownloadData || !EventDataSource(this).isEventsDownloadedAlready || isForcedSyncClicked) {
            ScreenReso.isDownloadData = false
            refreshEvents()
            isForcedSyncClicked = false
        }

        registerMyReceiver()

        val editor = getSharedPreferences("BADELFGPS", MODE_PRIVATE).edit()
        editor.clear().apply()

        badElf = BadELFGPSTracker(this)

        setSyncBadge()
        setTaskBadge()
        setSettingsBadge()
    }

    private fun setTrialBadge() {
        if (this::trialItem.isInitialized) {
            if (trialPeriod != 0L) {
                val daysCount = Util.calculateDaysCountFromMillis(trialPeriod)
                Util.setBadgeCount(
                    this, trialItem, daysCount.toString(),
                    true
                )
            }
        }
    }

    private fun setSyncBadge() {
        if (syncItem != null) {
            Util.setBadgeCount(
                this, syncItem, "",
                Util.isThereAnyDataToSync(this)
            )
        }
    }

    fun refreshEvents() {
        if (CheckNetwork.isInternetAvailable(this)) {
            if (isForcedSyncClicked)
                metaDataSource.resetEventTable()
            DownloadEventListTask(
                this,
                this
            ).execute()
        } else {
            Toast.makeText(
                this, getString(R.string.bad_internet_connectivity),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showReportAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
        builder.setPositiveButton(
            getString(R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setVisibilityTrialUser() {
        if (userRole == GlobalStrings.TRIAL_USER) {
            trialItem.isVisible = true
            setTrialBadge()
        }
    }

    fun prepareOptionMenuForTaskFrag() {
        newTaskItem.isVisible = true
        menuItemTaskMapView.isVisible = true
        menuItemTaskListView.isVisible = false
    }

    private fun prepareOptionMenuForTaskMapFrag() {
        newTaskItem.isVisible = true
        menuItemTaskListView.isVisible = true
        menuItemTaskMapView.isVisible = false
    }

    fun hideOptionMenuForTaskFrag() {
        newTaskItem.isVisible = false
        menuItemTaskListView.isVisible = false
        menuItemTaskMapView.isVisible = false
    }

    fun handleLiveFeedItemVisibility(isVisible: Boolean) {
        menuItemLiveFeed.isVisible = isVisible
    }

    private fun showTrialAlert() {
        val builder = AlertDialog.Builder(this, R.style.WrapContentDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.alert_trial_period, null, false)
        val btnGetPaidAcc = view.findViewById<Button>(R.id.btnGetPaidAcc)
        val tvMessage = view.findViewById<TextView>(R.id.tvOhNo)

        val isTrialOn = trialPeriod != 0L && trialPeriod >= System.currentTimeMillis()

        if (isTrialOn) {
            val count = Util.calculateDaysCountFromMillis(trialPeriod)
            tvMessage.text = "You have $count days left until your \ntrial period ends!"
        } else {
            builder.setCancelable(false)
            btnGetPaidAcc.text = "Okay"
            tvMessage.text =
                "Oh no! Your trial period is over! \nPlease contact us at " +
                        "\nsupport@qnopy.com to upgrade \nor extend your trial."
        }

        builder.setView(view)
//        builder.setCancelable(false)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        btnGetPaidAcc.setOnClickListener {
            alertDialog.cancel()
            if (isTrialOn)
                showContactSupportAlert()
            else
                Util.setLogout(this)
        }
    }

    fun setSyncMenuIconVisible(isVisible: Boolean) {
        syncItem?.isVisible = isVisible
    }

    fun setTitle(msg: String) {
        binding.toolBarTitle.text = msg
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (navHostFragment.childFragmentManager.backStackEntryCount == 1) {
            backButtonVisibility(false)
        }
    }

    fun backButtonVisibility(isVisible: Boolean) {
        if (siteIdForProjectUser.isEmpty()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(isVisible)
            supportActionBar?.setDisplayShowHomeEnabled(isVisible)
            supportActionBar?.setHomeButtonEnabled(isVisible)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
        }
    }

    private fun showContactSupportAlert() {
        val builder = AlertDialog.Builder(this, R.style.dialogStyle)
        builder.setTitle(R.string.contact_support)
        builder.setMessage(
            """                
            ${getString(R.string.email)} : ${GlobalStrings.SUPPORT_EMAIL}
            ${getString(R.string.phone)} : ${GlobalStrings.SUPPORT_PHONE}
            """.trimIndent()
        )
        val isTabletSize = resources.getBoolean(R.bool.isTablet)
        if (!isTabletSize) {
            builder.setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }

            builder.setPositiveButton(getString(R.string.call)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                val mobileNumber = GlobalStrings.SUPPORT_PHONE
                val intent = Intent()
                intent.action = Intent.ACTION_DIAL // Action for what intent called for
                intent.data =
                    Uri.parse("tel: $mobileNumber") // Data with intent respective action on intent
                startActivity(intent)
            }
        } else {
            builder.setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showEventBottomSheet(event: EventData, isEventScreen: Boolean) {
        try {
            val sheetView =
                LayoutInflater.from(this)
                    .inflate(R.layout.layout_bottom_sheet_email_logs, null, false)
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(sheetView)
            bottomSheet.show()

            // Remove default white color background
            val bottomSheetDialog: FrameLayout? = bottomSheet.findViewById(R.id.design_bottom_sheet)
            bottomSheetDialog?.background = null

            val llRename = sheetView.findViewById<LinearLayout>(R.id.llRename)

            val dividerCloseEvent = sheetView.findViewById<View>(R.id.dividerCloseEvent)
            val llCloseEvent = sheetView.findViewById<LinearLayout>(R.id.llCloseEvent)

            val dividerViewEvent = sheetView.findViewById<View>(R.id.dividerViewEvent)
            val llEventDetails = sheetView.findViewById<LinearLayout>(R.id.llEventDetails)

            val llDownloadEventData = sheetView.findViewById<LinearLayout>(R.id.llDownloadData)

            val dividerDownloadEvent =
                sheetView.findViewById<View>(R.id.dividerDownloadEvent)

            val dividerSendDoc = sheetView.findViewById<View>(R.id.dividerSendDoc)
            val dividerViewReport = sheetView.findViewById<View>(R.id.dividerViewReport)
            val dividerRenameEvent = sheetView.findViewById<View>(R.id.dividerRenameEvent)

            val llViewReport = sheetView.findViewById<LinearLayout>(R.id.llViewReport)
            val llMailSelf = sheetView.findViewById<LinearLayout>(R.id.llSendPdf)
            val llMailTeam = sheetView.findViewById<LinearLayout>(R.id.llSendDoc)
            val llCancel = sheetView.findViewById<LinearLayout>(R.id.llCancel)

            val isSiteDemo =
                SiteDataSource(this).isSiteTypeDemo(event.siteID)

            if (isEventScreen || isSiteDemo) {
                dividerCloseEvent.visibility = View.GONE
                llCloseEvent.visibility = View.GONE

                if (isSiteDemo) {
                    //don't hide email myself for demo project

                    llDownloadEventData.visibility = View.GONE
                    dividerDownloadEvent.visibility = View.GONE

                    llMailTeam.visibility = View.GONE
                    dividerSendDoc.visibility = View.GONE

                    llViewReport.visibility = View.GONE
                    dividerRenameEvent.visibility = View.GONE

                    llRename.visibility = View.GONE
                }
            } else {
                dividerCloseEvent.visibility = View.VISIBLE
                llCloseEvent.visibility = View.VISIBLE
            }

            dividerViewEvent.visibility = View.VISIBLE
            llEventDetails.visibility = View.VISIBLE

            llEventDetails.setOnClickListener {
                bottomSheet.cancel()
                showViewEventDetailsSheet(event)
            }

            llDownloadEventData.setOnClickListener {
                bottomSheet.cancel()
                onDownloadDataClicked(event)
            }

            llRename.setOnClickListener {
                bottomSheet.cancel()
                showEventRenameAlert(event)
            }

            llViewReport.setOnClickListener {
                bottomSheet.cancel()
                val i = Intent(this, MobileReportActivity::class.java)
                i.putExtra("SITE_NAME", event.siteName)
                i.putExtra("SITE_ID", event.siteID.toString() + "")
                i.putExtra("EVENT_ID", event.eventID.toString() + "")
                i.putExtra("APP_NAME", event.mobAppName)
                startActivity(i)
            }

            llMailSelf.setOnClickListener {
                bottomSheet.cancel()
                fetchReportsByIdNames(event, true)
//                getReport(isForPM = true, isPdf = true, event = event, isForSelf = true)
            }

            llMailTeam.setOnClickListener {
                bottomSheet.cancel()
//                getReport(isForPM = true, isPdf = false, event = event, isForSelf = false)
                fetchReportsByIdNames(event, false)
            }

            llCloseEvent.setOnClickListener {
                bottomSheet.cancel()
                closeEventClick(event)
            }

            llCancel.setOnClickListener {
                bottomSheet.cancel()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showViewEventDetailsSheet(event: EventData) {
        try {
            val sheetView =
                LayoutInflater.from(this)
                    .inflate(R.layout.layout_bottomsheet_event_details, null, false)
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(sheetView)
            bottomSheet.show()

            // Remove default white color background
            val bottomSheetDialog: FrameLayout? = bottomSheet.findViewById(R.id.design_bottom_sheet)
            bottomSheetDialog?.background = null

            val tvEventName = sheetView.findViewById<CustomTextView>(R.id.tvEventName)
            val tvSiteName = sheetView.findViewById<CustomTextView>(R.id.tvSiteName)
            val tvFormName = sheetView.findViewById<CustomTextView>(R.id.tvFormName)
            val tvCreatedBy = sheetView.findViewById<CustomTextView>(R.id.tvCreatedBy)
            val tvStartDate = sheetView.findViewById<CustomTextView>(R.id.tvEventStartDate)
            val btnStartEvent = sheetView.findViewById<CustomButton>(R.id.btnStartEvent)
            val llCancel = sheetView.findViewById<LinearLayout>(R.id.llCancel)

            tvEventName.text = event.eventName
            tvSiteName.text = event.siteName
            tvFormName.text = event.mobAppName
            tvCreatedBy.text = UserDataSource(this).getFullName(event.userId.toString())
            tvStartDate.text = Util.getFormattedDateFromMilliS(
                event.startDate,
                GlobalStrings.DATE_FORMAT_MMM_DD_YYYY_TIME
            )

            btnStartEvent.setOnClickListener {
                bottomSheet.cancel()
                startLocationIntent(event)
            }

            llCancel.setOnClickListener {
                bottomSheet.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeEventClick(event: EventData) {
        eventToClose = event
        val fieldDataSource = FieldDataSource(this)

        val reqDataList = fieldDataSource.getMandatoryFieldList(
            event.mobAppID.toString() + "",
            event.eventID.toString() + "",
            event.siteID.toString() + ""
        )

        if (CalendarFragment.hasRequiredLocationsFields(
                event.siteID, event.eventID,
                event.mobAppID, this
            )
        ) {
            //all operations are done in the condition method
        } else if (reqDataList != null && reqDataList.size > 0 && reqDataList[0].count > 0) {
            CalendarFragment.requiredDataInFormAlert(event, this)
        } else {
            closeEventAlert(event)
        }
    }

    private fun closeEventAlert(event: EventData) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.close_end_field_event)
        alertDialogBuilder
            .setMessage(R.string.sure_submit_data_and_close_event)
        alertDialogBuilder.setPositiveButton(
            getString(R.string.yes)
        ) { dialog, _ ->
            dialog.cancel()
            val capture = Util.getSharedPreferencesProperty(
                this,
                GlobalStrings.CAPTURE_SIGNATURE
            )
            val captureSignature = capture?.toBoolean() ?: false

            if (captureSignature) {
                val intent = Intent(this, CaptureSignature::class.java)
                intent.putExtra("EVENT_ID", event.eventID)
                intent.putExtra("APP_ID", event.mobAppID)
                intent.putExtra("SITE_ID", event.siteID)
                intent.putExtra("CLOSE", "true")
                intent.putExtra("UserID", userID)
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
        val eventData = EventDataSource(this)
        closeEvent = true
        val serverGenerated = eventData.isEventIDServerGenerated(event.eventID)
        val dEvent =
            eventData.getEventById(event.mobAppID, event.siteID, event.eventID.toString() + "")
        if (!serverGenerated) {
            val eventHandler = EventIDGeneratorTask(
                this, dEvent,
                username, password, false, this
            )
            eventHandler.execute()
        } else {
            if (CheckNetwork.isInternetAvailable(this)) {
                uploadFieldDataBeforeEndEvent(event.eventID)
            } else {
                CustomToast.showToast(
                    this,
                    getString(R.string.bad_internet_connectivity),
                    5
                )
            }
        }
    }

    private fun uploadFieldDataBeforeEndEvent(eventId: Int) {
        val dataUpload = Intent(this, DataSyncActivity::class.java)
        dataUpload.putExtra("USER_NAME", username)
        dataUpload.putExtra("PASS", password)
        dataUpload.putExtra("EVENT_ID", eventId)
        dataUpload.putExtra("CLOSE_EVENT", true)
        startActivityForResult(dataUpload, SubmittalsFragment.SYNC_ACTIVITY_REQUEST_CODE)
    }

    private fun showEventRenameAlert(event: EventData) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.alert_event_rename, null, false)
        builder.setView(view)

        val edtRenameEvent: CustomEditText = view.findViewById(R.id.edtRenameEvent)

        var eventName = ""
        if (event.eventName.trim() != event.mobAppName.trim())
            eventName = event.eventName

        edtRenameEvent.setText(eventName)
//        edtRenameEvent.requestFocus()

        val btnSubmit: CustomButton = view.findViewById(R.id.btnSubmit)
        val btnCancel: CustomButton = view.findViewById(R.id.btnCancel)

        val alertDialog = builder.create()
        alertDialog.show()
//        Util.showKeyboard(alertDialog.context, edtRenameEvent)

        btnSubmit.setOnClickListener {
            if (edtRenameEvent.text.toString().isNotEmpty()) {
                if (edtRenameEvent.text.toString().trim() != eventName.trim()) {
                    alertDialog.cancel()
                    event.eventName = edtRenameEvent.text.toString()
                    renameEventName(event)
                } else {
                    Toast.makeText(this, "This name already exists.", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please enter new event name.", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            alertDialog.cancel()
        }
    }

    private fun renameEventName(event: EventData) {

        val eventReq = DEvent()
        eventReq.eventId = event.eventID
        eventReq.eventName = event.eventName
        eventReq.createEventFlag = 0//for renaming
        eventReq.eventDate = event.startDate
        eventReq.siteId = event.siteID
        eventReq.mobileAppId = event.mobAppID
        eventReq.userId = event.userId
        eventReq.eventStartDate = event.startDate

        val eventNameUpdateRequest =
            EventNameUpdateRequest(event.eventID, event.eventName, userGuid)

        if (CheckNetwork.isInternetAvailable(this))
            homeViewModel.updateEventName(eventNameUpdateRequest)
        else
            Toast.makeText(
                this, getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun onDownloadDataClicked(event: EventData) {
        val syncDateSource = DataSyncDateSource(this)
        val timeMillis = syncDateSource.getDataSyncTime(
            event.eventID.toString() + "",
            event.siteID.toString() + ""
        )
        when {
            /*            timeMillis != 0L -> {
                            AlertManager.showDownloadDataWaitAlert(
                                this,
                                getString(R.string.download_data),
                                "Please wait for 00m00s to download data.", timeMillis
                            )
                        }*/
            CheckNetwork.isInternetAvailable(this) -> {
                eventDataToDownload = event
                val intent = Intent(this, DownloadYourOwnDataActivity::class.java)
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
                    this, getString(R.string.please_check_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startLocationIntent(event: EventData) {

        if (formSiteDataSource.isAppTypeNoLoc(
                event.mobAppID.toString(),
                event.siteID.toString()
            )
        ) {
            showFormDetailsScreen(event)
            return
        }

        Util.setSharedPreferencesProperty(
            this,
            GlobalStrings.CURRENT_SITEID, event.siteID.toString() + ""
        )
        Util.setSharedPreferencesProperty(
            this,
            GlobalStrings.CURRENT_SITENAME, event.siteName
        )
        Util.setSharedPreferencesProperty(
            this,
            GlobalStrings.CURRENT_APPID,
            event.mobAppID.toString() + ""
        )
        var locationIntent = Intent(this, LocationActivity::class.java)
        val isSplitScreenEnabled = Util.getSharedPrefBoolProperty(
            this,
            GlobalStrings.ENABLE_SPLIT_SCREEN
        )
        if (Util.isTablet(this) && isSplitScreenEnabled) locationIntent = Intent(
            this,
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

        val locations: java.util.ArrayList<qnopy.com.qnopyandroid.clientmodel.Location> =
            locationDataSource.getDefaultLocation(event.siteID, event.mobAppID)

        if (locations.size >= 1) {
            val location = locations[0]
            val locName = location.locationName
            val locationId = location.locationID
            val deviceId = DeviceInfo.getDeviceID(this)
            var userId: String? = "0"
            try {
                userId = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomeFragment", "Error in parsing Shared preferences for userID:" + e.message)
                val userData = UserDataSource(this)
                val username =
                    Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME)

                val newUser = userData.getUser(username)
                if (newUser != null) {
                    userId = newUser.userID.toString() + ""
                }
            }
            if (serverEventId < 0) {
                serverEventId =
                    EventDataSource(this).getServerEventID(serverEventId.toString())
            }
            val dispAppName = SiteMobileAppDataSource(this)
                .getMobileAppDisplayNameRollIntoApp(event.mobAppID, event.siteID)
            Util.setSharedPreferencesProperty(
                this,
                GlobalStrings.CURRENT_APPNAME,
                dispAppName
            )
            Util.setSharedPreferencesProperty(
                this,
                GlobalStrings.CURRENT_LOCATIONID,
                locationId
            )
            Util.setSharedPreferencesProperty(
                this,
                GlobalStrings.CURRENT_LOCATIONNAME,
                locName
            )
            Util.setSharedPreferencesProperty(
                this,
                GlobalStrings.SESSION_USERID,
                userId
            )
            Util.setSharedPreferencesProperty(
                this,
                GlobalStrings.SESSION_DEVICEID,
                deviceId
            )
            val childAppList: List<MobileApp> =
                mobileAppDataSource.getChildApps(event.mobAppID, event.siteID, locationId)
            val maxApps = childAppList.size
            if (maxApps == 0) {
                Toast.makeText(
                    this,
                    getString(R.string.no_forms_for_this_location), Toast.LENGTH_SHORT
                ).show()
                return
            }
            val locationDetailIntent = Intent(
                this,
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
                    "HomeScreenActivity",
                    "no_loc site event creation Error in Redirecting to Details Form:" + e.message
                )
                Toast.makeText(
                    this,
                    getString(R.string.unable_to_connect_to_server),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun fetchReportsByIdNames(event: EventData, isEmailMyself: Boolean) {
        val fetchReportsById = FetchReportsById(
            this, event.mobAppID.toString(),
            object : FetchReportsByIdResponseListener {
                override fun onGetReportsSuccess(
                    response: FetchAllReportByIdResponse,
                    isEmailMyself: Boolean
                ) {
                    Log.d("reports list", response.toString())
                    if (response.data.size == 1) {
                        reportDataToGenerate = response.data[0]
                        if (isEmailMyself) getReport(
                            isForPM = true,
                            isPdf = true,
                            event = event,
                            isForSelf = true
                        )
                        else getReport(
                            isForPM = true,
                            isPdf = false,
                            event = event,
                            isForSelf = false
                        )
                    } else showReportNamesBottomSheet(event, response.data, isEmailMyself)
                }

                override fun onGetReportsFailed(msg: String) {
                    Log.d("reports list failed", msg)
                }
            }, isEmailMyself
        )
        fetchReportsById.fetchReportNamesList()
    }


    private fun showReportNamesBottomSheet(
        event: EventData,
        reportsList: java.util.ArrayList<FetchAllReportByIdResponse.Data>,
        isEmailMyself: Boolean
    ) {

        val binding: BottomsheetReportNamesBinding =
            BottomsheetReportNamesBinding.inflate(LayoutInflater.from(this))
        mBottomSheetReportsList = BottomSheetDialog(this)
        mBottomSheetReportsList.setContentView(binding.getRoot())
        mBottomSheetReportsList.show()

        // Remove default white color background
        val bottomSheet: FrameLayout? = mBottomSheetReportsList
            .findViewById<FrameLayout>(R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            bottomSheet.background = null
        }

        val adapter = ReportsAdapter(reportsList, object : ReportClickedListener {
            override fun onReportClicked(report: FetchAllReportByIdResponse.Data) {
                mBottomSheetReportsList.cancel()
                reportDataToGenerate = report
                if (isEmailMyself) getReport(
                    isForPM = true,
                    isPdf = true,
                    event = event,
                    isForSelf = true
                )
                else getReport(true, isPdf = false, event = event, isForSelf = false)
            }
        })
        binding.rvReportNames.adapter = adapter
    }

    private fun getReport(
        isForPM: Boolean,
        isPdf: Boolean,
        event: EventData,
        isForSelf: Boolean
    ) {
        //even if isPdf has value, use of it depends on the isForPM value in api call
        if (CheckNetwork.isInternetAvailable(this)) {

            /*            if (!Util.isUrlV20OrMobileTest(this))
                            homeViewModel.sendReport(
                                isForPM,
                                isPdf,
                                event,
                                isForSelf,
                                getString(R.string.prod_base_uri),
                                getString(R.string.mobile_report_required)
                            )
                        else*/
            homeViewModel.sendReport(
                isForPM,
                isPdf,
                event,
                isForSelf,
                getString(R.string.prod_base_uri),
                getString(R.string.mobile_report_required)
            )
        } else {
            Toast.makeText(
                this,
                getString(R.string.bad_internet_connectivity),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun downloadForms() {
        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(
                this,
                getString(R.string.bad_internet_connectivity),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        ScreenReso.isDownloadData = true

        //1. Check any offline events, if there is any create events and update event ids then
        //2. Upload event data, coc data, location data in case there is any then
        //3. Check any task data to upload task data.
        val eventDbSource = EventDataSource(this)
        val eventList = eventDbSource
            .getClientGeneratedEventIDs(this)
        if (eventList.size > 0) {
            EventIDGeneratorTask(
                this, null,
                username, password, true, this
            ).execute()
        } else {
            //checking if any field data to upload then call download forms and later events will
            //be fetched as we'll be clearing tables to let submittals fragment know that it
            //should download events
            //then sync tasks
            uploadFieldData()
        }
    }

    fun uploadFieldData() {
        val tempLogsDataSource = TempLogsDataSource(this)
        val LDSource = LocationDataSource(this)
        val fieldSource = FieldDataSource(this)
        val attachDataSource = AttachmentDataSource(this)

        //CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData()
        fieldSource.checkAndUpdateClientEventInAttachmentData()
        LDSource.checkAndUpdateClientLocationInFieldData()
        LDSource.checkAndUpdateClientLocationInAttachmentData()

        val logDetails = LogDetails()
        logDetails.allIds = ""
        logDetails.date = Util.getFormattedDateFromMilliS(
            System.currentTimeMillis(),
            GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN
        )

        logDetails.screenName = "Event Dashboard Screen"
        logDetails.details =
            "Has field data before checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size
        tempLogsDataSource.insertTempLogs(logDetails)

        val isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable
        val isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync
        val isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync()
        val cocDataSource = CocMasterDataSource(this)
        val isCoCAvailableToSync = cocDataSource.syncableCOCID.size > 0

        logDetails.details =
            "Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size
        tempLogsDataSource.insertTempLogs(logDetails)

        logDetails.details =
            ("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                    + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                    + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync)
        tempLogsDataSource.insertTempLogs(logDetails)

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            syncTasks()
        } else {
            val dataUpload = Intent(this, DataSyncActivity::class.java)
            dataUpload.putExtra("USER_NAME", username)
            dataUpload.putExtra("PASS", password)
            dataUpload.putExtra("EVENT_ID", 0) //this id is used to close

            //the event which we don't require here
            startActivityForResult(dataUpload, BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun syncTasks() {
        if (!CheckNetwork.isInternetAvailable(this)) {
            CustomToast.showToast(
                this,
                getString(R.string.bad_internet_connectivity),
                Toast.LENGTH_SHORT
            )
            return
        }

        val taskDataRequest = TaskDataResponse.Data()
        val taskDetailsDataSource = TaskDetailsDataSource(this)
        val commentsDataSource = TaskCommentsDataSource(this)
        val attachmentsDataSource = TaskAttachmentsDataSource(this)

        val commentList = commentsDataSource.getAllUnSyncedComments("")
        val dataList = taskDetailsDataSource.getAllUnSyncedTasks("")
        taskAttachmentList = attachmentsDataSource.getAllUnSyncAttachments("")

        if (commentList.size == 0 && dataList.size == 0 && taskAttachmentList.size == 0) {
            callMetaSync()
            return
        }

        taskDataRequest.taskDataList = dataList
        taskDataRequest.commentList = commentList
        val baseUrl = (this.getString(R.string.prod_base_uri)
                + this.getString(R.string.prod_user_task_sync_data))
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(Gson().toJson(taskDataRequest))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        showAlertProgress(getString(R.string.syncing_tasks_please_wait))
        //        showProgressDialog(getString(R.string.syncing_tasks_please_wait));

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, baseUrl,
            jsonObject,
            Response.Listener { response ->
                val syncRes = Gson().fromJson(
                    response.toString(),
                    TaskDataResponse::class.java
                )
                if (syncRes.data.commentList.size > 0) {
                    for (comment in syncRes.data.commentList) {
                        commentsDataSource.updateIdAndSyncFlag(
                            comment.taskCommentId.toString() + "",
                            comment.taskId.toString() + "",
                            comment.clientCommentId.toString() + ""
                        )
                    }
                }
                if (syncRes.data.taskDataList.size > 0) {
                    for (details in syncRes.data.taskDataList) {
                        mapTaskIds[details.taskId] = details.clientTaskId
                        taskDetailsDataSource.updateSyncFlagAndId(
                            details.taskId.toString() + "",
                            details.clientTaskId.toString() + ""
                        )
                        attachmentsDataSource.updateTaskId(
                            details.taskId.toString() + "",
                            details.clientTaskId.toString() + ""
                        )
                    }
                }

                //fetching attachments again to get updated new saved above task id
                val attachmentList = attachmentsDataSource.getAllUnSyncAttachments("")
                if (attachmentList.size > 0) {
                    syncTaskAttachments(attachmentList)
                } else {
                    cancelAlertProgress()
                    callMetaSync()
                }
            },
            Response.ErrorListener { error ->
                Log.e("error", error.toString())
                dismissProgressDialog()
            }) {
            override fun getHeaders(): Map<String, String> {
                val ob = DeviceInfo.getDeviceInfo(this@HomeScreenActivity)
                val deviceToken = Util.getSharedPreferencesProperty(
                    this@HomeScreenActivity,
                    GlobalStrings.NOTIFICATION_REGISTRATION_ID
                )
                val uID = Util.getSharedPreferencesProperty(
                    this@HomeScreenActivity,
                    GlobalStrings.USERID
                )
                val paramsHeader: MutableMap<String, String> = HashMap()
                paramsHeader["user_guid"] = ob.user_guid
                paramsHeader["device_id"] = ob.deviceId
                paramsHeader["user_id"] = uID
                paramsHeader["device_token"] = deviceToken
                paramsHeader["Content-Type"] = "application/json"
                return paramsHeader
            }
        }
        val mRequestQueue = Volley.newRequestQueue(this)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            40000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        mRequestQueue.add(jsonObjectRequest)
    }

    private fun syncTaskAttachments(list: ArrayList<AttachmentList>) {
        for (attachment in list) {
            val imagePath = File(attachment.fileName)

            //note that we have added image path in fileName and server need only fileName not path
            val fileName = imagePath.name
            attachment.fileName = fileName

            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(Gson().toJson(attachment))
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            homeViewModel.syncTaskMedia(
                jsonObject,
                imagePath.absolutePath,
                getString(R.string.prod_base_uri),
                getString(R.string.prod_user_task_attachment_sync)
            )

//            SyncMedia(this, jsonObject, imagePath.absolutePath, list.size).execute()
        }
    }

    /*method is used when offline events are uploaded to server*/
    override fun onTaskCompleted(response: Any?) {
        val fieldData = FieldDataSource(this)
        val attachDataSrc = AttachmentDataSource(this)
        val eventData = EventDataSource(this)
        val serverGenEventID: Int

        if (response != null) {
            if (response is String) {
                if (response == "SUCCESS") {
                    uploadFieldData()
                } else {
                    Toast.makeText(
                        this,
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
                    SampleMapTagDataSource(this)
                        .updateEventID_SampleMapTag(
                            eventToClose.eventID.toString() + "",
                            serverGenEventID.toString() + ""
                        )

                    //changing client negative eventId to server gen Id so that the event can be closed from server
                    if (eventToClose.eventID < 0) eventToClose.eventID = serverGenEventID
                    if (CheckNetwork.isInternetAvailable(this)) {
                        if (closeEvent) {
                            uploadFieldDataBeforeEndEvent(eventToClose.eventID)
                        } else {
                            uploadFieldData()
                        }
                    } else {
                        CustomToast.showToast(
                            this,
                            getString(R.string.bad_internet_connectivity),
                            5
                        )
                    }
                } else {
                    if (response.responseCode == HttpStatus.NOT_ACCEPTABLE) {
                        //04-Mar-16
                        Toast.makeText(
                            this,
                            GlobalStrings.responseMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (response.responseCode == HttpStatus.NOT_FOUND || response.responseCode == HttpStatus.LOCKED) {
                        Util.setDeviceNOT_ACTIVATED(
                            this, username,
                            password
                        )
                    }
                    if (response.responseCode == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(
                            this,
                            GlobalStrings.responseMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.unable_to_connect_to_server),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onTaskCompleted() {
        //no use
    }

    override fun setGeneratedEventID(id: Int) {
        //no use
    }

    override fun setGeneratedEventID(obj: Any?) {
        //no use
    }

    private fun callMetaSync() {
        val metaIntent = Intent(applicationContext, MetaSyncActivity::class.java)
        metaIntent.putExtra(GlobalStrings.FROM_DASHBOARD, true)
        startActivity(metaIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                syncTasks()
            }
        } else if (requestCode == SubmittalsFragment.SYNC_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            if (data.hasExtra("SYNC_FLAG")) {
                val date = System.currentTimeMillis()
                val eventClosed = data.getBooleanExtra("SYNC_FLAG", false)
                var eventEndDate = data.getStringExtra("EVENT_END_DATE")!!.toLong()
                val dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false)
                if (eventEndDate < 1) {
                    eventEndDate = date
                }
                if (dataSynced && eventClosed) {
                    val eventData = EventDataSource(this)
                    val cp = CompletionPercentageDataSource(this)
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
                        this,
                        getString(R.string.event_has_been_closed),
                        Toast.LENGTH_LONG
                    ).show()

                    val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                    try {
                        if (fragment is HomeFragment) {
                            fragment.updateEvent(eventToClose)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.event_cannot_be_closed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        } else if (requestCode == SubmittalsFragment.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {
            closingEvents(eventToClose)
        }
    }

    override fun onEventDownloadSuccess() {
        val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        try {
            if (fragment is EventsFragment) {
                fragment.eventsViewModel.getAllEvents(siteIdForProjectUser)
            }

            if (fragment is ProjectFragment) {
                fragment.refreshHomeTabOrEvents()
            }

            downloadDemoSiteEventData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 05/01/2023 download all data
    fun downloadAllEventsData() {
        val intent = Intent(this, DownloadYourOwnDataActivity::class.java)
        intent.putExtra(
            GlobalStrings.KEY_EVENT_IDS,
            "0"
        )//sending 0 or null to the request will download all data
        downloadDataResult.launch(intent)
    }

    private fun downloadDemoSiteEventData() {
        val siteIds = siteDataSource.getAllDemoSitesIds(userID)

        if (!siteIds.isNullOrBlank()) {

            val listSites = Util.splitStringToArray(",", siteIds)

            for (siteId in listSites) {
                if (!fieldDataSource.isDataAvailableForSite(siteId)) {
                    val eventIds = eventDataSource.getAllEventsOfDemoSites(siteIds)
                    if (!eventIds.isNullOrEmpty()) {
                        val intent = Intent(this, DownloadYourOwnDataActivity::class.java)
                        intent.putExtra(GlobalStrings.KEY_EVENT_IDS, eventIds)
                        intent.putExtra(GlobalStrings.KEY_DEMO_SITES, true)
                        downloadDataResult.launch(intent)
                    }
                }
            }
        }
    }

    override fun onEventDownloadFailed() {
    }

    /**
     * This method is responsible to register an action to BroadCastReceiver
     */
    private fun registerMyReceiver() {
        try {
            val intentFilter = IntentFilter()
            //            intentFilter.addAction(GlobalStrings.BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * MyBroadCastReceiver is responsible to receive broadCast from register action
     */
    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Log.d(TAG, "onReceive() called")
                Toast.makeText(context, "Notification Received.", Toast.LENGTH_LONG).show()

                val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                if (fragment is SettingFragment) {
                    fragment.initNotificationCount()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadCastReceiver)
        // make sure to unregister your receiver after finishing of this activity
        badElf.disconnectTracker()
    }

    override fun onStop() {
        super.onStop()
        badElf.disconnectTracker()
    }

    override fun onLocationDeny() {
        //no use
    }

    fun openLiveFeedScreen() {
        val fragment =
            navHostFragment.childFragmentManager.primaryNavigationFragment
        try {
            if (fragment is ProjectFragment) {
                fragment.openLiveFeedActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSearchProjectScreen() {
        lateinit var navigationAction: NavDirections
        val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        try {
            if (fragment is HomeFragment) {
                navigationAction =
                    HomeFragmentDirections.actionHomeFragmentToSearchProjectFragment()
            } else if (fragment is ProjectFragment) {
                navigationAction =
                    ProjectFragmentDirections.actionProjectFragmentToSearchProjectFragment()
            }
            findNavController(R.id.navHost).navigate(navigationAction)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}