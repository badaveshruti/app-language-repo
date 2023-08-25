package qnopy.com.qnopyandroid.requestmodel;

/**
 * Created by Yogendra on 29-Feb-16.
 */
public class downloadFileModel {


    String userGuid;
    String fileGuid;
    String siteId;
    String fileName;

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public String getFileGuid() {
        return fileGuid;
    }

    public void setFileGuid(String fileGuid) {
        this.fileGuid = fileGuid;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
