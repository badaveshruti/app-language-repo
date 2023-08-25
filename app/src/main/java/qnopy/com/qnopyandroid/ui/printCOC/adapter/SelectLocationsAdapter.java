package qnopy.com.qnopyandroid.ui.printCOC.adapter;

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

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.customView.CustomTextView;

public class SelectLocationsAdapter extends RecyclerView.Adapter<SelectLocationsAdapter.ViewHolder>
        implements Filterable {

    private ArrayList<Location> listLocation;
    private ArrayList<Location> filteredListLocation;
    private Context mContext;
    private LocationFilter locFilter;

    public SelectLocationsAdapter(ArrayList<Location> listLocation, Context mContext) {
        this.listLocation = listLocation;
        this.filteredListLocation = listLocation;
        this.mContext = mContext;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_selectable_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Location location = filteredListLocation.get(position);

        holder.tvFieldLabel.setText(location.getLocationName());
        setFieldParamSelected(holder, location);
    }

    private void setFieldParamSelected(ViewHolder holder, Location location) {
        if (location.isSelected()) {
            holder.ivTick.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_check_blue));
        } else {
            holder.ivTick.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_check_white));
        }
    }

    @Override
    public int getItemCount() {
        return filteredListLocation.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView tvFieldLabel;
        private final ImageView ivTick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldLabel = itemView.findViewById(R.id.tvFieldParamName);
            ivTick = itemView.findViewById(R.id.ivTick);

            itemView.setOnClickListener(v -> {
                Location loc = filteredListLocation.get(getAbsoluteAdapterPosition());
                if (loc.isSelected()) {
                    filteredListLocation.get(getAbsoluteAdapterPosition()).setSelected(false);
                } else {
                    filteredListLocation.get(getAbsoluteAdapterPosition()).setSelected(true);
                }

                setFieldParamSelected(this, loc);
            });
        }
    }

    public ArrayList<Location> getSelectedItemsList() {
        Collection<Location> list = Collections2.filter(filteredListLocation, Location::isSelected);
        return Lists.newArrayList(list);
    }

    public void selectAll(boolean isSelectAll) {
        for (Location location : filteredListLocation) {
            location.setSelected(isSelectAll);
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (locFilter == null) {
            locFilter = new LocationFilter();
        }
        return locFilter;
    }

    public class LocationFilter extends Filter {

        private static final String TAG = "LocationFilter";

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i(TAG, "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = listLocation;
                results.count = listLocation.size();
                Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);

            } else {

                String filterableString;
                ArrayList<Location> filtered = new ArrayList<>();

                for (Location loc : listLocation) {
                    filterableString = loc.getLocationName();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(loc);
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
                filteredListLocation = new ArrayList<>();
            } else {
                filteredListLocation = (ArrayList<Location>) results.values;
            }
            notifyDataSetChanged();
        }
    }

}
