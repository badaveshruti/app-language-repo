package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SLovItem;

/**
 * Created by QNOPY on 5/7/2018.
 */

public class newLovResponseModel extends DefaultResponse {
    public List<newLovData> data;

    public List<newLovData> getData() {
        return data;
    }

    public void setData(List<newLovData> data) {
        this.data = data;
    }
}
