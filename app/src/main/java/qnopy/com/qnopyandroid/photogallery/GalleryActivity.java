package qnopy.com.qnopyandroid.photogallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Attachment;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class GalleryActivity extends ProgressDialogActivity {

    GridView mGrid;
    AppsAdapter mAdapter;
    Bundle extras;
    ImageView image;
    TextView notesText;
    ImageView editNotes;
    ImageView back;

    private int eventID = 0;
    private String locID = "0";
    private int siteID = 0;
    private int userID = 0;
    private int mobileAppID = 0;
    private int setID = 0;
    public String savedFileName = null; //to be returned back to calling intent
    ActionBar actionBar;

    AttachmentDataSource ads;
    List<Attachment> list = new ArrayList<Attachment>();
    public List<String> thumbIds = null;
    public List<Long> attachID = null;
    CheckBox cBox = null;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    protected String notes;

    int imageWidth = 0;
    int notesWidth = 0;
    int reDrawWidth = 0;
    int checkBoxWidth = 0;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        extras = getIntent().getExtras();
        setUserID(extras.getInt("USER_ID"));
        setSiteID(extras.getInt("SITE_ID"));
        setEventID(extras.getInt("EVENT_ID"));
        setLocID(extras.getString("LOC_ID"));
        setMobileAppID(extras.getInt("MOBILE_APP_ID"));
        setSetID(extras.getInt("SET_ID"));
        ads = new AttachmentDataSource(mContext);
        loadApps();
        // TODO: 09-Nov-15  filter by EventID and locationID Added

        list = ads.getAttachmentDataListForSiteFromDB(
                AttachmentDataSource.DataForSync.DataNotSynced, getSiteID(), getEventID(), getLocID(), getMobileAppID());
        thumbIds = getImageUrl();
        attachID = getAttachID();

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.qnopy_icon);
            actionBar.setTitle("Gallery");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000)
                // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .enableLogging() // Not necessary in common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                // .showStubImage(R.drawable.stub_image)
                // .showImageForEmptyUri(R.drawable.image_for_empty_url)
                .cacheInMemory().cacheOnDisc().build();

        setContentView(R.layout.grid_1);
        mGrid = (GridView) findViewById(R.id.myGrid);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // int columnWidth = (dm.widthPixels) / 3;
        int columnWidth = (dm.widthPixels);

        imageWidth = (30 * columnWidth) / 100;
        notesWidth = (50 * columnWidth) / 100;
        reDrawWidth = (10 * columnWidth) / 100;
        checkBoxWidth = (5 * columnWidth) / 100;

        System.out.println("width " + columnWidth + imageWidth + notesWidth
                + reDrawWidth + checkBoxWidth);


        mGrid.setColumnWidth(columnWidth);

        if (thumbIds.size() == 0 || thumbIds == null) {
            Toast.makeText(getApplicationContext(), "You have not saved any photos yet.",
                    Toast.LENGTH_LONG).show();
            this.finish();
        }

        mAdapter = new AppsAdapter();
        mGrid.setAdapter(mAdapter);
        mGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(),
                        DisplayImage.class);
                i.putExtra(DisplayImage.EXTRA_IMAGE, (int) id);
                i.putExtra("SiteID", getSiteID());
                i.putExtra("EVENT_ID", getEventID());
                i.putExtra("LOC_ID", getLocID());

                startActivity(i);
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addNotes(final String path, final String note,
                          final TextView textView) {
        notes = note;

        AlertDialog.Builder editalert = new AlertDialog.Builder(
                GalleryActivity.this);

        editalert.setTitle("Enter Notes");
        final EditText notesInput = new EditText(GalleryActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        notesInput.setLayoutParams(lp);
        notesInput.setText(note);

        if (notes != null) {
            notesInput.setSelection(notes.length());
        }
        editalert.setView(notesInput);

        editalert.setPositiveButton("POST",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        notes = notesInput.getText().toString();
                        AttachmentDataSource attach = new AttachmentDataSource(mContext);
                        String fparamID = attach.getFieldParameterID(getSiteID(), getEventID() + "", getLocID(), getMobileAppID(), path);
                        if (fparamID != null && !fparamID.isEmpty()) {
                            //19-Jan-16 Update note in d_FieldData Table
                            new FieldDataSource(mContext).updateNotesForLabel(getEventID() + "", Integer.parseInt(fparamID), getSetID(),
                                    getLocID(), notes, getSiteID(), getMobileAppID());
                        }
                        int ret = attach.updateNotes(getSiteID(), getEventID() + "",
                                path, notes);
                        System.out.println("return value = " + ret);
                        textView.setText(notes);
                    }
                });

        editalert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera_gallery:
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    public void handleCamera() {
//        SharedPref.setCamOrMap();
//        SharedPref.putInt("CURRENT_FORM_NUM", getCurrentFormNum());
//        SharedPref.putInt("CURRENT_SET", getCurSetID());
//        try {
//            if (getAttachmentNamePrefix() == null) {
//                Toast.makeText(this, "Name Prefix not found",
//                        Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "p_"
//                    + getAttachmentNamePrefix()); // create a file to save the
//            // image
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image
//            // file name
//
//            System.out.println("gggg" + "path in the URI is"
//                    + fileUri.getPath());
//            // start the image capture Intent
//            GlobalStrings.captureTime = System.currentTimeMillis();
//            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        } catch (Exception e) {
//            System.out.println("camera" + e.getLocalizedMessage());
//        }
//    }

//    public String getAttachmentNamePrefix(String siteID,String locID,) {
//        return attachmentNamePrefix;
//    }


    public List<String> getImageUrl() {

        List<String> tempThumbIds = new ArrayList<String>();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                Attachment attach = list.get(i);
                tempThumbIds.add(attach.getFileLocation());
                System.out.println("Image..." + tempThumbIds.get(i));
            }
        } else {
        }

        return tempThumbIds;
    }

    public List<Long> getAttachID() {

        List<Long> tempAttachID = new ArrayList<Long>();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                Attachment attach = list.get(i);
                tempAttachID.add(attach.getId());
                System.out.println("Image..." + tempAttachID.get(i));
            }
        } else {
        }

        return tempAttachID;
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    }

    public class AppsAdapter extends BaseAdapter {

        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final CheckableLayout l;
            String path = thumbIds.get(position);

            ViewHolder holder = new ViewHolder();

            final AttachmentDataSource attach = new AttachmentDataSource(mContext);
            notes = attach.readNotes(getSiteID(), getEventID() + "", path);

            if (convertView == null) {
                if (image != null) {
                    // i = null;
                    System.gc();
                }
                image = new ImageView(GalleryActivity.this);
                image.setScaleType(ImageView.ScaleType.CENTER);
                image.setLayoutParams(new LayoutParams(imageWidth, 190));
                image.setBackgroundColor(Color.WHITE);

                holder.textView = new TextView(GalleryActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        notesWidth, 190);
                params.leftMargin = 3;
                holder.textView.setLayoutParams(params);
                holder.textView.setBackgroundColor(Color.LTGRAY);
                // final AttachmentDataSource attach = new
                // AttachmentDataSource();
                // notes = attach.readNotes(getSiteId(), getEventID(), path);
                if (notes == null) {
                    notes = "Notes";
                }
                holder.textView.setText(notes);
                holder.textView.setTextSize(17f);
                holder.textView.setGravity(Gravity.CENTER);
                // holder.textView = notesText;
                holder.pos = position;
                holder.textView.setTag(holder);

                holder.textView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ViewHolder vh = (ViewHolder) arg0.getTag();
                        String path = thumbIds.get(vh.pos);
                        String note = attach.readNotes(getSiteID(),
                                getEventID() + "", path);
                        addNotes(path, note, vh.textView);
                    }
                });

                /*
                 * editNotes = new ImageView(GalleryActivity.this);
                 * LinearLayout.LayoutParams par = new
                 * LinearLayout.LayoutParams(90, 90); par.gravity =
                 * Gravity.CENTER_VERTICAL; // par.leftMargin = 5;
                 * editNotes.setLayoutParams(par);
                 * editNotes.setBackgroundResource(R.drawable.edit_notes);
                 * editNotes.setTag(holder); // editNotes.setText("Edit notes");
                 * editNotes.setOnClickListener(new OnClickListener() {
                 *
                 * @Override public void onClick(View v) { ViewHolder vh =
                 * (ViewHolder) v.getTag(); String path = thumbIds.get(vh.pos);
                 * String note = attach.readNotes(getSiteId(), getEventID(),
                 * path); addNotes(path, note, vh.textView);
                 * mAdapter.notifyDataSetChanged(); } });
                 */

				/*back = new ImageView(GalleryActivity.this);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(
						reDrawWidth, 70);
				par.gravity = Gravity.CENTER_VERTICAL;
				par.leftMargin = 5;
				back.setLayoutParams(par);
				back.setBackgroundResource(R.drawable.go_back);
				// editNotes.setText("Edit notes");
				back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});*/

                l = new CheckableLayout(GalleryActivity.this);
                l.setBackgroundColor(getResources().getColor(R.color.white_pressed));
                // l.setLayoutParams(new GridView.LayoutParams(199, 199));
                l.setLayoutParams(new GridView.LayoutParams(
                        LayoutParams.MATCH_PARENT, 199));

                l.addView(image);
                l.addView(holder.textView);
                // l.addView(editNotes);
//				l.addView(back);

            } else {
                l = (CheckableLayout) convertView;
                try {
                    image = (ImageView) l.getChildAt(0);
                    holder.textView = (TextView) l.getChildAt(1);
                    if (notes == null) {
                        notes = "Notes";
                    }
                    holder.pos = position;
                    holder.textView.setTag(holder);
                    holder.textView.setText(notes);

                    // editNotes = (ImageView) l.getChildAt(2);
//					back = (ImageView) l.getChildAt(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            String info = thumbIds.get(position);
//			Uri uri = Uri.parse(info);
//			System.out.println("URI " + uri);

            try {
                imageLoader.displayImage("file://" + info, image, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(Bitmap loadedImage) {

                            }
                        });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            return l;
        }

        public final int getCount() {
            if (thumbIds != null) {
                return thumbIds.size();
            }
            return 0;
        }

        public final Object getItem(int position) {
            return thumbIds.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    public class ViewHolder {
        TextView textView;
        int pos;
    }

    public class CheckableLayout extends LinearLayout implements Checkable {
        private boolean mChecked;
        ImageView chek = new ImageView(getApplicationContext());


        public CheckableLayout(Context context) {
            super(context);
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
            chek.setBackgroundResource(R.mipmap.checkbox);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    checkBoxWidth, 30);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 10, 15, 0);
            chek.setLayoutParams(params);
            if (checked) {
                if (getChildCount() > 1) {
                    removeView(chek);
                    addView(chek);
                    invalidate();
                    refreshDrawableState();
                } else {
                    System.out.println("child " + getChildCount());
                    requestLayout();
                    addView(chek);
                    System.out.println("child " + getChildCount());
                    invalidate();
                    refreshDrawableState();
                }
            } else {
                if (getChildCount() > 1) {
                    removeView(chek);
                } else {

                }
            }

        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }

    public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {

        public List<Integer> positions = new ArrayList<Integer>();

        @SuppressWarnings("static-access")
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select Items");
            mode.setSubtitle("1 item selected");
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            MenuItem item = menu.findItem(R.id.deleteItem);
            item.setShowAsAction(item.SHOW_AS_ACTION_ALWAYS);// value is 2
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<String> path = new ArrayList<String>();
            switch (item.getItemId()) {

                case R.id.deleteItem:
                    try {
                        if (positions != null) {
                            System.out.println("thumb " + thumbIds + " "
                                    + thumbIds.size());
                            for (int i = 0; i < positions.size(); i++) {
                                path.add(thumbIds.get(positions.get(i)));

                                System.out.println("selected " + path);
                                String filePath = thumbIds.get(positions.get(i));
                                deleteFileFromStorage(filePath);
                            }

                            if (ads.deleteAttachments(path)) {
                                System.out.println("thumb " + thumbIds + " "
                                        + thumbIds.size());
                                for (int i = 0; i < path.size(); i++) {

                                    int pos = -1;

                                    for (int j = 0; j < thumbIds.size(); j++) {
                                        if (thumbIds.get(j).equalsIgnoreCase(
                                                path.get(i))) {
                                            pos = j;
                                            break;
                                        }
                                    }
                                    if (pos != -1) {
                                        thumbIds.remove(pos);
                                    }
                                }
                                positions.clear();

                                System.out.println("thumb " + thumbIds + " "
                                        + thumbIds.size());
                                mAdapter.notifyDataSetChanged();
                                mGrid.setAdapter(mAdapter);
                                mGrid.invalidateViews();
                                Toast.makeText(getApplicationContext(), "Deleted",
                                        Toast.LENGTH_SHORT).show();
                            }

                            if (thumbIds.size() == 0) {
                                Toast.makeText(getApplicationContext(),
                                        "No more files to Display..",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                    mode.finish();
                    break;

                default:
                    break;
            }
            return true;
        }

        public void deleteFileFromStorage(String filePath) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{filePath}, null, null);
        }

        public void onDestroyActionMode(ActionMode mode) {
            positions.clear();
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            int selectCount = mGrid.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("1 item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }

            if (checked) {
                positions.add(position);
            } else {
                for (int i = 0; i < positions.size(); i++) {
                    if (positions.get(i) == position) {
                        positions.remove(i);
                    }
                }
            }
            System.out.println("selected " + positions);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater minflater = new MenuInflater(mContext);
        minflater.inflate(R.menu.gallery_menu, menu);
        return true;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (image != null) {
            image.setImageURI(null);
        }
    }

    private int getEventID() {
        return eventID;
    }

    private void setEventID(int eventID) {
        this.eventID = eventID;
    }

    private String getLocID() {
        return locID;
    }

    private void setLocID(String locID) {
        this.locID = locID;
    }

    private int getSiteID() {
        return siteID;
    }

    private void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    private int getUserID() {
        return userID;
    }

    private void setUserID(int userID) {
        this.userID = userID;
    }

    private int getMobileAppID() {
        return mobileAppID;
    }

    private void setMobileAppID(int mobileAppID) {
        this.mobileAppID = mobileAppID;
    }

    private int getSetID() {
        return setID;
    }

    private void setSetID(int setID) {
        this.setID = setID;
    }
}
