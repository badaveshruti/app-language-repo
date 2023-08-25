package qnopy.com.qnopyandroid.clientmodel.metaForms;

import java.util.ArrayList;

public class FormsData {
    private Integer formId;//roll_into_app_id

    private String locationStatusQuery;

    private String appType;

    private String approvalRequired;

    private String jsonKey;

    private String name;

    private ArrayList<FormTabs> formTabs;//save to s_SiteMobileApp

    private String eventFrequency;

    private String status;

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getLocationStatusQuery() {
        return locationStatusQuery;
    }

    public void setLocationStatusQuery(String locationStatusQuery) {
        this.locationStatusQuery = locationStatusQuery;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(String approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public void setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<FormTabs> getFormTabs() {
        return formTabs;
    }

    public void setFormTabs(ArrayList<FormTabs> formTabs) {
        this.formTabs = formTabs;
    }

    public String getEventFrequency() {
        return eventFrequency;
    }

    public void setEventFrequency(String eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
