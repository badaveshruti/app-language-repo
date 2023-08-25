package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class ConstructionMediaDataModel implements Serializable {

    private int postId;
    private int mediaId;
    private String fileName;
    private String fileKey;
    private int displayFlag;
    private int createdBy;
    private int modifiedBy;
    private Long serverCreationDate;
    private Long serverModificationDate;
    private Long creationDate;
    private Long modificationDate;
    private Double latitude;
    private Double longitude;
    private String caption;
    private int sMediaId;
    private int siteId;
    private String clientMediaId;
    private String attachmentType;
    private String mediaUploadStatus;
    private String file;
    //private transient MultipartFile file;


    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public int getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(int displayFlag) {
        this.displayFlag = displayFlag;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getServerCreationDate() {
        return serverCreationDate;
    }

    public void setServerCreationDate(Long serverCreationDate) {
        this.serverCreationDate = serverCreationDate;
    }

    public Long getServerModificationDate() {
        return serverModificationDate;
    }

    public void setServerModificationDate(Long serverModificationDate) {
        this.serverModificationDate = serverModificationDate;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getsMediaId() {
        return sMediaId;
    }

    public void setsMediaId(int sMediaId) {
        this.sMediaId = sMediaId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getClientMediaId() {
        return clientMediaId;
    }

    public void setClientMediaId(String clientMediaId) {
        this.clientMediaId = clientMediaId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getMediaUploadStatus() {
        return mediaUploadStatus;
    }

    public void setMediaUploadStatus(String mediaUploadStatus) {
        this.mediaUploadStatus = mediaUploadStatus;
    }

    /*public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }*/

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
