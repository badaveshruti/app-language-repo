package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.pref_model;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingModel;

/**
 * Created by Yogendra on 17-Jun-17.
 */

public class AppPreferenceDataSource {

    final String KEY_PreferenceMappingID = "PreferenceMappingID";
    final String KEY_FeatureID = "FeatureID";
    final String KEY_FeatureName = "FeatureName";
    final String KEY_FeatureKey = "FeatureKey";
    final String KEY_MappingStatus = "MappingStatus";
    final String KEY_UserID = "UserID";
    final String KEY_CompanyID = "CompanyID";

    Context context;
    String TAG = "AppPreferenceDataSource";
    public SQLiteDatabase database;

    public AppPreferenceDataSource(Context context) {

        Log.i(TAG, "AppPreferenceDataSource() IN time:" + System.currentTimeMillis());

        this.context = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }

        Log.i(TAG, "AppPreferenceDataSource() OUT time:" + System.currentTimeMillis());
    }

    public int storePreferenceMappingData(List<PreferenceMappingModel> preflist, int userID, int compnyID) {
        int ret = 0;
        ContentValues values;
        if (preflist == null) {
            return -1;
        } else {
            for (PreferenceMappingModel data : preflist) {
                values = new ContentValues();
                values.put(KEY_FeatureID, data.getFeatureId());
                values.put(KEY_FeatureName, data.getFeatureName());
                values.put(KEY_FeatureKey, data.getKey());
                values.put(KEY_MappingStatus, data.getStatus());
                values.put(KEY_UserID, userID);
                values.put(KEY_CompanyID, compnyID);

                try {
                    ret = (int) database.insert(DbAccess.TABLE_S_APP_PRFERENCE_MAPPING, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "storePreferenceMappingData() Feature  " + data.getFeatureName() + "  fail to insert.");
                }
            }
        }

        return ret;
    }

    public boolean isFeatureAvailable(String Feature_Key, int userID) {

//        int count = 1;
//        String query = "select count(" + KEY_FeatureKey + ") from s_PreferenceMapping where FeatureKey ='" + Feature_Key + "' and UserID = " + userID;
//        Cursor cursor = null;
//
//
//        try {
//            cursor = database.rawQuery(query, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                count = cursor.getInt(0);
//                cursor.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            count = 0;
//        }
        return 1 > 0;
    }

    public int truncateAppPreferenceMapping(int userID) {
        // TODO Auto-generated method stub

        int ret = 0;
        String whereClause = "UserID=?";
        String[] whereArgs = new String[]{userID + ""};
        try {
            ret = database.delete(DbAccess.TABLE_S_APP_PRFERENCE_MAPPING, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Truncate AppPreferenceMapping  Error= " + e.getMessage());

        }

        Log.i(TAG, "Ret value for Truncate AppPreferenceMapping  = " + ret);

        return ret;
    }


    public ArrayList<pref_model> getAllUserFeatureAvailable(int userID) {
        ArrayList<pref_model> user_features = new ArrayList<>();
        pref_model item;
        String query = "select FeatureID,FeatureName from s_PreferenceMapping where  UserID = " + userID;
//        String[] whereArgs = new String[]{"" + UserID};
        Log.i(TAG,"getAllUserFeatureAvailable() query:"+query);
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    item = new pref_model();
                    item.setFeatureID(cursor.getInt(0) + "");
                    item.setFeatureName(cursor.getString(1));
                    user_features.add(item);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user_features;
    }

    public HashMap<String, String> getAllCompanyFeatureAvailable(int cmpnyID) {
        HashMap<String, String> company_features = new HashMap<>();


        return company_features;

    }


}
