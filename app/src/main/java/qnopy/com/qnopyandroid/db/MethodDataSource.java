package qnopy.com.qnopyandroid.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.CoCBottles;

/**
 * Created by QNOPY on 3/18/2018.
 */

public class MethodDataSource {
    private static final String TAG = "COCMethod ";
    String KEY_Methods = "methods";
    Context mContext;
    public SQLiteDatabase database;

    public MethodDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public List<String> getMethods() {

        List<String> cocMethodsList = new ArrayList<>();
        String method = null;
        //    String query="select count(*) from d_FieldData where EventID="+eventID+" and SiteID="+siteID+" and StringValue!=null and (FieldParameterID!=15 and FieldParameterID!=25)";

        String query = "select DISTINCT methods from cm_methods";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                //CocMethods cocMethods=new CocMethods();
                //  cocMethods.setMethods(c.getString(0));
                method = c.getString(0);
                cocMethodsList.add(method);
                // c.close();

            } while (c.moveToNext());
            c.close();


        }
        return cocMethodsList;

    }


    public List<CoCBottles> getBottles(String methodids) {
        List<CoCBottles> cocBottleList = new ArrayList<>();
        String method = null;

        String query = "SELECT distinct container || ' ' || sugg_qty as bottles FROM cm_methods where cm_methods_id IN(" + methodids + ")";

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                CoCBottles bottleItem = new CoCBottles();
                //  cocMethods.setMethods(c.getString(0));
                bottleItem.setBottleName(c.getString(0));
                cocBottleList.add(bottleItem);
                // c.close();

            } while (c.moveToNext());
            c.close();


        }
        return cocBottleList;

    }


    public List<Chip> getMethodsChipList() {

        List<Chip> nVPair = new ArrayList<Chip>();

        String query;

        query = "select DISTINCT methods,cm_methods_id from cm_methods";

        Log.i(TAG, "getMethodsChipList() query:" + query);
        try {
            Cursor cur = database.rawQuery(query, null);

            if (cur != null && cur.moveToFirst()) {

                do {
                    String key = cur.getString(0);
                    String val = cur.getString(0);
                    String item_id = cur.getInt(1) + "";
                    Log.i(TAG, "getMethodsChipList() Add item:key:" + key + " value:" + val + " lov_item_id:" + item_id);
                    nVPair.add(new Chip(item_id, key, val));

                } while (cur.moveToNext());

                cur.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getMethodsChipList()  exception:" + e.getLocalizedMessage());
        }
        return nVPair;
    }


}
