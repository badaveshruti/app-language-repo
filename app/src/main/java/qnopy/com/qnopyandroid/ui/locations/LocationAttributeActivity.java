package qnopy.com.qnopyandroid.ui.locations;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.CustomExpandableListAdapter;
import qnopy.com.qnopyandroid.clientmodel.location_attribute_child_row;
import qnopy.com.qnopyandroid.clientmodel.location_attribute_group;
import qnopy.com.qnopyandroid.db.LocationAttributeDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class LocationAttributeActivity extends ProgressDialogActivity {
    private static final String TAG = "LocationAttribute";
    ExpandableListView expandableListView;
    TextView empty_tv;
    Button clearButton;
    CustomExpandableListAdapter expandableListAdapter;
    List<location_attribute_group> expandableListTitle;
    public static List<location_attribute_group> newExpandableListTitle;

    List<String> groupTitle;
    List<String> newGroupTitle;
    HashMap<String, List<location_attribute_child_row>> expandableListDetail;
    HashMap<String, List<location_attribute_child_row>> newExpandableListDetail;

    HashMap<String, String> hashMap = new HashMap<>();
    HashMap<String, String> outputMap;
    HashMap<String, String> hashMapToUncheckGroup = new HashMap<>();
    List<location_attribute_child_row> mMultipleSelectedAttributes = new ArrayList<>();

    Context context;
    ActionBar actionBar;
    static String SELECTED_ITEM_VALUE = null, SELECTED_ITEM_NAME = null;
    String siteID;
    Menu mMenu;

    EditText mEditTextSearchLocationUsingAttribute;
    ImageView mImageViewAttributeSearch;
    TextView mTextViewCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_attribute);

        context = this;

        expandableListView = findViewById(R.id.expandableListView);
        empty_tv = findViewById(R.id.empty_attrinute_tv);
        clearButton = findViewById(R.id.clear_btn);
        mEditTextSearchLocationUsingAttribute = findViewById(R.id.editTextSearchLocationusingAttributes);
        mImageViewAttributeSearch = findViewById(R.id.imageViewAttributeSearch);
        mTextViewCancel = findViewById(R.id.textViewCancel);

        final View parentEditSearch = (View) mEditTextSearchLocationUsingAttribute.getParent();
        parentEditSearch.post( new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                mEditTextSearchLocationUsingAttribute.getHitRect(rect);
                rect.top -= 100;    // increase top hit area
                rect.left -= 100;   // increase left hit area
                rect.bottom += 100; // increase bottom hit area
                rect.right += 100;  // increase right hit area
                parentEditSearch.setTouchDelegate( new TouchDelegate( rect , mEditTextSearchLocationUsingAttribute));
            }
        });

        final View parent = (View) mTextViewCancel.getParent();
        parent.post( new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                mTextViewCancel.getHitRect(rect);
                rect.top -= 100;    // increase top hit area
                rect.left -= 100;   // increase left hit area
                rect.bottom += 100; // increase bottom hit area
                rect.right += 100;  // increase right hit area
                parent.setTouchDelegate( new TouchDelegate( rect , mTextViewCancel));
            }
        });

        final View parentSearch = (View) mImageViewAttributeSearch.getParent();
        parentSearch.post( new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                mImageViewAttributeSearch.getHitRect(rect);
                rect.top -= 100;    // increase top hit area
                rect.left -= 100;   // increase left hit area
                rect.bottom += 100; // increase bottom hit area
                rect.right += 100;  // increase right hit area
                parentSearch.setTouchDelegate( new TouchDelegate( rect , mImageViewAttributeSearch));
            }
        });


        siteID = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID);

        expandableListDetail = new LocationAttributeDataSource(context).getData(siteID);
        groupTitle = new ArrayList<String>(expandableListDetail.keySet());


       /* final String last_selected_attribute_value = Util.getSharedPreferencesProperty(context, GlobalStrings.LAST_LOCATION_ATTRIBUTE_VALUE + "_" + siteID);
        String last_selected_attribute_name = Util.getSharedPreferencesProperty(context, GlobalStrings.LAST_LOCATION_ATTRIBUTE_NAME + "_" + siteID);

        Log.i(TAG, "Last selected Attribute:" + last_selected_attribute_value+" for site:"+siteID);
        if (last_selected_attribute_value == null) {
            clearButton.setVisibility(View.GONE);
        }*/

        setCheckedList();//last_selected_attribute_name,last_selected_attribute_value
        setAdapter();

        mEditTextSearchLocationUsingAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextSearchLocationUsingAttribute.setCursorVisible(true);
            }
        });

        mImageViewAttributeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mEditTextSearchLocationUsingAttribute.setFocusable(true);
                mEditTextSearchLocationUsingAttribute.setEnabled(true);
                mEditTextSearchLocationUsingAttribute.setFocusableInTouchMode(true);*/
                mEditTextSearchLocationUsingAttribute.setCursorVisible(true);
            }
        });

        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextSearchLocationUsingAttribute.setText("");
                mEditTextSearchLocationUsingAttribute.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view == null) {
                    view = new View(getApplicationContext());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                setAdapter();
                mTextViewCancel.setVisibility(View.GONE);
            }
        });
        mEditTextSearchLocationUsingAttribute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

                mTextViewCancel.setVisibility(View.VISIBLE);
                newExpandableListDetail = new LocationAttributeDataSource(context).getSearchedData(siteID, String.valueOf(s));
                newGroupTitle = new ArrayList<String>(newExpandableListDetail.keySet());

                newExpandableListTitle = new ArrayList<>();

                for (String key : newGroupTitle) {
                    location_attribute_group grp = new location_attribute_group();
                    grp.setTitle(key);
                    grp.setSelected(false);

                    ArrayList<location_attribute_child_row> child_list =
                            (ArrayList<location_attribute_child_row>) newExpandableListDetail.get(key);

                    for (location_attribute_child_row row : child_list) {

                       /* if (outputMap.isEmpty()){

                        }else {
                            for(Map.Entry<String, String> entry : outputMap.entrySet()) {
                                String attrName = entry.getKey();
                                String attrValue = entry.getValue();

                                String [] attributeName = attrName.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                                String Name = attributeName[0];

                                if ((Name.equalsIgnoreCase(key)) && (row.getChild_title().equalsIgnoreCase(attrValue))) {
                                    row.setSelected(true);
                                    grp.setSelected(true);
                                    clearButton.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        }*/

                        /*if (last_selected_attribute_value != null && row.getChild_title().equalsIgnoreCase(last_selected_attribute_value)) {
                            row.setSelected(true);
                            grp.setSelected(true);
                            clearButton.setVisibility(View.VISIBLE);
                            break;
                        }*/
                    }
                    newExpandableListTitle.add(grp);
                }
                if (newExpandableListTitle.size() > 0) {

                    Collections.sort(newExpandableListTitle, new Comparator<location_attribute_group>() {
                        @Override
                        public int compare(location_attribute_group lhs, location_attribute_group rhs) {

                            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                        }
                    });

                    Log.e("AttrFilter", "afterTextChanged: "+newExpandableListDetail.size());

                    expandableListAdapter = new CustomExpandableListAdapter(context,
                            newExpandableListTitle, newExpandableListDetail);
                    expandableListView.setAdapter(expandableListAdapter);
                    expandableListView.setVisibility(View.VISIBLE);
                    empty_tv.setVisibility(View.GONE);
                    expandableListAdapter.notifyDataSetChanged();
                } else {
                    empty_tv.setVisibility(View.VISIBLE);
                    expandableListView.setVisibility(View.GONE);
                    clearButton.setVisibility(View.GONE);
                }

            }
        });

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select Field Point Attribute");
        }


        clearButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                /*
                //unselect all others
                expandableListDetail = expandableListAdapter.getchildList();
                expandableListTitle = expandableListAdapter.getGroupList();

                removeCheckedItems(expandableListTitle, expandableListDetail);

                hashMap.clear();
                SharedPreferences settings = getSharedPreferences("MULTIPLEATTRIBUTE", Context.MODE_PRIVATE);
                settings.edit().clear().commit();

                expandableListAdapter.notifyDataSetChanged();
                SELECTED_ITEM_VALUE = null;
                SELECTED_ITEM_NAME = null;
                clearButton.setVisibility(View.GONE);
*/

                //todo storing selected attr  using hash map in shared preference
                SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
                if (pSharedPref != null){

                    if (hashMap.size() > 0){
                        JSONObject jsonObject = new JSONObject(hashMap);
                        String jsonString = jsonObject.toString();
                        SharedPreferences.Editor editor = pSharedPref.edit();
                        editor.remove("AttributeHashMap").commit();
                        editor.putString("AttributeHashMap", jsonString);
                        editor.commit();
                    }else if (hashMap.size() == 0 && outputMap.size() > 0){
                        JSONObject jsonObject = new JSONObject(outputMap);
                        String jsonString = jsonObject.toString();
                        SharedPreferences.Editor editor = pSharedPref.edit();
                        editor.remove("AttributeHashMap").commit();
                        editor.putString("AttributeHashMap", jsonString);
                        editor.commit();
                    }
                }
                finish();

            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                location_attribute_child_row row = expandableListAdapter.getChild(groupPosition, childPosition);
                location_attribute_group group = expandableListAdapter.getGroup(groupPosition);
                Log.e("childCount", "onChildClick: "+groupPosition+" child:--- "+childPosition);
                //unselect all Childs and groups
                expandableListDetail = expandableListAdapter.getchildList();
                expandableListTitle = expandableListAdapter.getGroupList();

                SELECTED_ITEM_VALUE = row.getChild_title();
                SELECTED_ITEM_NAME = group.getTitle();

                /*row.setSelected(true);
                group.setSelected(true);
                expandableListAdapter.notifyDataSetChanged();

                clearButton.setVisibility(View.VISIBLE);*/

                //HashMap<String, String> outputMap = new HashMap<>();
                SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
                try{
                    if (pSharedPref != null){
                        String jsonString = pSharedPref.getString("AttributeHashMap", (new JSONObject()).toString());
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Iterator<String> keysItr = jsonObject.keys();
                        while(keysItr.hasNext()) {
                            String k = keysItr.next();
                            String vl = (String) jsonObject.get(k);
                            hashMap.put(k,vl);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                    String attributeKey = row.getTitle()+"|"+row.getChild_title();
                    if (!hashMap.containsKey(attributeKey)){ //hashMap.containsKey(row.getTitle()+row.getChild_title())


                    row.setSelected(true);
                    //group.setSelected(true);

                    mMultipleSelectedAttributes.add(row);

                    //hashMap.put(row.getTitle()+row.getChild_title(), row.getChild_title());
                    hashMap.put(attributeKey, row.getChild_title());

                        for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                            String AttributeName = entry.getKey();

                            StringTokenizer st = new StringTokenizer(AttributeName, "|");
                            String Name = st.nextToken();

                            if (Name.equals(row.getTitle())){
                                GlobalStrings.mHashMapContainsSameAttributeKey ++;
                            }else {
                                GlobalStrings.mHashMapContainsSameAttributeKey = 0;
                                break;
                            }
                        }

                    hashMapToUncheckGroup.put(row.getTitle()+row.getChild_title(), row.getChild_title());

                    //removeCheckedItems(expandableListTitle, expandableListDetail);
                    //removeSelectedAttribute(mMultipleSelectedAttributes, SELECTED_ITEM_VALUE, SELECTED_ITEM_NAME);

                    //Log.e("multipleAttrSelection", "onChildClick:Size:-  "+ mMultipleSelectedAttributes.size()+" selected attribute value:- "+row.getChild_title()+" child select status:- "+row.isSelected()+" attribute name:- "+row.getTitle()+" group select status:- "+group.isSelected() );
                    expandableListAdapter.notifyDataSetChanged();

                    clearButton.setVisibility(View.VISIBLE);
                }else {
                    row.setSelected(false);
                    //hashMap.remove(row.getTitle()+row.getChild_title());
                    hashMap.remove(attributeKey);
                    //group.setSelected(false);
                        if (pSharedPref != null){

                            pSharedPref.edit().clear().commit();
                            JSONObject jsonObject = new JSONObject(hashMap);
                            String jsonString = jsonObject.toString();
                            SharedPreferences.Editor editor = pSharedPref.edit();
                            editor.remove("AttributeHashMap").commit();
                            editor.putString("AttributeHashMap", jsonString);
                            editor.commit();
                        }

                    expandableListAdapter.notifyDataSetChanged();

                    if (hashMap.size() == 0){
                        clearButton.setVisibility(View.GONE);
                    }
                }

                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("AttributeHashMap", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if (outputMap.isEmpty()){
            clearButton.setVisibility(View.GONE);
        }else {
            clearButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.location_attribute_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.action_save).setVisible(false);
        if (empty_tv != null) {

            if ( empty_tv.getVisibility()==View.VISIBLE){
                mMenu.findItem(R.id.action_save).setVisible(false);
                mMenu.findItem(R.id.action_cancel).setVisible(false);

            }else {
                //mMenu.findItem(R.id.action_save).setVisible(true);
                mMenu.findItem(R.id.action_clear).setVisible(true);
                mMenu.findItem(R.id.action_cancel).setVisible(true);

            }

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
               // Util.setSharedPreferencesProperty(context, GlobalStrings.LAST_LOCATION_ATTRIBUTE_VALUE + "_" + siteID, SELECTED_ITEM_VALUE);
               // Util.setSharedPreferencesProperty(context, GlobalStrings.LAST_LOCATION_ATTRIBUTE_NAME + "_" + siteID, SELECTED_ITEM_NAME);

                /*//todo storing selected attr  using hash map in shared preference
                SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
                if (pSharedPref != null){
                    JSONObject jsonObject = new JSONObject(hashMap);
                    String jsonString = jsonObject.toString();
                    SharedPreferences.Editor editor = pSharedPref.edit();
                    editor.remove("AttributeHashMap").commit();
                    editor.putString("AttributeHashMap", jsonString);
                    editor.commit();
                }
                finish();*/

                return true;

            case R.id.action_clear:

                clearButton.setVisibility(View.GONE);

                GlobalStrings.mHashMapContainsSameAttributeKey = 0;

                expandableListDetail = expandableListAdapter.getchildList();
                expandableListTitle = expandableListAdapter.getGroupList();

                removeCheckedItems(expandableListTitle, expandableListDetail);

                hashMap.clear();
                SharedPreferences settings = getSharedPreferences("MULTIPLEATTRIBUTE", Context.MODE_PRIVATE);
                settings.edit().clear().commit();

                expandableListAdapter.notifyDataSetChanged();
                SELECTED_ITEM_VALUE = null;
                SELECTED_ITEM_NAME = null;

                return true;

            case R.id.action_cancel:

                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }

    }

    private void setAdapter() {
        if (expandableListTitle.size() > 0) {

            Collections.sort(expandableListTitle, new Comparator<location_attribute_group>() {
                @Override
                public int compare(location_attribute_group lhs, location_attribute_group rhs) {

                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
            });



            expandableListAdapter = new CustomExpandableListAdapter(context,
                    expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setVisibility(View.VISIBLE);
            empty_tv.setVisibility(View.GONE);


        } else {
            empty_tv.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);



        }
    }

    private void setCheckedList() {
        outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("AttributeHashMap", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        expandableListTitle = new ArrayList<>();

        for (String key : groupTitle) {
            location_attribute_group grp = new location_attribute_group();
            grp.setTitle(key);
            grp.setSelected(false);

            ArrayList<location_attribute_child_row> child_list =
                    (ArrayList<location_attribute_child_row>) expandableListDetail.get(key);

            for (location_attribute_child_row row : child_list) {


                if (outputMap.isEmpty()){

                }else {
                    for(Map.Entry<String, String> entry : outputMap.entrySet()) {
                        String attrName = entry.getKey();
                        String attrValue = entry.getValue();

                        StringTokenizer st = new StringTokenizer(attrName, "|");
                        String Name = st.nextToken();

                        Log.e("attrName", "setCheckedList: "+Name+" "+attrName);

                        if ((Name.equalsIgnoreCase(key)) && (row.getChild_title().equalsIgnoreCase(attrValue))) {
                            row.setSelected(true);
                           // grp.setSelected(true);
                            clearButton.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }

               /* if ((last_selected_group!=null && last_selected_group.equalsIgnoreCase(key)) &&
                        (last_selected_value != null && row.getChild_title().equalsIgnoreCase(last_selected_value))) {
                    row.setSelected(true);
                    grp.setSelected(true);
                    clearButton.setVisibility(View.VISIBLE);
                    break;
                }*/
            }
            expandableListTitle.add(grp);
        }

    }

    private void removeCheckedItems(List<location_attribute_group> titles, HashMap<String, List<location_attribute_child_row>> childs) {

        for (location_attribute_group grp : titles) {
            grp.setSelected(false);

            ArrayList<location_attribute_child_row> child_list =
                    (ArrayList<location_attribute_child_row>) childs.get(grp.getTitle());

            for (location_attribute_child_row row : child_list) {

                if (row.isSelected()) {
                    row.setSelected(false);
                    //break;
                }
            }
        }

    }

    private void removeSelectedAttribute(List<location_attribute_child_row> mMultipleSelectedAttributes, String selectedItemValue, String selectedItemName) {
        for ( int i = 0; i < mMultipleSelectedAttributes.size(); i++){
            if (mMultipleSelectedAttributes.get(i).getChild_title().equals(selectedItemValue) && mMultipleSelectedAttributes.get(i).getTitle().equals(selectedItemName)){
                if (mMultipleSelectedAttributes.get(i).isSelected()){
                    mMultipleSelectedAttributes.get(i).setSelected(false);
                }
            }
        }
    }

}
