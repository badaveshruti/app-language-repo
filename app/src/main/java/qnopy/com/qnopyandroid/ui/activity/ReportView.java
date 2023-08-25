package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import java.util.ArrayList;
import java.util.HashMap;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.FieldParamInfo;
import qnopy.com.qnopyandroid.uicontrols.ReportMainLayout;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class ReportView extends ProgressDialogActivity {

    private static final String TAG = "ReportView";
    protected String siteName;
    protected String username;
    protected int siteID, parent_appID, eventID, current_appID;
    ActionBar actionBar;
    Context mContext;
    private String locId;
    private ArrayList<FieldParamInfo> paramLabelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras;

        mContext = this;
        extras = getIntent().getExtras();

//        TextView btn = (TextView) findViewById(R.id.SiteName);
        siteName = extras.getString("SITE_NAME");
        eventID = extras.getInt("EVENT_ID");
        siteID = extras.getInt("SITE_ID");
        parent_appID = extras.getInt("PARENT_APP_ID");
        current_appID = extras.getInt("CURRENT_APP_ID");
        locId = extras.getString(GlobalStrings.KEY_LOCATION_ID);

        username = extras.getString("USER_NAME");
        paramLabelList = (ArrayList<FieldParamInfo>) extras.getSerializable(GlobalStrings.FP_IDS_LIST);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Report");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // actionBar.hide();
        setContentView(new ReportMainLayout(this, this));
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUserName() {
        return username;
    }

    public int getSiteID() {
        return siteID;
    }

    public int getParentAppID() {
        return parent_appID;
    }

    public int getCurrentAppID() {
        return current_appID;
    }

    public int getEventID() {
        return eventID;
    }

    public String getLocId() {
        return locId;
    }

    public HashMap<String, FieldParamInfo> getParamLabelList() {
        HashMap<String, FieldParamInfo> labelList = new HashMap<>();
        for (FieldParamInfo fieldParamInfo : paramLabelList) {
            labelList.put(fieldParamInfo.getFieldParameterId(), fieldParamInfo);
        }
        return labelList;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }
}
