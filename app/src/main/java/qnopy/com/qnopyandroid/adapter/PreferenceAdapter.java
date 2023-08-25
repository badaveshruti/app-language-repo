package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.pref_model;

/**
 * Created by Yogendra on 19-Jun-17.
 */

public class PreferenceAdapter extends RecyclerView.Adapter<PreferenceAdapter.ViewHolder> {

    List<pref_model> prefObjects;

    private Context ObjContext;


    public PreferenceAdapter(List<pref_model> prefObjects, Context objContext) {
        this.prefObjects = prefObjects;
        this.ObjContext = objContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pref_item_row, parent, false);
        return new PreferenceAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        pref_model item = prefObjects.get(position);
        holder.Titletxt.setText(item.getFeatureName());

    }

    @Override
    public int getItemCount() {
        return prefObjects.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Titletxt;


        public ViewHolder(View itemView) {
            super(itemView);

            Titletxt = (TextView) itemView.findViewById(R.id.titletxt);

        }

    }
}
