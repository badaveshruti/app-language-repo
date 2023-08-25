package qnopy.com.qnopyandroid.requestmodel;

import java.util.List;

/**
 * Created by shantanu on 6/14/17.
 */

public class WorkOrderDataModel {

    private List<WorkOrderTask> workOrderTaskData;
    private List<TaskAttributes> taskAttributes;
    // private WorkOrderTask workOrderTasks;


    public List<WorkOrderTask> getWorkOrderTaskData() {
        return workOrderTaskData;
    }

    public void setWorkOrderTaskData(List<WorkOrderTask> workOrderTaskData) {
        this.workOrderTaskData = workOrderTaskData;
    }

    public List<TaskAttributes> getTaskAttributes() {
        return taskAttributes;
    }

    public void setTaskAttributes(List<TaskAttributes> taskAttributes) {
        this.taskAttributes = taskAttributes;
    }
}