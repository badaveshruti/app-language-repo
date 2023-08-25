package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BoreDepthsDataSource {

    public SQLiteDatabase database;

    final String KEY_UserID = "UserID";

    final String KEY_EventID = "EventID";

    final String KEY_MobileAppID = "MobileAppID";

    final String KEY_LocationID = "LocationID";

    final String KEY_DepthLevels = "DepthLevels";

    final String KEY_SetID = "SetID";

    final String KEY_ExtField1 = "ExtField1";

    final String KEY_ExtField2 = "ExtField2";

    final String KEY_ExtField3 = "ExtField3";

    public BoreDepthsDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        if (database==null){
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public void insertDepths(int userID, int eventID, int parentAppID,
                             int locID, List<Double> levels) {
        long ret = 0;
        try {
            database.beginTransaction();
            for (int i = 0; i < levels.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(KEY_UserID, userID);
                values.put(KEY_EventID, eventID);
                values.put(KEY_MobileAppID, parentAppID);
                values.put(KEY_LocationID, locID);
                values.put(KEY_DepthLevels, levels.get(i));

                try {
                    ret = database.insert(DbAccess.TABLE_BORE_DEPTHS, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    public List<Double> readDepths(int userID, int eventID, int parentAppID,
                                   int locID) {
        List<Double> depth = new ArrayList<Double>();
        Cursor c = null;
        String sql = "select DepthLevels from d_BoreDepths where UserID = ? " +
                "and EventID = ? and MobileAppID = ? and LocationID = ? order by DepthLevels asc";
        String[] whereArgs = new String[]{"" + userID, "" + eventID, "" + parentAppID, "" + locID};

        try {
            c = database.rawQuery(sql, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                depth.add(c.getDouble(0));
                c.moveToNext();
            }
            c.close();
        }

        System.out.println("depths " + depth);
        return depth;
    }

    public long insertNewDepth(int userID, int eventID, int parentAppID,
                               int locID, Double depth) {
        long ret = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_UserID, userID);
            values.put(KEY_EventID, eventID);
            values.put(KEY_MobileAppID, parentAppID);
            values.put(KEY_LocationID, locID);
            values.put(KEY_DepthLevels, depth);

            try {
                ret = database.insert(DbAccess.TABLE_BORE_DEPTHS, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
