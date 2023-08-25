package qnopy.com.qnopyandroid.clientmodel;

/**
 * Created by Yogendra on 03-May-17.
 */

public class PercentageModel {

    private String siteId;
    private String locationId;
    private String rollIntoAppId;

    private String start_date;
    private String end_date;
    private String percentage;

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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
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
