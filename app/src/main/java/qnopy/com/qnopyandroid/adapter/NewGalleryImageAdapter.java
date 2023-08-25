package qnopy.com.qnopyandroid.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;

public class NewGalleryImageAdapter extends BaseAdapter {

    private Activity context;

    private static ImageView imageView;

    private List<GalleryItem> plotsImages;

    private static ViewHolder holder;

    public NewGalleryImageAdapter(Activity context, List<GalleryItem> plotsImages) {

        this.context = context;
        this.plotsImages = plotsImages;

    }

    @Override
    public int getCount() {
        return plotsImages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        GalleryItem item = plotsImages.get(position);
        if (convertView == null) {

            holder = new ViewHolder();

            imageView = new ImageView(this.context);

            imageView.setPadding(3, 3, 3, 3);

            convertView = imageView;

            holder.imageView = imageView;

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Uri uri = Uri.fromFile(new File(item.getFileLocation()));

        Picasso.get().load(uri).placeholder(R.drawable.progress_animation)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .resize(150, 90).into(holder.imageView);
//		Bitmap thumbnail = getbitmap(item.getFileLocation());

//		holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//		holder.imageView.setLayoutParams(new Gallery.LayoutParams(150, 90));

        return imageView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

}
