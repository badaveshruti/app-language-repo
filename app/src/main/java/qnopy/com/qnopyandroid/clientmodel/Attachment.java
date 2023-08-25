package qnopy.com.qnopyandroid.clientmodel;


import qnopy.com.qnopyandroid.requestmodel.DEvent;

public class Attachment {


//    private Integer eventId;

    private DEvent event;

    //    private SLocation location;
    private String locationId;
    private String fileLocation;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    private String attachmentType;

    private Long timeTaken;

    private Double latitude;

    private Double longitude;

    private Integer userId;

    private String deviceId;

    private String notes;

    private Long creationDate;

    private Long id;

    private Integer mobileAppId;

    private Integer SiteID;

    private Integer SetID;
    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;

    private String extField6;

    private String extField7;

    private String fieldParameterID;
    private String attachmentDate;
    private String attachmentTime;
    private String modificationDate;
    private String azimuth;

    //for local use
    private String file1000;
    private String fileThumb;
    //end

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFile1000() {
        return file1000;
    }

    public void setFile1000(String file1000) {
        this.file1000 = file1000;
    }

    public String getFileThumb() {
        return fileThumb;
    }

    public void setFileThumb(String fileThumb) {
        this.fileThumb = fileThumb;
    }

    public String getFieldParameterID() {
        return fieldParameterID;
    }

    public void setFieldParameterID(String fieldParameterID) {
        this.fieldParameterID = fieldParameterID;
    }

    public String getAttachmentDate() {
        return attachmentDate;
    }

    public void setAttachmentDate(String attachmentDate) {
        this.attachmentDate = attachmentDate;
    }

    public String getAttachmentTime() {
        return attachmentTime;
    }

    public void setAttachmentTime(String attachmentTime) {
        this.attachmentTime = attachmentTime;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public Integer getSetId() {
        return SetID;
    }

    public void setSetId(Integer setId) {
        this.SetID = setId;
    }

    public Integer getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(Integer mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public Integer getSiteId() {
        return SiteID;
    }

    public void setSiteId(Integer siteId) {
        this.SiteID = siteId;
    }

    public String getExtField1() {
        return extField1;
    }

    public void setExtField1(String extField1) {
        this.extField1 = extField1;
    }

    public String getExtField2() {
        return extField2;
    }

    public void setExtField2(String extField2) {
        this.extField2 = extField2;
    }

    public String getExtField3() {
        return extField3;
    }

    public void setExtField3(String extField3) {
        this.extField3 = extField3;
    }

    public String getExtField4() {
        return extField4;
    }

    public void setExtField4(String extField4) {
        this.extField4 = extField4;
    }

    public String getExtField5() {
        return extField5;
    }

    public void setExtField5(String extField5) {
        this.extField5 = extField5;
    }

    public String getExtField6() {
        return extField6;
    }

    public void setExtField6(String extField6) {
        this.extField6 = extField6;
    }

    public String getExtField7() {
        return extField7;
    }

    public void setExtField7(String extField7) {
        this.extField7 = extField7;
    }


    public DEvent getEvent() {
        return event;
    }

    public void setEvent(DEvent event) {
        this.event = event;
    }

//    public SLocation getLocation() {
//        return location;
//    }
//
//    public void setLocation(SLocation location) {
//        this.location = location;
//    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    //    public byte[] getPicture() {
    //        return picture;
    //    }
    //
    //    public void setPicture(byte[] picture) {
    //        this.picture = picture;
    //    }
    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //    public Integer getEventId() {
//        return this.eventId;
//    }
//
//    public void setEventId(Integer id) {
//        this.eventId = id;
//    }
    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String string) {
        // TODO Auto-generated method stub
        this.fileLocation = string;
    }
}
