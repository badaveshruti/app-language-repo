package qnopy.com.qnopyandroid.fetchdraw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "Image.db";
	public static final int DATABASE_VERSION = 1;
	public static final String IMAGE_TABLE_NAME = "images";
	public static final String IMAGE_BITMAP = "bitmap";
	public static final String IMAGE_STROKE = "stroke";

	public static final String CREATE_IMAGE_TABLE = "create table if not exists "+IMAGE_TABLE_NAME+" ("+IMAGE_BITMAP+" BLOB)";

	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_IMAGE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+IMAGE_TABLE_NAME);
		onCreate(db);
	}

	// Insert/Update Image in Sqlite Database Table
	
	public boolean updateTable(byte[] _bitmap) {

		SQLiteDatabase mDatabase = this.getWritableDatabase();
		long check = -1;
		Cursor cursor = null;
		Log.e("DBHandler","updateTable");
		try { 
			ContentValues values = new ContentValues();
			values.put(IMAGE_BITMAP, _bitmap);
			cursor = mDatabase.rawQuery("Select * from "+IMAGE_TABLE_NAME, null);
			if(cursor.getCount() > 0) {
				check = mDatabase.update(IMAGE_TABLE_NAME, values, null, null);
			} else {
				check = mDatabase.insert(IMAGE_TABLE_NAME, null, values);
			}

		} finally {
			cursor.close();
			mDatabase.close();
		}
		if(check >= 0) {
			return true;
		} else {
			return false;
		}
	}

	// Get Saved Image in Sqlite Database
	
	public Cursor getDatas() {
		SQLiteDatabase mDatabase = this.getReadableDatabase();
		Cursor cursor = mDatabase.rawQuery("Select * from "+IMAGE_TABLE_NAME, null);
		return cursor;
	}

	// Delete Image After get from Activity
	
	public void deleteTableContent() {
		SQLiteDatabase mDatabase = this.getWritableDatabase();
		mDatabase.delete(IMAGE_TABLE_NAME, null, null);
		mDatabase.close();
	}

}
