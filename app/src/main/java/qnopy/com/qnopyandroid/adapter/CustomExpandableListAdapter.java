package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.location_attribute_child_row;
import qnopy.com.qnopyandroid.clientmodel.location_attribute_group;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    String mListTitle;
    private Context context;
    private List<location_attribute_group> expandableListTitle;
    //    private List<location_attribute_child_row> expandableListDetail;
    private HashMap<String, List<location_attribute_child_row>> expandableListDetail;
//    private String SELECTED_ITEM=null,SELECTED_GROUP=null;

    public CustomExpandableListAdapter(Context context, List<location_attribute_group> expandableListTitle,
                                       HashMap<String, List<location_attribute_child_row>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
//        this.SELECTED_ITEM=selected_item;
//        this.SELECTED_ITEM=selected_item;
    }

    @Override
    public location_attribute_child_row getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).getTitle())
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    public ArrayList<location_attribute_group> getGroupList() {
        return (ArrayList<location_attribute_group>) expandableListTitle;
    }

    public HashMap<String, List<location_attribute_child_row>> getchildList() {
        return expandableListDetail;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final location_attribute_child_row c_item = (location_attribute_child_row) getChild(listPosition, expandedListPosition);
        final String expandedListText = (String) c_item.getChild_title();
        final boolean isSelected = c_item.isSelected();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.attribute_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);

        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        expandedListTextView.setTypeface(type);

        ImageView selcted_child = (ImageView) convertView
                .findViewById(R.id.selected_item_iv);

        if (isSelected) {
            selcted_child.setVisibility(View.VISIBLE);
        } else {
            selcted_child.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).getTitle())
                .size();
    }

    @Override
    public location_attribute_group getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        location_attribute_group g_item = getGroup(listPosition);
        String listTitle = g_item.getTitle();
        mListTitle = g_item.getTitle();
        final boolean isSelected = g_item.isSelected();


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.attribute_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        listTitleTextView.setTypeface(type);

//        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        TextView textViewNoAttribiteFound = convertView.findViewById(R.id.textViewNoAttributes);
        ImageView selcted_group = (ImageView) convertView
                .findViewById(R.id.selected_group_iv);


        /*if(getChildrenCount(listPosition)==0){

            expandableListTitle.remove(listPosition);
            //expandableListDetail.remove(mListTitle);
            notifyDataSetChanged();
            //textViewNoAttribiteFound.setVisibility(View.VISIBLE);
            //textViewNoAttribiteFound.setText("No Attributes for- "+mListTitle);
            //listTitleTextView.setVisibility(View.GONE);

        }else{
            //textViewNoAttribiteFound.setVisibility(View.GONE);

        }*/
        listTitleTextView.setVisibility(View.VISIBLE);

        if (isSelected) {
            selcted_group.setVisibility(View.VISIBLE);
        } else {
            selcted_group.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}