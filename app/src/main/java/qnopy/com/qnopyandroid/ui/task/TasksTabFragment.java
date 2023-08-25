package qnopy.com.qnopyandroid.ui.task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.databinding.FragmentTasksTabBinding;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.MainDrawerActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

@AndroidEntryPoint
public class TasksTabFragment extends BaseFragment {

    private Site mSite;
    private AquaBlueServiceImpl mAquaBlueService;
    private SwipeRefreshLayout swipeRefresh;
    private TaskDataResponse taskDataResponse;
    private int selectedTab = 0;
    private Fragment fragment;
    private boolean isFromForms;
    private TaskIntentData taskData;
    private MenuItem menuItemSync;
    private MenuItem menuItemTaskListView;
    private MenuItem menuItemTaskMapView;

    @Inject
    TaskDetailsDataSource taskDetailsDataSource;
    @Inject
    TaskAttachmentsDataSource attachmentsDataSource;
    @Inject
    TaskCommentsDataSource commentsDataSource;
    private FragmentTasksTabBinding binding;
    private boolean isFabMenuOpen;

    public Fragment getFragment() {
        return fragment;
    }

    public TasksTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            //when there is argument with all data then its from forms, must handle this
            //in future if there are any arguments passed other than below from forms
            taskData = (TaskIntentData) getArguments().getSerializable(GlobalStrings.TASK_INTENT_DATA);
            mSite =
                    Utils.INSTANCE.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site.class);
            if (taskData != null)
                isFromForms = true;
        } else {
            isFromForms = false;
        }

        mAquaBlueService = new AquaBlueServiceImpl(getActivity());
        setTabLayout(view);

        initFabMenu();

        //this is to initiate options visibility as while inflating also code has done but seems
        //menu is inflated before the fragment loads
        if ((getActivity() instanceof HomeScreenActivity)) {
            ((HomeScreenActivity) getActivity()).prepareOptionMenuForTaskFrag();
        }
    }

    private void initFabMenu() {
        binding.fabTaskMenu.setVisibility(View.VISIBLE);

        binding.fabAddTask.setOnClickListener(v -> {
            binding.fabTaskMenu.collapse();
            createNewTask();
        });
        binding.fabTaskMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fabTaskMenu.collapse();
                binding.fabTaskList.setVisibility(View.VISIBLE);
                binding.fabTaskMap.setVisibility(View.GONE);
                loadTaskFragment(new TaskMapFragment());
            }
        });

        binding.fabTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fabTaskMenu.collapse();
                binding.fabTaskList.setVisibility(View.GONE);
                binding.fabTaskMap.setVisibility(View.VISIBLE);
                loadTaskFragment(new TaskFragment());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ((getActivity() instanceof HomeScreenActivity)) {
            ((HomeScreenActivity) getActivity()).hideOptionMenuForTaskFrag();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!(getActivity() instanceof HomeScreenActivity)) {
            //noinspection deprecation
            setHasOptionsMenu(true);
        }

        if (getActivity() instanceof HomeScreenActivity) {
            if (mSite != null) {
                ((HomeScreenActivity) getActivity()).backButtonVisibility(true);
            } else {
                ((HomeScreenActivity) getActivity()).setTitle("Tasks");
                ((HomeScreenActivity) getActivity()).setSyncMenuIconVisible(true);
                ((HomeScreenActivity) getActivity()).backButtonVisibility(false);
            }
        }
    }

    private void hitGetAllTasksApi() {

        if (!CheckNetwork.isInternetAvailable(getActivity())) {
            CustomToast.showToast(getActivity(), getString(R.string.bad_internet_connectivity), Toast.LENGTH_SHORT);
            return;
        }

        if (checkAnyTaskToSync()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.sync_tasks_before_reloading);
            builder.setPositiveButton(getString(R.string.action_sync), (dialog, which) -> syncTasks());

            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            new GetAllTasks().execute();
        }
    }

    private void setTabLayout(View view) {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.active)));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.completed_upper_case));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        loadTaskFragment(new TaskFragment());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                selectedTab = tab.getPosition();
                if (fragment != null && fragment instanceof TaskFragment)
                    loadTaskFragment(new TaskFragment());
                else if (fragment != null && fragment instanceof TaskMapFragment)
                    ((TaskMapFragment) fragment).getTaskList(selectedTab);
                else
                    loadTaskFragment(new TaskFragment());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void loadTaskFragment(Fragment frag) {
        fragment = frag;

        String siteId = "";
        if (getActivity() instanceof MainDrawerActivity) {
            siteId = ((MainDrawerActivity) getActivity()).getSiteIdForProjectUser();
        }

        if (getActivity() instanceof HomeScreenActivity) {
            siteId = ((HomeScreenActivity) getActivity()).getSiteIdForProjectUser();
        }

        if (mSite != null)
            siteId = mSite.getSiteID() + "";

        Bundle bundle = new Bundle();

        if (isFromForms)
            bundle.putSerializable(GlobalStrings.TASK_INTENT_DATA, taskData);

        bundle.putInt(GlobalStrings.SELECTED_TAB, selectedTab);
        bundle.putBoolean(GlobalStrings.IS_FROM_FORMS, isFromForms);
        bundle.putString(GlobalStrings.KEY_SITE_ID, siteId);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!(getActivity() instanceof HomeScreenActivity)) {
            inflater.inflate(R.menu.task_fragment_menu, menu);
            menuItemSync = menu.findItem(R.id.action_sync);

            menuItemTaskListView = menu.findItem(R.id.action_task_view);
            menuItemTaskMapView = menu.findItem(R.id.action_task_map_view);

            //added below condition as whenever either of the fragment is loaded options menu
            // is inflated again and to avoid menu icon hiding
            if (fragment != null && fragment instanceof TaskFragment) {
                menuItemTaskMapView.setVisible(true);
                menuItemTaskListView.setVisible(false);
            } else {
                menuItemTaskMapView.setVisible(false);
                menuItemTaskListView.setVisible(true);
            }
            setSyncBadge();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            Util.setSharedPreferencesProperty(getActivity(), GlobalStrings.IS_TASK_REFRESH,
                    true);
            if (getActivity() instanceof MainDrawerActivity) {
                ((MainDrawerActivity) getActivity()).downloadForms();
            } else if (getActivity() instanceof TaskTabActivity) {
                ((TaskTabActivity) getActivity()).downloadForms();
            }
//            syncTasks();
        }

        if (item.getItemId() == R.id.action_refresh) {
            hitGetAllTasksApi();
        }

        if (item.getItemId() == R.id.action_new_task) {
            createNewTask();
        }

        if (item.getItemId() == R.id.action_task_view) {
            menuItemTaskMapView.setVisible(true);
            menuItemTaskListView.setVisible(false);
            loadTaskFragment(new TaskFragment());
        }

        if (item.getItemId() == R.id.action_task_map_view) {
            menuItemTaskMapView.setVisible(false);
            menuItemTaskListView.setVisible(true);
            loadTaskFragment(new TaskMapFragment());
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        setSyncBadge();

        if (!taskDetailsDataSource.hasTasksData()) {
            hitGetAllTasksApi();
        }
    }

    private void setSyncBadge() {
        if (menuItemSync != null) {
            Util.setBadgeCount(getActivity(), menuItemSync, "",
                    Util.isThereAnyDataToSync(getActivity()));
        }
    }

    public void createNewTask() {
        if (fragment != null && fragment instanceof TaskFragment)
            ((TaskFragment) fragment).startEditTaskActivity();

        if (fragment != null && fragment instanceof TaskMapFragment)
            ((TaskMapFragment) fragment).startEditTaskActivity();
    }

    private boolean checkAnyTaskToSync() {
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        return commentList.size() != 0 || dataList.size() != 0 || attachmentList.size() != 0;
    }

    private void syncTasks() {

        if (!CheckNetwork.isInternetAvailable(getActivity())) {
            CustomToast.showToast(getActivity(), getString(R.string.bad_internet_connectivity), Toast.LENGTH_SHORT);
            return;
        }

        final TaskDataResponse.Data taskDataRequest = new TaskDataResponse.Data();

        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        if (commentList.size() == 0 && dataList.size() == 0 && attachmentList.size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.no_data_to_sync), Toast.LENGTH_SHORT).show();
            return;
        }

        taskDataRequest.setTaskDataList(dataList);
        taskDataRequest.setCommentList(commentList);

        String baseUrl = getActivity().getString(R.string.prod_base_uri)
                + getActivity().getString(R.string.prod_user_task_sync_data);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(taskDataRequest));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgress(getString(R.string.syncing_please_wait));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, baseUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                TaskDataResponse syncRes = new Gson().fromJson(response.toString(), TaskDataResponse.class);

                if (syncRes.getData().getCommentList().size() > 0) {
                    for (TaskDataResponse.CommentList comment : syncRes.getData().getCommentList()) {
                        commentsDataSource.updateIdAndSyncFlag(comment.getTaskCommentId() + "",
                                comment.getTaskId() + "",
                                comment.getClientCommentId() + "");
                    }
                }

                if (syncRes.getData().getTaskDataList().size() > 0) {
                    for (TaskDataResponse.TaskDataList details : syncRes.getData().getTaskDataList()) {
                        taskDetailsDataSource.updateSyncFlagAndId(details.getTaskId() + "",
                                details.getClientTaskId() + "");
                        attachmentsDataSource.updateTaskId(details.getTaskId() + "",
                                details.getClientTaskId() + "");
                    }
                }

                ArrayList<TaskDataResponse.AttachmentList> attachmentList
                        = attachmentsDataSource.getAllUnSyncAttachments("");

                if (attachmentList.size() > 0) {
                    syncAttachments(attachmentList);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.tasks_synced_successfully), Toast.LENGTH_SHORT).show();
                    cancelProgress();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(getActivity());
                String deviceToken = Util.getSharedPreferencesProperty(getActivity(),
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(getActivity(), GlobalStrings.USERID);

                Map<String, String> paramsHeader = new HashMap<String, String>();
                paramsHeader.put("user_guid", ob.getUser_guid());
                paramsHeader.put("device_id", ob.getDeviceId());
                paramsHeader.put("user_id", uID);
                paramsHeader.put("device_token", deviceToken);
                paramsHeader.put("Content-Type", "application/json");
                return paramsHeader;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonObjectRequest);

    }

    private void syncAttachments(ArrayList<TaskDataResponse.AttachmentList> list) {

        for (TaskDataResponse.AttachmentList attachment : list) {
            File imagePath = new File(attachment.getFileName());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new Gson().toJson(attachment));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SyncMedia(getActivity(), jsonObject, imagePath.getAbsolutePath(), list.size()).execute();
        }
    }

    int countMediaSync = 0;

    private class SyncMedia extends AsyncTask<MediaType, Void, String> {

        //        File mFile;
        MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
        AttachmentTaskResponseModel resultModel = null;
        Context mContext;
        JSONObject mJsonObjectMediaData;
        String mPath;
        int mMediaCount;

        SyncMedia(Context context, JSONObject jsonObjectMediaData, String path, int mediaCount) {
            mContext = context;
            mJsonObjectMediaData = jsonObjectMediaData;
            mPath = path;
            mMediaCount = mediaCount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(MediaType... mediaTypes) {
            String response = null;

            File file = new File(mPath);
            try {
                if (file.exists()) {
                    files.add("files", new FileSystemResource(file));
                }
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            files.add("media", mJsonObjectMediaData.toString());

            resultModel = mAquaBlueService.TaskMediaUpload(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.prod_user_task_attachment_sync),
                    files);
            if (resultModel != null) {
                if (resultModel.isSuccess()) {
                    response = "SUCCESS";
                } else {
                    response = "FALSE";
                }
            } else {
                Log.e("imageUpload", "doInBackground: fails to upload image attachment");
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                countMediaSync++;
                if (s.equals("FALSE")) {
                    Log.e("imageUpload", "onPostExecute: fails to upload image attachment");
                } else if (s.equals("SUCCESS")) {
                    Log.e("imageUpload", "onPostExecute: image attachment upload success" + resultModel.getData().getTaskId());

                    attachmentsDataSource.updateDataSyncFlag(resultModel.getData().getTaskId(),
                            resultModel.getData().getFileName(),
                            resultModel.getData().getClientTaskAttachmentId() == null
                                    ? resultModel.getData().getTaskAttachmentId()
                                    : resultModel.getData().getClientTaskAttachmentId(),
                            resultModel.getData().getTaskAttachmentId());
                }
            }

            if (mMediaCount == countMediaSync) {
                Toast.makeText(getActivity(), getString(R.string.tasks_synced_successfully), Toast.LENGTH_SHORT).show();
                cancelProgress();
            }
        }
    }

    private class GetAllTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(getString(R.string.downloading_tasks_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //fetching all tasks
            taskDataResponse = mAquaBlueService.getAllTasks(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.get_all_tasks), "0");

            if (taskDataResponse != null) {
                if (taskDataResponse.isSuccess()) {

                    //deleting task tables. Note- lastSyncDate sent as 0 you'll get all tasks so
                    //no issues to delete tables but if the lastSync date is managed not 0 then
                    //please update table if the data exist
                    taskDetailsDataSource.truncateTaskTables();

                    taskDetailsDataSource
                            .insertTaskData(taskDataResponse.getData()
                                    .getTaskDataList(), 1);

                    attachmentsDataSource.insertAttachmentData(taskDataResponse.getData()
                            .getAttachmentList(), 1);

                    commentsDataSource.insertTaskComments(taskDataResponse.getData().getCommentList(), 1);
                    loadTaskFragment(new TaskFragment());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            cancelProgress();
        }
    }
}
