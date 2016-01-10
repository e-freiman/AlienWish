package com.alienwish.gui;

import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.alienwish.R;

public class EventDetailsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
            eventDetailsFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, eventDetailsFragment).commit();
        }
    }
}
