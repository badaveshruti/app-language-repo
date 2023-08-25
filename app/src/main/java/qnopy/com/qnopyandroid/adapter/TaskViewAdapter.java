package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.TaskView;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.WorkOrderTaskDataSource;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.util.Util;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

/**
 * Created by QNOPY on 7/20/2017.
 */

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.ViewHolder> {

    List<TaskView> items;
    Context context;
    int eventid, formid, siteid = 0;
    String dispappName, locDesc = null;

    public TaskViewAdapter(ArrayList<TaskView> taskCardlist, Context context, int eventid, int formid, int siteid) {
        this.items = taskCardlist;
        this.context = context;
        this.eventid = eventid;
        this.formid = formid;
        this.siteid = siteid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.taskview_cardview, parent, false);
        return new TaskViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtTaskname.setText(items.get(position).getTaskname());
        holder.txtlocation.setText(items.get(position).getLocation());

        final TaskView currentItem = items.get(position);
        if (items.get(position).getTaskduedate() != null) {
            holder.taskduedate.setText(items.get(position).getTaskduedate());
        } else {
            holder.taskduedate.setText(" No");
        }

        if ((items.get(position).getStatus()).equalsIgnoreCase("completed")) {
            holder.taskswitch.setChecked(false);
            holder.taskswitch.setEnabled(false);
        }
            WorkOrderTaskDataSource workOrderTaskDataSource=new WorkOrderTaskDataSource(context);
          boolean istaskclosed= workOrderTaskDataSource.isTaskClosed(eventid);


        // }
        holder.gobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LocationDetailIntent = new Intent(context,
                        LocationDetailActivity.class);

                String locid = String.valueOf(currentItem.getLocationId());
                LocationDetailIntent.putExtra("EVENT_ID", eventid);
                LocationDetailIntent.putExtra("LOCATION_ID", locid);
                LocationDetailIntent.putExtra("APP_ID", formid);
                LocationDetailIntent.putExtra("SITE_ID", siteid);
                SiteDataSource siteDataSource = new SiteDataSource(context);
                String sitename = siteDataSource.getSiteNamefromID(siteid);
                LocationDetailIntent.putExtra("SITE_NAME", sitename);

                dispappName = new SiteMobileAppDataSource(context).getMobileAppDisplayNameRollIntoApp(formid, siteid);
                Log.i(TAG, "onCreate() Current Display name on header" +
                        "( Roll_Into_App DisplayName for MobileApp=" + formid + " and SiteID=" + siteid + " ):" + dispappName);

                if (dispappName == null) {
                    //  btn.setText(getSiteName());
                    dispappName = new SiteMobileAppDataSource(context).getMobileAppDisplayNameRollIntoAppForSite(formid);

                    Log.i(TAG, "onCreate() Current Display name on header" +
                            "( Roll_Into_App DisplayName for MobileApp=" + formid + "and SiteID=0 ):" + dispappName);
                }

                LocationDetailIntent.putExtra("APP_NAME", dispappName);
                LocationDetailIntent.putExtra("USER_ID", Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

                LocationDetailIntent.putExtra("LOCATION_NAME", currentItem.getLocation());
                LocationDetailIntent.putExtra("LOCATION_DESC", locDesc == null ? "" : locDesc);

                context.startActivity(LocationDetailIntent);
            }
        });

        holder.taskswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {

                if(!isChecked)
                {

                //  if(!isChecked)
                //  {

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setCancelable(false);
                dialog.setTitle("Close Task");
                dialog.setMessage("Are you sure you want to close the task?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        WorkOrderTaskDataSource workOrderTaskDataSource = new WorkOrderTaskDataSource(context);


                        int ret = workOrderTaskDataSource.updateStatus(Integer.parseInt(currentItem.getWoTaskID()), currentItem.getLocationId());

                        buttonView.setChecked(false);
                        Log.i(TAG, "updateStatus :" + ret);
                        buttonView.setEnabled(false);
                    }
                })
                        .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.taskswitch.setChecked(true);
                                dialog.dismiss();
                                dialog.cancel();
                                //Action for "Cancel".
                            }
                        });

                AlertDialog alert = dialog.create();
                alert.show();

            }else
                {
                    buttonView.setChecked(true);
                    buttonView.setEnabled(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtviewt1;
        public TextView txtTaskname;
        public TextView txtviewt2;
        public TextView txtmobname;
        public TextView txtviewt3;
        public TextView txtlocation;
        public TextView txtviewt4;
        public TextView taskduedate;
        public ImageButton gobtn;
        public SwitchCompat taskswitch;


        public ViewHolder(View itemView) {
            super(itemView);

            txtTaskname = (TextView) itemView.findViewById(R.id.txtTaskname);
            txtviewt3 = (TextView) itemView.findViewById(R.id.txtviewt3);
            txtlocation = (TextView) itemView.findViewById(R.id.txtlocation);
            txtviewt4 = (TextView) itemView.findViewById(R.id.txtviewt4);
            taskduedate = (TextView) itemView.findViewById(R.id.taskduedate);
            gobtn = (ImageButton) itemView.findViewById(R.id.gobtn);
            taskswitch = (SwitchCompat) itemView.findViewById(R.id.taskswitch);


        }
    }
}
