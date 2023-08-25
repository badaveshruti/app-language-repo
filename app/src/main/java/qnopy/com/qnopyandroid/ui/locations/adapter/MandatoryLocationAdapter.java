package qnopy.com.qnopyandroid.ui.locations.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.db.FieldDataSource;

public class MandatoryLocationAdapter
        extends RecyclerView.Adapter<MandatoryLocationAdapter.ViewHolder> {

    private ArrayList<Location> mandatoryLocationList = new ArrayList<>();
    private Context mContext;
    private String rollAppId;
    private String eventId;

    public MandatoryLocationAdapter(ArrayList<Location> mandatoryLocList,
                                    Context context, String rollIntoAppId, String eventID) {
        this.mandatoryLocationList = mandatoryLocList;
        this.mContext = context;
        this.rollAppId = rollIntoAppId;
        this.eventId = eventID;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mandatory_location_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Location location = mandatoryLocationList.get(position);
        holder.tvLocationName.setText(location.getLocationName());

        setRecyclerView(holder, location);
    }

    private void setRecyclerView(ViewHolder holder, Location loc) {
        FieldDataSource fieldDataSource = new FieldDataSource(mContext);
        ArrayList<RequiredFieldRowItem> mandatoryFieldList =
                fieldDataSource.getMandatoryFieldListByLocation(rollAppId + "",
                        eventId + "", loc.getSiteID() + "",
                        loc.getLocationID());

        if (!mandatoryFieldList.isEmpty()) {
            MandatoryFormsAdapter adapter = new MandatoryFormsAdapter(mandatoryFieldList, mContext);
            holder.rvMandatoryForms.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return mandatoryLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLocationName;
        private final RecyclerView rvMandatoryForms;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            rvMandatoryForms = itemView.findViewById(R.id.rvMandatoryFormsFields);
        }
    }
}
