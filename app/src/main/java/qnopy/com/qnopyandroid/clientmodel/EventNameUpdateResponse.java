package qnopy.com.qnopyandroid.clientmodel;

public class EventNameUpdateResponse {

    private Data data;

    private String success;

    private String message;

    private String responseCode;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public class Data {
        private String approvalStatus;

        private int eventId;

        private String notes;

        private double latitude;

        private String eventModificationDate;

        private String userGuid;

        private String deviceId;

        private String eventCreationDate;

        private int mobileAppId;

        private int createdBy;

        private int eventStatus;

        private int siteId;

        private String eventName;

        private String eventDate;

        private double longitude;

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public void setApprovalStatus(String approvalStatus) {
            this.approvalStatus = approvalStatus;
        }

        public int getEventId() {
            return eventId;
        }

        public void setEventId(int eventId) {
            this.eventId = eventId;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getEventModificationDate() {
            return eventModificationDate;
        }

        public void setEventModificationDate(String eventModificationDate) {
            this.eventModificationDate = eventModificationDate;
        }

        public String getUserGuid() {
            return userGuid;
        }

        public void setUserGuid(String userGuid) {
            this.userGuid = userGuid;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getEventCreationDate() {
            return eventCreationDate;
        }

        public void setEventCreationDate(String eventCreationDate) {
            this.eventCreationDate = eventCreationDate;
        }

        public int getMobileAppId() {
            return mobileAppId;
        }

        public void setMobileAppId(int mobileAppId) {
            this.mobileAppId = mobileAppId;
        }

        public int getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(int createdBy) {
            this.createdBy = createdBy;
        }

        public int getEventStatus() {
            return eventStatus;
        }

        public void setEventStatus(int eventStatus) {
            this.eventStatus = eventStatus;
        }

        public int getSiteId() {
            return siteId;
        }

        public void setSiteId(int siteId) {
            this.siteId = siteId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
