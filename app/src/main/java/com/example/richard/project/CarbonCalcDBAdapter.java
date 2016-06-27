
package com.example.richard.project;
// import statements
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class sets up the whole database.
 * @author Richard Barney
 * @version 1.0.0 April 15, 2015
 */
public class CarbonCalcDBAdapter {
    // general DB stuff
    private static final String DATABASE_NAME = "CarbonFootprintDatabase";
    private static final String SQLITE_TABLE = "CarbonFootprintTable";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBAdapter";

    // column names
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TRIP_CATEGORY = "trip_category";
    public static final String KEY_VEHICLE_TYPE = "vehicle_type";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_CO2_EMISSION = "co2_emission";
    public static final String KEY_DATE = "date";
    public static final String KEY_NOTE = "note";

    // other objects to help out
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    /** String to generate db */
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_TRIP_CATEGORY + "," +
                    KEY_VEHICLE_TYPE + "," +
                    KEY_DISTANCE + "," +
                    KEY_DATE + "," +
                    KEY_CO2_EMISSION + "," +
                    KEY_NOTE +");";

    /**
     * Help with the db.
     */
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

    public CarbonCalcDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open database.
     */
    public CarbonCalcDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close database.
     */
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    /**
     * Add a single row.
     */
    public long insertRow(String tripCategory, String vehicleType,
                       String distance, String date, String note) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_CATEGORY, tripCategory);
        initialValues.put(KEY_VEHICLE_TYPE, vehicleType);
        initialValues.put(KEY_DISTANCE, distance);
        initialValues.put(KEY_DATE, date);
        // calculate co2 emission by passing the vehicle type and distance
        // to the calcCarbonEmission method
        initialValues.put(KEY_CO2_EMISSION, calcCarbonEmission(vehicleType, distance));
        initialValues.put(KEY_NOTE, note);

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    /**
     * Calculate the CO2 emission based on distance and vehicle type.
     */
    public String calcCarbonEmission(String vehicleType, String distance) {
        double emission;
        String sEmission = "";
        StringBuilder sb = new StringBuilder(); // use StringBuilder object to help setup the stringS
        switch(vehicleType) {
            case "Bicycle":
                // 21 grams of CO2e/km avg according to http://www.ecf.com/wp-content/uploads/ECF_CO2_WEB.pdf
                // calc emission and convert to metric tonnes
                emission = Integer.parseInt(distance) * 21;
                // if emission is < 1000 just display 0.000 so that user does not
                // see something like 9.7E-4
                if (emission < 1000) {
                    sb.append("0.000");
                }
                else {
                    sb.append(Double.toString(emission / 1000000));
                }
                sEmission = sb.substring(0, 5) + " metric tonnes";
                break;
            case "Bus":
                // 898 grams of CO2e/km avg according to http://www.ecf.com/wp-content/uploads/ECF_CO2_WEB.pdf
                // calc emission and convert to metric tonnes
                emission = Integer.parseInt(distance) * 898;
                if (emission < 1000) {
                    sb.append("0.000");
                }
                else {
                    sb.append(Double.toString(emission / 1000000));
                }
                sEmission = sb.substring(0, 5) + " metric tonnes";
                break;
            case "Car":
                // 194 grams of CO2e/km avg for gasoline car according to http://www.ecf.com/wp-content/uploads/ECF_CO2_WEB.pdf
                // calc emission and convert to metric tonnes
                emission = Integer.parseInt(distance)* 194;
                // if emission is < 1000 just display 0.000 so that user does not
                // see something like 9.7E-4
                if (emission < 1000) {
                    sb.append("0.000");
                }
                else {
                    sb.append(Double.toString(emission / 1000000));
                }
                sEmission = sb.substring(0, 5) + " metric tonnes";
                break;
            default:
                sEmission = "Err - Invalid vehicle type";
                break;
        } // end switch
        return sEmission;// return a string holding the emission data
    }

    /**
     * Delete one row.
     * @param   id  long holding the row id to be delete.
     */
    public int deleteRow(long id) {
        return mDb.delete(SQLITE_TABLE, KEY_ROWID + " = ?" ,new String[] {String.valueOf(id)});
    }

    /**
     * Delete all rows.
     */
    public boolean deleteAllRows() {
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    /**
     * Retrieve a row by its ID.
     * @param   id  long holding the row id to be fetched.
     */
    public Cursor fetchRowById(long id) throws SQLException {
        Cursor mCursor = null;
        Log.d(TAG,"id is " + id);
        mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_TRIP_CATEGORY, KEY_VEHICLE_TYPE, KEY_DISTANCE, KEY_DATE, KEY_CO2_EMISSION, KEY_NOTE},
                KEY_ROWID + " = ?", new String[] {String.valueOf(id)}, null, null, null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Retrieve all the rows in the table.
     */
    public Cursor fetchAllRows() {
        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_TRIP_CATEGORY, KEY_VEHICLE_TYPE, KEY_DISTANCE, KEY_DATE, KEY_CO2_EMISSION, KEY_NOTE},
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Generate some test data.
     */
    public void insertSomeData() {
        insertRow("Biked to work","Bicycle","11","01/04/2015", "Biked from my house to work");
        insertRow("Bussed to work","Bus","11","02/04/2015", "Bussed from my house to work");
        insertRow("Drove to work","Car","11","03/04/2015", "Drove in a car from my house to work");
        insertRow("Bussed to school","Bus","18","04/04/2015","Bussed from my house to school");
        insertRow("Drove to school","Car","18","05/04/2015","Drove in a car from my house to school");
    }
}