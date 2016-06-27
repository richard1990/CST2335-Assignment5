/* Copyright © 2011-2013 mysamplecode.com, All rights reserved.
  This source code is provided to students of CST2335 for educational purposes only.
 */
/**
 * Code in this file is taken from examples provided by Prof. Todd Kelley.
 */
/**
 * Database adapter class for Tip Calculator
 * @author Laura Graham
 */
package com.example.richard.project;
//imports
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipCalcDBAdapter {
    /** Column name */
    public static final String KEY_ROWID = "_id";
    /** Column name */
    public static final String KEY_RESTAURANT = "restaurant";
    /** Column name */
    public static final String KEY_PRICE = "price";
    /** Column name */
    public static final String KEY_PERCENT = "tipPercent";
    /** Column name */
    public static final String KEY_TIP = "tipAmount";
    /** Column name */
    public static final String KEY_TOTAL = "total";
    /** Column name */
    public static final String KEY_DATE = "date";
    /** Column name */
    public static final String KEY_NOTES = "notes";

    private static final String TAG = "TipCalcDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    /** Database name */
    private static final String DATABASE_NAME = "Project";
    /** Table name */
    private static final String SQLITE_TABLE = "TipCalc";
    /** Version of database */
    private static final int DATABASE_VERSION = 3;
    /** Tip */
    private double mTip;
    /** Price */
    private double mPrice;
    /** Context */
    private final Context mCtx;

    /**
     * Creates the database
     */
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_RESTAURANT + "," +
                    KEY_PRICE + "," +
                    KEY_PERCENT + "," +
                    KEY_TIP + "," +
                    KEY_TOTAL + "," +
                    KEY_DATE + "," +
                    KEY_NOTES +");";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public TipCalcDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public TipCalcDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    /**
     * Creates a tip
     * @param restaurant
     * @param price
     * @param percent
     * @param date
     * @param notes
     * @return insertion of newly create tip into database
     */
    public long createTip(String restaurant, String price, String percent, String date, String notes) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RESTAURANT, restaurant);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_PERCENT, percent);
        initialValues.put(KEY_TIP, calculateTip(price, percent));
        initialValues.put(KEY_TOTAL, calculateTotal());
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_NOTES, notes);

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    /**
     * Calculates how much the tip is in dollars
     * @param price
     * @param percent
     * @return tip
     */
    public String calculateTip(String price, String percent){
        this.mPrice = Double.parseDouble(price);
        double mPercent = (Double.parseDouble(percent))/100;
        this.mTip = mPrice*mPercent;
        return Double.toString(mTip);
    }

    /**
     * Calculates the total cost including tip
     * @return total cost
     */
    public String calculateTotal(){
        double mTotal = mPrice+mTip;
        return Double.toString(mTotal);
    }

    /**
     * deletes a tip from the database
     * @param id
     * @return deleted tip from database
     */
    public int deleteTipDb(long id) {
        return mDb.delete(SQLITE_TABLE, KEY_ROWID + " = ?" ,new String[] {String.valueOf(id)});
    }

    /**
     * deletes all tips in the database
     * @return empty database
     */
    public boolean deleteAllTips() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    /**
     * retrieves the tip by the id provided
     * @param id
     * @return cursor
     * @throws SQLException
     */
    public Cursor fetchTipById(long id) throws SQLException {
        Cursor mCursor = null;
        Log.d(TAG,"id is " + id);
        mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_RESTAURANT, KEY_PRICE, KEY_PERCENT, KEY_TIP, KEY_TOTAL, KEY_DATE, KEY_NOTES},
                KEY_ROWID + " = ?", new String[] {String.valueOf(id)}, null, null, null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Retrieves all tips from the database
     * @return cursor
     */
    public Cursor fetchAllTips() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_RESTAURANT, KEY_PRICE, KEY_PERCENT, KEY_TIP, KEY_TOTAL, KEY_DATE, KEY_NOTES},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * inserts some tips into the database
     */
    public void insertSomeTips() {
        createTip("Pizza", "10.00", "15", "April 14", "No notes");
        createTip("CoffeePlace", "5.00", "20", "April 17", "Got coffee");
    }

}
