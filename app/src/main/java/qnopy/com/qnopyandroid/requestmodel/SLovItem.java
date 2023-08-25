package qnopy.com.qnopyandroid.requestmodel;


public class SLovItem {

    private Integer lovId;

    // TODO: 29-Nov-16
    private Integer lovItemId;
    //
    private String itemDisplayName;
    //
    private String itemValue;
    private String parentLovItemId;
    private String formId;

    private Integer siteId;
    private Integer companyId;

    private Long modifiedDate;
    private boolean insert;
    private Long creationDate;
    private String status;

    public Integer getLovItemId() {
        return lovItemId;
    }

    public void setLovItemId(Integer lovItemId) {
        this.lovItemId = lovItemId;
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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getL_itemId() {
        return lovItemId;
    }

    public void setL_itemId(Integer l_itemId) {
        this.lovItemId = l_itemId;
    }

    public String getParentLovItemId() {
        return parentLovItemId;
    }

    public void setParentLovItemId(String parentLovItemId) {
        this.parentLovItemId = parentLovItemId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getLovId() {
        return this.lovId;
    }

    public void setLovId(Integer lovId) {
        this.lovId = lovId;
    }

    public String getItemDisplayName() {
        return this.itemDisplayName;
    }

    public void setItemDisplayName(String itemDisplayName) {
        this.itemDisplayName = itemDisplayName;
    }

    public String getItemValue() {
        return this.itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }


}
