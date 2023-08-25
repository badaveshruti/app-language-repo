package qnopy.com.qnopyandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class NotesImagesSlideShowAdapter
        extends RecyclerView.Adapter<NotesImagesSlideShowAdapter.ViewHolder> {

    private ArrayList<GridImageItem> mPathList;
    private Context mContext;

    public NotesImagesSlideShowAdapter(ArrayList<GridImageItem> pathList, Context context) {
        this.mPathList = pathList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attachment_pager_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GridImageItem item = mPathList.get(position);
        File imagePath = new File(item.getImage_path());

        if (imagePath.exists()) {
            Picasso.get().load(imagePath)
                    .into(holder.ivAttachment);
            holder.pbAttachment.setVisibility(View.GONE);
        } else {

            //this else part is meant for location profile pictures as it doesn't have file key
            //and the imagepath fetched from item is the url to load image

            final String uID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);
            final DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) mContext);

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("user_guid", ob.getUser_guid());
            client.addHeader("device_id", ob.getDeviceId());
            client.addHeader("user_id", uID);
            client.addHeader("ratio", "original");
            client.addHeader("Content-Type", "application/octet-stream");

            try {
                client.post(item.getImage_path(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        try {
                            Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0,
                                    responseBody.length);

                            Glide.with(mContext).asBitmap().load(image)
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(holder.ivAttachment);
                            holder.pbAttachment.setVisibility(View.GONE);
                        } catch (IllegalArgumentException arg) {
                            arg.printStackTrace();
                            holder.pbAttachment.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.getMessage());
                        holder.pbAttachment.setVisibility(View.GONE);
                    }
                });
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPathList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PhotoView ivAttachment;
        private final ProgressBar pbAttachment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
            pbAttachment = itemView.findViewById(R.id.pbAttachment);
        }
    }
}
