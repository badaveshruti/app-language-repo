package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.SiteFormFields;

public class SiteFormFieldsSource {

    private Context mContext;
    private SQLiteDatabase database;
    private String TAG = "SiteFormFields";

    private static final String KEY_site_form_fields_id = "site_form_fields_id";
    private static final String KEY_is_required = "is_required";
    private static final String KEY_mandatory_field = "mandatory_field";
    private static final String KEY_mobile_app_id = "mobile_app_id";
    private static final String KEY_field_parameter_id = "field_parameter_id";
    private static final String KEY_calculated_field = "calculated_field";
    private static final String KEY_formula = "formula";
    private static final String KEY_percent_diff = "percent_diff";
    private static final String KEY_creation_date = "creation_date";
    private static final String KEY_created_by = "created_by";
    private static final String KEY_modification_date = "modification_date";
    private static final String KEY_modified_by = "modified_by";
    private static final String KEY_site_id = "site_id";
    private static final String KEY_location_id = "location_id";
    private static final String KEY_desired_units = "desired_units";
    private static final String KEY_ext_field1 = "ext_field1";
    private static final String KEY_ext_field2 = "ext_field2";
    private static final String KEY_ext_field3 = "ext_field3";
    private static final String KEY_ext_field4 = "ext_field4";
    private static final String KEY_ext_field5 = "ext_field5";
    private static final String KEY_ext_field6 = "ext_field6";
    private static final String KEY_ext_field7 = "ext_field7";
    private static final String KEY_field_parameter_operands = "field_parameter_operands";
    private static final String KEY_enable_parameter_notes = "enable_parameter_notes";
    private static final String KEY_showLast2 = "showLast2";
    private static final String KEY_parameter_hint = "parameter_hint";
    private static final String KEY_parent_parameter_id = "parent_parameter_id";
    private static final String KEY_routine_id = "routine_id";
    private static final String KEY_multiNote = "multiNote";
    private static final String KEY_straight_difference = "straight_difference";
    private static final String KEY_field_action = "field_action";
    private static final String KEY_field_score = "field_score";
    private static final String KEY_font_style = "font_style";

    public SiteFormFieldsSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        SQLiteDatabase.releaseMemory();
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public void insertSiteFormFields(List<SiteFormFields> dataList) {
        long ret = 0;

        database.beginTransaction();
        SiteFormFields siteFormFields;

        try {

            for (int i = 0; i < dataList.size(); i++) {
                ContentValues values = new ContentValues();
                siteFormFields = dataList.get(i);
                values.put(KEY_site_form_fields_id, siteFormFields.getSiteFormFieldId());
                values.put(KEY_is_required, siteFormFields.getIsRequired());
                values.put(KEY_mandatory_field, siteFormFields.getMandatoryField());
                values.put(KEY_mobile_app_id, siteFormFields.getMobileAppId());
                values.put(KEY_field_parameter_id, siteFormFields.getFieldParameterId());
                values.put(KEY_calculated_field, siteFormFields.getDesiredUnits());
                values.put(KEY_formula, siteFormFields.getFormula());
                values.put(KEY_percent_diff, siteFormFields.getPercentDifference());
                values.put(KEY_creation_date, siteFormFields.getCreationDate());
                values.put(KEY_created_by, siteFormFields.getCreatedBy());
                values.put(KEY_modification_date, siteFormFields.getModificationDate());
                values.put(KEY_modified_by, siteFormFields.getModifiedBy());
                values.put(KEY_site_id, siteFormFields.getSiteId());
                values.put(KEY_location_id, siteFormFields.getLocationId());
                values.put(KEY_desired_units, siteFormFields.getDesiredUnits());
                values.put(KEY_ext_field1, siteFormFields.getExtField1());
                values.put(KEY_ext_field2, siteFormFields.getExtField2());
                values.put(KEY_ext_field3, siteFormFields.getExtField3());
                values.put(KEY_ext_field4, siteFormFields.getExtField4());
                values.put(KEY_ext_field5, siteFormFields.getExtField5());
                values.put(KEY_ext_field6, siteFormFields.getExtField6());
                values.put(KEY_ext_field7, siteFormFields.getExtField7());
                values.put(KEY_field_parameter_operands, siteFormFields.getFieldParameterOperands());
                values.put(KEY_enable_parameter_notes, siteFormFields.getEnableParameterNotes());
                values.put(KEY_showLast2, siteFormFields.getShowLast2());
                values.put(KEY_parameter_hint, siteFormFields.getParameterHint());
                values.put(KEY_parent_parameter_id, siteFormFields.getParentParameterId());
                values.put(KEY_routine_id, siteFormFields.getRoutineId());
                values.put(KEY_multiNote, siteFormFields.getMultiNotes());
                values.put(KEY_straight_difference, siteFormFields.getStraightDifference());
                values.put(KEY_field_action, siteFormFields.getFieldAction());
                values.put(KEY_field_score, siteFormFields.getFieldScore());
                values.put(KEY_font_style, siteFormFields.getFontStyle());

                ret = database.insert(DbAccess.TABLE_SITE_FORM_FIELDS,
                        null, values);
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SiteFormFieldSource", e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
    }

    public SiteFormFields getMetaDataAttributes(int siteId, int mobAppId, int fpId) {
        SiteFormFields siteFormFields = null;

        Cursor cursor = null;
        String query = "select distinct site_id, site_form_fields_id, is_required, mandatory_field, " +
                "mobile_app_id, field_parameter_id, calculated_field, formula, percent_diff, " +
                "creation_date, created_by, modification_date, modified_by, location_id, desired_units, " +
                "ext_field1, ext_field2, ext_field3, ext_field4, ext_field5, ext_field6, ext_field7, " +
                "field_parameter_operands, enable_parameter_notes, showLast2, parameter_hint, " +
                "parent_parameter_id, routine_id, multiNote, straight_difference, field_action, " +
                "field_score, font_style from " + DbAccess.TABLE_SITE_FORM_FIELDS + " where site_id =? " +
                "and mobile_app_id = ? and field_parameter_id = ?";

        String[] whereClause = new String[]{siteId + "", mobAppId + "", fpId + ""};

        try {
            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                siteFormFields = new SiteFormFields();
                siteFormFields.setSiteId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_site_id)));
                siteFormFields.setSiteFormFieldId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_site_form_fields_id)));
                siteFormFields.setIsRequired(cursor.getString(cursor.getColumnIndexOrThrow(KEY_is_required)));
                siteFormFields.setMandatoryField(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_mandatory_field)));
                siteFormFields.setMobileAppId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_mobile_app_id)));
                siteFormFields.setFieldParameterId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_field_parameter_id)));
                siteFormFields.setCalculatedField(cursor.getString(cursor.getColumnIndexOrThrow(KEY_calculated_field)));
                siteFormFields.setFormula(cursor.getString(cursor.getColumnIndexOrThrow(KEY_formula)));
                siteFormFields.setExtField1(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field1)));
                siteFormFields.setExtField2(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field2)));
                siteFormFields.setExtField3(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field3)));
                siteFormFields.setExtField4(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field4)));
                siteFormFields.setExtField5(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field5)));
                siteFormFields.setExtField6(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field6)));
                siteFormFields.setExtField7(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ext_field7)));
                siteFormFields.setFieldParameterOperands(cursor.getString(cursor.getColumnIndexOrThrow(KEY_field_parameter_operands)));
                siteFormFields.setEnableParameterNotes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_enable_parameter_notes)));
                siteFormFields.setShowLast2(cursor.getString(cursor.getColumnIndexOrThrow(KEY_showLast2)));
                siteFormFields.setParameterHint(cursor.getString(cursor.getColumnIndexOrThrow(KEY_parameter_hint)));
                siteFormFields.setParentParameterId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_parent_parameter_id)));
                siteFormFields.setRoutineId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_routine_id)));
                siteFormFields.setMultiNotes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_multiNote)));
                siteFormFields.setStraightDifference(cursor.getString(cursor.getColumnIndexOrThrow(KEY_straight_difference)));
                siteFormFields.setFieldAction(cursor.getString(cursor.getColumnIndexOrThrow(KEY_field_action)));
                siteFormFields.setFieldScore(cursor.getString(cursor.getColumnIndexOrThrow(KEY_field_score)));
                siteFormFields.setFontStyle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_font_style)));
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
        return siteFormFields;
    }
}
