package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.RequiredFieldListAdapter;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.uiutils.DividerItemDecoration;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class RequiredFieldsListActivity extends ProgressDialogActivity {
    private static final String TAG = "ReqFieldsListActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    Context context;
    //private static String EVENTID = "0";
    //private static String PARENT_APP_ID = "0";

    int EVENTID = 0;
    int PARENT_APP_ID = 0;
    int SITEID = 0;
    String SITENAME = null;

    Bundle extras;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_required_fields_list);
        context = this;
        setActionBar();
//        Util.setOverflowButtonColor(RequiredFieldsListActivity.this, Color.BLACK);

        recyclerView = findViewById(R.id.req_list_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

        extras = getIntent().getExtras();
        if (extras != null) {
            EVENTID = extras.getInt("EVENT_ID");
            PARENT_APP_ID = extras.getInt("APP_ID");
            SITEID = extras.getInt("SITE_ID");
            SITENAME = extras.getString("SITENAME");
        } else {
            Toast.makeText(context, "Sorry,try again!", Toast.LENGTH_LONG).show();
            finish();
        }

        ArrayList<RequiredFieldRowItem> list = collectData();
        showData(list);
    }

    void setActionBar() {

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Log.i(TAG, "Option Item Selected:" + item.getTitle());
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }
        return true;
    }


    public ArrayList<RequiredFieldRowItem> collectData() {
        ArrayList<RequiredFieldRowItem> list = new ArrayList<>();
        RequiredFieldRowItem rowItem = new RequiredFieldRowItem();
        ArrayList<RequiredFieldRowItem> rlist = new ArrayList<>();

        int count = 0;
        FieldDataSource data = new FieldDataSource(context);
        // list=data.getMandatoryFieldData(PARENT_APP_ID+"",EVENTID+"");

        // for(int i=0;i<list.size();i++)
        // {
        //    rowItem=new required_field_row_item();
        //    String mobid=list.get(i).getChildAppId();

        rlist = data.getMandatoryFieldList(PARENT_APP_ID + "", EVENTID + "", SITEID + "");


        for (int i = 0; i < rlist.size(); i++) {
            rowItem = new RequiredFieldRowItem();
            rowItem.setCount(rlist.get(i).getCount());
            rowItem.setLocationId(rlist.get(i).getLocationId());
            rowItem.setTitle(rlist.get(i).getTitle());
            rowItem.setChildAppId(rlist.get(i).getChildAppId());
            rowItem.setParentAppId(rlist.get(i).getParentAppId());
            rowItem.setSiteId(SITEID);
            rowItem.setSiteName(SITENAME);
            list.add(rowItem);
        }
        return list;
    }

    public void showData(ArrayList<RequiredFieldRowItem> list) {
        RequiredFieldListAdapter adapter = new RequiredFieldListAdapter(context, list);
        recyclerView.setAdapter(adapter);
    }
}
