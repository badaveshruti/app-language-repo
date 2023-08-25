package qnopy.com.qnopyandroid.requestmodel;

import java.util.LinkedList;
import java.util.List;

public class SLov {

    private Integer lovId;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lov", cascade = CascadeType.ALL)
//    private List<SMetaData> metaDatas;

    //    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SLovItem> lovItems;

    private String lovName;

    private String lovDescription;

    private Long companyId;

    private Long siteId;

    private Integer createdBy;

    private Long creationDate;

    private String notes;

    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;


    public Integer getLovId() {
        return this.lovId;
    }

    public void setLovId(Integer lovId) {
        this.lovId = lovId;
    }

    public String getLovName() {
        return this.lovName;
    }

    public void setLovName(String lovName) {
        this.lovName = lovName;
    }

    public String getLovDescription() {
        return this.lovDescription;
    }

    public void setLovDescription(String lovDescription) {
        this.lovDescription = lovDescription;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getExtField1() {
        return this.extField1;
    }

    public void setExtField1(String extField1) {
        this.extField1 = extField1;
    }

    public String getExtField2() {
        return this.extField2;
    }

    public void setExtField2(String extField2) {
        this.extField2 = extField2;
    }

    public String getExtField3() {
        return this.extField3;
    }

    public void setExtField3(String extField3) {
        this.extField3 = extField3;
    }

    public String getExtField4() {
        return this.extField4;
    }

    public void setExtField4(String extField4) {
        this.extField4 = extField4;
    }

    public String getExtField5() {
        return this.extField5;
    }

    public void setExtField5(String extField5) {
        this.extField5 = extField5;
    }

//	public List<SMetaData> getMetaDatas() {
//        return this.metaDatas;
//    }
//
//	public void setMetaDatas(List<SMetaData> metaDatas) {
//        this.metaDatas = metaDatas;
//    }

    public List<SLovItem> getLovItems() {
        if (null == lovItems) {
            this.lovItems = new LinkedList<SLovItem>();
        }
        return this.lovItems;
    }

    public void setLovItems(List<SLovItem> lovItems) {
        this.lovItems = lovItems;
    }
}
