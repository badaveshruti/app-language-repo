package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import qnopy.com.qnopyandroid.clientmodel.BoreDefinition;


public class BoreDefinitionDataSource {

    public SQLiteDatabase database;

    final String KEY_UserID = "UserID";

    final String KEY_EventID = "EventID";

    final String KEY_MobileAppID = "MobileAppID";

    final String KEY_LocationID = "LocationID";

    final String KEY_TotalDeth = "TotalDepth";

    final String KEY_DepthDifference = "DepthDifference";

    final String KEY_ExtField1 = "ExtField1";

    final String KEY_ExtField2 = "ExtField2";

    final String KEY_ExtField3 = "ExtField3";

    public BoreDefinitionDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        if (database==null){
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public long insertUserInput(int userID, int eventID, int appID,
                                String locID, String tDepth, String dDiff) {
        long ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_UserID, userID);
        values.put(KEY_EventID, eventID);
        values.put(KEY_MobileAppID, appID);
        values.put(KEY_LocationID, locID);
        values.put(KEY_TotalDeth, tDepth);
        values.put(KEY_DepthDifference, dDiff);

        try {
            ret = database.insert(DbAccess.TABLE_BORE_DEFINITION, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean isExistsUserInput(int userID, int eventID, int appID, int locID) {
        Cursor c = null;
        String sql = "select count(TotalDepth) from d_BoreDefinition where UserID = ? " +
                "and EventID = ? and MobileAppID = ? and LocationID = ?";
        String[] whereArgs = new String[]{"" + userID, "" + eventID, "" + appID, "" + locID};
        try {
            c = database.rawQuery(sql, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c != null) {
            c.moveToFirst();
            if (c.getInt(0) > 0) {
                return true;
            }
            c.close();

        }


        return false;
    }

    public int getDepthAndDifference(int userID, int eventID, int appID,
                                     int locID) {
        Cursor c = null;
        int depth = 0;
//		BoreDefinition ui = new BoreDefinition();
        String sql = "select TotalDepth, DepthDifference from d_BoreDefinition where UserID = ? " +
                "and EventID = ? and MobileAppID = ? and LocationID = ?";
        String[] whereArgs = new String[]{"" + userID, "" + eventID, "" + appID, "" + locID};
        try {
            c = database.rawQuery(sql, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c != null) {
            c.moveToFirst();
//			ui = cursorToUserInput(c);
            while (!c.isAfterLast()) {
                depth = c.getInt(0);
                break;
            }
            c.close();
        }

        return depth;
    }

    public BoreDefinition cursorToUserInput(Cursor c) {
        BoreDefinition ui = new BoreDefinition();
        ui.setTotalDepth(c.getInt(0));
//		ui.setDepthDifference(c.getInt(1));
        return ui;
    }

    public int updateTotalDepth(int userID, int eventID, int parentAppID,
                                int locID, int newDepth) {
        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_TotalDeth, newDepth);
        String whereClause = "UserID = ? and EventID = ? and MobileAppID = ? and LocationID = ?";
        String[] whereArgs = new String[]{"" + userID, "" + eventID, "" + parentAppID, "" + locID};
        try {
            ret = database.update(DbAccess.TABLE_BORE_DEFINITION, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
