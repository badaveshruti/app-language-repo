package qnopy.com.qnopyandroid.requestmodel;

import java.util.List;

import qnopy.com.qnopyandroid.responsemodel.FileDataModel;
import qnopy.com.qnopyandroid.responsemodel.FolderDataModel;

/**
 * Created by Yogendra on 29-Mar-16.
 */
public class FileFolderData {

    private List<FolderDataModel> folderData;
    private List<FileDataModel> fileData;

    public List<FileDataModel> getFileData() {
        return fileData;
    }

    public void setFileData(List<FileDataModel> fileData) {
        this.fileData = fileData;
    }

    public List<FolderDataModel> getFolderData() {
        return folderData;
    }

    public void setFolderData(List<FolderDataModel> folderData) {
        this.folderData = folderData;
    }
}
