package qnopy.com.qnopyandroid.clientmodel;

public class LocationProfilePictures {
    private int locationId;
    private String attachmentURL;
    private int attachmentId;
    private Long creationDate;
    private Long modificationDate;
    private String thumbnailURL;
    private String attachmentVersion;

    private boolean insert;
    private String status;

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getAttachmentVersion() {
        return attachmentVersion;
    }

    public void setAttachmentVersion(String attachmentVersion) {
        this.attachmentVersion = attachmentVersion;
    }
}
