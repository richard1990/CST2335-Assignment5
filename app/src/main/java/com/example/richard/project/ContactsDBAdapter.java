package com.example.richard.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Zhuo Wang on 2015-Apr-15.
 * Creates and Initializes Database.
 */
public class ContactsDBAdapter {
    /**
     * The ID attribute.
     */
    public static final String KEY_ROWID = "_id";
    /**
     * The firstName attribute.
     */
    public static final String KEY_FIRSTNAME = "firstName";
    /**
     * The lastName attribute.
     */
    public static final String KEY_LASTNAME = "lastName";
    /**
     * The phoneNumber attribute.
     */
    public static final String KEY_PHONENUMBER = "phoneNumber";
    /**
     * The email attribute.
     */
    public static final String KEY_EMAIL = "email";
    /**
     * The note attribute.
     */
    public static final String KEY_NOTE = "note";
    /**
     * The createDate attribute.
     */
    public static final String KEY_CREATIONDATE = "createDate";
    /**
     * The TAG.
     */
    private static final String TAG = "ContactsDbAdapter";
    /**
     * The DatabaseHelper object.
     */
    private DatabaseHelper mDbHelper;
    /**
     * The SQLiteDatabase object.
     */
    private SQLiteDatabase mDb;
    /**
     * The database name.
     */
    private static final String DATABASE_NAME = "ContactsList";
    /**
     * The table name.
     */
    private static final String SQLITE_TABLE = "Contact";
    /**
     * The database version.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * The Context object.
     */
    private final Context mCtx;
    /**
     * The SQL Statement of creating database.
     */
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_FIRSTNAME + "," +
                    KEY_LASTNAME + "," +
                    KEY_PHONENUMBER + "," +
                    KEY_EMAIL + "," +
                    KEY_NOTE + "," +
                    KEY_CREATIONDATE +");";

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
    /**
     * The constructor.
     * @param ctx  The Context object.
     */

    public ContactsDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    /**
     * Gets the data repository in write mode
     */
    public ContactsDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    /**
     * Close the database.
     */

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
    /**
     * Insert every contact record's attribute into every column
     * @param firstName          The firstName.
     * @param lastName       The lastName.
     * @param phoneNumber           The phoneNumber.
     * @param email        The email.
     * @param note      The note.
     * @param createDate        The createDate.
     * @return
     */

    public long createContact(String firstName, String lastName, String phoneNumber,
                              String email, String note, String createDate) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FIRSTNAME, firstName);
        initialValues.put(KEY_LASTNAME, lastName);
        initialValues.put(KEY_PHONENUMBER, phoneNumber);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_NOTE, note);
        initialValues.put(KEY_CREATIONDATE, createDate);


        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }
    /**
     * Delete a record from database
     * @param id  The record ID
     * @return:
     */


    public int deleteRow(String id) {
        return mDb.delete(SQLITE_TABLE, KEY_ROWID + " = ?" ,new String[] {String.valueOf(id)});
    }



    public boolean deleteAllContacts() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null , null);//WHAT ARE THOSE NULL STANDS FOR???
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }
    /**
     * Get contact from database by Id
     * @param id             The record ID
     * @return               The result of get contact by ID
     * @throws SQLException  SQLException
     */

    public Cursor fetchContactById(String id) throws SQLException {
        Cursor mCursor = null;
        Log.d(TAG,"id is " + id);
        mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_FIRSTNAME, KEY_LASTNAME, KEY_PHONENUMBER, KEY_EMAIL, KEY_NOTE,
                        KEY_NOTE, KEY_CREATIONDATE},
                KEY_ROWID + " = ?", new String[] {id}, null, null, null);//WHAT ARE THOSE NULL STANDS FOR???
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchContactsByPhoneNumber(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_FIRSTNAME, KEY_LASTNAME, KEY_PHONENUMBER, KEY_EMAIL,
                            KEY_NOTE, KEY_CREATIONDATE},
                    null, null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_FIRSTNAME, KEY_LASTNAME, KEY_PHONENUMBER, KEY_EMAIL,
                            KEY_NOTE, KEY_CREATIONDATE},
                    KEY_PHONENUMBER + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    /**
     * Get all contacts from database
     * @return  The result of get all contacts from database
     */

    public Cursor fetchAllContacts() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_FIRSTNAME, KEY_LASTNAME, KEY_PHONENUMBER, KEY_EMAIL,
                        KEY_NOTE, KEY_CREATIONDATE},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Insert testing record into the database
     */

    public void insertSomeContacts() {
        createContact("Linda","Test1","613-4567890","test1@email.com","first test","03-28-2015");
        createContact("Daniel","Test2","613-54678950","test2@emai.com","second test","03-29-2015");
        createContact("Jannie","test3","613-5648978","test3@email.com","third test","03-30-2015");
    }
}
