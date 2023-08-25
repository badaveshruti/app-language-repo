package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.requestmodel.downloadFileModel;
import qnopy.com.qnopyandroid.responsemodel.FileDataModel;
import qnopy.com.qnopyandroid.responsemodel.FolderDataModel;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 23-Jan-16.
 */
@Singleton
public class FileFolderDataSource {

    Context mContext;
    private static final String TAG = "FileFolderDataSource";

    // TODO: 23-Jan-16 s_project_file COLUMNS

    final String KEY_file_id = "file_id";//file_id           INT (11)      NOT NULL,
    final String KEY_file_name = "file_name";//file_name         VARCHAR (255),
    final String KEY_file_type = "file_type";//file_type         VARCHAR (100),

    final String KEY_file_status = "file_status";//file_status       INT (11),

    final String KEY_file_description = "file_description";//file_description  VARCHAR (255),
    final String KEY_file_checksum = "file_checksum";//file_checksum     VARCHAR (255),
    final String KEY_file_guid = "file_guid";//file_guid         VARCHAR (100),
    final String KEY_download_status = "download_status";//download_status         BOOLEAN

    final String KEY_file_path = "file_path";//file_path         VARCHAR (255),


    // TODO: 23-Jan-16  s_project_folder COLUMNS


    final String KEY_folder_name = "folder_name";// folder_name        VARCHAR (100)
    final String KEY_folder_guid = "folder_guid";// folder_guid        VARCHAR (100) NOT NULL
    final String KEY_parent_id = "parent_id";// parent_id          INT (11)
    final String KEY_folder_status = "folder_status";// folder_status      INT (11)
    final String KEY_folder_description = "description";// description VARCHAR (255)


    //23-Jan-16 Common Columns
    final String KEY_site_id = " site_id";// site_id            INT (11),
    final String KEY_folder_id = " folder_id";// folder_id          INT (11),
    final String KEY_created_by = " created_by";// created_by         INT
    final String KEY_creation_date = "creation_date";// creation_date      BIGINT (20)
    final String KEY_modified_by = "modified_by";// modified_by        INT
    final String KEY_modification_date = "modification_date";// modification_date  BIGINT (20)
    final String KEY_notes = "notes";// notes              VARCHAR (500)

    //23-Jan-16  s_file_permission COLUMNS
    final String KEY_file_permission_id = " file_permission_id";// file_permission_id INT (11)      NOT NULL,
    final String KEY_role_id = " role_id           ";// role_id            INT (11),
    final String KEY_permission_id = " permission_id     ";// permission_id      INT (11),
    final String KEY_mobile_app_id = " mobile_app_id     ";// mobile_app_id      INT (11),
    final String KEY_permission_status = " permission_status ";// permission_status  INT (11),
    final String KEY_user_id = " user_id           ";// user_id            INT (11),
    final String KEY_company_id = " company_id        ";// company_id         INT (11),

    public SQLiteDatabase database;

    @Inject
    public FileFolderDataSource(Context context) {

        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public int truncateTempFileFolder(String siteId) {

		/*
        String query = "select F.MobileAppID from d_FieldData as F join d_Event as E" +
				" on F.EventID = E.EventID where F.MobileAppID in (select MobileAppID from s_MobileApp" +
				" where parent_app_id = ?) and F.CreationDate is NOT NULL and F.LocationID = ? and E.EventStatus = 1";
		*/

        int ret = 0;
        try {
            ret = database.delete(DbAccess.TABLE_TEMP_PROJECT_FOLDER, KEY_site_id + "=?", new String[]{siteId});
            ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
            Log.i(TAG, "Deleted Row Count for Folder SiteID:" + siteId + " :" + ret);
            ret = database.delete(DbAccess.TABLE_TEMP_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
            Log.i(TAG, "Deleted Row Count for File SiteID:" + siteId + " :" + ret);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    public int truncateFileFolder() {
        // TODO Auto-generated method stub

        int ret = 0;
        try {
            ret = database.delete(DbAccess.TABLE_TEMP_PROJECT_FOLDER, null, null);
            ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, null, null);
            Log.i(TAG, "Deleted Row Count for Folder :" + ret);
            ret = database.delete(DbAccess.TABLE_TEMP_PROJECT_FILE, null, null);
            ret = database.delete(DbAccess.TABLE_S_PROJECT_FOLDER, null, null);
            Log.i(TAG, "Deleted Row Count for File SiteID :" + ret);
            Log.i(TAG, "File Folder Truncated Status:" + Util.deleteFileFolderDir(mContext));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int truncateS_Folder(String siteId) {
        // TODO Auto-generated method stub

		/*
        String query = "select F.MobileAppID from d_FieldData as F join d_Event as E" +
				" on F.EventID = E.EventID where F.MobileAppID in (select MobileAppID from s_MobileApp" +
				" where parent_app_id = ?) and F.CreationDate is NOT NULL and F.LocationID = ? and E.EventStatus = 1";
		*/

        int ret = 0;
        try {
            ret = database.delete(DbAccess.TABLE_S_PROJECT_FOLDER, KEY_site_id + "=?", new String[]{siteId});
            Log.i(TAG, "Deleted Row Count for S_Folder SiteID:" + siteId + " :" + ret);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int truncateS_File(String siteId) {
        // TODO Auto-generated method stub

		/*
        String query = "select F.MobileAppID from d_FieldData as F join d_Event as E" +
				" on F.EventID = E.EventID where F.MobileAppID in (select MobileAppID from s_MobileApp" +
				" where parent_app_id = ?) and F.CreationDate is NOT NULL and F.LocationID = ? and E.EventStatus = 1";
		*/

        int ret = 0;
        try {
            ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
            Log.i(TAG, "Deleted Row Count for S_File SiteID:" + siteId + " :" + ret);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int updateS_File_Download_status(String siteId, String fileGuid, String status) {
        // TODO Auto-generated method stub

		/*
        String query = "select F.MobileAppID from d_FieldData as F join d_Event as E" +
				" on F.EventID = E.EventID where F.MobileAppID in (select MobileAppID from s_MobileApp" +
				" where parent_app_id = ?) and F.CreationDate is NOT NULL and F.LocationID = ? and E.EventStatus = 1";
		*/

        int ret = 0;
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_download_status, status);
            String whereClause = KEY_site_id + "=?  AND " + KEY_file_guid + "=?";
            String[] whereArgs = {siteId, fileGuid};

            ret = database.update(DbAccess.TABLE_S_PROJECT_FILE, values, whereClause, whereArgs);
            Log.i(TAG, "Updated File Status for S_File Guid:" + fileGuid + " :" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateS_File_Download_status error:" + e.getMessage());
        }
        return ret;
    }

    public void insertFileList(List<FileDataModel> fileList) {
        long ret = 0;
        database.beginTransaction();
        FileDataModel fileData;
        try {
            for (int i = 0; i < fileList.size(); i++) {
                ContentValues values = new ContentValues();
                fileData = fileList.get(i);
                values.put(KEY_file_id, fileData.getFileID());
                values.put(KEY_file_name, fileData.getFileName());
                values.put(KEY_file_type, fileData.getFileType());
                values.put(KEY_file_status, fileData.getFileStatus());
                values.put(KEY_file_description, fileData.getFileDescription());
                values.put(KEY_file_checksum, fileData.getFileChecksum());
                values.put(KEY_file_guid, fileData.getFileGuid());
                values.put(KEY_site_id, fileData.getSiteId());
                values.put(KEY_file_path, fileData.getFilePath());
                values.put(KEY_created_by, fileData.getCreatedBy());
                values.put(KEY_modified_by, fileData.getModifiedBy());
                values.put(KEY_creation_date, fileData.getCreationDate());
                values.put(KEY_modification_date, fileData.getModificationDate());
                values.put(KEY_notes, fileData.getNotes());
                values.put(KEY_folder_id, fileData.getFolderId());

                ret = database.insert(DbAccess.TABLE_S_PROJECT_FILE, null, values);
                Log.i(TAG, "insertFileList Ret:" + ret);

            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(TAG, "insertFileList Error " + e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
    }


    public void insertTempFileList(Context context, String siteID, List<FileDataModel> fileList) {
        long ret = 0;
        database.beginTransaction();
        FileDataModel fileData;
        String fileLocalPath = Util.getFileFolderDirPath(context, siteID);

        if (fileLocalPath.isEmpty())
            return;

        try {
            for (int i = 0; i < fileList.size(); i++) {
                ContentValues values = new ContentValues();
                fileData = fileList.get(i);
                values.put(KEY_file_id, fileData.getFileID());
                values.put(KEY_file_name, fileData.getFileName());
                values.put(KEY_file_type, fileData.getFileType());
                values.put(KEY_file_status, fileData.getFileStatus());
                values.put(KEY_file_description, fileData.getFileDescription());
                values.put(KEY_file_checksum, fileData.getFileChecksum());
                values.put(KEY_file_guid, fileData.getFileGuid());
                values.put(KEY_site_id, fileData.getSiteId());
                values.put(KEY_file_path, fileLocalPath);  // TODO: 02-Apr-16
                values.put(KEY_created_by, fileData.getCreatedBy());
                values.put(KEY_modified_by, fileData.getModifiedBy());
                values.put(KEY_creation_date, fileData.getCreationDate());
                values.put(KEY_modification_date, fileData.getModificationDate());
                values.put(KEY_notes, fileData.getNotes());
                values.put(KEY_folder_id, fileData.getFolderId());

                ret = database.insert(DbAccess.TABLE_TEMP_PROJECT_FILE, null, values);
                Log.i(TAG, "insertTempFileList Ret ID:" + ret);

            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
    }

//    public void insertFolderList(List<FolderDataModel> folderList) {
//        long ret = 0;
//        database.beginTransaction();
//        FolderDataModel folderData;
//        try {
//            for (int i = 0; i < folderList.size(); i++) {
//                ContentValues values = new ContentValues();
//                folderData = folderList.get(i);
//                values.put(KEY_folder_id, folderData.getId());
//                values.put(KEY_site_id, folderData.getSiteId());
//                values.put(KEY_folder_name, folderData.getFolderName());
//                values.put(KEY_folder_guid, folderData.getFolderGuid());
//                values.put(KEY_parent_id, folderData.getParentID());
//                values.put(KEY_folder_status, folderData.getFolderStatus());
//                values.put(KEY_folder_description, folderData.getFolderDescription());
//
//                values.put(KEY_created_by, folderData.getCreatedBy());
//                values.put(KEY_modified_by, folderData.getModifiedBy());
//                values.put(KEY_creation_date, folderData.getCreationDate());
//
//                ret = database.insert(DbAccess.TABLE_S_PROJECT_FOLDER, null, values);
//                Log.i(TAG, "insertFolderList Ret:" + ret);
//
//            }
//
//            database.setTransactionSuccessful();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "insertFolderList Error" + e.getLocalizedMessage());
//        } finally {
//            database.endTransaction();
//        }
//    }

//    public void insertTempFolderData(FolderDataModel folderData) {
//        long ret = 0;
//        database.beginTransaction();
//        try {
//            ContentValues values = new ContentValues();
//            values.put(KEY_folder_id, folderData.getId());
//            values.put(KEY_site_id, folderData.getSiteId());
//            values.put(KEY_folder_name, folderData.getFolderName());
//            values.put(KEY_folder_guid, folderData.getFolderGuid());
//            values.put(KEY_parent_id, folderData.getParentID());
//            values.put(KEY_folder_status, folderData.getFolderStatus());
//            values.put(KEY_folder_description, folderData.getFolderDescription());
//
//            values.put(KEY_created_by, folderData.getCreatedBy());
//            values.put(KEY_modified_by, folderData.getModifiedBy());
//            values.put(KEY_creation_date, folderData.getCreationDate());
//
//            ret = database.insert(DbAccess.TABLE_TEMP_PROJECT_FOLDER, null, values);
//            Log.i(TAG, "insertTempFolderData Ret:" + ret);
//
//            database.setTransactionSuccessful();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "insertTempFolderData Error" + e.getLocalizedMessage());
//        } finally {
//            database.endTransaction();
//        }
//    }

    public void insertFolderList(List<FolderDataModel> folderList) {
        long ret = 0;
        database.beginTransaction();
        FolderDataModel folderData;
        try {
            for (int i = 0; i < folderList.size(); i++) {
                ContentValues values = new ContentValues();
                folderData = folderList.get(i);
                values.put(KEY_folder_id, folderData.getId());
                values.put(KEY_site_id, folderData.getSiteId());
                values.put(KEY_folder_name, folderData.getFolderName());
                values.put(KEY_folder_guid, folderData.getFolderGuid());
                values.put(KEY_parent_id, folderData.getParentID());
                values.put(KEY_folder_status, folderData.getFolderStatus());
                values.put(KEY_folder_description, folderData.getFolderDescription());

                values.put(KEY_created_by, folderData.getCreatedBy());
                values.put(KEY_modified_by, folderData.getModifiedBy());
                values.put(KEY_creation_date, folderData.getCreationDate());

                ret = database.insert(DbAccess.TABLE_TEMP_PROJECT_FOLDER, null, values);
                ret = database.insert(DbAccess.TABLE_S_PROJECT_FOLDER, null, values);
                Log.i(TAG, "insertFolderList Ret:" + ret);

            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertTempFolderList Error" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
    }

    public List<FileDataModel> selectFileToMove(String fileguid, String siteID) {
        List<FileDataModel> list = new ArrayList<>();
        FileDataModel item;
        String query = null;


        String[] whereArgs = null;
        query = "select * from temp_project_file " +
                "where  " + KEY_file_guid + " like '" + fileguid + "' and " + KEY_site_id + "= ?";
        whereArgs = new String[]{siteID};

        Log.i(TAG, "selectFileListToMove Query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    item = new FileDataModel();
                    int fileid = cursor.getInt(0);
                    String filename = cursor.getString(1);
                    String file_type = cursor.getString(2);
                    int file_status = cursor.getInt(3);
                    String file_desc = cursor.getString(4);
                    String file_checksum = cursor.getString(5);
                    String file_guid = cursor.getString(6);
                    int siteid = cursor.getInt(7);
                    String fpath = cursor.getString(8);
                    int created_by = cursor.getInt(9);
                    String creation_date = cursor.getString(10);
                    int modified_by = cursor.getInt(11);
                    String modification_date = cursor.getString(12);
                    String notes = cursor.getString(13);
                    int folder_id = cursor.getInt(14);

                    item.setFileID(fileid);
                    item.setFileType(file_type);
                    item.setFileStatus(file_status);
                    item.setFileDescription(file_desc);
                    item.setFileChecksum(file_checksum);
                    item.setSiteId(siteid);
                    item.setFilePath(fpath);
                    item.setCreatedBy(created_by);
                    item.setCreationDate(Long.parseLong(creation_date));
                    item.setModifiedBy(modified_by);
                    item.setModificationDate(Long.parseLong(modification_date));
                    item.setNotes(notes);

                    item.setFileGuid(file_guid);
                    item.setFileName(filename);
                    item.setFolderId(folder_id);

                    list.add(item);
                    Log.i(TAG, "File To Move Item Added:" + item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<FileDataModel> getFileListToMove(String siteID) {
        List<FileDataModel> list = new ArrayList<>();
        FileDataModel item;
        String query = null;


        String[] whereArgs = null;
        query = " select * " +
                " from temp_project_file  " +
                " where  (" + KEY_file_guid + ") not in  " +
                " (select " + KEY_file_guid + " from s_project_file where site_id=?) and site_id=?";
        whereArgs = new String[]{siteID, siteID};

        Log.i(TAG, "selectFileListToMove Query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    item = new FileDataModel();
                    int fileid = cursor.getInt(0);
                    String filename = cursor.getString(1);
                    String file_type = cursor.getString(2);
                    int file_status = cursor.getInt(3);
                    String file_desc = cursor.getString(4);
                    String file_checksum = cursor.getString(5);
                    String file_guid = cursor.getString(6);
                    int siteid = cursor.getInt(7);
                    String fpath = cursor.getString(8);
                    int created_by = cursor.getInt(9);
                    String creation_date = cursor.getString(10);
                    int modified_by = cursor.getInt(11);
                    String modification_date = cursor.getString(12);
                    String notes = cursor.getString(13);
                    int folder_id = cursor.getInt(14);

                    item.setFileID(fileid);
                    item.setFileType(file_type);
                    item.setFileStatus(file_status);
                    item.setFileDescription(file_desc);
                    item.setFileChecksum(file_checksum);
                    item.setSiteId(siteid);
                    item.setFilePath(fpath);
                    item.setCreatedBy(created_by);
                    item.setCreationDate(Long.parseLong(creation_date));
                    item.setModifiedBy(modified_by);
                    item.setModificationDate(Long.parseLong(modification_date));
                    item.setNotes(notes);

                    item.setFileGuid(file_guid);
                    item.setFileName(filename);
                    item.setFolderId(folder_id);

                    list.add(item);
                    Log.i(TAG, "File To Move Item Added:" + item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    //25-Feb-16 Get Hashmap<fileGuid,filename>
    public ArrayList<downloadFileModel> getFileListToDownload(String siteID, String uGuid) {

        ArrayList<downloadFileModel> downloadFileList = new ArrayList<>();

        downloadFileModel item;
        String fileguid = "";
        String filename = "";


        String query = null;
        String[] whereArgs = null;


        Log.i(TAG, "getFileListToDownload siteID:" + siteID);


/*        query = "select file_guid,file_name " +
                " from temp_project_file  " +
                " where  (file_checksum) not in  " +
             " (select file_checksum from s_project_file where site_id=?) and site_id=?";
              */

        //21-Jun-16
        query = "select file_guid,file_name " +
                " from s_project_file where site_id=? and download_status=?";

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.Q){
            whereArgs = new String[]{siteID, "0"};  //Android 11+
        }else {
            whereArgs = new String[]{siteID, "FALSE"}; //Android 10 and below
        }


        Log.i(TAG, "getFileListToDownload Query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    item = new downloadFileModel();
                    fileguid = cursor.getString(0);
                    filename = cursor.getString(1);
                    item.setFileGuid(fileguid);
                    item.setFileName(filename);
                    item.setSiteId(siteID);
                    item.setUserGuid(uGuid);

                    if ((fileguid != null && !fileguid.isEmpty()) && (filename != null && !filename.isEmpty())) {
                        downloadFileList.add(item);
                    }
                    Log.i(TAG, "File Download Item Added:" + item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return downloadFileList;
    }

    public String removeRowFrom_sFile(String siteID) {


        String filepath = "";
        String filename = "";
        String fileguid = "";


        String query = null;
        String[] whereArgs = null;


        Log.i(TAG, "deleteRowFrom_sFile siteID:" + siteID);


        query = "select file_path,file_name,file_guid  " +
                "from s_project_file  " +
                "where  (file_checksum) not in  " +
                "(select file_checksum from temp_project_file where site_id=?) and site_id=?";
        whereArgs = new String[]{siteID, siteID};

        Log.i(TAG, "deleteRowFrom_sFile Query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    // item = new HashMap<String, String>();
                    filepath = cursor.getString(0);
                    filename = cursor.getString(1);
                    fileguid = cursor.getString(2);
                    // TODO: 28-Feb-16  Delete image from device first
                    if ((filepath != null && !filepath.isEmpty()) && (filename != null && !filename.isEmpty())) {
                        if (Util.deleteFile_Folder(filepath, fileguid)) {
                            return "Rows Deleted:" + deleteRow_sFile(fileguid);
                        }

                    }
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());

        }

        return "";
    }

    public int deleteRow_sFile(String fileGuid) {
        int ret = 0;
        String whereClause = "file_guid=?";
        String[] whereArgs = new String[]{fileGuid};
        try {
            ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteRow_sFile Error:" + e.getMessage());
        }
        return ret;
    }

    public ArrayList<FileFolderItem> getHomeFileFolderItemList(String siteID) {
        ArrayList<FileFolderItem> ffList = new ArrayList<FileFolderItem>();

        String query = null;
        String queryfile = null;
        String[] whereArgs = null;
        String[] whereArgsfile = null;

//		String query = "Select DISTINCT("+KEY_FieldParameterLabel+") from "+DbAccess.TABLE_FIELD_DATA;

        Log.i(TAG, "getHomeFileFolderList folderID= NULL, siteID:" + siteID);

        // TODO: 28-Jan-16  Collect Folder/s
        query = "Select folder_name,folder_id from s_project_folder where site_id=? and parent_id=" + 0;//parent id=0
        whereArgs = new String[]{siteID};


        Log.i(TAG, "getHomeFileFolderItemList Query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FileFolderItem item = new FileFolderItem();
                    String f_name = cursor.getString(0);
                    String f_id = cursor.getString(1);
                    item.setItemID(f_id);
                    item.setItemTitle(f_name);
                    item.setItemType("folder");

                    ffList.add(item);
                    Log.i(TAG, "Folder Item Added:" + item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: 28-Jan-16  Collect File/s
        queryfile = "Select file_name,file_id,file_guid from s_project_file where site_id=? and folder_id=0";
        whereArgsfile = new String[]{siteID};

        Log.i(TAG, "getSubFileFolderItemList Querry=" + query);
        Cursor cursorfile = null;
        try {
            cursorfile = database.rawQuery(queryfile, whereArgsfile);
            if (cursorfile != null && cursorfile.moveToFirst()) {
                do {
                    FileFolderItem item = new FileFolderItem();
                    String f_name = cursorfile.getString(0);
                    String f_id = cursorfile.getString(1);
                    String f_guid = cursorfile.getString(2);
                    item.setItemID(f_id);
                    item.setItemTitle(f_name);
                    item.setItemGuid(f_guid);
                    item.setItemType("file");
                    ffList.add(item);
                    Log.i(TAG, "File Item Added:" + item);
                } while (cursorfile.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        if (cursorfile != null) {
            cursorfile.close();
        }


        return ffList;
    }

    public ArrayList<FileFolderItem> getSubFileFolderItemList(String folderID, String siteID) {
        ArrayList<FileFolderItem> ffList = new ArrayList<FileFolderItem>();


        String query = null;
        String queryfile = null;
        String[] whereArgs = null;
        String[] whereArgsfile = null;

//		String query = "Select DISTINCT("+KEY_FieldParameterLabel+") from "+DbAccess.TABLE_FIELD_DATA;

        Log.i(TAG, "getSubFileFolderList folderID= " + folderID + " siteID:" + siteID);

        // TODO: 28-Jan-16  Collect Folder/s
        query = "Select folder_name,folder_id from s_project_folder where site_id=? and parent_id=?";
        whereArgs = new String[]{siteID, folderID};


        Log.i(TAG, "getSubFileFolderItemList Querry=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FileFolderItem item = new FileFolderItem();
                    String f_name = cursor.getString(0);
                    String f_id = cursor.getString(1);
                    item.setItemID(f_id);
                    item.setItemTitle(f_name);
                    item.setItemType("folder");
                    ffList.add(item);
                    Log.i(TAG, "Sub Folder Added:" + item);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: 28-Jan-16  Collect File/s
        queryfile = "Select file_name,file_id,file_guid from s_project_file where site_id=? and folder_id=?";
        whereArgsfile = new String[]{siteID, folderID};

        Log.i(TAG, "getSubFileFolderItemList Query=" + queryfile);
        Cursor cursorfile = null;
        try {
            cursorfile = database.rawQuery(queryfile, whereArgsfile);
            if (cursorfile != null && cursorfile.moveToFirst()) {
                do {
                    FileFolderItem item = new FileFolderItem();
                    String f_name = cursorfile.getString(0);
                    String f_id = cursorfile.getString(1);
                    String f_guid = cursorfile.getString(2);

                    item.setItemID(f_id);
                    item.setItemTitle(f_name);
                    item.setItemGuid(f_guid);
                    item.setItemType("file");
                    ffList.add(item);
                    Log.i(TAG, "Sub File Added:" + item);

                } while (cursorfile.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        if (cursorfile != null) {
            cursorfile.close();
        }

        return ffList;


    }

    public ArrayList<FileFolderItem> getKMZList(String siteID) {
        ArrayList<FileFolderItem> ffList = new ArrayList<FileFolderItem>();


        String query = null;
        String queryfile = null;
        String[] whereArgs = null;
        String[] whereArgsfile = null;

        // TODO: 22-Jun-16 KMZ LIST
        queryfile = "Select file_name,file_id," +
                "file_guid,file_path from s_project_file where site_id=?";
        whereArgsfile = new String[]{siteID};

        Log.i(TAG, "get KMZ File List Query=" + queryfile);
        Cursor cursorfile = null;
        try {
            cursorfile = database.rawQuery(queryfile, whereArgsfile);
            if (cursorfile != null && cursorfile.moveToFirst()) {
                do {
                    FileFolderItem item = new FileFolderItem();
                    String f_name = cursorfile.getString(0);
                    String f_id = cursorfile.getString(1);
                    String f_guid = cursorfile.getString(2);
                    String f_path = cursorfile.getString(3);

                    if (f_guid.contains(".kmz")) {
                        if (new File(f_path + File.separator + f_guid).exists()) {
                            item.setItemID(f_id);
                            item.setItemTitle(f_name);
                            item.setItemGuid(f_guid);
                            item.setItemType("file");
                            item.setItemPath(f_path);
                            ffList.add(item);
                            Log.i(TAG, "KMZ File Added:" + item);
                        }

                    }


                } while (cursorfile.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursorfile != null) {
            cursorfile.close();
        }

        return ffList;


    }

    public List<FileDataModel> getFileList(String folderId, String siteID) {
        List<FileDataModel> list = new ArrayList<>();
        FileDataModel item;
        String query = null;


        query = "select * from s_project_file " +
                "where  " + KEY_folder_id + " ="+ folderId+" and  "+ KEY_site_id + "= "+siteID;
        //String[] whereArgs = new String[]{folderId,siteID};

        Log.i(TAG, "selectFileListToMove Query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    item = new FileDataModel();
                    int fileid = cursor.getInt(0);
                    String filename = cursor.getString(1);
                    String file_type = cursor.getString(2);
                    int file_status = cursor.getInt(3);
                    String file_desc = cursor.getString(4);
                    String file_checksum = cursor.getString(5);
                    String file_guid = cursor.getString(6);
                    int siteid = cursor.getInt(7);
                    String fpath = cursor.getString(8);
                    int created_by = cursor.getInt(9);
                    String creation_date = cursor.getString(10);
                    int modified_by = cursor.getInt(11);
                    String modification_date = cursor.getString(12);
                    String notes = cursor.getString(13);
                    //int downloadStatus = cursor.getInt(14);
                    int folder_id = cursor.getInt(15);

                    item.setFileID(fileid);
                    item.setFileType(file_type);
                    item.setFileStatus(file_status);
                    item.setFileDescription(file_desc);
                    item.setFileChecksum(file_checksum);
                    item.setSiteId(siteid);
                    item.setFilePath(fpath);
                    item.setCreatedBy(created_by);

                    if (creation_date!=null){
                        item.setCreationDate(Long.parseLong(creation_date));
                    }else {
                        item.setCreationDate(System.currentTimeMillis());
                    }
//                    item.setModifiedBy(modified_by);
//                    item.setModificationDate(Long.parseLong(modification_date));
//                    item.setModificationDate(Long.parseLong(modification_date));
//                    item.setNotes(notes);

                    item.setFileGuid(file_guid);
                    item.setFileName(filename);
                    item.setFolderId(folder_id);

                    list.add(item);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFileList: "+e.getMessage() );
        }

        return list;
    }

}
