package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.NotificationRow;

public class NotificationsDataSource {
    private String KEY_Notification_ID = "Notification_ID";
    private String KEY_Notification_Title = "Notification_Title";
    private String KEY_Notification_Info = "Notification_Info";
    private String KEY_Notification_Status = "Notification_Status";//0=NEW,1=ARCHIEVED,2=DELETED
    private String KEY_Notification_Date = "Notification_Date";
    private String KEY_Notification_OperationCode = "Operation_Code";
    private String KEY_UserID = "User_ID";
    private String KEY_FormID = "FormID";
    private String KEY_SiteID = "SiteID";
    private String KEY_EventID = "EventID";
    private String KEY_cocId = "cocId";
    private String KEY_fileName = "fileName";
    Context context;
    String TAG = "NotificationsDataSource";
    public SQLiteDatabase database;

    public NotificationsDataSource(Context context) {
        this.context = context;

        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public int updateNotificationStatus(String NotificationID, String NotificationStatus) {
        ContentValues values = new ContentValues();
        int ret = 0;

        String whereClause = KEY_Notification_ID + "=?";
        String[] whereArgs = {NotificationID};
        values.put(KEY_Notification_Status, NotificationStatus);

        try {

            ret = database.update(DbAccess.TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
            Log.i("NotificationStatus", "updateNotifcationStatus()  Ret value for update NotifcationStatus = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("NotificationStatus", "updateNotifcationStatus()  Ret value for updateNotifcation Status Error= " + e.getMessage());

        }

        return ret;
    }

    public void updateNotifcationStatusWhenDownloaded(String NotificationStatus, String userId, String formId, String siteId, String eventId) {
        int ret = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_Notification_Status, NotificationStatus);
        String whereClause = "User_ID=? and FormID=? and SiteID=? and EventID=?";
        String[] whereArgs = new String[]{"" + userId, "" + formId, "" + siteId, "" + eventId};
        try {
            ret = database.update(DbAccess.TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
            Log.e("NotificationStatus", "updateNotifcationStatusWhenDownloaded: updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAllDownloadFormNotifcationStatus(String alloperationCode, String userID) {
        ContentValues values = new ContentValues();
        int ret = 0;

        String whereClause = KEY_Notification_OperationCode + " IN (" + alloperationCode + ") AND (" + KEY_UserID + "=? OR " + KEY_UserID + "=0)";
        String[] whereArgs = {userID};
        values.put(KEY_Notification_Status, "1");

        try {

            ret = database.update(DbAccess.TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
            Log.i(TAG, "updateAllDownloadFormNotifcationStatus() Where clause:" + whereClause + "  ,Where Args=" + whereArgs);
            Log.i(TAG, "updateAllDownloadFormNotifcationStatus()  Ret value for update NotifcationStatus = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "updateAllDownloadFormNotifcationStatus()  Ret value for updateNotifcation Status Error= " + e.getMessage());

        }

    }

    public void insertNotifications(NotificationRow nData) {

        long ret = 0;

        ContentValues values = new ContentValues();

        values.put(KEY_Notification_Title, nData.title);
        values.put(KEY_Notification_Info, nData.info);
        values.put(KEY_Notification_Date, System.currentTimeMillis());
        values.put(KEY_Notification_Status, nData.status);//NEW
        values.put(KEY_Notification_OperationCode, nData.operationCode);
        values.put(KEY_UserID, nData.userID);
        values.put(KEY_FormID, nData.formID);
        values.put(KEY_SiteID, nData.siteID);
        values.put(KEY_EventID, nData.eventID);
        values.put(KEY_cocId, nData.cocId);
        values.put(KEY_fileName, nData.fileName);

        try {
            ret = database.insert(DbAccess.TABLE_NOTIFICATIONS, null, values);
            Log.i(TAG, "Ret val of insertNotifications = " + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("insertNotifications()", "insertNotifications() Exception:" + e.getMessage());
        }
    }

    public ArrayList<NotificationRow> getAllNotificationsAvailable(int userID) {

        ArrayList<NotificationRow> notifications = new ArrayList<>();
        NotificationRow item;
        String query = "select " + KEY_Notification_ID + "," + KEY_Notification_Title + "," + KEY_Notification_Info + "," +
                " CAST(" + KEY_Notification_Date + " AS LONG)," + KEY_Notification_Status + "," +
                KEY_Notification_OperationCode + "," + KEY_FormID + "," + KEY_SiteID + ","
                + KEY_EventID + "," + KEY_cocId + "," + KEY_fileName
                + " from " + DbAccess.TABLE_NOTIFICATIONS + " WHERE (" + KEY_UserID + "="
                + userID + " OR User_ID=0)";
//        String[] whereArgs = new String[]{"" + UserID};
        Log.i(TAG, "getAllNotificationsAvailable() query:" + query);
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    int row_id = cursor.getInt(0);
                    int status = cursor.getInt(4);
                    String row_title = cursor.getString(1);
                    String row_info = cursor.getString(2);
                    String row_date = cursor.getLong(3) + "";
                    int operation_code = Integer.parseInt(cursor.getString(5));
                    int form_id = cursor.getInt(6);
                    int site_id = cursor.getInt(7);
                    int event_id = cursor.getInt(8);
                    int cocId = cursor.getInt(9);
                    String fileName = cursor.getString(10);

                    item = new NotificationRow(row_id, row_title, row_info, status,
                            row_date, operation_code, userID, form_id, site_id, event_id,
                            fileName, cocId);
                    notifications.add(item);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllNotificationsAvailable() :" + e.getMessage());
        }

        return notifications;
    }


    public int[] getNotificationCount(int user) {
        int[] count = new int[2];

        String query = "select count(*) from notifications where (" + KEY_UserID + "=" + user + " OR User_ID=0)" + " AND Notification_Status=" + GlobalStrings.READ;
        String query1 = "select count(*) from notifications where (" + KEY_UserID + "=" + user + " OR User_ID=0)" + " AND Notification_Status=" + GlobalStrings.UNREAD;
//        String[] whereArgs = new String[]{"" + UserID};
        Log.i(TAG, "getNotification READ Count() query:" + query);
        Log.i(TAG, "getNotification UNREAD Count() query:" + query1);
        Cursor cursor = null;
        Cursor cursor1 = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                int read_count = cursor.getInt(0);

                count[0] = read_count;
                // make sure to close the cursor
                cursor.close();
            }

            cursor1 = database.rawQuery(query1, null);

            if (cursor1 != null && cursor1.moveToFirst()) {

                int unread_count = cursor1.getInt(0);

                count[1] = unread_count;
                // make sure to close the cursor
                cursor1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getNotificationCount() :" + e.getMessage());
        }

        return count;
    }

    public ArrayList<NotificationRow> getNotificationEventId(int id) {
        ArrayList<NotificationRow> notifications = new ArrayList<>();
        NotificationRow item;
        String query = "select " + KEY_Notification_Title + "," + KEY_Notification_Info + "," +
                " CAST(" + KEY_Notification_Date + " AS LONG)," + KEY_Notification_Status + "," +
                KEY_Notification_OperationCode + "," + KEY_UserID + ","
                + KEY_FormID + "," + KEY_SiteID + "," + KEY_EventID + "," + KEY_cocId
                + "," + KEY_fileName + " from "
                + DbAccess.TABLE_NOTIFICATIONS + " WHERE (" + KEY_Notification_ID + "=" + id + ")";
//        String[] whereArgs = new String[]{"" + UserID};
        Log.i(TAG, "getAllNotificationsAvailable() query:" + query);
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    int status = cursor.getInt(3);
                    String row_title = cursor.getString(0);
                    String row_info = cursor.getString(1);
                    String row_date = cursor.getLong(2) + "";
                    int operation_code = Integer.parseInt(cursor.getString(4));
                    int userID = cursor.getInt(5);
                    int form_id = cursor.getInt(6);
                    int site_id = cursor.getInt(7);
                    int event_id = cursor.getInt(8);
                    int cocId = cursor.getInt(9);
                    String fileName = cursor.getString(10);

                    item = new NotificationRow(id, row_title, row_info, status, row_date,
                            operation_code, userID, form_id, site_id, event_id, fileName, cocId);
                    notifications.add(item);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllNotificationsAvailable() :" + e.getMessage());
        }

        return notifications;
    }
}
