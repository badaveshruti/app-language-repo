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

import qnopy.com.qnopyandroid.requestmodel.SSite;
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole;
import qnopy.com.qnopyandroid.responsemodel.newLovData;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class SiteUserRoleDataSource {

    //SiteUserRole
    final String KEY_UserID = "UserID";

    final String KEY_SiteID = "SiteID";
    final String KEY_favouriteStatus = "favouriteStatus";


    //Role
    final String KEY_Role = "Role";
    final String KEY_RoleDesc = "RoleDescription";

    //common
    final String KEY_RoleID = "RoleID";
    final String KEY_Notes = "Notes";
    final String KEY_ModifiedDate = "ModifiedDate";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_Createdby = "Createdby";
    final String KEY_SyncStatus = "SyncStatus";
    final String KEY_Status = "Status";

    private static final String TAG = "SiteUserRoleDataSource";

    public SQLiteDatabase database;

    @Inject
    public SiteUserRoleDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

//
//    public void insertSiteUserRoleArray(SSiteUserRole[] sroleArray) {
//        UserDataSource userSource = new UserDataSource();
//
//        for (int i = 0; i < sroleArray.length; i++) {
////			insertSiteUserRole (sroleArray[i]);
//            userSource.storeUser(sroleArray[i].getUser());
//        }
//        insertSiteUserRole(sroleArray);
//    }


    public void updateroleid(int siteid, String userid, int roleid, int oldroleid) {
        Cursor c = null;

        String query = "update s_SiteUserRole set RoleID =" + roleid + ", SyncStatus = 0  where " +
                "SiteID =" + siteid + " and UserID =" + userid + " and RoleID =" + oldroleid;


        Log.i(TAG, " updateroleid() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            // ret=c.getCount();
            Log.i(TAG, " updateroleid() for set result count:" + c.getCount());
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


    public SSiteUserRole isSiteAssigned(int siteid, String userid, int roleid) {
        SSiteUserRole siteUserRole = new SSiteUserRole();
        List<SSiteUserRole> userRoles = new ArrayList<>();
        String sql = "select UserID, SiteID, RoleID from s_SiteUserRole where SiteID =" + siteid + " and UserID=" + userid;


        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // siteUserRole=new SSiteUserRole();
                    siteUserRole.setUserId(cursor.getInt(0));
                    siteUserRole.setSiteId(cursor.getInt(1));
                    siteUserRole.setRoleId(cursor.getInt(2));
                    // userRoles.add(siteUserRole);

                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return siteUserRole;
    }

    public String getUserRole(String siteId, String userId) {
        SSiteUserRole siteUserRole = new SSiteUserRole();
        List<SSiteUserRole> userRoles = new ArrayList<>();
        String sql = "select RoleID from s_SiteUserRole where UserID = " + userId + " and SiteID = " + siteId;

        String userRole = "";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    userRole = cursor.getString(0);
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRole;
    }

    long insertSiteUserRole(SSiteUserRole[] sroleList) {
        String sql = "insert into s_SiteUserRole(UserID, SiteID, RoleID, Notes, CreationDate, ModifiedDate, Createdby) values (?, ?, ?, ?, ?, ?, ?);";
        long ret = 0;
        if (sroleList == null) {
            return -1;
        }
        try {
            database.beginTransaction();

            SQLiteStatement stmt = database.compileStatement(sql);
            for (int i = 0; i < sroleList.length; i++) {
                SSiteUserRole srole = sroleList[i];

                if (srole == null) {
                    return 0;
                }

                stmt.bindLong(1, srole.getUserId());
                stmt.bindLong(2, srole.getSiteId());
                if (srole.getRoleId() != null) {
                    stmt.bindLong(3, srole.getRoleId());
                } else {
                    stmt.bindLong(3, 0);
                }
//                if (srole.getNotes() != null) {
//                    stmt.bindString(4, srole.getNotes());
//                }
                if (srole.getCreationDate() != null) {
                    stmt.bindLong(5, srole.getCreationDate());
                }
                if (srole.getModifiedDate() != null) {
                    stmt.bindLong(6, srole.getModifiedDate());
                }
                if (srole.getCreatedBy() != null) {
                    stmt.bindLong(7, srole.getCreatedBy());
                }

                long entryID = stmt.executeInsert();
                stmt.clearBindings();
            }
            database.setTransactionSuccessful();

        } catch (Exception e) {
            System.out.println("syncc time" + e.getLocalizedMessage());
        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public long insertSiteUserRole(SSiteUserRole role) {
        long ret = 0;
        if (role == null) {
            return -1;
        }
        ContentValues values = new ContentValues();

        values.put(KEY_RoleID, role.getRoleId());
        values.put(KEY_UserID, role.getUserId());
        values.put(KEY_SiteID, role.getSiteId());
        values.put(KEY_favouriteStatus, "0");
        values.put(KEY_Status, "1");

        try {
            ret = database.insert(DbAccess.TABLE_SITE_USER_ROLE, null, values);
        } catch (Exception e) {
            System.out.println("gggg" + DbAccess.TABLE_ROLE + "exception mesg=" + e.getLocalizedMessage());
        }
        return ret;
    }

    public int bulkinsertSiteUserRole(List<SSiteUserRole> mRetSiteUserRoleList) {

        int ret = 0;
        try {
            database.beginTransaction();
            for (SSiteUserRole app : mRetSiteUserRoleList) {
                ContentValues values = new ContentValues();
                if (app != null) {
                    values.put(KEY_RoleID, app.getRoleId());
                    values.put(KEY_SiteID, app.getSiteId());
                    values.put(KEY_UserID, app.getUserId());
                    try {
                        ret = (int) database.insert(DbAccess.TABLE_SITE_USER_ROLE, null, values);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storeBulkMobileAppList");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int storeBulkSiteUserRole(List<SSiteUserRole> mRetSiteUserRoleList) {

        int ret = 0;
        try {
            String[] arrColumns = {KEY_RoleID, KEY_SiteID, KEY_UserID, SiteDataSource.KEY_Status,
                    KEY_favouriteStatus};
            boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_SITE_USER_ROLE,
                    database);
            String columns = Util.splitArrayToString(arrColumns);

            String sql = "INSERT INTO " + DbAccess.TABLE_SITE_USER_ROLE + "(" + columns + ")"
                    + " VALUES(?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();

            for (SSiteUserRole app : mRetSiteUserRoleList) {
                if (app != null) {

                    if (app.isInsert() || isTableEmpty) {
                        if (app.getRoleId() != null)
                            statement.bindLong(1, app.getRoleId());
                        else statement.bindNull(1);

                        if (app.getSiteId() != null)
                            statement.bindLong(2, app.getSiteId());
                        else statement.bindNull(2);

                        if (app.getUserId() != null)
                            statement.bindLong(3, app.getUserId());
                        else statement.bindNull(3);

                        if (app.getStatus() != null)
                            statement.bindString(4, app.getStatus());
                        else statement.bindNull(4);

                        statement.bindString(5, app.isFavourite() ? "1" : "0");

                        ret = (int) statement.executeInsert();
                        statement.clearBindings();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(KEY_RoleID, app.getRoleId());
                        values.put(SiteDataSource.KEY_Status, app.getStatus());

                        String whereClause = KEY_SiteID + " = ?" + KEY_UserID + " = ?";
                        String[] whereArgs = new String[]{app.getSiteId() + "",
                                app.getUserId() + ""};
                        ret = database.update(DbAccess.TABLE_SITE_USER_ROLE, values,
                                whereClause, whereArgs);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in storeBulkMobileAppList");
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public long insertSiteUserRoleFromSite(List<SSite> sites, int userID) {
        long ret = 0;
        if (sites == null) {
            return -1;
        }
        try {
            database.beginTransaction();
            for (int i = 0; i < sites.size(); i++) {
                ContentValues values = new ContentValues();
                SSite site = sites.get(i);
                if (site != null) {
                    values.put(KEY_UserID, userID);
                    values.put(KEY_SiteID, site.getSiteId());
                    values.put(KEY_RoleID, 1);
                    try {
                        ret = database.insert(DbAccess.TABLE_SITE_USER_ROLE, null, values);
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

    public long getLastModifiedDate(int userID) {
        Cursor cursor = null;
        long modifiedDate = 0;
        String query = "Select MAX(ModifiedDate) from s_SiteUserRole where UserID = " + userID;
        cursor = database.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            modifiedDate = cursor.getLong(0);
        }
        return modifiedDate;
    }

    public long insertUserData(int userid, int loginuserid, int siteid) {
        long ret = 0;
        long millis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(KEY_SiteID, siteid); // Contact Name
        values.put(KEY_UserID, userid); // Contact Phone Number
        values.put(KEY_RoleID, 3);
        values.put(KEY_Createdby, loginuserid);
        values.put(KEY_SyncStatus, 0);
        values.put(KEY_CreationDate, millis);
        // Inserting Row
        try {
            ret = database.insert(DbAccess.TABLE_SITE_USER_ROLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public long insertSiteUserRole(int siteid, int roleid, int userid) {
        long ret = 0;
        long millis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(KEY_SiteID, siteid); // Contact Name
        values.put(KEY_UserID, userid); // Contact Phone Number
        values.put(KEY_RoleID, roleid);
        values.put(KEY_Createdby, userid);
        values.put(KEY_SyncStatus, 0);
        values.put(KEY_CreationDate, millis);
        // Inserting Row
        try {
            ret = database.insert(DbAccess.TABLE_SITE_USER_ROLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }

    public long insertsiteforuser(int userid, int siteid) {
        long ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_SiteID, siteid); // Contact Name
        values.put(KEY_UserID, userid); // Contact Phone Number
        values.put(KEY_RoleID, 3);
        // Inserting Row
        try {
            ret = database.insert(DbAccess.TABLE_SITE_USER_ROLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //database.endTransaction();

        return ret;

    }

    public List<newLovData> getAllLovItemList() {
        List<newLovData> lovItemList = new ArrayList<>();
        newLovData lovItem = new newLovData();
        String query = "select lov_item_id,lov_id,item_display_name,item_value,created_by," +
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

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lovItemList;
    }

    public List<SSiteUserRole> getAllSiteUserData() {
        List<SSiteUserRole> siteUserRoleList = new ArrayList<>();
        SSiteUserRole siteUserRole = new SSiteUserRole();

        String query = "select UserID,SiteID,RoleID,Notes,CreationDate," +
                "ModifiedDate,Createdby from s_SiteUserRole where SyncStatus = 0";

        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    siteUserRole = new SSiteUserRole();
                    siteUserRole.setUserId(cursor.getInt(0));
                    siteUserRole.setSiteId(cursor.getInt(1));
                    siteUserRole.setRoleId(cursor.getInt(2));
                    siteUserRole.setCreationDate(cursor.getLong(4));
                    siteUserRole.setModifiedDate(cursor.getLong(5));
                    siteUserRole.setCreatedBy(cursor.getInt(6));
                    siteUserRoleList.add(siteUserRole);

                } while (cursor.moveToNext());
                cursor.close();

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siteUserRoleList;

    }

    public void deleteunsyncdata() {

        Cursor c = null;
        String sql = "delete from s_SiteUserRole where SyncStatus=0";

        Log.i(TAG, " deleteunsyncdata() for set query:" + sql);

        int ret = 0;
        try {
            c = database.rawQuery(sql, null);
            Log.i(TAG, " deleteunsyncdata() for set result count:" + c.getCount());
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

    public int updatesiteuseritem() {
        Cursor c = null;

        String query = "update s_SiteUserRole set SyncStatus = '1' where SyncStatus=0";


        Log.i(TAG, " updatesiteuseritem() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            ret = c.getCount();
            Log.i(TAG, " updatesiteuseritem() for set result count:" + c.getCount());
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

    public boolean isdataforsiteuserunsynced() {
        String query = null;
        Cursor cursor;
        int count = 0;
        try {
            query = "select count(*) from s_SiteUserRole where SyncStatus=0";


            cursor = database.rawQuery(query, null);
            Log.i(TAG, "isdataunsynced() siteuserrole query=" + query);

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


    public boolean isRoleassigned(int siteid, Integer userId) {

        boolean res = false;
        String query = "select UserID, SiteID, RoleID from s_SiteUserRole where SiteID =" + siteid + " and UserID=" + userId + " and RoleID = 3";
        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            int cnt = Integer.parseInt(c.getString(0));
            res = cnt > 0;
            c.close();
        }
        return res;
    }


}
