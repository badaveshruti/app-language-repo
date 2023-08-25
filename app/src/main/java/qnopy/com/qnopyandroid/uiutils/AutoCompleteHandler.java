package qnopy.com.qnopyandroid.uiutils;

import android.util.Log;

import java.util.HashMap;

import qnopy.com.qnopyandroid.adapter.LocationdetailAdapter;

/**
 * Created by Saurabh on 25-Sep-15.
 */
public class AutoCompleteHandler {
    private static final String TAG = "AutoCompleteHandler";

    //get Comma(,)  seperated Values splitby ">"

    public static String getCommaSeperatedValues(HashMap<String, String> nameValuePair,String selKeys, String splitBy) {
        if (!selKeys.isEmpty()) {
            String[] seperatedkeys = selKeys.split(splitBy);
            String selectedValues = "";
            for (int i = 0; i < seperatedkeys.length; i++) {
                try {

                String value =nameValuePair.get(seperatedkeys[i]);
                Log.i("GetValuesfromKey", " Key:" + seperatedkeys[i] + " Value:" + value);
                    //19-Jan-16 if key not found for value then put it as is
                    if (value==null){
                        value=seperatedkeys[i];
                    }
                selectedValues = selectedValues + (selectedValues.isEmpty() ? "" : "|") + value;
                Log.i("GetValuesfromKey", "Comma Seperated Values:" + selectedValues);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("AutoCompleteHandler","getCommaSeperatedValues Error:"+e.getMessage());
                }
            }
            return selectedValues;
        }
        return null;
    }

    //To get semi col (;) seperated keys splitby ","
    public static String getCharSeperatedKeys(HashMap<String, String> nameValuePair,String selValues,String splitBy) {
        try {

            if (selValues!=null && !selValues.isEmpty()) {
                String[] seperatedvalues = selValues.split(splitBy);
                String selectedkeys = "";
                for (int i = 0; i < seperatedvalues.length; i++) {
                    String key=null;

                    if (seperatedvalues[i]!=null && !seperatedvalues[i].isEmpty()){
                        key = FormMaster.getKeyByValue(nameValuePair, seperatedvalues[i]);
                    }

                    Log.i("GetValuesfromKey", "Key:" + key + "Value:" + seperatedvalues[i]);

                    if (key==null){
                       key=seperatedvalues[i];
                    }
                    selectedkeys = selectedkeys + (selectedkeys.isEmpty()? "" : ";") + key;
                    Log.i(TAG, "getCharSeperatedKeys()"+ splitBy+"  Seperated Keys:" + selectedkeys);
                }
                return selectedkeys;
            }
        } catch (Exception e) {
            Log.e("AutoCompleteHandler", "getCharSeperatedKeys() Exception:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
