package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.clientmodel.metaForms.Fields;
import qnopy.com.qnopyandroid.requestmodel.SMetaData;
import qnopy.com.qnopyandroid.responsemodel.newFormLabelResponse;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class MetaDataSource {

    private static final String TAG = "MetaDataSource";
    final String KEY_SiteID = "SiteID";
    final String KEY_MobileAppID = "MobileAppID";
    final String KEY_LocationID = "LocationID";
    final String KEY_FieldParameterID = "FieldParameterID";
    final String KEY_ParameterLabel = "ParameterLabel";
    final String KEY_RowOrder = "RowOrder";
    final String KEY_ColumnOrder = "ColumnOrder";
    final String KEY_FieldInputType = "FieldInputType";
    final String KEY_NameValuePair = "NameValuePair";
    final String KEY_DesiredUnits = "DesiredUnits";
    final String KEY_ValueType = "ValueType";
    final String KEY_Required_Y_N = "Required_Y_N";
    final String KEY_MandatoryField = "mandatoryField";
    final String KEY_LOCATION_IDS = "locationIds";
    final String KEY_Warning_high = "Warning_high";
    final String KEY_Warning_Low = "Warning_Low";
    final String KEY_high_limit = "high_limit";
    final String KEY_Low_limit = "Low_limit";
    final String KEY_LovID = "LovID";

    final String KEY_default_value = "default_value";
    final String KEY_ext_field1 = "ext_field1";
    final String KEY_ext_field2 = "ext_field2";
    final String KEY_ext_field3 = "ext_field3";
    final String KEY_ext_field4 = "ext_field4";
    final String KEY_ext_field5 = "ext_field5";
    final String KEY_ext_field6 = "ext_field6";
    final String KEY_ext_field7 = "ext_field7";
    final String KEY_field_parameter_operands = "field_parameter_operands";
    final String KEY_enable_parameter_notes = "enable_parameter_notes";
    final String KEY_showLast2 = "showLast2";
    final String KEY_standard_app = "standard_app";
    final String KEY_label_width = "label_width";
    final String KEY_object_height = "object_height";
    final String KEY_object_width = "object_width";
    final String KEY_parameter_hint = "parameter_hint";
    final String KEY_parent_parameter_id = "parent_parameter_id";
    final String KEY_percent_difference = "percent_difference";
    final String KEY_routine_id = "routine_id";

    final String KEY_Notes = "Notes";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModifiedDate = "ModifiedDate";
    final String KEY_Createdby = "Createdby";

    final String KEY_MULTINOTE = "multinote";
    final String KEY_enableParameterTasks = "enableParameterTasks";
    final String KEY_straight_difference = "straight_difference";
    final String KEY_field_action = "field_action";
    final String KEY_field_score = "field_score";
    final String KEY_font_style = "font_style";

//    final String KEY_Reviewer = "Reviewer";

    Context mContext;
    public SQLiteDatabase database;

    @Inject
    public MetaDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public List<MetaData> getMetaData(int MobileAppID, int SiteID,
                                      String locationID) {

        List<MetaData> dataList = new ArrayList<>();
        //st = new ArrayList<MetaData>();

        String[] metaColumns = new String[]{"FieldParameterID", "ParameterLabel",
                "RowOrder", "ColumnOrder", "FieldInputType", "NameValuePair", "DesiredUnits",
                "ValueType", "LovID", "Required_Y_N", "Warning_high", "Warning_Low",
                "high_limit", "Low_limit", "default_value", "ext_field1", "ext_field2", "ext_field3",
                "ext_field4", "ext_field5", "ext_field6", "ext_field7", "field_parameter_operands",
                "enable_parameter_notes", "showLast2", "standard_app", "label_width", "object_height",
                "object_width", "parameter_hint", "parent_parameter_id",
                "percent_difference", "routine_id", KEY_MandatoryField, KEY_LOCATION_IDS,
                KEY_straight_difference, KEY_field_action, KEY_field_score, KEY_font_style};
        //String whereClause = "SiteID=? AND MobileAppID=? AND LocationID=? and UPPER(ParameterLabel) != 'NOTES'";
        String whereClause = "MobileAppID=? and UPPER(ParameterLabel) != 'NOTES'";// and "+KEY_Reviewer+"!=1" ;

        String[] whereArgs = new String[]{"" + MobileAppID};

        String orderBy = "RowOrder";

        Cursor cursor = database.query(DbAccess.TABLE_META_DATA, metaColumns,
                whereClause, whereArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                MetaData data = cursorToMetaData(cursor);
                data.setCurrentFormID(MobileAppID);
                dataList.add(data);
            } while (cursor.moveToNext());

            cursor.close();
        }

        for (MetaData mitem : dataList) {
            //18-Jul-17 SET IS PARENT FIELD OR NOT
            mitem.isParentField = isFieldParentID(MobileAppID, mitem.getMetaParamID() + "");
            mitem.isChildField = isFieldChildID(MobileAppID, mitem.getMetaParamID() + "");
            mitem.childParamList = getchildparamIDList(MobileAppID, mitem.getMetaParamID() + "");
        }

        Collection<MetaData> list = Collections2.filter(dataList, new Predicate<MetaData>() {
            @Override
            public boolean apply(MetaData metaData) {
                return Arrays.asList(metaData.getLocationIds().split(",")).contains(locationID)
                        || metaData.getLocationIds().equals("0");
            }
        });

        if (!list.isEmpty())
            return Lists.newArrayList(list);
        else
            return dataList;
    }

    public List<MetaData> getMetaDataWithVisibleQueryOperands(int mobileAppID,
                                                              String locationID) {

        List<MetaData> dataList = new ArrayList<>();

        String query = "select distinct FieldParameterID, ParameterLabel, \n" +
                " RowOrder, ColumnOrder, FieldInputType, NameValuePair, DesiredUnits,\n" +
                " ValueType, LovID, Required_Y_N, Warning_high, Warning_Low,\n" +
                " high_limit, Low_limit, default_value, ext_field1, ext_field2, ext_field3,\n" +
                " ext_field4, ext_field5, ext_field6, ext_field7, field_parameter_operands,\n" +
                " enable_parameter_notes, showLast2, standard_app, label_width, object_height,\n" +
                " object_width, parameter_hint, parent_parameter_id,\n" +
                " percent_difference, routine_id, mandatoryField, locationIds,\n" +
                " straight_difference, field_action, field_score, font_style,\n" +
                " CASE WHEN field_parameter_operands like '%!!visible!!%' THEN 0 ELSE 1 END isVisible \n" +
                " from s_MetaData where MobileAppID = " + mobileAppID +
                " and UPPER(ParameterLabel) != 'NOTES' and (Status IS NULL or Status=1) order by RowOrder";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MetaData data = cursorToMetaData(cursor);
                data.setCurrentFormID(mobileAppID);

                data.isParentField = isFieldParentID(mobileAppID, data.getMetaParamID() + "");
                data.isChildField = isFieldChildID(mobileAppID, data.getMetaParamID() + "");
                data.childParamList = getchildparamIDList(mobileAppID, data.getMetaParamID() + "");
                dataList.add(data);
            } while (cursor.moveToNext());

            cursor.close();
        }

        Collection<MetaData> list = Collections2.filter(dataList, new Predicate<MetaData>() {
            @Override
            public boolean apply(MetaData metaData) {
                return Arrays.asList(metaData.getLocationIds().split(",")).contains(locationID)
                        || metaData.getLocationIds().equals("0");
            }
        });

        if (!list.isEmpty())
            return Lists.newArrayList(list);
        else
            return dataList;
    }

    public List<MetaData> getMetaDataForPercentageCalc(int MobileAppID, int SiteID,
                                                       String locationID) {

        List<MetaData> dataList = new ArrayList<>();

        String[] metaColumns = new String[]{"FieldParameterID", "ParameterLabel",
                "RowOrder", "ColumnOrder", "FieldInputType", "NameValuePair", "DesiredUnits",
                "ValueType", "LovID", "Required_Y_N", "Warning_high", "Warning_Low",
                "high_limit", "Low_limit", "default_value", "ext_field1", "ext_field2", "ext_field3",
                "ext_field4", "ext_field5", "ext_field6", "ext_field7", "field_parameter_operands",
                "enable_parameter_notes", "showLast2", "standard_app", "label_width", "object_height",
                "object_width", "parameter_hint", "parent_parameter_id",
                "percent_difference", "routine_id", KEY_MandatoryField, KEY_LOCATION_IDS,
                KEY_straight_difference, KEY_field_action, KEY_field_score, KEY_font_style};
        //String whereClause = "SiteID=? AND MobileAppID=? AND LocationID=? and UPPER(ParameterLabel) != 'NOTES'";
        String whereClause = "ValueType IS NOT NULL and (CASE WHEN FieldInputType IS NOT NULL " +
                "THEN FieldInputType NOT LIKE 'LABEL' ELSE FieldInputType = '' END) and MobileAppID=? and UPPER(ParameterLabel) != 'NOTES'";// and "+KEY_Reviewer+"!=1" ;

        String[] whereArgs = new String[]{"" + MobileAppID};

        String orderBy = "RowOrder";

        Cursor cursor = database.query(DbAccess.TABLE_META_DATA, metaColumns,
                whereClause, whereArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                MetaData data = cursorToMetaData(cursor);
                data.setCurrentFormID(MobileAppID);
                dataList.add(data);
            } while (cursor.moveToNext());

            cursor.close();
        }

        for (MetaData mitem : dataList) {
            //18-Jul-17 SET IS PARENT FIELD OR NOT
            mitem.isParentField = isFieldParentID(MobileAppID, mitem.getMetaParamID() + "");
            mitem.isChildField = isFieldChildID(MobileAppID, mitem.getMetaParamID() + "");
            mitem.childParamList = getchildparamIDList(MobileAppID, mitem.getMetaParamID() + "");
        }

        Collection<MetaData> list = Collections2.filter(dataList, new Predicate<MetaData>() {
            @Override
            public boolean apply(MetaData metaData) {
                return Arrays.asList(metaData.getLocationIds().split(",")).contains(locationID)
                        || metaData.getLocationIds().equals("0");
            }
        });

        if (list.size() > 0)
            return Lists.newArrayList(list);
        else
            return dataList;
    }


    //30-Nov-16
    public int getchildparamID(int mobileAppID, String parentID) {
        Cursor cursor = null;
        int childParamID = 0;

        String query = "Select distinct " + KEY_FieldParameterID + " from s_MetaData where MobileAppID = " + mobileAppID + " and " + KEY_parent_parameter_id + " = " + parentID;
        cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            childParamID = cursor.getInt(0);
            cursor.close();
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return childParamID;
    }

    //18-Jul-17 GET LIST OF CHILD IDS
    public List<Integer> getchildparamIDList(int mobileAppID, String parentID) {
        Cursor cursorl = null;
        int childParamID = 0;
        List<Integer> childList = new ArrayList<>();
        String query = "Select distinct " + KEY_FieldParameterID + " from s_MetaData " +
                " where MobileAppID = " + mobileAppID + " and " + KEY_parent_parameter_id + " = " + parentID;
        cursorl = database.rawQuery(query, null);
        if (cursorl != null && cursorl.moveToFirst()) {
            do {
                childParamID = cursorl.getInt(0);
                childList.add(childParamID);
                Log.i(TAG, "MobileApp:" + mobileAppID + ",Parent :" + parentID + " Child added: " + childParamID);
                Log.i("abhishekSpecies", "MobileApp:" + mobileAppID + ",Parent :" + parentID + " Child added: " + childParamID);
            } while (cursorl.moveToNext());
            cursorl.close();
        }

        if (cursorl != null && !cursorl.isClosed()) {
            cursorl.close();
        }
        return childList;
    }

    public List<Integer> getchildparamIDListWithFielParameterOperand(int mobileAppID, String parentID, String fieldParameterOperand) {
        Cursor cursorl = null;
        int childParamID = 0;
        List<Integer> childList = new ArrayList<>();
        String query = "Select distinct " + KEY_FieldParameterID + " from s_MetaData " +
                " where MobileAppID = " + mobileAppID + " and " + KEY_parent_parameter_id + " = " + parentID + " and " + KEY_field_parameter_operands + " = " + fieldParameterOperand;
        cursorl = database.rawQuery(query, null);
        if (cursorl != null && cursorl.moveToFirst()) {
            do {
                childParamID = cursorl.getInt(0);
                childList.add(childParamID);
                Log.i(TAG, "MobileApp:" + mobileAppID + ",Parent :" + parentID + " Child added: " + childParamID);
                Log.i("abhishekSpecies", "MobileApp:" + mobileAppID + ",Parent :" + parentID + " Child added: " + childParamID);
            } while (cursorl.moveToNext());
            cursorl.close();
        }

        if (cursorl != null && !cursorl.isClosed()) {
            cursorl.close();
        }
        return childList;
    }

    //18-Jul-17 CHECK  FIELD IS PARENT ID FOR OTHER
    public boolean isFieldParentID(int mobileAppID, String fieldID) {
        Cursor cursorp = null;
        int childParamID = 0;
        boolean result = false;
        String query = "Select distinct " + KEY_FieldParameterID + " from s_MetaData " +
                " where MobileAppID = " + mobileAppID + " and " + KEY_parent_parameter_id + " = " + fieldID;

        cursorp = database.rawQuery(query, null);
        if (cursorp != null && cursorp.moveToFirst()) {

            childParamID = cursorp.getInt(0);
            result = childParamID > 0;

            cursorp.close();
        }
        if (cursorp != null && !cursorp.isClosed()) {
            cursorp.close();
        }

        return result;
    }


    //18-Jul-17 CHECK  FIELD IS CHILD ID
    public boolean isFieldChildID(int mobileAppID, String fieldID) {
        Cursor cursorc = null;
        int childParamID = 0;
        boolean result = false;
        String query = "Select distinct " + KEY_FieldParameterID + " from s_MetaData " +
                " where MobileAppID = " + mobileAppID + " and " + KEY_FieldParameterID + " = " + fieldID + " and " + KEY_parent_parameter_id + " > 0 ";

        cursorc = database.rawQuery(query, null);
        if (cursorc != null && cursorc.moveToFirst()) {

            childParamID = cursorc.getInt(0);
            result = childParamID > 0;
            cursorc.close();
        }

        if (cursorc != null && !cursorc.isClosed()) {
            cursorc.close();
        }

        return result;
    }

    //03-May-17
    public int getTotalFieldParametersCount(int roll_into_app_ID, int siteID) {
        Cursor cursor = null;
        int totalCount = 0;

        String query = "select count(FieldParameterID) from s_MetaData where " +
                " MobileAppID IN (Select DISTINCT MobileAppID from s_SiteMobileApp where " +
                " roll_into_app_id=" + roll_into_app_ID + " and SiteID=" + siteID + ") " +
                " and FieldParameterID NOT IN (15,25)";
        cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            totalCount = cursor.getInt(0);
            cursor.close();
        }
        return totalCount;
    }

    public String getInputType(String fieldParamId) {
        Cursor cursor = null;
        String inputType = "";

        String query = "select FieldInputType from s_MetaData where " +
                "FieldParameterID = " + fieldParamId;
        cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            inputType = cursor.getString(0);
            cursor.close();
        }
        return inputType;
    }

    public int getRoll_Into_Form_Fields_Count(String siteID, String roll_into_app) {

        String query1;
        Cursor cursor2 = null;
        int res = 0;
        query1 = "select count(distinct fieldParameterId) total, b.SiteID, b.roll_into_app_id from s_MetaData a, s_SiteMobileApp b \n" +
                " where a.MobileAppID =b.MobileAppID " +
                " and b.SiteID=? " +
                " and b.roll_into_app_id=? " +
                " and a.FieldParameterID not in(15,25)\n" +
                " and a.FieldInputType NOT LIKE '' \n" +
                " and UPPER(a.ParameterLabel) NOT LIKE 'NOTES'";

        String[] args = new String[]{siteID, roll_into_app};
        try {
            cursor2 = database.rawQuery(query1, args);
            Log.i(TAG, "getRoll_Into_Form_Fields_Count() query=" + query1);
            if (cursor2 != null && cursor2.moveToFirst()) {
                res = Integer.parseInt(cursor2.getString(0));
                Log.i(TAG, "getRoll_Into_Form_Fields_Count() result=" + res);
                cursor2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getRoll_Into_Form_Fields_Count() Error=" + e.getMessage());

        } finally {
            if (cursor2 != null && !cursor2.isClosed()) {
                cursor2.close();
            }
        }

        return res;
    }

    public String getMultinotes(int appID, int paramID) {

        ArrayList<String> multinoteList = new ArrayList<>();
        String multinote = null;
        String query = "select multinote from s_MetaData " +
                " where MobileAppID=" + appID + " and FieldParameterID=" + paramID;

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                multinote = c.getString(0);
                // multinoteList.add(multinote);

            } while (c.moveToNext());

            c.close();

        }

        return multinote;
    }

    public List<MetaData> getMetaDataForNotes(int MobileAppID, int SiteID,
                                              String LocationID) {

        List<MetaData> dataList = new ArrayList<MetaData>();

        String[] metaColumns = new String[]{"FieldParameterID", "ParameterLabel",
                "RowOrder", "ColumnOrder", "FieldInputType", "NameValuePair", "DesiredUnits",
                "ValueType", "LovID", "Required_Y_N", "Warning_high", "Warning_Low",
                "high_limit", "Low_limit", "default_value", "ext_field1", "ext_field2", "ext_field3",
                "ext_field4", "ext_field5", "ext_field6", "ext_field7", "field_parameter_operands",
                "enable_parameter_notes", "showLast2", "standard_app", "label_width", "object_height",
                "object_width", "parameter_hint", "parent_parameter_id",
                "percent_difference", "routine_id", KEY_MandatoryField, KEY_LOCATION_IDS,
                KEY_straight_difference, KEY_field_action, KEY_field_score, KEY_font_style};
        String whereClause = "SiteID=? AND MobileAppID=? AND (LocationID=? OR LocationID=0) and UPPER(ParameterLabel)= 'NOTES'";// and "+KEY_Reviewer+"!=1";
        String[] whereArgs = new String[]{"" + SiteID, "" + MobileAppID,
                "" + LocationID};
        String orderBy = "RowOrder";
        Log.i(TAG, "getMetaDataForNotes() for MobileAppID=" + MobileAppID + ",SiteID=" + SiteID + ",LocationID=" + LocationID);
        Cursor cursor = database.query(DbAccess.TABLE_META_DATA, metaColumns,
                whereClause, whereArgs, null, null, orderBy);
        // System.out.println("mmm" + "After cursor in metadat");
        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                MetaData data = cursorToMetaData(cursor);
                dataList.add(data);
                cursor.moveToNext();
            }

            cursor.close();
            //  System.out.println("mmm" + " After cursor close");
        }
        return dataList;
    }

    private MetaData cursorToMetaData(Cursor cursor) {
        MetaData data = new MetaData();
        data.setMetaParamID(cursor.getInt(0));
        data.setMetaParamLabel(cursor.getString(1));
        data.setMetaRowOrder(cursor.getInt(2));
        data.setMetaColOrder(cursor.getInt(3));
        data.setMetaInputType(cursor.getString(4));
        data.setMetaNameValuePair(cursor.getString(5));
        data.setMetaDesiredUnits(cursor.getString(6));
        data.setMetaValueType(cursor.getString(7));
        data.setMetaLovId(cursor.getInt(8));
        data.setMetaRequired_Y_N(cursor.getString(9));
        data.setMetaWarningHigh(cursor.getFloat(10));
        data.setMetaWarningLow(cursor.getFloat(11));
        data.setMetaHighLimit(cursor.getFloat(12));
        data.setMetaLowLimit(cursor.getFloat(13));
        data.setDefaultValue(cursor.getString(14));
        data.setExtField1(cursor.getString(15));
        data.setExtField2(cursor.getString(16));
        data.setExtField3(cursor.getString(17));
        data.setExtField4(cursor.getString(18));
        data.setExtField5(cursor.getString(19));
        data.setExtField6(cursor.getString(20));
        data.setExtField7(cursor.getString(21));
        data.setFieldParameterOperands(cursor.getString(22));
        data.setIsEnableParameterNotes(cursor.getInt(23) != 0);
        data.setIsShowLast2(cursor.getInt(24) != 0);
        data.setIsStandardApp(cursor.getInt(25) != 0);
        data.setLabelWidth(cursor.getInt(26));
        data.setObjectHeight(cursor.getDouble(27));
        data.setObjectWidth(cursor.getDouble(28));
        data.setParameterHint(cursor.getString(29));
        data.setParentParameterId(cursor.getLong(30));
        data.setPercentDifference(cursor.getDouble(31));
        data.setRoutineId(cursor.getInt(32));
        data.setMandatoryField(cursor.getInt(33) + "");
        data.setLocationIds(cursor.getString(34) + "");
        data.setStraightDifference(cursor.getString(35));
        data.setFieldAction(cursor.getString(36));
        data.setFieldScore(cursor.getString(37));
        data.setFontStyle(cursor.getString(38));

        try {
            data.isRowVisible = cursor.getInt(39) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public void storeMetaDataArray(List<SMetaData> smetaList) {
        long t = System.currentTimeMillis();

        if (smetaList == null) {
            return;
        }

//        storeMetaDataTable(smetaList);
        storeBulkBindMetaDataTable(smetaList);
        long t1 = System.currentTimeMillis();
        System.out.println("alltime taken " + (t1 - t));
    }

    private long storeMetaDataTable(List<SMetaData> smetaList) {
        long ret = 0;

        long t = System.currentTimeMillis();
        try {
            database.beginTransaction();

            for (SMetaData smetaData : smetaList) {

                ContentValues values = new ContentValues();

                if (smetaData.getSiteId() == null) {
                    return -1;
                } else {
                    values.put(KEY_SiteID, smetaData.getSiteId());
                }
                if (smetaData.getLocationId() == null) {
                    return -1;
                } else {
                    values.put(KEY_LocationID, smetaData.getLocationId());
                }

                values.put(KEY_ParameterLabel, smetaData.getFieldParameterLabelAlias());
                values.put(KEY_RowOrder, smetaData.getRowOrder());
                String inputType = smetaData.getFieldInputType();
                values.put(KEY_FieldInputType, inputType == null ? "" : inputType);
                values.put(KEY_NameValuePair, smetaData.getNameValuePair());
                values.put(KEY_ValueType, smetaData.getValueType());
                values.put(KEY_LovID, smetaData.getLovId());
                values.put(KEY_FieldParameterID, smetaData.getFieldParameterId());

                values.put(KEY_Required_Y_N, smetaData.isRequired());

                values.put(KEY_Warning_high, smetaData.getWarningHigh());
                values.put(KEY_Warning_Low, smetaData.getWarningLow());
                values.put(KEY_high_limit, smetaData.getHighLimit());
                values.put(KEY_Low_limit, smetaData.getLowLimit());
                values.put(KEY_default_value, smetaData.getDefaultValue());
                values.put(KEY_parent_parameter_id, smetaData.getParentParameterId());
                values.put(KEY_MULTINOTE, smetaData.getMultinote());

                values.put(KEY_field_parameter_operands, smetaData.getFieldParameterOperands());
                values.put(KEY_enable_parameter_notes, smetaData.isEnableParameterNotes());
                values.put(KEY_showLast2, smetaData.isShowLast2());

                values.put(KEY_object_width, smetaData.getObjectWidth());
                values.put(KEY_parameter_hint, smetaData.getParameterHint());

                values.put(KEY_percent_difference, smetaData.getPercentDifference());
                values.put(KEY_routine_id, smetaData.getRoutineId());
                values.put(KEY_MobileAppID, smetaData.getMobileAppId());
                values.put(KEY_ext_field1, smetaData.getExtField1());
                values.put(KEY_ext_field2, smetaData.getExtField2());
                values.put(KEY_ext_field3, smetaData.getExtField3());
                values.put(KEY_ext_field7, smetaData.getExtField7());
                values.put(KEY_parameter_hint, smetaData.getParameterHint());
                values.put(KEY_MandatoryField, smetaData.getMandatoryField());
                values.put(KEY_enableParameterTasks, smetaData.isEnableParameterTasks());

                values.put(KEY_straight_difference, smetaData.getStraightDifference());
                values.put(KEY_field_action, smetaData.getFieldAction());
                values.put(KEY_field_score, smetaData.getFieldScore());
                values.put(KEY_font_style, smetaData.getFontStyle());

                if (smetaData.getLocationIds() == null || smetaData.getLocationIds().isEmpty())
                    values.put(KEY_LOCATION_IDS, "0");
                else
                    values.put(KEY_LOCATION_IDS, smetaData.getLocationIds());

                try {
                    ret = database.insert(DbAccess.TABLE_META_DATA, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "storeMetaDataTable() exception:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        long t1 = System.currentTimeMillis();
        Log.i(TAG, "storeMetaDataTable() time taken " + (t1 - t));
        Log.i(TAG, "storeMetaDataTable() stored count " + ret);
        return ret;
    }

    //used for v16 temporary
/*
    private long storeBulkBindMetaFieldsTable(List<Fields> smetaList) {
        long ret = 0;

        long t = System.currentTimeMillis();
        try {

            String[] arrColumns = {KEY_SiteID, KEY_LocationID, KEY_ParameterLabel, KEY_RowOrder, KEY_FieldInputType,
                    KEY_NameValuePair, KEY_ValueType, KEY_LovID, KEY_FieldParameterID, KEY_Required_Y_N,
                    KEY_Warning_high, KEY_Warning_Low, KEY_high_limit, KEY_Low_limit, KEY_default_value,
                    KEY_parent_parameter_id, KEY_MULTINOTE, KEY_field_parameter_operands,
                    KEY_enable_parameter_notes, KEY_showLast2, KEY_object_width, KEY_parameter_hint,
                    KEY_percent_difference, KEY_routine_id, KEY_MobileAppID, KEY_ext_field1, KEY_ext_field2,
                    KEY_ext_field3, KEY_ext_field7, KEY_parameter_hint, KEY_MandatoryField,
                    KEY_enableParameterTasks, KEY_straight_difference, KEY_field_action,
                    KEY_field_score, KEY_font_style, KEY_LOCATION_IDS, SiteDataSource.KEY_Status};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_META_DATA + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);

            database.beginTransaction();

            boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_META_DATA,
                    database);

            for (Fields sMetaData : smetaList) {

                if (isTableEmpty) {
                    if (sMetaData.getSite() == null) {
                        return -1;
                    } else {
                        statement.bindLong(1, sMetaData.getSiteId());
                    }
                    if (sMetaData.getLocationId() == null) {
                        return -1;
                    } else {
                        statement.bindLong(2, sMetaData.getLocationId());
                    }

                    if (sMetaData.getFieldParameterLabelAlias() != null)
                        statement.bindString(3, sMetaData.getFieldParameterLabelAlias());
                    else
                        statement.bindNull(3);

                    if (sMetaData.getRowOrder() != null)
                        statement.bindLong(4, sMetaData.getRowOrder());
                    else
                        statement.bindNull(4);

                    String inputType = sMetaData.getFieldInputType();
                    statement.bindString(5, inputType == null ? "" : inputType);

                    if (sMetaData.getNameValuePair() != null)
                        statement.bindString(6, sMetaData.getNameValuePair());
                    else
                        statement.bindNull(6);

                    if (sMetaData.getValueType() != null)
                        statement.bindString(7, sMetaData.getValueType());
                    else
                        statement.bindNull(7);

                    if (sMetaData.getLovId() != null)
                        statement.bindLong(8, sMetaData.getLovId());
                    else
                        statement.bindNull(8);

                    if (sMetaData.getFieldParameterId() != null)
                        statement.bindLong(9, sMetaData.getFieldParameterId());
                    else
                        statement.bindNull(9);

                    statement.bindLong(10, sMetaData.isRequired() ? 1 : 0);

                    if (sMetaData.getWarningHigh() != null)
                        statement.bindDouble(11, sMetaData.getWarningHigh());
                    else
                        statement.bindNull(11);

                    if (sMetaData.getWarningLow() != null)
                        statement.bindDouble(12, sMetaData.getWarningLow());
                    else
                        statement.bindNull(12);

                    if (sMetaData.getHighLimit() != null)
                        statement.bindDouble(13, sMetaData.getHighLimit());
                    else
                        statement.bindNull(13);

                    if (sMetaData.getLowLimit() != null)
                        statement.bindDouble(14, sMetaData.getLowLimit());
                    else
                        statement.bindNull(14);

                    if (sMetaData.getDefaultValue() != null)
                        statement.bindString(15, sMetaData.getDefaultValue());
                    else statement.bindNull(15);

                    if (sMetaData.getParentParameterId() != null)
                        statement.bindString(16, sMetaData.getParentParameterId());
                    else
                        statement.bindNull(16);

                    if (sMetaData.getMultinote() != null)
                        statement.bindString(17, sMetaData.getMultinote());
                    else
                        statement.bindNull(17);

                    if (sMetaData.getFieldParameterOperands() != null)
                        statement.bindString(18, sMetaData.getFieldParameterOperands());
                    else
                        statement.bindNull(18);

                    statement.bindLong(19, sMetaData.isEnableParameterNotes() ? 1 : 0);

                    statement.bindLong(20, sMetaData.isShowLast2() ? 1 : 0);

                    if (sMetaData.getObjectWidth() != null)
                        statement.bindDouble(21, sMetaData.getObjectWidth());
                    else statement.bindNull(21);

                    if (sMetaData.getParameterHint() != null)
                        statement.bindString(22, sMetaData.getParameterHint());
                    else
                        statement.bindNull(22);

                    if (sMetaData.getPercentDifference() != null)
                        statement.bindDouble(23, sMetaData.getPercentDifference());
                    else
                        statement.bindNull(23);

                    if (sMetaData.getRoutineId() != null)
                        statement.bindLong(24, sMetaData.getRoutineId());
                    else
                        statement.bindNull(24);

                    if (sMetaData.getMobileAppId() != null)
                        statement.bindLong(25, sMetaData.getMobileAppId());
                    else
                        statement.bindNull(25);

                    if (sMetaData.getExtField1() != null)
                        statement.bindString(26, sMetaData.getExtField1());
                    else
                        statement.bindNull(26);

                    if (sMetaData.getExtField2() != null)
                        statement.bindString(27, sMetaData.getExtField2());
                    else
                        statement.bindNull(27);

                    if (sMetaData.getExtField3() != null)
                        statement.bindString(28, sMetaData.getExtField3());
                    else
                        statement.bindNull(28);

                    if (sMetaData.getExtField7() != null)
                        statement.bindString(29, sMetaData.getExtField7());
                    else
                        statement.bindNull(29);

                    if (sMetaData.getParameterHint() != null)
                        statement.bindString(30, sMetaData.getParameterHint());
                    else
                        statement.bindNull(30);

                    if (sMetaData.getMandatoryField() != null)
                        statement.bindLong(31, sMetaData.getMandatoryField());
                    else
                        statement.bindNull(31);

                    statement.bindLong(32, sMetaData.isEnableParameterTasks() ? 1 : 0);

                    if (sMetaData.getStraightDifference() != null)
                        statement.bindString(33, sMetaData.getStraightDifference());
                    else
                        statement.bindNull(33);

                    if (sMetaData.getFieldAction() != null)
                        statement.bindString(34, sMetaData.getFieldAction());
                    else
                        statement.bindNull(34);

                    if (sMetaData.getFieldScore() != null)
                        statement.bindString(35, sMetaData.getFieldScore());
                    else
                        statement.bindNull(35);

                    if (sMetaData.getFontStyle() != null)
                        statement.bindString(36, sMetaData.getFontStyle());
                    else
                        statement.bindNull(36);

                    if (sMetaData.getLocationIds() == null || sMetaData.getLocationIds().isEmpty())
                        statement.bindString(37, "0");
                    else
                        statement.bindString(37, sMetaData.getLocationIds());

                    if (sMetaData.getStatus() != null)
                        statement.bindString(38, sMetaData.getStatus());
                    else statement.bindNull(38);

                    ret = statement.executeInsert();
                    statement.clearBindings();
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "storeMetaDataTable() exception:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        long t1 = System.currentTimeMillis();
        Log.i(TAG, "storeMetaDataTable() time taken " + (t1 - t));
        Log.i(TAG, "storeMetaDataTable() stored " + ret);
        return ret;
    }
*/

    public long storeBulkBindMetaDataTable(ArrayList<Fields> sMetaList) {
        long ret = 0;

        long t = System.currentTimeMillis();
        try {

            String[] arrColumns = {KEY_SiteID, KEY_LocationID, KEY_ParameterLabel, KEY_RowOrder, KEY_FieldInputType,
                    KEY_NameValuePair, KEY_ValueType, KEY_LovID, KEY_FieldParameterID, KEY_Required_Y_N,
                    KEY_Warning_high, KEY_Warning_Low, KEY_high_limit, KEY_Low_limit, KEY_default_value,
                    KEY_parent_parameter_id, KEY_MULTINOTE, KEY_field_parameter_operands,
                    KEY_enable_parameter_notes, KEY_showLast2, KEY_object_width, KEY_parameter_hint,
                    KEY_percent_difference, KEY_routine_id, KEY_MobileAppID, KEY_ext_field1, KEY_ext_field2,
                    KEY_ext_field3, KEY_ext_field7, KEY_parameter_hint, KEY_MandatoryField,
                    KEY_enableParameterTasks, KEY_straight_difference, KEY_field_action,
                    KEY_field_score, KEY_font_style, KEY_LOCATION_IDS, SiteDataSource.KEY_Status};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_META_DATA + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);

            database.beginTransaction();

            boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_META_DATA,
                    database);

            for (Fields sMetaData : sMetaList) {

                if (sMetaData.getFieldParameterId() == 19056)
                    Log.i("Task field hidden", "status = " + sMetaData.getStatus() + " appid = " + sMetaData.getMobileAppId());

                if (!isEntryAvailable(sMetaData.getTabId() + "",
                        sMetaData.getFieldParameterId() + "")) {

                    statement.bindLong(1, 0);

                    statement.bindLong(2, 0);

                    if (sMetaData.getName() != null)
                        statement.bindString(3, sMetaData.getName());
                    else
                        statement.bindNull(3);

                    if (sMetaData.getRowOrder() != null)
                        statement.bindLong(4, sMetaData.getRowOrder());
                    else
                        statement.bindNull(4);

                    String inputType = sMetaData.getFieldType();
                    statement.bindString(5, inputType == null ? "" : inputType);

                    if (sMetaData.getOptions() != null && sMetaData.getOptions().size() > 0)
                        statement.bindString(6, Util.getCabSeparatedString(sMetaData.getOptions()));
                    else
                        statement.bindNull(6);

                    if (sMetaData.getFieldType() != null)
                        statement.bindString(7, sMetaData.getFieldType());
                    else
                        statement.bindNull(7);

                    if (sMetaData.getLovId() != null)
                        statement.bindLong(8, sMetaData.getLovId());
                    else
                        statement.bindNull(8);

                    if (sMetaData.getFieldParameterId() != null)
                        statement.bindLong(9, sMetaData.getFieldParameterId());
                    else
                        statement.bindNull(9);

                    if (sMetaData.getMandatory() != null)
                        statement.bindLong(10, sMetaData.getMandatory() ? 1 : 0);
                    else
                        statement.bindLong(10, 0);

                    if (sMetaData.getWarningUpperLimit() != null)
                        statement.bindString(11, sMetaData.getWarningUpperLimit());
                    else
                        statement.bindNull(11);

                    if (sMetaData.getWarningLowerLimit() != null)
                        statement.bindString(12, sMetaData.getWarningLowerLimit());
                    else
                        statement.bindNull(12);

                    if (sMetaData.getNotToExceedUpperLimit() != null)
                        statement.bindString(13, sMetaData.getNotToExceedUpperLimit());
                    else
                        statement.bindNull(13);

                    if (sMetaData.getNotToExceedLowerLimit() != null)
                        statement.bindString(14, sMetaData.getNotToExceedLowerLimit());
                    else
                        statement.bindNull(14);

                    if (sMetaData.getDefaultValue() != null)
                        statement.bindString(15, sMetaData.getDefaultValue());
                    else statement.bindNull(15);

                    if (sMetaData.getParentFieldParameterAliasId() != null)
                        statement.bindString(16, sMetaData.getParentFieldParameterAliasId());
                    else
                        statement.bindNull(16);

                    if (sMetaData.getMultiNotes() != null)
                        statement.bindString(17, sMetaData.getMultiNotes());
                    else
                        statement.bindNull(17);

                    if (sMetaData.getFormula() != null)
                        statement.bindString(18, sMetaData.getFormula());
                    else
                        statement.bindNull(18);

                    statement.bindLong(19, sMetaData.isEnableAdditionalNotes() ? 1 : 0);

                    if (sMetaData.getFieldAction() != null
                            && sMetaData.getFieldAction().toLowerCase().contains("showlast2"))
                        statement.bindLong(20, 1);
                    else
                        statement.bindLong(20, 0);

                    //object width
                    statement.bindNull(21);

                    if (sMetaData.getGuide() != null)
                        statement.bindString(22, sMetaData.getGuide());
                    else
                        statement.bindNull(22);

                    if (sMetaData.getPercentDifference() != null)
                        statement.bindDouble(23, sMetaData.getPercentDifference());
                    else
                        statement.bindNull(23);

                    if (sMetaData.getRoutineID() != null)
                        statement.bindLong(24, sMetaData.getRoutineID());
                    else
                        statement.bindNull(24);

                    if (sMetaData.getTabId() != null)
                        statement.bindLong(25, sMetaData.getTabId());
                    else
                        statement.bindNull(25);

                    statement.bindNull(26); //ext1

                    if (sMetaData.getFieldAction() != null)
                        statement.bindString(27, sMetaData.getFieldAction());
                    else
                        statement.bindNull(27);

                    //ExtField3
                    if (sMetaData.getStraightDifference() != null)
                        statement.bindString(28, sMetaData.getStraightDifference());
                    else
                        statement.bindNull(28);

                    if (sMetaData.getFontStyle() != null)
                        statement.bindString(29, sMetaData.getFontStyle());
                    else
                        statement.bindNull(29);

                    if (sMetaData.getGuide() != null)
                        statement.bindString(30, sMetaData.getGuide());
                    else
                        statement.bindNull(30);

                    if (sMetaData.getMandatory() != null)
                        statement.bindLong(31, sMetaData.getMandatory() ? 1 : 0);
                    else
                        statement.bindLong(31, 0);

                    statement.bindLong(32, sMetaData.getEnableParameterTasks() ? 1 : 0);

                    if (sMetaData.getStraightDifference() != null)
                        statement.bindString(33, sMetaData.getStraightDifference());
                    else
                        statement.bindNull(33);

                    if (sMetaData.getFieldAction() != null)
                        statement.bindString(34, sMetaData.getFieldAction());
                    else
                        statement.bindNull(34);

                    if (sMetaData.getFieldScore() != null)
                        statement.bindString(35, sMetaData.getFieldScore());
                    else
                        statement.bindNull(35);

                    if (sMetaData.getFontStyle() != null)
                        statement.bindString(36, sMetaData.getFontStyle());
                    else
                        statement.bindNull(36);

                    if (sMetaData.getLocationIds() == null || sMetaData.getLocationIds().isEmpty())
                        statement.bindString(37, "0");
                    else
                        statement.bindString(37, sMetaData.getLocationIds());

                    if (sMetaData.getStatus() != null)
                        statement.bindString(38, sMetaData.getStatus());
                    else statement.bindNull(38);

                    ret = statement.executeInsert();
                    statement.clearBindings();
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "storeMetaDataTable() exception:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        long t1 = System.currentTimeMillis();
        Log.i(TAG, "storeMetaDataTable() stored " + ret);
        return ret;
    }

    private long storeBulkBindMetaDataTable(List<SMetaData> smetaList) {
        long ret = 0;

        long t = System.currentTimeMillis();
        try {

            String[] arrColumns = {KEY_SiteID, KEY_LocationID, KEY_ParameterLabel, KEY_RowOrder, KEY_FieldInputType,
                    KEY_NameValuePair, KEY_ValueType, KEY_LovID, KEY_FieldParameterID, KEY_Required_Y_N,
                    KEY_Warning_high, KEY_Warning_Low, KEY_high_limit, KEY_Low_limit, KEY_default_value,
                    KEY_parent_parameter_id, KEY_MULTINOTE, KEY_field_parameter_operands,
                    KEY_enable_parameter_notes, KEY_showLast2, KEY_object_width, KEY_parameter_hint,
                    KEY_percent_difference, KEY_routine_id, KEY_MobileAppID, KEY_ext_field1, KEY_ext_field2,
                    KEY_ext_field3, KEY_ext_field7, KEY_parameter_hint, KEY_MandatoryField,
                    KEY_enableParameterTasks, KEY_straight_difference, KEY_field_action,
                    KEY_field_score, KEY_font_style, KEY_LOCATION_IDS, SiteDataSource.KEY_Status};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_META_DATA + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);

            database.beginTransaction();

            boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_META_DATA,
                    database);

            for (SMetaData sMetaData : smetaList) {

                if (sMetaData.isInsert() || isTableEmpty) {
                    if (sMetaData.getSiteId() == null) {
                        return -1;
                    } else {
                        statement.bindLong(1, sMetaData.getSiteId());
                    }
                    if (sMetaData.getLocationId() == null) {
                        return -1;
                    } else {
                        statement.bindLong(2, sMetaData.getLocationId());
                    }

                    if (sMetaData.getFieldParameterLabelAlias() != null)
                        statement.bindString(3, sMetaData.getFieldParameterLabelAlias());
                    else
                        statement.bindNull(3);

                    if (sMetaData.getRowOrder() != null)
                        statement.bindLong(4, sMetaData.getRowOrder());
                    else
                        statement.bindNull(4);

                    String inputType = sMetaData.getFieldInputType();
                    statement.bindString(5, inputType == null ? "" : inputType);

                    if (sMetaData.getNameValuePair() != null)
                        statement.bindString(6, sMetaData.getNameValuePair());
                    else
                        statement.bindNull(6);

                    if (sMetaData.getValueType() != null)
                        statement.bindString(7, sMetaData.getValueType());
                    else
                        statement.bindNull(7);

                    if (sMetaData.getLovId() != null)
                        statement.bindLong(8, sMetaData.getLovId());
                    else
                        statement.bindNull(8);

                    if (sMetaData.getFieldParameterId() != null)
                        statement.bindLong(9, sMetaData.getFieldParameterId());
                    else
                        statement.bindNull(9);

                    statement.bindLong(10, sMetaData.isRequired() ? 1 : 0);

                    if (sMetaData.getWarningHigh() != null)
                        statement.bindDouble(11, sMetaData.getWarningHigh());
                    else
                        statement.bindNull(11);

                    if (sMetaData.getWarningLow() != null)
                        statement.bindDouble(12, sMetaData.getWarningLow());
                    else
                        statement.bindNull(12);

                    if (sMetaData.getHighLimit() != null)
                        statement.bindDouble(13, sMetaData.getHighLimit());
                    else
                        statement.bindNull(13);

                    if (sMetaData.getLowLimit() != null)
                        statement.bindDouble(14, sMetaData.getLowLimit());
                    else
                        statement.bindNull(14);

                    if (sMetaData.getDefaultValue() != null)
                        statement.bindString(15, sMetaData.getDefaultValue());
                    else statement.bindNull(15);

                    if (sMetaData.getParentParameterId() != null)
                        statement.bindString(16, sMetaData.getParentParameterId());
                    else
                        statement.bindNull(16);

                    if (sMetaData.getMultinote() != null)
                        statement.bindString(17, sMetaData.getMultinote());
                    else
                        statement.bindNull(17);

                    if (sMetaData.getFieldParameterOperands() != null)
                        statement.bindString(18, sMetaData.getFieldParameterOperands());
                    else
                        statement.bindNull(18);

                    statement.bindLong(19, sMetaData.isEnableParameterNotes() ? 1 : 0);

                    statement.bindLong(20, sMetaData.isShowLast2() ? 1 : 0);

                    if (sMetaData.getObjectWidth() != null)
                        statement.bindDouble(21, sMetaData.getObjectWidth());
                    else statement.bindNull(21);

                    if (sMetaData.getParameterHint() != null)
                        statement.bindString(22, sMetaData.getParameterHint());
                    else
                        statement.bindNull(22);

                    if (sMetaData.getPercentDifference() != null)
                        statement.bindDouble(23, sMetaData.getPercentDifference());
                    else
                        statement.bindNull(23);

                    if (sMetaData.getRoutineId() != null)
                        statement.bindLong(24, sMetaData.getRoutineId());
                    else
                        statement.bindNull(24);

                    if (sMetaData.getMobileAppId() != null)
                        statement.bindLong(25, sMetaData.getMobileAppId());
                    else
                        statement.bindNull(25);

                    if (sMetaData.getExtField1() != null)
                        statement.bindString(26, sMetaData.getExtField1());
                    else
                        statement.bindNull(26);

                    if (sMetaData.getExtField2() != null)
                        statement.bindString(27, sMetaData.getExtField2());
                    else
                        statement.bindNull(27);

                    if (sMetaData.getExtField3() != null)
                        statement.bindString(28, sMetaData.getExtField3());
                    else
                        statement.bindNull(28);

                    if (sMetaData.getExtField7() != null)
                        statement.bindString(29, sMetaData.getExtField7());
                    else
                        statement.bindNull(29);

                    if (sMetaData.getParameterHint() != null)
                        statement.bindString(30, sMetaData.getParameterHint());
                    else
                        statement.bindNull(30);

                    if (sMetaData.getMandatoryField() != null)
                        statement.bindLong(31, sMetaData.getMandatoryField());
                    else
                        statement.bindNull(31);

                    statement.bindLong(32, sMetaData.isEnableParameterTasks() ? 1 : 0);

                    if (sMetaData.getStraightDifference() != null)
                        statement.bindString(33, sMetaData.getStraightDifference());
                    else
                        statement.bindNull(33);

                    if (sMetaData.getFieldAction() != null)
                        statement.bindString(34, sMetaData.getFieldAction());
                    else
                        statement.bindNull(34);

                    if (sMetaData.getFieldScore() != null)
                        statement.bindString(35, sMetaData.getFieldScore());
                    else
                        statement.bindNull(35);

                    if (sMetaData.getFontStyle() != null)
                        statement.bindString(36, sMetaData.getFontStyle());
                    else
                        statement.bindNull(36);

                    if (sMetaData.getLocationIds() == null || sMetaData.getLocationIds().isEmpty())
                        statement.bindString(37, "0");
                    else
                        statement.bindString(37, sMetaData.getLocationIds());

                    if (sMetaData.getStatus() != null)
                        statement.bindString(38, sMetaData.getStatus());
                    else statement.bindNull(38);

                    ret = statement.executeInsert();
                    statement.clearBindings();
                } else {
                    ContentValues values = new ContentValues();

                    values.put(KEY_ParameterLabel, sMetaData.getFieldParameterLabelAlias());
                    values.put(KEY_RowOrder, sMetaData.getRowOrder());
                    String inputType = sMetaData.getFieldInputType();
                    values.put(KEY_FieldInputType, inputType == null ? "" : inputType);
                    values.put(KEY_NameValuePair, sMetaData.getNameValuePair());
                    values.put(KEY_ValueType, sMetaData.getValueType());
                    values.put(KEY_LovID, sMetaData.getLovId());
                    values.put(KEY_FieldParameterID, sMetaData.getFieldParameterId());

                    values.put(KEY_Required_Y_N, sMetaData.isRequired());

                    values.put(KEY_Warning_high, sMetaData.getWarningHigh());
                    values.put(KEY_Warning_Low, sMetaData.getWarningLow());
                    values.put(KEY_high_limit, sMetaData.getHighLimit());
                    values.put(KEY_Low_limit, sMetaData.getLowLimit());
                    values.put(KEY_default_value, sMetaData.getDefaultValue());
                    values.put(KEY_parent_parameter_id, sMetaData.getParentParameterId());
                    values.put(KEY_MULTINOTE, sMetaData.getMultinote());

                    values.put(KEY_field_parameter_operands, sMetaData.getFieldParameterOperands());
                    values.put(KEY_enable_parameter_notes, sMetaData.isEnableParameterNotes());
                    values.put(KEY_showLast2, sMetaData.isShowLast2());

                    values.put(KEY_object_width, sMetaData.getObjectWidth());
                    values.put(KEY_parameter_hint, sMetaData.getParameterHint());

                    values.put(KEY_percent_difference, sMetaData.getPercentDifference());
                    values.put(KEY_routine_id, sMetaData.getRoutineId());
                    values.put(KEY_ext_field1, sMetaData.getExtField1());
                    values.put(KEY_ext_field2, sMetaData.getExtField2());
                    values.put(KEY_ext_field3, sMetaData.getExtField3());
                    values.put(KEY_ext_field7, sMetaData.getExtField7());
                    values.put(KEY_parameter_hint, sMetaData.getParameterHint());
                    values.put(KEY_MandatoryField, sMetaData.getMandatoryField());
                    values.put(KEY_enableParameterTasks, sMetaData.isEnableParameterTasks());

                    values.put(KEY_straight_difference, sMetaData.getStraightDifference());
                    values.put(KEY_field_action, sMetaData.getFieldAction());
                    values.put(KEY_field_score, sMetaData.getFieldScore());
                    values.put(KEY_font_style, sMetaData.getFontStyle());
                    values.put(SiteDataSource.KEY_Status, sMetaData.getStatus());

                    String whereClause = KEY_SiteID + " = ?" + KEY_LocationID + " = ?"
                            + KEY_MobileAppID + " = ?";
                    String[] whereArgs = new String[]{sMetaData.getSiteId() + "",
                            sMetaData.getLocationId() + "", sMetaData.getMobileAppId() + ""};
                    ret = database.update(DbAccess.TABLE_META_DATA, values,
                            whereClause, whereArgs);
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "storeMetaDataTable() exception:" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }
        long t1 = System.currentTimeMillis();
        Log.i(TAG, "storeMetaDataTable() time taken " + (t1 - t));
        Log.i(TAG, "storeMetaDataTable() stored " + ret);
        return ret;
    }

    public static boolean isTableEmpty(String tableName, SQLiteDatabase database) {
        int count = 0;
        String query = "select count(*) from " + tableName;

        try (Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count == 0;
    }

    public boolean isEntryAvailable(String mobileAppId, String fieldParamId) {
        int count = 0;
        String query = "select count(*) from " + DbAccess.TABLE_META_DATA + " where MobileAppID = "
                + mobileAppId + " and FieldParameterID = " + fieldParamId;

        try (Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count > 0;
    }

    public int storeBulkMetaDataList(List<SMetaData> metaList) {
        String sql = "INSERT INTO " + DbAccess.TABLE_META_DATA + " ('" + KEY_LocationID + "', '" + KEY_SiteID + "', '"
                + KEY_ParameterLabel + "','" + KEY_RowOrder + "','" + KEY_FieldInputType + "','" + KEY_NameValuePair + "','"
                + KEY_ValueType + "','" + KEY_LovID + "','" + KEY_FieldParameterID + "','" + KEY_Required_Y_N + "','"
                + KEY_Warning_high + "','" + KEY_Warning_Low + "','" + KEY_high_limit + "','" + KEY_Low_limit + "','"
                + KEY_default_value + "','" + KEY_field_parameter_operands + "','" + KEY_enable_parameter_notes + "','"
                + KEY_showLast2 + "','" + KEY_object_width + "','" + KEY_parameter_hint + "','" + KEY_percent_difference + "','"
                + KEY_routine_id + "','" + KEY_MobileAppID + "','" + KEY_ext_field1 + "','" + KEY_ext_field2 + "','" + KEY_ext_field3 + "','" + KEY_MandatoryField + "') VALUES ";

        String values = "";
        for (int i = 0; i < metaList.size(); i++) {
            SMetaData ob = metaList.get(i);
            if (ob.getLocationId() == null || ob.getSiteId() == null) {
                return -1;
            }
            String inputType = ob.getFieldInputType() == null ? "" : ob.getFieldInputType();

            if (i != (metaList.size() - 1)) {


                values = values + "(" + ob.getLocationId() + "," + ob.getSiteId() + ",'" + ob.getFieldParameterLabelAlias()
                        + "','" + ob.getRowOrder() + "','" + inputType + "','" + ob.getNameValuePair() + "','"
                        + ob.getValueType() + "'," + ob.getLovId() + "," + ob.getFieldParameterId() + ",'"
                        + ob.isRequired() + "'," + ob.getWarningHigh() + "," + ob.getWarningLow() + "," + ob.getHighLimit() + ","
                        + ob.getLowLimit() + ",'" + ob.getDefaultValue() + "','" + ob.getFieldParameterOperands() + "','"
                        + ob.isEnableParameterNotes() + "','" + ob.isShowLast2() + "'," + ob.getObjectWidth() + ",'"
                        + ob.getParameterHint() + "'," + ob.getPercentDifference() + "," + ob.getRoutineId() + ","
                        + ob.getMobileAppId() + ",'" + ob.getExtField1() + "','" + ob.getExtField2() + "','" + ob.getExtField3() + "','" + ob.getMandatoryField() + "'),";

            } else {
                values = values + "(" + ob.getLocationId() + "," + ob.getSiteId() + ",'" + ob.getFieldParameterLabelAlias()
                        + "','" + ob.getRowOrder() + "','" + inputType + "','" + ob.getNameValuePair() + "','"
                        + ob.getValueType() + "'," + ob.getLovId() + "," + ob.getFieldParameterId() + ",'"
                        + ob.isRequired() + "'," + ob.getWarningHigh() + "," + ob.getWarningLow() + "," + ob.getHighLimit() + ","
                        + ob.getLowLimit() + ",'" + ob.getDefaultValue() + "','" + ob.getFieldParameterOperands() + "','"
                        + ob.isEnableParameterNotes() + "','" + ob.isShowLast2() + "'," + ob.getObjectWidth() + ",'"
                        + ob.getParameterHint() + "'," + ob.getPercentDifference() + "," + ob.getRoutineId() + ","
                        + ob.getMobileAppId() + ",'" + ob.getExtField1() + "','" + ob.getExtField2() + "','" + ob.getExtField3() + "','" + ob.getMandatoryField() + "')";
            }
        }

        sql = sql + values;

        Log.i(TAG, "Insert Bulk MetaData Query:" + sql);

        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            return metaList.size();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to store MetaData List:" + e.getMessage());

        }
        return 0;
    }

    public boolean isExistsLocationZero(int siteID, int appID) {
        //String query = "select DISTINCT LocationID from s_MetaData where LocationID=0 and siteID=? and MobileAppID=?";
        String query = "select DISTINCT LocationID from s_MetaData where LocationID=0 and siteID=? and MobileAppID in " +
                "(select distinct a.MobileAppID from s_MetaData A INNER JOIN s_MobileApp B ON A.MobileAppID = B.MobileAppID " +
                " Where parent_app_id = ?)";
        String[] whereArgs = new String[]{"" + siteID, "" + appID};
        Cursor cursor = null;
        int LocationID = -1;
        boolean exists = false;

        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    LocationID = cursor.getInt(0);
                    break;
                }
                cursor.close();
            }
        } catch (Exception e) {

        }
        if (LocationID == 0) {
            exists = true;
        }
        return exists;
    }

    String[] resetUserTables = new String[]{
            "s_SiteUserRole",
            "s_User"
    };

    //all tables to truncate when logout app is called
    String[] resetAppTables = new String[]{
            "s_MetaData",
            "r_FieldParameter",
            "s_Location",
            "s_MobileApp",
            "s_SiteUserRole",
            "s_Site",
            "s_lov_items",
            "s_SiteMobileApp",
            "s_Default_Values",
            "d_field_data_temp",
            "d_SampleMapTag",
            "d_sync_status",
            "LocFormStatus",
            "s_file_permission",
            "s_Location_Type",
            "s_lov",
            "s_lov_items",
            "s_project_file",
            "s_project_folder",
            "s_SiteUserRole",
            "s_User",
            "temp_project_file",
            "temp_project_folder",
            "d_Event",
            "d_Attachment",
            "d_FieldData",
            DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE,
            DbAccess.TABLE_S_APP_PRFERENCE_MAPPING,
            DbAccess.TABLE_NOTIFICATIONS,
            DbAccess.TABLE_S_LOCATION_ATTRIBUTE,
            DbAccess.TABLE_CONSTRUCTION_POSTDATA,
            DbAccess.TABLE_CONSTRUCTION_MEDIADATA,
            DbAccess.TABLE_CONSTRUCTION_CTAGDATA,
            DbAccess.TABLE_CONSTRUCTION_RTAGDATA,
            DbAccess.TABLE_TASK_DETAILS,
            DbAccess.TABLE_TASK_COMMENTS,
            DbAccess.TABLE_TASK_ATTACHMENTS,
            DbAccess.TABLE_TASK_USERS,
            DbAccess.TABLE_META_DATA_ATTRIBUTES,
            DbAccess.TABLE_LOCATION_PROFILE_PICTURES,
            DbAccess.TABLE_COPIED_FORM_TEMPLATE,
            DbAccess.TABLE_CM_COC_MASTER,
            DbAccess.TABLE_CM_COC_DETAILS,
            DbAccess.TABLE_CM_METHODS,
            DbAccess.TABLE_FORM_SITES,
            DbAccess.TABLE_LOGS_DATA
    };

    //tables to truncate when erase data called
    String[] tablesToErase = new String[]{
            DbAccess.TABLE_FIELD_DATA,
            DbAccess.TABLE_TEMP_D_FIELD_DATA,
            DbAccess.TABLE_D_FIELD_DATA_CONFLICT,
            DbAccess.TABLE_ATTACHMENT,
            DbAccess.TABLE_EVENT,
            DbAccess.TABLE_TASK_DETAILS,
            DbAccess.TABLE_TASK_COMMENTS,
            DbAccess.TABLE_TASK_ATTACHMENTS,
            DbAccess.TABLE_TASK_ATTACHMENTS,
            DbAccess.TABLE_TEMP_PROJECT_FILE,
            DbAccess.TABLE_TEMP_PROJECT_FOLDER,
            DbAccess.TABLE_TEMP_PROJECT_FOLDER,
            DbAccess.TABLE_S_PROJECT_FOLDER,
            DbAccess.TABLE_S_PROJECT_FILE,
            DbAccess.TABLE_D_SAMPLE_MAPTAG,
            DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE,
            DbAccess.TABLE_D_SYNC_STATUS,
            DbAccess.TABLE_DATA_SYNC_STATUS
    };

    //tables to truncate when meta data api called
    String[] metaTables = new String[]{
            "s_MetaData",
            "r_FieldParameter",
            "s_Location",
            "s_MobileApp",
            "s_SiteUserRole",
            "s_Site",
            "s_lov_items",
            DbAccess.TABLE_SITE_MOBILEAPP,
            "s_LocationAttribute",
            "s_Default_Values",
            "TaskDataList",
            "TaskDataAttachmentList",
            "TaskDataCommentList",
            DbAccess.TABLE_TASK_DETAILS,
            DbAccess.TABLE_TASK_COMMENTS,
            DbAccess.TABLE_TASK_ATTACHMENTS,
            DbAccess.TABLE_TASK_USERS,
            DbAccess.TABLE_META_DATA_ATTRIBUTES,
            DbAccess.TABLE_LOCATION_PROFILE_PICTURES,
            DbAccess.TABLE_D_SYNC_STATUS,
            DbAccess.TABLE_FORM_SITES,
            DbAccess.TABLE_CM_METHODS
    };

    String[] constructionMetaTables = new String[]{"s_Site", "s_SiteUserRole"};
    String[] constructionSimpleNoteDataTables = new String[]{"c_PostData", "c_MediaData",
            "c_CTagData", "c_RTagData"};

    public void truncateMetaData() {
        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            for (String metaTable : metaTables) {
                try {
                    ret = database.delete(metaTable, null, null);
                    Log.i(TAG, "deleted table name :" + metaTable);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Caught for Table name=" + metaTable + ret);
                }
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

    String[] TaskTables = new String[]{
            "TaskDataList",
            "TaskDataAttachmentList",
            "TaskDataCommentList"
    };

    public void resetAppData() {
        Log.i(TAG, "ResetAppData() IN time:" + System.currentTimeMillis());

        int ret = 0;
        String username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        Log.i(TAG, "ResetAppData() UserName:" + username);

        Util.setSharedPreferencesProperty(mContext, GlobalStrings.SHOW_HOSPITAL_ALERT_FOR_FIRSTTIME + username, null);

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            for (String resetAppTable : resetAppTables) {
                try {
                    ret = database.delete(resetAppTable, null, null);
                    Log.i(TAG, "deleted table name :" + resetAppTable);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error Caught for Table name=" + resetAppTable + ret);
                }
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

        Log.i(TAG, "ResetAppData() OUT time:" + System.currentTimeMillis());
    }

    public void resetUsersData() {

        int ret = 0;
        String username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        Log.i(TAG, "ResetAppData() UserName:" + username);

        Util.setSharedPreferencesProperty(mContext,
                GlobalStrings.SHOW_HOSPITAL_ALERT_FOR_FIRSTTIME + username, null);

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            for (String resetAppTable : resetUserTables) {
                try {
                    ret = database.delete(resetAppTable, null, null);
                    Log.i(TAG, "deleted table name :" + resetAppTable);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error Caught for Table name=" + resetAppTable + ret);
                }
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

    public void resetEventTable() {

        int ret = 0;
        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            try {
                ret = database.delete("d_Event", null, null);
                Log.i(TAG, "deleted table name : d_Event");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error Caught for Table name= d_Event" + ret);
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

    public void eraseData() {
        int ret = 0;
        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();
            for (String table : tablesToErase) {
                try {
                    ret = database.delete(table, null, null);
                    Log.i(TAG, "deleted table name :" + table);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error Caught for Table name=" + table + ret);
                }
            }
            database.setTransactionSuccessful();

            Util.setSharedPreferencesProperty(mContext,
                    GlobalStrings.ALL_EVENT_DATA_LAST_SYNC, 0L);
            Util.delete_All_Log();
            Util.deleteAllInternalImageFolders(mContext);
        } catch (Exception e) {
            e.printStackTrace();
            database = DbAccess.getInstance(mContext).database;
        } finally {
            if (database != null) {
                database.endTransaction();
            }
        }

        Log.i(TAG, "ResetAppData() OUT time:" + System.currentTimeMillis());
    }

    public ArrayList<newFormLabelResponse> getMetaDataForForm(int mobileAppId) {
        ArrayList<newFormLabelResponse> dataList = new ArrayList<newFormLabelResponse>();


        String selectQuery = "SELECT ParameterLabel,RowOrder,FieldInputType FROM "
                + DbAccess.TABLE_META_DATA + " where MobileAppID =" + mobileAppId;

        // SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        newFormLabelResponse data = new newFormLabelResponse();
        if (cursor != null && cursor.moveToFirst()) {
            do {

                data = new newFormLabelResponse();
                data.setFieldParameterLabelAlias(cursor.getString(0));
                data.setRowOrder(cursor.getString(1));
                data.setFieldInputType(cursor.getString(2));
                dataList.add(data);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return dataList;
    }

    public int storeField(List<newFormLabelResponse> mRetFormList, boolean isOffline) {
        int count = 0;
        if (mRetFormList == null) {
            return 0;
        }
        for (int i = 0; i < mRetFormList.size(); i++) {
            count = count + storenewLabelField(mRetFormList.get(i), isOffline);
        }
        return count;
    }

    public int storenewLabelField(newFormLabelResponse slabel, boolean isOffline) {
        long result = 0;
        ContentValues values = new ContentValues();
        if (slabel == null) {
            return -1;
        }

        //  long locallocationID = -(System.currentTimeMillis());

        if (isOffline) {
            //  values.put(KEY_SyncFlag, 1);
          /*  values.put(KEY_LocationID, 0);

        } else {
            values.put(KEY_LocationID, 0);*/
            // int mobid = getCurrentAppID();

            values.put(KEY_FieldParameterID, slabel.getFieldParameterId());
            values.put(KEY_MobileAppID, slabel.getMobileAppId());
            values.put(KEY_FieldInputType, slabel.getFieldInputType());
            values.put(KEY_ParameterLabel, slabel.getFieldParameterLabelAlias());
            values.put(KEY_RowOrder, slabel.getRowOrder());
            values.put(KEY_ext_field1, slabel.getRowOrder());
            values.put(KEY_ext_field2, slabel.getExtField2());
            values.put(KEY_ext_field3, slabel.getExtField3());

            try {
                result = database.insert(DbAccess.TABLE_META_DATA, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "storeLabel=" + result + "Exception Msg=" + e.getLocalizedMessage());
            }
        } else {
            values.put(KEY_SiteID, slabel.getSiteId());
            values.put(KEY_FieldParameterID, slabel.getFieldParameterId());
            values.put(KEY_MobileAppID, slabel.getMobileAppId());
            values.put(KEY_FieldInputType, slabel.getFieldInputType());
            values.put(KEY_ParameterLabel, slabel.getFieldParameterLabelAlias());
            values.put(KEY_RowOrder, slabel.getRowOrder());
            values.put(KEY_ext_field1, slabel.getExtField1());
            values.put(KEY_default_value, slabel.getDefaultValue());
            values.put(KEY_high_limit, slabel.getHighLimit());
            values.put(KEY_enable_parameter_notes, slabel.getEnableParameterNotes());
            values.put(KEY_LocationID, slabel.getLocationId());
//            values.put(KEY_MandatoryField, slabel.getLocationId());
            try {
                result = database.insert(DbAccess.TABLE_META_DATA, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "storeLabel=" + result + "Exception Msg=" + e.getLocalizedMessage());
            }
        }


        return (int) result;

    }

    public boolean isCheckCompanyIdForForm(String compid) {


        String selectQuery = " SELECT (1) FROM s_mobileApp INNER JOIN s_User ON s_mobileApp.CompanyID = s_User.CompanyID";

        Log.i(TAG, "isCheckCompanyIdForForm() query= " + selectQuery);

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getCount() > 0) {

                return true;
            }

            cursor.close();

        }
        Log.i(TAG, "isCheckCompanyIdForForm() OUT time= " + System.currentTimeMillis());

        return false;
    }

    public boolean isMandatoryForMobileApp(String parentappID) {


        String selectQuery = "select distinct(1) from s_metadata s inner join s_sitemobileapp sm on " +
                "s.MobileAppID=sm.MobileAppID where s.mandatoryField=2 and sm.roll_into_app_id=" + parentappID;

        Log.i(TAG, "isMandatoryForMobileApp() query= " + selectQuery);

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getCount() > 0) {

                return true;
            }

            cursor.close();

        }
        Log.i(TAG, "isMandatoryForMobileApp() OUT time= " + System.currentTimeMillis());

        return false;
    }

    public boolean getmandatoryField(int currentAppID) {
        Cursor c = null;
        String query = "SELECT * FROM s_MetaData where mandatoryField=2 and MobileAppID=" + currentAppID;

        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                if (c.getCount() > 0) {
                    return true;
                }
                c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public ArrayList<String> getFieldParameterIDIfExist(String inputType, int parentAppID) {
        ArrayList<String> fpid = new ArrayList<>();

        String query = "SELECT FieldParameterID from s_MetaData where FieldInputType='"
                + inputType + "'" + " and MobileAppID=" + parentAppID;

        Cursor c = database.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                fpid.add(c.getString(0));
            } while (c.moveToNext());

            c.close();
        }
        return fpid;
    }

    public String getFielParameterOperandFromFieldParentId(String key) {

        //ArrayList<MetaData> arrayList = new ArrayList<>();
        //MetaData metaData = new MetaData();
        String fieldParameterId = null;

        String query = "select field_parameter_operands from s_MetaData where FieldParameterID = " + key;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                fieldParameterId = cursor.getString(0);

                /*metaData.setCurrentFormID(Integer.parseInt(cursor.getString(cursor.getColumnIndex("MobileAppID"))));
                metaData.setMetaParamLabel(cursor.getString(cursor.getColumnIndex("ParameterLabel")));
                metaData.setMetaInputType(cursor.getString(cursor.getColumnIndex("FieldInputType")));
                metaData.setFieldParameterOperands(cursor.getString(cursor.getColumnIndex("field_parameter_operands")));
                arrayList.add(metaData);*/
            } while (cursor.moveToNext());
            cursor.close();
        }
        return fieldParameterId;
    }

    public String getFieldParamLabel(String fieldParamId) {

        String paramLabel = null;

        String query = "select ParameterLabel from s_MetaData where FieldParameterID = " + fieldParamId;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                paramLabel = cursor.getString(0);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return paramLabel;
    }

    public ArrayList<MetaData> getMetaDataOnOperand(String fieldParameterOperand) {
        ArrayList<MetaData> arrayList = new ArrayList<>();

        String query = "select MobileAppID, FieldParameterID, ParameterLabel, FieldInputType, " +
                "ValueType, field_parameter_operands, LovID, RowOrder, parent_parameter_id, " +
                "enable_parameter_notes, showLast2 from s_MetaData where field_parameter_operands = "
                + fieldParameterOperand;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                MetaData metaData = new MetaData();
                metaData.setCurrentFormID(Integer.parseInt(cursor.getString(0)));
                metaData.setMetaParamID(cursor.getInt(1));
                metaData.setMetaParamLabel(cursor.getString(2));
                metaData.setMetaInputType(cursor.getString(3));
                metaData.setMetaValueType(cursor.getString(4));
                metaData.setFieldParameterOperands(cursor.getString(5));
                metaData.setMetaLovId(cursor.getInt(6));
                metaData.setMetaRowOrder(cursor.getInt(7));
                metaData.setParentParameterId(cursor.getLong(8));

                arrayList.add(metaData);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return arrayList;
    }
};