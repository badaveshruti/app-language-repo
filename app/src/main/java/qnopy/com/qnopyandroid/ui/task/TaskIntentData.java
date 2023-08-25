package qnopy.com.qnopyandroid.ui.task;

import java.io.Serializable;

public class TaskIntentData implements Serializable {
    private String projectId;
    private int fieldParamId;
    private String locationId;
    private int mobileAppId;
    private int setId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getFieldParamId() {
        return fieldParamId;
    }

    public void setFieldParamId(int fieldParamId) {
        this.fieldParamId = fieldParamId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public int getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(int mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }
}
