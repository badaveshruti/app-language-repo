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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Lov;

/**
 * Created by QNOPY on 5/4/2018.
 */

public class LovAdapter extends ArrayAdapter<Lov> implements Filterable{

    List<Lov> lovObjects;
    List<Lov> filteredlovObjects;
    private  Context ObjContext;
    LovFilter lFilter = new LovFilter();


    public LovAdapter(@NonNull Context context, int resource, List<Lov> lovList) {
        super(context, resource, lovList);
        this.ObjContext = context;
        lovObjects = new ArrayList<Lov>();
        this.lovObjects = lovList;
        this.filteredlovObjects = lovList;
    }

    @Override
    public int getCount() {
        return filteredlovObjects.size();
    }

    @Nullable
    @Override
    public Lov getItem(int position) {
        return filteredlovObjects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lov lov = new Lov();
        lov = filteredlovObjects.get(position);
        ViewHolder viewHolder;
        //Location
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ObjContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.lovlist_item, null);

            viewHolder = new ViewHolder();
            viewHolder.lovtv = (TextView) convertView.findViewById(R.id.lovtitle_tv);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.go_iv);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lovtv.setText(lov.getLovName());
       // viewHolder.im.setImageBitmap(new LetterTileProvider(ObjContext).getCircularLetterTile(site.getSiteName()));

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (lFilter == null) {
            lFilter = new LovFilter();
        }

        return lFilter;

    }

    static class ViewHolder {
        public  TextView lovtv;
        public ImageView img;
        public  View mView;

    }

    public class LovFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i("lovAdapter", "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = lovObjects;
                results.count = lovObjects.size();
                Log.i("lovAdapter", "NoSearch Text ,Return Search Count:" + results.count);

            } else {

                String filterableString;
                ArrayList<Lov> filtered = new ArrayList<Lov>();

                for (Lov lov : lovObjects) {
                    filterableString = lov.getLovName();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(lov);
                    }

                }
                results.values = filtered;
                results.count = filtered.size();

                Log.i("lovAdapter", "Searched Text >Return Search Count:" + results.count);

            }
            //final ArrayList<String> nlist = new ArrayList<String>(count);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredlovObjects = new ArrayList<Lov>();
                notifyDataSetInvalidated();
            } else {
                filteredlovObjects = (ArrayList<Lov>) results.values;
                Log.i("SiteAdapter", "FilteredSites:" + filteredlovObjects);
                notifyDataSetChanged();
            }
        }
    }
}
