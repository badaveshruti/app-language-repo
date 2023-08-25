package qnopy.com.qnopyandroid.requestmodel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures;
import qnopy.com.qnopyandroid.clientmodel.Lov;
import qnopy.com.qnopyandroid.clientmodel.MetaDataAttributes;
import qnopy.com.qnopyandroid.clientmodel.SiteFormFields;
import qnopy.com.qnopyandroid.responsemodel.DefaultValueModel;
import qnopy.com.qnopyandroid.responsemodel.SLocationAttribute;

public class MetaSyncDataModel implements Serializable {

    private List<SSite> site;
    private List<SLocation> location;
    private List<SLocationAttribute> locationAttribute;

    private List<SMobileApp> mobileApp;

    private List<SSiteMobileApp> siteMobileApp;

    private List<SMetaData> metaData;

    private List<SLovItem> lovItem;
    private List<DefaultValueModel> defaultValues;
    private List<SSiteUserRole> siteUserRole;
    private List<Lov> lov;
    private List<MetaDataAttributes> metaDataAttributes;
    private List<SiteFormFields> siteFormFields;
    private List<LocationProfilePictures> locationProfilePictures;
    private ArrayList<FormSites> formSites;
    private Integer statusForGuid;

    private Long lastSyncDate;
    private boolean forceReset;

    public ArrayList<FormSites> getFormSites() {
        return formSites;
    }

    public void setFormSites(ArrayList<FormSites> formSites) {
        this.formSites = formSites;
    }

    public Long getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Long lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public boolean isForceReset() {
        return forceReset;
    }

    public void setForceReset(boolean forceReset) {
        this.forceReset = forceReset;
    }

    public List<SiteFormFields> getSiteFormFields() {
        return siteFormFields;
    }

    public void setSiteFormFields(List<SiteFormFields> siteFormFields) {
        this.siteFormFields = siteFormFields;
    }

    public List<LocationProfilePictures> getLocationProfilePictures() {
        return locationProfilePictures;
    }

    public void setLocationProfilePictures(List<LocationProfilePictures> locationProfilePictures) {
        this.locationProfilePictures = locationProfilePictures;
    }

    public List<Lov> getLov() {
        return lov;
    }

    public void setLov(List<Lov> lov) {
        this.lov = lov;
    }

    public List<MetaDataAttributes> getMetaDataAttributes() {
        return metaDataAttributes;
    }

    public void setMetaDataAttributes(List<MetaDataAttributes> metaDataAttributes) {
        this.metaDataAttributes = metaDataAttributes;
    }

    public List<Lov> getLovList() {
        return lov;
    }

    public void setLovList(List<Lov> lovList) {
        this.lov = lovList;
    }

    public List<SSiteUserRole> getSiteUserRole() {
        return siteUserRole;
    }

    public List<SLocationAttribute> getLocationAttribute() {
        return locationAttribute;
    }

    public void setLocationAttribute(List<SLocationAttribute> locationAttribute) {
        this.locationAttribute = locationAttribute;
    }

    public void setSiteUserRole(List<SSiteUserRole> siteUserRole) {
        this.siteUserRole = siteUserRole;
    }

    public List<DefaultValueModel> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(List<DefaultValueModel> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public Integer getStatusForGuid() {
        return statusForGuid;
    }

    public void setStatusForGuid(Integer statusForGuid) {
        this.statusForGuid = statusForGuid;
    }

    public List<SSite> getSite() {
        return site;
    }

    public void setSite(List<SSite> site) {
        this.site = site;
    }

    public List<SLocation> getLocation() {
        return location;
    }

    public void setLocation(List<SLocation> location) {
        this.location = location;
    }

    public List<SMobileApp> getMobileApp() {
        return mobileApp;
    }

    public void setMobileApp(List<SMobileApp> mobileApp) {
        this.mobileApp = mobileApp;
    }

    public List<SSiteMobileApp> getSiteMobileApp() {
        return siteMobileApp;
    }

    public void setSiteMobileApp(List<SSiteMobileApp> siteMobileApp) {
        this.siteMobileApp = siteMobileApp;
    }

    public List<SMetaData> getMetaData() {
        return metaData;
    }

    public void setMetaData(List<SMetaData> metaData) {
        this.metaData = metaData;
    }

    public List<SLovItem> getLovItem() {
        return lovItem;
    }

    public void setLovItem(List<SLovItem> lovItem) {
        this.lovItem = lovItem;
    }

    public static class FormSites {
        private Integer formSiteId;
        private Integer siteId;
        private Integer formId;
        private String status;
        private boolean isInsert;
        private String formName;

        public FormSites() {
            super();
        }

        public Integer getFormSiteId() {
            return formSiteId;
        }

        public void setFormSiteId(Integer formSiteId) {
            this.formSiteId = formSiteId;
        }

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public Integer getFormId() {
            return formId;
        }

        public void setFormId(Integer formId) {
            this.formId = formId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isInsert() {
            return isInsert;
        }

        public void setInsert(boolean insert) {
            isInsert = insert;
        }

        public String getFormName() {
            return formName;
        }

        public void setFormName(String formName) {
            this.formName = formName;
        }
    }
}
