/*
package qnopy.com.qnopyandroid.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource;
import qnopy.com.qnopyandroid.gps.GPSTracker;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.AddSite;
import qnopy.com.qnopyandroid.requestmodel.SLocation;
import qnopy.com.qnopyandroid.requestmodel.SiteModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.util.Util;


public class AddSiteActivity extends AppCompatActivity {


    EditText edtsitename, edtsiteno, edtaddr1, edtaddr2, edtcity, edtzip;
    Spinner spnstate;
    Button btnsubmit, btncancel;
    Context context;
    String state;
    String TAG = "Add Sites :";
    ProgressDialog progressDialog;
    AddsiteResponseModel mRetAddSiteResponse = null;
    AddSiteDataModel mRetAddSiteDataModel = null;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    String userGuid = null,userID;
    SiteModel sm = new SiteModel();
    List<SLocation> mRetLocationList = null;
    List<AddSite> mRetAddSiteList = null;
    //  SLocation loc=new SLocation();
    //AddSite asite=new AddSite();
    int siteid;
    ActionBar actionBar;
    String sitename, siteno;
    Bundle extras;
    Boolean download_forms,goto_site_screen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_site);

        context = this;
        declareID();

        userID=Util.getSharedPreferencesProperty(context,GlobalStrings.USERID);
        extras = getIntent().getExtras();
        if (extras!=null){
            if (extras.containsKey("GOTO_SITE_ACTIVITY")) {
                goto_site_screen = extras.getBoolean("GOTO_SITE_ACTIVITY");
//            goto_site_screen = true;
            }

            download_forms=extras.getBoolean("DOWNLOAD_FORMS");

        }


        List<String> list = new ArrayList<String>();
        list.add("Select");
        list.add("Alabama");
        list.add("Alaska");
        list.add("Arizona");
        list.add("Arkansas");
        list.add("California");
        list.add("Colorado");
        list.add("Connecticut");
        list.add("Delaware");
        list.add("District of Columbia");
        list.add("Florida");
        list.add("Georgia");
        list.add("Hawaii");
        list.add("Idaho");
        list.add("Illinois");
        list.add("Indiana");
        list.add("Iowa");
        list.add("Kansas");
        list.add("Kentucky");
        list.add("Louisiana");
        list.add("Maine");
        list.add("Maryland");
        list.add("Massachusetts");
        list.add("Michigan");
        list.add("Minnesota");
        list.add("Mississipi");
        list.add("Missouri");
        list.add("Montana");
        list.add("Nevada");
        list.add("New Hampshire");
        list.add("New Jersey");
        list.add("New Mexico");
        list.add("New York");
        list.add("North Carolina");
        list.add("North Dakota");
        list.add("Ohio");
        list.add("Oklahoma");
        list.add("Oregon");
        list.add("Pennsylvania");
        list.add("Rhode Island");
        list.add("South Carolina");
        list.add("South Dakota");
        list.add("Tennessee");
        list.add("Texas");
        list.add("Utah");
        list.add("Vermont");
        list.add("Virginia");
        list.add("Washington");
        list.add("West Virginia");
        list.add("Wisconsin");
        list.add("Wyoming");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnstate.setAdapter(dataAdapter);
        String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);

        userGuid = Util.getSharedPreferencesProperty(context, username);

        int compid = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));
        View cView = getLayoutInflater().inflate(R.layout.notedialog_actionbar, null);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setCustomView(cView);
        }


        spnstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state = String.valueOf(adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GPSTracker tracker = new GPSTracker(context);
                Double lat = tracker.getLatitude();
                Double longi = tracker.getLongitude();

                sitename = edtsitename.getText().toString();

 if (sitename.length() == 0) {
                    edtsitename.setError("This Field cannot be Blank");
                }

                String siteno = edtsiteno.getText().toString();
  if (siteno.length() == 0) {
                    edtsiteno.setError("This Field cannot be Blank");
                }

                String addr1 = edtaddr1.getText().toString();
                String addr2 = edtaddr2.getText().toString();
                String city = edtcity.getText().toString();
                String zip = edtzip.getText().toString();

                //  if(isValidated())


                sm.setSiteName(sitename);
                sm.setSiteNumber(siteno);
                sm.setAddress1(addr1);
                sm.setAddress2(addr2);
                sm.setCity(city);
                sm.setZipCode(zip);
                sm.setLatitude(String.valueOf(lat));
                sm.setLongitude(String.valueOf(longi));

                if (state == "Select") {
                    state = "";
                }
                sm.setState(state);

                if (CheckNetwork.isInternetAvailable(context)) {
                    if (sitename.length() != 0) {
                        new PostAddSiteTask().execute();
                    } else {
                        edtsitename.setError("Enter sitename.");
                        edtsitename.requestFocus();
                    }
                } else {
                    Toast.makeText(context, GlobalStrings.network_alert, Toast.LENGTH_LONG).show();

                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public boolean isValidated() {

        if (sitename.length() == 0) {
            edtsitename.setError("This Field cannot be blank");
        }
        if (siteno.length() == 0) {
            edtsiteno.setError("This Field cannot be blank");
        }
        return true;
    }

    private void declareID() {

        edtsitename = (EditText) findViewById(R.id.edtsitename);
        edtsiteno = (EditText) findViewById(R.id.edttxtsiteNumber);
        edtaddr1 = (EditText) findViewById(R.id.edttxtaddr1);
        edtaddr2 = (EditText) findViewById(R.id.edttxtaddr2);
        edtcity = (EditText) findViewById(R.id.edttxtcity);
        edtzip = (EditText) findViewById(R.id.edttxtzip);
        spnstate = (Spinner) findViewById(R.id.statespinner);
        btnsubmit = (Button) findViewById(R.id.btnsub);
        btncancel = (Button) findViewById(R.id.btncancel);

    }

    private class PostAddSiteTask extends AsyncTask<MediaType, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, " onPreExecute: Populating the request objects");
            // Init the progress dialog
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(MediaType... mediaTypes) {
            String response = null;

respModel = mAquaBlueService.v1_setAddLabelData(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.prod_add_form_label), metaformList, guid, password);


            try {

                if (null != mAquaBlueService) {

//                    mRetAddSiteResponse = mAquaBlueService.addSiteService(GlobalStrings.Local_Base_URL,
//                            getResources().getString(R.string.prod_addSite), sm,
//                            userGuid);
//
                    mRetAddSiteResponse = mAquaBlueService.addSiteService(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_addSite), sm,
                            userGuid);

                    if (null != mRetAddSiteResponse) {
                        if (mRetAddSiteResponse.isSuccess()) {
                            mRetAddSiteDataModel = mRetAddSiteResponse.getData();
                            response = "SUCCESS";

                        } else {
                            GlobalStrings.responseMessage = mRetAddSiteResponse.getMessage();
                            response = mRetAddSiteResponse.getResponseCode().toString();
                        }
                    } else {
                        response = "RETRY";
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;

        }

        @Override
        protected void onPostExecute(Object res) {
            super.onPostExecute(res);
            progressDialog.dismiss();

            if (res != null) {
                if (res == "SUCCESS") {
                    saveSiteData();
                } else if (res == "RETRY") {
                    Toast.makeText(context, "Failed to add Site,Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Failed to add Site,Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveSiteData() {

        if (mRetAddSiteDataModel != null) {

            mRetLocationList = mRetAddSiteDataModel.getLocation();
            mRetAddSiteList = mRetAddSiteDataModel.getSite();
            LocationDataSource locdatasrc = new LocationDataSource(context);


            for (int i = 0; i < mRetLocationList.size(); i++) {
                SLocation loc = new SLocation();
                loc = mRetLocationList.get(i);
                // SLocation loc = mRetLocationList.get(i);
                locdatasrc.storeLocation(loc,userID);

                SiteDataSource sitesrc = new SiteDataSource(context);

                AddSite asite = new AddSite();
                asite = mRetAddSiteList.get(i);
                sitesrc.storeForAddSite(asite);
                siteid = loc.getSiteId();




 SiteDataSource sitesrc=new SiteDataSource(context);

            for(int i=0;i<mRetAddSiteList.size();i++) {

                AddSite asite=new AddSite();
                 asite=mRetAddSiteList.get(i);
                sitesrc.storeForAddSite(asite);
            }


                int userid = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

                SiteUserRoleDataSource dataSource = new SiteUserRoleDataSource(context);
                dataSource.insertsiteforuser(userid, siteid);

                // int compid = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));

                // SiteMobileAppDataSource sm=new SiteMobileAppDataSource(context);
                // sm.insertsiteforcompany(siteid,compid);

            }
            Toast.makeText(context, "Site Added Successfully!", Toast.LENGTH_LONG).show();
            if (goto_site_screen) {
                ApplicationActivity.appActivity.finish();
                startActivity(new Intent(context, SiteActivity.class));
            }
            if (download_forms){
                Intent intent = new Intent(new Intent(context, MetaSyncActivity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            finish();


        } else {
            Toast.makeText(context, "Site Failed to Add!", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "HomeUp Pressed");
                finish();

        }

        return true;
    }
}
*/
