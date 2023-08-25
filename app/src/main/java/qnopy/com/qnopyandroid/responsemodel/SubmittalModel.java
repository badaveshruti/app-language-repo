package qnopy.com.qnopyandroid.responsemodel;

/**
 * Created by Yogendra on 11/10/2017.
 */

public class SubmittalModel {

    private int eventId;
    private int siteId;
    private String deviceId;
    private long eventDate;
    private String latitude;
    private String longitude;
    private int mobileAppId;
    private String notes;
    private String userGuid;
    private Integer eventStatus;
    private long eventCreationDate;
    private long eventModificationDate;
    private Integer createdBy;
    private long eventStartDate;
    private long eventEndDate;
    private String eventName;
    private String eventUserName;

    public String getEventUserName() {
        return eventUserName;
    }

    public void setEventUserName(String eventUserName) {
        this.eventUserName = eventUserName;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public long getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(long eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public long getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(long eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(int mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public long getEventCreationDate() {
        return eventCreationDate;
    }

    public void setEventCreationDate(long eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
    }

    public long getEventModificationDate() {
        return eventModificationDate;
    }

    public void setEventModificationDate(long eventModificationDate) {
        this.eventModificationDate = eventModificationDate;
    }
}
