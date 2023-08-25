package qnopy.com.qnopyandroid.responsemodel;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskDataResponse extends DefaultResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private ArrayList<CommentList> commentList;

        private ArrayList<TaskDataList> taskDataList;

        private ArrayList<AttachmentList> attachmentList;

        private String lastSyncDate;

        public ArrayList<CommentList> getCommentList() {
            return commentList;
        }

        public void setCommentList(ArrayList<CommentList> commentList) {
            this.commentList = commentList;
        }

        public ArrayList<TaskDataList> getTaskDataList() {
            return taskDataList;
        }

        public void setTaskDataList(ArrayList<TaskDataList> taskDataList) {
            this.taskDataList = taskDataList;
        }

        public ArrayList<AttachmentList> getAttachmentList() {
            return attachmentList;
        }

        public void setAttachmentList(ArrayList<AttachmentList> attachmentList) {
            this.attachmentList = attachmentList;
        }

        public String getLastSyncDate() {
            return lastSyncDate;
        }

        public void setLastSyncDate(String lastSyncDate) {
            this.lastSyncDate = lastSyncDate;
        }
    }

    public static class CommentList {
        private String modificationDate;

        private int createdBy;

        private int taskCommentId;

        private int clientCommentId;

        private String comment;

        private String modifiedBy;

        private long creationDate;

        private int taskId;

        private int isAttachment;

        public int getClientCommentId() {
            return clientCommentId;
        }

        public void setClientCommentId(int clientCommentId) {
            this.clientCommentId = clientCommentId;
        }

        public String getModificationDate() {
            return modificationDate;
        }

        public void setModificationDate(String modificationDate) {
            this.modificationDate = modificationDate;
        }

        public int getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(int createdBy) {
            this.createdBy = createdBy;
        }

        public int getTaskCommentId() {
            return taskCommentId;
        }

        public void setTaskCommentId(int taskCommentId) {
            this.taskCommentId = taskCommentId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public long getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(long creationDate) {
            this.creationDate = creationDate;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public int isAttachment() {
            return isAttachment;
        }

        public void setAttachment(int attachment) {
            isAttachment = attachment;
        }
    }

    public static class TaskDataList implements Serializable {

        private long modificationDate;

        private int createdBy;

        private int parentTaskId;

        private String taskOwner;

        private String taskDescription;

        private long dueDate;

        private int modifiedBy;

        private String taskTitle;

        private long creationDate;

        private int projectId;

        private int taskId;

        private int clientTaskId;

        private String taskStatus;

        private int fieldParameterId;

        private Long locationId;

        private Integer mobileAppId;

        private Integer setId;

        private Double latitude;

        private Double longitude;

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

        public void setFieldParameterId(Integer fieldParameterId) {
            this.fieldParameterId = fieldParameterId;
        }

        public Long getLocationId() {
            return locationId;
        }

        public void setLocationId(Long locationId) {
            this.locationId = locationId;
        }

        public Integer getMobileAppId() {
            return mobileAppId;
        }

        public void setMobileAppId(Integer mobileAppId) {
            this.mobileAppId = mobileAppId;
        }

        public Integer getSetId() {
            return setId;
        }

        public void setSetId(Integer setId) {
            this.setId = setId;
        }

        public int getFieldParameterId() {
            return fieldParameterId;
        }

        public void setFieldParameterId(int fieldParameterId) {
            this.fieldParameterId = fieldParameterId;
        }

        public int getClientTaskId() {
            return clientTaskId;
        }

        public void setClientTaskId(int clientTaskId) {
            this.clientTaskId = clientTaskId;
        }

        public long getModificationDate() {
            return modificationDate;
        }

        public void setModificationDate(long modificationDate) {
            this.modificationDate = modificationDate;
        }

        public int getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(int createdBy) {
            this.createdBy = createdBy;
        }

        public int getParentTaskId() {
            return parentTaskId;
        }

        public void setParentTaskId(int parentTaskId) {
            this.parentTaskId = parentTaskId;
        }

        public String getTaskOwner() {
            return taskOwner;
        }

        public void setTaskOwner(String taskOwner) {
            this.taskOwner = taskOwner;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

        public void setTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
        }

        public long getDueDate() {
            return dueDate;
        }

        public void setDueDate(long dueDate) {
            this.dueDate = dueDate;
        }

        public int getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(int modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public String getTaskTitle() {
            return taskTitle;
        }

        public void setTaskTitle(String taskTitle) {
            this.taskTitle = taskTitle;
        }

        public long getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(long creationDate) {
            this.creationDate = creationDate;
        }

        public int getProjectId() {
            return projectId;
        }

        public void setProjectId(int projectId) {
            this.projectId = projectId;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public String getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(String taskStatus) {
            this.taskStatus = taskStatus;
        }
    }

    public static class AttachmentList {
        private String fileName;

        private int displayFlag;

        private double latitude;

        private String fileKey;

        private long creationDate;

        private String mediaUploadStatus;

        private int taskAttachmentId;

        private String modificationDate;

        private int createdBy;

        private String fileExtension;

        private String attachmentDescription;

        private int commentId;

        private String modifiedBy;

        private int taskId;

        private double longitude;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getDisplayFlag() {
            return displayFlag;
        }

        public void setDisplayFlag(int displayFlag) {
            this.displayFlag = displayFlag;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getFileKey() {
            return fileKey;
        }

        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }

        public long getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(long creationDate) {
            this.creationDate = creationDate;
        }

        public String getMediaUploadStatus() {
            return mediaUploadStatus;
        }

        public void setMediaUploadStatus(String mediaUploadStatus) {
            this.mediaUploadStatus = mediaUploadStatus;
        }

        public int getTaskAttachmentId() {
            return taskAttachmentId;
        }

        public void setTaskAttachmentId(int taskAttachmentId) {
            this.taskAttachmentId = taskAttachmentId;
        }

        public String getModificationDate() {
            return modificationDate;
        }

        public void setModificationDate(String modificationDate) {
            this.modificationDate = modificationDate;
        }

        public int getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(int createdBy) {
            this.createdBy = createdBy;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getAttachmentDescription() {
            return attachmentDescription;
        }

        public void setAttachmentDescription(String attachmentDescription) {
            this.attachmentDescription = attachmentDescription;
        }

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
