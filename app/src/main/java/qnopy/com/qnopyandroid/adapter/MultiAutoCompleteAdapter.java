package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class MultiAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private static final String TAG = "MultiAutoAdapter";
    ArrayList<String> _items = new ArrayList<String>();
    ArrayList<String> orig = new ArrayList<String>();
    ArrayList<String> tempItems = new ArrayList<String>();
    String displayValue = null;
    int count = 0;
    Context mContext;

    public MultiAutoCompleteAdapter(Context context, int resource, ArrayList<String> items, String displayValue) {
        super(context, resource, items);

        for (int i = 0; i < items.size(); i++) {
            orig.add(items.get(i));
        }

        this.displayValue = displayValue;
        mContext=context;
    }

    String[] splitItems() {
        String displayItems[] = null;
        displayItems = this.displayValue.split(", ");
        return displayItems;
    }

    @Override
    public int getCount() {
        if (tempItems != null)
            return tempItems.size();
        else
            return 0;
    }

    @Override
    public String getItem(int arg0) {
        /*String[] displayItems = splitItems();
    	if(displayItems != null) {
    		if(count >= displayItems.length) {
    			count = 0;
    		}
    		if(_items.get(arg0).equalsIgnoreCase(displayItems[count])) {
    			count = 0;
    			return null;
    		}
    		count = count + 1;
    		return _items.get(arg0);
    	}
    	count = 0;*/
        String res = "";
        try {
            res = tempItems.get(arg0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		
    	return parent;
    }*/

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint != null) {
                    Log.i(TAG, "getFilter Constraints:" + constraint.toString());
                } else {
                    Log.i(TAG, "getFilter Constraints is NULL");

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
                     //   System.out.println("KKKK" + "counter=0" + " Adding all");
                    }
                    oReturn.values = tempItems;
                    oReturn.count = tempItems.size();
                } else {
                    tempItems.clear();
                    tempItems.addAll(orig);
                    oReturn.values = tempItems;
                    oReturn.count = tempItems.size();

                }
                Log.i(TAG, "Adding all in else clause size=" + tempItems.size());
                return oReturn;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//        	 String pos = null;

                // Now we have to inform the adapter about the new list filtered
//               if (results.count == 0) {
//                   tempItems.clear();
//                   notifyDataSetInvalidated();
//               } else {
//                   tempItems = (ArrayList<String>) results.values;
//                   Log.i(TAG, "FilteredItems:" + tempItems);
//                   notifyDataSetChanged();
//               }
                _items.clear();
        	 /*if(displayValue != null) {
	        	 for(int i=0; i<((ArrayList<String>) results.values).size(); i++) {
	        		 if(displayValue.contains(((ArrayList<String>) results.values).get(i))) {
	        			 pos = (String) ((ArrayList<String>) results.values).get(i);
	        			 break;
	        		 }
	        	 }
	        	 if(pos != null) {
	        		 ((ArrayList<String>) results.values).remove(pos);
	        	 }
        	 }*/

                if (results != null && results.count > 0) {
                    _items.addAll((ArrayList<String>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
                notifyDataSetChanged();

            }

        };

        return filter;

    }


}
