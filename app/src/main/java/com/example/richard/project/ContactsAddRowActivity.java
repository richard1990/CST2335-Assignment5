package com.example.richard.project;

// import statements

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * This Activity will allow the user to add a new record of data
 * to the contact list.
 * @author Zhuo Wang
 * @version 1.0.0 April 13, 2015
 */
public class ContactsAddRowActivity extends FragmentActivity {
    private ContactsDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mPhoneNumber;
    private TextView mEmail;
    private TextView mNote;
    private TextView mCreateDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_add_row);
        mFirstName = (EditText)findViewById(R.id.new_first);
        mLastName = (EditText)findViewById(R.id.new_last);
        mPhoneNumber = (EditText)findViewById(R.id.new_phoneNumber);
        mEmail = (EditText)findViewById(R.id.new_email);
        mNote = (EditText)findViewById(R.id.new_note);
        mCreateDate = (EditText)findViewById(R.id.new_createDate);
    }

    /**
     * This method will add a new row to the database based on
     * what the user entered.
     * @param   view    View object.
     */
    public void addRowToDatabase(View view) {
        dbHelper = new ContactsDBAdapter(this);
        // use AsyncTask to reduce load on main thread
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                // open the database, insert the new row of data,
                // update the cursor and then close the db
                dbHelper.open();
                dbHelper.createContact(mFirstName.getText().toString(),
                        mLastName.getText().toString(),
                        mPhoneNumber.getText().toString(),
                        mEmail.getText().toString(),
                        mNote.getText().toString(),
                        mCreateDate.getText().toString());
                Cursor cursor = dbHelper.fetchAllContacts();
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
                this, R.layout.contacts_row_layout,
                null,
                columns,
                to,
                0);
        finish(); // call finish() to return to MainActivity
    }
}
