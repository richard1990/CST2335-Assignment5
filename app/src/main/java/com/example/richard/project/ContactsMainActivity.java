package com.example.richard.project;

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
 * This is the main activity .
 * @author Zhuo Wang
 * @version 1.0.0 April 18, 2015
 */
public class ContactsMainActivity extends FragmentActivity
        implements ContactsListFragment.OnHeadlineSelectedListener {
    /**
     * The ContactsDbAdapter class object.
     */
    private ContactsDBAdapter dbHelper;
    /**
     * The ContactListFragment object.
     */
    private ContactsListFragment listFragment;
    private ContactsDetailFragment detailFragment;
    private SimpleCursorAdapter dataAdapter;
    private String id;
    boolean detailFragmentOpen = false;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState  If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_row_list);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.contacts_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            listFragment = new ContactsListFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contacts_fragment_container, listFragment).commit();
        }
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
        listFragment = new ContactsListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.contacts_fragment_container, listFragment).commitAllowingStateLoss();
    }
    /**
     * The user selected the headline of a contact from the ContactListFragment.
     * @param position  The position of selected contact.
     */

    public void onArticleSelected(String position) {
        detailFragmentOpen = true;
        // Create fragment and give it an argument for the selected article
        ContactsDetailFragment newFragment = new ContactsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ContactsDetailFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.remove(listFragment);
        transaction.add(R.id.contacts_fragment_container, newFragment);
        transaction.addToBackStack(null);

        // pass the row position to id to keep track of what row
        // the user is in in case he presses the delete button, and
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
        dbHelper = new ContactsDBAdapter(this);
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, delete the row based on the
                // id of the selected row, update the cursor and then
                // close the db
                dbHelper.open();
                dbHelper.deleteRow(id);
                Cursor cursor = dbHelper.fetchAllContacts();
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
                ContactsDBAdapter.KEY_FIRSTNAME,
                ContactsDBAdapter.KEY_LASTNAME,
                ContactsDBAdapter.KEY_PHONENUMBER,
                ContactsDBAdapter.KEY_EMAIL,
                ContactsDBAdapter.KEY_NOTE,
                ContactsDBAdapter.KEY_CREATIONDATE
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.firstName,
                R.id.lastName,
                R.id.phoneNumber,
                R.id.email,
                R.id.note,
                R.id.createDate,
        };
        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter( // MAIN THREAD
                this, R.layout.contacts_row_layout,
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
     * Called when the button to edit and view contact is clicked. Will start a new intent
     * using the EditAndViewContactFragment class.
     * @param   view    View object.
     */
    public void addNewContact(View view) {
        // get rid of previous fragment so that user
        // returns to MainActivity
        if (listFragment != null) {
            // make sure the fragment exists before removing it, then
            // remove the fragment and pop the back stack so that the fragments
            // don't overlap each other
            getSupportFragmentManager().beginTransaction().remove(listFragment).commitAllowingStateLoss();
            getSupportFragmentManager().popBackStack();
            onRestart();
        }

        Intent intent = new Intent(ContactsMainActivity.this, ContactsAddRowActivity.class);
        startActivity(intent);
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

    /**
     * Display the instruction to the user
     * @param view:
     */
    public void displayHelp(View view) {
        Context context = getApplicationContext();
        CharSequence text = "This application is about to add new contact and search for contact and" +
                "view contact. Please press button to explore. Created by teammate.";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}