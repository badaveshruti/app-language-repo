package qnopy.com.qnopyandroid.ui.locations.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import at.grabner.circleprogress.CircleProgressView;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.FormQueryData;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.LocationProfilePictureDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.NotesImagesSlideShowActivity;
import qnopy.com.qnopyandroid.ui.locations.LocInstructionsActivity;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "LocationAdapter";
    private final String mRollIntoAppId;
    private final String mEventId;
    private final OnLocationActionListener mListener;
    private String userID;
    private String username;
    private String userGuid;
    private String password;
    private Context mContext;
    private List<Location> mLocationList;
    private List<Location> mFilteredLocationList;
    private int margin = 0;
    private LocationFilter lFilter = new LocationFilter();
    private HashMap<String, String> mapLocQuery = new HashMap<>();
    private HashMap<String, FormQueryData> queryData = new HashMap<>();
    private LocationProfilePictureDataSource pictureDataSource;

    public LocationsAdapter(Context context, List<Location> locationList, String rollIntoAppId,
                            String eventId, OnLocationActionListener listener) {
        this.mContext = context;
        this.mLocationList = locationList;
        this.mFilteredLocationList = locationList;
        this.mRollIntoAppId = rollIntoAppId;
        this.mEventId = eventId;
        mListener = listener;
        pictureDataSource
                = new LocationProfilePictureDataSource(mContext);
        initValues();
    }

    private void initValues() {
        userID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);
        username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(mContext, username);
        password = Util.getSharedPreferencesProperty(mContext, GlobalStrings.PASSWORD);
    }

    @Override
    public int getItemCount() {
        return mFilteredLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cvLocation;
        private final View viewHeader;
        public TextView tvLocationName;
        public TextView tvRemainingFieldsCount;
        public ImageButton loc_tag, goto_next, loc_status, btnErase, btnLocInfo;
        public FrameLayout loc_tag_fl;
        public CircleProgressView circleProgressView;
        public ImageView ivFirstProfilePic;
        public ProgressBar pbAttachment;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cvLocation = itemView.findViewById(R.id.cvLocation);
            viewHeader = itemView.findViewById(R.id.viewHeader);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvRemainingFieldsCount = itemView.findViewById(R.id.tvRemainingFieldsCount);
            goto_next = itemView.findViewById(R.id.goto_next);
            loc_status = itemView.findViewById(R.id.loc_status);
            loc_tag = itemView.findViewById(R.id.tag_location);
            loc_tag_fl = itemView.findViewById(R.id.tag_location_fl);
            circleProgressView = itemView.findViewById(R.id.circleProgress);
            btnErase = itemView.findViewById(R.id.loc_erase);
            pbAttachment = itemView.findViewById(R.id.pbAttachment);
            ivFirstProfilePic = itemView.findViewById(R.id.ivFirstProfilePic);
            btnLocInfo = itemView.findViewById(R.id.ib_loc_info);

            btnLocInfo.setBackground(VectorDrawableUtils.getDrawable(mContext,
                    R.drawable.ic_info, R.color.event_start_blue));

            btnLocInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocInstructionsActivity.Companion.startLocInstrActivity(mContext, mFilteredLocationList
                            .get(getAbsoluteAdapterPosition()));
                }
            });

            itemView.setOnClickListener(v ->
                    mListener.onLocationItemClicked(mFilteredLocationList
                            .get(getAbsoluteAdapterPosition())));

            goto_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (queryData.containsKey(mFilteredLocationList
                            .get(getAbsoluteAdapterPosition()).getLocationID())) {
                        FormQueryData data = queryData
                                .get(mFilteredLocationList
                                        .get(getAbsoluteAdapterPosition()).getLocationID());
                        if (data != null && data.getColor() != null
                                && data.getColor().equalsIgnoreCase("red")) {
                            showStatusMessage(data.getStatusMessage());
                        }
                    }
                }
            });
        }
    }

    private void showStatusMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_popup_parameter_hint, null, false);
        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
        TextView tvHint = view.findViewById(R.id.tvParameterHint);
        tvHint.setText(message);
        tvHint.setTypeface(type);
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_location,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LocationsAdapter.ViewHolder viewHolder,
                                 int position) {
        Location loc = mFilteredLocationList.get(viewHolder.getAbsoluteAdapterPosition());

        if (loc.getLocationID().equals("-1")) {
            viewHolder.cvLocation.setVisibility(View.GONE);
            viewHolder.viewHeader.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cvLocation.setVisibility(View.VISIBLE);
            viewHolder.viewHeader.setVisibility(View.GONE);

//            setPercentageValue(viewHolder, loc);

            if (!queryData.containsKey(loc.getLocationID()))
                setFormQueryData(viewHolder, loc);
            else {
                setAlertIconAndMessage(loc.getLocationID(), viewHolder);
            }

            if (Double.parseDouble(loc.getLatitude()) != 0
                    && Double.parseDouble(loc.getLongitude()) != 0) {
                setTagStatusDrawable(1, viewHolder.loc_tag);
            } else {
                setTagStatusDrawable(0, viewHolder.loc_tag);
            }

            ArrayList<LocationProfilePictures> filePathList
                    = pictureDataSource.getAllProfilePictures(loc.getLocationID());

            if (filePathList.size() > 0 ||
                    (loc.getLocInstruction() != null && !loc.getLocInstruction().isEmpty()))
                viewHolder.btnLocInfo.setVisibility(View.VISIBLE);
            else
                viewHolder.btnLocInfo.setVisibility(View.GONE);

            try {
                if (loc.getCocflag() != null && loc.getCocflag().equals("1")) {
                    viewHolder.tvLocationName.setText(loc.getLocationName());
                    viewHolder.tvLocationName.setTextColor(mContext.getResources()
                            .getColor(R.color.color_chooser_orange2));
                } else {
                    viewHolder.tvLocationName.setText(loc.getLocationName());
                    viewHolder.tvLocationName.setTextColor(mContext.getResources()
                            .getColor(R.color.half_black));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Rect delegateArea = new Rect();
            viewHolder.loc_status.getHitRect(delegateArea);
            delegateArea.left += 100;
            delegateArea.bottom += 100;
            delegateArea.top += 100;
            TouchDelegate touchDelegate = new TouchDelegate(delegateArea, viewHolder.loc_status);

            if (viewHolder.loc_status.getParent() instanceof View) {
                ((View) viewHolder.loc_status.getParent()).setTouchDelegate(touchDelegate);
            }

            if (loc.getPercentage() > 0) {
                viewHolder.loc_status.setVisibility(View.VISIBLE);
                viewHolder.loc_status
                        .setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.location_edit_green));//green_dot
            } else {
                viewHolder.loc_status.setVisibility(View.VISIBLE);
                viewHolder.loc_status
                        .setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.location_edit_red));//red_dot
            }

            viewHolder.loc_status.setOnClickListener(v -> showEditLocationBottomSheet(viewHolder, loc));

            viewHolder.loc_tag.setOnClickListener(v -> viewHolder.loc_tag_fl.performClick());

            viewHolder.loc_tag_fl.setOnClickListener(v -> {
                Util.setSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_LOCATIONID,
                        loc.getLocationID());
                Util.setSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_LOCATIONNAME,
                        loc.getLocationName());
                mListener.onTagLocationClicked(loc);
            });

            viewHolder.btnErase.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onEraseLocationClicked(viewHolder, loc.getLocationID());
                }
            });
        }
    }

    private void setAlertIconAndMessage(String locationID, ViewHolder viewHolder) {
        FormQueryData data = queryData.get(locationID);
        if (data != null && data.getColor() != null) {

            if (data.getAlert() != null && !data.getAlert().isEmpty()) {
                viewHolder.goto_next
                        .setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.ic_warning_red));
            } else if ((data.getAlert() == null || data.getAlert().isEmpty())
                    && data.getColor().equalsIgnoreCase("green"))
                viewHolder.goto_next
                        .setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.ic_check_circle));
        }
    }

    private void setFormQueryData(ViewHolder viewHolder, Location loc) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            MobileAppDataSource mobileAppSource = new MobileAppDataSource(mContext);

            if (!mapLocQuery.containsKey(loc.getLocationID())) {

                List<MobileApp> childAppList = mobileAppSource.getChildApps(Integer.parseInt(mRollIntoAppId),
                        loc.getSiteID(), loc.getLocationID());

                Collection<MobileApp> list = Collections2.filter(childAppList, app -> app.getFormQuery() != null
                        && !app.getFormQuery().isEmpty());
                ArrayList<MobileApp> apps = Lists.newArrayList(list);

                if (apps.size() > 0)
                    mapLocQuery.put(loc.getLocationID(), apps.get(0).getFormQuery());
            }

            if (mapLocQuery.containsKey(loc.getLocationID())) {
                String query = mapLocQuery.get(loc.getLocationID());

                if (isExpressionQueryValid(query)) {
                    query = replaceQueryCols(query, loc.getLocationID());
                    queryData = new FieldDataSource(mContext).hitFormQuery(query);

                    if (queryData.containsKey(loc.getLocationID())) {
                        handler.post(() -> {
                            setAlertIconAndMessage(loc.getLocationID(), viewHolder);
                        });
                    }
                }
            }
        });
    }

    private boolean isExpressionQueryValid(String expression) {
        expression = expression.toLowerCase();

        return !expression.contains("insert") || !expression.contains("update")
                || !expression.contains("delete") || !expression.contains("truncate")
                || !expression.contains("drop");
    }

    public String replaceQueryCols(String query, String siteId) {

        HashMap<String, String> mapCols = new HashMap<>();
        mapCols.put("d_field_data", "d_FieldData");
        mapCols.put("s_location", "s_Location");
        mapCols.put("string_value", "StringValue");
        mapCols.put("field_parameter_id", "FieldParameterID");
        mapCols.put("violation_flag", "violationFlag");
        mapCols.put("location_id", "LocationID");
        mapCols.put("event_id", "EventID");
        mapCols.put("location_type", "LocationType");
        mapCols.put("cu_project_id", siteId);
        mapCols.put("site_id", "SiteID");
        mapCols.put("cu_eve_id", mEventId);
        mapCols.put("true", "1");
        mapCols.put("false", "0");

        for (Map.Entry<String, String> entry : mapCols.entrySet()) {
            query = query.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        return query;
    }

    private void showEditLocationBottomSheet(ViewHolder viewHolder, Location loc) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        bottomSheetDialog.setContentView(R.layout.layout_location_name_change);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BottomSheetDialog d = (BottomSheetDialog) bottomSheetDialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior
                                = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });

        TextView tvLocationChangeTitle, tvFieldPointTitle;
        final EditText edtEnterFieldPointName;
        Button btnSave, btnCancel;

        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");

        tvLocationChangeTitle = bottomSheetDialog.findViewById(R.id.textViewLocationNameChangeTitle);
        tvFieldPointTitle = bottomSheetDialog.findViewById(R.id.textViewFieldPointName);
        edtEnterFieldPointName = bottomSheetDialog.findViewById(R.id.editTextEnterFieldPointName);
        btnSave = bottomSheetDialog.findViewById(R.id.buttonSave);
        btnCancel = bottomSheetDialog.findViewById(R.id.buttonCancel);

        try {
            tvLocationChangeTitle.setTypeface(type);
            tvFieldPointTitle.setTypeface(type);
            edtEnterFieldPointName.setTypeface(type);
            btnSave.setTypeface(type);
            btnCancel.setTypeface(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        edtEnterFieldPointName.setText(loc.getLocationName());

        btnSave.setOnClickListener(v -> {
            String updatedLocationName = edtEnterFieldPointName.getText().toString();

            if (TextUtils.isEmpty(updatedLocationName)) {
                edtEnterFieldPointName.setError(mContext.getString(R.string.enter_field_point_name));
                edtEnterFieldPointName.setText(loc.getLocationName());
                return;
            }

            LocationDataSource locationDataSource = new LocationDataSource(mContext);

            if (locationDataSource.islocationAlreadyExists(updatedLocationName,
                    loc.getSiteID())) {
                Toast.makeText(mContext,
                        mContext.getString(R.string.you_have_location_with_same_name),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (loc.getCocflag() != null && loc.getCocflag().equals("1")) {
                viewHolder.tvLocationName.setText(updatedLocationName);
                viewHolder.tvLocationName.setTextColor(mContext.getResources()
                        .getColor(R.color.color_chooser_orange2));
            } else {
                viewHolder.tvLocationName.setText(updatedLocationName);
                viewHolder.tvLocationName.setTextColor(mContext.getResources()
                        .getColor(R.color.half_black));
            }

            locationDataSource.updateFieldPointName(loc.getLocationID(),
                    updatedLocationName, loc.getLatitude(),
                    loc.getLongitude());

            ArrayList<NewClientLocation> arrayListUpdatedLocationName
                    = locationDataSource.getUpdatedLocationNames(loc.getLocationID());

            if (CheckNetwork.isInternetAvailable(mContext)) {
                syncUpdatedLocationNameToServer(arrayListUpdatedLocationName);
            }

            SharedPreferences prefs = mContext.getSharedPreferences("Event_AppId",
                    MODE_PRIVATE);
            int eventId = prefs.getInt("EventId", 0);
            int appId = prefs.getInt("AppId", 0);

            Intent intent = new Intent(mContext, LocationActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("APP_ID", appId);
            mContext.startActivity(intent);
//                mContext.finish();
            bottomSheetDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void syncUpdatedLocationNameToServer(ArrayList<NewClientLocation>
                                                         arrayListUpdatedLocationName) {
        new AddLocationTask(arrayListUpdatedLocationName).execute();
    }

    private class AddLocationTask extends AsyncTask<MediaType, Void, Object> {
        AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(mContext);

        ArrayList<NewClientLocation> ArrayListUpdatedLocationName;

        public AddLocationTask(ArrayList<NewClientLocation> arrayListUpdatedLocationName) {
            ArrayListUpdatedLocationName = arrayListUpdatedLocationName;
        }

        @Override
        protected void onPreExecute() {

            Log.d(TAG, " onPreExecute: Populating the request objects");
            // Init the progress dialog
        }// end of onPreExecute

        @Override
        protected Object doInBackground(MediaType... params) {

            NewLocationResponseModel respModel = null;

            try {
                respModel = mAquaBlueService.v1_setAddLocationData(mContext.getResources().getString(R.string.prod_base_uri),
                        mContext.getResources().getString(R.string.prod_add_new_location),
                        ArrayListUpdatedLocationName, userGuid);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("locationName", "Error=" + e.getLocalizedMessage());
            }

            return respModel;
        }// end of doInBackground

        @Override
        protected void onPostExecute(Object result) {
            Log.e("locationName", " onPostExecute: Result = " + result);

            if (result != null) {
                NewLocationResponseModel respModel = (NewLocationResponseModel) result;

                if (respModel.isSuccess()) {
                    LocationDataSource loc = new LocationDataSource(mContext);
                    Log.e("locationName", " onPostExecute: Result = "
                            + respModel.getData().get(0).getLocationId());
                    loc.updateSyncFlag(respModel.getData().get(0).getLocationId());

                    //Toast.makeText(mContext, "Field point name updated successfully!", Toast.LENGTH_LONG).show();
                    //finish();

                } else {
                    GlobalStrings.responseMessage = respModel.getMessage();
                    HttpStatus respCode = respModel.getResponseCode();
                    if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                        Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                        // finish();
                    } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if (result.equals(HttpStatus.FAILED_DEPENDENCY.toString())) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if ((respCode == HttpStatus.EXPECTATION_FAILED) ||
                            (respCode == HttpStatus.UNAUTHORIZED) ||
                            (respCode == HttpStatus.CONFLICT)
                    ) {
                        Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                    }
                }
            }
        }// end of onPostExecute
    }// end of PostMessageTask

    private void setPercentageValue(ViewHolder viewHolder, Location loc) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        AtomicReference<Float> percentage = new AtomicReference<>((float) 0);

        executor.execute(() -> {
            FieldDataSource fieldDataSource = new FieldDataSource(mContext);
            percentage.set(fieldDataSource.getTotalPercentageFilledOfForms(loc.getSiteID() + "",
                    mRollIntoAppId, loc.getLocationID(), mEventId));

            if (percentage.get() >= 100)
                percentage.set(100f);

            handler.post(() -> {
                viewHolder.circleProgressView.setValue(percentage.get());

                if (percentage.get() == 0) {
                    viewHolder.circleProgressView.setVisibility(View.GONE);
                    viewHolder.btnErase.setVisibility(View.GONE);
                } else {
                    viewHolder.circleProgressView.setVisibility(View.VISIBLE);
                    viewHolder.btnErase.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void setTagStatusDrawable(int status, ImageButton img) {
        if (status == 0) {
            img.setBackgroundResource(R.drawable.ic_no_loc_marker);
        } else {
            img.setBackgroundResource(R.drawable.ic_loc_marker);
        }
    }

    @Override
    public Filter getFilter() {
        if (lFilter == null) {
            lFilter = new LocationFilter();
        }
        return lFilter;
    }

    private class LocationFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = mLocationList;
                results.count = mLocationList.size();
                Log.i("LocationFilter", "No search text,return search count:" + results.count);

            } else {
                String filterString = constraint.toString().toLowerCase();
                Log.i("locationAdapter", "SearchText:" + filterString);

                String filterableString;
                ArrayList<Location> filtered = new ArrayList<Location>();
                List<Location> traverseList = new ArrayList<>();
                traverseList.addAll(mLocationList);

                for (Location loc : traverseList) {
                    if (!loc.getLocationID().equals("-1")) {
                        filterableString = loc.getLocationName();
                        if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                            filtered.add(loc);
                        }
                    }
                }
                results.values = filtered;
                results.count = filtered.size();

                Log.i("locationsAdapter", "Searched Text >Return Search Count:" + results.count);
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                mFilteredLocationList = new ArrayList<Location>();
            } else {
                mFilteredLocationList = (ArrayList<Location>) results.values;
            }
            notifyDataSetChanged();
        }
    }

    public interface OnLocationActionListener {
        void onLocationItemClicked(Location location);

        void onEraseLocationClicked(LocationsAdapter.ViewHolder viewHolder, String locId);

        void onTagLocationClicked(Location location);
    }
}
