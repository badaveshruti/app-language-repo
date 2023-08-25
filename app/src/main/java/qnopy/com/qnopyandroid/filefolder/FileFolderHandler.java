package qnopy.com.qnopyandroid.filefolder;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;

/**
 * Created by Yogendra on 20-Jan-16.
 */
public class FileFolderHandler {


    private static final String TAG = "FileFolderHandler";

    ArrayList<FileFolderItem> ob = new ArrayList<FileFolderItem>();

    public static boolean createDirectory(String path) {
        File fp = new File(path);
        if (!fp.exists()) {
            Log.i(TAG, "Directory for creation:" + path);
            return fp.mkdirs();
        }
        return true;
    }

//    public void insertFileFolderData(Context context,List<FolderDataModel> dataArray) {
//
//        new FileFolderDataSource(context).insertFolderList(dataArray);
//
//        for (FolderDataModel fd:dataArray){
//           List<FileDataModel> fileList=fd.getFileList();
//          new FileFolderDataSource(context).insertTempFileList(fileList);
//       }
//
//    }

    public ArrayList<FileFolderItem> getFileFolder(Context context) {
        ob = new ArrayList<FileFolderItem>();
        FileFolderItem item;
        for (int i = 0; i < 5; i++) {
            item = new FileFolderItem();
            item.setItemID(i + "");
            item.setItemTitle("Folder " + i);
            item.setItemType("folder");
            ob.add(item);
        }
        return ob;
    }


    public ArrayList<FileFolderItem> getFileFolder2(Context context) {
        ob = new ArrayList<FileFolderItem>();
        FileFolderItem item;
        for (int i = 5; i < 10; i++) {
            item = new FileFolderItem();
            if (i == 7) {
                item.setItemID("" + i);
                item.setItemTitle("File");
                item.setItemType("file");
            } else {
                item.setItemID(i + "");
                item.setItemTitle("Folder " + i);
                item.setItemType("folder");
            }

            ob.add(item);
        }


        return ob;
    }


}
