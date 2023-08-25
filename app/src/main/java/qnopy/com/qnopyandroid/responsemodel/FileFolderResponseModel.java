package qnopy.com.qnopyandroid.responsemodel;

import qnopy.com.qnopyandroid.requestmodel.FileFolderData;

/**
 * Created by Yogendra on 29-Jan-16.
 */
public class FileFolderResponseModel extends DefaultResponse {

   private FileFolderData data;

    public FileFolderData getData() {
        return data;
    }

    public void setData(FileFolderData data) {
        this.data = data;
    }
}
