package qnopy.com.qnopyandroid.responsemodel;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.requestmodel.WorkOrderTask;

/**
 * Created by QNOPY on 7/21/2017.
 */

public class TaskResponseModel extends DefaultResponse {
    private ArrayList<WorkOrderTask> data;

    public ArrayList<WorkOrderTask> getData() {
        return data;
    }

    public void setData(ArrayList<WorkOrderTask> data) {
        this.data = data;
    }
}
