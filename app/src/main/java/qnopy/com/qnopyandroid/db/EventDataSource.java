package qnopy.com.qnopyandroid.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalModel;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class EventDataSource {
    final String KEY_EventID = "EventID";
    final String KEY_GeneratedBy = "GeneratedBy";
    final String KEY_DeviceID = "DeviceID";
    final String KEY_MobileAppID = "MobileAppID";
    final String KEY_SiteID = "SiteID";
    final String KEY_EventDate = "EventDate";
    final String KEY_EventStartDateTime = "EventStartDateTime";
    final String KEY_EventEndDateTime = "EventEndDateTime";
    final String KEY_Latitude = "Latitude";
    final String KEY_Longitude = "Longitude";
    final String KEY_UserID = "UserID";
    final String KEY_EventStatus = "EventStatus";
    final String KEY_EventName = "EventName";
    final String KEY_eventUserName = "eventUserName";
    final String KEY_Notes = "Notes";
    final String KEY_ClientEvent = "ClientEventID";
    Context context;
    String TAG = "EventDataSource";
    public SQLiteDatabase database;

    @Inject
    public EventDataSource(Context context) {

        Log.i(TAG, "EventDataSource() IN time:" + System.currentTimeMillis());
        this.context = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
        Log.i(TAG, "EventDataSource() OUT time:" + System.currentTimeMillis());
    }

    /*If event ID exists for the given params, returns the ID else creates a new one by inserting the record
    in to the event table and returns the id
    */

    //If event ID exists for the given params, returns the ID else returns zero
    public int pickEventID(int mobileAppID, int siteID, int userID,
                           Location gpsLocation, String deviceID) {
        int eventID = 0;
        Event event = null;

        EventDataSource eventData = new EventDataSource(context);

        event = eventData.getEvent(mobileAppID, siteID, userID, deviceID);
        if (event != null) {
            eventID = event.getEventId();
        }
        return eventID;
    }

    public boolean isEventIdExists(int eventID) {
        String query = "Select count(EventID) from d_Event where EventID = ?";
        String[] whereArgs = new String[]{"" + eventID};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "isEventIdExists() error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();
        }
        return count > 0;
    }

    public boolean isEventCreatedByUs(int eventID, int userId) {
        String query = "Select count(EventID) from d_Event where EventID = ? and UserID=?";
        String[] whereArgs = new String[]{"" + eventID, userId + ""};
        Cursor c = null;

        try {
            c = database.rawQuery(query, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "isEventCreatedByUs error:" + e.getMessage());
        }
        int count = 0;
        if (c != null && c.moveToFirst()) {
            do {
                count = c.getInt(0);
            } while (c.moveToNext());
            c.close();
        }

        Log.i(TAG, "isEventIdExists() result:" + count);

        return count > 0;
    }

    public int updateEventDeviceID(String NewDeviceID) {
        ContentValues values = new ContentValues();
        int ret = 0;

        values.put(KEY_DeviceID, NewDeviceID);

        try {

            ret = database.update(DbAccess.TABLE_EVENT, values, null, null);
            Log.i(TAG, "updateDeviceID()  Ret value for updateDeviceID = " + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "updateDeviceID()  Ret value for updateDeviceID Error= " + e.getMessage());

        }

        return ret;
    }

    private void insertEventIdforSubmittals(int eventID, String generatedBy, int mobileAppID,
                                            int siteID, int userID, double lat, double longt,
                                            String deviceID, long eventDateTime, long item,
                                            int eventstatus, String eventname, SubmittalModel event) {

        long ret = 0;

        if (deviceID == null) {
            deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);
        }

        if (!isEventIdExists(eventID)) {

            if (eventDateTime == 0) {
                eventDateTime = System.currentTimeMillis();
            }

            ContentValues values = new ContentValues();

            values.put(KEY_EventID, eventID);
            values.put(KEY_ClientEvent, eventID);
            values.put(KEY_GeneratedBy, generatedBy);
            values.put(KEY_DeviceID, deviceID);
            values.put(KEY_MobileAppID, mobileAppID);
            values.put(KEY_SiteID, siteID);
            values.put(KEY_EventDate, eventDateTime);
            values.put(KEY_EventStartDateTime, event.getEventStartDate());
            values.put(KEY_EventEndDateTime, event.getEventEndDate());
            values.put(KEY_Latitude, lat);
            values.put(KEY_Longitude, longt);
            values.put(KEY_UserID, userID);
            values.put(KEY_EventStatus, eventstatus);
            values.put(KEY_EventName, eventname);
            values.put(KEY_eventUserName, event.getEventUserName());

            try {
                ret = database.insert(DbAccess.TABLE_EVENT, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("insertEventId()", "insertEventId Exception:" + e.getMessage());
            }

        } else {
            ret = updateEventStatus(eventID);
        }

    }

    public int getServerEventID(String clientID) {

        int serverID = 0;
        String[] columns = new String[]{
                KEY_EventID};

        String whereClause = KEY_ClientEvent + "=?";
        String[] whereArgs = new String[]{"" + clientID};
        String orderBy = "null";

        Cursor cursor = database.query(DbAccess.TABLE_EVENT, columns,
                whereClause, whereArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            serverID = cursor.getInt(0);
            // make sure to close the cursor
            cursor.close();
        }

        return serverID;
    }


    public void insertEventData(DEvent event, String generatedBy) {

        long ret = 0;
        String deviceID;

        if (event.getDeviceId() == null) {
            deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);
        } else {
            deviceID = event.getDeviceId();
        }

        if (!isEventIdExists(event.getEventId())) {
            ContentValues values = new ContentValues();

            values.put(KEY_EventID, event.getEventId());
            values.put(KEY_ClientEvent, event.getEventId());
            values.put(KEY_GeneratedBy, generatedBy);
            values.put(KEY_DeviceID, deviceID);
            values.put(KEY_MobileAppID, event.getMobileAppId());
            values.put(KEY_SiteID, event.getSiteId());
            values.put(KEY_EventDate, event.getEventDate());

            if (event.getEventStartDate() == 0)
                values.put(KEY_EventStartDateTime, event.getEventDate());
            else
                values.put(KEY_EventStartDateTime, event.getEventStartDate());

            values.put(KEY_EventEndDateTime, event.getEventEndDate());
            values.put(KEY_EventName, event.getEventName());
            values.put(KEY_Latitude, event.getLatitude());
            values.put(KEY_Longitude, event.getLongitude());
            values.put(KEY_UserID, event.getUserId());
            values.put(KEY_EventStatus, 1);
            values.put(KEY_eventUserName, event.getEventUserName());

            try {
                ret = database.insert(DbAccess.TABLE_EVENT, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("insertEventId()", "insertEventId Exception:" + e.getMessage());
            }
        }
    }

    public void insertEventId(int eventID, String generatedBy, int mobileAppID,
                              int siteID, int userID, double lat, double longt, String deviceID,
                              long eventDateTime, long eventEndDate, String eventName) {

        long ret = 0;

        if (deviceID == null) {
            deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);
        }

        if (!isEventIdExists(eventID)) {

            if (eventDateTime == 0) {
                eventDateTime = System.currentTimeMillis();
            }

            ContentValues values = new ContentValues();

            values.put(KEY_EventID, eventID);
            values.put(KEY_ClientEvent, eventID);
            values.put(KEY_GeneratedBy, generatedBy);
            values.put(KEY_DeviceID, deviceID);
            values.put(KEY_MobileAppID, mobileAppID);
            values.put(KEY_SiteID, siteID);
            values.put(KEY_EventDate, eventDateTime);
            values.put(KEY_EventStartDateTime, eventDateTime);
            values.put(KEY_Latitude, lat);
            values.put(KEY_Longitude, longt);
            values.put(KEY_UserID, userID);

            if (eventDateTime > 0) {
                values.put(KEY_EventEndDateTime, eventEndDate);
            }

            if (!eventName.isEmpty())
                values.put(KEY_EventName, eventName);

            try {
                ret = database.insert(DbAccess.TABLE_EVENT, null, values);
                System.out.println("Ret val of insertEventID = " + ret);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("insertEventId()", "insertEventId Exception:" + e.getMessage());
            }

        } else {
            ret = updateEventStatus(eventID);
        }
    }


    //returns the whole event
    public Event getEvent(int mobileAppID, int siteID, int userID, String deviceID) {
        Log.i(TAG, "getEvent() IN time=" + System.currentTimeMillis());

        Event event = null;
        Log.i(TAG, "getEvent() Event for Mobile=" + mobileAppID + ",SiteID=" + siteID + ",DeviceID=" + deviceID + ",UserID=" + userID);

        String[] eventDataColumns = {KEY_EventID, KEY_DeviceID, KEY_MobileAppID, KEY_SiteID,
                KEY_EventDate, KEY_EventEndDateTime, KEY_Latitude, KEY_Longitude, KEY_UserID, KEY_EventStatus,
                KEY_EventStartDateTime, KEY_GeneratedBy, KEY_EventName};

        String whereClause = KEY_MobileAppID + "=?" + " and " + KEY_SiteID + "=? " + " and " + KEY_EventStatus + "= 1";//

        String[] whereArgs = {"" + mobileAppID, "" + siteID};//, "" + userID,deviceID
        String orderBy = null;

        try (Cursor cursor = database.query(DbAccess.TABLE_EVENT, eventDataColumns,
                whereClause, whereArgs, null, null, orderBy)) {

            if (cursor != null && cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {
                    event = cursorToEvent(cursor);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "getEvent() OUT time=" + System.currentTimeMillis());
        return event;
    }

    public DEvent getEventById(int mobileAppID, int siteID, String eventId) {

        DEvent event = new DEvent();

        String[] eventDataColumns = {KEY_EventID, KEY_DeviceID, KEY_MobileAppID, KEY_SiteID,
                KEY_EventDate, KEY_EventEndDateTime, KEY_Latitude, KEY_Longitude, KEY_UserID,
                KEY_EventStartDateTime, KEY_EventName};

        String whereClause = KEY_MobileAppID + "=?" + " and " + KEY_SiteID + "=? " + " and " + KEY_EventID + "= ?";//

        String[] whereArgs = {"" + mobileAppID, "" + siteID, eventId};
        String orderBy = null;

        try (Cursor cursor = database.query(DbAccess.TABLE_EVENT, eventDataColumns,
                whereClause, whereArgs, null, null, orderBy)) {

            if (cursor != null && cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {
                    event.setEventId(cursor.getInt(0));
                    event.setDeviceId(cursor.getString(1));
                    event.setMobileAppId(cursor.getInt(2));
                    event.setSiteId(cursor.getInt(3));
                    event.setEventDate(cursor.getLong(4));
                    event.setEventEndDate(cursor.getLong(5));
                    event.setLatitude(cursor.getDouble(6));
                    event.setLongitude(cursor.getDouble(7));
                    event.setUserId(cursor.getInt(8));
                    event.setEventStartDate(cursor.getLong(9));
                    event.setEventName(cursor.getString(10));

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "getEvent() OUT time=" + System.currentTimeMillis());
        return event;
    }

    public int getEventStatus(int eventID) {

        String[] eventDataColumns = {KEY_EventStatus};
        int res = 0;
        String whereClause = KEY_EventID + "=?";//

        String[] whereArgs = {"" + eventID};//, "" + userID
        String orderBy = null;

        Cursor cursor = null;
        try {
            cursor = database.query(DbAccess.TABLE_EVENT, eventDataColumns,
                    whereClause, whereArgs, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                res = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "getEventStatus() Error:" + e.getMessage());
        }


        return res;
    }

    public boolean isEventAvailable(String eventID, String formID, String siteID) {

        String[] eventDataColumns = {KEY_EventID};

        String whereClause = KEY_EventID + "=? AND " + KEY_MobileAppID + "=? AND " + KEY_SiteID + "=?";//

        String[] whereArgs = {eventID, formID, siteID};//, "" + userID
        String orderBy = null;

        Cursor cursor = null;
        try {
            cursor = database.query(DbAccess.TABLE_EVENT, eventDataColumns,
                    whereClause, whereArgs, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                boolean result = cursor.getInt(0) > 0;
                return result;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("getEventStatus", "getEventStatus Error:" + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();

        }


        return false;
    }

    private Event cursorToEvent(Cursor cursor) {
        Log.i(TAG, "cursorToEvent() IN time=" + System.currentTimeMillis());

        Event event = new Event();
        event.setEventId(cursor.getInt(0));
        event.setDeviceId(cursor.getString(1));
        event.setMobileAppId(cursor.getInt(2));
        event.setSiteId(cursor.getInt(3));
        event.setEventDate(cursor.getLong(4));
        event.setEventEndDateTime(cursor.getLong(5));
        event.setLatitude(cursor.getDouble(6));
        event.setLongitude(cursor.getDouble(7));
        event.setUserId(cursor.getInt(8));
        event.setEventStatus(cursor.getInt(9));
        event.setEventStartDateTime(cursor.getLong(10));
        event.setGeneratedBy(cursor.getString(11));

        Log.i(TAG, "cursorToEvent() Event Data:EventID=" + event.getEventId() + ",DeveiceID=" + event.getDeviceId() + "," +
                ",MobileAppID=" + event.getMobileAppId() + ",SiteID=" + event.getSiteId() + ",EventDate=" + event.getEventDate() +
                ",Latitude" + event.getLatitude() + ",Longitude=" + event.getLongitude() + ",UserID=" + event.getUserId() + "," +
                "EventStatus=" + event.getEventStatus() + ",EventStartDateTime=" + event.getEventStartDateTime() + ",GeneratedBy=" + event.getGeneratedBy());

        Log.i(TAG, "cursorToEvent() OUT time=" + System.currentTimeMillis());

        return event;
    }

    public int updateEventID(int clientEventID, EventResponseModel res) {

        int serverEventID = res.getData().getEventId();
        long eventCreationDate = Long.parseLong(res.getData().getEventCreationDate());

        Log.i(TAG, "updateEventID() IN time:" + System.currentTimeMillis());
        Log.i(TAG, "updateEventID() localEventID:" + clientEventID + "->ServerEventID:" + serverEventID);

        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_EventID, serverEventID);
        values.put(KEY_GeneratedBy, "S");
        values.put(KEY_EventStartDateTime, res.getData().getEventStartDate());
        values.put(KEY_EventDate, res.getData().getEventDate());

        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + clientEventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.i(TAG, "updateEventID() Error:" + e.getMessage());
            }
        }
        Log.i(TAG, "updateEventID() OUT time:" + System.currentTimeMillis());

        return ret;
    }

    public int updateEventStatus(int EventID) {
        int ret = 0;
        String nullValue = null;
        ContentValues values = new ContentValues();
        values.put(KEY_EventStatus, 1);
        values.put(KEY_EventEndDateTime, nullValue);
        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + EventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateEventStatus", "updateEventStatus Error:" + e.getMessage());
        }
        return ret;
    }

    public int updateEventStatus(int EventID, int status) {
        int ret = 0;
        String nullValue = null;
        ContentValues values = new ContentValues();
        values.put(KEY_EventStatus, status);
        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + EventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateEventStatus", "updateEventStatus Error:" + e.getMessage());
        }

        return ret;
    }

    public int updateEventName(int EventID, SubmittalModel event) {
        int ret = 0;
        String nullValue = null;
        ContentValues values = new ContentValues();

        values.put(KEY_EventName, event.getEventName());

        if (event.getEventStartDate() != 0)
            values.put(KEY_EventStartDateTime, event.getEventStartDate());

        if (event.getEventEndDate() != 0)
            values.put(KEY_EventEndDateTime, event.getEventEndDate());

        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + EventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateEventName", "updateEventName Error:" + e.getMessage());
        }
        return ret;
    }

    public int updateEventNameOnly(int EventID, String eventName) {
        int ret = 0;
        String nullValue = null;
        ContentValues values = new ContentValues();

        values.put(KEY_EventName, eventName);

        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + EventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateEventName", "updateEventName Error:" + e.getMessage());
        }
        return ret;
    }

    public int updateEventUserName(int EventID, SubmittalModel event) {
        int ret = 0;
        String nullValue = null;
        ContentValues values = new ContentValues();

        values.put(KEY_eventUserName, event.getEventUserName());

        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + EventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateEventUserName", "updateEventUserName Error:" + e.getMessage());
        }
        return ret;
    }

    public int updateEndEventStatus(int EventID, int status, long endEventTime) {
        int ret = 0;
        String nullValue = null;
        ContentValues values = new ContentValues();
        values.put(KEY_EventStatus, status);
        values.put(KEY_EventEndDateTime, endEventTime);
        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + EventID};
        try {
            ret = database.update(DbAccess.TABLE_EVENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateEventStatus", "updateEventStatus Error:" + e.getMessage());
        }
        return ret;
    }

    public boolean isEventIDServerGenerated(int eventID) {

        String query = "Select GeneratedBy from d_Event where EventID = ?";
        String[] whereArgs = new String[]{"" + eventID};
        Cursor c = null;
        String generatedBy = null;
        try {
            c = database.rawQuery(query, whereArgs);

            if (c != null && c.moveToFirst()) {
                generatedBy = c.getString(0);
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }

        if ((generatedBy != null) && (generatedBy.length() != 0)) {
            return generatedBy.equalsIgnoreCase("S");
        }
        return false;
    }

    public ArrayList<DEvent> getClientGeneratedEventIDs(Context context) {

        ArrayList<DEvent> eventList = new ArrayList<>();
        DEvent event = new DEvent();

        String query = "select EventID, DeviceID, MobileAppID, SiteID, UserID, Latitude, Longitude, " +
                KEY_EventDate + ", " + KEY_EventStartDateTime + ", " + KEY_EventEndDateTime + ", "
                + KEY_EventName + " from d_Event where EventID < 0";

        Cursor c = null;
        try {
            c = database.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {

                do {

                    UserDataSource ud = new UserDataSource(context);

                    event = new DEvent();

                    int eventID = c.getInt(0);
                    String deviceID = c.getString(1);
                    int mobapp = c.getInt(2);
                    int siteId = c.getInt(3);
                    int userId = c.getInt(4);
                    Double lat = c.getDouble(5);
                    Double longt = c.getDouble(6);

                    event.setEventId(eventID);
                    event.setDeviceId(deviceID);
                    event.setMobileAppId(mobapp);
                    event.setSiteId(siteId);
                    event.setUserId(userId);
                    event.setLatitude(lat);
                    event.setLongitude(longt);
                    event.setUserName(ud.getUserNameFromID(userId + ""));

                    //Commented on 15 Jan 21
                    // event.setEventDate(System.currentTimeMillis());
//                    event.setEventDate(System.currentTimeMillis() - 86400000);

                    //added on 15 Jan 21
                    event.setEventDate(c.getLong(7));
                    event.setEventStartDate(c.getLong(8));
                    event.setEventEndDate(c.getLong(9));
                    event.setEventName(c.getString(10));
                    eventList.add(event);

                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ClientEventIDs", "getClientGeneratedEventIDs error:" + e.getMessage());
            // TODO: handle exception
            return eventList;
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return eventList;
    }

    public ArrayList<DEvent> getClientGeneratedEventIDsFromD_fielddata(Context context) {

        ArrayList<DEvent> eventList = new ArrayList<>();
        DEvent event = new DEvent();

        String query = "select EventID,DeviceID,MobileAppID,SiteID,UserID,Latitude,Longitude from d_Event where EventID<0";

        Cursor c = null;
        try {
            c = database.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {

                do {

                    UserDataSource ud = new UserDataSource(context);

                    event = new DEvent();

                    int eventID = c.getInt(0);
                    String deviceID = c.getString(1);
                    int mobapp = c.getInt(2);
                    int siteId = c.getInt(3);
                    int userId = c.getInt(4);
                    Double lat = c.getDouble(5);
                    Double longt = c.getDouble(6);

                    event.setEventId(eventID);
                    event.setDeviceId(deviceID);
                    event.setMobileAppId(mobapp);
                    event.setSiteId(siteId);
                    event.setUserId(userId);
                    event.setLatitude(lat);
                    event.setLongitude(longt);
                    event.setUserName(ud.getUserNameFromID(userId + ""));
                    event.setEventDate(System.currentTimeMillis());
//                    event.setEventDate(System.currentTimeMillis() - 86400000);

                    eventList.add(event);

                } while (c.moveToNext());
                c.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ClientEventIDs", "getClientGeneratedEventIDs error:" + e.getMessage());
            // TODO: handle exception
            return eventList;
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return eventList;
    }

    private int getNextEventID() {
        String query = "SELECT MAX(EventID) AS max_id FROM " + DbAccess.TABLE_EVENT;
        Cursor cursor = database.rawQuery(query, null);

        int id = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        id += 1;
        return id;
    }

    public void closeEventStatus(int parentAppID, int siteID, long date, String eventID) {
        String sql = "update d_Event set EventStatus=0, EventEndDateTime=" + date
                + " where EventID= " + eventID + " and EventStatus>0 and EventStatus IS NOT 900";
        try {
            database.execSQL(sql);
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "closeEventStatus() Error:" + e.getMessage());
            }
            System.out.println("updateEventStstus");
        }
    }

    public int deleteEvents() {
        int ret = 0;
        String whereClause = null;
        String[] whereArgs = null;
        try {
            ret = database.delete(DbAccess.TABLE_EVENT, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void saveSubmittalsList(List<SubmittalModel> sub_list, int userID) {
        database.beginTransaction();
        for (SubmittalModel item : sub_list) {
            if (!isEventAvailable(item.getEventId() + "", item.getMobileAppId() + "",
                    item.getSiteId() + "")) {
                double latitude = 0, longitude = 0;
                String lat = item.getLatitude();
                String longi = item.getLongitude();
                if (lat != null) {
                    latitude = Double.parseDouble(lat);
                }
                if (longi != null) {
                    longitude = Double.parseDouble(longi);
                }

                insertEventIdforSubmittals(item.getEventId(), "S",
                        item.getMobileAppId(), item.getSiteId(), item.getCreatedBy(), latitude,
                        longitude, item.getDeviceId(), item.getEventCreationDate(),
                        item.getEventModificationDate(), item.getEventStatus(),
                        item.getEventName(), item);
            } else {//UPDATE EVENT
                updateEventName(item.getEventId(), item);

                if (item.getEventUserName() != null)
                    updateEventUserName(item.getEventId(), item);

                int local_status = getEventStatus(item.getEventId());

                if (item.getEventStatus() == 900) {
                    //11/10/2017 900
                    updateEventStatus(item.getEventId(), 900);
                } else if (item.getEventStatus() == 0 && (local_status == 1)) {
                    //11/10/2017 CONFLICT
                    updateEventStatus(item.getEventId(), 2);
                } else if (item.getEventStatus() == local_status) {
                    //11/10/2017 CLOSED OR ACTIVE WHATEVER THE STATUS
                    if (item.getEventStatus() == 0) {
                        updateEndEventStatus(item.getEventId(), local_status, item.getEventModificationDate());
                    } else
                        updateEventStatus(item.getEventId(), local_status);
                } else {
                    updateEventStatus(item.getEventId(), item.getEventStatus());
                }
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public ArrayList<EventData> getSubmittalsListOnSearch(int event_status, String searchedEvent) {

        ArrayList<EventData> alist = new ArrayList<>();
        Event event = new Event();
        EventData ob;
        LocationDataSource ld = new LocationDataSource(context);
        FieldDataSource fd = new FieldDataSource(context);

        String query = "Select DISTINCT d.EventID,d.MobileAppID,d.EventStartDateTime,d.EventEndDateTime,d.EventStatus," +
                " s.SiteName,sm.display_name_roll_into_app, d.SiteID,d.EventName, d.UserID, " +
                "e.modifiedBy, IFNuLL(e.Date, d.EventStartDateTime) as updatedDate " +
                " from d_Event d,s_Site s,s_SiteMobileApp sm LEFT OUTER join (select e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e " +
                " WHERE d.MobileAppID=sm.roll_into_app_id  and d.SiteID=sm.SiteID and d.SiteID=s.SiteID and d.EventStatus=" + event_status
                + " and (s.SiteName like '%" + searchedEvent + "%' or d.EventName like '%" + searchedEvent + "%')";

/*        query = "select distinct a.EventID,a.EventDate, (case when (a.EventDisplayName is '' or a.EventDisplayName is null) then c.displayName_roll_into_app " +
                "else a.EventDisplayName end) as MobileAppName,EventStartDateTime, IFNuLL(EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID," +
                "a.EventStatus,a.MobileAppID, count(distinct d.LocationID), e.Date, sm.ExtFIeld4 from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                "inner join s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id and a.SiteID =c.SiteID inner join s_Location d on a.SiteID=d.SiteID and a.EventStatus = ? " +
                "inner join s_MobileApp sm on c.roll_into_app_id = sm.MobileAppID LEFT OUTER join (select  e.EventID, MAX(IFNuLL(distinct e.CreationDate,0)) " +
                "as Date  from d_FieldData e group by e.EventID) e on a.EventID = e.EventID group by a.EventID,a.SiteID,a.MobileAppID";*/

        if (event_status == 1) {
            query = "Select DISTINCT d.EventID,d.MobileAppID,d.EventStartDateTime,d.EventEndDateTime,d.EventStatus," +
                    " s.SiteName,sm.display_name_roll_into_app, d.SiteID,d.EventName, d.UserID, " +
                    "e.modifiedBy, IFNuLL(e.Date, d.EventStartDateTime) as updatedDate " +
                    " from d_Event d,s_Site s,s_SiteMobileApp sm LEFT OUTER join (select e.EventID, " +
                    "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                    "from d_FieldData e group by e.EventID) e " +
                    " WHERE d.MobileAppID=sm.roll_into_app_id  and d.SiteID=sm.SiteID and d.SiteID=s.SiteID and d.EventStatus IN (1,900) " +
                    "and (s.SiteName like '%" + searchedEvent + "%' or d.EventName like '%" + searchedEvent + "%')";
        }

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    ob = new EventData();

                    String sttdate = c.getString(2);
                    String edate = c.getString(3);
                    int eventID = c.getInt(7);
                    String mobID = c.getString(1);
                    String siteID = c.getString(0);
                    String eventname = c.getString(8);

                    int locationCount = ld.getLocationCountForEvent(eventID);


                    if (event_status > 0) {
                        edate = fd.getMaxCreationDateForEvent(eventID, siteID) + "";
                    }

                    long startdate = 0;
                    long enddate = 0;
                    if (event_status > 0) {
                        edate = fd.getMaxCreationDateForEvent(eventID, siteID) + "";
                    }

                    if (sttdate != null) {
                        if (sttdate.length() < 13)
                            startdate = Long.parseLong(sttdate) * 1000;
                        else
                            startdate = Long.parseLong(sttdate);
                    }

                    if (edate != null) {
                        if (edate.length() < 13)
                            enddate = Long.parseLong(edate) * 1000;
                        else
                            enddate = Long.parseLong(edate);
                    }
                    String status = c.getString(4);
                    String sitename = c.getString(5);
                    String mobname = c.getString(6);
                    long updatedDate = c.getLong(11);

                    Log.i(TAG, "getSubmittalsList() siteName:" + sitename + " Event:" + mobname + " status:" + status + " start:" + startdate + " End:" + enddate);

                    ob.setEventID(eventID);
                    ob.setSiteID(Integer.parseInt(siteID));
                    ob.setMobAppID(Integer.parseInt(mobID));
                    ob.setStartDate(startdate);
                    ob.setEndDate(enddate);
                    ob.setStatus(Integer.parseInt(status));
                    ob.setSiteName(sitename);
                    ob.setMobAppName(mobname);
                    ob.setLocationCount(locationCount);
                    ob.setEventName(eventname);
                    ob.setUserId(c.getInt(9));
                    ob.setUpdatedBy(c.getString(10));
                    ob.setUpdatedDate(updatedDate);
                    ob.setModificationDate(updatedDate != 0 ? updatedDate : startdate);
                    alist.add(ob);

                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                // this gets called even if there is an exception somewhere above
                if (c != null)
                    c.close();
            }
        }

        return alist;
    }

    public ArrayList<EventData> getAllEventsOnSearch(int event_status, String searchedEvent) {

        ArrayList<EventData> mapEvents = new ArrayList<>();
        EventData ob;
        LocationDataSource ld = new LocationDataSource(context);
        FieldDataSource fd = new FieldDataSource(context);

        String query = " select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) " +
                "as sortingDate, IFNuLL(e.modifiedBy,e.UserID) as updatedBy," +
                "(case when (a.EventName is '' or a.EventName is Null) \n" +
                " then c.display_name_roll_into_app else a.EventName end) as MobileAppName, " +
                "a.EventStartDateTime as eventDate, \n" +
                "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                "a.MobileAppID, count(distinct d.LocationID),\n" +
                " e.Date, c.ExtField4, a.UserID, strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') " +
                "as strEventDate from d_Event a \n" +
                " inner join s_Site b on a.SiteID = b.SiteID  inner join s_SiteMobileApp c " +
                "on a.MobileAppID=c.roll_into_app_id  \n" +
                " inner join s_Location d on a.SiteID=d.SiteID inner join FormSites sm " +
                "on c.roll_into_app_id = sm.formId and a.SiteID =sm.siteId LEFT OUTER \n" +
                " join (select e.EventID, e.UserID, e.modifiedBy, " +
                "MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID\n" +
                "  where a.EventStatus = " + event_status + " and (b.SiteName like '%"
                + searchedEvent + "%' or a.EventName like '%" + searchedEvent + "%') " +
                "group by a.EventID, a.SiteID, a.MobileAppID";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    ob = new EventData();

                    int eventID = c.getInt(0);
                    long sortedDate = c.getLong(1);
                    String updatedBy = c.getString(2);
                    String eventName = c.getString(3);
                    long startDate = c.getLong(4);
                    long endDate = c.getLong(5);
                    String siteName = c.getString(6);
                    int siteID = c.getInt(7);
                    int eventStatus = c.getInt(8);
                    int mobAppID = c.getInt(9);
                    int locationCount = c.getInt(10);
                    String dateCreation = c.getString(11);//not sure which date it is
                    String extField4 = c.getString(12);
                    int userId = c.getInt(13);
                    String eventDateFormatted = c.getString(14);

//                    if (eventDateFormatted == null)
                    eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate + "");

                    ob.setEventID(eventID);
                    ob.setSiteID(siteID);
                    ob.setMobAppID(mobAppID);
                    ob.setStartDate(startDate);
                    ob.setEndDate(endDate);
                    ob.setStatus(eventStatus);
                    ob.setSiteName(siteName);
                    ob.setMobAppName(eventName);
                    ob.setLocationCount(locationCount);
                    ob.setEventName(eventName);
                    ob.setExtField4(extField4);
                    ob.setUserId(userId);
                    ob.setUpdatedBy(updatedBy); //this userId could be modifiedBy or userId
                    ob.setSortedDate(sortedDate);
                    ob.setEventDateFormatted(eventDateFormatted);
                    ob.setModificationDate(sortedDate != 0 ? sortedDate : startDate);

/*                    if (event_status == eventStatus && (eventName.contains(searchedEvent)
                            || siteName.contains(searchedEvent))) {
                        mapEvents.add(ob);
                    }*/

                    mapEvents.add(ob);
                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                // this gets called even if there is an exception somewhere above
                if (c != null)
                    c.close();
            }
        }

        return mapEvents;
    }

    public ArrayList<EventData> getSubmittalsList(int event_status) {

        ArrayList<EventData> alist = new ArrayList<>();
        Event event = new Event();
        EventData ob;
        LocationDataSource ld = new LocationDataSource(context);
        FieldDataSource fd = new FieldDataSource(context);

        String query = "Select DISTINCT d.EventID, d.MobileAppID,d.EventStartDateTime, d.EventEndDateTime, d.EventStatus," +
                " s.SiteName,sm.display_name_roll_into_app, d.SiteID,d.EventName, d.UserID, " +
                "e.modifiedBy, IFNuLL(e.Date, d.EventStartDateTime) as updatedDate " +
                " from d_Event d ,s_Site s ,s_SiteMobileApp sm LEFT OUTER join (select distinct e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e " +
                "WHERE d.MobileAppID=sm.roll_into_app_id  and d.SiteID=sm.SiteID and d.SiteID=s.SiteID " +
                "and d.EventStatus=" + event_status + " order by d.EventID desc";

        if (event_status == 1) {
            query = "Select DISTINCT d.EventID, d.MobileAppID, d.EventStartDateTime, d.EventEndDateTime,d.EventStatus," +
                    " s.SiteName,sm.display_name_roll_into_app, d.SiteID,d.EventName, d.UserID, " +
                    "e.modifiedBy, IFNuLL(e.Date, d.EventStartDateTime) as updatedDate " +
                    "from d_Event d,s_Site s,s_SiteMobileApp sm LEFT OUTER join (select distinct e.EventID, " +
                    "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                    "from d_FieldData e group by e.EventID) e " +
                    " WHERE d.MobileAppID=sm.roll_into_app_id  and d.SiteID=sm.SiteID and d.SiteID=s.SiteID " +
                    "and d.EventStatus IN (1,900)" + " order by d.EventID desc";
        } /*else if (event_status == 0) {
            query = "Select DISTINCT d.SiteID,d.MobileAppID,d.EventStartDateTime,d.EventEndDateTime,d.EventStatus," +
                    " s.SiteName,sm.display_name_roll_into_app, d.EventID,d.EventName, d.UserID " +
                    " from d_Event d,s_Site s,s_SiteMobileApp sm " +
                    " WHERE d.MobileAppID=sm.roll_into_app_id  and d.SiteID=sm.SiteID and d.SiteID=s.SiteID " +
                    "and d.EventStatus=" + event_status + " order by d.EventID desc";
        }*/

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    ob = new EventData();

                    String sttdate = c.getString(2);
                    String edate = c.getString(3);
                    int eventID = c.getInt(7);
                    String mobID = c.getString(1);
                    String siteID = c.getString(0);
                    String eventname = c.getString(8);

                    int locationCount = ld.getLocationCountForEvent(eventID);

                    if (event_status > 0) {
                        edate = fd.getMaxCreationDateForEvent(eventID, siteID) + "";
                    }

                    long startdate = 0;
                    long enddate = 0;
                    if (event_status > 0) {
                        edate = fd.getMaxCreationDateForEvent(eventID, siteID) + "";
                    }

                    if (sttdate != null) {
                        if (sttdate.length() < 13)
                            startdate = Long.parseLong(sttdate) * 1000;
                        else
                            startdate = Long.parseLong(sttdate);
                    }

                    if (edate != null) {
                        if (edate.length() < 13)
                            enddate = Long.parseLong(edate) * 1000;
                        else
                            enddate = Long.parseLong(edate);
                    }

                    String status = c.getString(4);
                    String sitename = c.getString(5);
                    String mobname = c.getString(6);

                    Log.i(TAG, "getSubmittalsList() siteName:" + sitename + " Event:" + mobname + " status:" + status + " start:" + startdate + " End:" + enddate);

                    ob.setEventID(eventID);
                    ob.setSiteID(Integer.parseInt(siteID));
                    ob.setMobAppID(Integer.parseInt(mobID));
                    ob.setStartDate(startdate);
                    ob.setEndDate(enddate);
                    ob.setStatus(Integer.parseInt(status));
                    ob.setSiteName(sitename);
                    ob.setMobAppName(mobname);
                    ob.setLocationCount(locationCount);
                    ob.setEventName(eventname);
                    ob.setUserId(c.getInt(9));
                    ob.setUpdatedBy(c.getString(10));
                    ob.setUpdatedDate(c.getLong(11));

                    alist.add(ob);

                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                // this gets called even if there is an exception somewhere above
                if (c != null)
                    c.close();
            }
        }

        return alist;
    }

    public ArrayList<EventData> getAllEvents(int event_status, String siteId) {

        ArrayList<EventData> mapEvents = new ArrayList<>();
        EventData ob;

        String query = "select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) as sortingDate, " +
                "IFNuLL(e.modifiedBy,e.UserID) as updatedBy,(case when (a.EventName is '' " +
                "or a.EventName is Null) then sm.formName else a.EventName end) " +
                "as MobileAppName, a.EventStartDateTime as eventDate, \n" +
                "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                "a.MobileAppID, count(distinct d.LocationID), e.Date, c.ExtField4, a.UserID, " +
                "strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') as strEventDate, sm.formName " +
                "from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                " inner join " +
                "s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id " +
                "inner join s_Location d on a.SiteID=d.SiteID inner join FormSites sm " +
                "on c.roll_into_app_id = sm.formId and a.SiteID = sm.siteId LEFT OUTER join (select e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID " +
                "where a.EventStatus = " + event_status;

        if (siteId != null && !siteId.isEmpty() && !siteId.equals("-1"))
            query = query + " and a.SiteID = " + siteId;

        query = query + " group by a.EventID,a.SiteID,a.MobileAppID";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    ob = new EventData();

                    int eventID = c.getInt(0);
                    long sortedDate = c.getLong(1);
                    String updatedBy = c.getString(2);
                    String eventName = c.getString(3);
                    long startDate = c.getLong(4);
                    long endDate = c.getLong(5);
                    String siteName = c.getString(6);
                    int siteID = c.getInt(7);
                    int eventStatus = c.getInt(8);
                    int mobAppID = c.getInt(9);
                    int locationCount = c.getInt(10);
                    String dateCreation = c.getString(11);//not sure which date it is
                    String extField4 = c.getString(12);
                    int userId = c.getInt(13);
                    @SuppressLint("Range") String mobAppName = c.getString(15);
                    String eventDateFormatted = c.getString(14);

//                    if (eventDateFormatted == null)
                    eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate + "");

                    ob.setEventID(eventID);
                    ob.setSiteID(siteID);
                    ob.setMobAppID(mobAppID);
                    ob.setStartDate(startDate);
                    ob.setEndDate(endDate);
                    ob.setStatus(eventStatus);
                    ob.setSiteName(siteName);
                    ob.setMobAppName(mobAppName);
                    ob.setLocationCount(locationCount);
                    ob.setEventName(eventName);
                    ob.setExtField4(extField4);
                    ob.setUserId(userId);
                    ob.setUpdatedBy(updatedBy); //this userId could be modifiedBy or userId
                    ob.setSortedDate(sortedDate);
                    ob.setEventDateFormatted(eventDateFormatted);
                    ob.setModificationDate(sortedDate != 0 ? sortedDate : startDate);

/*                    if (event_status == eventStatus) {
                        mapEvents.add(ob);
                    }*/
                    mapEvents.add(ob);
                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                // this gets called even if there is an exception somewhere above
                c.close();
            }
        }

        return mapEvents;
    }

    public HashMap<String, ArrayList<EventData>> getAllEvents(String siteId) {

        HashMap<String, ArrayList<EventData>> mapEvents = new HashMap<>();
        EventData ob;

        String query = "select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) as sortingDate, " +
                "IFNuLL(e.modifiedBy,e.UserID) as updatedBy,(case when (a.EventName is '' " +
                "or a.EventName is Null) then sm.formName else a.EventName end) " +
                "as MobileAppName, a.EventStartDateTime as eventDate, \n" +
                "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                "a.MobileAppID, count(distinct d.LocationID), e.Date, c.ExtField4, a.UserID, " +
                "strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') as strEventDate, sm.formName " +
                "from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                " inner join " +
                "s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id " +
                "inner join s_Location d on a.SiteID=d.SiteID inner join FormSites sm " +
                "on c.roll_into_app_id = sm.formId and a.SiteID = sm.siteId LEFT OUTER join (select e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID ";

        if (siteId != null && !siteId.isEmpty() && !siteId.equals("-1"))
            query = query + " where a.SiteID = " + siteId;

        query = query + " group by a.EventID,a.SiteID,a.MobileAppID";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    ob = new EventData();

                    int eventID = c.getInt(0);
                    long sortedDate = c.getLong(1);
                    String updatedBy = c.getString(2);
                    String eventName = c.getString(3);
                    long startDate = c.getLong(4);
                    long endDate = c.getLong(5);
                    String siteName = c.getString(6);
                    int siteID = c.getInt(7);
                    int eventStatus = c.getInt(8);
                    int mobAppID = c.getInt(9);
                    int locationCount = c.getInt(10);
                    String dateCreation = c.getString(11);//not sure which date it is
                    String extField4 = c.getString(12);
                    int userId = c.getInt(13);
                    String eventDateFormatted = c.getString(14);
                    @SuppressLint("Range") String mobAppName
                            = c.getString(15);

//                    if (eventDateFormatted == null)
                    eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate + "");

                    ob.setEventID(eventID);
                    ob.setSiteID(siteID);
                    ob.setMobAppID(mobAppID);
                    ob.setStartDate(startDate);
                    ob.setEndDate(endDate);
                    ob.setStatus(eventStatus);
                    ob.setSiteName(siteName);
                    ob.setMobAppName(mobAppName);
                    ob.setLocationCount(locationCount);
                    ob.setEventName(eventName);
                    ob.setExtField4(extField4);
                    ob.setUserId(userId);
                    ob.setUpdatedBy(updatedBy); //this userId could be modifiedBy or userId
                    ob.setSortedDate(sortedDate);
                    ob.setEventDateFormatted(eventDateFormatted);
                    ob.setModificationDate(sortedDate != 0 ? sortedDate : startDate);

                    if (mapEvents.containsKey(eventDateFormatted)) {
                        mapEvents.get(eventDateFormatted).add(ob);
                    } else {
                        ArrayList<EventData> alist = new ArrayList<>();
                        alist.add(ob);
                        mapEvents.put(eventDateFormatted, alist);
                    }
                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                c.close();
            }
        }

        return mapEvents;
    }

    public EventData getEvent(String eventId, String mobileAppId) {

        //note this query will work if there is event id available only, but to reduce db iterations adding more specific params
        EventData event = null;

        String query = "select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) as sortingDate, " +
                "IFNuLL(e.modifiedBy,e.UserID) as updatedBy,(case when (a.EventName is '' " +
                "or a.EventName is Null) then sm.formName else a.EventName end) " +
                "as MobileAppName, a.EventStartDateTime as eventDate, \n" +
                "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                "a.MobileAppID, count(distinct d.LocationID), e.Date, c.ExtField4, a.UserID, " +
                "strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') as strEventDate," +
                " sm.formName, a.eventUserName " +
                "from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                " inner join " +
                "s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id " +
                "inner join s_Location d on a.SiteID=d.SiteID inner join FormSites sm " +
                "on c.roll_into_app_id = sm.formId and a.SiteID = sm.siteId LEFT OUTER join (select e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID " +
                "where a.EventID = " + eventId +
                " and a.MobileAppID = " + mobileAppId + " group by a.EventID, a.SiteID, a.MobileAppID";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    event = new EventData();

                    int eventID = c.getInt(0);
                    long sortedDate = c.getLong(1);
                    String updatedBy = c.getString(2);
                    String eventName = c.getString(3);
                    long startDate = c.getLong(4);
                    long endDate = c.getLong(5);
                    String siteName = c.getString(6);
                    int siteID = c.getInt(7);
                    int eventStatus = c.getInt(8);
                    int mobAppID = c.getInt(9);
                    int locationCount = c.getInt(10);
                    String dateCreation = c.getString(11);//not sure which date it is
                    String extField4 = c.getString(12);
                    int userId = c.getInt(13);
                    String eventDateFormatted = c.getString(14);
                    @SuppressLint("Range") String mobAppName
                            = c.getString(15);
                    String eventUserName = c.getString(16);

//                    if (eventDateFormatted == null)
                    eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate + "");

                    event.setEventID(eventID);
                    event.setSiteID(siteID);
                    event.setMobAppID(mobAppID);
                    event.setStartDate(startDate);
                    event.setEndDate(endDate);
                    event.setStatus(eventStatus);
                    event.setSiteName(siteName);
                    event.setMobAppName(mobAppName);
                    event.setLocationCount(locationCount);
                    event.setEventName(eventName);
                    event.setExtField4(extField4);
                    event.setUserId(userId);
                    event.setUpdatedBy(updatedBy); //this userId could be modifiedBy or userId
                    event.setSortedDate(sortedDate);
                    event.setEventDateFormatted(eventDateFormatted);
                    event.setModificationDate(sortedDate != 0 ? sortedDate : startDate);
                    event.setEventUserName(eventUserName);

                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                c.close();
            }
        }

        return event;
    }

    public boolean isEventsDownloadedAlready() {

        int count = 0;
        try {
            String query = "Select DISTINCT d.SiteID,d.MobileAppID,d.EventStartDateTime,d.EventEndDateTime,d.EventStatus," +
                    " s.SiteName,sm.display_name_roll_into_app, d.EventID " +
                    " from d_Event d,s_Site s,s_SiteMobileApp sm " +
                    " WHERE d.MobileAppID=sm.roll_into_app_id and d.SiteID=s.SiteID limit 20";

            Cursor c = database.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                count = c.getCount();
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count > 0;
    }

    public String getAllEventsOfDemoSites(String siteIds) {

        String eventIds = "";

        String newQuery = "select GROUP_CONCAT(distinct EventID) from d_event where SiteID in (" + siteIds + ")";

        try {
            Cursor cur = database.rawQuery(newQuery, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    eventIds = cur.getString(0);
                } while (cur.moveToNext());
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventIds;
    }

    public ArrayList<DEvent> getSiteEvents(int appId, int siteId) {

        ArrayList<DEvent> eventArrayList = new ArrayList<>();

        try {
            String query = "Select EventName, EventStartDateTime, EventEndDateTime, EventID, MobileAppID," +
                    " SiteID, UserID from d_Event WHERE MobileAppID = ? and SiteID = ? and EventStatus = 1";

            String[] whereArgs = {appId + "", siteId + ""};

            Cursor cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DEvent event = new DEvent();
                    event.setEventName(cursor.getString(0));
                    event.setEventStartDate(cursor.getLong(1));
                    event.setEventEndDate(cursor.getLong(2));
                    event.setEventId(cursor.getInt(3));
                    event.setMobileAppId(cursor.getInt(4));
                    event.setSiteId(cursor.getInt(5));
                    event.setUserId(cursor.getInt(6));
                    eventArrayList.add(event);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventArrayList;
    }

    public ArrayList<DEvent> getRecentEvents_10() {

        ArrayList<DEvent> eventArrayList = new ArrayList<>();

        try {
//            String query = "Select EventName, EventStartDateTime, EventEndDateTime, EventID, MobileAppID," +
//                    " SiteID, UserID from d_Event where EventName != ''  order by EventStartDateTime limit 10 ";

            String query = "select e.EventName, e.EventStartDateTime,  e.EventEndDateTime,  e.EventID,  e.MobileAppID,e.SiteID,e.UserID ,s.SiteName " +
                    "from d_Event  e Inner Join s_Site  s on e.SiteID=s.SiteID where EventName != ''  " +
                    "order by EventStartDateTime limit 10 ";

            Cursor cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DEvent event = new DEvent();
                    event.setEventName(cursor.getString(0));
                    event.setEventStartDate(cursor.getLong(1));
                    event.setEventEndDate(cursor.getLong(2));
                    event.setEventId(cursor.getInt(3));
                    event.setMobileAppId(cursor.getInt(4));
                    event.setSiteId(cursor.getInt(5));
                    event.setUserId(cursor.getInt(6));
                    event.setSiteName(cursor.getString(7));
                    eventArrayList.add(event);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventArrayList;
    }

    public ArrayList<EventData> getRecent10Events() {

        ArrayList<EventData> mapEvents = new ArrayList<>();
        EventData ob;

        String query = "select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) as sortingDate, " +
                "IFNuLL(e.modifiedBy,e.UserID) as updatedBy,(case when (a.EventName is '' " +
                "or a.EventName is Null) then c.display_name_roll_into_app else a.EventName end) " +
                "as MobileAppName, a.EventStartDateTime as eventDate, \n" +
                "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                "a.MobileAppID, count(distinct d.LocationID), e.Date, sm.ExtField4, a.UserID, " +
                "strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') as strEventDate, c.display_name_roll_into_app " +
                "from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                " inner join " +
                "s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id and a.SiteID =c.SiteID " +
                "inner join s_Location d on a.SiteID=d.SiteID inner join s_MobileApp sm " +
                "on c.roll_into_app_id = sm.MobileAppID LEFT OUTER join (select e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID " +
                " where a.EventStatus = 1";


        query = query + " group by a.EventID,a.SiteID,a.MobileAppID order by sortingDate DESC limit 10";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    ob = new EventData();

                    int eventID = c.getInt(0);
                    long sortedDate = c.getLong(1);
                    String updatedBy = c.getString(2);
                    String eventName = c.getString(3);
                    long startDate = c.getLong(4);
                    long endDate = c.getLong(5);
                    String siteName = c.getString(6);
                    int siteID = c.getInt(7);
                    int eventStatus = c.getInt(8);
                    int mobAppID = c.getInt(9);
                    int locationCount = c.getInt(10);
                    String dateCreation = c.getString(11);//not sure which date it is
                    String extField4 = c.getString(12);
                    int userId = c.getInt(13);
                    @SuppressLint("Range") String mobAppName = c.getString(c.getColumnIndex("display_name_roll_into_app"));
                    String eventDateFormatted = c.getString(14);

//                    if (eventDateFormatted == null)
                    eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate + "");

                    ob.setEventID(eventID);
                    ob.setSiteID(siteID);
                    ob.setMobAppID(mobAppID);
                    ob.setStartDate(startDate);
                    ob.setEndDate(endDate);
                    ob.setStatus(eventStatus);
                    ob.setSiteName(siteName);
                    ob.setMobAppName(mobAppName);
                    ob.setLocationCount(locationCount);
                    ob.setEventName(eventName);
                    ob.setExtField4(extField4);
                    ob.setUserId(userId);
                    ob.setUpdatedBy(updatedBy); //this userId could be modifiedBy or userId
                    ob.setSortedDate(sortedDate);
                    ob.setEventDateFormatted(eventDateFormatted);
                    ob.setModificationDate(sortedDate != 0 ? sortedDate : startDate);

/*                    if (event_status == eventStatus) {
                        mapEvents.add(ob);
                    }*/
                    mapEvents.add(ob);
                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getSubmittalsList():" + e.getMessage());
            } finally {
                // this gets called even if there is an exception somewhere above
                c.close();
            }
        }
        return mapEvents;
    }

    public ArrayList<EventData> getRecentEvents(String siteId) {
        boolean isSiteTypeTimesheet = false;
        long next6DaysMillis = Util.addDaysToCurrentDate(6);

        if (siteId != null && !siteId.trim().isEmpty())
            isSiteTypeTimesheet = new SiteDataSource(context)
                    .isSiteTypeTimeSheet(Integer.parseInt(siteId));

        long sortingDate = System.currentTimeMillis();
        if (isSiteTypeTimesheet)
            sortingDate = next6DaysMillis;

        ArrayList<EventData> listEvents = new ArrayList<>();

        String query = "select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) as sortingDate, " +
                "IFNuLL(e.modifiedBy,e.UserID) as updatedBy,(case when (a.EventName is '' " +
                "or a.EventName is Null) then sm.formName else a.EventName end) " +
                "as MobileAppName, a.EventStartDateTime as eventDate, " +
                "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                "a.MobileAppID, count(distinct d.LocationID), e.Date, c.ExtField4, a.UserID, " +
                "strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') as strEventDate, sm.formName, a.EventDate " +
                "from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                " inner join " +
                "s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id " +
                "inner join s_Location d on a.SiteID=d.SiteID inner join FormSites sm " +
                "on c.roll_into_app_id = sm.formId and a.SiteID = sm.siteId LEFT OUTER join (select e.EventID, " +
                "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID ";

        if (siteId != null && !siteId.isEmpty() && !siteId.equals("-1"))
            query = query + " where a.EventStatus = 1 and a.EventStatus != 5 and a.SiteID = "
                    + siteId + " and sortingDate <= " + sortingDate;

        query = query + " group by a.EventID, a.SiteID, a.MobileAppID order by sortingDate desc ";

        if (isSiteTypeTimesheet)
            query = query + "limit 12";//past 6 and next 6 days
        else
            query = query + "limit 10";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            try {
                do {
                    EventData event = new EventData();

                    int eventID = c.getInt(0);
                    long sortedDate = c.getLong(1);
                    String updatedBy = c.getString(2);
                    String eventName = c.getString(3);
                    long startDate = c.getLong(4);
                    long endDate = c.getLong(5);
                    String siteName = c.getString(6);
                    int siteID = c.getInt(7);
                    int eventStatus = c.getInt(8);
                    int mobAppID = c.getInt(9);
                    int locationCount = c.getInt(10);
                    String dateCreation = c.getString(11);//not sure which date it is
                    String extField4 = c.getString(12);
                    int userId = c.getInt(13);
                    String eventDateFormatted = c.getString(14);
                    @SuppressLint("Range") String mobAppName
                            = c.getString(15);
                    long eventDate = c.getLong(16);

//                    if (eventDateFormatted == null)
                    if (startDate != 0)
                        eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate + "");
                    else
                        eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(eventDate + "");

                    event.setEventID(eventID);
                    event.setSiteID(siteID);
                    event.setMobAppID(mobAppID);

                    if (startDate != 0)
                        event.setStartDate(startDate);
                    else
                        event.setStartDate(eventDate);

                    event.setEndDate(endDate);
                    event.setStatus(eventStatus);
                    event.setSiteName(siteName);
                    event.setMobAppName(mobAppName);
                    event.setLocationCount(locationCount);
                    event.setEventName(eventName);
                    event.setExtField4(extField4);
                    event.setUserId(userId);
                    event.setUpdatedBy(updatedBy); //this userId could be modifiedBy or userId
                    event.setSortedDate(sortedDate);
                    event.setEventDateFormatted(eventDateFormatted);
                    event.setModificationDate(sortedDate != 0 ? sortedDate : startDate);

                    listEvents.add(event);
                } while (c.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in getRecentEvents():" + e.getMessage());
            } finally {
                c.close();
            }
        }

/*        Collections.sort(listEvents, (lhs, rhs) -> Long.compare(rhs.getStartDate(),
                lhs.getStartDate()));*/
        return listEvents;
    }
}
