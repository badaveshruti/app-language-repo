package qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker;

import static android.app.Activity.RESULT_OK;
import static qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity.dirPath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker.modules.LoadMoreModuleDelegate;
import qnopy.com.qnopyandroid.util.Util;

public class GalleryPickerFragment extends Fragment implements GridAdapterListener,
        LoadMoreModuleDelegate {

    RecyclerView mGalleryRecyclerView;
    ImageView mPreview;
    AppBarLayout mAppBarContainer;

    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_PNG = ".png";
    private static final int PREVIEW_SIZE = 900;
    private static final int MARGING_GRID = 2;
    private static final int RANGE = 20;

    private GridAdapter mGridAdapter;
    private ArrayList<File> mFiles;
    private boolean isLoading = false;
    private int mOffset;
    private boolean isFirstLoad = true;
    private Bitmap mBitmap;
    private File currentSelectedFile;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handlerForUI = new Handler(Looper.getMainLooper());

    public static GalleryPickerFragment newInstance() {
        return new GalleryPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initViews(view);
    }

    private void initViews(View view) {
        mGalleryRecyclerView = view.findViewById(R.id.mGalleryRecyclerView);
        mPreview = view.findViewById(R.id.mPreview);
        mAppBarContainer = view.findViewById(R.id.mAppBarContainer);

        if (isFirstLoad) {
            mGridAdapter = new GridAdapter(getContext());
        }
        mGridAdapter.setListener(this);
        mGalleryRecyclerView.setAdapter(mGridAdapter);
        mGridAdapter.notifyDataSetChanged();
        mGalleryRecyclerView.setHasFixedSize(true);
        mGalleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mGalleryRecyclerView.addItemDecoration(addItemDecoration());
        mOffset = 0;
        fetchMedia();
    }

    private RecyclerView.ItemDecoration addItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= 3) {
                    outRect.top = MARGING_GRID;
                }
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_media_picker, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (mBitmap != null) {

                String thumbsDirPath = dirPath + File.separator + GlobalStrings.THUMBNAILS_DIR + File.separator;

                //OG file saved
                String fName = "p_" + System.currentTimeMillis();
                String fileName = fName + ".jpg";
                File dirDest = new File(dirPath, fileName);
                String path = Util.saveBitmapToSDCard(mBitmap, dirDest,
                        GlobalStrings.COMPRESSION_RATE_100);
                //end

                //thumbnail creation
                String thumbFileName = fName + GlobalStrings.THUMBNAIL_EXTENSION;
                File dirThumbDest = new File(thumbsDirPath, thumbFileName);

                //extracting fileName for thumb file to avoid recycle error for resize
                if (currentSelectedFile != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        String ogFileName = currentSelectedFile.getName();

                        //as we already have a thumbnail saved for the file we will fetch name of that
                        //file to create path and if doesn't exist then create thumb

                        ogFileName = ogFileName.substring(0, ogFileName.lastIndexOf("."));
                        thumbFileName = ogFileName.split("\\.")[0] + GlobalStrings.THUMBNAIL_EXTENSION;
                        dirThumbDest = new File(thumbsDirPath, thumbFileName);
                        if (dirThumbDest.exists()) {
                            Log.i("Gallery picker", "thumbnail exist");
                            Log.i("Gallery picker", "thumbnail: " + dirThumbDest.getAbsolutePath());
                        } else {
                            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentSelectedFile.getAbsolutePath()),
                                    GlobalStrings.THUMBNAIL_HEIGHT_WIDTH, GlobalStrings.THUMBNAIL_HEIGHT_WIDTH);
                            if (thumbnail != null)
                                Util.saveBitmapToSDCard(thumbnail, dirThumbDest, GlobalStrings.COMPRESSION_RATE_100);
                        }
                    } else {
                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentSelectedFile.getAbsolutePath()),
                                GlobalStrings.THUMBNAIL_HEIGHT_WIDTH, GlobalStrings.THUMBNAIL_HEIGHT_WIDTH);
                        if (thumbnail != null)
                            Util.saveBitmapToSDCard(thumbnail, dirThumbDest, GlobalStrings.COMPRESSION_RATE_100);
                    }
                }

/*                //thumbnail file
                String thumbFileName = fName + GlobalStrings.THUMBNAIL_EXTENSION;
                File dirThumbDest = new File(thumbsPath, thumbFileName);
                Bitmap thumbBmp = Util.getResizedBitmap(mBitmap, 160, 160);
                Util.saveBitmapToSDCard(thumbBmp, dirThumbDest, GlobalStrings.COMPRESSION_RATE_100);*/

                Intent intent = new Intent();
                intent.putExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH, path);
                intent.putExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH, dirThumbDest.getAbsolutePath());

                //15/10/22 Note: for now we are putting same path as original file as our camera lib
                //currently not processing image greater than 900px so we cant enlarge the image using resize code
                //so until we receive good quality(of course we need to work on quality thing as
                //this is workaround only) image to process image further we will follow this.
                intent.putExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH, path);

                try {
                    if (((MediaPickerActivity) getActivity()).isFromFormMaster)
                        intent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, ((MediaPickerActivity) getActivity()).fieldParamId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchMedia() {
        mFiles = new ArrayList<>();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            File dirImage = BaseMenuActivity.getMediaStorageDirOS11Up(BaseMenuActivity.MEDIA_TYPE_IMAGE, getActivity());
            parseDir(dirImage);
            File dirDrawing = BaseMenuActivity.getMediaStorageDirOS11Up(BaseMenuActivity.MEDIA_TYPE_DRAWING, getActivity());
            parseDir(dirDrawing);
        } else {
            File dirDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            parseDir(dirDownloads);
            File dirDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            parseDir(dirDcim);
            File dirPictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            parseDir(dirPictures);
            File dirDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            parseDir(dirDocuments);
        }

        if (mFiles.size() > 0) {
            Collections.sort(mFiles, (o1, o2) -> String.valueOf(o2.lastModified()).compareTo(String.valueOf(o1.lastModified())));
            onClickMediaItem(mFiles.get(0));
            mGridAdapter.setItems(mFiles);
        }
        isFirstLoad = false;
    }

    private List<File> getRangePets() {
        if (mOffset < mFiles.size()) {
            if ((mOffset + RANGE) < mFiles.size()) {
                return mFiles.subList(mOffset, mOffset + RANGE);
            } else if ((mOffset + RANGE) >= mFiles.size()) {
                return mFiles.subList(mOffset, mFiles.size());
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    private void parseDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            parseFileList(files);
        }
    }

    private void parseFileList(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                if (!file.getName().toLowerCase().startsWith(".")
                        && !file.getName().equalsIgnoreCase("thumbnails")) {
                    parseDir(file);
                }
            } else {
                if (file.getName().toLowerCase().endsWith(EXTENSION_JPG)
                        || file.getName().toLowerCase().endsWith(EXTENSION_JPEG)
                        || file.getName().toLowerCase().endsWith(EXTENSION_PNG)) {
                    mFiles.add(file);
                }
            }
        }
    }

    private void loadNext() {
        if (!isLoading) {
            isLoading = true;
            mOffset += RANGE;
            List<File> files = new ArrayList<>(getRangePets());
            if (files.size() > 0) {
                mGridAdapter.addItems(files, mGridAdapter.getItemCount());
            }
            isLoading = false;
        }
    }

    private void displayPreview(File file) {

        Glide.with(getActivity())
                .load(file)
                .apply(new RequestOptions()
                        .override(PREVIEW_SIZE, PREVIEW_SIZE).centerCrop())
                .into(new Target<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mBitmap = drawableToBitmap(resource);
                        mPreview.setImageBitmap(mBitmap);
/*                        if (getActivity() instanceof MediaPickerActivity)
                            ((MediaPickerActivity) getActivity()).mSession.setFileToUpload(file, mBitmap);*/
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void setRequest(@Nullable Request request) {

                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onDestroy() {

                    }
                });
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onPause() {
        super.onPause();
        Picasso.get().cancelRequest(mPreview);
    }

    @Override
    public void onClickMediaItem(File file) {
        currentSelectedFile = file;
        displayPreview(file);
        mAppBarContainer.setExpanded(true, true);
    }

    @Override
    public void shouldLoadMore() {
        loadNext();
    }
}
