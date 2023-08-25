package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.FieldParamInfo;
import qnopy.com.qnopyandroid.customView.CustomTextView;

public class ReportFieldsSelectionAdapter extends
        RecyclerView.Adapter<ReportFieldsSelectionAdapter.ViewHolder> implements Filterable {

    private ArrayList<FieldParamInfo> fieldParamsList;
    private ArrayList<FieldParamInfo> filteredFieldParamsList;
    private Context mContext;
    private FieldFilter fieldFilter;

    public ReportFieldsSelectionAdapter(ArrayList<FieldParamInfo> fieldParamList, Context context) {
        this.fieldParamsList = fieldParamList;
        this.filteredFieldParamsList = fieldParamList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_selectable_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FieldParamInfo fieldData = filteredFieldParamsList.get(position);

        holder.tvFieldLabel.setText(fieldData.getFieldParameterLabel());
        setFieldParamSelected(holder, fieldData);
    }

    private void setFieldParamSelected(ViewHolder holder, FieldParamInfo fieldData) {
        if (fieldData.isSelected()) {
            holder.ivTick.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_check_blue));
        } else {
            holder.ivTick.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_check_white));
        }
    }

    @Override
    public int getItemCount() {
        return filteredFieldParamsList.size();
    }

    @Override
    public Filter getFilter() {
        if (fieldFilter == null) {
            fieldFilter = new FieldFilter();
        }
        return fieldFilter;
    }

    public ArrayList<FieldParamInfo> getSelectedItemsList() {
        ArrayList<FieldParamInfo> fieldParamInfos = new ArrayList<>();
        for (FieldParamInfo field : filteredFieldParamsList) {
            if (field.isSelected())
                fieldParamInfos.add(field);
        }

        if (fieldParamInfos.size() > 0)
            return fieldParamInfos;
        else
            return filteredFieldParamsList;
    }

    public ArrayList<FieldParamInfo> getItemsList() {
        return filteredFieldParamsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView tvFieldLabel;
        private final ImageView ivTick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldLabel = itemView.findViewById(R.id.tvFieldParamName);
            ivTick = itemView.findViewById(R.id.ivTick);

            itemView.setOnClickListener(v -> {
                FieldParamInfo fieldData = filteredFieldParamsList.get(getAdapterPosition());
                if (fieldData.isSelected()) {
                    filteredFieldParamsList.get(getAdapterPosition()).setSelected(false);
                } else {
                    filteredFieldParamsList.get(getAdapterPosition()).setSelected(true);
                }

                setFieldParamSelected(this, fieldData);
            });
        }
    }

    public class FieldFilter extends Filter {

        private static final String TAG = "FieldFilter";

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i(TAG, "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = fieldParamsList;
                results.count = fieldParamsList.size();
                Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);

            } else {

                String filterableString;
                ArrayList<FieldParamInfo> filtered = new ArrayList<>();

                for (FieldParamInfo field : fieldParamsList) {
                    filterableString = field.getFieldParameterLabel();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(field);
                    }
                }
                results.values = filtered;
                results.count = filtered.size();
                Log.i(TAG, "Searched Text >Return Search Count:" + results.count);
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredFieldParamsList = new ArrayList<>();
            } else {
                filteredFieldParamsList = (ArrayList<FieldParamInfo>) results.values;
            }
            notifyDataSetChanged();
        }
    }
}
