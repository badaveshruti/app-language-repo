package qnopy.com.qnopyandroid.requestmodel;


public class SLocationType {

    private Integer locationTypeId;

    private Integer locationId;

    private String locationType;

    public Integer getLocationTypeId() {
        return this.locationTypeId;
    }

    public void setLocationTypeId(Integer id) {
        this.locationTypeId = id;
    }

    private String notes;

    private Long creationDate;

    private Integer createdBy;

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
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

    public Integer getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
