package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.Site;

/**
 * Created by Yogendra on 29-Aug-16.
 */
public class ParentSiteAdapter extends ArrayAdapter<Site> {
    Context mContext;
    ArrayList<Site> mSiteList;

    public ParentSiteAdapter(Context context, int resource, List<Site> objects) {
        super(context, resource, objects);

        mSiteList = new ArrayList<Site>();
        mContext = context;
        mSiteList.addAll(objects);


    }

    @Override
    public int getCount() {
        return mSiteList.size();
    }

    @Override
    public Site getItem(int position) {
        return mSiteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        Button goBtn;
        ImageButton nextsitebtn;

    }

}
