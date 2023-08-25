package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.responsemodel.LocPercentageRespModel;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 03-May-17.
 */

public class CompletionPercentageDataSource {


    private static final String TAG = "PercentageDataSource";
    final String KEY_LocationPercentageID = "Loc_Percentage_ID";
    final String KEY_LocationID = "locationID";
    final String KEY_SiteID = "site_id";
    final String KEY_Roll_into_app_id = "roll_into_app_id";
    final String KEY_StartDate = "startDate";
    final String KEY_EndDate = "endDate";
    final String KEY_Percentage_from_server = "percentage_from_server";
    final String KEY_Percentage_from_local = "percentage_from_local";

    Context mContext;

    public SQLiteDatabase database;

    public CompletionPercentageDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }


    public void percentageInsertOrUpdateHandler(List<LocPercentageRespModel> locPercentageRespModels, String prevtime, String currenttime, boolean isFROM_SERVER) {
        for (LocPercentageRespModel locmodel : locPercentageRespModels) {
            if (isPercentageHistoryAvailable(locmodel.getSiteId(), locmodel.getRollIntoAppId(), locmodel.getLocationId())) {
                if (isFROM_SERVER) {
                    updateServerPercenatge(locmodel.getSiteId(), locmodel.getRollIntoAppId(), locmodel.getLocationId(), locmodel.getPercentage());
                } else {
                    updateLocalPercenatge(locmodel.getSiteId(), locmodel.getRollIntoAppId(), locmodel.getLocationId(), locmodel.getPercentage());
                }
            } else {
                if (isFROM_SERVER) {
                    storePercentageFromServer(locmodel, prevtime, currenttime);
                } else {
                    storePercentageFromLocal(locmodel, prevtime, currenttime);
                }
            }

        }

    }

    public void storePercentageFromServer(LocPercentageRespModel locmodel, String prevtime, String currenttime) {

        long ret = 0;
        database.beginTransaction();


        try {

            ContentValues values = new ContentValues();

            values.put(KEY_LocationID, locmodel.getLocationId());
            values.put(KEY_SiteID, locmodel.getSiteId());
            values.put(KEY_Roll_into_app_id, locmodel.getRollIntoAppId());
            values.put(KEY_Percentage_from_server, locmodel.getPercentage());
            values.put(KEY_StartDate, prevtime);
            values.put(KEY_EndDate, currenttime);
            //  values.put(KEY_LOCATIONID,locmodel.getLocationId());

            ret = database.insert(DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE, null, values);
            Log.i(TAG, "storePercentageFromServer()  Result:" + ret);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        // return ret;

    }


    public void storePercentageFromLocal(LocPercentageRespModel locmodel, String prevtime, String currenttime) {

        long ret = 0;
        database.beginTransaction();


        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LocationID, locmodel.getLocationId());
            values.put(KEY_SiteID, locmodel.getSiteId());
            values.put(KEY_Roll_into_app_id, locmodel.getRollIntoAppId());
            values.put(KEY_Percentage_from_local, locmodel.getPercentage());
            values.put(KEY_StartDate, prevtime);
            values.put(KEY_EndDate, currenttime);
            //  values.put(KEY_LOCATIONID,locmodel.getLocationId());

            ret = database.insert(DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE, null, values);
            Log.i(TAG, "storePercentageFromLocal()  Result:" + ret);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        // return ret;

    }


    public ArrayList<String> getEventDataLocationIDs(String eventID, String siteID) {
        ArrayList<String> locationIDs = new ArrayList<>();


        String query = "select distinct LocationID from d_FieldData where EventID=" + eventID + " and SiteID=" + siteID;
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    locationIDs.add(cursor.getString(0));
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "getEventDataLocationIDs() exception:" + e.getMessage());

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return locationIDs;
    }


    public void calculate_and_updateEventDataPercentageFromLocal(String roll_into_app, String eventID, String siteID, int total_count) {
        ArrayList<LocPercentageRespModel> LocalPercenatgeResultList = new ArrayList<>();
        LocPercentageRespModel model;
        Cursor cursor = null;

        ArrayList<String> locationIDList = getEventDataLocationIDs(eventID, siteID);
        if (locationIDList.size() > 0) {
            for (String locID : locationIDList) {

                String query ;

                // TODO: 04-Aug-17 UPADTED QUERY
                query = "select actual,a.locationId from \n" +
                        "(select count(distinct fieldParameterId) actual, b.locationId, a.siteId from s_SiteMobileApp a,d_FieldData b \n" +
                        "where a.SiteID=b.siteId \n" +
                        "and a.MobileAppID=b.mobileAppId \n" +
                        "and a.SiteID= " + siteID + " \n" +
                        "and a.roll_into_app_id= " + roll_into_app + " \n" +
                        "and b.EventID=" + eventID + "\n" +
                        "and b.locationId = " + locID + "\n" +
                        "and b.fieldParameterId not in(15,25) \n" +
                        "and (b.stringValue IS NOT NULL AND b.stringValue NOT LIKE '') group by b.locationId) a";

                Log.i(TAG, "calculate_and_update EventData Percentage From Local() query:" + query);
                try {
                    cursor = database.rawQuery(query, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            double percentage = 0;
                            double actual_filled_count = Integer.parseInt(cursor.getString(0));
//                            double total_count = Integer.parseInt(cursor.getString(1));
                            if (total_count != 0) {
                                percentage = 100 * (actual_filled_count / total_count);
                                percentage = Util.round(percentage, 2);
                            }
                            String completion_percentage = percentage + "";
                            Log.i(TAG, "calculate_and_update Event Data Percentage from Local() filled fields=" + actual_filled_count + " ,Total Fields=" + total_count + ", completion persentage:" + completion_percentage);

                            model = new LocPercentageRespModel(locID, siteID, roll_into_app, completion_percentage);

                        } while (cursor.moveToNext());

                        cursor.close();
                    } else {
                        model = new LocPercentageRespModel(locID, siteID, roll_into_app, "0.0");
                    }

                    LocalPercenatgeResultList.add(model);

                } catch (Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                        Log.e(TAG, "calculate_and_updateEventDataPercentageFromLocal() exception:" + e.getMessage());

                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }

            }
        }

        percentageInsertOrUpdateHandler(LocalPercenatgeResultList, null, null, false);

    }


    public boolean isPercentageHistoryAvailable(String siteID, String roll_into_app, String locationID) {

        int count = 0;

        String query = null;
        Cursor cursor;
        try {
            query = "select count(*) from " + DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE +
                    " where " + KEY_Roll_into_app_id + "=? and " + KEY_SiteID + "=? and " + KEY_LocationID + "=?";
            String[] args = new String[]{roll_into_app, siteID, locationID};

            cursor = database.rawQuery(query, args);
            Log.i(TAG, "isPercentageHistoryAvailable() s_LocationFormPercentage query=" + query);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
                Log.i(TAG, "isPercentageHistoryAvailable() result:" + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isPercentageHistoryAvailable() exception:" + e.getLocalizedMessage());
            return false;
        }
        return count > 0;
    }


    public boolean updateLocalPercenatge(String siteID, String roll_into_app, String locationID, String percenatge) {


        String query1;
        Cursor cursor2 = null;
        int res = 0;
        query1 = "Update " + DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE + " set " + KEY_Percentage_from_local + "=" + percenatge +
                " where " + KEY_Roll_into_app_id + "=? and " + KEY_SiteID + "=? and " + KEY_LocationID + "=?";
        String[] args = new String[]{roll_into_app, siteID, locationID};
        try {
            cursor2 = database.rawQuery(query1, args);
            Log.i(TAG, "updateLocalPercenatge() query=" + query1);
            if (cursor2 != null) {
                res = cursor2.getCount();
                Log.i(TAG, "updateLocalPercenatge() Update result=" + res);
                cursor2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateLocalPercenatge() Update Error=" + e.getMessage());

        } finally {
            if (cursor2 != null && !cursor2.isClosed()) {
                cursor2.close();
            }
        }


        return res > 0;
    }

    public boolean updateServerPercenatge(String siteID, String roll_into_app, String locationID, String percenatge) {


        String query1;
        Cursor cursor2 = null;
        int res = 0;
        query1 = "Update " + DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE + " set " + KEY_Percentage_from_server + "=" + percenatge +
                " where " + KEY_Roll_into_app_id + "=? and " + KEY_SiteID + "=? and " + KEY_LocationID + "=?";
        String[] args = new String[]{roll_into_app, siteID, locationID};


        try {
            cursor2 = database.rawQuery(query1, args);
            Log.i(TAG, "updateServerPercenatge() query=" + query1);
            if (cursor2 != null) {
                res = cursor2.getCount();
                Log.i(TAG, "updateServerPercenatge() Update result=" + res);
                cursor2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateServerPercenatge() Update Error=" + e.getMessage());

        } finally {
            if (cursor2 != null && !cursor2.isClosed()) {
                cursor2.close();
            }
        }


        return res > 0;
    }


    public boolean truncatePercentageByRollAppID_And_SiteID(String siteID, String roll_into_app) {

        int ret = 0;
        String whereClause = KEY_Roll_into_app_id + "=? and " + KEY_SiteID + "=?";
        String[] whereArgs = new String[]{roll_into_app, siteID};

        try {
            ret = database.delete(DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Truncate truncateByRollAppID_And_SiteID  Error= " + e.getMessage());

        }

        Log.i(TAG, "Ret value for Truncate truncateByRollAppID_And_SiteID  = " + ret);


        return ret > 0;
    }


    public double getPercentageforForm_n_Location(String rollIntoAppID, String siteID, String locationID) {

        double percentage = 0, local_p = 0, server_p = 0;
        Cursor cursor = null;
        String[] selectionColumns = new String[]{
                KEY_Percentage_from_server, KEY_Percentage_from_local};
        String whereClause = KEY_Roll_into_app_id + "=? and " + KEY_LocationID + "=? and " + KEY_SiteID + "=?";
        String[] whereArgs = new String[]{rollIntoAppID, locationID, siteID};

        try {
            cursor = database.query(DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE, selectionColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                String server_percentage = cursor.getString(0);
                String local_percentage = cursor.getString(1);

                if (server_percentage != null && !server_percentage.isEmpty()) {
                    server_p = Double.parseDouble(server_percentage);
                } else {
                    server_p = 0;
                }

                if (local_percentage != null && !local_percentage.isEmpty()) {
                    local_p = Double.parseDouble(local_percentage);
                } else {
                    local_p = 0;
                }


                if (server_p == 0 && local_p == 0) {
                    percentage = 0;
                } else if (server_p > local_p) {
                    percentage = server_p > 100 ? 100 : server_p;
                } else {
                    percentage = local_p > 100 ? 100 : local_p;
                }
                Log.i(TAG, "getPercentageforForm_n_Location() ServerPercentage:" + server_percentage + " Local Percentage:" + local_percentage + " Result Percentage:" + percentage);
                // make sure to close the cursor
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getPercentageforForm_n_Location() Error:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return percentage;
    }

}
