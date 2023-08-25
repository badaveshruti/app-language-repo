package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.PreferenceAdapter;
import qnopy.com.qnopyandroid.clientmodel.pref_model;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class AppPreferencesActivity extends ProgressDialogActivity {

    RecyclerView recyclerView;
    TextView emptyView;
    Context context;
    private LinearLayoutManager layoutManager;
    PreferenceAdapter adapter;
    int userID;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_preferences);
        context = this;

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("App Preferences");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.pref_emptyView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

        ArrayList<pref_model> list = collectData();
        if (list == null || list.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            showData(list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
//                startActivity(new Intent(context, DashboardActivity.class));
                finish();
                break;
        }
        return true;
    }

    public ArrayList<pref_model> collectData() {
        ArrayList<pref_model> list = new ArrayList<>();
        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);
        list = ds.getAllUserFeatureAvailable(userID);
        return list;
    }

    public void showData(ArrayList<pref_model> list) {

        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new PreferenceAdapter(list, context);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
