package qnopy.com.qnopyandroid.clientmodel.metaForms;

import java.util.ArrayList;

public class FormTabs {
    private Integer tabId;

    private Integer headerFlag;

    private String tabName;

    private String appType;

    private Double tabOrder;

    private String display;

    private String appDescription;

    private boolean allowMultipleSets;

    private ArrayList<Fields> fields;//save to s_MetaData

    public Integer getTabId() {
        return tabId;
    }

    public void setTabId(Integer tabId) {
        this.tabId = tabId;
    }

    public Integer getHeaderFlag() {
        return headerFlag;
    }

    public void setHeaderFlag(Integer headerFlag) {
        this.headerFlag = headerFlag;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Double getTabOrder() {
        return tabOrder;
    }

    public void setTabOrder(Double tabOrder) {
        this.tabOrder = tabOrder;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public boolean isAllowMultipleSets() {
        return allowMultipleSets;
    }

    public void setAllowMultipleSets(boolean allowMultipleSets) {
        this.allowMultipleSets = allowMultipleSets;
    }

    public ArrayList<Fields> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Fields> fields) {
        this.fields = fields;
    }
}
