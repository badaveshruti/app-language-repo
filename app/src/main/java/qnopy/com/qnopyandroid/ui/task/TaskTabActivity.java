package qnopy.com.qnopyandroid.ui.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.MetaSyncActivity;
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

@AndroidEntryPoint
public class TaskTabActivity extends ProgressDialogActivity implements OnTaskCompleted,
        DownloadEventListTask.OnEventDownloadListener {

    public static TaskIntentData taskData;
    private AquaBlueServiceImpl mAquaBlueService;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_tab);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.tasks));
        if (getIntent() != null) {
            taskData = (TaskIntentData) getIntent().getSerializableExtra(GlobalStrings.TASK_INTENT_DATA);
        }

        mAquaBlueService = new AquaBlueServiceImpl(this);
        username = Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(this, GlobalStrings.PASSWORD);

        loadTaskTabFragment();
    }

    private void loadTaskTabFragment() {
        Fragment fragment = new TasksTabFragment();
        Bundle bundle = new Bundle();
        if (taskData != null) {
            bundle.putSerializable(GlobalStrings.TASK_INTENT_DATA, taskData);
        }

        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrameTask, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    public void downloadForms() {

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            return;
        }

        EventDataSource eventDbSource = new EventDataSource(this);
        ArrayList<DEvent> eventList = eventDbSource
                .getClientGeneratedEventIDs(this);

        if (eventList.size() > 0) {
            new EventIDGeneratorTask(this, null,
                    username, password, true, this).execute();
        } else {
            //upon sync tasks
            //checking if any field data to upload then call download forms and later events will
            //be fetched as we'll be clearing tables to let submittals fragment know that it
            //should download events
            uploadFieldData();
        }
    }

    private void callMetaSync() {
        Intent metaIntent = new Intent(getApplicationContext(), MetaSyncActivity.class);
        metaIntent.putExtra(GlobalStrings.IS_FROM_TASK_TAB_ACT, true);
        startActivityForResult(metaIntent, GlobalStrings.REQUEST_TASK_TAB_ACTIVITY);
//        finish();
    }

    public void uploadFieldData() {

        LocationDataSource LDSource = new LocationDataSource(this);
        FieldDataSource fieldSource = new FieldDataSource(this);
        AttachmentDataSource attachDataSource = new AttachmentDataSource(this);

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        LDSource.checkAndUpdateClientLocationInFieldData();
        LDSource.checkAndUpdateClientLocationInAttachmentData();

        boolean isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();
        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(this);

        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            syncTasks();
        } else {
            Intent dataUpload = new Intent(this, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", 0); //this id is used to close
            //the event which we don't require here
            startActivityForResult(dataUpload, BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE);
        }
    }

    private void syncTasks() {

        if (!CheckNetwork.isInternetAvailable(this)) {
            CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_SHORT);
            return;
        }

        final TaskDataResponse.Data taskDataRequest = new TaskDataResponse.Data();

        final TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        final TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(this);
        TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        if (commentList.size() == 0 && dataList.size() == 0 && attachmentList.size() == 0) {
            downloadAllEvents();
            return;
        }

        taskDataRequest.setTaskDataList(dataList);
        taskDataRequest.setCommentList(commentList);

        String baseUrl = this.getString(R.string.prod_base_uri)
                + this.getString(R.string.prod_user_task_sync_data);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(taskDataRequest));
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        showProgressDialog(getString(R.string.syncing_tasks_please_wait));
        showAlertProgress();
        updateAlertProgressMsg(getString(R.string.syncing_tasks_please_wait));

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
//                    dismissProgressDialog();
                    cancelAlertProgress();
                    downloadAllEvents();
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
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(TaskTabActivity.this);
                String deviceToken = Util.getSharedPreferencesProperty(TaskTabActivity.this,
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(TaskTabActivity.this,
                        GlobalStrings.USERID);

                Map<String, String> paramsHeader = new HashMap<String, String>();
                paramsHeader.put("user_guid", ob.getUser_guid());
                paramsHeader.put("device_id", ob.getDeviceId());
                paramsHeader.put("user_id", uID);
                paramsHeader.put("device_token", deviceToken);
                paramsHeader.put("Content-Type", "application/json");
                return paramsHeader;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
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
            new SyncMedia(this, jsonObject, imagePath.getAbsolutePath(), list.size()).execute();
        }
    }

    int countMediaSync = 0;

    @Override
    public void onEventDownloadSuccess() {
        callMetaSync();
    }

    @Override
    public void onEventDownloadFailed() {
        callMetaSync();
    }

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

                    TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(mContext);
                    attachmentsDataSource.updateDataSyncFlag(resultModel.getData().getTaskId(),
                            resultModel.getData().getFileName(),
                            resultModel.getData().getClientTaskAttachmentId() == null
                                    ? resultModel.getData().getTaskAttachmentId()
                                    : resultModel.getData().getClientTaskAttachmentId(),
                            resultModel.getData().getTaskAttachmentId());
                }
            }

            if (mMediaCount == countMediaSync) {
//                dismissProgressDialog();
                cancelAlertProgress();
                downloadAllEvents();
            }
        }
    }

    private void downloadAllEvents() {
        if (CheckNetwork.isInternetAvailable(this)) {
            new DownloadEventListTask(this, this).execute();
        } else {
            Toast.makeText(this, getString(R.string.bad_internet_connectivity),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /* method is used when offline events are uploaded to server*/
    @Override
    public void onTaskCompleted(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                String result = (String) obj;
                if (result.equals("SUCCESS")) {
                    uploadFieldData();
                } else {
                    Toast.makeText(this, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onTaskCompleted() {
        //no use
    }

    @Override
    public void setGeneratedEventID(int id) {
        //no use
    }

    @Override
    public void setGeneratedEventID(Object obj) {
        //no use
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            syncTasks();
        }
        if (requestCode == GlobalStrings.REQUEST_TASK_TAB_ACTIVITY
                && resultCode == RESULT_OK && data != null) {
            try {
                boolean isSyncClicked = data.getExtras().getBoolean(GlobalStrings.IS_TASK_REFRESH);
                if (isSyncClicked) {
//                    LocationActivity.isRefreshCalledFromTabScreen = true; //to close activity
//                    SplitLocationAndMapActivity.isRefreshCalledFromTabScreen = true; //to close activity
                    /*Intent intent = new Intent();
                    intent.putExtra(GlobalStrings.IS_TASK_REFRESH, false);
                    setResult(RESULT_OK, intent);
                    finish();*/
                    loadTaskTabFragment();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
