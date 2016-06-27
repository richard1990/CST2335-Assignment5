package com.example.richard.project;
// import statements
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This class holds the data for when the user
 * clicks a row in the list.
 * @author Richard Barney
 * @version 1.0.0 April 15, 2015
 */
public class CarbonCalcDetailFragment extends Fragment {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    private CarbonCalcDBAdapter dbHelper;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        return rootView = inflater.inflate(R.layout.carbon_calc_row_detail, container, false);
    }

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
            updateArticleView(args.getLong(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentPosition);
        }
    }

    /**
     * Method that updates the article view
     * @param   position  long holding position.
     */
    public void updateArticleView(long position) {
        //TextView article = (TextView) getActivity().findViewById(R.id.article);
        //article.setText(Ipsum.Articles[position]);
        dbHelper = new CarbonCalcDBAdapter(getActivity());
        dbHelper.open();
        Cursor cursor = dbHelper.fetchRowById(position);
        if (cursor != null) {
            try {
                String tripCategory =
                        cursor.getString(cursor.getColumnIndexOrThrow("trip_category"));
                String tripVehicleType =
                        cursor.getString(cursor.getColumnIndexOrThrow("vehicle_type"));
                String tripDistance =
                        cursor.getString(cursor.getColumnIndexOrThrow("distance"));
                String tripDate =
                        cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String tripEmission =
                        cursor.getString(cursor.getColumnIndexOrThrow("co2_emission"));
                String tripNote =
                        cursor.getString(cursor.getColumnIndexOrThrow("note"));

                ((TextView) rootView.findViewById(R.id.trip_category)).setText(tripCategory);
                ((TextView) rootView.findViewById(R.id.vehicle_type)).setText(tripVehicleType);
                ((TextView) rootView.findViewById(R.id.distance)).setText(tripDistance);
                ((TextView) rootView.findViewById(R.id.carbon_calc_date)).setText(tripDate);
                ((TextView) rootView.findViewById(R.id.co2emission)).setText(tripEmission);
                ((TextView) rootView.findViewById(R.id.carbon_calc_note)).setText(tripNote);

            } catch (IllegalArgumentException e) {
                //Log.d(TAG, "IllegalArgumentException");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
}