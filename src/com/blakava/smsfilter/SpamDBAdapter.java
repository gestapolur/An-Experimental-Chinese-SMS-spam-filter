package com.blakava.smsfilter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SpamDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_ADDRESS = "ADDRESS";
	public static final String KEY_MSG = "MESSAGE";	
    
    private static final String TAG = "SpamDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String DATABASE_NAME = "SPAM1D";
    private static final String table1 = "TABLE1"; 

    private static final int DATABASE_VERSION = 5;
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE1 =
        " CREATE TABLE " + table1 + 
        " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
        " ADDRESS TEXT, "+
        " MESSAGE TEXT );";

    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(DATABASE_CREATE1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + table1 + ";");

            onCreate(db);
        }
    }
    
    public SpamDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    public SpamDBAdapter() {
    	this.mCtx = null;
    }

    public SpamDBAdapter open() throws SQLException {

        //mDb.execSQL("DROP DATABASE IF EXISTS " + DATABASE_NAME + ";");
    	if( mCtx != null)
    		mDbHelper = new DatabaseHelper(mCtx);       
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    //Insert
    public long insertEntryTable( String address , String msg ) {
        ContentValues initialValues = new ContentValues();
        initialValues.put( KEY_ADDRESS , address );
        initialValues.put( KEY_MSG , msg );
        return mDb.insert(table1, null, initialValues);
    }

    public boolean deleteEntryTable1(long rowId) {
        return mDb.delete(table1, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public void deleteData( int idx ) {
    	mDb.execSQL("DELETE FROM " + table1 + " WHERE _id= " + idx );
    }
    
    public void deleteTable1() {
    	mDb.execSQL("DROP TABLE IF EXISTS " + table1 + ";");
    }
    
    public Cursor fetchAllEntriesForTable() {
		if (mDb == null){
			this.open();
		}
		return mDb.query("TABLE1", new String[] { KEY_ROWID, KEY_ADDRESS , KEY_MSG }, null, null, null, null, null);
    }	
}
