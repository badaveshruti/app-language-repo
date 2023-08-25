package qnopy.com.qnopyandroid.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.ChangeEventAdapter;
import qnopy.com.qnopyandroid.clientmodel.ChangeEventModel;
import qnopy.com.qnopyandroid.db.FieldDataSource;

public class ChangeEventBottomSheetFragment extends BottomSheetDialogFragment {

    private ArrayList<ChangeEventModel> eventList = new ArrayList<>();
    private static final String KEY_SITE_ID = "siteId";
    private static final String KEY_EVENT_ID = "eventId";
    private static final String KEY_EVENT_STATUS = "eventStatus";
    private RecyclerView rvChangeEvent;
    private String siteId, eventStatus, eventId;
    private SheetEventClickListener mListener;

    public static ChangeEventBottomSheetFragment newInstance(String siteId, String eventStatus,
                                                             String eventId) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SITE_ID, siteId);
        bundle.putString(KEY_EVENT_STATUS, eventStatus);
        bundle.putString(KEY_EVENT_ID, eventId);
        ChangeEventBottomSheetFragment fragment = new ChangeEventBottomSheetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            siteId = getArguments().getString(KEY_SITE_ID);
            eventStatus = getArguments().getString(KEY_EVENT_STATUS);
            eventId = getArguments().getString(KEY_EVENT_ID);
        }

        FieldDataSource fieldDataSource = new FieldDataSource(getActivity());
        eventList = fieldDataSource.getEvents(siteId, eventStatus, eventId);

        rvChangeEvent = view.findViewById(R.id.rvChangeEvents);
        TextView tvClose = view.findViewById(R.id.tvClose);
        TextView tvNoEvents = view.findViewById(R.id.tvNoEventsFound);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rvChangeEvent.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        rvChangeEvent.setItemAnimator(new DefaultItemAnimator());

        if (eventList.size() > 0) {
            rvChangeEvent.setVisibility(View.VISIBLE);
            tvNoEvents.setVisibility(View.GONE);
        } else {
            rvChangeEvent.setVisibility(View.GONE);
            tvNoEvents.setVisibility(View.VISIBLE);
        }

        ChangeEventAdapter changeEventAdapter
                = new ChangeEventAdapter(this, eventList, mListener);
        rvChangeEvent.setAdapter(changeEventAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SheetEventClickListener) {
            mListener = (SheetEventClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SheetEventClickListener {
        void onSheetEventClicked(ChangeEventModel changeEventModel);
    }
}
