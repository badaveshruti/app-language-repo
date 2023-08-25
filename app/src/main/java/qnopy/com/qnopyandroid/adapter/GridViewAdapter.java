package qnopy.com.qnopyandroid.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.ui.activity.NoteDialogBoxActivity;
import qnopy.com.qnopyandroid.ui.activity.NotesImagesSlideShowActivity;
import qnopy.com.qnopyandroid.util.Util;

public class GridViewAdapter extends ArrayAdapter<GridImageItem> {

    private static final String TAG = "GridViewAdapter";
    private Context context;
    private int layoutResourceId;
    private ArrayList<GridImageItem> data = new ArrayList<GridImageItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<GridImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageDelete = (ImageView) row.findViewById(R.id.image_delete);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final GridImageItem item = data.get(position);
//        holder.imageTitle.setText(item.getTitle());
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder deletealert = new AlertDialog.Builder(
                        context);
                deletealert.setTitle(context.getString(R.string.alert));
                deletealert.setMessage(context.getString(R.string.are_you_sure_to_delete_photo));
                deletealert.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (new AttachmentDataSource(context).deleteImage(item.getImage_path())) {
                            data.remove(position);
                            notifyDataSetChanged();

                            if (data.size() < 1) {
                                ((NoteDialogBoxActivity) context).setEmpty();
                                Toast.makeText(context, context.getString(R.string.all_photos_are_removed), Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(context, context.getString(R.string.photo_removed), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.unable_to_remove_photo), Toast.LENGTH_LONG).show();
                        }

                    }
                }).setNegativeButton(context.getString(R.string.no), null);

                AlertDialog dialog = deletealert.create();
                dialog.show();
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotesImagesSlideShowActivity.class);
                intent.putExtra(GlobalStrings.PATH_LIST, data);
                intent.putExtra(GlobalStrings.POSITION, position);
                context.startActivity(intent);
            }
        });

        try {

            ScreenReso application = new ScreenReso();
            application.getScreenReso(context);
            int density = (int) application.getDensity();
            int thumbHeight = 150, thumbWidth = 150;
            if (density >= 240) {
                thumbHeight = 250;
                thumbWidth = 250;
            }
            Picasso.get().setLoggingEnabled(true);
            Picasso.get()
                    .load(new File(item.getImage_path()))
                    .placeholder(R.drawable.progress_animation)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(thumbWidth, thumbHeight)
                    .into(holder.image);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getCardAttachmentDataListFromDB Error:" + e.getMessage());
            holder.image.setImageBitmap(Util.getbitmap(item.getImage_path(), 150));
        }

        return row;
    }

    static class ViewHolder {
        ImageView imageDelete;
        ImageView image;
    }
}