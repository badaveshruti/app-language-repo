package qnopy.com.qnopyandroid.ui.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.interfacemodel.OnEventCardClickListener;
import qnopy.com.qnopyandroid.util.Util;

public class ChildEventsAdapter extends RecyclerView.Adapter<ChildEventsAdapter.ViewHolder> {

    private ArrayList<EventData> listEvents;
    private Context mContext;
    private OnEventCardClickListener mOnEventCardClickListener;
    private int mSelectedTab;

    public ChildEventsAdapter(ArrayList<EventData> childEvents, Context context,
                              int selectedTab, OnEventCardClickListener eventClickListener) {
        this.listEvents = childEvents;
        this.mContext = context;
        this.mOnEventCardClickListener = eventClickListener;
        this.mSelectedTab = selectedTab;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_calendar_events_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventData event = listEvents.get(position);
        enableViews(holder, event.getStatus() == 1);

        if (event.getStatus() == 0 || event.getStatus() == 2 || ScreenReso.isLimitedUser)
            holder.btnCloseEvent.setVisibility(View.INVISIBLE);
        else
            holder.btnCloseEvent.setVisibility(View.VISIBLE);

        holder.tvSiteName.setText(event.getSiteName());

        if (event.getEventName() == null || event.getEventName().trim().isEmpty())
            holder.tvEventName.setText(event.getMobAppName());
        else
            holder.tvEventName.setText(event.getEventName());

        holder.tvFormName.setText(event.getMobAppName());

        long startDate = event.getStartDate();
        if (String.valueOf(startDate).length() == 10)
            startDate = startDate * 1000;

        holder.tvUpdateOnDate.setText("Created: " + Util.getFormattedDateFromMilliS(startDate,
                "MM/dd/yyyy"));

/*        long updatedDate = event.getUpdatedDate();
        if (String.valueOf(updatedDate).length() == 10)
            updatedDate = updatedDate * 1000;

        if (updatedDate != startDate && updatedDate != 0) {
            String onDate = mContext.getString(R.string.updated) + " " + Util.getFormattedDateFromMilliS(startDate,
                    "hh:mm aa");
            holder.tvUpdateOnDate.setText(onDate);
        } else {
            String updated = mContext.getString(R.string.updated) + " NA";
            holder.tvUpdateOnDate.setText(updated);
        }*/

        UserDataSource userDataSource = new UserDataSource(mContext);
        String firstName = userDataSource.getFirstNameFromID(event.getUserId() + "");
        String createdBy = mContext.getString(R.string.created_by) + " " + firstName;
        holder.tvCreatedBy.setText(createdBy);

        if (event.getUpdatedBy() != null) {
            if (!event.getUpdatedBy().isEmpty()) {
                String updatedBy = mContext.getString(R.string.by) + " " + userDataSource.getFirstNameFromID(event.getUpdatedBy() + "");
                holder.tvUpdatedBy.setText(updatedBy);
            }
        } else {
            String updatedBy = "";
            holder.tvUpdatedBy.setText(updatedBy);
        }

        holder.btnCloseEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEventCardClickListener.onEventCloseClick(v,
                        holder.getAbsoluteAdapterPosition(), event);
            }
        });

        holder.tvEmailLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEventCardClickListener.onEmailLogsClicked(event);
            }
        });

        holder.tvDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEventCardClickListener.onDownloadDataClicked(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    private void enableViews(ViewHolder holder, boolean enable) {
        holder.tvEventName.setEnabled(enable);
        holder.tvFormName.setEnabled(enable);
        holder.tvSiteName.setEnabled(enable);
        holder.tvUpdateOnDate.setEnabled(enable);
        holder.tvUpdatedBy.setEnabled(enable);
        holder.tvCreatedBy.setEnabled(enable);

        if (enable) {
            holder.tvEmailLogs.setTextColor(ContextCompat.getColor(mContext, R.color.blue_start_new));
            holder.tvDownloadData.setTextColor(ContextCompat.getColor(mContext, R.color.blue_start_new));
        } else {
            holder.tvEmailLogs.setTextColor(ContextCompat.getColor(mContext, R.color.task_faint_blue));
            holder.tvDownloadData.setTextColor(ContextCompat.getColor(mContext, R.color.task_faint_blue));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventName;
        private final TextView tvSiteName;
        private final TextView tvFormName;
        private final TextView tvUpdateOnDate;
        private final TextView tvUpdatedBy;
        private final TextView tvCreatedBy;
        private final TextView tvEmailLogs;
        private final TextView tvDownloadData;
        private final RelativeLayout btnCloseEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvSiteName = itemView.findViewById(R.id.tvSiteName);
            tvFormName = itemView.findViewById(R.id.tvFormName);
            tvUpdateOnDate = itemView.findViewById(R.id.tvUpdateOnDate);
            tvUpdatedBy = itemView.findViewById(R.id.tvUpdatedBy);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvEmailLogs = itemView.findViewById(R.id.tvEmailLogs);
            tvDownloadData = itemView.findViewById(R.id.tvDownloadData);
            btnCloseEvent = itemView.findViewById(R.id.btnCloseEvent);

            itemView.setOnClickListener(v -> {
                EventData currentItem = listEvents.get(getBindingAdapterPosition());
                mOnEventCardClickListener.onEventCardClicked(currentItem);
            });
        }
    }
}
