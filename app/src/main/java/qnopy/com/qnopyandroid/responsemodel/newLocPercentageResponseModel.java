package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;


/**
 * Created by shantanu on 5/3/17.
 */

public class newLocPercentageResponseModel extends DefaultResponse {

    public List<LocPercentageRespModel> data;


    public List<LocPercentageRespModel> getData() {
        return data;
    }

    public void setData(List<LocPercentageRespModel> data) {
        this.data = data;
    }
}
