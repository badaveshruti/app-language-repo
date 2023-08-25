package qnopy.com.qnopyandroid.responsemodel;

public class CSVDataModel {
    private String SiteName;
    private String Username;
    private String MobileAppName;
    private String Location;
    private int EventId;
    private String Date;
    private String Time;
    private String SetId;
    private String ParameterLabel;
    private String StringValue;

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        this.SiteName = siteName;
    }

    public String getUserName() {
        return Username;
    }

    public void setUserName(String userName) {
        this.Username = userName;
    }

    public String getDisplayName() {
        return MobileAppName;
    }

    public void setDisplayName(String displayName) {
        this.MobileAppName = displayName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public int getEventId() {
        return EventId;
    }

    public void setEventId(int eventId) {
        this.EventId = eventId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getSetId() {
        return SetId;
    }

    public void setSetId(String setId) {
        this.SetId = setId;
    }

    public String getFieldParameterLabel() {
        return ParameterLabel;
    }

    public void setFieldParameterLabel(String fieldParameterLabel) {
        this.ParameterLabel = fieldParameterLabel;
    }

    public String getStringValue() {
        return StringValue;
    }

    public void setStringValue(String stringValue) {
        this.StringValue = stringValue;
    }
}
