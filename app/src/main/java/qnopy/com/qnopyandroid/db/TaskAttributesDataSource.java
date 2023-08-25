package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import qnopy.com.qnopyandroid.requestmodel.TaskAttributes;

/**
 * Created by QNOPY on 7/18/2017.
 */

public class TaskAttributesDataSource {
    private static final String TAG = "TaskAttributes ";

    final String KEY_TaskID = "TaskID";
    final String KEY_AttributeName = "attributeName";
    final String KEY_AttributeValue = "attributeValue";

    Context mContext;
    public SQLiteDatabase database;

    public TaskAttributesDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }
    String TaskAttributesTable = "task_attributes";


    public void truncateTaskAttributes() {

        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();

            try {
                ret = database.delete(TaskAttributesTable, null, null);
                Log.i(TAG, "deleted table:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Caught for Table name=" + ret);
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

    public int insertNewTaskAttributesList(TaskAttributes taskAttributes) {
        int ret = 0;

        database.beginTransaction();
        //   WorkOrderTask workOrderTask;

        try {

            //    for (int i = 0; i < dataList.size(); i++) {
            ContentValues values = new ContentValues();
            //  workOrderTask = dataList.get(i);

            values.put(KEY_TaskID, taskAttributes.getTaskId());
            values.put(KEY_AttributeName, taskAttributes.getName());
            values.put(KEY_AttributeValue, taskAttributes.getValue());

            ret = (int) database.insert(DbAccess.TABLE_TASK_ATTRIBUTES, null, values);
           // Log.i(TAG, "Insert new Data List Return Count:" + ret);
            // }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
        return ret;
    }


}
