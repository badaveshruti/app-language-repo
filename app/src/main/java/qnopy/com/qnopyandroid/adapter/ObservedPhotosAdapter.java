package qnopy.com.qnopyandroid.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.ui.activity.CardGalleryActivity;
import qnopy.com.qnopyandroid.ui.activity.FormActivity;

public class ObservedPhotosAdapter extends RecyclerView.Adapter<ObservedPhotosAdapter.ViewHolder> {

    private ArrayList<GalleryItem> photosList;
    private FormActivity mContext;
    private int fieldParamId;

    public ObservedPhotosAdapter(ArrayList<GalleryItem> photosList, FormActivity context, int fieldParamId) {
        this.photosList = photosList;
        this.mContext = context;
        this.fieldParamId = fieldParamId;
    }

    @NonNull
    @Override
    public ObservedPhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oberved_photos,
                null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservedPhotosAdapter.ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(Uri.fromFile(new File(photosList.get(position).getFileLocation())))
                .apply(new RequestOptions().override(350, 350)
                        .placeholder(R.drawable.placeholder_media)
                        .error(R.drawable.placeholder_error_media))
                .into(holder.ivObservedPhoto);
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivObservedPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivObservedPhoto = itemView.findViewById(R.id.ivObservedPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
        }
    }

    public void openGallery() {
        Intent i = new Intent(mContext,
                CardGalleryActivity.class);
        i.putExtra("SITE_ID", mContext.getSiteID());
        i.putExtra("EVENT_ID", mContext.getEventID());
        i.putExtra("LOC_ID", mContext.getLocID());
        i.putExtra("MOBILE_APP_ID", mContext.getCurrentAppID());
        i.putExtra("SET_ID", mContext.getCurSetID());
        i.putExtra("USER_ID", mContext.getUserID());
        i.putExtra("CURRENT_FORM_NUM", mContext.getCurrentFormNum());

        i.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, fieldParamId);
        try {
            mContext.startActivity(i);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public void addPhoto(String path) {
        GalleryItem galleryItem = new GalleryItem();
        galleryItem.setFileLocation(path);
        photosList.add(galleryItem);
        notifyDataSetChanged();
    }
}
