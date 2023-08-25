package qnopy.com.qnopyandroid.requestmodel;

/**
 * Created by QNOPY on 3/16/2018.
 */

public class SCocDetails {

    private Long locationId;
    private String sampleId;
    private String sampleDate;
    private String sampleTime;
    private String preservatives;
    private String container;
    private String method;
    private Integer methodId;
    private String cocDetailsId;
    private Integer deleteFlag;
    private Integer woTaskIds;
    private Long creationDate;
    private Long modificationDate;
    private Integer createdBy;
    private Integer modifiedBy;
    private Long serverCreationDate;
    private Long serverModificationDate;
    private String status;
    private Integer cocFlag;
    private Integer cocId;

    //06-05-2021 Added below three for use in printing labels
    private String analysis;
    private String locationName;
    private String noOfContainer;

    //02-04-2018 SAMPLE FIELD-PARAMETER ID
    private Integer fieldParameterId;
    private Integer dupFlag;

    public String getNoOfContainer() {
        return noOfContainer;
    }

    public void setNoOfContainer(String noOfContainer) {
        this.noOfContainer = noOfContainer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getFieldParameterId() {
        return fieldParameterId;
    }

    public void setFieldParameterId(Integer fieldParameterId) {
        this.fieldParameterId = fieldParameterId;
    }

    public Integer getDupFlag() {
        return dupFlag;
    }

    public void setDupFlag(Integer dupFlag) {
        this.dupFlag = dupFlag;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    public String getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(String sampleTime) {
        this.sampleTime = sampleTime;
    }

    public String getPreservatives() {
        return preservatives;
    }

    public void setPreservatives(String preservatives) {
        this.preservatives = preservatives;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getCocDetailsId() {
        return cocDetailsId;
    }

    public void setCocDetailsId(String cocDetailsId) {
        this.cocDetailsId = cocDetailsId;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getWoTaskIds() {
        return woTaskIds;
    }

    public void setWoTaskIds(Integer woTaskIds) {
        this.woTaskIds = woTaskIds;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCocFlag() {
        return cocFlag;
    }

    public void setCocFlag(Integer cocFlag) {
        this.cocFlag = cocFlag;
    }

    public Integer getCocId() {
        return cocId;
    }

    public void setCocId(Integer cocId) {
        this.cocId = cocId;
    }
}
