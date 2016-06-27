package com.example.richard.project;

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
 * Created by Zhuo Wang on 2015-04-10.
 *
 * The fragment of Contact list.
 */

public class ContactsListFragment extends ListFragment {

    OnHeadlineSelectedListener mCallback;
    /**
     * The ContactsDbAdapter object.
     */
    private ContactsDBAdapter dbHelper;
    /**
     * The instance of the SimpleCursorAdapter class.
     */
    private SimpleCursorAdapter dataAdapter;

    /**
     * The instance of the OnHeadlineSelectedListener interface.
     */

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */

        /** Called by ContactsListFragment when a list item is selected */

        public void onArticleSelected(String position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        //Generate ListView from SQLite Database
        displayListView();

        // Create an array adapter for the list view, using the Ipsum headlines array
        //   setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
    }

    /**
     * Displays the contacts list view.
     */

    private void displayListView() {
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                dbHelper = new ContactsDBAdapter(getActivity());
                dbHelper.open();
                //Clean all data
                //dbHelper.deleteAllContacts();
                //Add some data
                //dbHelper.insertSomeContacts();
                Cursor cursor = dbHelper.fetchAllContacts();
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

        // Cursor cursor = ((MainActivity)getActivity()).dbHelper.fetchAllContacts();
        // In class, I had these MAIN THREAD items in doInBackground(), but they need to be on the main thread
        // The desired columns to be bound  MAIN THREAD

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
        dataAdapter = new SimpleCursorAdapter(
                getActivity(), R.layout.contacts_row_layout,
                null,
                columns,
                to,
                0);

//        ListView listView = (ListView) findViewById(R.id.listView1);
//        // Assign adapter to ListView
//        listView.setAdapter(dataAdapter);
        setListAdapter(dataAdapter);


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
        mCallback.onArticleSelected(String.valueOf(id));

        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }
}