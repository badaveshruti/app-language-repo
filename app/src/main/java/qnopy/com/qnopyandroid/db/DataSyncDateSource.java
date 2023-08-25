package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import qnopy.com.qnopyandroid.clientmodel.DataSyncStatus;

public class DataSyncDateSource {

    private static final String KEY_SITE_ID = "siteId";
    private static final String KEY_EVENT_ID = "eventId";
    private static final String KEY_LAST_SYNC_DATE = "lastSyncDate";

    private Context mContext;
    private SQLiteDatabase database;
    private String TAG = "DataSyncStatusSource";

    public DataSyncDateSource(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public void insertDownloadDataSyncDate(DataSyncStatus dataSyncStatus) {

        database.beginTransaction();

        try {

            ContentValues values = new ContentValues();

            values.put(KEY_EVENT_ID, dataSyncStatus.getEventId());
            values.put(KEY_SITE_ID, dataSyncStatus.getSiteId());
            values.put(KEY_LAST_SYNC_DATE, dataSyncStatus.getLastSyncDate());

            try {
                database.insert(DbAccess.TABLE_DATA_SYNC_STATUS, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    public long getDataSyncTime(String eventID, String siteId) {

        String query = null;
        Cursor cursor;
        try {
            query = "select max(lastSyncDate) from data_sync_status where eventId = ? and siteId = ?";

            String[] whereArgs = {eventID, siteId};

            cursor = database.rawQuery(query, whereArgs);
            Log.i(TAG, "getFieldCount() d_FieldData query=" + query);

            if (cursor != null && cursor.moveToFirst()) {
                long lastSyncDate = cursor.getLong(0);
                long millisDiff = System.currentTimeMillis() - lastSyncDate;
                cursor.close();

                return millisDiff < 300000 ? millisDiff : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getDataSyncSync() exception:" + e.getLocalizedMessage());
            return 0;
        }

        Log.i(TAG, "getDataSyncSync() OUT time=" + System.currentTimeMillis());

        return 0;
    }

}
