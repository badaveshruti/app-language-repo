/*
package qnopy.com.qnopyandroid.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ui.ReportView;

*/
/**
 * Created by QNOPY on 2/19/2018.
 *//*


public class labeladapter extends ArrayAdapter<String> implements Filterable {

    ReportView objContext;
    List<String> labelObjects;
    List<String> filteredlabelObjects;
    List<Integer> itemposition=new ArrayList<>();
    String TAG = "labeladapter";
    List<String> checkedList = new ArrayList<>();
    ViewHolder viewHolder;
    labelFilter sFilter = new labelFilter();
    boolean[] checkBoxState;
    boolean flag = false;


    public labeladapter(@NonNull ReportView context, int resource, List<String> objects, boolean flag) {
        super(context, resource, objects);
        this.objContext = context;
        this.labelObjects = objects;
        this.filteredlabelObjects = objects;
        checkBoxState = new boolean[objects.size()];
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return filteredlabelObjects.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return filteredlabelObjects.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            Log.i(TAG, "getView() First time Create view holder TAG Start:" + System.currentTimeMillis());

            LayoutInflater inflater = objContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.labelview, null);

            viewHolder = new ViewHolder();
            viewHolder.labeltext = (TextView) rowView.findViewById(R.id.labeltext);
            viewHolder.checklabel = (CheckBox) rowView.findViewById(R.id.checklabel);

            rowView.setTag(viewHolder);
            Log.i(TAG, "getView() First time Create view holder TAG End:" + System.currentTimeMillis());
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
            Log.i(TAG, "getView() get view holder TAG already created:" + System.currentTimeMillis());
        }

        viewHolder.labeltext.setText(filteredlabelObjects.get(position));

        if (objContext.isselectall) {
            viewHolder.checklabel.setChecked(true);
            // for(int i=0;i<filteredlabelObjects.size();i++)
            //  {
            //      checkedList.add(filteredlabelObjects.get(i));
            //  }
        } else {
            viewHolder.checklabel.setChecked(false);
        }

        if(checkedList!=null && checkedList.contains(position))
        {
            viewHolder.checklabel.setChecked(true);
        }

        if(objContext.isselectall)
        {
            if (checkedList.size()>=filteredlabelObjects.size())
            {

            }else
            {
                for(int i=0;i<filteredlabelObjects.size();i++)
                {
                    checkedList.add(filteredlabelObjects.get(i));
                }
            }
        }

        viewHolder.checklabel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

              if(!objContext.isselectall) {
                  if (b) {
                      //for(int i=0;i<filteredlabelObjects.size();i++)
                      // {
                      String labelname = filteredlabelObjects.get(position);
                      checkedList.add(filteredlabelObjects.get(position));
                      //   itemposition.add(position);
                      //    compoundButton.setChecked(true);
                      // }

                      //   notifyDataSetChanged();

                  } else {
                      String name = filteredlabelObjects.get(position);

                      for (int i = 0; i < checkedList.size(); i++) {
                          if (checkedList.get(i) == name) {
                              checkedList.remove(i);
                              //  itemposition.add(position);

                              //  notifyDataSetChanged();

                          }
                      }
                  }
              }
            }
        });
        return rowView;
    }

    public List<String> getLabelList() {
        if (checkedList == null) {

            return null;
        } else
            return checkedList;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        if (sFilter == null) {
            sFilter = new labelFilter();
        }
        return sFilter;
    }

    static class ViewHolder {
        public TextView labeltext;
        public CheckBox checklabel;

    }

    public class labelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            Log.i("locationAdapter", "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = labelObjects;
                results.count = labelObjects.size();
                Log.i("locationAdapter", "NoSearch Text ,Return Search Count:" + results.count);

            } else {

                String filterableString;
                ArrayList<String> filtered = new ArrayList<String>();

                for (String label : labelObjects) {
                    filterableString = label;
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(label);
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
                filteredlabelObjects = new ArrayList<String>();
                // notifyDataSetInvalidated();
            } else {
                filteredlabelObjects = (ArrayList<String>) results.values;
                Log.i("SiteAdapter", "FilteredSites:" + filteredlabelObjects);
                notifyDataSetChanged();
            }
        }
    }
}*/
