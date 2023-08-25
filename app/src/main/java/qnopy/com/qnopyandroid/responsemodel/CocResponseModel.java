package qnopy.com.qnopyandroid.responsemodel;


import qnopy.com.qnopyandroid.requestmodel.CocDataModel;


public class CocResponseModel extends DefaultResponse {

    private CocDataModel data;

    public CocDataModel getData() {
        return data;
    }

    public void setData(CocDataModel data) {
        this.data = data;
    }
}
