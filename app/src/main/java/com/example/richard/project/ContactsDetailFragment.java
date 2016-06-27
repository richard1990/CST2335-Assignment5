package com.example.richard.project;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * Created by Zhuo Wang on 2015-04-15.
 *
 * The fragment of contact's details.
 */

public class ContactsDetailFragment extends Fragment {
    /**
     * The argument's position.
     */
    final static String ARG_POSITION = "position";
    final static String TAG = "IndividualFragment";
    /**
     * The ContactsDbAdapter object.
     */
    private ContactsDBAdapter dbHelper;
    /**
     * The value of current position.
     */
    String mCurrentPosition;
    /**
     * The View object.
     */
    View rootView;

    /**
     * Creates and returns the view hierarchy associated with the fragment_task_deatil.
     * @param inflater   The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container  If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Returns the view of contact_detail.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getString(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        return rootView = inflater.inflate(R.layout.contacts_row_detail, container, false);
    }
    /**
     * Called when the Fragment is visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(args.getString(ARG_POSITION));
        } else if (mCurrentPosition != null) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentPosition);
        }
    }
    /**
     * Displays the task's detail in task detail view.
     * @param position       The position of selected task.
     */

    public void updateArticleView(String position) {
        //TextView article = (TextView) getActivity().findViewById(R.id.article);
        // article.setText(Ipsum.Articles[position]);
        dbHelper = new ContactsDBAdapter(getActivity());
        dbHelper.open();
        Cursor cursor = dbHelper.fetchContactById(position);
        if (cursor != null) {
            try {
                String contactFirstName =
                        cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
                String contactLastName =
                        cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
                String contactPhoneNumber =
                        cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
                String contactEmail =
                        cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String contactNote =
                        cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String contactCreateDate =
                        cursor.getString(cursor.getColumnIndexOrThrow("createDate"));

                ((TextView) rootView.findViewById(R.id.firstName)).setText(contactFirstName);
                ((TextView) rootView.findViewById(R.id.lastName)).setText(contactLastName);
                ((TextView) rootView.findViewById(R.id.phoneNumber)).setText(contactPhoneNumber);
                ((TextView) rootView.findViewById(R.id.email)).setText(contactEmail);
                ((TextView) rootView.findViewById(R.id.note)).setText(contactNote);
                ((TextView) rootView.findViewById(R.id.createDate)).setText(contactCreateDate);
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "IllegalArgumentException");
            }
        }

        //mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putString(ARG_POSITION, mCurrentPosition);
    }
}