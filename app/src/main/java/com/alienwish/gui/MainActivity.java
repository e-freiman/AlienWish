package com.alienwish.gui;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alienwish.Alien;
import com.alienwish.R;

public class MainActivity extends AppCompatActivity {
    public static final int NOTIFIER_CLICKED_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Alien.isInitialized()) {
            Alien.init(getApplicationContext());
        }
        onConfigurationChanged(getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }

        EventListFragment.show(this);
    }
}
