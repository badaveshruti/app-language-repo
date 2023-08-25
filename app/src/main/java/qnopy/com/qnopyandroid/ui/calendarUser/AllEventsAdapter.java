package qnopy.com.qnopyandroid.ui.calendarUser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shuhart.stickyheader.StickyAdapter;

import java.util.ArrayList;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.util.Util;

public class AllEventsAdapter extends StickyAdapter<RecyclerView.ViewHolder,
        RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "Filter Event";
    public static int HEADER_VIEW = 0;
    public static int ITEM_VIEW = 1;
    public static int STICKY_HEADER_VIEW = 2;

    private ArrayList<EventData> listEvents;
    private ArrayList<EventData> filteredEventsList = new ArrayList<>();
    private Context mContext;
    private CalendarFragment mFragContext;
    private OnCalendarEventListener mListener;
    private EventFilter mFilter;
    private boolean isSwitchChecked = false;
    private boolean isCheckedFirstTime = true;

    public AllEventsAdapter(ArrayList<EventData> lisEvents, Context context,
                            OnCalendarEventListener listener, CalendarFragment fragContext) {
        this.listEvents = lisEvents;
        this.filteredEventsList = lisEvents;
        this.mContext = context;
        this.mFragContext = fragContext;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_calendar_events_item, parent, false);

        View headerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_startevent_header, parent, false);

        if (viewType == HEADER_VIEW)
            return new HeaderViewHolder(headerView);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventData event = filteredEventsList.get(position);

        if (event.getViewType() != HEADER_VIEW) {

            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            enableViews(itemHolder, event.getStatus() == 1);

            if (event.getStatus() == 0 || event.getStatus() == 2 || ScreenReso.isLimitedUser)
                itemHolder.btnCloseEvent.setVisibility(View.INVISIBLE);
            else
                itemHolder.btnCloseEvent.setVisibility(View.VISIBLE);

            itemHolder.tvSiteName.setText(event.getSiteName());

            if (event.getEventName() == null || event.getEventName().trim().isEmpty())
                itemHolder.tvEventName.setText(event.getMobAppName());
            else
                itemHolder.tvEventName.setText(event.getEventName());

            itemHolder.tvFormName.setText(event.getMobAppName());

            long startDate = event.getStartDate();
            if (String.valueOf(startDate).length() == 10)
                startDate = startDate * 1000;

            long sortDate = event.getSortedDate();
            if (String.valueOf(sortDate).length() == 10)
                sortDate = sortDate * 1000;

            itemHolder.tvUpdateOnDate.setText("Created: " + Util.getFormattedDateFromMilliS(startDate,
                    "MM/dd/yyyy"));

            String update;
            if (sortDate != startDate && sortDate != 0) {
                update = mContext.getString(R.string.updated) + " " + Util.getFormattedDateFromMilliS(sortDate,
                        "MM/dd/yyyy");
                itemHolder.tvUpdateOnDate.setText(update);
            }

            UserDataSource userDataSource = new UserDataSource(mContext);
            String firstName = userDataSource.getFirstNameFromID(event.getUserId() + "");
            String createdBy = mContext.getString(R.string.created_by) + " " + firstName;
            itemHolder.tvCreatedBy.setText(createdBy);

            if (event.getUpdatedBy() != null) {
                if (!event.getUpdatedBy().isEmpty()) {
                    String updatedBy = mContext.getString(R.string.by) + " " + userDataSource.getFirstNameFromID(event.getUpdatedBy() + "");
                    itemHolder.tvUpdatedBy.setText(updatedBy);
                }
            } else {
                String updatedBy = "";
                itemHolder.tvUpdatedBy.setText(updatedBy);
            }
        } else {
            bindHeaderView((HeaderViewHolder) holder, position);
        }
    }

    private void enableViews(ItemViewHolder holder, boolean enable) {
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

    @Override
    public int getItemCount() {
        return filteredEventsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return filteredEventsList.get(position).getViewType();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        //for now kept as 0 as our header will be at 0th pos only.
        //You can handle this in future if u have more sticky headers.
        return 0;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int headerPosition) {
        ((HeaderViewHolder) holder).fabStartEvent.setOnClickListener(v -> {
            mListener.onStartBtnEventClicked();
        });
        bindHeaderView((HeaderViewHolder) holder, headerPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return createViewHolder(parent, HEADER_VIEW);
    }

    private void bindHeaderView(HeaderViewHolder holder, int position) {
        EventData event = listEvents.get(position);

        if (filteredEventsList.size() > 1) {
            if (mFragContext.switchFilterForms.isChecked())
                holder.tvOnGoingForms.setText(mContext.getString(R.string.my_forms));
            else
                holder.tvOnGoingForms.setText(mContext.getString(R.string.ongoing_forms));

        } else if (filteredEventsList.size() == 1) {
            holder.tvOnGoingForms.setText(mContext.getString(R.string.no_forms_for_the_day));
        }
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new EventFilter();
        }
        return mFilter;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView fabStartEvent;
        private final CustomTextView tvOnGoingForms;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            fabStartEvent = itemView.findViewById(R.id.fabStartEvent);
            tvOnGoingForms = itemView.findViewById(R.id.tvOngoingForms);

            fabStartEvent.setOnClickListener(v -> {
                mListener.onStartBtnEventClicked();
            });
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventName;
        private final TextView tvSiteName;
        private final TextView tvFormName;
        private final TextView tvUpdateOnDate;
        private final TextView tvUpdatedBy;
        private final TextView tvCreatedBy;
        private final TextView tvEmailLogs;
        private final TextView tvDownloadData;
        private final RelativeLayout btnCloseEvent;

        public ItemViewHolder(@NonNull View itemView) {
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
                if (filteredEventsList.get(getBindingAdapterPosition()).getStatus() == 1) {
                    if (mListener != null) {
                        mListener.onCalendarEventClicked(filteredEventsList.get(getBindingAdapterPosition()));
                    }
                }
            });

            tvEmailLogs.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onEmailLogsClicked(filteredEventsList.get(getBindingAdapterPosition()));
                }
            });

            tvDownloadData.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onDownloadDataClicked(filteredEventsList.get(getBindingAdapterPosition()));
                }
            });

            btnCloseEvent.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onCloseButtonClicked(filteredEventsList.get(getBindingAdapterPosition()));
                }
            });
        }
    }

    public class EventFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i(TAG, "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = listEvents;
                results.count = listEvents.size();
                Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);
            } else {

                String filterableString;
                ArrayList<EventData> filtered = new ArrayList<>();

                for (EventData event : listEvents) {
                    filterableString = event.getUserId() + "";
                    if (filterableString.toLowerCase(Locale.getDefault())
                            .contains(filterString) || filterableString.toLowerCase(Locale.getDefault())
                            .contains("-1")) {
                        //added -1 constraint to keep header 0th value of list away from filtering
                        filtered.add(event);
                    }
                }

                results.values = filtered;
                results.count = filtered.size();

                Log.i(TAG, "Searched Text >Return Search Count:" + results.count);
            }
            return results;
        }

        int position = 1;

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredEventsList = listEvents;
            } else {
                filteredEventsList = (ArrayList<EventData>) results.values;
                Log.i(TAG, "FilteredSites:" + filteredEventsList);
            }
            notifyDataSetChanged();
        }
    }

    interface OnCalendarEventListener {
        void onCalendarEventClicked(EventData event);

        void onEmailLogsClicked(EventData event);

        void onDownloadDataClicked(EventData event);

        void onStartBtnEventClicked();

        void onCloseButtonClicked(EventData event);
    }
}
