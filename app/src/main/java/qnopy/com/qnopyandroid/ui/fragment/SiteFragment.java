package qnopy.com.qnopyandroid.ui.fragment;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.adapter.MySitelistRecyclerViewAdapter;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.responsemodel.EventResponseData;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.ApplicationActivity;
import qnopy.com.qnopyandroid.ui.activity.MainDrawerActivity;
import qnopy.com.qnopyandroid.ui.activity.StartNewEventActivity;
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.sitesProjectUser.AllSitesActivity;
import qnopy.com.qnopyandroid.ui.sitesProjectUser.SitesMapActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.DividerItemDecoration;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class SiteFragment extends Fragment implements
        MySitelistRecyclerViewAdapter.OnSiteClickedListener,
        DownloadEventListTask.OnEventDownloadListener, CustomAlert.LocationServiceAlertListener {

    private static final String TAG = "SiteFragment";
    private int userID = 0, company = 0;
    private String password = "", username = "";
    private OnSiteClickListener mListener;
    List<Site> siteList;
    Context context;
    private RecyclerView rvSite;
    private SearchView searchView;
    private MySitelistRecyclerViewAdapter adapter;
    private double lat = 0;
    private double longt = 0;
    private ProgressDialog progressDialog;
    private MenuItem menuItemSync;
    private boolean isSortdescending = false;
    private FusedLocationProviderClient fusedLocationClient;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private TextView tvNoSites;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public SiteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);

        userID = Integer.parseInt(Util.getSharedPreferencesProperty(getActivity(), GlobalStrings.USERID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);
        rvSite = view.findViewById(R.id.rvSite);

        searchView = view.findViewById(R.id.searchViewSite);
        tvNoSites = view.findViewById(R.id.tvNoSites);
        rvSite.setLayoutManager(new LinearLayoutManager(context));
        rvSite.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL_LIST));
        return view;
    }

    private void populateSites() {
        if (siteList.size() > 0) {
            adapter = new MySitelistRecyclerViewAdapter(context, siteList,
                    mListener, this);
            rvSite.setAdapter(adapter);

            rvSite.setVisibility(View.VISIBLE);
            tvNoSites.setVisibility(View.GONE);
        } else {
            rvSite.setVisibility(View.GONE);
            tvNoSites.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpSearchView();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(getActivity(), this);
            } else
                getLocation();
        } else
            getLocation();

        ConstraintLayout layoutSortControls = view.findViewById(R.id.layoutSortControls);

        if (ScreenReso.isProjectUser)
            layoutSortControls.setVisibility(View.VISIBLE);

        ImageView ivMapSites = view.findViewById(R.id.ivMapSites);
        ivMapSites.setImageDrawable(VectorDrawableUtils.getDrawable(getActivity(),
                R.drawable.ic_map_view, R.color.dark_gray));

        TextView tvAllProjects = view.findViewById(R.id.tvAllProjects);
        tvAllProjects.setCompoundDrawablesWithIntrinsicBounds(null, null,
                VectorDrawableUtils.getDrawable(getActivity(),
                        R.drawable.arrow_right_enabled, R.color.event_start_blue), null);

        tvAllProjects.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), MainDrawerActivity.class);
            Intent intent = new Intent(getActivity(), HomeScreenActivity.class);
            //sending -1 to show all projects, so checking -1 in MainDrawerActivity
            //so that no site specific events will be shown
            intent.putExtra(GlobalStrings.KEY_SITE_ID, "-1");
            startActivity(intent);
        });

        ImageView ivSiteMap = view.findViewById(R.id.ivMapSites);
        ivSiteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SitesMapActivity.class);
                startActivity(intent);
            }
        });

        ImageView ivSortSites = view.findViewById(R.id.ivSortSites);
        ivSortSites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siteList != null && siteList.size() > 0) {
                    if (isSortdescending) {
                        isSortdescending = false;
                        Collections.sort(siteList, (s1, s2) -> s1.getSiteName().compareToIgnoreCase(s2.getSiteName()));
                        ivSortSites.setImageDrawable(VectorDrawableUtils.getDrawable(getActivity(),
                                R.drawable.ic_sort_by_alphabet, R.color.dark_gray));
                    } else {
                        isSortdescending = true;
                        Collections.sort(siteList, (s1, s2) -> s2.getSiteName().compareToIgnoreCase(s1.getSiteName()));
                        ivSortSites.setImageDrawable(VectorDrawableUtils.getDrawable(getActivity(),
                                R.drawable.ic_sort_by_alphabet, R.color.event_start_blue));
                    }

                    adapter = new MySitelistRecyclerViewAdapter(context, siteList,
                            mListener, SiteFragment.this);
                    rvSite.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        siteList = populateSiteData(userID);
        populateSites();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CustomAlert.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                getLocation();
            }
        }

        if (ScreenReso.isDownloadData) {
            ScreenReso.isDownloadData = false;

            if (CheckNetwork.isInternetAvailable(getActivity())) {
                new DownloadEventListTask((AppCompatActivity) getActivity(),
                        SiteFragment.this).execute();
            } else {
                if (new EventDataSource(getActivity()).isEventsDownloadedAlready()) {
                    getAllEvents();
                } else
                    Toast.makeText(getActivity(), getString(R.string.bad_internet_connectivity),
                            Toast.LENGTH_LONG).show();
            }
        } else if (!new EventDataSource(getActivity()).isEventsDownloadedAlready()) {
            getAllEvents();
        }

        setSyncBadge();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // Request permission
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // Main code
            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    String result = "";

                    if (task.isSuccessful()) {
                        // Task completed successfully
                        Location location = task.getResult();
                        GlobalStrings.CURRENT_GPS_LOCATION = location;
                        if (location != null) {
                            result = "Location (success): " +
                                    location.getLatitude() +
                                    ", " +
                                    location.getLongitude();

                            lat = location.getLatitude();
                            longt = location.getLongitude();
                        }
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;
                    }
                    Log.d(TAG, "getCurrentLocation() result: " + result);
                }
            }));
        } else {
            Log.d(TAG, "Request fine location permission.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setSyncBadge() {
        if (menuItemSync != null) {
            Util.setBadgeCount(requireActivity(), menuItemSync, "",
                    Util.isThereAnyDataToSync(requireActivity()));
        }
    }

    private void getAllEvents() {
        DownloadEventListTask task = new DownloadEventListTask((AppCompatActivity) getActivity(),
                SiteFragment.this);
        task.execute();
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
            if (CheckNetwork.isInternetAvailable(context)) {
                try {
                    Util.setSharedPreferencesProperty(requireActivity(),
                            GlobalStrings.IS_SITE_REFRESH, true);

                    if (getActivity() instanceof MainDrawerActivity)
                        ((MainDrawerActivity) getActivity()).downloadForms();
                    else if (getActivity() instanceof AllSitesActivity)
                        ((AllSitesActivity) getActivity()).downloadForms();
                    else if (getActivity() instanceof HomeScreenActivity)
                        ((HomeScreenActivity) getActivity()).downloadForms();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            }

//            getAllEvents();
        }
        return true;
    }

    private void setUpSearchView() {
        searchView.setQueryHint(getString(R.string.search_project));

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSiteClickListener) {
            mListener = (OnSiteClickListener) context;
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
    public void OnSiteClicked(String siteName, int siteId) {
        if (ScreenReso.isProjectUser)
            return;

        int companyID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));

        SiteMobileAppDataSource appData = new SiteMobileAppDataSource(context);
        List<SSiteMobileApp> siteFormList = new ArrayList<>();

        siteFormList = appData.getAllAppsV16(siteId);

        if (siteFormList.size() > 1)
            invokeApplicationActivity(siteName, siteId);
        else if (siteFormList.size() == 1) {
            checkEventExist(siteFormList.get(0), siteId, siteName);
        }
        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID, siteId + "");
    }

    private void checkEventExist(SSiteMobileApp siteMobApp, int siteId, String siteName) {
        int eventID = 0;
        EventDataSource eventData = new EventDataSource(context);
        eventID = eventData.pickEventID(siteMobApp.getMobileAppId(), siteId,
                userID,
                GlobalStrings.CURRENT_GPS_LOCATION, DeviceInfo.getDeviceID(getActivity()));

        if (eventID == 0 && CheckNetwork.isInternetAvailable(context)) {
            checkActiveEvents(siteMobApp.getMobileAppId(), siteId);
        } else {
            EventDataSource eventDataSource = new EventDataSource(getActivity());
            ArrayList<DEvent> eventList = eventDataSource.getSiteEvents(siteMobApp.getMobileAppId(),
                    siteId);

            if (eventList.size() > 1)
                gotoStartNewScreen(siteMobApp, siteName, siteId);
            else
                startLocationActivity(eventID, siteMobApp.getMobileAppId(), siteId);
        }
    }

    private void gotoStartNewScreen(SSiteMobileApp siteMobApp, String siteName, int siteId) {
        Intent intent = new Intent(getActivity(), StartNewEventActivity.class);
        intent.putExtra(GlobalStrings.CURRENT_APPID, siteMobApp.getMobileAppId());
        intent.putExtra(GlobalStrings.CURRENT_SITEID, siteId);
        intent.putExtra(GlobalStrings.FORM_NAME, siteMobApp.getDisplay_name());
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
        intent.putExtra(GlobalStrings.USERID, userID);
        startActivity(intent);
    }

    private void checkActiveEvents(Integer mobileAppId, Integer siteId) {

        DEvent event = new DEvent();
        event.setSiteId(siteId);
        event.setMobileAppId(mobileAppId);
        event.setUserId(userID);
        event.setDeviceId(DeviceInfo.getDeviceID(getActivity()));
        event.setLatitude(lat);
        event.setLongitude(longt);
        event.setUserName(username);
        event.setEventDate(System.currentTimeMillis());
        event.setEventStartDate(System.currentTimeMillis());

        AsyncEventCheck eventHandler = new AsyncEventCheck(event);
        eventHandler.execute();
    }

    private void startLocationActivity(int eventID, Integer mobileAppId, int siteId) {
        Intent locationIntent = new Intent(getActivity(), LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(getActivity(),
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(getActivity()) && isSplitScreenEnabled)
            locationIntent = new Intent(getActivity(), SplitLocationAndMapActivity.class);

        locationIntent.putExtra("APP_ID", mobileAppId);
        locationIntent.putExtra("EVENT_ID", eventID);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(locationIntent);
    }

    private void invokeApplicationActivity(String siteName, int siteID) {

        Intent applicationIntent = new Intent(context, ApplicationActivity.class);

        applicationIntent.putExtra("SITE_NAME", siteName);
        applicationIntent.putExtra("SITE_ID", siteID);
        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID, ""
                + siteID);
        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME, siteName);
        Log.i(TAG, "Selected site id:" + siteID + "& siteName:" + siteName);
        context.startActivity(applicationIntent);
    }

    @Override
    public void onEventDownloadSuccess() {

    }

    @Override
    public void onEventDownloadFailed() {

    }

    @Override
    public void onLocationDeny() {
        //no use
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSiteClickListener {
        void onSiteClicked(Site item);
    }

    public List<Site> populateSiteData(int userid) {
        List<Site> list = new ArrayList<>();
        SiteDataSource siteData = new SiteDataSource(context);
        list = siteData.getAllSitesForUser(userid);
        return list;
    }

    void startProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    class AsyncEventCheck extends AsyncTask<Void, Void, EventResponseModel> {

        private final DEvent event;
        private final AquaBlueServiceImpl mAquaBlueService;

        public AsyncEventCheck(DEvent event) {
            this.event = event;
            mAquaBlueService = new AquaBlueServiceImpl(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected EventResponseModel doInBackground(Void... voids) {
            String guid = Util.getSharedPreferencesProperty(context, username);

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("deviceId", DeviceInfo.getDeviceID(context));
                jsonObject.put("siteId", event.getSiteId());
                jsonObject.put("mobileAppId", event.getMobileAppId());
                jsonObject.put("userGuid", guid);//"f8180e4a-3b36-11e5-9708-0ea7cb7cc776"
                jsonObject.put("userId", event.getUserId());
                jsonObject.put("eventDate", event.getEventDate());
                jsonObject.put("eventStartDate", event.getEventStartDate());
                jsonObject.put("createEventFlag", 0);

            } catch (JSONException e1) {
                e1.printStackTrace();
                Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
                return null;
            }

            return mAquaBlueService.generateEventIDFromServer(context.getResources().getString(R.string.prod_base_uri),
                    context.getResources().getString(R.string.prod_event_check), event,
                    username,
                    jsonObject);
        }

        @Override
        protected void onPostExecute(EventResponseModel eventResponse) {
            super.onPostExecute(eventResponse);
            dismissProgressDialog();
            if (eventResponse != null) {
                if (eventResponse.isSuccess()) {
                    Log.e("check events response", eventResponse.getMessage());
                    boolean hasActiveEvents = eventResponse.getMessage().equalsIgnoreCase("Active");

                    if (hasActiveEvents) {
                        insertEventDataToDb(eventResponse.getData());
                    } else {
                        showNoEventAlert();
                    }
                }
            }
        }
    }

    private void insertEventDataToDb(EventResponseData data) {

        DEvent event = new DEvent();
        event.setEventId(data.getEventId());
        event.setMobileAppId(data.getMobileAppId());
        event.setSiteId(data.getSiteId());
        event.setUserId(userID);
        event.setLatitude(data.getLatitude());
        event.setLongitude(data.getLongitude());
        event.setDeviceId(data.getDeviceId());
        event.setEventDate(data.getEventDate());
        event.setEventStartDate(data.getEventStartDate());
        event.setEventEndDate(data.getEventEndDate());
        event.setEventName(data.getEventName());

        String generatedBy = "S";
        EventDataSource eventData = new EventDataSource(context);
        eventData.insertEventData(event, generatedBy);

        Intent locationIntent = new Intent(context, LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(getActivity(),
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(getActivity()) && isSplitScreenEnabled)
            locationIntent = new Intent(context, SplitLocationAndMapActivity.class);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        locationIntent.putExtra("APP_ID", event.getMobileAppId());
        locationIntent.putExtra("EVENT_ID", event.getEventId());
        locationIntent.putExtra("fromaddsite", false);
        startActivity(locationIntent);
    }

    private void showNoEventAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.event_alert));
        builder.setMessage(getString(R.string.no_active_events_contact_qnopy_admin));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
