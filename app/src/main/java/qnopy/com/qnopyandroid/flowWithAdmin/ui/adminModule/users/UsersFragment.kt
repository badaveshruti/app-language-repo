package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.AddUserAlertBinding
import qnopy.com.qnopyandroid.databinding.FragmentUsersBinding
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnUserListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.AssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.DeAssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole
import qnopy.com.qnopyandroid.requestmodel.SUser
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : BaseFragment() {

    private lateinit var site: Site
    private lateinit var userAdapter: UsersAdapter
    private var companyId: Int = 0
    private var userId: Int = 0
    lateinit var binding: FragmentUsersBinding
    private lateinit var viewModel: UsersFragmentViewModel

    @Inject
    lateinit var siteUserRoleDataSource: SiteUserRoleDataSource

    @Inject
    lateinit var userDataSource: UserDataSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUsersBinding.inflate(inflater, container, false)

        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)

        viewModel = ViewModelProvider(this)[UsersFragmentViewModel::class.java]
        userId = Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.USERID).toInt()
        companyId =
            Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.COMPANYID).toInt()

        fetchUsersData()

        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (isShowInfoEnabled) {
            binding.ivInfoAssignedUser.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Users", "Any user assigned to a project can upload data to and" +
                                " access data from a project. To remove a user, tap the trash can " +
                                "icon next to their name. To manage different user access roles, " +
                                "please log into the QNOPY web portal and navigate to the Users " +
                                "tab for your project.", "Got it", "",
                        false, context
                    )
                }
            }
        } else {
            binding.ivInfoAssignedUser.visibility = View.GONE
        }

        binding.ivAddUser.setOnClickListener {
            binding.tvAddUser.performClick()
        }
        binding.tvAddUser.setOnClickListener {
            showAssignUserAlert()
        }

        addObserver()
        return binding.root
    }

    private fun fetchUsersData() {
        val usersList = viewModel.getUsersListForSite(userId.toString(), companyId, site.siteID)
        userAdapter = UsersAdapter(usersList, UserClickListener(), userId)
        binding.rvUsers.adapter = userAdapter
    }

    private fun addObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.assignUserResFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.please_wait))
                    }
                    is ApiState.Success -> {
                        cancelProgress()

                        val response = it.response as AssignUserResponse

                        if (response.success && response.responseCode.contains(
                                HttpStatus.OK.reasonPhrase,
                                ignoreCase = true
                            ) && response.data != null
                        ) {
                            val user = response.data

                            val userToInsert = SUser()
                            userToInsert.userId = user.userId
                            userToInsert.userName = response.userNameEntered
                            userToInsert.firstName = user.firstName
                            userToInsert.lastName = user.lastName

                            userDataSource.storeUser(userToInsert)

                            val siteUserRole = SSiteUserRole()
                            siteUserRole.roleId =
                                5 //added 5 and as it could be 2 or 5 only and default added on web is 5
                            siteUserRole.userId = user.userId
                            siteUserRole.siteId = site.siteID
                            siteUserRoleDataSource.insertSiteUserRole(siteUserRole)
                            fetchUsersData()
                        } else
                            showToast(response.message, true)
                    }
                    is ApiState.Failure -> {
                        cancelProgress()
                        showToast(it.toString(), true)
                    }
                    is ApiState.Empty -> {

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deAssignUserResFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.please_wait))
                    }
                    is ApiState.Success -> {
                        cancelProgress()

                        val response = it.response as DeAssignUserResponse

                        if (response.success && response.responseCode.contains(
                                HttpStatus.OK.reasonPhrase,
                                ignoreCase = true
                            )
                        ) {
                            if (response.userId != "0" && response.posToRemove != -1) {
                                userDataSource.deleteUserByUserId(response.userId)

                                if (this@UsersFragment::userAdapter.isInitialized) {
                                    userAdapter.removeItem(response.userId, response.posToRemove)
                                }
                                showToast("User de-assigned successfully..", true)
                            }
                        } else
                            showToast(response.message, true)
                    }
                    is ApiState.Failure -> {
                        cancelProgress()
                        showToast(it.toString(), true)
                    }
                    is ApiState.Empty -> {

                    }
                }
            }
        }
    }

    private fun showAssignUserAlert() {
        val builder = AlertDialog.Builder(requireContext(), R.style.WrapContentDialogEighty)

        val addUserBinding = AddUserAlertBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(addUserBinding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        addUserBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        addUserBinding.btnAssign.setOnClickListener {
            val userNameEntered = addUserBinding.tiUserName.editText?.text?.trim().toString()

            if (userNameEntered.isEmpty()) {
                showToast("Please enter username..", true)
            } else {
                dialog.cancel()
                if (CheckNetwork.isInternetAvailable(requireContext(), true))
                    viewModel.assignUser(userNameEntered, site.siteID)
            }
        }
    }

    inner class UserClickListener : OnUserListener {
        override fun onUserBatchClicked(user: SUser, pos: Int) {

        }

        override fun onUserNameClicked(user: SUser, pos: Int) {

        }

        override fun onUserDeleteClicked(user: SUser, pos: Int) {
            showDeleteUserAlert(user, pos)
        }

        override fun onUserAddClicked(user: SUser, pos: Int) {

        }
    }

    private fun showDeleteUserAlert(user: SUser, pos: Int) {
        AlertManager.showNormalAlertWithCallback(
            "De-assign User",
            "Do you want to de_assign ${user.userName}?",
            getString(R.string.yes),
            getString(R.string.cancel),
            true,
            requireContext()
        ) {
            //this is positive button click block
            if (CheckNetwork.isInternetAvailable(requireContext(), true))
                viewModel.deAssignUser(user.userId.toString(), site.siteID.toString(), pos)
        }
    }
}