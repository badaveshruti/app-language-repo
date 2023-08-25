package qnopy.com.qnopyandroid.ui.task.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.task.EditTaskActivity;
import qnopy.com.qnopyandroid.ui.task.TaskFragment;
import qnopy.com.qnopyandroid.util.Util;

public class NewTasksAdapter extends RecyclerView.Adapter<NewTasksAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<TaskDataResponse.TaskDataList> taskDataList;
    private int mSelectedTab;

    public NewTasksAdapter(Context mContext,
                           ArrayList<TaskDataResponse.TaskDataList> taskDataList,
                           Fragment fragment, int selectedTab) {
        this.mContext = mContext;
        this.mFragment = fragment;
        this.taskDataList = taskDataList;
        this.mSelectedTab = selectedTab;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tasks_item,
                parent, false);
        return new NewTasksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        TaskDataResponse.TaskDataList taskDetail = taskDataList.get(position);

        String projectName = "";
        String locationName = "";

        SiteDataSource siteDataSource = new SiteDataSource(mContext);
        projectName = siteDataSource.getSiteNamefromID(taskDetail.getProjectId());

        if (projectName != null && !projectName.isEmpty()) {
            holder.tvProjectName.setText("Project: " + projectName);
        } else {
            holder.tvProjectName.setText("Project: N/A");
        }

        LocationDataSource locationDataSource = new LocationDataSource(mContext);
        locationName = locationDataSource.getLocationName(taskDetail.getLocationId() + "");

        if (locationName != null && !locationName.isEmpty()) {
            holder.tvLocationName.setText("Location: " + locationName);
        } else {
            holder.tvLocationName.setText("Location: N/A");
        }

        long date = 0;
        if (String.valueOf(taskDetail.getCreationDate()).length() <= 10)
            date = taskDetail.getCreationDate() * 1000;
        else
            date = taskDetail.getCreationDate();

        if (date == 0)
            holder.tvDate.setText("");
        else holder.tvDate.setText(Util.getFormattedDateTime(date,
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN));

        holder.tvStatus.setText(taskDetail.getTaskStatus());

        if (taskDetail.getFieldParameterId() != 0) {
            MetaDataSource metaDataSource = new MetaDataSource(mContext);
            String paramLabel = metaDataSource.getFieldParamLabel(taskDetail.getFieldParameterId() + "");
            holder.tvFieldParamName.setText("Q: " + paramLabel);
        } else {
            holder.tvFieldParamName.setText("Q: N/A");
        }

        if (taskDetail.getTaskTitle() != null && !taskDetail.getTaskTitle().isEmpty()) {
            holder.tvTaskTitle.setText(taskDetail.getTaskTitle());
        } else {
            holder.tvTaskTitle.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return taskDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProjectName;
        private final TextView tvLocationName;
        private final TextView tvStatus;
        private final TextView tvTaskTitle;
        private final TextView tvFieldParamName;
        public final TextView tvDate;

        public ViewHolder(@NonNull @NotNull View view) {
            super(view);
            tvProjectName = view.findViewById(R.id.tvProjectName);
            tvLocationName = view.findViewById(R.id.tvLocationName);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvTaskTitle = view.findViewById(R.id.tvTaskTitle);
            tvFieldParamName = view.findViewById(R.id.tvFieldParamName);
            tvDate = view.findViewById(R.id.tvDate);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, EditTaskActivity.class);
                intent.putExtra(GlobalStrings.TASK_DATA, taskDataList.get(getBindingAdapterPosition()));
                mFragment.startActivityForResult(intent,
                        GlobalStrings.REQUEST_CODE_EDIT_TASK);
            });
        }
    }

    public void updateItem(TaskDataResponse.TaskDataList taskData) {

        if (taskData != null && taskDataList.size() == 0) {
            ((TaskFragment) mFragment).setListVisible();
        }

        if (taskData.getParentTaskId() == 0) {
            boolean isMatched = false;
            for (TaskDataResponse.TaskDataList task : taskDataList) {
                if (task.getTaskId() == taskData.getTaskId()) {
                    task.setTaskStatus(taskData.getTaskStatus());
                    task.setDueDate(taskData.getDueDate());
                    task.setTaskDescription(taskData.getTaskDescription());
                    task.setTaskTitle(taskData.getTaskTitle());
                    task.setTaskDescription(taskData.getTaskDescription());
                    task.setLatitude(taskData.getLatitude());
                    task.setLongitude(taskData.getLongitude());
                    notifyDataSetChanged();
                    isMatched = true;
                    break;
                }
            }

            //add newly inserted task to update the task list
            if (!isMatched) {
                taskDataList.add(taskData);
                Collections.sort(taskDataList, (o1, o2) -> String.valueOf(o2.getCreationDate()).
                        compareTo(String.valueOf(o1.getCreationDate())));
                notifyDataSetChanged();
            }
        }
    }

}
