package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.task.TaskIntentData;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class TaskDetailsDataSource {

    private static final String TASK_ID = "task_id";
    private static final String CLIENT_TASK_ID = "clientTaskId";
    private static final String TASK_TITLE = "task_title";
    private static final String TASK_DESCRIPTION = "task_description";
    private static final String PARENT_TASK_ID = "parent_task_id";
    private static final String TASK_STATUS = "task_status";
    private static final String PROJECT_ID = "project_id";
    private static final String FIELD_PARAMETER_ID = "field_parameter_id";
    private static final String LOCATION_ID = "location_id";
    private static final String MOBILE_APP_ID = "mobile_app_id";
    private static final String SET_ID = "set_id";
    private static final String TASK_OWNER = "task_owner";
    private static final String DUE_DATE = "due_date";
    private static final String CREATED_BY = "created_by";
    private static final String CREATION_DATE = "creation_date";
    private static final String MODIFIED_BY = "modified_by";
    private static final String MODIFICATION_DATE = "modification_date";
    private static final String SERVER_CREATION_DATE = "server_creation_date";
    private static final String SERVER_MODIFICATION = "server_modification_date";
    private static final String DATA_SYNC_FLAG = "data_sync_flag";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TAG = "Task Data Source";
    private Context mContext;
    private SQLiteDatabase database;

    @Inject
    public TaskDetailsDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        SQLiteDatabase.releaseMemory();
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public long insertTaskData(List<TaskDataResponse.TaskDataList> dataList, int dataSyncFlag) {
        long ret = 0;

        database.beginTransaction();
        TaskDataResponse.TaskDataList taskData;

        try {

            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                taskData = dataList.get(i);
                values.put(TASK_ID, taskData.getTaskId());
                values.put(TASK_TITLE, taskData.getTaskTitle());
                values.put(TASK_DESCRIPTION, taskData.getTaskDescription());
                values.put(PARENT_TASK_ID, taskData.getParentTaskId());
                values.put(TASK_STATUS, taskData.getTaskStatus());
                values.put(CLIENT_TASK_ID, taskData.getTaskId());
                values.put(TASK_OWNER, taskData.getTaskOwner());
                values.put(DUE_DATE, taskData.getDueDate());
                values.put(CREATED_BY, taskData.getCreatedBy());

                try {
                    values.put(PROJECT_ID, taskData.getProjectId());
                    values.put(FIELD_PARAMETER_ID, taskData.getFieldParameterId());
                    values.put(LOCATION_ID, taskData.getLocationId());
                    values.put(MOBILE_APP_ID, taskData.getMobileAppId());
                    values.put(SET_ID, taskData.getSetId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                values.put(CREATION_DATE, taskData.getCreationDate());
                values.put(MODIFIED_BY, taskData.getModifiedBy());
                values.put(MODIFICATION_DATE, taskData.getModificationDate());
                values.put(DATA_SYNC_FLAG, dataSyncFlag);
                values.put(LATITUDE, taskData.getLatitude());
                values.put(LONGITUDE, taskData.getLongitude());

                ret = database.insert(DbAccess.TABLE_TASK_DETAILS, null, values);
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

    public long storeBulkBindTaskData(List<TaskDataResponse.TaskDataList> dataList, int dataSyncFlag) {
        long ret = 0;

        String[] arrColumns = {TASK_ID, TASK_TITLE, TASK_DESCRIPTION,
                PARENT_TASK_ID, TASK_STATUS, CLIENT_TASK_ID, TASK_OWNER,
                DUE_DATE, CREATED_BY, PROJECT_ID, FIELD_PARAMETER_ID, LOCATION_ID, MOBILE_APP_ID,
                SET_ID, CREATION_DATE, MODIFIED_BY, MODIFICATION_DATE, DATA_SYNC_FLAG, LATITUDE,
                LONGITUDE};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_TASK_DETAILS + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {

            for (TaskDataResponse.TaskDataList taskData : dataList) {
                statement.bindLong(1, taskData.getTaskId());

                if (taskData.getTaskTitle() != null)
                    statement.bindString(2, taskData.getTaskTitle());
                else
                    statement.bindNull(2);

                if (taskData.getTaskDescription() != null)
                    statement.bindString(3, taskData.getTaskDescription());
                else
                    statement.bindNull(3);

                statement.bindLong(4, taskData.getParentTaskId());

                if (taskData.getTaskStatus() != null)
                    statement.bindString(5, taskData.getTaskStatus());
                else statement.bindNull(5);

                statement.bindLong(6, taskData.getTaskId());

                if (taskData.getTaskOwner() != null)
                    statement.bindString(7, taskData.getTaskOwner());
                else
                    statement.bindNull(7);

                statement.bindLong(8, taskData.getDueDate());
                statement.bindLong(9, taskData.getCreatedBy());

                try {
                    statement.bindLong(10, taskData.getProjectId());
                    statement.bindLong(11, taskData.getFieldParameterId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (taskData.getLocationId() != null)
                    statement.bindLong(12, taskData.getLocationId());
                else statement.bindNull(12);

                if (taskData.getMobileAppId() != null)
                    statement.bindLong(13, taskData.getMobileAppId());
                else statement.bindNull(13);

                if (taskData.getSetId() != null)
                    statement.bindLong(14, taskData.getSetId());
                else statement.bindNull(14);

                statement.bindLong(15, taskData.getCreationDate());
                statement.bindLong(16, taskData.getModifiedBy());
                statement.bindLong(17, taskData.getModificationDate());
                statement.bindLong(18, dataSyncFlag);

                if (taskData.getLatitude() != null)
                    statement.bindDouble(19, taskData.getLatitude());
                else statement.bindNull(19);

                if (taskData.getLongitude() != null)
                    statement.bindDouble(20, taskData.getLongitude());
                else statement.bindNull(20);

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

    public boolean hasTasksData() {
        Cursor cursor = null;
        int count = 0;

        String query = "select distinct count(task_id) from w_task_details limit 2";

        try {
            cursor = database.rawQuery(query, null);
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

    public ArrayList<TaskDataResponse.TaskDataList> getAllTasks(int status, int parentTaskId, String siteId) {

        Cursor cursor = null;
        ArrayList<TaskDataResponse.TaskDataList> taskList = new ArrayList<>();

        String query = "select distinct task_id, task_title, task_description," +
                " creation_date, modification_date, task_status, project_id," +
                " task_owner, due_date, created_by, modified_by, parent_task_id," +
                " location_id, field_parameter_id, mobile_app_id, set_id, project_id, latitude, longitude"
                + " from w_task_details where parent_task_id = ? and task_status NOT LIKE 'Discarded'";

        if (status == 0)
            query = query + "and task_status NOT like 'completed'";
        else
            query = query + "and task_status like 'completed'";

        if (siteId != null && !siteId.isEmpty() && !siteId.equals("-1"))
            query = query + " and project_id = " + siteId;

        try {
            cursor = database.rawQuery(query, new String[]{parentTaskId + ""});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.TaskDataList taskDetails = new TaskDataResponse.TaskDataList();
                    taskDetails.setTaskId(cursor.getInt(0));
                    taskDetails.setTaskTitle(cursor.getString(1));
                    taskDetails.setTaskDescription(cursor.getString(2));
                    taskDetails.setCreationDate(Math.max(cursor.getLong(3),
                            cursor.getLong(4)));
                    taskDetails.setTaskStatus(cursor.getString(5));
                    taskDetails.setProjectId(cursor.getInt(6));
                    taskDetails.setTaskOwner(cursor.getString(7));
                    taskDetails.setDueDate(cursor.getLong(8));
                    taskDetails.setCreatedBy(cursor.getInt(9));
                    taskDetails.setModifiedBy(cursor.getInt(10));
                    taskDetails.setParentTaskId(cursor.getInt(11));
                    taskDetails.setLocationId(cursor.getLong(12));
                    taskDetails.setFieldParameterId(cursor.getInt(13));
                    taskDetails.setMobileAppId(cursor.getInt(14));
                    taskDetails.setSetId(cursor.getInt(15));
                    taskDetails.setProjectId(cursor.getInt(16));
                    taskDetails.setLatitude(cursor.getDouble(17));
                    taskDetails.setLongitude(cursor.getDouble(18));
                    taskList.add(taskDetails);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                if (!cursor.isClosed()) {
                    cursor.close();
                }
        }
        return taskList;
    }

    public TaskDataResponse.TaskDataList getTaskData(String taskId) {

        Cursor cursor = null;
        TaskDataResponse.TaskDataList taskDetails = new TaskDataResponse.TaskDataList();

        String query = "select distinct task_id, task_title, task_description," +
                " creation_date, modification_date, task_status, project_id," +
                " task_owner, due_date, created_by, modified_by, parent_task_id," +
                " location_id, field_parameter_id, mobile_app_id, set_id, project_id, latitude, longitude"
                + " from w_task_details where task_id = ?";

        try {
            cursor = database.rawQuery(query, new String[]{taskId});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    taskDetails.setTaskId(cursor.getInt(0));
                    taskDetails.setTaskTitle(cursor.getString(1));
                    taskDetails.setTaskDescription(cursor.getString(2));
                    taskDetails.setCreationDate(Math.max(cursor.getLong(3),
                            cursor.getLong(4)));
                    taskDetails.setTaskStatus(cursor.getString(5));
                    taskDetails.setProjectId(cursor.getInt(6));
                    taskDetails.setTaskOwner(cursor.getString(7));
                    taskDetails.setDueDate(cursor.getLong(8));
                    taskDetails.setCreatedBy(cursor.getInt(9));
                    taskDetails.setModifiedBy(cursor.getInt(10));
                    taskDetails.setParentTaskId(cursor.getInt(11));
                    taskDetails.setLocationId(cursor.getLong(12));
                    taskDetails.setFieldParameterId(cursor.getInt(13));
                    taskDetails.setMobileAppId(cursor.getInt(14));
                    taskDetails.setSetId(cursor.getInt(15));
                    taskDetails.setProjectId(cursor.getInt(16));
                    taskDetails.setLatitude(cursor.getDouble(17));
                    taskDetails.setLongitude(cursor.getDouble(18));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                if (!cursor.isClosed()) {
                    cursor.close();
                }
        }
        return taskDetails;
    }

    public ArrayList<TaskDataResponse.TaskDataList>
    getAllTasksByFormDetails(int status, int parentTaskId, TaskIntentData taskData) {

        Cursor cursor = null;
        ArrayList<TaskDataResponse.TaskDataList> taskList = new ArrayList<>();

        String query = "select distinct task_id, task_title, task_description," +
                " creation_date, modification_date, task_status, project_id," +
                " task_owner, due_date, created_by, modified_by, parent_task_id," +
                " location_id, field_parameter_id, mobile_app_id, set_id, project_id, latitude, longitude"
                + " from w_task_details where parent_task_id = ? and " +
                "project_id = ? and field_parameter_id=? and set_id=? " +
                "and location_id=? and mobile_app_id=? and task_status NOT LIKE 'Discarded'";

        if (status == 0)
            query = query + "and task_status NOT like 'completed'";
        else
            query = query + "and task_status like 'completed'";

        try {
            //siteId as projectId both are similar

            cursor = database.rawQuery(query, new String[]{parentTaskId + "",
                    taskData.getProjectId(), taskData.getFieldParamId() + "",
                    taskData.getSetId() + "", taskData.getLocationId() + "",
                    taskData.getMobileAppId() + ""});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.TaskDataList taskDetails = new TaskDataResponse.TaskDataList();
                    taskDetails.setTaskId(cursor.getInt(0));
                    taskDetails.setTaskTitle(cursor.getString(1));
                    taskDetails.setTaskDescription(cursor.getString(2));
                    taskDetails.setCreationDate(Math.max(cursor.getLong(3),
                            cursor.getLong(4)));
                    taskDetails.setTaskStatus(cursor.getString(5));
                    taskDetails.setProjectId(cursor.getInt(6));
                    taskDetails.setTaskOwner(cursor.getString(7));
                    taskDetails.setDueDate(cursor.getLong(8));
                    taskDetails.setCreatedBy(cursor.getInt(9));
                    taskDetails.setModifiedBy(cursor.getInt(10));
                    taskDetails.setParentTaskId(cursor.getInt(11));
                    taskDetails.setLocationId(cursor.getLong(12));
                    taskDetails.setFieldParameterId(cursor.getInt(13));
                    taskDetails.setMobileAppId(cursor.getInt(14));
                    taskDetails.setSetId(cursor.getInt(15));
                    taskDetails.setProjectId(cursor.getInt(16));
                    taskDetails.setLatitude(cursor.getDouble(17));
                    taskDetails.setLongitude(cursor.getDouble(18));
                    taskList.add(taskDetails);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                if (!cursor.isClosed()) {
                    cursor.close();
                }
        }
        return taskList;
    }

    public ArrayList<TaskDataResponse.TaskDataList> getAllUnSyncedTasks(String taskId) {

        Cursor cursor = null;
        ArrayList<TaskDataResponse.TaskDataList> taskList = new ArrayList<>();

        String query = "select distinct task_id, task_title, task_description," +
                " creation_date, modification_date, task_status, project_id," +
                " task_owner, due_date, created_by, modified_by, parent_task_id, " +
                " location_id, field_parameter_id, mobile_app_id, set_id, project_id, latitude, longitude" +
                " from w_task_details where data_sync_flag = 0 ";

        try {

            if (!taskId.isEmpty()) {
                query = query + " and task_id = ?";
                cursor = database.rawQuery(query, new String[]{taskId});
            } else {
                cursor = database.rawQuery(query, null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskDataResponse.TaskDataList taskDetails = new TaskDataResponse.TaskDataList();
                    taskDetails.setTaskId(cursor.getInt(0));
                    taskDetails.setTaskTitle(cursor.getString(1));
                    taskDetails.setTaskDescription(cursor.getString(2));
                    taskDetails.setCreationDate(cursor.getLong(3));
                    if (cursor.getLong(4) != 0)
                        taskDetails.setModificationDate(cursor.getLong(4));
                    taskDetails.setTaskStatus(cursor.getString(5));
                    taskDetails.setProjectId(cursor.getInt(6));
                    taskDetails.setTaskOwner(cursor.getString(7));
                    taskDetails.setDueDate(cursor.getLong(8));
                    taskDetails.setCreatedBy(cursor.getInt(9));
                    if (cursor.getLong(10) != 0)
                        taskDetails.setModifiedBy(cursor.getInt(10));
                    taskDetails.setParentTaskId(cursor.getInt(11));
                    taskDetails.setLocationId(cursor.getLong(12));
                    taskDetails.setFieldParameterId(cursor.getInt(13));
                    taskDetails.setMobileAppId(cursor.getInt(14));
                    taskDetails.setSetId(cursor.getInt(15));
                    taskDetails.setProjectId(cursor.getInt(16));
                    taskDetails.setLatitude(cursor.getDouble(17));
                    taskDetails.setLongitude(cursor.getDouble(18));
                    taskList.add(taskDetails);

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
        return taskList;
    }

    public int getTaskCountForBadge(String siteId) {
        int count = 0;
        Cursor cursor = null;

        String query = "select distinct count(*) from w_task_details " +
                "where (task_status NOT LIKE 'Discarded' OR task_status NOT LIKE 'completed')";

        if (siteId != null && !siteId.isEmpty() && !siteId.equals("-1"))
            query = query + " and project_id = " + siteId;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    count = cursor.getInt(0);
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
        return count;
    }

    public boolean updateStatus(String status, String taskId) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));

        ContentValues values = new ContentValues();
        values.put(TASK_STATUS, status);
        values.put(MODIFIED_BY, userID);
        values.put(MODIFICATION_DATE, System.currentTimeMillis());
        values.put(DATA_SYNC_FLAG, 0);

        whereClause = TASK_ID + "=?";
        whereArgs = new String[]{taskId};
        try {
            ret = database.update(DbAccess.TABLE_TASK_DETAILS, values, whereClause, whereArgs);
            Log.e("Status updated", ret + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret > 0;
    }

    public boolean updateSyncFlagAndId(String taskId, String clientTaskId) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));

        ContentValues values = new ContentValues();
        values.put(TASK_ID, taskId);
        values.put(CLIENT_TASK_ID, taskId);
        values.put(DATA_SYNC_FLAG, 1);

        whereClause = CLIENT_TASK_ID + "=?";
        whereArgs = new String[]{clientTaskId};
        try {
            ret = database.update(DbAccess.TABLE_TASK_DETAILS, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret > 0;
    }

    public boolean updateSyncFlag(String taskId, int dataSyncFlag) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));

        ContentValues values = new ContentValues();
        values.put(DATA_SYNC_FLAG, dataSyncFlag);

        whereClause = TASK_ID + "=? ";
        whereArgs = new String[]{taskId};
        try {
            ret = database.update(DbAccess.TABLE_TASK_DETAILS, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret > 0;
    }

    public boolean updateTaskDetails(String taskId, int dataSyncFlag, TaskDataResponse.TaskDataList taskData,
                                     String status) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext,
                GlobalStrings.USERID));

        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, taskData.getTaskTitle());
        values.put(TASK_DESCRIPTION, taskData.getTaskDescription());
        values.put(DUE_DATE, taskData.getDueDate());
        values.put(TASK_STATUS, status);
        values.put(DATA_SYNC_FLAG, dataSyncFlag);

        try {
            values.put(PROJECT_ID, taskData.getProjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (taskData.getLatitude() != null)
            values.put(LATITUDE, taskData.getLatitude());

        if (taskData.getLongitude() != null)
            values.put(LONGITUDE, taskData.getLongitude());

        if (userID != 0) {
            values.put(MODIFICATION_DATE, System.currentTimeMillis());
            values.put(MODIFIED_BY, userID);
        }

        whereClause = TASK_ID + "=? ";
        whereArgs = new String[]{taskId};
        try {
            ret = database.update(DbAccess.TABLE_TASK_DETAILS, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("TaskDetails Update", "Task Details updated: " + ret);

        return ret > 0;
    }

    public boolean updateTasksLocationId(String oldLocationId, String newLocationId) {
        int ret = 0;
        String whereClause;
        String[] whereArgs;
        int userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext,
                GlobalStrings.USERID));

        ContentValues values = new ContentValues();
        values.put(LOCATION_ID, newLocationId);

        whereClause = LOCATION_ID + "=? ";
        whereArgs = new String[]{oldLocationId};
        try {
            ret = database.update(DbAccess.TABLE_TASK_DETAILS, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("TaskDetails Update", "Task Details updated: " + ret);

        return ret > 0;
    }

    String[] tablesToDelete = new String[]{
            DbAccess.TABLE_TASK_DETAILS,
            DbAccess.TABLE_TASK_COMMENTS,
            DbAccess.TABLE_TASK_ATTACHMENTS,
            DbAccess.TABLE_TASK_USERS
    };

    public void truncateTaskTables() {
        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            for (int i = 0; i < tablesToDelete.length; i++) {
                try {
                    ret = database.delete(tablesToDelete[i], null, null);
                    Log.i(TAG, "deleted table name :" + tablesToDelete[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Caught for Table name=" + tablesToDelete[i] + ret);
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            database = DbAccess.getInstance(mContext).database;
        } finally {
            if (database != null) {
                database.endTransaction();
            }
        }
    }

    public int getTasksCount(int fpid, String siteID, int setId,
                             int mobileAppId, String locationId) {
        Cursor cursor = null;
        int totalCount = 0;

        //siteId as projectId both are similar
        String query = "select count(task_id) from " + DbAccess.TABLE_TASK_DETAILS +
                " where project_id = ? and field_parameter_id=? and set_id=? " +
                "and location_id=? and mobile_app_id=?";
        String whereArgs[] = {siteID, fpid + "", setId + "", locationId, mobileAppId + ""};
        cursor = database.rawQuery(query, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            totalCount = cursor.getInt(0);
            cursor.close();
        }

        return totalCount;
    }

    public boolean checkTaskIdExist(int taskId) {
        Cursor cursor = null;
        int totalCount = 0;

        String query = "select count(task_id) from " + DbAccess.TABLE_TASK_DETAILS +
                " where task_id = ?";
        String whereArgs[] = {taskId + ""};
        cursor = database.rawQuery(query, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            totalCount = cursor.getInt(0);
            cursor.close();
        }

        return totalCount > 0;
    }
}
