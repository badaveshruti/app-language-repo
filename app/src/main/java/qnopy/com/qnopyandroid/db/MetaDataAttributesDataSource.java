package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.MetaDataAttributes;

public class MetaDataAttributesDataSource {
    private static final String KEY_SITE_ID = "SiteID";
    private static final String KEY_MOBILE_APP_ID = "MobileAppID";
    private static final String KEY_LOCATION_ID = "LocationID";
    private static final String KEY_FIELD_PARAMETER_ID = "FieldParameterID";
    private static final String KEY_PARAMETER_ID = "ParameterID";
    private static final String KEY_DESIRED_UNITS = "DesiredUnits";
    private static final String KEY_EXT_FIELD_1 = "ext_field1";
    private static final String KEY_EXT_FIELD_2 = "ext_field2";
    private static final String KEY_EXT_FIELD_3 = "ext_field3";
    private static final String KEY_EXT_FIELD_4 = "ext_field4";
    private static final String KEY_EXT_FIELD_5 = "ext_field5";
    private static final String KEY_EXT_FIELD_6 = "ext_field6";
    private static final String KEY_EXT_FIELD_7 = "ext_field7";
    private static final String KEY_FIELD_PARAMETER_OPERANDS = "field_parameter_operands";
    private static final String KEY_ENABLE_PARAMETER_NOTES = "enable_parameter_notes";
    private static final String KEY_SHOW_LAST2 = "showLast2";
    private static final String KEY_PARAMETER_HINT = "parameter_hint";
    private static final String KEY_PARENT_PARAMETER_ID = "parent_parameter_id";
    private static final String KEY_PERCENT_DIFFERENCE = "percent_difference";
    private static final String KEY_ROUTINE_ID = "routine_id";
    private static final String KEY_CREATION_DATE = "CreationDate";
    private static final String KEY_MODIFIED_DATE = "ModifiedDate";
    private static final String KEY_CREATED_BY = "Createdby";
    private static final String KEY_MULTI_NOTE = "multiNote";
    private static final String KEY_MANDATORY_FIELD = "mandatoryField";
    private static final String KEY_HIDE_FIELD = "hide";
    private static final String KEY_straightDifference = "straightDifference";
    private static final String KEY_fieldAction = "fieldAction";
    private static final String KEY_fontStyle = "fontStyle";
    private static final String KEY_formula = "formula";

    private Context mContext;
    private SQLiteDatabase database;
    private String TAG = "MetaDataAttributes";

    public MetaDataAttributesDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        SQLiteDatabase.releaseMemory();
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public void insertMetadataAttributes(List<MetaDataAttributes> dataList) {
        long ret = 0;

        database.beginTransaction();
        MetaDataAttributes attribute;
        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_META_DATA_ATTRIBUTES,
                database);
        try {

            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                attribute = dataList.get(i);
                values.put(KEY_PARAMETER_ID, attribute.getFieldParameterId());
                values.put(KEY_DESIRED_UNITS, attribute.getDesiredUnits());
                values.put(KEY_EXT_FIELD_1, attribute.getExt_field1());
                values.put(KEY_EXT_FIELD_2, attribute.getExt_field2());
                values.put(KEY_EXT_FIELD_3, attribute.getExt_field3());
                values.put(KEY_EXT_FIELD_4, attribute.getExt_field4());
                values.put(KEY_EXT_FIELD_5, attribute.getExt_field5());
                values.put(KEY_EXT_FIELD_6, attribute.getExt_field6());
                values.put(KEY_EXT_FIELD_7, attribute.getExt_field7());
                values.put(KEY_FIELD_PARAMETER_OPERANDS, attribute.getFormula());//fieldParamOperand field will not have data as formula will have all expressions
                values.put(KEY_ENABLE_PARAMETER_NOTES, attribute.isEnable_parameter_notes());
                values.put(KEY_SHOW_LAST2, attribute.isShowLast2());
                values.put(KEY_PARAMETER_HINT, attribute.getParameterHint());
                values.put(KEY_PARENT_PARAMETER_ID, attribute.getParent_parameter_id());

                if (attribute.getPercentDifference() != 0.0)
                    values.put(KEY_PERCENT_DIFFERENCE, attribute.getPercentDifference());

                if (attribute.getStraightDifference() != 0.0)
                    values.put(KEY_straightDifference, attribute.getStraightDifference());

                values.put(KEY_ROUTINE_ID, attribute.getRoutine_id());
                values.put(KEY_CREATION_DATE, attribute.getCreationDate());
                values.put(KEY_MODIFIED_DATE, attribute.getModifiedDate());
                values.put(KEY_CREATED_BY, attribute.getCreatedby());
                values.put(KEY_MULTI_NOTE, attribute.getMultiNote());
                values.put(KEY_MANDATORY_FIELD, attribute.getMandatoryField());
                values.put(SiteDataSource.KEY_Status, attribute.getStatus());
                values.put(KEY_HIDE_FIELD, attribute.getHide());

                values.put(KEY_fieldAction, attribute.getFieldAction());
                values.put(KEY_fontStyle, attribute.getFontStyle());
                values.put(KEY_formula, attribute.getFormula());

                if (attribute.isInsert() || isTableEmpty) {
                    values.put(KEY_SITE_ID, attribute.getSiteId());
                    values.put(KEY_MOBILE_APP_ID, attribute.getMobileAppId());
                    values.put(KEY_LOCATION_ID, attribute.getLocationId());
                    values.put(KEY_FIELD_PARAMETER_ID, attribute.getFieldParameterId());

                    ret = database.insert(DbAccess.TABLE_META_DATA_ATTRIBUTES,
                            null, values);
                } else {
                    String whereClause = KEY_SITE_ID + " = ?" + KEY_MOBILE_APP_ID + " = ?"
                            + KEY_LOCATION_ID + " = ?" + KEY_FIELD_PARAMETER_ID + " = ?";
                    String[] whereArgs = new String[]{attribute.getSiteId() + "",
                            attribute.getMobileAppId() + "", attribute.getLocationId() + "",
                            attribute.getFieldParameterId() + ""};
                    ret = database.update(DbAccess.TABLE_META_DATA_ATTRIBUTES, values,
                            whereClause, whereArgs);
                }
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("metadata attribute", e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
    }

    public MetaDataAttributes getMetaDataAttributes(int siteId, int mobAppId, int fpId) {
        MetaDataAttributes attributes = null;

        Cursor cursor = null;
        String query = "select distinct " +
                "SiteID, " +
                "MobileAppID, " +
                "LocationID, " +
                "FieldParameterID, " +
                "ParameterID, " +
                "DesiredUnits, " +
                "ext_field1, " +
                "ext_field2, " +
                "ext_field3, " +
                "ext_field4, " +
                "ext_field5, " +
                "ext_field6, " +
                "ext_field7, " +
                "field_parameter_operands, " +
                "enable_parameter_notes, " +
                "showLast2, " +
                "parameter_hint, " +
                "parent_parameter_id, " +
                "percent_difference, " +
                "routine_id, " +
                "CreationDate, " +
                "ModifiedDate, " +
                "Createdby, " +
                "multiNote, " +
                "mandatoryField, hide, fieldAction, " + KEY_straightDifference
                + ", " + KEY_fontStyle + ", " + KEY_formula
                + " from " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " where SiteID =? " +
                "and MobileAppID = ? and FieldParameterID = ?";

        String[] whereClause = new String[]{siteId + "", mobAppId + "", fpId + ""};

        try {
            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                attributes = new MetaDataAttributes();
                attributes.setSiteId(cursor.getInt(0));
                attributes.setMobileAppId(cursor.getInt(1));
                attributes.setLocationId(cursor.getInt(2));
                attributes.setFieldParameterId(cursor.getInt(3));
                attributes.setParameterID(cursor.getInt(4));
                attributes.setDesiredUnits(cursor.getString(5));
                attributes.setExt_field1(cursor.getString(6));
                attributes.setExt_field2(cursor.getString(7));
                attributes.setExt_field3(cursor.getString(8));
                attributes.setExt_field4(cursor.getString(9));
                attributes.setExt_field5(cursor.getString(10));
                attributes.setExt_field6(cursor.getString(11));
                attributes.setExt_field7(cursor.getString(12));
                attributes.setField_parameter_operands(cursor.getString(13));
                attributes.setEnable_parameter_notes(cursor.getInt(14) != 0);
                attributes.setShowLast2(cursor.getInt(15) != 0);
                attributes.setParameterHint(cursor.getString(16));
                attributes.setParameterID(cursor.getInt(17));
                attributes.setPercentDifference(cursor.getInt(18));
                attributes.setRoutine_id(cursor.getInt(19));
                attributes.setCreationDate(cursor.getLong(20));
                attributes.setModifiedDate(cursor.getLong(21));
                attributes.setCreatedby(cursor.getInt(22));
                attributes.setMultiNote(cursor.getString(23));
                attributes.setMandatoryField(cursor.getInt(24));
                attributes.setHide(cursor.getInt(25));
                attributes.setFieldAction(cursor.getString(cursor.getColumnIndexOrThrow(KEY_fieldAction)));
                attributes.setStraightDifference(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_straightDifference)));
                attributes.setFontStyle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_fontStyle)));
                attributes.setFormula(cursor.getString(cursor.getColumnIndexOrThrow(KEY_formula)));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return attributes;
    }

}
