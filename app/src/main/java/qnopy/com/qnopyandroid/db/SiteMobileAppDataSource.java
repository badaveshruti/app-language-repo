package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.metaForms.FormTabs;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class SiteMobileAppDataSource {

    private static final String TAG = "SiteMobileAppDataSource";
    private final Context mContext;
    SQLiteDatabase database;

    public final String KEY_SiteAppID = "SiteAppID";
    public final String KEY_SiteID = "SiteID";
    public final String KEY_MobileAppID = "MobileAppID";
    public final String KEY_AppOrder = "AppOrder";
    public final String KEY_roll_into_app_id = "roll_into_app_id";
    public final String KEY_parent_app_id = "parent_app_id";
    public final String KEY_display_name = "display_name";
    public final String KEY_ShowLast2 = "ShowLast2";
    public final String KEY_label_width = "label_width";
    public final String KEY_ExtField1 = "ExtField1";
    public final String KEY_ExtField2 = "ExtField2";
    public final String KEY_ExtField3 = "ExtField3";
    public final String KEY_ExtField4 = "ExtField4";
    public final String KEY_ExtField5 = "ExtField5";
    public final String KEY_ExtField6 = "ExtField6";
    public final String KEY_ExtField7 = "ExtField7";
    public final String KEY_ExtField8 = "ExtField8";
    public final String KEY_ExtField9 = "ExtField9";
    public final String KEY_ExtField10 = "ExtField10";
    public final String KEY_notes = "notes";
    public final String KEY_CreationDate = "CreationDate";
    public final String KEY_CreatedBy = "CreatedBy";
    public final String KEY_ModifiedDate = "ModifiedDate";
    public final String KEY_ModifiedBy = "ModifiedBy";
    public final String KEY_CompanyID = "CompanyID";
    public final String KEY_LocationID = "LocationID";
    public final String KEY_AppType = "app_type";
    public final String KEY_AllowMultipleSets = "allow_multiple_sets";
    public final String KEY_Display_name_roll_into_app = "display_name_roll_into_app";
    public final String KEY_headerFlag = "headerFlag";
    public final String KEY_formQuery = "formQuery";
    public final String KEY_appDescription = "appDescription";
    public final String KEY_captionRequired = "captionRequired";

    @Inject
    public SiteMobileAppDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public long insertsiteforcompany(int siteid, int companyid) {
        long ret = 0;
        try {
            database.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(KEY_SiteID, siteid);
            values.put(KEY_CompanyID, companyid);
            try {
                ret = database.insert(DbAccess.TABLE_SITE_MOBILEAPP, null, values);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long insertSiteMobileApp(List<SSiteMobileApp> sMobileApps, int compnyid) {
        long ret = 0;
        try {
            database.beginTransaction();
            for (int i = 0; i < sMobileApps.size(); i++) {
                ContentValues values = new ContentValues();
                SSiteMobileApp sMobileApp = sMobileApps.get(i);
                if (sMobileApp != null) {

                    values.put(KEY_SiteAppID, sMobileApp.getSiteAppId());
                    values.put(KEY_SiteID, sMobileApp.getSiteId());
                    values.put(KEY_MobileAppID, sMobileApp.getMobileAppId());
                    values.put(KEY_AppOrder, sMobileApp.getApp_order());
                    values.put(KEY_roll_into_app_id, sMobileApp.getRoll_into_app_id());
                    values.put(KEY_AppType, sMobileApp.getApp_type());
                    values.put(KEY_AllowMultipleSets, sMobileApp.isAllow_multiple_sets());
                    values.put(KEY_Display_name_roll_into_app, sMobileApp.getDisplay_name_roll_into_app());

                    // TODO: 30-Mar-16
                    values.put(KEY_CompanyID, compnyid);
                    values.put(KEY_display_name, sMobileApp.getDisplay_name());
                    values.put(KEY_label_width, sMobileApp.getLabel_width());

                    try {
                        ret = database.insert(DbAccess.TABLE_SITE_MOBILEAPP, null, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeBulkBindSiteMobileApp(List<SSiteMobileApp> sMobileApps, int compnyid) {
        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_SITE_MOBILEAPP,
                database);
        long ret = 0;

        try {
            String[] arrColumns = {KEY_SiteAppID, KEY_SiteID, KEY_MobileAppID,
                    KEY_AppOrder, KEY_roll_into_app_id, KEY_AppType, KEY_AllowMultipleSets,
                    KEY_Display_name_roll_into_app, KEY_CompanyID, KEY_display_name,
                    KEY_label_width, KEY_headerFlag, SiteDataSource.KEY_Status, KEY_formQuery,
                    KEY_captionRequired};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_SITE_MOBILEAPP + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();

            for (int i = 0; i < sMobileApps.size(); i++) {
                SSiteMobileApp sMobileApp = sMobileApps.get(i);
                if (sMobileApp != null) {
                    if (sMobileApp.isInsert() || isTableEmpty) {
                        if (sMobileApp.getSiteAppId() != null)
                            statement.bindLong(1, sMobileApp.getSiteAppId());
                        else
                            statement.bindNull(1);

                        if (sMobileApp.getSiteId() != null)
                            statement.bindLong(2, sMobileApp.getSiteId());
                        else
                            statement.bindNull(2);

                        if (sMobileApp.getMobileAppId() != null)
                            statement.bindLong(3, sMobileApp.getMobileAppId());
                        else
                            statement.bindNull(3);

                        statement.bindDouble(4, sMobileApp.getApp_order());

                        statement.bindLong(5, sMobileApp.getRoll_into_app_id());

                        if (sMobileApp.getApp_type() != null)
                            statement.bindString(6, sMobileApp.getApp_type());
                        else
                            statement.bindNull(6);

                        statement.bindLong(7, sMobileApp.isAllow_multiple_sets() ? 1 : 0);

                        if (sMobileApp.getDisplay_name_roll_into_app() != null)
                            statement.bindString(8, sMobileApp.getDisplay_name_roll_into_app());
                        else
                            statement.bindNull(8);

                        statement.bindLong(9, compnyid);

                        if (sMobileApp.getDisplay_name() != null)
                            statement.bindString(10, sMobileApp.getDisplay_name());
                        else
                            statement.bindNull(10);

                        if (sMobileApp.getLabel_width() != null)
                            statement.bindString(11, sMobileApp.getLabel_width());
                        else
                            statement.bindNull(11);

                        if (sMobileApp.getHeaderFlag() != null)
                            statement.bindLong(12, sMobileApp.getHeaderFlag());
                        else
                            statement.bindNull(12);

                        if (sMobileApp.getStatus() != null)
                            statement.bindString(13, sMobileApp.getStatus());
                        else statement.bindNull(13);

                        if (sMobileApp.getFormQuery() != null)
                            statement.bindString(14, sMobileApp.getFormQuery());
                        else statement.bindNull(14);

                        if (sMobileApp.getCaptionRequired() != null) {
                            int isRequired = 0;
                            if (sMobileApp.getCaptionRequired().equalsIgnoreCase("yes"))
                                isRequired = 1;

                            statement.bindLong(15, isRequired);
                        } else statement.bindNull(15);

                        ret = statement.executeInsert();
                        statement.clearBindings();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(KEY_SiteID, sMobileApp.getSiteId());
                        values.put(KEY_MobileAppID, sMobileApp.getMobileAppId());
                        values.put(KEY_AppOrder, sMobileApp.getApp_order());
                        values.put(KEY_roll_into_app_id, sMobileApp.getRoll_into_app_id());
                        values.put(KEY_AppType, sMobileApp.getApp_type());
                        values.put(KEY_AllowMultipleSets, sMobileApp.isAllow_multiple_sets());
                        values.put(KEY_Display_name_roll_into_app, sMobileApp.getDisplay_name_roll_into_app());
                        values.put(KEY_CompanyID, compnyid);
                        values.put(KEY_display_name, sMobileApp.getDisplay_name());
                        values.put(KEY_label_width, sMobileApp.getLabel_width());
                        values.put(SiteDataSource.KEY_Status, sMobileApp.getStatus());
                        values.put(KEY_formQuery, sMobileApp.getFormQuery());

                        String whereClause = KEY_SiteAppID + " = ?";
                        String[] whereArgs = new String[]{sMobileApp.getSiteAppId() + ""};
                        ret = database.update(DbAccess.TABLE_SITE_MOBILEAPP, values,
                                whereClause, whereArgs);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeSiteMobileApp(FormTabs formTab, int companyId,
                                   int rollAppId, String formQuery) {
        long ret = 0;
        MetaDataSource metaDataSource = new MetaDataSource(mContext);

        try {
            String[] arrColumns = {KEY_SiteAppID, KEY_MobileAppID,
                    KEY_AppOrder, KEY_roll_into_app_id, KEY_AppType, KEY_AllowMultipleSets,
                    KEY_Display_name_roll_into_app, KEY_CompanyID, KEY_display_name,
                    KEY_headerFlag, KEY_formQuery, KEY_appDescription};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_SITE_MOBILEAPP + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();

            if (formTab != null) {
                int uniqueId = Util.getRandomNumberInRange(111, 999999);
                statement.bindLong(1, uniqueId);

                if (formTab.getTabId() != null)
                    statement.bindLong(2, formTab.getTabId());
                else
                    statement.bindNull(2);

                statement.bindDouble(3, formTab.getTabOrder());

                statement.bindLong(4, rollAppId);

                if (formTab.getAppType() != null)
                    statement.bindString(5, formTab.getAppType());
                else
                    statement.bindNull(5);

                statement.bindLong(6, formTab.isAllowMultipleSets() ? 1 : 0);

                if (formTab.getTabName() != null)
                    statement.bindString(7, formTab.getTabName());
                else
                    statement.bindNull(7);

                statement.bindLong(8, companyId);

                if (formTab.getTabName() != null)
                    statement.bindString(9, formTab.getTabName());
                else
                    statement.bindNull(9);

                if (formTab.getHeaderFlag() != null)
                    statement.bindLong(10, formTab.getHeaderFlag());
                else
                    statement.bindNull(10);

                if (formQuery != null)
                    statement.bindString(11, formQuery);
                else statement.bindNull(11);

                if (formTab.getAppDescription() != null)
                    statement.bindString(12, formTab.getAppDescription());
                else statement.bindNull(12);

                ret = statement.executeInsert();
                statement.clearBindings();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            if (formTab != null && formTab.getFields() != null)
                metaDataSource.storeBulkBindMetaDataTable(formTab.getFields());
        }
        return ret;
    }

    public int storeBulkSiteMobileAppList(List<SSiteMobileApp> sMobileApps, int compnyid) {
        String sql = "INSERT INTO " + DbAccess.TABLE_SITE_MOBILEAPP
                + " ('" + KEY_SiteAppID + "', '" + KEY_SiteID + "', '"
                + KEY_MobileAppID + "','" + KEY_AppOrder + "','"
                + KEY_roll_into_app_id + "','" + KEY_AppType + "','"
                + KEY_AllowMultipleSets + "') VALUES ";
        String values = "";
        for (int i = 0; i < sMobileApps.size(); i++) {
            SSiteMobileApp ob = sMobileApps.get(i);
            if (i != (sMobileApps.size() - 1)) {
                values = values + "(" + ob.getSiteAppId() + "," + ob.getSiteId() + "," + ob.getMobileAppId()
                        + "," + ob.getApp_order() + "," + ob.getRoll_into_app_id() + ",'" + ob.getApp_type() + "','"
                        + ob.isAllow_multiple_sets() + "'),";

            } else {
                values = values + "(" + ob.getSiteAppId() + "," + ob.getSiteId() + "," + ob.getMobileAppId()
                        + "," + ob.getApp_order() + "," + ob.getRoll_into_app_id() + ",'" + ob.getApp_type() + "','"
                        + ob.isAllow_multiple_sets() + "')";
            }
        }

        sql = sql + values;

        Log.i(TAG, "Insert Bulk Site Mobile App Query:" + sql);

        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            return sMobileApps.size();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to store Site MobileApp List:" + e.getMessage());

        }
        return 0;
    }

    public List<SSiteMobileApp> getQnopyApps(int compID) {
        List<SSiteMobileApp> Qapps = new ArrayList<SSiteMobileApp>();

        String query;

        // TODO: 09-Dec-16 UPDATED QUERY TO GET COMPANY FORMS

        query = "select distinct a.MobileAppID, a.MobileAppName,COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName)" +
                " from s_MobileApp a inner join s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id where b.companyID=" + compID + " union " +
                " select distinct a.MobileAppID , a.MobileAppName,COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName)" +
                " from s_MobileApp a inner join s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id where (b.SiteID=0 " +
                " and b.companyID=" + compID + ") OR a.MobileAppID IN(659,665,666,669 ,690,694 ,695,711)";

        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SSiteMobileApp siteMobileApp = new SSiteMobileApp();
                    siteMobileApp.setMobileAppId(cursor.getInt(0));
                    siteMobileApp.setDisplay_name(cursor.getString(1));
                    siteMobileApp.setDisplay_name_roll_into_app(cursor.getString(2));

                    Log.i(TAG, "Added QNOPY FORM Item:" + siteMobileApp.getDisplay_name_roll_into_app());

                    Qapps.add(siteMobileApp);

                } while (cursor.moveToNext());

                cursor.close();

            }

            // make sure to close the cursor
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }

        return Qapps;
    }

    public List<SSiteMobileApp> getAllApps(int siteID, int compID) {

        List<SSiteMobileApp> apps = new ArrayList<SSiteMobileApp>();

        String query;

        // TODO: 09-Dec-16 UPDATED QUERY TO GET COMPANY FORMS

//        query = "select distinct a.MobileAppID , a.MobileAppName," +
//                " a.app_description,a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4," +
//                " a.ExtField5, a.ExtField6, a.ExtField7,COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName)" +
//                " from s_MobileApp a inner join s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id where b.SiteID=" + siteID +
//                " and b.companyID=" + compID + " union select distinct a.MobileAppID , a.MobileAppName," +
//                " a.app_description,a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4, " +
//                " a.ExtField5, a.ExtField6, a.ExtField7,COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName)" +
//                " from s_MobileApp a inner join s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id where b.SiteID=0 " +
//                " and b.companyID=" + compID + " and b.roll_into_app_id NOT IN(select roll_into_app_id from s_SiteMobileApp where SiteID =" + siteID + ")";

        query = "select distinct a.MobileAppID , a.MobileAppName," +
                " a.app_description,a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4," +
                " a.ExtField5, a.ExtField6, a.ExtField7,COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName)" +
                " from s_MobileApp a inner join s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id where " +
                "b.SiteID=" + siteID + " and (b." + SiteDataSource.KEY_Status + " IS NULL or b."
                + SiteDataSource.KEY_Status + "=1)";

        Log.i(TAG, "getAllApps() query:" + query);
        String[] whereArgs = new String[]{"" + siteID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SSiteMobileApp siteMobileApp = new SSiteMobileApp();
                    siteMobileApp.setMobileAppId(cursor.getInt(0));
                    siteMobileApp.setDisplay_name(cursor.getString(1));
                    siteMobileApp.setAppIcon(cursor.getString(8));
                    siteMobileApp.setDisplay_name_roll_into_app(cursor.getString(12));

                    Log.i(TAG, "Added Mobile App Item:" + siteMobileApp.getDisplay_name_roll_into_app());

                    if (!apps.contains(siteMobileApp)) {
                        apps.add(siteMobileApp);

                    }
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

    public List<SSiteMobileApp> getAllAppsV16(int siteID) {

        List<SSiteMobileApp> apps = new ArrayList<SSiteMobileApp>();

        String query = "select distinct formId, formName from FormSites where siteId="
                + siteID + " and status = 1";

        Log.i(TAG, "getAllAppsV16() query:" + query);
        String[] whereArgs = new String[]{"" + siteID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SSiteMobileApp siteMobileApp = new SSiteMobileApp();
                    siteMobileApp.setMobileAppId(cursor.getInt(0));
                    siteMobileApp.setDisplay_name(cursor.getString(1));
                    siteMobileApp.setDisplay_name_roll_into_app(cursor.getString(1));

                    if (siteMobileApp.getDisplay_name().toLowerCase().contains("gw")
                            || siteMobileApp.getDisplay_name().toLowerCase().contains("gwm")
                            || siteMobileApp.getDisplay_name().toLowerCase().contains("groundwater"))
                        siteMobileApp.setAppIcon("GWM");
                    else if (siteMobileApp.getDisplay_name().toLowerCase().contains("qnopy health and safety"))
                        siteMobileApp.setAppIcon("QHS");
                    else if (siteMobileApp.getDisplay_name().toLowerCase().contains("qnopy daily field logs"))
                        siteMobileApp.setAppIcon("DFL");
                    else if (siteMobileApp.getDisplay_name().toLowerCase().contains("qnopy simple soil logs")
                            || siteMobileApp.getDisplay_name().toLowerCase().contains("qnopy soil log"))
                        siteMobileApp.setAppIcon("SSL");

                    if (!apps.contains(siteMobileApp)) {
                        apps.add(siteMobileApp);
                    }
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

    public List<SSiteMobileApp> getAllAppsByCompanyID(int compid) {
//		String Error = null;
        List<SSiteMobileApp> apps = new ArrayList<SSiteMobileApp>();

        String query = "select distinct a.MobileAppID , a.MobileAppName, a.app_description," +
                "a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4, " +
                "a.ExtField5, a.ExtField6, a.ExtField7,COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName) from s_MobileApp a inner join " +
                "s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id " +
                "where b.CompanyID=" + compid + " and b.SiteID=0";

        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    //  MobileApp app = cursorToApp(cursor);
//                    MobileApp app = new MobileApp();
                    //  app.setAppID(cursor.getInt(0));
//                    app.setAppName(cursor.getString(1));
                    SSiteMobileApp siteMobileApp = new SSiteMobileApp();
                    siteMobileApp.setMobileAppId(cursor.getInt(0));
                    siteMobileApp.setDisplay_name(cursor.getString(1));
                    //  siteMobileApp.setAllow_multiple_sets(cursor.get(3));
                    siteMobileApp.setDisplay_name_roll_into_app(cursor.getString(12));
                    // siteMobileApp.s
                    Log.i(TAG, "Added Mobile App Item:");

                    if (!apps.contains(siteMobileApp)) {
                        apps.add(siteMobileApp);

                    }
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

    public String getMobileAppDisplayNameRollIntoApp(int mobID, int siteid) {

        String mobAppName = null;
        String query = "select distinct a.formName " +
                "from FormSites a inner join s_SiteMobileApp b on a.formId = b.roll_into_app_id " +
                "where  b.roll_into_app_id =" + mobID + " and a.siteId= " + siteid;

        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                mobAppName = cursor.getString(0);
                cursor.close();
            }
            // make sure to close the cursor
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }
        return mobAppName;
    }

    public String getMobileAppDisplayName(int mobID, int siteid) {

        Log.i(TAG, "getMobileAppDisplayName() IN time=" + System.currentTimeMillis());

        String mobAppName = null;

        String query = "select distinct  COALESCE(NULLIF(b.display_name,''), a.formName) as FormName " +
                "from FormSites a inner join s_SiteMobileApp b on a.formId = b.roll_into_app_id " +
                "where  b.MobileAppID =" + mobID + " and a.siteId= " + siteid;

        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                mobAppName = cursor.getString(0);
                cursor.close();
            }
            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "getMobileAppDisplayName() Error in DisplayName:" + e.getMessage());
        }

        Log.i(TAG, "getMobileAppDisplayName() OUT time=" + System.currentTimeMillis());

        return mobAppName;
    }

    public List<SSiteMobileApp> getsiteChildApps(int userID, int parentAppID, int siteID, int companyID) {
//		String Error = null;
        List<SSiteMobileApp> apps = new ArrayList<SSiteMobileApp>();

	/*
        String query = "select distinct MobileAppID, MobileAppName, app_description, " +
				"allow_multiple_sets, app_type, ExtField1, ExtField2, ExtField3, ExtField4, " +
				"ExtField5, ExtField6, ExtField7 from s_MobileApp where MobileAppID in (select A.MobileAppID " +
				" from s_MetaData A INNER JOIN s_SiteMobileApp B "+
				" ON A.MobileAppID = B.MobileAppID where roll_into_app_id =? and B.SiteID =?) "+
				" order by MobileAppID" ;
	*/
        // TODO: 30-Mar-16
        String query =/*"select distinct a.MobileAppID,MobileAppName, c.display_name ,app_description,a.allow_multiple_sets, a.ExtField1,a.ExtField2,a.ExtField3,a.ExtField4,a.ExtField5,a.ExtField6,a.ExtField7 from s_MobileApp a, s_MetaData b, s_SiteMobileApp c where \n" +
                "a.MobileAppID = b.MobileAppID and " +
                "a.MobileAppID=c.MobileAppID and " +
                "b.MobileAppID=c.MobileAppID and" +
                "c.roll_into_app_id= ("+parentAppID+") and" +
                " c.SiteID=" +
                " case WHEN EXISTS (select distinct MobileAppID from s_SiteMobileApp " +
                "where SiteID=("+siteID+"))THEN(("+siteID+"))ELSE(0)END order by AppOrder";
*/


            /*    "select distinct a.MobileAppID, a.MobileAppName, a.allow_multiple_sets, " +
                        "a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4," +
                        " a.ExtField5, a.ExtField6, a.ExtField7 from s_MobileApp a, s_MetaData b," +
                        " s_SiteMobileApp c  where a.MobileAppID = b.MobileAppID and a.MobileAppID=c.MobileAppID" +
                        " and c.roll_into_app_id="+parentAppID+" and (c.SiteID=0 or c.SiteID="+siteID+" ) order by AppOrder";
*/
                " select distinct a.MobileAppID,MobileAppName, c.display_name ,app_description,a.allow_multiple_sets, " +
                        "a.ExtField1,a.ExtField2,a.ExtField3,a.ExtField4,a.ExtField5,a.ExtField6,a.ExtField7 from s_MobileApp a, s_MetaData b" +
                        ", s_SiteMobileApp c where a.MobileAppID = b.MobileAppID and a.MobileAppID=c.MobileAppID and c.roll_into_app_id= (" + parentAppID + ")" +
                        " and c.SiteID=case" +
                        " WHEN EXISTS (select distinct MobileAppID from s_SiteMobileApp where SiteID=(" + siteID + ") )THEN " +
                        "((" + siteID + "))ELSE(0)END order by AppOrder";


                /*"select distinct a.MobileAppID,MobileAppName, c.display_name ,app_description," +
                " a.allow_multiple_sets, a.ExtField1,a.ExtField2,a.ExtField3,a.ExtField4,a.ExtField5," +
                " a.ExtField6,a.ExtField7 from s_MobileApp a, s_MetaData b, s_SiteMobileApp c where" +
                " a.MobileAppID = b.MobileAppID and a.MobileAppID=c.MobileAppID and c.roll_into_app_id=?" +
                "  and c.SiteID=case WHEN EXISTS (select distinct MobileAppID from s_SiteMobileApp where SiteID=(?)) " +
                "THEN (?) ELSE (0)END order by AppOrder";
*/
        // TODO: 11/10/16
       /* "select distinct a.MobileAppID, a.MobileAppName, " +
                " a.allow_multiple_sets, a.app_type, a.ExtField1, a.ExtField2, a.ExtField3, a.ExtField4, " +
                " a.ExtField5, a.ExtField6, a.ExtField7 from s_MobileApp a, s_MetaData b, s_SiteMobileApp c " +
                " where a.MobileAppID = b.MobileAppID and a.MobileAppID=c.MobileAppID and c.roll_into_app_id=?" +
                " and c.SiteID=?  order by AppOrder";
*/
// TODO: 02-Apr-16
//        query="Select distinct c.MobileAppID, c.MobileAppName,c.allow_multiple_sets,c.app_type, " +
//                " c.ExtField1, c.ExtField2, c.ExtField3, c.ExtField4," +
//                " c.ExtField5, c.ExtField6, c.ExtField7 " +
//                " From s_SiteMobileApp a, s_MobileApp c, s_User u,s_MetaData b " +
//                " Where c.MobileAppID = b.MobileAppID and  a.MobileAppID = c.MobileAppID and u.UserID ="+userID +
//                " And a.roll_into_app_id = ? And (u.CompanyID = ? or " +
//                " a.SiteID =? )";

        String Error;
        // String[] whereArgs = new String[]{"" + parentAppID, ""};//,"" + companyID

        //   String[] whereArgs = new String[]{"" + parentAppID, "" + siteID};//,"" + companyID

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SSiteMobileApp sm = new SSiteMobileApp();
                    sm.setMobileAppId(cursor.getInt(0));
                    sm.setDisplay_name(cursor.getString(2));
                    apps.add(sm);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();

            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "Error in getChildApps:" + e.getMessage());
            }
        }
        return apps;
    }

    public String getMobileAppDispNameForSite(int mobID) {
        String mobAppName = null;
        Log.i(TAG, "getMobileAppDispNameForSite() IN time=" + System.currentTimeMillis());

        String query = "select distinct COALESCE(NULLIF(b.display_name,''), a.MobileAppName) " +
                "from s_MobileApp a inner join " +
                "s_SiteMobileApp b on a.MobileAppID = b.MobileAppID " +
                "where  b.MobileAppID =" + mobID + " and b.SiteID=0";

        Log.i(TAG, "getMobileAppDispNameForSite() query=" + query);


        String[] whereArgs = new String[]{"" + mobID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                mobAppName = cursor.getString(0);
                cursor.close();
                Log.i(TAG, "getMobileAppDispNameForSite() displayName=" + mobAppName);

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "getMobileAppDispNameForSite() error in MobileAppDispNameForSite:" + e.getMessage());
        }

        Log.i(TAG, "getMobileAppDispNameForSite() OUT time=" + System.currentTimeMillis());

        return mobAppName;
    }


    public String getMobileAppDisplayNameRollIntoAppForSite(int mobID) {

        String mobAppName = null;
        String query = "select distinct  COALESCE(NULLIF(b.display_name_roll_into_app,''), a.MobileAppName) from s_MobileApp a inner join \n" +
                "s_SiteMobileApp b on a.MobileAppID = b.roll_into_app_id \n" +
                "where  b.roll_into_app_id =" + mobID + " and b.SiteID = 0";

        String[] whereArgs = new String[]{"" + mobID};
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                mobAppName = cursor.getString(0);
                cursor.close();

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        }
        return mobAppName;
    }

    public boolean isCopyDataforMobileApp(int currentAppID, int siteID) {
        boolean result = false;
        Cursor c = null;
        String query = " select ExtField2 from s_SiteMobileApp where ExtField2='COPY' " +
                " and  MobileAppID=" + currentAppID + " and  SiteID=" + siteID;

        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, "isCopyDataforMobileApp() Query:" + query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c != null && c.moveToFirst()) {
            result = c.getCount() > 0;
            c.close();
        }

        return result;

    }

    public List<SSiteMobileApp> getSignUpForms(int companyID) {
        List<SSiteMobileApp> apps = new ArrayList<SSiteMobileApp>();

        SSiteMobileApp mobileApp = null;
        String query = null;

        query = "select distinct display_name_roll_into_app,roll_into_app_id " +
                "from s_SiteMobileApp where SiteID=0 and CompanyID=" + companyID;
        //query = "select distinct display_name_roll_into_app,roll_into_app_id from s_SiteMobileApp where CompanyID="+companyID;

        Cursor cursor1 = null;
        try {
            cursor1 = database.rawQuery(query, null);
            if (cursor1 != null && cursor1.moveToFirst()) {
                do {
                    mobileApp = new SSiteMobileApp();
                    mobileApp.setRoll_into_app_id(cursor1.getInt(1));
                    mobileApp.setDisplay_name_roll_into_app(cursor1.getString(0));
                    apps.add(mobileApp);
                } while (cursor1.moveToNext());

                cursor1.close();

            }

            // make sure to close the cursor
        } catch (Exception e) {
//			Error = e.getLocalizedMessage();
            e.printStackTrace();
            Log.e(TAG, "Error in getAllApps:" + e.getMessage());
        } finally {
            if (cursor1 != null) {
                cursor1.close();
            }
        }
        return apps;
    }

    public String getAppTypeCOC(int appID) {
        String mobileAppId = null;
        String sql = "select MobileAppID from s_SiteMobileApp where app_type = 'coc' " +
                "and roll_into_app_id = " + appID;
        Cursor c = null;
        try {
            c = database.rawQuery(sql, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    mobileAppId = c.getString(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MobileAppDataSource", "getApptype exception: " + e.getMessage());
        }

        return mobileAppId;
    }
}
