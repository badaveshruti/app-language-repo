package qnopy.com.qnopyandroid.responsemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yogendra on 20-Feb-16.
 */
public class NewLocationResponseModel extends DefaultResponse implements Serializable {

    public List<NewClientLocation> data;


    public List<NewClientLocation> getData() {
        return data;
    }

    public void setData(List<NewClientLocation> data) {
        this.data = data;
    }
}
