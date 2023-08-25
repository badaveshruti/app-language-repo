package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

/**
 * Created by Yogendra on 08-Jul-16.
 */
public class SubmittalResponse {
    public List<SubmittalModel> eventList;
    private Long lastSyncDate;

    public Long getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Long lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public List<SubmittalModel> getEventList() {
        return eventList;
    }

    public void setEventList(List<SubmittalModel> eventList) {
        this.eventList = eventList;
    }
}
