package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class TaskCommentsDataSource {

    private static final String TASK_COMMENTS_ID = "task_comments_id";
    private static final String TASK_ID = "task_id";
    private static final String CLIENT_TASK_COMMENT_ID = "clientTaskCommentId";
    private static final String COMMENT = "comment";
    private static final String IS_ATTACHMENT = "is_attachment";
    private static final String CREATED_BY = "created_by";
    private static final String CREATION_DATE = "creation_date";
    private static final String MODIFIED_BY = "modified_by";
    private static final String MODIFICATION_DATE = "modification_date";
    private static final String SERVER_CREATION_DATE = "server_creation_date";
    private static final String SERVER_MODIFICATION_DATE = "server_modification_date";
    private static final String DATA_SYNC_FLAG = "data_sync_flag";
    private static final String TAG = "TaskCommentSource";
    private final Context mContext;
    private SQLiteDatabase database;

    @Inject
    public TaskCommentsDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        SQLiteDatabase.releaseMemory();
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public long insertTaskComments(List<TaskDataResponse.CommentList> commentLists, int dataSyncFlag) {
        long ret = 0;

        database.beginTransaction();
        TaskDataResponse.CommentList commentData;

        try {

            for (int i = 0; i < commentLists.size(); i++) {
                ContentValues values = new ContentValues();
                commentData = commentLists.get(i);
                values.put(TASK_ID, commentData.getTaskId());
                values.put(TASK_COMMENTS_ID, commentData.getTaskCommentId());
                values.put(COMMENT, commentData.getComment());
                values.put(IS_ATTACHMENT, commentData.isAttachment());
                values.put(CREATED_BY, commentData.getCreatedBy());
                values.put(CREATION_DATE, commentData.getCreationDate());
                values.put(MODIFIED_BY, commentData.getModifiedBy());
                values.put(MODIFICATION_DATE, commentData.getModificationDate());
                values.put(CLIENT_TASK_COMMENT_ID, commentData.getTaskCommentId());
                values.put(DATA_SYNC_FLAG, dataSyncFlag);

                ret = database.insert(DbAccess.TABLE_TASK_COMMENTS, null, values);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeBulkBindTaskComments(List<TaskDataResponse.CommentList> commentLists, int dataSyncFlag) {
        long ret = 0;

        String[] arrColumns = {TASK_ID, TASK_COMMENTS_ID, COMMENT, IS_ATTACHMENT, CREATED_BY,
                CREATION_DATE, MODIFIED_BY, MODIFICATION_DATE, CLIENT_TASK_COMMENT_ID, DATA_SYNC_FLAG};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_TASK_COMMENTS + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {

            for (TaskDataResponse.CommentList commentData : commentLists) {
                if (commentData.getComment() != null)
                    statement.bindLong(1, commentData.getTaskId());
                else statement.bindNull(1);

                if (commentData.getComment() != null)
                    statement.bindLong(2, commentData.getTaskCommentId());
                else statement.bindNull(2);

                if (commentData.getComment() != null)
                    statement.bindString(3, commentData.getComment());
                else
                    statement.bindNull(3);

                if (commentData.getComment() != null)
                    statement.bindLong(4, commentData.isAttachment());
                else statement.bindNull(4);

                if (commentData.getComment() != null)
                    statement.bindLong(5, commentData.getCreatedBy());
                else statement.bindNull(5);

                if (commentData.getComment() != null)
                    statement.bindLong(6, commentData.getCreationDate());
                else statement.bindNull(6);

                if (commentData.getModifiedBy() != null)
                    statement.bindString(7, commentData.getModifiedBy());
                else statement.bindNull(7);

                if (commentData.getModificationDate() != null)
                    statement.bindString(8, commentData.getModificationDate());
                else statement.bindNull(8);

                if (commentData.getComment() != null)
                    statement.bindLong(9, commentData.getTaskCommentId());
                else statement.bindNull(8);

                if (commentData.getComment() != null)
                    statement.bindLong(10, dataSyncFlag);
                else statement.bindNull(8);

                ret = statement.executeInsert();
                statement.clearBindings();
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public ArrayList<TaskDataResponse.CommentList> getAllComments(int taskId) {
        Cursor cursor = null;
        ArrayList<TaskDataResponse.CommentList> commentList = new ArrayList<>();

        String query = "select distinct task_comments_id, task_id, comment," +
                " creation_date, modification_date, created_by, modified_by," +
                " data_sync_flag from w_task_comments where task_id = ? ";

        try {
            cursor = database.rawQuery(query, new String[]{taskId + ""});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.CommentList commentDetails = new TaskDataResponse.CommentList();
                    commentDetails.setTaskCommentId(cursor.getInt(0));
                    commentDetails.setTaskId(cursor.getInt(1));
                    commentDetails.setComment(cursor.getString(2));
                    commentDetails.setCreationDate(Math.max(cursor.getLong(3),
                            cursor.getLong(4)));
                    commentDetails.setCreatedBy(cursor.getInt(5));
                    commentDetails.setModifiedBy(cursor.getString(6));
                    commentList.add(commentDetails);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return commentList;
    }

    public ArrayList<TaskDataResponse.CommentList> getAllUnSyncedComments(String taskId) {
        Cursor cursor = null;
        ArrayList<TaskDataResponse.CommentList> commentList = new ArrayList<>();

        String query = "select distinct task_comments_id, task_id, comment," +
                " creation_date, modification_date, created_by, modified_by," +
                " data_sync_flag from w_task_comments where data_sync_flag = 0";

        if (!taskId.isEmpty()) {
            query = query + " and task_id = ?";
            cursor = database.rawQuery(query, new String[]{taskId});
        } else {
            cursor = database.rawQuery(query, null);
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.CommentList commentDetails = new TaskDataResponse.CommentList();
                    commentDetails.setTaskCommentId(cursor.getInt(0));
                    commentDetails.setTaskId(cursor.getInt(1));
                    commentDetails.setComment(cursor.getString(2));
                    commentDetails.setCreationDate(cursor.getLong(3));
//                    commentDetails.setModificationDate(cursor.getString(4));
                    commentDetails.setCreatedBy(cursor.getInt(5));
//                    commentDetails.setModifiedBy(cursor.getString(6));
                    commentList.add(commentDetails);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return commentList;
    }

    public boolean updateIdAndSyncFlag(String commentId, String taskId, String clientCommentId) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;

        ContentValues values = new ContentValues();
        values.put(TASK_COMMENTS_ID, commentId);
        values.put(DATA_SYNC_FLAG, 1);
        values.put(TASK_ID, taskId);

        whereClause = TASK_COMMENTS_ID + "=?";
        whereArgs = new String[]{clientCommentId};
        try {
            ret = database.update(DbAccess.TABLE_TASK_COMMENTS, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret > 0;
    }
}
