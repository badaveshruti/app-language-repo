package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;

/**
 * Created by QNOPY on 3/20/2018.
 */

public class CustomListAdapterDialog extends BaseAdapter {

    private ArrayList<SCocMaster> listData;

    private LayoutInflater layoutInflater;

    public CustomListAdapterDialog(Context context, ArrayList<SCocMaster> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listviewitem, null);
            holder = new ViewHolder();
            holder.unitView = (TextView) convertView.findViewById(R.id.txtview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.unitView.setText(listData.get(position).getCocDisplayId());
        return convertView;
    }

    static class ViewHolder {
        TextView unitView;
        TextView quantityView;

    }

}
