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
 * Details fragment class for Tip Calculator
 * @author Laura Graham
 */
package com.example.richard.project;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TipCalcDetailFragment extends Fragment {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    private TipCalcDBAdapter dbHelper;
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
        return rootView = inflater.inflate(R.layout.tip_calc_row_detail, container, false);
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

    public void updateArticleView(long position) {
        //TextView article = (TextView) getActivity().findViewById(R.id.article);
        //article.setText(Ipsum.Articles[position]);
        dbHelper = new TipCalcDBAdapter(getActivity());
        dbHelper.open();
        Cursor cursor = dbHelper.fetchTipById(position);
        if (cursor != null) {
            try {
                String restaurant =
                        cursor.getString(cursor.getColumnIndexOrThrow("restaurant"));
                String price =
                        cursor.getString(cursor.getColumnIndexOrThrow("price"));
                String percent =
                        cursor.getString(cursor.getColumnIndexOrThrow("tipPercent"));
                String tip =
                        cursor.getString(cursor.getColumnIndexOrThrow("tipAmount"));
                String total =
                        cursor.getString(cursor.getColumnIndexOrThrow("total"));
                String date =
                        cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String notes =
                        cursor.getString(cursor.getColumnIndexOrThrow("notes"));

                ((TextView) rootView.findViewById(R.id.restaurant)).setText(restaurant);
                ((TextView) rootView.findViewById(R.id.price)).setText(price);
                ((TextView) rootView.findViewById(R.id.percent)).setText(percent);
                ((TextView) rootView.findViewById(R.id.tip)).setText(tip);
                ((TextView) rootView.findViewById(R.id.total)).setText(total);
                ((TextView) rootView.findViewById(R.id.date)).setText(date);
                ((TextView) rootView.findViewById(R.id.notes)).setText(notes);

            } catch (IllegalArgumentException e) {
                //Log.d(TAG, "IllegalArgumentException");
            }
        }
        //mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
}