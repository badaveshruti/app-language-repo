package qnopy.com.qnopyandroid.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.pchmn.materialchips.ChipView;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.db.MethodDataSource;
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class AutoCompleteHandlerActivity extends ProgressDialogActivity {

    String TAG = "ACHandlerActivity";
    Context context;
    ArrayList<ChipView> mchipList;
    ArrayList<String> acItemList;
    List<Chip> lovList;
    List<Chip> selectedChips;
    View editlov;

    ChipsInput mChipsInput;
    TextView helperView;
    androidx.appcompat.app.ActionBar actionBar;

    Bundle extras;
    String INPUT_TYPE = "MULTIAUTOCOMPLETE", SITE_ID, COMPANY_ID, POSITION;
    String sel_values = null;
    int LOV_ID = 0, PARENT_LOV_ID = 0, SET_ID = 0;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete_handler);
        context = this;
        mChipsInput = findViewById(R.id.chips_input);
        helperView = findViewById(R.id.helper_text);
        String boldString = getString(R.string.to_enter_custom_text_add_semicolon);
        Spanned result;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(boldString, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(boldString);
        }
        helperView.setText(result);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        extras = getIntent().getExtras();

        SITE_ID = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID);
        COMPANY_ID = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);

        if (extras != null) {//&& extras.containsKey("INPUT_TYPE")

            POSITION = extras.getString("POSITION");
            INPUT_TYPE = extras.getString("INPUT_TYPE");
            LOV_ID = extras.getInt("LOV_ID");
            SET_ID = extras.getInt("SET_ID");
            PARENT_LOV_ID = extras.getInt("PARENT_LOV_ID");
            sel_values = extras.getString("SELECTED_VALUES");

            Log.i(TAG, "Position=" + POSITION + ",Selected Values=" + sel_values + ",LovID=" + LOV_ID +
                    ", ParentLovID=" + PARENT_LOV_ID + ",InputType=" + INPUT_TYPE);
        } else {
            Log.e(TAG, "NO Extras found");
            finish();
        }

        userId = Integer.parseInt(Util.getSharedPreferencesProperty(this, GlobalStrings.USERID));

        mChipsInput.setShowChipDetailed(false);
        mChipsInput.setChipDeletable(true);
        mChipsInput.setShowMultipleChip(!INPUT_TYPE.equalsIgnoreCase("AUTOCOMPLETE"));

        if (INPUT_TYPE.equalsIgnoreCase("MULTIMETHODS")) {
            getMethodsList();
        } else {
            getLovList();
        }

        setAlreadySelectedValues(sel_values);

        // chips listener
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.i(TAG, "chip added : " + newSize);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.i(TAG, "chip removed: " + chip.getLabel());
                mChipsInput.onTap("");
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.i(TAG, "text changed: " + text.toString());
                String entered = text.toString();

                if (!TextUtils.isEmpty(entered)) {
                    if (INPUT_TYPE.equalsIgnoreCase("AUTOCOMPLETE")) {
                        selectedChips = new ArrayList<Chip>();
                        selectedChips = (List<Chip>) mChipsInput.getSelectedChipList();
                        for (Chip delete_chip : selectedChips) {
                            mChipsInput.removeChip(delete_chip);
                        }
                    }
                } else {
                    mChipsInput.onTap("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.autocomplete_screen_menu, menu);

//03-06-2018 SSL ERROR SO NEW TEMP APK
        if (!(INPUT_TYPE.equalsIgnoreCase("MULTIMETHOD"))) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    int userrole = 0;
                    editlov = findViewById(R.id.action_edit);

                    SiteUserRoleDataSource dataSource = new SiteUserRoleDataSource(context);

                    if (editlov != null) {

                        String strUserRole = "";
                        strUserRole = dataSource.getUserRole(SITE_ID,
                                userId + "");

                        if (strUserRole != null && !strUserRole.isEmpty()) {
                            try {
                                userrole = Integer.parseInt(strUserRole);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                        if (userrole != 0) {
                            if ((userrole == GlobalStrings.SUPER_ADMIN || userrole == GlobalStrings.CLIENT_ADMIN ||
                                    userrole == GlobalStrings.PROJECT_MANAGER)
                                    &&
                                    (INPUT_TYPE.equalsIgnoreCase("MULTIAUTOCOMPLETE") ||
                                            INPUT_TYPE.equalsIgnoreCase("AUTOCOMPLETE"))) {
                                editlov.setVisibility(View.VISIBLE);
                            } else {
                                editlov.setVisibility(View.GONE);
                            }
                        } else {
                            editlov.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if (requestCode == 1) {
        if (resultCode == RESULT_OK) {
            //Update List
            getLovList();
            // }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveSelectedData();
                return true;

            case R.id.action_cancel:

                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;

            case R.id.action_edit:
                Intent intent1 = new Intent(context, AddLovActivity.class);
                intent1.putExtra("LOV_ID", LOV_ID);
                intent1.putExtra("PARENT_LOV_ID", PARENT_LOV_ID);
                startActivity(intent1);
                finish();
                return true;

            case android.R.id.home:

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage(getString(R.string.do_you_want_to_save));
                alertBuilder.setTitle(getString(R.string.alert));
                alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveSelectedData();
                    }
                });
                alertBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                });

                Dialog alert = alertBuilder.create();
                alert.show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "dispatchTouchEvent()");
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void getLovList() {
        LovDataSource lovDS = new LovDataSource(context);
        lovList = lovDS.getACLovList(LOV_ID, SITE_ID, COMPANY_ID, PARENT_LOV_ID);
        // pass contact list to chips input
        mChipsInput.setFilterableList(lovList);
        //mChipsInput.refrshScreen();
    }

    private void getMethodsList() {
        MethodDataSource methodDS = new MethodDataSource(context);
        lovList = methodDS.getMethodsChipList();
        // pass list to chips input
        mChipsInput.setFilterableList(lovList);
    }

    private void saveSelectedData() {

        String listString = "";
        selectedChips = (List<Chip>) mChipsInput.getSelectedChipList();

        for (int i = 0; i < selectedChips.size(); i++) {
            Chip chip = selectedChips.get(i);
            if (i == 0) {
                listString = chip.getInfo();
            } else {
                listString = listString + "|" + chip.getInfo();
            }
        }
        Log.i(TAG, "Save Values:" + listString);

        if (INPUT_TYPE.equalsIgnoreCase("AUTOSETGENERATOR")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final String finalListString = listString;

            builder.setTitle(getString(R.string.auto_generator))
                    .setMessage(getString(R.string.do_you_want_to_create_or_update_set_for_selected_values))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.putExtra("POSITION", POSITION);
                            intent.putExtra("RESULT", finalListString);
                            intent.putExtra("AUTO_GENERATE", true);
                            Util.setSharedPreferencesProperty(context, GlobalStrings.AUTO_GENERATE, true + "");
                            intent.putExtra("OLD_VALUES", sel_values);
                            intent.putExtra("SET_ID", SET_ID);
                            intent.putExtra("INPUT_TYPE", INPUT_TYPE);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.putExtra("POSITION", POSITION);
                            intent.putExtra("RESULT", finalListString);
                            intent.putExtra("AUTO_GENERATE", false);
                            Util.setSharedPreferencesProperty(context, GlobalStrings.AUTO_GENERATE, false + "");
                            intent.putExtra("OLD_VALUES", sel_values);
                            intent.putExtra("SET_ID", SET_ID);
                            intent.putExtra("INPUT_TYPE", INPUT_TYPE);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("POSITION", POSITION);
            intent.putExtra("RESULT", listString);
            intent.putExtra("OLD_VALUES", sel_values);
            intent.putExtra("SET_ID", SET_ID);
            intent.putExtra("INPUT_TYPE", INPUT_TYPE);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void setAlreadySelectedValues(String selValues) {

        if (selValues != null && !selValues.isEmpty()) {
            String splitby = "\\|";
            String[] seperatedValues = selValues.split(splitby);

            for (String seperatedValue : seperatedValues) {

                if (lovList.size() < 1) {
                    String key = seperatedValue;
                    String temp_id = System.currentTimeMillis() + "" + Util.getRandomNumberInRange(0, 99999);
                    Log.e(TAG, "Value not found so save for key:" + seperatedValue + ",ID=" + temp_id);

                    Chip acChip = new Chip(temp_id, key, key);
                    mChipsInput.addChip(acChip);
                } else {
                    for (int j = 0; j < lovList.size(); j++) {

                        Chip chip = lovList.get(j);

                        if (chip.getInfo().equals(seperatedValue)) {
                            String key = chip.getLabel();

                            Chip acChip = new Chip(chip.getId().toString(), key, chip.getInfo());

                            mChipsInput.addChip(acChip);

                            break;
                            //STOP FOR FURTHER SEARCH AND GET OUT FROM HERE
                        }

                        if (j == (lovList.size() - 1)) {
                            //IF VALUE NOT FOUND IN LOV_LIST THEN SET KEY=VALUE
                            if (!chip.getInfo().equals(seperatedValue)) {

                                String key = seperatedValue;
                                String temp_id = System.currentTimeMillis() + "" + Util.getRandomNumberInRange(0, 99999);
                                Log.e(TAG, "Value not found so save for key:" + seperatedValue + ",ID=" + temp_id);

                                Chip acChip = new Chip(temp_id, key, key);
                                mChipsInput.addChip(acChip);
                            }
                        }

                    }
                }
            }
        }

        //27-Jul-17 APP CRASH KEY-BOARD NOT OPENED UP SO DELAYED FILTER
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
//                Toast.makeText(context, "Show First time List", Toast.LENGTH_SHORT).show();
                mChipsInput.onTap("");
            }
        }, 500);
    }
}
