package qnopy.com.qnopyandroid.responsemodel;

import qnopy.com.qnopyandroid.requestmodel.MetaSyncDataModel;

/**
 * Created by Yogendra on 03-Mar-16.
 */
public class MetaSyncResponseModel extends DefaultResponse {

    private MetaSyncDataModel data;

    public MetaSyncDataModel getData() {
        return data;
    }

    public void setData(MetaSyncDataModel data) {
        this.data = data;
    }
}
