package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures;

/* Created on 10 September, 2020 by PatelSanket */
@Singleton
public class LocationProfilePictureDataSource {
    final String KEY_LOCATION_ID = "locationId";
    final String KEY_ATTACHMENT_ID = "attachmentId";
    final String KEY_ATTACHMENT_URL = "attachmentURL";
    final String KEY_THUMBNAIL_URL = "thumbnailURL";
    final String KEY_CREATION_DATE = "creationDate";
    final String KEY_MODIFIED_DATE = "modifiedDate";

    private Context mContext;
    private SQLiteDatabase database;
    private static final String TAG = "AttachmentDS";

    @Inject
    public LocationProfilePictureDataSource(Context context) {
        Log.i(TAG, "LocationProfilePictureDataSource() IN time:" + System.currentTimeMillis());
        mContext = context;

        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
        Log.i(TAG, "LocationProfilePictureDataSource() OUT time:" + System.currentTimeMillis());
    }

    public int insertLocProfilePictures(List<LocationProfilePictures> profilePicsList) {

        int ret = 0;
        try {
            database.beginTransaction();
            for (LocationProfilePictures picture : profilePicsList) {
                ContentValues values = new ContentValues();
                if (picture != null) {
                    values.put(KEY_ATTACHMENT_ID, picture.getAttachmentId());
                    values.put(KEY_ATTACHMENT_URL, picture.getAttachmentURL());
                    values.put(KEY_THUMBNAIL_URL, picture.getThumbnailURL());
                    values.put(KEY_CREATION_DATE, picture.getCreationDate());
                    values.put(KEY_MODIFIED_DATE, picture.getModificationDate());
                    values.put(KEY_MODIFIED_DATE, picture.getModificationDate());
                    values.put(SiteDataSource.KEY_Status, picture.getStatus());

                    try {
                        if (picture.isInsert()
                                || MetaDataSource.isTableEmpty(DbAccess.TABLE_LOCATION_PROFILE_PICTURES,
                                database)) {
                            values.put(KEY_LOCATION_ID, picture.getLocationId());
                            ret = (int) database.insert(DbAccess.TABLE_LOCATION_PROFILE_PICTURES,
                                    null, values);
                        } else {
                            String whereClause = "LocationID = ?";
                            String[] whereArgs = new String[]{picture.getLocationId() + ""};
                            ret = database.update(DbAccess.TABLE_LOCATION_PROFILE_PICTURES, values,
                                    whereClause, whereArgs);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in insertLocProfPics");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public boolean isAttachmentExists(String attachmentUrl) {
        int count = 0;
        String query = "select count(*) from " + DbAccess.TABLE_LOCATION_PROFILE_PICTURES +
                " where attachmentURL LIKE ?";

        String[] whereArgs = {attachmentUrl};

        Log.i(TAG, "isProfileAttachmentExists() Query:" + query);
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count > 0;
    }

    public boolean deleteAttachment(String fileLocation) {
        String query = "delete from " + DbAccess.TABLE_LOCATION_PROFILE_PICTURES
                + " where attachmentURL LIKE ?";

        String[] whereArgs = {fileLocation};

        try {
            database.execSQL(query, whereArgs);
        } catch (Exception e) {
            System.out.println("deleteProfileAttachment " + e.getLocalizedMessage());
        }
        return true;
    }

    public ArrayList<LocationProfilePictures> getAllProfilePictures(String locationID) {
        ArrayList<LocationProfilePictures> filePathList = new ArrayList<>();
        Cursor cursor = null;
        String query = "select locationId, attachmentId, attachmentURL, thumbnailURL, " +
                "creationDate, modifiedDate from " + DbAccess.TABLE_LOCATION_PROFILE_PICTURES
                + " where locationId = " + locationID;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    LocationProfilePictures pictures = new LocationProfilePictures();
                    pictures.setLocationId(cursor.getInt(0));
                    pictures.setAttachmentId(cursor.getInt(1));

                    String attachmentUrl = cursor.getString(2);
                    if (attachmentUrl != null && !attachmentUrl.contains("emulated"))
                        attachmentUrl = attachmentUrl.replace("/", "");

                    pictures.setAttachmentURL(attachmentUrl);

                    String thumbUrl = cursor.getString(3);
                    if (thumbUrl != null)
                        thumbUrl = thumbUrl.replace("/", "");
                    pictures.setThumbnailURL(thumbUrl);

                    pictures.setCreationDate(cursor.getLong(4));
                    pictures.setModificationDate(cursor.getLong(5));
                    filePathList.add(pictures);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return filePathList;
    }
}
