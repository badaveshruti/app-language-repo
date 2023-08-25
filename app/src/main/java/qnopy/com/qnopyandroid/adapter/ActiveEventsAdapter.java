package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.util.Util;

public class ActiveEventsAdapter extends RecyclerView.Adapter<ActiveEventsAdapter.ViewHolder> {

    private ArrayList<DEvent> eventList;
    private Context context;
    private OnEventItemClickListener mListener;

    public ActiveEventsAdapter(ArrayList<DEvent> eventArrayList,
                               Context context, OnEventItemClickListener listener) {
        this.eventList = eventArrayList;
        this.context = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_active_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DEvent event = eventList.get(position);

        try {
            if (event.getEventName().isEmpty())
                holder.tvEventName.setVisibility(View.GONE);
        } catch (Exception e) {
            holder.tvEventName.setVisibility(View.GONE);
        }

        String eventName = context.getString(R.string.name) + ":" + event.getEventName();
        holder.tvEventName.setText(eventName);

        UserDataSource userDataSource = new UserDataSource(context);
        String userName = userDataSource.getUserNameFromID(event.getUserId() + "");

        String eventDate;
        if (String.valueOf(event.getEventStartDate()).length() == 10)
            eventDate = " - Event date: " + Util.getFormattedDateTime(event.getEventStartDate()
                    * 1000, GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN);
        else
            eventDate = " - Event date: " + Util.getFormattedDateTime(event.getEventStartDate(),
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN);

        String by = context.getString(R.string.by) + userName + eventDate;
        holder.tvEventDate.setText(by);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventName;
        private final TextView tvEventDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onEventItemClicked(eventList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnEventItemClickListener {
        void onEventItemClicked(DEvent event);
    }
}
