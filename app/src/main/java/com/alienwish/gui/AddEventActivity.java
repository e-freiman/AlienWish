package com.alienwish.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alienwish.R;
import com.jakewharton.rxbinding.view.RxView;

public class AddEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        RxView.clicks(findViewById(R.id.activity_add_event_add_button)).subscribe(notification -> {
            finish();
        });
    }
}
