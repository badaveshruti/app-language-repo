package qnopy.com.qnopyandroid.responsemodel;


import qnopy.com.qnopyandroid.requestmodel.SUser;

/**
 * Created by Yogendra on 04-Mar-16.
 */
public class ActivationResponseModel extends DefaultResponse {

    private SUser data;


    public SUser getData() {
        return data;
    }

    public void setData(SUser data) {
        this.data = data;
    }
}
