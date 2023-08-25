package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.DefaultValueModel;

/**
 * Created by Yogendra on 31-Aug-16.
 */
public class DefaultValueDataSource {
    public SQLiteDatabase database;
    Context mContext;
    private static final String TAG = "DValueDataSource";

    final String KEY_DefaultValueID = "DefaultValueID";
    final String KEY_LocationID = "LocationID";
    final String KEY_MobileAppID = "MobileAppID";
    final String KEY_UpperLimit = "upperLimit";
    final String KEY_LowerLimit = "lowerLimit";
    final String KEY_DefaultValue = "defaultValue";
    final String KEY_fieldParameterID = "fieldParameterID";
    final String KEY_warningHigh = "warning_high";
    final String KEY_warningLow = "warning_low";
    final String KEY_warningValue = "warningValue";
    final String KEY_setId = "setId";

    public DefaultValueDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public int getdefvalue(String locid, int mobappid) {
        Cursor cursor = null;
        int defaultValue = 0;
        String query = "select d.defaultValue from s_Default_Values d where " +
                "d.LocationID=" + locid + " and d.MobileAppID=" + mobappid;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    defaultValue = cursor.getInt(0);

                    Log.i(TAG, "DefaultValue:" + defaultValue);

                } while (cursor.moveToNext());

                cursor.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in Location User N device:" + e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public long saveDefaultValueList(List<DefaultValueModel> dataList) {
        long ret = 0;
        database.beginTransaction();
        DefaultValueModel dvData;
        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_S_DEFAULT_VALUES,
                database);
        try {
            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                dvData = dataList.get(i);

                values.put(KEY_UpperLimit, dvData.getHighLimitDefaultValue());
                values.put(KEY_LowerLimit, dvData.getLowLimitDefaultValue());
                values.put(KEY_DefaultValue, dvData.getDefaultValue());
                values.put(KEY_warningHigh, dvData.getWarningHighDefaultValue());
                values.put(KEY_warningLow, dvData.getWarningLowDefaultValue());
                values.put(SiteDataSource.KEY_Status, dvData.getStatus());
                values.put(KEY_warningValue, dvData.getWarningValue());
                values.put(KEY_setId, dvData.getSetId());

                if (dvData.isInsert() || isTableEmpty) {
                    values.put(KEY_LocationID, dvData.getLocationId());
                    values.put(KEY_fieldParameterID, dvData.getFieldParameterId());
                    values.put(KEY_MobileAppID, dvData.getMobileAppId());
                    ret = database.insert(DbAccess.TABLE_S_DEFAULT_VALUES, null, values);
                } else {
                    String whereClause = KEY_fieldParameterID + " = ?" + KEY_LocationID + " = ?"
                            + KEY_MobileAppID + " = ?";
                    String[] whereArgs = new String[]{dvData.getFieldParameterId() + "",
                            dvData.getLocationId() + "", dvData.getMobileAppId() + ""};
                    ret = database.update(DbAccess.TABLE_S_DEFAULT_VALUES, values,
                            whereClause, whereArgs);
                }
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveDefaultValueList error:" + e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public String getDefaultValue(String locID, String mobAppID, String fpID, String setId) {

        String query = null;
        String defaultValue = null;

/*        query = "select defaultValue,upperLimit,lowerLimit from s_Default_Values where  " +
                "LocationID=" + locID + " AND MobileAppID=" + mobAppID + " AND fieldParameterID= "
                + fpID + " and Status = 1 and (setId = " + setId + " or setId = 0 or setId is null)";*/

        query = "Select distinct defaultValue " +
                "From s_Default_Values a " +
                "WHERE setId = " +
                "(select ifnull(max(setId),0) set_to_get " +
                "from s_Default_Values a " +
                "where a.setId = " + setId +
                " and a.MobileAppID = " + mobAppID + " And a.fieldParameterId = " + fpID + " And a.LocationID = " + locID + ") " +
                "and a.MobileAppID = " + mobAppID + " And a.fieldParameterId = " + fpID + " And a.LocationID = " + locID;

        Log.i(TAG, "query=" + query);
        String[] whereClause = new String[]{setId, mobAppID, fpID, locID};
        try (Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    defaultValue = cursor.getString(0);
                    Log.i(TAG, "DefaultValue:" + defaultValue);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in Location User N device:" + e.getLocalizedMessage());
        }

        return defaultValue;
    }

    public DefaultValueModel getDefaultValueToWarn(String locID, String mobAppID, String fpID, String setId) {

        String query = null;
        DefaultValueModel defaultValue = new DefaultValueModel();

        query = "Select distinct defaultValue, upperLimit, lowerLimit,warning_high, warning_low, " +
                "warningValue, setId, LocationID, MobileAppID, fieldParameterID " +
                "From s_Default_Values a " +
                "WHERE setId = " +
                "(select ifnull(max(setId),0) set_to_get " +
                "from s_Default_Values a " +
                "where a.setId = " + setId +
                " and a.MobileAppID = " + mobAppID + " And a.fieldParameterId = " + fpID + " And a.LocationID = " + locID + ") " +
                "and a.MobileAppID = " + mobAppID + " And a.fieldParameterId = " + fpID + " And a.LocationID = " + locID;

        Log.i(TAG, "query=" + query);
        String[] whereClause = new String[]{setId, mobAppID, fpID, locID};

        Log.i(TAG, "query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    defaultValue = new DefaultValueModel();
                    String d_value = cursor.getString(0);
                    String upperlimit = cursor.getString(1);
                    String lowerlimit = cursor.getString(2);
                    String warningh = cursor.getString(3);
                    String warningl = cursor.getString(4);
                    String warningValue = cursor.getString(5);

                    defaultValue.setDefaultValue(d_value);
                    defaultValue.setHighLimitDefaultValue(upperlimit);

                    defaultValue.setLowLimitDefaultValue(lowerlimit);
                    defaultValue.setWarningHighDefaultValue(warningh);
                    defaultValue.setWarningLowDefaultValue(warningl);
                    defaultValue.setWarningValue(warningValue);

                    Log.i(TAG, "DefaultValue:" + defaultValue);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in Location User N device:" + e.getLocalizedMessage());
        }

        return defaultValue;
    }

    public void updateWarningLowValue(String locationID, String currentFormId,
                                      String fieldParameterId, String newWarningLowValue) {
        Log.e("Qnopy", "updateWarningLowValue: " + locationID + ", "
                + currentFormId + ", " + fieldParameterId + ", " + newWarningLowValue);

        ContentValues values = new ContentValues();
        int ret = 0;
        values.put(KEY_warningLow, newWarningLowValue);
        String whereClause = "LocationID=? and MobileAppID=? and fieldParameterID=?";
        String[] whereArgs = new String[]{"" + locationID, "" + currentFormId, "" + fieldParameterId};
        try {
            ret = database.update(DbAccess.TABLE_S_DEFAULT_VALUES, values, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Ret value for update LocFormStatus  = " + ret);
        // return ret;
    }

    public void insertNewWarningLowValue(String locationID, String currentFormId, String fieldParameterId, String newWarningLowValue) {
        Log.e("sonawane", "updateWarningLowValue: " + locationID + ", " + currentFormId + ", " + fieldParameterId + ", " + newWarningLowValue);
        long ret = 0;
        database.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_LocationID, locationID);
            values.put(KEY_MobileAppID, currentFormId);
            values.put(KEY_fieldParameterID, fieldParameterId);
            values.put(KEY_warningLow, newWarningLowValue);
            String whereClause = "LocationID=? and MobileAppID=? and fieldParameterID=?";
            String[] whereArgs = new String[]{"" + locationID, "" + currentFormId, "" + fieldParameterId};

            ret = database.insert(DbAccess.TABLE_S_DEFAULT_VALUES, null, values);
            Log.i("sonawane", "save Default Value List :" + ret);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveDefaultValueList error:" + e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
    }
}
