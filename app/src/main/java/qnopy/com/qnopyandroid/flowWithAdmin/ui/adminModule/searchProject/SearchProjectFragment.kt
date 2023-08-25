package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse
import qnopy.com.qnopyandroid.databinding.FragmentSearchProjectBinding
import qnopy.com.qnopyandroid.db.*
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.adapter.ProjectAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.Project
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.MetaSyncDataModel
import qnopy.com.qnopyandroid.requestmodel.SSite
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole
import qnopy.com.qnopyandroid.util.Util

@AndroidEntryPoint
class SearchProjectFragment : BaseFragment() {
    private var userId: String = ""
    val viewModel by viewModels<SearchProjectViewModel>()
    lateinit var binding: FragmentSearchProjectBinding

    lateinit var projectAdapter: ProjectAdapter
    private var companyId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchProjectBinding.inflate(layoutInflater, container, false)

        userId = Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.USERID)
        companyId =
            Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.COMPANYID).toInt()

        binding.edtSearchProject.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Util.hideKeyboard(requireActivity())
                if (!CheckNetwork.isInternetAvailable(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().getString(R.string.bad_internet_connectivity),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnEditorActionListener false
                }

                val keyword = binding.edtSearchProject.text.toString().trim()
                viewModel.fetchProjectList(ProjectListRequest(keyword))
                return@setOnEditorActionListener true
            }
            false
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeScreenActivity).setTitle("Search Projects")
        (requireActivity() as HomeScreenActivity).backButtonVisibility(true)
    }

    val projectJob = lifecycleScope.launchWhenStarted {
        viewModel.projectState.collect {
            when (it) {
                is ApiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvNoProjects.visibility = View.GONE
                }
                is ApiState.Failure -> {
                    binding.progressBar.visibility = View.GONE
                }
                is ApiState.Success -> {
                    binding.progressBar.visibility = View.GONE

                    val res = it.response as ProjectListResponse
                    if (res.success) {
                        res.data?.let { list ->
                            projectAdapter =
                                ProjectAdapter(list, OnProjectAssignListener(), requireContext())
                            binding.rvProjects.adapter = projectAdapter
                            if (list.isNotEmpty()) {
                                binding.tvNoProjects.visibility = View.GONE
                            } else {
                                binding.tvNoProjects.visibility = View.VISIBLE
                            }
                        }
                    }

                }
                is ApiState.Empty -> {}
            }
        }
    }

    val assignProjectJob = lifecycleScope.launchWhenStarted {
        viewModel.assignProjectSF.collect {
            when (it) {
                is ApiState.Loading -> {
                    showProgress()
                }
                is ApiState.Failure -> {
                    cancelProgress()
                }
                is ApiState.Success -> {
                    cancelProgress()

                    val response = it.response as MetaFormsJsonResponse
                    if (response.isSuccess && response.data != null
                        && response.responseCode.equals(HttpStatus.OK.reasonPhrase, true)
                    ) {
                        if (this@SearchProjectFragment::projectAdapter.isInitialized) {
                            projectAdapter.updateAssignButton(viewModel.vProject)
                        }

                        saveMetaForms(response)
                        Toast.makeText(
                            requireContext(),
                            "Project has been assigned.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is ApiState.Empty -> {}
            }
        }
    }

    private fun saveMetaForms(metaFormsResponse: MetaFormsJsonResponse): Int {
        val siteAppSource = SiteMobileAppDataSource(requireContext())
        if (metaFormsResponse.isSuccess) {

            val userRoleId = UserDataSource(requireContext()).getUserRolefromID(userId.toInt())

            //save project to sSite
            val siteSource = SiteDataSource(requireContext())
            val assignedSite = SSite()
            assignedSite.siteId = viewModel.vProject.siteId
            assignedSite.siteName = viewModel.vProject.siteName
            assignedSite.status = "1"
            assignedSite.siteType = GlobalStrings.SITE_TYPE_NON_PHASE_1 //default
            assignedSite.latitude = 0.0
            assignedSite.longitude = 0.0
            siteSource.storeSite(assignedSite)

            //save project and user to siteUserRole
            val sUserRole = SiteUserRoleDataSource(requireContext())
            val siteUser = SSiteUserRole()
            siteUser.userId = userId.toInt()
            siteUser.siteId = viewModel.vProject.siteId
            siteUser.roleId = userRoleId
            sUserRole.insertSiteUserRole(siteUser)

            //saving project forms and metadata if any
            var count = 0
            for (form in metaFormsResponse.data.forms) {
                val rollAppId = form.formsDetails.formId

                val listFormSites = arrayListOf<MetaSyncDataModel.FormSites>()
                val site = MetaSyncDataModel.FormSites()
                site.formId = form.formsDetails.formId
                site.formName = form.formsDetails.name
                site.siteId = viewModel.vProject.siteId
                site.status = "1"
                site.isInsert = true
                site.formSiteId = Util.getRandomNumberInRange(1111, 99999)//primary key needed

                listFormSites.add(site)

                val formSiteDb = FormSitesDataSource(requireContext())
                formSiteDb.storeBulkMobileAppList(listFormSites, true)

                for (formTabs in form.formsDetails.formTabs) {
                    count = siteAppSource.storeSiteMobileApp(
                        formTabs, companyId, rollAppId,
                        form.formsDetails.locationStatusQuery
                    ).toInt()
                }
            }
            Log.i("Assign Site data Stored", "Data Stored for v16$count")
            return count
        }
        return 0
    }

    inner class OnProjectAssignListener : ProjectAdapter.OnProjectAssignListener {
        override fun onProjectAssignClicked(project: Project) {
            if (CheckNetwork.isInternetAvailable(requireContext(), true)) {
                viewModel.vProject = project
                viewModel.assignProject(project.siteId.toString())
            }
        }
    }
}