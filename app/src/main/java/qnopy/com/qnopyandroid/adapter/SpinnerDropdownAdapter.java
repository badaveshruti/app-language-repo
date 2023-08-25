package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by QNOPY on 8/24/2017.
 */

public class SpinnerDropdownAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private List<String> itemList;

    public SpinnerDropdownAdapter(Context context, List<String> itemList) {

        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(context);
        }
        TextView txt = (TextView) convertView;
        //TextView txt = new TextView(context);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setText(getItem(position));
        txt.setTextColor(Color.parseColor("#000000"));

        final TextView finalItem = txt;
        txt.post(new Runnable() {
            @Override
            public void run() {
                finalItem.setSingleLine(false);
            }
        });
        return txt;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TextView txt = new TextView(context);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setGravity(Gravity.CENTER);
        txt.setText(itemList.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }
}
