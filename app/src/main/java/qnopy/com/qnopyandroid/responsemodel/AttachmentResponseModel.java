package qnopy.com.qnopyandroid.responsemodel;

/**
 * Created by Yogendra on 04-Mar-16.
 */
public class AttachmentResponseModel extends DefaultResponse {

    UploadStatus data;

    public UploadStatus getData() {
        return data;
    }

    public void setData(UploadStatus data) {
        this.data = data;
    }
    //    UploadStatus[] data;
//
//    public UploadStatus[] getData() {
//        return data;
//    }
//
//    public void setData(UploadStatus[] data) {
//        this.data = data;
//    }
//    ArrayList<UploadStatus> data;
//
//    public ArrayList<UploadStatus> getData() {
//        return data;
//    }
//
//    public void setData(ArrayList<UploadStatus> data) {
//        this.data = data;
//    }
}
