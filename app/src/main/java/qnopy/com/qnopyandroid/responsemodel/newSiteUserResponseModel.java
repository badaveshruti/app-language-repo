package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole;

/**
 * Created by QNOPY on 5/7/2018.
 */

public class newSiteUserResponseModel extends DefaultResponse {

    public List<SSiteUserRole> data;

    public List<SSiteUserRole> getData() {
        return data;
    }

    public void setData(List<SSiteUserRole> data) {
        this.data = data;
    }
}
