package qnopy.com.qnopyandroid.responsemodel;

/**
 * Created by shantanu on 5/3/17.
 */

public class LocPercentageRespModel {

    private String  locationId;
    private String  siteId;
    private String  rollIntoAppId;
    private String percentage;

    public LocPercentageRespModel(String locationId, String siteId, String rollIntoAppId, String percentage) {
        this.locationId = locationId;
        this.siteId = siteId;
        this.rollIntoAppId = rollIntoAppId;
        this.percentage = percentage;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getRollIntoAppId() {
        return rollIntoAppId;
    }

    public void setRollIntoAppId(String rollIntoAppId) {
        this.rollIntoAppId = rollIntoAppId;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
