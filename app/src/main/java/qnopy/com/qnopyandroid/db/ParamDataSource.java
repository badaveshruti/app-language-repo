package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Set;

import qnopy.com.qnopyandroid.requestmodel.RFieldParameter;
import qnopy.com.qnopyandroid.requestmodel.RFieldParameterUnit;
import qnopy.com.qnopyandroid.requestmodel.RUnitConverter;

public class ParamDataSource {


    //for r_FieldParameter
    final String KEY_ParameterLabel = "ParameterLabel";
    final String KEY_Cas = "Cas";

    //for r_FieldParameterUnit
    final String KEY_Units = "Units";
    final String KEY_ModifiedDate = "ModifiedDate";

    //for r_UnitConverter
    final String KEY_FromUnits = "FromUnits";
    final String KEY_ToUnits = "ToUnits";
    final String KEY_Multiplier = "Multiplier";

    //common
    final String KEY_FieldParameterID = "FieldParameterID";
    final String KEY_Notes = "Notes";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_Createdby = "Createdby";

    public SQLiteDatabase database;

    public ParamDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        if (database==null){
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public long insertFieldParameter(List<RFieldParameter> fp) {
        long ret = 0;
        try {
            database.beginTransaction();
            for (int i = 0; i < fp.size(); i++) {

                RFieldParameter param = fp.get(i);

                ContentValues values = new ContentValues();
                values.put(KEY_FieldParameterID, param.getFieldParameterId());
                values.put(KEY_ParameterLabel, param.getParameterLabel());
                values.put(KEY_Cas, param.getCas());
                values.put(KEY_CreationDate, param.getCreationDate());
                values.put(KEY_ModifiedDate, param.getModifiedDate());
                values.put(KEY_Createdby, param.getCreatedBy());

                try {
                    ret = database.insert(DbAccess.TABLE_FIELD_PARAMETER, null, values);
                } catch (Exception e) {
                    System.out.println("gggg" + DbAccess.TABLE_FIELD_PARAMETER + "exception mesg=" + e.getLocalizedMessage());
                }

            }
            database.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            database.endTransaction();
        }
        return ret;

    }

    public void insertFieldParamUnitSet(Set<RFieldParameterUnit> unitSet, int fieldParameterID) {

        if (unitSet == null) {
            return;
        }

        for (RFieldParameterUnit unit : unitSet) {
            insertFieldParameterUnit(unit, fieldParameterID);
        }


    }

    public long insertFieldParameterUnit(RFieldParameterUnit unit, int fieldParameterID) {
        long ret = 0;
        if (unit == null) {
            return -1;
        }
        ContentValues values = new ContentValues();

        values.put(KEY_FieldParameterID, fieldParameterID);
        values.put(KEY_Units, unit.getUnits());
        values.put(KEY_Notes, unit.getNotes());
        values.put(KEY_CreationDate, unit.getCreationDate());
        values.put(KEY_ModifiedDate, unit.getModifiedDate());
        values.put(KEY_Createdby, unit.getCreatedBy());

        try {
            ret = database.insert(DbAccess.TABLE_PARAM_UNIT, null, values);
        } catch (Exception e) {
            System.out.println("gggg" + DbAccess.TABLE_PARAM_UNIT + "exception " +
                    "mesg=" + e.getLocalizedMessage());
        }
        return ret;
    }

    public void storeUnitConverterArray(RUnitConverter[] unitConArray) {

        if (unitConArray == null) {
            return;
        }
        for (int i = 0; i < unitConArray.length; i++) {
            insertUnitConverter(unitConArray[i]);
        }
    }

    public long insertUnitConverter(RUnitConverter unitcon) {
        long ret = 0;
        if (unitcon == null) {
            return -1;
        }
        ContentValues values = new ContentValues();

        values.put(KEY_FromUnits, unitcon.getFromUnits());
        values.put(KEY_ToUnits, unitcon.getToUnits());
        values.put(KEY_Multiplier, unitcon.getMultiplier());
        values.put(KEY_Notes, unitcon.getNotes());
        values.put(KEY_CreationDate, unitcon.getCreationDate());
        values.put(KEY_Createdby, unitcon.getCreatedBy());
        try {
            ret = database.insert(DbAccess.TABLE_UNIT_CONVERTER, null, values);
        } catch (Exception e) {
            System.out.println("gggg" + DbAccess.TABLE_UNIT_CONVERTER + "exception mesg=" + e.getLocalizedMessage());
        }
        return ret;
    }

    int getModTimeUnitConverter() {

        String query = "SELECT MAX(KEY_ModifiedDate) AS max_id FROM " + DbAccess.TABLE_UNIT_CONVERTER;
        Cursor cursor = database.rawQuery(query, null);

        int time = 0;
        if (cursor.moveToFirst()) {
            do {
                time = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return time;
    }


}
