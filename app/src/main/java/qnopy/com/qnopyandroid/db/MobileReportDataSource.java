package qnopy.com.qnopyandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Yogendra on 05-Oct-17.
 */

public class MobileReportDataSource {
    Context context;
    String TAG = "MobileReportDSource";
    public SQLiteDatabase database;

    public MobileReportDataSource(Context context) {

        this.context = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }

    }


}
