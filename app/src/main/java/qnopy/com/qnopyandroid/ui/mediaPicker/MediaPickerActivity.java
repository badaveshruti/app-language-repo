package qnopy.com.qnopyandroid.ui.mediaPicker;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ui.mediaPicker.capturePhoto.CapturePhotoFragment;
import qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker.GalleryPickerFragment;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.MEDIA_TYPE_IMAGE;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.getMediaStorageDirOS11Up;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.getMediaStorageDirectory;

public class MediaPickerActivity extends ProgressDialogActivity {

    private ViewPager2 viewPagerPicker;
    private TabLayout tabLayout;
    private MediaFragPagerAdapter pagerAdapter;
    public static String dirPath = "";
    private boolean isCamera;
    public boolean isFromFormMaster;
    public int fieldParamId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_picker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.gallery));

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            dirPath = getMediaStorageDirOS11Up(MEDIA_TYPE_IMAGE, this).getAbsolutePath();
        } else {
            dirPath = getMediaStorageDirectory(MEDIA_TYPE_IMAGE).getAbsolutePath();
        }

        if (getIntent() != null) {
            isCamera = getIntent().getBooleanExtra(GlobalStrings.IS_CAMERA, false);
            isFromFormMaster = getIntent().getBooleanExtra(GlobalStrings.IS_FROM_FORM_MASTER, false);
            fieldParamId = getIntent().getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
        }

        setUpViewPager();
    }

    private void setUpViewPager() {
        tabLayout = findViewById(R.id.tabLayout);

        viewPagerPicker = findViewById(R.id.viewPagerPicker);
        pagerAdapter = new MediaFragPagerAdapter(this, getListFragment());
        viewPagerPicker.setAdapter(pagerAdapter);
        viewPagerPicker.setUserInputEnabled(false);

        if (!isCamera) {
            viewPagerPicker.setCurrentItem(0, true);
            getSupportActionBar().setTitle(getString(R.string.gallery));
            tabLayout.getTabAt(0).select();
        } else {
            viewPagerPicker.setCurrentItem(1, true);
            getSupportActionBar().setTitle(R.string.camera);
            tabLayout.getTabAt(1).select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    viewPagerPicker.setCurrentItem(0, true);
                    getSupportActionBar().setTitle(getString(R.string.gallery));
                } else {
                    viewPagerPicker.setCurrentItem(1, true);
                    getSupportActionBar().setTitle(getString(R.string.camera));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Fragment> getListFragment() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(GalleryPickerFragment.newInstance());
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.gallery)));

        fragments.add(CapturePhotoFragment.newInstance());
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.camera)));

        return fragments;
    }
}
