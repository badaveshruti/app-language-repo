package qnopy.com.qnopyandroid.TaskDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.TaskModelClasses.TaskComments;
import qnopy.com.qnopyandroid.TaskModelClasses.TaskDataList;
import qnopy.com.qnopyandroid.db.DbAccess;

public class CommentTaskDataSource {

    final String Key_TaskCommentId = "taskCommentId";
    final String Key_TaskId = "taskId";
    final String Key_Comment = "comment";
    final String Key_IsAttachment = "isAttachment";
    final String Key_CreatedBy = "createdBy";
    final String Key_CreationDate = "creationDate";
    final String Key_ModifiedBy = "modifiedBy";
    final String Key_ModificationDate = "modificationDate";
    final String Key_DataSyncFlag = "dataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public CommentTaskDataSource(Context mContext) {
        this.mContext = mContext;
        database = DbAccess.getInstance(mContext).database;
        if (database == null) {
            DbAccess.getInstance(mContext).open();
            database = DbAccess.getInstance(mContext).database;
        }
    }

    public void storeTaskCommentsDataToDB(ArrayList<TaskComments> mArrayListTaskComments) {
        database.beginTransaction();
        try {

            for (TaskComments t : mArrayListTaskComments){
                ContentValues contentValues = new ContentValues();

                contentValues.put(Key_TaskCommentId, t.getTaskCommentId());
                contentValues.put(Key_TaskId, t.getTaskId());
                contentValues.put(Key_Comment, t.getComment());
                contentValues.put(Key_IsAttachment, t.getIsAttachment());
                contentValues.put(Key_CreatedBy, t.getCreatedBy());
                contentValues.put(Key_CreationDate, t.getCreationDate());
                contentValues.put(Key_ModifiedBy, t.getModifiedBy());
                contentValues.put(Key_ModificationDate, t.getModificationDate());

                database.insert("TaskDataCommentList", null, contentValues);
            }
            database.setTransactionSuccessful();
            Log.e("storeTaskData", "storeTaskCommentsDataToDB: Comments data store to DB successfully");
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("storeTaskData", "storeTaskCommentsDataToDB:"+e.getMessage());
        } finally {
            database.endTransaction();
        }
    }

    public ArrayList<TaskComments> getAllCommentsForTask(Integer taskId) {
        String taskID = String.valueOf(taskId);
        ArrayList<TaskComments> arrayListCommentsForTask = new ArrayList<>();

        String query = "select taskCommentId, taskId, comment, isAttachment, createdBy, creationDate, modifiedBy, modificationDate " +
                "from TaskDataCommentList where taskId = "+taskId;

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);

        try {

            if (cursor != null && cursor.moveToFirst()){

                do {

                    TaskComments taskComments = new TaskComments();
                    taskComments.setTaskCommentId(cursor.getInt(0));
                    taskComments.setTaskId(cursor.getInt(1));
                    taskComments.setComment(cursor.getString(2));
                    taskComments.setIsAttachment(cursor.getInt(3));
                    taskComments.setCreatedBy(cursor.getInt(4));
                    taskComments.setCreationDate(cursor.getLong(5));
                    taskComments.setModifiedBy(cursor.getInt(6));
                    taskComments.setModificationDate(cursor.getLong(7));

                    arrayListCommentsForTask.add(taskComments);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayListCommentsForTask;
    }

    public void deleteAlreadyExistingComments(ArrayList<TaskComments> mArrayListTaskComments) {

    }

    public void insertNewCommentTODB(long taskCommentId, Integer taskId, String comment, int isAttachment, String createdBy,
                                     long creationDate, int modifiedBy, int modificationDate) {
        int ret = 0;
        database.beginTransaction();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Key_TaskCommentId, taskCommentId);
            contentValues.put(Key_TaskId, taskId);
            contentValues.put(Key_Comment, comment);
            contentValues.put(Key_IsAttachment, isAttachment);
            contentValues.put(Key_CreatedBy, createdBy);
            contentValues.put(Key_CreationDate, creationDate);
            contentValues.put(Key_ModifiedBy, modifiedBy);
            contentValues.put(Key_ModificationDate, modificationDate);
            contentValues.put(Key_DataSyncFlag, 0);

            database.insert("TaskDataCommentList", null, contentValues);
            database.setTransactionSuccessful();
            Log.e("newComment", "insertNewCommentTODB: NEW COMMENT INSERTED SUCCESSFULLY");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("newComment", "insertNewCommentTODB: FAIL TO INSERT NEW COMMENT");
        }finally {
            database.endTransaction();
        }
    }

    public ArrayList<TaskComments> getNewCommentFromDB() {
        ArrayList<TaskComments>  arrayListNewComments = new ArrayList<>();

        String query = "select taskCommentId, taskId, comment, isAttachment, createdBy, creationDate, modifiedBy, modificationDate " +
                "from TaskDataCommentList where dataSyncFlag = 0";

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);

        try {

            if (cursor != null && cursor.moveToFirst()){

                do {

                    TaskComments taskComments = new TaskComments();
                    taskComments.setTaskCommentId(cursor.getInt(0));
                    taskComments.setTaskId(cursor.getInt(1));
                    taskComments.setComment(cursor.getString(2));
                    taskComments.setIsAttachment(cursor.getInt(3));
                    taskComments.setCreatedBy(cursor.getInt(4));
                    taskComments.setCreationDate(cursor.getLong(5));
                    taskComments.setModifiedBy(cursor.getInt(6));
                    taskComments.setModificationDate(cursor.getLong(7));

                    arrayListNewComments.add(taskComments);

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayListNewComments;
    }

    public void updateTaskCommentIdAndDataSyncFlag(int serverGeneratedTaskCommentId, int taskID) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Key_DataSyncFlag, 1);

        String whereClause = "taskId = ?";
        String[] whereArgs = new String[]{"" + taskID};

        try {
            database.update("TaskDataCommentList", contentValues, whereClause, whereArgs);
            Log.e("DSFC", "updateTaskCommentIdAndDataSyncFlag: DATA SYNC FLAG UPDATED FOR TASK ID - "+taskID);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("DSFC", "updateTaskCommentIdAndDataSyncFlag: DATA SYNC FLAG IS NOT UPDATED FOR TASK ID - "+taskID);
        }
    }

    public int getCommentCount(Integer taskId) {
        int commentCount = 0;
        
        try {
            String countQuery = "SELECT  * FROM TaskDataCommentList WHERE taskId = "+taskId;
            Cursor cursor = database.rawQuery(countQuery, null);
            commentCount = cursor.getCount();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return commentCount;
    }
}
