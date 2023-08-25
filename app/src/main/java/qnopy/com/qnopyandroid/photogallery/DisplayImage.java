package qnopy.com.qnopyandroid.photogallery;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class DisplayImage extends ProgressDialogActivity implements OnClickListener {
    private String IMAGE_CACHE_DIR = "images";
    public static String EXTRA_IMAGE = "extra_image";
    int siteID = 0;
    String LocID = "0";
    String eventID = "0";
    int mobAppID = 0;
    private String siteName = "";
    private String locationName = "";
    public ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    public ViewPager mPager;
    ActionBar actionBar;
    Context mcontext;

    File file[];
    static boolean demoFlag = false;

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
//            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_image);
        mcontext = this;
        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        siteID = getIntent().getIntExtra("SiteID", 0);
        eventID = getIntent().getStringExtra("EVENT_ID");
        LocID = getIntent().getStringExtra("LOC_ID");
        mobAppID = getIntent().getIntExtra("MOB_APP_ID", 0);
        siteName = getIntent().getStringExtra("SITE_NAME");
        locationName = getIntent().getStringExtra("LOCATION_NAME");

        Log.i("Display Image", "Received siteID:" + siteID + " EventID:" + eventID + " LocationID:" + LocID);
        Images i = new Images(mcontext, siteID, LocID, eventID, siteName, locationName, mobAppID);

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.

        final int longest = (Math.max(height, width)) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        if (getIntent().hasExtra("demo")) {
            demoFlag = true;

            String path = Environment.getExternalStorageDirectory().toString() + GlobalStrings.DEMO_IMAGE_PATH;
            Log.d("Files", "Path: " + path);
            File f = new File(path);
            file = f.listFiles();
            mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), file.length);

        } else {
            demoFlag = false;
            mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), i.imageUrls.length);
        }

        // Set up ViewPager and backing adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
        mPager.setOffscreenPageLimit(2);
        mPager.setBackgroundColor(Color.GRAY);
        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
//        if (Utils.hasHoneycomb()) {
        actionBar = getSupportActionBar();

        // Hide title text and set home_selected as up
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
// Hide and show the ActionBar as the visibility changes
            mPager.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int vis) {
                            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                actionBar.hide();
                            } else {
                                actionBar.show();
                            }
                        }
                    });

            // Start low profile mode and hide ActionBar
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            actionBar.hide();
//        }

        }


        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
//            case R.id.clear_cache:
//                mImageFetcher.clearCache();
//                Toast.makeText(
//                        this, R.string.clear_cache_complete_toast,Toast.LENGTH_SHORT).show();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    public class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public int mSize;
        Images i = new Images(mcontext, siteID, LocID, eventID, siteName, locationName, mobAppID);

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            if (demoFlag) {
                return ImageDetailFragment.newInstance(file[position].toString(),
                        siteID, LocID, eventID, siteName,
                        locationName, mobAppID + "");

            } else {
                return ImageDetailFragment.newInstance(i.imageUrls[position], siteID,
                        LocID, eventID, siteName,
                        locationName, mobAppID + "");
            }
        }
    }

    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
