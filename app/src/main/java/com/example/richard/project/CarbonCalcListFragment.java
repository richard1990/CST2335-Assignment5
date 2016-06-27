package com.example.richard.project;
// import statements
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * This class handles the list.
 * @author Richard Barney
 * @version 1.0.0 April 15, 2015
 */
public class CarbonCalcListFragment extends ListFragment {
    OnHeadlineSelectedListener mCallback;
    private CarbonCalcDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by CountryListFragment when a list item is selected */
        public void onArticleSelected(long position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        //Generate ListView from SQLite Database
        displayListView();
    }

    /**
     * Displays the data.
     */
    private void displayListView() {
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, update the cursor and then
                // close the db
                dbHelper = new CarbonCalcDBAdapter(getActivity());
                dbHelper.open();
                //Add some data
                //dbHelper.insertSomeData();
                //Delete all data
                //dbHelper.deleteAllRows();
                Cursor cursor = dbHelper.fetchAllRows();
                dbHelper.close();
                return cursor;  // about to be passed by AsyncTask to onPostExecute
            }
            @Override
            public void onPostExecute(Cursor cursor) {
                //ListView listView = (ListView) findViewById(R.id.listView1);
                // Assign adapter to ListView
                dataAdapter.changeCursor(cursor);  //MAIN THREAD after doInBackground returns the cursor

            }
        }.execute();
        // columns to be bound MAIN THREAD
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
                getActivity(), R.layout.carbon_calc_row_layout,
                null,
                columns,
                to,
                0);

        setListAdapter(dataAdapter); // MAIN THREAD
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        mCallback.onArticleSelected(id);

        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }
}