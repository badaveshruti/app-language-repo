package qnopy.com.qnopyandroid.responsemodel;


import qnopy.com.qnopyandroid.requestmodel.MetaSyncConstructionDataModel;

public class MetaSyncConstructionResponseModel extends DefaultResponse {

    private MetaSyncConstructionDataModel dataConstruction;

    public MetaSyncConstructionDataModel getDataConstruction() {
        return dataConstruction;
    }

    public void setDataConstruction(MetaSyncConstructionDataModel dataConstruction) {
        this.dataConstruction = dataConstruction;
    }
}
