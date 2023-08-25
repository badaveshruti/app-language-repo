package qnopy.com.qnopyandroid.requestmodel;

/**
 * Created by shantanu on 11/7/16.
 */

public class AddSite {

    private Integer siteId;
    private String siteName;
    private Double latitude;
    private Double longitude;
    private Integer parentSiteId;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getParentSiteId() {
        return parentSiteId;
    }

    public void setParentSiteId(Integer parentSiteId) {
        this.parentSiteId = parentSiteId;
    }
}
