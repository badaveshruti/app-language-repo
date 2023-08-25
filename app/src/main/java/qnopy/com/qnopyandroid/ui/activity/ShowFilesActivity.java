package qnopy.com.qnopyandroid.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.ShowFilesAdapter;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.FileUtils;
import qnopy.com.qnopyandroid.util.Util;

import static androidx.core.content.FileProvider.getUriForFile;

public class ShowFilesActivity extends ProgressDialogActivity implements ShowFilesAdapter.OnFileClickListener {

    private RecyclerView rvFiles;
    private String folderName = "";
    private CustomTextView tvNoFileFound;
    private String siteName = "";
    private String filePath = "";
    private boolean isFromPrintCOC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            folderName = getIntent().getStringExtra(GlobalStrings.FOLDER_NAME);
            siteName = getIntent().getStringExtra(GlobalStrings.CURRENT_SITENAME);

            isFromPrintCOC = getIntent().getAction().contains(GlobalStrings.PRINT_COC);
            filePath = getIntent().getStringExtra(GlobalStrings.KEY_FILE_PATH);
        }

        if (isFromPrintCOC)
            getSupportActionBar().setTitle("COC Files");
        else getSupportActionBar().setTitle(siteName);

        rvFiles = findViewById(R.id.rvFiles);
        tvNoFileFound = findViewById(R.id.tvNoFiles);
        getFolderData();
    }

    private void getFolderData() {
        String path = "";

        if (!isFromPrintCOC) {
            path = FileUtils.getPathOfStoredFile(GlobalStrings.COC_DOCS_FOLDER)
                    + "/" + folderName + "/";
        } else path = filePath;

        ArrayList<String> listFiles = Util.getFolderData(path);

        Collections.sort(listFiles, (f1, f2) -> Long.compare(new File(f2).lastModified(), new File(f1).lastModified()));

        ShowFilesAdapter showFilesAdapter = new ShowFilesAdapter(listFiles, this, this);
        rvFiles.setAdapter(showFilesAdapter);

        if (listFiles.size() > 0) {
            rvFiles.setVisibility(View.VISIBLE);
            tvNoFileFound.setVisibility(View.GONE);
        } else {
            rvFiles.setVisibility(View.INVISIBLE);
            tvNoFileFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFileClick(String path) {
        File fileLocation = new File(path);
        Uri contentUri = getUriForFile(getApplicationContext(), "com.aqua.fieldbuddy.provider", fileLocation);

        Intent csvIntent = new Intent(Intent.ACTION_VIEW);
        Util.setDataAndTypeForIntent(path, csvIntent, contentUri);
        csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        csvIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager manager = getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(csvIntent, 0);
        if (infos.size() > 0) {
            startActivity(Intent.createChooser(csvIntent, "Choose app to open"));
        } else {
            CustomToast.showToast(this, "You may not have a proper app " +
                    "for viewing this document", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}