package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse
import qnopy.com.qnopyandroid.databinding.FragmentFormsBinding
import qnopy.com.qnopyandroid.db.FormSitesDataSource
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFormListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms.adapter.FormsAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel.AssignFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.FileUtil
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.requestmodel.MetaSyncDataModel
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

@AndroidEntryPoint
class FormsFragment : BaseFragment() {

    private lateinit var jobGetAssignedForms: Job
    private lateinit var mobileFormsJob: Job
    private lateinit var jobAssignForm: Job
    private lateinit var jobShowPdf: Job
    private var companyId: Int = 0
    private lateinit var site: Site
    lateinit var binding: FragmentFormsBinding

    private val formViewModel: FormsFragmentViewModel by viewModels()
    private lateinit var getFormsViewModel: GetNewFormsViewModel

    lateinit var formAdapter: FormsAdapter
    private var formsList: ArrayList<SSiteMobileApp> = arrayListOf()
    private var assignedFormsList: ArrayList<Form> = arrayListOf()
    private var originalFormList: ArrayList<Form> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormsBinding.inflate(layoutInflater, container, false)

        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)

        getFormsViewModel = ViewModelProvider(
            this
        )[GetNewFormsViewModel::class.java]

        companyId =
            Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.COMPANYID).toInt()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivGetForm.setOnClickListener(GetNewFormListener())
        binding.tvGetNewForm.setOnClickListener(GetNewFormListener())

        handleInfoButtonVisibility()
        setFormsAdapter()
        addObserver()

        binding.edtSearchForm.addTextChangedListener {
            if (this::formAdapter.isInitialized) {
                formAdapter.filter.filter(it.toString())
            }
        }

        binding.swipeRefreshLayoutForms.apply {
            setOnRefreshListener {
                isRefreshing = false
                getAssignedForms()
            }
        }
    }

    private fun handleInfoButtonVisibility() {
        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (isShowInfoEnabled) {
            binding.ivInfoGetNew.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        requireActivity(),
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Get new form", "Tap this to use an existing QNOPY form that’s " +
                                "not already assigned to your project. For example, if you only " +
                                "have a daily field log but need to add a ground water monitoring " +
                                "form to your project, tap Get New Form, find the form you want, " +
                                "and then tap Assign. You will now be able to start a new Event " +
                                "with the groundwater form.", "Got it", "",
                        false, context
                    )
                }
            }
        } else {
            binding.ivInfoGetNew.visibility = View.GONE
        }
    }

    private fun setFormsAdapter() {
        formsList = formViewModel.getAllForms(site.siteID) as ArrayList<SSiteMobileApp>
        originalFormList.addAll(arrayListOf())
        formAdapter =
            FormsAdapter(
                arrayListOf(),
                FormListener(),
                requireContext()
            )

        binding.rvForms.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = formAdapter
        }
    }

    private fun getAssignedForms() {
        if (CheckNetwork.isInternetAvailable(requireContext(), true)) {
            assignedFormsList.clear()
            originalFormList.clear()
            if (this::formAdapter.isInitialized) {
                formAdapter.notifyDataSetChanged()
                binding.rvForms.visibility = View.INVISIBLE
                binding.tvNoForms.visibility = View.VISIBLE
            }
            getFormsViewModel.getAssignedForms(FormListRequest(0, site.siteID))
        }
    }

    override fun onStart() {
        super.onStart()
        getAssignedForms()
    }

    private fun addObserver() {

        jobGetAssignedForms = lifecycleScope.launchWhenStarted {
            getFormsViewModel.assignedFormsResponse.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.loading_forms))
                    }

                    is ApiState.Success -> {
                        cancelProgress()
                        val formsResponse = it.response as FormListResponse

                        if (formsResponse.success && formsResponse.responseCode.equals(
                                HttpStatus.OK.reasonPhrase,
                                ignoreCase = true
                            ) && formsResponse.data != null
                        ) {

                            val formList =
                                formsResponse.data.formList.filter { form -> form.status == "1" }
                            if (formList.isNotEmpty()) {
                                binding.rvForms.visibility = View.VISIBLE
                                binding.tvNoForms.visibility = View.GONE

                                originalFormList.addAll(formList)
                                assignedFormsList.addAll(formList)
                                formAdapter =
                                    FormsAdapter(
                                        assignedFormsList,
                                        FormListener(),
                                        requireContext()
                                    )
                                binding.rvForms.adapter = formAdapter
                            } else {
                                binding.rvForms.visibility = View.INVISIBLE
                                binding.tvNoForms.visibility = View.VISIBLE
                            }
                        } else if (!formsResponse.success && formsResponse.responseCode?.lowercase()
                            == HttpStatus.UNAUTHORIZED.reasonPhrase.lowercase()
                        ) {
                            binding.rvForms.visibility = View.INVISIBLE
                            binding.tvNoForms.visibility = View.VISIBLE
                            GlobalStrings.responseMessage = formsResponse.message
                            Util.setDeviceNOT_ACTIVATED(requireActivity(), "", "")
                        } else {
                            binding.rvForms.visibility = View.INVISIBLE
                            binding.tvNoForms.visibility = View.VISIBLE
                            formsResponse.message?.let { msg -> showToast(msg, true) }
                        }
                    }

                    is ApiState.Failure -> {
                        cancelProgress()
                    }

                    is ApiState.Empty -> {}
                }
            }
        }

        jobShowPdf = lifecycleScope.launchWhenStarted {
            getFormsViewModel.showPreviewState.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress()
                    }

                    is ApiState.Failure -> {
                        //handle failure
                        cancelProgress()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiState.Success -> {
                        cancelProgress()
                        val res = (it.response as ResponseBody)
                        withContext(Dispatchers.IO) {
                            val byteArray: ByteArray = res.bytes()
                            withContext(Dispatchers.Main) {
                                FileUtil.saveFileToInternalStorage(
                                    requireContext(),
                                    "${getFormsViewModel.vForm.formName}" + FileUtil.FILETYPE_PDF,
                                    byteArray
                                )?.let { file ->
                                    FileUtil.openFile(requireContext(), file)
                                }
                            }
                        }
                    }

                    is ApiState.Empty -> {}
                }
            }
        }

        jobAssignForm = lifecycleScope.launchWhenStarted {
            getFormsViewModel.assignAFormState.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.assigning_form))
                    }

                    is ApiState.Success -> {
                        cancelProgress()
                        val response = it.response as MetaFormsJsonResponse
                        if (response.isSuccess && response.data != null
                            && response.responseCode.lowercase() == "ok"
                        ) {
                            if (this@FormsFragment::formAdapter.isInitialized) {
                                formAdapter.updateAssignButton(getFormsViewModel.vForm.formId)
                            }
                            saveMetaForms(response)
                            Toast.makeText(
                                requireContext(),
                                "Form has been assigned.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is ApiState.Failure -> {
                        cancelProgress()
                    }

                    is ApiState.Empty -> {}
                }
            }
        }

        mobileFormsJob = lifecycleScope.launchWhenResumed {
            getFormsViewModel.mobileFormsFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.loading_new_forms))
                    }

                    is ApiState.Success -> {
                        cancelProgress()
                        val response = it.response as FormListResponse
                        response.data?.formList?.let { newFormList ->
                            if (originalFormList.isEmpty() && newFormList.isNotEmpty()) {
                                binding.rvForms.visibility = View.VISIBLE
                                binding.tvNoForms.visibility = View.GONE
                            }

                            //clearing add adding again so that if api hit again shown list will have new updated data
                            assignedFormsList.clear()
                            assignedFormsList.addAll(originalFormList)

                            newFormList.forEach { newForm ->
                                newForm.getNewForm = true
                                /*                                val form = SSiteMobileApp()
                                                                form.display_name = newForm.formName
                                                                form.mobileAppId = newForm.formId
                                                                form.isGetNewForm = true
                                                                form.formPreview = newForm.formPreview
                                                                formsList.add(form)*/
                            }

                            assignedFormsList.addAll(newFormList)

                            if (this@FormsFragment::formAdapter.isInitialized) {
                                formAdapter.updateOriginalList(assignedFormsList)
                                formAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    is ApiState.Failure -> {
                        cancelProgress()
                    }

                    is ApiState.Empty -> {}
                }
            }
        }
    }

    private fun saveMetaForms(metaFormsResponse: MetaFormsJsonResponse): Int {
        val siteAppSource = SiteMobileAppDataSource(requireContext())
        if (metaFormsResponse.isSuccess) {
            var count = 0
            for (form in metaFormsResponse.data.forms) {
                val rollAppId = form.formsDetails.formId

                val listFormSites = arrayListOf<MetaSyncDataModel.FormSites>()
                val site = MetaSyncDataModel.FormSites()
                site.formId = form.formsDetails.formId
                site.formName = form.formsDetails.name
                site.siteId = this.site.siteID
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
            Log.i("SiteMobileAppData", "Data Stored for v16$count")
            return count
        }
        return 0
    }

    /*    override fun onPause() {
            super.onPause()
            jobAssignForm.cancel()
            jobShowPdf.cancel()
    //        jobGetAssignedForms.cancel()
    //        mobileFormsJob.cancel()
        }*/

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::jobAssignForm.isInitialized)
            jobAssignForm.cancel()
        if (this::jobShowPdf.isInitialized)
            jobShowPdf.cancel()
        if (this::jobGetAssignedForms.isInitialized)
            jobGetAssignedForms.cancel()
        if (this::mobileFormsJob.isInitialized)
            mobileFormsJob.cancel()
    }

    inner class FormListener : OnFormListener {
        override fun onShowPreviewClicked(form: Form) {
            val file =
                FileUtil.getFileByName(requireContext(), form.formName + FileUtil.FILETYPE_PDF)

            if (file.exists()) {
                FileUtil.openFile(requireContext(), file)
            } else {

                if (!CheckNetwork.isInternetAvailable(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.bad_internet_connectivity),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                if (!form.formPreview.isNullOrBlank() && !form.formPreview.equals("null")) {
                    val newForm = Form()
                    newForm.formId = form.formId
                    newForm.formName = form.formName
                    newForm.formPreview = form.formPreview

                    getFormsViewModel.downloadPreviewPdf(newForm)
                } else
                    showToast("No preview found!", true)
            }
        }

        override fun onFormTitleClicked(form: Form) {
        }

        override fun onFormAssignClicked(form: Form) {
            showAssignAlert(form)
        }
    }

    inner class GetNewFormListener : View.OnClickListener {
        override fun onClick(v: View?) {

            if (!CheckNetwork.isInternetAvailable(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.bad_internet_connectivity),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            getFormsViewModel.fetchMobileForm(FormListRequest(0, site.siteID))

            /*            val navigationAction =
                            ProjectFragmentDirections.actionProjectFragmentToGetNewFormsFragment(site)
                        requireActivity().findNavController(R.id.navHost).navigate(navigationAction)*/
        }
    }

    fun showAssignAlert(form: Form) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Do you want to assign this project to you?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()

            if (!CheckNetwork.isInternetAvailable(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.bad_internet_connectivity),
                    Toast.LENGTH_LONG
                ).show()
                return@setPositiveButton
            }

            getFormsViewModel.assignFormToProject(
                AssignFormRequest(
                    siteId = site.siteID,
                    arrayListOf(form.formId)
                )
            )
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}