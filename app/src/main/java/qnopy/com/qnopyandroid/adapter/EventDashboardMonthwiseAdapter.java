package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment;
import qnopy.com.qnopyandroid.ui.events.SubmittalsFragment;
import qnopy.com.qnopyandroid.util.Util;

public class EventDashboardMonthwiseAdapter extends RecyclerView.Adapter<EventDashboardMonthwiseAdapter.ViewHolder> {

    private Context mContext;
    private HashMap<Long, List<EventData>> mHashMapMonthWiseData;
    private List<EventData> mEvents;

    private MyDashboardRecyclerViewAdapter mMyDashboardRecyclerViewAdapter;

    private SubmittalsFragment.OnListFragmentInteractionListener mListener;
    private int mTab;
    private SubmittalsFragment mSubmittalsFragment;

    private OnEventCardClickListner mOnEventCardClickListner;

    String mSearchedEvent;

    public EventDashboardMonthwiseAdapter(Context mContext,
                                          HashMap<Long,
                                                  List<EventData>> mHashMapMonthWiseData,
                                          SubmittalsFragment.OnListFragmentInteractionListener mListener,
                                          int SELECTED_TAB, SubmittalsFragment submittalsFragment) {

        this.mContext = mContext;
        this.mHashMapMonthWiseData = mHashMapMonthWiseData;
        this.mListener = mListener;
        this.mTab = SELECTED_TAB;
        this.mSubmittalsFragment = submittalsFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_event_dashboard_monthwise, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //Log.e("mHashMapMonthWiseData", "onBindViewHolder: "+mHashMapMonthWiseData.size());

        // TreeMap to store values of HashMap
        TreeMap<Long, List<EventData>> sorted = new TreeMap<>(Collections.reverseOrder());

        // Copy all data from hashMap into TreeMap
        sorted.putAll(mHashMapMonthWiseData);

        // Display the TreeMap which is naturally sorted
        for (Map.Entry<Long, List<EventData>> entry : sorted.entrySet()) {
            System.out.println("Key =================================== " + entry.getKey() + ", Value ============================================= " + entry.getValue());

            Long key = entry.getKey();
            mEvents = new ArrayList<>();
            mEvents = entry.getValue();

            Collections.sort(mEvents, new Comparator<EventData>() {
                @Override
                public int compare(EventData lhs, EventData rhs) {
                    //mSortSelection = "Ascending";
                    return Util.getMMddyyyyFromMilliSeconds(String.valueOf(rhs.getStartDate()))
                            .compareTo(Util.getMMddyyyyFromMilliSeconds(String.valueOf(lhs.getStartDate())));
                }
            });

/*            StringTokenizer tokens = new StringTokenizer(key, "-");
            String year = tokens.nextToken();
            String month = tokens.nextToken();*/

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(key);

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

            String date = CalendarFragment.getConvertedDate(year + "-" + month);

            viewHolder.linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(date);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(mContext.getResources().getColor(R.color.qnopy_teal));
            textView.setPadding(6, 0, 0, 0);
            viewHolder.linearLayout.addView(textView);

            RecyclerView recyclerView = new RecyclerView(mContext);

            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false));

            mMyDashboardRecyclerViewAdapter = new MyDashboardRecyclerViewAdapter(mContext, mEvents, mListener, mTab, mSubmittalsFragment);
            recyclerView.setAdapter(mMyDashboardRecyclerViewAdapter);
            mMyDashboardRecyclerViewAdapter.notifyDataSetChanged();
            viewHolder.linearLayout.addView(recyclerView);

            mMyDashboardRecyclerViewAdapter.setmOnEventClosedButtonClickListner(new OnEventClosedButtonClickListner() {
                @Override
                public void onEventClosedButtonClick(View view, int position, int appId, int eventId, int siteId, String siteName) {
                    Log.e("onEventClose", "onEventClosedButtonClick: position- " + (position + 1) + " \n MobileAppId- " + appId + " \n EventId- " + eventId + " \n SiteId- " + siteId + " \n SiteName- " + siteName);

                    mOnEventCardClickListner.onEventCardClick(view, position, appId, eventId, siteId, siteName);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewMonthWiseEvent;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.layoutEventDash);
            recyclerViewMonthWiseEvent = itemView.findViewById(R.id.recycler_view_monthWise_Event);
        }
    }

    public void setmOnEventCardClickListner(OnEventCardClickListner mOnEventCardClickListner) {
        this.mOnEventCardClickListner = mOnEventCardClickListner;
    }
}
