package qnopy.com.qnopyandroid.requestmodel;

import java.util.HashSet;
import java.util.Set;

public class DEvent {

    private Integer eventId;
    private Set<DAttachment> attachments;
    private Set<DFieldData> fieldDatas;
    private String deviceId;
    private String mobileAppName;
    private Integer mobileAppId;
    private String siteName;
    private Integer siteId;
    private Integer createdBy;
    private Long eventDate;
    private Double latitude;
    private Double longitude;
    private String userName;
    private Integer userId;
    private String notes;
    private Long eventCreationDate;
    private Long eventStartDate;
    private Long eventEndDate;
    private String eventName;
    private Long eventModificationDate;
    private boolean active;
    private String eventUserName;

    public String getEventUserName() {
        return eventUserName;
    }

    public void setEventUserName(String eventUserName) {
        this.eventUserName = eventUserName;
    }

    //used for api purpose for rename for convenience
    private int createEventFlag = 1;

    public int getCreateEventFlag() {
        return createEventFlag;
    }

    public void setCreateEventFlag(int createEventFlag) {
        this.createEventFlag = createEventFlag;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Long getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Long eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Long getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Long eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

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

    public Set<DAttachment> getAttachments() {
        if (null == this.attachments) {
            this.attachments = new HashSet<DAttachment>();
        }
        return attachments;
    }

    public void setAttachments(Set<DAttachment> attachments) {
        this.attachments = attachments;
    }

    public Set<DFieldData> getFieldDatas() {
        if (null == this.fieldDatas) {
            this.fieldDatas = new HashSet<DFieldData>();
        }
        return fieldDatas;
    }

    public void setFieldDatas(Set<DFieldData> fieldDatas) {
        this.fieldDatas = fieldDatas;
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

    public Long getEventCreationDate() {
        return eventCreationDate;
    }

    public void setEventCreationDate(Long eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
    }

    public Long getEventModificationDate() {
        return eventModificationDate;
    }

    public void setEventModificationDate(Long eventModificationDate) {
        this.eventModificationDate = eventModificationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
