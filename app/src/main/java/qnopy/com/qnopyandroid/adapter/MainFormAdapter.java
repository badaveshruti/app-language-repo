package qnopy.com.qnopyandroid.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pchmn.materialchips.util.LetterTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.ui.activity.ApplicationActivity;

/**
 * Created by myog3 on 02-12-2017.
 */

public class MainFormAdapter extends RecyclerView.Adapter<MainFormAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "MainFormAdapter";
    List<SSiteMobileApp> allObjects;
    List<SSiteMobileApp> filteredObjects;
    private ApplicationActivity context;
    FormFilter fFilter = new FormFilter();

    public MainFormAdapter(ArrayList<SSiteMobileApp> Objects, ApplicationActivity objContext) {
        this.allObjects = Objects;
        this.filteredObjects = Objects;
        context = objContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {


        SSiteMobileApp item = filteredObjects.get(position);
        ViewHolder itemViewHolder = (ViewHolder) viewHolder;
        itemViewHolder.mItem = item;
        itemViewHolder.mTitleView.setText(item.getDisplay_name_roll_into_app());
        itemViewHolder.mAvator.setImageBitmap(new LetterTileProvider(context).getCircularLetterTile(item.getDisplay_name_roll_into_app()));

        Log.e("formIcon", "onBindViewHolder: " + item.getAppIcon());
        String formIcon = item.getAppIcon();

        if (formIcon != null) {
            if (formIcon.equals("CM")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_construction_monitoring_icon));
            } else if (formIcon.equals("DFL")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_daily_log_icon));
            } else if (formIcon.equals("GWM")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_gwm_icon));
            } else if (formIcon.equals("PIESA")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_phase_icon));
            } else if (formIcon.equals("QHS")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_healthsafty_icon));
            } else if (formIcon.equals("SV")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_soil_vapor_icon));
            } else if (formIcon.equals("SSL")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_soil_log_icon));
            } else if (formIcon.equals("PL")) {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_photo_log_icon));
            } else {
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_default_icon));
            }
        } else {
            itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_default_icon));
        }

        /*switch (item.getMobileAppId()) {

            case 659:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_gwm_icon));
                break;
            case 665:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_healthsafty_icon));
                break;
            case 666:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_daily_log_icon));
                break;
            case 669:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_soil_log_icon));
                break;
            case 690:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_construction_monitoring_icon));
                break;
            case 694:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_photo_log_icon));
                break;
            case 695:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_soil_vapor_icon));
                break;
            case 711:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_phase_icon));
                break;

            default:
                itemViewHolder.mAvator.setImageDrawable(context.getResources().getDrawable(R.mipmap.form_default_icon));
                break;

        }*/


    }

    @Override
    public int getItemCount() {
        return filteredObjects.size();
    }


    @Override
    public Filter getFilter() {
        if (fFilter == null) {
            fFilter = new FormFilter();
        }
        return fFilter;
    }

    public class FormFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            Log.i(TAG, "SearchText:" + filterString);
            if (constraint.length() == 0) {
                results.values = allObjects;
                results.count = allObjects.size();
                Log.i(TAG, "No search Text ,Return Search Count:" + results.count);

            } else {

                String filterableString;
                ArrayList<SSiteMobileApp> filtered = new ArrayList<SSiteMobileApp>();

                for (SSiteMobileApp app : allObjects) {
                    filterableString = app.getDisplay_name_roll_into_app();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(app);
                    }
                }

                results.values = filtered;
                results.count = filtered.size();

                Log.i(TAG, "Searched Text >Return Search Count:" + results.count);

            }
            //final ArrayList<String> nlist = new ArrayList<String>(count);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filteredObjects = new ArrayList<SSiteMobileApp>();
                notifyDataSetChanged();
            } else {
                filteredObjects = (ArrayList<SSiteMobileApp>) results.values;
                notifyDataSetChanged();
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvator;
        public final TextView mTitleView;
        public SSiteMobileApp mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvator = (ImageView) view.findViewById(R.id.avator_iv);
            mTitleView = (TextView) view.findViewById(R.id.title_tv);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItem != null)
                        context.onclickAppItem(mItem);
                    // Toast.makeText(v.getContext(), mTitleView.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
