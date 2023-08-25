package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.ShowuserlistAdapter;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 5/9/2018.
 */

public class ShowuserlistActivity extends ProgressDialogActivity {

    Context context;
    ListView listView;
    ShowuserlistAdapter adapter;
    Toolbar toolbar;
    ActionBar actionBar;
    TextView emptylist_view;
    public static Activity ShowuserlistActivity;
    List<SUser> values;
    ArrayList<SUser> ulist = new ArrayList<>();

    Bundle extras;
    int siteid = 0, userID = 0, companyID = 0;
    String sitename = null;
    Boolean isfromassignproject = false;
    Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showuserlist_layout);
        context = this;

        toolbar = findViewById(R.id.userlist_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Assign Project");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        extras = getIntent().getExtras();
        if (extras != null) {
            sitename = extras.getString("SITE_NAME");
            siteid = extras.getInt("SITE_ID");
            isfromassignproject = extras.getBoolean("fromassignuser");

        }
        listView = findViewById(R.id.userListView);
        //   searchTV = (SearchView) findViewById(R.id.search_txt);
        emptylist_view = findViewById(R.id.empty_user);
        // searchView = (MaterialSearchView) findViewById(R.id.search_view);

        ShowuserlistActivity = this;
        GlobalStrings.currentContext = this;

        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        companyID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));

        UserDataSource userDataSource = new UserDataSource(context);
        values = userDataSource.getuserList(userID + "", companyID, siteid);

        if (values != null && values.size() > 0) {
            adapter = new ShowuserlistAdapter(context, R.layout.list_item, values);
            listView.setAdapter(adapter);
            emptylist_view.setVisibility(View.GONE);
        } else {
            android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
            alertBuilder.setMessage("There are no other users available under your organization.");
            alertBuilder.setTitle("Alert Message");
            alertBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*Intent intentBacktoSiteActivity
                            = new Intent(ShowuserlistActivity.this,
                            MainDrawerActivity.class);*/

                    Intent intentBacktoSiteActivity
                            = new Intent(ShowuserlistActivity.this,
                            HomeScreenActivity.class);
                    intentBacktoSiteActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intentBacktoSiteActivity);
                    finish();
                    SiteActivity.siteActivity.finish();
                    MainDrawerActivity.mainDrawerActivity.finish();
                }
            });
            // alertBuilder.setCancelable(false);
            Dialog alert = alertBuilder.create();
            alert.show();

            emptylist_view.setVisibility(View.VISIBLE);
        }
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_user_menu, menu);
        mMenu = menu;

        //DON'T SHOW SAVE OPTION IF LIST IS EMPTY
        if (emptylist_view.getVisibility() == View.VISIBLE) {
            mMenu.findItem(R.id.action_user_save).setVisible(false);
        } else {
            mMenu.findItem(R.id.action_user_save).setVisible(true);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_save:
                if (adapter != null) {
                    ulist = adapter.getUserCheckedList();
                }

                if (ulist != null && ulist.size() > 0) {

                    Intent intent = new Intent(this, UserListActivity.class);
                    Bundle bundle = new Bundle();
                    // Gson gson=new Gson();
                    //String myJson = gson.toJson(ulist);
                    Bundle args = new Bundle();
                    args.putSerializable("ArralistofUser", ulist);
                    intent.putExtra("BUNDLE", args);
                    //   intent.putExtra("ArralistofUser", ulist);
                    // intent.putExtra("ArralistofUser",ulist);
                    intent.putExtra("SITE_ID", siteid);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setCancelable(false);
                    dialog.setTitle("Select User");
                    dialog.setMessage("Please select at least one user.");
                    dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Delete".
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                }

                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
}

