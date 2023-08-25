package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.ChangeEventModel;
import qnopy.com.qnopyandroid.clientmodel.CopyTemplateModel;
import qnopy.com.qnopyandroid.clientmodel.FieldData;
import qnopy.com.qnopyandroid.clientmodel.FieldParamInfo;
import qnopy.com.qnopyandroid.clientmodel.FormQueryData;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.ReportTable;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.requestmodel.DFieldData;
import qnopy.com.qnopyandroid.requestmodel.EventFieldData;
import qnopy.com.qnopyandroid.requestmodel.FieldDataForEventDownload;
import qnopy.com.qnopyandroid.requestmodel.LocFormStatus;
import qnopy.com.qnopyandroid.responsemodel.CSVDataModel;
import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;
import qnopy.com.qnopyandroid.ui.forms.FormsAdapter;
import qnopy.com.qnopyandroid.uiutils.FormMaster;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class FieldDataSource {

    private static final String TAG = "FieldDataSource ";
    final int All = 0;
    final int MailNotSent = 1;
    final int NotSynced = 2;

    enum FieldDataColsBulkInsert {
        KEY_EventID(1), KEY_LocationID(2), KEY_FieldParameterID(3),
        KEY_FieldParameterLabel(4), KEY_MeasurementTime(5), KEY_StringValue(6), KEY_oldStringValue(7),
        KEY_oldNote(8), KEY_Latitude(9), KEY_Longitude(10), KEY_ExtField1(11), KEY_DeviceID(12), KEY_SiteID(13),
        KEY_UserID(14), KEY_MobileAppID(15), KEY_ExtField2(16), KEY_ExtField3(17), KEY_ExtField4(18),
        KEY_CreationDate(19), KEY_ViolationFlag(20), KEY_DataSyncFlag(21), KEY_fieldUUID(22);

        private final int index;

        public int getIndex() {
            return index;
        }

        FieldDataColsBulkInsert(int value) {
            this.index = value;
        }
    }

    final String KEY_EventID = "EventID";// INT NOT NULL REFERENCES d_Event (EventID)
    final String KEY_LocationID = "LocationID";// INT NOT NULL REFERENCES s_Location (LocationID)
    final String KEY_FieldParameterID = "FieldParameterID";// INT NOT NULL REFERENCES s_FieldParameter
    // (FieldParameterID)
    final String KEY_FieldParameterLabel = "FieldParameterLabel";
    final String KEY_MeasurementTime = "MeasurementTime";// LONG NOT NULL
    final String KEY_StringValue = "StringValue";// VARCHAR(100)
    final String KEY_oldStringValue = "oldStringValue";// VARCHAR(4000)
    final String KEY_oldNote = "oldNote";// VARCHAR(4000)
    final String KEY_NumericValue = "NumericValue";// REAL
    final String KEY_Units = "Units";// VARCHAR(50)
    final String KEY_Latitude = "Latitude";// REAL
    final String KEY_Longitude = "Longitude";// REAL
    final String KEY_ExtField1 = "ExtField1";// VARCHAR(100)
    final String KEY_ExtField2 = "ExtField2";// VARCHAR(100)
    final String KEY_ExtField3 = "ExtField3";// VARCHAR(100)
    final String KEY_ExtField4 = "ExtField4";// VARCHAR(100)
    final String KEY_ExtField5 = "ExtField5";// VARCHAR(100)
    final String KEY_ExtField6 = "ExtField6";// VARCHAR(100)
    final String KEY_ExtField7 = "ExtField7";// VARCHAR(100)
    final String KEY_Notes = "Notes";// VARCHAR(200)
    final String KEY_CreationDate = "CreationDate";// long

    final String KEY_ModificationDate = "ModificationDate";

    final String KEY_EmailSentFlag = "EmailSentFlag";// VARCHAR(1)
    final String KEY_DataSyncFlag = "DataSyncFlag";// VARCHAR(1)
    final String KEY_CorrectedLat = "CorrectedLatitude";
    final String KEY_CorrectedLong = "CorrectedLongitude";

    final String KEY_FieldDataID = "FieldDataID";// INTEGER PRIMARY KEY AUTOINCREMENT

    final String KEY_SiteID = "SiteID";
    final String KEY_MobileAppID = "MobileAppID";
    final String KEY_UserID = "UserID";
    final String KEY_StatusID = "StatusID";
    final String KEY_DeviceID = "deviceId";

    //21-Jul-16
    final String KEY_modifiedByDeviceId = "modifiedByDeviceId";
    final String KEY_modifiedBy = "modifiedBy";
    final String KEY_COPY_STATUS = "copy_status";
    final String KEY_COPY_FROM = "copy_from";
    final String KEY_bottlesCheckOptions = "bottlesCheckOptions";
    final String KEY_ViolationFlag = "violationFlag";
    final String KEY_fieldUUID = "fieldUUID";

    public SQLiteDatabase database;
    Context mContext;
    static final String DATA_SYNC_FLAG = "1";
    static final String DATA_SYNC_UPDATE_FLAG = "2";

    @Inject
    public FieldDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        SQLiteDatabase.releaseMemory();
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    //clear event table data 11 Jan, 23
    public void truncateFieldTable() {
        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            try {
                ret = database.delete(DbAccess.TABLE_FIELD_DATA, null, null);
                Log.i(TAG, "deleted table name :" + DbAccess.TABLE_FIELD_DATA);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Caught for Table name=" + DbAccess.TABLE_FIELD_DATA + ret);
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

    //11 Jan, 23
    public boolean deleteEventData(String eventId) {
        String query = "delete from " + DbAccess.TABLE_FIELD_DATA + " where EventID =" + eventId;
        try {
            database.execSQL(query);
        } catch (Exception e) {
            System.out.println("deleteFieldData " + e.getLocalizedMessage());
        }

        return true;
    }

    public void updateset(String locationID, int eventID, int AppID, int siteID, int curSetID) {
        Cursor c = null;

        String query = "update d_FieldData set ExtField1=CAST(ExtField1 AS INTEGER)-1 where " +
                "LocationID=" + locationID + " and EventID =" + eventID +
                " and SiteID=" + siteID + " and MobileAppID=" + AppID +
                " and CAST(ExtField1 AS INTEGER)>" + curSetID;

        Log.i(TAG, " updateset() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updateset() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    public String hitExpressionQuery(String query) {
        String value = "";

        try {
            Cursor c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                value = c.getString(0);
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public HashMap<String, FormQueryData> hitFormQuery(String query) {
        HashMap<String, FormQueryData> queryData = new HashMap<>();
        try {
            Cursor c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    FormQueryData data = new FormQueryData();
                    data.setLocationId(c.getString(c.getColumnIndexOrThrow("LocationID")));
                    data.setAlert(c.getString(c.getColumnIndexOrThrow("alert")));
                    data.setColor(c.getString(c.getColumnIndexOrThrow("color")));
                    data.setStatusMessage(c.getString(c.getColumnIndexOrThrow("status_message")));
                    queryData.put(data.getLocationId(), data);
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryData;
    }

    public COCExpressionResults hitCOCExpressionQuery(String query) {
        COCExpressionResults results = new COCExpressionResults();
        Cursor c = database.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            results.setStringValue(c.getString(0));
            results.setDate(c.getString(1));
            results.setTime(c.getString(2));
            c.close();
        }
        return results;
    }

    public String getShowFieldsStringValues(String siteId, String locId,
                                            String mobileAppId, String setId, String fieldParamIds) {
        String queryShowFieldIds = "select GROUP_CONCAT(StringValue) from d_FieldData where " +
                "SiteID = ? and LocationID = ? and MobileAppID = ? and " +
                "FieldParameterID in (" + fieldParamIds + ") and ExtField1 = ? and " +
                "StringValue IS NOT NULL and StringValue !=''";

        String[] whereArgs = {siteId, locId, mobileAppId, setId};

        String showFieldIds = "";

        Cursor cursorShowFields = database.rawQuery(queryShowFieldIds, whereArgs);
        if (cursorShowFields != null && cursorShowFields.moveToFirst()) {
            do {
                showFieldIds = cursorShowFields.getString(0);
            } while (cursorShowFields.moveToNext());
            cursorShowFields.close();
        }
        return showFieldIds;
    }

    public ArrayList<MetaData> getShowFields(String mobileAppId) {
        String queryShowFieldIds = "select distinct FieldParameterID, MobileAppID, ParameterLabel, " +
                "substr(field_action, instr(field_action, '|') + 1, length(field_action)) as sortOrder " +
                "from s_MetaData where MobileAppID = " + mobileAppId +
                " and lower(field_action) like '%show%' and (Status IS NULL or Status=1) order by sortOrder";

        ArrayList<MetaData> listShowFieldIds = new ArrayList<>();

        Cursor cursorShowFields = database.rawQuery(queryShowFieldIds, null);
        if (cursorShowFields != null && cursorShowFields.moveToFirst()) {
            do {
                MetaData metaData = new MetaData();
                metaData.setMetaParamID(cursorShowFields.getInt(0));
                metaData.setFormID(cursorShowFields.getInt(1));
                metaData.setMetaParamLabel(cursorShowFields.getString(2));
                listShowFieldIds.add(metaData);
            } while (cursorShowFields.moveToNext());
            cursorShowFields.close();
        }
        return listShowFieldIds;
    }

    public ArrayList<DFieldData> getDepthFieldsStringValues(String siteId, String locId,
                                                            String eventId, MetaData metaData) {
        String queryDepthFields = "select FieldParameterLabel, CAST(round(StringValue) as INT) " +
                "as StringValue, FieldParameterID, extField1 from d_FieldData where " +
                "EventID = ? and SiteID = ? and MobileAppID = ? and LocationID = ? " +
                "and FieldParameterID = ?";

        String[] whereArgs = {eventId, siteId, metaData.getFormID() + "", locId,
                metaData.getMetaParamID() + ""};

        ArrayList<DFieldData> listDepthFields = new ArrayList<>();

        Cursor cursorDepthFields = database.rawQuery(queryDepthFields, whereArgs);
        if (cursorDepthFields != null && cursorDepthFields.moveToFirst()) {
            do {
                DFieldData fieldData = new DFieldData();
                fieldData.setFieldParameterLabel(cursorDepthFields.getString(0));
                fieldData.setStringValue(cursorDepthFields.getString(1));
                fieldData.setFieldParameterId(cursorDepthFields.getInt(2));
                fieldData.setSetId(cursorDepthFields.getInt(3));
                listDepthFields.add(fieldData);
            } while (cursorDepthFields.moveToNext());
            cursorDepthFields.close();
        }
        return listDepthFields;
    }

    public HashMap<Integer, MetaData> getDepthFields(String mobileAppIds) {
        String queryDepthFieldIds = "select distinct FieldParameterID, MobileAppID, ParameterLabel " +
                "from s_MetaData where MobileAppID in (" + mobileAppIds + ") " +
                "and lower(field_action) = 'depth' and (Status IS NULL or Status=1)";
        HashMap<Integer, MetaData> mapDepthFields = new HashMap<>();

        Cursor cursorDepthFields = database.rawQuery(queryDepthFieldIds, null);
        if (cursorDepthFields != null && cursorDepthFields.moveToFirst()) {
            do {
                MetaData metaData = new MetaData();
                metaData.setMetaParamID(cursorDepthFields.getInt(0));
                metaData.setFormID(cursorDepthFields.getInt(1));
                metaData.setMetaParamLabel(cursorDepthFields.getString(2));
                mapDepthFields.put(metaData.getFormID(), metaData);
            } while (cursorDepthFields.moveToNext());
            cursorDepthFields.close();
        }
        return mapDepthFields;
    }

    public int getMaxSoilLogDepth(@NotNull String siteId, @NotNull String locId,
                                  @NotNull String eventId, String mobileAppIds, String depthFieldIds) {

        String query = "select MAX(CAST (round(StringValue) as INT) ) from d_FieldData where SiteID = ? " +
                "and EventID = ? and LocationID = ? and FieldParameterID in (" + depthFieldIds + ")";

        String[] whereArgs = {siteId, eventId, locId};

        Cursor c = database.rawQuery(query, whereArgs);
        int maxDepth = 0;
        if (c != null && c.moveToFirst()) {
            maxDepth = c.getInt(0);
            c.close();
        }
        return maxDepth;
    }

    public class COCExpressionResults {
        private String stringValue;
        private String date;
        private String time;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public boolean isDataAvailable(int eventID, int siteID) {

        boolean result = false;
        //    String query="select count(*) from d_FieldData where EventID="+eventID+" and SiteID="+siteID+" and StringValue!=null and (FieldParameterID!=15 and FieldParameterID!=25)";

        String query = "select count(*) from d_FieldData where " +
                "EventID=" + eventID + " and SiteID=" + siteID + " and " +
                "StringValue is not null and FieldParameterID not in (15,25)";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            int cnt = Integer.parseInt(c.getString(0));
            result = cnt > 0;
            c.close();
        }
        return result;
    }

    public boolean isDataAvailableForSite(String siteID) {

        boolean result = false;

        String query = "select count(*) from d_FieldData where " +
                "SiteID= " + siteID + " and " +
                "StringValue is not null and FieldParameterID not in (15,25)";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            int cnt = Integer.parseInt(c.getString(0));
            result = cnt > 0;
            c.close();
        }
        return result;
    }

    public boolean isSetGeneratedAlready(String siteID,
                                         String eventID, String locID, int formID, int fieldParamID, String setvalue) {

        boolean result = false;
        //    String query="select count(*) from d_FieldData where EventID="+eventID+" and SiteID="+siteID+" and StringValue!=null and (FieldParameterID!=15 and FieldParameterID!=25)";

        String query = "select count(*) from d_FieldData where " +
                " EventID=" + eventID + " and SiteID=" + siteID + " and " + KEY_LocationID + "=" + locID + " and " +
                KEY_MobileAppID + "=" + formID + " and " + KEY_FieldParameterID + "=" + fieldParamID +
                " and StringValue ='" + setvalue.trim() + "' COLLATE NOCASE";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            int cnt = Integer.parseInt(c.getString(0));
            result = cnt > 0;
            c.close();
        }
        return result;
    }

    //27 Aug, 2020
    public float getTotalPercentageFilledOfForms(String siteID,
                                                 String rollIntoAppId, String locationId, String eventId) {

        boolean result = false;
        //    String query="select count(*) from d_FieldData where EventID="+eventID+" and SiteID="+siteID+" and StringValue!=null and (FieldParameterID!=15 and FieldParameterID!=25)";

        String query = "select a.FieldParameterId, a.locationIds, b.SiteID, b.roll_into_app_id, " +
                "a.FieldInputType from s_MetaData a, s_SiteMobileApp b where a.MobileAppID =b.MobileAppID " +
                "and b.SiteID= ? and b.roll_into_app_id= ? and a.FieldParameterID not in (15,25) " +
                "and a.FieldInputType != '' and UPPER(a.ParameterLabel) != 'NOTES'";

        String[] whereArgs = {siteID, rollIntoAppId};

        Cursor c = database.rawQuery(query, whereArgs);

        int totalMetaFields = 0;
        if (c != null && c.moveToFirst()) {
            do {
                String locIds = c.getString(1);
                String[] splitArray = locIds.split(",");
                Set<String> set = new HashSet<>(Arrays.asList(splitArray));
                String inputType = c.getString(4);

                if (inputType == null)
                    inputType = "";

                if ((set.contains(locationId) || locIds.equals("0"))
                        && !(inputType.isEmpty() || inputType.equals("PHOTOS")))
                    totalMetaFields++;
            } while (c.moveToNext());
            c.close();
        }

        float fieldStringCount = getTotalFieldDataStringValuesFilled(siteID, rollIntoAppId, locationId, eventId);

        if (totalMetaFields != 0) {
            return fieldStringCount * 100 / totalMetaFields;
        } else {
            return totalMetaFields;
        }
    }

    public boolean hasFieldDataForEvent(String siteID, String eventId) {

        String query = "select count(*) from d_FieldData where EventID = ? and SiteID = ? " +
                "and (StringValue is not null or StringValue !='') and (FieldParameterID != 15 " +
                "and FieldParameterID != 25)";

        String[] whereArgs = {eventId, siteID};

        Cursor cursor = database.rawQuery(query, whereArgs);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    //27 Aug, 2020
    public int getTotalFieldDataStringValuesFilled(String siteID,
                                                   String rollIntoAppId, String locationId, String eventId) {

/* //Old query. Only diff is distinct word in nested query
   query: select actual ,a.locationId from (select count(distinct fieldParameterId) actual, b.locationId, a.siteId from s_SiteMobileApp a,d_FieldData b
        where a.SiteID=b.siteId and a.MobileAppID=b.mobileAppId and a.SiteID= 6334 and a.roll_into_app_id= 140 and b.locationId = 266518 and b.eventId = 359118
        and b.fieldParameterId not in(15,25) and (b.stringValue IS NOT NULL AND b.stringValue != '') group by b.locationId) a
      */
        String query = "select actual ,a.LocationID from (select count(fieldParameterId) " +
                "actual, b.LocationID, a.SiteID from s_SiteMobileApp a, d_FieldData b " +
                "where a.SiteID=b.SiteID and a.MobileAppID=b.MobileAppID and a.SiteID= ? " +
                "and a.roll_into_app_id= ? " +
                "and b.LocationID = ? and b.EventID = ? and b.FieldParameterID " +
                "not in(15,25) and (b.StringValue IS NOT NULL AND b.StringValue != '' and b.ExtField1 = 1) " +
                "group by b.LocationID) a";

        String[] whereArgs = {siteID, rollIntoAppId, locationId, eventId};

        Cursor c = database.rawQuery(query, whereArgs);

        int cnt = 0;
        if (c != null && c.moveToFirst()) {
            cnt = Integer.parseInt(c.getString(0));
            c.close();
        }
        return cnt;
    }

    //27 Aug, 2020
    public int getRequiredFieldDataFilledCount(String siteID,
                                               String mobileAppId, String locationId,
                                               String eventId, String setId, String fieldParamIds) {

        String query = "select count(StringValue) from d_FieldData where SiteID = ? and " +
                "EventID = ? and MobileAppID = ? and LocationID= ? and ExtField1 = ? " +
                "and FieldParameterID in " + fieldParamIds + " and (StringValue NOT NULL AND StringValue != '')";

        String[] whereArgs = {siteID, eventId, mobileAppId, locationId, setId};

        Cursor c = database.rawQuery(query, whereArgs);

        int cnt = 0;
        if (c != null && c.moveToFirst()) {
            cnt = Integer.parseInt(c.getString(0));
            c.close();
        }
        return cnt;
    }

    //27 Aug, 2020
    public ArrayList<String> getAllRequiredFieldParams(String mobileAppId, String locationID,
                                                       FormMaster formMaster, FormsAdapter formsAdapter) {

        ArrayList<String> fieldParamIds = new ArrayList<>();

        String query = "select distinct FieldParameterID, ParameterLabel, Required_Y_N, locationIds " +
                "from s_MetaData where MobileAppID = ? and Required_Y_N = 1";

        String[] whereArgs = {mobileAppId};

        Cursor c = database.rawQuery(query, whereArgs);

        int fpId = 0;
        if (c != null && c.moveToFirst()) {

            do {
                fpId = Integer.parseInt(c.getString(0));

                String locIds = c.getString(3);
                String[] splitArray = locIds.split(",");
                Set<String> set = new HashSet<>(Arrays.asList(splitArray));

                if (set.contains(locationID) || locIds.equals("0")) {

                    if (formMaster != null) {
                        FormMaster.DataHolder tempData
                                = formMaster.getFmapObject().get(fpId);

                        String stringValue = "";

                        if (tempData != null && tempData.value != null && tempData.value.isEmpty()) {
                            stringValue = tempData.value;
                        }

                        boolean noStringValue = stringValue.isEmpty();

                        if (formMaster.getMapMetaObjects().containsKey(fpId)) {
                            try {
                                if (formMaster.getMapMetaObjects().get(fpId).isRowVisible &&
                                        noStringValue)
                                    fieldParamIds.add(fpId + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (formsAdapter != null) {
                        if (formsAdapter.getCurrentReadingAndVisibility(fpId))
                            fieldParamIds.add(fpId + "");
                    } else
                        fieldParamIds.add(fpId + "");
                }
            } while (c.moveToNext());
            c.close();
        }
        return fieldParamIds;
    }

    public int updateStringValueForSign(int spid, int appid, String value, int setid, String evntID, String locationID) {
        Cursor c = null;
        // String ret="update into d_f
        String query;
        if (value == null) {

            query = "update d_fieldData set " + KEY_StringValue + "=" + value + " where " + KEY_FieldParameterID + " =" + spid
                    + "  and " + KEY_MobileAppID + " = " + appid + " and " + KEY_ExtField1 + " = " + setid + " and EventID=" + evntID + " and LocationID=" + locationID;

        } else {
            query = "update d_fieldData set " + KEY_StringValue + "='" + value + "' where " + KEY_FieldParameterID + " =" + spid
                    + "  and " + KEY_MobileAppID + " = " + appid + " and " + KEY_ExtField1 + " = " + setid + " and EventID=" + evntID + " and LocationID=" + locationID;
        }

        Log.i(TAG, "updateStringValueForSign() Update Sign in d_fielddata=" + query);
        int ret = 0;
        try {

            c = database.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                ret = c.getCount();
            }

            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                // c = null;
            }
        }
        return ret;
        // return database.rawQuery(query,null);
    }

    public boolean isFieldUUIDExist(String uuid) {
        Cursor c = null;
        if (uuid != null) {
            int count = 0;
            String query = "select count(" + KEY_fieldUUID + ") from d_FieldData where " + KEY_fieldUUID
                    + " = '" + uuid + "'";

            try {
                c = database.rawQuery(query, null);
                if (c != null && c.moveToFirst()) {
                    count = c.getInt(0);
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            }

            return count > 0;
        } else {
            return true;
        }
    }

    public void insertFieldDataList(List<FieldData> dataList, int userID, String deviceID) {
        long ret = 0;
        database.beginTransaction();
        FieldData fieldData;

        SharedPreferences prefs = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE);
        String lat = prefs.getString("latitude", "");
        String lng = prefs.getString("longitude", "");

        try {
            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                fieldData = dataList.get(i);

                String value = fieldData.getStringValue();
                values.put(KEY_EventID, fieldData.getEventID());
                values.put(KEY_LocationID, fieldData.getLocationID());
                values.put(KEY_FieldParameterID, fieldData.getFieldParameterID());
                values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
                values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
                values.put(KEY_StringValue, (value != null) ? value.trim() : null);
                values.put(KEY_Units, fieldData.getUnits());

                if (lat.equals("") && lng.equals("")) {
                    Log.e("fieldLatLng", "insertFieldDataList: NO BAD ELF LAT LNG " + lat + " lng " + lng);
                    values.put(KEY_Latitude, fieldData.getLatitude());
                    values.put(KEY_Longitude, fieldData.getLongitude());
                } else {
                    Log.e("fieldLatLng", "insertFieldDataList: BAD ELF LAT LNG CAPTURE " + lat + " lng " + lng);
                    values.put(KEY_Latitude, Double.parseDouble(lat));
                    values.put(KEY_Longitude, Double.parseDouble(lng));
                }

                values.put(KEY_ExtField1, fieldData.getExtField1());

                values.put(KEY_DeviceID, deviceID);
                values.put(KEY_SiteID, fieldData.getSiteID());

                values.put(KEY_UserID, userID);
                values.put(KEY_MobileAppID, fieldData.getMobileAppID());

                if (fieldData.getExtField2() != null) {

                    String date = fieldData.getExtField2();
                    if (!date.contains("/")) {
                        date = Util.getMMddyyyyFromMilliSeconds(date);
                    }

                    values.put(KEY_ExtField2, date);
                }
                if (fieldData.getExtField3() != null) {
                    String time = fieldData.getExtField3();

                    if (!time.contains(":")) {
                        time = Util.gethhmmFromMilliSeconds(time);
                    }

                    values.put(KEY_ExtField3, time);
                }

                if (fieldData.getExtField4() != null) {
                    values.put(KEY_ExtField4, fieldData.getExtField4());
                }
                if (fieldData.getExtField7() != null) {
                    values.put(KEY_ExtField7, fieldData.getExtField7());
                }

                values.put(KEY_Notes, fieldData.getNotes());
                /*if (fieldData.getCreationDate() != 0) {
                    values.put(KEY_CreationDate, fieldData.getCreationDate());
                }*/

                String uuid = Util.randomUUID(mContext, true);
                values.put(KEY_fieldUUID, uuid);

                ret = database.insert(DbAccess.TABLE_FIELD_DATA, null, values);
                Log.i(TAG, "insertFieldDataList() Data result=" + ret);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertFieldDataList() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
    }

    public int updateRow_dFieldData(List<FieldData> dataList) {
        int ret = 0;

        for (FieldData fieldData : dataList) {

            ContentValues values = new ContentValues();

            String value = fieldData.getStringValue();

            value = StringUtils.capitalize(value);
            values.put(KEY_StringValue, value != null ? value.trim() : null);
            values.put(KEY_EventID, fieldData.getEventID());
            values.put(KEY_LocationID, fieldData.getLocationID());
            values.put(KEY_FieldParameterID, fieldData.getFieldParameterID());
            values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
/*
            values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
            values.put(KEY_Units, fieldData.getUnits());
            values.put(KEY_Latitude, fieldData.getLatitude());
            values.put(KEY_Longitude, fieldData.getLongitude());
*/
            values.put(KEY_ExtField1, fieldData.getExtField1());

            if (fieldData.getExtField2() != null) {
                values.put(KEY_ExtField2, fieldData.getExtField2());
            }
            if (fieldData.getExtField3() != null) {
                values.put(KEY_ExtField3, fieldData.getExtField3());
            }
            if (fieldData.getExtField4() != null) {
                values.put(KEY_ExtField4, 1);//Event Status Open=1,close=0
            }
            if (fieldData.getCreationDate() != 0) {
                values.put(KEY_CreationDate, fieldData.getCreationDate());
            }

            values.put(KEY_Notes, fieldData.getNotes());
            values.put(KEY_SiteID, fieldData.getSiteID());
            values.put(KEY_UserID, fieldData.getUserID());
            values.put(KEY_MobileAppID, fieldData.getMobileAppID());

            values.put(KEY_modifiedByDeviceId, fieldData.getModifiedByDeviceId());
            values.put(KEY_modifiedBy, fieldData.getModifiedBy());

            String flag = getDataSyncFlagIfUpdate(fieldData.getEventID() + "", Integer.parseInt(fieldData.getExtField1()),
                    fieldData.getLocationID() + "", fieldData.getSiteID() + "", fieldData.getMobileAppID(),
                    fieldData.getFieldParameterID());

            values.put(KEY_DataSyncFlag, flag);

            String whereClause = "EventID=? AND MobileAppID=? AND LocationID=? " +
                    "AND FieldParameterID=? AND ExtField1=? and SiteID = ? ";// AND MobileAppID=? AND deviceId=?
            String[] whereArgs = new String[]{fieldData.getEventID() + "", fieldData.getMobileAppID() + "", fieldData.getLocationID() + "",
                    "" + fieldData.getFieldParameterID()
                    , fieldData.getExtField1() + "", fieldData.getSiteID() + ""};
            try {
                ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
                Log.i(TAG, "Updated Rows:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "updateValue Error:" + e.getMessage());
            }
        }
        return ret;
    }

    public int updateValue(int eventID, int paramID, int setID, String locationID, String value, int siteID, int appID,
                           Location gpsLocation, String deviceID, String userID) {

        double latitude = 0;
        double longitude = 0;
        String nullValue = null;
        int ret = 0;
//		FieldData fieldData = new FieldData();

        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
        }

        ContentValues values = new ContentValues();

        value = StringUtils.capitalize(value);
        values.put(KEY_StringValue, value == null ? value : value.trim());
        values.put(KEY_Latitude, latitude);
        values.put(KEY_Longitude, longitude);

        values.put(KEY_ModificationDate, System.currentTimeMillis());

        values.put(KEY_EmailSentFlag, nullValue);

        String flag = getDataSyncFlagIfUpdate(eventID + "", setID, locationID,
                siteID + "", appID, paramID);
        values.put(KEY_DataSyncFlag, flag);

        values.put(KEY_modifiedByDeviceId, deviceID);
        values.put(KEY_modifiedBy, userID);

        Log.i(TAG, "ModifiedBy :" + userID + " ModifiedByDeviceID:" + deviceID);

        String whereClause = "FieldParameterID=? AND ExtField1=? AND LocationID=? AND " +
                " EventID =? AND SiteID=? AND MobileAppID=?";//
        String[] whereArgs = new String[]{"" + paramID, "" + setID, "" + locationID, "" + eventID, "" + siteID, "" + appID};//
        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "Updated Rows:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateValue Error:" + e.getMessage());
        }
//		fieldData.setMeasurementTime(mTime);
        return ret;
    }

    public ArrayList<String> getBottleCheckOptions(String eventID, int setID, String locationID, String siteID,
                                                   int appID, int fieldParameterId) {

        String query = "select " + KEY_bottlesCheckOptions + " from d_FieldData where SiteID = ? and " +
                "EventID = ? and MobileAppID = ? and LocationID= ? and ExtField1 = ? and " + KEY_FieldParameterID + "=?";

        String[] whereArgs = {siteID + "", eventID + "", appID + "", locationID + "", setID + "", fieldParameterId + ""};

        Cursor c = database.rawQuery(query, whereArgs);

        ArrayList<String> checkOptionsList = new ArrayList<>();
        String checkOptions = "";
        if (c != null && c.moveToFirst()) {
            checkOptions = c.getString(0);
            c.close();
        }

        if (checkOptions != null && !checkOptions.trim().isEmpty()) {
            String[] splitArray = checkOptions.split("\\|");
            checkOptionsList.addAll(Arrays.asList(splitArray));
        }

        return checkOptionsList;
    }

    public int updateCheckOptions(String eventID, int setID, String locationID, String siteID,
                                  int appID, int fieldParameterId, String bottleOptions) {

        ContentValues values = new ContentValues();
        int ret = 0;
        values.put(KEY_bottlesCheckOptions, bottleOptions);
        String whereClause = "LocationID=? and ExtField1=? and EventID =? and SiteID=? " +
                "and MobileAppID=? and " + KEY_FieldParameterID + "=?";
        String[] whereArgs = new String[]{"" + locationID, "" + setID, "" + eventID, "" + siteID,
                "" + appID, fieldParameterId + ""};

        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "update bottle options Ret value for updateValue = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "bottle options Error=" + e.getMessage());
        }
        return ret;
    }

    //update d_FieldData set CreationDate='100' where LocationID=1 and ExtField1=1 and EventID in (select EventID from d_Event where SiteID=1 and MobileAppID=2);
    public int updateCreationDate(int eventID, int setID, String locationID, int siteID, int appID, long date) {

        ContentValues values = new ContentValues();
        int ret = 0;
        values.put(KEY_CreationDate, date);
//		String whereClause =  "ExtField1=? AND LocationID=?";
        String whereClause = "LocationID=? and ExtField1=? and EventID =? and SiteID=? and MobileAppID=? ";
        String[] whereArgs = new String[]{"" + locationID, "" + setID, "" + eventID, "" + siteID, "" + appID};

        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "updateCreationDate() Ret value for updateValue = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "updateCreationDate() Error=" + e.getMessage());
        }
        return ret;
    }

    public int updateCreationDateByParamId(int eventID, int setID, String locationID,
                                           int siteID, int appID, long date, int paramId) {

        ContentValues values = new ContentValues();
        int ret = 0;
        values.put(KEY_CreationDate, date);

        String whereClause = "LocationID=? and ExtField1=? and EventID =? and SiteID=? " +
                "and MobileAppID=? and " + KEY_FieldParameterID + " = ?";
        String[] whereArgs = new String[]{"" + locationID, "" + setID, "" + eventID, "" + siteID, "" + appID, paramId + ""};

        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "updateCreationDate() Ret value for updateValue = " + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "updateCreationDate() Error=" + e.getMessage());
        }
        return ret;
    }

    public int updateDateInExt2(int eventID, int setID, String locationID,
                                int siteID, int appID, String date) {
        int ret = 0;
        if (date != null) {
            if (!date.contains("/")) {
                date = Util.getMMddyyyyFromMilliSeconds(date);
            }
        }

        if (date == null) {
            Log.e(TAG, "Returning to back from updateDateInExt2 wid 0");
            return ret;
        }

        //commented on 3 Feb, 23 as this query work for old data sync changes
/*        String query = "UPDATE d_FieldData SET ExtField2 = ?, EmailSentFlag = null, DataSyncFlag = null " +
                "WHERE LocationID = ? and ExtField1 = ? and EventID = ? and SiteID = ? and MobileAppID = ?";*/

        String query = "UPDATE d_FieldData SET ExtField2 = ? "
                + ", EmailSentFlag = null, DataSyncFlag = " +
                "(CASE WHEN DataSyncFlag = 1 THEN 2 WHEN DataSyncFlag = 2 THEN 2 ELSE NULL END) " +
                "WHERE LocationID = ? and ExtField1 = ? and EventID = ? and SiteID = ? " +
                "and MobileAppID = ?";

        String[] whereArgs = new String[]{date, "" + locationID, "" + setID, "" + eventID, "" + siteID, "" + appID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ret = cursor.getInt(0);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateDateInExt2 query failed msg= " + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        Log.i(TAG, "updateDateInExt2() Result=" + ret);
        return ret;
    }

    public int updateTimeInExt3(int eventID, int setID, String locationID, int siteID, int appID, String time) {
        int ret = 0;
        if (time != null) {
            if (!time.contains(":")) {
                time = Util.gethhmmFromMilliSeconds(time);
            }
        }

        if (time == null) {
            Log.e(TAG, "Returning to back from updateTimeInExt3 wid 0");
            return ret;
        }

        //commented on 3 Feb, 23 as this query work for old data sync changes
/*        String query = "UPDATE d_FieldData SET ExtField3 = ?, EmailSentFlag = null, DataSyncFlag = null " +
                "WHERE LocationID = ? and ExtField1 = ? and EventID = ? and SiteID = ? and MobileAppID = ?";*/

        String query = "UPDATE d_FieldData SET ExtField3 = ?, EmailSentFlag = null, DataSyncFlag = " +
                "(CASE WHEN DataSyncFlag = 1 THEN 2 WHEN DataSyncFlag = 2 THEN 2 ELSE NULL END) " +
                "WHERE LocationID = ? and ExtField1 = ? and EventID = ? and SiteID = ? and MobileAppID = ?";

        String[] whereArgs = new String[]{time, "" + locationID, "" + setID, "" + eventID, "" + siteID, "" + appID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ret = cursor.getInt(0);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateDateInExt3 query failed msg= " + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        Log.i(TAG, "updateDateInExt3() Result=" + ret);
        return ret;
    }

    public void updateCorrectedLatLong(int eventID, int setID, String locationID, int siteID, int appID, double corr_lat, double corr_long) {

        String nullValue = null;
        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_CorrectedLat, corr_lat);
        values.put(KEY_CorrectedLong, corr_long);
        values.put(KEY_Latitude, corr_lat);
        values.put(KEY_Longitude, corr_long);

        values.put(KEY_EmailSentFlag, nullValue);

        //commented on 29/11/22 by SanketPatel as dataSyncFlag is already updated before this statement called in saveAndUpdate Data method
        //in case if u wish to use this method stand alone then change the query and check for if dataSyncFlag is 1 then make it to 2 to update
        //follow updateDateInExt2 method query to use case
//        values.put(KEY_DataSyncFlag, nullValue);

        String whereClause = "LocationID=? and ExtField1=? and EventID =? and SiteID=? and MobileAppID=?";
        String[] whereArgs = new String[]{"" + locationID, "" + setID, "" + eventID, "" + siteID, "" + appID};
        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "updateCorrectedLatLong() Ret value for updateValue = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateCorrectedLatLong() Error :" + e.getMessage());
        }
    }

    public int updateEventID(int clientEventID, int serverEventID) {


        Log.i(TAG, "updateEventID() localEventID:" + clientEventID + "->ServerEventID:" + serverEventID);
        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_EventID, serverEventID);
        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + clientEventID};
        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "updateEventID() Update Event result:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateEventID() Exception:" + e.getMessage());

        }
        return ret;
    }

    public int updateMeasurementTime(int eventID, int setID, String locID, int siteID, int appID, long value) {
        int ret = 0;

        //commented on 3 Feb, 23 as this query work for old data sync changes

/*        String query = "UPDATE d_FieldData SET MeasurementTime = ?"
                + ", EmailSentFlag = null, DataSyncFlag = null " +
                "WHERE LocationID = ? and ExtField1 = ? and EventID = ? and SiteID = ? and MobileAppID = ?";*/

        String query = "UPDATE d_FieldData SET MeasurementTime = ?"
                + ", EmailSentFlag = null, DataSyncFlag = " +
                "(CASE WHEN DataSyncFlag = 1 THEN 2 WHEN DataSyncFlag = 2 THEN 2 ELSE NULL END) " +
                "WHERE LocationID = ? and ExtField1 = ? and EventID = ? and SiteID = ? and MobileAppID = ?";

        String[] whereArgs = new String[]{value + "", "" + locID, "" + setID, "" + eventID, "" + siteID, "" + appID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ret = cursor.getInt(0);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateMeasurementTime query failed msg= " + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        Log.i(TAG, "updateMeasurementTime() Result=" + ret);
        return ret;
    }

    public int getLastSavedSetIDForOpenEvent(int eventID, String locID, int siteID, int appID) {
        String[] whereArgs = null;
        int id = 0;

        //11/10/2017 UPDATED QUERY EVENT_STATUS
        String query = "SELECT MAX(CAST (ExtField1 AS INT)) FROM d_FieldData as F " +
                " JOIN d_Event as E on F.EventID = E.EventID " +
                " where F.LocationID=? and F.SiteID=? and F.MobileAppID=? and E.EventStatus>0 and F.CreationDate IS NOT NULL and F.EventID = " + eventID;

        whereArgs = new String[]{"" + locID, "" + siteID, "" + appID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    id = cursor.getInt(0);
                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLastSavedSetIDForOpenEvent() query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return id;
    }


    public boolean highlightFormData(int siteID, String locID, int eventID, int setID, int mobileAppID) {
        String[] whereArgs = null;
        int count = 0;

        String query = "select count(*) from d_FieldData where LocationID = ? and EventID = ?  and ExtField1 = ? and MobileAppID = ?  and CreationDate is not null";
        whereArgs = new String[]{"" + locID, "" + eventID, "" + setID, "" + mobileAppID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
                if (count > 0) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, " highlightFormData() Error:" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return false;
    }

    public int getWorkingFieldSetID(String locID, int siteID, int appID) { //incomplete or in progress

        String[] whereArgs = null;

//		String query = "SELECT MAX(ExtField1) AS max_id FROM "+DbAccess.TABLE_FIELD_DATA+
//		" WHERE "+KEY_LocationID+"=?" + " AND CreationDate IS NULL";
        String query = "SELECT MAX(CAST(ExtField1 AS INT)) FROM d_FieldData as F " +
                " JOIN d_Event as E on F.EventID=E.EventID " +
                " where F.LocationID=? and F.SiteID=? and F.MobileAppID=? and CreationDate IS NULL and E.EventStatus>0";//

        whereArgs = new String[]{"" + locID, "" + siteID, "" + appID};

        Cursor cursor = null;
        int id = 0;

        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    id = cursor.getInt(0);
                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " getWorkingFieldSetID() query Error=" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        Log.i(TAG, "getWorkingFieldSetID:" + id + " For Child MobileApp:" + appID);

        return id;
    }

    public long getMaxCreationDateForEvent(int eventID, String siteID) { //incomplete or in progress

        String[] whereArgs = null;

        String query = "SELECT MAX(CAST(CreationDate AS LONG)) FROM d_FieldData  " +
                " where SiteID=? and EventID=?";//


        whereArgs = new String[]{siteID, "" + eventID};

        Cursor cursor = null;
        long last_date = 0;

        Log.i(TAG, "getMaxCreationDateForEvent() query=" + query);
        Log.i(TAG, "getMaxCreationDateForEvent() whereArgs=siteID:" + whereArgs[0] + " EventID:" + whereArgs[1]);
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    last_date = cursor.getLong(0);
                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " getMaxCreationDateForEvent() query Error=" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return last_date;
    }


    public int getmaxSetID_MobileApp(String locID, String eventID, String appID) { //incomplete or in progress
        int maxset = 1;
        String[] whereArgs = null;

        String query = "SELECT max(cast(ExtField1 as int)) FROM d_FieldData where EventID=? and MobileAppID=? and LocationID=?";//and CreationDate IS NULL

        whereArgs = new String[]{eventID, appID, locID};
        Log.i(TAG, "getmaxSetID query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                maxset = Integer.parseInt(cursor.getString(0));
                Log.i(TAG, "getmaxSetID maxset=" + maxset);
                if (maxset < 1) {
                    maxset = 1;
                }
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getmaxSetID query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        return maxset;

    }

    public int getNextSetID_MobileApp(String locID, String eventID, String appID) { //incomplete or in progress
        int maxset = 1;
        String[] whereArgs = null;

        String query = "SELECT  max(cast(ExtField1 as int)) FROM d_FieldData where EventID=? and MobileAppID=? and LocationID=?";//and CreationDate IS NULL

        whereArgs = new String[]{eventID, appID, locID};
        Log.i(TAG, "getmaxSetID query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                maxset = cursor.getInt(0);
                Log.i(TAG, "getNextSetID max set=" + maxset);
                maxset = maxset + 1;
                Log.i(TAG, "getNextSetID next set=" + maxset);

                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getmaxSetID query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        return maxset;

    }


    public int getSetForAutoSetValue(String locID, String eventID, String appID, String value, int fpid) { //incomplete or in progress
        int maxset = 0;
        String[] whereArgs = null;

        String query = "SELECT distinct cast(ExtField1 as int) FROM " +
                "d_FieldData where EventID=? and MobileAppID=? and LocationID=? and FieldParameterID=? and ( \n" +
                "StringValue like '" + value + "' OR StringValue ISNULL)\n"; //and CreationDate IS NULL

        whereArgs = new String[]{eventID, appID, locID, fpid + ""};
        Log.i(TAG, "getmaxSetID query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                maxset = cursor.getInt(0);
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getmaxSetID query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        return maxset;

    }

    public String getPreviousReading(int eventID, int setID, String locID, int siteID, int appID,
                                     int fieldParameterID, String deviceID) {

        String query = null;
        String value = null;

        //09-May-17 Removed DeviceID
        String[] whereArgs = null;
        query = "select StringValue FROM d_FieldData as F JOIN d_Event as E on F.EventID = E.EventID" +
                " where F.ExtField1=? and F.LocationID=? and F.SiteID=? and F.MobileAppID=?" +
                " and  F.FieldParameterID=? and E.EventStatus>0 and F.EventID =" + eventID;//and  F.deviceId=?

        whereArgs = new String[]{"" + setID, "" + locID, "" + siteID, "" + appID, "" + fieldParameterID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        value = cursor.getString(0);
                    } while (cursor.moveToNext());

                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "qqqq" + e.getLocalizedMessage());
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return value;
    }

    public boolean isNoteTaken_Data(int eventID, int setID, String locID, int siteID, int appID,
                                    int fieldParameterID) {

        String[] fieldDataColumns = null;
        String whereClause = null;
        String[] whereArgs = null;
        boolean result = false;

        fieldDataColumns = new String[]{KEY_Notes};

        whereClause = KEY_EventID + "=? and " + KEY_ExtField1 + "=? " +
                "and " + KEY_LocationID + "=? and " + KEY_FieldParameterID + "=? and "
                + KEY_SiteID + "=? and " + KEY_MobileAppID + "=?";
        whereArgs = new String[]{eventID + "", "" + setID, "" + locID, fieldParameterID + "",
                siteID + "", appID + ""};

        Cursor cursor;
        try {
            cursor = database.query(DbAccess.TABLE_FIELD_DATA, fieldDataColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                String notevalue = cursor.getString(0);
                result = (notevalue != null && !notevalue.trim().isEmpty());
                cursor.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isNoteTaken() Error :" + e.getMessage());
        }

        return result;
    }

    //This method gets the field data list for a particular location with "locID", setID, where datasyncflag=0
    public List<FieldData> getFieldDataListForSet(int eventID, int setID, String locID) {

        List<FieldData> dataList = new ArrayList<FieldData>();
        String[] fieldDataColumns = null;
        String whereClause = null;
        String[] whereArgs = null;
        String orderBy = null;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_FieldParameterID, KEY_FieldParameterLabel, KEY_MeasurementTime,
                KEY_StringValue, KEY_Units, KEY_Latitude, KEY_Longitude, KEY_Notes,
                KEY_CreationDate, KEY_FieldDataID};

        whereClause = KEY_EventID + "=? and " + KEY_ExtField1 + "=? and " + KEY_LocationID + "=?";
        whereArgs = new String[]{eventID + "", "" + setID, "" + locID};

        orderBy = null;
        //orderBy = "KEY_FieldDataID";
        Cursor cursor;
        try {
            cursor = database.query(DbAccess.TABLE_FIELD_DATA, fieldDataColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FieldData data = cursorToFieldData(cursor);
                    dataList.add(data);

                } while (cursor.moveToNext());
                cursor.close();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dataList;
        }

        Log.i(TAG, "getFieldDataListForSet() OUT time:" + System.currentTimeMillis());

        return dataList;
    }

    private FieldData cursorToFieldData(Cursor cursor) {
        FieldData data = new FieldData();

        try {
            data.setEventID(cursor.getInt(0));
            data.setLocationID(cursor.getString(1));
            data.setFieldParameterID(cursor.getInt(2));
            data.setFieldParameterLabel(cursor.getString(3));
            data.setMeasurementTime(cursor.getLong(4));
            data.setStringValue(cursor.getString(5));
            data.setUnits(cursor.getString(6));
            data.setLatitude(cursor.getDouble(7));
            data.setLongitude(cursor.getDouble(8));
            data.setNotes(cursor.getString(9));
            data.setCreationDate(cursor.getLong(10));
            data.setFieldDataID(cursor.getInt(11));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("mmm" + e.getLocalizedMessage());
        }

        return data;
    }

    public List<String> getDistinctParamLabels(int siteID, int parentAppID,
                                               int currentAppID, String locId,
                                               HashMap<String, FieldParamInfo> paramLabelList) {

        List<String> list = new ArrayList<>();
        String query = null;
        String[] whereArgs = null;

//		String query = "Select DISTINCT("+KEY_FieldParameterLabel+") from "+DbAccess.TABLE_FIELD_DATA;

        if (currentAppID == 0) {
            Log.i(TAG, "Fetch Report CurrentAppID=" + currentAppID + " ParentID:" + parentAppID);

            query = "SELECT DISTINCT FieldParameterLabel, FieldParameterID FROM d_FieldData as F " +
                    "JOIN d_Event as E on F.EventID = E.EventID " +
                    "where E.SiteID=? and F.MobileAppID in (select distinct a.MobileAppID from s_MetaData A " +
                    " INNER JOIN s_MobileApp B ON A.MobileAppID = B.MobileAppID Where parent_app_id = ?) " +
                    " and F.CreationDate is not null and F.LocationID = ?";//E.mobileappid
            whereArgs = new String[]{"" + siteID, "" + parentAppID, locId};
        } else {
            Log.i(TAG, "Fetch Report CurrentAppID=" + currentAppID + " ParentID:" + parentAppID);
/*            query = "SELECT DISTINCT FieldParameterLabel, FieldParameterID FROM d_FieldData as F " +
                    "JOIN d_Event as E on F.EventID = E.EventID " +
                    "where E.SiteID=? and F.MobileAppID=? and F.CreationDate is not null and F.LocationID = ?";*/
            query = "SELECT DISTINCT ParameterLabel, FieldParameterID FROM s_MetaData " +
                    "WHERE MobileAppID = ? and UPPER(ParameterLabel) != 'NOTES' order by RowOrder ";
            whereArgs = new String[]{"" + currentAppID};
        }

        Log.i(TAG, "Fetch Report Querry=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (paramLabelList.containsKey(cursor.getString(1)))
                        list.add(cursor.getString(0));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getDistinctParamLabels() exception:" + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    public ArrayList<FieldParamInfo> getParamLabelsForReport(int currentAppID) {

        ArrayList<FieldParamInfo> listFieldData = new ArrayList<>();
        String query = null;
        String[] whereArgs = null;

        query = "SELECT DISTINCT ParameterLabel, FieldParameterID FROM s_MetaData " +
                "WHERE MobileAppID = ? and UPPER(ParameterLabel) != 'NOTES' order by RowOrder ";

        whereArgs = new String[]{currentAppID + ""};

        Log.i(TAG, "Fetch Report Querry=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String fieldParamLabel = cursor.getString(0);
                    String fieldParamId = cursor.getString(1);
                    FieldParamInfo fieldData = new FieldParamInfo();
                    fieldData.setFieldParameterLabel(fieldParamLabel);
                    fieldData.setFieldParameterId(fieldParamId);

                    listFieldData.add(fieldData);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getDistinctParamLabels() exception:" + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listFieldData;
    }

    String returnCase(String param) {
        String queryMiddle = " CASE (FieldParameterLabel) WHEN " + "\"" + param + "\"" + " THEN StringValue END AS " +
                "'" + param + "'";
        return queryMiddle;
    }

    public boolean isParamIdExists(int setID, int eventID, String locationID, int siteID,
                                   int currentAppID, String deviceID, int usrID,
                                   int fieldparameterId) {
        Cursor c = null;
        if (locationID != null) {
            int count = 0;
            String query = "select count(FieldParameterID) from d_FieldData where " +
                    " LocationID=? and SiteID=? and EventID=? and ExtField1=? and MobileAppID=? " +
                    "and FieldParameterID=?";

            String[] whereArgs = new String[]{locationID, "" + siteID, eventID + "", setID + "",
                    currentAppID + "", fieldparameterId + ""};

            try {
                c = database.rawQuery(query, whereArgs);
                if (c != null && c.moveToFirst()) {
                    do {
                        count = c.getInt(0);

                    } while (c.moveToNext());
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            }

            return count > 0;
        } else {
            return true;
        }
    }

    public int getMaxOfSetForEvent(int eventID) {

        int count = 0;
        String query = "select max(ExtField1) from d_FieldData where EventID=" + eventID;
        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();
        }

        return count;
    }

    public boolean isMandatoryFieldFilled(String mobappid, String evntid, String locationID) {

        Cursor cursor = null;
        String query = "select count(d.FieldParameterID) from s_metadata s inner join  d_fielddata  d  on " +
                "s.FieldParameterID=d.FieldParameterID and s.MobileAppID=d.MobileAppID where s.MobileAppID=" + mobappid + " and" +
                " (d.StringValue=' ' or d.StringValue<0 or d.StringValue is null) and s.mandatoryField=2 and " +
                "d.EventID=" + evntid + " and d.LocationID=" + locationID + " group by d.ExtField1";


        cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public ArrayList<RequiredFieldRowItem> getMandatoryFieldList(String parentAppId, String eventId,
                                                                 String siteId) {
        Cursor cursor = null;
        String query = "select Distinct count(DISTINCT s.FieldParameterID),s.MobileAppID," +
                "sm.display_name from s_MetaData s,s_SiteMobileApp sm where s.mandatoryField=2 and s.MobileAppID IN\n" +
                "(Select distinct sma.MobileAppID from s_SiteMobileApp sma where sma.roll_into_app_id="
                + parentAppId + " and sma.SiteID=" + siteId + ") and s.FieldParameterID NOT IN\n" +
                "(select distinct df.FieldParameterID from d_FieldData df where " +
                "(df.StringValue IS NOT NULL and df.StringValue NOT LIKE '') and df.EventID="
                + eventId + " and df.FieldParameterID IN \n" +
                "(select m.FieldParameterID from s_MetaData m where m.mandatoryField=2 and m.MobileAppID IN \n" +
                "(Select distinct sma2.MobileAppID from s_SiteMobileApp sma2  where sma2.roll_into_app_id="
                + parentAppId + " and sma2.SiteID=" + siteId + "))) and sm.roll_into_app_id="
                + parentAppId + " and s.MobileAppID=sm.MobileAppID and sm.SiteID=" + siteId
                + " GROUP BY s.MobileAppID";

        query = "select DISTINCT a.FieldParameterID, a.field_parameter_operands,d.LocationID, " +
                "d.ExtField1, d.StringValue, a.ParameterLabel, a.MobileAppID " +
                "from s_SiteMobileApp b, s_MetaData a left outer join d_FieldData " +
                "d on a.FieldParameterID = d.FieldParameterID and d.EventID = " + eventId + " where " +
                "a.MobileAppID = b.MobileAppID and b.SiteID = " + siteId +
                " and b.roll_into_app_id = " + parentAppId + " and a.mandatoryField = 2 and " +
                "(d.FieldParameterID is not null and (d.StringValue is null or d.StringValue =''))";

        SiteMobileAppDataSource siteMobileAppDataSource = new SiteMobileAppDataSource(mContext);
        ArrayList<RequiredFieldRowItem> requiredFieldRowItems = new ArrayList<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, RequiredFieldRowItem>>> locMap
                = new HashMap<>();

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int fpId = cursor.getInt(cursor.getColumnIndexOrThrow("FieldParameterID"));
                    String operand = cursor.getString(cursor.getColumnIndexOrThrow("field_parameter_operands"));
                    int locationId = cursor.getInt(cursor.getColumnIndexOrThrow("LocationID"));
                    int setId = cursor.getInt(cursor.getColumnIndexOrThrow("ExtField1"));
                    String paramLabel = cursor.getString(cursor.getColumnIndexOrThrow("ParameterLabel"));
                    int appId = cursor.getInt(cursor.getColumnIndexOrThrow("MobileAppID"));

                    String displayName
                            = siteMobileAppDataSource
                            .getMobileAppDisplayName(appId,
                                    Integer.parseInt(siteId));

                    RequiredFieldRowItem rowItem = new RequiredFieldRowItem();
                    rowItem.setCount(1);
                    rowItem.setParentAppId(parentAppId);
                    rowItem.setChildAppId(appId + "");
                    rowItem.setTitle(displayName);
                    rowItem.setEventId(eventId);

                    if (operand != null && !operand.isEmpty() && operand.contains("!!visible!!")) {
                        List<String> queryArray = Util.splitStringToArray("~", operand);
                        if (!queryArray.isEmpty()) {
                            for (String queryExpr : queryArray) {
                                if (queryExpr.contains("!!visible!!")) {

                                    String visibleQuery = replaceSetOrVisibleQueryCols(queryExpr,
                                            locationId, eventId, siteId, setId);

                                    if (!visibleQuery.isEmpty()) {
                                        boolean showField = new FieldDataSource(mContext)
                                                .hitExpressionQuery(visibleQuery).equals("1");
                                        if (showField) {

                                            HashMap<Integer, HashMap<Integer,
                                                    RequiredFieldRowItem>> appIdMap
                                                    = new HashMap<>();

                                            if (locMap.containsKey(locationId)) {
                                                appIdMap = locMap.get(locationId);
                                            }

                                            HashMap<Integer, RequiredFieldRowItem> mapFieldParam
                                                    = new HashMap<>();
                                            mapFieldParam.put(fpId, rowItem);

                                            if (appIdMap.containsKey(appId)) {
                                                HashMap<Integer, RequiredFieldRowItem> mapFp
                                                        = appIdMap.get(appId);
                                                if (mapFp != null && !mapFp.containsKey(fpId)) {
                                                    mapFp.put(fpId, rowItem);
                                                } else {
                                                    appIdMap.put(appId, mapFieldParam);
                                                }
                                            } else {
                                                //adding new appid key with new fieldParamId and row
                                                appIdMap.put(appId, mapFieldParam);
                                            }

                                            locMap.put(locationId, appIdMap);
                                        }
                                    }
                                }
                            }
                        } else {

                            HashMap<Integer, HashMap<Integer,
                                    RequiredFieldRowItem>> appIdMap
                                    = new HashMap<>();

                            if (locMap.containsKey(locationId)) {
                                appIdMap = locMap.get(locationId);
                            }

                            HashMap<Integer, RequiredFieldRowItem> mapFieldParam
                                    = new HashMap<>();
                            mapFieldParam.put(fpId, rowItem);

                            if (appIdMap.containsKey(appId)) {
                                HashMap<Integer, RequiredFieldRowItem> mapFp
                                        = appIdMap.get(appId);
                                if (mapFp != null && !mapFp.containsKey(fpId)) {
                                    mapFp.put(fpId, rowItem);
                                } else {
                                    appIdMap.put(appId, mapFieldParam);
                                }
                            } else {
                                //adding new appid key with new fieldParamId and row
                                appIdMap.put(appId, mapFieldParam);
                            }

                            locMap.put(locationId, appIdMap);
                        }
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        if (!locMap.isEmpty()) {

            for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, RequiredFieldRowItem>>> locEntry : locMap.entrySet()) {
                HashMap<Integer, HashMap<Integer, RequiredFieldRowItem>> appIdMap = locEntry.getValue();

                for (Map.Entry<Integer, HashMap<Integer, RequiredFieldRowItem>> entry : appIdMap.entrySet()) {
                    RequiredFieldRowItem rowItem = new RequiredFieldRowItem();
                    rowItem.setCount(entry.getValue().size());
                    rowItem.setParentAppId(parentAppId);
                    rowItem.setChildAppId(entry.getKey() + "");
                    rowItem.setLocationId(locEntry.getKey());

                    Map.Entry<Integer, RequiredFieldRowItem> appTitle
                            = entry.getValue().entrySet().iterator().next();
                    rowItem.setTitle(appTitle.getValue().getTitle());
                    rowItem.setEventId(eventId);
                    requiredFieldRowItems.add(rowItem);
                }
            }
        }

        return requiredFieldRowItems;
    }

    public String replaceSetOrVisibleQueryCols(String expression, int locationID, String eventID,
                                               String siteID, int setId) {

        expression = expression.replace("!!visible!!", "");

        HashMap<String, String> mapCols = new HashMap<>();
        mapCols.put("d_field_data", "d_FieldData");
        mapCols.put("d_event", "d_Event");
        mapCols.put("s_site_mobile_app", "s_SiteMobileApp");
        mapCols.put("string_value", "StringValue");
        mapCols.put("field_parameter_id", "FieldParameterID");
        mapCols.put("mobile_app_id", "MobileAppID");
        mapCols.put("location_id", "LocationID");
        mapCols.put("event_id", "EventID");
        mapCols.put("set_id", "ExtField1");
        mapCols.put("site_id", "SiteID");
        mapCols.put("app_order", "AppOrder");
        mapCols.put("cu_loc_id", locationID + "");
        mapCols.put("cu_eve_id", eventID);
        mapCols.put("cu_project_id", siteID);
        mapCols.put("cu_setId", setId + "");
        mapCols.put("cu_ext_field1", setId + "");
        mapCols.put("true", "1");
        mapCols.put("false", "0");

        for (Map.Entry<String, String> entry : mapCols.entrySet()) {
            expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        return expression;
    }

    public ArrayList<RequiredFieldRowItem> getMandatoryFieldListByLocation(String parentappid,
                                                                           String eventId,
                                                                           String siteid,
                                                                           String locationId) {
        int count = 0;
        Cursor cursor = null;
        ArrayList<RequiredFieldRowItem> requiredFieldRowItems = new ArrayList<>();

        String query = "select Distinct count(DISTINCT s.FieldParameterID),s.MobileAppID," +
                "sm.display_name from s_MetaData s,s_SiteMobileApp sm where s.mandatoryField=2 and s.MobileAppID IN " +
                "(Select distinct sma.MobileAppID  from s_SiteMobileApp sma " +
                "where sma.roll_into_app_id=" + parentappid + " and sma.SiteID=" + siteid + ") " +
                "and s.FieldParameterID NOT IN " +
                "(select distinct df.FieldParameterID from d_FieldData df where " +
                "(df.StringValue IS NOT NULL and df.StringValue NOT LIKE '') and " +
                "df.EventID = " + eventId + " and df.LocationID = " + locationId + " and df.FieldParameterID IN " +
                "(select m.FieldParameterID from s_MetaData m where m.mandatoryField=2 and m.MobileAppID IN " +
                "(Select distinct sma2.MobileAppID from s_SiteMobileApp sma2 " +
                "where sma2.roll_into_app_id = " + parentappid + " and sma2.SiteID=" + siteid + "))) " +
                "and sm.roll_into_app_id=" + parentappid + " and s.MobileAppID=sm.MobileAppID " +
                "and sm.SiteID=" + siteid + " GROUP BY s.MobileAppID";

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    RequiredFieldRowItem rowItem = new RequiredFieldRowItem();
                    rowItem.setCount(cursor.getInt(0));
                    rowItem.setParentAppId(parentappid);
                    rowItem.setChildAppId(cursor.getString(1));
                    rowItem.setTitle(cursor.getString(2));
                    rowItem.setEventId(eventId);
                    requiredFieldRowItems.add(rowItem);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return requiredFieldRowItems;
    }

    public boolean getCountOfRequiredField(int eventID) {

        Cursor cursor = null;
        String query = "SELECT distinct count(distinct f.FieldParameterID),sm.roll_into_app_id,sm.display_name,f.EventID from s_MetaData m inner join " +
                "s_SiteMobileApp sm on m.MobileAppID = sm.MobileAppID and " +
                "m.mandatoryField=2 inner join d_Event e on" +
                " sm.roll_into_app_id= e.MobileAppID and e.EventID =" + eventID + " left outer join d_FieldData f on " +
                "e.EventID = f.EventID and sm.MobileAppID=f.MobileAppID and" +
                " f.FieldParameterID=m.FieldParameterID WHERE m.mandatoryField=2 and" +
                " (f.StringValue IS NULL OR f.StringValue='') and f.EventID=" + eventID + " group by f.MobileAppID,f.ExtField1 having count(distinct m.FieldParameterID)>0";

        cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean isDatainD_Field(int eventID) {

        String selectQuery = "select (1) from d_fielddata where EventID=" + eventID;


        // Log.i(TAG, "isCheckCompanyIdForForm() query= " + selectQuery);

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getCount() > 0) {

                return true;
            }

            cursor.close();

        }
        return false;
    }

    //03-Jul-17 CREATED BY SHWETA FOR DOWNLOAD EVENT DATA and updated by sanket
    public int insertFieldDataListforUser(FieldDataForEventDownload fieldData, String userid) {

        if (isFieldUUIDExist(fieldData.getUuid()))
            return updateFieldData(fieldData);

        int ret = 0;
        database.beginTransaction();
        //   FieldDataForEventDownload fieldData;
        try {
            /* for (int i = 0; i < dataList.size(); i++) {*/
            ContentValues values = new ContentValues();
            //   fieldData = dataList.get(i);

            String value = fieldData.getStringValue();
            String note = fieldData.getNotes();
            values.put(KEY_EventID, fieldData.getEventId());
            values.put(KEY_LocationID, fieldData.getLocationId());
            values.put(KEY_FieldParameterID, fieldData.getFieldParameterId());
            values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
            values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());
            values.put(KEY_StringValue, value != null ? value.trim() : null);
            values.put(KEY_oldStringValue, (value != null) ? value.trim() : null);
            values.put(KEY_oldNote, (note != null) ? note.trim() : null);
            values.put(KEY_Latitude, fieldData.getLatitude());
            values.put(KEY_Longitude, fieldData.getLongitude());
            values.put(KEY_ExtField1, fieldData.getExtField1());

            values.put(KEY_DeviceID, fieldData.getDeviceId());
            values.put(KEY_SiteID, fieldData.getSiteId());

            values.put(KEY_UserID, fieldData.getUserId());
            values.put(KEY_MobileAppID, fieldData.getMobileAppId());

            if (fieldData.getExtField2() != null) {

                String date = fieldData.getExtField2();
                if (!date.contains("/")) {
                    date = Util.getMMddyyyyFromMilliSeconds(date);
                }

                values.put(KEY_ExtField2, date);
            }
            if (fieldData.getExtField3() != null) {
                String time = fieldData.getExtField3();

                if (!time.contains(":")) {
                    time = Util.gethhmmFromMilliSeconds(time);
                }

                values.put(KEY_ExtField3, time);
            }

            if (fieldData.getExtField4() != null) {
                values.put(KEY_ExtField4, fieldData.getExtField4());
            }

            values.put(KEY_Notes, fieldData.getNotes());
            if (fieldData.getCreationDate() != null) {
                values.put(KEY_CreationDate, fieldData.getCreationDate());
            }

            values.put(KEY_ViolationFlag, fieldData.getViolationFlag());

            values.put(KEY_DataSyncFlag, 1);
            values.put(KEY_fieldUUID, fieldData.getUuid());

            ret = (int) database.insert(DbAccess.TABLE_FIELD_DATA, null, values);
            Log.i(TAG, "insertFieldDataList() Data result=" + ret);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertFieldDataList() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeBulkBindFieldData(ArrayList<FieldDataForEventDownload> fieldDataList) {

        long count = 0;
        String[] arrColumns = {KEY_EventID, KEY_LocationID, KEY_FieldParameterID,
                KEY_FieldParameterLabel, KEY_MeasurementTime, KEY_StringValue, KEY_oldStringValue,
                KEY_oldNote, KEY_Latitude, KEY_Longitude, KEY_ExtField1, KEY_DeviceID, KEY_SiteID,
                KEY_UserID, KEY_MobileAppID, KEY_ExtField2, KEY_ExtField3, KEY_ExtField4,
                KEY_CreationDate, KEY_ViolationFlag, KEY_DataSyncFlag, KEY_fieldUUID};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_FIELD_DATA + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {

            for (FieldDataForEventDownload fieldData : fieldDataList) {

                if (fieldData.getEventId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_EventID.getIndex(),
                            fieldData.getEventId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_EventID.getIndex());

                if (fieldData.getLocationId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_LocationID.getIndex(),
                            fieldData.getLocationId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_LocationID.getIndex());

                if (fieldData.getFieldParameterId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_FieldParameterID.getIndex(),
                            fieldData.getFieldParameterId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_FieldParameterID.getIndex());

                if (fieldData.getFieldParameterLabel() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_FieldParameterLabel.getIndex(),
                            fieldData.getFieldParameterLabel());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_FieldParameterLabel.getIndex());

                if (fieldData.getMeasurementTime() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_MeasurementTime.getIndex(),
                            fieldData.getMeasurementTime());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_MeasurementTime.getIndex());

                if (fieldData.getStringValue() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_StringValue.getIndex(),
                            fieldData.getStringValue());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_StringValue.getIndex());

                statement.bindNull(FieldDataColsBulkInsert.KEY_oldStringValue.getIndex());

                statement.bindNull(FieldDataColsBulkInsert.KEY_oldNote.getIndex());

                if (fieldData.getLatitude() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_Latitude.getIndex(),
                            fieldData.getLatitude());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_Latitude.getIndex());

                if (fieldData.getLongitude() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_Longitude.getIndex(),
                            fieldData.getLongitude());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_Longitude.getIndex());

                if (fieldData.getExtField1() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_ExtField1.getIndex(),
                            fieldData.getExtField1());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_ExtField1.getIndex());

                if (fieldData.getDeviceId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_DeviceID.getIndex(),
                            fieldData.getDeviceId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_DeviceID.getIndex());

                if (fieldData.getSiteId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_SiteID.getIndex(),
                            fieldData.getSiteId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_SiteID.getIndex());

                if (fieldData.getUserId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_UserID.getIndex(),
                            fieldData.getUserId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_UserID.getIndex());

                if (fieldData.getMobileAppId() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_MobileAppID.getIndex(),
                            fieldData.getMobileAppId());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_MobileAppID.getIndex());

                if (fieldData.getExtField2() != null) {
                    String date = fieldData.getExtField2();
                    if (!date.contains("/")) {
                        date = Util.getMMddyyyyFromMilliSeconds(date);
                    }

                    statement.bindString(FieldDataColsBulkInsert.KEY_ExtField2.getIndex(),
                            date);
                } else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_ExtField2.getIndex());


                if (fieldData.getExtField3() != null) {
                    String time = fieldData.getExtField3();

                    if (!time.contains(":")) {
                        Log.d("FieldData ext3 failed", fieldData.toString());
                        time = Util.gethhmmFromMilliSeconds(time);
                    }

                    statement.bindString(FieldDataColsBulkInsert.KEY_ExtField3.getIndex(),
                            time);
                } else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_ExtField3.getIndex());


                if (fieldData.getExtField4() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_ExtField4.getIndex(),
                            fieldData.getExtField4());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_ExtField4.getIndex());

                if (fieldData.getCreationDate() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_CreationDate.getIndex(),
                            fieldData.getCreationDate());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_CreationDate.getIndex());

                if (fieldData.getViolationFlag() != null)
                    statement.bindLong(FieldDataColsBulkInsert.KEY_ViolationFlag.getIndex(),
                            fieldData.getViolationFlag());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_ViolationFlag.getIndex());

                statement.bindString(FieldDataColsBulkInsert.KEY_DataSyncFlag.getIndex(),
                        "1");

                if (fieldData.getUuid() != null)
                    statement.bindString(FieldDataColsBulkInsert.KEY_fieldUUID.getIndex(),
                            fieldData.getUuid());
                else
                    statement.bindNull(FieldDataColsBulkInsert.KEY_fieldUUID.getIndex());

                count = statement.executeInsert();
                statement.clearBindings();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Bulk insertion of field data:" + e.getMessage());
        } finally {
            database.endTransaction();
        }

        return count;
    }

    private int updateFieldData(FieldDataForEventDownload fieldData) {
        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_EventID, fieldData.getEventId());
        values.put(KEY_LocationID, fieldData.getLocationId());
        values.put(KEY_FieldParameterID, fieldData.getFieldParameterId());
        values.put(KEY_FieldParameterLabel, fieldData.getFieldParameterLabel());
        values.put(KEY_MeasurementTime, fieldData.getMeasurementTime());

        String value = fieldData.getStringValue();
        values.put(KEY_StringValue, value != null ? value.trim() : null);
        values.put(KEY_oldStringValue, (value != null) ? value.trim() : null);

        String note = fieldData.getNotes();
        values.put(KEY_oldNote, (note != null) ? note.trim() : null);

        values.put(KEY_Latitude, fieldData.getLatitude());
        values.put(KEY_Longitude, fieldData.getLongitude());
        values.put(KEY_ExtField1, fieldData.getExtField1());

        values.put(KEY_DeviceID, fieldData.getDeviceId());
        values.put(KEY_SiteID, fieldData.getSiteId());

        values.put(KEY_UserID, fieldData.getUserId());
        values.put(KEY_MobileAppID, fieldData.getMobileAppId());

        if (fieldData.getExtField2() != null) {

            String date = fieldData.getExtField2();
            if (!date.contains("/")) {
                date = Util.getMMddyyyyFromMilliSeconds(date);
            }

            values.put(KEY_ExtField2, date);
        }
        if (fieldData.getExtField3() != null) {
            String time = fieldData.getExtField3();

            if (!time.contains(":")) {
                time = Util.gethhmmFromMilliSeconds(time);
            }

            values.put(KEY_ExtField3, time);
        }

        if (fieldData.getExtField4() != null) {
            values.put(KEY_ExtField4, fieldData.getExtField4());
        }

        values.put(KEY_Notes, fieldData.getNotes());
        if (fieldData.getCreationDate() != null) {
            values.put(KEY_CreationDate, fieldData.getCreationDate());
        }

        values.put(KEY_ViolationFlag, fieldData.getViolationFlag());

        values.put(KEY_DataSyncFlag, 1);
        values.put(KEY_fieldUUID, fieldData.getUuid());

        String whereClause = KEY_fieldUUID + " = ?";

        String[] whereArgs = new String[]{fieldData.getUuid()};
        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);

            Log.i(TAG, "updateDownloadedData() Result=" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateDownloadedData()Error in query=" + e.getMessage());
        }

        return ret;
    }

    public boolean isCopiedforEvent(int eventID, String locationID, String MobileAppID, String setID) {
        int cnt = 0;
        Cursor c = null;
        String query = "select copy_status from d_FieldData where LocationID=" + locationID +
                " and " + KEY_MobileAppID + "=" + MobileAppID +
                " and " + KEY_ExtField1 + "=" + setID +
                " and " + KEY_EventID + "=" + eventID +
                " and " + KEY_COPY_STATUS + "='COPIED'";

        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                cnt = c.getCount();
                if (cnt > 0) {
                    return true;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    public boolean isDataAvailableforLastClosedEvent(int lasteventid, int currentAppID, String extField1, String locationID) {
        boolean result = false;

        String query = "select count (*) from d_FieldData where " + KEY_LocationID + "=" + locationID +
                " and " + KEY_MobileAppID + "=" + currentAppID +
                " and " + KEY_ExtField1 + "=" + extField1 +
                " and " + KEY_EventID + "=" + lasteventid;

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            int cnt = Integer.parseInt(c.getString(0));
            result = cnt > 0;
            c.close();
        }
        return result;
    }

    public ArrayList<FieldData> getDataForLastClosedEvent(int lastclosedEventID, String locationID, String mobileAppaID, String setID) {
        int cnt = 0;
        FieldData fielddata = new FieldData();
        ArrayList<FieldData> alist = new ArrayList<>();
        String query = "select StringValue, Notes,FieldParameterID,UserID,ExtField1 from d_FieldData where" +
                " (StringValue NOTNULL and length(trim(StringValue))>0) " +
                "  and " + KEY_EventID + "=" + lastclosedEventID +
                "  and " + KEY_LocationID + "=" + locationID +
                "  and " + KEY_MobileAppID + "=" + mobileAppaID +
                "  and " + KEY_ExtField1 + "=" + setID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                fielddata = new FieldData();
                fielddata.setStringValue(cursor.getString(0));
                fielddata.setNotes(cursor.getString(1));
                fielddata.setFieldParameterID(cursor.getInt(2));
                fielddata.setUserID(cursor.getInt(3));
                fielddata.setExtField1(cursor.getString(4));
                alist.add(fielddata);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return alist;
    }

    public void updatecopyData(int eventID, int currentAppID, String extField1, String string_val, String notes, int fieldparamid, String locationID, int userid) {
        Cursor c = null;
        int ret = 0;

        String query = "Update d_fielddata set StringValue ='" + string_val + "', Notes='" + notes + "', copy_status='COPIED', copy_from=" + userid + " Where EventID=" + eventID +
                " And MobileAppID=" + currentAppID + " And Extfield1=" + extField1 + " And FieldparameterID=" + fieldparamid + " and LocationID=" + locationID;

        try {
            c = database.rawQuery(query, null);
            c.moveToFirst();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                // c=null;
            }
        }
    }


    public void updateUndoData(int eventID, int currentAppID, String extField1, String locationID) {
        Cursor c = null;
        int ret = 0;

//        String query = "Update d_FieldData set StringValue =NULL,Notes=NULL," +
//                " copy_status=NULL,copy_from=NULL  Where EventID=" + eventID + "" +
//                " and MobileAppID=" + currentAppID + " and Extfield1=" + extField1 +
//                " and LocationID=" + locationID;
//        Log.i(TAG, "updateUndoData() querry:" + query);
//        try {
//            c = database.rawQuery(query, null);
//            c.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (c != null) {
//                c.close();
//                c = null;
//            }
//        }

        String nullValue = null;
        ContentValues values = new ContentValues();
        values.put(KEY_StringValue, nullValue);
        values.put(KEY_Notes, nullValue);
        values.put(KEY_COPY_FROM, nullValue);
        values.put(KEY_COPY_STATUS, nullValue);
        String whereClause = "LocationID=? and ExtField1=? and EventID =? and MobileAppID=?";
        String[] whereArgs = new String[]{locationID, extField1, "" + eventID, "" + currentAppID};
        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);

            Log.i(TAG, "updateUndoData() Result=" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateUndoData()Error in query=" + e.getMessage());
        }
    }

    public CopyTemplateModel copyFieldData(String mobileAppId, String locationId, int eventID) {
        ArrayList<CopyTemplateModel.CopyData> copyDataList = new ArrayList<>();

        String query = "select StringValue, FieldParameterID, FieldParameterLabel, SiteID, ExtField1, " +
                "Notes, UserID , MobileAppID from d_FieldData " +
                "where FieldParameterID not in(15,25) and (StringValue <> '' " +
                "and length(trim(StringValue)) > 0 ) and " +
                "LocationID = ? and EventID = ?";

        String[] whereArgs = new String[]{locationId, eventID + ""};
        Log.i(TAG, "copyTemplate() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        CopyTemplateModel.CopyData copyData = new CopyTemplateModel.CopyData();
                        copyData.setStringValue(cursor.getString(0));
                        copyData.setFieldParameterId(cursor.getInt(1));
                        copyData.setFieldParameterLabel(cursor.getString(2));
                        copyData.setSiteId(cursor.getInt(3));
                        copyData.setExtField1(cursor.getString(4));
                        copyData.setNotes(cursor.getString(5));
                        copyData.setUserId(cursor.getInt(6));
                        copyData.setMobileAppId(cursor.getInt(7));

                        copyDataList.add(copyData);
                    } while (cursor.moveToNext());

                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "copy template error" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        }

        CopyTemplateModel templateModel = new CopyTemplateModel();
        templateModel.setCopiedData(copyDataList);
        return templateModel;
    }

    private FieldData tempFieldDataForPasteTemplate;

    public void pasteFormData(CopyTemplateModel.CopyData copyData, String locationID,
                              int eventID, int userId, String deviceId, int siteId) {

        ArrayList<FieldData> listFieldData = new ArrayList<>();

        long creationDate = System.currentTimeMillis();
        long measurementTime = creationDate + 10;

        if (!isParamIdExists(Integer.parseInt(copyData.getExtField1()), eventID, locationID, siteId,
                copyData.getMobileAppId(), deviceId, userId, 25)) {
            //for date id: 25

            FieldData fieldDataDate = new FieldData();
            fieldDataDate.setEventID(eventID);
            fieldDataDate.setFieldParameterLabel("Date");
            fieldDataDate.setStringValue(Util.getMMddyyyyFromMilliSeconds(creationDate + ""));
            fieldDataDate.setLocationID(locationID);
            fieldDataDate.setFieldParameterID(25);
            fieldDataDate.setExtField1(copyData.getExtField1());
            fieldDataDate.setNotes(copyData.getNotes());
            fieldDataDate.setSiteID(siteId);
            fieldDataDate.setUserID(userId);
            fieldDataDate.setMobileAppID(copyData.getMobileAppId());
            fieldDataDate.setModifiedByDeviceId(deviceId);
            fieldDataDate.setModifiedBy(userId + "");

            try {
                if (GlobalStrings.CURRENT_GPS_LOCATION != null) {
                    fieldDataDate.setLatitude(GlobalStrings.CURRENT_GPS_LOCATION.getLatitude());
                    fieldDataDate.setLongitude(GlobalStrings.CURRENT_GPS_LOCATION.getLongitude());
                } else {
                    fieldDataDate.setLatitude(0.0);
                    fieldDataDate.setLongitude(0.0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fieldDataDate.setLatitude(0.0);
                fieldDataDate.setLongitude(0.0);
            }

            fieldDataDate.setExtField2(Util.getMMddyyyyFromMilliSeconds(creationDate + ""));
            fieldDataDate.setExtField3(Util.getFormattedDateFromMilliS(creationDate, GlobalStrings.DATE_FORMAT_24hr_H_M));
            fieldDataDate.setCreationDate(creationDate);
            fieldDataDate.setMeasurementTime(measurementTime);
            tempFieldDataForPasteTemplate = fieldDataDate;
            listFieldData.add(fieldDataDate);
        }

        if (!isParamIdExists(Integer.parseInt(copyData.getExtField1()), eventID, locationID, siteId,
                copyData.getMobileAppId(), deviceId, userId, 15)) {
            //for date id: 15

            FieldData fieldDataDate = new FieldData();
            fieldDataDate.setEventID(eventID);
            fieldDataDate.setFieldParameterLabel("Time");
            fieldDataDate.setStringValue(Util.getFormattedDateFromMilliS(creationDate, GlobalStrings.DATE_FORMAT_24hr_H_M));
            fieldDataDate.setLocationID(locationID);
            fieldDataDate.setFieldParameterID(15);
            fieldDataDate.setExtField1(copyData.getExtField1());
            fieldDataDate.setNotes(copyData.getNotes());
            fieldDataDate.setSiteID(siteId);
            fieldDataDate.setUserID(userId);
            fieldDataDate.setMobileAppID(copyData.getMobileAppId());
            fieldDataDate.setModifiedByDeviceId(deviceId);
            fieldDataDate.setModifiedBy(userId + "");

            try {
                fieldDataDate.setLatitude(GlobalStrings.CURRENT_GPS_LOCATION.getLatitude());
                fieldDataDate.setLongitude(GlobalStrings.CURRENT_GPS_LOCATION.getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
                fieldDataDate.setLatitude(0.0);
                fieldDataDate.setLongitude(0.0);
            }

            fieldDataDate.setExtField2(Util.getMMddyyyyFromMilliSeconds(creationDate + ""));
            fieldDataDate.setExtField3(Util.getFormattedDateFromMilliS(creationDate, GlobalStrings.DATE_FORMAT_24hr_H_M));
            fieldDataDate.setCreationDate(creationDate);
            fieldDataDate.setMeasurementTime(measurementTime);
            tempFieldDataForPasteTemplate = fieldDataDate;
            listFieldData.add(fieldDataDate);
        } else {
            tempFieldDataForPasteTemplate = getFieldData(eventID, copyData.getExtField1(),
                    locationID, siteId + "", "25", copyData.getMobileAppId() + "");
        }

        if (listFieldData.size() > 0) {
            insertFieldDataList(listFieldData, userId, deviceId);
            updateCreationDateByParamId(eventID, Integer.parseInt(copyData.getExtField1()), locationID, siteId,
                    copyData.getMobileAppId(), tempFieldDataForPasteTemplate.getCreationDate(), 15);
            updateCreationDateByParamId(eventID, Integer.parseInt(copyData.getExtField1()), locationID, siteId,
                    copyData.getMobileAppId(), tempFieldDataForPasteTemplate.getCreationDate(), 25);
        }

        listFieldData.clear();

        FieldData fieldData = new FieldData();
        fieldData.setEventID(eventID);
        fieldData.setFieldParameterLabel(copyData.getFieldParameterLabel());
        fieldData.setStringValue(copyData.getStringValue());
        fieldData.setLocationID(locationID);
        fieldData.setFieldParameterID(copyData.getFieldParameterId());
        fieldData.setExtField1(copyData.getExtField1());
        fieldData.setNotes(copyData.getNotes());
        fieldData.setSiteID(siteId);
        fieldData.setUserID(userId);
        fieldData.setMobileAppID(copyData.getMobileAppId());
        fieldData.setModifiedByDeviceId(deviceId);
        fieldData.setModifiedBy(userId + "");
        listFieldData.add(fieldData);

        if (isParamIdExists(Integer.parseInt(copyData.getExtField1()), eventID, locationID, siteId,
                copyData.getMobileAppId(), deviceId, userId, copyData.getFieldParameterId())) {
            updateRow_dFieldData(listFieldData);
        } else {
            if (tempFieldDataForPasteTemplate != null) {
                listFieldData.get(0).setMeasurementTime(tempFieldDataForPasteTemplate.getMeasurementTime());
                insertFieldDataList(listFieldData, userId, deviceId);
                updateCreationDate(eventID, Integer.parseInt(copyData.getExtField1()), locationID, siteId,
                        copyData.getMobileAppId(), tempFieldDataForPasteTemplate.getCreationDate());
                updateDateInExt2(eventID, Integer.parseInt(copyData.getExtField1()), locationID,
                        siteId, copyData.getMobileAppId(), tempFieldDataForPasteTemplate.getExtField2());
                updateTimeInExt3(eventID, Integer.parseInt(copyData.getExtField1()), locationID,
                        siteId, copyData.getMobileAppId(), tempFieldDataForPasteTemplate.getExtField3());
            }
        }
    }

    private void updateCreationDateForCopiedForms(int appId, int currentSetID, int eventID, int siteID,
                                                  String locationID, int userID, FieldData fieldData) {

        long creationDate = System.currentTimeMillis();

        String oldCreationDate = getCreationDateForMobileApp(appId, eventID, siteID,
                locationID, userID, currentSetID);

        if (oldCreationDate == null) {
            updateCreationDate(eventID, currentSetID, locationID, siteID,
                    appId, creationDate);
        }
    }

    public boolean getFieldDataCount() {
        int count = 0;

        String query = null;
        Cursor cursor;
        try {

            query = " select count(*) from d_FieldData F where ((F.ModificationDate is null and F.StringValue is not null) or \n" +
                    " (F.ModificationDate is not null and F.StringValue is not null)) and \n" +
                    " (F.CreationDate IS NOT NULL and (F.DataSyncFlag is NULL OR " +
                    "F.DataSyncFlag LIKE '' OR F.DataSyncFlag = 2)) and F.StringValue not like '' and " +
                    " (F.EventID>0 AND F.LocationID >0) and FieldParameterID NOT IN (15,25)";

/*            query = "select count(*) from d_FieldData where EventID = ? and LocationID = ? and " +
                    "SiteID = ? and MobileAppID = ? and StringValue IS NOT NULL " +
                    "and StringValue != '' and FieldParameterID NOT IN (15,25)";*/

//            String[] whereArgs = {eventID, locationID, siteId, mobAppId};

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldCount() exception:" + e.getLocalizedMessage());
            return false;
        }

        Log.i(TAG, "getFieldCount() OUT time=" + System.currentTimeMillis());

        return count > 0;
    }

    public FieldData getFieldData(int eventID, String setID, String locID, String siteID,
                                  String fieldParamId, String appId) {

        String[] fieldDataColumns;
        String whereClause;
        String[] whereArgs;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_FieldParameterID, KEY_FieldParameterLabel, KEY_MeasurementTime,
                KEY_StringValue, KEY_CreationDate, KEY_ExtField1, KEY_ExtField2, KEY_ExtField3};

        whereClause = KEY_EventID + "=? and " + KEY_ExtField1 + "=? and " + KEY_LocationID + "=?" +
                " and FieldParameterID = ? and SiteID = ? and MobileAppID = ?";

        whereArgs = new String[]{eventID + "", "" + setID, "" + locID, fieldParamId, siteID, appId};
        FieldData fieldData = new FieldData();
        Cursor cursor;
        try {
            cursor = database.query(DbAccess.TABLE_FIELD_DATA, fieldDataColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    fieldData.setEventID(cursor.getInt(0));
                    fieldData.setLocationID(cursor.getString(1));
                    fieldData.setFieldParameterID(cursor.getInt(2));
                    fieldData.setFieldParameterLabel(cursor.getString(3));
                    fieldData.setMeasurementTime(cursor.getLong(4));
                    fieldData.setStringValue(cursor.getString(5));
                    fieldData.setCreationDate(cursor.getLong(6));
                    fieldData.setExtField1(cursor.getString(7));
                    fieldData.setExtField2(cursor.getString(8));
                    fieldData.setExtField3(cursor.getString(9));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return fieldData;
    }

    public HashMap<String, FieldData> getRowDataForPrintLabel(int eventID, String setID, String locID, String siteID,
                                                              String appId) {

        HashMap<String, FieldData> mapFieldData = new HashMap<>();
        String[] fieldDataColumns;
        String whereClause;
        String[] whereArgs;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_FieldParameterID, KEY_FieldParameterLabel,
                KEY_StringValue, KEY_ExtField1, KEY_ExtField2, KEY_ExtField3};

        whereClause = KEY_EventID + "=? and " + KEY_ExtField1 + "=? and " + KEY_LocationID + "=?" +
                " and SiteID = ? and MobileAppID = ?";

        whereArgs = new String[]{eventID + "", "" + setID, "" + locID, siteID, appId};
        Cursor cursor;
        try {
            cursor = database.query(DbAccess.TABLE_FIELD_DATA, fieldDataColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FieldData fieldData = new FieldData();
                    fieldData.setEventID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EventID)));
                    fieldData.setLocationID(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocationID)));
                    fieldData.setFieldParameterID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FieldParameterID)));
                    fieldData.setFieldParameterLabel(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FieldParameterLabel)));
                    fieldData.setStringValuePrintUseOnly(cursor.getString(cursor.getColumnIndexOrThrow(KEY_StringValue)));
                    fieldData.setExtField1(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ExtField1)));
                    fieldData.setExtField2(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ExtField2)));
                    fieldData.setExtField3(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ExtField3)));
                    mapFieldData.put(fieldData.getFieldParameterLabel().trim().toLowerCase(), fieldData);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mapFieldData;
    }

    public boolean clearLocationData(String eventId, String locId, String siteId) {
        int ret = 0;
        String whereClause = "EventID =? and LocationID=? and SiteID=?";
        String[] whereArgs = new String[]{eventId, locId, siteId};
        try {
            ret = database.delete(DbAccess.TABLE_FIELD_DATA, whereClause, whereArgs);
            database.delete(DbAccess.TABLE_ATTACHMENT, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret > 0;
    }

    public class LocSet {
        String mobileAppID;
        String locationID;
        int setID;

        public String getMobID() {
            return mobileAppID;
        }

        public String getLocID() {
            return locationID;
        }

        public int getSetID() {
            return setID;
        }
    }

    public List<LocSet> getLocationSetMap(int eventID, int siteID,
                                          int parentAppID, int currentAppID, String locId) {
        List<LocSet> list = new ArrayList();
        LocSet locSet;
        String query = null;
        String[] whereArgs = null;

        if (currentAppID == 0) {
            query = "SELECT DISTINCT F.LocationID, F.ExtField1 FROM d_FieldData as F " +
                    "JOIN d_Event as E on F.EventID=E.EventID " +
                    "where E.SiteID=? and F.MobileAppID in (select distinct a.MobileAppID from s_MetaData A " +
                    " INNER JOIN s_MobileApp B ON A.MobileAppID = B.MobileAppID Where parent_app_id = ?) " +
                    " and F.CreationDate is not null and F.EventID=" + eventID +
                    " and and F.LocationID = ? order By F.LocationID";//E.mobileappid
            whereArgs = new String[]{"" + siteID, "" + parentAppID, locId};
        } else {
            query = "SELECT DISTINCT F.LocationID, F.ExtField1 FROM d_FieldData as F " +
                    "JOIN d_Event as E on F.EventID=E.EventID " +
                    "where E.SiteID=? and F.MobileAppID=? and F.LocationID = ? and F.CreationDate is not null and F.EventID=" + eventID +
                    " order By F.LocationID";
            whereArgs = new String[]{"" + siteID, "" + currentAppID, locId};
        }
        Log.i(TAG, "getLocationSetMap() query=" + query + "\n" + "whereArgs=" + whereArgs);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    locSet = new LocSet();
                    locSet.locationID = cursor.getString(0);
                    locSet.setID = cursor.getInt(1);
                    locSet.mobileAppID = currentAppID + "";
                    list.add(locSet);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLocationSetMap() exception:" + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    public HashMap<String, String> getRowData(String mobID, String locID,
                                              int setID, HashMap<String,
            FieldParamInfo> paramLabelList, int siteID, int eventID) {

        HashMap<String, String> map = new HashMap<>();

        String query = "select DISTINCT M.FieldParameterID, M.ParameterLabel, F.StringValue " +
                "FROM d_FieldData as F JOIN d_Event as E on F.EventID = E.EventID " +
                "JOIN s_MetaData as M on M.FieldParameterID = F.FieldParameterID " +
                "where F.ExtField1=? and F.LocationID=? and F.SiteID=? and F.MobileAppID=? " +
                "and E.EventStatus>0 and F.EventID =?";
        Cursor cursor = null;
        String[] whereArgs = new String[]{"" + setID, locID, siteID + "", mobID, eventID + ""};

        //adding setid value to fill first setid column
        map.put("SetID", setID + "");

        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (paramLabelList.containsKey(cursor.getString(0)))
                        map.put(cursor.getString(1), cursor.getString(2));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getRowData() exception:" + e.getMessage());

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return map;
    }


    public boolean isExistsLocationData(int locID, int siteID, int appID) {

        boolean exists = false;
        int existingLocID = -1;
        String[] whereArgs = null;


        String query = "SELECT DISTINCT LocationID FROM d_FieldData as F " +
                " JOIN d_Event as E on F.EventID=E.EventID " +
                " where LocationID=? and E.SiteID=? and F.MobileAppID=? and CreationDate IS NOT NULL";

        whereArgs = new String[]{"" + locID, "" + siteID, "" + appID};
        Log.i(TAG, " isExistsLocationData() query=" + query);
        Log.i(TAG, "isExistsLocationData() whereArgs=" + whereArgs[0] + whereArgs[1] + whereArgs[2]);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        existingLocID = cursor.getInt(0);
                        break;
                    } while (cursor.moveToNext());
                }

                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isExistsLocationData() exception:" + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        if (existingLocID == locID) {
            exists = true;
        }
//		isExistsAppData(appID, locID);

        return exists;
    }

    public List<FieldData> getListForEventDownload(int eventID) {
        List<FieldData> dFielddataList = new ArrayList<>();
        FieldData fielddata = null;
        String[] whereClause = null;

        String query = null;

        query = "select distinct F.LocationID, F.FieldParameterID, F.MeasurementTime" +
                ",F.StringValue, F.Units, F.Latitude, F.Longitude, F.Notes, F.CreationDate," +
                " F.ExtField1, F.FieldParameterLabel, F.FieldDataID," +
                " F.CorrectedLatitude, F.CorrectedLongitude, F.ExtField2," +
                " F.ExtField3, F.ExtField4, F.SiteID, F.UserID,F.MobileAppID, " +
                "F.ModificationDate,F.deviceId,F.modifiedByDeviceId,F.modifiedBy , F.DataSyncFlag " +
                " from d_FieldData as F where F.EventID=" + eventID;
//        whereClause = new String[]{"" + eventID};
        Cursor c = null;

        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    fielddata = cursorToDFieldDataforDownloadEvent(c, eventID);
                    dFielddataList.add(fielddata);
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                // c = null;
            }
        }

        return dFielddataList;
    }

    public String getCreationDateForMobileApp(int currentAppID, int eventID, int siteID, String locationID, int userID, int extfield1) {

        String creationdate11 = null;
        Cursor cursor = null;
        String query = "select distinct CreationDate from d_FieldData where" +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID + " and LocationID=" + locationID + " and UserID=" + userID + " and (FieldParameterID=25 OR FieldParameterID=15)";

        query = "select min(CreationDate) from d_FieldData where" +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID + " and LocationID=" + locationID + " and ExtField1=" + extfield1;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    creationdate11 = cursor.getString(0);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                // c = null;
            }

        }

        return creationdate11;

    }

    public String getExt2ForMobileApp(int currentAppID, int eventID, int siteID, String locationID, int extfield1) {

        String ext2 = null;
        Cursor cursor = null;
        String query = "select distinct CreationDate from d_FieldData where" +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID + " and LocationID=" + locationID + " and (FieldParameterID=25 OR FieldParameterID=15)";

        query = " select StringValue from d_FieldData where " +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID +
                " and LocationID=" + locationID + " and ExtField1=" + extfield1 + " and FieldParameterID=25";
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                ext2 = cursor.getString(0);

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getExt2NExt3ForMobileApp() error:" + ext2);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return ext2;

    }


    public String getExt3ForMobileApp(int currentAppID, int eventID, int siteID, String locationID, int extfield1) {

        String ext3 = null;
        Cursor cursor = null;
        String query = "select distinct CreationDate from d_FieldData where" +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID + " and LocationID=" + locationID + " and (FieldParameterID=25 OR FieldParameterID=15)";

        query = " select StringValue from d_FieldData where " +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID +
                " and LocationID=" + locationID + " and ExtField1=" + extfield1 + " and FieldParameterID=15";
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                ext3 = cursor.getString(0);

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getExt3ForMobileApp() error:" + ext3);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return ext3;
    }

    //you can get field data list by event
    public List<FieldDataSyncStaging> getFieldDataListForEvent(int eventID) {
        List<FieldDataSyncStaging> dfieldData = new ArrayList<FieldDataSyncStaging>();
        FieldDataSyncStaging data = null;

        String query = null;
        String[] whereClause = null;
//
        query = "select F.LocationID, F.FieldParameterID, F.MeasurementTime, " +
                " F.StringValue, F.Units, F.Latitude, F.Longitude, F.Notes, F.CreationDate, F.ExtField1, " +
                "F.FieldParameterLabel, F.FieldDataID, " +
                " F.CorrectedLatitude, F.CorrectedLongitude, F.ExtField2, F.ExtField3, F.ExtField4, F.SiteID, F.UserID, " +
                "F.MobileAppID, F.ModificationDate,F.deviceId,F.modifiedByDeviceId,F.modifiedBy from d_FieldData as F " +
                " where F.EventID=? " +
                " and ((F.ModificationDate is null and F.StringValue is not null) or " +
                "(F.ModificationDate is not null and F.StringValue is null)" +
                " or (F.ModificationDate is not null and F.StringValue is not null)) and " +
                "(F.CreationDate IS NOT NULL and (F.DataSyncFlag is NULL or F.DataSyncFlag = 2))";

        whereClause = new String[]{"" + eventID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereClause);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                data = cursorToDFieldData(cursor, eventID);//04-Dec-15 added
                dfieldData.add(data);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldDataListForEvent() exception:" + e.getLocalizedMessage());
            return dfieldData;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return dfieldData;
    }

    public ArrayList<FieldDataSyncStaging> getAllFieldDataForReordering(int eventID) {
        ArrayList<FieldDataSyncStaging> fieldDataList = new ArrayList<>();
        FieldDataSyncStaging data = null;

        String query = null;
        String[] whereClause = null;

        query = "select F.LocationID, F.FieldParameterID, F.MeasurementTime, " +
                "F.StringValue, F.Units, F.Latitude, F.Longitude, F.Notes, F.CreationDate, F.ExtField1, " +
                "F.FieldParameterLabel, F.FieldDataID, F.CorrectedLatitude, F.CorrectedLongitude, " +
                "F.ExtField2, F.ExtField3, F.ExtField4, F.SiteID, F.UserID, " +
                "F.MobileAppID, F.ModificationDate, F.deviceId, F.modifiedByDeviceId, F.modifiedBy " +
                "from d_FieldData as F where EventID = ? order by LocationID, MobileAppID, " +
                "CreationDate, ExtField1";

        whereClause = new String[]{"" + eventID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereClause);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                data = cursorToDFieldData(cursor, eventID);//04-Dec-15 added
                fieldDataList.add(data);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldDataListForEvent() exception:" + e.getLocalizedMessage());
            return fieldDataList;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return fieldDataList;
    }

    public void updateFieldDataSet(FieldDataSyncStaging fieldData, int setIndex) {
        Cursor cursor = null;

        String query = "UPDATE d_FieldData SET modifiedBy = ?, ExtField1 = ? where EventID = ? and ExtField1 = ? " +
                "and LocationID = ? and MobileAppID= ? and CreationDate = ?";

        String[] whereClause = new String[]{"" + fieldData.getModifiedBy(), setIndex + "",
                fieldData.getEventId() + "", fieldData.getExtField1(), fieldData.getLocationId() + "",
                fieldData.getMobileAppId() + "", fieldData.getCreationDate() + ""};

        int ret = 0;
        try {
            cursor = database.rawQuery(query, whereClause);
            Log.i(TAG, " updateFieldDataset() result count:" + cursor.getCount());
            cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public List<FieldDataSyncStaging> getFieldDataListForUploadAll() {
        List<FieldDataSyncStaging> dfieldData = new ArrayList<FieldDataSyncStaging>();
        FieldDataSyncStaging data = null;

        String query = null;
        String[] whereClause = null;

        //23-Mar-17 NEGATIVE EVENT DATA WILL NOT SYNC

        query = "select F.LocationID, F.FieldParameterID, F.MeasurementTime, " +
                "F.StringValue, F.Units, F.Latitude, F.Longitude, F.Notes, F.CreationDate, F.ExtField1, " +
                "F.FieldParameterLabel, F.FieldDataID, " +
                "F.CorrectedLatitude, F.CorrectedLongitude, F.ExtField2, " +
                "F.ExtField3, F.ExtField4, F.SiteID, F.UserID, " +
                "F.MobileAppID, F.ModificationDate,F.deviceId, " +
                "F.modifiedByDeviceId,F.modifiedBy,F.EventID, F.violationFlag, F.fieldUUID from d_FieldData as F " +
                "where ((F.ModificationDate is null and F.StringValue is not null) or " +
                "(F.ModificationDate is not null and F.StringValue is not null)) and " +
                "(F.CreationDate IS NOT NULL and (F.DataSyncFlag is NULL OR F.DataSyncFlag LIKE '')) " +
                "and F.StringValue not like '' and (F.EventID > 0  AND F.LocationID > 0)";
        // whereClause = new String[]{"" + eventID};
        Cursor cursor = null;

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);
        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));

        try {
            cursor = database.rawQuery(query, null);
            if (cursor == null) {
                return null;
            }

            logDetails.setScreenName("All un-synced data query");

            boolean cursorHasData = !cursor.isClosed();
            boolean databaseConnection = database != null && database.isOpen();

            logDetails.setDetails("Checking cursor and database instance: Database: "
                    + databaseConnection + " Cursor: " + cursorHasData);

            tempLogsDataSource.insertTempLogs(logDetails);

            if (cursor.moveToFirst()) {
                do {
                    data = cursorToDFieldDataSyncAll(cursor);//04-Dec-15 added
                    dfieldData.add(data);
                    Log.i(TAG, "Event=" + data.getEventId() + " Field Label=" + data.getFieldParameterLabel() + " FpID=" + data.getFieldParameterId() + " String Value=" + data.getStringValue());
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldDataListForEvent() exception:" + e.getLocalizedMessage());

            logDetails.setScreenName("Inside get all unsynced data query");
            logDetails.setDetails("Inside Fetch all unsynced data query exception: "
                    + e);

            tempLogsDataSource.insertTempLogs(logDetails);

            return dfieldData;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dfieldData;
    }

    public List<FieldDataSyncStaging> getFieldDataListForUpdate() {
        List<FieldDataSyncStaging> dfieldData = new ArrayList<FieldDataSyncStaging>();
        FieldDataSyncStaging data = null;

        String query = null;
        String[] whereClause = null;

        //23-Mar-17 NEGATIVE EVENT DATA WILL NOT SYNC

        query = "select F.LocationID, F.FieldParameterID, F.MeasurementTime, " +
                "F.StringValue, F.Units, F.Latitude, F.Longitude, F.Notes, F.CreationDate, F.ExtField1, " +
                "F.FieldParameterLabel, F.FieldDataID, " +
                "F.CorrectedLatitude, F.CorrectedLongitude, F.ExtField2, " +
                "F.ExtField3, F.ExtField4, F.SiteID, F.UserID, " +
                "F.MobileAppID, F.ModificationDate,F.deviceId, " +
                "F.modifiedByDeviceId,F.modifiedBy,F.EventID, F.violationFlag, F.fieldUUID from d_FieldData as F " +
                "where ((F.ModificationDate is null and F.StringValue is not null) or " +
                "(F.ModificationDate is not null and F.StringValue is not null)) and " +
                "(F.CreationDate IS NOT NULL and F.DataSyncFlag = 2 ) " +
                "and F.StringValue not like '' and (F.EventID > 0  AND F.LocationID > 0)";
        // whereClause = new String[]{"" + eventID};
        Cursor cursor = null;

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);
        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));

        try {
            cursor = database.rawQuery(query, null);
            if (cursor == null) {
                return null;
            }

            logDetails.setScreenName("All un-synced data query");

            boolean cursorHasData = !cursor.isClosed();
            boolean databaseConnection = database != null && database.isOpen();

            logDetails.setDetails("Checking cursor and database instance: Database: "
                    + databaseConnection + " Cursor: " + cursorHasData);

            tempLogsDataSource.insertTempLogs(logDetails);

            if (cursor.moveToFirst()) {
                do {
                    data = cursorToDFieldDataSyncAll(cursor);//04-Dec-15 added
                    dfieldData.add(data);
                    Log.i(TAG, "Event=" + data.getEventId() + " Field Label=" + data.getFieldParameterLabel() + " FpID=" + data.getFieldParameterId() + " String Value=" + data.getStringValue());
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldDataListForEvent() exception:" + e.getLocalizedMessage());

            logDetails.setScreenName("Inside get all unsynced data query");
            logDetails.setDetails("Inside Fetch all unsynced data query exception: "
                    + e);

            tempLogsDataSource.insertTempLogs(logDetails);

            return dfieldData;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dfieldData;
    }

    public void checkAndUpdateClientEventInFieldData() {

        Log.i(TAG, "checkAndUpdateClientEventInData() IN time=" + System.currentTimeMillis());

        String query, query1 = null;
        String[] whereClause = new String[]{};
        Cursor cursor, cursor2;
        try {
            query = "Select EventID,ClientEventID from d_Event where ClientEventID IN " +
                    "(select DISTINCT EventID from d_FieldData where EventID<0)";
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String map_event = cursor.getString(0);
                    String clientevent = cursor.getString(1);
                    query1 = "Update d_FieldData set EventID=" + map_event + " where EventID=" + clientevent;
                    cursor2 = database.rawQuery(query1, null);
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkAndUpdateClientEventInData() exception:" + e.getLocalizedMessage());
        }

        Log.i(TAG, "checkAndUpdateClientEventInData() OUT time=" + System.currentTimeMillis());
    }


    public void checkAndUpdateClientEventInAttachmentData() {

        Log.i(TAG, "checkAndUpdateClientEventInAttachmentData() IN time=" + System.currentTimeMillis());

        String query, query1 = null;
        String[] whereClause = null;
//
        whereClause = new String[]{};
        Cursor cursor, cursor2;
        try {
            query = "Select EventID,ClientEventID from d_Event where ClientEventID IN (select DISTINCT EventID from d_Attachment where EventID<0)";
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String clientevent = cursor.getString(1);
                    String map_event = cursor.getString(0);
                    query1 = "Update d_Attachment set EventID=" + map_event + " where EventID=" + clientevent;
                    cursor2 = database.rawQuery(query1, null);
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkAndUpdateClientEventInAttachmentData() exception:" + e.getLocalizedMessage());
        }
        Log.i(TAG, "checkAndUpdateClientEventInAttachmentData() OUT time=" + System.currentTimeMillis());
    }

    public boolean isFieldDataAvailableToSync() {
        //06-Jul-17 DON'T SYNC SAME DATA IF ALREADY SYNCED
        updateSyncFlagFor_AlreadySyncedData();
        isDateSync();

        Log.i(TAG, "isFieldDataAvailableToSync() IN time=" + System.currentTimeMillis());
        int count = 0;

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));

        String query = null;
        Cursor cursor;
        try {
            query = " select count(*) from d_FieldData F where ((F.ModificationDate is null and F.StringValue is not null) or \n" +
                    " (F.ModificationDate is not null and F.StringValue is not null)) and \n" +
                    " (F.CreationDate IS NOT NULL and (F.DataSyncFlag is NULL OR " +
                    "F.DataSyncFlag LIKE '' OR F.DataSyncFlag = 2)) and F.StringValue not like '' and " +
                    " (F.EventID>0 AND F.LocationID >0)";

            cursor = database.rawQuery(query, null);

            logDetails.setScreenName("isFieldDataAvailableToSync() query");

            boolean cursorHasData = cursor != null && !cursor.isClosed() && cursor.getCount() > 0;
            boolean databaseConnection = database != null && database.isOpen();

            logDetails.setDetails("Checking cursor and database instance: Database: "
                    + databaseConnection + " Cursor: " + cursorHasData);

            tempLogsDataSource.insertTempLogs(logDetails);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isFieldDataAvailableToSync() exception:" + e.getLocalizedMessage());

            logDetails.setScreenName("isFieldDataAvailableToSync() query exception");
            logDetails.setDetails("Field data to sync string value check query exception: "
                    + e);

            tempLogsDataSource.insertTempLogs(logDetails);
            return false;
        }

        Log.i(TAG, "isFieldDataAvailableToSync() OUT time=" + System.currentTimeMillis());

        return count > 0;
    }


    //checking if any row has string value changed but sync flag is not managed,
    //so if any rows found with not matching stringvalue with old one then its flag is managed accordingly
    private boolean isDateSync() {
        Cursor c, c1;
        int cnt = 0;
        String query, query1;

        try {
            query = "select distinct MobileAppID,SiteID,LocationID,ExtField1,EventID from" +
                    " d_FieldData where (StringValue!=oldStringValue or oldNote!=Notes) " +
                    "and (FieldParameterID=25 or FieldParameterID=15)";

            c = database.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                FieldData fd = new FieldData();
                int mobid = c.getInt(0);
                fd.setMobileAppID(mobid);
                int siteid = c.getInt(1);
                fd.setSiteID(siteid);
                String locid = c.getString(2);
                fd.setLocationID(locid);
                String setid = c.getString(3);
                fd.setExtField1(setid);
                int eventid = c.getInt(4);
                fd.setEventID(eventid);

                try {

                    String nullValue = null;
                    query1 = "update d_FieldData set DataSyncFlag = (CASE WHEN DataSyncFlag = 1 THEN 2 WHEN DataSyncFlag = 2 THEN 2 ELSE NULL END) "
                            + " where EventID=" + eventid + " and SiteID=" + siteid +
                            " and MobileAppID=" + mobid + " and LocationID=" + locid + " and ExtField1=" + setid;

                    c1 = database.rawQuery(query1, null);
                    if (c1 != null && c1.moveToFirst()) {
                        cnt = c1.getCount();
                        c1.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "isDateSync() Exception:" + e.getMessage());
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isDateSync() exception:" + e.getLocalizedMessage());

            return false;
        }
        return cnt > 0;
    }

    public boolean isFieldDataAvailableToSync_Service() {
        //06-Jul-17 DON'T SYNC SAME DATA IF ALREADY SYNCED
        updateSyncFlagFor_AlreadySyncedData();

        Log.i(TAG, "isFieldDataAvailableToSync() IN time=" + System.currentTimeMillis());
        int count = 0;

        String query = null;
        Cursor cursor;
        try {

            query = " select count(*) from d_FieldData F where ((F.ModificationDate is null and F.StringValue is not null) or \n" +
                    " (F.ModificationDate is not null and F.StringValue is null) or \n" +
                    " (F.ModificationDate is not null and F.StringValue is not null)) and \n" +
                    " (F.CreationDate IS NOT NULL and F.DataSyncFlag IS NOT 1) and " +
                    " (F.EventID>0 AND F.LocationID >0)";

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isFieldDataAvailableToSync_Service() exception:" + e.getLocalizedMessage());
            return false;
        }


        return count > 0;// >10 updated to 0
    }

    public boolean isDataAvailableToSync() {

        Log.i(TAG, "isDataAvailableToSync() IN time=" + System.currentTimeMillis());
        int count = 0;

        String query = null;
        String[] whereClause = new String[]{};
        Cursor cursor, cursor2;
        try {
            query = "select count(*) from d_FieldData where DataSyncFlag IS NOT 1 ";
            cursor = database.rawQuery(query, null);
            Log.i(TAG, "isDataAvailableToSync() d_FieldData query=" + query);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();

                if (count < 1) {//25-Feb-17 ALL FIELD-DATA IS SYNCED NOW CHECK FOR ATTACHMENT
                    query = "select count(*) from d_Attachment where DataSyncFlag IS NOT 1";
                    cursor2 = database.rawQuery(query, null);
                    Log.i(TAG, "isDataAvailableToSync() d_Attachment query=" + query);

                    if (cursor2 != null && cursor2.moveToFirst()) {
                        count = cursor2.getInt(0);
                        cursor2.close();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isDataAvailableToSync() exception:" + e.getLocalizedMessage());
            return false;
        }

        Log.i(TAG, "isDataAvailableToSync() OUT time=" + System.currentTimeMillis());

        return count > 0;
    }

    //04-Dec-15 Added this method
    private FieldDataSyncStaging cursorToDFieldData(Cursor cursor, int eventID) {
        FieldDataSyncStaging data = new FieldDataSyncStaging();

        try {
            Integer LocID = cursor.getInt(0);

//            String loc = cursor.getString(1);
            Integer paramID = cursor.getInt(1);
            Long measurementTime = cursor.getLong(2);

            String stringvalue = cursor.getString(3);
            String units = cursor.getString(4);
            Double lat = cursor.getDouble(5);
            Double lon = cursor.getDouble(6);


            String note = cursor.getString(7);
            Long creationDatee = cursor.getLong(8);

            String ext1 = cursor.getString(9);
            String paramLable = cursor.getString(10);

            Integer fDataID = cursor.getInt(11);
            Double corrlat = cursor.getDouble(12);
            Double corrlon = cursor.getDouble(13);
            String ext2 = cursor.getString(14);
            String ext3 = cursor.getString(15);
            String ext4 = cursor.getString(16);
            data.setSiteId(cursor.getInt(17));

            int userID = cursor.getInt(18);
            if (userID < 1) {
                userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
            }
            Log.i(TAG, "USerID:" + userID);
            data.setUserId(userID);
            Integer mobappID = cursor.getInt(19);
            Long modDate = cursor.getLong(20);
            String mDeviceId = cursor.getString(21);
            String ModifiedByDeviceId = cursor.getString(22);
            Integer ModifiedBy = cursor.getInt(23);

            Integer eventStatus = 1;
            Boolean processFlag = true;

            data.setmDeviceId(mDeviceId);
            data.setModifiedByDeviceId(ModifiedByDeviceId);
            data.setModifiedBy(ModifiedBy);
            data.setFieldDataSyncStaginId(fDataID);//04-Dec-15
            data.setEventId(eventID);
            data.setFieldParameterLabel(paramLable == null ? "" : paramLable);
            data.setFieldParameterId(paramID);
            data.setLocationId(LocID);
            data.setStringValue(stringvalue);//(stringvalue == null ? "" : stringvalue);

            data.setUnits(units);// == null ? "" : units);
            data.setLatitude(lat);//== null ? 0.0 : lat);
            data.setLongitude(lon);//== null ? 0.0 : lon);
            data.setExtField1(ext1);// == null ? "" : ext1);
            data.setExtField2(ext2);// == null ? "" : ext2);
            data.setExtField3(ext3);// == null ? "" : ext3);
            data.setExtField4(ext4);//== null ? "" : ext4);

            data.setNotes(note);// == null ? "" : note);
            data.setCreationDate(creationDatee);// == null ? (long) 0.0 : creationDatee);

            data.setCorrectedLatitude(corrlat);// == null ? 0.0 : corrlat);
            data.setCorrectedLongitude(corrlon);// == null ? 0.0 : corrlon);
            data.setMobileAppId(mobappID);// == null ? -1 : mobappID);
            data.setSetId(Integer.parseInt(ext1));//12-JAN-15
            data.setMeasurementTime(measurementTime);
            data.setModificationDate(modDate);//== null) ? 0 : modDate);

            long mDate = data.getMeasurementTime() == null ? 0 : data.getMeasurementTime();
            if (mDate < 86400000) {
                data.setMeasurementTime(data.getCreationDate() == null ? 0 : data.getCreationDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "cursorToDFieldData() exception:" + e.getLocalizedMessage());
        }
        return data;
    }

    private FieldData cursorToDFieldDataforDownloadEvent(Cursor cursor, int eventID) {
        FieldData data = new FieldData();

        try {
            Integer LocID = cursor.getInt(0);

//            String loc = cursor.getString(1);
            Integer paramID = cursor.getInt(1);
            Long measurementTime = cursor.getLong(2);

            String stringvalue = cursor.getString(3);
            String units = cursor.getString(4);
            Double lat = cursor.getDouble(5);
            Double lon = cursor.getDouble(6);


            String note = cursor.getString(7);
            Long creationDatee = cursor.getLong(8);

            String ext1 = cursor.getString(9);
            String paramLable = cursor.getString(10);

            Integer fDataID = cursor.getInt(11);
            Double corrlat = cursor.getDouble(12);
            Double corrlon = cursor.getDouble(13);
            String ext2 = cursor.getString(14);
            String ext3 = cursor.getString(15);
            String ext4 = cursor.getString(16);
            data.setSiteID(cursor.getInt(17));

            int userID = cursor.getInt(18);
            if (userID < 1) {
                userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
            }
            Log.i(TAG, "USerID:" + userID);
            data.setUserID(userID);
            Integer mobappID = cursor.getInt(19);
            Long modDate = cursor.getLong(20);
            String mDeviceId = cursor.getString(21);
            String ModifiedByDeviceId = cursor.getString(22);
            Integer ModifiedBy = cursor.getInt(23);
            String dataSyncFlag = cursor.getString(24);

            Integer eventStatus = 1;
            Boolean processFlag = true;


            data.setDataSyncFlag(dataSyncFlag);
            data.setDeviceId(mDeviceId);
            data.setModifiedByDeviceId(ModifiedByDeviceId);
            data.setModifiedBy(String.valueOf(ModifiedBy));
            // data.setEventStatus(eventStatus);// TODO: 13-Dec-15
            // data.setProcessFlag(processFlag);// TODO: 13-Dec-15
            data.setFieldDataID(fDataID);// TODO: 04-Dec-15
//            data.setDataSyncFlag(0);// TODO: 04-Dec-15 added
//            data.setEmailSentFlag(0);// TODO: 04-Dec-15 added
            data.setEventID(eventID);
            data.setFieldParameterLabel(paramLable == null ? "" : paramLable);
//            data.setFieldParameterLabelAlias("");// TODO: 04-Dec-15
            data.setFieldParameterID(paramID);

//            data.setLocation(loc);//(loc == null ? "" : loc);
            data.setLocationID(String.valueOf(LocID));


            data.setStringValue(stringvalue);//(stringvalue == null ? "" : stringvalue);
//            data.setNumericValue((double) 0);// TODO: 04-Dec-15

            data.setUnits(units);// == null ? "" : units);
            data.setLatitude(lat);//== null ? 0.0 : lat);
            data.setLongitude(lon);//== null ? 0.0 : lon);
            data.setExtField1(ext1);// == null ? "" : ext1);
            data.setExtField2(ext2);// == null ? "" : ext2);
            data.setExtField3(ext3);// == null ? "" : ext3);
            data.setExtField4(ext4);//== null ? "" : ext4);

//            data.setExtField5("");// TODO: 04-Dec-15
//            data.setExtField6("");// TODO: 04-Dec-15
//            data.setExtField7("");// TODO: 04-Dec-15

            data.setNotes(note);// == null ? "" : note);
            data.setCreationDate(creationDatee);// == null ? (long) 0.0 : creationDatee);
//			default
//            data.setParentSetId(0);// TODO: 04-Dec-15


//			data.setFieldParameterLabelAlias(cursor.getString(12));
//            data.setFieldDataId(cursor.getInt(13));// TODO: 04-Dec-15
            data.setCorrectedLatitude(corrlat);// == null ? 0.0 : corrlat);
            data.setCorrectedLongitude(corrlon);// == null ? 0.0 : corrlon);
            data.setMobileAppID(mobappID);// == null ? -1 : mobappID);
            data.setCurSetID(Integer.parseInt(ext1));// TODO: 12-JAN-15
//            data.setServerCreationDate((long) 0);// TODO: 04-Dec-15
//            data.setServerModificationDate((long) 0);// TODO: 04-Dec-15
            data.setMeasurementTime(measurementTime);
            data.setModificationDate(modDate);//== null) ? 0 : modDate);

            //  long mdate = data.getMeasurementTime() == null ? 0 : data.getMeasurementTime();
            //if (mdate == 0 || mdate < 86400000) {
            data.setMeasurementTime(data.getCreationDate());
            //}

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "cursorToDFieldData() exception:" + e.getLocalizedMessage());
        }

        return data;
    }

    private FieldDataSyncStaging cursorToDFieldDataSyncAll(Cursor cursor) {
        FieldDataSyncStaging data = new FieldDataSyncStaging();

        try {
            Integer LocID = cursor.getInt(0);

//            String loc = cursor.getString(1);
            Integer paramID = cursor.getInt(1);
            Long measurementTime = cursor.getLong(2);

            String stringvalue = cursor.getString(3);
            String units = cursor.getString(4);
            Double lat = cursor.getDouble(5);
            Double lon = cursor.getDouble(6);

            String note = cursor.getString(7);
            Long creationDatee = cursor.getLong(8);

            String ext1 = cursor.getString(9);
            String paramLable = cursor.getString(10);

            Integer fDataID = cursor.getInt(11);
            Double corrlat = cursor.getDouble(12);
            Double corrlon = cursor.getDouble(13);
            String ext2 = cursor.getString(14);
            String ext3 = cursor.getString(15);
            String ext4 = cursor.getString(16);
            data.setSiteId(cursor.getInt(17));

            int userID = cursor.getInt(18);
            if (userID < 1) {
                userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
            }
            Log.i(TAG, "USerID:" + userID);
            data.setUserId(userID);
            Integer mobappID = cursor.getInt(19);
            Long modDate = cursor.getLong(20);
            String mDeviceId = cursor.getString(21);
            String ModifiedByDeviceId = cursor.getString(22);
            Integer ModifiedBy = cursor.getInt(23);
            String eventID = cursor.getString(24);
            int violationFlag = cursor.getInt(cursor.getColumnIndexOrThrow("violationFlag"));
            String uuid = cursor.getString(26);

            data.setmDeviceId(mDeviceId);
            data.setModifiedByDeviceId(ModifiedByDeviceId);
            data.setModifiedBy(ModifiedBy);

            data.setFieldDataSyncStaginId(fDataID);// TODO: 04-Dec-15

            data.setEventId(Integer.parseInt(eventID));
            data.setFieldParameterLabel(paramLable == null ? "" : paramLable);

            data.setFieldParameterId(paramID);

            data.setLocationId(LocID);


            data.setStringValue(stringvalue);//(stringvalue == null ? "" : stringvalue);
            data.setUnits(units);// == null ? "" : units);
            data.setLatitude(lat);//== null ? 0.0 : lat);
            data.setLongitude(lon);//== null ? 0.0 : lon);
            data.setExtField1(ext1);// == null ? "" : ext1);
            data.setExtField2(ext2);// == null ? "" : ext2);
            data.setExtField3(ext3);// == null ? "" : ext3);
            data.setExtField4(ext4);//== null ? "" : ext4);
            data.setNotes(note);// == null ? "" : note);
            data.setCreationDate(creationDatee);// == null ? (long) 0.0 : creationDatee);
            data.setCorrectedLatitude(corrlat);// == null ? 0.0 : corrlat);
            data.setCorrectedLongitude(corrlon);// == null ? 0.0 : corrlon);
            data.setMobileAppId(mobappID);// == null ? -1 : mobappID);
            data.setSetId(Integer.parseInt(ext1));//12-JAN-15
            data.setMeasurementTime(measurementTime);
            data.setModificationDate(modDate);//== null) ? 0 : modDate);
            data.setUuid(uuid);

            long mDate = data.getMeasurementTime() == null ? 0 : data.getMeasurementTime();
            if (mDate < 86400000) {
                data.setMeasurementTime(data.getCreationDate() == null ? 0 : data.getCreationDate());
            }

            data.setViolationFlag(violationFlag);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "cursorToDFieldData() exception:" + e.getLocalizedMessage());
        }

        return data;
    }

    public EventFieldData getDEvent(int eventID) {
        String query = "SELECT E.SiteID, SiteName, E.MobileAppID, MobileAppName, E.UserID, " +
                "UserName, E.EventDate, E.Latitude, E.Longitude, E.DeviceID from d_Event as E " +
                "JOIN s_Site as S on E.SiteID=S.SiteID " +
                "JOIN s_MobileApp as M on E.MobileAppID=M.MobileAppID JOIN s_User as U on E.UserID=U.UserID " +
                "where eventID=?";
        String[] whereClause = new String[]{"" + eventID};
        EventFieldData devent = new EventFieldData();
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, whereClause);
            if (cursor == null) {
                System.out.println("No data for getDEvent");
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                devent = new EventFieldData();
                devent.setEventId(eventID);
                devent.setActive(true);
                devent.setSiteId(cursor.getInt(0));
                devent.setSiteName(cursor.getString(1));
                devent.setMobileAppId(cursor.getInt(2));
                devent.setMobileAppName(cursor.getString(3));
                devent.setUserId(cursor.getInt(4));
                devent.setUserName(cursor.getString(5));
                devent.setEventDate(cursor.getLong(6));
                devent.setLatitude(cursor.getDouble(7));
                devent.setLongitude(cursor.getDouble(8));
                devent.setDeviceId(cursor.getString(9));
                // TODO: 05-Dec-15
//                devent.setEventModificationDate((long) 0);
//                devent.setEventCreationDate((long) 0);
//                devent.setNotes("");
                cursor.moveToNext();
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
            Log.e(TAG, "getDEvent Error:" + ignored.getMessage());
        }

        if (cursor != null) {
            cursor.close();
        }

        if (devent != null) {
            devent.setEventDate(System.currentTimeMillis());
        }

        return devent;
    }

    public List<FieldDataSyncStaging> collectDataToReSyncAll() {

//        List<EventFieldData> eventList = new ArrayList<EventFieldData>();
        List<FieldDataSyncStaging> fieldDataList = null;
//        EventFieldData dEvent = null;

        try {
            fieldDataList = getFieldDataToReSync();
        } catch (Exception e) {
            Log.e(TAG, "collect Data For Upload All error msg=" + e.getLocalizedMessage());
            TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);

            LogDetails logDetails = new LogDetails();
            logDetails.setAllIds("");
            logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
            logDetails.setScreenName("All un-synced data query");
            logDetails.setDetails("Fetch all unsynced data query exception: "
                    + e);

            tempLogsDataSource.insertTempLogs(logDetails);
        }

        return fieldDataList;
    }

    //added on 10 May, 2022
    public List<FieldDataSyncStaging> getFieldDataToReSync() {
        List<FieldDataSyncStaging> dfieldData = new ArrayList<FieldDataSyncStaging>();
        FieldDataSyncStaging data = null;

        String query = "select F.LocationID, F.FieldParameterID, F.MeasurementTime, " +
                "F.StringValue, F.Units, F.Latitude, F.Longitude, F.Notes, F.CreationDate, F.ExtField1, " +
                "F.FieldParameterLabel, F.FieldDataID, " +
                "F.CorrectedLatitude, F.CorrectedLongitude, F.ExtField2, " +
                "F.ExtField3, F.ExtField4, F.SiteID, F.UserID, " +
                "F.MobileAppID, F.ModificationDate,F.deviceId,F.modifiedByDeviceId, " +
                "F.modifiedBy,F.EventID, F.violationFlag,F.fieldUUID from d_FieldData as F " +
                "where ((F.ModificationDate is null and F.StringValue is not null) or " +
                "(F.ModificationDate is not null and F.StringValue is not null)) and " +
                "F.CreationDate IS NOT NULL " +
                "and F.StringValue not like '' and (F.EventID > 0  AND F.LocationID > 0)";
        // whereClause = new String[]{"" + eventID};
        Cursor cursor = null;

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);
        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));

        try {
//			System.out.println("cccc"+"Before query getFieldDataListForEvent");
            cursor = database.rawQuery(query, null);
//			System.out.println("cccc"+"After query getFieldDataListForEvent");
            if (cursor == null) {
                return null;
            }

            logDetails.setScreenName("All un-synced data query");

            boolean cursorHasData = !cursor.isClosed();
            boolean databaseConnection = database != null && database.isOpen();

            logDetails.setDetails("Checking cursor and database instance: Database: "
                    + databaseConnection + " Cursor: " + cursorHasData);

            tempLogsDataSource.insertTempLogs(logDetails);

            if (cursor.moveToFirst()) {
                do {
                    data = cursorToDFieldDataSyncAll(cursor);//04-Dec-15 added
                    dfieldData.add(data);
                    Log.i(TAG, "Event=" + data.getEventId() + " Field Label=" + data.getFieldParameterLabel() + " FpID=" + data.getFieldParameterId() + " String Value=" + data.getStringValue());
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldDataListForEvent() exception:" + e.getLocalizedMessage());

            logDetails.setScreenName("Inside get all unsynced data query");
            logDetails.setDetails("Inside Fetch all unsynced data query exception: "
                    + e);

            tempLogsDataSource.insertTempLogs(logDetails);

            return dfieldData;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dfieldData;
    }

    public HashMap<String, List<FieldDataSyncStaging>> collectDataForSyncUpload() {

        HashMap<String, List<FieldDataSyncStaging>> mapFieldData = new HashMap<>();
        List<FieldDataSyncStaging> fieldDataListInsert = null;
        List<FieldDataSyncStaging> fieldDataListUpdate = null;

        try {
            fieldDataListInsert = getFieldDataListForUploadAll();
            fieldDataListUpdate = getFieldDataListForUpdate();

            if (!fieldDataListInsert.isEmpty())
                mapFieldData.put(GlobalStrings.FIELD_DATA_INSERT, fieldDataListInsert);

            if (!fieldDataListUpdate.isEmpty())
                mapFieldData.put(GlobalStrings.FIELD_DATA_UPDATE, fieldDataListUpdate);
        } catch (Exception e) {
            Log.e(TAG, "collect Data For Upload All error msg=" + e.getLocalizedMessage());
            TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);

            LogDetails logDetails = new LogDetails();
            logDetails.setAllIds("");
            logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
            logDetails.setScreenName("All un-synced data query");
            logDetails.setDetails("Fetch all unsynced data query exception: "
                    + e);

            tempLogsDataSource.insertTempLogs(logDetails);
        }

        return mapFieldData;
    }

/*
    public void setDataSyncFlag(AttachmentDataSource.SyncType type, List<FieldData> dataList) {
        int ret;
        String whereClause = null;
        String[] whereArgs = null;

        if (dataList != null) {
            ContentValues values = new ContentValues();
            if (type == AttachmentDataSource.SyncType.data) {
                values.put(KEY_DataSyncFlag, "1");
            } else if (type == AttachmentDataSource.SyncType.email) {
                values.put(KEY_EmailSentFlag, "1");
            }
            whereClause = "FieldDataID=?";
            for (int i = 0; i < dataList.size(); i++) {
                whereArgs = new String[]{"" + dataList.get(i).getFieldDataID()};
                try {
                    ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
                } catch (Exception e) {

                }
            }
        }
    }
*/

    public void setDataSyncFlagDFieldData(AttachmentDataSource.SyncType type, List<Integer> dataIDSet) {
        int ret;
        String whereClause = null;
        String[] whereArgs = null;
        String query = null;
        Cursor cursor = null;

        if (dataIDSet != null) {
            ContentValues values = new ContentValues();
            if (type == AttachmentDataSource.SyncType.data) {
                query = "update d_FieldData set DataSyncFlag = '1', ModificationDate = CreationDate," +
                        " oldStringValue=StringValue, oldNote = Notes " +
                        " where FieldDataID = ?";
                for (int i = 0; i < dataIDSet.size(); i++) {
                    whereArgs = new String[]{"" + dataIDSet.get(i)};
                    try {
                        database.execSQL(query, whereArgs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//				values.put(KEY_DataSyncFlag, "1");

            } else if (type == AttachmentDataSource.SyncType.email) {
                values.put(KEY_EmailSentFlag, "1");
                whereClause = "FieldDataID=?";
                for (int i = 0; i < dataIDSet.size(); i++) {
                    whereArgs = new String[]{"" + dataIDSet.get(i)};
                    try {
                        ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);

                    } catch (Exception e) {
                        String mesg = e.getLocalizedMessage() + e.getMessage();
                        System.out.println("mesg" + mesg);
                    }
                }
            }
        }
    }


    public String getParamLabelNotes(int fieldParameterID, int setID,
                                     int appID, String locID, int siteID) {
        // TODO Auto-generated method stub
        String query = null;
//		String whereClause = null;
        String[] whereArgs = null;
        query = "select F.Notes FROM d_FieldData as F JOIN d_Event as E on F.EventID=E.EventID " +
                " where " + KEY_ExtField1 + "=?" + " and F." + KEY_LocationID + "=?" + " and " + " F.SiteID=? " + "and " + "F.MobileAppID=? " +
                " and " + KEY_FieldParameterID + "=?";

        whereArgs = new String[]{"" + setID, "" + locID, "" + siteID, "" + appID, "" + fieldParameterID};
        System.out.println("qqqq" + "query=" + query);
//		System.out.println("qqqq args"+"setID="+setID+ "locid="+locID+"siteID="+siteID+"mapp="+appID+"fid="+fieldParameterID);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getParamLabelNotes() error:" + e.getLocalizedMessage());
        }
        String value = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    value = cursor.getString(0);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        return value;
    }

    public Long getModificationDate(int fieldParameterID, int setID,
                                    int appID, String locID, int siteID) {
        String query = null;
        Long value = null;

//		String whereClause = null;
        String[] whereArgs = null;
        query = "select F.ModificationDate FROM d_FieldData as F JOIN d_Event as E on F.EventID = E.EventID" +
                " where " + KEY_ExtField1 + "=?" + " and F." + KEY_LocationID + "=?" + " and " + " F.SiteID=? " + "and " + "F.MobileAppID=? " +
                " and " + KEY_FieldParameterID + "=?";

        whereArgs = new String[]{"" + setID, "" + locID, "" + siteID, "" + appID, "" + fieldParameterID};
        Log.i(TAG, "getModificationDate() " + "query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    value = cursor.getLong(0);
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getModificationDate Error:" + e.getLocalizedMessage());
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

        return value;
    }

    public int updateNotesForLabel(String eventID, int paramId, int curSetID, String locationID,
                                   String notesValue, int siteID, int currentAppID) {
        String nullValue = null;
        int ret = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_MeasurementTime, System.currentTimeMillis());
        values.put(KEY_Notes, notesValue);
        // values.put(KEY_StringValue,notesValue);
        values.put(KEY_EmailSentFlag, nullValue);

        String flag = getDataSyncFlagIfUpdate(eventID, curSetID, locationID,
                siteID + "", currentAppID, paramId);
        values.put(KEY_DataSyncFlag, flag);

        values.put(KEY_ModificationDate, System.currentTimeMillis());

        String whereClause = "FieldParameterID=? AND ExtField1=? AND LocationID=? and " +
                "EventID =? and SiteID=? and MobileAppID=?";
        String[] whereArgs = new String[]{"" + paramId, "" + curSetID, "" + locationID, ""
                + eventID, "" + siteID, "" + currentAppID};

        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateNotesForLabel() Note Updation Error:" + e.getMessage());
        }
        Log.i(TAG, "Ret value for updateValue = " + ret);
        return ret;
    }

    public int updateViolationFlag(String eventID, int paramId, int curSetID, String locationID,
                                   String violationFlag, int siteID, int currentAppID) {
        String nullValue = null;
        int ret = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_MeasurementTime, System.currentTimeMillis());
        values.put(KEY_ViolationFlag, violationFlag);

        String flag = getDataSyncFlagIfUpdate(eventID, curSetID, locationID,
                siteID + "", currentAppID, paramId);
        values.put(KEY_DataSyncFlag, flag);

        values.put(KEY_ModificationDate, System.currentTimeMillis());

        String whereClause = "FieldParameterID=? AND ExtField1=? AND LocationID=? and " +
                "EventID =? and SiteID=? and MobileAppID=?";
        String[] whereArgs = new String[]{"" + paramId, "" + curSetID, "" + locationID, ""
                + eventID, "" + siteID, "" + currentAppID};

        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "Ret value for updateViolationFlag = " + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateViolationFlag() Note Updating Error:" + e.getMessage());
        }
        return ret;
    }

    public List<String> getChildAppIdListHavingData(String locationID, String eventID) {

        String query = "select distinct MobileAppID from d_FieldData where LocationID = ? and EventID = ? ";

        String[] whereArgs = new String[]{locationID, eventID};
        List<String> mobAppIDs = new ArrayList<String>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    mobAppIDs.add(cursor.getString(0));

                    Log.i(TAG, "getChildAppIdListHavingData mobAppIDs " + cursor.getString(0));

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getChildAppIdListHavingData() error: " + e.getLocalizedMessage());
        }
        return mobAppIDs;
    }


    public int deleteset(String locationID, int eventID, int AppID, int curSetID, int siteid) {
        int ret = 0;
        String whereClause = "EventID =? and LocationID=? and SiteID=? and MobileAppID=? and CAST(ExtField1 AS INTEGER)=?";
        String[] whereArgs = new String[]{"" + eventID, locationID + "", siteid + "", AppID + "", curSetID + ""};
        try {
            ret = database.delete(DbAccess.TABLE_FIELD_DATA, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int deleteformFieldDataFromAllTables(String locationID, int eventID, int AppID, int siteid) {
        int ret = 0;
        String whereClause = "EventID =? and LocationID=? and SiteID=? and MobileAppID=?";
        String[] whereArgs = new String[]{"" + eventID, locationID + "", siteid + "", AppID + ""};
        try {
            ret = database.delete(DbAccess.TABLE_FIELD_DATA, whereClause, whereArgs);
            Log.i(TAG, "deleteformFieldDataFromAllTables() Fielddata:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            whereClause = "EventID =? and LocationID=? and SiteID=? and MobileAppID=?";
            whereArgs = new String[]{"" + eventID, locationID + "", siteid + "", AppID + ""};
            ret = database.delete(DbAccess.TABLE_ATTACHMENT, whereClause, whereArgs);
            Log.i(TAG, "deleteformFieldDataFromAllTables() Attachment:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            whereClause = "EventID =? and LocationID=? and SiteID=? and MobAppID=?";
            whereArgs = new String[]{"" + eventID, locationID + "", siteid + "", AppID + ""};

            ret = database.delete(DbAccess.TABLE_D_SAMPLE_MAPTAG, whereClause, whereArgs);
            Log.i(TAG, "deleteformFieldDataFromAllTables() SamapleMapTag:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return ret;
    }

    public int deleteFieldDataforEvent(int eventID) {
        int ret = 0;
        String whereClause = "EventID =?";
        String[] whereArgs = new String[]{"" + eventID};
        try {
            ret = database.delete(DbAccess.TABLE_FIELD_DATA, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    public boolean updateSyncFlagFor_AlreadySyncedData() {
        int ret = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_DataSyncFlag, "1");
            String whereClause = "IFNULL(oldStringValue,'')=IFNULL(StringValue,'') and IFNULL(oldNote,'')=IFNULL(Notes,'')";
            String[] whereArgs = null;
            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            Log.i(TAG, "Updated Records Count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "UpdateSyncFlagFor_AlreadySyncedData() failed to update already synced data flag to 'synced'");

            TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);

            LogDetails logDetails = new LogDetails();
            logDetails.setAllIds("");
            logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
            logDetails.setScreenName("updateSyncFlagFor_AlreadySyncedData() query exception");
            logDetails.setDetails("Exception: " + e);

            tempLogsDataSource.insertTempLogs(logDetails);

            return false;
        }
        return true;
    }

    public int deleteFieldData() {
        int ret = 0;
        String whereClause = null;
        String[] whereArgs = null;
        try {
            ret = database.delete(DbAccess.TABLE_FIELD_DATA, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DeleteFieldData", "Error:" + e.getLocalizedMessage());
        }
        Log.i("DeleteFieldData", "Ret:" + ret);
        return ret;
    }

    public int deleteFieldDataByEventID(String eventID) {
        int ret = 0;
        String whereClause = KEY_EventID + "=?";
        String[] whereArgs = {eventID};
        try {
            ret = database.delete(DbAccess.TABLE_FIELD_DATA, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DeleteFieldData", "Error:" + e.getLocalizedMessage());
        }
        Log.i("DeleteFieldData", "Ret:" + ret);
        return ret;
    }

    public int updateDepth(int eventID, int curSetID, int locationID,
                           int siteID, int currentAppID, Double depth) {
        ContentValues values = new ContentValues();
        int ret = 0;

        values.put(KEY_ExtField4, depth);
        String whereClause = "LocationID=? and ExtField1=? and EventID =? and SiteID=? and MobileAppID=?";
        String[] whereArgs = new String[]{"" + locationID, "" + curSetID, "" + eventID, "" + siteID, "" + currentAppID};

        try {

            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, whereClause, whereArgs);
            System.out.println("Ret value for updateValue = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public int updateFieldDataDeviceID(String NewDeviceID) {
        ContentValues values = new ContentValues();
        int ret = 0;

        values.put(KEY_DeviceID, NewDeviceID);
//        String whereClause =null;
//        String[] whereArgs = new String[]{"" + locationID, "" + curSetID, "" + eventID, "" + siteID, "" + currentAppID};

        try {

            ret = database.update(DbAccess.TABLE_FIELD_DATA, values, null, null);
            Log.i(TAG, "updateDeviceID()  Ret value for updateDeviceID = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "updateDeviceID()  Ret value for updateDeviceID Error= " + e.getMessage());

        }

        return ret;
    }

    public String fieldDataForDepth(int eventID, int userID, int locID,
                                    Double level, String[] fieldParamId) {
        Cursor c = null;
        String fieldData = "";
        String sql = "select StringValue from d_FieldData where EventID = ? and UserID = ? " +
                "and LocationID = ? and ExtField4 = ? and FieldParameterID = ?";
        String[] whereArgs = null;
        try {
            for (int i = 0; i < fieldParamId.length; i++) {
                whereArgs = new String[]{"" + eventID, "" + userID, "" + locID, "" + level, "" + fieldParamId[i]};
                try {
                    c = database.rawQuery(sql, whereArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (c != null) {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        String field = c.getString(0);
                        if (field != null) {
                            if (i == 0) {
                                fieldData = field;
                            } else {
                                fieldData = fieldData + ", " + field;
                            }
                        }
                        break;
                    }
                    c.close();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("field " + fieldData);
        return fieldData;
    }

    public boolean isExistsHeaderData(String locID, int eventID, int userID,
                                      int id, int siteID) {
        Cursor c = null;
        String sql = "select CreationDate from d_FieldData where LocationID = ? " +
                "and EventID = ? and UserID = ? and MobileAppID = ? and SiteID = ?";
        String[] whereArgs = new String[]{"" + locID, "" + eventID, "" + userID, "" + id, "" + siteID};
        try {
            c = database.rawQuery(sql, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long count = 0;
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                count = c.getLong(0);
                if (count != 0) {
                    return true;
                }
                c.moveToNext();
            }
            c.close();
        }

        return false;
    }

    public String getStringValueFromId(int eventID, String locID, int appID, int setID, String fpId) {
        String sql = "select StringValue from d_FieldData where EventID = ? and LocationID = ? " +
                "and MobileAppID = ? and ExtField1 = ? and FieldParameterID = ?";
        String[] whereArgs = new String[]{"" + eventID, "" + locID, "" + appID, "" + setID, "" + fpId};
        Cursor cursor = null;
        String value = null;
        try {
            cursor = database.rawQuery(sql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    value = cursor.getString(0);
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getStringValueFromId() error:" + e.getMessage());
        }

        return value;
    }

    public ArrayList<String> getStringValueForGPS(int eventID, String parentID) {
        String sql = "select StringValue,ExtField1,LocationID from d_FieldData where EventID = ?  and FieldParameterID IN " +
                "(SELECT FieldParameterID from s_MetaData where FieldInputType='GPS' and MobileAppID=?)";
        String[] whereArgs = new String[]{"" + eventID, "" + parentID};
        Cursor cursor = null;
        ArrayList<String> gps_valueList = new ArrayList<>();
        try {
            cursor = database.rawQuery(sql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String latLong = cursor.getString(0);
                    String ext1 = cursor.getString(1);
                    String locationID = cursor.getString(2);
                    Log.i(TAG, "getStringValueForGPS()  Gps Point for event(" + eventID + ") : locationID=" + locationID + " ,LatLong=" + latLong + ",Set=" + ext1);
                    gps_valueList.add(latLong);
                } while (cursor.moveToNext());

                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getStringValueFromId() error:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return gps_valueList;
    }

    public String getMeasurmentTimeFromId(int eventID, String locID, int appID, int setID, String op) {
        String sql = "select MeasurementTime from d_FieldData where EventID = ? and LocationID = ? and MobileAppID = ? and ExtField1 = ? and FieldParameterID = ?";
        String[] whereArgs = new String[]{"" + eventID, "" + locID, "" + appID, "" + setID, "" + op};
        Cursor cursor = null;
        String value = null;
        try {
            cursor = database.rawQuery(sql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                value = cursor.getString(0);
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public Long getMeasurementTime(int eventID, String locID, String appID, int setID, String fpId, int siteId) {
        String sql = "select MeasurementTime from d_FieldData where EventID = ? and " +
                "LocationID = ? and MobileAppID = ? and ExtField1 = ? and FieldParameterID = ? and SiteID = ?";
        String[] whereArgs = new String[]{"" + eventID, "" + locID, appID, "" + setID, "" + fpId, siteId + ""};

        Cursor cursor = null;
        Long value = null;
        try {
            cursor = database.rawQuery(sql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                value = cursor.getLong(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public Long getMeasurementTimeByExt2n3(int eventID, String locID, String appID,
                                           int setID, String fpId, int siteId) {
        String sql = "select ExtField2, ExtField3 from d_FieldData where EventID = ? and " +
                "LocationID = ? and MobileAppID = ? and ExtField1 = ? and FieldParameterID = ? and SiteID = ?";
        String[] whereArgs = new String[]{"" + eventID, "" + locID, appID, "" + setID, "" + fpId, siteId + ""};

        Cursor cursor = null;
        Long value = null;
        try {
            cursor = database.rawQuery(sql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                String date = cursor.getString(0);
                String time = cursor.getString(1);

                value = Util.getMilliseconds(date + " " + time, GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public int getChildAppStatus(int MobAppID, String locationID) {
        String query = "select StatusID from LocFormStatus where MobileAppID = ? and LocationID =?";

        String[] whereArgs = new String[]{"" + MobAppID, "" + locationID};

        Cursor cursor = null;
        try {

            cursor = database.rawQuery(query, whereArgs);
            int count = cursor.getCount();
            if (count > 0) {
                if (cursor.moveToLast()) {
                    int status = cursor.getInt(0);
                    Log.i("getChildAppStatus", "MobAppID:" +
                            MobAppID + " And LocationID:" + locationID + " And Status :" + status);
                    cursor.close();
                    return status;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getChildAppStatus Error: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return -1;
    }

    public boolean isLocationInStatus(String locationID) {

        String query = "select StatusID from LocFormStatus where LocationID =?";

        String[] whereArgs = new String[]{"" + locationID};

        Cursor cursor = null;
        try {

            cursor = database.rawQuery(query, whereArgs);
            int count = cursor.getCount();
            if (count > 0) {
                if (cursor.moveToLast()) {
                    int status = cursor.getInt(0);
                    Log.i("isLocationInStatus", "LocationID:" + locationID + " And Status :" + status);
                    cursor.close();
                    return true;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getChildAppStatus Error: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return false;
    }

    public ArrayList<LocFormStatus> collectChildAppStatus(int eventID) {
        ArrayList<LocFormStatus> sdata = new ArrayList<LocFormStatus>();

        String query = "Select a.LocationID, a.MobileAppID, count(distinct FieldParameterID) status_count " +
                " from d_FieldData a Where EventID = " + eventID +
                " and (StringValue not null and StringValue !='') group by a.LocationID, a.MobileAppID having a.ExtField1 = Min(a.ExtField1)";

        // 7/10/2018 UPDATED QUERY FOR LOCATION STATUS
        query = "Select a.LocationID, a.MobileAppID, count(distinct a.FieldParameterID) status_count \n" +
                " from d_FieldData a left outer join s_Default_Values b\n" +
                " on a.LocationID = b.LocationID\n" +
                " and a.FieldParameterID = b.FieldParameterID\n" +
                " Where EventID =" + eventID +
                " and a.FieldParameterID not in (15,25)\n" +
                " and (StringValue not null and StringValue !='')\n" +
                " and b.LocationID is null                \n" +
                "group by a.LocationID, a.MobileAppID having a.ExtField1 = Min(a.ExtField1)";

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            int count = cursor.getCount();
            int status = 1;//grey color
            if (count > 0) {
                LocFormStatus lform;
                cursor.moveToFirst();
                do {
                    lform = new LocFormStatus();
                    int percentage = cursor.getInt(2);
                    if (percentage == 1 || percentage == 2) {
                        status = 0;//Red color
                    } else if (percentage > 2) {
                        status = 2;//Green color
                    } else {
                        status = 1;
                    }
//                    cursor.getInt(2)>50?2:1;
                    int mobID = cursor.getInt(1);
                    String Location = cursor.getString(0);
                    lform.LocationID = Location;
                    lform.MobileAppID = mobID;
                    lform.StatusID = status;
                    Log.i("getChildAppStatus", "MobAppID:" + mobID + " And LocationID:" + Location + " And Status :" + status);
                    sdata.add(lform);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getChildAppStatus Error: " + e.getMessage());
        } finally {
            if (cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return sdata;
    }

    public ArrayList<LocFormStatus> collectChildAppStatusByRollApp(int eventID, int siteID, int roll_into_app) {

        ArrayList<LocFormStatus> sdata = new ArrayList<LocFormStatus>();

        String query =
//                "Select a.LocationID,  a.MobileAppID, count(distinct FieldParameterID) status_count " +
//                " from d_FieldData a Where EventID = " + eventID +
//                " and (StringValue not null and StringValue !='') group by a.LocationID, a.MobileAppID having a.ExtField1 = Min(a.ExtField1)";
                "select a.LocationID ,cast( a.dcount as float)*1.0/cast(tcount as float)*100\n" +
                        "        from\n" +
                        "                (\n" +
                        "                        select a.LocationID, count(FieldParameterID) dcount\n" +
                        "                        from d_FieldData a,s_SiteMobileApp b,d_Event c\n" +
                        "                        where a.MobileAppID=b.MobileAppID AND b.roll_into_app_id=c.MobileAppID and b.SiteID=" + siteID + " and  a.EventID=" + eventID + " and a.FieldParameterID NOT IN (15,25)\n" +
                        "                        group by a.EventID, a.LocationID) a,\n" +
                        "                (\n" +
                        "                        select count(FieldParameterID) tcount\n" +
                        "        from s_MetaData a,s_SiteMobileApp b where a.MobileAppID=b.MobileAppID AND b.roll_into_app_id=" + roll_into_app + "and b.SiteID=" + siteID + " and a.FieldParameterID NOT IN (15,25) ) b\n" +
                        "        where 1";


        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            int count = cursor.getCount();
            int status = 1;//grey color
            if (count > 0) {
                LocFormStatus lform;
                cursor.moveToFirst();
                do {
                    lform = new LocFormStatus();
                    int percentage = cursor.getInt(2);
                    if (percentage == 1 || percentage == 2) {
                        status = 0;//Red color
                    } else if (percentage > 2) {
                        status = 2;//Green color
                    } else {
                        status = 1;
                    }
//                    cursor.getInt(2)>50?2:1;
                    int mobID = cursor.getInt(1);
                    String Location = cursor.getString(0);
                    lform.LocationID = Location;
                    lform.MobileAppID = mobID;
                    lform.StatusID = status;
                    Log.i("getChildAppStatus", "MobAppID:" + mobID + " And LocationID:" + Location + " And Status :" + status);
                    sdata.add(lform);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("getChildAppStatus Error: " + e.getMessage());
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return sdata;
    }

    public void insertChildAppStatus(int eventID) {
        ArrayList<LocFormStatus> dataList = collectChildAppStatus(eventID);
        if (dataList.size() > 0) {
            long ret = 0;
            database.beginTransaction();
            LocFormStatus statusData;

            try {
                for (int i = 0; i < dataList.size(); i++) {
                    ContentValues values = new ContentValues();
                    statusData = dataList.get(i);

                    values.put(KEY_MobileAppID, statusData.MobileAppID);
                    values.put(KEY_LocationID, statusData.LocationID);
                    values.put(KEY_StatusID, statusData.StatusID);

                    ret = database.insert(DbAccess.TABLE_LOC_FORM_STATUS, null, values);
                    Log.i("insertChildAppStatus", "Inserted Ret:" + ret);
                }

                database.setTransactionSuccessful();

            } catch (Exception e) {
                Log.e(TAG, "insertChildAppStatus() error:" + e.getLocalizedMessage() + ret);
            } finally {
                database.endTransaction();
            }

        } else {
            Log.i("insertChildAppStatus", "No data to insert in LocFormStatus");
            return;
        }
    }


    public int updateChildAppStatus(int MobAppID, int locationID, int status) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        int ret = 0;
        values.put(KEY_StatusID, status);
        String whereClause = "LocationID=? and MobileAppID=?";
        String[] whereArgs = new String[]{"" + locationID, "" + MobAppID};
        try {
            ret = database.update(DbAccess.TABLE_LOC_FORM_STATUS, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Ret value for update LocFormStatus  = " + ret);
        return ret;
    }

    public int truncateLocFormStatus() {

        int ret = 0;
        try {
            ret = database.delete(DbAccess.TABLE_LOC_FORM_STATUS, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Truncate LocFormStatus  Error= " + e.getMessage());

        }

        Log.i(TAG, "Ret value for Truncate LocFormStatus  = " + ret);

        return ret;
    }


    public ArrayList<HashMap<String, List<ReportTable>>> getReportData(String siteID, String eventID) {
        ArrayList<HashMap<String, List<ReportTable>>> multiple_location_data_list
                = new ArrayList<HashMap<String, List<ReportTable>>>();
        ArrayList<String> locationList = getLocationList_d_fielddata(eventID);
        HashMap<String, List<ReportTable>> loc_data;

        for (String locationID : locationList) {
            String locName = new LocationDataSource(mContext).getLocationName(locationID);
            if (locName != null) {
                ArrayList<ReportTable> d_list = getLocationWiseReportTables(eventID, locationID, siteID);

                loc_data = new HashMap<String, List<ReportTable>>();
                loc_data.put(locName, d_list);
                multiple_location_data_list.add(loc_data);
            } else
                Log.i(TAG, "No Location Name for id:" + locationID + " so skipped.");
        }

        return multiple_location_data_list;
    }

    private ArrayList<String> getLocationList_d_fielddata(String eventID) {

        ArrayList<String> locationList = new ArrayList<>();

        String sql = "select Distinct LocationID from d_FieldData where EventID = ? ";
        String[] whereArgs = new String[]{"" + eventID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String locationID = cursor.getString(0);
                    locationList.add(locationID);
                } while (cursor.moveToNext());

                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLocationList_d_fielddata() error:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return locationList;
    }

    private ArrayList<ReportTable> getLocationWiseReportTables(String eventID, String locationID, String siteID) {
        ArrayList<ReportTable> locationReportList = new ArrayList<>();
        ReportTable rtable;
        ArrayList<String> mobileAppList = (ArrayList<String>) getChildAppIdListHavingData(locationID, eventID);
        SiteMobileAppDataSource smds = new SiteMobileAppDataSource(mContext);

        for (String mobID : mobileAppList) {
            boolean isAllowMultipleSet = isAllowMultipleSetEnabled(mobID);
            String mobapp_name = smds.getMobileAppDisplayName(Integer.parseInt(mobID), Integer.parseInt(siteID));
            HashMap<String, ArrayList<String>> mapFieldsParams = getMetaDataForChildForm(mobID);
            ArrayList<String> fieldParamList = mapFieldsParams.get(mobID);
            ArrayList<List<HashMap<String, String>>> data_rows
                    = getReportDataList_mobileApp(eventID, locationID, mobID, siteID, mapFieldsParams);

            rtable = new ReportTable(isAllowMultipleSet, mobapp_name, fieldParamList, data_rows);
            locationReportList.add(rtable);
        }

        return locationReportList;
    }


    public ArrayList<List<HashMap<String, String>>> getReportDataList_mobileApp(String eventId,
                                                                                String locationId,
                                                                                String mobileAppId,
                                                                                String siteID,
                                                                                HashMap<String,
                                                                                        ArrayList<String>> mapFieldsParams) {
        ArrayList<List<HashMap<String, String>>> rList = new ArrayList<>();

        List<HashMap<String, String>> param_value_List = new ArrayList<>();
        int maxSet = getmaxSetID_MobileApp(locationId, eventId, mobileAppId);

        for (int set = 1; set <= maxSet; set++) {
            param_value_List = getListOfStringValueForChildApp(set + "", mobileAppId,
                    eventId, locationId, siteID, mapFieldsParams);
            rList.add(param_value_List);
        }
        return rList;
    }


    private List<HashMap<String, String>> getListOfStringValueForChildApp(String setID,
                                                                          String mobileAppId,
                                                                          String eventID,
                                                                          String locationID,
                                                                          String siteID,
                                                                          HashMap<String, ArrayList<String>> mapFieldsParams) {
        List<HashMap<String, String>> paramList = new ArrayList<>();
        HashMap<String, String> item;

        String selectQuery =
                "Select FieldParameterLabel,IFNULL(StringValue,'') " +
                        "from d_FieldData where " +
                        "ExtField1=" + setID + " and" +
                        " MobileAppID=" + mobileAppId + " and EventID=" + eventID + " and LocationID=" + locationID;

        selectQuery = "select DISTINCT M.FieldParameterID, M.ParameterLabel, IFNULL(F.StringValue,'')" +
                "FROM d_FieldData as F JOIN d_Event as E on F.EventID = E.EventID " +
                "JOIN s_MetaData as M on M.FieldParameterID = F.FieldParameterID " +
                "where F.ExtField1=? and F.LocationID=? and F.SiteID=? and F.MobileAppID=? " +
                "and F.EventID =?";
        String[] whereArgs = new String[]{"" + setID, locationID, siteID + "", mobileAppId, eventID + ""};

        // SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, whereArgs);
        Log.i(TAG, "getListOfStringValueForChildApp() query:" + selectQuery);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                item = new HashMap<>();
                String label = cursor.getString(1);
                String value = cursor.getString(2);

                if (mapFieldsParams.containsKey(label)) {
                    item.put(label, value);
                    paramList.add(item);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return paramList;
    }

    public ArrayList<HashMap<String, List<ReportTable>>> getReportDataForForm(String siteID,
                                                                              String eventID,
                                                                              String locationID,
                                                                              String mobAppId) {
        ArrayList<HashMap<String, List<ReportTable>>> multiple_location_data_list
                = new ArrayList<>();
        HashMap<String, List<ReportTable>> loc_data;

        String locName = new LocationDataSource(mContext).getLocationName(locationID);
        if (locName != null) {
            ArrayList<ReportTable> d_list = getLocationReportTableForForm(eventID, locationID, siteID, mobAppId);

            loc_data = new HashMap<>();
            loc_data.put(locName, d_list);
            multiple_location_data_list.add(loc_data);
        } else
            Log.i(TAG, "No Location Name for id:" + locationID + " so skipped.");

        return multiple_location_data_list;
    }

    private ArrayList<ReportTable> getLocationReportTableForForm(String eventID, String locationID,
                                                                 String siteID, String mobAppId) {
        ArrayList<ReportTable> locationReportList = new ArrayList<>();
        ReportTable rtable;
        ArrayList<String> mobileAppList = (ArrayList<String>) getChildAppIdListHavingData(locationID, eventID);
        SiteMobileAppDataSource smds = new SiteMobileAppDataSource(mContext);

        boolean isAllowMultipleSet = isAllowMultipleSetEnabled(mobAppId);
        String mobapp_name = smds.getMobileAppDisplayName(Integer.parseInt(mobAppId), Integer.parseInt(siteID));
        HashMap<String, ArrayList<String>> mapFieldsParams = getMetaDataForChildForm(mobAppId);
        ArrayList<String> fieldParamList = mapFieldsParams.get(mobAppId);
        ArrayList<List<HashMap<String, String>>> data_rows
                = getReportDataList_mobileApp(eventID, locationID, mobAppId, siteID, mapFieldsParams);

        rtable = new ReportTable(isAllowMultipleSet, mobapp_name, fieldParamList, data_rows);
        rtable.setMapFieldsParams(mapFieldsParams);

        locationReportList.add(rtable);

        return locationReportList;
    }

    private HashMap<String, ArrayList<String>> getMetaDataForChildForm(String mobileAppId) {
        HashMap<String, ArrayList<String>> mapLabels = new HashMap<>();
        ArrayList<String> labelList = new ArrayList<>();

        String selectQuery = "SELECT Distinct ParameterLabel, FieldParameterID FROM "
                + DbAccess.TABLE_META_DATA + " where MobileAppID =" + mobileAppId
                + " AND FieldInputType NOT LIKE '' AND UPPER(ParameterLabel) NOT LIKE 'NOTES'";

        // SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String label = cursor.getString(0);
                labelList.add(label);

                String fpId = cursor.getString(1);
                ArrayList<String> listId = new ArrayList<String>();//this list will always have one
                //element which is fieldParamId of the field used to keep the fieldId for click purpose in html
                listId.add(fpId);

                mapLabels.put(label, listId); //we need labels by mob app id also and we need label names also
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (mapLabels.size() > 0) {
            mapLabels.put(mobileAppId, labelList);
        }
        return mapLabels;
    }

    private boolean isAllowMultipleSetEnabled(String mobileAppId) {

        boolean result = false;
        String selectQuery = "select allow_multiple_sets from s_MobileApp where MobileAppID=" + mobileAppId;

        // SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            int allowMultiSet = cursor.getInt(0);
            result = allowMultiSet > 0;
            cursor.close();
        }
        return result;
    }

    //8 Apr, 20
    public boolean isValidToCreateNewSet(String mobileAppId, String locationId, String
            siteId, String extField1) {

        boolean result = false;
        String selectQuery = "select count(" + KEY_StringValue + ") from " + DbAccess.TABLE_FIELD_DATA
                + " where fieldParameterId <> 25 and fieldParameterId <> 15 " +
                "and StringValue IS NOT NULL and StringValue != '' and LocationID= ? and SiteID = ? " +
                " and MobileAppID = ? and ExtField1 = ?";

        String[] whereArgs = {locationId, siteId, mobileAppId, extField1};

        // SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, whereArgs);

        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            result = count > 0;
            cursor.close();
        }
        return result;
    }

    public ArrayList<ChangeEventModel> getEvents(String siteId, String eventStatus, String
            eventId) {

        ArrayList<ChangeEventModel> eventList = new ArrayList<>();
/*        String selectQuery = "select distinct a.EventID, ifnull(e.Date, a.EventDate) as date, " +
                "(case when (a.EventName is '' or a.EventName is null) then " +
                "c.display_name_roll_into_app else a.EventName end) as MobileAppName, " +
                "b.SiteName, b.SiteID,a.MobileAppID, d.Location, d.LocationID from d_Event a " +
                "inner join s_Site b on a.SiteID = b.SiteID inner join s_SiteMobileApp c on " +
                "a.MobileAppID=c.roll_into_app_id and a.SiteID =c.SiteID inner join s_Location d on " +
                "a.SiteID= ? and a.EventStatus = ? and a.EventID != ? inner join s_MobileApp sm on " +
                "c.roll_into_app_id = sm.MobileAppID INNER join (select  e.EventID, e.LocationID, " +
                "mobileAppId,MAX(IFNuLL(distinct e.CreationDate,0)) as Date from d_FieldData e " +
                "group by e.EventID) e on a.EventID = e.EventID and d.LocationID = e.LocationID " +
                "group by a.EventID,a.SiteID,a.MobileAppID ORDER by date DESC";  */

        String selectQuery = "select distinct  d.EventID, s.roll_into_app_id, d.LocationID, d.SiteID, " +
                "d.ModificationDate as date, d.ExtField1, (case when (b.EventName is '' or b.EventName is null) " +
                "then s.display_name_roll_into_app else b.EventName end) as MobileAppName " +
                "from d_FieldData d, (select distinct a.EventID,  max(b.ModificationDate) as dt, " +
                "a.EventName from d_event a, d_FieldData b where a.EventID = b.EventID " +
                "and a.SiteID = ? and EventStatus = ? and a.EventID != ? group by a.EventID) b, s_Site c, s_SiteMobileApp s " +
                "where d.EventID = b.EventID and  d.ModificationDate = dt " +
                "and d.SiteID = c.SiteID and d.MobileAppID = s.MobileAppID and d.SiteID = s.SiteID";

        String[] whereArgs = {siteId, eventStatus, eventId};

        // SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ChangeEventModel eventModel = new ChangeEventModel();
                eventModel.setEventID(cursor.getInt(0));
                eventModel.setMobileAppID(cursor.getInt(1));
                eventModel.setLocationID(cursor.getInt(2));
                eventModel.setSiteID(cursor.getInt(3));
                eventModel.setDate(cursor.getString(4));
                eventModel.setExtField1(cursor.getString(5));
                eventModel.setDisplayName(cursor.getString(6));
                eventModel.setSiteName(getSiteName(eventModel.getSiteID() + ""));
                eventModel.setLocation(getLocationName(eventModel.getLocationID() + ""));
                eventList.add(eventModel);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return eventList;
    }

    private String getSiteName(String siteId) {
        String selectQuery = "select distinct SiteName from s_Site where SiteID = ?";

        String[] whereArgs = {siteId};
        String siteName = "";
        Cursor cursor = database.rawQuery(selectQuery, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            siteName = cursor.getString(0);
            cursor.close();
        }
        return siteName;
    }

    private String getLocationName(String locId) {

        String selectQuery = "select distinct Location from s_Location where LocationID = ?";

        String[] whereArgs = {locId};
        String locationName = "";
        Cursor cursor = database.rawQuery(selectQuery, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            locationName = cursor.getString(0);
            cursor.close();
        }
        return locationName;
    }

    public ArrayList<CSVDataModel> getDataForCSV(String date) {
        ArrayList<CSVDataModel> csvDataList = new ArrayList<>();
        String selectQuery = "Select b.SiteName, u.Username, d.display_name_roll_into_app, c.Location, a.EventID, a.ExtField2 as Date," +
                " a.ExtField3 as Time, a.ExtField1 as SetID, a.FieldParameterLabel as ParameterLabel, a.StringValue" +
                " from d_FieldData a, s_Site b, s_Location c, s_SiteMobileApp d, s_User u where a.LocationID = c.LocationID" +
                " and a.SiteID = b.SiteID and a.MobileAppID = d.MobileAppID and u.UserID = a.UserID";

        Cursor cursor = null;
        cursor = database.rawQuery(selectQuery, null);

        if (date != null && !date.isEmpty()) {
            selectQuery = selectQuery + " and a.ExtField2 = ?";
            String[] whereArgs = {date};
            cursor = database.rawQuery(selectQuery, whereArgs);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CSVDataModel csvDataModel = new CSVDataModel();
                csvDataModel.setSiteName(cursor.getString(0));
                csvDataModel.setUserName(cursor.getString(1));
                csvDataModel.setDisplayName(cursor.getString(2));
                csvDataModel.setLocation(cursor.getString(3));
                csvDataModel.setEventId(cursor.getInt(4));
                csvDataModel.setDate(cursor.getString(5));
                csvDataModel.setTime(cursor.getString(6));
                csvDataModel.setSetId(cursor.getString(7));
                csvDataModel.setFieldParameterLabel(cursor.getString(8));
                csvDataModel.setStringValue(cursor.getString(9));

                csvDataList.add(csvDataModel);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return csvDataList;
    }

    public String getDataSyncFlagIfUpdate(String eventID, int setID, String locationID, String siteID,
                                          int appID, int paramID) {
        String flag = null;

        String currentFlag = getDataSyncFlag(eventID, setID, locationID,
                siteID, appID, paramID);

        if (currentFlag != null && currentFlag.equals(DATA_SYNC_FLAG))
            flag = DATA_SYNC_UPDATE_FLAG;
        else
            flag = currentFlag;

        return flag;
    }

    public String getDataSyncFlag(String eventID, int setID, String locationID, String siteID,
                                  int appID, int fieldParameterId) {

        String query = "select " + KEY_DataSyncFlag + " from d_FieldData where SiteID = ? and " +
                "EventID = ? and MobileAppID = ? and LocationID= ? and ExtField1 = ? and "
                + KEY_FieldParameterID + "=?";

        String[] whereArgs = {siteID + "", eventID + "", appID + "", locationID + "",
                setID + "", fieldParameterId + ""};

        Cursor c = database.rawQuery(query, whereArgs);

        String flag = null;
        if (c != null && c.moveToFirst()) {
            flag = c.getString(0);
            c.close();
        }

        return flag;
    }
}

