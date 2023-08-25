package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.requestmodel.SUser;

@Singleton
public class UserDataSource {
    private static final String TAG = "UserDataSource";
    final String KEY_UserID = "UserID";
    final String KEY_UserNumber = "UserNumber";
    final String KEY_UserName = "Username";
    final String KEY_Password = "Password";
    final String KEY_ToMail = "ToMail";
    final String KEY_CCMail = "CCMail";
    final String KEY_Firstname = "Firstname";
    final String KEY_Lastname = "LastName";
    final String KEY_CompanyID = "CompanyID";
    final String KEY_Notes = "Notes";
    final String KEY_UserGuid = "UserGuid";
    final String KEY_Status = "status";
    final String KEY_CONTACTNO = "ContactNumber";
    final String KEY_USERTYPE = "UserType";
    final String KEY_USERROLE = "UserRole";
    final String KEY_USERAPPTYPE = "UserAppType";
    Context mContext;

    public SQLiteDatabase database;

    @Inject
    public UserDataSource(Context context) {
        database = DbAccess.getInstance(context).database;
        mContext = context;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public User getUser(String name) {

        String whereClause = null;
        String[] whereArgs = null;

        User user = null;

        String[] userColumns = new String[]{
                KEY_UserID, KEY_UserName, KEY_Password, KEY_ToMail, KEY_CCMail, KEY_CompanyID, KEY_UserGuid, KEY_Notes};

        whereClause = "upper(Username)=upper(?)";
        whereArgs = new String[]{"" + name};
        String orderBy = "null";
        Cursor cursor;

        try {
            cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = new User();
                    user = cursorToUser(cursor);

                } while (cursor.moveToNext());

                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getUser:" + e.getMessage());
        }
        return user;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserID(cursor.getInt(0));
        user.setUserName(cursor.getString(1));
        user.setUserPasswd(cursor.getString(2));
        user.setToMail(cursor.getString(3));
        user.setCCMail(cursor.getString(4));
        user.setCompanyID(cursor.getInt(5));
        user.setUserGuid(cursor.getString(6));
        user.setNotes(cursor.getString(7));

        return user;
    }

    public boolean isCardEnable(String userID) {

        String res = "";
        String whereClause = "UserID=? ";
        String[] whereArgs = new String[]{userID};

        String[] userColumns = new String[]{
                KEY_Notes};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getString(0);
                    Log.i(TAG, "Enable App type:" + res);
                    if (res != null && !res.isEmpty() && res.equalsIgnoreCase("city")) {
                        return true;
                    }
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isCardEnable Error:" + e.getMessage());
        }

        return false;
    }

    public long storeUser(SUser user) {
        long ret = 0;
        if (user == null) {
            return ret;
        }
        //02-Oct-16 Delete user already exist
        deleteUser(user.getUserName());

        ContentValues values = new ContentValues();
        values.put(KEY_UserID, user.getUserId());
        if (user.getUserNumber() == null) {
            user.setUserNumber("0");
        }
        values.put(KEY_UserNumber, user.getUserNumber());
        values.put(KEY_UserName, user.getUserName());
        values.put(KEY_Password, user.getPassword() == null ? "" : user.getPassword());
        values.put(KEY_ToMail, user.getToEmailList());
        values.put(KEY_CCMail, user.getCcEmailList());
        values.put(KEY_Firstname, user.getFirstName());
        values.put(KEY_Lastname, user.getLastName());
        values.put(KEY_CompanyID, user.getCompanyId());
        values.put(KEY_Notes, user.getNotes());
        values.put(KEY_UserGuid, user.getUserGuid());
        // values.put(KEY_USERTYPE,user.getUserType());
        values.put(KEY_USERROLE, user.getUserRole());
        values.put(KEY_USERAPPTYPE, user.getUserAppType());

        try {
            ret = database.insertWithOnConflict(DbAccess.TABLE_USER, null,
                    values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.i(TAG, "storeUser RET=" + ret);
        } catch (Exception e) {
            Log.e(TAG, "Exception msg=" + e.getLocalizedMessage());
        }

        return ret;
    }

    public void truncateUserTable() {

        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();

            try {
                ret = database.delete(DbAccess.TABLE_USER, null, null);
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

    public Boolean deleteUser(String userName) {

        boolean result = false;
        int res = 0;
        String whereClause = "Username=?";
        String[] whereArgs = new String[]{userName};

        res = database.delete(DbAccess.TABLE_USER, whereClause, whereArgs);

        if (res > 0) {
            result = true;
        }
        return result;
    }

    public Boolean deleteUserByUserId(String userId) {

        boolean result = false;
        int res = 0;
        String whereClause = "UserID = ?";
        String[] whereArgs = new String[]{userId};

        try {
            res = database.delete(DbAccess.TABLE_USER, whereClause, whereArgs);

            if (res > 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

/*    public Boolean deleteUserByUserId(String userId) {
        String query = "delete from " + DbAccess.TABLE_USER + " where UserID =" + userId;
        try {
            database.execSQL(query);
        } catch (Exception e) {
            System.out.println("deleteFieldData " + e.getLocalizedMessage());
        }

        return true;
    }*/

    public boolean deleteUser(String userName, String pass) {
        Log.i(TAG, "deleteUser() IN time=" + System.currentTimeMillis());

        boolean result = false;
        int res = 0;
        String whereClause = "Username=? and Password=?";
        String[] whereArgs = new String[]{userName, pass};
        try {
            Log.i(TAG, "deleteUser() username:" + userName + " Password:" + pass);

            res = database.delete(DbAccess.TABLE_USER, whereClause, whereArgs);
            Log.i(TAG, "deleteUser() Deleted user result:" + res);
            if (res > 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            Log.i(TAG, "deleteUser() Error:" + e.getMessage());

        }
        Log.i(TAG, "deleteUser() OUT time=" + System.currentTimeMillis());

        return result;
    }

    public int getUserRole(String username) {
        String query = "select distinct UserRole from s_User where  Username='" + username + "'";

        int res = 0;
        try {
            Cursor cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getInt(0);
                    Log.i(TAG, "UserRole:" + res);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public int getUserRolefromID(int userid) {
        String query = "select distinct UserRole from s_User where  UserID=" + userid;

        int res = 0;
        try {
            Cursor cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getInt(0);
                    Log.i(TAG, "UserRole:" + res);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getUserCompanyID(String userName, String pass) {

        boolean result = false;
        int res = 0;
        String whereClause = "Username=? and Password=?";
        String[] whereArgs = new String[]{userName, pass};

        String[] userColumns = new String[]{
                KEY_CompanyID};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getInt(0);
                    Log.i(TAG, "CompanyID:" + res);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return res + "";
    }

    public List<SUser> getuserList(String userID, int companyID, int siteid) {
        Cursor cursor = null;
        ArrayList<SUser> users = new ArrayList<>();
        SUser user = new SUser();

        String query = "select distinct UserID, Username from s_User where UserID " +
                "not in (select distinct UserID from s_SiteUserRole where\n" +
                " SiteID =" + siteid + " ) and CompanyID=" + companyID + " and UserID!=" + userID;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = new SUser();
                    user.setUserId(cursor.getInt(0));
                    user.setUserName(cursor.getString(1));
                    //  user.setUserType(cursor.getString(23));
                    users.add(user);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getuserList() error:" + e.getMessage());
        }
        return users;
    }

    public ArrayList<SUser> getUsersForAdmin(int siteId) {
        Cursor cursor = null;
        ArrayList<SUser> users = new ArrayList<>();
        SUser user = new SUser();

        String query = "select distinct U.UserID, U.Username, R.SiteID from s_User AS U " +
                "JOIN s_SiteUserRole AS R ON R.UserID = U.UserID where R.SiteID = " + siteId +
                " and (U.Status is null or U.Status = 1)";
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = new SUser();
                    user.setUserId(cursor.getInt(0));
                    user.setUserName(cursor.getString(1));
//                      user.setUserType(cursor.getString(23));
                    users.add(user);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getuserList() error:" + e.getMessage());
        }
        return users;
    }

    public List<SUser> getuserListtoassignproject(String userid) {

        Cursor cursor = null;
        ArrayList<SUser> users = new ArrayList<>();
        SUser user = new SUser();

        String query = "select UserID,Username,UserRole from s_User where UserID!=" + userid;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = new SUser();
                    user.setUserId(cursor.getInt(0));
                    user.setUserName(cursor.getString(1));
                    user.setUserRole(cursor.getInt(2));
                    //  user.setUserType(cursor.getString(23));
                    users.add(user);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public String getUserNameFromID(String userID) {

        String res = "";
        String whereClause = "UserID=? ";
        String[] whereArgs = new String[]{userID};

        String[] userColumns = new String[]{
                KEY_UserName};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getString(0);
                    Log.i(TAG, "UserID:" + res);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getUserNameFromID:" + e.getMessage());
        }

        return res;
    }

    public String getFirstNameFromID(String userID) {

        String res = "";
        String whereClause = "UserID=? ";
        String[] whereArgs = new String[]{userID};

        String[] userColumns = new String[]{
                KEY_Firstname};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getString(0);
                    Log.i(TAG, "UserID:" + res);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getUserNameFromID:" + e.getMessage());
        }

        return res;
    }

    public String getUserNameFromIDWithInitials(String userID) {

        String userName = "";
        String whereClause = "UserID=? ";
        String[] whereArgs = new String[]{userID};

        String[] userColumns = new String[]{
                KEY_Firstname, KEY_Lastname};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String firstName = cursor.getString(0);
                    String lastName = cursor.getString(1);
                    if ((firstName != null && !firstName.isEmpty()) && (lastName != null && !lastName.isEmpty()))
                        userName = String.valueOf(firstName.charAt(0)) + lastName.charAt(0);
                    else if (firstName != null && !firstName.isEmpty())
                        userName = String.valueOf(firstName.charAt(0));
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            userName = "";
            Log.e(TAG, "getUserNameInitials:" + e.getMessage());
        }

        return userName.toUpperCase();
    }

    public String getFullName(String userID) {

        String userName = "";
        String whereClause = "UserID=? ";
        String[] whereArgs = new String[]{userID};

        String[] userColumns = new String[]{
                KEY_Firstname, KEY_Lastname};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String firstName = cursor.getString(0);
                    String lastName = cursor.getString(1);
                    userName = firstName + " " + lastName;
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            userName = "";
            Log.e(TAG, "getUserNameInitials:" + e.getMessage());
        }

        return userName.toUpperCase();
    }

    public int getUserIDFromName(String name) {

        int res = 0;
        String whereClause = "Username=? ";
        String[] whereArgs = new String[]{name};

        String[] userColumns = new String[]{
                KEY_UserName};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    res = cursor.getInt(0);
                    Log.i(TAG, "UserID:" + res);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getUserNameFromID:" + e.getMessage());
        }

        return res;
    }

    public long storeSelfSignUpUser(SUser user) {
        long ret = 0;
        if (user == null) {
            return ret;
        }
        //02-Oct-16 Delete user already exist
        deleteUser(user.getUserName());

        ContentValues values = new ContentValues();
        values.put(KEY_UserID, user.getUserId());
        if (user.getUserNumber() == null) {
            user.setUserNumber("0");
        }
        values.put(KEY_UserNumber, user.getUserNumber());
        values.put(KEY_UserName, user.getUserName());
        values.put(KEY_Password, user.getPassword() == null ? "" : user.getPassword());
        values.put(KEY_Firstname, user.getFirstName());
        values.put(KEY_Lastname, user.getFirstName());
        values.put(KEY_CompanyID, user.getCompanyId());
        values.put(KEY_CONTACTNO, user.getContactnumber());
        values.put(KEY_ToMail, user.getPrimaryEmail());
        values.put(KEY_Status, user.getStatus());
        values.put(KEY_Notes, user.getNotes());
        values.put(KEY_UserGuid, user.getUserGuid());
        values.put(KEY_UserID, user.getUserId());

        try {
            ret = database.insertWithOnConflict(DbAccess.TABLE_USER, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            Log.i(TAG, "storeUser RET=" + ret);
            Log.i(TAG, "USERTYPE" + user.getUserType());

        } catch (Exception e) {
            if (e != null) {
                Log.e(TAG, "Exception msg=" + e.getLocalizedMessage());
            }
        }

        return ret;
    }

    public String getUserAppType(String userId) {
        String userAppType = "";

        String whereClause = "UserID=? ";
        String[] whereArgs = new String[]{userId};

        String[] userColumns = new String[]{KEY_USERAPPTYPE};
        try {
            Cursor cursor = database.query(DbAccess.TABLE_USER, userColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    userAppType = cursor.getString(0);
                    Log.i(TAG, "UserID:" + userAppType);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getUserNameFromID:" + e.getMessage());
        }

        return userAppType;
    }

    public User getCurrentUserData() {
        User user = null;
        Cursor cursor;

        try {
            cursor = database.rawQuery("select * from s_User where " +
                    "(Username is not null and Username != '') and " +
                    "(UserGuid is not null and UserGuid != '') ", null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_UserID)));
                user.setUserPasswd(cursor.getString(cursor.getColumnIndexOrThrow(KEY_Password)));
                user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_Firstname)));
                user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_Lastname)));
                user.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserName)));
                user.setUserGuid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGuid)));
                user.setCompanyID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CompanyID)));
                user.setUserAppType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERAPPTYPE)));
                user.setUserRole(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USERROLE)));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getUser:" + e.getMessage());
        }
        return user;
    }
}

