package qnopy.com.qnopyandroid.requestmodel;


public class DFieldData {

    private Integer fieldDataId;

    private DEvent event;

    String fieldParameterLabel;

    String fieldParameterLabelAlias;

    Integer fieldParameterId;

    String location;

    Integer locationId;

    private Long measurementTime;

    private String stringValue;

    private Double numericValue;

    private String units;

    private Double latitude;

    private Double longitude;

    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;

    private String extField6;

    private String extField7;

    private String notes;

    private Long creationDate;

    private Integer parentSetId;

    private Double correctedLatitude;

    private Double correctedLongitude;

    private Integer mobileAppId;

    private Integer userId;

    private Integer siteId;

    private Integer setId;

    private Long serverCreationDate;

    private Long serverModificationDate;

    private Long modificationDate;

    public Integer getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(Integer mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public String getFieldParameterLabel() {
        return fieldParameterLabel;
    }

    public void setFieldParameterLabel(String fieldParameterLabel) {
        this.fieldParameterLabel = fieldParameterLabel;
    }

    public String getFieldParameterLabelAlias() {
        return fieldParameterLabelAlias;
    }

    public void setFieldParameterLabelAlias(String fieldParameterLabelAlias) {
        this.fieldParameterLabelAlias = fieldParameterLabelAlias;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DEvent getEvent() {
        return event;
    }

    public void setEvent(DEvent event) {
        this.event = event;
    }

    public Integer getFieldParameterId() {
        return fieldParameterId;
    }

    public void setFieldParameterId(Integer fieldParameterId) {
        this.fieldParameterId = fieldParameterId;
    }

//    public RFieldParameter getFieldParameter() {
//        return fieldParameter;
//    }
//
//    public void setFieldParameter(RFieldParameter fieldParameter) {
//        this.fieldParameter = fieldParameter;
//    }

//    public SLocation getLocation() {
//        return location;
//    }
//
//    public void setLocation(SLocation location) {
//        this.location = location;
//    }

    public Long getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(Long measurementTime) {
        this.measurementTime = measurementTime;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
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

    public String getExtField1() {
        return extField1;
    }

    public void setExtField1(String extField1) {
        this.extField1 = extField1;
    }

    public String getExtField2() {
        return extField2;
    }

    public void setExtField2(String extField2) {
        this.extField2 = extField2;
    }

    public String getExtField3() {
        return extField3;
    }

    public void setExtField3(String extField3) {
        this.extField3 = extField3;
    }

    public String getExtField4() {
        return extField4;
    }

    public void setExtField4(String extField4) {
        this.extField4 = extField4;
    }

    public String getExtField5() {
        return extField5;
    }

    public void setExtField5(String extField5) {
        this.extField5 = extField5;
    }

    public String getExtField6() {
        return extField6;
    }

    public void setExtField6(String extField6) {
        this.extField6 = extField6;
    }

    public String getExtField7() {
        return extField7;
    }

    public void setExtField7(String extField7) {
        this.extField7 = extField7;
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

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public Integer getFieldDataId() {
        return this.fieldDataId;
    }

    public void setFieldDataId(Integer id) {
        this.fieldDataId = id;
    }

    public Integer getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getParentSetId() {
        return this.parentSetId;
    }

    public void setParentSetId(Integer parentSetId) {
        this.parentSetId = parentSetId;
    }

    public Double getCorrectedLatitude() {
        return this.correctedLatitude;
    }

    public void setCorrectedLatitude(Double correctedLatitude) {
        this.correctedLatitude = correctedLatitude;
    }

    public Double getCorrectedLongitude() {
        return this.correctedLongitude;
    }

    public void setCorrectedLongitude(Double correctedLongitude) {
        this.correctedLongitude = correctedLongitude;
    }
}
