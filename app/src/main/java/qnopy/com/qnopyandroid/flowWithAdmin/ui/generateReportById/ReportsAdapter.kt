package qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.databinding.ItemReportNameBinding

class ReportsAdapter(
    private var listReports: ArrayList<FetchAllReportByIdResponse.Data>,
    private val listener: ReportClickedListener
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {


    interface ReportClickedListener {
        fun onReportClicked(report: FetchAllReportByIdResponse.Data)
    }

    inner class ViewHolder(val binding: ItemReportNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: FetchAllReportByIdResponse.Data) = with(binding) {
            binding.tvReportName.text = report.reportName

            binding.root.setOnClickListener {
                listener.onReportClicked(listReports[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReportNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listReports.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listReports[position])
    }
}