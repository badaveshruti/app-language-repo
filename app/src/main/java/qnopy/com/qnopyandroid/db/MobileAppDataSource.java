package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.requestmodel.MetaSyncDataModel;
import qnopy.com.qnopyandroid.requestmodel.SMobileApp;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class MobileAppDataSource {

    private static final String TAG = "MobileAppDataSource";
    //for MObile App
    final String KEY_MOBILE_APP_ID = "MobileAppID";
    final String KEY_MOBILE_APP_NAME = "MobileAppName";
    final String KEY_MOBILE_APP_DESC = "app_description";
    final String KEY_MOBILE_APP_PARENT_ID = "parent_app_id";
    final String KEY_MOBILE_APP_TYPE = "app_type";
    final String KEY_MOBILE_APP_ALLOW = "allow_multiple_sets";
    final String KEY_MOBILE_APP_EXT_FIELD1 = "ExtField1";
    final String KEY_MOBILE_APP_EXT_FIELD2 = "ExtField2";
    final String KEY_MOBILE_APP_EXT_FIELD3 = "ExtField3";
    final String KEY_MOBILE_APP_EXT_FIELD4 = "ExtField4";
    final String KEY_MOBILE_APP_EXT_FIELD5 = "ExtField5";
    final String KEY_MOBILE_APP_EXT_FIELD6 = "ExtField6";
    final String KEY_MOBILE_APP_EXT_FIELD7 = "ExtField7";
    final String KEY_MOBILE_APP_LABEL_WIDTH = "label_width";
    final String KEY_COMPANYID = "companyID";

    public SQLiteDatabase database;
    private Context mContext;

    @Inject
    public MobileAppDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        mContext = context;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    //	SELECT DISTINCT(s_MetaData.MobileAppID), MobileAppName
//	from s_MetaData INNER JOIN s_MobileApp on s_MetaData.MobileAppID=s_MobileApp.
//	MobileAppID where SiteID='2';
    public List<MobileApp> getAllApps(int siteID) {
//		String Error = null;
        List<MobileApp> apps = new ArrayList<MobileApp>();

		/*String query = "select distinct MobileAppID, MobileAppName, app_description, " +
                "allow_multiple_sets, app_type, ExtField1, ExtField2, ExtField3, ExtField4, " +
				"ExtField5, ExtField6, ExtField7 from s_MobileApp a inner join "+
				"(select distinct( b.parent_app_id)  from s_MetaData a inner join s_MobileApp b"+
								" on a.MobileAppID = b.MobileAppID and a.SiteID=?) b "+
				"on a.MobileAppID = b.parent_app_id";*/

        String query =

                "select distinct a.MobileAppID, a.MobileAppName, a.app_description," +
                        "a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4, " +
                        "a.ExtField5, a.ExtField6, a.ExtField7 from s_MobileApp a inner join " +
                        "s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id " +
                        "where SiteID=?";
//
//                "select distinct a.MobileAppID, b.display_name, a.app_description, " +
//                        "a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4, " +
//                        "a.ExtField5, a.ExtField6, a.ExtField7 from s_MobileApp a inner join " +
//                        "s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id " +
//                        "where SiteID=? and b.roll_into_app_id=b.MobileAppID";



		/*String query = "select distinct MobileAppID, MobileAppName, app_description, " +
                "allow_multiple_sets, app_type, ExtField1, ExtField2, ExtField3, ExtField4, " +
				"ExtField5, ExtField6, ExtField7 from s_MobileApp where parent_app_id " +
				"in(select distinct(parent_app_id) from s_SiteMobileApp where SiteID=?)";*/

        String[] whereArgs = new String[]{"" + siteID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MobileApp app = cursorToApp(cursor);
//                    MobileApp app = new MobileApp();
//                    app.setAppID(cursor.getInt(0));
//                    app.setAppName(cursor.getString(1));
                    Log.i(TAG, "Added Mobile App Item:" + app);
                    apps.add(app);
//                    cursor.moveToNext();
                } while (cursor.moveToNext());

                cursor.close();

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }
        return apps;
    }


    public List<String> getAllAppsForSite(int siteID) {
//		String Error = null;
        List<String> apps = new ArrayList<String>();

        String query = "select distinct a.MobileAppName from s_MobileApp a inner join " +
                " s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id " +
                " where SiteID=?";

        String[] whereArgs = new String[]{"" + siteID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String appName = cursor.getString(0);

                    Log.i(TAG, "Added Mobile App Item:" + appName);
                    apps.add(appName);
                } while (cursor.moveToNext());

                cursor.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps For Site:" + e.getMessage());
        }
        return apps;
    }

    public List<Integer> getAllChildAppIDForParentApp(int rollIntoAppID, int siteID) {
//		String Error = null;
        List<Integer> apps = new ArrayList<Integer>();

        String query = "select distinct MobileAppID from s_SiteMobileApp where roll_into_app_id=? and SiteID=? ORDER BY AppOrder";
        query = "select distinct a.MobileAppID,a.MobileAppName,c.display_name,app_description,a.allow_multiple_sets,a.ExtField1,a.ExtField2" +
                ",a.ExtField3,a.ExtField4,a.ExtField5,a.ExtField6,a.ExtField7 from s_MobileApp a, s_MetaData b, s_SiteMobileApp c " +
                "where  a.MobileAppID = b.MobileAppID and a.MobileAppID=c.MobileAppID  and c.roll_into_app_id=" + rollIntoAppID + " and" +
                " c.SiteID= case WHEN EXISTS (select distinct ifnull(SiteID,0) from s_SiteMobileApp where SiteID=(" + siteID + ")" +
                " and MobileAppID = c.MobileAppID) THEN ((" + siteID + ")) ELSE (0) END order by AppOrder";

        query = "select distinct a.MobileAppID,MobileAppName,c.display_name,c.notes,app_description,a.allow_multiple_sets, a.app_type, a.ExtField1,a.ExtField2," +
                "a.ExtField3,a.ExtField4,a.ExtField5,a.ExtField6,a.ExtField7,group_concat(distinct cm.coc_id) coc_id,group_concat(distinct cm.coc_display_id) coc_display_id from s_MobileApp a, s_SiteMobileApp c " +
                "LEFT OUTER JOIN cm_coc_master cm on a.MobileAppID = cm.form_id and c.SiteID = cm.site_id " +
                "where a.MobileAppID = c.MobileAppID and c.roll_into_app_id=" + rollIntoAppID + " and c.SiteID=" + siteID + " group by a.MobileAppID order by AppOrder\n";

//        String[] whereArgs = new String[]{"" + rollIntoAppID,siteID+""};
        Cursor cursor;
        try {

            Log.i(TAG, "getAllChildAppID For ParentApp :" + rollIntoAppID + "  ->query =" + query);
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int appID = cursor.getInt(0);

                    Log.i(TAG, "Added Mobile App ID:" + appID);
                    apps.add(appID);
                } while (cursor.moveToNext());

                cursor.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps For Site:" + e.getMessage());
        }
        return apps;
    }


    public String getMobileAppName(int mobID) {
        String query = "select distinct MobileAppName from s_MobileApp where MobileAppID=?";

        String[] whereArgs = new String[]{"" + mobID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                String mobAppName = cursor.getString(0);
                cursor.close();
                return mobAppName;

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }
        return null;
    }


    public String getMobileAppDisplayNameByMobID(int mobID, String siteID) {
        String query = "select DISTINCT display_name from s_SiteMobileApp where MobileAppID=?";

        String[] whereArgs = new String[]{"" + mobID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                String mobAppName = cursor.getString(0);
                cursor.close();
                return mobAppName;

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }
        return null;
    }

    public String getMobileAppDisplayName(int mobID) {
        String query = "select distinct display_name from s_SiteMobileApp where roll_into_app_id=?";

        String[] whereArgs = new String[]{"" + mobID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {

                String mobAppName = cursor.getString(0);
                cursor.close();
                return mobAppName;

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }
        return null;
    }

    public int getMobileAppIDonCard(String mobName) {
        String query = "select distinct MobileAppID from s_MobileApp where MobileAppName=?";

        String[] whereArgs = new String[]{"" + mobName};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                int mobAppID = cursor.getInt(0);
                cursor.close();
                return mobAppID;
            }

            // make sure to close the cursor
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in get Mob App ID:" + e.getMessage());

        }
        return 0;
    }

    public List<MobileApp> getChildApps(int parentAppID, int siteID, String locationId) {

        List<MobileApp> apps = new ArrayList<MobileApp>();
        LocationDataSource locationDataSource = new LocationDataSource(mContext);
        HashMap<String, String> mapLocationTabs = locationDataSource.getLocationTabs(locationId,
                siteID + "");

        HashMap<Integer, Integer> mapIds = new HashMap<>();//using this map to avoid redundant
        // entry as even after adding distinct to query a duplicate entry is visible

        //12/15/16
        /*String query = "select distinct a.MobileAppID,a.MobileAppName,c.display_name,app_description,a.allow_multiple_sets,a.ExtField1,a.ExtField2," +
                " a.ExtField3,a.ExtField4,a.ExtField5,a.ExtField6,a.ExtField7 from s_MobileApp a, s_MetaData b, s_SiteMobileApp c " +
                "where  a.MobileAppID = b.MobileAppID and a.MobileAppID=c.MobileAppID  and c.roll_into_app_id=" + parentAppID + " and" +
                " c.SiteID= case WHEN EXISTS (select distinct ifnull(SiteID,0) from s_SiteMobileApp where SiteID=(" + siteID + ")" +
                " and MobileAppID = c.MobileAppID) THEN ((" + siteID + ")) ELSE (0) END order by AppOrder";*/

        String query = "select distinct c.MobileAppID, a.formId, c.display_name," +
                "c.allow_multiple_sets, c.headerFlag, c.formQuery, a.formName, c.appDescription " +
                "from FormSites a, s_MetaData b, s_SiteMobileApp c " +
                "where c.MobileAppID = b.MobileAppID and a.formId = " + parentAppID +
                " and c.roll_into_app_id = " + parentAppID + " and a.siteId = " + siteID +
                " order by c.AppOrder";

        Cursor cursor = null;
        String dispname = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MobileApp app = new MobileApp();
                    app.setAppID(cursor.getInt(0));
                    app.setParentAppId(1);
                    app.setAppName(cursor.getString(2));
                    app.setAllowMultipleSets(cursor.getInt(3));
                    app.setHeaderFlag(cursor.getInt(4));
                    app.setFormQuery(cursor.getString(5));

                    String appDesc = cursor.getString(7);
                    app.setAppDescription(appDesc);

                    if (appDesc != null && !appDesc.isEmpty()) {
                        List<String> appDescList = Util.splitStringToArray("|", appDesc);
                        if (appDescList.size() > 1)
                            try {
                                app.setTabOrderForReport(Integer.parseInt(appDescList.get(1)));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                    }

                    if (!mapLocationTabs.isEmpty()) {
                        if (mapLocationTabs.containsKey(app.getAppID() + "")) {
                            if (!mapIds.containsKey(app.getAppID())) {
                                mapIds.put(app.getAppID(), app.getAppID());
                                apps.add(app);
                            }
                        }
                    } else {
                        if (!mapIds.containsKey(app.getAppID())) {
                            mapIds.put(app.getAppID(), app.getAppID());
                            apps.add(app);
                        }
                    }

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getChildApps:" + e.getMessage());
        }

        if (locationId != null) {
            int formDefault1
                    = new LocationDataSource(mContext).getLocationFormDefault(locationId);
            Collection<MobileApp> list = Collections2.filter(apps, app -> app.getHeaderFlag() == formDefault1);
            apps = Lists.newArrayList(list);
        }

        return apps;
    }

    private MobileApp cursorToApp(Cursor cursor) {
        MobileApp app = new MobileApp();
        app.setAppID(cursor.getInt(0));
        app.setAppName(cursor.getString(1));
//        app.setAppDescription(cursor.getString(2));
        //app.setParentAppId(cursor.getInt(4));
        app.setAllowMultipleSets(cursor.getInt(2));
        app.setAppType(cursor.getString(3));
        app.setExtField1(cursor.getString(4));
        app.setExtField2(cursor.getString(5));
        app.setExtField3(cursor.getString(6));
        app.setExtField4(cursor.getString(7));
        app.setExtField5(cursor.getString(8));
        app.setExtField6(cursor.getString(9));
        app.setExtField7(cursor.getString(10));
        return app;
    }


    public long insertMobileApp(SMobileApp app) {
        long ret = 0;
        if (app == null) {
            return -1;
        }
        ContentValues values = new ContentValues();

        values.put(KEY_MOBILE_APP_ID, app.getMobileAppId());
        values.put(KEY_MOBILE_APP_NAME, app.getMobileAppName());
        values.put(KEY_MOBILE_APP_ALLOW, app.isMutlipeSetsAllowed());
        values.put(KEY_MOBILE_APP_TYPE, app.getAppType());
        values.put(KEY_MOBILE_APP_LABEL_WIDTH, app.getLabelWidth());
        //       values.put(KEY_COMPANYID, 1);
        try {
            ret = database.insert(DbAccess.TABLE_MOBILE_APPS, null, values);
        } catch (Exception e) {
            System.out.println("gggg" + DbAccess.TABLE_MOBILE_APPS + "exception mesg=" + e.getLocalizedMessage());
        }
        return ret;
    }

    public int storeBulkMobileAppList(List<SMobileApp> appList) {

        int ret = 0;
        try {
            database.beginTransaction();
            for (SMobileApp app : appList) {
                ContentValues values = new ContentValues();
                if (app != null) {
                    values.put(KEY_MOBILE_APP_NAME, app.getMobileAppName());
                    values.put(KEY_MOBILE_APP_ALLOW, app.isMutlipeSetsAllowed());
                    values.put(KEY_MOBILE_APP_TYPE, app.getAppType());
                    values.put(KEY_MOBILE_APP_EXT_FIELD4, app.getExtField4());
                    values.put(KEY_MOBILE_APP_LABEL_WIDTH, app.getLabelWidth());
                    try {
                        if (app.isInsert()) {
                            values.put(KEY_MOBILE_APP_ID, app.getMobileAppId());
                            ret = (int) database.insert(DbAccess.TABLE_MOBILE_APPS, null, values);
                        } else {
                            String whereClause = KEY_MOBILE_APP_ID + " = ?";
                            String[] whereArgs = new String[]{app.getMobileAppId() + ""};
                            ret = database.update(DbAccess.TABLE_MOBILE_APPS, values,
                                    whereClause, whereArgs);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storeBulkMobileAppList");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeBulkMobileAppList(ArrayList<MetaSyncDataModel.FormSites> appList) {

        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_MOBILE_APPS,
                database);
        long ret = 0;
        try {
            String[] arrColumns = {KEY_MOBILE_APP_ID, KEY_MOBILE_APP_NAME,
                    SiteDataSource.KEY_Status};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_MOBILE_APPS + "(" + columns + ")"
                    + " VALUES(?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            for (MetaSyncDataModel.FormSites app : appList) {
                if (app != null) {
                    if (isTableEmpty) {
                        statement.bindLong(1, app.getFormId());

                        if (app.getFormName() != null)
                            statement.bindString(2, app.getFormName());
                        else
                            statement.bindNull(2);

                        if (app.getFormName() != null)
                            statement.bindString(3, app.getStatus());
                        else
                            statement.bindNull(3);

                        ret = statement.executeInsert();
                        statement.clearBindings();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storeBulkFormSitedMobileAppList");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeBulkBindMobileAppList(List<SMobileApp> appList) {

        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_MOBILE_APPS,
                database);
        long ret = 0;
        try {
            String[] arrColumns = {KEY_MOBILE_APP_ID, KEY_MOBILE_APP_NAME, KEY_MOBILE_APP_ALLOW,
                    KEY_MOBILE_APP_TYPE, KEY_MOBILE_APP_EXT_FIELD4, KEY_MOBILE_APP_LABEL_WIDTH,
                    SiteDataSource.KEY_Status};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_MOBILE_APPS + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            for (SMobileApp app : appList) {
                if (app != null) {
                    if (app.isInsert() || isTableEmpty) {
                        statement.bindLong(1, app.getMobileAppId());

                        if (app.getMobileAppName() != null)
                            statement.bindString(2, app.getMobileAppName());
                        else
                            statement.bindNull(2);

                        statement.bindLong(3, app.isMutlipeSetsAllowed() ? 1 : 0);

                        if (app.getAppType() != null)
                            statement.bindString(4, app.getAppType());
                        else statement.bindNull(4);

                        if (app.getExtField4() != null)
                            statement.bindString(5, app.getExtField4());
                        else statement.bindNull(5);

                        if (app.getLabelWidth() != null)
                            statement.bindDouble(6, app.getLabelWidth());
                        else statement.bindNull(6);

                        if (app.getStatus() != null)
                            statement.bindString(7, app.getStatus());
                        else statement.bindNull(7);

                        ret = statement.executeInsert();
                        statement.clearBindings();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(KEY_MOBILE_APP_ALLOW, app.isMutlipeSetsAllowed());
                        values.put(KEY_MOBILE_APP_TYPE, app.getAppType());
                        values.put(KEY_MOBILE_APP_EXT_FIELD4, app.getExtField4());
                        values.put(KEY_MOBILE_APP_LABEL_WIDTH, app.getLabelWidth());
                        values.put(SiteDataSource.KEY_Status, app.getStatus());

                        String whereClause = KEY_MOBILE_APP_NAME + " = ?";
                        String[] whereArgs = new String[]{app.getMobileAppName() + ""};
                        ret = database.update(DbAccess.TABLE_MOBILE_APPS, values,
                                whereClause, whereArgs);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storeBulkMobileAppList");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public String getAppType(int appID) {
        String appType = null;
        String sql = "select app_type from s_MobileApp where MobileAppID = " + appID;
        Cursor c = null;
        try {
            c = database.rawQuery(sql, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    appType = c.getString(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MobileAppDataSource", "getApptype exception: " + e.getMessage());
        }

        return appType;
    }

}