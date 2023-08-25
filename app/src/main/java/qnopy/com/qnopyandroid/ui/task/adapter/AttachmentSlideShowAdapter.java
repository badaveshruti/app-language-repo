package qnopy.com.qnopyandroid.ui.task.adapter;

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
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.task.AttachmentSlideShowActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class AttachmentSlideShowAdapter extends
        RecyclerView.Adapter<AttachmentSlideShowAdapter.ViewHolder> {

    private ArrayList<TaskDataResponse.AttachmentList> attachmentList;
    private Context mContext;

    public AttachmentSlideShowAdapter(ArrayList<TaskDataResponse.AttachmentList> attachmentList,
                                      Context context) {
        this.attachmentList = attachmentList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public AttachmentSlideShowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_attachment_pager_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AttachmentSlideShowAdapter.ViewHolder holder,
                                 int position) {
        final TaskDataResponse.AttachmentList attachment = attachmentList.get(position);

        final String uID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);
        final DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) mContext);

/*        String path = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            path = BaseMenuActivity.getMediaStorageDirOS11Up(MEDIA_TYPE_IMAGE, mContext)
                    .getAbsolutePath();
        } else {
            path = getMediaStorageDirectory(MEDIA_TYPE_IMAGE).getAbsolutePath();
        }*/

        File imagePath = new File(attachment.getFileName());

        if (imagePath.exists()) {
            Picasso.get().load(imagePath)
                    .into(holder.ivAttachment);
            holder.pbAttachment.setVisibility(View.GONE);
        } else {

            String baseUrl = mContext.getString(R.string.prod_base_uri)
                    + mContext.getString(R.string.prod_user_task_attachment_data) + "/"
                    + attachment.getFileKey() + "/" + attachment.getFileName();

/*            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {

                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("user_guid", ob.getUser_guid())
                                    .addHeader("device_id", ob.getDeviceId())
                                    .addHeader("user_id", uID)
                                    .addHeader("file_key", attachment.getFileKey())
                                    .addHeader("ratio", "original")
                                    .addHeader("Content-Type",
                                            "application/octet-stream").build();
                            return chain.proceed(newRequest);
                        }
                    }).build();

            Picasso picasso = new Picasso.Builder(mContext)
                    .downloader(new OkHttp3Downloader(client)).listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.d("picasso error", exception.getStackTrace().toString());
                        }
                    }).build();

            picasso.load(baseUrl).resize(500, 500)
                    .centerCrop().into(holder.ivAttachment, new Callback() {
                @Override
                public void onSuccess() {
                    holder.pbAttachment.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    holder.pbAttachment.setVisibility(View.GONE);
                }
            });*/

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("user_guid", ob.getUser_guid());
            client.addHeader("device_id", ob.getDeviceId());
            client.addHeader("user_id", uID);
            client.addHeader("file_key", attachment.getFileKey());
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
        return attachmentList.size();
    }

    public void removeAttachment(int position) {
        attachmentList.remove(position);
        notifyItemRemoved(position);

        if (attachmentList.isEmpty()) {
            ((AttachmentSlideShowActivity) mContext).finish();
        }
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
