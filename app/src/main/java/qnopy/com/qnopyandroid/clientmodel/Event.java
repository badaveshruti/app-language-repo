package qnopy.com.qnopyandroid.clientmodel;

public class Event {

    private Integer eventId;

    private String deviceId;

    private String mobileAppName;

    private Integer mobileAppId;

    private String siteName;

    private Integer siteId;

    private Long eventDate;

    private Long eventEndDateTime;

    private Long eventStartDateTime;

    private Double latitude;

    private Double longitude;

    private String userName;

    private Integer userId;

    private String notes;

    private Integer eventStatus;

    private String generatedBy;

    public String getMobileAppName() {
        return mobileAppName;
    }

    public void setMobileAppName(String mobileAppName) {
        this.mobileAppName = mobileAppName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(Integer mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public Long getEventStartDateTime() {
        return eventStartDateTime;
    }

    public void setEventStartDateTime(Long eventStartDateTime) {
        this.eventStartDateTime = eventStartDateTime;
    }

    public Long getEventEndDateTime() {
        return eventEndDateTime;
    }

    public void setEventEndDateTime(Long eventEndDateTime) {
        this.eventEndDateTime = eventEndDateTime;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer id) {
        this.eventId = id;
    }

}
