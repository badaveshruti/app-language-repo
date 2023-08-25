package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;
import qnopy.com.qnopyandroid.responsemodel.CocObjectModel;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 3/16/2018.
 */

public class CocMasterDataSource {
    private static final String TAG = "CocMaster ";

    String KEY_CocID = "coc_id";
    String KEY_CocDisplayID = "coc_display_id";
    String KEY_SiteID = "site_id";
    String KEY_ShipDate = "ship_date";
    String KEY_CreationDate = "creation_date";
    String KEY_ModifiedBy = "modified_by";
    String KEY_CreatedBy = "created_by";
    String KEY_ModificationDate = "modification_date";
    String KEY_CocSetupID = "coc_setup_id";
    String KEY_SpecialInstruction = "special_instruction";
    String KEY_Status = "status";
    String KEY_FormID = "form_id";
    String KEY_ClientCOCID = "client_coc_id";
    String KEY_eventId = "eventId";

    Context mContext;
    public SQLiteDatabase database;

    public CocMasterDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public void truncateCocMaster() {

        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }
        try {
            database.beginTransaction();
            try {
                ret = database.delete(DbAccess.TABLE_CM_COC_MASTER, null, null);
                Log.i(TAG, "deleted table:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Caught for Table name=" + ret);
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

    public int insertNewCoCMaster(SCocMaster cocMasters) {
        int ret = 0;

        database.beginTransaction();

        try {

            ContentValues values = new ContentValues();

            values.put(KEY_CocID, cocMasters.getCocId());
            values.put(KEY_CocDisplayID, cocMasters.getCocDisplayId());
            values.put(KEY_SiteID, cocMasters.getSiteId());
            values.put(KEY_ShipDate, cocMasters.getShipDate());
            values.put(KEY_CocSetupID, cocMasters.getCocSetupId());
            values.put(KEY_FormID, cocMasters.getFormId());
            values.put(KEY_CreationDate, cocMasters.getCreationDate());
            values.put(KEY_ModificationDate, cocMasters.getModificationDate());
            values.put(KEY_CreatedBy, cocMasters.getCreatedBy());
            values.put(KEY_ModifiedBy, cocMasters.getModifiedBy());
            values.put(KEY_ClientCOCID, cocMasters.getClientCocId());
            values.put(KEY_Status, cocMasters.getStatus());
            values.put(KEY_eventId, cocMasters.getEventId());

            ret = (int) database.insert(DbAccess.TABLE_CM_COC_MASTER, null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertNewCoCMaster() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int storeBulkCoCMaster(List<SCocMaster> cocMasterList) {
        int ret = 0;

        database.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            for (SCocMaster cocMasters : cocMasterList) {

                values.put(KEY_CocID, cocMasters.getCocId());
                values.put(KEY_CocDisplayID, cocMasters.getCocDisplayId());
                values.put(KEY_SiteID, cocMasters.getSiteId());
                values.put(KEY_ShipDate, cocMasters.getShipDate());
                values.put(KEY_CocSetupID, cocMasters.getCocSetupId());
                values.put(KEY_FormID, cocMasters.getFormId());
                values.put(KEY_CreationDate, cocMasters.getCreationDate());
                values.put(KEY_ModificationDate, cocMasters.getModificationDate());
                values.put(KEY_CreatedBy, cocMasters.getCreatedBy());
                values.put(KEY_ModifiedBy, cocMasters.getModifiedBy());
                values.put(KEY_ClientCOCID, cocMasters.getClientCocId());
                values.put(KEY_Status, cocMasters.getStatus());
                values.put(KEY_eventId, cocMasters.getEventId());

                ret = (int) database.insert(DbAccess.TABLE_CM_COC_MASTER, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertNewCoCMaster() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int storeBulkBindCoCMaster(List<SCocMaster> cocMasterList) {
        int ret = 0;
        String[] arrColumns = {KEY_CocID, KEY_CocDisplayID, KEY_SiteID,
                KEY_ShipDate, KEY_CocSetupID, KEY_FormID, KEY_CreationDate,
                KEY_ModificationDate, KEY_CreatedBy, KEY_ModifiedBy, KEY_ClientCOCID, KEY_Status, KEY_eventId};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_CM_COC_MASTER + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {
            for (SCocMaster cocMasters : cocMasterList) {

                if (cocMasters.getCocId() != null)
                    statement.bindLong(1, cocMasters.getCocId());
                else
                    statement.bindNull(1);

                if (cocMasters.getCocDisplayId() != null)
                    statement.bindString(2, cocMasters.getCocDisplayId());
                else
                    statement.bindNull(2);

                if (cocMasters.getSiteId() != null)
                    statement.bindLong(3, cocMasters.getSiteId());
                else
                    statement.bindNull(3);

                if (cocMasters.getShipDate() != null)
                    statement.bindLong(4, cocMasters.getShipDate());
                else
                    statement.bindNull(4);

                if (cocMasters.getCocSetupId() != null)
                    statement.bindLong(5, cocMasters.getCocSetupId());
                else
                    statement.bindNull(5);

                if (cocMasters.getFormId() != null)
                    statement.bindLong(6, cocMasters.getFormId());
                else
                    statement.bindNull(6);

                if (cocMasters.getCreationDate() != null)
                    statement.bindLong(7, cocMasters.getCreationDate());
                else
                    statement.bindNull(7);

                if (cocMasters.getModificationDate() != null)
                    statement.bindLong(8, cocMasters.getModificationDate());
                else
                    statement.bindNull(8);

                if (cocMasters.getCreatedBy() != null)
                    statement.bindLong(9, cocMasters.getCreatedBy());
                else
                    statement.bindNull(9);

                if (cocMasters.getModifiedBy() != null)
                    statement.bindLong(10, cocMasters.getModifiedBy());
                else
                    statement.bindNull(10);

                if (cocMasters.getClientCocId() != null)
                    statement.bindString(11, cocMasters.getClientCocId());
                else
                    statement.bindNull(11);

                if (cocMasters.getStatus() != null)
                    statement.bindString(12, cocMasters.getStatus());
                else
                    statement.bindNull(12);

                if (cocMasters.getEventId() != null)
                    statement.bindLong(13, cocMasters.getEventId());
                else
                    statement.bindNull(13);

                ret = (int) statement.executeInsert();
                statement.clearBindings();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertNewCoCMaster() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public List<CocObjectModel> getallCoCMasterData() {
        List<CocObjectModel> listData = new ArrayList<>();
        SCocMaster scocMaster = new SCocMaster();
        List<SCocDetails> sCocDetailsList = new ArrayList<>();
        SCocDetails sdetails = new SCocDetails();
        String[] whereArgs = null;

        Long locationId;
        String sampleId;
        String sampleDate;
        String sampleTime;
        String method;
        int methodId;
        int deleteFlag;
        long creationDate;
        long modificationDate;
        int createdBy;
        int modifiedBy;
        String status;
        int cocId, samplefpId = 0, dupFlag = 0;

        ArrayList<Integer> syncablecoc = getSyncableCOCID();
        if (syncablecoc.size() > 0) {
            for (int cocID : syncablecoc) {

                String query = "select coc_id,method,method_id,location_id,status,delete_flag,sample_date,sample_time,sample_id," +
                        "modification_date,modified_by,created_by,creation_date,field_parameter_id,dup_flag from cm_coc_details " +
                        "where coc_id =" + cocID + " and server_creation_date=-1 and status='COMPLETED'";

                Log.i(TAG, "getallCoCMasterData() query=" + query);
                Cursor cursor = null;
                TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);
                LogDetails logDetails = new LogDetails();
                logDetails.setAllIds("");
                logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                        GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));

                try {
                    cursor = database.rawQuery(query, whereArgs);

                    logDetails.setScreenName("Get all coc master data query");

                    boolean cursorHasData = cursor != null && !cursor.isClosed();
                    boolean databaseConnection = database != null && database.isOpen();
                    logDetails.setDetails("Checking cursor and database instance: Database: "
                            + databaseConnection + " Cursor: " + cursorHasData);

                    tempLogsDataSource.insertTempLogs(logDetails);

                    if (cursor != null && cursor.moveToFirst()) {

                        do {
                            cocId = cursor.getInt(0);
                            method = cursor.getString(1);
                            methodId = cursor.getInt(2);
                            locationId = cursor.getLong(3);
                            status = cursor.getString(4);
                            deleteFlag = cursor.getInt(5);
                            sampleDate = cursor.getString(6);
                            sampleTime = cursor.getString(7);
                            sampleId = cursor.getString(8);
                            modificationDate = cursor.getLong(9);
                            modifiedBy = cursor.getInt(10);
                            createdBy = cursor.getInt(11);
                            creationDate = cursor.getLong(12);
                            samplefpId = cursor.getInt(13);
                            dupFlag = cursor.getInt(14);

                            sdetails = new SCocDetails();

                            sdetails.setCocId(cocId);
                            sdetails.setMethod(method);
                            sdetails.setMethodId(methodId);
                            sdetails.setLocationId(locationId);
                            sdetails.setFieldParameterId(samplefpId);
                            sdetails.setDupFlag(dupFlag);
                            sdetails.setStatus(status);
                            sdetails.setDeleteFlag(deleteFlag);
                            sdetails.setSampleDate(sampleDate);
                            sdetails.setSampleTime(sampleTime);
                            sdetails.setSampleId(sampleId);
                            sdetails.setModificationDate(modificationDate);
                            sdetails.setModifiedBy(modifiedBy);
                            sdetails.setCreatedBy(createdBy);
                            sdetails.setCreationDate(creationDate);

                            sCocDetailsList.add(sdetails);

                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "getallCoCMasterData() query failed msg=" + e.getLocalizedMessage());

                    logDetails.setScreenName("Get all coc master data query exception");
                    logDetails.setDetails("Exception: " + e);

                    tempLogsDataSource.insertTempLogs(logDetails);

                } finally {
                    if (!cursor.isClosed()) {
                        cursor.close();
                        cursor = null;
                    }
                }

                scocMaster = getCoCMasterDataForCOCID(cocID + "");
                CocObjectModel syncOB = new CocObjectModel();
                syncOB.setsCocDetails(sCocDetailsList);
                syncOB.setsCocMaster(scocMaster);
                listData.add(syncOB);
            }
        }

        return listData;
    }

    public ArrayList<Integer> getSyncableCOCID() {

        ArrayList<Integer> cocIDlist = new ArrayList<>();

        int cocId;

        String[] whereArgs = null;


        String query = "select DISTINCT coc_id from cm_coc_details where server_creation_date=-1 and status='COMPLETED'";
        Log.i(TAG, "getSyncableCOCID() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    cocId = cursor.getInt(0);
                    cocIDlist.add(cocId);

                } while (cursor.moveToNext());

                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getCoCMasterDataForCOCID() query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return cocIDlist;
    }

    public SCocMaster getCoCMasterDataForCOCID(String coc_ID) {

        SCocMaster scocMaster = new SCocMaster();

        int cocId, siteId, formId, createdBy;
        String cocDisplayId, status, clientCocId;
        long creationDate;

        String[] whereArgs = null;


        String query = "SELECT coc_id,coc_display_id,client_coc_id,form_id,site_id,status," +
                "creation_date,created_by, eventId from cm_coc_master " +
                "where coc_id IN(" + coc_ID + ")";

        Log.i(TAG, "getCoCMasterDataForCOCID() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);

            if (cursor != null && cursor.moveToFirst()) {


                cocId = cursor.getInt(0);
                cocDisplayId = cursor.getString(1);
                clientCocId = cursor.getString(2);
                formId = cursor.getInt(3);
                siteId = cursor.getInt(4);
                status = cursor.getString(5);
                creationDate = cursor.getLong(6);
                createdBy = cursor.getInt(7);

                scocMaster.setClientCocId(clientCocId);
                scocMaster.setCocDisplayId(cocDisplayId);
                scocMaster.setFormId(formId);
                scocMaster.setSiteId(siteId);
                scocMaster.setCocId(cocId);
                scocMaster.setStatus(status);
                scocMaster.setCreationDate(creationDate);
                scocMaster.setCreatedBy(createdBy);
                scocMaster.setEventId(cursor.getInt(8));

                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getCoCMasterDataForCOCID() query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return scocMaster;
    }

    public ArrayList<SCocMaster> getCoCListForSelectedEvent(String eventId) {

        ArrayList<SCocMaster> listCOC = new ArrayList<>();

        String query = "select coc_id, coc_display_id from cm_coc_master where eventId = " + eventId;

        Log.i(TAG, "getCOCListForSelectedEvent() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SCocMaster scocMaster = new SCocMaster();
                    scocMaster.setCocId(cursor.getInt(0));
                    scocMaster.setCocDisplayId(cursor.getString(1));
                    listCOC.add(scocMaster);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getCOCListForSelectedEvent() query failed msg=" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return listCOC;
    }

}
