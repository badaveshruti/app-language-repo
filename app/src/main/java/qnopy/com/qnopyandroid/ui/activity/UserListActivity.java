package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.responsemodel.newSiteUserResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 5/6/2018.
 */

public class UserListActivity extends ProgressDialogActivity {
    private static final String TAG = "UserListActivity";
    List<SUser> values;
    UserListAdapter adapter;
    Toolbar toolbar;
    ActionBar actionBar;
    ListView listView;
    TextView emptylist_view;
    Context context;
    public static Activity UserListActivity;
    ProgressDialog progressDialog;
    List<SSiteUserRole> newSiteUserList;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    String guid = null, username = null, password = null;
    Bundle extras;
    ArrayList<SUser> sUserArrayList = new ArrayList<>();
    int siteid = 0;
    boolean isassigned = false;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_layout);

        toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Assign Project");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.userListView);
        //   searchTV = (SearchView) findViewById(R.id.search_txt);
        emptylist_view = findViewById(R.id.empty_users);
        // searchView = (MaterialSearchView) findViewById(R.id.search_view);

        context = this;
        UserListActivity = this;
        GlobalStrings.currentContext = this;

        extras = getIntent().getExtras();
        if (extras != null) {
            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("BUNDLE");
            sUserArrayList = (ArrayList<SUser>) args.getSerializable("ArralistofUser");
            siteid = extras.getInt("SITE_ID");
        }
        //   sUserArrayList= (ArrayList<SUser>) this.getIntent().getSerializableExtra("ArralistofUser");

        String login_userid = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        UserDataSource userDataSource = new UserDataSource(context);

        if (sUserArrayList != null && sUserArrayList.size() > 0) {
            for (int i = 0; i < sUserArrayList.size(); i++) {
                SiteUserRoleDataSource siteUserRoleDataSource = new SiteUserRoleDataSource(context);

              //  sUserArrayList.get(i);
                Log.i("user list", "" + sUserArrayList.get(i));
                int uid = sUserArrayList.get(i).getUserId();

                String name = sUserArrayList.get(i).getUserName();

                UserDataSource dataSource = new UserDataSource(context);

//                Iterator myVeryOwnIterator = sUserArrayList.get(0).getUserId();
//                while(myVeryOwnIterator.hasNext()) {
//                    String key=(String)myVeryOwnIterator.next();
//                    String value=(String)meMap.get(key);
//                    Toast.makeText(ctx, "Key: "+key+" Value: "+value, Toast.LENGTH_LONG).show();
//                }

                int id = sUserArrayList.get(i).getUserId();
                isassigned = siteUserRoleDataSource.isRoleassigned(siteid, uid);
                if (!isassigned) {
                    //  SUser sUser = sUserArrayList.get(i);

                    long ret = siteUserRoleDataSource.insertUserData(sUserArrayList.get(i).getUserId(), Integer.parseInt(login_userid), siteid);
                    Log.i(TAG, "insertUserData res= " + ret);
                }
            }
        }

        if (sUserArrayList != null && sUserArrayList.size() > 0) {
            adapter = new UserListAdapter(context, R.layout.list_item, sUserArrayList,siteid);
            listView.setAdapter(adapter);
            emptylist_view.setVisibility(View.GONE);
        } else {
            emptylist_view.setVisibility(View.GONE);
        }

//        values = userDataSource.getuserListtoassignproject(userid);
//        if (values != null && values.size() > 0) {
//            adapter = new UserListAdapter(context, R.layout.list_item, values);
//            listView.setAdapter(adapter);
//            emptylonist_view.setVisibility(View.GONE);
//        } else {
//            emptylist_view.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_save:
                if (CheckNetwork.isInternetAvailable(context)) {
                    new PostAddUserTask().execute();
                } else {
                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();

                }

                return true;
            case android.R.id.home:
                final SiteUserRoleDataSource siteUserRoleDataSource = new SiteUserRoleDataSource(context);
                boolean unsyncdata = siteUserRoleDataSource.isdataforsiteuserunsynced();
                if (unsyncdata) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Do you want to assign project to users ?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Assign",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (CheckNetwork.isInternetAvailable(context)) {
                                        new PostAddUserTask().execute();
                                    } else {
                                        Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    siteUserRoleDataSource.deleteunsyncdata();
                                    Intent i = new Intent(context, SiteActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(i, 1);

                                }
                            });
                    alertDialog.show();


                } else {
                    finish();
                }

               //    finish();
                return true;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final SiteUserRoleDataSource siteUserRoleDataSource = new SiteUserRoleDataSource(context);
        boolean unsyncdata = siteUserRoleDataSource.isdataforsiteuserunsynced();
        if (unsyncdata) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Do you want to assign project to users ?");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Assign",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (CheckNetwork.isInternetAvailable(context)) {
                                new PostAddUserTask().execute();
                            } else {
                                Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            siteUserRoleDataSource.deleteunsyncdata();
                            Intent i = new Intent(context, SiteActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(i, 1);

                        }
                    });
            alertDialog.show();


        } else {
            finish();
        }
       //  finish();
    }

    private class PostAddUserTask extends AsyncTask<MediaType, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected Object doInBackground(MediaType... mediaTypes) {
            newSiteUserResponseModel respModel = null;

            try {

                SiteUserRoleDataSource siteUserRoleDataSource = new SiteUserRoleDataSource(context);
                newSiteUserList = siteUserRoleDataSource.getAllSiteUserData();

                username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);

                guid = Util.getSharedPreferencesProperty(context, username);
                if (newSiteUserList != null && newSiteUserList.size() > 0) {

                    respModel = mAquaBlueService.v1_setAssignuserData(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_assignuser), newSiteUserList, guid);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error=" + e.getLocalizedMessage());
            }
            return respModel;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            Log.d(TAG, " onPostExecute: Result = " + result);

            progressDialog.dismiss();
            if (result != null) {
                newSiteUserResponseModel respModel = (newSiteUserResponseModel) result;
                //  newLocationResponseModel respModel = (newLocationResponseModel) result;

                if (respModel.isSuccess()) {
                    SiteUserRoleDataSource siteUserRoleDataSource = new SiteUserRoleDataSource(context);

                    // for(int i=0;i<respModel.getData().size();i++) {

                    int res = siteUserRoleDataSource.updatesiteuseritem();//Server Responce of Location
                    Log.i(TAG, "NewLov/s added :" + res);
                    //  }
//                    if(Util.getSharedPreferencesProperty(mContext,GlobalStrings.COMPANYID).equalsIgnoreCase("99999"))
//                    {
//                        DefaultValueDataSource dv=new DefaultValueDataSource(mContext);
//                      dv.insertDefaultValueList(respModel.getData());
//                    }
//
//                    if(Util.getSharedPreferencesProperty(mContext,GlobalStrings.USERTTYPE).equalsIgnoreCase("latest"))
//                    {
//                        DefaultValueDataSource dv=new DefaultValueDataSource(mContext);
//                        dv.insertDefaultValueList(respModel.getData());
//                    }

                    Toast.makeText(context, "Assigned Project to user successfully!", Toast.LENGTH_LONG).show();
                   // finish();
                    Intent i = new Intent(context, SiteActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(i, 1);

                } else {
                    GlobalStrings.responseMessage = respModel.getMessage();
                    HttpStatus respCode = respModel.getResponseCode();
                    if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                        Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
                        // finish();
                    } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                        Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if (result.equals(HttpStatus.FAILED_DEPENDENCY.toString())) {
                        Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if ((respCode == HttpStatus.EXPECTATION_FAILED) ||
                            (respCode == HttpStatus.UNAUTHORIZED) ||
                            (respCode == HttpStatus.CONFLICT)
                            ) {
                        Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
                    }
                }


            } else {
                Toast.makeText(context, "Sorry,try Again!", Toast.LENGTH_LONG).show();

            }

        }// end of onPostExecu
    }
}
