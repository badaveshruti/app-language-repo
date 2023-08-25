package qnopy.com.qnopyandroid.photogallery;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.NewGalleryImageAdapter;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class NewGalleryActivity extends ProgressDialogActivity {

    private ImageView selectedImageView;

    private ImageView leftArrowImageView;

    private ImageView rightArrowImageView;

    private Gallery gallery;

    private int selectedImagePosition = 0;

    //	private List<Drawable> drawables;
    List<GalleryItem> drawables = null;
    Bundle extras;

    Context context;
    String siteID, siteName, locationID, locationName, childmobappID, eventID;

    private NewGalleryImageAdapter galImageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_main);

        context = this;
        setExtras();

//		getDrawablesList();
        getFormGalleryThumbList();
        setupUI();

    }

    private void setExtras() {
        extras = getIntent().getExtras();
        eventID = extras.getString("EVENT_ID");
        siteID = extras.getString("SITE_ID");
        locationID = extras.getString("LOCATION_ID");
        childmobappID = extras.getString("MOBAPP_ID");
        siteName = extras.getString("SITE_NAME");
        locationName = extras.getString("LOCATION_NAME");

    }

    private void setupUI() {

        selectedImageView = (ImageView) findViewById(R.id.selected_imageview);
        leftArrowImageView = (ImageView) findViewById(R.id.left_arrow_imageview);
        rightArrowImageView = (ImageView) findViewById(R.id.right_arrow_imageview);
        gallery = (Gallery) findViewById(R.id.gallery);

        leftArrowImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedImagePosition > 0) {
                    --selectedImagePosition;

                }

                gallery.setSelection(selectedImagePosition, false);
            }
        });

        rightArrowImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedImagePosition < drawables.size() - 1) {
                    ++selectedImagePosition;

                }

                gallery.setSelection(selectedImagePosition, true);

            }
        });

        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedImagePosition = pos;

                if (selectedImagePosition > 0 && selectedImagePosition < drawables.size() - 1) {

                    leftArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_enabled));
                    rightArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_enabled));

                }
                if (selectedImagePosition > 0) {
                    leftArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_enabled));

                }

                if (selectedImagePosition < drawables.size() - 1) {
                    rightArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_enabled));

                }
                if (selectedImagePosition == 0) {

                    leftArrowImageView.setImageDrawable(null);

                } else if (selectedImagePosition == drawables.size() - 1) {

                    rightArrowImageView.setImageDrawable(null);
                }


                changeBorderForSelectedImage(selectedImagePosition);
                setSelectedImage(selectedImagePosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        galImageAdapter = new NewGalleryImageAdapter(this, drawables);

        gallery.setAdapter(galImageAdapter);

        if (drawables.size() > 0) {

            gallery.setSelection(selectedImagePosition, false);

        }

        if (drawables.size() == 1) {

            rightArrowImageView.setImageDrawable(null);
        }

    }

    private void changeBorderForSelectedImage(int selectedItemPos) {

        int count = gallery.getChildCount();

        for (int i = 0; i < count; i++) {

            ImageView imageView = (ImageView) gallery.getChildAt(i);
            imageView.setBackgroundDrawable(null);
            imageView.setPadding(3, 3, 3, 3);

        }

        ImageView imageView = (ImageView) gallery.getSelectedView();
        imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selected_image_border));
        imageView.setPadding(3, 3, 3, 3);
    }

//	private void getDrawablesList() {
//
//		drawables = new ArrayList<Drawable>();
//		drawables.add(getResources().getDrawable(R.drawable.natureimage1));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage2));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage3));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage4));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage5));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage6));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage7));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage8));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage9));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage10));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage11));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage12));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage13));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage14));
//		drawables.add(getResources().getDrawable(R.drawable.natureimage15));
//
//	}

    public List<GalleryItem> getFormGalleryThumbList() {
        AttachmentDataSource ads = new AttachmentDataSource(context);
//		List<Attachment> list = ads.getAttachmentDataListFromDB(DataForSync.DataNotSynced);
        drawables = ads.getCardAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced,
                siteID, eventID, locationID, siteName, locationName, childmobappID);
        Collections.sort(drawables, new CustomComparator());
        if (drawables.size() < 1) {
            finish();
            Toast.makeText(context, "No pictures captured for this form", Toast.LENGTH_LONG).show();
        }
        return drawables;
    }


    private void setSelectedImage(int selectedImagePosition) {

//        BitmapDrawable bd = (BitmapDrawable) drawables.get(selectedImagePosition);
//        Bitmap b = Bitmap.createScaledBitmap(bd.getBitmap(), (int) (bd.getIntrinsicHeight() * 0.9), (int) (bd.getIntrinsicWidth() * 0.7), false);
//        selectedImageView.setImageBitmap(b);
//        selectedImageView.setScaleType(ScaleType.FIT_XY);


        Uri uri = Uri.fromFile(new File(drawables.get(selectedImagePosition).getFileLocation()));

        Picasso.get().load(uri)
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(selectedImageView);
    }

    private class CustomComparator implements Comparator<GalleryItem> {

        @Override
        public int compare(GalleryItem lhs, GalleryItem rhs) {

            if (lhs.getTxtDate() != null && rhs.getTxtDate() != null) {

                Long date1 = Long.parseLong(lhs.getTxtDate());
                Long date2 = Long.parseLong(rhs.getTxtDate());

                int count = date2.compareTo(date1);
                return count;

            }
            return 0;
        }
    }
}