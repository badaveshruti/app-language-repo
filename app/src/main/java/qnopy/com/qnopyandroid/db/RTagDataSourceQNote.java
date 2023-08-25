package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.ConstructionRefTagDataModel;


public class RTagDataSourceQNote {

    final String KEY_UserID = "UserID";
    final String KEY_SiteID = "SiteId";
    final String KEY_CompanyID = "CompanyID";
    final String KEY_TagID = "TagID";
    final String KEY_Tag = "Tag";
    final String KEY_CreatedBy = "CreatedBy";
    final String KEY_ModifiedBy = "ModifiedBy";
    final String KEY_ServerCreationDate = "ServerCreationDate";
    final String KEY_ServerModificationDate = "ServerModificationDate";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModificationDate = "ModificationDate";
    final String KEY_ClientTagId = "ClientTagId";
    final String KEY_DataSyncFlag = "DataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public RTagDataSourceQNote(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }


    public int storeRTagDataSource(ArrayList<ConstructionRefTagDataModel> mArrayListRTagData) {
        int ret = 0;

        if (mArrayListRTagData == null) {
            return -1;
        }

        database.beginTransaction();
        try {
            for (ConstructionRefTagDataModel s : mArrayListRTagData) {
                byte[] data = Base64.decode(s.getTag(), Base64.NO_WRAP | Base64.URL_SAFE);
                String tag = new String(data, "UTF-8");

                ContentValues values = new ContentValues();
                values.put(KEY_UserID, s.getUserId());
                values.put(KEY_SiteID, s.getSiteId());
                values.put(KEY_CompanyID, s.getCompanyId());
                values.put(KEY_TagID, s.getTagId());
                values.put(KEY_Tag, tag );
                values.put(KEY_CreatedBy, s.getCreatedBy());
                values.put(KEY_ModifiedBy, s.getModifiedBy());
                values.put(KEY_ServerCreationDate, s.getServerCreationDate());
                values.put(KEY_ServerModificationDate, s.getModificationDate());
                values.put(KEY_CreationDate, s.getCreationDate());
                values.put(KEY_ModificationDate, s.getModificationDate());
                values.put(KEY_ClientTagId, s.getClientTagId());

                ret = (int) database.insert(DbAccess.TABLE_CONSTRUCTION_RTAGDATA, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("storeSimpleNoteDataDB", "storeRTagDataSource: "+e.getMessage());
        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public List<Chip> getAllTagsForSite(String mSiteId) {

        List<Chip> tagsForSite = new ArrayList<>();

        String query = "select TagID, Tag from c_RTagData where SiteId = ?";
        String[] whereArgs = null;
        whereArgs = new String[]{mSiteId};
        Cursor cursor = null;

        cursor = database.rawQuery(query, whereArgs);

        if(cursor != null && cursor.moveToFirst()){
            do {
                    String tagId = cursor.getString(0);
                    String tag = cursor.getString(1);

                    tagsForSite.add(new Chip(tagId, tag, tag));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return tagsForSite;
    }

    public boolean checkRefTagIdExist(long clientRefTagId) {

        String query = "select count(TagID) from c_RTagData where TagID = ?";
        String[] whereArgs = new String[]{"" + clientRefTagId};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("RTagIdExist", "CheckIfRTagIdExist() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();

        }

        Log.i("RTagIdExist", "CheckIfRTagIdExist() result:" + count);

        if (count > 0){
            return true;
        }
        return false;
    }

    public void insertCustomTagToRTagData(String userID, String siteId, String companyID, long tagId, String tag, String createdBy,
                                          String modifiedBy, String serverCreationDate, String serverModificationDate,
                                          long creationDate, String modificationDate, String clientTagId) {
        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_UserID, userID);
            values.put(KEY_SiteID, siteId);
            values.put(KEY_CompanyID, companyID);
            values.put(KEY_TagID, tagId);
            values.put(KEY_Tag, tag );
            values.put(KEY_CreatedBy, createdBy);
            values.put(KEY_ModifiedBy, modifiedBy);
            values.put(KEY_ServerCreationDate, serverCreationDate);
            values.put(KEY_ServerModificationDate, serverModificationDate);
            values.put(KEY_CreationDate, creationDate);
            values.put(KEY_ModificationDate, modificationDate);
            values.put(KEY_ClientTagId, clientTagId);

            database.insert(DbAccess.TABLE_CONSTRUCTION_RTAGDATA, null, values);

            database.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            Log.i("RTagCustom", "insertCustomTagToRTagData: "+e.getMessage());
        }finally {
            database.endTransaction();
        }

    }

    public void updateDataSyncFlagToSave(long clientRefTagIdNegative) {

        ContentValues values = new ContentValues();
        values.put(KEY_DataSyncFlag, "0");
        String whereClause = "TagID = ?";
        String[] whereArgs = new String[]{"" + clientRefTagIdNegative};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_RTAGDATA, values, whereClause, whereArgs);
            Log.i("RTagDataSyncFlagSave", " RTag data sync flag updated to 0" );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RTagDataSyncFlagSave", "RTag data sync flag Error:" + e.getMessage());

        }

    }

    public int getRTagDataSyncFlag() {

        int count = 0;
        String query = "select count(DataSyncFlag) from c_RTagData where DataSyncFlag = 0";
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

    public ArrayList<ConstructionRefTagDataModel> getNewRTagForPostFromDB() {
        ArrayList<ConstructionRefTagDataModel> arrayListNewRefTag = new ArrayList<>();

        String query = "select UserID, SiteId, CompanyID, TagID, Tag, CreatedBy, ModifiedBy, ServerCreationDate, ServerModificationDate, " +
                "CreationDate, ModificationDate, ClientTagId from c_RTagData where DataSyncFlag = 0";


        Cursor cursor = null;
        Log.i("newRTagDataGetQuery", "Get rTag Date query:" + query);

        cursor = database.rawQuery(query, null);

        try {

            if (cursor != null && cursor.moveToFirst()){
                do {
                    ConstructionRefTagDataModel constructionRefTagDataModel = new ConstructionRefTagDataModel();

                    constructionRefTagDataModel.setUserId(cursor.getInt(0));
                    constructionRefTagDataModel.setSiteId(cursor.getInt(1));
                    constructionRefTagDataModel.setCompanyId(cursor.getInt(2));
                    constructionRefTagDataModel.setTagId(cursor.getInt(3));
                    constructionRefTagDataModel.setTag(cursor.getString(4));
                    constructionRefTagDataModel.setCreatedBy(cursor.getInt(5));
                    constructionRefTagDataModel.setModifiedBy(cursor.getInt(6));
                    constructionRefTagDataModel.setServerCreationDate(cursor.getLong(7));
                    constructionRefTagDataModel.setServerModificationDate(cursor.getLong(8));
                    constructionRefTagDataModel.setCreationDate(cursor.getLong(9));
                    constructionRefTagDataModel.setModificationDate(cursor.getLong(10));
                    constructionRefTagDataModel.setClientTagId(cursor.getString(11));

                    arrayListNewRefTag.add(constructionRefTagDataModel);

                }while (cursor.moveToNext());
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return arrayListNewRefTag;
    }

    public void updateTagIdAndDataSyncFlagToSync(String serverGeneratedTagId, String refClientTagId, String clientTagId) {

        ContentValues values = new ContentValues();
        //values.put(KEY_TagID, serverGeneratedTagId);
        //values.put(KEY_ClientTagId, refClientTagId);
        values.put(KEY_DataSyncFlag, "1");
        String whereClause = "TagID = ?";
        String[] whereArgs = new String[]{"" + refClientTagId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_RTAGDATA, values, whereClause, whereArgs);
            Log.i("RTagDataSyncFlagSync", " RTag data sync flag updated to 1 with server generated tagId: "+serverGeneratedTagId+" update with "+refClientTagId );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RTagDataSyncFlagSync", "RTag data sync flag Error:" + e.getMessage());

        }

        /*ContentValues contentValues = new ContentValues();
        contentValues.put("TagID", serverGeneratedTagId);
        String whereClauseTag = "cTagId = ?";
        String[] whereArgsTag = new String[]{"" + clientTagId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_CTAGDATA, contentValues, whereClauseTag, whereArgsTag);
            Log.i("CTagDataTagId", "CTag tagId update to"+serverGeneratedTagId+" for clientTagId  - "+clientTagId );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CTagDataTagId", "CTag data sync flag and postId Error:" + e.getMessage());

        }*/
    }

    public boolean checkIfServerGeneratedTagIdExists(ArrayList<ConstructionRefTagDataModel> mArrayListRTagData) {

        String serverGeneratedTagId = null;
        for (int i = 0; i < mArrayListRTagData.size(); i++){
            serverGeneratedTagId = String.valueOf(mArrayListRTagData.get(i).getTagId());
        }

        String query = "select count(TagID) from c_RTagData where TagID = ?";
        String[] whereArgs = new String[]{"" + serverGeneratedTagId};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ServerTagIdExistForRTag", "CheckIfServerTagIdExist() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();

        }
        Log.i("ServerTagIdExistForRTag", "CheckIfServerTagIdExist() result:" + count);


        if (count > 0){
            return true;
        }
        return false;
    }
}
