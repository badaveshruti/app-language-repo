
package qnopy.com.qnopyandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.FieldData;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.ui.activity.FormActivity;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.uiutils.FormMaster;

public class LocationdetailAdapter extends ArrayAdapter<MetaData> {

    private static final String TAG = "LocationdetailAdapter";

    static Context mContext;
    static LocationDetailActivity parentContext;

    private List<MetaData> metaObjects;

    public HashMap<String, FormMaster.DataHolder> mapObject;

    List<FieldData> previousReading1 = null, previousReading2 = null, currentReading = null;

    FieldDataSource fieldDataSource;

    public FormActivity ObjContext = null;

    public String inputType, currentFieldParamID, sampleFieldParamID;

    FormMaster formMaster;

    public LocationdetailAdapter(FormActivity formActivity, int resource,
                                 int textViewResourceId, List<MetaData> objects, Context locDetailcontext, FormMaster formMaster
    ) {
        super(formActivity, resource, textViewResourceId, objects);
        this.formMaster = formMaster;
       // formMaster.setmAdapter(this);
        this.ObjContext = formActivity;
        mContext = formActivity.appContext;
        parentContext = (LocationDetailActivity) locDetailcontext;

        this.metaObjects = formMaster.filteredMetaObjects;
        fieldDataSource = new FieldDataSource(ObjContext);
        currentReading = formActivity.getCurrentReading1List();
        previousReading1 = formActivity.getPreviousReading1List();
        previousReading2 = formActivity.getPreviousReading2List();
        this.mapObject = formActivity.mapObject;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return metaObjects.size();
    }

    @Override
    public MetaData getItem(int position) {
        return metaObjects.get(position);
    }

    @NonNull
    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FormMaster.ViewHolder viewHolder = null;
        final MetaData metaData = getItem(position);
        inputType = metaData.getMetaInputType();
        currentFieldParamID = metaData.getMetaParamID() + "";

        convertView = metaData.getForm_field_row();
        viewHolder = (FormMaster.ViewHolder) convertView.getTag();
        convertView.setFocusable(true);
        //convertView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
//        metaData.setRow_holder(viewHolder);
        //convertView.setTag(viewHolder);

        return convertView;
    }

}
