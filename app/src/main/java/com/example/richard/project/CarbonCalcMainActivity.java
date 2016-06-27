package com.example.richard.project;
// import statements
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * This is the main Activity class for the Carbon Calculator.
 * @author Richard Barney
 * @version 1.0.0 April 15, 2015
 */
public class CarbonCalcMainActivity extends FragmentActivity
        implements CarbonCalcListFragment.OnHeadlineSelectedListener {

    private CarbonCalcDetailFragment detailFragment;
    private CarbonCalcListFragment listFragment;
    private CarbonCalcDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private long id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carbon_calc_row_list);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.carbon_calc_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            listFragment = new CarbonCalcListFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.carbon_calc_fragment_container, listFragment).commit();
        }
    }

    /**
     * Action bar stuff.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_one:
                go(CarbonCalcMainActivity.class);
                break;
            case R.id.action_two:
                go(ContactsMainActivity.class);
                break;
            case R.id.action_three:
                go(TipCalcMainActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void go(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    public void onRestart() {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestart();
        // remove the old fragment to not let them overlap
        // then recreate the fragment with the new data added
        // to the database
        if (listFragment != null) {
            // make sure the fragment exists before removing it
            getSupportFragmentManager().beginTransaction().remove(listFragment).commitAllowingStateLoss();
        }
        listFragment = new CarbonCalcListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.carbon_calc_fragment_container, listFragment).commitAllowingStateLoss();
    }

    @Override
    public void onArticleSelected(long position) {
        // Create fragment and give it an argument for the selected article
        CarbonCalcDetailFragment newFragment = new CarbonCalcDetailFragment();
        Bundle args = new Bundle();
        args.putLong(CarbonCalcDetailFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // move the fragments around, remove the listFragment to prevent multiple fragments
        // from overlapping, then add the new fragment
        transaction.remove(listFragment);
        transaction.add(R.id.carbon_calc_fragment_container, newFragment);
        transaction.addToBackStack(null);

        // pass the row position to id to keep track of what row
        // the user is in incase he presses the delete button, and
        // pass the fragment object to fragmentDetail to keep
        // track of the instance of the fragment detail which
        // is needed if the row is deleted
        id = position;
        detailFragment = newFragment;

        // Commit the transaction
        transaction.commit();
    }

    /**
     * Called when button is clicked to delete a row.
     * @param   view    View object.
     */
    public void deleteRow(View view) {
        dbHelper = new CarbonCalcDBAdapter(this);
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, delete the row based on the
                // id of the selected row, update the cursor and then
                // close the db
                dbHelper.open();
                dbHelper.deleteRow(id);
                Cursor cursor = dbHelper.fetchAllRows();
                dbHelper.close();
                return cursor;  // about to be passed by AsyncTask to onPostExecute
            }
            @Override
            public void onPostExecute(Cursor cursor) {
                dataAdapter.changeCursor(cursor); // MAIN THREAD after doInBackground returns the cursor
                cursor.close();
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
        // the user will be stuck in the detail fragment, so to fix this
        // remove the fragment and pop the backstack to return them to the
        // main activity, then call onRestart to refresh the list
        getSupportFragmentManager().beginTransaction().remove(detailFragment).commitAllowingStateLoss();
        getSupportFragmentManager().popBackStack();
        onRestart();
    }

    /**
     * Called when the button to add a row is clicked. Will start a new intent
     * using the AddRowActivity class.
     * @param   view    View object.
     */
    public void addRow(View view) {
        // get rid of previous fragment so that user
        // returns to MainActivity
        if (listFragment != null) {
            // make sure the fragment exists before removing it, then
            // remove the fragment and pop the backstack so that the fragments
            // don't overlap eachother
            getSupportFragmentManager().beginTransaction().remove(listFragment).commitAllowingStateLoss();
            getSupportFragmentManager().popBackStack();
            onRestart();
        }
        Intent intent = new Intent(CarbonCalcMainActivity.this, CarbonCalcAddRowActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the button to display help is clicked. Will display
     * a toast with some instructions. Code taken from Android Developer
     * (http://developer.android.com/guide/topics/ui/notifiers/toasts.html).
     * @param   view    View object.
     */
    public void displayHelp(View view) {
        Context context = getApplicationContext();
        CharSequence text = "This app is a Carbon Emission Calculator. Based on the "
                +"distance travelled and the vehicle type, the app will "
                +"tell you your CO2 emission in metric tonnes. Note that "
                +"the trip category must be Car, Bus, or Bicycle for the app to "
                +"function properly. Created by Richard Barney.";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}