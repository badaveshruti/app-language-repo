package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Random;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.AddLovAdapter;
import qnopy.com.qnopyandroid.clientmodel.LovItems;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.newLovData;
import qnopy.com.qnopyandroid.responsemodel.newLovResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 5/4/2018.
 */

public class AddLovActivity extends ProgressDialogActivity {
    private static final String TAG = "AddLovActivity";
    Context mContext;
    String itemname = null;
    String itemvalue = null;
    Button btnsave, btncancel;
    EditText edtname, edtvalue;
    int lovid = 0;
    int userid = 0;
    AddLovAdapter adapter;
    ListView listView;
    LinearLayout lov_list_container;
    TextView emptylistview;

    public static Activity addlovActivity;
    public List<LovItems> Lov_List;
    String companyid = null, siteID = "0";
    Toolbar toolbar;
    ActionBar actionBar;
    ProgressDialog progressDialog;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    List<newLovData> newLovList;
    String guid = null, username = null, password = null;
    String lovname = null;
    int Parentlovid = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lov);

        mContext = this;

        Bundle extras = getIntent().getExtras();
        lovid = extras.getInt("LOV_ID");
        lovname = extras.getString("LOV_NAME");
        Parentlovid = extras.getInt("PARENT_LOV_ID");
        siteID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_SITEID);

        listView = findViewById(R.id.addlovListView);
        //   searchTV = (SearchView) findViewById(R.id.search_txt);
        lov_list_container = findViewById(R.id.lovLis);
        emptylistview = findViewById(R.id.empty_add_lov);
        // searchView = (MaterialSearchView) findViewById(R.id.search_view);

        addlovActivity = this;

        toolbar = findViewById(R.id.add_lov_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0f);
        }

        companyid = Util.getSharedPreferencesProperty(mContext, GlobalStrings.COMPANYID);

        GlobalStrings.currentContext = this;
        LovDataSource dataSource = new LovDataSource(mContext);
        //  if(companyid!=null) {
        Lov_List = dataSource.getItemValues(lovid, companyid, siteID);
        //}
        if (Lov_List != null && Lov_List.size() > 0) {
            adapter = new AddLovAdapter(mContext, AddLovActivity.this, R.layout.list_item);
            listView.setAdapter(adapter);
            emptylistview.setVisibility(View.GONE);

        } else {
            emptylistview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_lov_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_Lov:

                LayoutInflater factory = LayoutInflater.from(mContext);
                final View DialogView = factory.inflate(R.layout.lovcustomdialog, null);
                final AlertDialog Dialog = new AlertDialog.Builder(mContext).create();
                Dialog.setCanceledOnTouchOutside(true);
                Dialog.setView(DialogView);
                btnsave = DialogView.findViewById(R.id.btn_add);
                btncancel = DialogView.findViewById(R.id.btn_cancel);
                edtname = DialogView.findViewById(R.id.editemname);
                edtvalue = DialogView.findViewById(R.id.edtitemval);
                Dialog.show();

                Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);
                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = edtname.getText().toString();
                        String value = edtvalue.getText().toString();

                        LovDataSource lovDataSource = new LovDataSource(mContext);

                        boolean checkname = lovDataSource.checkname(name, lovid);
                        boolean checkvalue = lovDataSource.checkvalue(value, lovid);

                        if (name.trim().equalsIgnoreCase("")) {
                            edtname.setError(getString(R.string.enter_item_name));
                        } else if (value.trim().equalsIgnoreCase("")) {
                            edtvalue.setError(getString(R.string.enter_item_value));
                        } else if (checkname) {
                            edtname.setError(getString(R.string.iyem_name_already_exist));

                        } else if (checkvalue) {
                            // edtname.requestFocus();
                            edtvalue.setError(getString(R.string.iyem_value_already_exist));
                        } else {
                            //  LovDataSource lovDataSource = new LovDataSource(mContext);
                            Random random = new Random();
                            String temp_l_item_id = "-" + String.format("%04d", random.nextInt(9999));

                            companyid = Util.getSharedPreferencesProperty(mContext, GlobalStrings.COMPANYID);

                            boolean ischecklov = lovDataSource.ischecklovavailable(lovid, name, companyid);
                            if (ischecklov) {
                                Toast.makeText(mContext, getString(R.string.lov_item_already_exist), Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                userid = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
                                int siteid = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_SITEID));

                                long ret = lovDataSource.insertlovs(name, value,
                                        lovid, userid, temp_l_item_id, companyid, siteid, Parentlovid);
                                Log.i("insertlovs", String.valueOf(ret));
                            }

                            reloadData();

                            Dialog.dismiss();
                        }
                        // adapter.notifyDataSetChanged();
                        //listView.setAdapter(adapter);
                    }
                });

                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog.dismiss();
                    }
                });

                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save:
                if (CheckNetwork.isInternetAvailable(mContext)) {
                    LovDataSource lovDataSource = new LovDataSource(mContext);
                    newLovList = lovDataSource.getAllLovItemList();
                    if (newLovList != null && newLovList.size() > 0) {
                        new PostAddLovTask().execute();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(getString(R.string.message_upper_case))
                                .setMessage((getString(R.string.no_changes_to_save)))
                                .setNegativeButton((getString(R.string.exit_upper_case)), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .setPositiveButton(getString(R.string.go_back_upper_case), null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        //Toast.makeText(mContext, "Looks like there is no new lov added.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
        }
        return true;
    }

    public void reloadData() {

        LovDataSource dataSource = new LovDataSource(mContext);
        Lov_List = dataSource.getItemValues(lovid, companyid, siteID);
        if (Lov_List.size() > 1) {
            //  adapter = new AddLovAdapter(mContext, qnopy.com.qnopyandroid.ui.AddLovActivity.this, R.layout.list_item);

            adapter.updateLovItemsList();

            adapter.notifyDataSetChanged();
        } else {
            adapter = new AddLovAdapter(mContext, AddLovActivity.this,
                    R.layout.list_item);
            listView.setAdapter(adapter);
            finish();
        }
    }

    public void reloadDatafordel() {

        LovDataSource dataSource = new LovDataSource(mContext);
        Lov_List = dataSource.getItemValues(lovid, companyid, siteID);
        //  adapter = new AddLovAdapter(mContext, qnopy.com.qnopyandroid.ui.AddLovActivity.this, R.layout.list_item);
        adapter.updateLovItemsList();
        adapter.notifyDataSetChanged();

    }

    private class PostAddLovTask extends AsyncTask<MediaType, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(MediaType... mediaTypes) {
            newLovResponseModel respModel = null;

            try {

                LovDataSource lovDataSource = new LovDataSource(mContext);
                newLovList = lovDataSource.getAllLovItemList();

                username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
                password = Util.getSharedPreferencesProperty(mContext, GlobalStrings.PASSWORD);

                guid = Util.getSharedPreferencesProperty(mContext, username);
                if (newLovList != null && newLovList.size() > 0) {
                    respModel = mAquaBlueService.v1_setAddLovData(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_add_new_lovitem), newLovList, guid);

                  /*  if (respModel.isSuccess() && respModel.getData().size() > 0) {

                        for (int i = 0; i < respModel.getData().size(); i++) {
                            if(respModel.getData().get(i).getLovItemSyncStatus()==true) {
                                int res = lovDataSource.updatelovitem(respModel.getData().get(i));//Server Responce of Location
                                Log.i(TAG, "New Lov Item added :" + res);
                            }
//                            else
//                            {
//                                Toast.makeText(mContext,"Lov Item not added.",Toast.LENGTH_SHORT).show();
//                            }
                        }

                    }*/
//                    else if (respModel.isSuccess() && respModel.getData() == null) {
//                        int res = lovDataSource.updatelovitemsyncflag();//Server Responce of Location
//                        Log.i(TAG, "update all lov(0) item syncflag :" + res);
//
//                    }
//                    else
//                    {
//                        Toast.makeText(mContext,"Lov Item not added.",Toast.LENGTH_SHORT).show();
//                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "PostAddLovTask() Error=" + e.getLocalizedMessage());
                return null;
            }
            return respModel;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            Log.d(TAG, " onPostExecute: Result = " + result);

            progressDialog.dismiss();
            LovDataSource lovDataSource = new LovDataSource(mContext);
            if (result != null) {
                newLovResponseModel respModel = (newLovResponseModel) result;
                if (respModel.isSuccess()) {

                    if (respModel.isSuccess() && respModel.getData().size() > 0) {

                        for (int i = 0; i < respModel.getData().size(); i++) {
                            if (respModel.getData().get(i).getLovItemSyncStatus() == true) {
                                int res = lovDataSource.updatelovitem(respModel.getData().get(i));//Server Responce of Location
                                Log.i(TAG, "New Lov Item added :" + res);
                                Toast.makeText(mContext, getString(R.string.all_lovs_added_to_server_success), Toast.LENGTH_SHORT).show();
                                reloadData();
                            } else {
                                Toast.makeText(mContext, getString(R.string.lov_item_failed_to_sync) + respModel.getData().get(i).getItemDisplayName(), Toast.LENGTH_SHORT).show();
                            }
//                            else
//                            {
//                                Toast.makeText(mContext,"Lov Item not added.",Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }

//                    Intent returnIntent = new Intent();
//                    //returnIntent.putExtra("result",1);
//                    setResult(RESULT_OK,returnIntent);
                } else {
                    GlobalStrings.responseMessage = respModel.getMessage();
                    HttpStatus respCode = respModel.getResponseCode();
                    if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                        Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                        // finish();
                    } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                        Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if (result.equals(HttpStatus.FAILED_DEPENDENCY.toString())) {
                        Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();

                    } else if ((respCode == HttpStatus.EXPECTATION_FAILED) ||
                            (respCode == HttpStatus.UNAUTHORIZED) ||
                            (respCode == HttpStatus.CONFLICT)
                    ) {
                        Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                    }
                }

            } else {
                Toast.makeText(mContext, getString(R.string.please_try_again), Toast.LENGTH_LONG).show();
            }

        }// end of onPostExecute
    }// end of PostMessageTa
}

