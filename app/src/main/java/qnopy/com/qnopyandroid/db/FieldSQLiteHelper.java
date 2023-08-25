package qnopy.com.qnopyandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.util.Util;

public class FieldSQLiteHelper extends SQLiteOpenHelper {

//	private static String DB_PATH = "/data/data/com.aqua.fieldbuddy/databases/";
    // private static final String DATABASE_NAME = "aquafina";

    private static final int DATABASE_VERSION = 1;
    private Context ObjContext;
    private static final String TAG = "FieldSQLiteHelper";

    // Database creation sql statement
    // private static final String DATABASE_CREATE = "create table "
    // + TABLE_COMMENTS + "(" + COLUMN_ID
    // + " integer primary key autoincrement, " + COLUMN_COMMENT
    // + " text not null);";

    public FieldSQLiteHelper(Context context) {

        super(context, GlobalStrings.DATABASE_NAME, null, DATABASE_VERSION);

//		uncomment the following to enable db from externall storage
//		super(context, Environment.getExternalStoragePublicDirectory(
//				Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"//"+GlobalStrings.DATABASE_NAME, null, DATABASE_VERSION);
        ObjContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // System.out.println(/* FieldSQLiteHelper.class.getName(), */
        // "Upgrading database from version " + oldVersion + " to " + newVersion
        // + ", which will destroy all old data");
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        // onCreate(db);
    }

    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        String path = ObjContext.getFilesDir().getPath();
        InputStream myInput = ObjContext.getAssets().open(GlobalStrings.DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = Util.getBaseContextPath(ObjContext) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = Util.getBaseContextPath(ObjContext) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;
            File dbfile = new File(myPath);

            if (dbfile.exists()) {
                Log.i(TAG, "DataBase exist");
                checkDB = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READONLY);

                if (checkDB != null) {

                    checkDB.close();

                }
            } else {
                return false;

            }

        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in checkDataBase Existance");
            // database does't exist yet.
            return false;
        }


        return checkDB != null;
    }

    public void createDbExternal() {
        this.getReadableDatabase();
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }

    }

    public void CopyDbFromExternalStorage() {
        boolean ret = false;

        if (getExternalStorageState() == false) {
            System.out.println("mmm" + "MEDIA NOT MOUNTED");

            Toast toast = Toast.makeText(ObjContext, "MEDIA NOT MOUNTED",
                    Toast.LENGTH_LONG);
            toast.show();
        }

        // Open your external db as the input stream

        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        path += "/" + "aquablue/" + GlobalStrings.DATABASE_NAME;

        InputStream myInput = null;
        try {
            myInput = new FileInputStream(path);
            System.out.println("mmm" + "Opened " + path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("mmm" + "Could Not Open " + path);
            return;
        }

        // Path to the just created empty db
        String outFileName = Util.getBaseContextPath(ObjContext) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput;
        try {
            myOutput = new FileOutputStream(outFileName);

            myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
//				System.out.println("mmm" + "Copying db-wrote-" + length);
            }

            // Close the streams

            myOutput.flush();

            myOutput.close();

            myInput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    boolean getExternalStorageState() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }
}
