package qnopy.com.qnopyandroid.clientmodel;

public class DataSyncStatus {
    private String eventId;
    private String siteId;
    private long lastSyncDate;

    public DataSyncStatus(String eventId, String siteId, long lastSyncDate) {
        this.eventId = eventId;
        this.siteId = siteId;
        this.lastSyncDate = lastSyncDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public long getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(long lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }
}
