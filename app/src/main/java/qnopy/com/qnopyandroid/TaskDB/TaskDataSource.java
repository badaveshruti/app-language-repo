package qnopy.com.qnopyandroid.TaskDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.TaskModelClasses.TaskDataList;
import qnopy.com.qnopyandroid.db.DbAccess;

public class TaskDataSource {

    final String Key_TaskId = "taskId";
    final String Key_TaskTitle = "taskTitle";
    final String Key_TaskDescription = "taskDescription";
    final String Key_ParentTaskId = "parentTaskId";
    final String Key_TaskStatus = "taskStatus";
    final String Key_ProjectId = "projectId";
    final String Key_TaskOwner = "taskOwner";
    final String Key_DueDate = "dueDate";
    final String Key_CreatedBy = "createdBy";
    final String Key_CreationDate = "creationDate";
    final String Key_ModifiedBy = "modifiedBy";
    final String Key_ModificationDate = "modificationDate";
    final String Key_DataSyncFlag = "dataSyncFlag";

    public SQLiteDatabase database;
    Context mContext;

    public TaskDataSource(Context mContext) {
        this.mContext = mContext;
        database = DbAccess.getInstance(mContext).database;
        if (database == null) {
            DbAccess.getInstance(mContext).open();
            database = DbAccess.getInstance(mContext).database;
        }
    }

    public void storeTaskDataToDB(ArrayList<TaskDataList> mArrayListTaskDataList) {


        database.beginTransaction();
        try {

            for (TaskDataList t : mArrayListTaskDataList) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(Key_TaskId, t.getTaskId());
                contentValues.put(Key_TaskTitle, t.getTaskTitle());
                contentValues.put(Key_TaskDescription, t.getTaskDescription());
                contentValues.put(Key_ParentTaskId, t.getParentTaskId());
                contentValues.put(Key_TaskStatus, t.getTaskStatus());
                contentValues.put(Key_ProjectId, t.getProjectId());
                contentValues.put(Key_TaskOwner, t.getTaskOwner());
                contentValues.put(Key_DueDate, t.getDueDate());
                contentValues.put(Key_CreatedBy, t.getCreatedBy());
                contentValues.put(Key_CreationDate, t.getCreationDate());
                contentValues.put(Key_ModifiedBy, t.getModifiedBy());
                contentValues.put(Key_ModificationDate, t.getModificationDate());

                database.insert("TaskDataList", null, contentValues);
            }
            database.setTransactionSuccessful();
            Log.e("storeTaskData", "storeTaskDataToDB: Task data list stored successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("storeTaskData", "storeTaskDataToDB:" + e.getMessage());
        } finally {
            database.endTransaction();
        }
    }

    public ArrayList<TaskDataList> getAllTaskDataFromDB() {
        ArrayList<TaskDataList> arrayListTaskDataList = new ArrayList<>();

        String query = "select taskId, taskTitle, taskDescription, parentTaskId, taskStatus, projectId, taskOwner, dueDate, createdBy, " +
                "creationDate, modifiedBy, modificationDate from TaskDataList";

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    TaskDataList taskDataList = new TaskDataList();
                    taskDataList.setTaskId(cursor.getInt(0));
                    taskDataList.setTaskTitle(cursor.getString(1));
                    taskDataList.setTaskDescription(cursor.getString(2));
                    taskDataList.setParentTaskId(cursor.getInt(3));
                    taskDataList.setTaskStatus(cursor.getString(4));
                    taskDataList.setProjectId(cursor.getInt(5));
                    taskDataList.setTaskOwner(cursor.getInt(6));
                    taskDataList.setDueDate(cursor.getLong(7));
                    taskDataList.setCreatedBy(cursor.getInt(8));
                    taskDataList.setCreationDate(cursor.getLong(9));
                    taskDataList.setModifiedBy(cursor.getInt(10));
                    taskDataList.setModificationDate(cursor.getLong(11));

                    arrayListTaskDataList.add(taskDataList);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayListTaskDataList;
    }

    public void deleteAlreadyExistingTask(ArrayList<TaskDataList> mArrayListTaskDataList) {

    }

    public void updateTaskDatawithStatusUpdatedToDatabase(Integer taskId, String statusUpdate, Integer modifiedBy, long modificationDate) {
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(Key_TaskStatus, statusUpdate);
            contentValues.put(Key_ModifiedBy, modifiedBy);
            contentValues.put(Key_ModificationDate, modificationDate);
            contentValues.put(Key_DataSyncFlag, 0);

            String whereClause = "taskId = ?";
            String[] whereArgs = new String[]{"" + taskId};

            database.update("TaskDataList", contentValues, whereClause, whereArgs);

            Log.e("newTaskData", "insertTaskDatawithStatusUpdatedToDatabase: task status change stored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("newTaskData", "insertTaskDatawithStatusUpdatedToDatabase: task status change does not stored.");
        }
    }

    public ArrayList<TaskDataList> getTaskDataHavingTaskStatusChange() {
        ArrayList<TaskDataList> arrayListNewStatusTaskData = new ArrayList<>();

        String query = "select taskId, taskTitle, taskDescription, parentTaskId, taskStatus, projectId, taskOwner, dueDate, createdBy, " +
                "creationDate, modifiedBy, modificationDate from TaskDataList where dataSyncFlag = 0";

        Cursor cursor = null;

        cursor = database.rawQuery(query, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    TaskDataList taskDataList = new TaskDataList();
                    taskDataList.setTaskId(cursor.getInt(0));
                    taskDataList.setTaskTitle(cursor.getString(1));
                    taskDataList.setTaskDescription(cursor.getString(2));
                    taskDataList.setParentTaskId(cursor.getInt(3));
                    taskDataList.setTaskStatus(cursor.getString(4));
                    taskDataList.setProjectId(cursor.getInt(5));
                    taskDataList.setTaskOwner(cursor.getInt(6));
                    taskDataList.setDueDate(cursor.getLong(7));
                    taskDataList.setCreatedBy(cursor.getInt(8));
                    taskDataList.setCreationDate(cursor.getLong(9));
                    taskDataList.setModifiedBy(cursor.getInt(10));
                    taskDataList.setModificationDate(cursor.getLong(11));

                    arrayListNewStatusTaskData.add(taskDataList);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayListNewStatusTaskData;
    }

    public void updateDataSyncFlagOnStatusChange(int taskID) {
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(Key_DataSyncFlag, 1);

            String whereClause = "taskId = ?";
            String[] whereArgs = new String[]{"" + taskID};

            database.update("TaskDataList", contentValues, whereClause, whereArgs);

            Log.e("newTaskData", "updateDataSyncFlagOnStatusChange: updated data sync flag to 1.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("newTaskData", "updateDataSyncFlagOnStatusChange: Fail to update data sync flag.");
        }
    }
}
