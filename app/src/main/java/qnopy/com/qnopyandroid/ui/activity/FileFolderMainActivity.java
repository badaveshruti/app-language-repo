package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.ui.fragment.FileListFragment;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class FileFolderMainActivity extends ProgressDialogActivity
        implements FileListFragment.OnListFragmentInteractionListener {

    ActionBar actionBar;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_folder_main);
        mContext = this;

//        Util.setOverflowButtonColor(FileFolderMainActivity.this, Color.BLACK);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle(getString(R.string.project_folder));
            actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setCustomView(cView);
        }
    }

    @Override
    public void onListFragmentInteraction(FileFolderItem item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater minflater = getMenuInflater();
        minflater.inflate(R.menu.menu_filefolder_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_sync:
                if (CheckNetwork.isInternetAvailable(mContext)) {
                    finish();
                    startActivity(new Intent(mContext, FileFolderSyncActivity.class));
                } else {
                    Toast.makeText(mContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }

                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;

    }
}
