package qnopy.com.qnopyandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.ReportFieldsSelectionAdapter;
import qnopy.com.qnopyandroid.clientmodel.FieldParamInfo;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class ReportFieldsSelectionActivity extends ProgressDialogActivity {

    private String siteName;
    private int eventID;
    private int siteID;
    private int parent_appID;
    private int current_appID;
    private String locId;
    private String username;
    private SearchView searchView;
    private RecyclerView rvFieldParams;
    private ReportFieldsSelectionAdapter adapter;
    private ArrayList<FieldParamInfo> paramLabelList = new ArrayList<>();
    private String preferenceKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_fields_selection);

        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                siteName = extras.getString("SITE_NAME");
                eventID = extras.getInt("EVENT_ID");
                siteID = extras.getInt("SITE_ID");
                parent_appID = extras.getInt("PARENT_APP_ID");
                current_appID = extras.getInt("CURRENT_APP_ID");
                locId = extras.getString(GlobalStrings.KEY_LOCATION_ID);
                username = extras.getString("USER_NAME");
            }
        }

        setTitle("Report Fields");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferenceKey = siteID + "|" + eventID + "|" + current_appID + "|" + locId;
        setUpUi();
    }

    private void setUpUi() {
        setUpSearchView();

        rvFieldParams = findViewById(R.id.rvFieldParams);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        rvFieldParams.addItemDecoration(dividerItemDecoration);

        if (Util.getSharedPrefFieldProperty(this, preferenceKey) != null) {
            paramLabelList = new Gson().fromJson(Util.getSharedPrefFieldProperty(this,
                    preferenceKey), FieldParamsSelected.class).getFieldParamInfo();
        }

        if (paramLabelList.size() == 0) {
            FieldDataSource fieldSource = new FieldDataSource(this);
            paramLabelList = fieldSource.getParamLabelsForReport(current_appID);
        }

        if (paramLabelList.size() > 0) {
            adapter = new ReportFieldsSelectionAdapter(paramLabelList, this);
            rvFieldParams.setAdapter(adapter);
        }
    }

    private void setUpSearchView() {
        searchView = findViewById(R.id.searchViewFieldParams);
        searchView.setQueryHint("Search Report Fields");

        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        if (item.getItemId() == R.id.action_save) {
            showFormReport();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFormReport() {
        saveSelectedParams();

        Intent reportIntent = new Intent(this, ReportView.class);
        reportIntent.putExtra("CURRENT_APP_ID", current_appID);
        reportIntent.putExtra("PARENT_APP_ID", 0);
        reportIntent.putExtra("SITE_ID", siteID);
        reportIntent.putExtra("SITE_NAME", siteName);
        reportIntent.putExtra("USER_NAME", username);
        reportIntent.putExtra("EVENT_ID", eventID);
        reportIntent.putExtra(GlobalStrings.KEY_LOCATION_ID, locId);
        reportIntent.putExtra(GlobalStrings.FP_IDS_LIST, adapter.getSelectedItemsList());
        startActivity(reportIntent);
    }

    private void saveSelectedParams() {
        //adding selected field params to shared preferences
        FieldParamsSelected paramsSelected = new FieldParamsSelected(adapter.getItemsList()); //getting all array with already selected items
        Util.setSharedPrefFieldProperty(this, preferenceKey, new Gson().toJson(paramsSelected));
    }

    @Override
    public void onBackPressed() {
        saveSelectedParams();
        super.onBackPressed();
    }

    public class FieldParamsSelected {
        ArrayList<FieldParamInfo> fieldParamInfo;

        public FieldParamsSelected(ArrayList<FieldParamInfo> fieldParamInfo) {
            this.fieldParamInfo = fieldParamInfo;
        }

        public ArrayList<FieldParamInfo> getFieldParamInfo() {
            return fieldParamInfo;
        }

        public void setFieldParamInfo(ArrayList<FieldParamInfo> fieldParamInfo) {
            this.fieldParamInfo = fieldParamInfo;
        }
    }
}