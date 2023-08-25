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
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;

public class MandatoryFormsAdapter extends RecyclerView.Adapter<MandatoryFormsAdapter.ViewHolder> {

    private ArrayList<RequiredFieldRowItem> mandatoryLocationList = new ArrayList<>();
    private Context mContext;

    public MandatoryFormsAdapter(ArrayList<RequiredFieldRowItem> mandatoryLocList,
                                 Context context) {
        this.mandatoryLocationList = mandatoryLocList;
        this.mContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forms_fields_count, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        RequiredFieldRowItem location = mandatoryLocationList.get(position);
        holder.tvLocationName.setText(location.getTitle());
        holder.tvFieldCount.setText("Mandatory Field Count:" + location.getCount());
    }

    @Override
    public int getItemCount() {
        return mandatoryLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLocationName;
        private final TextView tvFieldCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvFieldCount = itemView.findViewById(R.id.tvRemainingFieldsCount);
        }
    }
}
