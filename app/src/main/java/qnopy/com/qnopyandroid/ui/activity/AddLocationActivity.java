package qnopy.com.qnopyandroid.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.location_item;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class AddLocationActivity extends ProgressDialogActivity {

    private static final String TAG = "AddLocation";
    Context mContext;
    ActionBar actionBar;
    LinearLayout llContainer, default_itm;
    Button savebtn, closebtn;
    EditText loc_nameET, headerET, instructionET;
    TextView mHeader, txtinstruct;
    int loccount, mobileappid = 0;
    ArrayList<location_item> iList;
    ArrayList<NewClientLocation> newLocationList;
    String username, guid, siteID, password, userID;
    ProgressDialog progressDialog;

    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    String fieldPointHint = "Field Point";
    boolean isCloseLocationScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        mContext = this;

        fieldPointHint = getString(R.string.field_point);

        iList = new ArrayList<>();
//        Util.setOverflowButtonColor(AddLocationActivity.this, Color.BLACK);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("NO_LOCATION")) {
                isCloseLocationScreen = true;
            }
            mobileappid = extras.getInt("MOBILEAPP_ID", 0);
        }

        default_itm = findViewById(R.id.defaultItem);
        location_item ob = new location_item();
        ob.setId(R.id.defaultItem);
        ob.setLlParent(default_itm);
        iList.add(ob);

        username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(mContext, GlobalStrings.PASSWORD);
        guid = Util.getSharedPreferencesProperty(mContext, username);
        siteID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_SITEID);
        userID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);

        llContainer = findViewById(R.id.location_container);
        loc_nameET = findViewById(R.id.location_nameET);

        loc_nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LocationDataSource loc = new LocationDataSource(mContext);
                String loc_name = s.toString();
                if (loc.islocationAlreadyExists(loc_name, Integer.parseInt(siteID))) {
                    loc_nameET.setError(getString(R.string.field_point_already_exist));
                } else {
                    loc_nameET.setError(null);
                }
            }
        });

        headerET = findViewById(R.id.headerET);
        instructionET = findViewById(R.id.instructionET);

        savebtn = findViewById(R.id.savebutton);

        closebtn = findViewById(R.id.closebutton);
        txtinstruct = findViewById(R.id.txtinstruct);

        if (mobileappid == 659) {
            txtinstruct.setText(R.string.gwm_no_fieldpoints_alert);
            fieldPointHint = getString(R.string.monitoring_well);

        } else if (mobileappid == 669) {
            txtinstruct.setText(R.string.soil_boring_no_fieldpoints_alert);
            fieldPointHint = getString(R.string.soil_boring);

        } else if (mobileappid == 695) {
            txtinstruct.setText(R.string.vapor_sampling_no_fieldpoints_alert);
            fieldPointHint = getString(R.string.vapor_sampling);

        } else {
            txtinstruct.setText(R.string.default_fieldpoints_alert);
            fieldPointHint = getString(R.string.field_point);
        }

        loc_nameET.setHint(fieldPointHint);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated(iList)) {
                    if (CheckNetwork.isInternetAvailable(mContext)) {
                        new PostAddLocationTask().execute();
                    } else {
                        LocationDataSource loc = new LocationDataSource(mContext);
                        List<NewClientLocation> locList = loc.storeLocations(newLocationList, true);//Offline creation Location
                        Log.i(TAG, "No.of NewLocation/s added :" + locList.size());

                        NewLocationResponseModel respModel = new NewLocationResponseModel();
                        respModel.setData(locList);

                        Intent intent = new Intent();
                        intent.putExtra(GlobalStrings.ADDED_LOCATIONS, respModel);
                        setResult(RESULT_OK, intent);

                        Toast.makeText(mContext, getString(R.string.new_field_point_added_successfully), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.are_you_sure_to_cancel))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isCloseLocationScreen) {
                                    LocationActivity.LocActivity.finish();
                                }
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mHeader = findViewById(R.id.sitenameTV);
        String site = Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_SITENAME);
        mHeader.setText(site);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            // actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (Locale.getDefault().getLanguage().contains("en"))
                actionBar.setTitle(getString(R.string.add) + " " + fieldPointHint + "s");
            else
                actionBar.setTitle(getString(R.string.add) + " " + fieldPointHint);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_add_Location:
                loccount++;
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout llview = new LinearLayout(mContext);
                llview.setId(loccount);
                llview.setLayoutParams(params);
                llview.setGravity(Gravity.CENTER);
                llview.setWeightSum(1);
                llview.setOrientation(LinearLayout.HORIZONTAL);
                llview.addView(getEditText(mContext, R.id.location_nameET + loccount, fieldPointHint));
//                llview.addView(getEditText(mContext, R.id.headerET + loccount, "Header"));
//                llview.addView(getEditText(mContext, R.id.instructionET + loccount, "Instruction"));
                llview.addView(getDeleteButton(mContext, loccount));

                llContainer.addView(llview);

                location_item ob = new location_item();
                ob.setId(loccount);
                ob.setLlParent(llview);
                iList.add(ob);
                break;
            case android.R.id.home:
                if (isCloseLocationScreen) {
                    LocationActivity.LocActivity.finish();
                }
                finish();
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (isCloseLocationScreen) {
                    LocationActivity.LocActivity.finish();
                }
                finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private EditText getEditText(Context context, int id, String hint) {
        final EditText eBox = new EditText(context);
        eBox.setId(id);
        eBox.setHint(hint);
        eBox.setHintTextColor(context.getResources().getColor(R.color.dialog_edittext_color_hint));
//        eBox.setGravity(Gravity.CENTER);

        final LinearLayout.LayoutParams lparams;
        if (hint.equals(fieldPointHint)) {
            lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.weight = 1F;
        } else {
            lparams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.35F);
            lparams.setMargins(4, 0, 0, 0);
        }

        eBox.setLayoutParams(lparams);

        eBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LocationDataSource loc = new LocationDataSource(mContext);
                String loc_name = s.toString();
                if (loc.islocationAlreadyExists(loc_name, Integer.parseInt(siteID))) {
                    eBox.setError(getString(R.string.field_point_already_exist));
                }
            }
        });

        return eBox;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ImageButton getDeleteButton(Context context, final int id) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(8, 0, 0, 0);

        ImageButton dbutton = new ImageButton(context);
        dbutton.setId(id);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            dbutton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_menu_delete));
        } else {
            dbutton.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_delete));
        }

        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Location Child Count:" + iList.size());
                for (location_item item : iList) {
                    if (item.getId() == id) {
                        llContainer.removeView(item.getLlParent());
                        iList.remove(item);
                        Log.i(TAG, "Location Child Count after Remove:" + iList.size());
                        break;
                    }
                }
            }
        });
        return dbutton;
    }

    private boolean isValidated(ArrayList<location_item> data) {
        newLocationList = new ArrayList<NewClientLocation>();

        for (location_item item : data) {
            LinearLayout parent = item.getLlParent();
            int count = parent.getChildCount();
            NewClientLocation obj = new NewClientLocation();
            obj.setSiteId(siteID);
            obj.setLocationId(0 + "");
            obj.setCreationDate(System.currentTimeMillis() + "");
            obj.setCreatedBy(userID);

            //this case may come when app id can be nothing for adding location from admin location screen
            if (mobileappid != 0)
                obj.setLocationType(mobileappid + "");

            for (int i = 0; i < count; i++) {
                View cview = parent.getChildAt(i);
                if (cview instanceof EditText) {

                    EditText child = (EditText) cview;
                    String value = child.getText().toString();

                    if (i == 0) {
                        if (value.isEmpty()) {//location
                            child.setError(getString(R.string.no_value_entered));
                            child.requestFocus();
                            return false;
                        } else if (new LocationDataSource(mContext).islocationAlreadyExists(value, Integer.parseInt(siteID))) {
                            //do Nothing
                            child.setError(getString(R.string.field_point_already_exist));
                            child.requestFocus();
                            return false;
                        } else {
                            // obj = new newClientLocation();
                            obj.setLocation(value);
                            obj.setLatitude("0");
                            obj.setLongitude("0");
                            newLocationList.add(obj);
                            break;
                        }
                    }
                }
//                    else {
//                        if (i == 1) {//Header
//                            obj.setExtField1(value);
//                        } else if (i == 2) {//instruction
//                            obj.setExtField2(value);
//                            newLocationList.add(obj);
//                            break;
//                        }
//
//                    }
            }
        }
        return true;
    }

    private class PostAddLocationTask extends AsyncTask<MediaType, Void, Object> {

        @Override
        protected void onPreExecute() {

            Log.d(TAG, " onPreExecute: Populating the request objects");
            // Init the progress dialog
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }// end of onPreExecute

        @Override
        protected Object doInBackground(MediaType... params) {

            NewLocationResponseModel respModel = null;

            try {
                // Get handle to HTTP service

                Log.d(TAG, " doInBackground  username:" + username + " ,Guid: " + guid + ",siteID: " + siteID);

//                respModel = mAquaBlueService.v1_setAddLocationData(GlobalStrings.Local_Base_URL,
//                        getResources().getString(R.string.prod_add_new_location), newLocationList, guid);

                respModel = mAquaBlueService.v1_setAddLocationData(getResources().getString(R.string.prod_base_uri),
                        getResources().getString(R.string.prod_add_new_location), newLocationList, guid);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error=" + e.getLocalizedMessage());
            }

            return respModel;
        }// end ofdoInBackground

        @Override
        protected void onPostExecute(Object result) {
            Log.d(TAG, " onPostExecute: Result = " + result);

            progressDialog.dismiss();
            if (result != null) {
                NewLocationResponseModel respModel = (NewLocationResponseModel) result;

                if (respModel.isSuccess()) {
                    LocationDataSource loc = new LocationDataSource(mContext);

                    loc.storeLocations(respModel.getData(), false);//Server Response of Location

                    Toast.makeText(mContext, getString(R.string.new_field_point_added_successfully), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    intent.putExtra(GlobalStrings.ADDED_LOCATIONS, respModel);
                    setResult(RESULT_OK, intent);

                    finish();
                } else {
                    GlobalStrings.responseMessage = respModel.getMessage();
                    HttpStatus respCode = respModel.getResponseCode();
                    if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                        Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                        Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                        // finish();
                    }
                    if (respCode.equals("401")) {

                        if (GlobalStrings.responseMessage != null && !GlobalStrings.responseMessage.isEmpty())
                            GlobalStrings.responseMessage = respModel.getMessage();
                        else
                            GlobalStrings.responseMessage = getString(R.string.device_disabled_contact_to_activate_device);

                        CustomAlert.showUnAuthAlert(AddLocationActivity.this,
                                GlobalStrings.responseMessage, getString(R.string.alert));
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
    }// end of PostMessageTask
}
