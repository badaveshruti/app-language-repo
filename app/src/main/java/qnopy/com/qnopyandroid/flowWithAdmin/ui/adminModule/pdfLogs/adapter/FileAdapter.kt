package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.databinding.PdfFileLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnPdfClickListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLog
import qnopy.com.qnopyandroid.flowWithAdmin.utility.FileUtil
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import java.util.*

class FileAdapter(val fileList: MutableList<PdfLog>, val mOnPdfClickListener: OnPdfClickListener) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>(), Filterable {
    private val DATE_FORMAT = "MM/dd/yyyy"

    private val mFilter = LogFilter()
    private val originalList = fileList.toMutableList()

    inner class FileViewHolder(val binding: PdfFileLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(logFile: PdfLog) = with(binding) {
            fileTitleTextView.text =
                if (logFile.eventName.isNullOrEmpty()) logFile.reportName + "." + logFile.fileFormat else logFile.eventName
            fileDateTv.text = Util.getFormattedDateFromMilliS(
                logFile.date ?: Calendar.getInstance().timeInMillis,
                DATE_FORMAT
            )

            val isFileExist = FileUtil.isFileExists(
                binding.root.context,
                logFile.fileKey + "." + logFile.fileFormat
            )

            if (isFileExist) {
                downloadImg.setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        binding.root.context, R.drawable.showpassword, R.color.event_start_blue
                    )
                )
            }

            if (logFile.fileFormat?.equals("pdf") == true) {
                fileImgView.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_pdf_icon
                    )
                )
            } else if (logFile.fileFormat?.equals("docx") == true) {
                fileImgView.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_docx_icon
                    )
                )
            } else {
                fileImgView.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_unknown_file
                    )
                )
            }

            binding.root.setOnClickListener {
                mOnPdfClickListener.onPdfClicked(
                    logFile,
                    absoluteAdapterPosition
                )
            }
/*            fileDateTv.setOnClickListener {
                mOnPdfClickListener.onPdfClicked(
                    logFile,
                    absoluteAdapterPosition
                )
            }*/
            downloadImg.setOnClickListener {
                if (!isFileExist)
                    mOnPdfClickListener.onPdfDownloadClicked(
                        logFile,
                        absoluteAdapterPosition
                    )
                else
                    mOnPdfClickListener.onPdfClicked(
                        logFile,
                        absoluteAdapterPosition
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding: PdfFileLayoutBinding =
            PdfFileLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {

        val pdfLog = fileList[position]
        holder.bind(pdfLog)
    }

    override fun getItemCount(): Int = fileList.size

    override fun getFilter(): Filter = mFilter

    private inner class LogFilter() : Filter() {
        override fun performFiltering(charSeq: CharSequence?): FilterResults {
            val filteredList = arrayListOf<PdfLog>()
            val result = FilterResults()
            if (charSeq == null || charSeq.toString().isEmpty()) {
                filteredList.apply {
                    clear()
                    addAll(originalList)
                }
            } else {
                val pattern = charSeq.toString().lowercase().trim()
                originalList.forEach {
                    if ((it.reportName?.lowercase()?.contains(pattern.lowercase()) == true) ||
                        it.fileFormat?.lowercase()?.contains(pattern.lowercase()) == true ||
                        it.eventName?.lowercase()?.contains(pattern.lowercase()) == true
                    ) {
                        filteredList.add(it)
                    }
                }
            }
            result.apply {
                values = filteredList
                count = filteredList.size
            }
            return result
        }

        override fun publishResults(charSeq: CharSequence?, result: FilterResults?) {
            fileList.apply {
                clear()
                addAll(result?.values as ArrayList<PdfLog>)
                notifyDataSetChanged()
            }
        }

    }

}