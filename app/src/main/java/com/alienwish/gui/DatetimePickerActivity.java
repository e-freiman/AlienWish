package com.alienwish.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.alienwish.R;

public class DatetimePickerActivity extends AppCompatActivity {

    private static final String tagDateSpec = "date";
    private static final String tagTimeSpec = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime_picker);

        TabHost tabs = (TabHost)findViewById(R.id.activity_datetime_picker_tabs);
        tabs.setup();

        TabHost.TabSpec dateSpec = tabs.newTabSpec(tagDateSpec);
        dateSpec.setContent(R.id.activity_datetime_picker_date);
        dateSpec.setIndicator(getResources().getString(R.string.activity_datetime_picker_tab_date));
        tabs.addTab(dateSpec);

        TabHost.TabSpec timeSpec = tabs.newTabSpec(tagTimeSpec);
        timeSpec.setContent(R.id.activity_datetime_picker_time);
        timeSpec.setIndicator(getResources().getString(R.string.activity_datetime_picker_tab_time));
        tabs.addTab(timeSpec);

        tabs.setCurrentTabByTag(tagDateSpec);
    }
}
