package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.view.MenuItem
import android.view.MotionEvent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.yield
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.databinding.ActivityCreateProjectBinding
import qnopy.com.qnopyandroid.db.SiteDataSource
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.requestmodel.SSite
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole
import qnopy.com.qnopyandroid.ui.activity.MapDragActivity
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity
import qnopy.com.qnopyandroid.util.Util
import javax.inject.Inject

@AndroidEntryPoint
class CreateProjectActivity : ProgressDialogActivity() {

    private var userId: String = ""
    private lateinit var motionEvent: MotionEvent
    private lateinit var binding: ActivityCreateProjectBinding
    private lateinit var locationResult: ActivityResultLauncher<Intent>
    private var createProjectRequest: CreateProjectRequest = CreateProjectRequest()
    private val viewModel: CreateProjectViewModel by viewModels()

    @Inject
    lateinit var siteDataSource: SiteDataSource

    @Inject
    lateinit var siteUserRoleDataSource: SiteUserRoleDataSource

    @Inject
    lateinit var userDataSource: UserDataSource

    companion object {
        fun startActivity(context: Context, launcher: ActivityResultLauncher<Intent>) {
            launcher.launch(Intent(context, CreateProjectActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = "Create Project"
        Utils.setToolbarTitleAndBackBtn(title, true, this)

        locationResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data.let {
                        val latLng: LatLng =
                            it?.getParcelableExtra<Parcelable>(GlobalStrings.FETCHED_LOCATION) as LatLng
                        val lat = Util.round(latLng.latitude, 5).toString()
                        val lng = Util.round(latLng.longitude, 5).toString()

                        binding.tiLatitude.editText?.setText(lat)
                        binding.tiLongitude.editText?.setText(lng)
                    }
                }
            }

        userId = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID)
        val compID = Util.getSharedPreferencesProperty(this, GlobalStrings.COMPANYID)
        createProjectRequest.status = "1"
        createProjectRequest.companyId = compID.toInt()

        setUpUi()
    }

    private fun setUpUi() {
//        Util.showKeyboard(this, binding.tiProjectName.editText)

        addObserver()
        binding.tiProjectName.editText?.requestFocus()
        motionEvent = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            MotionEvent.ACTION_UP,
            0f,
            0f,
            0
        )
        binding.tiProjectName.editText?.dispatchTouchEvent(motionEvent)

        binding.ibLocation.setOnClickListener {
            val intent = Intent(this@CreateProjectActivity, MapDragActivity::class.java)
            locationResult.launch(intent)
        }

        binding.btnCreateProject.setOnClickListener {
            binding.apply {

                createProjectRequest.siteName = tiProjectName.editText?.text.toString()
                createProjectRequest.project = tiProjectName.editText?.text.toString()
                createProjectRequest.siteNumber = tiProjectNumber.editText?.text.toString()
                createProjectRequest.projectNumber = tiProjectNumber.editText?.text.toString()
                createProjectRequest.client = tiClient.editText?.text.toString()
                createProjectRequest.address1 = tiClient.editText?.text.toString()
                createProjectRequest.city = tiCity.editText?.text.toString()
                createProjectRequest.state = tiState.editText?.text.toString()
                createProjectRequest.zip = tiZip.editText?.text.toString()

                val lat = tiLatitude.editText?.text.toString().trim()
                val lng = tiLongitude.editText?.text.toString().trim()

                if (lat.trim().isNotEmpty())
                    createProjectRequest.latitude = lat.toDouble()
                else
                    createProjectRequest.latitude = 0.0

                if (lng.trim().isNotEmpty())
                    createProjectRequest.longitude = lng.toDouble()
                else
                    createProjectRequest.longitude = 0.0
            }

            validateSaveRequest(createProjectRequest)
        }
    }

    private fun addObserver() {
        lifecycleScope.launchWhenStarted {
            yield()
            viewModel.createProjectResFlow.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        showAlertProgress("Creating Project...")
                    }

                    is ApiState.Success -> {
                        cancelAlertProgress()
                        val response = it.response as CreateProjectResponse

                        if (response.success && response.responseCode.contains("ok", true)) {
                            response.data?.let { siteId ->
                                if (siteId.isNotEmpty()) {
                                    val site = SSite()
                                    site.siteId = siteId.toInt()
                                    site.siteName = createProjectRequest.siteName
                                    site.siteNumber = createProjectRequest.siteNumber
                                    site.clientName = createProjectRequest.client
                                    site.address1 = createProjectRequest.address1
                                    site.city = createProjectRequest.city
                                    site.state = createProjectRequest.state
                                    site.zipCode = createProjectRequest.zip
                                    site.latitude = createProjectRequest.latitude
                                    site.longitude = createProjectRequest.longitude
                                    site.siteType = GlobalStrings.SITE_TYPE_NON_PHASE_1//default
                                    site.status = createProjectRequest.status

                                    siteDataSource.storeSite(site)

                                    if (userId.trim().isNotEmpty()) {
                                        val roleId =
                                            userDataSource.getUserRolefromID(userId.toInt())
                                        val siteUserRole = SSiteUserRole()
                                        siteUserRole.userId = userId.toInt()
                                        siteUserRole.siteId = siteId.toInt()
                                        siteUserRole.roleId = roleId

                                        siteUserRoleDataSource.insertSiteUserRole(siteUserRole)
                                    }
                                    showToast("Project created", true)
                                    val intent = Intent()
                                    intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }
                        } else if (response.success && response.responseCode.contains(
                                HttpStatus.NOT_ACCEPTABLE.reasonPhrase,
                                true
                            )
                        ) {
                            showToast(response.message, true)
                        }
                    }

                    is ApiState.Failure -> {
                        cancelAlertProgress()
                    }

                    is ApiState.Empty -> {

                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateSaveRequest(request: CreateProjectRequest) {
        var msg = ""
        if (request.project.trim().isEmpty())
            msg = "Enter project name"
        else if (request.projectNumber.trim().isEmpty())
            msg = "Enter project number"
        else if (request.latitude == 0.0 && request.longitude > 0.0) {
            msg = "Enter Latitude or fetch location using location icon"
        } else if (request.latitude > 0.0 && request.longitude == 0.0) {
            msg = "Enter Longitude or fetch location using location icon"
        }

        if (msg.trim().isNotEmpty())
            showToast(msg, true)
        else
            viewModel.createProject(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        motionEvent.recycle()
    }
}