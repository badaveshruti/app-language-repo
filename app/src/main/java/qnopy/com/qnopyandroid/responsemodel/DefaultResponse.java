package qnopy.com.qnopyandroid.responsemodel;

import org.springframework.http.HttpStatus;

/**
 * Created by Yogendra on 04-Mar-16.
 */
public class DefaultResponse {
    private boolean success;
    private HttpStatus responseCode;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public HttpStatus getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HttpStatus responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
