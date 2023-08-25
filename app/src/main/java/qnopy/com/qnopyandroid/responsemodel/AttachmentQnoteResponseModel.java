package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.ConstructionMediaDataModel;

public class AttachmentQnoteResponseModel extends DefaultResponse{

   List<ConstructionMediaDataModel> data;

    public List<ConstructionMediaDataModel> getData() {
        return data;
    }

    public void setData(List<ConstructionMediaDataModel> data) {
        this.data = data;
    }
}
