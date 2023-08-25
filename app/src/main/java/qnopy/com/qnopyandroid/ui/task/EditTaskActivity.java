package qnopy.com.qnopyandroid.ui.task;

import static qnopy.com.qnopyandroid.GlobalStrings.COMPRESSION_RATE_100;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_ASSIGNED;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_COMPLETED;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_DISCARDED;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_ONGOING;
import static qnopy.com.qnopyandroid.GlobalStrings.STATUS_OPEN;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.MEDIA_TYPE_IMAGE;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.REQUEST_CODE_MEDIA_PICKER;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.REQUEST_CODE_PIX_IMAGE_PICKER;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.getMediaStorageDirectory;
import static qnopy.com.qnopyandroid.ui.activity.FormActivity.CAPTURE_GPS_LOCATION_REQUEST_CODE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.databinding.ActivityEditTaskBinding;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.MapDragActivity;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class EditTaskActivity extends ProgressDialogActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_SITE = 4139;
    private TaskDataResponse.TaskDataList taskDetails;
    private Options pickerOptions;
    private BottomSheetDialog mBottomSheetStatus;
    private int userID;
    private TaskAttachmentsDataSource attachmentsDataSource;
    private ProgressDialog progressDialog;
    private AquaBlueServiceImpl mAquaBlueService;
    private boolean isCreateNewTask;
    private int taskID = 0;
    private long dueDateMillis = 0;
    private String projectId = "";
    private int fieldParamId;
    private Long locationId;
    private int mobileAppId;
    private int setId;
    private boolean isCreateTaskWithNoRef;
    private LatLng latLongsFetched;
    private ActivityEditTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.entry_form_done);

        mAquaBlueService = new AquaBlueServiceImpl(this);

        if (getIntent() != null) {
            //taskDetails will have value if it is edit mode
            taskDetails = (TaskDataResponse.TaskDataList) getIntent().getSerializableExtra(GlobalStrings.TASK_DATA);

            //below 5 values will have values only when it is new task create mode from forms screen
            projectId = getIntent().getStringExtra(GlobalStrings.KEY_SITE_ID);
            fieldParamId = getIntent().getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
            locationId = getIntent().getLongExtra(GlobalStrings.KEY_LOCATION_ID, 0);
            mobileAppId = getIntent().getIntExtra(GlobalStrings.KEY_MOBILE_APP_ID, 0);
            setId = getIntent().getIntExtra(GlobalStrings.KEY_SET_ID, 0);

            try {
                if (taskDetails != null)
                    projectId = taskDetails.getProjectId() + "";
            } catch (Exception e) {
                e.printStackTrace();
            }

            //boolean is set true if the create new task called from home screen tasks tab
            if (fieldParamId == 0 && locationId == 0 && mobileAppId == 0 && setId == 0)
                isCreateTaskWithNoRef = true;
        }

        isCreateNewTask = (taskDetails == null);

        if (!isCreateNewTask) {
            taskID = taskDetails.getTaskId();
            getSupportActionBar().setTitle(taskDetails.getTaskTitle());
        } else {
            taskID = -Util.randInt(100, 999999);
            getSupportActionBar().setTitle(R.string.create_task);
        }

        userID = Integer.parseInt(Util.getSharedPreferencesProperty(this, GlobalStrings.USERID));
        attachmentsDataSource = new TaskAttachmentsDataSource(this);

        setUpUi();
    }

    private void setUpUi() {
        if (!isCreateNewTask) {
   /*         binding.edtTaskTitle.setEnabled(false);
            binding.edtDescription.setEnabled(false);
            tvTaskDate.setEnabled(false);*/

            binding.edtTaskTitle.setText(taskDetails.getTaskTitle());
            binding.tvStatus.setText(taskDetails.getTaskStatus());
            if (taskDetails.getTaskDescription() != null
                    && !taskDetails.getTaskDescription().isEmpty())
                binding.edtDescription.setText(taskDetails.getTaskDescription());

            binding.tvStatusChooser.setText(taskDetails.getTaskStatus());
            setStatusBackground(taskDetails.getTaskStatus());

            UserDataSource userDataSource = new UserDataSource(this);
            String userName = userDataSource.getUserNameFromID(taskDetails.getCreatedBy() + "");
            binding.tvCreatedBy.setText("Created by: " + userName);
            if (taskDetails.getDueDate() > 0)
                binding.tvDueDate.setText(Util.getFormattedDateTime(taskDetails.getDueDate(),
                        GlobalStrings.DATE_FORMAT_MMM_DD_YYYY_H_M_12HR));
            else
                binding.tvDueDate.setText("Set due date");

            if (taskDetails.getLatitude() != null && taskDetails.getLongitude() != null) {
                String location = taskDetails.getLatitude() + "/" + taskDetails.getLongitude();
                binding.tvLatLngs.setText(location);
            }
        } else {
            binding.edtTaskTitle.setEnabled(true);
            binding.edtTaskTitle.setHint(R.string.enter_title);
            binding.edtDescription.setEnabled(true);
            binding.tvDueDate.setEnabled(true);
            binding.tvDueDate.setHint(R.string.choose_due_date);
            binding.tvStatus.setText("Open");
            binding.tvStatusChooser.setText("Open");
            setStatusBackground("Open");
            String username = Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME);
            binding.tvCreatedBy.setText("Created by: " + username);
/*            tvCreationDate.setText(Util.getFormattedDateTime(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN));*/
        }

        binding.tvDueDate.setOnClickListener(this);
        binding.tvAddNewAttachment.setOnClickListener(this);
        binding.ivAddAttachment.setOnClickListener(this);
        binding.tvAddNewComment.setOnClickListener(this);
        binding.tvStatusChooser.setOnClickListener(this);
        binding.tvLatLngs.setOnClickListener(this);

        setViewAttachmentText();
//        updateProject();
    }

/*
    private void updateProject() {
        tvSelectProject = findViewById(R.id.tvSelectProject);
        tvSelectProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTaskActivity.this, SiteActivity.class);
                intent.putExtra(GlobalStrings.IS_FROM_ADD_EDIT_TASK, true);
                startActivityForResult(intent, REQUEST_CODE_SITE);
            }
        });

        if (projectId != null && !projectId.isEmpty()) {
            SiteDataSource siteDataSource = new SiteDataSource(this);
            if (Util.hasDigitDecimalOnly(projectId)) {
                String siteName = siteDataSource.getSiteNamefromID(Integer.parseInt(projectId));
                tvSelectProject.setText(siteName);
            }
        }
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        setViewAttachmentText();
        setViewCommentText();
    }

    private void setViewCommentText() {
        TaskCommentsDataSource taskCommentsDataSource = new TaskCommentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> attachmentList
                = taskCommentsDataSource.getAllComments(taskID);

        if (attachmentList.size() > 0)
            binding.tvAddNewComment.setText(getString(R.string.view) + " " + attachmentList.size());
        else
            binding.tvAddNewComment.setText(getString(R.string.add_new_lower_case));
    }

    private void setStatusBackground(String status) {
        switch (status) {
            case STATUS_ASSIGNED:
                binding.tvStatusChooser.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.round_corner_assigned));
                break;
            case STATUS_ONGOING:
                binding.tvStatusChooser.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.round_corner_ongoing));
                break;
            case STATUS_COMPLETED:
                binding.tvStatusChooser.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.round_corner_complete));
                break;
            case STATUS_DISCARDED:
                binding.tvStatusChooser.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.round_corner_discarded));
                break;
            default:
                binding.tvStatusChooser.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.round_corner_open));
                break;
        }
    }

    private void initGalleryPicker() {

        pickerOptions = Options.init()
                .setRequestCode(REQUEST_CODE_PIX_IMAGE_PICKER)
                .setCount(1)
                .setFrontfacing(false)
                .setExcludeVideos(true)
                .setVideoDurationLimitinSeconds(60)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath(
                        getMediaStorageDirectory(MEDIA_TYPE_IMAGE).getAbsolutePath()
                );
    }

    public void openImagePicker() {
        try {
            startActivityForResult(new Intent(this, MediaPickerActivity.class),
                    REQUEST_CODE_MEDIA_PICKER);
/*            initGalleryPicker();
            Pix.start(this, pickerOptions);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit_task, menu);
        MenuItem item = menu.findItem(R.id.menu_save_task);
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
        item.setTitle(spanString);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.menu_save_task) {
            //we have disabled save button as it may cause issues when you have an offline location
            //created or if you want the button then you'll have to manage uploading any offline
            //events or location then try updating/creating the task

            if (!isCreateNewTask) {
                updateAllDetailsInDb();
                saveTask();
            } else {
                if (binding.edtTaskTitle.getText().toString().isEmpty()) {
                    showToast(getString(R.string.add_task_title), true);
                } /*else if (tvTaskDate.getText().toString().isEmpty()) {
                    showToast("Please add due date", true);
                }*/ else {
                    saveTaskInDb();
                    saveTask();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTaskInDb() {
        //creating new task in db
        List<TaskDataResponse.TaskDataList> dataList = new ArrayList<>();
        taskDetails = new TaskDataResponse.TaskDataList();
        taskDetails.setTaskId(taskID);
        taskDetails.setTaskTitle(binding.edtTaskTitle.getText().toString());
        taskDetails.setTaskDescription(binding.edtDescription.getText().toString());
        taskDetails.setParentTaskId(0);
        taskDetails.setTaskStatus(binding.tvStatusChooser.getText().toString());

        if (latLongsFetched != null) {
            taskDetails.setLatitude(latLongsFetched.latitude);
            taskDetails.setLongitude(latLongsFetched.longitude);
        }

        if (projectId != null && !projectId.isEmpty())
            taskDetails.setProjectId(Integer.parseInt(projectId));

        if (!isCreateTaskWithNoRef) {
            taskDetails.setFieldParameterId(fieldParamId);
            taskDetails.setLocationId(locationId);
            taskDetails.setMobileAppId(mobileAppId);
            taskDetails.setSetId(setId);
        }

        taskDetails.setClientTaskId(taskID);
        taskDetails.setTaskOwner(userID + "");
        taskDetails.setDueDate(dueDateMillis);
        taskDetails.setCreatedBy(userID);
        taskDetails.setCreationDate(System.currentTimeMillis());
        dataList.add(taskDetails);

        TaskDetailsDataSource dataSource = new TaskDetailsDataSource(this);
        if (!dataSource.checkTaskIdExist(taskID))
            dataSource.insertTaskData(dataList, 0);
        else
            dataSource.updateTaskDetails(taskID + "", 0,
                    taskDetails, binding.tvStatusChooser.getText().toString());
    }

    @Override
    public void onBackPressed() {

        if (isCreateNewTask) {
            if (!binding.edtTaskTitle.getText().toString().isEmpty()
                /*&& !tvTaskDate.getText().toString().isEmpty()*/) {
                saveTaskInDb();
            }
        } else {
            updateAllDetailsInDb();
        }

        setResultData();
    }

    private void updateAllDetailsInDb() {

        int projectID = 0;
        if (projectId != null && !projectId.isEmpty()) {
            projectID = Integer.parseInt(projectId);
        }

        TaskDetailsDataSource dataSource = new TaskDetailsDataSource(this);
        if (!binding.edtTaskTitle.getText().toString().equals(taskDetails.getTaskTitle())
                || !binding.edtDescription.getText().toString().equals(taskDetails.getTaskDescription())
                || dueDateMillis > taskDetails.getDueDate() || latLongsFetched != null
                || taskDetails.getProjectId() != projectID) {

            if (dueDateMillis == 0)
                dueDateMillis = taskDetails.getDueDate();

            taskDetails.setTaskTitle(binding.edtTaskTitle.getText().toString());
            taskDetails.setTaskDescription(binding.edtDescription.getText().toString());
            taskDetails.setDueDate(dueDateMillis);

            if (projectId != null && !projectId.isEmpty())
                taskDetails.setProjectId(Integer.parseInt(projectId));

            if (latLongsFetched != null) {
                taskDetails.setLatitude(latLongsFetched.latitude);
                taskDetails.setLongitude(latLongsFetched.longitude);
            }

            dataSource.updateTaskDetails(taskID + "", 0,
                    taskDetails, binding.tvStatusChooser.getText().toString());
        }
    }

    private void setResultData() {
        Intent intent = new Intent();
        if (taskDetails != null)
            intent.putExtra(GlobalStrings.TASK_DATA, taskDetails);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.tvAddNewAttachment.getId()) {

            ArrayList<TaskDataResponse.AttachmentList> attachmentList
                    = attachmentsDataSource.getAllAttachments(taskID);

            if (attachmentList.size() > 0) {
                Intent intent = new Intent(this, AttachmentSlideShowActivity.class);
                intent.putExtra(GlobalStrings.TASK_ID, taskID);
                startActivity(intent);
            } else {
                openImagePicker();
            }
        } else if (view.getId() == binding.ivAddAttachment.getId()) {
            openImagePicker();
        } else if (view.getId() == R.id.llOpen) {
            binding.tvStatusChooser.setText(STATUS_OPEN);
            binding.tvStatus.setText(STATUS_OPEN);
            setStatusBackground(STATUS_OPEN);
            if (!isCreateNewTask)
                taskDetails.setTaskStatus(STATUS_OPEN);
            updateStatusInDB();
            mBottomSheetStatus.cancel();
        } else if (view.getId() == R.id.llOngoing) {
            binding.tvStatusChooser.setText(STATUS_ONGOING);
            binding.tvStatus.setText(STATUS_ONGOING);
            setStatusBackground(STATUS_ONGOING);
            if (!isCreateNewTask)
                taskDetails.setTaskStatus(STATUS_ONGOING);
            updateStatusInDB();
            mBottomSheetStatus.cancel();
        } else if (view.getId() == R.id.llAssigned) {
            binding.tvStatusChooser.setText(STATUS_ASSIGNED);
            setStatusBackground(STATUS_ASSIGNED);
            if (!isCreateNewTask)
                taskDetails.setTaskStatus(STATUS_ASSIGNED);
            updateStatusInDB();
            mBottomSheetStatus.cancel();
        } else if (view.getId() == R.id.llDiscarded) {
            binding.tvStatusChooser.setText(STATUS_DISCARDED);
            setStatusBackground(STATUS_DISCARDED);
            if (!isCreateNewTask)
                taskDetails.setTaskStatus(STATUS_DISCARDED);
            updateStatusInDB();
            mBottomSheetStatus.cancel();
        } else if (view.getId() == R.id.llCompleted) {
            binding.tvStatusChooser.setText(STATUS_COMPLETED);
            setStatusBackground(STATUS_COMPLETED);
            if (!isCreateNewTask)
                taskDetails.setTaskStatus(STATUS_COMPLETED);
            updateStatusInDB();
            mBottomSheetStatus.cancel();
        } else if (view.getId() == R.id.llCancel) {
            mBottomSheetStatus.cancel();
        } else if (view.getId() == R.id.tvStatusChooser) {
            showStatusBottomSheetDialog();
        } else if (view.getId() == binding.tvAddNewComment.getId()) {
            Intent intent = new Intent(this, TaskCommentsActivity.class);
            intent.putExtra(GlobalStrings.TASK_ID, taskID);
            startActivity(intent);
        } else if (view.getId() == R.id.tvDueDate) {
            showDateTimePicker(binding.tvDueDate);
        } else if (view.getId() == R.id.tvLatLngs) {
            Intent intent = new Intent(this, MapDragActivity.class);
            startActivityForResult(intent, CAPTURE_GPS_LOCATION_REQUEST_CODE);
        }
    }

    public void showDateTimePicker(final TextView tvDate) {
        final Calendar currentDate = Calendar.getInstance();

        if (dueDateMillis != 0)
            currentDate.setTimeInMillis(dueDateMillis);
        else {
            if (taskDetails != null)
                currentDate.setTimeInMillis(taskDetails.getDueDate());
        }

        Calendar date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(EditTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                        dueDateMillis = date.getTimeInMillis();

                        SimpleDateFormat sdf
                                = new SimpleDateFormat(GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN_12HR,
                                Locale.US);
                        tvDate.setText(sdf.format(date.getTime()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE),
                        false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DATE));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateStatusInDB() {
        TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        if (!isCreateNewTask)
            taskDetailsDataSource.updateStatus(binding.tvStatusChooser.getText().toString(),
                    taskID + "");
        else if (!binding.edtTaskTitle.getText().toString().isEmpty()
            /* && !tvTaskDate.getText().toString().isEmpty()*/) {
            saveTaskInDb();
        }
    }

    private void showStatusBottomSheetDialog() {
        try {
            View sheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_status, null);
            mBottomSheetStatus = new BottomSheetDialog(this);
            mBottomSheetStatus.setContentView(sheetView);
            mBottomSheetStatus.show();

            // Remove default white color background
            FrameLayout bottomSheet = mBottomSheetStatus
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);

            LinearLayout llOngoing = sheetView.findViewById(R.id.llOngoing);
            LinearLayout llOpen = sheetView.findViewById(R.id.llOpen);
            LinearLayout llAssigned = sheetView.findViewById(R.id.llAssigned);
            LinearLayout llCompleted = sheetView.findViewById(R.id.llCompleted);
            LinearLayout llDiscarded = sheetView.findViewById(R.id.llDiscarded);
            LinearLayout llCancel = sheetView.findViewById(R.id.llCancel);

            llOngoing.setOnClickListener(this);
            llOpen.setOnClickListener(this);
            llAssigned.setOnClickListener(this);
            llCompleted.setOnClickListener(this);
            llDiscarded.setOnClickListener(this);
            llCancel.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        if ((requestCode == REQUEST_CODE_PIX_IMAGE_PICKER)
                && resultCode == RESULT_OK && data != null) {
            ArrayList<String> pathList = data.getStringArrayListExtra((Pix.IMAGE_RESULTS));
            if (pathList != null) {
                if (pathList.size() > 0) {
                    for (String path : pathList) {
                        Bitmap bitmap;
                        bitmap = Util.correctBitmapRotation(path);
                        Bitmap cropIMg = Util.cropToSquare(bitmap);
                        String fileName = "p_" + System.currentTimeMillis() + ".jpg";
                        File toSave = new File(getMediaStorageDirectory(MEDIA_TYPE_IMAGE).getAbsolutePath(), fileName);
                        Util.saveBitmapToSDCard(cropIMg, toSave, COMPRESSION_RATE_100);

                        saveAttachmentToDb(fileName);
                        setViewAttachmentText();
                    }
                }
            }
        } else if ((requestCode == REQUEST_CODE_MEDIA_PICKER)
                && resultCode == RESULT_OK && data != null) {
            if (isCreateNewTask)
                saveTaskInDb();
            taskDetailsDataSource.updateSyncFlag(taskID + "", 0);

            String path = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH);

            //04/11/22 try creating respected cols in the task attachment table for below files and
            //update db for data sync when api changes for task is done from web
            String path1000 = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH);
            String pathThumb = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH);

            if (path != null) {
                saveAttachmentToDb(path);
                setViewAttachmentText();
            }
        } else if (requestCode == CAPTURE_GPS_LOCATION_REQUEST_CODE && resultCode == RESULT_OK
                && data != null) {
            latLongsFetched = (LatLng) data.getParcelableExtra(GlobalStrings.FETCHED_LOCATION);

            latLongsFetched = new LatLng(Util.round(latLongsFetched.latitude, 5),
                    Util.round(latLongsFetched.longitude, 5));

            //ignore the below warning.. it is null sometimes
            if (latLongsFetched != null) {
                String location = latLongsFetched.latitude + "/" + latLongsFetched.longitude;
                binding.tvLatLngs.setText(location);
            }
        } else if (requestCode == REQUEST_CODE_SITE && resultCode == RESULT_OK
                && data != null) {
            Site site = (Site) data.getSerializableExtra(GlobalStrings.SITE_DETAILS);
            if (site != null) {
//                binding.tvSelectProject.setText(site.getSiteName());
//                projectId = site.getSiteID() + "";
            }
        }
    }

    private void setViewAttachmentText() {
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllAttachments(taskID);

        if (attachmentList.size() > 0)
            binding.tvAddNewAttachment.setText(getString(R.string.view) + " " + attachmentList.size());
        else
            binding.tvAddNewAttachment.setText(getString(R.string.add_new_lower_case));
    }

    private void saveAttachmentToDb(String fileName) {

        ArrayList<TaskDataResponse.AttachmentList> list = new ArrayList<>();
        TaskDataResponse.AttachmentList attachment = new TaskDataResponse.AttachmentList();
        attachment.setTaskId(taskID);
        attachment.setTaskAttachmentId(-Util.randInt(0, 9999));
        attachment.setCommentId(0);
        attachment.setFileName(fileName);
        attachment.setDisplayFlag(1);
        attachment.setCreatedBy(userID);
        attachment.setCreationDate(System.currentTimeMillis());
        list.add(attachment);

        TaskAttachmentsDataSource dataSource = new TaskAttachmentsDataSource(this);
        dataSource.insertAttachmentData(list, 0);
    }

    private void saveTask() {

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            return;
        }

        final TaskDataResponse.Data taskDataResponse = new TaskDataResponse.Data();

        final TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        final TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(this);
        TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments(taskID + "");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks(taskID + "");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments(taskID + "");

        if (commentList.size() == 0 && dataList.size() == 0 && attachmentList.size() == 0) {
            Toast.makeText(this, getString(R.string.no_data_to_save), Toast.LENGTH_SHORT).show();
            return;
        }

        taskDataResponse.setTaskDataList(dataList);
        taskDataResponse.setCommentList(commentList);

        String baseUrl = getString(R.string.prod_base_uri)
                + getString(R.string.prod_user_task_sync_data);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(taskDataResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startProgressBar();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, baseUrl,
                jsonObject, response -> {
            TaskDataResponse syncRes = new Gson().fromJson(response.toString(), TaskDataResponse.class);

            if (syncRes.getData().getCommentList().size() > 0) {
                for (TaskDataResponse.CommentList comment : syncRes.getData().getCommentList()) {
                    commentsDataSource.updateIdAndSyncFlag(comment.getTaskCommentId() + "",
                            comment.getTaskId() + "", comment.getClientCommentId() + "");
                }
            }

            if (syncRes.getData().getTaskDataList().size() > 0) {
                for (TaskDataResponse.TaskDataList details : syncRes.getData().getTaskDataList()) {
                    taskDetailsDataSource.updateSyncFlagAndId(details.getTaskId() + "",
                            details.getClientTaskId() + "");

                    attachmentsDataSource.updateTaskId(details.getTaskId() + "",
                            details.getClientTaskId() + "");
                    taskID = details.getTaskId();//for attachment upload reference
                    taskDetails.setTaskId(taskID);
                }
            }

            ArrayList<TaskDataResponse.AttachmentList> attachmentList1
                    = attachmentsDataSource.getAllUnSyncAttachments(taskID + "");

            if (attachmentList1.size() > 0)
                syncAttachments(attachmentList1);
            else {
                Toast.makeText(this, getString(R.string.task_synced_successfully), Toast.LENGTH_SHORT).show();
                cancelDialog();
                setResultData();
            }
        }, error -> Log.e("error", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(EditTaskActivity.this);
                String deviceToken = Util.getSharedPreferencesProperty(EditTaskActivity.this,
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(EditTaskActivity.this,
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

        RequestQueue mRequestQueue = Volley.newRequestQueue(EditTaskActivity.this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            new SyncMedia(this, jsonObject, imagePath.getAbsolutePath(),
                    list.size()).execute();
        }
    }

    int countMedia = 0;

    private class SyncMedia extends AsyncTask<MediaType, Void, String> {

        //        File mFile;
        MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
        AttachmentTaskResponseModel attachmentResponse = null;
        Context mContext;
        JSONObject mJsonObjectMediaData;
        String mPath;
        int mediaCount;

        SyncMedia(Context context, JSONObject jsonObjectMediaData, String path, int size) {
            mContext = context;
            mJsonObjectMediaData = jsonObjectMediaData;
            mPath = path;
            mediaCount = size;
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

            attachmentResponse = mAquaBlueService.TaskMediaUpload(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.prod_user_task_attachment_sync),
                    files);
            if (attachmentResponse != null) {
                if (attachmentResponse.isSuccess()) {
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
                countMedia++;
                if (s.equals("FALSE")) {
                    Log.e("imageUpload", "onPostExecute: fails to upload image attachment");
                } else if (s.equals("SUCCESS")) {
                    Log.e("imageUpload", "onPostExecute: image attachment upload success"
                            + attachmentResponse.getData().getTaskId());

                    TaskAttachmentsDataSource attachmentsDataSource
                            = new TaskAttachmentsDataSource(mContext);
                    attachmentsDataSource.updateDataSyncFlag(attachmentResponse.getData().getTaskId(),
                            attachmentResponse.getData().getFileName(),
                            attachmentResponse.getData().getClientTaskAttachmentId() == null
                                    ? attachmentResponse.getData().getTaskAttachmentId()
                                    : attachmentResponse.getData().getClientTaskAttachmentId(),
                            attachmentResponse.getData().getTaskAttachmentId());
                }
            }

            if (mediaCount == countMedia) {
                cancelDialog();
                Toast.makeText(mContext, getString(R.string.task_synced_successfully), Toast.LENGTH_SHORT).show();
                setResultData();
            }
        }
    }

    public void startProgressBar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.syncing_please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void cancelDialog() {
        if (progressDialog.isShowing())
            progressDialog.cancel();
    }
}
