package qnopy.com.qnopyandroid.clientmodel;

/**
 * Created by Yogendra on 26-Aug-16.
 */
public class MapLocation {
    String LocationID;
    int SiteID;
    String LocationName;
    String LocationDesc;
    String ExtField7;
    String ExtField2;
    String Latitude;
    String Longitude;
    boolean isData;

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String locationID) {
        LocationID = locationID;
    }

    public int getSiteID() {
        return SiteID;
    }

    public void setSiteID(int siteID) {
        SiteID = siteID;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getLocationDesc() {
        return LocationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        LocationDesc = locationDesc;
    }

    public String getExtField7() {
        return ExtField7;
    }

    public void setExtField7(String extField7) {
        ExtField7 = extField7;
    }

    public String getExtField2() {
        return ExtField2;
    }

    public void setExtField2(String extField2) {
        ExtField2 = extField2;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public boolean isData() {
        return isData;
    }

    public void setData(boolean data) {
        isData = data;
    }
}
