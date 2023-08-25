package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pchmn.materialchips.util.LetterTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.ui.fragment.SiteFragment.OnSiteClickListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Site} and makes a call to the
 * specified {@link OnSiteClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySitelistRecyclerViewAdapter extends RecyclerView
        .Adapter<MySitelistRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private final OnSiteClickedListener siteClickListener;
    SiteFilter sFilter = new SiteFilter();
    Context context;
    private List<Site> siteObjects;
    private List<Site> filteredSiteObjects;
    private OnSiteClickListener mListener;
    String TAG = "SiteListAdapter";

    public MySitelistRecyclerViewAdapter(Context context, List<Site> objects,
                                         OnSiteClickListener listener,
                                         OnSiteClickedListener siteClickedListener) {

        siteObjects = new ArrayList<Site>();
        this.siteObjects = objects;
        this.filteredSiteObjects = objects;
        this.context = context;
        mListener = listener;
        siteClickListener = siteClickedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = filteredSiteObjects.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mTitleView.setText(holder.mItem.getSiteName());
        holder.mAvator.setImageBitmap(new LetterTileProvider(context).getCircularLetterTile(holder.mItem.getSiteName()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSiteClicked(holder.mItem);
                    if (siteClickListener != null)
                        siteClickListener.OnSiteClicked(holder.mItem.getSiteName(), holder.mItem.getSiteID());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredSiteObjects.size();
    }

    @Override
    public Filter getFilter() {
        if (sFilter == null) {
            sFilter = new SiteFilter();
        }
        return sFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvator;
        public final TextView mTitleView;
        public Site mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvator = (ImageView) view.findViewById(R.id.avator_iv);
            mTitleView = (TextView) view.findViewById(R.id.title_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }


    public class SiteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i(TAG, "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = siteObjects;
                results.count = siteObjects.size();
                Log.i(TAG, "NoSearch Text ,Return Search Count:" + results.count);

            } else {

                String filterableString;
                ArrayList<Site> filtered = new ArrayList<Site>();

                for (Site site : siteObjects) {
                    filterableString = site.getSiteName();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(site);
                    }
                }
                results.values = filtered;
                results.count = filtered.size();

                Log.i(TAG, "Searched Text >Return Search Count:" + results.count);

            }
            //final ArrayList<String> nlist = new ArrayList<String>(count);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredSiteObjects = new ArrayList<Site>();
                notifyDataSetChanged();
            } else {
                filteredSiteObjects = (ArrayList<Site>) results.values;
                Log.i(TAG, "FilteredSites:" + filteredSiteObjects);
                notifyDataSetChanged();
            }
        }
    }

    public interface OnSiteClickedListener {
        void OnSiteClicked(String siteName, int siteId);
    }
}
