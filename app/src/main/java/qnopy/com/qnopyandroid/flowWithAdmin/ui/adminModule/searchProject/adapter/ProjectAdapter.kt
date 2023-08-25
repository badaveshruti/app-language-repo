package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.databinding.NewProjectLayoutBinding
import qnopy.com.qnopyandroid.db.SiteDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.Project
import qnopy.com.qnopyandroid.util.Util

class ProjectAdapter(
    val projectList: ArrayList<Project>,
    val listener: OnProjectAssignListener,
    val context: Context
) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>(), Filterable {
    private var userId: String = ""
    private val originalList: List<Project> = projectList.toList()
    private val mFilter: ProjectFilter = ProjectFilter()

    init {
        userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID)
    }

    inner class ProjectViewHolder(val binding: NewProjectLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) = with(binding) {
            tvProjectName.text = project.siteName

            project.isNotAssigned =
                !SiteDataSource(context).isSiteExistForUser(userId, project.siteId.toString())

            if (project.isNotAssigned) {
                btnAssign.background =
                    ContextCompat.getDrawable(context, R.drawable.round_corner_blue)
                btnAssign.isEnabled = true
                btnAssign.text = "Select"
            } else {
                btnAssign.background =
                    ContextCompat.getDrawable(context, R.drawable.rounded_gray_bg)
                btnAssign.text = "Assigned"
                btnAssign.isEnabled = false
            }

            btnAssign.setOnClickListener {
                projectList[absoluteAdapterPosition].position = absoluteAdapterPosition
                listener.onProjectAssignClicked(projectList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding: NewProjectLayoutBinding =
            NewProjectLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int = projectList.size

    override fun getFilter(): Filter = mFilter

    fun updateAssignButton(vProject: Project) {
        var posToUpdate = 0
        projectList.forEachIndexed { pos, site ->
            if (site.siteId == vProject.siteId) {
                site.isNotAssigned = false
                posToUpdate = pos
            }
        }

        notifyItemChanged(posToUpdate)
    }

    inner class ProjectFilter : Filter() {
        override fun performFiltering(charSeq: CharSequence?): FilterResults {
            val filteredList = arrayListOf<Project>()
            val result = FilterResults()
            if (charSeq == null || charSeq.toString().isEmpty()) {
                filteredList.apply {
                    clear()
                    addAll(originalList)
                }
            } else {
                val pattern = charSeq.toString().lowercase().trim()
                originalList.forEach {
                    if (it.siteName?.lowercase()?.contains(pattern.lowercase()) == true) {
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
            projectList.apply {
                clear()
                addAll(result?.values as ArrayList<Project>)
                notifyDataSetChanged()
            }
        }
    }

    interface OnProjectAssignListener {
        fun onProjectAssignClicked(project: Project)
    }
}