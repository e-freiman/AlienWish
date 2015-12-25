package com.alienwish.gui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.alienwish.R;

public class DatetimePickerActivity extends AppCompatActivity {

    private static final String TAG_DATE_SPEC = "date";
    private static final String TAG_TIME_SPEC = "time";
    private static final String OPEN_TAB_EXTRA = "current_tab";

    public static Intent createPickDataIntent(Context context) {
        Intent intent = new Intent(context, DatetimePickerActivity.class);
        intent.putExtra(OPEN_TAB_EXTRA, TAG_DATE_SPEC);
        return intent;
    }

    public static Intent createPickTimeIntent(Context context) {
        Intent intent = new Intent(context, DatetimePickerActivity.class);
        intent.putExtra(OPEN_TAB_EXTRA, TAG_TIME_SPEC);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime_picker);

        TabHost tabs = (TabHost)findViewById(R.id.activity_datetime_picker_tabs);
        tabs.setup();

        TabHost.TabSpec dateSpec = tabs.newTabSpec(TAG_DATE_SPEC);
        dateSpec.setContent(R.id.activity_datetime_picker_date);
        dateSpec.setIndicator(getResources().getString(R.string.activity_datetime_picker_tab_date));
        tabs.addTab(dateSpec);

        TabHost.TabSpec timeSpec = tabs.newTabSpec(TAG_TIME_SPEC);
        timeSpec.setContent(R.id.activity_datetime_picker_time);
        timeSpec.setIndicator(getResources().getString(R.string.activity_datetime_picker_tab_time));
        tabs.addTab(timeSpec);

        tabs.setCurrentTabByTag(getIntent().getStringExtra(OPEN_TAB_EXTRA));
    }
}
