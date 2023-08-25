package qnopy.com.qnopyandroid.clientmodel;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.requestmodel.FieldDataForEventDownload;

public class DownloadEventDataResponse {
    private Data data;

    private boolean success;

    private String message;

    private String responseCode;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public static class Data {
        private ArrayList<FieldDataAttachment> fieldDataAttachementList;

        private ArrayList<FieldDataForEventDownload> fieldDataList;

        private String lastSyncDate;

        public String getLastSyncDate() {
            return lastSyncDate;
        }

        public void setLastSyncDate(String lastSyncDate) {
            this.lastSyncDate = lastSyncDate;
        }

        public ArrayList<FieldDataAttachment> getFieldDataAttachmentList() {
            return fieldDataAttachementList;
        }

        public void setFieldDataAttachmentList(ArrayList<FieldDataAttachment> fieldDataAttachment) {
            this.fieldDataAttachementList = fieldDataAttachment;
        }

        public ArrayList<FieldDataForEventDownload> getFieldDataList() {
            return fieldDataList;
        }

        public void setFieldDataList(ArrayList<FieldDataForEventDownload> fieldDataList) {
            this.fieldDataList = fieldDataList;
        }
    }

    public static class FieldDataAttachment {
        private String attachmentDate;

        private String fileKeyThumbImageEncode;

        private String notes;

        private String attachmentType;

        private String signedBy;

        private Double latitude;

        private String fileKeyImage;

        private String azimuth;

        private Integer pinId;

        private Integer attachmentVersion;

        private String deviceId;

        private Integer mobileAppId;

        private String fileKeyImageEncode;

        private String fileOnFileSystem;//file name from server

        private Integer locationId;

        private long serverModificationDate;

        private Integer fieldParameterId;

        private Integer setId;

        private Integer atobts;

        private Integer id;

        private long serverCreationDate;

        private Double longitude;

        private Integer photoOrder;

        private Integer eventId;

        private String attachmentTime;

        private String fileKeyThumbImage;

        private long creationDate;

        private Integer userId;

        private String extField3;

        private String timeTaken;

        private String extField4;

        private String extField1;

        private long modificationDate;

        private String extField2;

        private String extField7;

        private String extField5;

        private String extField6;

        private Integer siteId;

        private String modificationNotes;

        private String photoTimestamp;

        private Integer status;

        private String uuid;

        public String getAttachmentDate() {
            return attachmentDate;
        }

        public void setAttachmentDate(String attachmentDate) {
            this.attachmentDate = attachmentDate;
        }

        public String getFileKeyThumbImageEncode() {
            return fileKeyThumbImageEncode;
        }

        public void setFileKeyThumbImageEncode(String fileKeyThumbImageEncode) {
            this.fileKeyThumbImageEncode = fileKeyThumbImageEncode;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getAttachmentType() {
            return attachmentType;
        }

        public void setAttachmentType(String attachmentType) {
            this.attachmentType = attachmentType;
        }

        public String getSignedBy() {
            return signedBy;
        }

        public void setSignedBy(String signedBy) {
            this.signedBy = signedBy;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getFileKeyImage() {
            return fileKeyImage;
        }

        public void setFileKeyImage(String fileKeyImage) {
            this.fileKeyImage = fileKeyImage;
        }

        public String getAzimuth() {
            return azimuth;
        }

        public void setAzimuth(String azimuth) {
            this.azimuth = azimuth;
        }

        public Integer getPinId() {
            return pinId;
        }

        public void setPinId(Integer pinId) {
            this.pinId = pinId;
        }

        public Integer getAttachmentVersion() {
            return attachmentVersion;
        }

        public void setAttachmentVersion(Integer attachmentVersion) {
            this.attachmentVersion = attachmentVersion;
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

        public String getFileKeyImageEncode() {
            return fileKeyImageEncode;
        }

        public void setFileKeyImageEncode(String fileKeyImageEncode) {
            this.fileKeyImageEncode = fileKeyImageEncode;
        }

        public String getFileOnFileSystem() {
            return fileOnFileSystem;
        }

        public void setFileOnFileSystem(String fileOnFileSystem) {
            this.fileOnFileSystem = fileOnFileSystem;
        }

        public Integer getLocationId() {
            return locationId;
        }

        public void setLocationId(Integer locationId) {
            this.locationId = locationId;
        }

        public long getServerModificationDate() {
            return serverModificationDate;
        }

        public void setServerModificationDate(long serverModificationDate) {
            this.serverModificationDate = serverModificationDate;
        }

        public Integer getFieldParameterId() {
            return fieldParameterId;
        }

        public void setFieldParameterId(Integer fieldParameterId) {
            this.fieldParameterId = fieldParameterId;
        }

        public Integer getSetId() {
            return setId;
        }

        public void setSetId(Integer setId) {
            this.setId = setId;
        }

        public Integer getAtobts() {
            return atobts;
        }

        public void setAtobts(Integer atobts) {
            this.atobts = atobts;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public long getServerCreationDate() {
            return serverCreationDate;
        }

        public void setServerCreationDate(long serverCreationDate) {
            this.serverCreationDate = serverCreationDate;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Integer getPhotoOrder() {
            return photoOrder;
        }

        public void setPhotoOrder(Integer photoOrder) {
            this.photoOrder = photoOrder;
        }

        public Integer getEventId() {
            return eventId;
        }

        public void setEventId(Integer eventId) {
            this.eventId = eventId;
        }

        public String getAttachmentTime() {
            return attachmentTime;
        }

        public void setAttachmentTime(String attachmentTime) {
            this.attachmentTime = attachmentTime;
        }

        public String getFileKeyThumbImage() {
            return fileKeyThumbImage;
        }

        public void setFileKeyThumbImage(String fileKeyThumbImage) {
            this.fileKeyThumbImage = fileKeyThumbImage;
        }

        public long getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(long creationDate) {
            this.creationDate = creationDate;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getExtField3() {
            return extField3;
        }

        public void setExtField3(String extField3) {
            this.extField3 = extField3;
        }

        public String getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(String timeTaken) {
            this.timeTaken = timeTaken;
        }

        public String getExtField4() {
            return extField4;
        }

        public void setExtField4(String extField4) {
            this.extField4 = extField4;
        }

        public String getExtField1() {
            return extField1;
        }

        public void setExtField1(String extField1) {
            this.extField1 = extField1;
        }

        public long getModificationDate() {
            return modificationDate;
        }

        public void setModificationDate(long modificationDate) {
            this.modificationDate = modificationDate;
        }

        public String getExtField2() {
            return extField2;
        }

        public void setExtField2(String extField2) {
            this.extField2 = extField2;
        }

        public String getExtField7() {
            return extField7;
        }

        public void setExtField7(String extField7) {
            this.extField7 = extField7;
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

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public String getModificationNotes() {
            return modificationNotes;
        }

        public void setModificationNotes(String modificationNotes) {
            this.modificationNotes = modificationNotes;
        }

        public String getPhotoTimestamp() {
            return photoTimestamp;
        }

        public void setPhotoTimestamp(String photoTimestamp) {
            this.photoTimestamp = photoTimestamp;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
