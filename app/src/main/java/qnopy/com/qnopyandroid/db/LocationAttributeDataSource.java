package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.ModelClassLocationsWithAttribute;
import qnopy.com.qnopyandroid.clientmodel.location_attribute_child_row;
import qnopy.com.qnopyandroid.responsemodel.SLocationAttribute;

/**
 * Created by QNOPY_YOGENDRA on 7/3/2018.
 */

public class LocationAttributeDataSource {


    private static final String TAG = "LocationAttributeDS";
    Context mContext;
    SQLiteDatabase database;


    final String KEY_LocAttributesId = "locAttributesId";
    final String KEY_LocationID = "locationID";
    final String KEY_attributeName = "attributeName";
    final String KEY_attributeValue = "attributeValue";
    final String KEY_createdBy = "createdBy";
    final String KEY_creationDate = "creationDate";
    final String KEY_modifiedBy = "modifiedBy";
    final String KEY_modifiedDate = "modifiedDate";

    public LocationAttributeDataSource(Context context) {
        this.mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public int storeBulkLocationAttributeList(List<SLocationAttribute> laList) {

        int listSize = laList.size();

        boolean isTableEmpty = MetaDataSource.isTableEmpty(DbAccess.TABLE_S_LOCATION_ATTRIBUTE,
                database);

        database.beginTransaction();

        try {

            for (SLocationAttribute loc : laList) {
                ContentValues values = new ContentValues();

                values.put(KEY_attributeName, loc.getAttributeName());

                values.put(KEY_LocAttributesId, loc.getLocAttributesId());
                values.put(KEY_attributeValue, loc.getAttributeValue());
                values.put(KEY_createdBy, loc.getCreatedBy());
                values.put(KEY_modifiedBy, loc.getModifiedBy());

                values.put(KEY_creationDate, loc.getCreationDate());
                values.put(KEY_modifiedDate, loc.getModifiedDate());
                values.put(SiteDataSource.KEY_Status, loc.getStatus());

                //   values.put(KEY_LOCATIONTYPE,loc.getLocationType());
                try {
                    if (loc.isInsert() || isTableEmpty) {
                        values.put(KEY_LocationID, loc.getLocationId());
                        listSize = (int) database.insert(DbAccess.TABLE_S_LOCATION_ATTRIBUTE, null, values);
                    } else {
                        String whereClause = "LocationID = ?";
                        String[] whereArgs = new String[]{loc.getLocationId() + ""};
                        listSize = database.update(DbAccess.TABLE_S_LOCATION_ATTRIBUTE, values,
                                whereClause, whereArgs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "storeBulkLocationAttributeList() Error in store:" + e.getMessage());
                }
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Bulk insertion of Location Attribute:" + e.getMessage());
        } finally {
            database.endTransaction();
        }

        return listSize;
    }

    public HashMap<String, List<location_attribute_child_row>> getData(String siteID) {

        HashMap<String, List<location_attribute_child_row>> expandableListDetail = new HashMap<String, List<location_attribute_child_row>>();

        String query = "select DISTINCT attributeName from s_LocationAttribute" +
                " WHERE attributeName!=null or attributeName!=''";

        query = "select distinct s.attributeName from s_LocationAttribute s " +
                " INNER JOIN  s_Location l on s.locationID = l.LocationID" +
                " where (s.attributeName!=null or s.attributeName!='') and l.SiteID= " + siteID;

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {

            do {

                ArrayList<location_attribute_child_row> childs = new ArrayList<>();

                String title = c.getString(0);
                childs = getAttributeValuesForAttributeName(title, siteID);
                expandableListDetail.put(title, childs);

            } while (c.moveToNext());
            c.close();
        }


//        expandableListDetail = new HashMap<String, List<location_attribute_child_row>>();

        return expandableListDetail;
    }

    public ArrayList<location_attribute_child_row> getAttributeValuesForAttributeName(String attributeName, String siteID) {

        ArrayList<location_attribute_child_row> childlist = new ArrayList<>();

/*        String query = "select DISTINCT attributeValue from s_LocationAttribute" +
                " WHERE attributeName='" + attributeName + "' and (attributeValue!=null or attributeValue!='') ";*/

        String query = "select distinct attributeValue from s_LocationAttribute s INNER JOIN " +
                "s_Location l on s.LocationID = l.LocationID  WHERE s.attributeName='" + attributeName + "' " +
                "and (s.attributeValue!=null or s.attributeValue!='') and l.SiteID=" + siteID;

        Log.i(TAG, "getAttributeValuesForAttributeName() query:" + query);
        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {

            do {
                location_attribute_child_row row = new location_attribute_child_row();

                String child_title = c.getString(0);

                row.setTitle(attributeName);
                row.setChild_title(child_title);

                childlist.add(row);

            } while (c.moveToNext());
            c.close();
        }
        return childlist;
    }

    public ArrayList<ModelClassLocationsWithAttribute> getAllLocationWithAttribute(String siteID) {
        ArrayList<ModelClassLocationsWithAttribute> arrayList = new ArrayList<>();
        String query = "select distinct s.locationID ,s.attributeName, s.attributeValue from s_LocationAttribute s " +
                " INNER JOIN  s_Location l on s.locationID = l.LocationID" +
                " where l.SiteID= " + siteID;

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {

            do {
                ModelClassLocationsWithAttribute modelClassLocationsWithAttribute = new ModelClassLocationsWithAttribute();
                modelClassLocationsWithAttribute.setmLocationId(c.getString(0));
                modelClassLocationsWithAttribute.setmAttributeName(c.getString(1));
                modelClassLocationsWithAttribute.setmAttributeValue(c.getString(2));
                arrayList.add(modelClassLocationsWithAttribute);

                /*String locId = c.getString(0);
                String attrName = c.getString(1);
                String attrValue = c.getString(2);
                arrayList.add(locId);
                arrayList.add(attrName);
                arrayList.add(attrValue);*/
                Log.e(TAG, "getAllLocationWithAttribute: " + c.getString(0));

            } while (c.moveToNext());
            c.close();
        }
        return arrayList;
    }

    public HashMap<String, List<location_attribute_child_row>> getSearchedData(String siteID, String attributeValue) {
        HashMap<String, List<location_attribute_child_row>> newExpandableListDetail = new HashMap<String, List<location_attribute_child_row>>();
        String query = "select distinct s.attributeName from s_LocationAttribute s " +
                " INNER JOIN  s_Location l on s.locationID = l.LocationID" +
                " where (s.attributeName!=null or s.attributeName!='') and l.SiteID= " + siteID;

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {

            do {

                ArrayList<location_attribute_child_row> searchedChilds = new ArrayList<>();

                String title = c.getString(0);
                searchedChilds = getSearchedAttributeValuesForAttributeNameandAttributeValue(title, attributeValue);
                if (searchedChilds.size() > 0) {
                    newExpandableListDetail.put(title, searchedChilds);
                }

            } while (c.moveToNext());
            c.close();
        }
        return newExpandableListDetail;
    }

    public ArrayList<location_attribute_child_row> getSearchedAttributeValuesForAttributeNameandAttributeValue(String attributeName, String attributeValue) {
        ArrayList<location_attribute_child_row> newSearchedChildlist = new ArrayList<>();

        String query = "select DISTINCT attributeValue from s_LocationAttribute" +
                " WHERE attributeName='" + attributeName + "' and attributeValue like '" + '%' + attributeValue + '%' + "' and (attributeValue!=null or attributeValue!='')  ";

        Log.i(TAG, "getAttributeValuesForAttributeName() query:" + query);
        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {

            do {

                location_attribute_child_row row = new location_attribute_child_row();

                String child_title = c.getString(0);

                row.setTitle(attributeName);
                row.setChild_title(child_title);

                newSearchedChildlist.add(row);

            } while (c.moveToNext());
            c.close();
        }

        return newSearchedChildlist;
    }
}

