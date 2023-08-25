package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import qnopy.com.qnopyandroid.clientmodel.LogDetails;

public class TempLogsDataSource {

    private static final String KEY_DATE = "date";
    private static final String KEY_SCREEN_NAME = "screen_name";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_ALL_IDS = "allIds";

    private Context mContext;
    private SQLiteDatabase database;

    public TempLogsDataSource(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public void insertTempLogs(LogDetails logData) {

        database.beginTransaction();

        try {

            ContentValues values = new ContentValues();

            values.put(KEY_DATE, logData.getDate());
            values.put(KEY_SCREEN_NAME, logData.getScreenName());
            values.put(KEY_DETAILS, logData.getDetails());
            values.put(KEY_ALL_IDS, logData.getAllIds());

            try {
                database.insert(DbAccess.TABLE_LOGS_DATA, null, values);
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
}
