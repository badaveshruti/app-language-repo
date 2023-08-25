package qnopy.com.qnopyandroid.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.grabner.circleprogress.CircleProgressView;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.LocationProfilePictureDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.NotesImagesSlideShowActivity;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

import static android.content.Context.MODE_PRIVATE;

public class LocationAdapter extends ArrayAdapter<Location> implements Filterable {

    private final String mRollIntoAppId;
    private final String mEventId;
    List<Location> locationObjects;
    List<Location> filteredlocationObjects;
    private final LocationActivity objContext;
    FieldDataSource fSource;
    LocationFilter lFilter = new LocationFilter();
    int currentUserID;
    String TAG = "locationAdapter";
    private int margin = 0;

    String password, userID, companyID, username;
    String userGuid = null;
    String deviceID;
    private OnEraseLocationListener mListener;
    private boolean showRemainingFields = false;

    public LocationAdapter(LocationActivity context, int resource,
                           int textViewResourceId, List<Location> objects, String rollIntoAppId,
                           String eventId, OnEraseLocationListener listener) {
        super(context, resource, textViewResourceId, objects);
        this.objContext = context;
        locationObjects = new ArrayList<Location>();
        this.locationObjects = objects;
        fSource = new FieldDataSource(context);
        this.filteredlocationObjects = objects;
        this.mRollIntoAppId = rollIntoAppId;
        this.mEventId = eventId;
        mListener = listener;
        // objContext = context

        currentUserID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID));
        // Log.i(TAG, "locationAdapter() Total locations (" + objects.size() + ") to show/render.");

        userID = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID);
        username = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(objContext, username);
        password = Util.getSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD);
        companyID = Util.getSharedPreferencesProperty(objContext, GlobalStrings.COMPANYID);
        deviceID = DeviceInfo.getDeviceID(objContext);
    }

    @Override
    public int getCount() {
        return filteredlocationObjects.size();
    }

    public List<Location> getFilteredList() {
        return filteredlocationObjects;
    }

    public void setSortedList(List<Location> objcts) {
        filteredlocationObjects = objcts;
        notifyDataSetChanged();
    }

    @Override
    public Location getItem(int position) {
        return filteredlocationObjects.get(position);
    }

    public void updateMandatoryAlert() {
        showRemainingFields = true;
    }

    public static class ViewHolder {
        public TextView tvLocationName;
        public TextView tvRemainingFieldsCount;
        public Location locObj;

        public ImageButton loc_tag, goto_next, loc_status, btnErase;
        public FrameLayout loc_tag_fl;
        public CircleProgressView circleProgressView;
        public RelativeLayout layoutLocationProfile;
        public ImageView ivFirstProfilePic;
        public ProgressBar pbAttachment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        Location loc = new Location();
        loc = filteredlocationObjects.get(position);
        final ViewHolder viewHolder;
        //Location

/*
        Log.i(TAG, "getView() Location Item at position=" + position + " LocationName=" + loc.getLocationName() + "" +
                ",LocationId=" + loc.getLocationID() + ",Location Header=" + loc.getLocationDesc() + ",Location Instruction=" + loc.getExtField2());
*/

        if (rowView == null) {

            LayoutInflater inflater = objContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.adapter_location, null);

            viewHolder = new ViewHolder();
            viewHolder.tvLocationName = (TextView) rowView.findViewById(R.id.tvLocationName);
            viewHolder.tvRemainingFieldsCount = (TextView) rowView.findViewById(R.id.tvRemainingFieldsCount);
            viewHolder.goto_next = (ImageButton) rowView.findViewById(R.id.goto_next);
            viewHolder.loc_status = (ImageButton) rowView.findViewById(R.id.loc_status);
            viewHolder.loc_tag = (ImageButton) rowView.findViewById(R.id.tag_location);
            viewHolder.loc_tag_fl = (FrameLayout) rowView.findViewById(R.id.tag_location_fl);
            viewHolder.circleProgressView = rowView.findViewById(R.id.circleProgress);
            viewHolder.btnErase = rowView.findViewById(R.id.loc_erase);
            viewHolder.layoutLocationProfile = rowView.findViewById(R.id.layoutLocationProfile);
            viewHolder.pbAttachment = rowView.findViewById(R.id.pbAttachment);

            viewHolder.ivFirstProfilePic = rowView.findViewById(R.id.ivFirstProfilePic);
//            viewHolder.imageHolder = (LinearLayout) rowView.findViewById(R.id.dataImage);
//            viewHolder.textv2 = (TextView) rowView.findViewById(R.id.detailText);
            //   viewHolder.form_status = (TextView) rowView.findViewById(R.id.f_status);
            viewHolder.locObj = loc;

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        LocationProfilePictureDataSource pictureDataSource = new LocationProfilePictureDataSource(objContext);
        ArrayList<LocationProfilePictures> filePathList
                = pictureDataSource.getAllProfilePictures(loc.getLocationID());

        //we'll need to clear all child view i.e. stacked images from layout and margin to 0
        //else it'll create new images and increase margin
        viewHolder.layoutLocationProfile.removeAllViews();
        margin = 0;
        if (filePathList.size() > 0) {
            setImageStack(filePathList, viewHolder);
            viewHolder.layoutLocationProfile.setVisibility(View.VISIBLE);
        } else {
            viewHolder.layoutLocationProfile.setVisibility(View.GONE);
        }

//        rowView.setClickable(true);
        FieldDataSource fieldDataSource = new FieldDataSource(objContext);
        float percentage = fieldDataSource.getTotalPercentageFilledOfForms(loc.getSiteID() + "",
                mRollIntoAppId, loc.getLocationID(), mEventId);

        if (percentage >= 100)
            percentage = 100;

        viewHolder.circleProgressView.setValue(percentage);

        if (percentage == 0) {
            viewHolder.circleProgressView.setVisibility(View.GONE);
            viewHolder.btnErase.setVisibility(View.GONE);
        } else {
            viewHolder.circleProgressView.setVisibility(View.VISIBLE);
            viewHolder.btnErase.setVisibility(View.VISIBLE);
        }

        if (Double.parseDouble(loc.getLatitude()) != 0 && Double.parseDouble(loc.getLongitude()) != 0) {
            setTagStatusDrawable(1, viewHolder.loc_tag);
        } else {
            setTagStatusDrawable(0, viewHolder.loc_tag);
        }

        try {
//            int userid= Integer.parseInt(Util.getSharedPreferencesProperty(ObjContext, GlobalStrings.USERID));
            if (loc.getCocflag() != null && loc.getCocflag().equals("1")) {
                viewHolder.tvLocationName.setText(loc.getLocationName());
                viewHolder.tvLocationName.setTextColor(getContext().getResources().getColor(R.color.color_chooser_orange2));
            } else {
                viewHolder.tvLocationName.setText(loc.getLocationName());
                viewHolder.tvLocationName.setTextColor(getContext().getResources().getColor(R.color.half_black));
            }

            // viewHolder.textv.setText(loc.getLocationName());
//
//            if (viewHolder.textv2 != null) {
//                viewHolder.textv2.setText(loc.getExtField2());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.locObj = loc;


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
                    .setBackground(getContext().getResources().getDrawable(R.drawable.location_edit_green));//green_dot
        } else {
            viewHolder.loc_status.setVisibility(View.VISIBLE);
            viewHolder.loc_status
                    .setBackground(getContext().getResources().getDrawable(R.drawable.location_edit_red));//red_dot
        }

        final Location finalLoc1 = loc;
        viewHolder.loc_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(objContext, R.style.DialogStyle);
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
                                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        }, 0);
                    }
                });

                TextView TextViewLocationChangeTitle, TextViewFieldPointTitle;
                final EditText EditTextEnterFieldPointName;
                Button ButtonSave, ButtonCancel;

                Typeface type = Typeface.createFromAsset(objContext.getAssets(), "fonts/Roboto-Regular.ttf");

                TextViewLocationChangeTitle = bottomSheetDialog.findViewById(R.id.textViewLocationNameChangeTitle);
                TextViewFieldPointTitle = bottomSheetDialog.findViewById(R.id.textViewFieldPointName);
                EditTextEnterFieldPointName = bottomSheetDialog.findViewById(R.id.editTextEnterFieldPointName);
                ButtonSave = bottomSheetDialog.findViewById(R.id.buttonSave);
                ButtonCancel = bottomSheetDialog.findViewById(R.id.buttonCancel);

                TextViewLocationChangeTitle.setTypeface(type);
                TextViewFieldPointTitle.setTypeface(type);
                EditTextEnterFieldPointName.setTypeface(type);
                ButtonSave.setTypeface(type);
                ButtonCancel.setTypeface(type);

                EditTextEnterFieldPointName.setText(finalLoc1.getLocationName());

                ButtonSave.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updatedLocationName = EditTextEnterFieldPointName.getText().toString();

                        if (TextUtils.isEmpty(updatedLocationName)) {
                            EditTextEnterFieldPointName.setError(objContext.getString(R.string.enter_field_point_name));
                            EditTextEnterFieldPointName.setText(finalLoc1.getLocationName());
                            return;
                        }

                        LocationDataSource locationDataSource = new LocationDataSource(objContext);

                        if (locationDataSource.islocationAlreadyExists(updatedLocationName,
                                finalLoc1.getSiteID())) {
                            Toast.makeText(objContext,
                                    objContext.getString(R.string.you_have_location_with_same_name),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (finalLoc1.getCocflag() != null && finalLoc1.getCocflag().equals("1")) {
                            viewHolder.tvLocationName.setText(updatedLocationName);
                            viewHolder.tvLocationName.setTextColor(getContext().getResources()
                                    .getColor(R.color.color_chooser_orange2));
                        } else {
                            viewHolder.tvLocationName.setText(updatedLocationName);
                            viewHolder.tvLocationName.setTextColor(getContext().getResources().getColor(R.color.half_black));
                        }

                        locationDataSource.updateFieldPointName(finalLoc1.getLocationID(),
                                updatedLocationName, finalLoc1.getLatitude(),
                                finalLoc1.getLongitude());

                        ArrayList<NewClientLocation> arrayListUpdatedLocationName = locationDataSource.getUpdatedLocationNames(finalLoc1.getLocationID());

                        if (CheckNetwork.isInternetAvailable(objContext)) {
                            syncUpdatedLocationNameToServer(arrayListUpdatedLocationName);
                        }

                        SharedPreferences prefs = objContext.getSharedPreferences("Event_AppId", MODE_PRIVATE);
                        int eventId = prefs.getInt("EventId", 0);
                        int appId = prefs.getInt("AppId", 0);

                        Intent intent = new Intent(objContext, LocationActivity.class);
                        intent.putExtra("EVENT_ID", eventId);
                        intent.putExtra("APP_ID", appId);
                        getContext().startActivity(intent);
                        objContext.finish();
                        bottomSheetDialog.dismiss();
                    }
                });

                ButtonCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });
//        else {
//            viewHolder.loc_status.setVisibility(View.GONE);
//        }

        final Location finalLoc = loc;
        OnClickListener LocTagListener = new OnClickListener() {
            public void onClick(View v) {
                //put your desired action here
                Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_LOCATIONID, finalLoc.getLocationID());
                Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_LOCATIONNAME, finalLoc.getLocationName());
                Log.i(TAG, "Clicked on TAG Location:" + finalLoc.getLocationName() + ",LocationID=" + finalLoc.getLocationID()
                        + ",Location Header=" + finalLoc.getLocationDesc() + ",Lat=" + finalLoc.getLatitude() + ",longitude=" + finalLoc.getLongitude());

                objContext.onclickTagLocation(finalLoc.getLocationID(), finalLoc.getLatitude(), finalLoc.getLongitude(), finalLoc.getLocationName());
            }
        };

        viewHolder.loc_tag.setOnClickListener(LocTagListener);
        viewHolder.loc_tag_fl.setOnClickListener(LocTagListener);

        viewHolder.btnErase.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onEraseLocationClicked(viewHolder, finalLoc.getLocationID());
            }
        });

//        rowView.setOnClickListener(ClickListener);
        return rowView;
    }

    public final int MAX_IMAGE_SHOW_COUNT = 9;

    private void setImageStack(ArrayList<LocationProfilePictures> filePathList,
                               ViewHolder viewHolder) {

        ArrayList<GridImageItem> imagesList = new ArrayList<>();

        for (int i = 0; i < filePathList.size(); i++) {
            LocationProfilePictures path = filePathList.get(i);
            String attachmentUrl = "";
            String thumbUrl = "";

            //checking if attachment id is negative then it is local attachment else from server
            if (path.getAttachmentId() > 0) {
                thumbUrl = attachmentUrl = objContext.getString(R.string.prod_base_uri)
                        + "download" + path.getAttachmentURL();
            } else {
                thumbUrl = attachmentUrl = path.getAttachmentURL();
            }

            //thumb url will be used to show images in stack images and attachmentUrl will download original image
            if (i <= MAX_IMAGE_SHOW_COUNT)
                addImageStack(thumbUrl, viewHolder, i);
            else
                addImageStack(thumbUrl, viewHolder, filePathList.size());

            imagesList.add(new GridImageItem(attachmentUrl));
        }

        viewHolder.layoutLocationProfile.setOnClickListener(v -> {
            Intent intent = new Intent(objContext, NotesImagesSlideShowActivity.class);
            intent.putExtra(GlobalStrings.PATH_LIST, imagesList);
            intent.putExtra(GlobalStrings.POSITION, 0);
            objContext.startActivity(intent);
        });
    }

    private void addImageStack(String filePath, ViewHolder viewHolder, int pos) {

        //showing 10 images only and later part will be blank image with further image count
        if (pos <= MAX_IMAGE_SHOW_COUNT) {

            //below is left margin for each imageView in layout to overlap each other like stack
            if (margin == 0) {
                margin = 50;
            } else {
                margin += 20;
            }

            RelativeLayout.LayoutParams lp = new RelativeLayout
                    .LayoutParams(Util.dpToPx(33),
                    Util.dpToPx(33));
            lp.setMargins(Util.dpToPx(margin), 0, 0, 0);
            CircularImageView circularImageView = new CircularImageView(objContext);
            circularImageView.setLayoutParams(lp);
            circularImageView.setBorderWidth(Util.dpToPx(1));
            circularImageView.setBorderColor(ContextCompat.getColor(objContext,
                    R.color.light_grey));
            loadImages(filePath, circularImageView);
            viewHolder.layoutLocationProfile.addView(circularImageView);
        } else {
            margin += 20;

            RelativeLayout.LayoutParams lp = new RelativeLayout
                    .LayoutParams(Util.dpToPx(34),
                    Util.dpToPx(34));
            lp.setMargins(Util.dpToPx(margin), 0, 0, 0);
//                lp.addRule(RelativeLayout.CENTER_VERTICAL);
            CustomTextView circularTextView = new CustomTextView(objContext);
            circularTextView.setLayoutParams(lp);
            circularTextView.setBackground(ContextCompat.getDrawable(objContext,
                    R.drawable.circle_grey_bg));
            circularTextView.setGravity(Gravity.CENTER);
            circularTextView.setTextColor(ContextCompat.getColor(objContext,
                    R.color.white));
            circularTextView.setTextSize(Util.dpToPx(5));
            String imageCount = "+" + (pos - MAX_IMAGE_SHOW_COUNT);
            circularTextView.setText(imageCount);
            viewHolder.layoutLocationProfile.addView(circularTextView);
        }
    }

    private void loadImages(String filePath, CircularImageView circularImageView) {

        final String uID = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID);
        final DeviceInfoModel ob = DeviceInfo.getDeviceInfo(objContext);

        File file = new File(filePath);

        if (file.exists()) {
            Picasso.get().load(file)
                    .into(circularImageView);
//            holder.pbAttachment.setVisibility(View.GONE);
        } else {

            Glide.with(objContext).asGif().load(
                    R.drawable.loader)
                    .into(circularImageView);

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("user_guid", ob.getUser_guid());
            client.addHeader("device_id", ob.getDeviceId());
            client.addHeader("user_id", uID);
            client.addHeader("ratio", "original");
            client.addHeader("Content-Type", "application/octet-stream");

            try {
                client.post(filePath, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        try {
                            Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0,
                                    responseBody.length);

                            Glide.with(objContext).asBitmap().load(image)
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(circularImageView);
//                            holder.pbAttachment.setVisibility(View.GONE);
                        } catch (IllegalArgumentException arg) {
                            arg.printStackTrace();
//                            holder.pbAttachment.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.getMessage());
//                        holder.pbAttachment.setVisibility(View.GONE);
                    }
                });
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
        }
    }

    private void syncUpdatedLocationNameToServer(ArrayList<NewClientLocation> arrayListUpdatedLocationName) {
        new PostAddLocationTask(arrayListUpdatedLocationName).execute();
    }

    private class PostAddLocationTask extends AsyncTask<MediaType, Void, Object> {
        AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(objContext);

        ArrayList<NewClientLocation> ArrayListUpdatedLocationName;

        public PostAddLocationTask(ArrayList<NewClientLocation> arrayListUpdatedLocationName) {
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
                respModel = mAquaBlueService.v1_setAddLocationData(objContext.getResources().getString(R.string.prod_base_uri),
                        objContext.getResources().getString(R.string.prod_add_new_location), ArrayListUpdatedLocationName, userGuid);
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
                    LocationDataSource loc = new LocationDataSource(objContext);
                    Log.e("locationName", " onPostExecute: Result = " + respModel.getData().get(0).getLocationId());
                    loc.updateSyncFlag(respModel.getData().get(0).getLocationId());

                    //Toast.makeText(ObjContext, "Field point name updated successfully!", Toast.LENGTH_LONG).show();
                    //finish();

                } else {
                    GlobalStrings.responseMessage = respModel.getMessage();
                    HttpStatus respCode = respModel.getResponseCode();
                    if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                        Util.setDeviceNOT_ACTIVATED((Activity) objContext, username, password);
                        // finish();
                    } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if (result.equals(HttpStatus.FAILED_DEPENDENCY.toString())) {
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if ((respCode == HttpStatus.EXPECTATION_FAILED) ||
                            (respCode == HttpStatus.UNAUTHORIZED) ||
                            (respCode == HttpStatus.CONFLICT)
                    ) {
                        Util.setDeviceNOT_ACTIVATED((Activity) objContext, username, password);
                    }
                }
            }
        }// end of onPostExecute
    }// end of PostMessageTask

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
                results.values = locationObjects;
                results.count = locationObjects.size();
                Log.i("LocationFilter", "No search text,return search count:" + results.count);

            } else {
                String filterString = constraint.toString().toLowerCase();
                Log.i("locationAdapter", "SearchText:" + filterString);

                String filterableString;
                ArrayList<Location> filtered = new ArrayList<Location>();
                List<Location> traverseList = new ArrayList<>();
                traverseList.addAll(locationObjects);

                for (Location loc : traverseList) {
                    filterableString = loc.getLocationName();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(loc);
                    }

                }
                results.values = filtered;
                results.count = filtered.size();

                Log.i("locationAdapter", "Searched Text >Return Search Count:" + results.count);

            }

            //final ArrayList<String> nlist = new ArrayList<String>(count);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredlocationObjects = new ArrayList<Location>();
                notifyDataSetInvalidated();
            } else {
                filteredlocationObjects = (ArrayList<Location>) results.values;
                Log.i("locationAdapter", "FilteredLocations:" + filteredlocationObjects);
//                filteredlocationObjects = ObjContext.getSortedList(filteredlocationObjects);
                notifyDataSetChanged();
            }
        }
    }

    public interface OnEraseLocationListener {
        void onEraseLocationClicked(ViewHolder viewHolder, String locId);
    }
}

