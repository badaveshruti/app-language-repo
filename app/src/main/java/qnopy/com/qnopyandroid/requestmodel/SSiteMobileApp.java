package qnopy.com.qnopyandroid.requestmodel;


import java.io.Serializable;

public class SSiteMobileApp implements Serializable {

    private Integer siteAppId;

    private Integer siteId;

    private Integer mobileAppId;

    private float app_order;

    private int roll_into_app_id;

    private String display_name;
    private String app_type;
    private boolean allow_multiple_sets;

    private String label_width;

    private String ext_field2;

    private String appIcon;

    private int company_id;

    private String display_name_roll_into_app;

    private int companyId;
    private String extField3;
    private String status;
    private Long modifiedDate;
    private Long creationDate;
    private boolean insert;
    private Integer headerFlag;
    private String formQuery;

    //for convenience 5 May, 22
    boolean getNewForm;
    String formPreview;
    String captionRequired;
    //end

    public String getCaptionRequired() {
        return captionRequired;
    }

    public void setCaptionRequired(String captionRequired) {
        this.captionRequired = captionRequired;
    }

    public String getFormPreview() {
        return formPreview;
    }

    public void setFormPreview(String formPreview) {
        this.formPreview = formPreview;
    }

    public boolean isGetNewForm() {
        return getNewForm;
    }

    public void setGetNewForm(boolean getNewForm) {
        this.getNewForm = getNewForm;
    }

    public SSiteMobileApp() {
        super();
    }

    public SSiteMobileApp(boolean isNewForm) {
        this.getNewForm = isNewForm;
    }

    public String getFormQuery() {
        return formQuery;
    }

    public void setFormQuery(String formQuery) {
        this.formQuery = formQuery;
    }

    public String getExt_field2() {
        return ext_field2;
    }

    public void setExt_field2(String ext_field2) {
        this.ext_field2 = ext_field2;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getExtField3() {
        return extField3;
    }

    public void setExtField3(String extField3) {
        this.extField3 = extField3;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getHeaderFlag() {
        return headerFlag;
    }

    public void setHeaderFlag(Integer headerFlag) {
        this.headerFlag = headerFlag;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getDisplay_name_roll_into_app() {
        return display_name_roll_into_app;
    }

    public void setDisplay_name_roll_into_app(String display_name_roll_into_app) {
        this.display_name_roll_into_app = display_name_roll_into_app;
    }

    public boolean isAllow_multiple_sets() {
        return allow_multiple_sets;
    }

    public void setAllow_multiple_sets(boolean allow_multiple_sets) {
        this.allow_multiple_sets = allow_multiple_sets;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public Integer getSiteAppId() {
        return siteAppId;
    }

    public void setSiteAppId(Integer siteAppId) {
        this.siteAppId = siteAppId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(Integer mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

//	public String toString() {
//        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
//    }

    public float getApp_order() {
        return app_order;
    }

    public void setApp_order(float app_order) {
        this.app_order = app_order;
    }

    public int getRoll_into_app_id() {
        return roll_into_app_id;
    }

    public void setRoll_into_app_id(int roll_into_app_id) {
        this.roll_into_app_id = roll_into_app_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

//    public byte getShow_last2() {
//        return show_last2;
//    }
//
//    public void setShow_last2(byte show_last2) {
//        this.show_last2 = show_last2;
//    }

    public String getLabel_width() {
        return label_width;
    }

    public void setLabel_width(String label_width) {
        this.label_width = label_width;
    }

    //    public String getExt_field1() {
//        return ext_field1;
//    }
//
//    public void setExt_field1(String ext_field1) {
//        this.ext_field1 = ext_field1;
//    }
//
//    public String getExt_field2() {
//        return ext_field2;
//    }
//
//    public void setExt_field2(String ext_field2) {
//        this.ext_field2 = ext_field2;
//    }
//
//    public String getExt_field3() {
//        return ext_field3;
//    }
//
//    public void setExt_field3(String ext_field3) {
//        this.ext_field3 = ext_field3;
//    }
//
//    public String getExt_field4() {
//        return ext_field4;
//    }
//
//    public void setExt_field4(String ext_field4) {
//        this.ext_field4 = ext_field4;
//    }
//
//    public String getExt_field5() {
//        return ext_field5;
//    }
//
//    public void setExt_field5(String ext_field5) {
//        this.ext_field5 = ext_field5;
//    }
//
//    public String getExt_field6() {
//        return ext_field6;
//    }
//
//    public void setExt_field6(String ext_field6) {
//        this.ext_field6 = ext_field6;
//    }
//
//    public String getExt_field7() {
//        return ext_field7;
//    }
//
//    public void setExt_field7(String ext_field7) {
//        this.ext_field7 = ext_field7;
//    }
//
//    public String getExt_field8() {
//        return ext_field8;
//    }
//
//    public void setExt_field8(String ext_field8) {
//        this.ext_field8 = ext_field8;
//    }
//
//    public String getExt_field9() {
//        return ext_field9;
//    }
//
//    public void setExt_field9(String ext_field9) {
//        this.ext_field9 = ext_field9;
//    }
//
//    public String getExt_field10() {
//        return ext_field10;
//    }
//
//    public void setExt_field10(String ext_field10) {
//        this.ext_field10 = ext_field10;
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
//    public long getCreation_date() {
//        return creation_date;
//    }
//
//    public void setCreation_date(long creation_date) {
//        this.creation_date = creation_date;
//    }
//
//    public int getCreated_by() {
//        return created_by;
//    }
//
//    public void setCreated_by(int created_by) {
//        this.created_by = created_by;
//    }
//
//    public long getModified_date() {
//        return modified_date;
//    }
//
//    public void setModified_date(long modified_date) {
//        this.modified_date = modified_date;
//    }
//
//    public int getModified_by() {
//        return modified_by;
//    }
//
//    public void setModified_by(int modified_by) {
//        this.modified_by = modified_by;
//    }
//
    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }
//
//    public int getLocation_id() {
//        return location_id;
//    }
//
//    public void setLocation_id(int location_id) {
//        this.location_id = location_id;
//    }
//
//    public int getParent_app_id() {
//        return parent_app_id;
//    }
//
//    public void setParent_app_id(int parent_app_id) {
//        this.parent_app_id = parent_app_id;
//    }


    @Override
    public String toString() {
        return getDisplay_name_roll_into_app();
    }
}