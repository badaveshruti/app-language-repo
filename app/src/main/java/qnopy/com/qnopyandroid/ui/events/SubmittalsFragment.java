package qnopy.com.qnopyandroid.ui.events;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.adapter.MyDashboardRecyclerViewAdapter;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.clientmodel.EventData;
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
import qnopy.com.qnopyandroid.interfacemodel.OnEventCardClickListener;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalResponseCollector;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.signature.CaptureSignature;
import qnopy.com.qnopyandroid.ui.activity.AgreementActivity;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity;
import qnopy.com.qnopyandroid.ui.activity.MainDrawerActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportActivity;
import qnopy.com.qnopyandroid.ui.activity.RequiredFieldsListActivity;
import qnopy.com.qnopyandroid.ui.activity.SiteActivity;
import qnopy.com.qnopyandroid.ui.calendarUser.AllEventsAdapter;
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.DownloadEventData;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 11/18/2017.
 */

public class SubmittalsFragment extends Fragment implements OnTaskCompleted,
        OnEventCardClickListener, DownloadEventData.DownloadEventDataListener {

    Context context;
    private String locationName = "", username, password, userGuid, lastSyncDate;
    private int userID = 0;
    TextView emptyview;//, activetb, closedtb, conflicttb;
    int ACTIVE = 1, CLOSED = 0, CONFLICT = 2;
    int SELECTED_TAB = 0;

    /*
        List<dashboard_data_card> dashboardlist;
    */

    FloatingActionsMenu menuMultipleActions;
    FloatingActionButton showsite;
    private SubmittalsFragment.OnListFragmentInteractionListener mListener;
    private ProgressDialog progressDialog;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(context);
    private static final String TAG = "SubmittalsFragment";
    SyncStatusDataSource SyncStatusob;
    RecyclerView recyclerView;
    ImageButton goto_site_button;
    SearchView search_by, searchView;
    MyDashboardRecyclerViewAdapter adapter;
    //    ProgressBar loader;
    View contentview;
    LinearLayout progressll;
    FrameLayout content_container_fl;
    EventTask getDataTask;
    Handler mHandler;
//    MaterialSearchView searchView;

    Menu dmenu;

    private String savedFilePath;
    public static final int CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE = 104;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    String deviceID;
    boolean closeEvent = false;
    int mAppid, mEventId, mSiteId;
    String mSiteName;
    public static int serverGenEventID = 0;
    FieldDataSource fieldSource = null;
    LocationDataSource LDSource = null;
    AttachmentDataSource attachDataSource = null;
    boolean isLocationsAvailableToSync = false, isCoCAvailableToSync = false,
            isAttachmentsAvailableToSync = false, isFieldDataAvailableToSync = false;

    ArrayList<EventData> searchedEventList = new ArrayList<>();
    EventDataSource edsSearched;

    ImageView mImageViewEventDashBoard, mImageViewTaskListDashBoard;
    TextView mTextViewEventDashBoard, mTextViewTaskListDashBoard;

    int mFragmentOpenCount = 0;
    private BottomSheetDialog mBottomSheetEmailLogs;
    private MenuItem menuItemSync;
    private androidx.appcompat.app.AlertDialog progressBar;

    public SubmittalsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
        mAquaBlueService = new AquaBlueServiceImpl(context);
        edsSearched = new EventDataSource(context);

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        deviceID = DeviceInfo.getDeviceID(context);
        //  initMenu();
        getDataTask = new EventTask();
        Log.e("submitalFrag", "onCreate: within submittal fragment:------- userGuid:-- and -- userID:--" + userGuid + " -- " + userID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentview = inflater.inflate(R.layout.activity_dashboard, container, false);
        getDataTask = new EventTask();

        emptyview = contentview.findViewById(R.id.empty);
        progressll = contentview.findViewById(R.id.progress_container);
        content_container_fl = contentview.findViewById(R.id.content_container);
        recyclerView = contentview.findViewById(R.id.rr);
        menuMultipleActions = contentview.findViewById(R.id.multiple_actions);
        showsite = contentview.findViewById(R.id.action_showsite);

        mImageViewEventDashBoard = contentview.findViewById(R.id.imageViewEventDashBoard);
        mImageViewTaskListDashBoard = contentview.findViewById(R.id.imageViewTaskListDashBoard);

        mTextViewEventDashBoard = contentview.findViewById(R.id.textViewEventDashBoard);
        mTextViewTaskListDashBoard = contentview.findViewById(R.id.textViewTaskDashBoard);

        showsite.setVisibility(View.VISIBLE);
        //mImageViewCreateNewEvent.setVisibility(View.VISIBLE);

        if (ScreenReso.isLimitedUser)
            showsite.setVisibility(View.GONE);

        final Context context = contentview.getContext();
//        searchView = (MaterialSearchView) contentview.findViewById(R.id.search_view);

        if (getArguments() != null) {
            SELECTED_TAB = getArguments().getInt(GlobalStrings.SELECTED_TAB, 0);
        }

        Log.i(TAG, "FRAGMENT CLICKED TAB :" + SELECTED_TAB);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            SyncStatusob = new SyncStatusDataSource(context);
            lastSyncDate = SyncStatusob.getLastSyncDate(userID, GlobalStrings.SYNC_DATE_TYPE_EVENT);
            Log.i(TAG, "LastSync date for card:" + lastSyncDate);
        }

        showsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.start_new_event);
                builder.setMessage(R.string.do_you_want_to_start_event);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String siteId = ((MainDrawerActivity) getActivity()).getSiteIdForProjectUser();

                        if (ScreenReso.isProjectUser && siteId != null && !siteId.isEmpty() && !siteId.equals("-1")) {
                            ((MainDrawerActivity) getActivity()).startApplicationActivity("");
                        } else {
                            startActivity(new Intent(context, SiteActivity.class));
                        }
//                       getActivity().overridePendingTransition(R.anim.right_to_left,
//                                R.anim.left_to_right);
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mImageViewEventDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class fragmentClass = null;
                Fragment fragment = null;

                fragmentClass = SubmittalsFragment.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fragment != null) {
                    Bundle args = new Bundle();
                    args.putInt("SELECTED", SELECTED_TAB);
                    fragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.load_fragment, fragment);
                    //transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }
            }
        });

        mTextViewEventDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class fragmentClass = null;
                Fragment fragment = null;

                fragmentClass = SubmittalsFragment.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fragment != null) {
                    Bundle args = new Bundle();
                    args.putInt("SELECTED", SELECTED_TAB);
                    fragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.load_fragment, fragment);
                    //transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }
            }
        });

        search_by = contentview.findViewById(R.id.searchby);
        search_by.setQueryHint(getString(R.string.search_select_proj));

        ImageView closeButton = (ImageView) search_by.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEvents();
                search_by.setQueryHint(getString(R.string.search_select_proj));
            }
        });

        search_by.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

               /* if (adapter != null) {
                    adapter.getFilter().filter(query);
                    adapter.notifyDataSetChanged();
                }*/

                search_by.setQueryHint(search_by.getQuery());
                switch (SELECTED_TAB) {
                    case 1:
                        // getDataTask.execute(CLOSED);
                        searchedEventList = edsSearched.getAllEventsOnSearch(CLOSED, query);
                        showData(searchedEventList);
                        break;
                    case 2:
                        //getDataTask.execute(CONFLICT);
                        searchedEventList = edsSearched.getAllEventsOnSearch(CONFLICT, query);
                        showData(searchedEventList);
                        break;
                    default:
                        //getDataTask.execute(ACTIVE);
                        searchedEventList = edsSearched.getAllEventsOnSearch(ACTIVE, query);
                        showData(searchedEventList);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                /*if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    adapter.notifyDataSetChanged();
                }*/
                /*switch (SELECTED_TAB) {
                    case 1:
                        // getDataTask.execute(CLOSED);
                        searchedEventList = edsSearched.getSubmittalsListOnSearch(CLOSED, newText);
                        showData(searchedEventList);
                        break;
                    case 2:
                        //getDataTask.execute(CONFLICT);
                        searchedEventList = edsSearched.getSubmittalsListOnSearch(CONFLICT, newText);
                        showData(searchedEventList);
                        break;
                    default:
                        //getDataTask.execute(ACTIVE);
                        searchedEventList = edsSearched.getSubmittalsListOnSearch(ACTIVE, newText);
                        showData(searchedEventList);
                }*/
                return false;
            }
        });

        return contentview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.city_data_menu, menu);
        dmenu = menu;
        menuItemSync = menu.findItem(R.id.action_sync);
        setSyncBadge();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (CheckNetwork.isInternetAvailable(context)) {
                    ScreenReso.isDownloadData = true;
                    try {
                        Util.setSharedPreferencesProperty(getActivity(),
                                GlobalStrings.IS_EVENTS_REFRESH, true);
                        ((MainDrawerActivity) getActivity()).downloadForms();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    new PostMessageTask().execute();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
                break;

            case android.R.id.home:
                break;

            case R.id.action_search:

                searchView = new SearchView(((MainDrawerActivity) context).getSupportActionBar().getThemedContext());
                MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                MenuItemCompat.setActionView(item, searchView);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        adapter.getFilter().filter(query);
                        adapter.notifyDataSetChanged();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        adapter.getFilter().filter(query);
                        adapter.notifyDataSetChanged();
                        return false;
                    }
                });

                searchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dmenu.findItem(R.id.action_sync).setVisible(true);
                    }
                });
                break;
        }
        return true;
    }

    // Set this up in the UI thread.
    public void showData(ArrayList<EventData> allEventsList) {
        // Collections.sort(list, new CustomComparator());
        if (allEventsList == null || allEventsList.size() < 1) {

            search_by.setVisibility(View.VISIBLE);
            search_by.clearFocus();
            if (!new EventDataSource(context).isEventsDownloadedAlready())
                emptyview.setText(R.string.no_field_event_alert);
            else
                emptyview.setText(R.string.no_field_events);

            emptyview.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        } else {
            search_by.setVisibility(View.VISIBLE);
            search_by.setIconified(true);

            emptyview.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            //Collections.sort(list, new CustomComparator());

            HashMap<Long, List<EventData>> mHashMap = new HashMap<>();

            for (int i = 0; i < allEventsList.size(); i++) {
                EventData EventData = allEventsList.get(i);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(allEventsList.get(i).getStartDate());

                String mYear = String.valueOf(calendar.get(Calendar.YEAR));
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                String mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                String yearMonth = mYear + "-" + month;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                long timeInMilliseconds = 0l;
                try {
                    Date mDate = sdf.parse(yearMonth);
                    timeInMilliseconds = mDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    if (mHashMap.containsKey(timeInMilliseconds)) {
                        mHashMap.get(timeInMilliseconds).add(EventData);
                    } else {
                        List<EventData> listDataCard = new ArrayList<>();
                        listDataCard.add(EventData);
                        mHashMap.put(timeInMilliseconds, listDataCard);
                    }
                } catch (NullPointerException n) {
                    n.printStackTrace();
                }
            }

            ArrayList<EventData> listHeaderEvent = new ArrayList<>();
            for (Map.Entry<Long, List<EventData>> entry : mHashMap.entrySet()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(entry.getKey());

                String year = String.valueOf(calendar.get(Calendar.YEAR));
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

                //below will give date in e.g. NOV 2020
                String date = CalendarFragment.getConvertedDate(year + "-" + month);

                EventData event = new EventData();
                event.setViewType(AllEventsAdapter.HEADER_VIEW);
                event.setUserId(-1);
                event.setEventName(date);
                event.setStartDate(entry.getKey());

                listHeaderEvent.add(event);
            }

            Map<Long, List<EventData>> mapEventsByDate = new TreeMap<>(mHashMap);

            Collections.sort(listHeaderEvent, (lhs, rhs) -> Long.compare(rhs.getStartDate(),
                    lhs.getStartDate()));

            TempEventsAdapter eventsAdapter = new TempEventsAdapter(listHeaderEvent, getActivity(),
                    mapEventsByDate, this, this, SELECTED_TAB);
            recyclerView.setAdapter(eventsAdapter);

//            StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(eventsAdapter);
//            decorator.attachToRecyclerView(recyclerView);
        }
    }

    public void scrollToPos(int pos) {
        recyclerView.scrollToPosition(pos);
    }

    private void closeEventAlert(final int appId, final int eventId, final int siteId, final String siteName) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(R.string.close_end_field_event);
        alertDialogBuilder
                .setMessage(R.string.sure_submit_data_and_close_event);

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String capture = Util.getSharedPreferencesProperty(context, GlobalStrings.CAPTURE_SIGNATURE);
                        boolean CAPTURE_SIGNATURE = false;

                        if (capture == null) {
                            CAPTURE_SIGNATURE = false;
                        } else {
                            CAPTURE_SIGNATURE = Boolean.parseBoolean(capture);
                        }

                        if (CAPTURE_SIGNATURE) {
                            Intent intent = new Intent(context, CaptureSignature.class);
                            intent.putExtra("EVENT_ID", eventId);
                            intent.putExtra("APP_ID", appId);
                            intent.putExtra("SITE_ID", siteId);
                            intent.putExtra("CLOSE", "true");
                            intent.putExtra("UserID", userID);
                            startActivityForResult(intent, CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE);
                        } else {
                            closingEvents(appId, eventId, siteId, siteName);
                        }
//                        closingEvents();
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                        //drawer = false;
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Log.i(TAG, "closeEventAlert() End time:" + System.currentTimeMillis());
    }

    private void closingEvents(int appId, int eventId, int siteId, String siteName) {

        EventDataSource eventData = new EventDataSource(context);

        Log.i(TAG, "closingEvents() Session UserID:" + userID);

        Log.i(TAG, "closingEvents() getEvent arguments :AppID=" + appId +
                ",SiteID=" + siteId + ",userID=" + userID + ",DeviceID=" + deviceID);

//        event = eventData.getEvent(getAppID(), getSiteID(), userID, getDeviceID());
        Log.i(TAG, "closingEvents() getEvent End:" + System.currentTimeMillis());

        closeEvent = true;

        Log.i(TAG, "closingEvents() Call GPS Tracker start time:" + System.currentTimeMillis());

        boolean serverGenerated = eventData.isEventIDServerGenerated(eventId);
        Log.i(TAG, "closingEvents() Check EventID Server Generated result:" + serverGenerated);

        DEvent dEvent = eventData.getEventById(appId, siteId, eventId + "");

        if (!serverGenerated) {

            Log.i(TAG, "closingEvents() EventID is Client Generated ");

/*            final DEvent event = new DEvent();
            event.setSiteId(siteId);
            event.setMobileAppId(appId);
            event.setUserId(userID);
            event.setEventDate(System.currentTimeMillis());
            event.setDeviceId(deviceID);
            event.setLatitude(lat);
            event.setLongitude(longt);
            event.setUserName(username);
            event.setEventId(eventId);*/

            EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(this, dEvent,
                    this.username, this.password, false, getContext());
            eventHandler.execute();
        } else {
            /*
             * fieldSource.updateEventEndDateTime(getAppID(), dateString);
             */

            Log.i(TAG, "closingEvents() EventID Found Server Generated ");

            if (CheckNetwork.isInternetAvailable(context)) {
                uploadFieldDataBeforeEndEvent(eventId);
            } else {
                Log.i(TAG, "closingEvents() No Internet.Delete captured signture(s) = " + savedFilePath);

                removeAttachmentAfterSyncResult(savedFilePath, eventId);
                CustomToast.showToast((Activity) context, getString(R.string.bad_internet_connectivity), 5);
            }
        }

        Log.i(TAG, "closingEvents() End time:" + System.currentTimeMillis());
    }

    private void removeAttachmentAfterSyncResult(String savedFilePath, int eventId) {
        Log.i(TAG, "removeAttachmentAfterSyncResult() EventID Not Server Generated ");

        Log.i(TAG, "removeAttachmentAfterSyncResult() IN time:" + System.currentTimeMillis());
        deleteFileFromStorage(savedFilePath);
        int count = new AttachmentDataSource(context).deleteAttachment(eventId, "S");
        Log.i(TAG, "Removed No.of Attachment:" + count);
        Log.i(TAG, "removeAttachmentAfterSyncResult() OUT time:" + System.currentTimeMillis());
    }

    private void deleteFileFromStorage(String filePath) {
        Log.i(TAG, "deleteFileFromStorage() Start time:" + System.currentTimeMillis());
        Log.i(TAG, "deleteFileFromStorage() FilePath:" + filePath);

        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);
        }

        Log.i(TAG, "deleteFileFromStorage() End time:" + System.currentTimeMillis());
    }

    private void uploadFieldDataBeforeEndEvent(int eventId) {
        Log.i(TAG, "Upload Field Data endEvent Call  start:" + System.currentTimeMillis());
        Intent dataUpload = new Intent(context, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", eventId);
        dataUpload.putExtra("CLOSE_EVENT", true);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        Log.i(TAG, "Upload Field Data endEvent arguments:EventID-=" + eventId +
                ",UserName=" + username + ",Password=" + password);

        Log.i(TAG, "Upload Field Data endEvent Call End:" + System.currentTimeMillis());
    }

    private void RequiredDataInFormAlert(final int appId, final int eventId, final int siteId, final String siteName) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(R.string.attention);
        alertDialogBuilder.setTitle(R.string.attention);
        alertDialogBuilder
                .setMessage(getString(R.string.some_forms_have_mandatory_field_need_to_be_filled));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(R.string.show,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent req_intent = new Intent(context, RequiredFieldsListActivity.class);

                        req_intent.putExtra("APP_ID", appId);
                        req_intent.putExtra("EVENT_ID", eventId);
                        req_intent.putExtra("SITE_ID", siteId);
                        req_intent.putExtra("SITENAME", siteName);
                        startActivity(req_intent);

                        dialog.dismiss();
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_upper_case),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

      /*  SharedPreferences.Editor editor = getContext().getSharedPreferences("BADELFGPS", getContext().MODE_PRIVATE).edit();
        editor.clear().commit();

        new BadELFGPSTracker(context);*/

        getDataTask = new EventTask();

        if (ScreenReso.isDownloadData) {
            ScreenReso.isDownloadData = false;

            if (CheckNetwork.isInternetAvailable(context)) {
                EventDownloadTask pt = new EventDownloadTask();
                pt.execute();
            } else {
                if (new EventDataSource(context).isEventsDownloadedAlready()) {
                    switch (SELECTED_TAB) {
                        case 1:
                            getDataTask.execute(CLOSED);
                            break;
                        case 2:
                            getDataTask.execute(CONFLICT);
                            break;
                        default:
                            getDataTask.execute(ACTIVE);
                    }
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (!new EventDataSource(context).isEventsDownloadedAlready()) {

                if (CheckNetwork.isInternetAvailable(context)) {
                    EventDownloadTask pt = new EventDownloadTask();
                    pt.execute();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            } else {

                switch (SELECTED_TAB) {
                    case 1:
                        getDataTask.execute(CLOSED);
                        break;
                    case 2:
                        getDataTask.execute(CONFLICT);
                        break;
                    default:
                        getDataTask.execute(ACTIVE);
                }
            }
        }       //getEvents();

        setSyncBadge();
    }

    private void setSyncBadge() {
        if (menuItemSync != null) {
            Util.setBadgeCount(getActivity(), menuItemSync, "",
                    Util.isThereAnyDataToSync(getActivity()));
        }
    }

    private void getEvents() {
        getDataTask = new EventTask();
        if (!new EventDataSource(context).isEventsDownloadedAlready()) {

            if (CheckNetwork.isInternetAvailable(context)) {
                EventDownloadTask pt = new EventDownloadTask();
                pt.execute();
            } else {
                Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            }
        } else {
            switch (SELECTED_TAB) {
                case 1:
                    getDataTask.execute(CLOSED);
                    break;
                case 2:
                    getDataTask.execute(CONFLICT);
                    break;
                default:
                    getDataTask.execute(ACTIVE);
            }
        }
    }

    private void showLoading() {
        progressll.setVisibility(View.VISIBLE);
        content_container_fl.setVisibility(View.GONE);
        search_by.setVisibility(View.GONE);
        search_by.setIconified(true);
        search_by.clearFocus();
    }

    private void dismissLoading() {
        progressll.setVisibility(View.GONE);
        content_container_fl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskCompleted(Object obj) {
        FieldDataSource fieldData = new FieldDataSource(context);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(context);
        EventDataSource eventData = new EventDataSource(context);

        if (obj != null) {

            if (obj instanceof String) {

                String result = (String) obj;

                if (result.equals("SUCCESS")) {

                    Event event = eventData.getEvent(mAppid, mSiteId, userID, deviceID);
                    int currentEventID = event.getEventId();
                    //setEventID(currentEventID);
                    uploadFieldData();
                } else {
                    Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
                }
            } else if (obj instanceof EventResponseModel) {

                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {

                    serverGenEventID = result.getData().getEventId();
                    setGeneratedEventID(result);
                    Log.i(TAG, "Event ID From Server:" + serverGenEventID);

                    fieldData.updateEventID(mEventId, serverGenEventID);
                    attachDataSrc.updateEventID(mEventId, serverGenEventID);
                    eventData.updateEventID(mEventId, result);
                    new SampleMapTagDataSource(context).updateEventID_SampleMapTag(mEventId + "", serverGenEventID + "");

                    //setEventID(serverGenEventID);

                    //changing client negative eventId to server gen Id so that the event can be closed from server
                    if (mEventId < 0)
                        mEventId = serverGenEventID;

                    if (CheckNetwork.isInternetAvailable(context)) {
                        if (closeEvent) {
                            uploadFieldDataBeforeEndEvent(mEventId);
                        } else {
                            uploadFieldData();
                        }
                    } else {
                        CustomToast.showToast((Activity) context, getString(R.string.bad_internet_connectivity), 5);
                    }
                } else {
                    if (result.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                        //04-Mar-16
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                    if ((result.getResponseCode() == HttpStatus.NOT_FOUND) || (result.getResponseCode() == HttpStatus.LOCKED)) {
                        Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
//                    Toast.makeText(context,GlobalStrings.responseMessage,Toast.LENGTH_LONG).show();
                    }
                    if (result.getResponseCode() == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFieldData() {

        LDSource = new LocationDataSource(context);
        fieldSource = new FieldDataSource(context);
        attachDataSource = new AttachmentDataSource(context);

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(getActivity());

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
        logDetails.setScreenName("Events Screen");
        logDetails.setDetails("Has field data before checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());

        tempLogsDataSource.insertTempLogs(logDetails);

        isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();//24-Mar-17
        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(context);

        isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        logDetails.setDetails("Has field data upon checking old strings? Rows: "
                + fieldSource.collectDataForSyncUpload().size());
        tempLogsDataSource.insertTempLogs(logDetails);

        logDetails.setDetails("CHECKING DATA TO SYNC - " + " Has locations:"
                + isLocationsAvailableToSync
                + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync);
        tempLogsDataSource.insertTempLogs(logDetails);

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            Toast.makeText(context, getString(R.string.no_data_to_sync), Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "uploadFieldData() Upload Field Data Called:" + System.currentTimeMillis());
            Intent dataUpload = new Intent(context, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", mEventId);
            startActivity(dataUpload);
        }
        Log.i(TAG, "Upload Field Data arguments:username=" + username + ",password=" + password + ",EventID=" + mEventId);
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void setGeneratedEventID(int id) {
        if (id != 0) {
            serverGenEventID = id;
            Log.i(TAG, "setGeneratedEventID() serverEventID:" + id);
        }
    }

    @Override
    public void setGeneratedEventID(Object obj) {
        EventResponseModel res = (EventResponseModel) obj;
        int id = res.getData().getEventId();
        if (id != 0) {
            serverGenEventID = id;
            Log.i(TAG, "setGeneratedEventID() serverEventID:" + id);
        }
    }

    @Override
    public void onEventCloseClick(View view, int position, EventData event) {
        mAppid = event.getMobAppID();
        mEventId = event.getEventID();
        mSiteId = event.getSiteID();
        mSiteName = event.getSiteName();

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        ArrayList<RequiredFieldRowItem> reqDataList
                = fieldDataSource.getMandatoryFieldList(mAppid + "",
                mEventId + "", mSiteId + "");

        if (CalendarFragment.hasRequiredLocationsFields(event.getSiteID(), event.getEventID(),
                event.getMobAppID(), requireContext())) {
            //all operations are done in the condition method
        } else if (reqDataList != null && reqDataList.size() > 0 && reqDataList.get(0).getCount() > 0) {
            RequiredDataInFormAlert(mAppid, mEventId, mSiteId, mSiteName);
        } else {
            closeEventAlert(mAppid, mEventId, mSiteId, mSiteName);
        }
    }

    @Override
    public void onEmailLogsClicked(EventData event) {
        showEmailLogsBottomSheet(event);
    }

    @Override
    public void onEventCardClicked(EventData event) {

        FieldDataSource fieldDataSource = new FieldDataSource(requireActivity());
        if (userID != event.getUserId() &&
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

        int event_status = event.getStatus();

        if (event_status == 1) {

            Util.setSharedPreferencesProperty(requireActivity(), GlobalStrings.CURRENT_SITEID,
                    event.getSiteID() + "");
            Util.setSharedPreferencesProperty(requireActivity(), GlobalStrings.CURRENT_APPID,
                    event.getMobAppID() + "");
            Util.setSharedPreferencesProperty(requireActivity(), GlobalStrings.CURRENT_SITENAME,
                    event.getSiteName() + "");

            Intent locationIntent = new Intent(requireActivity(), LocationActivity.class);

            boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(requireActivity(),
                    GlobalStrings.ENABLE_SPLIT_SCREEN);

            if (Util.isTablet(requireActivity()) && isSplitScreenEnabled)
                locationIntent = new Intent(requireActivity(), SplitLocationAndMapActivity.class);

            locationIntent.putExtra("APP_ID", event.getMobAppID());
            locationIntent.putExtra("SITE_ID", event.getSiteID());
            locationIntent.putExtra("SITE_NAME", event.getSiteName());
            locationIntent.putExtra("EVENT_ID", event.getEventID());

            startActivity(locationIntent);
        }
    }

    @Override
    public void onDownloadDataClicked(EventData event) {
        DataSyncDateSource syncDateSource = new DataSyncDateSource(getActivity());
        long timeMillis =
                syncDateSource.getDataSyncTime(event.getEventID() + "",
                        event.getSiteID() + "");

        if (timeMillis != 0) {
            AlertManager.showDownloadDataWaitAlert((AppCompatActivity) getActivity(),
                    getActivity().getString(R.string.download_data),
                    "Please wait for 00m00s to download data.", timeMillis);
        } else if (CheckNetwork.isInternetAvailable(getActivity())) {
            Intent i = new Intent(getActivity(), DownloadYourOwnDataActivity.class);
            i.putExtra("SITE_NAME", event.getSiteName());
            i.putExtra("SITEID", event.getSiteID());
            i.putExtra("EVENTID", event.getEventID());
            i.putExtra("APP_NAME", event.getMobAppName());
            i.putExtra("PARENTAPPID", event.getMobAppID());
            i.putExtra("REPORT", "TRUE");
            startActivity(i);
        } else {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmailLogsBottomSheet(EventData event) {
        try {
            View sheetView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.layout_bottom_sheet_email_logs, null);
            mBottomSheetEmailLogs = new BottomSheetDialog(getActivity());
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
                Intent i = new Intent(getActivity(), MobileReportActivity.class);
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

    private void getReport(boolean isForPM, boolean isPdf, EventData event, boolean isForSelf) {
        //even if isPdf has value, use of it depends on the isForPM value in api call

        if (CheckNetwork.isInternetAvailable(getActivity())) {
            new SendReportTask(isForPM, isPdf, event, isForSelf).execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
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
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.emailing_logs_please_wait));
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
                AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(getActivity());

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
            } catch (NullPointerException n) {
                n.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
                    msg = getString(R.string.unable_to_generate_report);
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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

    private class EventTask extends AsyncTask<Integer, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Object doInBackground(Integer... args) {

            int event_status = args[0];
            EventDataSource eds = new EventDataSource(context);

            String siteId = "";
            if (getActivity() instanceof MainDrawerActivity) {
                siteId = ((MainDrawerActivity) getActivity()).getSiteIdForProjectUser();
            }

            return eds.getAllEvents(event_status, siteId);
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            dismissLoading();
            //            SELECTED_TAB = 0;
            showData((ArrayList<EventData>) s);
        }
    }

    private class EventDownloadTask extends AsyncTask<MediaType, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }// end of onPreExecute

        @Override
        protected String doInBackground(MediaType... params) {

            SubmittalResponseCollector mResponse = new SubmittalResponseCollector();
            List<SubmittalModel> mSubmittalList = new ArrayList<>();
            String response;
            try {
                if (null != mAquaBlueService) {
                    lastSyncDate = SyncStatusob.getLastSyncDate(userID, GlobalStrings.SYNC_DATE_TYPE_EVENT);

                    mResponse = mAquaBlueService.getEventList(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.download_eventlist),
                            userID + "", lastSyncDate);

                    if (null != mResponse) {
                        Log.e("CallsCheck", "doInBackground: event list response called");
                        if (!mResponse.isSuccess()) {
                            String code = mResponse.getResponseCode().toString();
                            if (code.equals("401")) {
                                response = "DEVICE-DISABLE";
                            } else if (code.equals("417")) {
                                response = "USER-SUSPENDED";
                            } else {
                                response = "FALSE";
                                Log.e("Agreement", "doInBackground: mResponse.isSuccess() is--- " + mResponse.isSuccess() + " msg " + mResponse.getMessage() + " code " + mResponse.getResponseCode());
                            }
                        } else if (mResponse.isSuccess()) {
                            response = "SUCCESS";
                            Log.e("Agreement", "doInBackground: mResponse.isSuccess() is--- " + mResponse.isSuccess());
                            String newSyncTime_server = mResponse.getData().getLastSyncDate() + "";
                            Log.i(TAG, "All submittal last sync time:" + newSyncTime_server);

                            if (mResponse.getData().getEventList().size() > 0) {

                                mSubmittalList = mResponse.getData().getEventList();
                                //11/10/2017 INSERT EVENT LIST
                                EventDataSource eds = new EventDataSource(context);
                                eds.saveSubmittalsList(mSubmittalList, userID);
                                SyncStatusob = new SyncStatusDataSource(context);
                                //24-May-16 Update LastSyncDate in d_sync_status table
                                SyncStatusob.insertLastSyncDate(userID, Long.valueOf(newSyncTime_server),
                                        GlobalStrings.SYNC_DATE_TYPE_EVENT);
                            }
                        } else {
                            GlobalStrings.responseMessage = mResponse.getMessage();
                            response = mResponse.getResponseCode().toString();
                            if (mResponse.getResponseCode() == HttpStatus.LOCKED) {
                                response = HttpStatus.LOCKED.toString();
                            }
                            if (mResponse.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                                response = HttpStatus.NOT_ACCEPTABLE.toString();
                            }
                            if (mResponse.getResponseCode() == HttpStatus.NOT_FOUND) {
                                response = HttpStatus.NOT_FOUND.toString();
                            }
                            if (mResponse.getResponseCode() == HttpStatus.BAD_REQUEST) {
                                response = HttpStatus.BAD_REQUEST.toString();
                            }
                        }
                    } else {
                        response = "RETRY";
                    }
                } else {
                    response = "RETRY";
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "All submittals doInBackground() Exception:" + e.getMessage());
                return null;
            }

            return response;
        }// end ofdoInBackground

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, " onPostExecute: Result = " + result);
            dismissLoading();
            showResult(result);
        }// end of onPostExecute
    }

    private void showResult(String result) {
        Log.d(TAG, " All Submittals sync result= " + result);
        if (result != null) {
            if (result.equals("FALSE")) {
                showAgreement();
            } else if (result.equals("SUCCESS")) {
                getDataTask = new EventTask();

                switch (SELECTED_TAB) {
                    case 1:
                        getDataTask.execute(CLOSED);
                        break;
                    case 2:
                        getDataTask.execute(CONFLICT);
                        break;
                    default:
                        getDataTask.execute(ACTIVE);
                }
            } else if (result.equals("USER-SUSPENDED")) {
                String msg = getString(R.string.account_disabled_contact_qnopy_if_error);
                CustomAlert.showAlert(context, msg, getString(R.string.alert));
            } else if (result.equals("DEVICE-DISABLE")) {
                String msg = getString(R.string.device_disabled_contact_to_activate_device);
                CustomAlert.showAlert(context, msg, getString(R.string.alert));
            } else if (result.equals(HttpStatus.NOT_ACCEPTABLE.toString())) {
                Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
            } else if (result.equals("RETRY")) {
                Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
            } else if (result.equals(HttpStatus.LOCKED.toString()) || result.equals(HttpStatus.NOT_FOUND.toString())) {
                Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
            } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
        }
    }// end of showResult

    private void showAgreement() {

        Intent intent = new Intent(getContext(), AgreementActivity.class);
        intent.putExtra("input", "dashboard");
//        startActivity(intent);
        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//        getActivity().finish();
    }

    void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public interface OnListFragmentInteractionListener {
        //Update argument type and name
        void onListFragmentInteraction(EventData item);
    }

    public class CustomComparator implements Comparator<EventData> {
        @Override
        public int compare(EventData lhs, EventData rhs) {
            int res = 0;
            try {
                if (SELECTED_TAB == 1) {//CLOSED

                    if (rhs.getEndDate() > lhs.getEndDate()) {
                        res = 1;
                    } else {
                        res = -1;
                    }
                    //  res = (int) (rhs.getEndDate() - lhs.getEndDate());

                } else {//ACTIVE OR CONFLICT

                    if (rhs.getStartDate() > lhs.getStartDate()) {
                        res = 1;
                    } else {
                        res = -1;
                    }
                    //res = (int) (rhs.getStartDate() - lhs.getStartDate());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error:" + e.getMessage());
                return -1;
            }

//            if (res==0){
//                res=-1;
//            }

            Log.i(TAG, "Comparison result:" + res);
            return res;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult() CAPTURE_SIGNATURE Start time:" + System.currentTimeMillis());
            boolean isOk = true;
            closingEvents(mAppid, mEventId, mSiteId, mSiteName);
            Log.i(TAG, "onActivityResult() CAPTURE_SIGNATURE End time:" + System.currentTimeMillis());
        } else if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {

            Log.i(TAG, "onActivityResult() SYNC_ACTIVITY Start time:" + System.currentTimeMillis());

            if (data.hasExtra("SYNC_FLAG")) {
                long date = System.currentTimeMillis();

                boolean eventClosed = data.getBooleanExtra("SYNC_FLAG", false);
                long eventEndDate = Long.parseLong(data.getStringExtra("EVENT_END_DATE"));
                boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);

                if (eventEndDate < 1) {
                    eventEndDate = date;
                }
                if (dataSynced && eventClosed) {
                    EventDataSource eventData = new EventDataSource(context);
                    CompletionPercentageDataSource cp = new CompletionPercentageDataSource(context);

                    eventData.closeEventStatus(mAppid, mSiteId, eventEndDate, mEventId + "");
                    cp.truncatePercentageByRollAppID_And_SiteID(mSiteId + "", mAppid + "");
                    SharedPref.resetCamOrMap();

                    Toast.makeText(context, getString(R.string.event_has_been_closed), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context,
                            getString(R.string.event_cannot_be_closed),
                            Toast.LENGTH_LONG).show();
                }
                if (savedFilePath != null) {
                    removeAttachmentAfterSyncResult(savedFilePath, mEventId);
                }
            }
            Log.i(TAG, "onActivityResult() SYNC_ACTIVITY End time:" + System.currentTimeMillis());
        }
    }

    @Override
    public void showDownloadEventProgress() {
        progressBar = AlertManager.showQnopyProgressBar((AppCompatActivity) requireActivity(),
                "Checking to see if there is data for this event..");
        progressBar.show();
    }

    @Override
    public void cancelDownloadEventProgress() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.cancel();
        }
    }

    @Override
    public void showLocationScreen(EventData event) {
        startLocationIntent(event);
    }
}