package qnopy.com.qnopyandroid.requestmodel;

public class SLocation {

    private int locationId;
//
//    private Set<DAttachment> attachments;
//
//    private Set<DFieldData> fieldDatas;
//
//    private Set<SLocationType> locationTypes;
//
//    private Set<SMetaData> metaDatas;

    private int siteId;
    private String locationName;
    private String locInstruction;
    private String locFormHeader;
    private String status; //03-Oct-16

    private Double latitude;

    private Double longitude;

    private Float toc;

//    private String aoc;
//
//    private String aquiferZone;
//
//    private Long installDate;
//
//    private String casingType;

    private Float startScreen;
    private Float endScreen;
    private Float dtb;
    private Float wellDiameter;
    private String locationType;

    private Long modifiedDate;
    private Long creationDate;
    private boolean insert;
    private Integer formDefault;
    private String locationTabs;

//
//    private String extField1;
//
//    private String extField2;
//
//    private String extField3;
//
//    private String extField4;
//
//    private String extField5;
//
//    private String extField6;
//
//    private String extField7;
//
//    private String notes;
//
//    private Long creationDate;
//
//    private Long modifiedDate;
//
//    private Integer createdBy;


    public String getLocationTabs() {
        return locationTabs;
    }

    public void setLocationTabs(String locationTabs) {
        this.locationTabs = locationTabs;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public Integer getFormDefault() {
        return formDefault;
    }

    public void setFormDefault(Integer formDefault) {
        this.formDefault = formDefault;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocInstruction() {
        return locInstruction;
    }

    public void setLocInstruction(String locInstruction) {
        this.locInstruction = locInstruction;
    }

    public String getLocFormHeader() {
        return locFormHeader;
    }

    public void setLocFormHeader(String locFormHeader) {
        this.locFormHeader = locFormHeader;
    }

    public Float getWellDiameter() {
        return wellDiameter;
    }

    public void setWellDiameter(Float wellDiameter) {
        this.wellDiameter = wellDiameter;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }


//    public Set<DAttachment> getAttachments() {
//        if (null == attachments) {
//            attachments = new HashSet<DAttachment>();
//        }
//        return attachments;
//    }

//    public void setAttachments(Set<DAttachment> attachments) {
//        this.attachments = attachments;
//    }

//    public Set<DFieldData> getFieldDatas() {
//        if (null == fieldDatas) {
//            fieldDatas = new HashSet<DFieldData>();
//        }
//        return fieldDatas;
//    }

//    public void setFieldDatas(Set<DFieldData> fieldDatas) {
//        this.fieldDatas = fieldDatas;
//    }

//    public Set<SLocationType> getLocationTypes() {
//        if (null == locationTypes) {
//            locationTypes = new HashSet<SLocationType>();
//        }
//        return locationTypes;
//    }

//    public void setLocationTypes(Set<SLocationType> locationTypes) {
//        this.locationTypes = locationTypes;
//    }

//    public Set<SMetaData> getMetaDatas() {
//        if (null == metaDatas) {
//            metaDatas = new HashSet<SMetaData>();
//        }
//        return metaDatas;
//    }

//    public void setMetaDatas(Set<SMetaData> metaDatas) {
//        this.metaDatas = metaDatas;
//    }

    public String getLocation() {
        return locationName;
    }

    public void setLocation(String location) {
        this.locationName = location;
    }

//    public String getLocationAlias() {
//        return locationAlias;
//    }
//
//    public void setLocationAlias(String locationAlias) {
//        this.locationAlias = locationAlias;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

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

    public Float getToc() {
        return toc;
    }

    public void setToc(Float toc) {
        this.toc = toc;
    }

//    public String getAoc() {
//        return aoc;
//    }
//
//    public void setAoc(String aoc) {
//        this.aoc = aoc;
//    }
//
//    public String getAquiferZone() {
//        return aquiferZone;
//    }
//
//    public void setAquiferZone(String aquiferZone) {
//        this.aquiferZone = aquiferZone;
//    }
//
//    public Long getInstallDate() {
//        return installDate;
//    }
//
//    public void setInstallDate(Long installDate) {
//        this.installDate = installDate;
//    }
//
//    public String getCasingType() {
//        return casingType;
//    }
//
//    public void setCasingType(String casingType) {
//        this.casingType = casingType;
//    }

    public Float getStartScreen() {
        return startScreen;
    }

    public void setStartScreen(Float startScreen) {
        this.startScreen = startScreen;
    }

    public Float getEndScreen() {
        return endScreen;
    }

    public void setEndScreen(Float endScreen) {
        this.endScreen = endScreen;
    }

    public Float getDtb() {
        return dtb;
    }

    public void setDtb(Float dtb) {
        this.dtb = dtb;
    }

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
//    public String getExtField4() {
//        return extField4;
//    }
//
//    public void setExtField4(String extField4) {
//        this.extField4 = extField4;
//    }
//
//    public String getExtField5() {
//        return extField5;
//    }
//
//    public void setExtField5(String extField5) {
//        this.extField5 = extField5;
//    }
//
//    public String getExtField6() {
//        return extField6;
//    }
//
//    public void setExtField6(String extField6) {
//        this.extField6 = extField6;
//    }
//
//    public String getExtField7() {
//        return extField7;
//    }
//
//    public void setExtField7(String extField7) {
//        this.extField7 = extField7;
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

    public int getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Integer id) {
        this.locationId = id;
    }

}
