package qnopy.com.qnopyandroid.clientmodel;

public class LogDetails {

    private String date;
    private String details;
    private String screenName;
    private String allIds;

    public String getAllIds() {
        return allIds;
    }

    public void setAllIds(String allIds) {
        this.allIds = allIds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
