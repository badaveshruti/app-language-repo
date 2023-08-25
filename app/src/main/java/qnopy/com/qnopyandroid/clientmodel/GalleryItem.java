package qnopy.com.qnopyandroid.clientmodel;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Yogendra on 15-Apr-16.
 */
public class GalleryItem implements Serializable {

    private String locationName;
    private String siteName;
    private String fileLocation;
    private String txtDate;
    private String uniqueID;
    private String txtNote;
    private Double latitude;
    private Double longitude;
    private int siteID;
    private String locID;
    private String eventID;
    private int mobAppID;
    private int setID;
    private String fieldParamID;
    private String azimuth;
    private String attachmentType;
    private String fileKey;
    private String fileKeyEncode;
    private String fileKeyThumb;
    private String fileKeyThumbEncode;
    private String originalFileName;
    private Bitmap imageLoaded;

    private String attachmentUUID;

    public String getAttachmentUUID() {
        return attachmentUUID;
    }

    public void setAttachmentUUID(String attachmentUUID) {
        this.attachmentUUID = attachmentUUID;
    }

    public Bitmap getImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(Bitmap imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    public String getFileKeyEncode() {
        return fileKeyEncode;
    }

    public void setFileKeyEncode(String fileKeyEncode) {
        this.fileKeyEncode = fileKeyEncode;
    }

    public String getFileKeyThumbEncode() {
        return fileKeyThumbEncode;
    }

    public void setFileKeyThumbEncode(String fileKeyThumbEncode) {
        this.fileKeyThumbEncode = fileKeyThumbEncode;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileKeyThumb() {
        return fileKeyThumb;
    }

    public void setFileKeyThumb(String fileKeyThumb) {
        this.fileKeyThumb = fileKeyThumb;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getFieldParamID() {
        return fieldParamID;
    }

    public void setFieldParamID(String fieldParamID) {
        this.fieldParamID = fieldParamID;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public int getSetID() {
        return setID;
    }

    public void setSetID(int setID) {
        this.setID = setID;
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

    public String getLocID() {
        return locID;
    }

    public void setLocID(String locID) {
        this.locID = locID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
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

//    public Bitmap getThumbnail() {
//        return thumbnail;
//    }
//
//    public void setThumbnail(Bitmap thumbnail) {
//        this.thumbnail = thumbnail;
//    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getTxtNote() {
        return txtNote;
    }

    public void setTxtNote(String txtNote) {
        this.txtNote = txtNote;
    }
}
