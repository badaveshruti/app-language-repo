package qnopy.com.qnopyandroid.ui.login.model;

public class FetchUserGuidResponse {

    private Data data;
    private boolean success;
    private String error;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public class Data {
        private String userGuid;
        private String userKeycloakGuid;

        public String getUserGuid() {
            return userGuid;
        }

        public void setUserGuid(String userGuid) {
            this.userGuid = userGuid;
        }

        public String getUserKeycloakGuid() {
            return userKeycloakGuid;
        }

        public void setUserKeycloakGuid(String userKeycloakGuid) {
            this.userKeycloakGuid = userKeycloakGuid;
        }
    }
}
