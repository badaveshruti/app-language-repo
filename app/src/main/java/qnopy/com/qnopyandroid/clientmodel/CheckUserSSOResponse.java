package qnopy.com.qnopyandroid.clientmodel;

import com.google.gson.annotations.SerializedName;

public class CheckUserSSOResponse {
    private String companyId;

    private String realm;

    private RealmJson realmJson;

    private boolean sso;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public RealmJson getRealmJson() {
        return realmJson;
    }

    public void setRealmJson(RealmJson realmJson) {
        this.realmJson = realmJson;
    }

    public boolean isSso() {
        return sso;
    }

    public void setSso(boolean sso) {
        this.sso = sso;
    }

    public static class RealmJson {

        @SerializedName("public-client")
        private boolean publicClient;

        @SerializedName("confidential-port")
        private String confidentialPort;

        private String resource;

        private String realm;

        @SerializedName("auth-server-url")
        private String authServerUrl;

        @SerializedName("ssl-required")
        private String sslRequired;

        public boolean isPublicClient() {
            return publicClient;
        }

        public void setPublicClient(boolean publicClient) {
            this.publicClient = publicClient;
        }

        public String getConfidentialPort() {
            return confidentialPort;
        }

        public void setConfidentialPort(String confidentialPort) {
            this.confidentialPort = confidentialPort;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getRealm() {
            return realm;
        }

        public void setRealm(String realm) {
            this.realm = realm;
        }

        public String getAuthServerUrl() {
            return authServerUrl;
        }

        public void setAuthServerUrl(String authServerUrl) {
            this.authServerUrl = authServerUrl;
        }

        public String getSslRequired() {
            return sslRequired;
        }

        public void setSslRequired(String sslRequired) {
            this.sslRequired = sslRequired;
        }
    }
}
