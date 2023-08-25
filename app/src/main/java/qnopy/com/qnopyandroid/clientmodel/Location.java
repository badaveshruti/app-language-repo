package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class Location implements Serializable {
    private String LocationID;
    private int SiteID;
    private String LocationName;
    private String LocationDesc;
    private String ExtField7;
    private String ExtField2;
    private String Latitude;
    private String Longitude;
    private int percentage = 0;
    private String cocflag;
    private String locationType;
    private String locFormHeader;
    private String locInstruction;
    private int userID;
    private int formDefault;
    private String locationTabs;

    //added for personal use
    private boolean selected;

    private boolean hasLocationPics;

    public Location() {
    }

    public Location(String locationID) {
        LocationID = locationID;
    }

    public boolean isHasLocationPics() {
        return hasLocationPics;
    }

    public void setHasLocationPics(boolean hasLocationPics) {
        this.hasLocationPics = hasLocationPics;
    }

    public String getLocationTabs() {
        return locationTabs;
    }

    public void setLocationTabs(String locationTabs) {
        this.locationTabs = locationTabs;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getFormDefault() {
        return formDefault;
    }

    public void setFormDefault(int formDefault) {
        this.formDefault = formDefault;
    }

    public String getLocFormHeader() {
        return locFormHeader;
    }

    public void setLocFormHeader(String locFormHeader) {
        this.locFormHeader = locFormHeader;
    }

    public String getLocInstruction() {
        return locInstruction;
    }

    public void setLocInstruction(String locInstruction) {
        this.locInstruction = locInstruction;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getCocflag() {
        return cocflag;
    }

    public void setCocflag(String cocflag) {
        this.cocflag = cocflag;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
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

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String id) {
        this.LocationID = id;
    }

    public int getSiteID() {
        return SiteID;
    }

    public void setSiteID(int id) {
        this.SiteID = id;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String name) {
        this.LocationName = name;
    }

    public void setLocationDesc(String desc) {
        this.LocationDesc = desc;
    }

    public String getLocationDesc() {
        return this.LocationDesc;
    }

    public void setExtField2(String field) {
        this.ExtField2 = field;
    }

    public String getExtField2() {
        return this.ExtField2;
    }

    public void setExtField7(String field) {
        this.ExtField7 = field;
    }

    public String getExtField7() {
        return this.ExtField7;
    }

    @Override
    public String toString() {
        return LocationName;
    }
}

