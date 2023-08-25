package qnopy.com.qnopyandroid.ui.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.interfacemodel.OnEventCardClickListener;
import qnopy.com.qnopyandroid.photogallery.AsyncTask;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class TempEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int HEADER_VIEW = 0;
    public static int ITEM_VIEW = 1;
    private final SubmittalsFragment fragContext;
    private final OnEventCardClickListener mOnEventCardClickListener;
    private final int mSelectedTab;

    private ArrayList<EventData> listEvents;
    private ArrayList<EventData> filteredEventsList;
    private Context mContext;
    private Map<Long, List<EventData>> mapAllEvents;

    public TempEventsAdapter(ArrayList<EventData> listEvent,
                             Context context, Map<Long, List<EventData>> mapEvents,
                             SubmittalsFragment submittalsFragment,
                             OnEventCardClickListener eventClickListener, int selectedTab) {
        this.listEvents = listEvent;
        this.filteredEventsList = listEvent;
        this.mContext = context;
        this.mapAllEvents = mapEvents;
        this.fragContext = submittalsFragment;
        this.mOnEventCardClickListener = eventClickListener;
        this.mSelectedTab = selectedTab;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_calendar_events_item, parent, false);

        View headerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_event_header, parent, false);

        if (viewType == HEADER_VIEW)
            return new HeaderViewHolder(headerView);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        EventData event = filteredEventsList.get(position);

        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        holder.tvHeaderName.setText(event.getEventName());

        if (event.isExpanded()) {
            holder.ivExpandArrow.setImageDrawable(VectorDrawableUtils.getDrawable(mContext,
                    R.drawable.expand_arrow, R.color.btn_fg));
//            holder.rvEvents.setVisibility(View.VISIBLE);
            setRecyclerView(holder, event, true);
        } else {
            holder.ivExpandArrow.setImageDrawable(VectorDrawableUtils.getDrawable(mContext,
                    R.drawable.list_arrow_right, R.color.btn_fg));
//            holder.rvEvents.setVisibility(View.GONE);
            setRecyclerView(holder, event, false);
        }
    }

    private void setRecyclerView(HeaderViewHolder holder, EventData event,
                                 boolean hasData) {
        holder.rvEvents.setLayoutManager(new LinearLayoutManager(mContext));
        new PopulateEventsTask(holder, event, hasData).execute();
    }

    private class PopulateEventsTask extends AsyncTask<Void, Void, ChildEventsAdapter> {

        HeaderViewHolder holder;
        EventData event;
        boolean hasData;

        public PopulateEventsTask(HeaderViewHolder holder, EventData event, boolean hasData) {
            this.holder = holder;
            this.event = event;
            this.hasData = hasData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.ivExpandArrow.setVisibility(View.GONE);
            holder.progressBarExpand.setVisibility(View.VISIBLE);
        }

        @Override
        protected ChildEventsAdapter doInBackground(Void... voids) {
            ChildEventsAdapter childEventsAdapter = null;
            if (hasData) {
                if (mapAllEvents.containsKey(event.getStartDate())) {

                    if (mapAllEvents.get(event.getStartDate()) != null) {
                        List<EventData> list = mapAllEvents.get(event.getStartDate());

                        Collections.sort(list, new Comparator<EventData>() {
                            @Override
                            public int compare(EventData lhs, EventData rhs) {
                                return Long.compare(rhs.getModificationDate(), lhs.getModificationDate());
                            }
                        });

                        childEventsAdapter = new ChildEventsAdapter(new ArrayList<>(list),
                                mContext, mSelectedTab, mOnEventCardClickListener);
                    }
                }
            } else {
                childEventsAdapter = new ChildEventsAdapter(new ArrayList<>(),
                        mContext, mSelectedTab, mOnEventCardClickListener);
            }

            return childEventsAdapter;
        }

        @Override
        protected void onPostExecute(ChildEventsAdapter adapter) {
            super.onPostExecute(adapter);
            holder.rvEvents.setAdapter(adapter);
            holder.progressBarExpand.setVisibility(View.GONE);
            holder.ivExpandArrow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredEventsList.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView tvHeaderName;
        private final ImageView ivExpandArrow;
        private final RecyclerView rvEvents;
        private final ProgressBar progressBarExpand;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderName = itemView.findViewById(R.id.tvHeaderName);
            ivExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
            rvEvents = itemView.findViewById(R.id.rvEvents);
            progressBarExpand = itemView.findViewById(R.id.progressBarExpand);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventData event = filteredEventsList.get(getAdapterPosition());
                    fragContext.scrollToPos(getAbsoluteAdapterPosition());

                    if (event.isExpanded()) {
                        event.setExpanded(false);
                        ivExpandArrow.setImageDrawable(VectorDrawableUtils.getDrawable(mContext,
                                R.drawable.list_arrow_right, R.color.btn_fg));
                        //                        rvEvents.setVisibility(View.GONE);
                        setRecyclerView(HeaderViewHolder.this, event, false);

                    } else {
                        event.setExpanded(true);
//                        rvEvents.setVisibility(View.VISIBLE);
                        ivExpandArrow.setImageDrawable(VectorDrawableUtils.getDrawable(mContext,
                                R.drawable.expand_arrow, R.color.btn_fg));
                        setRecyclerView(HeaderViewHolder.this, event, true);
                    }
                    notifyItemChanged(getAbsoluteAdapterPosition());
//                    notifyDataSetChanged();
                }
            });
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
