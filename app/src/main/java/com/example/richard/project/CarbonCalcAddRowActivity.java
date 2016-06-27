package com.example.richard.project;
// import statements
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

/**
 * This Activity will allow the user to add a new row of data
 * to the Carbon Calculator.
 * @author Richard Barney
 * @version 1.0.0 April 15, 2015
 */
public class CarbonCalcAddRowActivity extends FragmentActivity {
    private CarbonCalcDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private EditText mTripCategory;
    private EditText mVehicleType;
    private EditText mDistance;
    private EditText mDate;
    private EditText mNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carbon_calc_add_row);
        mTripCategory = (EditText)findViewById(R.id.new_trip_category);
        mVehicleType = (EditText)findViewById(R.id.new_vehicle_type);
        mDistance = (EditText)findViewById(R.id.new_distance);
        mDate = (EditText)findViewById(R.id.new_carbon_calc_date);
        mNote = (EditText)findViewById(R.id.new_carbon_calc_note);
    }

    /**
     * This method will add a new row to the database based on
     * what the user entered.
     * @param   view    View object.
     */
    public void addRowToDatabase(View view) {
        dbHelper = new CarbonCalcDBAdapter(this);
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, insert the new row of data,
                // update the cursor and then close the db
                dbHelper.open();
                dbHelper.insertRow(mTripCategory.getText().toString(),
                        mVehicleType.getText().toString(),
                        mDistance.getText().toString(),
                        mDate.getText().toString(),
                        mNote.getText().toString());
                Cursor cursor = dbHelper.fetchAllRows();
                dbHelper.close();
                return cursor;  // about to be passed by AsyncTask to onPostExecute
            }
            @Override
            public void onPostExecute(Cursor cursor) {
                dataAdapter.changeCursor(cursor); // MAIN THREAD after doInBackground returns the cursor
            }
        }.execute();
        // columns
        String[] columns = new String[]{
                CarbonCalcDBAdapter.KEY_TRIP_CATEGORY,
                CarbonCalcDBAdapter.KEY_VEHICLE_TYPE,
                CarbonCalcDBAdapter.KEY_DISTANCE,
                CarbonCalcDBAdapter.KEY_DATE,
                CarbonCalcDBAdapter.KEY_CO2_EMISSION,
                CarbonCalcDBAdapter.KEY_NOTE
        };
        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.trip_category,
                R.id.vehicle_type,
                R.id.distance,
                R.id.carbon_calc_date,
                R.id.co2emission,
                R.id.carbon_calc_note
        };
        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter( // MAIN THREAD
                this, R.layout.carbon_calc_row_layout,
                null,
                columns,
                to,
                0);
        finish(); // call finish() to return to MainActivity
    }
}
