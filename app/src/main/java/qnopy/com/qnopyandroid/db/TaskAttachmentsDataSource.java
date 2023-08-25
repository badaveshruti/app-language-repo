package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class TaskAttachmentsDataSource {

    private static final String TASK_ATTACHMENT_ID = "task_attachments_id";
    private static final String TASK_ID = "task_id";
    private static final String FILE_NAME = "file_name";
    private static final String FILE_KEY = "file_key";
    private static final String ATTACHMENT_DESCRIPTION = "attachment_description";
    private static final String COMMENT_ID = "comment_id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String DISPLAY_FLAG = "display_flag";
    private static final String CREATED_BY = "created_by";
    private static final String CREATION_DATE = "creation_date";
    private static final String MODIFIED_BY = "modified_by";
    private static final String MODIFICATION_DATE = "modification_date";
    private static final String SERVER_CREATION_DATE = "server_creation_date";
    private static final String SERVER_MODIFICATION_DATE = "server_modification_date";
    private static final String DATA_SYNC_FLAG = "data_sync_flag";
    private static final String TAG = "TaskAttachmentSource";
    private Context mContext;
    private SQLiteDatabase database;

    @Inject
    public TaskAttachmentsDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        SQLiteDatabase.releaseMemory();
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public long insertAttachmentData(List<TaskDataResponse.AttachmentList> attachmentLists,
                                     int dataSyncFlag) {
        long ret = 0;

        database.beginTransaction();
        TaskDataResponse.AttachmentList attachmentData;

        try {

            for (int i = 0; i < attachmentLists.size(); i++) {
                ContentValues values = new ContentValues();
                attachmentData = attachmentLists.get(i);
                values.put(TASK_ID, attachmentData.getTaskId());
                values.put(TASK_ATTACHMENT_ID, attachmentData.getTaskAttachmentId());
                values.put(FILE_NAME, attachmentData.getFileName());
                values.put(FILE_KEY, attachmentData.getFileKey());
                values.put(ATTACHMENT_DESCRIPTION, attachmentData.getAttachmentDescription());
                values.put(COMMENT_ID, attachmentData.getCommentId());
                values.put(LATITUDE, attachmentData.getLatitude());
                values.put(LONGITUDE, attachmentData.getLongitude());
                values.put(DISPLAY_FLAG, attachmentData.getDisplayFlag());
                values.put(CREATED_BY, attachmentData.getCreatedBy());
                values.put(CREATION_DATE, attachmentData.getCreationDate());
                values.put(MODIFIED_BY, attachmentData.getModifiedBy());
                values.put(MODIFICATION_DATE, attachmentData.getModificationDate());
                values.put(DATA_SYNC_FLAG, dataSyncFlag);

                ret = database.insert(DbAccess.TABLE_TASK_ATTACHMENTS, null, values);
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

    public long storeBulkBindAttachmentData(List<TaskDataResponse.AttachmentList> attachmentLists,
                                            int dataSyncFlag) {
        long ret = 0;

        String[] arrColumns = {TASK_ID, TASK_ATTACHMENT_ID, FILE_NAME, FILE_KEY,
                ATTACHMENT_DESCRIPTION, COMMENT_ID, LATITUDE, LONGITUDE,
                DISPLAY_FLAG, CREATED_BY, CREATION_DATE, MODIFIED_BY, MODIFICATION_DATE,
                DATA_SYNC_FLAG};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_TASK_ATTACHMENTS + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {
            for (TaskDataResponse.AttachmentList attachmentData : attachmentLists) {
                statement.bindLong(1, attachmentData.getTaskId());
                statement.bindLong(2, attachmentData.getTaskAttachmentId());

                if (attachmentData.getFileName() != null)
                    statement.bindString(3, attachmentData.getFileName());
                else statement.bindNull(3);

                if (attachmentData.getFileKey() != null)
                    statement.bindString(4, attachmentData.getFileKey());
                else statement.bindNull(4);

                if (attachmentData.getAttachmentDescription() != null)
                    statement.bindString(5, attachmentData.getAttachmentDescription());
                else
                    statement.bindNull(5);

                statement.bindLong(6, attachmentData.getCommentId());
                statement.bindDouble(7, attachmentData.getLatitude());
                statement.bindDouble(8, attachmentData.getLongitude());
                statement.bindLong(9, attachmentData.getDisplayFlag());
                statement.bindLong(10, attachmentData.getCreatedBy());
                statement.bindLong(11, attachmentData.getCreationDate());

                if (attachmentData.getModifiedBy() != null)
                    statement.bindString(12, attachmentData.getModifiedBy());
                else statement.bindNull(12);

                if (attachmentData.getModificationDate() != null)
                    statement.bindString(13, attachmentData.getModificationDate());
                else statement.bindNull(13);

                statement.bindLong(14, dataSyncFlag);

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

    public boolean updateAttachment(ArrayList<TaskDataResponse.AttachmentList> list, String oldAttachmentId) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));

        for (TaskDataResponse.AttachmentList attachmentData : list) {

            ContentValues values = new ContentValues();
            values.put(TASK_ID, attachmentData.getTaskId());
            values.put(TASK_ATTACHMENT_ID, attachmentData.getTaskAttachmentId());
            values.put(FILE_NAME, attachmentData.getFileName());
            values.put(FILE_KEY, attachmentData.getFileKey());
            values.put(ATTACHMENT_DESCRIPTION, attachmentData.getAttachmentDescription());
            values.put(COMMENT_ID, attachmentData.getCommentId());
            values.put(LATITUDE, attachmentData.getLatitude());
            values.put(LONGITUDE, attachmentData.getLongitude());
            values.put(DISPLAY_FLAG, attachmentData.getDisplayFlag());
            values.put(CREATED_BY, attachmentData.getCreatedBy());
            values.put(CREATION_DATE, attachmentData.getCreationDate());
            values.put(MODIFIED_BY, attachmentData.getModifiedBy());
            values.put(MODIFICATION_DATE, attachmentData.getModificationDate());
            values.put(DATA_SYNC_FLAG, 1);

            whereClause = TASK_ATTACHMENT_ID + "=?";
            whereArgs = new String[]{oldAttachmentId};
            try {
                ret = database.update(DbAccess.TABLE_TASK_ATTACHMENTS, values, whereClause, whereArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret > 0;
    }

    public boolean updateAttachmentSyncFlag(TaskDataResponse.AttachmentList attachment) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));

        ContentValues values = new ContentValues();
        values.put(MODIFIED_BY, userID);
        values.put(MODIFICATION_DATE, System.currentTimeMillis());
        values.put(DISPLAY_FLAG, 0);
        values.put(DATA_SYNC_FLAG, 0);

        whereClause = TASK_ID + "=?";
        whereArgs = new String[]{attachment.getTaskAttachmentId() + ""};
        try {
            ret = database.update(DbAccess.TABLE_TASK_ATTACHMENTS, values, whereClause, whereArgs);
            //05-Jun-20 if attachment_id is negative then remove the attachment from db add remove query in else
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret > 0;
    }

    public boolean isAttachmentExist(int taskId, String fileName) {
        Cursor cursor = null;
        ArrayList<TaskDataResponse.AttachmentList> attachmentList = new ArrayList<>();

        String query = "select distinct task_attachments_id " +
                "from w_task_attachments where task_id = ? and file_name = ?";

        try {
            cursor = database.rawQuery(query, new String[]{taskId + "", fileName});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.AttachmentList attachment = new TaskDataResponse.AttachmentList();
                    attachment.setTaskAttachmentId(cursor.getInt(0));
                    attachmentList.add(attachment);
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

        if (attachmentList.size() > 1)
            return attachmentList.get(0).getTaskAttachmentId() > 0;

        return false;
    }

    public ArrayList<TaskDataResponse.AttachmentList> getAllAttachments(int taskId) {
        Cursor cursor = null;
        ArrayList<TaskDataResponse.AttachmentList> attachmentList = new ArrayList<>();

        String query = "select distinct task_attachments_id, task_id, file_name," +
                "file_key, attachment_description, display_flag, creation_date," +
                " modification_date, created_by, modified_by," +
                " data_sync_flag from w_task_attachments where task_id = ? and display_flag = 1";

        try {
            cursor = database.rawQuery(query, new String[]{taskId + ""});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.AttachmentList attachment = new TaskDataResponse.AttachmentList();
                    attachment.setTaskAttachmentId(cursor.getInt(0));
                    attachment.setTaskId(cursor.getInt(1));
                    attachment.setFileName(cursor.getString(2));
                    attachment.setFileKey(cursor.getString(3));
                    attachment.setAttachmentDescription(cursor.getString(4));
                    attachment.setDisplayFlag(cursor.getInt(5));
                    attachment.setCreationDate(cursor.getInt(6));
                    attachment.setModificationDate(cursor.getString(7));
                    attachment.setCreatedBy(cursor.getInt(8));
                    attachment.setModifiedBy(cursor.getString(9));
                    attachmentList.add(attachment);
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
        return attachmentList;
    }

    public ArrayList<TaskDataResponse.AttachmentList> getAllUnSyncAttachments(String taskId) {
        Cursor cursor = null;
        ArrayList<TaskDataResponse.AttachmentList> attachmentList = new ArrayList<>();

        String query = "select distinct task_attachments_id, task_id, file_name," +
                "file_key, attachment_description, display_flag, creation_date," +
                " modification_date, created_by, modified_by," +
                " data_sync_flag from w_task_attachments where data_sync_flag = 0 ";

        if (!taskId.isEmpty()) {
            query = query + " and task_id = ?";
            cursor = database.rawQuery(query, new String[]{taskId});
        } else {
            cursor = database.rawQuery(query, null);
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.AttachmentList attachment = new TaskDataResponse.AttachmentList();
                    attachment.setTaskAttachmentId(cursor.getInt(0));
                    attachment.setTaskId(cursor.getInt(1));

                    String fileName = cursor.getString(2);

/*                    if (fileName.contains("storage") || fileName.contains("emulated"))
                        attachment.setFileName(new File(fileName).getName());
                    else*/
                    attachment.setFileName(fileName);

                    attachment.setFileKey(cursor.getString(3));
                    attachment.setAttachmentDescription(cursor.getString(4));
                    attachment.setDisplayFlag(cursor.getInt(5));
                    attachment.setCreationDate(cursor.getInt(6));
                    attachment.setModificationDate(cursor.getString(7));
                    attachment.setCreatedBy(cursor.getInt(8));
                    attachment.setModifiedBy(cursor.getString(9));
                    attachmentList.add(attachment);
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
        return attachmentList;
    }

    public void updateDataSyncFlag(Integer taskId, String fileName,
                                   int clientTaskAttachmentId, int attachmentId) {
        int ret = 0;

        String[] whereArgs = new String[]{attachmentId + "", "1", clientTaskAttachmentId + "", taskId + ""};

        String query = "UPDATE w_task_attachments SET task_attachments_id = ?, " +
                "data_sync_flag = ? where task_attachments_id = ? and task_id = ? and " +
                "file_name like '%" + fileName + "%'";

        if (clientTaskAttachmentId != attachmentId) {
            query = "UPDATE w_task_attachments SET task_attachments_id = ?, " +
                    "data_sync_flag = ? where task_id = ? and " +
                    "file_name like '%" + fileName + "%'";
            whereArgs = new String[]{attachmentId + "", "1", taskId + ""};
        }

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ret = cursor.getInt(0);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Update task attachment query failed = " + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        Log.i(TAG, "Update task attachment Result=" + ret);
    }

    public void updateDisplayFlag(Integer taskId, String fileName, int taskAttachmentId) {

        if (isAttachmentLocal(taskId, taskAttachmentId)) {
            deleteAttachment(taskId, taskAttachmentId);
        } else {
            int ret = 0;

            String query = "UPDATE w_task_attachments set display_flag = ?, " +
                    "data_sync_flag = ? where task_attachments_id = ? and task_id = ? and " +
                    "file_name like '%" + fileName + "%'";

            String[] whereArgs = new String[]{"0", "0", taskAttachmentId + "", "" + taskId};

            Cursor cursor = null;
            try {
                cursor = database.rawQuery(query, whereArgs);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        ret = cursor.getInt(0);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Update task attachment dispFlag failed = " + e.getLocalizedMessage());
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            Log.i(TAG, "Update task attachment dispFlag =" + ret);
        }
    }

    public boolean deleteAttachment(int taskId, int taskAttachmentId) {
        int ret = 0;
        String whereClause = TASK_ID + " = ? and " + TASK_ATTACHMENT_ID + " =? ";
        String[] whereArgs = new String[]{"" + taskId, taskAttachmentId + ""};
        try {
            ret = database.delete(DbAccess.TABLE_TASK_ATTACHMENTS, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret > 0;
    }

    public boolean isAttachmentLocal(int taskId, int attachmentId) {
        Cursor cursor = null;
        int count = 0;

        String query = "select distinct count(task_attachments_id) " +
                "from w_task_attachments where task_id = ? and " + TASK_ATTACHMENT_ID
                + "= ? and (" + FILE_KEY + " is NULL or " + FILE_KEY + " is '')";

        try {
            cursor = database.rawQuery(query, new String[]{taskId + "", attachmentId + ""});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
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

        return count > 0;
    }

    public void updateTaskId(String taskId, String clientTaskId) {
        int ret = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TASK_ID, taskId);

            String whereClause = TASK_ID + " = ? ";
            String[] whereArgs = new String[]{clientTaskId};

            ret = database.update(DbAccess.TABLE_TASK_ATTACHMENTS, contentValues,
                    whereClause, whereArgs);
            Log.e("TaskAttachment", "updateAttachmentTaskId: UPDATED: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TaskAttachment", "updateAttachmentTaskId: DOES NOT UPDATED SUCCESSFULLY");
        }
    }
}
