package qnopy.com.qnopyandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.gps.GPSTracker;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.AddSite;
import qnopy.com.qnopyandroid.requestmodel.SLocation;
import qnopy.com.qnopyandroid.requestmodel.SiteModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 11/9/2017.
 */

public class CreateProjectActivity extends ProgressDialogActivity {

    String TAG = "CreateProjectActivity";
    Context mcontext;
    TextView txt11, txt12, txt13, txt14, txtsitenum, txtaddr1, txtaddr2, txtzip, txtcity, txt15;
    EditText edtsitenum, edtaddr1, edtaddr2, edtzip, edtsitename, edtcity;
    Button btndetails;
    ActionBar actionBar;
    ImageView imgadd, minuschange;
    LinearLayout detailsll;
    String sitename;
    SiteModel sm = new SiteModel();
    ProgressDialog progressDialog;
    AddsiteResponseModel mRetAddSiteResponse = null;
    AddSiteDataModel mRetAddSiteDataModel = null;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    String userGuid, username = null, userID;
    List<SLocation> mRetLocationList = null;
    List<AddSite> mRetAddSiteList = null;
    int siteid = 0;
    String formIds = null;
    Bundle extras;
    ArrayList<Integer> formlist = new ArrayList<>();
    String formid, pending = null;
    ObjectMapper mapper = new ObjectMapper();
    boolean fromApplication = false;
    private BadELFGPSTracker badElf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createproject_layout);
        mcontext = this;

        initialise();

        extras = getIntent().getExtras();

        if (extras != null) {
            formlist = extras.getIntegerArrayList("ArrayList");
            Log.i("", "" + formlist);
            pending = extras.getString("Pending");
            fromApplication = extras.getBoolean("fromApplicationActivity");

        }

        if (pending != null && pending.equals("inactive")) {
            Util.setSharedPreferencesProperty(mcontext, "CreateProjectActivity", "");

        } else if (!fromApplication) {
            Util.setSharedPreferencesProperty(mcontext, "CreateProjectActivity", "active");
        }
        //View cView = getLayoutInflater().inflate(R.layout.registration_actionbar, null);

        //   boolean isFirstTime = (Util.getSharedPreferencesProperty(mcontext, GlobalStrings.IS_FIRST_TIME_LAUNCH) == null);

        String userid = Util.getSharedPreferencesProperty(mcontext, GlobalStrings.USERID);
        String isFirstTime = Util.getSharedPreferencesProperty(mcontext, userid);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Name your Project");
            actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setCustomView(cView);
        }

        imgadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgadd.setVisibility(View.GONE);
                minuschange.setVisibility(View.VISIBLE);
                detailsll.setVisibility(View.VISIBLE);

            }
        });

        minuschange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgadd.setVisibility(View.VISIBLE);
                minuschange.setVisibility(View.GONE);
                detailsll.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        badElf = new BadELFGPSTracker(CreateProjectActivity.this);
    }

    @Override
    protected void onPause() {
        badElf.disconnectTracker();
        super.onPause();
    }

    @Override
    protected void onStop() {
        badElf.disconnectTracker();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        String s = Util.getSharedPreferencesProperty(mcontext, "SiteSuccess");
        if (s != null && s.equals("Ok")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else if (fromApplication) {
            finish();
        } else {
            finish();
        }
        //}
    }

    private void initialise() {
        txt11 = findViewById(R.id.txt11);
        txt12 = findViewById(R.id.txt12);
        txt13 = findViewById(R.id.txt13);
        txt15 = findViewById(R.id.txt15);
        txtcity = findViewById(R.id.txtcity);
        //  txt14 = findViewById(R.id.txt14);
        txtsitenum = findViewById(R.id.sitenum);
        txtaddr1 = findViewById(R.id.addr1);
        txtaddr2 = findViewById(R.id.txtaddr2);
        txtzip = findViewById(R.id.zipcode);
        edtsitename = findViewById(R.id.edtsitename);
        edtsitenum = findViewById(R.id.edtsitenum);
        edtaddr1 = findViewById(R.id.edtaddr1);
        edtaddr2 = findViewById(R.id.edtaddr2);
        edtzip = findViewById(R.id.edtzip);
        edtcity = findViewById(R.id.edtcity);
        btndetails = findViewById(R.id.btndetails);
        imgadd = findViewById(R.id.imgadd);
        minuschange = findViewById(R.id.minuschange);
        detailsll = findViewById(R.id.detailsll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.self_signup, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                boolean isvalid = validateEntry();

                if (isvalid) {

                    Util.hideKeyboard(CreateProjectActivity.this);

                    Double lat = null;
                    Double longi = null;
                    if (GlobalStrings.CURRENT_GPS_LOCATION != null) {
                        lat = GlobalStrings.CURRENT_GPS_LOCATION.getLatitude();
                        longi = GlobalStrings.CURRENT_GPS_LOCATION.getLongitude();
                    }

                    sitename = edtsitename.getText().toString().trim();

               /* if (sitename.length() == 0) {
                    edtsitename.setError("This Field cannot be Blank");
                }*/
                    String siteno = edtsitenum.getText().toString();
              /*  if (siteno.length() == 0) {
                    edtsiteno.setError("This Field cannot be Blank");
                }*/
                    String addr1 = edtaddr1.getText().toString();
                    String addr2 = edtaddr2.getText().toString();
                    String zip = edtzip.getText().toString();
                    String city = edtcity.getText().toString();

                    //  if(isValidated())

                    if (sitename != null) {
                        sm.setSiteName(sitename);

                    }
                    if (siteno != null) {
                        sm.setSiteNumber(siteno);

                    }
                    if (addr1 != null) {
                        sm.setAddress1(addr1);
                    }
                    if (addr2 != null) {
                        sm.setAddress2(addr2);

                    }
                    if (zip != null) {
                        sm.setZipCode(zip);
                    }
                    if (lat != null) {
                        sm.setLatitude(String.valueOf(lat));
                    }

                    if (longi != null) {
                        sm.setLongitude(String.valueOf(longi));
                    }
                    if (city != null) {
                        sm.setCity(city);
                    }

                    if (CheckNetwork.isInternetAvailable(mcontext)) {
                        if (sitename.length() != 0) {
                            new PostAddSiteTask().execute();
                        } else {
                            edtsitename.setError("Enter Site Name");
                            edtsitename.requestFocus();
                        }
                    } else {
                        Toast.makeText(mcontext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                    }
                }
                return true;

            case android.R.id.home:
                Log.i(TAG, "HomeUp Pressed");
                //  finish();

                String s = Util.getSharedPreferencesProperty(mcontext, "SiteSuccess");
                if (s != null && s.equals("Ok")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                } else {
                    finish();
                }

            default:
                return true;
        }
    }

    private boolean validateEntry() {
        if (edtsitename.getText() == null) {
            edtsitename.requestFocus();
            edtsitename.setError("Please Enter the Project Name");
            return false;
        } else {
            return true;
        }
    }

    public class PostAddSiteTask extends AsyncTask<MediaType, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, " onPreExecute: Populating the request objects");
            // Init the progress dialog
            progressDialog = new ProgressDialog(mcontext);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(MediaType... mediaTypes) {
            String response = null;
            try {
                if (null != mAquaBlueService) {
                    username = Util.getSharedPreferencesProperty(mcontext, GlobalStrings.USERNAME);
                    userGuid = Util.getSharedPreferencesProperty(mcontext, username);

                    formIds = TextUtils.join(",", formlist).trim();
                    mRetAddSiteResponse = mAquaBlueService.addSiteService(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_addSite), sm, formIds,
                            userGuid);

                    if (null != mRetAddSiteResponse) {
                        if (mRetAddSiteResponse.isSuccess()) {
                            mRetAddSiteDataModel = mRetAddSiteResponse.getData();
                            saveSiteData();
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

                    String userid = Util.getSharedPreferencesProperty(mcontext, GlobalStrings.USERID);
                    Util.setSharedPreferencesProperty(mcontext, userid, "FIRST_TIME");

                    //saveSiteData();
                    LayoutInflater factory = LayoutInflater.from(mcontext);
                    final View DialogView = factory.inflate(R.layout.addsitelayout, null);
                    final AlertDialog Dialog = new AlertDialog.Builder(mcontext).create();
                    Dialog.setCanceledOnTouchOutside(false);
                    Dialog.setView(DialogView);
                    Button s = DialogView.findViewById(R.id.btn_yes);
                    s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);

                            //     Util.setSharedPreferencesProperty(mcontext, GlobalStrings.USERID, "FIRSTTIME");
                            Intent intent = new Intent(mcontext, MetaSyncActivity.class);
                            intent.putExtra("ArrayList", formlist);
                            intent.putExtra("fromaddsite", true);
                            intent.putExtra("fromactivation", false);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Util.setSharedPreferencesProperty(mcontext, "SiteSuccess", "Ok");
                            finish();
                        }
                    });
                    Dialog.show();


                } else if (res == "RETRY") {
                    Toast.makeText(mcontext, "Failed to add Site,Please try again.", Toast.LENGTH_LONG).show();
                } else if (res.equals(HttpStatus.UNAUTHORIZED.toString())) {
                    Toast.makeText(mcontext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    Util.setLogout(CreateProjectActivity.this);
                } else if (res.equals(HttpStatus.NOT_ACCEPTABLE.toString())) {
                    Toast.makeText(mcontext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    Util.setLogout(CreateProjectActivity.this);
                } else if (res.equals(HttpStatus.EXPECTATION_FAILED.toString())) {
                    Toast.makeText(mcontext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    Util.setLogout(CreateProjectActivity.this);
                } else {
                    Toast.makeText(mcontext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mcontext, "Failed to add Site,Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveSiteData() {

        if (mRetAddSiteDataModel != null) {

            mRetLocationList = mRetAddSiteDataModel.getLocation();
            mRetAddSiteList = mRetAddSiteDataModel.getSite();
            LocationDataSource locdatasrc = new LocationDataSource(mcontext);

            for (int i = 0; i < mRetLocationList.size(); i++) {
                SLocation loc = new SLocation();
                loc = mRetLocationList.get(i);
                // SLocation loc = mRetLocationList.get(i);
                locdatasrc.storeLocation(loc, userID);

                SiteDataSource sitesrc = new SiteDataSource(mcontext);

                AddSite asite = new AddSite();
                asite = mRetAddSiteList.get(i);
                sitesrc.storeForAddSite(asite);
                siteid = loc.getSiteId();


                int userid = Integer.parseInt(Util.getSharedPreferencesProperty(mcontext, GlobalStrings.USERID));

                SiteUserRoleDataSource dataSource = new SiteUserRoleDataSource(mcontext);
                dataSource.insertsiteforuser(userid, siteid);

            }

            Util.setSharedPreferencesProperty(mcontext, "CreateProjectActivity", null);
            Util.setSharedPreferencesProperty(mcontext, "Activation_Code", null);
            Util.setSharedPreferencesProperty(mcontext, "RollAppFormActivity", null);


        } else {
            Toast.makeText(mcontext, "Site Failed to Add!", Toast.LENGTH_LONG).show();

        }
    }
}