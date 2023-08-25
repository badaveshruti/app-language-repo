package qnopy.com.qnopyandroid.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CompletionPercentageDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.gps.GPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.signature.CaptureSignature;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportActivity;
import qnopy.com.qnopyandroid.ui.activity.RequiredFieldsListActivity;
import qnopy.com.qnopyandroid.ui.events.SubmittalsFragment;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

import static android.app.Activity.RESULT_OK;

public class MyDashboardRecyclerViewAdapter extends RecyclerView.Adapter<MyDashboardRecyclerViewAdapter.ViewHolder> implements Filterable, OnTaskCompleted {

    private Context context;
    private List<EventData> items;
    private List<EventData> filtered_items;

    private String TAG = "MyDashboardRecyclerViewAdapter";
    private SubmittalsFragment.OnListFragmentInteractionListener mListener;
    //DashboardActivity mcontext;
    private CardFilter cardFilter = new CardFilter();
    private int mSelectedTab;
    private String username = null;
    private String userGuid = null;
    private String password = null;
    private String deviceID;
    private int userId;
    private boolean closeEvent = false;
    private String savedFilePath;
    public static final int CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE = 104;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    private Fragment mFragment;
    private int mAppid, mEventId, mSiteId;
    private String mSiteName;

    private OnEventClosedButtonClickListner mOnEventClosedButtonClickListner;

    MyDashboardRecyclerViewAdapter(Context context, List<EventData> dashboardlist,
                                   SubmittalsFragment.OnListFragmentInteractionListener listener, int selectedTab, SubmittalsFragment submittalsFragment) {

        items = new ArrayList<>();
        this.items = dashboardlist;
        this.filtered_items = dashboardlist;
        this.context = context;
        mListener = listener;
        mSelectedTab = selectedTab;

        mFragment = submittalsFragment;

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userId = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        deviceID = DeviceInfo.getDeviceID(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_gridlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final EventData item = filtered_items.get(position);
        holder.currentItem = item;

        int event_status = item.getStatus();
        if (event_status == 2) {
            holder.activelinearlayout.setBackgroundResource(R.mipmap.conflict_event_bg);
        } else if (event_status == 0) {
            holder.activelinearlayout.setBackgroundResource(R.mipmap.close_event_bg);
        } else {
            holder.activelinearlayout.setBackgroundResource(R.mipmap.active_event_bg);
        }

        switch (item.getMobAppID()) {

            case 659:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.groundwater_monitoring_black));
                break;
            case 665:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.health_safty_black));
                break;
            case 666:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.log_black));
                break;
            case 669:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.soil_loging_black));
                break;
            case 690:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.construction_monitoring_black));
                break;
            case 694:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.photo_log));
                break;
            case 695:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.vapor_sampling_black));
                break;
            case 711:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.drawable.phase_black));
                break;

            default:
                holder.imageform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_default_icon));
                break;
        }

        holder.viewSite.setText(item.getSiteName());

        if (item.getEventName() != null && item.getEventName().length() > 0) {
            holder.viewForm.setText(item.getEventName());

        } else {
            holder.viewForm.setText(item.getMobAppName());
        }
        String start = "-", end = "-";

        if (item.getStartDate() != 0) {
            // start = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(item.getStartDate());
            start = Util.getMMddyyyyFromMilliSeconds(String.valueOf(item.getStartDate()));
        }

        if (item.getEndDate() != 0) {
            end = Util.getMMddyyyyFromMilliSeconds(String.valueOf(item.getEndDate()));
        }

        if (start != null) {
            holder.viewStartDate.setText(start);
        }

        if (end != null) {
            holder.viewEndDate.setText(end);
        }
        holder.viewLocationCount.setText(item.getLocationCount() + "");
    }

    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public Filter getFilter() {
        if (cardFilter == null) {
            cardFilter = new CardFilter();
        }
        return cardFilter;
    }

    @Override
    public void onTaskCompleted(Object obj) {

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

    private class CardFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i("SubmittalsAdapter", "Search text:" + filterString);

            if (constraint.length() == 0) {
                filtered_items = items;
                results.values = filtered_items;
                results.count = filtered_items.size();
                Log.i(TAG, "No Search Text,Return search count:" + results.count);

            } else {

                String matchingSite, matchingForm;
                ArrayList<EventData> filtered = new ArrayList<EventData>();

                for (EventData item : items) {
                    matchingSite = item.getSiteName();
                    matchingForm = item.getMobAppName();
                    if ((matchingSite.toLowerCase(Locale.getDefault()).contains(filterString)) ||
                            (matchingForm.toLowerCase(Locale.getDefault()).contains(filterString))) {
                        filtered.add(item);
                    }
                }

                results.values = filtered;
                results.count = filtered.size();

                Log.i(TAG, "Searched Text >Return Search Count:" + results.count);

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filtered_items = new ArrayList<EventData>();
            } else {
                filtered_items = (ArrayList<EventData>) results.values;
                Log.i(TAG, "Filtered_Items:" + filtered_items);
            }

            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView viewSite;
        public TextView viewStartDate;
        public TextView viewEndDate;
        public TextView viewForm;
        public TextView viewLocationCount;
        public LinearLayout activelinearlayout;
        public LinearLayout closedlinearlayout;
        public LinearLayout conflictlinearlayout;
        public ImageView view_report;
        public ImageView download_data;
        public ImageView imageform;
        public RelativeLayout view_report_rl, download_report_rl;
        ImageView imageViewCloseEvent;
        public EventData currentItem;

        public ViewHolder(final View itemView) {
            super(itemView);

            mView = itemView;

            viewSite = (TextView) itemView.findViewById(R.id.sitenametv);
            viewStartDate = (TextView) itemView.findViewById(R.id.startdatetv);
            viewEndDate = (TextView) itemView.findViewById(R.id.enddatetv);
            viewForm = (TextView) itemView.findViewById(R.id.formtv);
            viewForm.setSelected(true);
            viewLocationCount = (TextView) itemView.findViewById(R.id.loc_count_tv);
            activelinearlayout = itemView.findViewById(R.id.activelinearlayout);
            view_report = itemView.findViewById(R.id.view_report_iv);
            view_report_rl = itemView.findViewById(R.id.view_report_rl);
            download_data = itemView.findViewById(R.id.download_data_iv);
            download_report_rl = itemView.findViewById(R.id.download_data_rl);
            imageform = itemView.findViewById(R.id.imageform);

            imageViewCloseEvent = itemView.findViewById(R.id.imageViewCloseEvent);

            if (mSelectedTab == 0 || mSelectedTab == 2) {
                imageViewCloseEvent.setVisibility(View.VISIBLE);
            }

            imageViewCloseEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "click on card:- "+(getAdapterPosition()+1), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Submit and End onClick() Start time:" + System.currentTimeMillis());


                    if (CheckNetwork.isInternetAvailable(context)) {
                        mAppid = currentItem.getMobAppID();
                        mEventId = currentItem.getEventID();
                        mSiteId = currentItem.getSiteID();
                        mSiteName = currentItem.getSiteName();

                        mOnEventClosedButtonClickListner.onEventClosedButtonClick(v, getAdapterPosition(), mAppid, mEventId, mSiteId, mSiteName);

                        /*FieldDataSource fieldDataSource = new FieldDataSource(context);

                        ArrayList<required_field_row_item> reqDataList = fieldDataSource.getMandatoryFieldList(currentItem.getMobAppID() + "", currentItem.getEventID() + "", currentItem.getSiteID() + "");

                        if (reqDataList != null && reqDataList.size() > 0 && reqDataList.get(0).getCount() > 0) {
                            RequiredDataInFormAlert(currentItem.getMobAppID(), currentItem.getEventID(), currentItem.getSiteID(), currentItem.getSiteName());
                        } else {
                            closeEventAlert(currentItem.getMobAppID(), currentItem.getEventID(), currentItem.getSiteID(), currentItem.getSiteName());
                        }*/

                    } else {
                        Toast.makeText(context, context.getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                    }
                    Log.i(TAG, "Submit and End onClick() End time:" + System.currentTimeMillis());
                }
            });

            view_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, MobileReportActivity.class);
                    i.putExtra("SITE_NAME", currentItem.getSiteName());
                    i.putExtra("SITE_ID", currentItem.getSiteID() + "");
                    i.putExtra("EVENT_ID", currentItem.getEventID() + "");
                    i.putExtra("APP_NAME", currentItem.getMobAppName());
                    context.startActivity(i);
                }
            });

            view_report_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, MobileReportActivity.class);
                    i.putExtra("SITE_NAME", currentItem.getSiteName());
                    i.putExtra("SITE_ID", currentItem.getSiteID() + "");
                    i.putExtra("EVENT_ID", currentItem.getEventID() + "");
                    i.putExtra("APP_NAME", currentItem.getMobAppName());
                    context.startActivity(i);
                }
            });

            download_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckNetwork.isInternetAvailable(context)) {
                        Intent i = new Intent(context, DownloadYourOwnDataActivity.class);
                        i.putExtra("SITE_NAME", currentItem.getSiteName());
                        i.putExtra("SITEID", currentItem.getSiteID());
                        i.putExtra("EVENTID", currentItem.getEventID());
                        i.putExtra("APP_NAME", currentItem.getMobAppName());
                        i.putExtra("PARENTAPPID", currentItem.getMobAppID());
                        i.putExtra("REPORT", "TRUE");
                        context.startActivity(i);
                    } else {
                        Toast.makeText(context, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            download_report_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckNetwork.isInternetAvailable(context)) {
                        Intent i = new Intent(context, DownloadYourOwnDataActivity.class);
                        i.putExtra("SITE_NAME", currentItem.getSiteName());
                        i.putExtra("SITEID", currentItem.getSiteID());
                        i.putExtra("EVENTID", currentItem.getEventID());
                        i.putExtra("APP_NAME", currentItem.getMobAppName());
                        i.putExtra("PARENTAPPID", currentItem.getMobAppID());
                        i.putExtra("REPORT", "TRUE");
                        context.startActivity(i);
                    } else {
                        Toast.makeText(context, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int event_status = currentItem.getStatus();

                    if (event_status != 0) {

                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID,
                                currentItem.getSiteID() + "");
                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_APPID,
                                currentItem.getMobAppID() + "");
                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME,
                                currentItem.getSiteName() + "");

                        Intent locationIntent = new Intent(context, LocationActivity.class);

                        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(context,
                                GlobalStrings.ENABLE_SPLIT_SCREEN);

                        if (Util.isTablet(context) && isSplitScreenEnabled)
                            locationIntent = new Intent(context, SplitLocationAndMapActivity.class);

                        locationIntent.putExtra("APP_ID", currentItem.getMobAppID());
                        locationIntent.putExtra("SITE_ID", currentItem.getSiteID());
                        locationIntent.putExtra("SITE_NAME", currentItem.getSiteName());
                        locationIntent.putExtra("EVENT_ID", currentItem.getEventID());

                        context.startActivity(locationIntent);
                    } else {
//                        if (CheckNetwork.isInternetAvailable(context)) {
//                            Intent i = new Intent(context, DownloadYourOwnDataActivity.class);
//                            i.putExtra("SITE_NAME", currentItem.getSiteName());
//                            i.putExtra("SITEID", currentItem.getSiteID());
//                            i.putExtra("EVENTID", currentItem.getEventID());
//                            i.putExtra("APP_NAME", currentItem.getMobAppName());
//                            i.putExtra("PARENTAPPID", currentItem.getMobAppID());
//                            i.putExtra("REPORT", "TRUE");
//                            context.startActivity(i);
//                        } else {
//                            Intent i = new Intent(context, MobileReportActivity.class);
//                            i.putExtra("SITE_NAME", currentItem.getSiteName());
//                            i.putExtra("SITE_ID", currentItem.getSiteID() + "");
//                            i.putExtra("EVENT_ID", currentItem.getEventID() + "");
//                            i.putExtra("APP_NAME", currentItem.getMobAppName());
//                            context.startActivity(i);
//                        }

                        //18/11/2017

                        FieldDataSource fd = new FieldDataSource(context);
                        boolean isAvailable = fd.isDataAvailable(currentItem.getEventID(), currentItem.getSiteID());

                        if (CheckNetwork.isInternetAvailable(context) && isAvailable) {
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setCancelable(false);
                            dialog.setTitle("");
                            dialog.setMessage("Do you want to download data or view report?");
                            dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    if (CheckNetwork.isInternetAvailable(context)) {
                                        Intent i = new Intent(context, DownloadYourOwnDataActivity.class);
                                        i.putExtra("SITE_NAME", currentItem.getSiteName());
                                        i.putExtra("SITEID", currentItem.getSiteID());
                                        i.putExtra("EVENTID", currentItem.getEventID());
                                        i.putExtra("APP_NAME", currentItem.getMobAppName());
                                        i.putExtra("PARENTAPPID", currentItem.getMobAppID());
                                        i.putExtra("REPORT", "TRUE");
                                        context.startActivity(i);
                                    } else {
                                        Intent i = new Intent(context, MobileReportActivity.class);
                                        i.putExtra("SITE_NAME", currentItem.getSiteName());
                                        i.putExtra("SITE_ID", currentItem.getSiteID() + "");
                                        i.putExtra("EVENT_ID", currentItem.getEventID() + "");
                                        i.putExtra("APP_NAME", currentItem.getMobAppName());
                                        context.startActivity(i);
                                    }
                                }
                            })
                                    .setNegativeButton("Report ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(context, MobileReportActivity.class);
                                            i.putExtra("SITE_NAME", currentItem.getSiteName());
                                            i.putExtra("SITE_ID", currentItem.getSiteID() + "");
                                            i.putExtra("EVENT_ID", currentItem.getEventID() + "");
                                            i.putExtra("APP_NAME", currentItem.getMobAppName());
                                            context.startActivity(i);
                                        }
                                    })

                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .setNeutralButton("Cancel ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            dialog.cancel();
                                        }


                                    });


                            final AlertDialog alert = dialog.create();
                            alert.show();

                        } else if (!(CheckNetwork.isInternetAvailable(context)) && isAvailable) {
                            Intent i = new Intent(context, MobileReportActivity.class);
                            i.putExtra("SITE_NAME", currentItem.getSiteName());
                            i.putExtra("SITE_ID", currentItem.getSiteID() + "");
                            i.putExtra("EVENT_ID", currentItem.getEventID() + "");
                            i.putExtra("APP_NAME", currentItem.getMobAppName());
                            context.startActivity(i);
                        } else if (CheckNetwork.isInternetAvailable(context) && !(isAvailable)) {
                            Intent i = new Intent(context, DownloadYourOwnDataActivity.class);
                            i.putExtra("SITE_NAME", currentItem.getSiteName());
                            i.putExtra("SITEID", currentItem.getSiteID());
                            i.putExtra("EVENTID", currentItem.getEventID());
                            i.putExtra("APP_NAME", currentItem.getMobAppName());
                            i.putExtra("PARENTAPPID", currentItem.getMobAppID());
                            i.putExtra("REPORT", "TRUE");
                            context.startActivity(i);
                        } else {
                            Toast.makeText(context, "No Data Available. Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    public void setmOnEventClosedButtonClickListner(OnEventClosedButtonClickListner mOnEventClosedButtonClickListner) {
        this.mOnEventClosedButtonClickListner = mOnEventClosedButtonClickListner;
    }

    private void RequiredDataInFormAlert(final int mobAppID, final int eventID, final int siteID, final String siteName) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(context.getString(R.string.attention));
        alertDialogBuilder
                .setMessage(context.getString(R.string.some_forms_have_mandatory_field_need_to_be_filled));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(context.getString(R.string.show),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent req_intent = new Intent(context, RequiredFieldsListActivity.class);

                        req_intent.putExtra("APP_ID", mobAppID);
                        req_intent.putExtra("EVENT_ID", eventID);
                        req_intent.putExtra("SITE_ID", siteID);
                        req_intent.putExtra("SITENAME", siteName);
                        context.startActivity(req_intent);

                        dialog.dismiss();
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel_upper_case),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void closeEventAlert(final int mobAppID, final int eventID, final int siteID, final String siteName) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Close & End Field Event");
        alertDialogBuilder
                .setMessage("Are you sure you want to submit the data and close the event?" +
                        "\nData will be committed to the server and you will not able to change it." +
                        "\n\nPlease click 'YES' to proceed or 'NO' to go back");

        alertDialogBuilder.setPositiveButton(" YES ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String capture = Util.getSharedPreferencesProperty(context, GlobalStrings.CAPTURE_SIGNATURE);
                        Boolean CAPTURE_SIGNATURE = false;

                        if (capture == null) {
                            CAPTURE_SIGNATURE = false;
                        } else {
                            CAPTURE_SIGNATURE = Boolean.parseBoolean(capture);
                        }

                        if (CAPTURE_SIGNATURE) {
                            Intent intent = new Intent(context, CaptureSignature.class);
                            intent.putExtra("EVENT_ID", eventID);
                            intent.putExtra("APP_ID", mobAppID);
                            intent.putExtra("SITE_ID", siteID);
                            intent.putExtra("CLOSE", "true");
                            intent.putExtra("UserID", userId);
                            ((Activity) context).startActivityForResult(intent, CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE);
                        } else {
                            closingEvents(mobAppID, eventID, siteID, siteName);
                        }
//                        closingEvents();


                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                        //drawer = false;
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Log.i(TAG, "closeEventAlert() End time:" + System.currentTimeMillis());
    }

    private void closingEvents(int mobAppID, int eventID, int siteID, String siteName) {
        EventDataSource eventData = new EventDataSource(context);

        Log.i(TAG, "closingEvents() Session UserID:" + userId);

        Log.i(TAG, "closingEvents() getEvent arguments :AppID=" + mobAppID +
                ",SiteID=" + siteID + ",userID=" + userId + ",DeviceID=" + deviceID);

//        event = eventData.getEvent(getAppID(), getSiteID(), userID, getDeviceID());
        Log.i(TAG, "closingEvents() getEvent End:" + System.currentTimeMillis());

        closeEvent = true;

        Log.i(TAG, "closingEvents() Call GPS Tracker start time:" + System.currentTimeMillis());

        GPSTracker tracker = new GPSTracker(context);
        double lat = tracker.getLatitude();
        double longt = tracker.getLongitude();
        Log.i(TAG, "closingEvents() GPS Tracker latitude:" + lat + ",longitude:" + longt);
        tracker.stopUsingGPS();
        
        boolean serverGenerated = eventData.isEventIDServerGenerated(eventID);
        Log.i(TAG, "closingEvents() Check EventID Server Generated result:" + serverGenerated);

        if (!serverGenerated) {

            Log.i(TAG, "closingEvents() EventID Not Found Server Generated ");

            final DEvent event = new DEvent();
            event.setSiteId(siteID);
            event.setMobileAppId(mobAppID);
            event.setUserId(userId);
            event.setEventDate(System.currentTimeMillis());
            event.setDeviceId(deviceID);
            event.setLatitude(lat);
            event.setLongitude(longt);
            event.setUserName(username);
            EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(this, event, this.username, this.password, false);
            eventHandler.execute();
            Log.i(TAG, "closingEvents() Get EventID from Server arguments:SiteID=" + siteID +
                    ",MobileAppID=" + mobAppID + ",UserID=" + userId + ",EventDate=" + event.getEventDate() + ",DeviceID=" + deviceID +
                    ",Latitude=" + lat + ",Longitude=" + longt + ",UserName=" + username);

        } else {
            /*
             * fieldSource.updateEventEndDateTime(getAppID(), dateString);
             */

            Log.i(TAG, "closingEvents() EventID Found Server Generated ");

            if (CheckNetwork.isInternetAvailable(context)) {
                uploadFieldDataBeforeEndEvent(eventID);
            } else {
                Log.i(TAG, "closingEvents() No Internet.Delete captured signture(s) = " + savedFilePath);

                removeAttachmentAfterSyncResult(savedFilePath, eventID);
                CustomToast.showToast((Activity) context, context.getString(R.string.bad_internet_connectivity), 5);
            }
        }

        Log.i(TAG, "closingEvents() End time:" + System.currentTimeMillis());


    }

    private void removeAttachmentAfterSyncResult(String savedFilePath, int eventID) {
        Log.i(TAG, "removeAttachmentAfterSyncResult() EventID Not Server Generated ");

        Log.i(TAG, "removeAttachmentAfterSyncResult() IN time:" + System.currentTimeMillis());
        deleteFileFromStorage(savedFilePath);
        int count = new AttachmentDataSource(context).deleteAttachment(eventID, "S");
        Log.i(TAG, "Removed No.of Attachment:" + count);
        Log.i(TAG, "removeAttachmentAfterSyncResult() OUT time:" + System.currentTimeMillis());
    }

    public void deleteFileFromStorage(String filePath) {

        Log.i(TAG, "deleteFileFromStorage() Start time:" + System.currentTimeMillis());
        Log.i(TAG, "deleteFileFromStorage() FilePath:" + filePath);

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

        Log.i(TAG, "deleteFileFromStorage() End time:" + System.currentTimeMillis());

    }

    public void uploadFieldDataBeforeEndEvent(int eventID) {
        Log.i(TAG, "Upload Field Data endEvent Call  start:" + System.currentTimeMillis());
        Intent dataUpload = new Intent(context, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", eventID);
        dataUpload.putExtra("CLOSE_EVENT", true);
        ((Activity) context).startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        Log.i(TAG, "Upload Field Data endEvent arguments:EventID-=" + eventID +
                ",UserName=" + username + ",Password=" + password);

        Log.i(TAG, "Upload Field Data endEvent Call End:" + System.currentTimeMillis());

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                    Toast.makeText(context, "The Event has been closed.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context,
                            "The event can not be closed,please try again!",
                            Toast.LENGTH_LONG).show();
                }
                if (savedFilePath != null) {
                    removeAttachmentAfterSyncResult(savedFilePath, mEventId);

                }
            }
            Log.i(TAG, "onActivityResult() SYNC_ACTIVITY End time:" + System.currentTimeMillis());

        }
    }

}