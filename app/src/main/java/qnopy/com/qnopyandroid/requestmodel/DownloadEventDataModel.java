package qnopy.com.qnopyandroid.requestmodel;

import java.util.List;

/**
 * Created by shantanu on 6/26/17.
 */

public class DownloadEventDataModel {
    private List<FieldDataForEventDownload> data;

    public List<FieldDataForEventDownload> getFielddata() {
        return data;
    }

    public void setFielddata(List<FieldDataForEventDownload> fielddata) {
        this.data = fielddata;
    }
}
