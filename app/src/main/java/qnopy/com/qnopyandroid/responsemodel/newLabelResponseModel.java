package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

/**
 * Created by shantanu on 7/30/16.
 */
public class newLabelResponseModel extends DefaultResponse {

    public List<newFormLabelResponse> data;


    public List<newFormLabelResponse> getData() {
        return data;
    }

    public void setData(List<newFormLabelResponse> data) {
        this.data = data;
    }
}
