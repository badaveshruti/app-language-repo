package qnopy.com.qnopyandroid.TaskDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.TaskModelClasses.TaskAttachments;
import qnopy.com.qnopyandroid.TaskModelClasses.TaskDataList;
import qnopy.com.qnopyandroid.db.DbAccess;

public class AttachmentTaskDataSource {

    final String Key_TaskAttachmentId = "taskAttachmentId";
    final String Key_TaskId = "taskId";
    final String Key_FileName = "fileName";
    final String Key_FileExtension = "fileExtension";
    final String Key_FileKey = "fileKey";
    final String Key_AttachmentDescription = "attachmentDescription";
    final String Key_CommentId = "commentId";
    final String Key_DisplayFlag = "displayFlag";
    final String Key_Latitude = "latitude";
    final String Key_Longitude = "longitude";
    final String Key_CreatedBy = "createdBy";
    final String Key_CreationDate = "creationDate";
    final String Key_ModifiedBy = "modifiedBy";
    final String Key_ModificationDate = "modificationDate";
    final String Key_MediaUploadStatus = "mediaUploadStatus";
    final String Key_DataSyncFlag = "dataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public AttachmentTaskDataSource(Context mContext) {
        this.mContext = mContext;
        database = DbAccess.getInstance(mContext).database;
        if (database == null) {
            DbAccess.getInstance(mContext).open();
            database = DbAccess.getInstance(mContext).database;
        }
    }

    public void storeTaskAttachmentDataToDB(ArrayList<TaskAttachments> mArrayListTaskAttachments) {
        database.beginTransaction();
        try {

            for (TaskAttachments t : mArrayListTaskAttachments){
                ContentValues contentValues = new ContentValues();

                contentValues.put(Key_TaskAttachmentId, t.getTaskAttachmentId());
                contentValues.put(Key_TaskId, t.getTaskId());
                contentValues.put(Key_FileName, t.getFileName());
                contentValues.put(Key_FileKey, t.getFileKey());
                contentValues.put(Key_AttachmentDescription, t.getAttachmentDescription());
                contentValues.put(Key_CommentId, t.getCommentId());
                contentValues.put(Key_DisplayFlag, t.getDisplayFlag());
                contentValues.put(Key_Latitude, t.getLatitude());
                contentValues.put(Key_Longitude, t.getLongitude());
                contentValues.put(Key_CreatedBy, t.getCreatedBy());
                contentValues.put(Key_CreationDate, t.getCreationDate());
                contentValues.put(Key_ModifiedBy, t.getModifiedBy());
                contentValues.put(Key_ModificationDate, t.getModificationDate());
                contentValues.put(Key_MediaUploadStatus, t.getMediaUploadStatus());

                database.insert("TaskDataAttachmentList", null, contentValues);
            }
            database.setTransactionSuccessful();
            Log.e("storeTaskData", "storeTaskAttachmentDataToDB: Attachment data store to DB successfully");
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("storeTaskData", "storeTaskAttachmentDataToDB:"+e.getMessage());
        } finally {
            database.endTransaction();
        }
    }

    public ArrayList<TaskAttachments> getAllAttachmentsForTask(Integer taskId) {
        ArrayList<TaskAttachments> attachments = new ArrayList<>();

        String query = "select taskAttachmentId, taskId, fileName, fileKey, attachmentDescription, commentId, displayFlag, latitude," +
                "longitude, createdBy, creationDate, modifiedBy, modificationDate, mediaUploadStatus from TaskDataAttachmentList where taskId = "+taskId; //+" and dataSyncFlag = 0 or null"

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);
        try {

            if (cursor != null && cursor.moveToFirst()){

                do {

                    TaskAttachments taskAttachments = new TaskAttachments();

                    taskAttachments.setTaskAttachmentId(cursor.getInt(0));
                    taskAttachments.setTaskId(cursor.getInt(1));
                    taskAttachments.setFileName(cursor.getString(2));
                    taskAttachments.setFileKey(cursor.getString(3));
                    taskAttachments.setAttachmentDescription(cursor.getString(4));
                    taskAttachments.setCommentId(cursor.getInt(5));
                    taskAttachments.setDisplayFlag(cursor.getInt(6));
                    taskAttachments.setLatitude(cursor.getDouble(7));
                    taskAttachments.setLongitude(cursor.getDouble(8));
                    taskAttachments.setCreatedBy(cursor.getInt(9));
                    taskAttachments.setCreationDate(cursor.getLong(10));
                    taskAttachments.setModifiedBy(cursor.getInt(11));
                    taskAttachments.setModificationDate(cursor.getLong(12));

                    attachments.add(taskAttachments);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return attachments;
    }

    public int getAttachmentCountForTask(Integer taskId) {
        int count = 0;

        try {
            String countQuery = "SELECT  * FROM TaskDataAttachmentList WHERE taskId = "+taskId;
            Cursor cursor = database.rawQuery(countQuery, null);
            count = cursor.getCount();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return count;
    }

    public void deleteAlreadyExistingAttachments(ArrayList<TaskAttachments> mArrayListTaskAttachments) {

    }

    public void insertNewAttachmentToDatabase(int randomAttachmentId, Integer taskId, String mAttachmentName, String fileKey,
                                              String attachmentDesc, int commentId, int displayFlag, Double latitude, Double longitude,
                                              int createdBy, long creationDate, int modifiedBy, int modificationDate) {

        database.beginTransaction();

        try {

            ContentValues contentValues = new ContentValues();

            contentValues.put(Key_TaskAttachmentId, randomAttachmentId);
            contentValues.put(Key_TaskId, taskId);
            contentValues.put(Key_FileName, mAttachmentName);
            contentValues.put(Key_FileExtension, "");
            contentValues.put(Key_FileKey, fileKey);
            contentValues.put(Key_AttachmentDescription, "");
            contentValues.put(Key_CommentId, commentId);
            contentValues.put(Key_DisplayFlag, displayFlag);
            contentValues.put(Key_Latitude, latitude);
            contentValues.put(Key_Longitude, longitude);
            contentValues.put(Key_CreatedBy, createdBy);
            contentValues.put(Key_CreationDate, creationDate);
            contentValues.put(Key_ModifiedBy, modifiedBy);
            contentValues.put(Key_ModificationDate, modificationDate);
            contentValues.put(Key_MediaUploadStatus, "");
            contentValues.put(Key_DataSyncFlag, 0);

            database.insert("TaskDataAttachmentList", null, contentValues);
            database.setTransactionSuccessful();

            Log.e("newAttachment", "insertNewAttachmentToDatabase: new attachment stored successfully");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("newAttachment", "insertNewAttachmentToDatabase: new attachment does not stored ");
        }finally {
            database.endTransaction();
        }

    }

    public ArrayList<TaskAttachments> getNewAttachmentFromDB() {
        ArrayList<TaskAttachments> newAttachments = new ArrayList<>();

        String query = "select taskAttachmentId, taskId, fileName, fileKey, attachmentDescription, commentId, displayFlag, latitude," +
                "longitude, createdBy, creationDate, modifiedBy, modificationDate, mediaUploadStatus from TaskDataAttachmentList where dataSyncFlag = 0";

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);
        try {

            if (cursor != null && cursor.moveToFirst()){

                do {

                    TaskAttachments taskAttachments = new TaskAttachments();

                    taskAttachments.setTaskAttachmentId(cursor.getInt(0));
                    taskAttachments.setTaskId(cursor.getInt(1));
                    taskAttachments.setFileName(cursor.getString(2));
                    taskAttachments.setFileKey(cursor.getString(3));
                    taskAttachments.setAttachmentDescription(cursor.getString(4));
                    taskAttachments.setCommentId(cursor.getInt(5));
                    taskAttachments.setDisplayFlag(cursor.getInt(6));
                    taskAttachments.setLatitude(cursor.getDouble(7));
                    taskAttachments.setLongitude(cursor.getDouble(8));
                    taskAttachments.setCreatedBy(cursor.getInt(9));
                    taskAttachments.setCreationDate(cursor.getLong(10));
                    taskAttachments.setModifiedBy(cursor.getInt(11));
                    taskAttachments.setModificationDate(cursor.getLong(12));

                    newAttachments.add(taskAttachments);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return newAttachments;
    }

    public void updateDataSyncFlag(Integer taskId, String fileName) {

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Key_DataSyncFlag, 1);

            String whereClause = "taskId = ? and fileName = ?";
            String[] whereArgs = new String[]{"" + taskId, fileName};

            database.update("TaskDataAttachmentList", contentValues, whereClause, whereArgs);
            Log.e("DSFA", "updateDataSyncFlag: UPDATED SUCCESSFULLY");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("DSFA", "updateDataSyncFlag: DOES NOT UPDATED SUCCESSFULLY");
        }
    }
}
