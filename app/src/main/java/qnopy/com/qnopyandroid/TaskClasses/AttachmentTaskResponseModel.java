package qnopy.com.qnopyandroid.TaskClasses;

import qnopy.com.qnopyandroid.TaskModelClasses.TaskAttachments;
import qnopy.com.qnopyandroid.responsemodel.DefaultResponse;

public class AttachmentTaskResponseModel extends DefaultResponse {

    TaskAttachments data;

    public TaskAttachments getData() {
        return data;
    }

    public void setData(TaskAttachments data) {
        this.data = data;
    }

}
