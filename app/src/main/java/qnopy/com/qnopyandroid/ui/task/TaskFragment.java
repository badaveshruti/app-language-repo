package qnopy.com.qnopyandroid.ui.task;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Collections;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.activity.MainDrawerActivity;
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask;
import qnopy.com.qnopyandroid.ui.task.adapter.NewTasksAdapter;
import qnopy.com.qnopyandroid.util.Util;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

/**
 * This fragment will show Active ot Completed tasks.
 */
public class TaskFragment extends Fragment {

    private int selectedTab = 0;
    private ViewPager2 rvTasks;
    private ArrayList<TaskDataResponse.TaskDataList> taskDataLists = new ArrayList<>();
    private TextView tvNoTasks;
    private NewTasksAdapter adapter;
    private boolean isFromForms;
    private TaskIntentData taskData;
    private ScrollingPagerIndicator indicator;
    private RecyclerView recyclerTasks;
    private String siteId;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            selectedTab = getArguments().getInt(GlobalStrings.SELECTED_TAB, 0);
            isFromForms = getArguments().getBoolean(GlobalStrings.IS_FROM_FORMS);
            taskData = (TaskIntentData) getArguments().getSerializable(GlobalStrings.TASK_INTENT_DATA);
            siteId = getArguments().getString(GlobalStrings.KEY_SITE_ID);
        }

        rvTasks = view.findViewById(R.id.rvTasks);
        recyclerTasks = view.findViewById(R.id.recyclerTasks);
        tvNoTasks = view.findViewById(R.id.tvNoTasks);
        indicator = view.findViewById(R.id.indicator);

//        setRecyclerView();
    }

    private void setRecyclerView() {

        TaskDetailsDataSource dataSource = new TaskDetailsDataSource(getActivity());
        if (!isFromForms)
            taskDataLists = dataSource.getAllTasks(selectedTab, 0, siteId);//parent task id is 0
        else
            taskDataLists = dataSource.getAllTasksByFormDetails(selectedTab,
                    0, taskData);//parent task id is 0

        if (taskDataLists.size() > 0) {
//            rvTasks.setVisibility(View.VISIBLE);
            recyclerTasks.setVisibility(View.VISIBLE);
            tvNoTasks.setVisibility(View.GONE);

            Collections.sort(taskDataLists, (o1, o2) -> String.valueOf(o2.getCreationDate()).
                    compareTo(String.valueOf(o1.getCreationDate())));
        } else {
//            rvTasks.setVisibility(View.INVISIBLE);
            recyclerTasks.setVisibility(View.INVISIBLE);
            tvNoTasks.setVisibility(View.VISIBLE);
        }

        adapter = new NewTasksAdapter(getActivity(), taskDataLists, this, selectedTab);
        recyclerTasks.setHasFixedSize(true);
        recyclerTasks.setItemAnimator(new DefaultItemAnimator());
        recyclerTasks.setAdapter(adapter);
    }

    public void setListVisible() {
        rvTasks.setVisibility(View.VISIBLE);
        tvNoTasks.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
        boolean isRefresh = Util.getSharedPrefBoolProperty(getActivity(), GlobalStrings.IS_TASK_REFRESH);
        if (isRefresh) {
            Util.setSharedPreferencesProperty(getActivity(), GlobalStrings.IS_TASK_REFRESH,
                    false);
            if (CheckNetwork.isInternetAvailable(getActivity()) && getActivity() instanceof MainDrawerActivity) {
                new DownloadEventListTask((AppCompatActivity) getActivity(), new DownloadEventListTask.OnEventDownloadListener() {
                    @Override
                    public void onEventDownloadSuccess() {
                        //no use
                    }

                    @Override
                    public void onEventDownloadFailed() {
                        //no use
                    }
                }, "").execute();
            }
            setRecyclerView();
        }
    }

    public void startEditTaskActivity() {

        Intent intent
                = new Intent(getActivity(), EditTaskActivity.class);

        if (isFromForms) {
            intent.putExtra(GlobalStrings.KEY_SITE_ID, taskData.getProjectId());
            intent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID,
                    taskData.getFieldParamId());
            intent.putExtra(GlobalStrings.KEY_LOCATION_ID,
                    Long.parseLong(taskData.getLocationId()));
            intent.putExtra(GlobalStrings.KEY_MOBILE_APP_ID,
                    taskData.getMobileAppId());
            intent.putExtra(GlobalStrings.KEY_SET_ID,
                    taskData.getSetId());
        } else if (siteId != null && !siteId.isEmpty())
            intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId);

        startActivityForResult(intent,
                GlobalStrings.REQUEST_CODE_EDIT_TASK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalStrings.REQUEST_CODE_EDIT_TASK
                && resultCode == RESULT_OK && data != null) {
            if (adapter != null) {
                TaskDataResponse.TaskDataList task
                        = (TaskDataResponse.TaskDataList) data.getSerializableExtra(
                        GlobalStrings.TASK_DATA);
                if (task != null) {
                    adapter.updateItem(task);
                    recyclerTasks.setVisibility(View.VISIBLE);
                    tvNoTasks.setVisibility(View.GONE);
                }
            }
        }
    }
}
