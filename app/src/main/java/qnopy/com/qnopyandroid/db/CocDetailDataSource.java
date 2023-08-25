package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 3/16/2018.
 */

public class CocDetailDataSource {
    private static final String TAG = "CocDetail ";

    String KEY_CocDetailID = "coc_details_id";
    String KEY_CocID = "coc_id";
    String KEY_LocationID = "location_id";
    String KEY_MethodID = "method_id";
    String KEY_WorkOrderID = "wo_id";
    String KEY_WorkOrderTaskID = "wo_task_id";
    String KEY_CreationDate = "creation_date";
    String KEY_CreatedBy = "created_by";
    String KEY_ModificationDate = "modification_date";
    String KEY_ModifiedBy = "modified_by";
    String KEY_ModificationNotes = "modification_notes";
    String KEY_CocRemark = "coc_remarks";
    String KEY_Preservatives = "preservatives";
    String KEY_Container = "container";
    String KEY_Method = "method";
    String KEY_SampleID = "sample_id";
    String KEY_SampleDate = "sample_date";
    String KEY_SampleTime = "sample_time";
    String KEY_DeleteFlag = "delete_flag";
    String KEY_Matrix = "matrix";
    String KEY_Status = "status";
    String KEY_CocFlag = "coc_flag";
    String KEY_ServerCreationDate = "server_creation_date";
    String KEY_ServerModificationDate = "server_modification_date";
    String KEY_field_parameter_id = "field_parameter_id";
    String KEY_dup_flag = "dup_flag";


    Context mContext;
    public SQLiteDatabase database;

    public CocDetailDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public void truncateCocDetail() {

        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();

            try {
                ret = database.delete(DbAccess.TABLE_CM_COC_DETAILS, null, null);
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

    public int storeBulkCoCDetails(List<SCocDetails> cocDetailsList) {
        int ret = 0;

        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (SCocDetails cocDetails : cocDetailsList) {

                values.put(KEY_LocationID, cocDetails.getLocationId());
                values.put(KEY_SampleID, cocDetails.getSampleId());
                values.put(KEY_SampleDate, cocDetails.getSampleDate());
                values.put(KEY_SampleTime, cocDetails.getSampleTime());
                values.put(KEY_Preservatives, cocDetails.getPreservatives());
                values.put(KEY_Container, cocDetails.getContainer());
                values.put(KEY_Method, cocDetails.getMethod());
                values.put(KEY_MethodID, cocDetails.getMethodId());
                values.put(KEY_CocDetailID, cocDetails.getCocDetailsId());
                values.put(KEY_DeleteFlag, cocDetails.getDeleteFlag());
                values.put(KEY_WorkOrderTaskID, cocDetails.getWoTaskIds());
                values.put(KEY_CreationDate, cocDetails.getCreationDate());
                values.put(KEY_ModificationDate, cocDetails.getModificationDate());
                values.put(KEY_CreatedBy, cocDetails.getCreatedBy());
                values.put(KEY_ModifiedBy, cocDetails.getModifiedBy());
                values.put(KEY_ServerCreationDate, cocDetails.getServerCreationDate());
                values.put(KEY_ServerModificationDate, cocDetails.getModificationDate());
                values.put(KEY_Status, cocDetails.getStatus());
                values.put(KEY_CocFlag, cocDetails.getCocFlag());
                values.put(KEY_CocID, cocDetails.getCocId());
                values.put(KEY_field_parameter_id, cocDetails.getFieldParameterId());
                values.put(KEY_dup_flag, cocDetails.getDupFlag());

                ret = (int) database.insert(DbAccess.TABLE_CM_COC_DETAILS, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertNewCoCdetail() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int storeBulkBindCoCDetails(List<SCocDetails> cocDetailsList) {
        int ret = 0;

        String[] arrColumns = {KEY_LocationID, KEY_SampleID, KEY_SampleDate,
                KEY_SampleTime, KEY_Preservatives, KEY_Container, KEY_Method,
                KEY_MethodID, KEY_CocDetailID, KEY_DeleteFlag, KEY_WorkOrderTaskID, KEY_CreationDate,
                KEY_ModificationDate, KEY_CreatedBy, KEY_ModifiedBy, KEY_ServerCreationDate,
                KEY_ServerModificationDate, KEY_Status, KEY_CocFlag, KEY_CocID,
                KEY_field_parameter_id, KEY_dup_flag};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_CM_COC_DETAILS + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {
            for (SCocDetails cocDetails : cocDetailsList) {

                if (cocDetails.getLocationId() != null)
                    statement.bindLong(1, cocDetails.getLocationId());
                else
                    statement.bindNull(1);

                if (cocDetails.getSampleId() != null)
                    statement.bindString(2, cocDetails.getSampleId());
                else
                    statement.bindNull(2);

                if (cocDetails.getSampleDate() != null)
                    statement.bindString(3, cocDetails.getSampleDate());
                else statement.bindNull(3);

                if (cocDetails.getSampleTime() != null)
                    statement.bindString(4, cocDetails.getSampleTime());
                else
                    statement.bindNull(4);

                if (cocDetails.getPreservatives() != null)
                    statement.bindString(5, cocDetails.getPreservatives());
                else
                    statement.bindNull(5);

                if (cocDetails.getContainer() != null)
                    statement.bindString(6, cocDetails.getContainer());
                else
                    statement.bindNull(6);

                if (cocDetails.getMethod() != null)
                    statement.bindString(7, cocDetails.getMethod());
                else
                    statement.bindNull(7);

                if (cocDetails.getMethodId() != null)
                    statement.bindLong(8, cocDetails.getMethodId());
                else
                    statement.bindNull(8);

                if (cocDetails.getCocDetailsId() != null)
                    statement.bindString(9, cocDetails.getCocDetailsId());
                else
                    statement.bindNull(9);

                if (cocDetails.getDeleteFlag() != null)
                    statement.bindLong(10, cocDetails.getDeleteFlag());
                else
                    statement.bindNull(10);

                if (cocDetails.getWoTaskIds() != null)
                    statement.bindLong(11, cocDetails.getWoTaskIds());
                else
                    statement.bindNull(11);

                if (cocDetails.getCreationDate() != null)
                    statement.bindLong(12, cocDetails.getCreationDate());
                else
                    statement.bindNull(12);

                if (cocDetails.getModificationDate() != null)
                    statement.bindLong(13, cocDetails.getModificationDate());
                else
                    statement.bindNull(13);

                if (cocDetails.getCreatedBy() != null)
                    statement.bindLong(14, cocDetails.getCreatedBy());
                else
                    statement.bindNull(14);

                if (cocDetails.getModifiedBy() != null)
                    statement.bindLong(15, cocDetails.getModifiedBy());
                else
                    statement.bindNull(15);

                if (cocDetails.getServerCreationDate() != null)
                    statement.bindLong(16, cocDetails.getServerCreationDate());
                else
                    statement.bindNull(16);

                if (cocDetails.getModificationDate() != null)
                    statement.bindLong(17, cocDetails.getModificationDate());
                else
                    statement.bindNull(17);

                if (cocDetails.getStatus() != null)
                    statement.bindString(18, cocDetails.getStatus());
                else
                    statement.bindNull(18);

                if (cocDetails.getCocFlag() != null)
                    statement.bindLong(19, cocDetails.getCocFlag());
                else
                    statement.bindNull(19);

                if (cocDetails.getCocId() != null)
                    statement.bindLong(20, cocDetails.getCocId());
                else
                    statement.bindNull(20);

                if (cocDetails.getFieldParameterId() != null)
                    statement.bindLong(21, cocDetails.getFieldParameterId());
                else
                    statement.bindNull(21);

                if (cocDetails.getDupFlag() != null)
                    statement.bindLong(22, cocDetails.getDupFlag());
                else
                    statement.bindNull(22);

                ret = (int) statement.executeInsert();
                statement.clearBindings();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertNewBulkCoCDetail() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int insertNewCoCdetail(SCocDetails cocDetails) {
        int ret = 0;

        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_LocationID, cocDetails.getLocationId());
            values.put(KEY_SampleID, cocDetails.getSampleId());
            values.put(KEY_SampleDate, cocDetails.getSampleDate());
            values.put(KEY_SampleTime, cocDetails.getSampleTime());
            values.put(KEY_Preservatives, cocDetails.getPreservatives());
            values.put(KEY_Container, cocDetails.getContainer());
            values.put(KEY_Method, cocDetails.getMethod());
            values.put(KEY_MethodID, cocDetails.getMethodId());
            values.put(KEY_CocDetailID, cocDetails.getCocDetailsId());
            values.put(KEY_DeleteFlag, cocDetails.getDeleteFlag());
            values.put(KEY_WorkOrderTaskID, cocDetails.getWoTaskIds());
            values.put(KEY_CreationDate, cocDetails.getCreationDate());
            values.put(KEY_ModificationDate, cocDetails.getModificationDate());
            values.put(KEY_CreatedBy, cocDetails.getCreatedBy());
            values.put(KEY_ModifiedBy, cocDetails.getModifiedBy());
            values.put(KEY_ServerCreationDate, cocDetails.getServerCreationDate());
            values.put(KEY_ServerModificationDate, cocDetails.getModificationDate());
            values.put(KEY_Status, cocDetails.getStatus());
            values.put(KEY_CocFlag, cocDetails.getCocFlag());
            values.put(KEY_CocID, cocDetails.getCocId());
            values.put(KEY_field_parameter_id, cocDetails.getFieldParameterId());
            values.put(KEY_dup_flag, cocDetails.getDupFlag());

            ret = (int) database.insert(DbAccess.TABLE_CM_COC_DETAILS, null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertNewCoCdetail() error:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public List<SCocDetails> getDefaultMethodfromcocDetail(String currCocID, String locationID) {

        //    String query = null;
        String defaultValue = null;
        List<SCocDetails> details = new ArrayList<>();

        String query = "select distinct method_id as cm_methods_id, method as methods " +
                "from cm_coc_details where coc_id =" + currCocID + " and location_id ="
                + locationID + " and delete_flag = 0 and (method!=null or method!='')";

        Log.i(TAG, "getDefaultMethodfromcocDetail() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SCocDetails cocDetails = new SCocDetails();
                    cocDetails.setMethodId(cursor.getInt(0));
                    cocDetails.setMethod(cursor.getString(1));
                    details.add(cocDetails);

                    Log.i(TAG, "DefaultValue:" + defaultValue);

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in coc default :" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return details;
    }

    public String getSampleID_from_cocDetail(String currCocID, String locationID, String fieldParamID) {

        //    String query = null;
        String sampleIDValue = null;
        List<SCocDetails> details = new ArrayList<>();

        if (currCocID == null)
            return null;

        String query = "select sample_id " +
                "from cm_coc_details where coc_id =" + currCocID + " and location_id ="
                + locationID + " and field_parameter_id =" + fieldParamID;

        Log.i(TAG, "getSampleID_from_cocDetail() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                sampleIDValue = cursor.getString(0);
                cursor.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in coc default :" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return sampleIDValue;
    }

    public int getMethodIDForMethods(String methodName) {

        //    String query = null;
        String defaultValue = null;
        int methodID = 0;

        String query = "select cm_methods_id,methods from cm_methods where methods like '" + methodName + "'";
        Log.i(TAG, "query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                methodID = cursor.getInt(0);
                cursor.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in coc default :" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return methodID;
    }

    public String getCOCFlagForLocation(String locId) {

        String cocFlag = "0";

        String query = "select distinct location_id, coc_flag from cm_coc_details where location_id = " + locId + " and coc_flag = 1";
        Log.i(TAG, "query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                cocFlag = cursor.getString(0);
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in coc default :" + e.getLocalizedMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        return cocFlag;
    }

    public void updateSyncedCoc(List<SCocDetails> list) {
        int ret = 0;


        for (SCocDetails sitem : list) {
            ContentValues values = new ContentValues();
            values.put(KEY_CocDetailID, sitem.getCocDetailsId());
            values.put(KEY_ServerCreationDate, sitem.getServerCreationDate());
            values.put(KEY_ServerModificationDate, sitem.getServerModificationDate());

            String whereClause = null;
            String[] whereArgs = null;
            if (sitem.getMethodId() != 0) {
                whereClause = KEY_CocID + "=? and " + KEY_LocationID + "= ? and " + KEY_MethodID + "=?";
                whereArgs = new String[]{sitem.getCocId() + "", sitem.getLocationId() + "", sitem.getMethodId() + ""};

            } else {
                whereClause = KEY_CocID + "=? and " + KEY_LocationID + "= ?";
                whereArgs = new String[]{sitem.getCocId() + "", sitem.getLocationId() + ""};
            }

            try {
                ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, values, whereClause, whereArgs);
                Log.i(TAG, "updateSyncedCoc count:" + ret);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "updateSyncedCoc Exception:" + e.getLocalizedMessage());
            }
        }

    }


    public void deleteCocMethods(String cocID, String locationID, String methodID) {
        int ret = 0;


        ContentValues values = new ContentValues();
        values.put(KEY_DeleteFlag, 1);


        String whereClause = KEY_CocID + "=? and " + KEY_LocationID + "= ? and " + KEY_MethodID + "=?";
        String[] whereArgs = new String[]{cocID, locationID, methodID};
        try {
            ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, values, whereClause, whereArgs);
            Log.i(TAG, "deleteMethodsCoc() count:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteMethodsCoc() Exception:" + e.getLocalizedMessage());
        }
    }

    public void deleteAllCocMethodsForCoCAndLocationID(String cocID, String locationID) {
        int ret = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_DeleteFlag, 1);

        String whereClause = KEY_CocID + "=? and " + KEY_LocationID + "= ? ";
        String[] whereArgs = new String[]{cocID, locationID};
        try {
            ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, values, whereClause, whereArgs);
            Log.i(TAG, "deleteAllCocMethodsForCoCAndLocationID() count:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteAllCocMethodsForCoCAndLocationID() Exception:" + e.getLocalizedMessage());
        }
    }


    public void updateDeleteFlag(String cocID, String LocationID, String methodID, String userID) {
        int ret = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_ModificationDate, System.currentTimeMillis());
        values.put(KEY_ModifiedBy, userID);
        values.put(KEY_DeleteFlag, 0);

        String whereClause = KEY_CocID + "=? and " + KEY_LocationID + "= ? and " + KEY_MethodID + "=?";
        String[] whereArgs = new String[]{cocID, LocationID, methodID};
        try {
            ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, values, whereClause, whereArgs);
            Log.i(TAG, "updateCocMethods() count:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateCocMethods() Exception:" + e.getLocalizedMessage());
        }
    }

    public boolean isCocAndLocationPresentAlready(String cocID, String locID) {
        int count = 0;

        String query = null;
        String[] whereClause = null;

        whereClause = new String[]{};
        Cursor cursor = null;
        try {
            query = "select count(*) from cm_coc_details where location_id=" + locID + " and coc_id=" + cocID;
            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isLocationSampledAlready() exception:" + e.getLocalizedMessage());
            return false;
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    public boolean isCocMethodPresentAlready(String cocID, String locID, String methodID) {
        int count = 0;

        String query = null;
        String[] whereClause = null;

        whereClause = new String[]{};
        Cursor cursor = null;
        try {
            query = "select count(*) from cm_coc_details where location_id=" + locID + " and coc_id="
                    + cocID + " and method_id=" + methodID;
            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isLocationSampledAlready() exception:" + e.getLocalizedMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    public boolean isCocMethodPresentAlready(String cocID, String locID, String methodID, int dupFlag) {
        int count = 0;

        String query = null;
        String[] whereClause = null;

        whereClause = new String[]{};
        Cursor cursor = null;
        try {
            query = "select count(*) from cm_coc_details where location_id=" + locID + " and coc_id="
                    + cocID + " and method_id=" + methodID + " and dup_flag = " + dupFlag;

            if (dupFlag == 0)
                query = "select count(*) from cm_coc_details where location_id=" + locID + " and coc_id="
                        + cocID + " and method_id=" + methodID + " and (dup_flag = " + dupFlag
                        + " or dup_flag is null)";

            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isLocationSampledAlready() exception:" + e.getLocalizedMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    public String[] getSampleDetailsForCOCID(String cocID, String locID) {
        String[] sampleDetails = new String[3];
        String sampleID, sampleDate, sampleTime;
        String query = null;
        String[] whereClause = null;

        whereClause = new String[]{};
        Cursor cursor = null;
        try {
            query = "select sample_id,sample_date,sample_time FROM cm_coc_details where " +
                    "coc_id=" + cocID + " and location_id=" + locID + " and (sample_id !=null or sample_id !='')";
            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                sampleID = cursor.getString(0);
                sampleDate = cursor.getString(1);
                sampleTime = cursor.getString(2);
                cursor.close();

                sampleDetails[0] = sampleID;
                sampleDetails[1] = sampleDate;
                sampleDetails[2] = sampleTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isLocationSampledAlready() exception:" + e.getLocalizedMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return sampleDetails;
    }

    public ArrayList<SCocDetails> getAllLabelsToPrint(String cocID, ArrayList<String> locIDs) {

        String locationIds = StringUtils.join(locIDs, ",");

        ArrayList<SCocDetails> cocLabels = new ArrayList<>();

        String query = "select distinct m.cm_methods_id, m.methods, m.container, m.preservative, " +
                "c.location_id, c.coc_id, m.analyses, m.noOfContainer, c.sample_id, c.sample_date, " +
                "c.sample_time, l.Location from cm_coc_details c inner JOIN cm_methods m " +
                "on c.method_id = m.cm_methods_id INNER JOIN s_Location l " +
                "on c.location_id = l.LocationID where c.location_id in (" + locationIds + ") " +
                "and c.coc_id = " + cocID;

        Log.i(TAG, "getAllLabelsToPrint() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SCocDetails cocDetails = new SCocDetails();
                    cocDetails.setMethodId(cursor.getInt(0));
                    cocDetails.setMethod(cursor.getString(1));
                    cocDetails.setContainer(cursor.getString(2));
                    cocDetails.setPreservatives(cursor.getString(3));
                    cocDetails.setLocationId(cursor.getLong(4));
                    cocDetails.setCocId(cursor.getInt(5));
                    cocDetails.setAnalysis(cursor.getString(6));
                    cocDetails.setNoOfContainer(cursor.getString(7));
                    cocDetails.setSampleId(cursor.getString(8));
                    cocDetails.setSampleDate(cursor.getString(9));
                    cocDetails.setSampleTime(cursor.getString(10));
                    cocDetails.setLocationName(cursor.getString(11));
                    cocLabels.add(cocDetails);

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in getCOCLabels :" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cocLabels;
    }

    public ArrayList<SCocDetails> getCOCMethodsForLocation(String cocID, String locID, boolean isDupSampleId) {

        ArrayList<SCocDetails> cocMethods = new ArrayList<>();

        String query = "select distinct sample_id, sample_date, sample_time, method_id, method, delete_flag from cm_coc_details " +
                "where coc_id = " + cocID + " and location_id = " + locID + " and (method!=null or method!='') ";

        if (isDupSampleId) {
            query = "select distinct sample_id, sample_date, sample_time, method_id, method, delete_flag from cm_coc_details " +
                    "where coc_id = " + cocID + " and location_id = " + locID + " and (method!=null or method!='') and " +
                    "dup_flag = 1 ";
        }

        Log.i(TAG, "getCOCMethodsForLocation() query=" + query);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SCocDetails cocDetails = new SCocDetails();
                    cocDetails.setSampleId(cursor.getString(0));
                    cocDetails.setSampleDate(cursor.getString(1));
                    cocDetails.setSampleTime(cursor.getString(2));
                    cocDetails.setMethodId(cursor.getInt(3));
                    cocDetails.setMethod(cursor.getString(4));
                    cocDetails.setDeleteFlag(cursor.getInt(5));
                    cocMethods.add(cocDetails);

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error in getCOCMethodsForLocation :" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cocMethods;
    }

    public void updateSampleID(String date, String time, String sampleID, String cocID, String locID, String userID) {

        int ret = 0;

        //19-03-2018 UPDATE SAMPLE ID IN CoCDetails
        ContentValues values = new ContentValues();
        values.put(KEY_SampleID, sampleID);
        values.put(KEY_SampleDate, date);
        values.put(KEY_SampleTime, time);
        values.put(KEY_ModificationDate, System.currentTimeMillis() + "");
        values.put(KEY_ModifiedBy, userID);
        values.put(KEY_Status, "COMPLETED");
        values.put(KEY_ServerCreationDate, "-1");

        String whereClause = "location_id = ? and coc_id = ?";
        String[] whereArgs = new String[]{locID, cocID};
        try {
            ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, values, whereClause, whereArgs);
            Log.i(TAG, "updateSampleID() update sample id count:" + ret);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateSampleID() update Exception:" + e.getLocalizedMessage());
        }
    }

    public void updateSampleID(String date, String time, String sampleID, String cocID,
                               String locID, String userID, String methodId, int dupFlag) {

        int ret = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_SampleID, sampleID);
        values.put(KEY_SampleDate, date);
        values.put(KEY_SampleTime, time);
        values.put(KEY_ModificationDate, System.currentTimeMillis() + "");
        values.put(KEY_ModifiedBy, userID);
        values.put(KEY_Status, "COMPLETED");
        values.put(KEY_DeleteFlag, "0");
        values.put(KEY_ServerCreationDate, "-1");

        String whereClause = "location_id = ? and coc_id = ? and method_id = ? and (dup_flag = ? or dup_flag is null)";

        if (dupFlag == 1) {
            whereClause = "location_id = ? and coc_id = ? and method_id = ? and dup_flag = ?";
        }

        String[] whereArgs = new String[]{locID, cocID, methodId, dupFlag + ""};

        try {
            ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, values, whereClause, whereArgs);
            Log.i(TAG, "updateSampleID() update sample id count:" + ret);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateSampleID() update Exception:" + e.getLocalizedMessage());
        }
    }


}
