package qnopy.com.qnopyandroid.responsemodel;


public class EventResponseModel extends DefaultResponse {

    private EventResponseData data;

    //added for rename check. personal use, not a part of response
    private boolean isRenameEvent;

    public boolean isRenameEvent() {
        return isRenameEvent;
    }

    public void setRenameEvent(boolean renameEvent) {
        isRenameEvent = renameEvent;
    }

    public EventResponseData getData() {
        return data;
    }

    public void setData(EventResponseData data) {
        this.data = data;
    }

}

