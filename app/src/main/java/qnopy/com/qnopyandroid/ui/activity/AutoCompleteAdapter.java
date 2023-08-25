package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    ArrayList<String> _items = new ArrayList<String>();
    ArrayList<String> orig = new ArrayList<String>();
    ArrayList<String> tempItems = new ArrayList<String>();

    Context ObjContext;

    public AutoCompleteAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.ObjContext = context;
        for (int i = 0; i < items.size(); i++) {
            orig.add(items.get(i));
        }
    }

    @Override
    public int getCount() {
        if (_items != null)
            return _items.size();
        else
            return 0;
    }

//    @Override
//    public String getItem(int arg0) {
//        return _items.get(arg0);
//    }
//    static class ViewHolder {
//        public TextView textv;
//
//    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View rowView = convertView;
//
//        String item_name = orig.get(position);
//        ViewHolder viewHolder;
//        if (rowView == null) {
//
////            LayoutInflater inflater = (Activity)ObjContext.getLayoutInflater();
////            rowView = LayoutInflater.from(ObjContext)
////                    .inflate(R.layout.autocomplete_item, parent, false);
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            rowView = inflater.inflate(R.layout.autocomplete_item, null);
//
//            viewHolder = new ViewHolder();
//            viewHolder.textv = (TextView) rowView.findViewById(R.id.item);
//
//            rowView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) rowView.getTag();
//        }
//
//        viewHolder.textv.setText(item_name);
//
//        return  rowView;
//    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint != null) {
                    System.out.println("KKKK" + "Constraints" + constraint.toString());
                } else {
                    System.out.println("KKKK" + "Constraints IS NULL");
                }
                FilterResults oReturn = new FilterResults();

                String temp;
                int counters = 0;
                if ((constraint != null) && (constraint.length() != 0)) {

                    tempItems.clear();
                    if (orig != null && orig.size() > 0) {
                        for (int i = 0; i < orig.size(); i++) {
                            temp = orig.get(i).toUpperCase();
                            if (temp.startsWith(constraint.toString().toUpperCase())) {
                                tempItems.add(orig.get(i));
                                counters++;
                            }
                        }
                    }

                    if (counters == 0) {
                        tempItems.clear();
//                      _items = orig;
                        tempItems.addAll(orig);
                        System.out.println("KKKK" + "counter=0" + " Adding all");
                    }
                    oReturn.values = tempItems;
                    oReturn.count = tempItems.size();
                } else {
                    tempItems.clear();
                    tempItems.addAll(orig);
                    oReturn.values = tempItems;
                    oReturn.count = tempItems.size();

                }
                System.out.println("KKKK" + "Adding allin else clause size=" + tempItems.size());
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                _items.clear();
                _items.addAll((ArrayList<String>) results.values);

                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
