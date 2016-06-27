/*
 * Copyright (C) 2012 The Android Open Source Project
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
/**
 * List fragment class for Tip Calculator
 * @author Laura Graham
 */
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

public class TipCalcListFragment extends ListFragment {
    OnHeadlineSelectedListener mCallback;
    private TipCalcDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by TipCalcListFragment when a list item is selected */
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
        // Create an array adapter for the list view, using the Ipsum headlines array
        //setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
    }

    /**
     * Displays the tip list view
     */
    private void displayListView() {
        new AsyncTask<Object, Object, Cursor>() {
            @Override
            public Cursor doInBackground(Object... ignore) {
                dbHelper = new TipCalcDBAdapter(getActivity());
                dbHelper.open();
                //Clean all data
                //dbHelper.deleteAllTips();
                //Add some data
                //dbHelper.insertSomeTips();
                Cursor cursor = dbHelper.fetchAllTips();
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
        // In class, I had these MAIN THREAD items in doInBackground(), but they need to be on the main thread
        // The desired columns to be bound  MAIN THREAD
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
        dataAdapter = new SimpleCursorAdapter(             // MAIN THREAD
                getActivity(), R.layout.tip_calc_row_layout,
                null,
                columns,
                to,
                0);

        setListAdapter(dataAdapter);  //MAIN THREAD
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