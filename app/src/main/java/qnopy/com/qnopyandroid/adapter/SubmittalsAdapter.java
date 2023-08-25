package qnopy.com.qnopyandroid.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.gps.GPSTracker;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.ui.activity.DashboardActivity;
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportActivity;
import qnopy.com.qnopyandroid.ui.activity.RequiredFieldsListActivity;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 05-Jul-16.
 */
public class SubmittalsAdapter extends RecyclerView.Adapter<SubmittalsAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "SubmittalsAdapter";
    //    int FILTER_BY = 0;//0=Location,1=Site,2=Form
    List<EventData> items;
    List<EventData> filtered_items;
    DashboardActivity context;
    int[] followup_id = new int[]{R.mipmap.presence_online, R.mipmap.presence_offline};
    CardFilter cardFilter = new CardFilter();
    boolean closeEvent = false;
    private String username, userID, deviceID, password;

    public SubmittalsAdapter(List<EventData> items, DashboardActivity context) {

        this.context = context;
        this.items = new ArrayList<>();
        this.items = items;
        this.filtered_items = items;

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);
    }

    @Override
    public SubmittalsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dash_card_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final EventData item = filtered_items.get(position);
        holder.currentItem = item;

        int event_status = item.getStatus();
        if (event_status == 1) {
            holder.closeevent_switch.setVisibility(View.VISIBLE);

            holder.status_button.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));//Active
        } else if (event_status == 0) {
            holder.closeevent_switch.setVisibility(View.GONE);
            holder.status_button.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));//Closed
        } else {
            holder.status_button.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));//Conflict
            holder.closeevent_switch.setVisibility(View.VISIBLE);

        }
        holder.viewSite.setText(item.getSiteName());
        holder.viewForm.setText(item.getMobAppName());
        String start = "", end = "Working";

        if (item.getStartDate() != 0) {
            start = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(item.getStartDate());
        }

        if (item.getEndDate() != 0) {
            end = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(item.getEndDate());
        }

        holder.viewStartDate.setText(start);
        holder.viewEndDate.setText(end);
        holder.viewLocationCount.setText(item.getLocationCount() + "");


    }


    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    public void removeItem(EventData item) {
        filtered_items.remove(item);

    }

    @Override
    public Filter getFilter() {
        if (cardFilter == null) {
            cardFilter = new CardFilter();
        }
        return cardFilter;
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

                String filterableString;
                ArrayList<EventData> filtered = new ArrayList<EventData>();

                for (EventData item : items) {
                    filterableString = item.getSiteName();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        public Button status_button;
        SwitchCompat closeevent_switch;
        public TextView viewSite, viewForm, viewStartDate, viewEndDate, viewLocationCount;

        public EventData currentItem;


        public ViewHolder(View itemView) {
            super(itemView);

            viewSite = (TextView) itemView.findViewById(R.id.sitenametv);
            viewStartDate = (TextView) itemView.findViewById(R.id.startdatetv);
            viewEndDate = (TextView) itemView.findViewById(R.id.enddatetv);
            viewForm = (TextView) itemView.findViewById(R.id.formtv);
            viewLocationCount = (TextView) itemView.findViewById(R.id.loc_count_tv);
            status_button = (Button) itemView.findViewById(R.id.statusbtn);
            closeevent_switch = (SwitchCompat) itemView.findViewById(R.id.closeeventsw);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int event_status = currentItem.getStatus();


                    if (event_status != 0) {

                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID, currentItem.getSiteID() + "");
                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_APPID, currentItem.getMobAppID() + "");
                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME, currentItem.getSiteName() + "");

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
                }
            });

            closeevent_switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Alert");
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to submit the data and close the event?" +
                            "\nData will be committed to the server and you will not able to change it." +
                            "\n\nPlease click 'YES' to proceed or 'NO' to go back");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            closeevent_switch.setChecked(true);
                            DashboardActivity.itemToDelete = currentItem;

                            if (CheckNetwork.isInternetAvailable(context)) {
                                FieldDataSource fieldDataSource = new FieldDataSource(context);

                                ArrayList<RequiredFieldRowItem> reqDataList =
                                        fieldDataSource.getMandatoryFieldList(currentItem.getMobAppID() + "", currentItem.getEventID() + "",
                                                currentItem.getSiteID() + "");

                                if (reqDataList != null && reqDataList.size() > 0 && reqDataList.get(0).getCount() > 0) {
                                    RequiredDataInFormAlert();
                                } else {
                                    closingEvents(currentItem);
                                }

                            } else {
                                Toast.makeText(context, context.getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                                closeevent_switch.setChecked(false);

                            }

                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            closeevent_switch.setChecked(false);

                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.show();
                }
            });

//            closeevent_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (b){
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Alert");
//                        builder.setMessage("Are you sure to close this event?");
//                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//
//                        builder.setNegativeButton("NO", null);
//
//                        Dialog dialog = builder.create();
//                        dialog.show();
//                    }
//                }
//            });


//            status_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int event_status = currentItem.getStatus();
//
//                    if (event_status == 0) {
//                        // TODO: 11/10/2017 DOWNLOAD DATA & SHOW REPORT
//
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
//
//                    } else if (event_status == 1) {
//                        // TODO: 11/10/2017 CLOSE EVENT
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Alert");
//                        builder.setMessage("Are you sure to close this event?");
//                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//
//                        builder.setNegativeButton("NO", null);
//
//                        Dialog dialog = builder.create();
//                        dialog.show();
//                    }
//                }
//            });

        }

        public void closingEvents(EventData item) {

            EventDataSource eventData = new EventDataSource(context);

            closeEvent = true;

            GPSTracker tracker = new GPSTracker(context);
            double lat = tracker.getLatitude();
            double longt = tracker.getLongitude();
            tracker.stopUsingGPS();
            Log.i(TAG, "closingEvents() GPS Tracker latitude:" + lat + ",longitude:" + longt);


            boolean serverGenerated = eventData
                    .isEventIDServerGenerated(item.getEventID());
            Log.i(TAG, "closingEvents() Check EventID Server Generated result:" + serverGenerated);

            if (!serverGenerated) {

                Log.i(TAG, "closingEvents() EventID Not Found Server Generated ");

                DEvent event = new DEvent();
                event.setSiteId(item.getSiteID());
                event.setMobileAppId(item.getMobAppID());
                event.setUserId(Integer.valueOf(userID));
                event.setEventDate(item.getStartDate());

                event.setDeviceId(deviceID);
                event.setLatitude(lat);
                event.setLongitude(longt);
                event.setUserName(username);
                EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(context,
                        event, username, password, false);
                eventHandler.execute();

            } else {

                if (CheckNetwork.isInternetAvailable(context)) {
                    context.uploadFieldDataBeforeEndEvent();
                } else {
                    CustomToast.showToast(context,
                            context.getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG);
                }
            }

        }


        public void RequiredDataInFormAlert() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            alertDialogBuilder.setTitle(context.getString(R.string.attention));
            alertDialogBuilder
                    .setMessage(context.getString(R.string.some_forms_have_mandatory_field_need_to_be_filled));
            // set positive button: Yes message
            alertDialogBuilder.setPositiveButton(context.getString(R.string.show),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent req_intent = new Intent(context, RequiredFieldsListActivity.class);

                            req_intent.putExtra("APP_ID", currentItem.getMobAppID());
                            req_intent.putExtra("EVENT_ID", currentItem.getEventID());
                            req_intent.putExtra("SITE_ID", currentItem.getSiteID());
                            req_intent.putExtra("SITENAME", currentItem.getSiteName());
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
    }
}
