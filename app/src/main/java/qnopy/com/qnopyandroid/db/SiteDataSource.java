package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.requestmodel.AddSite;
import qnopy.com.qnopyandroid.requestmodel.SSite;

@Singleton
public class SiteDataSource {

    private static final String TAG = "SiteDataSource";
    final String KEY_SiteID = "SiteID";
    final String KEY_SiteName = "SiteName";
    final String KEY_SiteNumber = "SiteNumber";
    final String KEY_Address1 = "Address1";
    final String KEY_Address2 = "Address2";
    final String KEY_MobileReportRequired = "mobileReportRequired";
    final String KEY_City = "City";
    final String KEY_State = "State";
    final String KEY_ZipCode = "ZipCode";
    final String KEY_CompanyName = "CompanyName";
    final String KEY_GeoTrackerID = "GeoTrackerID";
    final String KEY_EPAID = "EPAID";
    final String KEY_StartDate = "StartDate";
    final String KEY_EndDate = "EndDate";
    public static final String KEY_Status = "Status";
    final String KEY_SiteType = "SiteType";
    final String KEY_Latitude = "Latitude";
    final String KEY_Longitude = "Longitude";
    final String KEY_ClientName = "clientName";
    final String KEY_Z = "Z";
    final String KEY_ExtField1 = "ExtField1";
    final String KEY_ExtField2 = "ExtField2";
    final String KEY_ExtField3 = "ExtField3";
    final String KEY_Notes = "Notes";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModifiedDate = "ModifiedDate";
    final String KEY_Createdby = "Createdby";


    public SQLiteDatabase database;
    Context mContext;

    @Inject
    public SiteDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public String getSiteNamefromID(int siteid) {
        String sql = "select distinct SiteName from s_Site where SiteID =" + siteid;
        String name = null;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null && cur.moveToFirst()) {
                name = cur.getString(0);
                cur.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteDetails:" + e.getMessage());

        }
        return name;
    }

    public List<Site> getAllSitesForUser(int UserID) {

        List<Site> sites = new ArrayList<Site>();

        String query = "select distinct S.SiteID, S.SiteName, S.SiteType, S.Latitude, S.Longitude," +
                "(select max(ifnull(modificationDate, creationDate)) from d_FieldData " +
                "where SiteID = S.SiteID) as lastUpdatedDate, R.Status from s_Site as S JOIN " +
                "s_SiteUserRole as R on S.SiteID=R.SiteID and R.UserID=? and (R."
                + SiteDataSource.KEY_Status + " IS NULL or R." + SiteDataSource.KEY_Status + "=1)";
        String[] whereArgs = new String[]{"" + UserID};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Site site = cursorToSite(cursor);
                    sites.add(site);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(sites, new Comparator<Site>() {
            @Override
            public int compare(Site s1, Site s2) {
                return Long.compare(s2.getLastUpdatedDate(), s1.getLastUpdatedDate());
            }
        });

        return sites;
    }

    public boolean isSiteExistForUser(String userID, String siteId) {

        String query = "select count(*) from (select distinct S.SiteID, S.SiteName, S.SiteType, " +
                "R.Status from s_Site as S JOIN " +
                "s_SiteUserRole as R on S.SiteID=R.SiteID and R.UserID= ? " +
                "and (R.Status IS NULL or R.Status =1)) where SiteID = ?";

        String[] whereArgs = new String[]{userID, siteId};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //the above method getAllSitesForUser is same only return value is different
    public HashMap<Integer, Site> getAllSitesForTask(int userId) {

        HashMap<Integer, Site> sites = new HashMap<>();

        String query = "select S.SiteID, S.SiteName, S.SiteType from s_Site as S JOIN " +
                "s_SiteUserRole as R on S.SiteID=R.SiteID and R.UserID=? and (S."
                + SiteDataSource.KEY_Status + " IS NULL or S." + SiteDataSource.KEY_Status + "=1)";
        String[] whereArgs = new String[]{"" + userId};
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                int cnt = cursor.getCount();
                do {

                    Site site = new Site();
                    site.setSiteID(cursor.getInt(0));
                    site.setSiteName(cursor.getString(1));
                    site.setSiteType(cursor.getString(2));

                    sites.put(site.getSiteID(), site);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sites;
    }

    public List<String> getAllSiteListForUser(int UserID) {


        List<String> sites = new ArrayList<String>();

        String query = "select S.SiteName from s_Site as S JOIN s_SiteUserRole as R on S.SiteID=R.SiteID and R.UserID=?";
        String[] whereArgs = new String[]{"" + UserID};
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                int cnt = cursor.getCount();

                do {
                    String siteName = cursor.getString(0);
                    sites.add(siteName);
                } while (cursor.moveToNext());

                Log.i(TAG, "No. of Sites:" + cnt);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return sites;
    }

    public List<Site> getSitesForUser(String userid) {
        List<Site> siteList = new ArrayList<>();

        Site site = new Site();
        String query = "select distinct SiteID, SiteName from s_Site  where SiteID  not in (select distinct SiteID from s_SiteUserRole where UserID =" + userid + ")";
        Cursor cur = null;
        try {
            cur = database.rawQuery(query, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    site = new Site();
                    site.setSiteID(cur.getInt(0));
                    site.setSiteName(cur.getString(1));
                    siteList.add(site);
                } while (cur.moveToNext());
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siteList;
    }

    public List<Site> getSitesForConstructionApp(String userid) {
        int UserID = Integer.parseInt(userid);
        List<Site> sites = new ArrayList<Site>();

        String query = "select S.SiteID, S.SiteName, S.SiteType, S.Latitude, S.Longitude from s_Site as S JOIN " +
                "s_SiteUserRole as R on S.SiteID=R.SiteID and R.UserID=?";
        String[] whereArgs = new String[]{"" + UserID};
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                int cnt = cursor.getCount();
                Log.e("siteCount", "getSitesForConstructionApp: " + cnt);
                do {
                    Site site = cursorToSite(cursor);
                    sites.add(site);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sites;
    }

    public List<Site> getSiteListforaddLov(String userid) {

        List<Site> sitelist = new ArrayList<>();
        Site site = new Site();
        String sql = "select distinct S.SiteID, S.SiteName from s_Site AS S JOIN s_SiteUserRole AS R ON R.UserID=" + userid;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    site = new Site();
                    site.setSiteID(cur.getInt(0));
                    site.setSiteName(cur.getString(1));
                    sitelist.add(site);
                } while (cur.moveToNext());
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sitelist;
    }

    private Site cursorToSite(Cursor cursor) {
        Site site = new Site();
        site.setSiteID(cursor.getInt(0));
        site.setSiteName(cursor.getString(1));
        site.setSiteType(cursor.getString(2));
        site.setLatitude(cursor.getDouble(3));
        site.setLongitude(cursor.getDouble(4));
        site.setStatus(cursor.getString(5));

        try {
            site.setLastUpdatedDate(cursor.getLong(5));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return site;
    }

    public long storeSite(SSite ssite) {
        long ret = 0;

        if (ssite == null) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_SiteID, ssite.getSiteId());
        values.put(KEY_SiteName, ssite.getSiteName());
        values.put(KEY_SiteNumber, ssite.getSiteNumber());
        values.put(KEY_ClientName, ssite.getClientName());
        values.put(KEY_Address1, ssite.getAddress1());
        values.put(KEY_City, ssite.getCity());
        values.put(KEY_State, ssite.getState());
        values.put(KEY_ZipCode, ssite.getZipCode());
        values.put(KEY_Latitude, ssite.getLatitude());
        values.put(KEY_Longitude, ssite.getLongitude());
        values.put(KEY_SiteType, ssite.getSiteType());
        values.put(KEY_Status, ssite.getStatus());

        try {
            ret = database.insert(DbAccess.TABLE_SITES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to Store Sites:" + e.getMessage());
        }

        return ret;
    }

    public long storeForAddSite(AddSite asite) {
        long ret = 0;

        if (asite == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_SiteID, asite.getSiteId());
        values.put(KEY_SiteName, asite.getSiteName());
        values.put(KEY_Latitude, asite.getLatitude());
        values.put(KEY_Longitude, asite.getLongitude());

        try {

            ret = database.insert(DbAccess.TABLE_SITES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to Store Sites:" + e.getMessage());
        }

        return ret;
    }

    public int storeBulkSiteList(List<SSite> ssite) {
        int ret = 0;

        if (ssite == null) {
            return -1;
        }

        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_SITES,
                database);

        database.beginTransaction();
        try {
            for (SSite site : ssite) {
                ContentValues values = new ContentValues();
                values.put(KEY_SiteName, site.getSiteName());
                values.put(KEY_Latitude, site.getLatitude());
                values.put(KEY_Longitude, site.getLongitude());
                values.put(KEY_Address1, site.getAddress1());
                values.put(KEY_Address2, site.getAddress2());
                values.put(KEY_MobileReportRequired, site.getMobileReportRequired());
                values.put(KEY_SiteType, site.getSiteType());
                values.put(KEY_ClientName, site.getClientName());
                values.put(KEY_Status, site.getStatus());

                if (site.isInsert() || isTableEmpty) {
                    values.put(KEY_SiteID, site.getSiteId());
                    ret = (int) database.insert(DbAccess.TABLE_SITES, null, values);
                } else {
                    String whereClause = KEY_SiteID + " = ?";
                    String[] whereArgs = new String[]{site.getSiteId() + ""};
                    try {
                        ret = database.update(DbAccess.TABLE_SITES, values, whereClause, whereArgs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "update Site Exception:" + e.getLocalizedMessage());
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to Store Sites:" + e.getMessage());
        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public String getSiteDetails(int siteID) {
        String sql = "select SiteName from s_Site where SiteID = " + siteID;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteDetails:" + e.getMessage());
        }
        String site = null;
        if (cur != null) {
            cur.moveToFirst();
            site = cur.getString(0);
        }
        return site;
    }

    public boolean isSiteTypeDefault(int siteID) {
        String sql = "select SiteType from s_Site where SiteID = " + siteID;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null) {
                cur.moveToFirst();
                if (cur.getCount() > 0) {
                    String siteType = cur.getString(0);
                    if (siteType != null && siteType.equalsIgnoreCase("personal"))
                        return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteType:" + e.getMessage());
            return false;
        }

        return false;
    }


    public boolean isSiteTypeNoLoc(int siteID) {
        String sql = "select SiteType from s_Site where SiteID = " + siteID;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null) {
                cur.moveToFirst();
                if (cur.getCount() > 0) {
                    String siteType = cur.getString(0);
                    if (siteType != null && siteType.equalsIgnoreCase("no_loc"))
                        return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteType:" + e.getMessage());
            return false;
        }

        return false;
    }

    public boolean isSiteTypeDemo(int siteID) {
        String sql = "select SiteType from s_Site where SiteID = " + siteID;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null) {
                cur.moveToFirst();
                if (cur.getCount() > 0) {
                    String siteType = cur.getString(0);
                    if (siteType != null && siteType.equalsIgnoreCase("demo"))
                        return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteType:" + e.getMessage());
            return false;
        }

        return false;
    }

    public boolean isSiteTypeTimeSheet(int siteID) {
        String sql = "select SiteType from s_Site where SiteID = " + siteID;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null) {
                cur.moveToFirst();
                String siteType = cur.getString(0);
                if (siteType != null && siteType.equalsIgnoreCase("timesheet"))
                    return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteType:" + e.getMessage());
            return false;
        }

        return false;
    }

    public int getSiteIDfrmName(String name) {
        String sql = "select SiteID from s_Site where SiteName = '" + name + "'";
        int siteID = 0;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
            if (cur != null && cur.moveToFirst()) {
                siteID = cur.getInt(0);
                cur.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteDetails:" + e.getMessage());

        }

        return siteID;
    }

    public String getSiteMobileReportRequiredStatus(int siteID) {
        String sql = "select mobileReportRequired from s_Site where SiteID = " + siteID;
        Cursor cur = null;
        try {
            cur = database.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception to get SiteDetails:" + e.getMessage());

        }
        String siteMobileReportRequired = null;
        if (cur != null && cur.moveToFirst()) {
            do {
                siteMobileReportRequired = cur.getString(0);
            } while (cur.moveToNext());
            cur.close();
        }
        return siteMobileReportRequired;
    }

    public List<Site> getAllSitesForUser(String userid) {

        List<Site> siteList = new ArrayList<>();

        Site site = new Site();
        Cursor cur = null;

        String newQuery = "select a.SiteID, a.SiteName, a.Status, a.SiteType, a.Latitude, a.Longitude, b.favouriteStatus " +
                "from s_Site as a " +
                "LEFT JOIN " +
                "s_SiteUserRole as b " +
                "on a.SiteID =b.SiteID " +
                "where a.SiteID in (select distinct SiteID from s_SiteUserRole where UserID = " + userid + " ) " +
                "and UserID = " + userid + " " +
                "group by a.SiteID, a.SiteName, a.Status, a.SiteType, a.Latitude, a.Longitude "; /*+
                "order by favouriteStatus desc ";*/
        try {
            cur = database.rawQuery(newQuery, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    site = new Site();
                    site.setSiteID(cur.getInt(0));
                    site.setSiteName(cur.getString(1));
                    site.setStatus(cur.getString(2));
                    site.setSiteType(cur.getString(3));
                    site.setLatitude(cur.getDouble(4));
                    site.setLongitude(cur.getDouble(5));
                    site.setFavStatus(cur.getString(6).equals("1"));
                    siteList.add(site);
                } while (cur.moveToNext());
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siteList;
    }

    public Site getSiteDetails(String siteId, String userId) {

        Site site = new Site();
        Cursor cur = null;

        String newQuery = "select distinct a.SiteID, a.SiteName, a.Status, a.SiteType, a.Latitude, " +
                "a.Longitude, b.favouriteStatus from s_Site as a LEFT JOIN s_SiteUserRole as b " +
                "on a.SiteID =b.SiteID where a.SiteID = " + siteId + " and b.SiteID = " + siteId +
                " and UserID = " + userId;
        try {
            cur = database.rawQuery(newQuery, null);
            if (cur != null && cur.moveToFirst()) {
                site.setSiteID(cur.getInt(0));
                site.setSiteName(cur.getString(1));
                site.setStatus(cur.getString(2));
                site.setSiteType(cur.getString(3));
                site.setLatitude(cur.getDouble(4));
                site.setLongitude(cur.getDouble(5));
                site.setFavStatus(cur.getString(6).equals("1"));
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return site;
    }

    public String getAllDemoSitesIds(String userid) {

        String siteIds = "";

        String newQuery = "select GROUP_CONCAT(distinct a.SiteID) from s_Site as a LEFT JOIN " +
                "s_SiteUserRole as b on a.SiteID = b.SiteID  where a.SiteID in (select distinct " +
                "SiteID from s_SiteUserRole where UserID = " + userid + ") and SiteType = 'demo' ";

        try {
            Cursor cur = database.rawQuery(newQuery, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    siteIds = cur.getString(0);
                } while (cur.moveToNext());
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siteIds;
    }

    public void updateFavStatus(@NotNull String siteId, @NotNull String userId, @NotNull Boolean favStatus) {

        String fav = "0";
        if (favStatus)
            fav = "1";

        String updateProjectSql = "update s_SiteUserRole set favouriteStatus = " + fav
                + " where SiteID = " + siteId + " and UserId = " + userId;
        Log.e(TAG, "updateFavStatus: " + updateProjectSql);
        try {
            database.execSQL(updateProjectSql);
            Log.e(TAG, "updateFavStatus: Success " + siteId);
        } catch (Exception e) {
            Log.e(TAG, "updateFavStatus: " + e.getMessage());
        }
    }
}
