package qnopy.com.qnopyandroid.ui.task.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.task.EditTaskActivity;
import qnopy.com.qnopyandroid.util.Util;

import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_ASSIGNED;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_COMPLETED;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_DISCARDED;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_ONGOING;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<TaskDataResponse.TaskDataList> taskDataList;

    SubTaskAdapter(Context context, Fragment fragment,
                   ArrayList<TaskDataResponse.TaskDataList> taskDataList) {
        this.mContext = context;
        this.mFragment = fragment;
        this.taskDataList = taskDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_subtask,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskDataResponse.TaskDataList taskDetail = taskDataList.get(position);

        holder.ivUserPic.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_user));
        holder.tvInnerTaskTitle.setText(taskDetail.getTaskTitle());
        holder.tvInnerStatus.setText(taskDetail.getTaskStatus());
        setStatusBackground(taskDetail.getTaskStatus(), holder.tvInnerStatus);

        if (taskDetail.getDueDate() != 0)
            holder.tvDate.setText(Util.getFormattedDateTime(taskDetail.getDueDate(),
                    GlobalStrings.DATE_FORMAT_MMM_DD_YYYY));
        else
            holder.tvDate.setText("N/A");

        String taskCount = String.valueOf(position + 2);
        holder.tvTaskCount.setText(taskCount);

        if (!taskDetail.getTaskStatus().equalsIgnoreCase("Completed")) {
            holder.ivTick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_green_border_circle));
        } else {
            holder.ivTick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_tick));
        }
    }

    private void setStatusBackground(String status, TextView tvStatusChooser) {
        switch (status) {
            case STATUS_ASSIGNED:
                tvStatusChooser
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.assigned_dot,
                                0, 0, 0);
                break;
            case STATUS_ONGOING:
                tvStatusChooser
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ongoing_dot,
                                0, 0, 0);
                break;
            case STATUS_COMPLETED:
                tvStatusChooser
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.complete_dot,
                                0, 0, 0);
                break;
            case STATUS_DISCARDED:
                tvStatusChooser
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.discarded_dot,
                                0, 0, 0);
                break;
            default:
                tvStatusChooser
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.open_dot,
                                0, 0, 0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return taskDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvInnerTaskTitle;
        private final CardView cvInnerTask;
        private final TextView tvInnerStatus;
        private final TextView tvTaskCount;
        private final ImageView ivTick;
        private final ImageView ivUserPic;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivTick = view.findViewById(R.id.ivTick);
            ivUserPic = view.findViewById(R.id.ivUserPic);
            tvDate = view.findViewById(R.id.tvDate);
            tvInnerTaskTitle = view.findViewById(R.id.tvInnerTaskTitle);
            cvInnerTask = view.findViewById(R.id.cvInnerTask);
            tvInnerStatus = view.findViewById(R.id.tvInnerStatus);
            tvTaskCount = view.findViewById(R.id.tvTaskCount);

            cvInnerTask.setOnClickListener(v -> {
                if (!taskDataList.get(getAdapterPosition()).getTaskStatus()
                        .equalsIgnoreCase(GlobalStrings.STATUS_COMPLETED)) {
                    Intent intent = new Intent(mContext, EditTaskActivity.class);
                    intent.putExtra(GlobalStrings.TASK_DATA, taskDataList.get(getAdapterPosition()));
                    mFragment.startActivityForResult(intent,
                            GlobalStrings.REQUEST_CODE_EDIT_TASK);
                }
            });
        }
    }

    void updateItem(TaskDataResponse.TaskDataList taskData) {

        boolean isMatched = false;
        for (TaskDataResponse.TaskDataList task : taskDataList) {
            if (task.getTaskId() == taskData.getTaskId()) {
                task.setTaskStatus(taskData.getTaskStatus());
                task.setDueDate(taskData.getDueDate());
                task.setTaskDescription(taskData.getTaskDescription());
                task.setTaskTitle(taskData.getTaskTitle());
                task.setTaskDescription(taskData.getTaskDescription());
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
