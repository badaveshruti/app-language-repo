package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import qnopy.com.qnopyandroid.clientmodel.CopiedTemplate;

public class CopiedFormTemplatesDataSource {
    final String KEY_FILE_NAME = "fileName";
    final String KEY_CREATION_DATE = "creation_date";
    final String KEY_COPIED_TEMPLATE = "copiedTemplate";

    private Context mContext;
    private SQLiteDatabase database;
    private static final String TAG = "AttachmentDS";

    public CopiedFormTemplatesDataSource(Context context) {
        Log.i(TAG, "LocationProfilePictureDataSource() IN time:" + System.currentTimeMillis());
        mContext = context;

        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
        Log.i(TAG, "CopiedFormTemplatesDataSource() OUT time:" + System.currentTimeMillis());
    }

    public boolean insertCopyFormTemplate(CopiedTemplate copiedTemplate) {

        int ret = 0;
        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_FILE_NAME, copiedTemplate.getFileName());
            values.put(KEY_CREATION_DATE, System.currentTimeMillis());
            values.put(KEY_COPIED_TEMPLATE, copiedTemplate.getCopiedForm());

            try {
                ret = (int) database.insert(DbAccess.TABLE_COPIED_FORM_TEMPLATE,
                        null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in insertCopyForms");
        } finally {
            database.endTransaction();
        }
        return ret > 0;
    }

    public ArrayList<CopiedTemplate> getAllCopiedForms() {
        ArrayList<CopiedTemplate> filePathList = new ArrayList<>();
        Cursor cursor = null;
        String query = "select fileName, creation_date, copiedTemplate from " +
                DbAccess.TABLE_COPIED_FORM_TEMPLATE;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CopiedTemplate copyForm = new CopiedTemplate();
                    copyForm.setFileName(cursor.getString(0));
                    copyForm.setCreationDate(cursor.getLong(1));
                    copyForm.setCopiedForm(cursor.getString(2));
                    filePathList.add(copyForm);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        if (filePathList.size() > 0)
            Collections.sort(filePathList, (lhs, rhs)
                    -> Long.compare(rhs.getCreationDate(), lhs.getCreationDate()));

        return filePathList;
    }
}
