package qnopy.com.qnopyandroid.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.TaskFromSite;
import qnopy.com.qnopyandroid.ui.activity.TaskDetailActivity;
import qnopy.com.qnopyandroid.util.Util;

import static android.content.ContentValues.TAG;

/**
 * Created by QNOPY on 7/19/2017.
 */

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> implements Filterable {

    List<TaskFromSite> items;
    int eventID, siteID = 0;
    int formid;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    public TaskDetailActivity ObjContext = null;
    PlanFilter planFilter = new PlanFilter(-1);

    public PlanListAdapter(ArrayList<TaskFromSite> planCardlist, TaskDetailActivity context) {
        this.items = planCardlist;
        this.ObjContext = context;
    }

    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_cardview, parent, false);
        return new PlanListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlanListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txtPlanname.setText(items.get(position).getPlanname());
        holder.txtSitename.setText(items.get(position).getSitename());
        holder.txtformname.setText(items.get(position).getFormname());
        holder.tasksnumtxt.setText(items.get(position).getTaskcount());
        String duedate = items.get(position).getWo_planendDate();

        /*Double newduedate= Double.valueOf(duedate);
        newduedate=newduedate*1000;
*/
        if (duedate != null) {
            Long newdate = Long.valueOf(duedate);
            newdate = newdate * 1000;
            //   String photoDate = null;

            //  String photoDate = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(newdate);

            String date = Util.getMMddyyyyFromMilliSeconds(String.valueOf(newdate));
            holder.duedatetext.setText(date);
        } else {
            holder.duedatetext.setText(" NA");
        }

        holder.navigatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formid = Integer.parseInt(items.get(position).getParentappid());
                setFormid(formid);
                siteID = Integer.parseInt(items.get(position).getSiteid());
                String planname = items.get(position).getPlanname();
                int wo_id = Integer.parseInt(items.get(position).getWorkorderid());
                ObjContext.getEventIDForTask(formid, siteID, planname, wo_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getFormid() {
        return formid;
    }

    public void setFormid(int formid) {
        this.formid = formid;
    }

    @Override
    public Filter getFilter() {
        if (planFilter == null) {
            planFilter = new PlanFilter(-1);
        }
        return planFilter;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtviewc1;
        public TextView txtPlanname;
        public TextView txtviewc2;
        public TextView tasksnumtxt;
        public TextView txtviewc3;
        public TextView txtviewc4;
        public TextView txtviewc5;
        public TextView txtformname;
        public TextView txtSitename;
        public TextView duedatetext;
        public ImageButton navigatebtn;


        public ViewHolder(View itemView) {
            super(itemView);
            txtviewc2 = (TextView) itemView.findViewById(R.id.txtviewc2);
            txtviewc3 = (TextView) itemView.findViewById(R.id.txtviewc3);
            txtPlanname = (TextView) itemView.findViewById(R.id.txtPlanname);
            tasksnumtxt = (TextView) itemView.findViewById(R.id.tasksnumtxt);
            duedatetext = (TextView) itemView.findViewById(R.id.duedatetext);
            txtformname = (TextView) itemView.findViewById(R.id.txtformname);
            txtSitename = (TextView) itemView.findViewById(R.id.txtSitename);
            navigatebtn = (ImageButton) itemView.findViewById(R.id.navigatebtn);
        }
    }

    private class PlanFilter extends Filter {

        int FILTER_BY = -1;

        public PlanFilter(int filter_type) {
            this.FILTER_BY = filter_type;

        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i("locationAdapter", "SearchText:" + filterString);


            if (constraint.length() == 0) {
                results.values = items;
                results.count = items.size();
                Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);

            } else {

                String filterableString = null;
                ArrayList<TaskFromSite> filtered = new ArrayList<TaskFromSite>();

                for (TaskFromSite item : items) {
                    filterableString = item.getFormname();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(item);
                    }

                }
                results.values = filtered;
                results.count = filtered.size();

                Log.i("locationAdapter", "Searched Text >Return Search Count:" + results.count);

            }
            //final ArrayList<String> nlist = new ArrayList<String>(count);


               /* case 1://by Location

                    if (constraint.length() == 0) {
                        results.values = items;
                        results.count = items.size();
                        Log.i(TAG, "No Search Text ,Return Search Count:" + results.count);

                    } else {

                        String filterableString;
                        ArrayList<TaskFromSite> filtered = new ArrayList<TaskFromSite>();

                        for (TaskFromSite loc : items) {
                            filterableString = loc.getTaskcount();
                            if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                                filtered.add(loc);
                            }

                        }
                        results.values = filtered;
                        results.count = filtered.size();

                        Log.i("locationAdapter", "Searched Text >Return Search Count:" + results.count);

                    }
                    //final ArrayList<String> nlist = new ArrayList<String>(count);

                    break;
                case 2://by Site

                    if (constraint.length() == 0) {
                        results.values = items;
                        results.count = items.size();
                        Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);

                    } else {

                        String filterableString;
                        ArrayList<TaskFromSite> filtered = new ArrayList<TaskFromSite>();

                        for (TaskFromSite item : items) {
                            filterableString = item.getFormname();
                            if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                                filtered.add(item);
                            }

                        }
                        results.values = filtered;
                        results.count = filtered.size();

                        Log.i("locationAdapter", "Searched Text >Return Search Count:" + results.count);

                    }
                    //final ArrayList<String> nlist = new ArrayList<String>(count);

                    break;

                case 3://by Form

                    if (constraint.length() == 0) {
                        results.values = items;
                        results.count = items.size();
                        Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);

                    } else {

                        String filterableString;
                        ArrayList<TaskFromSite> filtered = new ArrayList<TaskFromSite>();

                        for (TaskFromSite item : items) {
                            filterableString = item.getSitename();
                            if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                                filtered.add(item);
                            }

                        }

                        results.values = filtered;
                        results.count = filtered.size();

                        Log.i("locationAdapter", "Searched Text >Return Search Count:" + results.count);

                    }
                    //final ArrayList<String> nlist = new ArrayList<String>(count);

                    break;*/


            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            if (results.count == 0) {
                items = new ArrayList<TaskFromSite>();

            } else {
                items = (ArrayList<TaskFromSite>) results.values;
                Log.i(TAG, "Filtered_Items:" + items);
                notifyDataSetChanged();
            }
        }
    }
}