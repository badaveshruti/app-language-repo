package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.text.Html;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.clientmodel.ConstructionModifiedPostIds;
import qnopy.com.qnopyandroid.clientmodel.ConstructionPostDataModel;

public class PostDataSourceQNote {

    final String KEY_PostID = "PostID";
    final String KEY_UserID = "UserID";
    final String KEY_SiteID = "SiteId";
    final String KEY_LocationID = "LocationId";
    final String KEY_DisplayFlag = "DisplayFlag";
    final String KEY_PostText = "PostText";
    final String KEY_CreatedBy = "CreatedBy";
    final String KEY_ModifiedBy = "ModifiedBy";
    final String KEY_ServerCreationDate = "ServerCreationDate";
    final String KEY_ServerModificationDate = "ServerModificationDate";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModificationDate = "ModificationDate";
    final String KEY_Latitude = "Latitude";
    final String KEY_Longitude = "Longitude";
    final String KEY_sPostId = "sPostID";
    final String KEY_ClientPostId = "ClientPostId";
    final String KEY_PostUserName = "PostUserName";
    final String KEY_DataSyncFlag = "DataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public PostDataSourceQNote(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }


    public int storePostData(ArrayList<ConstructionPostDataModel> mArrayListPostData) {
        int ret = 0;

        String text = null;
        if (mArrayListPostData == null) {
            return -1;
        }

        database.beginTransaction();
        try {
            for (ConstructionPostDataModel s : mArrayListPostData) {
                if (s.getPostText().equals("") || s.getPostText().isEmpty()){
                    text = "";
                }else {
                   try {
                       byte[] data = Base64.decode(s.getPostText(), Base64.NO_WRAP | Base64.URL_SAFE );

                       //byte[] data = Base64.decode(s.getPostText(), Base64.URL_SAFE | Base64.DEFAULT);
                       text = new String(data, "UTF-8");
                       text = Html.fromHtml(text).toString();
                   }catch (Exception e){
                       e.printStackTrace();
                       //text = s.getPostText();
                   }
                }

                ContentValues values = new ContentValues();
                values.put(KEY_PostID, s.getPostId());
                values.put(KEY_UserID, s.getUserId());
                values.put(KEY_SiteID, s.getSiteId());
                values.put(KEY_LocationID, s.getLocationId());
                values.put(KEY_DisplayFlag, s.getDisplayFlag());
                values.put(KEY_PostText, text);
                values.put(KEY_CreatedBy, s.getCreatedBy());
                values.put(KEY_ModifiedBy, s.getModifiedBy());
                values.put(KEY_ServerCreationDate, s.getServerCreationDate());
                values.put(KEY_ServerModificationDate, s.getModificationDate());
                values.put(KEY_CreationDate, s.getCreationDate());
                values.put(KEY_ModificationDate, s.getModificationDate());
                values.put(KEY_Latitude, s.getLatitude());
                values.put(KEY_Longitude, s.getLongitude());
                values.put(KEY_sPostId, s.getsPostId());
                values.put(KEY_ClientPostId, s.getClientPostId());
                values.put(KEY_PostUserName, s.getPostUserName());

                ret = (int) database.insert(DbAccess.TABLE_CONSTRUCTION_POSTDATA, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("storeSimpleNoteDataDB", "store Post Data:"+e.getMessage());

        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public ArrayList<ConstructionPostDataModel> getPostDataFromDB(String mSiteId, String userID) {
        ArrayList<ConstructionPostDataModel> arrayListPostData = new ArrayList<>();

        String query = "select PostID, UserID, SiteId, LocationId, DisplayFlag, PostText, CreatedBy, ModifiedBy, ServerCreationDate, " +
                "ServerModificationDate, CreationDate, ModificationDate, Latitude, Longitude, sPostID, ClientPostId, PostUserName from c_PostData where SiteId=? AND UserID=? AND PostID > 0";
        String[] whereArgs = null;
        whereArgs = new String[]{mSiteId, userID};

        Cursor cursor = null;
        Log.i("postDataGetQuery", "Get Post Date query:" + query);

        cursor = database.rawQuery(query, whereArgs);
        String text = null;
        try {
            if(cursor != null && cursor.moveToFirst()){

                do {
                    ConstructionPostDataModel constructionPostDataModel = new ConstructionPostDataModel();
                    constructionPostDataModel.setPostId(cursor.getLong(0));
                    constructionPostDataModel.setUserId(cursor.getInt(1));
                    constructionPostDataModel.setSiteId(cursor.getInt(2));
                    constructionPostDataModel.setLocationId(cursor.getInt(3));
                    constructionPostDataModel.setDisplayFlag(cursor.getInt(4));
                    constructionPostDataModel.setPostText(cursor.getString(5));
                    constructionPostDataModel.setCreatedBy(cursor.getInt(6));
                    constructionPostDataModel.setModifiedBy(cursor.getInt(7));
                    constructionPostDataModel.setServerCreationDate(cursor.getLong(8));
                    constructionPostDataModel.setServerModificationDate(cursor.getLong(9));
                    constructionPostDataModel.setCreationDate(cursor.getLong(10));
                    constructionPostDataModel.setModificationDate(cursor.getLong(11));
                    constructionPostDataModel.setLatitude(cursor.getDouble(12));
                    constructionPostDataModel.setLongitude(cursor.getDouble(13));
                    constructionPostDataModel.setsPostId(cursor.getInt(14));
                    constructionPostDataModel.setClientPostId(cursor.getString(15));
                    constructionPostDataModel.setPostUserName(cursor.getString(16));

                    arrayListPostData.add(constructionPostDataModel);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("postDataGetQuery", "Get Post Date query ERROR: " + e);
        }

        return arrayListPostData;
    }

    public int insertNewPost(long mCurrentDateTimeMillisecNegative, String userID, String siteId, String locationId, String displayFlag,
                             String postTextEncoded, String createdBy, String modifiedBy, String serverCreationDate, String serverModificationDate,
                             long currentDateTimeMillisec, String modificationDate, Double latitude, Double longitude, String sPostId, String clientId, String postUsername) {

        int ret = 0;
        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PostID, mCurrentDateTimeMillisecNegative);
            values.put(KEY_UserID, userID);
            values.put(KEY_SiteID, siteId);
            values.put(KEY_LocationID, locationId);
            values.put(KEY_DisplayFlag, displayFlag);
            values.put(KEY_PostText, postTextEncoded);
            values.put(KEY_CreatedBy, createdBy);
            values.put(KEY_ModifiedBy, modifiedBy);
            values.put(KEY_ServerCreationDate, serverCreationDate);
            values.put(KEY_ServerModificationDate, serverModificationDate);
            values.put(KEY_CreationDate, currentDateTimeMillisec);
            values.put(KEY_ModificationDate, modificationDate);
            values.put(KEY_Latitude, latitude);
            values.put(KEY_Longitude, longitude);
            values.put(KEY_sPostId, sPostId);
            values.put(KEY_ClientPostId, clientId);
            values.put(KEY_PostUserName, postUsername);

            ret = (int) database.insert(DbAccess.TABLE_CONSTRUCTION_POSTDATA, null, values);
            database.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            database.endTransaction();
        }
        return ret;
    }

    public boolean CheckIfPostIdExist(long mCurrentDateTimeMillisecNegative) {

        String query = "select count(PostID) from c_PostData where PostID = ?";
        String[] whereArgs = new String[]{"" + mCurrentDateTimeMillisecNegative};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("PostIdExist", "CheckIfPostIdExist() error:" + e.getMessage());
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

    public boolean checkIfServerGeneratedPostIdExist(ArrayList<ConstructionPostDataModel> mArrayListPostData) {

        Long serverPostId = null;
        for (int i = 0; i < mArrayListPostData.size(); i++){
            serverPostId = mArrayListPostData.get(i).getPostId();
        }

        String query = "select count(PostID) from c_PostData where PostID = ?";
        String[] whereArgs = new String[]{"" + serverPostId};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ServerPostIdExist", "CheckIfServerPostIdExist() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();

        }
        Log.i("ServerPostIdExist", "CheckIfServerPostIdExist() result:" + count);


        if (count > 0){
            return true;
        }
        return false;
    }

    public ArrayList<ConstructionPostDataModel> getNewPostDataFromDB() {

        ArrayList<ConstructionPostDataModel> arrayListNewPostData = new ArrayList<>();

        String query = "select PostID, UserID, SiteId, LocationId, DisplayFlag, PostText, CreatedBy, ModifiedBy, ServerCreationDate, " +
                "ServerModificationDate, CreationDate, ModificationDate, Latitude, Longitude, sPostID, ClientPostId, PostUserName from c_PostData where DataSyncFlag = 0";

        Cursor cursor = null;
        Log.i("newPostDataGetQuery", "Get Post Date query:" + query);

        cursor = database.rawQuery(query, null);

        try {
            if(cursor != null && cursor.moveToFirst()){

                do {
                    ConstructionPostDataModel constructionPostDataModel = new ConstructionPostDataModel();
                    constructionPostDataModel.setPostId(cursor.getLong(0));
                    constructionPostDataModel.setUserId(cursor.getInt(1));
                    constructionPostDataModel.setSiteId(cursor.getInt(2));
                    constructionPostDataModel.setLocationId(cursor.getInt(3));
                    constructionPostDataModel.setDisplayFlag(cursor.getInt(4));
                    constructionPostDataModel.setPostText(cursor.getString(5));
                    constructionPostDataModel.setCreatedBy(cursor.getInt(6));
                    constructionPostDataModel.setModifiedBy(cursor.getInt(7));
                    constructionPostDataModel.setServerCreationDate(cursor.getLong(8));
                    constructionPostDataModel.setServerModificationDate(cursor.getLong(9));
                    constructionPostDataModel.setCreationDate(cursor.getLong(10));
                    constructionPostDataModel.setModificationDate(cursor.getLong(11));
                    constructionPostDataModel.setLatitude(cursor.getDouble(12));
                    constructionPostDataModel.setLongitude(cursor.getDouble(13));
                    constructionPostDataModel.setsPostId(cursor.getInt(14));
                    constructionPostDataModel.setClientPostId(cursor.getString(15));
                    constructionPostDataModel.setPostUserName(cursor.getString(16));

                    arrayListNewPostData.add(constructionPostDataModel);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("newPostDataGetQuery", "Get Post Date query ERROR: " + e);
        }
        return arrayListNewPostData;
    }

    public void UpdateDataSyncFlagToSave(long mCurrentDateTimeMillisecNegative) {

        ContentValues values = new ContentValues();
        values.put(KEY_DataSyncFlag, "0");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + mCurrentDateTimeMillisecNegative};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
            Log.i("PostDataSyncFlagSync", " Post data sync flag updated to 0" );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PostDataSyncFlagSync", "Post data sync flag Error:" + e.getMessage());

        }
    }

    public void updateDataSyncFlagToSyncAndPostId(String serverGeneratedPostId, long clientGeneratedPostId) {

        ContentValues values = new ContentValues();
        values.put(KEY_PostID, serverGeneratedPostId);
        values.put(KEY_DataSyncFlag, "1");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + clientGeneratedPostId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
            Log.i("PostDataSyncFlagSync", "Post data sync flag updated to 1 and postId to - "+serverGeneratedPostId+ " ClientGeneratedPostid:- " +clientGeneratedPostId );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PostDataSyncFlagSync", "Post data sync flag and postId Error:" + e.getMessage());

        }
    }
    public void updateDataSyncFlagToSyncAndPostIdForMedia(String serverGeneratedPostId, long clientGeneratedPostId) {

        ContentValues values = new ContentValues();
        values.put(KEY_PostID, serverGeneratedPostId);
        //values.put(KEY_DataSyncFlag, "0");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + clientGeneratedPostId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
            Log.i("PostIdforMedia", "PostID For Media to - "+serverGeneratedPostId+ " ClientGeneratedPostid:- " +clientGeneratedPostId );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PostIdforMedia", "postId for media Error:" + e.getMessage());

        }
    }

    public void updatePostText(long mCurrentDateTimeMillisecNegative, String mPostText) {
        ContentValues values = new ContentValues();
        values.put(KEY_PostText, mPostText);
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + mCurrentDateTimeMillisecNegative};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
            Log.i("PostDataSyncFlagSync", " Post text updated to : "+mPostText );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PostDataSyncFlagSync", "Post text update Error:" + e.getMessage());

        }
    }

    public int getPostZeroDataSyncFlag() {
        int count = 0;
        String query = "select count(DataSyncFlag) from c_PostData where DataSyncFlag = 0";
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

    public void updatePostIdAndDataSyncFlagToSyncForTag(String serverGeneratedPostId, long mClientGeneratedPostId, String clientPostId) {

        ContentValues values = new ContentValues();
        values.put(KEY_PostID, serverGeneratedPostId);
        values.put(KEY_ClientPostId, clientPostId);
        values.put(KEY_DataSyncFlag, "1");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + clientPostId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
            Log.i("PostDataSyncFlagSyncTag", "Post data sync flag updated to 1 and postId to - "+serverGeneratedPostId+ " ClientGeneratedPostid:- " +mClientGeneratedPostId + "/ "+clientPostId );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PostDataSyncFlagSyncTag", "Post data sync flag and postId Error:" + e.getMessage());

        }

    }

    public void updateDisplayFlagToZero(ArrayList<ConstructionModifiedPostIds> mModifiedPostIds) {
        for (int i = 0; i < mModifiedPostIds.size(); i++){
            ContentValues values = new ContentValues();
            values.put(KEY_DisplayFlag, "0");
            String whereClause = "PostID = ?";
            String[] whereArgs = new String[]{"" + mModifiedPostIds.get(i).getOldPostIds()};
            try {
                database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
                Log.i("modifiedPostId", " display flag updated to zero for post ids: "+mModifiedPostIds.get(i).getOldPostIds());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("modifiedPostId", "fail to update display flag to zero" + e.getMessage());

            }
        }
    }

    public void updateDisplayFlagToZeroOnDelete(Long postId) {

        ContentValues values = new ContentValues();
        values.put(KEY_DisplayFlag, "-1");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + postId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_POSTDATA, values, whereClause, whereArgs);
            Log.i("OnDeletePost", " display flag updated to zero for post ids: "+postId+" onClick of delete");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OnDeletePost", "fail to update display flag to zero onClickOf Delete" + e.getMessage());

        }
    }

    public ArrayList<ConstructionPostDataModel> getPostDataToDelete(Long postId) {
        ArrayList<ConstructionPostDataModel> arrayListPostDataDelete = new ArrayList<>();

        String query = "select PostID, UserID, SiteId, LocationId, DisplayFlag, PostText, CreatedBy, ModifiedBy, ServerCreationDate, " +
                "ServerModificationDate, CreationDate, ModificationDate, Latitude, Longitude, sPostID, ClientPostId, PostUserName from c_PostData where PostID = "+postId;

        Cursor cursor = null;
        Log.i("postDataDelete", "Get Post Date query:" + query);

        cursor = database.rawQuery(query, null);

        try {
            if(cursor != null && cursor.moveToFirst()){

                do {
                    ConstructionPostDataModel constructionPostDataModel = new ConstructionPostDataModel();
                    constructionPostDataModel.setPostId(cursor.getLong(0));
                    constructionPostDataModel.setUserId(cursor.getInt(1));
                    constructionPostDataModel.setSiteId(cursor.getInt(2));
                    constructionPostDataModel.setLocationId(cursor.getInt(3));
                    constructionPostDataModel.setDisplayFlag(cursor.getInt(4));
                    constructionPostDataModel.setPostText(cursor.getString(5));
                    constructionPostDataModel.setCreatedBy(cursor.getInt(6));
                    constructionPostDataModel.setModifiedBy(cursor.getInt(7));
                    constructionPostDataModel.setServerCreationDate(cursor.getLong(8));
                    constructionPostDataModel.setServerModificationDate(cursor.getLong(9));
                    constructionPostDataModel.setCreationDate(cursor.getLong(10));
                    constructionPostDataModel.setModificationDate(cursor.getLong(11));
                    constructionPostDataModel.setLatitude(cursor.getDouble(12));
                    constructionPostDataModel.setLongitude(cursor.getDouble(13));
                    constructionPostDataModel.setsPostId(cursor.getInt(14));
                    constructionPostDataModel.setClientPostId(cursor.getString(15));
                    constructionPostDataModel.setPostUserName(cursor.getString(16));

                    arrayListPostDataDelete.add(constructionPostDataModel);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("postDataDelete", "Get Post Date query ERROR: " + e);
        }
        return arrayListPostDataDelete;
    }
}
