package qnopy.com.qnopyandroid.clientmodel;

public class EventNameUpdateRequest {

    private int eventId;
    private String eventName;
    private String userGuid;

    //14/09/22 Sanket: For now below parameters are not used at backend but in case in future further details
    //needed to update then we can add below data also
    private String eventEndDate;

    private String notes;

    private String latitude;

    private String deviceId;

    private String userId;

    private String mobileAppId;

    private String eventStatus;

    private String siteId;

    private String eventDate;

    private String longitude;

    public EventNameUpdateRequest(int eventId, String eventName, String userGuid) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.userGuid = userGuid;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
