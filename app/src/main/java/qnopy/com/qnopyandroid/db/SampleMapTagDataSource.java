package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.sample_tag;

/**
 * Created by Yogendra on 16-Aug-16.
 */
public class SampleMapTagDataSource {

    private static final String TAG = "SM_TagDataSource ";

    final String KEY_SampleTagID = "SampleTagID";//Primary KEY
    final String KEY_LocationID = "LocationID";
    final String KEY_EventID = "EventID";// VARCHAR(100)

    final String KEY_SiteID = "SiteID";
    final String KEY_UserID = "UserID";
    final String KEY_MobileAppID = "MobAppID";

    final String KEY_FieldParameterID = "FieldParamID";
    final String KEY_SampleValue = "SampleValue";
    final String KEY_FilePath = "FilePath";

    final String KEY_Latitude = "Latitude";// REAL
    final String KEY_Longitude = "Longitude";// REAL
    final String KEY_SetID = "SetID";// VARCHAR(100)


    public SQLiteDatabase database;
    Context mContext;

    public SampleMapTagDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }


    public void saveSampleMapTag(sample_tag tag) {
        long ret = 0;
        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();

            values.put(KEY_EventID, tag.getEventID());
            values.put(KEY_LocationID, tag.getLocationID());
            values.put(KEY_MobileAppID, tag.getMobAppID());
            values.put(KEY_SiteID, tag.getSiteID());
            values.put(KEY_UserID, tag.getUserID());
            values.put(KEY_FieldParameterID, tag.getFieldParamID());

            values.put(KEY_SampleValue, tag.getSampleValue());
            values.put(KEY_Latitude, tag.getLatitude());
            values.put(KEY_Longitude, tag.getLongitude());
            values.put(KEY_SetID, tag.getSetID());
            values.put(KEY_FilePath, tag.getFilePath());

            ret = database.insert(DbAccess.TABLE_D_SAMPLE_MAPTAG, null, values);
            Log.i(TAG, "saveSampleMapTag row added:" + ret);

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "saveSampleMapTag:" + e.getMessage());

        } finally {
            database.endTransaction();
        }
    }

    public int updateRow_SampleMapTag(sample_tag tag) {
        int ret = 0;


        ContentValues values = new ContentValues();

        values.put(KEY_SampleValue, tag.getSampleValue());
        values.put(KEY_Latitude, tag.getLatitude());
        values.put(KEY_Longitude, tag.getLongitude());

        String whereClause = KEY_UserID + "=?  AND " + KEY_EventID + "=? AND " + KEY_MobileAppID + "=? AND " +
                KEY_LocationID + "=? AND " + KEY_FieldParameterID + "=? AND  " + KEY_SampleValue + "=?  AND  " + KEY_SiteID + "=? ";
        String[] whereArgs = new String[]
                {tag.getUserID(), tag.getEventID(), tag.getMobAppID(), tag.getLocationID(),
                        tag.getFieldParamID(), tag.getSampleValue(), tag.getSiteID()};
        try {
            ret = database.update(DbAccess.TABLE_D_SAMPLE_MAPTAG, values, whereClause, whereArgs);
            Log.i(TAG, "updateRow_SampleMapTag Updated Rows:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateRow_SampleMapTag updateValue Error:" + e.getMessage());
        }

        return ret;
    }

    public boolean moveScreenShotsToAttachmentDB() {


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


    public void autoDeleteSampleTagRowData() {
        // TODO Auto-generated method stub

        String query = "delete from d_SampleMapTag where d_SampleMapTag.SampleTagID IN\n" +
                "(select t.SampleTagID from d_FieldData d,d_SampleMapTag t where  d.StringValue LIKE '' and  d.EventID=t.EventID \n" +
                "and d.ExtField1=t.SetID and d.FieldParameterID=t.FieldParamID and d.MobileAppID=t.MobAppID AND d.LocationID and t.LocationID AND d.SiteID=t.SiteID)";

        try {
            database.execSQL(query);
            Log.i(TAG, "Deleted Data from Sample Tag");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "autoDeleteSampleTagRowData error:" + e.getMessage());
        }
    }

    public void autoUpdateSampleTagRowData() {
        // TODO Auto-generated method stub
        List<sample_tag> itemList = getUpdatableSampleTagRowData();
        updateSampleTagString(itemList);
    }

    public void updateSampleTagString(List<sample_tag> dataList) {
        int ret;
        String whereClause = null;
        String[] whereArgs = null;

        if (dataList != null) {

            for (sample_tag tag : dataList) {

                ContentValues values = new ContentValues();
                values.put(KEY_SampleValue, tag.getSampleValue());

                whereClause = KEY_SampleTagID + "=?";

                whereArgs = new String[]{"" + tag.getSampleTagID()};
                try {
                    ret = database.update(DbAccess.TABLE_D_SAMPLE_MAPTAG, values, whereClause, whereArgs);
                    Log.i(TAG, "update Sample Tag String rows(" + ret + ") success :");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "updateSampleTagString error:" + e.getMessage());

                }
            }
        }
    }


    public List<sample_tag> getUpdatableSampleTagRowData() {
        // TODO Auto-generated method stub
        List<sample_tag> updateList = new ArrayList<>();
        Cursor cursor;
        String query = "select t.SampleTagID, d.StringValue from d_FieldData d,d_SampleMapTag t where d.LocationID=t.LocationID \n" +
                "and d.MobileAppID=t.MobAppID and d.SiteID=t.SiteID and d.EventID=t.EventID\n" +
                "and d.UserID=t.UserID and d.FieldParameterID=t.FieldParamID and d.ExtField1=t.SetID and \n" +
                "(t.SampleValue NOTNULL AND d.StringValue NOTNULL and t.SampleValue!=d.StringValue)";


        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    sample_tag mTagItem = new sample_tag();
                    int sample_id = cursor.getInt(0);
                    String updatestring = cursor.getString(1);
                    mTagItem.setSampleTagID(sample_id + "");
                    mTagItem.setSampleValue(updatestring);
                    updateList.add(mTagItem);

                } while (cursor.moveToNext());
                cursor.close();

            }

            Log.i(TAG, "Auto Update Data from Sample Tag");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "autoUpdateSampleTagRowData error:" + e.getMessage());
        }

        return updateList;
    }


    public int deleteRow_SampleMapTag(sample_tag tag) {
        int ret = 0;


        String whereClause = KEY_UserID + "=?  AND " + KEY_EventID + "=? AND " + KEY_MobileAppID + "=? AND " +
                KEY_LocationID + "=? AND " + KEY_FieldParameterID + "=? AND  " + KEY_SampleValue + "=?  AND  " + KEY_SiteID + "=? ";
        String[] whereArgs = new String[]
                {
                        tag.getUserID(), tag.getEventID(), tag.getMobAppID(), tag.getLocationID(),
                        tag.getFieldParamID(), tag.getSampleValue(), tag.getSiteID()};
        try {
            ret = database.delete(DbAccess.TABLE_D_SAMPLE_MAPTAG, whereClause, whereArgs);

            Log.i(TAG, "updateRow_SampleMapTag Updated Rows:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Delete Row_SampleMapTag updateValue Error:" + e.getMessage());
        }

        return ret;
    }

    public int truncate_SampleMapTag() {
        int ret = 0;

        try {
            ret = database.delete(DbAccess.TABLE_D_SAMPLE_MAPTAG, null, null);

            Log.i(TAG, "truncate_SampleMapTag  Rows:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " truncate_SampleMapTag updateValue Error:" + e.getMessage());
        }

        return ret;
    }


    public int updateRow_SampleMapTagFilePath(sample_tag tag) {
        int ret = 0;


        ContentValues values = new ContentValues();

        values.put(KEY_FilePath, tag.getFilePath());

        String whereClause = KEY_UserID + "=?  AND " + KEY_EventID + "=? AND " + KEY_MobileAppID + "=? AND " +
                KEY_LocationID + "=? AND " + KEY_FieldParameterID + "=?  AND  " + KEY_SiteID + "=? ";
        String[] whereArgs = new String[]
                {
                        tag.getUserID(), tag.getEventID(), tag.getMobAppID(), tag.getLocationID(),
                        tag.getFieldParamID(), tag.getSiteID()};
        try {
            ret = database.update(DbAccess.TABLE_D_SAMPLE_MAPTAG, values, whereClause, whereArgs);
            Log.i(TAG, "update FilePath_SampleMapTag Updated Rows:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update FilePath_SampleMapTag updateValue Error:" + e.getMessage());
        }

        return ret;
    }

    public int updateEventID_SampleMapTag(String oldEvent, String newEventID) {
        int ret = 0;


        ContentValues values = new ContentValues();

        values.put(KEY_EventID, newEventID);

        String whereClause = KEY_EventID + "=? ";
        String[] whereArgs = new String[]{oldEvent};
        try {
            ret = database.update(DbAccess.TABLE_D_SAMPLE_MAPTAG, values, whereClause, whereArgs);
            Log.i(TAG, "updateEventID_SampleMapTag() update EventID in SampleMapTag Updated Rows:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateEventID_SampleMapTag() update EventID in SampleMapTag updateValue Error:" + e.getMessage());
        }

        return ret;
    }
//
//   public int updateLocationID_SampleMapTag(String oldLocID, String newLocID,String eventID) {
//        int ret = 0;
//
//
//        ContentValues values = new ContentValues();
//
//        values.put(KEY_LocationID, newLocID);
//
//        String whereClause = KEY_LocationID + "=? and "+KEY_EventID+"=?";
//        String[] whereArgs = new String[]{oldLocID,eventID };
//        try {
//            ret = database.update(DbAccess.TABLE_D_SAMPLE_MAPTAG, values, whereClause, whereArgs);
//            Log.i(TAG, "updateLocationID_SampleMapTag() update LocationID in SampleMapTag Updated Rows:" + ret);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "updateLocationID_SampleMapTag() update LocationID in SampleMapTag updateValue Error:" + e.getMessage());
//        }
//
//        return ret;
//    }

    public List<sample_tag> getSampleTagListForUser(String locID, String siteID, String appID, String userID, String eventID, String fieldPID, String set) {
        String[] whereArgs = null;
        int id = 0;
        String location, site, mobapp, event, samplevalue, setID, user, filepath;
        Double lati, longi;
        List<sample_tag> tagList = new ArrayList<>();
        sample_tag mTagItem;

        String query;

        autoDeleteSampleTagRowData();
        autoUpdateSampleTagRowData();
//        if (set != null) {
//            query = "SELECT * FROM " + DbAccess.TABLE_D_SAMPLE_MAPTAG +
//                    " where " + KEY_LocationID + "=? AND " + KEY_SiteID + "=? AND " + KEY_MobileAppID + "=? AND " +
//                    KEY_UserID + "=? AND " + KEY_EventID + "=?  AND " + KEY_FieldParameterID + "=? AND " + KEY_SetID + "=? ";
//
//            whereArgs = new String[]{locID, siteID, appID, userID, eventID, fieldPID, set};
//
//            Log.i(TAG, "whereArgs= Loc:" + whereArgs[0] + "Site:" + whereArgs[1] + "MobApp:" + whereArgs[2]
//                    + "User:" + whereArgs[3] + "Event:" + whereArgs[4] + "FieldParamID:" + whereArgs[5] + "Set ID:" + whereArgs[6]);
//        } else {
        query = "SELECT * FROM " + DbAccess.TABLE_D_SAMPLE_MAPTAG +
                " where " + KEY_LocationID + "=? AND " + KEY_SiteID + "=? AND " + KEY_MobileAppID + "=? AND " +
                KEY_UserID + "=? AND " + KEY_EventID + "=?  AND " + KEY_FieldParameterID + "=? ";

        whereArgs = new String[]{locID, siteID, appID, userID, eventID, fieldPID};

        Log.i(TAG, "getSampleTagListForUser() whereArgs= Loc:" + whereArgs[0] + "Site:" + whereArgs[1] + "MobApp:" + whereArgs[2]
                + "User:" + whereArgs[3] + "Event:" + whereArgs[4] + "FieldParamID:" + whereArgs[5]);
        // }
        Log.i(TAG, "getSampleTagListForUser query=" + query);

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    mTagItem = new sample_tag();
                    id = cursor.getInt(0);
                    location = cursor.getString(1);
                    event = cursor.getString(2);
                    site = cursor.getString(3);
                    user = cursor.getString(4);
                    mobapp = cursor.getString(5);
                    fieldPID = cursor.getString(6);
                    samplevalue = cursor.getString(7);
                    filepath = cursor.getString(8);
                    lati = cursor.getDouble(9);
                    longi = cursor.getDouble(10);
                    setID = cursor.getString(11);

                    mTagItem.setLocationID(location);
                    mTagItem.setEventID(event);
                    mTagItem.setSampleTagID(id + "");
                    mTagItem.setSiteID(site);
                    mTagItem.setUserID(user);
                    mTagItem.setMobAppID(mobapp);
                    mTagItem.setFieldParamID(fieldPID);
                    mTagItem.setSampleValue(samplevalue);
                    mTagItem.setFilePath(filepath);
                    mTagItem.setLatitude(lati);
                    mTagItem.setLongitude(longi);
                    mTagItem.setSetID(setID);

                    tagList.add(mTagItem);
                } while (cursor.moveToNext());
                cursor.close();

            }

        } catch (Exception e) {
            Log.e(TAG, "getSetIDSeriesForUser Error:" + e.getMessage());
            e.printStackTrace();

        }


        return tagList;
    }

    public int getSampleTagCount(String locID, String siteID, String appID, String userID, String eventID, String fieldPID) {
        String[] whereArgs = null;
        int id = 0;
        String location, site, mobapp, event, samplevalue, setID, user, filepath;
        Double lati, longi;
        List<sample_tag> tagList = new ArrayList<>();
        sample_tag mTagItem;

        String query = "SELECT count(*) FROM " + DbAccess.TABLE_D_SAMPLE_MAPTAG +
                " where " + KEY_LocationID + "=? AND " + KEY_SiteID + "=? AND " + KEY_MobileAppID + "=? AND " +
                KEY_UserID + "=? AND " + KEY_EventID + "=?  AND " + KEY_FieldParameterID + "=? ";

        whereArgs = new String[]{locID, siteID, appID, userID, eventID, fieldPID};
        Log.i(TAG, "getSampleTagListForUser query=" + query);
        Log.i(TAG, "whereArgs= Loc:" + whereArgs[0] + "Site:" + whereArgs[1] + "MobApp:" + whereArgs[2]
                + "User:" + whereArgs[3] + "Event:" + whereArgs[4] + "FieldParamID:" + whereArgs[5]);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
//                cursor.close();

            }

        } catch (Exception e) {
            Log.e(TAG, "getSetIDSeriesForUser Error:" + e.getMessage());
            e.printStackTrace();

        }


        return 0;
    }

    public int deletesetfromsamplemap(String locationID, int siteID, int eventID, int curSetID, int AppID) {

        int ret = 0;
        String whereClause = "EventID =? and LocationID=? and SiteID=? and MobAppID=? and CAST(SetID AS INTEGER)=?";
        String[] whereArgs = new String[]{"" + eventID, locationID + "", siteID + "", AppID + "", curSetID + ""};
        try {
            ret = database.delete(DbAccess.TABLE_D_SAMPLE_MAPTAG, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void updatesampletag(String locationID, int siteID, int eventID, int curSetID, int currentAppID) {

        Cursor c = null;

        String query = "update d_SampleMapTag set SetID=CAST(SetID AS INTEGER)-1 where locationId=" + locationID + " and eventId =" + eventID +
                " and siteId=" + siteID + " and MobAppID=" + currentAppID + " and CAST(SetID AS INTEGER) > " + curSetID;

        Log.i(TAG, " updatesampletag() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updatesampletag() for set result count:" + c.getCount());
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
}
