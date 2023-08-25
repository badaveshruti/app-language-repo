package qnopy.com.qnopyandroid.requestmodel;

public class RStatus {

    private Integer statusId;

    private String status;

    private String notes;

    private Long creationDate;

    private Integer createdBy;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getStatusId() {
        return this.statusId;
    }

    public void setStatusId(Integer id) {
        this.statusId = id;
    }

}
