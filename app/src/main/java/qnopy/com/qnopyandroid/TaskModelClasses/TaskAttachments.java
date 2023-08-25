package qnopy.com.qnopyandroid.TaskModelClasses;

import java.io.Serializable;

public class TaskAttachments implements Serializable {

    private Integer taskAttachmentId;
    private Integer clientTaskAttachmentId;
    private Integer taskId;
    private String fileName;
    private String fileExtension;
    private String fileKey;
    private String attachmentDescription;
    private Integer commentId;
    private Integer displayFlag;
    private Double latitude;
    private Double longitude;
    private Integer createdBy;
    private Long creationDate;
    private Integer modifiedBy;
    private Long modificationDate;
    private Boolean mediaUploadStatus;

    public Integer getClientTaskAttachmentId() {
        return clientTaskAttachmentId;
    }

    public void setClientTaskAttachmentId(Integer clientTaskAttachmentId) {
        this.clientTaskAttachmentId = clientTaskAttachmentId;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Integer getTaskAttachmentId() {
        return taskAttachmentId;
    }

    public void setTaskAttachmentId(Integer taskAttachmentId) {
        this.taskAttachmentId = taskAttachmentId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
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

    public String getAttachmentDescription() {
        return attachmentDescription;
    }

    public void setAttachmentDescription(String attachmentDescription) {
        this.attachmentDescription = attachmentDescription;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(Integer displayFlag) {
        this.displayFlag = displayFlag;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Boolean getMediaUploadStatus() {
        return mediaUploadStatus;
    }

    public void setMediaUploadStatus(Boolean mediaUploadStatus) {
        this.mediaUploadStatus = mediaUploadStatus;
    }
}
