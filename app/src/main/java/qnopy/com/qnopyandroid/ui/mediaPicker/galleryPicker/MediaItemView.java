package qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.lang.ref.WeakReference;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker.modules.ReboundModule;
import qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker.modules.ReboundModuleDelegate;


public class MediaItemView extends RelativeLayout implements ReboundModuleDelegate {

    ImageView mMediaThumb;

    private File mCurrentFile;
    private ReboundModule mReboundModule = ReboundModule.getInstance(this);
    private WeakReference<MediaItemViewListener> mWrListener;

    void setListener(MediaItemViewListener listener) {
        this.mWrListener = new WeakReference<>(listener);
    }

    public MediaItemView(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.media_item_view, this);
        mMediaThumb = view.findViewById(R.id.mMediaThumb);
    }

    public void bind(File file) {
        mCurrentFile = file;
        mReboundModule.init(mMediaThumb);
        Glide.with(getContext())
                .load(Uri.fromFile(file))
                .apply(new RequestOptions().override(350, 350)
                        .placeholder(R.drawable.placeholder_media)
                        .error(R.drawable.placeholder_error_media))
                .into(mMediaThumb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void onTouchActionUp() {
        mWrListener.get().onClickItem(mCurrentFile);
    }
}
