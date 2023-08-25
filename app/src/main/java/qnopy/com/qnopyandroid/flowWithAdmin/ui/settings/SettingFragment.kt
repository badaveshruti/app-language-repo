package qnopy.com.qnopyandroid.flowWithAdmin.ui.settings

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.databinding.FragmentSettingBinding
import qnopy.com.qnopyandroid.databinding.SendDbAlertBinding
import qnopy.com.qnopyandroid.db.MetaDataSource
import qnopy.com.qnopyandroid.db.NotificationsDataSource
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.settings.model.KeycloakLogoutResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.FileUtil
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.ui.activity.HelpActivity
import qnopy.com.qnopyandroid.ui.activity.MapForSiteActivity
import qnopy.com.qnopyandroid.ui.activity.NotificationActivity
import qnopy.com.qnopyandroid.ui.activity.SiteActivity
import qnopy.com.qnopyandroid.uicontrols.CustomToast
import qnopy.com.qnopyandroid.uiutils.SendDBTask
import qnopy.com.qnopyandroid.util.CSVUtil
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import zendesk.chat.ChatEngine
import zendesk.messaging.MessagingActivity
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var cbShowInfoBtn: CheckBox
    private lateinit var cbFasterForms: CheckBox
    private var counterView: TextView? = null
    private var redCircle: FrameLayout? = null

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var userName: String
    private var userRole: Int = 0

    private lateinit var cbCaptureSignature: CheckBox
    private lateinit var cbSplitScreen: CheckBox

    private var keyCloakGuid: String? = ""

    @Inject
    lateinit var metaDataSource: MetaDataSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        userName = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME)
        userRole = viewModel.getUserRole(userName)

        val userDbSource = UserDataSource(requireContext())
        keyCloakGuid =
            Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.KEYCLOAK_GUID)
        binding.tvFirstName.text =
            userDbSource.getFirstNameFromID((requireActivity() as HomeScreenActivity).userID)

        with(getString(R.string.version) + " " + Util.getAppVersionName(context)) {
            binding.versionTitle.text = this
        }
        binding.tvUserName.text = userName

        binding.navigationMenu.setNavigationItemSelectedListener(this)
        initVisibility()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeScreenActivity).setSyncMenuIconVisible(false)

        val navItem: MenuItem = binding.navigationMenu.menu.findItem(R.id.nav_notification)
        val rootView = navItem.actionView as FrameLayout

        redCircle = rootView.findViewById(R.id.view_alert_red_circle)
        counterView = rootView.findViewById(R.id.view_alert_count_textview)

        setShowInfoCheckbox()

        setSplitScreenCheckbox()

        setCaptureSignatureAndBgService()

        setFasterFormsCheckbox()

        addObserver()
    }

    private fun setShowInfoCheckbox() {
        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            requireContext(), GlobalStrings.ENABLE_INFO_BUTTONS
        )

        val checkItem: MenuItem = binding.navigationMenu.menu.findItem(R.id.menu_show_info)

        if (!ScreenReso.isMobile2POINT0) checkItem.isVisible = false

        cbShowInfoBtn = checkItem.actionView as CheckBox
        cbShowInfoBtn.isChecked = isShowInfoEnabled

        cbShowInfoBtn.setOnCheckedChangeListener { _, isChecked ->
            Util.setSharedPreferencesProperty(
                requireContext(), GlobalStrings.ENABLE_INFO_BUTTONS, isChecked
            )
        }
    }

    private fun addObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.openIdLogoutResponse.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.please_wait))
                    }

                    is ApiState.Failure -> {
                        cancelProgress()
                        showToast(it.toString(), false)
                    }

                    is ApiState.Success -> {
                        cancelProgress()
                        /*if (Util.isStagingUrl(requireContext())) {
                            val response = Gson().fromJson(
                                it.response.toString(), KeycloakLogoutResponse::class.java
                            )

                            if (response.success && response.data != null)
                                deleteAppData()
                            else if (!response.success && !response.error.isNullOrEmpty()) showToast(
                                response.error,
                                false
                            ) else showToast("Error logging out... please try again", false)
                        } else*/
                            deleteAppData()
                    }

                    is ApiState.Empty -> {
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initNotificationCount()
    }

    fun initNotificationCount() {
        if (counterView != null) {
            val count =
                NotificationsDataSource(requireContext()).getNotificationCount((requireActivity() as HomeScreenActivity).userID.toInt())
            if (count[1] > 0) {
                redCircle!!.visibility = View.VISIBLE
                counterView!!.text = if (count[1] > 99) "99+" else count[1].toString() + ""
                runAnimation()
            } else {
                redCircle!!.visibility = View.GONE
            }
        }

        (requireActivity() as HomeScreenActivity).setSettingsBadge()
    }

    private fun runAnimation() {
        val alertAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.notification_alert)
        alertAnim.reset()
        redCircle!!.clearAnimation()
        redCircle!!.startAnimation(alertAnim)
    }

    private fun setFasterFormsCheckbox() {
        val isFasterFormsEnabled = Util.isShowNewForms(requireContext())
        val checkItem: MenuItem = binding.navigationMenu.menu.findItem(R.id.menu_faster_forms)

        val title = "New Faster Form Loader<font color='#F44336'><i> (beta 0.5)</i></font>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            checkItem.title = Html.fromHtml(
                title, Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            checkItem.title = Html.fromHtml(title)
        }

        cbFasterForms = checkItem.actionView as CheckBox
        cbFasterForms.isChecked = isFasterFormsEnabled

        cbFasterForms.setOnCheckedChangeListener { _, isChecked ->
//            showFasterFormsAlert(cbFasterForms)
            Util.setSharedPreferencesProperty(
                context, GlobalStrings.IS_SHOW_FASTER_FORMS, isChecked
            )
        }
    }

    private fun showFasterFormsAlert(
        cbFasterForms: CheckBox
    ) {
        val isFasterFormsEnabled = Util.isShowNewForms(requireContext())
        cbFasterForms.isChecked = isFasterFormsEnabled

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Faster form loading")

        if (isFasterFormsEnabled) builder.setMessage(
            "Do you want to disable this feature?"
        )
        else builder.setMessage(
            "Enabling this feature allows forms to load faster than before." + " Do you want to enable this feature?"
        )

        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            dialog.cancel()

            Util.setSharedPreferencesProperty(
                context, GlobalStrings.IS_SHOW_FASTER_FORMS, !isFasterFormsEnabled
            )
            cbFasterForms.isChecked = !isFasterFormsEnabled
        }

        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setSplitScreenCheckbox() {
        val isSplitScreenEnabled = Util.getSharedPrefBoolProperty(
            requireContext(), GlobalStrings.ENABLE_SPLIT_SCREEN
        )
        val checkItem: MenuItem =
            binding.navigationMenu.menu.findItem(R.id.menu_enable_split_screen)
        if (!Util.isTablet(requireContext())) checkItem.isVisible = false

        cbSplitScreen = checkItem.actionView as CheckBox
        cbSplitScreen.isChecked = isSplitScreenEnabled

        cbSplitScreen.setOnCheckedChangeListener { _, isChecked ->
            Util.setSharedPreferencesProperty(
                requireContext(), GlobalStrings.ENABLE_SPLIT_SCREEN, isChecked
            )
        }
    }

    private fun setCaptureSignatureAndBgService() {
        val capture = Util.getSharedPreferencesProperty(
            requireContext(), GlobalStrings.CAPTURE_SIGNATURE
        )
        val CAPTURE = capture?.toBoolean() ?: false

        Util.setSharedPreferencesProperty(
            requireContext(), GlobalStrings.CAPTURE_SIGNATURE, CAPTURE.toString()
        )

        val checkItem: MenuItem = binding.navigationMenu.menu.findItem(R.id.enable_signature)
        cbCaptureSignature = checkItem.actionView as CheckBox
        cbCaptureSignature.isChecked = CAPTURE
        cbCaptureSignature.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            Util.setSharedPreferencesProperty(
                requireContext(), GlobalStrings.CAPTURE_SIGNATURE, isChecked.toString()
            )
        }
    }

    private fun initVisibility() {
        if (userRole in listOf(
                GlobalStrings.SUPER_ADMIN,
                GlobalStrings.CLIENT_ADMIN,
                GlobalStrings.PROJECT_MANAGER,
                GlobalStrings.TRIAL_USER
            )
        ) {
            binding.navigationMenu.menu.findItem(R.id.nav_reset_app).isVisible = true
        }

        if (!Util.isTablet(requireContext())) {
            binding.navigationMenu.menu.findItem(R.id.menu_enable_split_screen).isVisible = false
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeScreenActivity).setTitle("Settings")
        (requireActivity() as HomeScreenActivity).backButtonVisibility(false)
    }

    //REF -> MainDrawerActivity-> onNavigationItemSelected
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_notification -> {
                val notificationIntent = Intent(requireContext(), NotificationActivity::class.java)
                startActivity(notificationIntent)
            }

            R.id.nav_download_all_event_data -> {
                downloadAllDataAlert()
            }

            R.id.nav_hospital -> {
                if (CheckNetwork.isInternetAvailable(context, true)) {
                    val mapIntent = Intent(requireContext(), MapForSiteActivity::class.java)
                    mapIntent.putExtra("PREV_CONTEXT", "LocationDetail")
                    mapIntent.putExtra("OPERATION", "nearby")
                    startActivity(mapIntent)
                }
            }

            R.id.nav_reset_app -> {
                if (viewModel.isDataAvailableToSync()) {
                    val builder = AlertDialog.Builder(requireContext(), R.style.dialogStyle)
                    builder.setIcon(
                        VectorDrawableUtils.getDrawable(
                            requireContext(), R.drawable.ic_warning_red
                        )
                    )
                    builder.setTitle(getString(R.string.warning))
                        .setMessage(R.string.there_is_unsync_data)
                        .setPositiveButton(R.string.erase) { _, _ ->
                            alertForDeletingData(
                                requireContext()
                            )
                        }.setNegativeButton(R.string.cancel_upper_case, null)
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    alertForDeletingData(requireContext())
                }
            }

            R.id.nav_update_app -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=com.aqua.fieldbuddy")
                startActivity(intent)
            }

            R.id.assign_project -> {
                val intentAssignProj = Intent(requireActivity(), SiteActivity::class.java)
                intentAssignProj.putExtra("FromAssignProject", true)
                startActivity(intentAssignProj)
            }

            R.id.enable_signature -> {
                val CAPTURE: Boolean = !cbCaptureSignature.isChecked
                Util.setSharedPreferencesProperty(
                    requireContext(), GlobalStrings.CAPTURE_SIGNATURE, CAPTURE.toString()
                )
                cbCaptureSignature.isChecked = CAPTURE
            }

            R.id.menu_enable_split_screen -> {
                val isSplitScreenEnabled: Boolean = !cbSplitScreen.isChecked
                Util.setSharedPreferencesProperty(
                    requireContext(), GlobalStrings.ENABLE_SPLIT_SCREEN, isSplitScreenEnabled
                )
                cbSplitScreen.isChecked = isSplitScreenEnabled

            }

            R.id.menu_show_info -> {
                val isShowInfoEnabled: Boolean = !cbShowInfoBtn.isChecked
                Util.setSharedPreferencesProperty(
                    requireContext(), GlobalStrings.ENABLE_INFO_BUTTONS, isShowInfoEnabled
                )
                cbShowInfoBtn.isChecked = isShowInfoEnabled
            }

            R.id.nav_copy_db -> {
                val intentChooser = Intent(Intent.ACTION_GET_CONTENT)
                intentChooser.type = "*/*"
                intentChooser.addCategory(Intent.CATEGORY_DEFAULT)
                copyDbResultLauncher.launch(intentChooser)
            }

            R.id.nav_download_forms -> {
                if (requireActivity() is HomeScreenActivity) {
                    (requireActivity() as HomeScreenActivity).isForcedSyncClicked = true
                    (requireActivity() as HomeScreenActivity).downloadForms()
                }
            }

            R.id.menu_faster_forms -> {
                val isFasterForms: Boolean = !cbFasterForms.isChecked
                Util.setSharedPreferencesProperty(
                    context, GlobalStrings.IS_SHOW_FASTER_FORMS, isFasterForms
                )
                cbFasterForms.isChecked = isFasterForms
            }

            R.id.nav_contact_support -> {
                showContactSupportAlert(requireContext())
            }

            R.id.nav_signout -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.sign_out))
                    .setMessage(getString(R.string.are_you_sure_to_sign_out))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        hasSSOSession()
                    }.setNegativeButton(getString(R.string.no), null)
                val dia = builder.create()
                dia.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dia.show()
            }

            R.id.nav_send_db -> {
                if (CheckNetwork.isInternetAvailable(requireContext(), true)) {
                    showSendDbAlert()
                }
            }

            R.id.nav_help -> {
                startActivity(Intent(context, HelpActivity::class.java))
            }

            R.id.nav_chat_support -> {
                MessagingActivity.builder().withEngines(ChatEngine.engine()).show(requireContext())
            }

            R.id.export_all_data_csv -> {
                val list = viewModel.getDataForCSV("")
                if (list.size > 0) {
                    val csvData = CSVUtil.toCSV(list, ',', true)
                    FileUtil.exportCSVFile(requireContext(), csvData)
                } else {
                    CustomToast.showToast(
                        requireActivity(), getString(R.string.no_data_to_export), Toast.LENGTH_SHORT
                    )
                }
            }

            R.id.export_today_csv_data -> {
                val todayDataList = viewModel.getDataForCSV(
                    Util.getFormattedDate(System.currentTimeMillis())
                )
                if (todayDataList.size > 0) {
                    val csvData = CSVUtil.toCSV(todayDataList, ',', true)
                    FileUtil.exportCSVFile(requireContext(), csvData)
                } else {
                    CustomToast.showToast(
                        requireActivity(), getString(R.string.no_data_to_export), Toast.LENGTH_SHORT
                    )
                }
            }
        }
        return false
    }

    private fun showSendDbAlert() {
        val builder = AlertDialog.Builder(requireContext(), R.style.WrapContentDialogEighty)

        val sendDbBinding = SendDbAlertBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(sendDbBinding.root)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        sendDbBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        sendDbBinding.btnUpload.setOnClickListener {

            val userIssue = sendDbBinding.edtIssueDesc.text.toString().trim()
            if (userIssue.isEmpty()) showToast("Please provide description.", true)
            else {
                if (CheckNetwork.isInternetAvailable(context, true)) {
                    dialog.cancel()
                    sendDbBinding.edtIssueDesc.clearFocus()
                    Util.hideKeyboardFrom(requireContext(), sendDbBinding.edtIssueDesc)
                    SendDBTask(requireActivity() as AppCompatActivity, userIssue).execute()
                }
            }
        }
    }

    private fun downloadAllDataAlert() {
        val builder = AlertDialog.Builder(requireContext(), R.style.dialogStyle)
        builder.setIcon(
            VectorDrawableUtils.getDrawable(
                requireContext(), R.drawable.downloadicon, R.color.colorPrimary
            )
        )
        builder.setTitle(getString(R.string.DownloadData))
            .setMessage(R.string.download_all_data_msg).setPositiveButton(R.string.yes) { _, _ ->
                if (requireActivity() is HomeScreenActivity) {
                    val homeScreenActivity = requireActivity() as HomeScreenActivity
                    homeScreenActivity.downloadAllEventsData()
                }
            }.setNegativeButton(R.string.no, null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun hasSSOSession() {
        val refreshToken = Util.getSharedPreferencesProperty(
            requireContext(), GlobalStrings.KEY_OPEN_ID_TOKEN_RESPONSE
        )

        val ssoResponse = Util.getSharedPrefSSOResponseProperty(
            requireContext(), GlobalStrings.KEY_SSO_RESPONSE
        )

        if (!refreshToken.isNullOrEmpty() && ssoResponse != null && ssoResponse.realmJson != null) {
            var tokenEndPointUrl =
                (ssoResponse.realmJson.authServerUrl + "realms/" + ssoResponse.realm + "/protocol/openid-connect/logout?client_id=qnopy-mobile&refresh_token=${refreshToken}")

/*            if (Util.isStagingUrl(requireContext())) {
                if (keyCloakGuid.isNullOrEmpty()) {
                    showToast("Error logging out..", true)
                    return
                }

                tokenEndPointUrl = "${requireActivity().getString(R.string.base_url_no_auth)}${
                    requireActivity().getString(R.string.url_logout_keycloak_user)
                }$keyCloakGuid"
            }*/

            if (CheckNetwork.isInternetAvailable(
                    requireContext(),
                    false
                )
            ) viewModel.logoutOpenIdAuth(tokenEndPointUrl)
            else deleteAppData()
        } else deleteAppData()
    }

    private var copyDbResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    FileUtil.copyDataBase(requireContext(), it.data)
                }
            }
        }

    private fun showContactSupportAlert(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.dialogStyle)
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

    private fun alertForDeletingData(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(
            context
        )
        alertDialogBuilder.setTitle("Erase App")
        alertDialogBuilder.setIcon(
            VectorDrawableUtils.getDrawable(
                requireContext(), R.drawable.ic_clean, R.color.faint_black
            )
        )
        val view = LayoutInflater.from(context).inflate(
            R.layout.layout_erase_data, null, false
        )
        alertDialogBuilder.setView(view)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        val btnErase = view.findViewById<TextView>(R.id.tvErase)
        val edtErase = view.findViewById<EditText>(R.id.edtErase)

        edtErase.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.equals("Erase", ignoreCase = true)) {
                    btnErase.visibility = View.VISIBLE
                } else {
                    btnErase.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        btnErase.setOnClickListener {
            metaDataSource.eraseData()
            Toast.makeText(requireContext(), "Your data has been erased.", Toast.LENGTH_LONG).show()
            alertDialog.cancel()
        }
    }

    private fun deleteAppData() {
        Util.setLogout(requireActivity())
    }
}