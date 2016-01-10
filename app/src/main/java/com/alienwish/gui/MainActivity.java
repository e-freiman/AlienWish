package com.alienwish.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alienwish.Alien;
import com.alienwish.EventStorage;
import com.alienwish.R;
import com.alienwish.Utils;
import com.jakewharton.rxbinding.view.RxView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Alien.isInitialized()) {
            Alien.init(getApplicationContext());
        }

    }
}
