package qnopy.com.qnopyandroid.clientmodel;

import android.util.Log;

public class RollAppToSite {
    String TAG = "RollAppToSite";

    private String RollAppDisplayName;
    private int rollAppId;
    private boolean isChecked = false;

    public RollAppToSite(String rollAppDisplayName, int rollAppId, boolean isChecked) {
        RollAppDisplayName = rollAppDisplayName;
        this.rollAppId = rollAppId;
        this.isChecked = isChecked;
    }

    public void setRollAppDisplayName(String rollAppDisplayName) {
        RollAppDisplayName = rollAppDisplayName;
    }

    public void setRollAppId(int rollAppId) {
        this.rollAppId = rollAppId;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getRollAppDisplayName() {
        return RollAppDisplayName;
    }

    public int getRollAppId() {
        return rollAppId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public String toString() {
        Log.i(TAG, "RollAppToSite DisplayName=" + RollAppDisplayName + ",RollAppId=" + rollAppId);
        return super.toString();
    }
}
