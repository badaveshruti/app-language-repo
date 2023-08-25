package qnopy.com.qnopyandroid.customWidgets;

//import org.xml.sax.Parser;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;

import java.util.HashMap;
import java.util.LinkedHashSet;

import qnopy.com.qnopyandroid.adapter.LocationdetailAdapter;
import qnopy.com.qnopyandroid.uiutils.AutoCompleteHandler;

public class FBMultiAutoComplete extends androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView {

    Context ObjectContext;
    HashMap<String, String> nameValuePaire;
    LinkedHashSet<String> keySet;

    LocationdetailAdapter adaptercontext;

    public FBMultiAutoComplete(Context context, HashMap<String, String> nameValuePaire) {
        super(context);

        ObjectContext = context;
        this.nameValuePaire = nameValuePaire;
        keySet = new LinkedHashSet<>();
        this.setTextSize(16);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    public void setLocationAdapterContext(LocationdetailAdapter context) {
        this.adaptercontext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);
        System.out.println("FBMultiAutoComplete onTouchEvent:" + getText() + "instance" + this.getText());
        performFiltering(getText(), 0);
        return true;
    }

    public void initialSetUp() {

        final FBMultiAutoComplete autCompleteContext = this;
        autCompleteContext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FBMultiAutoComplete", "i am in ClickListener");

                String displayValue = (String) ((MultiAutoCompleteTextView) v).getText().toString();
                displayValue = displayValue + (displayValue.isEmpty() || ((displayValue.charAt(displayValue.length() - 1)) + "").equals(";") ? "" : ";");
                autCompleteContext.setText(displayValue);
                //set Cursor to end of String
                autCompleteContext.setSelection(autCompleteContext.getText().length());
            }
        });

        this.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String itemClicked = parent.getAdapter().getItem(position).toString();
                keySet = new LinkedHashSet<>();

                String posStr = (String) autCompleteContext.getTag();
                String selectedKeys = (String) autCompleteContext.getText().toString();
                int lenth = selectedKeys.length();

                String[] keys = selectedKeys.split(";");
                for (int i = 0; i<keys.length; i++) {
                    keySet.add(keys[i]);

                }

                // TODO: 27-Apr-16 Skip ALready Added Item
//                if (selectedKeys.contains(itemClicked)) {
//                    Log.i("FBMultiAutoComplte", itemClicked + " Already Added");
//                    int trimUpto = lenth - (itemClicked.length() + 1);
//                    selectedKeys = selectedKeys.substring(0, trimUpto);
//                }

                selectedKeys = "";
                for (String s : keySet) {
                    selectedKeys = selectedKeys + s + ";";

                }

                if (selectedKeys.lastIndexOf(";") == (lenth - 1) && lenth != 0)//Check whether ";" is last char or not
                {
                    selectedKeys = selectedKeys.substring(0, selectedKeys.lastIndexOf(";"));
                }
                autCompleteContext.setText(selectedKeys);
                int pos = Integer.parseInt(posStr);
                //set Cursor to end of String
                autCompleteContext.setSelection(autCompleteContext.getText().length());

                //TODO: 24-Sep-15 Save Values with comma(,) seperated instead of item_display_name with semicol(;) seperated
                String selectedValues = AutoCompleteHandler.getCommaSeperatedValues(nameValuePaire, selectedKeys, ";");
                Log.i("MultiAuto_onItemclick", "Selectedkeys:" + selectedKeys + " Values:" + selectedValues);
//                autCompleteContext.adaptercontext.updateData(posStr, selectedValues, pos);



            }
        });


        this.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                String posStr = (String) v.getTag();
                String selectedKeys = (String) ((MultiAutoCompleteTextView) v).getText().toString();

                int lenth = selectedKeys.length();
                if (selectedKeys.lastIndexOf(";") == (lenth - 1) && lenth != 0)//Check whether ";" is last char or not
                {
                    selectedKeys = selectedKeys.substring(0, selectedKeys.lastIndexOf(";"));
                }
                //  autCompleteContext.setText(selectedKeys);
                int pos = Integer.parseInt(posStr);
                //set Cursor to end of String
                // autCompleteContext.setSelection(autCompleteContext.getText().length());

                //TODO: 24-Sep-15 Save Values with comma(,) seperated instead of item_display_name with semi col(;) seperated
                String selectedValues = AutoCompleteHandler.getCommaSeperatedValues(nameValuePaire, selectedKeys, ";");
                Log.i("MultiAuto_onFocusChange", "Selectedkeys:" + selectedKeys + " Values:" + selectedValues);
//                autCompleteContext.adaptercontext.updateOnFocusChange(hasFocus, posStr, selectedValues);

            }
        });
    }
}
