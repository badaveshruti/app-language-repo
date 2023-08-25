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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.LocationdetailAdapter;
import qnopy.com.qnopyandroid.adapter.SpinnerCustomAdapter;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.clientmodel.MetaDataForForm;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.form_item;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.newFormLabelResponse;
import qnopy.com.qnopyandroid.responsemodel.newLabelResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by shantanu on 7/26/16.
 */
public class AddFormFieldActivity extends AppCompatActivity {

    RelativeLayout rr;
    Spinner spinnerlabel, spinnertype, spinnerfieldtype;
    Context context;
    List<MobileApp> childAppList = null;
    private int currentFormNum = 0;
    List<MetaDataForForm> listmeta = null;
    ArrayList<form_item> iList;
    String currentinputtype, username, pass;
    int mobileappid;
    MetaData data = null;
    List<String> listtype;
    Button btnadd;
    TextView txtinputtype;
    String TAG = "Labels :";
    SpinnerCustomAdapter adapter;
    ArrayAdapter<String> dataadapter;
    String item, paramlabel, fieldtype;
    int siteID, paramid;
    int roworder;
    int labelcount = 0;
    EditText fieldlabel, headerETL, instructionETL;
    MetaDataForForm formset;
    ArrayList<newFormLabelResponse> metaformList;
    ProgressDialog progressDialog;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    FloatingActionsMenu menuMultipleAction;
    ArrayList<newFormLabelResponse> newFormList;
    // String userguid = "ddcf799e-93b3-11e5-9328-0aa26b506601";
    //  String userguid = "ddd1f884-93b3-11e5-9328-0aa26b506601";
    //  String password = "cffdafe6c0836f9f6fa7eb08a58523dce6c0748e";
    LinearLayout llContainer1, default_itm;
    TextView mHeader;
    Button savelabelbtn, closelabelbtn;
    public PopupWindow popup;
    androidx.appcompat.app.ActionBar actionBar;
    String password = null;
    View cView;
    LocationdetailAdapter locaDetailAdapter;
    private boolean invalidating = false;
    boolean eventClosed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_addform);
        context = this;
        init();

        Toast.makeText(context, getString(R.string.you_can_add_four_labels), Toast.LENGTH_SHORT).show();

//        View cView = getLayoutInflater().inflate(R.layout.text_view, null);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.qnopy_icon);
            actionBar.setTitle("Qnopy");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        iList = new ArrayList<form_item>();

        default_itm = findViewById(R.id.labeldefaultItem);
        form_item ob = new form_item();
        ob.setId(R.id.labeldefaultItem);
        ob.setLlParent(default_itm);
        iList.add(ob);

        Intent intent = getIntent();
        mobileappid = intent.getIntExtra("appId", 11);
        password = intent.getStringExtra("password");

        //    siteID = intent.getIntExtra("siteID", 12);

        loadSpinnerData();
        loadTypeSpinner();

        spinnerfieldtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentinputtype = parent.getItemAtPosition(position).toString();
                Log.i(TAG, "" + currentinputtype);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerlabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // item = parent.getItemAtPosition(position).toString();
                newFormLabelResponse item = (newFormLabelResponse) parent.getItemAtPosition(position);
                roworder = Integer.parseInt(item.getRowOrder());
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        savelabelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated(iList)) {
                    if (CheckNetwork.isInternetAvailable(context)) {
                        new PostAddLabelTask().execute();
                    } else {
                        Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();

                           /* MetaDataSource src = new MetaDataSource(context);
                            int res = src.storeField(metaformList, true);//Offline creation Location
                            Log.i(TAG, "No.of Forms added :" + res);
                            Toast.makeText(context, "New Field Added Successfully!", Toast.LENGTH_LONG).show();
                            if (CheckNetwork.isInternetAvailable(context)) {
                                new PostAddLabelTask().execute();
                            }
                            finish();*/
                    }
                }
            }
        });

        closelabelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.are_you_sure_to_cancel))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private boolean isValidated(ArrayList<form_item> data) {
        metaformList = new ArrayList<>();

        for (form_item item : data) {
            LinearLayout parent = item.getLlParent();
            int count = parent.getChildCount();
            newFormLabelResponse obj = new newFormLabelResponse();
            //obj.setRowOrder(String.valueOf(roworder));
            //obj.setFieldParameterLabelAlias(paramlabel);
            //obj.setFieldInputType(fieldtype);
            //   obj.setSiteId(0);
            // obj.setLocationId(0);
            obj.setMobileAppId(mobileappid);
            // obj.setFieldParameterId(paramid);

            try {

                for (int i = 0; i < count; i++) {
                    View cview = parent.getChildAt(i);
                    if (cview instanceof EditText) {
                        EditText child = (EditText) cview;
                        String value = child.getText().toString();
                        roworder++;

                        if (value.isEmpty()) {//location
                            child.setError(getString(R.string.no_value_entered));
                            child.requestFocus();
                            return false;
                        } else {
                            cview = parent.getChildAt(i + 1);

                            if (cview instanceof Spinner) {
                                Spinner sp = (Spinner) cview;
                                String spval = sp.getSelectedItem().toString();
                                if (spval.isEmpty()) {//location
                                    child.setError(getString(R.string.no_value_entered));
                                    child.requestFocus();
                                    return false;
                                } else {
                                    obj.setFieldParameterLabelAlias(value);
                                    obj.setRowOrder(String.valueOf(roworder));
                                    obj.setFieldInputType(spval);
                                    obj.setLovId("0");
                                    obj.setValueType("null");
                                    obj.setLocationId(0);
                                    Long id = -(System.currentTimeMillis() / 1000);
                                    obj.setFieldParameterId(id + "");
                                    // obj.setFieldParameterId(id);
                                    //obj.setFieldParameterId(id);
                                    metaformList.add(obj);
                                    break;

                                }
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (menuMultipleAction.isExpanded()) {

                Rect outRect = new Rect();
                menuMultipleAction.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                    menuMultipleAction.collapse();
            }

            try {
                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.dispatchTouchEvent(ev);
    }*/

    private void loadTypeSpinner() {

        List<String> list = new ArrayList<String>();
        list.add("SELECT");
        list.add("TEXT");
        list.add("NUMERIC");
        list.add("RADIO");
        list.add("SPINNER");
        list.add("Date");
        list.add("Time");

        dataadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfieldtype.setAdapter(dataadapter);
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
                labelcount++;

                if (labelcount <= 3) {
                    if (labelcount == 3) {
                        item.setVisible(false);

                        new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.add_field))
                                .setMessage(getString(R.string.you_can_add_four_fields))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout llview = new LinearLayout(context);
                    llview.setId(labelcount);
                    llview.setLayoutParams(params);
                    llview.setGravity(Gravity.CENTER);
                    llview.setWeightSum(1);
                    llview.setOrientation(LinearLayout.HORIZONTAL);
                    llview.addView(getEditText(context, R.id.fieldlabel + labelcount, getString(R.string.label)));
                    llview.addView(getSpinner(context, R.id.spinnerfieldtype + labelcount));
                    llview.addView(getDeleteButton(context, labelcount));

                    llContainer1.addView(llview);

                    form_item ob = new form_item();
                    ob.setId(labelcount);
                    ob.setLlParent(llview);
                    iList.add(ob);
                    break;
                } else {
                    item.setVisible(false);
                    Toast.makeText(context, getString(R.string.you_can_add_four_labels), Toast.LENGTH_LONG).show();
                }

            case android.R.id.home:
                finish();
        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ImageButton getDeleteButton(Context context, final int id) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(8, 0, 0, 0);

        ImageButton dbutton = new ImageButton(context);
        dbutton.setId(id);
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            dbutton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_menu_delete));
        } else {
            dbutton.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_delete));
        }

        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Location Child Count:" + iList.size());
                for (form_item item : iList) {
                    if (item.getId() == id) {
                        llContainer1.removeView(item.getLlParent());
                        iList.remove(item);
                        if (labelcount < 3)
                            Log.i(TAG, "Location Child Count after Remove:" + iList.size());
                        break;
                    }
                }
            }

        });
        return dbutton;
    }

    private EditText getEditText(final Context context, int id, String hint) {
        final EditText eBox = new EditText(context);

        eBox.setId(id);
        eBox.setHint(hint);
//        eBox.setGravity(Gravity.CENTER);

        final LinearLayout.LayoutParams lparams;
        if (hint.equals("Label")) {
            lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.weight = 1F;
        } else {
            lparams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.35F);
            lparams.setMargins(4, 0, 0, 0);
        }
        eBox.setLayoutParams(lparams);
        return eBox;
    }

       /* eBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MetaDataSource datasrc = new MetaDataSource(context);
                MetaDataForForm form = new MetaDataForForm();
                String label_name = s.toString();
              *//*  if(datasrc.islocationAlreadyExists(loc_name, Integer.parseInt(siteID))){
                    eBox.setError("Location Already Added for this site");
                }*//*
                form.setMetaParamLabel(label_name);
                form.setMetaRowOrder(roworder);
                form.setMetaInputType(fieldtype);

                datasrc.addformdetails(form);
            }
        });

        return eBox;
    }*/


    private Spinner getSpinner(final Context context, int id) {

        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(8, 0, 0, 0);

        final Spinner spin = new Spinner(context);

        loadTypeSpinner();

        spin.setAdapter(dataadapter);

        spin.setId(id);

        return spin;
    }


    private void init() {
        spinnerlabel = findViewById(R.id.spinnerlabel);
        llContainer1 = findViewById(R.id.label_container);
        fieldlabel = findViewById(R.id.fieldlabel);
        spinnerfieldtype = findViewById(R.id.spinnerfieldtype);
        // loc_nameET = (EditText) findViewById(R.id.location_nameET);
        headerETL = findViewById(R.id.headerETL);
        instructionETL = findViewById(R.id.instructionETL);

        savelabelbtn = findViewById(R.id.savebuttonnew);

        closelabelbtn = findViewById(R.id.closebuttonnew);


        // btnadd = (Button) findViewById(R.id.btnadd);
        //  txtinputtype = (TextView) findViewById(R.id.txtinputtype);
    }

    private void loadSpinnerData() {
        MetaDataSource dataSource = new MetaDataSource(context);

        List<newFormLabelResponse> labels = new ArrayList<>();

        // Spinner Drop down elements

        labels = dataSource.getMetaDataForForm(mobileappid);

        ArrayList<newFormLabelResponse> metaList = dataSource.getMetaDataForForm(mobileappid);

        Log.i(TAG, "data:" + labels);

        adapter = new SpinnerCustomAdapter(context, android.R.layout.simple_spinner_dropdown_item, metaList);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerlabel.setAdapter(adapter);

    }


    private class PostAddLabelTask extends AsyncTask<MediaType, Void, Object> {

        @Override
        protected void onPreExecute() {

            Log.d(TAG, " onPreExecute: Populating the request objects");
            // Init the progress dialog
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }// end of onPreExecute


        @Override
        protected Object doInBackground(MediaType... params) {

            newLabelResponseModel respModel = null;

            try {
                // Get handle to HTTP service

                //  Log.d(TAG, " doInBackground  username:" + username + " ,Guid: " + guid + ",siteID: " + siteID);


                String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);

                String guid = Util.getSharedPreferencesProperty(context, username);

                //  String password=getPassword();


                // String guid = Util.getSharedPreferencesProperty(context, getUsername());


//                respModel = mAquaBlueService.v1_setAddLabelData(GlobalStrings.Local_Base_URL,
//                        getResources().getString(R.string.prod_add_form_label), metaformList, guid, password);
//
                respModel = mAquaBlueService.v1_setAddLabelData(getResources().getString(R.string.prod_base_uri),
                        getResources().getString(R.string.prod_add_form_label), metaformList, guid, password);


            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error=" + e.getLocalizedMessage());
            }

            return respModel;
        }// end ofdoInBackground


        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            Log.d(TAG, " onPostExecute: Result = " + result);

            progressDialog.dismiss();
            username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
            pass = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
            if (result != null) {
                newLabelResponseModel respModel = (newLabelResponseModel) result;


                if (respModel.isSuccess()) {
                    MetaDataSource loc = new MetaDataSource(context);
                    int res = loc.storeField(respModel.getData(), false);

                    //Server Responce of Location

                    Log.i(TAG, "No.of NewForm/s added :" + res);
                    Toast.makeText(context, getString(R.string.new_label_added_successfully), Toast.LENGTH_LONG).show();

                    Intent out = new Intent();
                    out.putExtra("Success", true);
                    setResult(RESULT_OK, out);

                    // refreshScreen();
                    finish();
                } else {
                    GlobalStrings.responseMessage = respModel.getMessage();
                    HttpStatus respCode = respModel.getResponseCode();
                    if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                        Util.setDeviceNOT_ACTIVATED((Activity) context, username, pass);
                        // finish();
                    } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                        Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, getString(R.string.please_try_again), Toast.LENGTH_LONG).show();
            }
        }// end of onPostExecute
    }// end of PostMessageTask

    public void refreshScreen() {
        locaDetailAdapter.notifyDataSetChanged();
    }

    public void populateLocationDetail() {

        String deviceID =
                Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);

        // DeviceInfo.getDeviceID(CurrentContext);
        Log.i(TAG, "Current DeviceID:" + deviceID);
        if (deviceID == null || deviceID.isEmpty()) {
            deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID);
            Log.i(TAG, "Stored Current DeviceID:" + deviceID);

        }
    }

    public void setInvalidating() {
        invalidating = true;
    }


}

