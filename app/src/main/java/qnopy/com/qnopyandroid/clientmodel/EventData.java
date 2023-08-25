package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

import qnopy.com.qnopyandroid.ui.calendarUser.AllEventsAdapter;

/**
 * Created by Yogendra on 05-Jul-16.
 */
public class EventData implements Serializable {
    private String siteName;
    private String mobAppName;
    private long startDate, endDate;
    private int siteID;
    private int eventID;
    private int mobAppID;
    private int locationCount = 0;
    private int status;//0=closed,1=active,2=conflict
    private String eventName;//0=closed,1=active,2=conflict

    //added on 10 Aug 20 for all events purpose
    private int userId;
    private String updatedBy;
    private String extField4;
    private long sortedDate;
    private String eventDateFormatted;
    private int viewType = AllEventsAdapter.ITEM_VIEW;
    private boolean isExpanded;
    private long updatedDate;
    private long modificationDate;
    private String eventUserName;

    public String getEventUserName() {
        return eventUserName;
    }

    public void setEventUserName(String eventUserName) {
        this.eventUserName = eventUserName;
    }

    public long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getEventDateFormatted() {
        return eventDateFormatted;
    }

    public void setEventDateFormatted(String eventDateFormatted) {
        this.eventDateFormatted = eventDateFormatted;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getExtField4() {
        return extField4;
    }

    public void setExtField4(String extField4) {
        this.extField4 = extField4;
    }

    public long getSortedDate() {
        return sortedDate;
    }

    public void setSortedDate(long sortedDate) {
        this.sortedDate = sortedDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getLocationCount() {
        return locationCount;
    }

    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }

    public String getMobAppName() {
        return mobAppName;
    }

    public void setMobAppName(String mobAppName) {
        this.mobAppName = mobAppName;
    }

    public int getMobAppID() {
        return mobAppID;
    }

    public void setMobAppID(int mobAppID) {
        this.mobAppID = mobAppID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Override
    public String toString() {
        return "dashboard_data_card{" +
                "siteName='" + siteName + '\'' +
                ", mobAppName='" + mobAppName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", siteID=" + siteID +
                ", eventID=" + eventID +
                ", mobAppID=" + mobAppID +
                ", locationCount=" + locationCount +
                ", status=" + status +
                ", eventName='" + eventName + '\'' +
                ", userId=" + userId +
                ", updatedBy='" + updatedBy + '\'' +
                ", extField4='" + extField4 + '\'' +
                ", sortedDate=" + sortedDate +
                ", eventDateFormatted='" + eventDateFormatted + '\'' +
                ", viewType=" + viewType +
                ", isExpanded=" + isExpanded +
                ", updatedDate=" + updatedDate +
                ", modificationDate=" + modificationDate +
                '}';
    }
}
