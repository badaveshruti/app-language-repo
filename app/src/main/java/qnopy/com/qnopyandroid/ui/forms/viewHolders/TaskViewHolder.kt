package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class TaskViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity
) : RecyclerView.ViewHolder(view) {
    val tvTaskLabel: CustomTextView = view.findViewById(R.id.tvTaskLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val tvTaskCount: CustomTextView = view.findViewById(R.id.tvTaskCount)

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
        formOperations.addTaskIcon(metaData, layoutFieldControls)
    }

    fun setTaskCount(metadata: MetaData) {
        formOperations.getDefaultValueToSet(metadata, tvTaskLabel)

        var taskCount = 0
        val taskDetailsDataSource = TaskDetailsDataSource(context)
        taskCount = taskDetailsDataSource.getTasksCount(
            metadata.metaParamID,
            context.siteID.toString(), context.curSetID, context.currentAppID, context.locationID
        )

        if (taskCount > 0) {
            val count = taskCount.toString()
            scopeMainThread.launch { tvTaskCount.text = count }
            formOperations.handleTextData(metadata, tvTaskLabel)
        } else {
            scopeMainThread.launch { tvTaskCount.text = metadata.currentReading }
        }
    }

    fun addClickListener(metadata: MetaData) {
        scopeMainThread.launch {
            tvTaskCount.setOnClickListener {
                formOperations.openTaskActivity(metadata)
            }
        }
    }
}