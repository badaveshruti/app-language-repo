package qnopy.com.qnopyandroid.clientmodel;

public class FetchUserDataRequest {

    private long lastSync;
    private String password;
    private String userGuid;

    public FetchUserDataRequest(String userGuid) {
        this.lastSync = 0;
        this.password = "string";
        this.userGuid = userGuid;
    }

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }
}
