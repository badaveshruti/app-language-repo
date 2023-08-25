package qnopy.com.qnopyandroid.clientmodel;

//get meta data for particular site and mobile app and location


import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SLovItem;

@SuppressWarnings("unused")
public class Lov {
    //	Integer SiteID;
//	Integer MobileAppID;
//	Integer LocationID;

//    Integer lovId;
//
//
//
//    Integer lov_id;
//    public String lov_name;
//    public String lov_description;
//    public Integer company_id;
//    public Integer site_id;
//    //private String	notes;
//    //public Integer 	created_by;
//
//    public String ext_field1;
//    public String ext_field2;
//    public String ext_field3;
//    public String ext_field4;
//    public String ext_field5;
//
//    String notes;
//    Long creation_date;
//    //Date 	ModifiedDate;
//    Integer created_by;
//
//    //HashMap<String, String> nameValueMap;
//
//    public Integer getLovID() {
//        return lov_id;
//    }
//
//    public void setLovID(Integer value) {
//        lov_id = value;
//    }
//
//    public String getLovName() {
//        return lov_name;
//    }
//
//    public void setLovName(String value) {
//        lov_name = value;
//    }
//
//    public String getLovDescription() {
//        return lov_description;
//    }
//
//    public void setLovDescription(String value) {
//        lov_description = value;
//    }
//
//    public Integer getCompanyID() {
//        return company_id;
//    }
//
//    public void setCompanyID(Integer value) {
//        company_id = value;
//    }
//
//    public Integer getSiteID() {
//        return site_id;
//    }
//
//    public void setSiteID(Integer value) {
//        site_id = value;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String value) {
//        notes = value;
//    }
//
//    public Integer getCreatedBy() {
//        return created_by;
//    }
//
//    public void setCreatedBy(Integer value) {
//        created_by = value;
//    }
//
//    public Long getCreationDate() {
//        return creation_date;
//    }
//
//    public void setCreationDate(Long value) {
//        creation_date = value;
//    }
//
//    public String getExtField1() {
//        return ext_field1;
//    }
//
//    public void setExtField1(String value) {
//        ext_field1 = value;
//    }
//
//    public String getExtField2() {
//        return ext_field2;
//    }
//
//    public void setExtField2(String value) {
//        ext_field2 = value;
//    }
//
//    public String getExtField3() {
//        return ext_field3;
//    }
//
//    public void setExtField3(String value) {
//        ext_field3 = value;
//    }
//
//    public String getExtField4() {
//        return ext_field4;
//    }
//
//    public void setExtField4(String value) {
//        ext_field4 = value;
//    }
//
//    public String getExtField5() {
//        return ext_field5;
//    }
//
//    public void setExtField5(String value) {
//        ext_field5 = value;
//    }

    private Integer lovId;

    // @OneToMany(fetch = FetchType.LAZY, mappedBy = "lov", cascade =
    // CascadeType.ALL)
    // private List<SMetaData> metaDatas;

    // @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)

    private List<SLovItem> lovItems;

    private String lovName;

    private String lovDescription;

    private Long companyId;

    private int siteId;

    private Integer createdBy;

    private Long creationDate;

    private String notes;

    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;

    private String modifiedDate;
    private boolean insert;
    private String status;

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

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

    public Integer getLovId() {
        return lovId;
    }

    public void setLovId(Integer lovId) {
        this.lovId = lovId;
    }

    public List<SLovItem> getLovItems() {
        return lovItems;
    }

    public void setLovItems(List<SLovItem> lovItems) {
        this.lovItems = lovItems;
    }

    public String getLovName() {
        return lovName;
    }

    public void setLovName(String lovName) {
        this.lovName = lovName;
    }

    public String getLovDescription() {
        return lovDescription;
    }

    public void setLovDescription(String lovDescription) {
        this.lovDescription = lovDescription;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
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

    public String getExtField5() {
        return extField5;
    }

    public void setExtField5(String extField5) {
        this.extField5 = extField5;
    }
}