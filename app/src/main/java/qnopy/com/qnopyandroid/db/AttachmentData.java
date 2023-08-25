package qnopy.com.qnopyandroid.db;

public class AttachmentData {
    int EventID;// INT NOT NULL REFERENCES d_Event (EventID)
    String LocationID;// INT NOT NULL REFERENCES s_Location (LocationID)
    String AttachmentType = null;
    String FileLocation = null;
    double Latitude;// REAL
    double Longitude;// REAL
    String Notes = null;// VARCHAR(200)
    long CreationDate;// long
    String EmailSentFlag;// VARCHAR(1)
    String DataSyncFlag;// VARCHAR(1)
    private Long timeTaken;
    private Integer userId;
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
    private String name;

    //for local use
    private String file1000;
    private String fileThumb;
    //end

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public int getEventID() {
        return EventID;
    }

    public void setEventID(int id) {
        EventID = id;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String loc) {
        LocationID = loc;
    }

    public String getAttachmentType() {
        return AttachmentType;
    }

    public void setAttachementType(String type) {
        AttachmentType = type;
    }

    public String getFileLocation() {
        return FileLocation;
    }

    public void setFileLocation(String location) {
        FileLocation = location;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double value) {
        Latitude = value;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double value) {
        Longitude = value;
    }

    public void setCreationDate(long date) {
        CreationDate = date;
    }

    public long getCreationData() {
        return CreationDate;
    }

    public String getEmailSentFlag() {
        return EmailSentFlag;
    }

    public void setEmailSentFlag(String flag) {
        EmailSentFlag = flag;
    }


    public String getDataSyncFlag() {
        return DataSyncFlag;
    }

    public void setDataSyncFlag(String flag) {
        DataSyncFlag = flag;
    }
}


