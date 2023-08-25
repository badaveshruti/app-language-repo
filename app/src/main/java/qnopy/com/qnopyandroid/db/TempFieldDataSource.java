package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.FieldData;
import qnopy.com.qnopyandroid.responsemodel.d_field_data_model;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 21-May-16.
 */
public class TempFieldDataSource {

    private static final String TAG = "TempFieldDataSource ";

    final String KEY_EventID = "eventId";
    final String KEY_LocationID = "locationId";
    final String KEY_FieldParameterID = "fieldParameterId";

    final String KEY_FieldParameterLabel = "fieldParameterLabel";
    final String KEY_MeasurementTime = "measurementTime";// LONG NOT NULL
    final String KEY_StringValue = "stringValue";// VARCHAR(100)
    final String KEY_NumericValue = "numericValue";// REAL
    final String KEY_Units = "units";// VARCHAR(50)
    final String KEY_Latitude = "latitude ";// REAL
    final String KEY_Longitude = "longitude";// REAL
    final String KEY_ExtField1 = "extField1";// VARCHAR(100)
    final String KEY_ExtField2 = "extField2";// VARCHAR(100)
    final String KEY_ExtField3 = "extField3";// VARCHAR(100)
    final String KEY_ExtField4 = "extField4";// VARCHAR(100)
    final String KEY_ExtField5 = "extField5";// VARCHAR(100)
    final String KEY_ExtField6 = "extField6";// VARCHAR(100)
    final String KEY_ExtField7 = "extField7";// VARCHAR(100)

    final String KEY_Notes = "notes";         // VARCHAR(200)
    final String KEY_ServerCreationDate = "server_creation_date";// long
    final String KEY_ServerModificationDate = "server_modification_date";

    final String KEY_ModificationDate = "modificationDate"; // long
    final String KEY_CreationDate = "creationDate";// long

    final String KEY_EmailSentFlag = "emailSentFlag";// VARCHAR(1)
    final String KEY_DataSyncFlag = "dataSyncFlag";// VARCHAR(1)
    final String KEY_CorrectedLat = "correctedLatitude";
    final String KEY_CorrectedLong = "correctedLongitude";

    final String KEY_FieldDataID = "fieldDataId";// INTEGER PRIMARY KEY AUTOINCREMENT

    final String KEY_SiteID = "siteId";
    final String KEY_MobileAppID = "mobileAppId";
    final String KEY_UserID = "userId";

    final String KEY_DeviceID = "deviceId";
    final String KEY_ParentSetID = "parent_set_id";

    // TODO: 21-Jul-16
    final String KEY_modifiedByDeviceId = "modifiedByDeviceId";
    final String KEY_modifiedBy = "modifiedBy";


    public SQLiteDatabase database;
    Context mContext;

    public TempFieldDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public int deleteRow(List<FieldData> deleteIDs) {
        int ret = 0;
        for (FieldData row : deleteIDs) {
            String whereClause = KEY_FieldDataID + "=?";
            String[] whereArgs = new String[]{row.getFieldDataID() + ""};
            ret = database.delete(DbAccess.TABLE_TEMP_D_FIELD_DATA, whereClause, whereArgs);
            // ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
        }
        return ret;
    }

    public int truncateD_fieldDataTemp() {
        int ret = 0;
        ret = database.delete(DbAccess.TABLE_TEMP_D_FIELD_DATA, null, null);
        // ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
        return ret;
    }


    public int truncateD_fieldDataTempForEvent(int eventID) {
        int ret = 0;
        String where = KEY_EventID + "=" + eventID;
        ret = database.delete(DbAccess.TABLE_TEMP_D_FIELD_DATA, where, null);
        // ret = database.delete(DbAccess.TABLE_S_PROJECT_FILE, KEY_site_id + "=?", new String[]{siteId});
        return ret;
    }

    public void saveCityDataList(List<d_field_data_model> dataList) {
        long ret = 0;
        database.beginTransaction();
        d_field_data_model fieldData;
        try {
            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                fieldData = dataList.get(i);
                values.put(KEY_EventID, fieldData.getEventId());
                values.put(KEY_LocationID, fieldData.getLocationId());
                values.put(KEY_FieldParameterID, fieldData.getFieldParameterId());
                values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
                values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
                values.put(KEY_StringValue, fieldData.getStringValue());
//                values.put(KEY_Units, fieldData.getUnits());
//                values.put(KEY_Latitude, fieldData.getLatitude());
//                values.put(KEY_Longitude, fieldData.getLongitude());
                values.put(KEY_ExtField1, fieldData.getExtField1());
                if (fieldData.getExtField2() != null) {
                    values.put(KEY_ExtField2, fieldData.getExtField2());
                }
//                if (fieldData.getExtField4() != null) {
//                    values.put(KEY_ExtField4, fieldData.getExtField4());
//                }
                values.put(KEY_Notes, fieldData.getNotes());
                if (fieldData.getCreationDate() != null && Double.parseDouble(fieldData.getCreationDate()) != 0) {
                    values.put(KEY_CreationDate, fieldData.getCreationDate());
                }
                values.put(KEY_SiteID, fieldData.getSiteId());
                int userID = 0;
                userID = Integer.parseInt(fieldData.getUserId());
                if (userID < 1) {
                    userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
                }
                Log.i(TAG, "USerID:" + userID);

                values.put(KEY_UserID, userID);
                values.put(KEY_MobileAppID, fieldData.getMobileAppId());

                ret = database.insert(DbAccess.TABLE_FIELD_DATA, null, values);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
    }

    public void insertTempFieldDataList(List<d_field_data_model> dataList) {
        long ret = 0;
        database.beginTransaction();
        d_field_data_model fieldData;

        try {
            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                fieldData = dataList.get(i);

                values.put(KEY_EventID, fieldData.getEventId());
                values.put(KEY_LocationID, fieldData.getLocationId());
                values.put(KEY_FieldParameterID, fieldData.getFieldParameterId());
                values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
                values.put(KEY_StringValue, fieldData.getStringValue());
                values.put(KEY_Notes, fieldData.getNotes());
                values.put(KEY_SiteID, fieldData.getSiteId());
                values.put(KEY_MobileAppID, fieldData.getMobileAppId());
                values.put(KEY_ExtField1, fieldData.getExtField1());
                values.put(KEY_ParentSetID, fieldData.getParentSetId());
                values.put(KEY_FieldDataID, fieldData.getFieldDataId());
                values.put(KEY_modifiedByDeviceId, fieldData.getModifiedByDeviceId());
                values.put(KEY_modifiedBy, fieldData.getModifiedBy());

                values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
                values.put(KEY_DeviceID, fieldData.getDeviceId());
                // TODO: 29-Jul-16
                values.put(KEY_ExtField4, fieldData.getExtField4());


                if (fieldData.getExtField2() != null) {
                    values.put(KEY_ExtField2, fieldData.getExtField2());
                }

                if (fieldData.getExtField3() != null) {
                    values.put(KEY_ExtField3, fieldData.getExtField3());
                }

                if (!fieldData.getCreationDate().isEmpty() && !fieldData.getCreationDate().equals("0")) {
                    values.put(KEY_CreationDate, fieldData.getCreationDate());
                }
                if (!fieldData.getModificationDate().isEmpty() && !fieldData.getModificationDate().equals("0")) {
                    values.put(KEY_ModificationDate, fieldData.getModificationDate());
                }

                int userID = Integer.parseInt(fieldData.getUserId());
                if (userID < 1) {
                    userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
                }

                Log.i(TAG, "USerID:" + userID);
                values.put(KEY_UserID, userID);

                ret = database.insert(DbAccess.TABLE_TEMP_D_FIELD_DATA, null, values);
                Log.i(TAG, "Inserted Row TempFieldData:" + ret);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {

            e.printStackTrace();
            Log.e(TAG, "insertTempFieldDataList Error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
    }


    public void insertConflictFieldDataList(List<FieldData> dataList) {
        long ret = 0;
        database.beginTransaction();
        FieldData fieldData;

        try {
            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                fieldData = dataList.get(i);

                values.put(KEY_EventID, fieldData.getEventID());
                values.put(KEY_LocationID, fieldData.getLocationID());
                values.put(KEY_FieldParameterID, fieldData.getFieldParameterID());
                values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
                values.put(KEY_StringValue, fieldData.getStringValue());
                values.put(KEY_Notes, fieldData.getNotes());
                values.put(KEY_SiteID, fieldData.getSiteID());
                values.put(KEY_MobileAppID, fieldData.getMobileAppID());
                values.put(KEY_ExtField1, fieldData.getExtField1());
                values.put(KEY_ParentSetID, fieldData.getParent_set_id());
                values.put(KEY_FieldDataID, fieldData.getFieldDataID());

                if (fieldData.getExtField2() != null) {
                    values.put(KEY_ExtField2, fieldData.getExtField2());
                }

                if (fieldData.getExtField3() != null) {
                    values.put(KEY_ExtField3, fieldData.getExtField3());
                }

                if (fieldData.getCreationDate() != 0) {
                    values.put(KEY_CreationDate, fieldData.getCreationDate());
                }
                if (fieldData.getModificationDate() != 0) {
                    values.put(KEY_ModificationDate, fieldData.getModificationDate());
                }

                int userID = fieldData.getUserID();
                if (userID < 1) {
                    userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
                }

                Log.i(TAG, "USerID:" + userID);
                values.put(KEY_UserID, userID);


                ret = database.insert(DbAccess.TABLE_D_FIELD_DATA_CONFLICT, null, values);
                Log.i(TAG, "Inserted Row d_FieldData_Conflict:" + ret);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {

            e.printStackTrace();
            Log.e(TAG, "insertTempFieldDataList Error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
    }


    public List<FieldData> getUpdatableDataFrom_DFieldDataTemp() {
        List<FieldData> dataList = new ArrayList<FieldData>();

        String query = null;

        query = " select * from d_FieldData d cross join  d_field_data_temp t " +
                " WHERE  d.LocationID = t.locationId " +
                " and d.MobileAppID = t.mobileAppId and " +
                " d.FieldParameterID = t.fieldParameterId " +
                " and d.deviceId = t.deviceId and d.extField1 = t.extField1 and d.EventID = t.eventId " +
                " and ((d.ModificationDate < t.modificationDate) OR (d.ModificationDate ISNULL AND t.modificationDate IS NOT NULL))";

        Log.i(TAG, "getUpdatableDataFrom_DFieldDataTemp Query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FieldData data = cursorToFieldData(cursor);
                    dataList.add(data);
                } while (cursor.moveToNext());

                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getUpdatableDataFrom_DFieldDataTemp Error:" + e.getMessage());
        }

        return dataList;
    }


//    public List<FieldData> collectUserWiseDataFrom_DFieldDataTemp() {
//        List<FieldData> dataList = new ArrayList<FieldData>();
//
//        String query = null;
//
//        query = "select * from d_field_data_temp  " +
//                " WHERE  d.SiteID = t.siteId and d.LocationID = t.locationId " +
//                " and d.MobileAppID = t.mobileAppId and " +
//                " d.FieldParameterID = t.fieldParameterId " +
//                " and d.UserID = t.userId ";
//
//        Log.i(TAG, "getUpdatableDataFrom_DFieldDataTemp Query=" + query);
//
//        Cursor cursor = null;
//        try {
//            cursor = database.rawQuery(query, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//                    FieldData data = cursorToFieldData(cursor);
//                    dataList.add(data);
//                } while (cursor.moveToNext());
//
//                cursor.close();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "getUpdatableDataFrom_DFieldDataTemp Error:" + e.getMessage());
//        }
//
//        return dataList;
//    }
//
//    public List<FieldData> collectConflictDataFrom_DFieldDataTemp() {
//        List<FieldData> dataList = new ArrayList<FieldData>();
//
//        String query = null;
//
//        query = "select * from d_FieldData d cross join  d_field_data_temp t WHERE  " +
//                "d.SiteID = t.siteId and d.LocationID = t.locationId and d.MobileAppID = t.mobileAppId " +
//                "and d.fieldParameterId = t.fieldParameterId and d.extField1 = t.extField1" +
//                " and (d.stringValue IS NOT NULL OR d.stringValue != '<null>')";
//
//        Log.i(TAG, "collect Conflict DataFrom_DFieldDataTemp Query=" + query);
//
//        Cursor cursor = null;
//        try {
//            cursor = database.rawQuery(query, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//
//                    FieldData data = cursorToFieldData(cursor);
//                    dataList.add(data);
//                } while (cursor.moveToNext());
//
//                cursor.close();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "collectConflictDataFrom_DFieldDataTemp Error:" + e.getMessage());
//        }
//
//        return dataList;
//    }

    public boolean insertNewEventFromTemp() {


        String query = null;

        query = "insert into d_Event(EventID,EventStatus,MobileAppID,SiteID,DeviceID,EventDate,UserID,GeneratedBy) " +
                "select DISTINCT t.eventId,1,sm.roll_into_app_id,t.siteId ,-9999,-9999,-9999,'S' \n" +
                "from d_field_data_temp t,s_SiteMobileApp sm \n" +
                " where eventId NOT IN( Select EventID from d_Event) AND t.mobileAppId=sm.MobileAppID AND t.siteId=sm.SiteID group by 1 ";

        Log.i(TAG, "insertNewEventFromTemp Query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null) {
                Log.i(TAG, " insertNewEventFromTemp Cursor Count:" + cursor.getCount());
                cursor.close();
            }
            return true;


        } catch (Exception e) {

            e.printStackTrace();
            Log.e(TAG, "insert NewEvent From Temp Error:" + e.getMessage());
            return false;
        }

    }

    public int moveFieldDataListinTemp(List<FieldData> fieldDataList,int eventID) {
        int res = truncateD_fieldDataTempForEvent(eventID);
        Log.i(TAG, "moveFieldDataListinTemp() truncateD_fieldDataTempForEvent result:" + res);

        // TODO: 03-Jul-17 TRUNCATED DATA From TEMP
        int ret = 0;

        database.beginTransaction();
        FieldData fieldData;

        try {

            for (int i = 0; i < fieldDataList.size(); i++) {
                ContentValues values = new ContentValues();
                fieldData = fieldDataList.get(i);
                String value = fieldData.getStringValue();
                values.put(KEY_EventID, fieldData.getEventID());
                values.put(KEY_LocationID, fieldData.getLocationID());
                values.put(KEY_FieldParameterID, fieldData.getFieldParameterID());
                values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
                values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
                values.put(KEY_StringValue, value == null ? value : value.trim());
                values.put(KEY_Units, fieldData.getUnits());
                values.put(KEY_Latitude, fieldData.getLatitude());
                values.put(KEY_Longitude, fieldData.getLongitude());
                values.put(KEY_ExtField1, fieldData.getExtField1());
                values.put(KEY_DeviceID, fieldData.getDeviceId());
                values.put(KEY_Notes, fieldData.getNotes());
                values.put(KEY_SiteID, fieldData.getSiteID());
                values.put(KEY_MobileAppID, fieldData.getMobileAppID());
                values.put(KEY_DataSyncFlag, fieldData.getDataSyncFlag());
                values.put(KEY_modifiedByDeviceId, fieldData.getModifiedByDeviceId());
                values.put(KEY_modifiedBy, fieldData.getModifiedBy());
                values.put(KEY_FieldDataID, fieldData.getFieldDataID());

                int userID = fieldData.getUserID();
                if (userID < 1) {
                    userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
                }

                Log.i(TAG, "UserID:" + userID);
                values.put(KEY_UserID, userID);

                if (fieldData.getExtField2() != null) {
                    values.put(KEY_ExtField2, fieldData.getExtField2());
                }

                if (fieldData.getExtField3() != null) {
                    values.put(KEY_ExtField3, fieldData.getExtField3());
                }

                if (fieldData.getExtField4() != null) {
                    values.put(KEY_ExtField4, fieldData.getExtField4());
                }
                if (fieldData.getCreationDate() != 0) {
                    values.put(KEY_CreationDate, fieldData.getCreationDate());
                }

                ret = (int) database.insert(DbAccess.TABLE_TEMP_D_FIELD_DATA, null, values);
                Log.i(TAG, "Insert new Data List Return Count:" + ret);
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


    public HashMap<String, String> collectSourceUserNDeviceFrom_DFieldDataTemp() {
        HashMap<String, String> replaceList = new HashMap<String, String>();

        String query = null;

        query = " select DISTINCT t.deviceId,t.UserID,t.LocationID,t.eventId,t.mobileAppId from d_FieldData d,d_field_data_temp t " +
                " where d.LocationID=t.locationId and d.MobileAppID=t.mobileAppId " +
                " and d.EventID=t.eventId and t.creationDate < d.CreationDate ";

        Log.i(TAG, "collect Source User N Device From _DFieldDataTemp Query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String devID = cursor.getString(0);
                    String userID = cursor.getString(1);
                    String locID = cursor.getString(2);
                    String eventID = cursor.getString(3);
                    String mobID = cursor.getString(4);

                    replaceList.put("USER_ID", userID);
                    replaceList.put("DEVICE_ID", devID);
                    replaceList.put("LOCATION_ID", locID);
                    replaceList.put("EVENT_ID", eventID);
                    replaceList.put("MOB_ID", mobID);
                } while (cursor.moveToNext());

                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "collectSourceUserNDeviceFrom_DFieldDataTemp Error:" + e.getMessage());
            return null;
        }

        return replaceList;
    }

    public int updateUserNDevice_IN_TEMP(HashMap<String, String> rList) {
        int ret = 0;

        String devID, userID, locationID, mobAppID, eventID;

        if (rList != null && rList.size() > 0) {


            devID = rList.get("DEVICE_ID");
            userID = rList.get("USER_ID");
            locationID = rList.get("LOCATION_ID");
            mobAppID = rList.get("MOB_ID");
            eventID = rList.get("EVENT_ID");

            ContentValues values = new ContentValues();

            values.put(KEY_UserID, userID);
            values.put(KEY_DeviceID, devID);


            String whereClause = "locationId=? and eventId=? and mobileAppId=? ";// AND MobileAppID=?
            String[] whereArgs = new String[]{locationID, eventID, mobAppID};//, "" + appID
            try {
                ret = database.update(DbAccess.TABLE_TEMP_D_FIELD_DATA, values, whereClause, whereArgs);
                Log.i(TAG, "update User N Device_IN_TEMP :" + ret);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "updateUserNDevice_IN_TEMP Error:" + e.getMessage());
            }
        }
        return ret;
    }

    public List<FieldData> collectNewDataFrom_DFieldDataTemp() {
        List<FieldData> dataList = new ArrayList<FieldData>();

        String query = null;

        query = "select * from d_field_data_temp";

        Log.i(TAG, "collectNewDataFrom_DFieldDataTemp Query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FieldData data = cursorToFieldData(cursor);
                    dataList.add(data);
                } while (cursor.moveToNext());

                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "collectConflictDataFrom_DFieldDataTemp Error:" + e.getMessage());
        }

        return dataList;
    }

    private FieldData cursorToFieldData(Cursor cursor) {
        FieldData data = new FieldData();

        try {
            data.setEventID(cursor.getInt(0));
            data.setLocationID(cursor.getString(1));
            data.setFieldParameterID(cursor.getInt(2));
            data.setSiteID(cursor.getInt(3));
            data.setUserID(cursor.getInt(4));
            data.setMobileAppID(cursor.getInt(5));
            data.setFieldParameterLabel(cursor.getString(6));
            data.setMeasurementTime(cursor.getLong(7));
            data.setStringValue(cursor.getString(8));
            String numeric = cursor.getString(9);
            if (numeric != null && !numeric.isEmpty()) {
                data.setNumericValue(Double.parseDouble(numeric));
            }

            data.setUnits(cursor.getString(10));
            String lat = cursor.getString(11);
            String longi = cursor.getString(12);

            if (lat != null && !lat.isEmpty()) {
                data.setLatitude(Double.parseDouble(lat));
            }

            if (longi != null && !longi.isEmpty()) {
                data.setLongitude(Double.parseDouble(longi));
            }

            data.setExtField1(cursor.getString(13));
            data.setExtField2(cursor.getString(14));
            data.setExtField3(cursor.getString(15));
            data.setExtField4(cursor.getString(16));
            data.setExtField5(cursor.getString(17));
            data.setExtField6(cursor.getString(18));
            data.setExtField7(cursor.getString(19));
            data.setNotes(cursor.getString(20));

            String creationDate = cursor.getString(21);
            String modificationDate = cursor.getString(22);

            if (creationDate != null) {
                data.setCreationDate(Long.parseLong(creationDate));
            }
            if (modificationDate != null) {
                data.setCreationDate(Long.parseLong(modificationDate));
            }

            data.setFieldDataID(cursor.getInt(25));
            data.setParent_set_id(cursor.getInt(26));
            data.setCorrectedLongitude(cursor.getInt(27));
            data.setCorrectedLatitude(cursor.getInt(28));
            data.setDeviceId(cursor.getString(31));
            data.setModifiedByDeviceId(cursor.getString(32));
            data.setModifiedBy(cursor.getString(33));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "cursorToFieldData" + e.getLocalizedMessage());
        }

        return data;
    }
}



