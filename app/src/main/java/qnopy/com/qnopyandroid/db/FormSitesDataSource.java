package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.metaForms.FormsData;
import qnopy.com.qnopyandroid.requestmodel.MetaSyncDataModel;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class FormSitesDataSource {

    private static final String TAG = "MobileAppDataSource";
    final String KEY_formSiteId = "formSiteId";
    final String KEY_siteId = "siteId";
    final String KEY_formId = "formId";
    final String KEY_formName = "formName";
    final String KEY_status = "status";
    final String KEY_insert = "isInsert";
    final String KEY_appType = "appType";
    final String KEY_approvalRequired = "approvalRequired";

    public SQLiteDatabase database;
    private Context mContext;

    @Inject
    public FormSitesDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        mContext = context;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public long storeBulkMobileAppList(ArrayList<MetaSyncDataModel.FormSites> appList, boolean override) {

        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_FORM_SITES,
                database);
        long ret = 0;
        try {
            String[] arrColumns = {KEY_formSiteId, KEY_formId, KEY_formName, KEY_siteId,
                    KEY_status, KEY_insert};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_FORM_SITES + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            for (MetaSyncDataModel.FormSites app : appList) {
                if (app != null) {
                    if (isTableEmpty || override) {

                        statement.bindLong(1, app.getFormSiteId());
                        statement.bindLong(2, app.getFormId());

                        if (app.getFormName() != null)
                            statement.bindString(3, app.getFormName());
                        else
                            statement.bindNull(3);

                        statement.bindLong(4, app.getSiteId());

                        if (app.getFormName() != null)
                            statement.bindString(5, app.getStatus());
                        else
                            statement.bindNull(5);

                        statement.bindString(6, app.isInsert() ? "1" : "0");

                        ret = statement.executeInsert();
                        statement.clearBindings();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storeBulkFormSites");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public void updateAppTypeAndApproval(FormsData form) {
        database.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_appType, form.getAppType());
        values.put(KEY_approvalRequired, form.getApprovalRequired());

        String whereClause = KEY_formId + " = ?";
        String[] whereArgs = new String[]{form.getFormId() + ""};
        int ret = database.update(DbAccess.TABLE_FORM_SITES, values,
                whereClause, whereArgs);
        if (ret > 0)
            Log.e("AppType", "Form data updated");
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public boolean isAppTypeSoilLog(String formId, String siteId) {
        boolean isTypeSoilLog = false;

        String query = "select formId from FormSites where formId = " + formId +
                " and siteId = " + siteId + " and lower(appType) = 'soil log'";

        database.beginTransaction();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            isTypeSoilLog = cursor.getCount() > 0;
            cursor.close();
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        return isTypeSoilLog;
    }

    public boolean isAppTypeNoLoc(String formId, String siteId) {
        boolean isTypeSoilLog = false;

        String query = "select formId from FormSites where formId = " + formId +
                " and siteId = " + siteId + " and lower(appType) = 'no_loc'";

        database.beginTransaction();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            isTypeSoilLog = cursor.getCount() > 0;
            cursor.close();
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        return isTypeSoilLog;
    }
}
