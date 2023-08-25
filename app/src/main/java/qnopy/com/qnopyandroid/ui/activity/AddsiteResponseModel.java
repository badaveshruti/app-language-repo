package qnopy.com.qnopyandroid.ui.activity;

import qnopy.com.qnopyandroid.responsemodel.DefaultResponse;

/**
 * Created by shantanu on 11/7/16.
 */
public class AddsiteResponseModel extends DefaultResponse {

    private AddSiteDataModel data;


    public AddSiteDataModel getData() {
        return data;
    }

    public void setData(AddSiteDataModel data) {
        this.data = data;
    }
}
