package qnopy.com.qnopyandroid.TaskModelClasses;

import java.io.Serializable;

public class TaskComments implements Serializable {

    private Integer taskCommentId;
    private Integer taskId;
    private String comment;
    private Integer isAttachment;
    private Integer createdBy;
    private Long creationDate;
    private Integer modifiedBy;
    private Long modificationDate;


    public Integer getTaskCommentId() {
        return taskCommentId;
    }

    public void setTaskCommentId(Integer taskCommentId) {
        this.taskCommentId = taskCommentId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIsAttachment() {
        return isAttachment;
    }

    public void setIsAttachment(Integer isAttachment) {
        this.isAttachment = isAttachment;
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
}
