package qnopy.com.qnopyandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.db.LocationDataSource;

/**
 * Created by Yogendra on 03-Mar-17.
 */

public class RequiredFieldListAdapter extends RecyclerView.Adapter<RequiredFieldListAdapter.ViewHolder> {

    private static final String TAG = "ReqFieldListAdapter";
    List<RequiredFieldRowItem> items;
    Context context;

    public RequiredFieldListAdapter(Context context, ArrayList<RequiredFieldRowItem> list) {
        super();
        this.context = context;
        items = new ArrayList<RequiredFieldRowItem>();
        items = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.required_field_row, parent, false);
        return new RequiredFieldListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final int position = pos;
        final RequiredFieldRowItem item = items.get(position);

        try {
            LocationDataSource locationDataSource = new LocationDataSource(context);
            String locName = locationDataSource.getLocationName(item.getLocationId() + "");
            holder.tvLocName.setText(locName);
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvLocName.setVisibility(View.GONE);
        }

        holder.title_txt.setText(item.getTitle());
        // holder.title_txt.setTypeface(Typeface.DEFAULT_BOLD);
        holder.count_txt.setText(item.getCount() + "");

        holder.goto_required_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequiredFieldRowItem rowItem = items.get(position);
/*
               int appid= Integer.parseInt(rowItem.getChildAppId());
                int eventid= Integer.parseInt(rowItem.getEventId());
                int locid=rowItem.getLocationId();
                String dispname=rowItem.getTitle();*/

              /*  Intent intent=new Intent(context, LocationDetailActivity.class);

                intent.putExtra("EVENT_ID", eventid);
                intent.putExtra("LOCATION_ID", locid);
                intent.putExtra("APP_ID",appid);
                intent.putExtra("SITE_ID",rowItem.getSiteId());
                intent.putExtra("SITE_NAME",rowItem.getSiteName());
                intent.putExtra("APP_NAME", rowItem.getTitle());

                context.startActivity(intent);
*/

              /*  ((Activity)context).finish();
                view.setAnimation(
                        AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
*/

//                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView goto_required_btn;
        public TextView title_txt;
        public TextView count_txt;
        public TextView tvLocName;

        public ViewHolder(View itemView) {
            super(itemView);
            goto_required_btn = (TextView) itemView.findViewById(R.id.goto_required);
            title_txt = (TextView) itemView.findViewById(R.id.req_title);
            count_txt = (TextView) itemView.findViewById(R.id.req_count);
            tvLocName = (TextView) itemView.findViewById(R.id.tvLocName);
        }
    }
}
