package qnopy.com.qnopyandroid.requestmodel;

import java.io.Serializable;
import java.util.List;

public class MetaSyncConstructionDataModel implements Serializable {

    private List<SSite> siteConstruction;
    private List<SSiteUserRole> siteUserRoleConstruction;

    public List<SSite> getSiteConstruction() {
        return siteConstruction;
    }

    public void setSiteConstruction(List<SSite> siteConstruction) {
        this.siteConstruction = siteConstruction;
    }

    public List<SSiteUserRole> getSiteUserRoleConstruction() {
        return siteUserRoleConstruction;
    }

    public void setSiteUserRoleConstruction(List<SSiteUserRole> siteUserRoleConstruction) {
        this.siteUserRoleConstruction = siteUserRoleConstruction;
    }
}
