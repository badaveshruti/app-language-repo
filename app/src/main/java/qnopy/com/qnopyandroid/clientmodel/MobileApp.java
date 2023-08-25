package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class MobileApp implements Serializable {
    int MobileAppID;
    String MobileAppName;
    String app_description;
    int parent_app_id;
    int allow_multiple_sets;
    String app_type;
    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;

    private String extField6;

    private String extField7;

    private int headerFlag;
    //added for selection and deSelection in adapter
    private boolean isSelected;
    private String formQuery;

    private Integer tabOrderForReport;

    public Integer getTabOrderForReport() {
        return tabOrderForReport;
    }

    public void setTabOrderForReport(Integer tabOrderForReport) {
        this.tabOrderForReport = tabOrderForReport;
    }

    public String getFormQuery() {
        return formQuery;
    }

    public void setFormQuery(String formQuery) {
        this.formQuery = formQuery;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public int getHeaderFlag() {
        return headerFlag;
    }

    public void setHeaderFlag(int headerFlag) {
        this.headerFlag = headerFlag;
    }

    public int getAppID() {
        return MobileAppID;
    }

    public void setAppID(int id) {
        MobileAppID = id;
    }

    public String getAppName() {
        return MobileAppName;
    }

    public void setAppName(String name) {
        MobileAppName = name;
    }

    @Override
    public String toString() {
        return MobileAppName;
    }

    public String getAppDescription() {
        return app_description;
    }

    public void setAppDescription(String value) {
        app_description = value;
    }

    public int getParentAppId() {
        return parent_app_id;
    }

    public void setParentAppId(int value) {
        parent_app_id = value;
    }

    public int getAllowMultipleSets() {
        return allow_multiple_sets;
    }

    public void setAllowMultipleSets(int value) {
        allow_multiple_sets = value;
    }

    public String getAppType() {
        return app_type;
    }

    public void setAppType(String type) {
        app_type = type;
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

    public String getExtField6() {
        return this.extField6;
    }

    public void setExtField6(String extField6) {
        this.extField6 = extField6;
    }

    public String getExtField7() {
        return this.extField7;
    }

    public void setExtField7(String extField7) {
        this.extField7 = extField7;
    }

}



