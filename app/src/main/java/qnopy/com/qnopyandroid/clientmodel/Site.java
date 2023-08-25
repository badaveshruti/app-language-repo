package qnopy.com.qnopyandroid.clientmodel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;

public class Site implements Serializable {
    int SiteID;
    String SiteName;
    String Status;
    private boolean favStatus;
    String siteType;
    String Notes;
    Double latitude;
    Double longitude;
    long lastUpdatedDate;//date will be taken from dFieldData to know if site data updated recently or not

    //added for my convenience 4 May, 22
    private boolean isExpanded;
    private ArrayList<EventData> eventList = new ArrayList<>();
    private ArrayList<SSiteMobileApp> formsList = new ArrayList<>();

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public ArrayList<EventData> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<EventData> eventList) {
        this.eventList = eventList;
    }

    public ArrayList<SSiteMobileApp> getFormsList() {
        return formsList;
    }

    public void setFormsList(ArrayList<SSiteMobileApp> formsList) {
        this.formsList = formsList;
    }

    public boolean isFavStatus() {
        return favStatus;
    }

    public void setFavStatus(boolean favStatus) {
        this.favStatus = favStatus;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getSiteID() {
        return SiteID;
    }

    public void setSiteID(int id) {
        this.SiteID = id;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String name) {
        this.SiteName = name;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    @Override
    public String toString() {
        return SiteID + " " + SiteName + " " + favStatus;
    }
}
