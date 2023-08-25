package qnopy.com.qnopyandroid.responsemodel;

import qnopy.com.qnopyandroid.requestmodel.SUser;

/**
 * Created by QNOPY on 12/2/2017.
 */

public class RegistrationResponseModel extends DefaultResponse {

    private SUser data;

    public SUser getData() {
        return data;
    }

    public void setData(SUser data) {
        this.data = data;
    }
}
