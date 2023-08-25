package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentPdfLogsBinding
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnPdfClickListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.adapter.FileAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLog
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.FileUtil
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

@AndroidEntryPoint
class PdfLogsFragment : BaseFragment() {

    private lateinit var viewModel: PdfLogsViewModel
    private lateinit var site: Site

    private lateinit var binding: FragmentPdfLogsBinding
    lateinit var fileAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPdfLogsBinding.inflate(layoutInflater, container, false)

        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)

        viewModel = ViewModelProvider(
            this
        )[PdfLogsViewModel::class.java]

        addObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fileAdapter = FileAdapter(viewModel.vPdfList, OnPdfFileClickListener())
        binding.filesRv.adapter = fileAdapter

        binding.searchView.doOnTextChanged { text, _, _, _ ->
            fileAdapter.filter.filter(
                text
            )
        }

        handleInfoButtonVisibility()
        fetchPdfLogs()
    }

    private fun handleInfoButtonVisibility() {
        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (isShowInfoEnabled) {

            binding.ivInfoPdfLogs.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Pdf logs", "You can download previously-generated PDF logs " +
                                "right to your device. Tap the blue arrow icon next to the file " +
                                "you want to download it. Note that PDF logs must have already " +
                                "been generated from the QNOPY web portal in order to download " +
                                "them to your device.", "Got it", "",
                        false, context
                    )
                }
            }
        } else {
            binding.ivInfoPdfLogs.visibility = View.GONE
        }
    }

    private fun fetchPdfLogs() {
        if (CheckNetwork.isInternetAvailable(requireContext(), true))
            viewModel.fetchPdfLogs(PdfLogsRequest(site.siteID.toString(), "0"))
    }

    inner class OnPdfFileClickListener : OnPdfClickListener {
        override fun onPdfClicked(pdfLog: PdfLog, pos: Int) {
            val file =
                FileUtil.getFileByName(requireContext(), pdfLog.fileKey + "." + pdfLog.fileFormat)
            if (file.exists()) {
                FileUtil.openFile(requireContext(), file)
            } else {
                downloadPdfApi(pdfLog, pos, true)
            }
        }

        override fun onPdfDownloadClicked(pdfLog: PdfLog, pos: Int) {
            downloadPdfApi(pdfLog, pos, false)
        }
    }

    fun downloadPdfApi(pdfLog: PdfLog, pos: Int, openAfterDownload: Boolean) {
        if (CheckNetwork.isInternetAvailable(requireContext(), true)) {
            viewModel.downloadPdf(pdfLog, pos, openAfterDownload)
        }
    }

    fun addObserver() {
        val job = lifecycleScope.launchWhenStarted {
            viewModel.fetchPdfLogsSF.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress(getString(R.string.fetching_pdf_logs))
                    }
                    is ApiState.Failure -> {
                        //handle failure
                        cancelProgress()
                        showToast(it.msg.message.toString(), true)
                    }
                    is ApiState.Success -> {
                        val response = (it.response as PdfLogsResponse)

                        if (response.success && response.responseCode.equals(
                                HttpStatus.OK.reasonPhrase,
                                ignoreCase = true
                            ) && response.data != null
                        ) {
                            val pdfList = response.data.printLogList
                            viewModel.vPdfList.clear()
                            pdfList?.let { list ->
                                viewModel.vPdfList.addAll(list as MutableList<PdfLog>)
                                fileAdapter =
                                    FileAdapter(viewModel.vPdfList, OnPdfFileClickListener())
                                binding.filesRv.adapter = fileAdapter
//                                fileAdapter.notifyDataSetChanged()

                                if (viewModel.vPdfList.isNotEmpty()) {
                                    binding.filesRv.visibility = View.VISIBLE
                                    binding.tvNoPdfLogs.visibility = View.GONE
                                } else {
                                    binding.filesRv.visibility = View.INVISIBLE
                                    binding.tvNoPdfLogs.visibility = View.VISIBLE
                                }
                                cancelProgress()
                            }
                        } else {
                            cancelProgress()
                            response.message?.let { msg -> showToast(msg, true) }
                        }
                    }
                    is ApiState.Empty -> {}
                }
            }
        }

        val jobShowPdf = lifecycleScope.launchWhenStarted {
            viewModel.showPdfState.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress()
                    }
                    is ApiState.Failure -> {
                        //handle failure
                        cancelProgress()
                        showToast(it.msg.message.toString(), true)
                    }
                    is ApiState.Success -> {
                        cancelProgress()
                        val res = (it.response as ResponseBody)
                        withContext(Dispatchers.IO) {
                            val byteArray: ByteArray = res.bytes()
                            withContext(Dispatchers.Main) {
                                FileUtil.saveFileToInternalStorage(
                                    requireContext(),
                                    "${viewModel.vPdfLog.fileKey}" + "." + viewModel.vPdfLog.fileFormat,
                                    byteArray
                                )?.let { file ->
                                    fileAdapter.notifyItemChanged(viewModel.vPdfDownloadingPos)
                                    if (viewModel.vOpenAfterDownload) {
                                        FileUtil.openFile(requireContext(), file)
                                    }
                                }
                            }
                        }
                    }
                    is ApiState.Empty -> {}
                }
            }
        }
    }


}