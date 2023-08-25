package qnopy.com.qnopyandroid.responsemodel;

import java.io.Serializable;

public class SLocationAttribute implements Serializable {

    private static final long serialVersionUID = -1936263574205282704L;
    private Integer locAttributesId;
    private Integer locationId;
    private String attributeName;
    private String attributeValue;
    private Integer createdBy;
    private Long creationDate;
    private Integer modifiedBy;
    private Long modifiedDate;

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

    public Integer getLocAttributesId() {
        return locAttributesId;
    }

    public void setLocAttributesId(Integer locAttributesId) {
        this.locAttributesId = locAttributesId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
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

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
