package qnopy.com.qnopyandroid.responsemodel;

/**
 * Created by QNOPY on 5/7/2018.
 */

public class newLovData {


    private Integer lovItemId;

    private Integer lovId;

    private String itemDisplayName;

    private String itemValue;

    private String itemDescription;

    private Integer createdBy;

    private Long creationDate;

    private String notes;

    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;

    private Integer siteId;

    private Integer modifiedBy;

    private Long modificationDate;

    //newly added for add lov-item from mobile app
    private Integer companyId;

    private Long lovItemIdMobApp;

    private Boolean lovItemSyncStatus;

    public Boolean getLovItemSyncStatus() {
        return lovItemSyncStatus;
    }

    public void setLovItemSyncStatus(Boolean lovItemSyncStatus) {
        this.lovItemSyncStatus = lovItemSyncStatus;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Long getLovItemIdMobApp() {
        return lovItemIdMobApp;
    }

    public void setLovItemIdMobApp(Long lovItemIdMobApp) {
        this.lovItemIdMobApp = lovItemIdMobApp;
    }

    public Integer getLovItemId() {
        return lovItemId;
    }

    public void setLovItemId(Integer lovItemId) {
        this.lovItemId = lovItemId;
    }

    public Integer getLovId() {
        return lovId;
    }

    public void setLovId(Integer lovId) {
        this.lovId = lovId;
    }

    public String getItemDisplayName() {
        return itemDisplayName;
    }

    public void setItemDisplayName(String itemDisplayName) {
        this.itemDisplayName = itemDisplayName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getExtField5() {
        return extField5;
    }

    public void setExtField5(String extField5) {
        this.extField5 = extField5;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
