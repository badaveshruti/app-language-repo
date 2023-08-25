package qnopy.com.qnopyandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.versionedparcelable.VersionedParcel;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.ChangeEventModel;
import qnopy.com.qnopyandroid.ui.fragment.ChangeEventBottomSheetFragment;
import qnopy.com.qnopyandroid.util.Util;

public class ChangeEventAdapter extends RecyclerView.Adapter<ChangeEventAdapter.ViewHolder> {

    private final ChangeEventBottomSheetFragment.SheetEventClickListener mListener;
    private ChangeEventBottomSheetFragment mContext;
    private ArrayList<ChangeEventModel> listEvents;

    public ChangeEventAdapter(ChangeEventBottomSheetFragment mContext, ArrayList<ChangeEventModel> listEvents,
                              ChangeEventBottomSheetFragment.SheetEventClickListener listener) {
        this.mContext = mContext;
        this.listEvents = listEvents;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_change_event, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ChangeEventModel eventModel = listEvents.get(position);
        String displayName = eventModel.getDisplayName();
        try {
            displayName = eventModel.getDisplayName() + " - " + Util.getFormattedDate(Long.parseLong(eventModel.getDate()));
        } catch (VersionedParcel.ParcelException | NumberFormatException e) {
            e.printStackTrace();
        }
        viewHolder.tvEventName.setText(displayName);
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.edtEventName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onSheetEventClicked(listEvents.get(getAbsoluteAdapterPosition()));
                        mContext.dismiss();
                    }
                }
            });
        }
    }
}
