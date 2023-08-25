/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qnopy.com.qnopyandroid.photogallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;


/**
 * This fragment will populate the children of the ViewPager from {@link }.
 */
public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;
    private Button delete;
    static int siteID = 0;
    static String LocID = "0";
    static String eventID = "0";
    static String mobappID = "0";
    static String siteName = "";
    static String locationName = "";

    Context mContext;
    private ProgressBar pbAttachment;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static ImageDetailFragment newInstance(String imageUrl, int siteId,
                                                  String LocationID, String EventID, String sName,
                                                  String lName, String mobApp) {
        siteID = siteId;
        LocID = LocationID;
        eventID = EventID;
        siteName = sName;
        locationName = lName;
        mobappID = mobApp;
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDetailFragment() {
    }

    /**
     * Populate image using a url from extras, use the convenience factory method
     * to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        mContext = getContext();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.cardImageView);
        pbAttachment = v.findViewById(R.id.pbAttachment);
        delete = (Button) v.findViewById(R.id.delete);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("mImageUrl " + mImageUrl);
                AttachmentDataSource ads = new AttachmentDataSource(mContext);
                ads.deleteImage(mImageUrl);
                File file = new File(mImageUrl);
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

                Images i = new Images(mContext, siteID, LocID, eventID, siteName, locationName, Integer.parseInt(mobappID));
                i.list = ads.getCardAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced,
                        siteID + "", eventID + "", LocID, siteName, locationName, mobappID);
                Collections.sort(i.list, new CustomComparator());

                i.imageUrls = i.getImageUrl();
                if (i.imageUrls.length == 0) {
                    Toast.makeText(getActivity(), "No Images to Display..", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

            }
        });
        delete.setVisibility(View.INVISIBLE);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (getActivity() instanceof DisplayImage) {
            mImageFetcher = ((DisplayImage) getActivity()).getImageFetcher();

            if (new File(mImageUrl).exists())
                mImageFetcher.loadImage(mImageUrl, mImageView);
            else
                loadImage(mImageView, mImageUrl);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity())) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
//				delete.setVisibility(View.VISIBLE);
//				Toast.makeText(getActivity(), "Long Click event", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void loadImage(ImageView imageView, String fileKeyEncode) {

        pbAttachment.setVisibility(View.VISIBLE);

        String baseUrl = mContext.getString(R.string.prod_base_uri)
                + SubUrls.URL_DOWNLOAD_PDF + "?file=" + fileKeyEncode;

        final String uID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);
        final DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) mContext);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("user_guid", ob.getUser_guid());
        client.addHeader("device_id", ob.getDeviceId());
        client.addHeader("user_id", uID);
        client.addHeader("ratio", "original");
        client.addHeader("Content-Type", "application/octet-stream");
        try {
            client.post(baseUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    try {
                        Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0,
                                responseBody.length);

                        Glide.with(mContext).asBitmap().load(image)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(imageView);
                        pbAttachment.setVisibility(View.GONE);
                    } catch (IllegalArgumentException arg) {
                        arg.printStackTrace();
                        pbAttachment.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.getMessage());
                    pbAttachment.setVisibility(View.GONE);
                }
            });
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }


    public class CustomComparator implements Comparator<GalleryItem> {

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
