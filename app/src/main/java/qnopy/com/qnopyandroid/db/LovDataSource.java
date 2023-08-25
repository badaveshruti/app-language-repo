package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.Lov;
import qnopy.com.qnopyandroid.clientmodel.LovItems;
import qnopy.com.qnopyandroid.requestmodel.SLovItem;
import qnopy.com.qnopyandroid.responsemodel.newLovData;
import qnopy.com.qnopyandroid.util.Util;

public class LovDataSource {

    private static final String TAG = "LovDataSource";
    final String KEY_LovID = "lov_id";
    final String KEY_LovName = "lov_name";
    final String KEY_LovDescription = "lov_description";
    final String KEY_CompanyID = "company_id";
    final String KEY_SYNCFLAG = "syncflag";
    final String KEY_SiteID = "site_id";
    final String KEY_Notes = "notes";
    final String KEY_Createdby = "created_by";
    final String KEY_CreationDate = "creation_date";
    final String KEY_ExtField1 = "ext_field1";
    final String KEY_ExtField2 = "ext_field2";
    final String KEY_ExtField3 = "ext_field3";
    final String KEY_ExtField4 = "ext_field4";
    final String KEY_ExtField5 = "ext_field5";

    final String KEY_LovItemID = "lov_item_id";
    final String KEY_LovItem_ItemDisplay_Name = "item_display_name";
    final String KEY_LovItem_ItemValue = "item_value";
    final String KEY_LovItem_ItemDescription = "item_description";
    final String KEY_LovItem_notes = "notes";
    final String KEY_LovItem_CreatedBy = "created_by";
    final String KEY_LovItem_CreationDate = "creation_date";
    final String KEY_LovItem_ExtField1 = "ext_field1";
    final String KEY_LovItem_ExtField2 = "ext_field2";
    final String KEY_LovItem_ExtField3 = "ext_field3";
    final String KEY_LovItem_ExtField4 = "ext_field4";
    final String KEY_LovItem_ExtField5 = "ext_field5";
    final String KEY_LovItem_CompanyID = "company_id";
    final String KEY_LovItem_SiteID = "site_id";

    // TODO: 29-Nov-16
    final String KEY_Parent_LovItemID = "parentLovItemId";
    final String KEY_FormID = "formId";
    final String KEY_L_ItemID = "l_item_id";


    public SQLiteDatabase database;
    Context mcontext;
    String companyID;

    public LovDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
        mcontext = context;
        companyID = Util.getSharedPreferencesProperty(mcontext, GlobalStrings.COMPANYID);
    }

    public int storebulkLovlist(List<Lov> lovlist) {

        int ret = 0;
        try {
            database.beginTransaction();
            for (Lov app : lovlist) {
                ContentValues values = new ContentValues();
                if (app != null) {
                    values.put(KEY_LovID, app.getLovId());
                    values.put(KEY_LovName, app.getLovName());
                    values.put(KEY_LovDescription, app.getLovDescription());
                    values.put(KEY_CompanyID, app.getCompanyId());
                    values.put(KEY_SiteID, app.getSiteId());
                    values.put(KEY_Createdby, app.getCreatedBy());
                    values.put(KEY_CreationDate, app.getCreationDate());
                    values.put(KEY_Notes, app.getNotes());
                    values.put(KEY_ExtField1, app.getExtField1());
                    values.put(KEY_ExtField2, app.getExtField2());
                    values.put(KEY_ExtField3, app.getExtField3());
                    values.put(KEY_ExtField4, app.getExtField4());
                    values.put(KEY_ExtField5, app.getExtField5());

                    try {
                        ret = (int) database.insert(DbAccess.TABLE_S_LOV, null, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storebulkLovlist");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long storeBulkBindLovList(List<Lov> lovList) {
        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_S_LOV,
                database);
        long ret = 0;
        try {
            String[] arrColumns = {KEY_LovID, KEY_LovName, KEY_LovDescription, KEY_CompanyID, KEY_SiteID,
                    KEY_Createdby, KEY_CreationDate, KEY_Notes, KEY_ExtField1, KEY_ExtField2,
                    KEY_ExtField3, KEY_ExtField4, KEY_ExtField5, SiteDataSource.KEY_Status};

            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_S_LOV + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();

            for (Lov app : lovList) {
                if (app != null) {
                    if (app.isInsert() || isTableEmpty) {
                        if (app.getLovId() != null)
                            statement.bindLong(1, app.getLovId());
                        else
                            statement.bindNull(1);

                        if (app.getLovName() != null)
                            statement.bindString(2, app.getLovName());
                        else
                            statement.bindNull(2);

                        if (app.getLovDescription() != null)
                            statement.bindString(3, app.getLovDescription());
                        else
                            statement.bindNull(3);

                        if (app.getCompanyId() != null)
                            statement.bindLong(4, app.getCompanyId());
                        else
                            statement.bindNull(4);

                        statement.bindLong(5, app.getSiteId());

                        if (app.getCreatedBy() != null)
                            statement.bindLong(6, app.getCreatedBy());
                        else
                            statement.bindNull(6);

                        if (app.getCreationDate() != null)
                            statement.bindLong(7, app.getCreationDate());
                        else
                            statement.bindNull(7);

                        if (app.getNotes() != null)
                            statement.bindString(8, app.getNotes());
                        else
                            statement.bindNull(8);

                        if (app.getExtField1() != null)
                            statement.bindString(9, app.getExtField1());
                        else
                            statement.bindNull(9);

                        if (app.getExtField2() != null)
                            statement.bindString(10, app.getExtField2());
                        else
                            statement.bindNull(10);

                        if (app.getExtField3() != null)
                            statement.bindString(11, app.getExtField3());
                        else
                            statement.bindNull(11);

                        if (app.getExtField4() != null)
                            statement.bindString(12, app.getExtField4());
                        else statement.bindNull(12);

                        if (app.getExtField5() != null)
                            statement.bindString(13, app.getExtField5());
                        else statement.bindNull(13);

                        if (app.getStatus() != null)
                            statement.bindString(14, app.getStatus());
                        else statement.bindNull(14);

                        ret = statement.executeInsert();
                        statement.clearBindings();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(KEY_LovName, app.getLovName());
                        values.put(KEY_LovDescription, app.getLovDescription());
                        values.put(KEY_CompanyID, app.getCompanyId());
                        values.put(KEY_SiteID, app.getSiteId());
                        values.put(KEY_Createdby, app.getCreatedBy());
                        values.put(KEY_CreationDate, app.getCreationDate());
                        values.put(KEY_Notes, app.getNotes());
                        values.put(KEY_ExtField1, app.getExtField1());
                        values.put(KEY_ExtField2, app.getExtField2());
                        values.put(KEY_ExtField3, app.getExtField3());
                        values.put(KEY_ExtField4, app.getExtField4());
                        values.put(KEY_ExtField5, app.getExtField5());
                        values.put(SiteDataSource.KEY_Status, app.getStatus());

                        String whereClause = KEY_LovID + " = ?";
                        String[] whereArgs = new String[]{app.getLovId() + ""};
                        ret = database.update(DbAccess.TABLE_S_LOV, values,
                                whereClause, whereArgs);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storebulkLovlist");
        } finally {
            database.endTransaction();
        }
        return ret;
    }


    public boolean ischecklovavailable(int lovid, String name, String companyid) {
        Cursor c = null;
        boolean res = false;


        String query = "select item_value, item_display_name, lov_item_id" +
                " from s_lov_items where item_display_name = '" + name + "' and company_id =" + companyid + " and lov_id=" + lovid;

        c = database.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {

            if (c.getCount() > 0) {
                return true;
            }

            c.close();
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return res;
    }


    public int get_navigateToformID(int lovID, String item_display_name) {
        Cursor cursor = null;
        int navigateTo_formID = 0;

        String query = "Select " + KEY_FormID + " from s_lov_items " +
                " where lov_id=" + lovID + " and item_display_name=\"" + item_display_name + "\"";
        cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            navigateTo_formID = cursor.getInt(0);
        }
        return navigateTo_formID;
    }


    public void storeLovItemsListWithStmt(List<SLovItem> lovItemList) {
        SLovItem lovItem = null;
        long ret = 0;
//        String sql = "insert into s_lov_items(lov_item_id, lov_id, item_display_name, item_value, item_description, notes, created_by, creation_date, ext_field1, ext_field2, ext_field3, ext_field4, ext_field5) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        // TODO: 29-Nov-16
        String sql = "insert into s_lov_items(" + KEY_L_ItemID + "," + KEY_LovID + "," + KEY_LovItem_ItemDisplay_Name + "," + KEY_LovItem_ItemValue + "," +
                "" + KEY_LovItem_SiteID + "," + KEY_LovItem_CompanyID + "," + KEY_Parent_LovItemID + "," + KEY_FormID + ") values (?, ?, ?, ?, ?, ?,?,?);";
        long t = System.currentTimeMillis();
        if (lovItemList != null) {
            try {
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sql);
                for (int i = 0; i < lovItemList.size(); i++) {
                    lovItem = lovItemList.get(i);
                    if (lovItem == null) {
                        continue;
                    }

                    stmt.bindLong(1, lovItem.getL_itemId());
                    stmt.bindLong(2, lovItem.getLovId());

                    if (lovItem.getItemDisplayName() != null) {
                        stmt.bindString(3, lovItem.getItemDisplayName());
                    }

                    if (lovItem.getItemValue() != null) {
                        stmt.bindString(4, lovItem.getItemValue());
                    }

                    if (lovItem.getSiteId() != null) {
                        stmt.bindString(5, lovItem.getSiteId() + "");
                    }
                    if (lovItem.getCompanyId() != null) {
                        stmt.bindString(6, lovItem.getCompanyId() + "");
                    }
                    if (lovItem.getParentLovItemId() != null) {
                        stmt.bindString(7, lovItem.getParentLovItemId());
                    }
                    if (lovItem.getFormId() != null) {
                        stmt.bindString(8, lovItem.getFormId());
                    }

                    long entryID = stmt.executeInsert();
                    stmt.clearBindings();

                }
                database.setTransactionSuccessful();
                long t1 = System.currentTimeMillis();
                System.out.println("llltime taken " + (t1 - t));


            } catch (Exception e) {
                System.out.println("llltime exception "
                        + e.getLocalizedMessage());
            } finally {
                database.endTransaction();
            }
        }
    }

    public long insertlovs(String name, String value, int lovid, int userid, String temp_id,
                           String companyid, int siteid, int parentlovid) {
        long ret = 0;

        ContentValues values = new ContentValues();

        values.put(KEY_LovID, lovid);
        values.put(KEY_LovItem_ItemDisplay_Name, name);
        values.put(KEY_LovItem_ItemValue, value);
        values.put(KEY_LovItem_CreatedBy, userid);
        values.put(KEY_L_ItemID, temp_id);
        values.put(KEY_LovItem_CreationDate, System.currentTimeMillis() + "");
        values.put(KEY_CompanyID, companyid);
        values.put(KEY_SYNCFLAG, 0);
        values.put(KEY_SiteID, siteid);
        values.put(KEY_LovItem_ItemDescription, "new");
        values.put(KEY_Parent_LovItemID, parentlovid);

        //       values.put(KEY_COMPANYID, 1);
        try {
            ret = database.insert(DbAccess.TABLE_LOV_ITEMS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertlovs() exception msg=" + e.getLocalizedMessage());
        }
        return ret;

    }

    public void storeBulkLovItems(List<SLovItem> lovItemList) {
        long ret = 0;
        SLovItem lovItem = null;
        if (lovItemList != null && lovItemList.size() > 0) {
            try {
                Log.i(TAG, "storeBulkLovItems() No.of items:" + lovItemList.size());
                database.beginTransaction();
                for (int i = 0; i < lovItemList.size(); i++) {
                    lovItem = lovItemList.get(i);
                    ContentValues values = new ContentValues();

                    values.put(KEY_L_ItemID, lovItem.getL_itemId());
                    values.put(KEY_LovID, lovItem.getLovId());

                    values.put(KEY_LovItem_ItemDisplay_Name, lovItem.getItemDisplayName());
                    values.put(KEY_LovItem_ItemValue, lovItem.getItemValue());
                    values.put(KEY_LovItem_SiteID, lovItem.getSiteId());
                    values.put(KEY_LovItem_CompanyID, lovItem.getCompanyId());
                    values.put(KEY_Parent_LovItemID, lovItem.getParentLovItemId());
                    values.put(KEY_FormID, lovItem.getFormId());

                    try {
                        ret = database.insert(DbAccess.TABLE_LOV_ITEMS, null, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
            }
        }
    }

    public void storeBulkBindLovItems(List<SLovItem> lovItemList) {
        long ret = 0;
        if (lovItemList != null && lovItemList.size() > 0) {
            try {

                String[] arrColumns = {KEY_L_ItemID, KEY_LovID, KEY_LovItem_ItemDisplay_Name,
                        KEY_LovItem_ItemValue, KEY_LovItem_SiteID, KEY_LovItem_CompanyID, KEY_Parent_LovItemID,
                        KEY_FormID, SiteDataSource.KEY_Status};

                String columns = Util.splitArrayToString(arrColumns);

                String sql = "INSERT INTO " + DbAccess.TABLE_LOV_ITEMS + "(" + columns + ")"
                        + " VALUES(?,?,?,?,?,?,?,?,?)";
                SQLiteStatement statement = database.compileStatement(sql);
                boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_LOV_ITEMS,
                        database);
                database.beginTransaction();
                for (SLovItem lovItem : lovItemList) {

                    if (lovItem.isInsert() || isTableEmpty) {
                        if (lovItem.getL_itemId() != null)
                            statement.bindLong(1, lovItem.getL_itemId());
                        else
                            statement.bindNull(1);

                        if (lovItem.getLovId() != null)
                            statement.bindLong(2, lovItem.getLovId());
                        else
                            statement.bindNull(2);

                        if (lovItem.getItemDisplayName() != null)
                            statement.bindString(3, lovItem.getItemDisplayName());
                        else
                            statement.bindNull(3);

                        if (lovItem.getItemValue() != null)
                            statement.bindString(4, lovItem.getItemValue());
                        else
                            statement.bindNull(4);

                        if (lovItem.getSiteId() != null)
                            statement.bindLong(5, lovItem.getSiteId());
                        else
                            statement.bindNull(5);

                        if (lovItem.getCompanyId() != null)
                            statement.bindLong(6, lovItem.getCompanyId());
                        else
                            statement.bindNull(6);

                        if (lovItem.getParentLovItemId() != null)
                            statement.bindString(7, lovItem.getParentLovItemId());
                        else
                            statement.bindNull(7);

                        if (lovItem.getFormId() != null)
                            statement.bindString(8, lovItem.getFormId());
                        else
                            statement.bindNull(8);

                        if (lovItem.getStatus() != null)
                            statement.bindString(9, lovItem.getStatus());
                        else statement.bindNull(9);

                        ret = statement.executeInsert();
                        statement.clearBindings();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(KEY_LovID, lovItem.getLovId());

                        values.put(KEY_LovItem_ItemDisplay_Name, lovItem.getItemDisplayName());
                        values.put(KEY_LovItem_ItemValue, lovItem.getItemValue());
                        values.put(KEY_LovItem_SiteID, lovItem.getSiteId());
                        values.put(KEY_LovItem_CompanyID, lovItem.getCompanyId());
                        values.put(KEY_Parent_LovItemID, lovItem.getParentLovItemId());
                        values.put(KEY_FormID, lovItem.getFormId());
                        values.put(SiteDataSource.KEY_Status, lovItem.getStatus());

                        String whereClause = KEY_L_ItemID + " = ?";
                        String[] whereArgs = new String[]{lovItem.getL_itemId() + ""};
                        ret = database.update(DbAccess.TABLE_LOV_ITEMS, values,
                                whereClause, whereArgs);
                    }
                }
                database.setTransactionSuccessful();
                Log.i(TAG, "storeBulkLovItems() stored:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
            }
        }
    }

    public void updateset(String locationID, int eventID, int AppID, int siteID, int curSetID) {
        Cursor c = null;

        String query = "update d_FieldData set ExtField1=CAST(ExtField1 AS INTEGER)-1 where LocationID=" + locationID + " and EventID =" + eventID +
                " and SiteID=" + siteID + " and MobileAppID=" + AppID +
                " and CAST(ExtField1 AS INTEGER)>" + curSetID;

        Log.i(TAG, " updateset() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updateset() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }


    public int updatelovitem(newLovData newLovData) {
        Cursor c = null;

        String query = "update s_lov_items set syncFlag = 1," + KEY_L_ItemID + "=" + newLovData.getLovItemId()
                + " where " + KEY_L_ItemID + "=" + newLovData.getExtField5() + "";

        Log.i(TAG, " updatelovitem() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updatelovitem() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return ret;
    }

    public int updatelovitemsyncflag() {
        Cursor c = null;

        String query = "update s_lov_items set syncFlag = 1 where " + KEY_SYNCFLAG + "=0";


        Log.i(TAG, " updatelovitemsyncflag() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updatelovitemsyncflag() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return ret;
    }

    public void updateLovforItem(String companyid, String userid, Integer lovitemId, String name, String value, int siteid) {

        Cursor c = null;
        String modificationdate = String.valueOf(System.currentTimeMillis());

        String query = "update s_lov_items set item_display_name ='" + name + "', item_value ='" + value + "'" + "," +
                " company_id=" + companyid + ", syncFlag = 0, modified_by =" + userid + ", modification_date ="
                + modificationdate + ",site_id=" + siteid + " where lov_item_id =" + lovitemId;

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updateLovforItem() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }


    public int deleteLovItem(String companyid, Integer lovitemId) {
        int ret = 0;
        String whereClause = KEY_CompanyID + "=? AND " + KEY_LovItemID + "=?";
        String[] whereArgs = new String[]{companyid, lovitemId + ""};
        try {
            ret = database.delete(DbAccess.TABLE_LOV_ITEMS, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteLovItem() error:" + e.getMessage());
        }
        return ret;
    }

    public void updateSyncFlag(int companyid, Integer lovItemID) {

        Cursor c = null;
        String query = "update s_lov_items set syncFlag = 0 where l_item_id=" + lovItemID;

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            ret = c.getCount();
            Log.i(TAG, " updateSyncFlag() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

    }


    public void updateProjectforLovItem(Integer lovitemid, String userid, int siteid, String cmanyid) {
        Cursor c = null;

        String modificationdate = String.valueOf(System.currentTimeMillis());

        String query = "update s_lov_items set site_id =" + siteid + ", " +
                "company_id=" + cmanyid + "," +
                "syncFlag = 0, modified_by =" + userid + "," +
                " modification_date =" + modificationdate +
                " where lov_item_id =" + lovitemid;


        Log.i(TAG, " updateProjectforLovItem() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            ret = c.getCount();
            Log.i(TAG, " updateProjectforLovItem() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

    }


    //10-Jul-17
    public List<Chip> getACLovList(int lovID, String siteID, String companyID, int parentlovItemID) {

        List<Chip> nVPair = new ArrayList<Chip>();

        String query;
        Log.i(TAG, "getACLovList() :site:" + siteID + " company:" + companyID + " Lov:" + lovID);

        query = "select distinct item_display_name, item_value,lov_item_id from " +
                " s_lov_items where lov_id = " + lovID +
                " and (company_id = " + companyID + " or " +
                " company_id=0) and (site_id = " + siteID +
                " or site_id=0) and parentLovItemId=" + parentlovItemID
                + " and item_value is not null and Status = 1";

        Log.i(TAG, "getACLovList() query:" + query);
        try {
            Cursor cur = database.rawQuery(query, null);

            if (cur != null && cur.moveToFirst()) {


                do {
                    String key = cur.getString(0);
                    String val = cur.getString(1);
                    String item_id = cur.getInt(2) + "";
                    Log.i(TAG, "getACLovList() Add item:key:" + key + " value:" + val + " lov_item_id:" + item_id);

                    nVPair.add(new Chip(item_id, key, val));

                } while (cur.moveToNext());

                cur.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getACLovList()  exception:" + e.getLocalizedMessage());
        }
        return nVPair;
    }

    public List<LovItems> getItemValues(int lovid, String companyid, String siteID) {
        List<LovItems> data = new ArrayList<LovItems>();
        LovItems lov = new LovItems();
        String query;
        query = " select distinct item_value, item_display_name, lov_item_id, site_id,company_id ,syncflag " +
                " from s_lov_items where lov_id = " + lovid + " and (company_id =" + companyid + "  or company_id =0) " +
                " and (site_id =" + siteID + "  or site_id = 0) and (item_value is not null or item_value != '')";

        try {

            Cursor cur = database.rawQuery(query, null);

            if (cur != null && cur.moveToFirst()) {

                do {
                    lov = new LovItems();
                    lov.setItemValue(cur.getString(0));
                    lov.setItemDisplayName(cur.getString(1));
                    lov.setLovItemID(cur.getInt(2));
                    lov.setSite_id(cur.getInt(3));
                    lov.setCompany_id(cur.getInt(4));
                    lov.setSyncFlag(cur.getInt(5));

                    if (!lov.getItemValue().isEmpty() && !lov.getItemDisplayName().isEmpty())
                        data.add(lov);
                } while (cur.moveToNext());

                cur.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLovItemValues() Exception :" + e.getLocalizedMessage());
        }
        return data;
    }

    public List<LovItems> getLovItemValues(int lovID, int siteID, int companyID, int parentlovItemID) {
        //  System.out.println("hehehehe3" + lovID);
        List<LovItems> data = new ArrayList<LovItems>();

        String query;
//        if (parentlovItemID>0){

        // TODO: 30-Nov-16
        query = "select distinct lov_id, item_display_name, item_value, item_description from " +
                "s_lov_items where lov_id=" + lovID + " and (company_id = " + companyID + " or" +
                " company_id=0) and (site_id = " + siteID +
                " or site_id=0) and parentLovItemId=" + parentlovItemID + " and item_value is not null";

        try {

            long d = System.currentTimeMillis();
            //Cursor cur = database.query(DbAccess.TABLE_LOV_ITEMS, column, whereClause, whereArgs, null, null, orderby);
            Cursor cur = database.rawQuery(query, null);

            //  System.out.println("LovID:" + lovID);
            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    LovItems dataList = cursorToLovItem(cur);
                    data.add(dataList);
                    cur.moveToNext();
                    // System.out.println("hehehehe6 " + dataList);
                }

                cur.close();
                long d1 = System.currentTimeMillis() - d;
                //   System.out.println("timetaken112... " + d1);
            } else {
                // System.out.println("getLovItemValues():" + "cursor in lov is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getLovItemValues()Exception :" + e.getLocalizedMessage());
        }
        return data;
    }


    private LovItems cursorToLovItem(Cursor cur) {
        LovItems data = new LovItems();
        int columnCount = 0;

        //data.setLovItemID(cur.getInt(columnCount++));
        data.setLovID(cur.getInt(columnCount++));
        data.setItemDisplayName(cur.getString(columnCount++));
        data.setItemValue(cur.getString(columnCount++));
        data.setItemDescription(cur.getString(columnCount));

        return data;
    }

    public List<newLovData> getAllLovItemList() {
        List<newLovData> lovItemList = new ArrayList<>();
        newLovData lovItem = new newLovData();

        String query = "select l_item_id,lov_id,item_display_name,item_value,created_by," +
                "creation_date,ext_field5,site_id,modified_by,modification_date," +
                "company_id from s_lov_items where syncflag=0";
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lovItem = new newLovData();
                    lovItem.setLovItemId(cursor.getInt(0));
                    lovItem.setLovId(cursor.getInt(1));
                    lovItem.setItemDisplayName(cursor.getString(2));
                    lovItem.setItemValue(cursor.getString(3));
                    lovItem.setCreatedBy(cursor.getInt(4));
                    lovItem.setCreationDate(cursor.getLong(5));
                    lovItem.setExtField5(cursor.getString(6));
                    lovItem.setSiteId(cursor.getInt(7));
                    lovItem.setModifiedBy(cursor.getInt(8));
                    lovItem.setModificationDate(cursor.getLong(9));
                    lovItem.setCompanyId(cursor.getInt(10));
                    lovItemList.add(lovItem);

                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllLovItemList() Error:" + e.getMessage());
        }
        return lovItemList;
    }

    public List<Lov> getLovList() {
        List<Lov> lovList = new ArrayList<>();
        Lov lov = new Lov();
        String query = "select lov_id, lov_name, site_Id from s_lov";
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lov = new Lov();
                    lov.setLovId(cursor.getInt(0));
                    lov.setLovName(cursor.getString(1));
                    lov.setSiteId(cursor.getInt(2));
                    lovList.add(lov);

                } while (cursor.moveToNext());
                cursor.close();

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lovList;
    }

    public String getKeyForLovValue(int lovID, String value, String siteID) {

        String key = null;
        String[] column = new String[]{"item_display_name"};
        String whereClause = "lov_id=? and item_value=? and (company_id = ? or company_id=0) and (site_id =?  or site_id=0)";
        String[] whereArgs = new String[]{"" + lovID, value, companyID, siteID};

        try {

            Cursor cur = database.query(DbAccess.TABLE_LOV_ITEMS, column, whereClause, whereArgs, null, null, null);

            if (cur != null && cur.moveToFirst()) {

                key = cur.getString(0);
                System.out.println("lovkey " + key);

                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getKeyForLovValue exception:" + e.getLocalizedMessage());
        }
        return key;
    }

    // TODO: 12-Jan-16
    public LinkedHashMap<String, String> getLovNameValuePair(int lovID, String siteID) {


        LinkedHashMap<String, String> nVPair = new LinkedHashMap<String, String>();

        String query;

        query = "select distinct item_display_name, item_value from " +
                " s_lov_items where lov_id = " + lovID +
                " and (company_id = " + companyID + " or " +
                " company_id=0) and (site_id = " + siteID +
                " or site_id=0) and item_value is not null";

        try {
            long d = System.currentTimeMillis();
            //Cursor cur = database.query(DbAccess.TABLE_LOV_ITEMS, column, whereClause, whereArgs, null, null, orderby);
            Cursor cur = database.rawQuery(query, null);

            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    String key = cur.getString(0);
                    String val = cur.getString(1);

                    nVPair.put(key, val);
                    cur.moveToNext();
                    //   Log.i("getLovNameValuePair", "site:" + siteID + " company:" + companyID + " Lov:" + lovID + " Lov Name Value Pair:" + nVPair);
                }

                cur.close();
                long d1 = System.currentTimeMillis() - d;
                // System.out.println("time taken Lov Name Value Pair(milisec)... " + d1);
            } else {
                //System.out.println("getLovNameValuePair():" + "cursor in lov is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getLovNameValuePair Exception():" + e.getLocalizedMessage());
        }
        return nVPair;
    }


    // TODO: 18-Jul-17 MOVED FROM META DATA SOURCE
    public String getLovValueForKey(int lovID, String value) {

        String key = null;
        String[] column = new String[]{"item_value"};
        String whereClause = "lov_id=? and item_display_name=?";
        String[] whereArgs = new String[]{"" + lovID, value};

        try {

            Cursor cur = database.query(DbAccess.TABLE_LOV_ITEMS, column, whereClause, whereArgs, null, null, null);

            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    key = cur.getString(0);
                    System.out.println("lovkey " + key);
                    break;
                }

                cur.close();
            } else {
                //   System.out.println("mmmm" + "cursor in lovkey is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getLovValueForKey exception:" + e.getLocalizedMessage());
        }
        return key;
    }

    // TODO: 18-Jul-17 MOVED FROM META DATA SOURCE
    public int getparentLovItemID(int lovID, String displayname) {
        Cursor cursor = null;
        int childLovID = 0;
        if (displayname != null) {
            String query = "select distinct l_item_id from s_lov_items where lov_id=" + lovID + " and item_display_name=\"" + displayname + "\"";
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                childLovID = cursor.getInt(0);
                cursor.close();
            }
        }
        return childLovID;
    }


    public boolean checkvalue(String value, int lovid) {
        Cursor c = null;
        boolean res = false;


        String query = "select count(*) from s_lov_items where item_value = '" + value + "'" + " and lov_id=" + lovid;
        c = database.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {

            c = database.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                int cnt = Integer.parseInt(c.getString(0));
                res = cnt > 0;
                c.close();
            }
            return res;
        }

        return false;

    }

    public boolean checkname(String name, int lovid) {
        Cursor c = null;
        boolean res = false;


        String query = "select count(*) from s_lov_items where item_display_name = '" + name + "'" + " and lov_id=" + lovid;
        c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            int cnt = Integer.parseInt(c.getString(0));
            res = cnt > 0;
            c.close();
        }
        return res;

    }

    public boolean isdataunsynced() {
        String query = null;
        Cursor cursor;
        int count = 0;
        try {
            query = "select count(*) from s_lov_items where syncflag=0";


            cursor = database.rawQuery(query, null);
            Log.i(TAG, "isdataunsynced() lovitem query=" + query);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isFieldDataAvailableToSync() exception:" + e.getLocalizedMessage());
            return false;
        }
        return false;

    }

    public void deleteunsyncdataforlov() {

        Cursor c = null;
        String sql = "delete from s_lov_items where syncflag=0";

        Log.i(TAG, " deleteunsyncdataforlov() :" + sql);

        int ret = 0;
        try {
            c = database.rawQuery(sql, null);
            Log.i(TAG, " deleteunsyncdataforlov() for result count:" + c.getCount());
            c.moveToFirst();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

    }

    public String getLovItemIdFromItemName(String itemDisplayName) {
        Cursor c = null;
        String lovItemId = null;

        itemDisplayName = itemDisplayName.replaceAll("'", "");
        Log.e("itemDisplayName", "getLovItemIdFromItemName: " + itemDisplayName);

        String query = "select l_item_id from s_lov_items where item_display_name like '%" + itemDisplayName + "%'";
        c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            lovItemId = c.getString(0);
            c.close();
        }
        return lovItemId;
    }

}
