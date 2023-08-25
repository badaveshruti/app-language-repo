package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.RollAppToSite;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.ui.activity.RollAppFormActivity;

/**
 * Created by QNOPY on 12/3/2017.
 */

public class RollAppFormAdapter extends RecyclerView.Adapter<RollAppFormAdapter.ViewHolder> {

    List<SSiteMobileApp> appObjects;
    private Context context;
    RollAppFormActivity parent;
    ArrayList<Integer> checkedList ;
    ArrayList<RollAppToSite> filteredAppObjects = new ArrayList<>();
    //SiteFilter sFilter = new SiteFilter();

    public RollAppFormAdapter(List<SSiteMobileApp> items, Context context, RollAppFormActivity parent) {

        this.context = context;
        this.parent = parent;
        this.appObjects = new ArrayList<>();
        this.appObjects = items;
        checkedList = new ArrayList<>();

        if (items == null) {
            return;
        } else {
            setList(items);
        }
    }

    private void setList(List<SSiteMobileApp> items) {

        for (SSiteMobileApp item : items) {
            filteredAppObjects.add(new RollAppToSite(item.getDisplay_name_roll_into_app(),
                    item.getRoll_into_app_id(), false));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rollapp_listitem, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RollAppToSite item = filteredAppObjects.get(position);
        holder.currentItem = item;

        holder.apptitle.setText(item.getRollAppDisplayName());
        holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_default_icon));
        holder.checkBox.setChecked(item.isChecked());

        switch (item.getRollAppId()) {

            case 659:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_gwm_icon));
                break;
            case 665:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_healthsafty_icon));
                break;
            case 666:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_daily_log_icon));
                break;
            case 669:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_soil_log_icon));
                break;
            case 690:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_construction_monitoring_icon));
                break;
            case 694:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_photo_log_icon));
                break;
            case 695:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_soil_vapor_icon));
                break;
            case 711:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_phase_icon));
                break;

            default:
                holder.imgform.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_default_icon));
                break;

        }

    }

    @Override
    public int getItemCount() {
        return filteredAppObjects.size();
    }

    public ArrayList<Integer> getFormList() {
        if (checkedList == null) {
            return null;
        } else
            return checkedList;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView apptitle;
        public ImageView imgform;
        public CheckBox checkBox;
        public View mView;
        public RollAppToSite currentItem;

        public ViewHolder(View itemView) {
            super(itemView);
            apptitle = itemView.findViewById(R.id.title_tv);
            imgform = itemView.findViewById(R.id.avator_iv);
            checkBox = itemView.findViewById(R.id.checkbox);
            mView = itemView;

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                    int rollappid = currentItem.getRollAppId();

                    currentItem.setChecked(isCheck);

                    if (isCheck) {
                        if (!checkedList.contains(rollappid)) {
                            checkedList.add(rollappid);
                        }

                    } else {
                        if (checkedList.contains(rollappid)) {
//                            Object ob=rollappid;
                            checkedList.remove(Integer.valueOf(rollappid));
                        }
                    }

                    if (checkedList.size() > 0) {
                        parent.toggleSaveMenuItem(true);
                    }else {
                        parent.toggleSaveMenuItem(false);

                    }
                }
            });
        }
    }
}


