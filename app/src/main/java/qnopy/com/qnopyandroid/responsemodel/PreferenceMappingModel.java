package qnopy.com.qnopyandroid.responsemodel;

/**
 * Created by Yogendra on 17-Jun-17.
 */

public class PreferenceMappingModel {
   private int featureId, status;
   private String featureName, key;

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
