package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.CocDataModel;
import qnopy.com.qnopyandroid.util.Util;

public class CMMethodsDataSource {

    String KEY_cm_methods_id = "cm_methods_id";
    String KEY_analyses = "analyses";
    String KEY_methods = "methods";
    String KEY_container = "container";
    String KEY_sugg_qty = "sugg_qty";
    String KEY_preservative = "preservative";
    String KEY_hold_time = "hold_time";
    String KEY_created_by = "created_by";
    String KEY_creation_date = "creation_date";
    String KEY_modified_by = "modified_by";
    String KEY_modified_date = "modified_date";
    String KEY_labName = "labName";
    String KEY_matrix = "matrix";
    String KEY_noOfContainer = "noOfContainer";

    Context mContext;
    public SQLiteDatabase database;
    private String TAG = "CmMethodsTable";

    public CMMethodsDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public int insertCMMethods(CocDataModel.CocMethod cocMasters) {
        int ret = 0;

        database.beginTransaction();

        try {

            ContentValues values = new ContentValues();

            values.put(KEY_cm_methods_id, cocMasters.getMethodId());
            values.put(KEY_analyses, cocMasters.getAnalyses());
            values.put(KEY_methods, cocMasters.getMethodName());
            values.put(KEY_container, cocMasters.getContainer());
            values.put(KEY_sugg_qty, cocMasters.getSuggQty());
            values.put(KEY_preservative, cocMasters.getPreservative());
            values.put(KEY_hold_time, cocMasters.getHoldTime());
            values.put(KEY_labName, cocMasters.getLabName());
            values.put(KEY_matrix, cocMasters.getMatrix());
            values.put(KEY_noOfContainer, cocMasters.getNoOfContainer());
/*            values.put(KEY_created_by, null);
            values.put(KEY_creation_date, cocMasters.getCreatedBy());
            values.put(KEY_modified_by, cocMasters.getModifiedBy());
            values.put(KEY_modified_date, cocMasters.getClientCocId());*/

            ret = (int) database.insert(DbAccess.TABLE_CM_METHODS, null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insert cm methods error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int storeCMMethods(List<CocDataModel.CocMethod> cocMethodsList) {
        int ret = 0;

        database.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            for (CocDataModel.CocMethod cocMasters : cocMethodsList) {

                values.put(KEY_cm_methods_id, cocMasters.getMethodId());
                values.put(KEY_analyses, cocMasters.getAnalyses());
                values.put(KEY_methods, cocMasters.getMethodName());
                values.put(KEY_container, cocMasters.getContainer());
                values.put(KEY_sugg_qty, cocMasters.getSuggQty());
                values.put(KEY_preservative, cocMasters.getPreservative());
                values.put(KEY_hold_time, cocMasters.getHoldTime());
                values.put(KEY_labName, cocMasters.getLabName());
                values.put(KEY_matrix, cocMasters.getMatrix());
                values.put(KEY_noOfContainer, cocMasters.getNoOfContainer());
/*            values.put(KEY_created_by, null);
            values.put(KEY_creation_date, cocMasters.getCreatedBy());
            values.put(KEY_modified_by, cocMasters.getModifiedBy());
            values.put(KEY_modified_date, cocMasters.getClientCocId());*/

                ret = (int) database.insert(DbAccess.TABLE_CM_METHODS, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insert cm methods error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int storeBulkBindCMMethods(List<CocDataModel.CocMethod> cocMethodsList) {
        int ret = 0;

        String[] arrColumns = {KEY_cm_methods_id, KEY_analyses, KEY_methods,
                KEY_container, KEY_sugg_qty, KEY_preservative, KEY_hold_time,
                KEY_labName, KEY_matrix, KEY_noOfContainer};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_CM_METHODS + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();
        try {
            for (CocDataModel.CocMethod cocMasters : cocMethodsList) {

                if (cocMasters.getMethodId() != null)
                    statement.bindString(1, cocMasters.getMethodId());
                else
                    statement.bindNull(1);

                if (cocMasters.getAnalyses() != null)
                    statement.bindString(2, cocMasters.getAnalyses());
                else
                    statement.bindNull(2);

                if (cocMasters.getMethodName() != null)
                    statement.bindString(3, cocMasters.getMethodName());
                else
                    statement.bindNull(3);

                if (cocMasters.getContainer() != null)
                    statement.bindString(4, cocMasters.getContainer());
                else
                    statement.bindNull(4);

                if (cocMasters.getSuggQty() != null)
                    statement.bindString(5, cocMasters.getSuggQty());
                else statement.bindNull(5);

                if (cocMasters.getPreservative() != null)
                    statement.bindString(6, cocMasters.getPreservative());
                else statement.bindNull(6);

                if (cocMasters.getHoldTime() != null)
                    statement.bindString(7, cocMasters.getHoldTime());
                else
                    statement.bindNull(7);

                if (cocMasters.getLabName() != null)
                    statement.bindString(8, cocMasters.getLabName());
                else statement.bindNull(8);

                if (cocMasters.getMatrix() != null)
                    statement.bindString(9, cocMasters.getMatrix());
                else statement.bindNull(9);

                if (cocMasters.getNoOfContainer() != null)
                    statement.bindString(10, cocMasters.getNoOfContainer());
                else statement.bindNull(10);

                ret = (int) statement.executeInsert();
                statement.clearBindings();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insert cm methods error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

}
