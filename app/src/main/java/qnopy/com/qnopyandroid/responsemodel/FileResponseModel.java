package qnopy.com.qnopyandroid.responsemodel;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Created by Yogendra on 29-Feb-16.
 */
public class FileResponseModel {

    private HttpStatus responseCode;
    private List<FileDataModel> data;


    public HttpStatus getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HttpStatus responseCode) {
        this.responseCode = responseCode;
    }

    public List<FileDataModel> getData() {
        return data;
    }

    public void setData(List<FileDataModel> data) {
        this.data = data;
    }


}
