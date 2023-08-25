package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.WorkOrderDataModel;
import qnopy.com.qnopyandroid.requestmodel.WorkOrderTask;

/**
 * Created by shantanu on 6/14/17.
 */

public class WorkOrderResponseModel extends DefaultResponse {

    private WorkOrderDataModel data;

    public WorkOrderDataModel getData() {
        return data;
    }

    public void setData(WorkOrderDataModel data) {
        this.data = data;
    }
}

