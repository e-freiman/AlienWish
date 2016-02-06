package com.alienwish.gui;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;

import com.alienwish.Alien;
import com.alienwish.App;
import com.alienwish.GuiStates;
import com.alienwish.R;

public class MainActivity extends Activity {
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
        updateState(App.getInstance().getState());
    }

    private void updateState(GuiStates state) {
        App.getInstance().setState(state);
        switch (state) {
            case Events:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    EventListFragment.show(this);
                } else {
                    EventListFragment.show(this);
                    EventDetailsFragment.hide(this);
                }
                break;
            case Details:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    EventDetailsFragment.show(this, null, false);
                } else {
                    Fragment target = EventListFragment.show(this);
                    EventDetailsFragment.show(this, target, false);
                }
                break;
            default:
                throw new IllegalStateException("Wrong GUI state");
        }
    }

    @Override
    public void onBackPressed() {
        switch (App.getInstance().getState()) {
            case Events:
                super.onBackPressed();
                break;
            case Details:
                updateState(GuiStates.Events);
                break;
            default:
                throw new IllegalStateException("Wrong GUI state");
        }
    }
}
