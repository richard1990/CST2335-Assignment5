package com.example.richard.project;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Laura on 2015-04-13.
 *
 * Adds a new Tip entry to the database
 */
public class TipCalcAddRowActivity extends FragmentActivity {
    private TipCalcDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private EditText mRestaurant;
    private EditText mPrice;
    private EditText mPercent;
    private EditText mDate;
    private EditText mNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tip_calc_add_row);
        mRestaurant = (EditText)findViewById(R.id.new_restaurant);
        mPrice = (EditText)findViewById(R.id.new_price);
        mPercent = (EditText)findViewById(R.id.new_percent);
        mDate = (EditText)findViewById(R.id.new_date);
        mNote = (EditText)findViewById(R.id.new_note);
    }

    /**
     * Adds a new tip to the database
     * @param   view
     */
    public void saveTip(View view) {
        dbHelper = new TipCalcDBAdapter(this);
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, insert the new row of data,
                // update the cursor and then close the db
                dbHelper.open();
                dbHelper.createTip(mRestaurant.getText().toString(),
                        mPrice.getText().toString(),
                        mPercent.getText().toString(),
                        mDate.getText().toString(),
                        mNote.getText().toString());
                Cursor cursor = dbHelper.fetchAllTips();
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
                TipCalcDBAdapter.KEY_RESTAURANT,
                TipCalcDBAdapter.KEY_PRICE,
                TipCalcDBAdapter.KEY_PERCENT,
                TipCalcDBAdapter.KEY_TIP,
                TipCalcDBAdapter.KEY_TOTAL,
                TipCalcDBAdapter.KEY_DATE,
                TipCalcDBAdapter.KEY_NOTES
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.restaurant,
                R.id.price,
                R.id.percent,
                R.id.tip,
                R.id.total,
                R.id.date,
                R.id.notes,
        };
        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter( // MAIN THREAD
                this, R.layout.tip_calc_row_list,
                null,
                columns,
                to,
                0);
        finish(); // call finish() to return to TipCalcMainActivity
    }
}
