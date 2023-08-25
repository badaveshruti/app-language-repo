package qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker;

import android.content.Context;
import android.view.ViewGroup;

import java.io.File;
import java.lang.ref.WeakReference;

import qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker.adapters.RecyclerViewAdapterBase;
import qnopy.com.qnopyandroid.ui.mediaPicker.galleryPicker.adapters.ViewWrapper;

class GridAdapter extends RecyclerViewAdapterBase<File, MediaItemView>
        implements MediaItemViewListener {

    private final Context context;

    GridAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    private WeakReference<GridAdapterListener> mWrListener;

    void setListener(GridAdapterListener listener) {
        this.mWrListener = new WeakReference<>(listener);
    }

    @Override
    protected MediaItemView onCreateItemView(ViewGroup parent, int viewType) {
        return new MediaItemView(context);
    }

    @Override
    public void onBindViewHolder(ViewWrapper<MediaItemView> viewHolder, final int position) {
        MediaItemView itemView = viewHolder.getView();
        itemView.setListener(this);
        itemView.bind(mItems.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onClickItem(File file) {
        mWrListener.get().onClickMediaItem(file);
    }
}