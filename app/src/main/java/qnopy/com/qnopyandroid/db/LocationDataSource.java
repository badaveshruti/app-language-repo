package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.MapLocation;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;
import qnopy.com.qnopyandroid.requestmodel.SLocation;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.ui.activity.FormActivity;
import qnopy.com.qnopyandroid.ui.task.TaskTabActivity;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class LocationDataSource {
    private static final String TAG = "LocationDataSource";
    final String KEY_LocationID = "LocationID";
    final String KEY_ClientLocationID = "ClientLocationID";
    final String KEY_SiteID = "SiteID";
    final String KEY_Location = "Location";
    final String KEY_LocationTypeID = "LocationTypeID";
    final String KEY_LocationAlias = "LocationAlias";
    final String KEY_Status = "Status";
    final String KEY_LocationDesc = "LocationDesc";
    final String KEY_Latitude = "Latitude";
    final String KEY_Longitude = "Longitude";
    final String KEY_SyncFlag = "SyncFlag";
    final String KEY_TOC = "TOC";
    final String KEY_AOC = "AOC";
    final String KEY_Aquiferzone = "Aquiferzone";
    final String KEY_InstallDate = "InstallDate";
    final String KEY_CasingType = "CasingType";
    final String KEY_StartScreen = "StartScreen";
    final String KEY_EndScreen = "EndScreen";
    final String KEY_DTB = "DTB";
    final String KEY_ExtField1 = "ExtField1";
    final String KEY_ExtField2 = "ExtField2";
    final String KEY_ExtField3 = "ExtField3";
    final String KEY_ExtField4 = "ExtField4";
    final String KEY_ExtField5 = "ExtField5";
    final String KEY_ExtField6 = "ExtField6";
    final String KEY_ExtField7 = "ExtField7";
    final String KEY_Notes = "Notes";
    final String KEY_CreationDate = "CreationDate";
    final String KEY_ModifiedDate = "ModifiedDate";
    final String KEY_Createdby = "Createdby";
    final String KEY_LocInstruction = "loc_instruction";
    final String KEY_LocFormHeader = "loc_form_header";
    final String KEY_WellDiameter = "well_diameter";
    final String KEY_LOCATIONTYPE = "LocationType";
    final String KEY_formDefault = "FormDefault";
    final String KEY_location_tabs = "location_tabs";
    Context mContext;

    public SQLiteDatabase database;

    @Inject
    public LocationDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

    public int setDataStoredFlag(int siteID, String locationID) {

        Log.i(TAG, "setDataStoredFlag() IN time=" + System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put("ExtField7", "1");
        String whereClause = "SiteID=? AND LocationID=?";
        String[] whereArgs = new String[]{"" + siteID, "" + locationID};
        int ret = database.update(DbAccess.TABLE_LOCATION, values, whereClause, whereArgs);

        Log.i(TAG, "setDataStoredFlag() Ret value for setDataStoredFlag = " + ret);
        Log.i(TAG, "setDataStoredFlag() OUT time=" + System.currentTimeMillis());

        return ret;
    }

    public List<Location> getAllLocation(int siteID, int rollAppID) {

        List<Location> locationList = new ArrayList<Location>();
        Cursor cursor = null;
        try {
            String query;

            query = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude,ifnull(wo.CocFlag,0) as CocFlag ,wo.UserID from s_Location s LEFT JOIN  s_work_order_task wo \n" +
                    "on s.LocationID = wo.LocationID where SiteID=" + siteID + " and s.LocationType like '%" + rollAppID + "%'\n" +
                    "union\n" +
                    "\n" +
                    "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude,ifnull(wo.CocFlag,0) as CocFlag ,wo.UserID from s_Location s LEFT JOIN  s_work_order_task wo \n" +
                    "on s.LocationID = wo.LocationID where SiteID=" + siteID + " and s.LocationType is null";

            query = " select distinct s.Location,s.LocationType,s.LocationID, s.SiteID , s.loc_form_header," +
                    " s.loc_instruction,  s.ExtField7, s.Latitude,s.Longitude ,IFNuLL(wo.coc_flag,0) as CocFlag" +
                    " from s_Location s  LEFT JOIN  cm_coc_details wo on" +
                    " s.LocationID = wo.location_id where SiteID=" + siteID + " and (s.LocationType like '%" + rollAppID + "%'" +
                    " or s.LocationType is null or s.LocationType = '')";

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = cursorToLocationForMap(cursor);

                    locationList.add(loc);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationList;
    }


    public List<Location> getAllLocationWithAttribute(int siteID, int rollAppID, String attributeName, String attributeValue) {

        List<Location> locationList = new ArrayList<Location>();
        Cursor cursor = null;
        try {
            String query;


            query = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude,ifnull(wo.CocFlag,0) as CocFlag ,wo.UserID from s_Location s LEFT JOIN  s_work_order_task wo \n" +
                    "on s.LocationID = wo.LocationID where SiteID=" + siteID + " and s.LocationType like '%" + rollAppID + "%'\n" +
                    "union\n" +
                    "\n" +
                    "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude,ifnull(wo.CocFlag,0) as CocFlag ,wo.UserID from s_Location s LEFT JOIN  s_work_order_task wo \n" +
                    "on s.LocationID = wo.LocationID where SiteID=" + siteID + " and s.LocationType is null";

            query = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID , s.loc_form_header," +
                    " s.loc_instruction,  s.ExtField7, s.Latitude,s.Longitude ,IFNuLL(wo.coc_flag,0) as CocFlag" +
                    " from s_Location s " +
                    " INNER JOIN s_LocationAttribute a on s.LocationID = a.locationID  and a.attributeName = '" + attributeName + "' and a.attributeValue = '" + attributeValue + "'" +
                    " LEFT JOIN  cm_coc_details wo on" +
                    " s.LocationID = wo.location_id where SiteID=" + siteID + " and (s.LocationType like '%" + rollAppID + "%'" +
                    " or s.LocationType is null)";

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = cursorToLocationForMap(cursor);

                    locationList.add(loc);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationList;
    }

    public List<Location> getAllLocationWithMultipleAttribute(int siteID, int rollAppID, String attributeName, String attributeValue, HashMap<String, String> outputMap) {

        List<Location> locationList = new ArrayList<Location>();
        Cursor cursor = null;
        try {
            String query = "";
            if (GlobalStrings.mHashMapContainsSameAttributeKey > 0) {
                for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                    String AttributeName = entry.getKey();
                    String AttributeValue = entry.getValue();

                    StringTokenizer st = new StringTokenizer(AttributeName, "|");
                    String Name = st.nextToken();
                    //Toast.makeText(mContext, ""+attrName[0], Toast.LENGTH_SHORT).show();
                    Log.e("attrName", "setCheckedList: " + Name + " ----------------------- " + AttributeName);

                    query = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID , s.loc_form_header," +
                            " s.loc_instruction,  s.ExtField7, s.Latitude,s.Longitude ,IFNuLL(wo.coc_flag,0) as CocFlag" +
                            " from s_Location s " +
                            " INNER JOIN s_LocationAttribute a on s.LocationID = a.locationID " +
                            "and a.attributeName = '" + Name + "' and a.attributeValue = '" + AttributeValue + "'" +
                            " LEFT JOIN  cm_coc_details wo on" +
                            " s.LocationID = wo.location_id where SiteID=" + siteID
                            + " and (s.LocationType like '%" + rollAppID + "%'" +
                            " or s.LocationType is null or s.LocationType = '') and s.FormDefault  = 0 and (s." +
                            SiteDataSource.KEY_Status + " IS NULL or s." + SiteDataSource.KEY_Status + "=1)";

                    cursor = database.rawQuery(query, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Location loc = cursorToLocationForMap(cursor);

                            locationList.add(loc);

                        } while (cursor.moveToNext());
                        // make sure to close the cursor
                        cursor.close();
                    }
                }

            } else {
                for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                    String AttributeName = entry.getKey();
                    String AttributeValue = entry.getValue();

                    StringTokenizer st = new StringTokenizer(AttributeName, "|");
                    String Name = st.nextToken();
                    //Toast.makeText(mContext, ""+attrName[0], Toast.LENGTH_SHORT).show();
                    Log.e("attrName", "setCheckedList: " + Name + " ----------------------- " + AttributeName);

                    if (query.equals("")) {
                        query = "select distinct a.locationID from s_LocationAttribute a " +
                                "INNER JOIN s_Location b on a.locationID = b.LocationID where " +
                                "attributeName = '" + Name + "' and b.SiteID = " + siteID
                                + " and attributeValue = '" + AttributeValue + "'";
                    } else {
                        query = "select distinct a.locationID from s_LocationAttribute a where " +
                                "attributeName = '" + Name + "' and attributeValue = '" + AttributeValue + "'" +
                                "and  a.locationID in(" + query + ")";
                    }
                }

                cursor = database.rawQuery(query, null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationforMapWithAttribute(cursor);

                        locationList.add(loc);

                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }
                locationList = getLocationsDetalis(locationList, siteID, rollAppID);
            }

        } catch (Exception e) {
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationList;
    }

    private List<Location> getLocationsDetalis(List<Location> locationList, int siteID, int rollAppID) {
        List<Location> locationListDetails = new ArrayList<Location>();
        Cursor cursor = null;
        String query;
        try {

            for (int i = 0; i < locationList.size(); i++) {

                query = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID , s.loc_form_header, " +
                        " s.loc_instruction,  s.ExtField7, s.Latitude,s.Longitude ,IFNuLL(wo.coc_flag,0) as CocFlag" +
                        " from s_Location s  LEFT JOIN  cm_coc_details wo on" +
                        " s.LocationID = wo.location_id where s.LocationID = " + locationList.get(i).getLocationID() + " and s.SiteID = " + siteID + " and (s.LocationType like '%" + rollAppID + "%'" +
                        " or s.LocationType is null)";

                cursor = database.rawQuery(query, null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationForMap(cursor);

                        locationListDetails.add(loc);

                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }
            }
        } catch (Exception e) {
            return locationListDetails;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationListDetails;
    }

    public String getLocationInstruction(String locId, int siteID, int rollAppID) {

        String locInstr = "";
        Cursor cursor = null;
        String query;
        try {

            query = "select distinct s.Location, s.LocationID, s.SiteID, " +
                    "s.loc_instruction from s_Location s " +
                    "where s.LocationID = "
                    + locId + " and s.SiteID = " + siteID
                    + " and (s.LocationType like '%" + rollAppID + "%'" +
                    " or s.LocationType is null)";

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                locInstr = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocInstruction));
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            return locInstr;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locInstr;
    }

    public ArrayList<Location> getDataForEventLocationV15(int siteID, int rollAppID, int eventID,
                                                          boolean hasData, boolean allData) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            String query = "";

            if (allData) {
                query = "Select distinct a.Location, a.LocationID, a.LocationType, a.SiteID, " +
                        "a.loc_form_header, a.loc_instruction, a.ExtField7, a.Latitude, a.Longitude, " +
                        "a.FormDefault, a.location_tabs " +
                        "from s_Location a inner join s_EventLocations b " +
                        "on a.LocationID = b.LocationID where b.EventID = " + eventID + " and (a." +
                        SiteDataSource.KEY_Status + " IS NULL or a." + SiteDataSource.KEY_Status + "=1)";
            } else if (hasData) {
                query = "select distinct s.Location, s.LocationID, s.LocationType, s.SiteID, s.loc_form_header, " +
                        "s.loc_instruction, s.ExtField7, s.Latitude, s.Longitude, s.FormDefault, s.location_tabs " +
                        "from (select distinct a.Location,a.LocationID, a.LocationType, a.SiteID, a.loc_form_header, " +
                        "a.loc_instruction, a.ExtField7, a.Latitude,a.Longitude, a.FormDefault, a.location_tabs " +
                        "from s_Location a, d_FieldData b, s_EventLocations l where " +
                        "a.LocationID=b.LocationID and a.LocationID = l.LocationID " +
                        "and b.EventID = l.EventID and b.EventID= " + eventID + " and " +
                        "(a.LocationType like '%" + rollAppID + "%' or a.LocationType = '' or a.LocationType is null)" +
                        " and (a." + SiteDataSource.KEY_Status + " IS NULL or a." + SiteDataSource.KEY_Status + "=1)) s ";
            } else {
                query = "select distinct s.Location,s.LocationID, s.LocationType, s.SiteID, s.loc_form_header, " +
                        "s.loc_instruction, s.ExtField7, s.Latitude, s.Longitude, s.FormDefault, s.location_tabs " +
                        "from (select distinct s.LocationID, s.LocationType, s.Location, s.SiteID, " +
                        "s.loc_form_header, s.loc_instruction , s.ExtField7, s.Latitude, s.Longitude, s.FormDefault, s.location_tabs " +
                        "from s_Location s inner join s_EventLocations l on " +
                        "s.LocationID = l.LocationID and l.EventID = " + eventID +
                        " where s.SiteID = " + siteID +
                        " and (s.LocationType like '%" + rollAppID + "%' or s.LocationType = '') " +
                        "and s.LocationID NOT IN (select distinct LocationID from d_FieldData " +
                        "where SiteID = " + siteID + " and EventID = " + eventID + ")" +
                        " and (s." + SiteDataSource.KEY_Status + " IS NULL or s." + SiteDataSource.KEY_Status + "=1)) s";
            }

            cursor = database.rawQuery(query, null);

            Log.i(TAG, "get event location Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = new Location();
                    loc.setLocationName(cursor.getString(0));
                    loc.setLocationID(cursor.getString(1));
                    loc.setLocationType(cursor.getString(2));
                    loc.setSiteID(cursor.getInt(3));
                    loc.setLocFormHeader(cursor.getString(4));
                    loc.setLocInstruction(cursor.getString(5));
                    loc.setExtField7(cursor.getString(6));
                    loc.setLatitude(cursor.getString(7));
                    loc.setLongitude(cursor.getString(8));
                    loc.setFormDefault(cursor.getInt(9));
                    loc.setLocationTabs(cursor.getString(10));

                    locationList.add(loc);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return locationList;
    }

    public ArrayList<Location> getDefaultLocation(int siteID, int rollAppID) {

        ArrayList<Location> locNonFormDef = new ArrayList<>();

        try {
            String queryAllData = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault, location_tabs  " +
                    "from s_Location where SiteID = " + siteID + " and (LocationType like '%" + rollAppID + "%' " +
                    "or (LocationType = '' or LocationType is null)) and (FormDefault = 0 or FormDefault = 2) and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1) order by LocationID asc";

            locNonFormDef = queryLocations(queryAllData);
        } catch (Exception e) {
            e.printStackTrace();
            return locNonFormDef;
        }

        return locNonFormDef;
    }

    public ArrayList<Location> getDataForEventLocation(int siteID, int rollAppID, int eventID,
                                                       boolean hasData, boolean allData) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            String query = "";

            if (allData) {
                query = "Select distinct a.Location, a.LocationID, a.LocationType, a.SiteID, " +
                        "a.loc_form_header, a.loc_instruction, a.ExtField7, a.Latitude, a.Longitude " +
                        "from s_Location a inner join s_EventLocations b " +
                        "on a.LocationID = b.LocationID where b.EventID = " + eventID;
            } else if (hasData) {
                query = "select distinct s.Location, s.LocationID, s.LocationType, s.SiteID, s.loc_form_header, " +
                        "s.loc_instruction, s.ExtField7, s.Latitude, s.Longitude " +
                        "from (select distinct a.Location,a.LocationID, a.LocationType, a.SiteID, a.loc_form_header, " +
                        "a.loc_instruction, a.ExtField7, a.Latitude,a.Longitude " +
                        "from s_Location a, d_FieldData b, s_EventLocations l where " +
                        "a.LocationID=b.LocationID and a.LocationID = l.LocationID " +
                        "and b.EventID = l.EventID and b.EventID= " + eventID + " and " +
                        "(a.LocationType like '%" + rollAppID + "%' or a.LocationType = '')) s";
            } else {
                query = "select distinct s.Location,s.LocationID, s.LocationType, s.SiteID, s.loc_form_header, " +
                        "s.loc_instruction, s.ExtField7, s.Latitude, s.Longitude " +
                        "from (select distinct s.LocationID, s.LocationType, s.Location, s.SiteID, " +
                        "s.loc_form_header, s.loc_instruction , s.ExtField7, s.Latitude, s.Longitude " +
                        "from s_Location s inner join s_EventLocations l on " +
                        "s.LocationID = l.LocationID and l.EventID = " + eventID +
                        " where s.SiteID = " + siteID +
                        " and (s.LocationType like '%" + rollAppID + "%' or s.LocationType = '') " +
                        "and s.LocationID NOT IN (select distinct LocationID from d_FieldData " +
                        "where SiteID = " + siteID + " and EventID = " + eventID + ")) s";
            }

            cursor = database.rawQuery(query, null);

            Log.i(TAG, "get event location Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = new Location();
                    loc.setLocationName(cursor.getString(0));
                    loc.setLocationID(cursor.getString(1));
                    loc.setLocationType(cursor.getString(2));
                    loc.setSiteID(cursor.getInt(3));
                    loc.setLocFormHeader(cursor.getString(4));
                    loc.setLocInstruction(cursor.getString(5));
                    loc.setExtField7(cursor.getString(6));
                    loc.setLatitude(cursor.getString(7));
                    loc.setLongitude(cursor.getString(8));

                    locationList.add(loc);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return locationList;
    }

    public ArrayList<Location> getAllRequiredLocFormDefaultOrNon(int siteID, int rollAppID) {

        ArrayList<Location> allLocationsList = new ArrayList<>();

        try {
            String queryAllData = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault, location_tabs  " +
                    "from s_Location where SiteID = " + siteID + " and (LocationType like '%" + rollAppID + "%' " +
                    "or (LocationType = '' or LocationType is null)) and FormDefault = 2 and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            allLocationsList.addAll(queryLocations(queryAllData));
        } catch (Exception e) {
            e.printStackTrace();
            return allLocationsList;
        }

        return allLocationsList;
    }

    public HashMap<String, ArrayList<Location>> getAllLocDefaultOrNonAdmin(int siteID) {

        HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();
        try {

            String queryAllDataFormDefault = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault, location_tabs  " +
                    "from s_Location where SiteID = " + siteID + " and FormDefault  = 1 and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            String queryAllData = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault, location_tabs  " +
                    "from s_Location where SiteID = " + siteID + " and (FormDefault = 0 or FormDefault = 2) and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            ArrayList<Location> locFormDef = queryLocations(queryAllDataFormDefault);
            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            ArrayList<Location> locNonFormDef = queryLocations(queryAllData);
            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }

    public HashMap<String, ArrayList<Location>> getAllDataLocFormDefaultOrNon(int siteID,
                                                                              int rollAppID) {

        HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();
        try {

            String queryAllDataFormDefault = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault, location_tabs  " +
                    "from s_Location where SiteID = " + siteID + " and (LocationType like '%" + rollAppID + "%' " +
                    "or (LocationType = '' or LocationType is null)) and FormDefault = 1 and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            String queryAllData = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault, location_tabs  " +
                    "from s_Location where SiteID = " + siteID + " and (LocationType like '%" + rollAppID + "%' " +
                    "or (LocationType = '' or LocationType is null)) and (FormDefault = 0 or FormDefault = 2) and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            ArrayList<Location> locFormDef = queryLocations(queryAllDataFormDefault);
            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            ArrayList<Location> locNonFormDef = queryLocations(queryAllData);
            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }

    public HashMap<String, ArrayList<Location>> getAllDataLocFormDefOrNonWithAttr(int siteID,
                                                                                  int rollAppID,
                                                                                  HashMap<String, String> outputMap) {

        HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();

        ArrayList<Location> locFormDef = new ArrayList<>();
        ArrayList<Location> locNonFormDef = new ArrayList<>();
        try {

            String queryAllData = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID , s.loc_form_header," +
                    " s.loc_instruction,  s.ExtField7, s.Latitude,s.Longitude ,IFNuLL(wo.coc_flag,0) as CocFlag, s.FormDefault" +
                    " , s.location_tabs from s_Location s " +
                    " INNER JOIN s_LocationAttribute a on s.LocationID = a.locationID " +
                    "and a.attributeName = ? and a.attributeValue = ?" +
                    " LEFT JOIN  cm_coc_details wo on" +
                    " s.LocationID = wo.location_id where SiteID=" + siteID
                    + " and (s.LocationType like '%" + rollAppID + "%'" +
                    " or s.LocationType is null or s.LocationType = '') and (s." +
                    SiteDataSource.KEY_Status + " IS NULL or s." + SiteDataSource.KEY_Status + "=1) " +
                    "and (s.FormDefault = ? ";

            for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                String attributeName = entry.getKey();
                String attributeValue = entry.getValue();

                StringTokenizer st = new StringTokenizer(attributeName, "|");
                String name = st.nextToken();

                String[] selectionArgs = {name, attributeValue, "0"};

                StringBuilder builder = new StringBuilder();
                builder.append(queryAllData);
                builder.append("or s.FormDefault = 2 ");

                Cursor cursor = database.rawQuery(builder.toString(), selectionArgs);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationForMap(cursor);
                        locNonFormDef.add(loc);
                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }

                builder.setLength(0);
                builder.append(queryAllData);
                builder.append(")");

                selectionArgs[2] = "1";
                Cursor cursorFD = database.rawQuery(builder.toString(), selectionArgs);

                if (cursorFD != null && cursorFD.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationForMap(cursorFD);
                        locFormDef.add(loc);
                    } while (cursorFD.moveToNext());
                    // make sure to close the cursor
                    cursorFD.close();
                }
            }

            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }

    public HashMap<String, ArrayList<Location>> getNoDataLocFormDefaultOrNon(int siteID,
                                                                             int rollAppID, String eventId) {

        HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();
        try {

            String queryNoDataFormDefault = "select distinct s.Location,S.LocationType, s.LocationID, s.SiteID, " +
                    "s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude," +
                    "s.FormDefault from (select distinct s.LocationID, s.LocationType,s.Location,s.SiteID, " +
                    "s.loc_form_header,s.loc_instruction ,s.ExtField7,s.Latitude,s.Longitude," +
                    "s.FormDefault, s.location_tabs from s_Location s where SiteID = " + siteID + " and (s.LocationType " +
                    "like '%" + rollAppID + "%' or (s.LocationType = '' or s.LocationType is null)) and s.FormDefault = 1 " +
                    "and LocationID NOT IN (select distinct LocationID from d_FieldData " +
                    "where SiteID = " + siteID + " and EventID = " + eventId + ") and (s."
                    + SiteDataSource.KEY_Status + " IS NULL or s." + SiteDataSource.KEY_Status + "=1)) s";

            String queryNoData = "select distinct s.Location,s.LocationType, s.LocationID, s.SiteID, " +
                    "s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude," +
                    "s.FormDefault from (select distinct s.LocationID, s.LocationType, s.Location,s.SiteID, " +
                    "s.loc_form_header,s.loc_instruction ,s.ExtField7,s.Latitude,s.Longitude," +
                    "s.FormDefault, s.location_tabs from s_Location s where SiteID = " + siteID + " and (s.LocationType " +
                    "like '%" + rollAppID + "%' or (s.LocationType = '' or s.LocationType is null)) " +
                    "and (FormDefault  = 0 or FormDefault  = 2) and " +
                    "LocationID NOT IN (select distinct LocationID from d_FieldData where " +
                    "SiteID = " + siteID + " and EventID = " + eventId + ") and (s."
                    + SiteDataSource.KEY_Status + " IS NULL OR s." + SiteDataSource.KEY_Status + "=1)) s";

            ArrayList<Location> locFormDef = queryLocations(queryNoDataFormDefault);
            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            ArrayList<Location> locNonFormDef = queryLocations(queryNoData);
            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }

    public HashMap<String, ArrayList<Location>> getHasDataLocFormDefaultOrNon(int siteID,
                                                                              int rollAppID, String eventId) {

        HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();
        try {

            String queryHasDataFormDefault = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, " +
                    "s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude,s.FormDefault " +
                    "from (select distinct a.Location,a.LocationType, a.LocationID, a.SiteID, a.loc_form_header, " +
                    "a.loc_instruction, a.ExtField7, a.Latitude,a.Longitude,a.FormDefault, a.location_tabs from " +
                    "s_Location a, d_FieldData b where a.LocationID=b.LocationID and a.SiteID = " +
                    siteID + " and b.EventID= " + eventId + " and (a.LocationType like '%" +
                    rollAppID + "%' or (a.LocationType = '' or a.LocationType is null)) and a.FormDefault = 1 and (a."
                    + SiteDataSource.KEY_Status + " IS NULL or a." + SiteDataSource.KEY_Status + "=1)) s";

            String queryHasData = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, " +
                    "s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude " +
                    "from (select distinct a.Location,a.LocationType,a.LocationID, a.SiteID, a.loc_form_header, " +
                    "a.loc_instruction, a.ExtField7, a.Latitude,a.Longitude, a.location_tabs from s_Location a, " +
                    "d_FieldData b where a.LocationID=b.LocationID and a.SiteID = " + siteID + " and " +
                    "b.EventID= " + eventId + " and (a.LocationType like '%" + rollAppID + "%' or " +
                    "(a.LocationType = '' or a.LocationType is null)) and " +
                    "(a.FormDefault  = 0 or a.FormDefault  = 2) and (a."
                    + SiteDataSource.KEY_Status + " IS NULL or a." + SiteDataSource.KEY_Status + "=1)) s";

            ArrayList<Location> locFormDef = queryLocations(queryHasDataFormDefault);
            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            ArrayList<Location> locNonFormDef = queryLocations(queryHasData);
            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }

    private ArrayList<Location> queryLocations(String query) {
        Cursor cursor = null;
        ArrayList<Location> locationList = new ArrayList<>();
        cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Location loc = cursorToLocationDataFormDefault(cursor);
                locationList.add(loc);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return locationList;
    }

    private Location cursorToLocationDataFormDefault(Cursor cursor) {
        Location loc = new Location();

        try {
            loc.setLocationName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_Location)));

            loc.setLocationID(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocationID)));
            loc.setSiteID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SiteID)));
            loc.setLocationDesc(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocFormHeader)));//loc_form_header
            loc.setExtField2(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocInstruction)));//location_instruction
            loc.setLocInstruction(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocInstruction)));//location_instruction
            loc.setExtField7(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ExtField7)));
            loc.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_Latitude)) + "");
            loc.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_Longitude)) + "");
            loc.setLocationType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATIONTYPE)));
            loc.setFormDefault(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_formDefault)));
            loc.setLocationTabs(cursor.getString(cursor.getColumnIndexOrThrow(KEY_location_tabs)));
            loc.setCocflag("0");

            FieldDataSource fd = new FieldDataSource(mContext);
            loc.setPercentage(fd.isLocationInStatus(loc.getLocationID() + "") ? 1 : 0);
        } catch (Exception e) {
            Log.i("Cursor issue", "Issue with cursor data fetch");
            e.printStackTrace();
        }
        return loc;
    }

    private ArrayList<MapLocation> queryMapLocations(String query) {
        Cursor cursor;
        ArrayList<MapLocation> locationList = new ArrayList<>();
        cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MapLocation loc = cursorToLocationDataFormDefaultMap(cursor);
                locationList.add(loc);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return locationList;
    }

    private MapLocation cursorToLocationDataFormDefaultMap(Cursor cursor) {
        MapLocation loc = new MapLocation();
        loc.setLocationName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_Location)));

        loc.setLocationID(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocationID)));
        loc.setSiteID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SiteID)));
        loc.setLocationDesc(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocFormHeader)));//loc_form_header
        loc.setExtField2(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LocInstruction)));//location_instruction
        loc.setExtField7(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ExtField7)));
        //used getDouble instead string coz if we use getString then latlongs that have 000 after decimal are getting formatted to 2 decimals
        loc.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_Latitude)) + "");
        loc.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_Longitude)) + "");
        return loc;
    }

    public ArrayList<Location> getHasDataLocation(int siteID, int rollAppID, int eventID) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            String query = "";

            query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction," +
                    " s.ExtField7, s.Latitude,s.Longitude,IFNuLL(s.coc_flag,0) as CocFlag from" +
                    " (select distinct a.Location,a.LocationID, a.SiteID, a.loc_form_header, a.loc_instruction," +
                    " a.ExtField7, a.Latitude,a.Longitude, wo.coc_flag from s_Location a , d_FieldData b " +
                    " LEFT JOIN  cm_coc_details wo on a.LocationID = wo.location_id where " +
                    " a.LocationID=b.LocationID and a.SiteID = " + siteID + " and b.EventID= " + eventID +
                    " and (a.LocationType like '%" + rollAppID + "%' or a.LocationType = '' " +
                    "or a.LocationType ISNULL or a.LocationType = '')) s";

            cursor = database.rawQuery(query, null);

            Log.i(TAG, "get HasData LocationForSiteCoC() Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = cursorToLocationforData(cursor, eventID + "");
                    locationList.add(loc);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return locationList;
    }

    public ArrayList<Location> getHasDataLocationWithAttribute(int siteID, int rollAppID, int eventID,
                                                               String attributeName, String attributeValue) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            String query = "";

            query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction," +
                    " s.ExtField7, s.Latitude,s.Longitude,IFNuLL(s.coc_flag,0) as CocFlag from" +
                    " (select distinct a.Location,a.LocationID, a.SiteID, a.loc_form_header, a.loc_instruction," +
                    " a.ExtField7, a.Latitude,a.Longitude, wo.coc_flag from s_Location a , d_FieldData b " +
                    " LEFT JOIN  cm_coc_details wo on a.LocationID = wo.location_id  " +
                    " INNER JOIN s_LocationAttribute c on a.LocationID = c.locationID  and c.attributeName = '" + attributeName + "' and c.attributeValue = '" + attributeValue + "'" +
                    " where a.LocationID=b.LocationID and a.SiteID = " + siteID + " and b.EventID= " + eventID +
                    " and (a.LocationType like '%" + rollAppID + "%' or a.LocationType = '' or a.LocationType ISNULL)) s";

            cursor = database.rawQuery(query, null);

            Log.i(TAG, "get HasData getHasDataLocationWithAttribute() Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = cursorToLocationforData(cursor, eventID + "");
                    locationList.add(loc);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return locationList;
    }

    public ArrayList<Location> getHasDataLocationWithMultipleAttribute(int siteID, int rollAppID,
                                                                       int eventID, String attributeName,
                                                                       String attributeValue,
                                                                       HashMap<String, String> outputMap) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                String AttributeName = entry.getKey();
                String AttributeValue = entry.getValue();

                StringTokenizer st = new StringTokenizer(AttributeName, "|");
                String Name = st.nextToken();

                String query = "";

                query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction," +
                        " s.ExtField7, s.Latitude,s.Longitude,IFNuLL(s.coc_flag,0) as CocFlag from" +
                        " (select distinct a.Location,a.LocationID, a.SiteID, a.loc_form_header, a.loc_instruction," +
                        " a.ExtField7, a.Latitude,a.Longitude, wo.coc_flag from s_Location a , d_FieldData b " +
                        " LEFT JOIN  cm_coc_details wo on a.LocationID = wo.location_id  " +
                        " INNER JOIN s_LocationAttribute c on a.LocationID = c.locationID  and c.attributeName = '" + Name + "' and c.attributeValue = '" + AttributeValue + "'" +
                        " where a.LocationID=b.LocationID and a.SiteID = " + siteID + " and b.EventID= " + eventID +
                        " and (a.LocationType like '%" + rollAppID + "%' or a.LocationType = '' " +
                        "or a.LocationType ISNULL or a.LocationType = '') and a.FormDefault  = 0 and (a." +
                        SiteDataSource.KEY_Status + " IS NULL or a." + SiteDataSource.KEY_Status + "=1)) s";

                cursor = database.rawQuery(query, null);

                Log.i(TAG, "get HasData getHasDataLocationWithAttribute() Query:" + query);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationforData(cursor, eventID + "");
                        locationList.add(loc);

                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return locationList;
    }

    public ArrayList<Location> getNoDataLocation(int siteID, int rollAppID, int eventID) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            String query = "";

            query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header," +
                    " s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude, " +
                    "IFNuLL(s.coc_flag,0) as CocFlag from(select distinct s.LocationID,s.Location,s.SiteID," +
                    " s.loc_form_header,s.loc_instruction ,s.ExtField7,s.Latitude,s.Longitude , " +
                    "wo.coc_flag from s_Location s LEFT JOIN  cm_coc_details wo on" +
                    " s.LocationID = wo.location_id " +
                    "where SiteID =" + siteID + " and (s.LocationType like '%" + rollAppID + "%' " +
                    "or s.LocationType is null or s.LocationType = '') " +
                    "and s.LocationID NOT IN (select distinct LocationID from d_FieldData where" +
                    " SiteID =" + siteID + " and EventID =" + eventID + ")) s";

            cursor = database.rawQuery(query, null);

            Log.i(TAG, "get No Data LocationForSiteCoC() Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Location loc = cursorToLocationforData(cursor, eventID + "");
                    locationList.add(loc);

                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return locationList;
    }

    public HashMap<String, ArrayList<Location>> getNoOrHasDataLocFormDefNonWithAttr(int siteID, int rollAppID, int eventID,
                                                                                    HashMap<String, String> outputMap, boolean isNoData) {


        HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();

        ArrayList<Location> locFormDef = new ArrayList<>();
        ArrayList<Location> locNonFormDef = new ArrayList<>();

        try {

            String query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header," +
                    " s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude, " +
                    "IFNuLL(s.coc_flag,0) as CocFlag, s.FormDefault from(select distinct s.LocationID,s.Location,s.SiteID," +
                    " s.loc_form_header,s.loc_instruction ,s.ExtField7,s.Latitude,s.Longitude , " +
                    " wo.coc_flag, s.FormDefault, s.location_tabs from s_Location s  " +
                    " INNER JOIN s_LocationAttribute a on s.LocationID = a.locationID " +
                    "and a.attributeName = ? and a.attributeValue = ?" +
                    " LEFT JOIN  cm_coc_details wo on" +
                    " s.LocationID = wo.location_id " +
                    " where SiteID =" + siteID + " and (s.LocationType like '%" + rollAppID + "%' " +
                    "or s.LocationType is null or s.LocationType = '') and (s." +
                    SiteDataSource.KEY_Status + " IS NULL or s." + SiteDataSource.KEY_Status + "=1)" +
                    " and s.LocationID NOT IN (select distinct LocationID from d_FieldData where" +
                    " SiteID =" + siteID + " and EventID =" + eventID + ") and (s.FormDefault = ? ";

            if (!isNoData)
                query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction," +
                        " s.ExtField7, s.Latitude,s.Longitude,IFNuLL(s.coc_flag,0) as CocFlag, s.FormDefault from" +
                        " (select distinct a.Location,a.LocationID, a.SiteID, a.loc_form_header, a.loc_instruction," +
                        " a.ExtField7, a.Latitude,a.Longitude, wo.coc_flag, a.FormDefault, a.location_tabs from s_Location a , d_FieldData b " +
                        " LEFT JOIN  cm_coc_details wo on a.LocationID = wo.location_id  " +
                        " INNER JOIN s_LocationAttribute c on a.LocationID = c.locationID " +
                        "and c.attributeName = ? and c.attributeValue = ?" +
                        " where a.LocationID=b.LocationID and a.SiteID = " + siteID + " and b.EventID= " + eventID +
                        " and (a.LocationType like '%" + rollAppID + "%' or a.LocationType = '' " +
                        "or a.LocationType IS NULL or a.LocationType = '') and (a." +
                        SiteDataSource.KEY_Status + " IS NULL or a." + SiteDataSource.KEY_Status + "=1) and (a.FormDefault = ? ";

            for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                String AttributeName = entry.getKey();
                String attributeValue = entry.getValue();

                StringTokenizer st = new StringTokenizer(AttributeName, "|");
                String name = st.nextToken();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(query);
                if (!isNoData)
                    stringBuilder.append("or a.FormDefault = 2)) s");
                else
                    stringBuilder.append("or s.FormDefault = 2)) s");

                String[] selectionArgs = {name, attributeValue, "0"};
                Cursor cursor = database.rawQuery(stringBuilder.toString(), selectionArgs);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationforData(cursor, eventID + "");
                        locNonFormDef.add(loc);

                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }

                stringBuilder.setLength(0);
                stringBuilder.append(query);
                stringBuilder.append(")) s");
                selectionArgs[2] = "1";
                Cursor cursorFD = database.rawQuery(stringBuilder.toString(), selectionArgs);

                if (cursorFD != null && cursorFD.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationforData(cursorFD, eventID + "");
                        locFormDef.add(loc);

                    } while (cursorFD.moveToNext());
                    // make sure to close the cursor
                    cursorFD.close();
                }
            }

            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }

    public ArrayList<Location> getNoDataLocationWithMultipleAttribute(int siteID, int rollAppID,
                                                                      int eventID,
                                                                      String attributeName,
                                                                      String attributeValue,
                                                                      HashMap<String, String> outputMap) {

        ArrayList<Location> locationList = new ArrayList<>();
        Cursor cursor = null;
        try {

            for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                String AttributeName = entry.getKey();
                String AttributeValue = entry.getValue();

                StringTokenizer st = new StringTokenizer(AttributeName, "|");
                String Name = st.nextToken();

                String query = "";

                query = "select distinct s.Location,s.LocationID, s.SiteID, s.loc_form_header," +
                        " s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude, " +
                        "IFNuLL(s.coc_flag,0) as CocFlag from(select distinct s.LocationID,s.Location,s.SiteID," +
                        " s.loc_form_header,s.loc_instruction ,s.ExtField7,s.Latitude,s.Longitude , " +
                        " wo.coc_flag from s_Location s  " +
                        " INNER JOIN s_LocationAttribute a on s.LocationID = a.locationID  and a.attributeName = '" + Name + "' and a.attributeValue = '" + AttributeValue + "'" +
                        " LEFT JOIN  cm_coc_details wo on" +
                        " s.LocationID = wo.location_id " +
                        " where SiteID =" + siteID + " and (s.LocationType like '%" + rollAppID + "%' " +
                        "or s.LocationType is null or s.LocationType = '') and s.FormDefault = 0 and (s." +
                        SiteDataSource.KEY_Status + " IS NULL or s." + SiteDataSource.KEY_Status + "=1)" +
                        " and s.LocationID NOT IN (select distinct LocationID from d_FieldData where" +
                        " SiteID =" + siteID + " and EventID =" + eventID + ")) s";

                cursor = database.rawQuery(query, null);

                Log.i(TAG, "get NoDAta getNoDataLocationWithAttribute() Query:" + query);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Location loc = cursorToLocationforData(cursor, eventID + "");
                        locationList.add(loc);

                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return locationList;
    }


    public int getLocationCountForEvent(int eventID) {

        int count = 0;

        Cursor cursor = null;
        try {

            String query =
                    " Select DISTINCT LocationID from d_FieldData where EventID=" + eventID;
            cursor = database.rawQuery(query, null);

            Log.i(TAG, "getLocationCountForEvent() Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getCount();
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return count;
    }

    public int getLocationFormDefault(String locationId) {

        int formDefault = 0;

        Cursor cursor = null;
        try {

            String query =
                    " Select DISTINCT LocationID, FormDefault from " + DbAccess.TABLE_LOCATION
                            + " where " + KEY_LocationID + " = " + locationId;
            cursor = database.rawQuery(query, null);

            Log.i(TAG, "getLocationFormDefault() Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                formDefault = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_formDefault));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return formDefault;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        //added this check as 2 is for required location id but it is considered 0 else you wont get any forms for location
        if (formDefault == 2)
            formDefault = 0;

        return formDefault;
    }

    public HashMap<String, String> getLocationTabs(String locationId, String siteId) {

        HashMap<String, String> mapLocationTabs = new HashMap<>();

        Cursor cursor = null;
        try {

            String query =
                    " Select DISTINCT LocationID, location_tabs from " + DbAccess.TABLE_LOCATION
                            + " where " + KEY_LocationID + " = " + locationId + " and "
                            + KEY_SiteID + "=" + siteId;
            cursor = database.rawQuery(query, null);

            Log.i(TAG, "getLocationTabs() Query:" + query);

            if (cursor != null && cursor.moveToFirst()) {
                String locationTab = cursor.getString(cursor.getColumnIndexOrThrow(KEY_location_tabs));

                List<String> listIds = new ArrayList<>();
                if (locationTab != null && !locationTab.isEmpty())
                    listIds = Util.splitStringToArray(",", locationTab);

                if (!listIds.isEmpty()) {
                    for (String ids : listIds) {
                        mapLocationTabs.put(ids, ids);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mapLocationTabs;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return mapLocationTabs;
    }

    public boolean islocationAlreadyExists(String loc_name, int siteID) {

//		System.out.println("MMM"+"LocationID");
        loc_name = (loc_name == null) ? null : loc_name.trim();
        List<Location> locationList = new ArrayList<Location>();

        String[] locationColumns = new String[]{"LocationID"};

        String whereClause = "Location=? AND SiteID=?";
        String[] whereArgs = new String[]{loc_name, "" + siteID};

        Cursor cursor = null;
        try {
            cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getCount();
                // make sure to close the cursor
                cursor.close();
                return count > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    public boolean isLocationNameExists(String loc_name, int siteID, String locId, String eventId) {

        loc_name = (loc_name == null) ? null : loc_name.trim();

        String[] locationColumns = new String[]{"LocationID"};

        String whereClause = "Location=? AND SiteID=? and LocationID=? and EventID=?";
        String[] whereArgs = new String[]{loc_name, "" + siteID, locId, eventId};

        Cursor cursor = null;
        try {
            cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getCount();
                // make sure to close the cursor
                cursor.close();
                return count > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }


    public int getLocationCountForForm(int siteID, int rollAppID) {

        int count = 0;

        Cursor cursor = null;
        try {
            String query;

            // TODO: 04-Jul-17 CoC Query UPDATED
            query = "Select distinct Location, LocationID,SiteID,loc_form_header,loc_instruction,ExtField7,Latitude,Longitude,'0' CocFlag,0 from\n" +
                    " (select distinct a.Location,a.LocationID,a.Location,a.LocationID,a.SiteID,a.loc_form_header," +
                    "  a.loc_instruction,a.ExtField7,a.Latitude,a.Longitude from s_Location a, s_Default_Values b\n" +
                    " where a.LocationID = b.LocationID and b.MobileAppID = " + rollAppID + "\n" +
                    " and a.SiteID = " + siteID + " and b.fieldParameterId = 0)";

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                count = cursor.getCount();
                cursor.close();
            }
        } catch (Exception e) {
            return count;
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return count;
    }

    public List<MapLocation> getLocationForFormOnMap(int siteID, int rollAppID) {

        List<MapLocation> locationList = new ArrayList<MapLocation>();
        Cursor cursor = null;
        try {

            String query = "";

            query = "\n" +
                    "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID, s.loc_form_header, s.loc_instruction," +
                    " s.ExtField7, s.Latitude,s.Longitude,ifnull(wo.CocFlag,0) as CocFlag ,wo.UserID " +
                    "from s_Location s LEFT JOIN  s_work_order_task wo \n" +
                    "                    on s.LocationID = wo.LocationID where SiteID=" + siteID +
                    " and s.LocationType like '%" + rollAppID + "%' and ((s.Latitude<>0 or s.Latitude<>0.0) and (s.Longitude<>0 or s.Longitude<>0.0))\n" +
                    "                    union\n" +
                    "       select distinct s.Location,s.LocationType,s.LocationID, s.SiteID," +
                    " s.loc_form_header, s.loc_instruction, s.ExtField7, s.Latitude,s.Longitude,ifnull(wo.CocFlag,0) as CocFlag ,wo.UserID from s_Location s LEFT JOIN  s_work_order_task wo \n" +
                    "                 on s.LocationID = wo.LocationID where SiteID=" + siteID + " and s.LocationType is null and ((s.Latitude<>0 or s.Latitude<>0.0) and (s.Longitude<>0 or s.Longitude<>0.0))";

            cursor = database.rawQuery(query, null);

//            cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
//                    whereClause, whereArgs, null, "COLLATE NOCASE", orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MapLocation loc = new MapLocation();

                    loc.setLocationName(cursor.getString(0));
                    String locID = cursor.getString(2);

                    loc.setLocationID(locID);
                    loc.setSiteID(cursor.getInt(3));
                    loc.setLocationDesc(cursor.getString(4));
                    loc.setExtField2(cursor.getString(5));
                    loc.setExtField7(cursor.getString(6));
                    loc.setLatitude(cursor.getDouble(7) + "");
                    loc.setLongitude(cursor.getDouble(8) + "");
                    //7/18/2018
                    FieldDataSource fd = new FieldDataSource(mContext);
                    loc.setData(fd.isLocationInStatus(loc.getLocationID()));//isDataAvailableForLocation(locID)
                    Log.i(TAG, "Data is Available for Location:" + loc.getLocationName() + " Status:" + loc.isData());

                    locationList.add(loc);
                } while (cursor.moveToNext());
                // make sure to close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return locationList;
    }

    public HashMap<String, ArrayList<MapLocation>> getAllDataLocFormDefaultOrNonForMap(String siteID,
                                                                                       int rollAppID) {

        HashMap<String, ArrayList<MapLocation>> mapLocations = new HashMap<>();
        try {

            String queryAllDataFormDefault = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault  " +
                    "from s_Location where SiteID = " + siteID + " and (LocationType like '%" + rollAppID + "%' " +
                    "or LocationType = '') and FormDefault = 1 and ((Latitude<>0 or Latitude<>0.0) " +
                    "and (Longitude<>0 or Longitude<>0.0)) and ("
                    + SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            String queryAllData = "select distinct Location,LocationType,LocationID, SiteID, " +
                    "loc_form_header, loc_instruction, ExtField7, Latitude,Longitude, FormDefault  " +
                    "from s_Location where SiteID = " + siteID + " and (LocationType like '%" + rollAppID + "%' " +
                    "or LocationType = '') and (FormDefault = 0 or FormDefault = 2) " +
                    "and ((Latitude<>0 or Latitude<>0.0) and (Longitude<>0 or Longitude<>0.0)) and (" +
                    SiteDataSource.KEY_Status + " IS NULL or " + SiteDataSource.KEY_Status + "=1)";

            ArrayList<MapLocation> locFormDef = queryMapLocations(queryAllDataFormDefault);
            if (!locFormDef.isEmpty()) mapLocations.put(GlobalStrings.FORM_DEFAULT, locFormDef);

            ArrayList<MapLocation> locNonFormDef = queryMapLocations(queryAllData);
            if (!locNonFormDef.isEmpty())
                mapLocations.put(GlobalStrings.NON_FORM_DEFAULT, locNonFormDef);

        } catch (Exception e) {
            e.printStackTrace();
            return mapLocations;
        }

        return mapLocations;
    }


    public List<MapLocation> getLocationForFormOnMapWithAttribute(int siteID, int rollAppID, HashMap<String, String> outputMap) {

        List<MapLocation> locationList = new ArrayList<MapLocation>();
        Cursor cursor = null;
        try {

            for (Map.Entry<String, String> entry : outputMap.entrySet()) {
                String AttributeName = entry.getKey();
                String AttributeValue = entry.getValue();

                StringTokenizer st = new StringTokenizer(AttributeName, "|");
                String Name = st.nextToken();
                //Toast.makeText(mContext, ""+attrName[0], Toast.LENGTH_SHORT).show();
                Log.e("attrName", "setCheckedList: " + Name + " ----------------------- " + AttributeName);

                String query = "select distinct s.Location,s.LocationType,s.LocationID, s.SiteID , s.loc_form_header," +
                        " s.loc_instruction,  s.ExtField7, s.Latitude,s.Longitude ,IFNuLL(wo.coc_flag,0) as CocFlag" +
                        " from s_Location s " +
                        " INNER JOIN s_LocationAttribute a on s.LocationID = a.locationID  and a.attributeName = '" + Name + "' and a.attributeValue = '" + AttributeValue + "'" +
                        " LEFT JOIN  cm_coc_details wo on" +
                        " s.LocationID = wo.location_id where SiteID=" + siteID + " and (s.LocationType like '%" + rollAppID + "%'" +
                        " or s.LocationType is null)";

                cursor = database.rawQuery(query, null);

//            cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
//                    whereClause, whereArgs, null, "COLLATE NOCASE", orderBy);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        MapLocation loc = new MapLocation();

                        loc.setLocationName(cursor.getString(0));
                        String locID = cursor.getString(2);

                        loc.setLocationID(locID);
                        loc.setSiteID(cursor.getInt(3));
                        loc.setLocationDesc(cursor.getString(4));
                        loc.setExtField2(cursor.getString(5));
                        loc.setExtField7(cursor.getString(6));
                        loc.setLatitude(cursor.getDouble(7) + "");
                        loc.setLongitude(cursor.getDouble(8) + "");
                        // TODO: 7/18/2018
                        FieldDataSource fd = new FieldDataSource(mContext);
                        loc.setData(fd.isLocationInStatus(loc.getLocationID()));//isDataAvailableForLocation(locID)
                        Log.i(TAG, "Data is Available for Location:" + loc.getLocationName() + " Status:" + loc.isData());

                        locationList.add(loc);
                    } while (cursor.moveToNext());
                    // make sure to close the cursor
                    cursor.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return locationList;
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return locationList;
    }

    private Location cursorToLocationForMap(Cursor cursor) {
        Location loc = new Location();
        loc.setLocationName(cursor.getString(0));

        loc.setLocationType(cursor.getString(1));
        loc.setLocationID(cursor.getString(2));
        loc.setSiteID(cursor.getInt(3));
        loc.setLocationDesc(cursor.getString(4));//loc_form_header
        loc.setExtField2(cursor.getString(5));//location_instruction
        loc.setExtField7(cursor.getString(6));
        loc.setLatitude(cursor.getDouble(7) + "");
        loc.setLongitude(cursor.getDouble(8) + "");
        loc.setCocflag(cursor.getString(9));

        try {
            loc.setFormDefault(cursor.getInt(10));
            loc.setLocationTabs(cursor.getString(11));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        loc.setCocflag(cursor.getString(9));
        // loc.setUserID(cursor.getInt(10));
        FieldDataSource fd = new FieldDataSource(mContext);
        loc.setPercentage(fd.isLocationInStatus(loc.getLocationID() + "") ? 1 : 0);

        return loc;
    }

    private Location cursorToLocationforMapWithAttribute(Cursor cursor) {
        Location loc = new Location();

        loc.setLocationID(cursor.getString(0));

        return loc;
    }

    private Location cursorToLocationforData(Cursor cursor, String eventId) {
        Location loc = new Location();
        loc.setLocationName(cursor.getString(0));

        loc.setLocationID(cursor.getString(1));
        loc.setSiteID(cursor.getInt(2));
        loc.setLocationDesc(cursor.getString(3));//loc_form_header
        loc.setExtField2(cursor.getString(4));//location_instruction
        loc.setExtField7(cursor.getString(5));
        loc.setLatitude(cursor.getDouble(6) + "");
        loc.setLongitude(cursor.getDouble(7) + "");
        loc.setCocflag(cursor.getString(8));
        loc.setFormDefault(cursor.getInt(9));

        try {
            loc.setLocationTabs(cursor.getString(10));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FieldDataSource fd = new FieldDataSource(mContext);
        loc.setPercentage(fd.isLocationInStatus(loc.getLocationID() + "") ? 1 : 0);

        return loc;
    }

    public String isCOCDataAvailableForLocation(String locID, String eventId) {
        String cocFlag = "0";

        String[] whereClause = new String[]{locID, eventId};
        Cursor cursor = null;
        try {
            String query = "select distinct a.coc_flag from cm_coc_details a " +
                    "JOIN cm_coc_master b on a.location_id = ? where b.eventId = ?";

            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                cocFlag = cursor.getString(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isCOCDataAvailableForLocation() exception:" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cocFlag;
    }

    public boolean isDataAvailableForLocation(String locID) {
        int count = 0;

        String query = null;
        String[] whereClause = null;
//
        whereClause = new String[]{};
        Cursor cursor = null;
        try {
            query = "select count(*) from d_FieldData where LocationID=" + locID;
            cursor = database.rawQuery(query, whereClause);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFieldDataListForEvent() exception:" + e.getLocalizedMessage());
            return false;
        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }
        return count > 0;
    }

    public void setLocationSyncFlagSlocation(ArrayList<NewClientLocation> response) {

        for (NewClientLocation location : response) {
            if (location.getExtField7() != null && !location.getExtField7().isEmpty()) {

                if (FormActivity.locationIDChanged.equals(location.getExtField7())) {
                    FormActivity.locationIDChanged = location.getLocationId();
                    try {
                        TaskTabActivity.taskData.setLocationId(location.getLocationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                EventLocationDataSource eventLocationDS = new EventLocationDataSource(mContext);
                eventLocationDS.updateLocId(location.getExtField7(), location.getLocationId(),
                        location.getSiteId());
                //15-May-16 Update LocationID and SyncFlag
                updateOldLocationID(location.getLocationId(), location.getExtField7());
                new TaskDetailsDataSource(mContext).updateTasksLocationId(location.getExtField7(), location.getLocationId());
            } else {
                //15-May-16 Update SyncFlag only
                updateSyncFlag(location.getLocationId());
            }
        }
    }

    public ArrayList<NewClientLocation> collectLocationsToUpload() {

        ArrayList<NewClientLocation> uploadList = new ArrayList<>();
        NewClientLocation loc;

        String[] locationColumns = new String[]{
                "Location", "LocationDesc", "ExtField7", "Latitude", "Longitude", "LocationID",
                "SiteID", "CreationDate", "ModifiedDate", "Createdby", "locationType", "Status"};

        String whereClause = KEY_SyncFlag + "=? OR LocationID < 0";
        String[] whereArgs = new String[]{"" + 1};
        String orderBy = "null";
        Cursor cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
                whereClause, whereArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                loc = new NewClientLocation();

                loc.setLocation(cursor.getString(0));
                loc.setExtField7(cursor.getString(2));
                loc.setLatitude(cursor.getDouble(3) + "");
                loc.setLongitude(cursor.getDouble(4) + "");
                loc.setLocationId(cursor.getString(5));
                loc.setSiteId(cursor.getString(6));
                //10/26/2017 ADDED  following 3
                loc.setCreationDate(cursor.getString(7));
                loc.setModifiedDate(cursor.getString(8));
                loc.setCreatedBy(cursor.getString(9));
                loc.setLocationType(cursor.getString(10));
                loc.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("Status")));
                uploadList.add(loc);

            } while (cursor.moveToNext());

            // make sure to close the cursor
            cursor.close();
        }

        return uploadList;
    }

    public Location getLocationDetailsByName(String locationName, String siteID) {
        Location loc = new Location();

        String[] locationColumns = new String[]{
                "Location", "LocationDesc", "ExtField7", "Latitude", "Longitude", "LocationID"};

        String whereClause = "Location=? and SiteID=?";
        String[] whereArgs = new String[]{locationName, siteID};
        String orderBy = "null";
        Cursor cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
                whereClause, whereArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {

            loc.setLocationName(cursor.getString(0));
            loc.setLocationDesc(cursor.getString(1));
            loc.setExtField7(cursor.getString(2));
            loc.setLatitude(cursor.getString(3));
            loc.setLongitude(cursor.getString(4));
            loc.setLocationID(cursor.getString(5));

            Log.i(TAG, "LOcation Details for LocName:" + locationName + " :" + loc);

            // make sure to close the cursor
            cursor.close();
        }

        //01-Aug-17
        if (isCOClocation(loc.getLocationID())) {
            loc.setCocflag("1");
        } else {
            loc.setCocflag("0");
        }

        return loc;
    }

    public boolean isCOClocation(String locID) {

//		System.out.println("MMM"+"LocationID");
        List<Location> locationList = new ArrayList<Location>();

        String[] locationColumns = new String[]{"Location"};

        String whereClause = "LocationID=?";
        String[] whereArgs = new String[]{locID};

        Cursor cursor = null;
        try {
            cursor = database.query(DbAccess.TABLE_WORK_ORDER_TASK_NEW, locationColumns,
                    whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getCount();
                // make sure to close the cursor
                cursor.close();

                return count > 0;

            }


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    public String getLocationName(String locationID) {
        String name = null;
        String query = "select Location from s_Location where LocationID=?";
        Cursor cursor = null;
        String[] whereArgs = new String[]{"" + locationID};
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(0);
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        return name;
    }

    public String getServerLocationID(String clientlocaID) {

        String serverLocationID = null;
        String[] locationColumns = new String[]{
                KEY_LocationID};

        String whereClause = KEY_ClientLocationID + "=?";
        String[] whereArgs = new String[]{"" + clientlocaID};
        String orderBy = "null";

        Cursor cursor = database.query(DbAccess.TABLE_LOCATION, locationColumns,
                whereClause, whereArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            serverLocationID = cursor.getInt(0) + "";
            // make sure to close the cursor
            cursor.close();
        }

        return serverLocationID;
    }

    public void checkAndUpdateClientLocationInFieldData() {


        String query, query1 = null;
        String[] whereClause = new String[]{};
        Cursor cursor, cursor2;
        try {
            query = "Select " + KEY_LocationID + "," + KEY_ClientLocationID + " from " + DbAccess.TABLE_LOCATION + " where " + KEY_ClientLocationID + " " +
                    "IN (select DISTINCT " + KEY_LocationID + " from " + DbAccess.TABLE_FIELD_DATA + " where " + KEY_LocationID + "<0)";
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String map_id = cursor.getString(0);
                    String clientid = cursor.getString(1);
                    query1 = "Update d_FieldData set " + KEY_LocationID + "=" + map_id + " where " + KEY_LocationID + "=" + clientid;
                    cursor2 = database.rawQuery(query1, null);
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkAndUpdate ClientLocation In FieldData() exception:" + e.getLocalizedMessage());
        }
    }

    public void checkAndUpdateClientLocationInAttachmentData() {

        String query, query1 = null;
        String[] whereClause = new String[]{};
        Cursor cursor, cursor2;
        try {
            query = "Select " + KEY_LocationID + "," + KEY_ClientLocationID + " from " + DbAccess.TABLE_LOCATION + " where " + KEY_ClientLocationID + " " +
                    "IN (select DISTINCT " + KEY_LocationID + " from " + DbAccess.TABLE_ATTACHMENT + " where " + KEY_LocationID + "<0)";
            cursor = database.rawQuery(query, null);
            Log.i(TAG, "checkAndUpdate ClientLocation In FieldData() collect -ve Location d_FieldData query=" + query);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String map_id = cursor.getString(0);
                    String clientid = cursor.getString(1);
                    query1 = "Update " + DbAccess.TABLE_ATTACHMENT + " set " + KEY_LocationID + "=" + map_id + " " +
                            "where " + KEY_LocationID + "=" + clientid;
                    cursor2 = database.rawQuery(query1, null);
                    Log.i(TAG, "checkAndUpdate ClientLocation In Attachment() query=" + query);
                    if (cursor2 != null) {
                        Log.i(TAG, "checkAndUpdate ClientLocation In Attachment() Update result=" + cursor2.getCount());
                        cursor2.close();
                    } else
                        Log.i(TAG, "checkAndUpdate ClientLocation In Attachment() Update result=" + 0);

                } while (cursor.moveToNext());
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkAndUpdate ClientLocation In Attachment() exception:" + e.getLocalizedMessage());
        }


    }

    public ArrayList<SCocMaster> getCoCIDsForChildFormIDs(String childlist, String siteID) {

        ArrayList<SCocMaster> cocMasters = new ArrayList<>();
        String query = "select distinct cm.coc_id , cm.coc_display_id  from" +
                " cm_coc_details cd LEFT JOIN cm_coc_master cm on cd.coc_id = cm.coc_id where cm.site_id=" + siteID + " and cm.form_id IN(" + childlist + ")";
        Cursor c = null;
        try {
            SCocMaster cocMaster = new SCocMaster();
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    cocMaster = new SCocMaster();
                    cocMaster.setCocId(c.getInt(0));
                    cocMaster.setCocDisplayId(c.getString(1));
                    cocMasters.add(cocMaster);

                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return cocMasters;
    }

    public ArrayList<SCocMaster> getAllCoCIDs(String eventId, String siteID) {

        ArrayList<SCocMaster> cocMasters = new ArrayList<>();
        String query = "select distinct coc_id , coc_display_id from" +
                " cm_coc_master " +
                "where site_id = " + siteID + " and eventId = " + eventId;
        try (Cursor c = database.rawQuery(query, null)) {
            if (c != null && c.moveToFirst()) {
                do {
                    SCocMaster cocMaster = new SCocMaster();
                    cocMaster.setCocId(c.getInt(0));
                    cocMaster.setCocDisplayId(c.getString(1));
                    cocMasters.add(cocMaster);

                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cocMasters;
    }


    public boolean isOfflineLocationsAvailable() {


        int count = 0;
        String query = "select count(Location) from s_Location where " + KEY_SyncFlag + "=1";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);

            TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);

            LogDetails logDetails = new LogDetails();
            logDetails.setAllIds("");
            logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
            logDetails.setScreenName("Offline location available data query");

            boolean cursorHasData = cursor != null && !cursor.isClosed();
            boolean databaseConnection = database != null && database.isOpen();

            logDetails.setDetails("Checking cursor and database instance: Database: "
                    + databaseConnection + " Cursor: " + cursorHasData);

            tempLogsDataSource.insertTempLogs(logDetails);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    public boolean updateLocationLatLong(String locID, String latitude, String longitude) {
        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_Latitude, latitude);
        values.put(KEY_Longitude, longitude);
        values.put(KEY_ModifiedDate, System.currentTimeMillis());
        values.put(KEY_SyncFlag, 1);

        String whereClause = "LocationID = ?";
        String[] whereArgs = new String[]{locID};
        try {
            ret = database.update(DbAccess.TABLE_LOCATION, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateLocation Exception:" + e.getLocalizedMessage());
        }

        return ret > 0;
    }


    private void updateOldLocationIDDefault(String locationId, String oldLocID) {
        int ret = 0;
        String nullValue = null;


        // TODO: 13-May-16 Update in s_Location Table
        ContentValues values = new ContentValues();
        values.put(KEY_LocationID, locationId);

        String whereClause = "LocationID = ?";
        String[] whereArgs = new String[]{oldLocID};
        try {
            ret = database.update(DbAccess.TABLE_S_DEFAULT_VALUES, values, whereClause, whereArgs);
            Log.i(TAG, "update Offline LocationID count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }
    }

    public boolean updateOldLocationID(String newLocID, String oldLocID) {
        int ret = 0;
        String nullValue = null;


        //13-May-16 Update in s_Location Table
        ContentValues values = new ContentValues();
        values.put(KEY_LocationID, newLocID);
        values.put(KEY_SyncFlag, nullValue);
        values.put(KEY_ClientLocationID, oldLocID);

        //13-May-16 Update In dFieldData and dAttachment Table
        ContentValues dvalues = new ContentValues();
        dvalues.put(KEY_LocationID, newLocID);

        String whereClause = "LocationID = ?";
        String[] whereArgs = new String[]{oldLocID};
        try {
            ret = database.update(DbAccess.TABLE_LOCATION, values, whereClause, whereArgs);
            Log.i(TAG, "update Offline LocationID count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }
        try {
            ret = database.update(DbAccess.TABLE_FIELD_DATA, dvalues, whereClause, whereArgs);
            Log.i(TAG, "update Offline LocationID in dFieldData count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }
        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, dvalues, whereClause, whereArgs);
            Log.i(TAG, "update Offline LocationID in d_Attachment count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }
        try {
            ret = database.update(DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE, dvalues, "locationID=?", whereArgs);
            Log.i(TAG, "update Offline LocationID in s_LocationFormPercentage count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }
        try {
            ret = database.update(DbAccess.TABLE_D_SAMPLE_MAPTAG, dvalues, "LocationID=?", whereArgs);
            Log.i(TAG, "update Offline LocationID in d_SampleMapTag count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }


        //23-03-2018 COC_DETAILS
        ContentValues val = new ContentValues();
        val.put("location_id", newLocID);


        try {
            ret = database.update(DbAccess.TABLE_CM_COC_DETAILS, val, "location_id=?", whereArgs);
            Log.i(TAG, "update Offline LocationID in cm_coc_details count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update OldLocationID Exception:" + e.getLocalizedMessage());
        }


        return ret > 0;
    }

    public boolean updateSyncFlag(String LocID) {
        int ret = 0;
        String nullValue = null;


        // TODO: 13-May-16 Update in s_Location Table
        ContentValues values = new ContentValues();
        values.put(KEY_SyncFlag, nullValue);


        String whereClause = "LocationID = ?";
        String[] whereArgs = new String[]{LocID};
        try {
            ret = database.update(DbAccess.TABLE_LOCATION, values, whereClause, whereArgs);
            Log.i(TAG, "update Sync Flag count:" + ret);
            Log.e("locationName", "update Sync Flag count:" + LocID);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update SyncFlag Exception:" + e.getLocalizedMessage());
        }

        return ret > 0;
    }

    public List<NewClientLocation> storeLocations(List<NewClientLocation> mRetLocationList, boolean isOffline) {
        List<NewClientLocation> locationList = new ArrayList<>();
        if (mRetLocationList == null) {
            return locationList;
        }
        for (int i = 0; i < mRetLocationList.size(); i++) {
            NewClientLocation location = storeLocation(mRetLocationList.get(i), isOffline);
            locationList.add(location);
        }
        return locationList;
    }

    public long storeLocation(SLocation sloc, String userID) {

        SharedPreferences prefs = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE);
        String lat = prefs.getString("latitude", "");
        String lng = prefs.getString("longitude", "");

        long ret = 0;
        ContentValues values = new ContentValues();
        if (sloc == null) {
            return -1;
        }

        values.put(KEY_LocationID, sloc.getLocationId());
        values.put(KEY_ClientLocationID, sloc.getLocationId());
        values.put(KEY_SiteID, sloc.getSiteId());
        values.put(KEY_Location, sloc.getLocation());

        if (lat.equals("") && lng.equals("")) {
            Log.e("formSlocation", "storeLocation: NO BAD ELF LAT LNG CAPTURE");
            values.put(KEY_Latitude, sloc.getLatitude());
            values.put(KEY_Longitude, sloc.getLongitude());
        } else {
            Log.e("formSlocation", "storeLocation: BAD ELF LAT LNG CAPTURE " + lat + " LNG " + lng);
            values.put(KEY_Latitude, Double.parseDouble(lat));
            values.put(KEY_Longitude, Double.parseDouble(lng));
        }


        values.put(KEY_TOC, sloc.getToc());
        values.put(KEY_StartScreen, sloc.getStartScreen());
        values.put(KEY_EndScreen, sloc.getEndScreen());
        values.put(KEY_DTB, sloc.getDtb());
        values.put(KEY_LocInstruction, sloc.getLocInstruction());
        values.put(KEY_LocFormHeader, sloc.getLocFormHeader());
        values.put(KEY_WellDiameter, sloc.getWellDiameter());
        values.put(KEY_Status, sloc.getStatus());
        values.put(KEY_CreationDate, System.currentTimeMillis());
        values.put(KEY_ModifiedDate, System.currentTimeMillis());
        values.put(KEY_Createdby, userID);

        try {
            ret = database.insert(DbAccess.TABLE_LOCATION, null, values);
        } catch (Exception e) {
            System.out.println("ret=" + ret + "Exception Mesg=" + e.getLocalizedMessage());
        }
        return ret;
    }

    public int storeBulkLocationList(List<SLocation> locList) {

        int listSize = locList.size();

        database.beginTransaction();

        try {

            for (SLocation loc : locList) {
                ContentValues values = new ContentValues();

                values.put(KEY_LocationID, loc.getLocationId());
                values.put(KEY_ClientLocationID, loc.getLocationId());

                values.put(KEY_SiteID, loc.getSiteId());
                values.put(KEY_Location, loc.getLocation());
                values.put(KEY_Latitude, loc.getLatitude());
                values.put(KEY_Longitude, loc.getLongitude());
                values.put(KEY_TOC, loc.getToc());
                values.put(KEY_StartScreen, loc.getStartScreen());
                values.put(KEY_EndScreen, loc.getEndScreen());
                values.put(KEY_DTB, loc.getDtb());
                values.put(KEY_LocInstruction, loc.getLocInstruction());
                values.put(KEY_LocFormHeader, loc.getLocFormHeader());
                values.put(KEY_WellDiameter, loc.getWellDiameter());

                String loctype = loc.getLocationType();
                if (loctype != null && loctype.contains("f")) {
                    loctype = loctype.replace("f", "");
                    loctype = loctype.trim();
                }
                values.put(KEY_LOCATIONTYPE, loctype);

                //   values.put(KEY_LOCATIONTYPE,loc.getLocationType());
                try {
                    listSize = (int) database.insert(DbAccess.TABLE_LOCATION, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Bulk insertion of Location :" + e.getMessage());
        } finally {
            database.endTransaction();
        }

        return listSize;
    }

    public long storeBulkBindLocationList(List<SLocation> locList) {
        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_LOCATION,
                database);
        long count = 0;
        String[] arrColumns = {KEY_LocationID, KEY_ClientLocationID, KEY_SiteID, KEY_Location, KEY_Latitude,
                KEY_Longitude, KEY_TOC, KEY_StartScreen, KEY_EndScreen, KEY_DTB,
                KEY_LocInstruction, KEY_LocFormHeader, KEY_WellDiameter, KEY_LOCATIONTYPE,
                KEY_formDefault, SiteDataSource.KEY_Status, KEY_location_tabs};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_LOCATION + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {

            for (SLocation loc : locList) {

                if (/*loc.isInsert() || */isTableEmpty) {
                    statement.bindLong(1, loc.getLocationId());

                    statement.bindLong(2, loc.getLocationId());//clientLocId

                    if (loc.getSiteId() != null)
                        statement.bindLong(3, loc.getSiteId());
                    else
                        statement.bindNull(3);

                    if (loc.getLocation() != null)
                        statement.bindString(4, loc.getLocation());
                    else
                        statement.bindNull(4);

                    if (loc.getLatitude() != null)
                        statement.bindDouble(5, loc.getLatitude());
                    else
                        statement.bindNull(5);

                    if (loc.getLongitude() != null)
                        statement.bindDouble(6, loc.getLongitude());
                    else
                        statement.bindNull(6);

                    if (loc.getToc() != null)
                        statement.bindDouble(7, loc.getToc());
                    else
                        statement.bindNull(7);

                    if (loc.getStartScreen() != null)
                        statement.bindDouble(8, loc.getStartScreen());
                    else
                        statement.bindNull(8);

                    if (loc.getEndScreen() != null)
                        statement.bindDouble(9, loc.getEndScreen());
                    else
                        statement.bindNull(9);

                    if (loc.getDtb() != null)
                        statement.bindDouble(10, loc.getDtb());
                    else
                        statement.bindNull(10);

                    if (loc.getLocInstruction() != null)
                        statement.bindString(11, loc.getLocInstruction());
                    else
                        statement.bindNull(11);

                    if (loc.getLocFormHeader() != null)
                        statement.bindString(12, loc.getLocFormHeader());
                    else
                        statement.bindNull(12);

                    if (loc.getWellDiameter() != null)
                        statement.bindDouble(13, loc.getWellDiameter());
                    else
                        statement.bindNull(13);

                    String loctype = loc.getLocationType();
                    if (loctype != null && loctype.contains("f")) {
                        loctype = loctype.replace("f", "");
                        loctype = loctype.trim();
                        statement.bindString(14, loctype);
                    } else if (loctype != null) {
                        statement.bindString(14, loctype);
                    } else
                        statement.bindNull(14);

                    if (loc.getFormDefault() != null)
                        statement.bindLong(15, loc.getFormDefault());
                    else statement.bindNull(15);

                    if (loc.getStatus() != null)
                        statement.bindString(16, loc.getStatus());
                    else statement.bindNull(16);

                    if (loc.getLocationTabs() != null)
                        statement.bindString(17, loc.getLocationTabs());
                    else statement.bindNull(17);

                    count = statement.executeInsert();
                    statement.clearBindings();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(KEY_SiteID, loc.getSiteId());
                    values.put(KEY_Location, loc.getLocation());
                    values.put(KEY_Latitude, loc.getLatitude());
                    values.put(KEY_Longitude, loc.getLongitude());
                    values.put(KEY_TOC, loc.getToc());
                    values.put(KEY_StartScreen, loc.getStartScreen());
                    values.put(KEY_EndScreen, loc.getEndScreen());
                    values.put(KEY_DTB, loc.getDtb());
                    values.put(KEY_LocInstruction, loc.getLocInstruction());
                    values.put(KEY_LocFormHeader, loc.getLocFormHeader());
                    values.put(KEY_WellDiameter, loc.getWellDiameter());
                    values.put(SiteDataSource.KEY_Status, loc.getStatus());
                    values.put(KEY_location_tabs, loc.getLocationTabs());

                    String whereClause = KEY_LocationID + " = ?";
                    String[] whereArgs = new String[]{loc.getLocationId() + ""};
                    count = database.update(DbAccess.TABLE_LOCATION, values,
                            whereClause, whereArgs);
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Bulk insertion of Location :" + e.getMessage());
        } finally {
            database.endTransaction();
        }

        return count;
    }

    public NewClientLocation storeLocation(NewClientLocation sloc, boolean isOffline) {

        SharedPreferences prefs = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE);
        String lat = prefs.getString("latitude", "");
        String lng = prefs.getString("longitude", "");

        long ret = 0;
        ContentValues values = new ContentValues();
        if (sloc == null) {
            return null;
        } else if (!islocationAlreadyExists(sloc.getLocation(), Integer.parseInt(sloc.getSiteId()))) {
            long locallocationID = -((long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L);//getting 10 digit random no
            //for all locations if multiple locations are added at same time
            String locationName = sloc.getLocation().trim();
            if (isOffline) {
                sloc.setLocationId(locallocationID + "");
                values.put(KEY_SyncFlag, 1);
                values.put(KEY_LocationID, locallocationID);
                values.put(KEY_ClientLocationID, locallocationID);
            } else {
                values.put(KEY_LocationID, sloc.getLocationId());
                values.put(KEY_ClientLocationID, sloc.getLocationId());
            }

            values.put(KEY_SiteID, sloc.getSiteId());
            values.put(KEY_Location, locationName);

//		values.put(KEY_LocationTypeID, sloc.getLocationTypeId());
            values.put(KEY_LocationAlias, sloc.getLocationAlias());
            values.put(KEY_Status, sloc.getStatus());

            if (lat.equals("") && lng.equals("")) {
                Log.e("mapSlocation", "storeLocation: NO BAD ELF LAT LNG CAPTURE");
                values.put(KEY_Latitude, sloc.getLatitude());
                values.put(KEY_Longitude, sloc.getLongitude());
            } else {
                Log.e("mapSlocation", "storeLocation: BAD ELF LAT LNG CAPTURE " + lat + " LNG " + lng);
                values.put(KEY_Latitude, lat);
                values.put(KEY_Longitude, lng);
            }

            values.put(KEY_TOC, sloc.getToc());
            values.put(KEY_AOC, sloc.getAoc());
            values.put(KEY_Aquiferzone, sloc.getAquiferZone());
            values.put(KEY_InstallDate, sloc.getInstallDate());
            values.put(KEY_CasingType, sloc.getCasingType());
            values.put(KEY_StartScreen, sloc.getStartScreen());
            values.put(KEY_EndScreen, sloc.getEndScreen());
            values.put(KEY_DTB, sloc.getDtb());
            values.put(KEY_LocationDesc, sloc.getExtField1());
            values.put(KEY_ExtField1, sloc.getExtField1());
            values.put(KEY_ExtField2, sloc.getExtField2());
            values.put(KEY_ExtField3, sloc.getExtField3());
            values.put(KEY_ExtField4, sloc.getExtField4());
            values.put(KEY_ExtField5, sloc.getExtField5());
            values.put(KEY_ExtField6, sloc.getExtField6());
            //values.put(KEY_ExtField7, sloc.getExtField7()); this is commented as it is being used for
            values.put(KEY_Notes, sloc.getNotes());
            values.put(KEY_CreationDate, System.currentTimeMillis());
            values.put(KEY_ModifiedDate, System.currentTimeMillis());
            values.put(KEY_Createdby, sloc.getCreatedBy());

            String loctype = sloc.getLocationType();
            if (loctype != null && loctype.contains("f")) {
                loctype = loctype.replace("f", "");
                loctype = loctype.trim();
            }
            values.put(KEY_LOCATIONTYPE, loctype);
            values.put(KEY_formDefault, 0);

            try {
                ret = database.insert(DbAccess.TABLE_LOCATION, null, values);
                Log.i(TAG, "Store Map Location=" + ret);
                if (ret > 0)
                    return sloc;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "storeLocation=" + ret + "Exception Mesg=" + e.getLocalizedMessage());
            }
        }

        return null;
    }

    public void updateFieldPointName(String locationID, String updatedLocationName, String latitude, String longitude) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_Location, updatedLocationName);
        contentValues.put(KEY_Latitude, latitude);
        contentValues.put(KEY_Longitude, longitude);
        contentValues.put(KEY_ModifiedDate, System.currentTimeMillis());
        contentValues.put(KEY_SyncFlag, 1);
        contentValues.put(KEY_Status, 1);

        String whereClause = "LocationID = ?";
        String[] whereArgs = new String[]{locationID};
        try {
            database.update(DbAccess.TABLE_LOCATION, contentValues, whereClause, whereArgs);
            Log.e("updateLocName", "updateFieldPointName: location Name Updated SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("updateLocName", "updateFieldPointName: location Name Does Not Updated " + e.getLocalizedMessage());
        }
    }

    public ArrayList<NewClientLocation> getUpdatedLocationNames(String locationID) {
        ArrayList<NewClientLocation> arrayList = new ArrayList<>();

        try {
            String query = "select LocationID, Location, Latitude, Longitude, ModifiedDate from s_Location where LocationID = " + locationID;

            Cursor cursor = null;

            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                do {

                    NewClientLocation objnewClientLocation = new NewClientLocation();

                    objnewClientLocation.setLocationId(cursor.getString(0));
                    objnewClientLocation.setLocation(cursor.getString(1));
                    objnewClientLocation.setSiteId("");
                    objnewClientLocation.setLocationAlias("");
                    objnewClientLocation.setStatus("");
                    objnewClientLocation.setLatitude(String.valueOf(cursor.getDouble(2)));
                    objnewClientLocation.setLongitude(String.valueOf(cursor.getDouble(3)));
                    objnewClientLocation.setToc("");
                    objnewClientLocation.setAoc("");
                    objnewClientLocation.setAquiferZone("");
                    objnewClientLocation.setInstallDate("");
                    objnewClientLocation.setCasingType("");
                    objnewClientLocation.setStartScreen("");
                    objnewClientLocation.setEndScreen("");
                    objnewClientLocation.setDtb("");
                    objnewClientLocation.setExtField1("");
                    objnewClientLocation.setExtField2("");
                    objnewClientLocation.setExtField3("");
                    objnewClientLocation.setExtField4("");
                    objnewClientLocation.setExtField5("");
                    objnewClientLocation.setExtField6("");
                    objnewClientLocation.setExtField7("");
                    objnewClientLocation.setNotes("");
                    objnewClientLocation.setCreationDate("");
                    objnewClientLocation.setModifiedDate(cursor.getString(4));
                    objnewClientLocation.setCreatedBy("");
                    objnewClientLocation.setLocationType("");
                    arrayList.add(objnewClientLocation);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void deleteLocation(String locationId) {
/*
        String query = "update s_Location set Status = 0 and SyncFlag = 1 where LocationID = " + locationId;
        try {
            database.execSQL(query);
        } catch (Exception e) {
            Log.e(TAG, "deleteLocation: " + e);
        }
*/

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_Status, 0);
        contentValues.put(KEY_SyncFlag, 1);

        String whereClause = "LocationID = ?";
        String[] whereArgs = new String[]{locationId};
        try {
            database.update(DbAccess.TABLE_LOCATION, contentValues, whereClause, whereArgs);
            Log.e("deleteLoc", "location deleted");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("deleteLoc", "location not deleted " + e.getLocalizedMessage());
        }
    }
}
