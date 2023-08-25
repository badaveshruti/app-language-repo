package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.pchmn.materialchips.util.LetterTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Site;

/**
 * Created by Yogendra on 03-Jan-17.
 */

public class SiteAdapter extends ArrayAdapter<Site> implements Filterable {

    List<Site> siteObjects;
    List<Site> filteredSiteObjects;
    private  Context ObjContext;
    SiteFilter sFilter = new SiteFilter();

    public SiteAdapter(Context context, int resource, List<Site> objects) {
        super(context, resource, objects);

        this.ObjContext = context;
        siteObjects = new ArrayList<Site>();
        this.siteObjects = objects;
        this.filteredSiteObjects = objects;
    }

    @Override
    public int getCount() {
        return filteredSiteObjects.size();
    }

    @Nullable
    @Override
    public Site getItem(int position) {
        return filteredSiteObjects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Site site = new Site();
        site = filteredSiteObjects.get(position);
        ViewHolder viewHolder;
        //Location
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ObjContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.sitetv = (TextView) convertView.findViewById(R.id.title_tv);
            viewHolder.avator = (ImageView) convertView.findViewById(R.id.avator_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.sitetv.setText(site.getSiteName());
        viewHolder.avator.setImageBitmap(new LetterTileProvider(ObjContext).getCircularLetterTile(site.getSiteName()));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (sFilter == null) {
            sFilter = new SiteFilter();
        }

        return sFilter;
    }

    static class ViewHolder {
        public  TextView sitetv;
        public  ImageView avator;
        public  View mView;
    }

    public class SiteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i("locationAdapter", "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = siteObjects;
                results.count = siteObjects.size();
                Log.i("locationAdapter", "NoSearch Text ,Return Search Count:" + results.count);

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

                Log.i("locationAdapter", "Searched Text >Return Search Count:" + results.count);

            }
            //final ArrayList<String> nlist = new ArrayList<String>(count);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredSiteObjects = new ArrayList<Site>();
                notifyDataSetInvalidated();
            } else {
                filteredSiteObjects = (ArrayList<Site>) results.values;
                Log.i("SiteAdapter", "FilteredSites:" + filteredSiteObjects);
                notifyDataSetChanged();
            }
        }
    }
}
