
package qnopy.com.qnopyandroid.push_notifications;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class NotificationRequestModel {

    @SerializedName("data")
    private NotificationData mData;
    @SerializedName("to")
    private String mTo;

    public NotificationData getData() {
        return mData;
    }

    public void setData(NotificationData data) {
        mData = data;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

}
