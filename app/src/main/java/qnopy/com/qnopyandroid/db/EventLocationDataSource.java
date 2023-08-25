package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class EventLocationDataSource {
    private static final String TAG = "EventLocDataSource";
    final String KEY_LocationID = "LocationID";
    final String KEY_EventID = "EventID";
    final String KEY_MobileAppID = "MobileAppID";
    final String KEY_Location = "Location";
    final String KEY_SiteID = "SiteID";


    public SQLiteDatabase database;
    Context mContext;

    @Inject
    public EventLocationDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public int insertEventLocations(List<NewClientLocation> addedLocations, String eventId, String mAppId) {
        int ret = 0;
        database.beginTransaction();
        try {
            for (NewClientLocation location : addedLocations) {

                ContentValues values = new ContentValues();
                values.put(KEY_SiteID, location.getSiteId());
                values.put(KEY_Location, location.getLocation());
                values.put(KEY_LocationID, location.getLocationId());
                values.put(KEY_EventID, eventId);
                values.put(KEY_MobileAppID, mAppId);

                ret = (int) database.insert(DbAccess.TABLE_EVENT_LOCATIONS, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to Store Sites:" + e.getMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public void updateEventId(String oldEventId, String newEventId) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_EventID, newEventId);

        String whereClause = KEY_EventID + "= ?";
        String[] whereArgs = new String[]{oldEventId};
        try {
            database.update(DbAccess.TABLE_EVENT_LOCATIONS, contentValues, whereClause, whereArgs);
            Log.e("EventLocUpdate", "EventId Updated");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EventLocUpdate", "EventId not updated " + e.getLocalizedMessage());
        }
    }

    public void updateLocId(String oldLocId, String newLocId, String siteId) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_LocationID, newLocId);

        String whereClause = KEY_LocationID + "= ? and " + KEY_SiteID + "= ?";
        String[] whereArgs = new String[]{oldLocId, siteId};
        try {
            database.update(DbAccess.TABLE_EVENT_LOCATIONS, contentValues, whereClause, whereArgs);
            Log.e("EventLocUpdate", "LocId Updated");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EventLocUpdate", "LocId not updated " + e.getLocalizedMessage());
        }
    }
}
