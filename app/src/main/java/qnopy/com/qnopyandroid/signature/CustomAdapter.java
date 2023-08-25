package qnopy.com.qnopyandroid.signature;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.requestmodel.CustomerSign;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class CustomAdapter extends BaseAdapter {
    private static final String TAG = "CustomAdapter";
    CaptureSignature mContext;
    ArrayList<CustomerSign> arryList;
    ViewHolder holder;

    ListView mlistView;

    public CustomAdapter(Context context, ArrayList<CustomerSign> arryList) {
        this.mContext = (CaptureSignature) context;
        this.arryList = arryList;
    }

    @Override
    public int getCount() {
        return arryList.size();
    }

    @Override
    public CustomerSign getItem(int position) {
        if (arryList.size() > 0) {
            return arryList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (parent instanceof ListView) {
            mlistView = (ListView) parent;
        }

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = (LinearLayout) mInflater.inflate(
                    R.layout.customerlistview, null);
            holder.customerName = (TextView) convertView
                    .findViewById(R.id.customerName);
            holder.signature = (ImageView) convertView
                    .findViewById(R.id.customerSignature);
            holder.delete = (ImageView) convertView
                    .findViewById(R.id.ivDelete);

            holder.delete.setImageDrawable(VectorDrawableUtils.getDrawable(mContext,
                    R.drawable.delete, R.color.black_faint));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomerSign customerSign = getItem(position);
        if (customerSign != null) {

            Bitmap bitmap = Util.getSignbitmap(customerSign.getFilepath());
            holder.signature.setImageBitmap(bitmap);
            notifyDataSetChanged();

            holder.customerName.setText(customerSign.getName());

            holder.delete.setTag(position);
            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomerSign customerSign1 = getItem(position);

                    if (customerSign1 != null) {

                        String path = customerSign1.getFilepath();

                        AttachmentDataSource dataSource = new AttachmentDataSource(mContext);

                        dataSource.deleteImage(path);
                        Log.i("Deleted Attachment", "Deleted successful");

                        arryList.remove(customerSign1);

                        mContext.customerSigns = arryList;
                        mContext.setSignatureNameString();
                        notifyDataSetChanged();

                        Toast.makeText(mContext, "Signature Removed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        notifyDataSetChanged();

        return convertView;
    }

    private static class ViewHolder {
        ImageView delete;
        ImageView signature;
        TextView customerName;
    }

    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }


        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    OnClickListener deleteOnclickListner = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            arryList.remove(getItem(pos));
            notifyDataSetChanged();//CustomAdapter.this.
            if (getCount() < 1) {
                mlistView.setVisibility(View.GONE);

            }

        }
    };

    public Bitmap loadBitmapFormView(View view, String file) {

        if (view.getMeasuredHeight() <= 0 && view.getMeasuredWidth() <= 0) {
            view.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        Bitmap bitmap = null;

        try {

            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                    view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            // bitmap = resizePic(file);
//            OutputStream os = null;

//            os = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);

            Log.i(TAG, "Signature Thumbnail Size:" + bitmap.getRowBytes() * bitmap.getHeight());
            Canvas canvas = new Canvas(bitmap);
            view.layout(view.getLeft(), view.getTop(), view.getRight(),
                    view.getBottom());

//            Drawable bgDrawable =view.getBackground();
//            if (bgDrawable!=null)
//                bgDrawable.draw(canvas);
//            else
//                canvas.drawColor(Color.WHITE);
            view.draw(canvas);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getThumbnail(Bitmap bitmap) {

        byte[] imageData = null;
        Bitmap imageBitmap = null;
        try {

            final int THUMBNAIL_SIZE = 64;

            imageBitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imageBitmap;
    }

    public class customDeleteOnClickListener implements OnClickListener {

        CustomerSign itemToDelete;

        public customDeleteOnClickListener(CustomerSign item) {
            this.itemToDelete = item;
        }

        @Override
        public void onClick(View v) {
//            int pos = (int) v.getTag();
            Log.i(TAG, "Item To Delete Name:" + itemToDelete.getName());
            arryList.remove(itemToDelete);
            notifyDataSetChanged();
            if (getCount() < 1) {
                mlistView.setVisibility(View.GONE);
            }
        }
    }
}