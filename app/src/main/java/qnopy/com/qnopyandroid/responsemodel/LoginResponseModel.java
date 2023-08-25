package qnopy.com.qnopyandroid.responsemodel;

import qnopy.com.qnopyandroid.requestmodel.SUser;

/**
 * Created by Yogendra on 03-Mar-16.
 */
public class LoginResponseModel extends DefaultResponse {

    private SUser data;


    public SUser getData() {
        return data;
    }

    public void setData(SUser data) {
        this.data = data;
    }
}
