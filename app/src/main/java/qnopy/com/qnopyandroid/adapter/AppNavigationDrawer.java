package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;

public class AppNavigationDrawer extends ArrayAdapter<MobileApp> {
    private static final String TAG = "AppNavigationDrawer";
    private LocationDetailActivity context;
    private LayoutInflater Layf;
    private List<MobileApp> mApp;
    int mCurrentAppID;
//    private float density;

    public AppNavigationDrawer(Context context, int appDrawerAdapter,
                               int appLabel, List<MobileApp> mApp, int currentAppID) {
        super(context, appDrawerAdapter, appLabel, mApp);
        this.context = (LocationDetailActivity) context;
        this.Layf = LayoutInflater.from(context);
        this.mApp = mApp;
        mCurrentAppID = currentAppID;
//        ScreenReso application = ((ScreenReso) this.context.getApplication());
//        application.getScreenReso(context);
//        density = application.getDensity();

    }

    @Override
    public int getCount() {
        return mApp.size();
    }

    @Override
    public MobileApp getItem(int position) {
        return mApp.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            Log.i(TAG, "getView() IN time:" + System.currentTimeMillis());

            ViewHolder holder = null;
            final int appNo = position;
            MobileApp info = mApp.get(position);
            int appId = info.getAppID();
            FieldDataSource fieldSource = new FieldDataSource(context);
            //05-Jan-16 Commented
//			boolean isSubmittedForm = fieldSource.isSubmittedForm(appId,
//					context.getEventID(), context.getLocID(),
//					context.getSiteId());

            //05-Jan-16

            int status = fieldSource.getChildAppStatus(appId, context.getLocID());
//            Log.i("FormStatusPopup", "AppID:" + appId + " And LocationID:" + context.getLocID() + " Status:" + status);
            Log.i(TAG, "getView() MobileApp=" + info.getAppName() + " ,position=" + position);
            if (convertView == null) {
                convertView = Layf.inflate(R.layout.app_drawer_adapter, null);

                holder = new ViewHolder();

                holder.childAppName = (TextView) convertView
                        .findViewById(R.id.app_label);

                holder.rowConatiner = (RelativeLayout) convertView
                        .findViewById(R.id.row_holder);

                holder.isDataSubmitted = (ImageView) convertView
                        .findViewById(R.id.is_data_submitted);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (appId == this.mCurrentAppID) {
                holder.rowConatiner.setBackgroundColor(context.getResources().getColor(R.color.qnopy_teal));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.rowConatiner.setBackground(null);
                }
            }

            holder.childAppName.setText(info.getAppName());
            Log.i(TAG, "getView() Setting App Name:" + info.getAppName());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "getView() Selectd Form No.:" + appNo);
                    context.dismissPopup(appNo);
                }
            });

            holder.isDataSubmitted.setVisibility(View.GONE);//hiding as it doesn't work well it
            // shows wrong status even if form filled totally if need to show it then change the
            // status query accordingly

            if (status == 2) {
                holder.isDataSubmitted
                        .setBackgroundResource(R.mipmap.green_square);
            } else if (status == 0) {
                holder.isDataSubmitted
                        .setBackgroundResource(R.mipmap.red_square);
            } else {
                holder.isDataSubmitted
                        .setBackgroundResource(R.mipmap.grey_square);
            }

//            if (holder.isDataSubmitted != null) {
//                if (status == 2) {
//                    holder.isDataSubmitted
//                            .setBackgroundResource(R.mipmap.green_square);
//                } else if (status == 1) {
//                    holder.isDataSubmitted
//                            .setBackgroundResource(R.mipmap.red_square);
//                } else {
//                    holder.isDataSubmitted
//                            .setBackgroundResource(R.mipmap.grey_square);
//                }
//
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getView() Error :" + e.getMessage());

        }
        Log.i(TAG, "getView() OUT time:" + System.currentTimeMillis());

        return convertView;

    }

    public static class ViewHolder {
        TextView childAppName;
        ImageView isDataSubmitted;
        RelativeLayout rowConatiner;
    }
}
