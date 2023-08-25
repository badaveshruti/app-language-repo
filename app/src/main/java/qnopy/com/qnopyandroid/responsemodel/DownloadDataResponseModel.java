package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.FieldDataForEventDownload;

/**
 * Created by shantanu on 6/26/17.
 */

public class DownloadDataResponseModel extends DefaultResponse {

    // private DownloadEventDataModel data;
    private List<FieldDataForEventDownload> data;

    public List<FieldDataForEventDownload> getData() {
        return data;
    }

    public void setData(List<FieldDataForEventDownload> data) {
        this.data = data;
    }
}
