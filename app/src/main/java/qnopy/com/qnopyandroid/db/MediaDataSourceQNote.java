package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.clientmodel.ConstructionMediaDataModel;
import qnopy.com.qnopyandroid.clientmodel.ConstructionModifiedMediaIds;


public class MediaDataSourceQNote {

    final String KEY_PostID = "PostID";
    final String KEY_MediaID = "MediaID";
    final String KEY_FileName = "FileName";
    final String KEY_FileKey = "FileKey";
    final String KEY_DisplayFlag = "DisplayFlag";
    final String KEY_CreatedBy = "CreatedBy";
    final String KEY_ModifiedBy = "ModifiedBy";
    final String KEY_ServerCreationDate = "ServerCreationDate";
    final String KEY_ServerModificationDate = "ServerModificationDate";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModificationDate = "ModificationDate";
    final String KEY_Latitude = "Latitude";
    final String KEY_Longitude = "Longitude";
    final String KEY_Caption = "Caption";
    final String KEY_sMediaID = "sMediaId";
    final String KEY_SiteID = "SiteId";
    final String KEY_ClientMediaId = "ClientMediaId";
    final String KEY_AttachmentType = "attachmentType";
    final String KEY_MediaUploadStatus = "MediaUploadStatus";
    final String KEY_File = "File";
    final String KEY_DataSyncFlag = "DataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public MediaDataSourceQNote(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }


    public int storeMediaData(ArrayList<ConstructionMediaDataModel> mArrayListMediaData) {

        int ret = 0;

        if (mArrayListMediaData == null) {
            return -1;
        }

        database.beginTransaction();
        try {
            for (ConstructionMediaDataModel s : mArrayListMediaData) {
                ContentValues values = new ContentValues();
                values.put(KEY_PostID, s.getPostId());
                values.put(KEY_MediaID, s.getMediaId());
                values.put(KEY_FileName, s.getFileName());
                values.put(KEY_FileKey, s.getFileKey());
                values.put(KEY_DisplayFlag, s.getDisplayFlag());
                values.put(KEY_CreatedBy, s.getCreatedBy());
                values.put(KEY_ModifiedBy, s.getModifiedBy());
                values.put(KEY_ServerCreationDate, s.getServerCreationDate());
                values.put(KEY_ServerModificationDate, s.getModificationDate());
                values.put(KEY_CreationDate, s.getCreationDate());
                values.put(KEY_ModificationDate, s.getModificationDate());
                values.put(KEY_Latitude, s.getLatitude());
                values.put(KEY_Longitude, s.getLongitude());
                values.put(KEY_Caption, s.getCaption());
                values.put(KEY_sMediaID, s.getsMediaId());
                values.put(KEY_SiteID, s.getSiteId());
                values.put(KEY_ClientMediaId, s.getClientMediaId());
                values.put(KEY_AttachmentType, s.getAttachmentType());
                values.put(KEY_MediaUploadStatus, s.getMediaUploadStatus());

                ret = (int) database.insert(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("storeSimpleNoteDataDB", "storeMediaData: "+e.getMessage());
        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public ArrayList<ConstructionMediaDataModel> getMediaData(Long postId) {
        ArrayList<ConstructionMediaDataModel> arrayListMediaData = new ArrayList<>();

        String postID = String.valueOf(postId);

        String query = "select PostID, MediaID, FileName, FileKey, DisplayFlag, CreatedBy, ModifiedBy, ServerCreationDate, ServerModificationDate, " +
                "CreationDate, ModificationDate, Latitude, Longitude, Caption, sMediaId, SiteId, ClientMediaId, attachmentType, MediaUploadStatus, File from c_MediaData where PostID = ?";
        String[] whereArgs = null;

        whereArgs = new String[]{postID};

        Cursor cursor = null;
        Log.i("postDataGetQuery", "Get Post Date query:" + query);

        cursor = database.rawQuery(query, whereArgs);

        try {

            if(cursor != null && cursor.moveToFirst()){

                do {
                    ConstructionMediaDataModel constructionMediaDataModel = new ConstructionMediaDataModel();

                    constructionMediaDataModel.setPostId(cursor.getInt(0));
                    constructionMediaDataModel.setMediaId(cursor.getInt(1));
                    constructionMediaDataModel.setFileName(cursor.getString(2));
                    constructionMediaDataModel.setFileKey(cursor.getString(3));
                    constructionMediaDataModel.setDisplayFlag(cursor.getInt(4));
                    constructionMediaDataModel.setCreatedBy(cursor.getInt(5));
                    constructionMediaDataModel.setModifiedBy(cursor.getInt(6));
                    constructionMediaDataModel.setServerCreationDate(cursor.getLong(7));
                    constructionMediaDataModel.setServerModificationDate(cursor.getLong(8));
                    constructionMediaDataModel.setCreationDate(cursor.getLong(9));
                    constructionMediaDataModel.setModificationDate(cursor.getLong(10));
                    constructionMediaDataModel.setLatitude(cursor.getDouble(11));
                    constructionMediaDataModel.setLongitude(cursor.getDouble(12));
                    constructionMediaDataModel.setCaption(cursor.getString(13));
                    constructionMediaDataModel.setsMediaId(cursor.getInt(14));
                    constructionMediaDataModel.setSiteId(cursor.getInt(15));
                    constructionMediaDataModel.setClientMediaId(cursor.getString(16));
                    constructionMediaDataModel.setAttachmentType(cursor.getString(17));
                    constructionMediaDataModel.setMediaUploadStatus(cursor.getString(18));
                    constructionMediaDataModel.setFile(cursor.getString(19));

                    arrayListMediaData.add(constructionMediaDataModel);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return arrayListMediaData;
    }

    public boolean checkIfPostIdInMediaExists(long mCurrentDateTimeMillisecNegative) {
        String query = "select count(PostID) from c_MediaData where PostID = ?";
        String[] whereArgs = new String[]{"" + mCurrentDateTimeMillisecNegative};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("PostIdExistMedia", "CheckIfPostIdExist() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();

        }

        Log.i("PostIdExist", "CheckIfPostIdExist() result:" + count);

        if (count > 0){
            return true;
        }
        return false;
    }

    public int insertNewMedia(long currentDateTimeMillisecNegative, int randomMediaId, String fileName, String fileKey,
                              String displayFlag, String createdBy, String modifiedBy, String serverCreationDate,
                              String serverModificationDate, long creationDate, String modificationDate, Double latitude,
                              Double longitude, String caption, String sMediaId, String siteId, String clientMediaId,
                              String attachmentType, String mediaUploadStatus, String file) {
        int ret = 0;

        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PostID, currentDateTimeMillisecNegative);
            values.put(KEY_MediaID, randomMediaId);
            values.put(KEY_FileName, fileName);
            values.put(KEY_FileKey, fileKey);
            values.put(KEY_DisplayFlag, displayFlag);
            values.put(KEY_CreatedBy, createdBy);
            values.put(KEY_ModifiedBy, modifiedBy);
            values.put(KEY_ServerCreationDate, serverCreationDate);
            values.put(KEY_ServerModificationDate, serverModificationDate);
            values.put(KEY_CreationDate, creationDate);
            values.put(KEY_ModificationDate, modificationDate);
            values.put(KEY_Latitude, latitude);
            values.put(KEY_Longitude, longitude);
            values.put(KEY_Caption, caption);
            values.put(KEY_sMediaID, sMediaId);
            values.put(KEY_SiteID, siteId);
            values.put(KEY_ClientMediaId, clientMediaId);
            values.put(KEY_AttachmentType, attachmentType);
            values.put(KEY_MediaUploadStatus, mediaUploadStatus);
            values.put(KEY_File, file);

            ret = (int) database.insert(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, null, values);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public void UpdateMediaDataSyncFlagToSave(long mCurrentDateTimeMillisecNegative) {
        ContentValues values = new ContentValues();
        values.put(KEY_DataSyncFlag, "0");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + mCurrentDateTimeMillisecNegative};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, values, whereClause, whereArgs);
            Log.i("MediaDataSyncFlagSync", " Media data sync flag updated to 0" );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MediaDataSyncFlagSync", "Media data sync flag Error:" + e.getMessage());

        }
    }

    public int getMediaZeroDataSyncFlag() {

        int count = 0;
        String query = "select count(DataSyncFlag) from c_MediaData where DataSyncFlag = 0";
        Cursor cursor = null;

        cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return count;
    }

    public ArrayList<ConstructionMediaDataModel> getNewMediaDataFromDB() {
        ArrayList<ConstructionMediaDataModel> arrayListMediaData = new ArrayList<>();

        String query = "select PostID, MediaID, FileName, FileKey, DisplayFlag, CreatedBy, ModifiedBy, ServerCreationDate, ServerModificationDate, " +
                "CreationDate, ModificationDate, Latitude, Longitude, Caption, sMediaId, SiteId, ClientMediaId, attachmentType, MediaUploadStatus, File" +
                " from c_MediaData where DataSyncFlag = 0";

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()){
            do {
                ConstructionMediaDataModel constructionMediaDataModel = new ConstructionMediaDataModel();

                constructionMediaDataModel.setPostId(cursor.getInt(0));
                constructionMediaDataModel.setMediaId(cursor.getInt(1));
                constructionMediaDataModel.setFileName(cursor.getString(2));
                constructionMediaDataModel.setFileKey(cursor.getString(3));
                constructionMediaDataModel.setDisplayFlag(cursor.getInt(4));
                constructionMediaDataModel.setCreatedBy(cursor.getInt(5));
                constructionMediaDataModel.setModifiedBy(cursor.getInt(6));
                constructionMediaDataModel.setServerCreationDate(cursor.getLong(7));
                constructionMediaDataModel.setServerModificationDate(cursor.getLong(8));
                constructionMediaDataModel.setCreationDate(cursor.getLong(9));
                constructionMediaDataModel.setModificationDate(cursor.getLong(10));
                constructionMediaDataModel.setLatitude(cursor.getDouble(11));
                constructionMediaDataModel.setLongitude(cursor.getDouble(12));
                constructionMediaDataModel.setCaption(cursor.getString(13));
                constructionMediaDataModel.setsMediaId(cursor.getInt(14));
                constructionMediaDataModel.setSiteId(cursor.getInt(15));
                constructionMediaDataModel.setClientMediaId(cursor.getString(16));
                constructionMediaDataModel.setAttachmentType(cursor.getString(17));
                constructionMediaDataModel.setMediaUploadStatus(cursor.getString(18));


                arrayListMediaData.add(constructionMediaDataModel);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return arrayListMediaData;
    }

    public void updatePostIDMediaId(String serverGeneratedMediaId, String serverGeneratedPostId, long mClientGeneratedPostId) {

        ContentValues values = new ContentValues();
        values.put(KEY_PostID, serverGeneratedPostId);
        values.put(KEY_MediaID, serverGeneratedMediaId);
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + mClientGeneratedPostId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, values, whereClause, whereArgs);
            Log.i("PostIdMediaIdforMedia", "PostID For Media to - "+serverGeneratedPostId+ " ClientGeneratedPostid:- " +mClientGeneratedPostId + " server generated media id: "+serverGeneratedMediaId);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PostIdMediaIdforMedia", "postId for media Error:" + e.getMessage());

        }
    }

    public void updateDataSyncFlagToSync(int mediaId) {
        ContentValues values = new ContentValues();
        values.put(KEY_DataSyncFlag, "1");
        String whereClause = "MediaID = ?";
        String[] whereArgs = new String[]{"" + mediaId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, values, whereClause, whereArgs);
            Log.i("MediaDataSyncFlagSync", " media uploaded successfully amd data sync flag updated to 1" );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MediaDataSyncFlagSync", "Media data sync flag Error:" + e.getMessage());

        }
    }

    public void updateMediaDataDisplayFlagToZero(ArrayList<ConstructionModifiedMediaIds> mModifiedMediaIds) {
        for (int i = 0; i < mModifiedMediaIds.size(); i++){
            ContentValues values = new ContentValues();
            values.put(KEY_DisplayFlag, "0");
            String whereClause = "MediaID = ?";
            String[] whereArgs = new String[]{"" + mModifiedMediaIds.get(i).getOldMediaId()};
            try {
                database.update(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, values, whereClause, whereArgs);
                Log.i("modifiedMediaId", " media display flag updated to zero for mediaId's: " + mModifiedMediaIds.get(i).getOldMediaId());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("modifiedMediaId", "fail to update display flag to zero" + e.getMessage());

            }
        }
    }

    public void updateMediaDisplayFlagToZeroOnDelete(Long postId) {
        ContentValues values = new ContentValues();
        values.put(KEY_DisplayFlag, "-1");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + postId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_MEDIADATA, values, whereClause, whereArgs);
            Log.i("OnDeletePost", " media display flag updated to zero for PostId: " + postId+" onClick of Delete");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OnDeletePost", "fail to update display flag to zero onClick of delete" + e.getMessage());

        }
    }

}
