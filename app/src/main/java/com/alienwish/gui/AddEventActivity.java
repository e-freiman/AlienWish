package com.alienwish.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alienwish.R;
import com.jakewharton.rxbinding.view.RxView;

public class AddEventActivity extends AppCompatActivity {

    private static final int DATETIME_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        RxView.clicks(findViewById(R.id.activity_add_event_add_button)).subscribe(notification -> {
            finish();
        });

        RxView.clicks(findViewById(R.id.activity_add_event_date)).subscribe(notification -> {
            startActivityForResult(DatetimePickerActivity.createPickDataIntent(this), DATETIME_REQUEST_CODE);
        });

        RxView.clicks(findViewById(R.id.activity_add_event_time)).subscribe(notification -> {
            startActivityForResult(DatetimePickerActivity.createPickTimeIntent(this), DATETIME_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
