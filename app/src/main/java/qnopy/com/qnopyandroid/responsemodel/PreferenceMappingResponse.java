package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

/**
 * Created by Yogendra on 17-Jun-17.
 */

public class PreferenceMappingResponse extends DefaultResponse  {

    private List<PreferenceMappingModel> data;

    public List<PreferenceMappingModel> getData() {
        return data;
    }

    public void setData(List<PreferenceMappingModel> data) {
        this.data = data;
    }
}
