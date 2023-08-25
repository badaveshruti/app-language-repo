package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.ConstructionModifiedTagIds;
import qnopy.com.qnopyandroid.clientmodel.ConstructionPostMediaTagsDataModel;
import qnopy.com.qnopyandroid.clientmodel.ConstructioncTagDataModel;

public class CTagDataSourceQNote {

    final String KEY_MediaID = "MediaID";
    final String KEY_PostID = "PostID";
    final String KEY_TagID = "TagID";
    final String KEY_CreatedBy = "CreatedBy";
    final String KEY_ModifiedBy = "ModifiedBy";
    final String KEY_ServerCreationDate = "ServerCreationDate";
    final String KEY_ServerModificationDate = "ServerModificationDate";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModificationDate = "ModificationDate";
    final String KEY_sTagID = "sTagId";
    final String KEY_SiteID = "SiteId";
    final String KEY_DisplayFlag = "DisplayFlag";
    final String KEY_ClientTagId = "ClientTagId";
    final String KEY_cTagID = "cTagId";
    final String KEY_DataSyncFlag = "DataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public CTagDataSourceQNote(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }


    public int storeCTagDataSource(ArrayList<ConstructioncTagDataModel> mArrayListCTagData) {
        int ret = 0;

        if (mArrayListCTagData == null) {
            return -1;
        }

        database.beginTransaction();
        try {
            for (ConstructioncTagDataModel s : mArrayListCTagData) {
                ContentValues values = new ContentValues();
                values.put(KEY_MediaID, s.getMediaId());
                values.put(KEY_PostID, s.getPostId());
                values.put(KEY_TagID, s.getTagId());
                values.put(KEY_CreatedBy, s.getCreatedBy());
                values.put(KEY_ModifiedBy, s.getModifiedBy());
                values.put(KEY_ServerCreationDate, s.getServerCreationDate());
                values.put(KEY_ServerModificationDate, s.getModificationDate());
                values.put(KEY_CreationDate, s.getCreationDate());
                values.put(KEY_ModificationDate, s.getModificationDate());
                values.put(KEY_sTagID, s.getsTagId());
                values.put(KEY_SiteID, s.getSiteId());
                values.put(KEY_DisplayFlag, s.getDisplayFlag());
                values.put(KEY_ClientTagId, s.getClientTagId());
                values.put(KEY_cTagID, s.getCtagId());

                ret = (int) database.insert(DbAccess.TABLE_CONSTRUCTION_CTAGDATA, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("storeSimpleNoteDataDB", "storeCTagDataSource: "+e.getMessage());
        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public ArrayList<ConstructionPostMediaTagsDataModel> getTags(String mSiteId, String mUserId, Long postId) {
        ArrayList<ConstructionPostMediaTagsDataModel> arrayListPostMediaTag = new ArrayList<>();
        String postID = String.valueOf(postId);

        String[] whereArgs = null;
        String query = "select C.MediaID, C.PostID, C.TagID, C.CreatedBy, C.ModifiedBy, C.ServerCreationDate, C.ServerModificationDate, " +
                "C.CreationDate, C.ModificationDate, C.sTagId, C.SiteId, C.DisplayFlag, C.cTagId, R.UserID, R.SiteId, R.CompanyID, R.TagID, " +
                "R.Tag, R.CreatedBy, R.ModifiedBy, R.ServerCreationDate, R.ServerModificationDate, R.CreationDate, R.ModificationDate from c_CTagData C inner join c_RTagData R on R.TagID = C.TagID where C.PostID = ?";

        whereArgs = new String[]{postID};
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, whereArgs);

            if(cursor != null && cursor.moveToFirst()){

                do {
                    ConstructionPostMediaTagsDataModel constructionPostMediaTagsDataModel = new ConstructionPostMediaTagsDataModel();

                    constructionPostMediaTagsDataModel.setC_mediaId(cursor.getInt(0));
                    constructionPostMediaTagsDataModel.setC_postId(cursor.getInt(1));
                    constructionPostMediaTagsDataModel.setC_tagId(cursor.getInt(2));
                    constructionPostMediaTagsDataModel.setC_createdBy(cursor.getInt(3));
                    constructionPostMediaTagsDataModel.setC_modifiedBy(cursor.getInt(4));
                    constructionPostMediaTagsDataModel.setC_serverCreationDate(cursor.getLong(5));
                    constructionPostMediaTagsDataModel.setC_serverModificationDate(cursor.getLong(6));
                    constructionPostMediaTagsDataModel.setC_creationDate(cursor.getLong(7));
                    constructionPostMediaTagsDataModel.setC_modificationDate(cursor.getLong(8));
                    constructionPostMediaTagsDataModel.setC_sTagId(cursor.getInt(9));
                    constructionPostMediaTagsDataModel.setC_siteId(cursor.getInt(10));
                    constructionPostMediaTagsDataModel.setC_displayFlag(cursor.getInt(11));
                    constructionPostMediaTagsDataModel.setC_ctagId(cursor.getInt(12));

                    constructionPostMediaTagsDataModel.setR_userId(cursor.getInt(13));
                    constructionPostMediaTagsDataModel.setR_siteId(cursor.getInt(14));
                    constructionPostMediaTagsDataModel.setR_companyId(cursor.getInt(15));
                    constructionPostMediaTagsDataModel.setR_tagId(cursor.getInt(16));
                    String tag = cursor.getString(17);
                    constructionPostMediaTagsDataModel.setR_tag(tag);
                    Log.e("qnoteTags", "getTags: -----"+tag);
                    constructionPostMediaTagsDataModel.setR_createdBy(cursor.getInt(18));
                    constructionPostMediaTagsDataModel.setR_modifiedBy(cursor.getInt(19));
                    constructionPostMediaTagsDataModel.setR_serverCreationDate(cursor.getLong(20));
                    constructionPostMediaTagsDataModel.setR_serverModificationDate(cursor.getLong(21));
                    constructionPostMediaTagsDataModel.setR_creationDate(cursor.getLong(22));
                    constructionPostMediaTagsDataModel.setR_modificationDate(cursor.getLong(23));

                    arrayListPostMediaTag.add(constructionPostMediaTagsDataModel);
                }while (cursor.moveToNext());
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return arrayListPostMediaTag;
    }

    public List<Chip> getAllTagsForSite(String mSiteId) {

        return null;
    }

    public boolean checkIfcTagIdExist(int randomCTagId) {

        String query = "select count(cTagId) from c_CTagData where cTagId = ?";
        String[] whereArgs = new String[]{"" + randomCTagId};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("cTagIdExist", "CheckIfcTagIdExist() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();

        }

        Log.i("cTagIdExist", "CheckIfcTagIdExist() result:" + count);

        if (count > 0){
            return true;
        }
        return false;
    }

    public void insertNewcTagForPost(String mediaId, long postId, String tagId, String createdBy, String modifiedBy,
                                     String serverCreationDate, String serverModificatonDate, String creationDate, String modificationDate,
                                     String sTagId, String siteId, String displayFlag, String clientTagId, String cTagId) {

        int ret = 0;
        database.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_MediaID, mediaId);
            values.put(KEY_PostID, postId);
            values.put(KEY_TagID, tagId);
            values.put(KEY_CreatedBy, createdBy);
            values.put(KEY_ModifiedBy, modifiedBy);
            values.put(KEY_ServerCreationDate, serverCreationDate);
            values.put(KEY_ServerModificationDate, serverModificatonDate);
            values.put(KEY_CreationDate, creationDate);
            values.put(KEY_ModificationDate, modificationDate);
            values.put(KEY_sTagID, sTagId);
            values.put(KEY_SiteID, siteId);
            values.put(KEY_DisplayFlag, displayFlag);
            values.put(KEY_ClientTagId, clientTagId);
            values.put(KEY_cTagID, cTagId);
            values.put(KEY_DataSyncFlag, "0");

            database.insert(DbAccess.TABLE_CONSTRUCTION_CTAGDATA, null, values);

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("storeNewCTagDataDB", "insertNewcTagForPost: "+e.getMessage());
        } finally {
            database.endTransaction();
        }

    }

    public int getCTagZeroDataSyncFlag() {
        int count = 0;
        String query = "select count(DataSyncFlag) from c_CTagData where DataSyncFlag = 0";
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

    public ArrayList<ConstructioncTagDataModel> getNewCTagForPostFromDB() {
        ArrayList<ConstructioncTagDataModel> arrayListNewCTagData = new ArrayList<>();

        String query = "select MediaID, PostID, TagID, CreatedBy, ModifiedBy, ServerCreationDate, ServerModificationDate, CreationDate, " +
                "ModificationDate, sTagId, SiteId, DisplayFlag, ClientTagId, cTagId from c_CTagData where DataSyncFlag = 0";

        Cursor cursor = null;
        Log.i("newCTagDataGetQuery", "Get cTag Date query:" + query);

        cursor = database.rawQuery(query, null);
        try {
            if (cursor != null && cursor.moveToFirst()){
                do {

                    ConstructioncTagDataModel constructioncTagDataModel = new ConstructioncTagDataModel();
                    constructioncTagDataModel.setMediaId(cursor.getInt(0));
                    constructioncTagDataModel.setPostId(cursor.getInt(1));
                    constructioncTagDataModel.setTagId(cursor.getInt(2));
                    constructioncTagDataModel.setCreatedBy(cursor.getInt(3));
                    constructioncTagDataModel.setModifiedBy(cursor.getInt(4));
                    constructioncTagDataModel.setServerCreationDate(cursor.getLong(5));
                    constructioncTagDataModel.setServerModificationDate(cursor.getLong(6));
                    constructioncTagDataModel.setCreationDate(cursor.getLong(7));
                    constructioncTagDataModel.setModificationDate(cursor.getLong(8));
                    constructioncTagDataModel.setsTagId(cursor.getInt(9));
                    constructioncTagDataModel.setSiteId(cursor.getInt(10));
                    constructioncTagDataModel.setDisplayFlag(cursor.getInt(11));
                    constructioncTagDataModel.setClientTagId(cursor.getString(12));
                    constructioncTagDataModel.setCtagId(cursor.getInt(13));

                    arrayListNewCTagData.add(constructioncTagDataModel);

                } while (cursor.moveToNext());
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayListNewCTagData;
    }

    public void updateCTagIDAndPostId(String serverGeneratedCTagId, String serverGeneratedPostId, String clientTagId) {

        ContentValues values = new ContentValues();
        values.put(KEY_PostID, serverGeneratedPostId);
        values.put(KEY_ClientTagId, clientTagId);
        values.put(KEY_cTagID, serverGeneratedCTagId);
        values.put(KEY_DataSyncFlag, "1");
        String whereClause = "cTagId = ?";
        String[] whereArgs = new String[]{"" + clientTagId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_CTAGDATA, values, whereClause, whereArgs);
            Log.i("CTagDataSyncFlagSyncTag", "CTag data sync flag updated to 1 and postId to - "+serverGeneratedPostId+ " serverGeneratedCTagId:- " +serverGeneratedCTagId + "/ "+clientTagId );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CTagDataSyncFlagSyncTag", "CTag data sync flag and postId Error:" + e.getMessage());

        }
    }

    public boolean checkIfServerGeneratedPostIdExist(ArrayList<ConstructioncTagDataModel> mArrayListCTagData) {

        String serverPostId = null;
        for (int i = 0; i < mArrayListCTagData.size(); i++){
            serverPostId = String.valueOf(mArrayListCTagData.get(i).getPostId());
        }

        String query = "select count(PostID) from c_CTagData where PostID = ?";
        String[] whereArgs = new String[]{"" + serverPostId};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ServerPostIdExistForTag", "CheckIfServerPostIdExist() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();

        }
        Log.i("ServerPostIdExistForTag", "CheckIfServerPostIdExist() result:" + count);


        if (count > 0){
            return true;
        }
        return false;
    }

    public void updateTagDataDisplayFlagToZero(ArrayList<ConstructionModifiedTagIds> mModifiedTagIds) {

        for (int i = 0; i < mModifiedTagIds.size(); i++){
            ContentValues values = new ContentValues();
            values.put(KEY_DisplayFlag, "0");
            String whereClause = "TagID = ?";
            String[] whereArgs = new String[]{"" + mModifiedTagIds.get(i).getOldTagId()};
            try {
                database.update(DbAccess.TABLE_CONSTRUCTION_CTAGDATA, values, whereClause, whereArgs);
                Log.i("modifiedTagId", "display flag of cTagData updated to zero for tagId: "+mModifiedTagIds.get(i).getOldTagId());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("modifiedTagId", "fail to update diaplay flag to zero of cTagData" + e.getMessage());

            }
        }

    }

    public void updateTagDataDisplayFlagToZeroOnDelete(Long postId) {
        ContentValues values = new ContentValues();
        values.put(KEY_DisplayFlag, "-1");
        String whereClause = "PostID = ?";
        String[] whereArgs = new String[]{"" + postId};
        try {
            database.update(DbAccess.TABLE_CONSTRUCTION_CTAGDATA, values, whereClause, whereArgs);
            Log.i("OnDeletePost", "display flag of cTagData updated to zero for postId: "+postId+" onClick of Delete");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OnDeletePost", "fail to update diaplay flag to zero of cTagData onClick of delete" + e.getMessage());

        }
    }

    public ArrayList<ConstructioncTagDataModel> getCTagDataToDelete(Long postId) {
        ArrayList<ConstructioncTagDataModel> arrayListCTagData = new ArrayList<>();

        String query = "select MediaID, PostID, TagID, CreatedBy, ModifiedBy, ServerCreationDate, ServerModificationDate, CreationDate, " +
                "ModificationDate, sTagId, SiteId, DisplayFlag, ClientTagId, cTagId from c_CTagData where PostID = "+postId;

        Cursor cursor = null;
        Log.i("cTagDataOnDelete", "Get cTag Date query:" + query);

        cursor = database.rawQuery(query, null);
        try {
            if (cursor != null && cursor.moveToFirst()){
                do {

                    ConstructioncTagDataModel constructioncTagDataModel = new ConstructioncTagDataModel();
                    constructioncTagDataModel.setMediaId(cursor.getInt(0));
                    constructioncTagDataModel.setPostId(cursor.getInt(1));
                    constructioncTagDataModel.setTagId(cursor.getInt(2));
                    constructioncTagDataModel.setCreatedBy(cursor.getInt(3));
                    constructioncTagDataModel.setModifiedBy(cursor.getInt(4));
                    constructioncTagDataModel.setServerCreationDate(cursor.getLong(5));
                    constructioncTagDataModel.setServerModificationDate(cursor.getLong(6));
                    constructioncTagDataModel.setCreationDate(cursor.getLong(7));
                    constructioncTagDataModel.setModificationDate(cursor.getLong(8));
                    constructioncTagDataModel.setsTagId(cursor.getInt(9));
                    constructioncTagDataModel.setSiteId(cursor.getInt(10));
                    constructioncTagDataModel.setDisplayFlag(cursor.getInt(11));
                    constructioncTagDataModel.setClientTagId(cursor.getString(12));
                    constructioncTagDataModel.setCtagId(cursor.getInt(13));

                    arrayListCTagData.add(constructioncTagDataModel);

                } while (cursor.moveToNext());
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayListCTagData;
    }

    public ArrayList<ConstructionPostMediaTagsDataModel> getAllSelectedTags(String mSiteId) {
        ArrayList<ConstructionPostMediaTagsDataModel> arrayList = new ArrayList<>();
        String query = "select C.MediaID, C.PostID, C.TagID, C.CreatedBy, C.ModifiedBy, C.ServerCreationDate, C.ServerModificationDate, " +
                "C.CreationDate, C.ModificationDate, C.sTagId, C.SiteId, C.DisplayFlag, C.cTagId, R.UserID, R.SiteId, R.CompanyID, R.TagID, " +
                "R.Tag, R.CreatedBy, R.ModifiedBy, R.ServerCreationDate, R.ServerModificationDate, R.CreationDate, R.ModificationDate from c_CTagData C inner join c_RTagData R on R.TagID = C.TagID where C.DataSyncFlag = 0 and C.DisplayFlag = 1 and C.SiteId = "+mSiteId;

        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);

            if(cursor != null && cursor.moveToFirst()){

                do {
                    ConstructionPostMediaTagsDataModel constructionPostMediaTagsDataModel = new ConstructionPostMediaTagsDataModel();

                    constructionPostMediaTagsDataModel.setC_mediaId(cursor.getInt(0));
                    constructionPostMediaTagsDataModel.setC_postId(cursor.getInt(1));
                    constructionPostMediaTagsDataModel.setC_tagId(cursor.getInt(2));
                    constructionPostMediaTagsDataModel.setC_createdBy(cursor.getInt(3));
                    constructionPostMediaTagsDataModel.setC_modifiedBy(cursor.getInt(4));
                    constructionPostMediaTagsDataModel.setC_serverCreationDate(cursor.getLong(5));
                    constructionPostMediaTagsDataModel.setC_serverModificationDate(cursor.getLong(6));
                    constructionPostMediaTagsDataModel.setC_creationDate(cursor.getLong(7));
                    constructionPostMediaTagsDataModel.setC_modificationDate(cursor.getLong(8));
                    constructionPostMediaTagsDataModel.setC_sTagId(cursor.getInt(9));
                    constructionPostMediaTagsDataModel.setC_siteId(cursor.getInt(10));
                    constructionPostMediaTagsDataModel.setC_displayFlag(cursor.getInt(11));
                    constructionPostMediaTagsDataModel.setC_ctagId(cursor.getInt(12));

                    constructionPostMediaTagsDataModel.setR_userId(cursor.getInt(13));
                    constructionPostMediaTagsDataModel.setR_siteId(cursor.getInt(14));
                    constructionPostMediaTagsDataModel.setR_companyId(cursor.getInt(15));
                    constructionPostMediaTagsDataModel.setR_tagId(cursor.getInt(16));
                    String tag = cursor.getString(17);
                    constructionPostMediaTagsDataModel.setR_tag(tag);
                    Log.e("qnoteTags", "getTags: -----"+tag);
                    constructionPostMediaTagsDataModel.setR_createdBy(cursor.getInt(18));
                    constructionPostMediaTagsDataModel.setR_modifiedBy(cursor.getInt(19));
                    constructionPostMediaTagsDataModel.setR_serverCreationDate(cursor.getLong(20));
                    constructionPostMediaTagsDataModel.setR_serverModificationDate(cursor.getLong(21));
                    constructionPostMediaTagsDataModel.setR_creationDate(cursor.getLong(22));
                    constructionPostMediaTagsDataModel.setR_modificationDate(cursor.getLong(23));

                    arrayList.add(constructionPostMediaTagsDataModel);
                }while (cursor.moveToNext());
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayList;
    }

    public void updateTagIdToRemove(String label) {

        String query = "update c_CTagData set DisplayFlag = 0 where TagID in(select TagID from c_RTagData where Tag = +label)";

        try {
            database.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
