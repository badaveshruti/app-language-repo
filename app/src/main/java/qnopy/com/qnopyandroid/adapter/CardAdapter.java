package qnopy.com.qnopyandroid.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.fetchdraw.FetchDrawScreen;
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls;
import qnopy.com.qnopyandroid.photogallery.DisplayImage;
import qnopy.com.qnopyandroid.ui.activity.CardGalleryActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 13-Apr-16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private static final String TAG = "CardAdapter";
    List<GalleryItem> items;
    Context mContext;
    protected String notes;
    protected String mobAppName;
    Map<String, String> map = new HashMap<String, String>();
    String updatetext = null;
    String finalnote = null;
    String username, password, userID;
    String fileloc = null;

    public CardAdapter(Context context, ArrayList<GalleryItem> list) {
        super();
        this.mContext = context;
        items = new ArrayList<GalleryItem>();
        items = list;
        // this.mobAppName=appName;
    }

    public CardAdapter(Context context, ArrayList<GalleryItem> list, String filepath) {
        super();
        this.mContext = context;
        items = new ArrayList<GalleryItem>();
        items = list;
        fileloc = filepath;
        // this.mobAppName=appName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_gallery_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {

        GalleryItem item = items.get(position);

        final String filePath = item.getFileLocation();
        File fFile = null, sFile = null;

        if (fileloc != null) {
            fFile = new File(fileloc);
        }
        if (filePath != null) {
            sFile = new File(filePath);
        }

        if (fFile != null && sFile != null) {

            if (fFile.equals(sFile)) {

                // Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                holder.imageView.requestFocus(holder.getAbsoluteAdapterPosition());
                //  holder.imageView.setAnimation(animation);
            }
        }

        try {
            if (filePath != null) {
                Picasso.get()
                        .load(new File(filePath))
//                    .placeholder(R.drawable.progress_animation)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .resize(110, 110)
                        .into(holder.imageView);
                enableUpdateOptions(View.VISIBLE, holder, item);
            } else if (item.getImageLoaded() != null) {
                try {
                    Glide.with(mContext).asBitmap()
                            .load(items.get(holder.getAbsoluteAdapterPosition()).getImageLoaded())
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(holder.imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (item.getFileKey() != null) {
                        loadImage(holder, item, position);
                    }
                }
                enableUpdateOptions(View.GONE, holder, item);
            } else if (item.getFileKey() != null) {
                loadImage(holder, item, position);
                enableUpdateOptions(View.GONE, holder, item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getCardAttachmentDataListFromDB Error:" + e.getMessage());
        }

        holder.ViewLocation.setText(item.getLocationName());
        holder.ViewSite.setText(item.getSiteName());
        String az = item.getAzimuth();
        if (az == null || az.equalsIgnoreCase("N/A")) {
            az = "N/A";
            //holder.ViewAzimuth.setText(az);
        }
        //else {
        holder.ViewAzimuth.setText(az);
        // }

        MobileAppDataSource ob = new MobileAppDataSource(mContext);
        mobAppName = ob.getMobileAppDisplayNameByMobID(item.getMobAppID(), item.getSiteID() + "");
        holder.ViewForm.setText(mobAppName);
        holder.ViewSet.setText("" + item.getSetID());
        Long millis = Long.parseLong(item.getTxtDate());
        String photoDate = null;
        if (millis != null) {
            photoDate = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(millis);
        }

        holder.ViewDate.setText(photoDate);

        String s = item.getTxtNote();
        if (s != null && s.contains("|")) {
            String s2 = splitnotes(s);
            holder.tvNote.setText(s2);
        } else {
            holder.tvNote.setText(s);
        }

        holder.ViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder deletealert = new AlertDialog.Builder(
                        mContext);
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                deletealert.setTitle(mContext.getString(R.string.alert));
                deletealert.setMessage(mContext.getString(R.string.are_you_sure_to_delete_photo)
                        + fileName + " ?");
                deletealert.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AttachmentDataSource ob = new AttachmentDataSource(mContext);

                        if (ob.deleteImage(item.getFileLocation())) {
                            deleteFileFromStorage(filePath);
                            items.remove(holder.getAbsoluteAdapterPosition());
                            notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                            if (items.size() < 1) {
                                Toast.makeText(mContext, mContext.getString(R.string.all_photos_are_removed),
                                        Toast.LENGTH_LONG).show();
                                ((CardGalleryActivity) mContext).finish();
                            }
                        }
                    }
                }).setNegativeButton(mContext.getString(R.string.no), null);

                AlertDialog dialog = deletealert.create();
                dialog.show();
            }
        });

        holder.ViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AttachmentDataSource attach = new AttachmentDataSource(mContext);
                String note = attach.readNotes(item.getSiteID(),
                        item.getEventID(), filePath);
                addNotes(filePath, note, holder.tvNote, item);
            }
        });

        holder.ViewDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
                password = Util.getSharedPreferencesProperty(mContext, GlobalStrings.PASSWORD);
                userID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);

                Intent intent = new Intent(mContext, FetchDrawScreen.class);
                intent.putExtra("USER_NAME", username);
                intent.putExtra("PASS", password);
                intent.putExtra("EVENT_ID", Integer.parseInt(item.getEventID()));
                intent.putExtra("LOC_ID", item.getLocID());
                intent.putExtra("SITE_ID", item.getSiteID());
                intent.putExtra("USER_ID", userID);
                intent.putExtra("MOBILE_APP_ID", item.getMobAppID());
                intent.putExtra("SET_ID", item.getSetID());
                intent.putExtra("FILE_NAME_PREFIX", item.getFileLocation());
                intent.putExtra("SAVE_DIRECTORY", item.getFileLocation());
                intent.putExtra("DRAW_PHOTO", true);
                mContext.startActivity(intent);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,
                        DisplayImage.class);

                GalleryItem item = items.get(holder.getAbsoluteAdapterPosition());

                i.putExtra(DisplayImage.EXTRA_IMAGE, holder.getAbsoluteAdapterPosition());
                i.putExtra("SiteID", item.getSiteID());
                i.putExtra("EVENT_ID", item.getEventID());
                i.putExtra("LOC_ID", item.getLocID());
                i.putExtra("MOB_APP_ID", item.getMobAppID());
                i.putExtra("SITE_NAME", item.getSiteName());
                i.putExtra("LOCATION_NAME", item.getLocationName());

                mContext.startActivity(i);
            }
        });

        if (item.getFileKey() == null) {
            if (item.getAttachmentType().equalsIgnoreCase("L")) {
                holder.ivSetAsProfile.setVisibility(View.GONE);
                holder.ivRemoveAsProfile.setVisibility(View.VISIBLE);
            } else {
                holder.ivSetAsProfile.setVisibility(View.VISIBLE);
                holder.ivRemoveAsProfile.setVisibility(View.GONE);
            }
        }

        holder.ivSetAsProfile.setOnClickListener(v -> {
            showSetAsProfilePicAlert(mContext.getString(R.string.set_this_attachment_as_profile_picture),
                    true, item, holder, mContext.getString(R.string.set_as_profile_picture));
        });

        holder.ivRemoveAsProfile.setOnClickListener(v -> {
            showSetAsProfilePicAlert(mContext.getString(R.string.remove_attachment_as_profile_picture),
                    false, item, holder, mContext.getString(R.string.remove_as_profile_picture));
        });
    }

    private void enableUpdateOptions(int isEnable, ViewHolder holder, GalleryItem item) {
        holder.ivSetAsProfile.setVisibility(isEnable);
        holder.ViewUpdate.setVisibility(isEnable);
        holder.ViewDraw.setVisibility(isEnable);
        holder.ViewDelete.setVisibility(isEnable);

        if (isEnable == View.GONE)
            if (item.getTxtNote() != null && !item.getTxtNote().isEmpty()) {
                holder.tvNote.setVisibility(View.VISIBLE);
            } else {
                holder.tvNote.setVisibility(View.GONE);
            }
    }

    private void loadImage(ViewHolder holder, GalleryItem attachment, int position) {

        String baseUrl = mContext.getString(R.string.prod_base_uri)
                + SubUrls.URL_DOWNLOAD_PDF + "?file=";

        if (attachment.getFileKeyThumbEncode() != null)
            baseUrl = baseUrl + attachment.getFileKeyThumbEncode();
        else if (attachment.getFileKeyEncode() != null)
            baseUrl = baseUrl + attachment.getFileKeyEncode();

        final String uID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);
        final DeviceInfoModel ob = DeviceInfo.getDeviceInfo(mContext);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("user_guid", ob.getUser_guid());
        client.addHeader("device_id", ob.getDeviceId());
        client.addHeader("user_id", uID);

        try {
            client.post(baseUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    try {

                        Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0,
                                responseBody.length);

                        items.get(position).setImageLoaded(image);

                        Glide.with(mContext).asBitmap().load(image)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(holder.imageView);
//                        holder.pbAttachment.setVisibility(View.GONE);
                    } catch (Exception arg) {
                        arg.printStackTrace();
//                        holder.pbAttachment.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.getMessage());
//                    holder.pbAttachment.setVisibility(View.GONE);
                }
            });
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }

    private void showSetAsProfilePicAlert(String message, boolean isSetProfile, GalleryItem item, ViewHolder holder, String title) {
        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(mContext);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(mContext.getString(R.string.ok), (dialog, which) -> {
            dialog.dismiss();
            if (isSetProfile) {
                attachmentDataSource.updateAttachmentType(item, "L");
                holder.ivSetAsProfile.setVisibility(View.GONE);
                holder.ivRemoveAsProfile.setVisibility(View.VISIBLE);
            } else {
                attachmentDataSource.updateAttachmentType(item, "P");
                holder.ivSetAsProfile.setVisibility(View.VISIBLE);
                holder.ivRemoveAsProfile.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteFileFromStorage(String filePath) {
        File file = new File(filePath);
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
        MediaScannerConnection.scanFile(mContext,
                new String[]{filePath}, null, null);
    }

    private String splitnotes(String s) {
        String notetext = null;
        if (s != null && s.contains("|")) {
            String[] value_split = s.split("\\|");
            for (String s1 : value_split) {
                if (s1.contains(":")) {
                    String[] t = s1.split(":");
                    if (t.length > 1) {
                        map.put(t[0], t[1].trim());
                    } else {
                        map.put(t[0], null);
                    }
                }
            }

            for (String s2 : map.keySet()) {
                if (s2.equalsIgnoreCase("Note")) {
                    notetext = map.get(s2);
                }
            }
        } else {
            notetext = s;
        }
        return notetext;
    }


    private void addNotes(final String path, final String note,
                          final TextView tvNote, final GalleryItem item) {

        final EditText notetxt;
        Button postbtn;
        final TextView textcounter, errortxt;
        notes = note;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                mContext);
        dialogBuilder.setTitle(mContext.getString(R.string.update_note));
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.card_update_note_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        notetxt = dialogView.findViewById(R.id.card_notetxt);
        postbtn = dialogView.findViewById(R.id.post_note_btn);
        textcounter = dialogView.findViewById(R.id.textViewCounter2);
        errortxt = dialogView.findViewById(R.id.textViewError);

        final String notestext = splitnotes(note);
        notetxt.setText(notestext);
        //13-Jun-17 Set Counter

        if (notestext != null) {
            int length = notestext.length();
            notetxt.setSelection(Math.min(length, 2000));
            int count = 2000 - notestext.length();
            Log.i(TAG, "Total Character entered:" + notestext.length() + " result:" + count);
//                textcounter.setText("" +res);

            if (count <= 0) {
                textcounter.setText("0");
                errortxt.setVisibility(View.VISIBLE);
                errortxt.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errortxt.setVisibility(View.GONE);
                    }
                }, 5000);
            } else {
                textcounter.setText(String.valueOf(count));
                errortxt.setVisibility(View.GONE);
            }
        }

        notetxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int res = 2000 - s.length();
                Log.i(TAG, "Total Character entered:" + s.length() + " result:" + res);

                if (res <= 0) {
                    textcounter.setText("0");
                    errortxt.setVisibility(View.VISIBLE);
                    errortxt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            errortxt.setVisibility(View.GONE);
                        }
                    }, 5000);
                } else {
                    textcounter.setText(String.valueOf(res));
                    errortxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        editalert.setView(notesInput);

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                notes = note;
                //  notes = notesInput.getText().toString();
                updatetext = notetxt.getText().toString();

                if (updatetext.contains("|") || (updatetext.contains(":"))) {
                    if (updatetext.contains("|")) {
                        updatetext = updatetext.replace("|", ",");
                    }
                    if (updatetext.contains(":")) {
                        updatetext = updatetext.replace(":", "-");
                    }
                }

                String splitNotes = splitnotes(note);

                if (splitNotes != null) {
                    if (note.contains(splitNotes)) {
                        finalnote = note.replace(splitNotes, updatetext);
                        // finalnote=note;
                        notes = finalnote;
                    }
                } else {
                    notes = notetxt.getText().toString();
                }

                AttachmentDataSource attach = new AttachmentDataSource(mContext);
                String fparamID = item.getFieldParamID(); //attach.getFieldParameterID(item.getSiteID(), item.getEventID(), item.getLocID(), item.getMobAppID(), path);

                if (fparamID != null && !fparamID.isEmpty()) {
                    //19-Jan-16 Update note in d_FieldData Table
                    MetaDataSource metaDataSource = new MetaDataSource(mContext);
                    String inputType = metaDataSource.getInputType(fparamID);
                    boolean isPhotosType = inputType.equals("PHOTOS");

                    if (!isPhotosType) {
                        new FieldDataSource(mContext).updateNotesForLabel(item.getEventID(),
                                Integer.parseInt(fparamID), item.getSetID(),
                                item.getLocID(), notes, item.getSiteID(), item.getMobAppID());

                        int ret = attach.updateMultiNotes(item.getSiteID(), item.getEventID(),
                                notes, fparamID, item.getLocID(), item.getMobAppID() + "",
                                item.getSetID() + "", "");
                        Log.i(TAG, "Note return value = " + ret);
                    } else {
                        int ret = attach.updateMultiNotes(item.getSiteID(), item.getEventID(),
                                notes, fparamID, item.getLocID(), item.getMobAppID() + "",
                                item.getSetID() + "", path);
                        Log.i(TAG, "Note return value = " + ret);
                    }

/*                    for (GalleryItem updatableItem : items) {
                        if (updatableItem.getFieldParamID() != null && !updatableItem.getFieldParamID().isEmpty()) {
                            if (updatableItem.getLocID().equals(item.getLocID()) &&
                                    updatableItem.getMobAppID() == item.getMobAppID()
                                    && updatableItem.getSetID() == item.getSetID()
                                    && updatableItem.getFieldParamID().equalsIgnoreCase(fparamID)) {
                                updatableItem.setTxtNote(notes);
                            }
                        }
                    }*/
                } else {
                    int ret = attach.updateNotes(item.getSiteID(), item.getEventID(),
                            path, notes);
                    Log.i(TAG, "Note return value = " + ret);
                }

                item.setTxtNote(notes);
//                notifyDataSetChanged();

                if (notes != null && notes.contains("|")) {
                    String showText = splitnotes(notes);
                    tvNote.setText(showText);
                } else {
                    tvNote.setText(notes);
                }
            }
        });

        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageButton ViewDelete, ViewUpdate, ViewDraw, ivSetAsProfile, ivRemoveAsProfile;

        public TextView ViewLocation, ViewSite, ViewDate, tvNote, ViewForm, ViewSet, ViewAzimuth;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.cardImageView);
            ViewLocation = itemView.findViewById(R.id.textViewLocation);
            ViewSite = itemView.findViewById(R.id.textViewSite);
            ViewDate = itemView.findViewById(R.id.textViewDate);
            tvNote = itemView.findViewById(R.id.textViewNote);
            ViewForm = itemView.findViewById(R.id.textViewMobileApp);
            ViewSet = itemView.findViewById(R.id.textViewSet);
            ViewAzimuth = itemView.findViewById(R.id.azimuthtxt);
            ivRemoveAsProfile = itemView.findViewById(R.id.ivRemoveAsProfile);

            ivSetAsProfile = itemView.findViewById(R.id.ivSetAsProfile);
            ViewUpdate = itemView.findViewById(R.id.cardUpdateButton);
            ViewDraw = itemView.findViewById(R.id.drawButton);
            ViewDelete = itemView.findViewById(R.id.cardDeleteButton);

            userID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID);

            AppPreferenceDataSource ds = new AppPreferenceDataSource(mContext);
            //KEY_DRAW_APP
            if (ds.isFeatureAvailable(GlobalStrings.KEY_DRAW_APP, Integer.parseInt(userID))) {
                ViewDraw.setVisibility(View.VISIBLE);
            } else {
                ViewDraw.setVisibility(View.GONE);
            }
        }
    }

    public Bitmap getbitmap(String path) {
        Bitmap imgthumBitmap = null;
        try {
            final int THUMBNAIL_SIZE = 110;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearroutstream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgthumBitmap;
    }
}