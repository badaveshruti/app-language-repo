package qnopy.com.qnopyandroid.util;

import android.content.Context;

import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SyncStatusDataSource;


/**
 * Created by Yogendra on 09-Oct-16.
 */

public class PublicFunctions {

    public static void ResetApp(Context context){
        FieldDataSource field = new FieldDataSource(context);
        AttachmentDataSource attach = new AttachmentDataSource(context);
        EventDataSource event = new EventDataSource(context);
        SampleMapTagDataSource smTag = new SampleMapTagDataSource(context);
        SyncStatusDataSource sd = new SyncStatusDataSource(context);
        FileFolderDataSource fd = new FileFolderDataSource(context);

        sd.truncateD_SyncStatus();
        fd.truncateFileFolder();
        smTag.truncate_SampleMapTag();
        field.deleteFieldData();
        attach.deleteAttachment();
        event.deleteEvents();
    }
}
