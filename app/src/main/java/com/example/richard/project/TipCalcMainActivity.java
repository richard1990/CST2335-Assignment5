/* Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Code in this file is taken from examples provided by Prof. Todd Kelley.
 */
package com.example.richard.project;
//imports
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
 * Main class for Tip Calculator
 * @author Laura Graham
 */
public class TipCalcMainActivity extends FragmentActivity
        implements TipCalcListFragment.OnHeadlineSelectedListener {

    private TipCalcDetailFragment detailFragment;
    private TipCalcListFragment listFragment;
    private TipCalcDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private long id;
    boolean detailFragmentOpen = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tip_calc_row_list);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.tip_calc_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            listFragment = new TipCalcListFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tip_calc_fragment_container, listFragment).commit();
        }
    }

    /**
     * create menu
     * @param menu
     * @return menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * redirects to selected class
     * @param item
     * @return menu option selected
     */
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
        listFragment = new TipCalcListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.tip_calc_fragment_container, listFragment).commitAllowingStateLoss();
    }

    /**
     * go to detail fragment when list item is selected
     * @param position
     */
    @Override
    public void onArticleSelected(long position) {
        // The user selected the headline of an article from the TipCalcListFragment

        // Create fragment and give it an argument for the selected article
        TipCalcDetailFragment newFragment = new TipCalcDetailFragment();
        Bundle args = new Bundle();
        args.putLong(TipCalcDetailFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.tip_calc_fragment_container, newFragment);
        transaction.addToBackStack(null);

        // get the ID of the row and fragment
        id = position;
        detailFragment = newFragment;
        // Commit the transaction
        transaction.commit();
    }

    /**
     * delete tip from database
     * @param view
     */
    public void deleteTip(View view) {
        dbHelper = new TipCalcDBAdapter(this);
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, delete the row based on the
                // id of the selected row, update the cursor and then
                // close the db
                dbHelper.open();
                dbHelper.deleteTipDb(id);
                Cursor cursor = dbHelper.fetchAllTips();
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
                R.id.notes
        };
        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter( // MAIN THREAD
                this, R.layout.tip_calc_row_list,
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
     * creates a new tip
     * @param view
     */
    public void newTip(View view) {
        // get rid of previous fragment so that user
        // returns to TipCalcMainActivity
        if (listFragment != null) {
            // make sure the fragment exists before removing it, then
            // remove the fragment and pop the backstack so that the fragments
            // don't overlap eachother
            getSupportFragmentManager().beginTransaction().remove(listFragment).commitAllowingStateLoss();
            getSupportFragmentManager().popBackStack();
            onRestart();
        }
        Intent intent = new Intent(TipCalcMainActivity.this, TipCalcAddRowActivity.class);
        startActivity(intent);
    }

    /**
     * Display a toast with details.
     */
    public void displayHelp(View view) {
        Context context = getApplicationContext();
        CharSequence text = "This app is a tip calculator. Created by Laura Graham.";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}