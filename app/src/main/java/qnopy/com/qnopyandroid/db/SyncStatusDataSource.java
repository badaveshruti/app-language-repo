package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Yogendra on 21-May-16.
 */
public class SyncStatusDataSource {
    private static final String TAG = "SyncStatusDataSource";

    // TODO: 21-May-16 TABLE D_SYNC_STATUS
    final String KEY_Sync_ID = "Sync_id";// INT NOT NULL PRIMARY KEY
    final String KEY_Event_ID = "Event_id";// INT
    final String KEY_User_ID = "User_id";// INT NOT NULL
    final String KEY_Last_SyncDate = "last_sync_date"; // DEFAULT 0
    final String KEY_Type = "type"; // DEFAULT 0


    public SQLiteDatabase database;
    Context mContext;

    public SyncStatusDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public String getLastSyncDate(int userID, String type) {

        String lastSyncDate = "0";
        String[] whereArgs = null;
        String query = "SELECT MAX (CAST (last_sync_date AS LONG)) FROM " + DbAccess.TABLE_D_SYNC_STATUS +
                " where User_id=? and " + KEY_Type + "=?";

        whereArgs = new String[]{userID + "", type};
        Log.i(TAG, "Get LastSync Date query:" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                lastSyncDate = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getLastSyncDate:" + e.getMessage());
        }

        lastSyncDate = lastSyncDate == null ? "0" : lastSyncDate;

        return lastSyncDate;
    }

    public int truncateD_SyncStatus() {
        int ret = 0;


        ret = database.delete(DbAccess.TABLE_D_SYNC_STATUS, null, null);
        // ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
        return ret;
    }


    public void insertLastSyncDate(int userID, Long lastSyncDate, String type) {
        long ret = 0;
        database.beginTransaction();
        ContentValues values = new ContentValues();

        values.put(KEY_User_ID, userID);
        values.put(KEY_Last_SyncDate, lastSyncDate);
        values.put(KEY_Type, type);

        try {
            ret = database.insert(DbAccess.TABLE_D_SYNC_STATUS, null, values);
            database.setTransactionSuccessful();
            Log.i(TAG, "Inserted Rows in Last Sync:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error:" + e.getMessage());
        } finally {
            database.endTransaction();
        }
    }
}
