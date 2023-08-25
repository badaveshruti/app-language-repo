package qnopy.com.qnopyandroid.clientmodel;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;

public class DataSyncRequest {

    private int eventId;
    private ArrayList<FieldDataSyncStaging> data;

    public DataSyncRequest(int eventId, ArrayList<FieldDataSyncStaging> data) {
        this.eventId = eventId;
        this.data = data;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public ArrayList<FieldDataSyncStaging> getData() {
        return data;
    }

    public void setData(ArrayList<FieldDataSyncStaging> data) {
        this.data = data;
    }
}
