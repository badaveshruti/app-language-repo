package qnopy.com.qnopyandroid.ui.task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class TaskMapFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private FloatingActionButton fabSatellite;
    private boolean isSatellite;
    private GoogleMap mMap;
    private int mSelectedTab = 0;
    private boolean isFromForms;
    private TaskIntentData taskData;
    private ArrayList<TaskDataResponse.TaskDataList> taskDataLists = new ArrayList<>();
    private BadELFGPSTracker badElf;
    private String siteId;

    public TaskMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (getArguments() != null) {
            mSelectedTab = getArguments().getInt(GlobalStrings.SELECTED_TAB, 0);
            isFromForms = getArguments().getBoolean(GlobalStrings.IS_FROM_FORMS);
            taskData = (TaskIntentData) getArguments().getSerializable(GlobalStrings.TASK_INTENT_DATA);
            siteId = getArguments().getString(GlobalStrings.KEY_SITE_ID);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fabSatellite = view.findViewById(R.id.fabSatellite);

        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSatellite) {
                    isSatellite = false;
                    fabSatellite.setImageDrawable(VectorDrawableUtils
                            .getDrawable(getActivity(), R.drawable.ic_satellite,
                                    R.color.black_faint));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    isSatellite = true;
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    fabSatellite.setImageDrawable(VectorDrawableUtils
                            .getDrawable(getActivity(), R.drawable.ic_satellite,
                                    R.color.qnopy_splash));
                }
            }
        });
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

    public void getTaskList(int selectedTab) {
        mSelectedTab = selectedTab;
        TaskDetailsDataSource dataSource = new TaskDetailsDataSource(getActivity());
        if (!isFromForms)
            taskDataLists = dataSource.getAllTasks(selectedTab, 0, siteId);//parent task id is 0
        else
            taskDataLists = dataSource.getAllTasksByFormDetails(selectedTab,
                    0, taskData);//parent task id is 0

        addMarkersToMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        badElf = new BadELFGPSTracker(getActivity());

        if (mMap != null)
            getTaskList(mSelectedTab);
    }

    @Override
    public void onPause() {
        badElf.disconnectTracker();
        super.onPause();
    }

    @Override
    public void onStop() {
        badElf.disconnectTracker();
        super.onStop();
    }

    @SuppressLint({"PotentialBehaviorOverride", "MissingPermission"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        try {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
//                        mMap.animateCamera(zoom);
                    }
                });

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setInfoWindowAdapter(new InfoWindowAdapter());

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                List<String> arr = Util.splitStringToArray(",", marker.getSnippet());
                if (!arr.isEmpty()) {
                    TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(getActivity());

                    Intent intent = new Intent(getActivity(), EditTaskActivity.class);
                    intent.putExtra(GlobalStrings.TASK_DATA, taskDetailsDataSource.getTaskData(arr.get(0)));
                    startActivityForResult(intent,
                            GlobalStrings.REQUEST_CODE_EDIT_TASK);
                }
            }
        });

        getTaskList(mSelectedTab);
    }

    private void addMarkersToMap() {

        mMap.clear();

        if (taskDataLists.size() > 0) {

            for (TaskDataResponse.TaskDataList task : taskDataLists) {

                if (task.getLatitude() != null && task.getLongitude() != null) {

                    if (task.getLatitude() != 0 && task.getLongitude() != 0) {

                        String snippet = task.getTaskId() + ", " + task.getTaskTitle();

                        if (mSelectedTab == 0) {
                            mMap.addMarker(new MarkerOptions()
                                    .draggable(false).snippet(snippet)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_dot))
                                    .position(new LatLng(task.getLatitude(), task.getLongitude())));
                        } else {
                            mMap.addMarker(new MarkerOptions()
                                    .draggable(false).snippet(snippet)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_dot))
                                    .position(new LatLng(task.getLatitude(), task.getLongitude())));
                        }
                    }
                }
            }
        }
    }

    class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View infoView;
        private final TextView tvInfoTitle;
        private final Button btnEditMarker;

        public InfoWindowAdapter() {
            infoView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.layout_infowindow, null, false);
            tvInfoTitle = infoView.findViewById(R.id.tvInfoTitle);
            btnEditMarker = infoView.findViewById(R.id.btnEditMarker);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            List<String> arr = Util.splitStringToArray(",", marker.getSnippet());
            if (!arr.isEmpty())
                tvInfoTitle.setText(arr.get(1)); //at 0th position is task id and 1st pos is task name
            return infoView;
        }
    }
}