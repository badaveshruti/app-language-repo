package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.responsemodel.newFormLabelResponse;

/**
 * Created by shantanu on 7/28/16.
 */

public class SpinnerCustomAdapter extends ArrayAdapter<newFormLabelResponse> {

    private Context context;
    // Your custom values for the spinner (User)
    private newFormLabelResponse[] values;
    ArrayList<newFormLabelResponse> dataList;

    public SpinnerCustomAdapter(Context context, int resource, ArrayList<newFormLabelResponse> dataList) {
        super(context, resource);
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public newFormLabelResponse getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


/*
   @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MetaDataForForm currentItem=new MetaDataForForm();
        currentItem=this.getItem(position);
        TextView label = new TextView(context);
        label.setText(currentItem.getMetaParamLabel());
        return label;
    }*/

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        newFormLabelResponse currentItem = new newFormLabelResponse();
        currentItem = this.getItem(position);
        TextView label = new TextView(context);
        String labelList = currentItem.getFieldParameterLabelAlias();
        label.setTextSize(20);
        label.setMinHeight(40);
        label.setTextColor(0xB2000000);
        label.setText(labelList);
        return label;
    }
}
