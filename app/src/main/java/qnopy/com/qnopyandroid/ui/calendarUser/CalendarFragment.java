package qnopy.com.qnopyandroid.ui.calendarUser;

import static android.app.Activity.RESULT_OK;
import static qnopy.com.qnopyandroid.ui.events.SubmittalsFragment.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;
import com.ycuwq.calendarview.CalendarLayout;
import com.ycuwq.calendarview.CalendarView;
import com.ycuwq.calendarview.Date;
import com.ycuwq.calendarview.PagerInfo;

import org.joda.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.CompletionPercentageDataSource;
import qnopy.com.qnopyandroid.db.DataSyncDateSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SyncStatusDataSource;
import qnopy.com.qnopyandroid.db.TempLogsDataSource;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.signature.CaptureSignature;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity;
import qnopy.com.qnopyandroid.ui.activity.MainDrawerActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportActivity;
import qnopy.com.qnopyandroid.ui.activity.RequiredFieldsListActivity;
import qnopy.com.qnopyandroid.ui.activity.SiteActivity;
import qnopy.com.qnopyandroid.ui.events.SubmittalsFragment;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.DownloadEventData;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.MyRecyclerScroll;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

public class CalendarFragment extends Fragment implements AllEventsAdapter.OnCalendarEventListener,
        DownloadEventListTask.OnEventDownloadListener, OnTaskCompleted,
        DownloadEventData.DownloadEventDataListener {

    public static final int LOCATION_ACT_REQUEST = 1278;
    private TextView tvMonthYr;
    private HashMap<String, List<Date>> mScheme;
    private String dateSelected = "";
    private RecyclerView rvAllEvents;
    private AlertDialog progressBar;
    private HashMap<String, ArrayList<EventData>> mapEvents = new HashMap<>();
    private HashMap<String, Integer> mapEventsDates = new HashMap<>();
    private AquaBlueServiceImpl mAquaBlueService;
    private String username;
    private String userGuid;
    private String password;
    private String userID;
    private String deviceID;
    private SyncStatusDataSource syncStatusob;
    private String lastSyncDate;
    private CalendarLayout calendarLayout;
    private CalendarView calendarView;
    public SwitchCompat switchFilterForms;
    private Date selectedDate;
    private AllEventsAdapter adapter;
    private RecyclerViewReadyCallback recyclerViewReadyCallback;
    private BottomSheetDialog mBottomSheetEmailLogs;
    private StickyHeaderItemDecorator decorator;
    private LinearLayout layoutStartEvent;
    private boolean closeEvent;
    private EventData eventToClose;
    private Integer serverGenEventID;
    private MenuItem menuItemSync;
    private AlertDialog progressBarDownloadData;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userID = Util.getSharedPreferencesProperty(requireActivity(), GlobalStrings.USERID);
        username = Util.getSharedPreferencesProperty(requireActivity(), GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(requireActivity(), username);
        password = Util.getSharedPreferencesProperty(requireActivity(), GlobalStrings.PASSWORD);
        setUpCalendar(view);
    }

    private void setUpCalendar(View view) {
        layoutStartEvent = view.findViewById(R.id.layoutStartEventButton);
        CustomTextView fabStartEvent = view.findViewById(R.id.fabStartEvent);
        fabStartEvent.setOnClickListener(v -> {
            onStartBtnEventClicked();
        });

        if (ScreenReso.isLimitedUser)
            layoutStartEvent.setVisibility(View.GONE);

        calendarLayout = view.findViewById(R.id.calendarLayout);
        tvMonthYr = view.findViewById(R.id.tvMonthYr);
        switchFilterForms = view.findViewById(R.id.switchFilterForms);

        setHasOptionsMenu(true);
        calendarView = view.findViewById(R.id.calendarView);
        rvAllEvents = view.findViewById(R.id.rvAllEvents);

        calendarLayout.shrink();
        calendarView.setBottomTextColor(Color.BLACK);
        calendarView.setBottomTextSize(40);

        calendarView.setSelectedItemColor(ContextCompat.getColor(requireActivity(),
                R.color.red));

        calendarView.setOnDateSelectedListener(date -> {
//                String currentDate = date.getYear() + "/" + date.getMonth() + "/" + date.getDay();
            String currentDate = date.toString();

            Log.e("Calendar fra", "on date called");

            int year = date.getYear(), month = date.getMonth();
            tvMonthYr.setText(getConvertedDate(year + "-" + month));

            if (!dateSelected.isEmpty()) {
                if (layoutStartEvent.getTranslationY() > 0)
                    showAnim();
            }

            if (!dateSelected.equals(currentDate)) {
                //commented on 30 Dec, 21 to avoid calling event api when screen opens first time
/*                if (CheckNetwork.isInternetAvailable(requireActivity()) && dateSelected.isEmpty()) {
                    new DownloadEventListTask((AppCompatActivity) requireActivity(),
                            CalendarFragment.this, "").execute();
                } else {
                    if (new EventDataSource(requireActivity()).isSubmittalsDownloadedAlready()) {
                        getAllEvents();
                    } else if (!CheckNetwork.isInternetAvailable(requireActivity()))
                        Toast.makeText(requireActivity(), getString(R.string.bad_internet_connectivity),
                                Toast.LENGTH_LONG).show();
                }*/

                dateSelected = date.toString();
                selectedDate = date;

                if (!new EventDataSource(requireActivity()).isEventsDownloadedAlready()) {
                    refreshEvents();
                } else {
                    if (mapEvents.size() > 0) {
                        if (mapEvents.containsKey(dateSelected))
                            showData(mapEvents.get(dateSelected), dateSelected);
                        else
                            showData(new ArrayList<>(), dateSelected);
                    } else
                        getAllEvents();
                }
            }
        });

        calendarView.setOnPageSelectedListener(new CalendarView.OnPageSelectedListener() {
            @Override
            public List<Date> onPageSelected(@NonNull PagerInfo pagerInfo) {
                if (mScheme == null) {
                    return null;
                }
                int year = pagerInfo.getYear(), month = pagerInfo.getMonth(),
                        mondayDay = pagerInfo.getMondayDay();
                tvMonthYr.setText(getConvertedDate(year + "-" + month));

                if (pagerInfo.getType() == PagerInfo.TYPE_MONTH) {
                    return mScheme.get(year + "-" + month);
                } else {
                    List<Date> schemes = new ArrayList<>();
                    LocalDate monday = new LocalDate(year, month, mondayDay);
                    Date tempDate = new Date(year, month, mondayDay);
                    for (int i = 1; i <= 7; i++) {
                        LocalDate localDate = monday.withDayOfWeek(i);
                        tempDate.setYear(localDate.getYear());
                        tempDate.setMonth(localDate.getMonthOfYear());
                        tempDate.setDay(localDate.getDayOfMonth());
                        List<Date> monthScheme = mScheme.get(localDate.getYear() + "-" + localDate.getMonthOfYear());
                        if (monthScheme == null) {
                            continue;
                        }
                        int index = monthScheme.indexOf(tempDate);
                        if (index >= 0) {
                            schemes.add(monthScheme.get(index));
                        }
                    }
                    return schemes;
                }
            }
        });

        generateSchemes();

        switchFilterForms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adapter.getFilter().filter(userID);
            } else {
                adapter.getFilter().filter("");
            }
        });

        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.simple_grow);
        layoutStartEvent.startAnimation(animation);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (selectedDate != null) {
            if (ScreenReso.isDownloadData) {
                ScreenReso.isDownloadData = false;
                if (CheckNetwork.isInternetAvailable(requireActivity())) {
                    new DownloadEventListTask((AppCompatActivity) requireActivity(),
                            CalendarFragment.this).execute();
                } else {
                    if (new EventDataSource(requireActivity()).isEventsDownloadedAlready()) {
                        getAllEvents();
                    } else
                        Toast.makeText(requireActivity(), getString(R.string.bad_internet_connectivity),
                                Toast.LENGTH_LONG).show();
                }
            } else
                getAllEvents();

            calendarView.scrollToDate(selectedDate.getYear(), selectedDate.getMonth(),
                    selectedDate.getDay(), true);
        }

        setSyncBadge();
    }

    private void setSyncBadge() {
        if (menuItemSync != null) {
            Util.setBadgeCount(requireActivity(), menuItemSync, "",
                    Util.isThereAnyDataToSync(requireActivity()));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_calendar, menu);
        menuItemSync = menu.findItem(R.id.action_sync);
        setSyncBadge();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            ScreenReso.isDownloadData = true;
            if (CheckNetwork.isInternetAvailable(requireActivity())) {
                try {
                    Util.setSharedPreferencesProperty(requireActivity(), GlobalStrings.IS_CALENDAR_REFRESH,
                            true);
                    ((MainDrawerActivity) requireActivity()).downloadForms();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(requireActivity(), getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    private void refreshEvents() {
        if (CheckNetwork.isInternetAvailable(requireActivity())) {
            new DownloadEventListTask((AppCompatActivity) requireActivity(),
                    CalendarFragment.this).execute();
        } else {
            Toast.makeText(requireActivity(), getString(R.string.bad_internet_connectivity),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getAllEvents() {
        if (getActivity() != null) {
            progressBar = AlertManager.showQnopyProgressBar((AppCompatActivity) requireActivity(),
                    requireActivity().getString(R.string.loading));
            new FetchAllEventsDbTask().execute();
        }
    }

    public void showAlertProgress() {
        if (progressBar != null) progressBar.show();
    }

    public void cancelAlertProgress() {
        if (progressBar != null && progressBar.isShowing())
            progressBar.cancel();
    }

    private void generateSchemes() {
        mScheme = new HashMap<>();
        for (int i = 1; i < 12; i++) {
            List<Date> list = new ArrayList<>();
            for (int j = 1; j < 28; j++) {
                list.add(new Date(2018, i, j));
            }
            mScheme.put(2018 + "-" + i, list);
        }
    }

    public static String getConvertedDate(String time) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        java.util.Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return displayFormat.format(date);
        }
        return "";
    }

    @Override
    public void onCalendarEventClicked(EventData event) {

        FieldDataSource fieldDataSource = new FieldDataSource(requireActivity());

        if (Integer.parseInt(userID) != event.getUserId() &&
                !fieldDataSource.hasFieldDataForEvent(event.getSiteID() + "",
                        event.getEventID() + "")) {
            DownloadEventData downloadEventData = new DownloadEventData(requireActivity(),
                    this, event);
            downloadEventData.showDownloadDataAlert();
        } else {
            startLocationIntent(event);
        }
    }

    private void startLocationIntent(EventData event) {

        Util.setSharedPreferencesProperty(requireActivity(),
                GlobalStrings.CURRENT_SITEID, event.getSiteID() + "");
        Util.setSharedPreferencesProperty(requireActivity(),
                GlobalStrings.CURRENT_SITENAME, event.getSiteName());
        Util.setSharedPreferencesProperty(requireActivity(), GlobalStrings.CURRENT_APPID,
                event.getMobAppID() + "");

        Intent locationIntent = new Intent(requireActivity(), LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(requireActivity(),
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(requireActivity()) && isSplitScreenEnabled)
            locationIntent = new Intent(requireActivity(), SplitLocationAndMapActivity.class);

        locationIntent.putExtra("APP_ID", event.getMobAppID());
        locationIntent.putExtra("SITE_ID", event.getSiteID() + "");
        locationIntent.putExtra("SITE_NAME", event.getSiteName());
        locationIntent.putExtra("EVENT_ID", event.getEventID());
        locationIntent.putExtra("fromaddsite", false);
        startActivityForResult(locationIntent, LOCATION_ACT_REQUEST);
    }

    @Override
    public void onEmailLogsClicked(EventData event) {
//        showSendReportPopupWindow(calendarLayout, event);
        showEmailLogsBottomSheet(event);
    }

    @Override
    public void onDownloadDataClicked(EventData event) {

        DataSyncDateSource syncDateSource = new DataSyncDateSource(requireActivity());
        long timeMillis =
                syncDateSource.getDataSyncTime(event.getEventID() + "",
                        event.getSiteID() + "");

        if (timeMillis != 0) {
            AlertManager.showDownloadDataWaitAlert((AppCompatActivity) requireActivity(),
                    requireActivity().getString(R.string.download_data),
                    "Please wait for 00m00s to download data.", timeMillis);
        } else if (CheckNetwork.isInternetAvailable(requireActivity())) {
            Intent i = new Intent(requireActivity(), DownloadYourOwnDataActivity.class);
            i.putExtra("SITE_NAME", event.getSiteName());
            i.putExtra("SITEID", event.getSiteID());
            i.putExtra("EVENTID", event.getEventID());
            i.putExtra("APP_NAME", event.getMobAppName());
            i.putExtra("PARENTAPPID", event.getMobAppID());
            i.putExtra("REPORT", "TRUE");
            startActivity(i);
        } else {
            Toast.makeText(requireActivity(), getString(R.string.please_check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStartBtnEventClicked() {
        String siteId = ((MainDrawerActivity) getActivity()).getSiteIdForProjectUser();

        if (ScreenReso.isProjectUser && siteId != null && !siteId.isEmpty() && !siteId.equals("-1")) {
            ((MainDrawerActivity) getActivity()).startApplicationActivity(dateSelected);
        } else {
            long milliSec = Util.getTimeInMillisAddingCurrentTime(dateSelected);
            Intent intent = new Intent(requireActivity(), SiteActivity.class);
            intent.putExtra(GlobalStrings.EVENT_STAR_DATE, milliSec);
            startActivity(intent);
        }
    }

    @Override
    public void onCloseButtonClicked(EventData event) {
        eventToClose = event;
        FieldDataSource fieldDataSource = new FieldDataSource(requireActivity());

        ArrayList<RequiredFieldRowItem> reqDataList
                = fieldDataSource.getMandatoryFieldList(event.getMobAppID() + "",
                event.getEventID() + "", event.getSiteID() + "");

        if (CalendarFragment.hasRequiredLocationsFields(event.getSiteID(), event.getEventID(),
                event.getMobAppID(), requireContext())) {
            //all operations are done in the condition method
        } else if (reqDataList != null && reqDataList.size() > 0 && reqDataList.get(0).getCount() > 0) {
            requiredDataInFormAlert(event, getActivity());
        } else {
            closeEventAlert(event);
        }
    }

    public static boolean hasRequiredLocationsFields(int siteId, int eventId, int mobAppId, Context context) {
        LocationDataSource locationDataSource = new LocationDataSource(context);
        ArrayList<Location> requiredLocationList
                = locationDataSource.getAllRequiredLocFormDefaultOrNon(siteId,
                mobAppId);

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        boolean hasRequiredLocationsField = false;
        if (requiredLocationList != null && !requiredLocationList.isEmpty()) {
            for (Location location : requiredLocationList) {
                ArrayList<RequiredFieldRowItem> mandatoryFieldList =
                        fieldDataSource.getMandatoryFieldListByLocation(mobAppId + "",
                                eventId + "", siteId + "",
                                location.getLocationID());
                if (!mandatoryFieldList.isEmpty()) {
                    hasRequiredLocationsField = mandatoryFieldList.get(0).getCount() > 0;
                    break;
                }
            }
        }

        if (hasRequiredLocationsField)
            LocationActivity.MandatoryFieldForRequiredLocAlert(requiredLocationList, context,
                    eventId, mobAppId);
        return hasRequiredLocationsField;
    }

    public static void requiredDataInFormAlert(EventData event, Context context) {
        android.app.AlertDialog.Builder alertDialogBuilder
                = new android.app.AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(R.string.attention);
        alertDialogBuilder.setTitle(R.string.attention);
        alertDialogBuilder
                .setMessage(context.getString(R.string.some_forms_have_mandatory_field_need_to_be_filled));
        alertDialogBuilder.setPositiveButton(R.string.show,
                (dialog, id) -> {
                    Intent req_intent = new Intent(context, RequiredFieldsListActivity.class);

                    req_intent.putExtra("APP_ID", event.getMobAppID());
                    req_intent.putExtra("EVENT_ID", event.getEventID());
                    req_intent.putExtra("SITE_ID", event.getSiteID());
                    req_intent.putExtra("SITENAME", event.getSiteName());
                    context.startActivity(req_intent);

                    dialog.dismiss();
                });
        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel_upper_case),
                (dialog, id) -> {
                    dialog.cancel();
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void closeEventAlert(EventData event) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(requireActivity());

        alertDialogBuilder.setTitle(R.string.close_end_field_event);
        alertDialogBuilder
                .setMessage(R.string.sure_submit_data_and_close_event);

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String capture = Util.getSharedPreferencesProperty(requireActivity(), GlobalStrings.CAPTURE_SIGNATURE);
                        boolean CAPTURE_SIGNATURE = false;

                        if (capture == null) {
                            CAPTURE_SIGNATURE = false;
                        } else {
                            CAPTURE_SIGNATURE = Boolean.parseBoolean(capture);
                        }

                        if (CAPTURE_SIGNATURE) {
                            Intent intent = new Intent(requireActivity(), CaptureSignature.class);
                            intent.putExtra("EVENT_ID", event.getEventID());
                            intent.putExtra("APP_ID", event.getMobAppID());
                            intent.putExtra("SITE_ID", event.getSiteID());
                            intent.putExtra("CLOSE", "true");
                            intent.putExtra("UserID", userID);
                            startActivityForResult(intent, CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE);
                        } else {
                            closingEvents(event);
                        }
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void closingEvents(EventData event) {

        EventDataSource eventData = new EventDataSource(requireActivity());
        closeEvent = true;

        boolean serverGenerated = eventData.isEventIDServerGenerated(event.getEventID());

        DEvent dEvent = eventData.getEventById(event.getMobAppID(), event.getSiteID(), event.getEventID() + "");

        if (!serverGenerated) {

            EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(this, dEvent,
                    this.username, this.password, false, getContext());
            eventHandler.execute();
        } else {
            if (CheckNetwork.isInternetAvailable(requireActivity())) {
                uploadFieldDataBeforeEndEvent(event.getEventID());
            } else {
                CustomToast.showToast(requireActivity(), getString(R.string.bad_internet_connectivity), 5);
            }
        }
    }

    private void uploadFieldDataBeforeEndEvent(int eventId) {
        Intent dataUpload = new Intent(requireActivity(), DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", eventId);
        dataUpload.putExtra("CLOSE_EVENT", true);
        startActivityForResult(dataUpload, SubmittalsFragment.SYNC_ACTIVITY_REQUEST_CODE);
    }

    private void getReport(boolean isForPM, boolean isPdf, EventData event, boolean isForSelf) {
        //even if isPdf has value, use of it depends on the isForPM value in api call

        if (CheckNetwork.isInternetAvailable(requireActivity())) {
            new SendReportTask(isForPM, isPdf, event, isForSelf).execute();
        } else {
            Toast.makeText(requireActivity(), getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
    }

    private void showEmailLogsBottomSheet(EventData event) {
        try {
            View sheetView = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.layout_bottom_sheet_email_logs, null);
            mBottomSheetEmailLogs = new BottomSheetDialog(requireActivity());
            mBottomSheetEmailLogs.setContentView(sheetView);
            mBottomSheetEmailLogs.show();

            // Remove default white color background
            FrameLayout bottomSheet = mBottomSheetEmailLogs
                    .findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackground(null);
            }

            LinearLayout llViewReport = sheetView.findViewById(R.id.llViewReport);
            LinearLayout llSendPdf = sheetView.findViewById(R.id.llSendPdf);
            LinearLayout llSendDoc = sheetView.findViewById(R.id.llSendDoc);
            LinearLayout llCancel = sheetView.findViewById(R.id.llCancel);

            llViewReport.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                Intent i = new Intent(requireActivity(), MobileReportActivity.class);
                i.putExtra("SITE_NAME", event.getSiteName());
                i.putExtra("SITE_ID", event.getSiteID() + "");
                i.putExtra("EVENT_ID", event.getEventID() + "");
                i.putExtra("APP_NAME", event.getMobAppName());
                startActivity(i);
            });

            llSendPdf.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                getReport(true, true, event, true);
            });

            llSendDoc.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                getReport(true, false, event, false);
            });

            llCancel.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEventDownloadSuccess() {
        getAllEvents();
    }

    @Override
    public void onEventDownloadFailed() {
        CustomToast.showToast(requireActivity(), requireActivity().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
    }

    @Override
    public void onTaskCompleted(Object obj) {
        FieldDataSource fieldData = new FieldDataSource(requireActivity());
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(requireActivity());
        EventDataSource eventData = new EventDataSource(requireActivity());

        if (obj != null) {

            if (obj instanceof String) {

                String result = (String) obj;

                if (result.equals("SUCCESS")) {

                    Event event = eventData.getEvent(eventToClose.getMobAppID(),
                            eventToClose.getSiteID(), Integer.parseInt(userID), deviceID);
                    int currentEventID = event.getEventId();
                    //setEventID(currentEventID);
                    uploadFieldData();
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
                }

            } else if (obj instanceof EventResponseModel) {

                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {

                    serverGenEventID = result.getData().getEventId();
                    setGeneratedEventID(result);

                    fieldData.updateEventID(eventToClose.getEventID(), serverGenEventID);
                    attachDataSrc.updateEventID(eventToClose.getEventID(), serverGenEventID);
                    eventData.updateEventID(eventToClose.getEventID(), result);
                    new SampleMapTagDataSource(requireActivity())
                            .updateEventID_SampleMapTag(eventToClose.getEventID() + "",
                                    serverGenEventID + "");

                    //setEventID(serverGenEventID);

                    //changing client negative eventId to server gen Id so that the event can be closed from server
                    if (eventToClose.getEventID() < 0)
                        eventToClose.setEventID(serverGenEventID);

                    if (CheckNetwork.isInternetAvailable(requireActivity())) {
                        if (closeEvent) {
                            uploadFieldDataBeforeEndEvent(eventToClose.getEventID());
                        } else {
                            uploadFieldData();
                        }
                    } else {
                        CustomToast.showToast((Activity) requireActivity(), getString(R.string.bad_internet_connectivity), 5);
                    }
                } else {
                    if (result.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                        //04-Mar-16
                        Toast.makeText(requireActivity(), GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                    if ((result.getResponseCode() == HttpStatus.NOT_FOUND) || (result.getResponseCode() == HttpStatus.LOCKED)) {
                        Util.setDeviceNOT_ACTIVATED((Activity) requireActivity(), username, password);
//                    Toast.makeText(requireActivity(),GlobalStrings.responseMessage,Toast.LENGTH_LONG).show();
                    }
                    if (result.getResponseCode() == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(requireActivity(), GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Toast.makeText(requireActivity(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void setGeneratedEventID(int id) {

    }

    @Override
    public void setGeneratedEventID(Object obj) {

    }

    private void uploadFieldData() {

        LocationDataSource locationSource = new LocationDataSource(requireActivity());
        FieldDataSource fieldSource = new FieldDataSource(requireActivity());
        AttachmentDataSource attachDataSource = new AttachmentDataSource(requireActivity());

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(requireActivity());

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
        logDetails.setScreenName("Calendar Screen - sync while closing event");
        logDetails.setDetails("Has field data before checking old strings? Rows: "
                + fieldSource.collectDataForSyncUpload().size());

        tempLogsDataSource.insertTempLogs(logDetails);

        boolean isLocationsAvailableToSync = locationSource.isOfflineLocationsAvailable();//24-Mar-17
        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(requireActivity());

        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        logDetails.setDetails("Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());
        tempLogsDataSource.insertTempLogs(logDetails);

        logDetails.setDetails("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync);
        tempLogsDataSource.insertTempLogs(logDetails);


        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            Toast.makeText(requireActivity(), getString(R.string.no_data_to_sync), Toast.LENGTH_LONG).show();
        } else {
            Intent dataUpload = new Intent(requireActivity(), DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", eventToClose.getEventID());
            startActivity(dataUpload);
        }
    }

    @Override
    public void showDownloadEventProgress() {
        progressBarDownloadData = AlertManager.showQnopyProgressBar((AppCompatActivity) requireActivity(),
                "Checking to see if there is data for this event..");
        progressBarDownloadData.show();
    }

    @Override
    public void cancelDownloadEventProgress() {
        if (progressBarDownloadData != null && progressBarDownloadData.isShowing()) {
            progressBarDownloadData.cancel();
        }
    }

    @Override
    public void showLocationScreen(EventData event) {
        startLocationIntent(event);
    }

    private class SendReportTask extends AsyncTask<MediaType, Void, String> {

        private final boolean isForSelf;
        private boolean isForPM;
        private boolean isPdf;
        private ProgressDialog progressDialog;
        private String isDownloaded;
        private String mErrorString;
        private String mResponseString;
        private EventData mEvent;

        public SendReportTask(boolean isForPm, boolean isPdf, EventData event, boolean isForSelf) {
            this.isForPM = isForPm;
            this.isPdf = isPdf;
            this.mEvent = event;
            this.isForSelf = isForSelf;
            progressDialog = new ProgressDialog(requireActivity());
            progressDialog.setMessage("Emailing logs please wait..");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isForPM)
                progressDialog.show();
        }

        @Override
        protected String doInBackground(MediaType... params) {
            try {
                AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(requireActivity());

                isDownloaded = mAquaBlueService.generateReport(getResources().getString(R.string.prod_base_uri),
                        getResources().getString(R.string.mobile_report_required),
                        mEvent.getSiteID() + "", mEvent.getEventID() + "", mEvent.getMobAppID() + "",
                        mEvent.getUserId() + "", isForPM, isPdf, isForSelf);
                if (isDownloaded.equals("false")) {
                    Log.i("Report to PM", "FDownloadData response :" + isDownloaded);
                    mResponseString = "false";
                } else {
                    mResponseString = isDownloaded;
                }
            } catch (Exception n) {
                n.printStackTrace();
            }
            return mResponseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isForPM) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                String msg;
                if (s.equals("false")) {
                    msg = getString(R.string.unable_to_gen_report);
                } else {
                    msg = s;
                }
                showReportAlert(msg);
            } else {
                showResult(s);
            }
        }
    }

    private void showReportAlert(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireActivity());
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showResult(String s) {
        try {
            if (s.equals("false")) {
                Toast.makeText(requireActivity(), getString(R.string.unable_to_send_email_logs), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "" + s, Toast.LENGTH_LONG).show();
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FetchAllEventsDbTask extends AsyncTask<Integer, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress();
        }

        @Override
        protected Object doInBackground(Integer... args) {
            EventDataSource eds = new EventDataSource(requireActivity());

            String siteId = "";
            if (getActivity() instanceof MainDrawerActivity) {
                siteId = ((MainDrawerActivity) getActivity()).getSiteIdForProjectUser();
            }

            return eds.getAllEvents(siteId);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            cancelAlertProgress();
            mapEvents = (HashMap<String, ArrayList<EventData>>) result;

            if (mapEvents.size() > 1) {

                for (Entry<String, ArrayList<EventData>> stringArrayListEntry
                        : mapEvents.entrySet()) {
                    Entry pair = stringArrayListEntry;

                    ArrayList<EventData> value = mapEvents.get(pair.getKey());
                    mapEventsDates.put(pair.getKey().toString(), value.size());
                }

                calendarView.setMapEventsDates(mapEventsDates);
            }

            if (mapEvents.containsKey(dateSelected))
                showData(mapEvents.get(dateSelected), dateSelected);
            else
                showData(new ArrayList<>(), dateSelected);
        }
    }

    private void showData(ArrayList<EventData> lisEvents, String key) {

        EventData event = new EventData();
        event.setViewType(AllEventsAdapter.HEADER_VIEW);
        event.setUserId(-1);//setting -1 to avoid error as filter will match forms by userId
        event.setEventDateFormatted(dateSelected);//Note: i have used this date field to
        //keep track for filter state by date

        recyclerViewReadyCallback = () -> {

            if (switchFilterForms.isChecked()) {
                //added this check as if switch is already on then even after
                //setting setChecked as true checkChangedListener won't be called
                if (switchFilterForms.isChecked()) {
                    if (adapter != null) adapter.getFilter().filter(userID);
                }
            }
        };

        rvAllEvents.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (recyclerViewReadyCallback != null) {
                    recyclerViewReadyCallback.onLayoutReady();
                }
                rvAllEvents.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Collections.sort(lisEvents, new Comparator<EventData>() {
            @Override
            public int compare(EventData lhs, EventData rhs) {
                //mSortSelection = "Ascending";
                return Long.compare(rhs.getModificationDate(), lhs.getModificationDate());
            }
        });

        ArrayList<EventData> eventsList = new ArrayList<>();
        eventsList.add(event);
        eventsList.addAll(lisEvents);

        adapter = new AllEventsAdapter(eventsList, requireActivity(),
                this, this);
        rvAllEvents.setAdapter(adapter);

        rvAllEvents.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                showAnim();
            }

            @Override
            public void hide() {
                layoutStartEvent.animate().translationY(layoutStartEvent.getHeight()
                        + Util.pxToDp(16)).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });

        decorator = new StickyHeaderItemDecorator(adapter);
        decorator.attachToRecyclerView(rvAllEvents);
    }

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    public void showAnim() {
        layoutStartEvent.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_ACT_REQUEST
                && resultCode == RESULT_OK) {
//            getAllEvents();
        }

        if (requestCode == CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            boolean isOk = true;
            closingEvents(eventToClose);
        } else if (requestCode == SubmittalsFragment.SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {

            if (data.hasExtra("SYNC_FLAG")) {
                long date = System.currentTimeMillis();

                boolean eventClosed = data.getBooleanExtra("SYNC_FLAG", false);
                long eventEndDate = Long.parseLong(data.getStringExtra("EVENT_END_DATE"));
                boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);

                if (eventEndDate < 1) {
                    eventEndDate = date;
                }
                if (dataSynced && eventClosed) {
                    EventDataSource eventData = new EventDataSource(requireActivity());
                    CompletionPercentageDataSource cp = new CompletionPercentageDataSource(requireActivity());

                    eventData.closeEventStatus(eventToClose.getMobAppID(), eventToClose.getSiteID(),
                            eventEndDate, eventToClose.getEventID() + "");
                    cp.truncatePercentageByRollAppID_And_SiteID(eventToClose.getSiteID() + "",
                            eventToClose.getMobAppID() + "");
                    SharedPref.resetCamOrMap();

                    Toast.makeText(requireActivity(), getString(R.string.event_has_been_closed), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(requireActivity(),
                            getString(R.string.event_cannot_be_closed),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
