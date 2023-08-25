package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.databinding.FragmentGetNewFormsBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms.GetNewFormsViewModel
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.adapter.NewFormsAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel.AssignFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel.AssignFormResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.FileUtil
import qnopy.com.qnopyandroid.util.Util

@AndroidEntryPoint
class GetNewFormsFragment : Fragment() {

    lateinit var binding: FragmentGetNewFormsBinding
    val args by navArgs<GetNewFormsFragmentArgs>()
    val viewModel: GetNewFormsViewModel by viewModels()
    var formsList: ArrayList<Form> = arrayListOf()
    lateinit var newFormsAdapter: NewFormsAdapter

    var companyId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGetNewFormsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newFormsAdapter = NewFormsAdapter(formsList, OnFormListener())

        companyId = Util.getSharedPreferencesProperty(activity, GlobalStrings.COMPANYID).toInt()

        binding.formsRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = newFormsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeScreenActivity).setTitle("Get Form")

        viewModel.fetchMobileForm(FormListRequest(0, args.site.siteID))
    }

    private inner class OnFormListener : OnFormClickListener {
        override fun OnFormPreviewClicked(form: Form, pos: Int) {

            val file =
                FileUtil.getFileByName(requireContext(), form.formName + FileUtil.FILETYPE_PDF)

            if (file.exists()) {
                FileUtil.openFile(requireContext(), file)
            } else {
                if ((form.formPreview.isNullOrBlank()) || (form.formPreview.equals("null"))) {
                    Toast.makeText(requireContext(), "No preview found", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.downloadPreviewPdf(form)
                }
            }
        }

        override fun OnFormTitleClicked(form: Form, pos: Int) {

        }

        override fun OnFormAssignClicked(form: Form, pos: Int) {
            viewModel.assignFormToProject(
                AssignFormRequest(
                    siteId = 5923,
                    arrayListOf(form.formId)
                )
            )
        }

    }

/*    val job = lifecycleScope.launchWhenStarted {
        viewModel.mobileFormsFlow.observe(viewLifecycleOwner) { formsList ->
            when (formsList) {
                is ApiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ApiState.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val response = formsList.response as FormListResponse
                    response.data?.formList?.let {
                        this@GetNewFormsFragment.formsList.clear()
                        val assignedForms = it.filter { form -> form.status != "0" }
                        this@GetNewFormsFragment.formsList.addAll(ArrayList(assignedForms))
                        newFormsAdapter.notifyItemRangeChanged(
                            0,
                            this@GetNewFormsFragment.formsList.size - 1
                        )
                    }
                }
                is ApiState.Failure -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                is ApiState.Empty -> {}
            }
        }
    }

    val jobShowPdf = lifecycleScope.launchWhenStarted {
        viewModel.showPreviewState.collect {
            when (it) {
                is ApiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ApiState.Failure -> {
                    //handle failure
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
                is ApiState.Success -> {

                    val res = (it.response as ResponseBody)
                    withContext(Dispatchers.IO) {
                        val byteArray: ByteArray = res.bytes()
                        withContext(Dispatchers.Main) {
                            FileUtil.saveFileToInternalStorage(
                                requireContext(),
                                "${viewModel.vForm.formName}" + FileUtil.FILETYPE_PDF,
                                byteArray
                            )?.let { file ->
                                binding.progressBar.visibility = View.INVISIBLE
                                FileUtil.openFile(requireContext(), file)
                            }
                        }
                    }
                }
                is ApiState.Empty -> {}
            }
        }
    }

    val jobAssignForm = lifecycleScope.launchWhenStarted {
        viewModel.assignFormState.collect {
            when (it) {
                is ApiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ApiState.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val response = it.response as AssignFormResponse
                    Toast.makeText(requireContext(), "Assigned form success", Toast.LENGTH_SHORT)
                        .show()
                }
                is ApiState.Failure -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                is ApiState.Empty -> {}
            }
        }
    }*/
}