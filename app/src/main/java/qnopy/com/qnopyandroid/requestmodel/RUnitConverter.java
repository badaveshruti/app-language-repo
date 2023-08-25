package qnopy.com.qnopyandroid.requestmodel;

public class RUnitConverter {

    private Integer id;

    private String fromUnits;

    private String toUnits;

    private Double multiplier;

    private String notes;

    private Long creationDate;

    private Integer createdBy;

    public String getFromUnits() {
        return fromUnits;
    }

    public void setFromUnits(String fromUnits) {
        this.fromUnits = fromUnits;
    }

    public String getToUnits() {
        return toUnits;
    }

    public void setToUnits(String toUnits) {
        this.toUnits = toUnits;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
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

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
