package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.LovAdapter;
import qnopy.com.qnopyandroid.clientmodel.Lov;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

/**
 * Created by QNOPY on 5/4/2018.
 */
public class LovListActivity extends ProgressDialogActivity {

    private static final String TAG = "LovListActivity";
    List<Lov> values;
    LovAdapter adapter;
    Toolbar toolbar;
    ActionBar actionBar;
    ListView listView;
    LinearLayout lov_list_container;
    TextView emptylist_view;
    Context context;
    MaterialSearchView searchView;
    public static Activity lovActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lovlistview);

        toolbar = findViewById(R.id.lov_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Select Lov");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.lovListView);
        //   searchTV = (SearchView) findViewById(R.id.search_txt);
        lov_list_container = findViewById(R.id.lovLis);
        emptylist_view = findViewById(R.id.empty_lov);
        searchView = findViewById(R.id.search_view);

        context = this;
        lovActivity = this;
        GlobalStrings.currentContext = this;

        LovDataSource lovDataSource = new LovDataSource(context);
        values = lovDataSource.getLovList();
        if (values != null && values.size() > 0) {
            adapter = new LovAdapter(
                    context,
                    R.layout.list_item, values);
            listView.setAdapter(adapter);
            emptylist_view.setVisibility(View.GONE);
        } else {
            emptylist_view.setVisibility(View.VISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Lov lov = (Lov) parent.getAdapter().getItem(position);//values.get(position);
                Intent intent = new Intent(context, AddLovActivity.class);
                intent.putExtra("LOV_ID", lov.getLovId());
                intent.putExtra("LOV_NAME", lov.getLovName());
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i(TAG, "Search Text:" + query);
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
}
