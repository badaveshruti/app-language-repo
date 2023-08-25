package qnopy.com.qnopyandroid.requestmodel;

import java.io.Serializable;

public class SSite implements Serializable {

    private Integer siteId;
    private Integer parentSiteId;
    private String siteName;

    private String siteNumber;
    private String address1;
    private String address2;
    private String mobileReportRequired;
    private String clientName;

    private String city;

    private String state;

    private String zipCode;
    private String status;
    private String siteType;
    private Double latitude;
    private Double longitude;
    private Long creationDate;
    private Long modifiedDate;
    private boolean insert;

    public String getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getParentSiteId() {
        return parentSiteId;
    }

    public void setParentSiteId(Integer parentSiteId) {
        this.parentSiteId = parentSiteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Integer id) {
        this.siteId = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getMobileReportRequired() {
        return mobileReportRequired;
    }

    public void setMobileReportRequired(String mobileReportRequired) {
        this.mobileReportRequired = mobileReportRequired;
    }

    //    public String getSiteNumber() {
//        return siteNumber;
//    }

//    public void setSiteNumber(String siteNumber) {
//        this.siteNumber = siteNumber;
//    }

//
//    public String getCity() {
//        return city;
//    }

//    public void setCity(String city) {
//        this.city = city;
//    }

//    public String getState() {
//        return state;
//    }

//    public void setState(String state) {
//        this.state = state;
//    }

//    public String getZipCode() {
//        return zipCode;
//    }
//
//    public void setZipCode(String zipCode) {
//        this.zipCode = zipCode;
//    }

//    public String getGeoTrackerId() {
//        return geoTrackerId;
//    }

    //    public void setGeoTrackerId(String geoTrackerId) {
//        this.geoTrackerId = geoTrackerId;
//    }
//
//    public String getEpaid() {
//        return epaid;
//    }
//
//    public void setEpaid(String epaid) {
//        this.epaid = epaid;
//    }
//
//    public Long getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(Long startDate) {
//        this.startDate = startDate;
//    }
//
//    public Long getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(Long endDate) {
//        this.endDate = endDate;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
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

//    public Float getZ() {
//        return z;
//    }
//
//    public void setZ(Float z) {
//        this.z = z;
//    }
//
//    public String getExtField1() {
//        return extField1;
//    }
//
//    public void setExtField1(String extField1) {
//        this.extField1 = extField1;
//    }
//
//    public String getExtField2() {
//        return extField2;
//    }
//
//    public void setExtField2(String extField2) {
//        this.extField2 = extField2;
//    }
//
//    public String getExtField3() {
//        return extField3;
//    }
//
//    public void setExtField3(String extField3) {
//        this.extField3 = extField3;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
//    public Long getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Long creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    public Long getModifiedDate() {
//        return modifiedDate;
//    }
//
//    public void setModifiedDate(Long modifiedDate) {
//        this.modifiedDate = modifiedDate;
//    }
//
//    public Integer getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(Integer createdBy) {
//        this.createdBy = createdBy;
//    }

}
