package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SUser;

/**
 * Created by Yogendra on 13-Jul-16.
 */
public class LoginResponseModelV4 extends DefaultResponse {
    private List<SUser> data;

    public List<SUser> getData() {
        return data;
    }

    public void setData(List<SUser> data) {
        this.data = data;
    }
}
