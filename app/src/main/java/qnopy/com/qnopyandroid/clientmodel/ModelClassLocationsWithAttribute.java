package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class ModelClassLocationsWithAttribute implements Serializable {

    String mLocationId;
    String mAttributeName;
    String mAttributeValue;

    public String getmLocationId() {
        return mLocationId;
    }

    public void setmLocationId(String mLocationId) {
        this.mLocationId = mLocationId;
    }

    public String getmAttributeName() {
        return mAttributeName;
    }

    public void setmAttributeName(String mAttributeName) {
        this.mAttributeName = mAttributeName;
    }

    public String getmAttributeValue() {
        return mAttributeValue;
    }

    public void setmAttributeValue(String mAttributeValue) {
        this.mAttributeValue = mAttributeValue;
    }
}