package com.alienwish.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alienwish.Alien;
import com.alienwish.R;

public class MainActivity extends AppCompatActivity {
    public static final int NOTIFIER_CLICKED_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Alien.isInitialized()) {
            Alien.init(getApplicationContext());
        }
    }
}
