package qnopy.com.qnopyandroid.requestmodel;

public class RType {

    private Integer typeId;

    private String type;

    private String notes;

    private Long creationDate;

    private Integer createdBy;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer id) {
        this.typeId = id;
    }

}
