package com.alienwish.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alienwish.Alien;
import com.alienwish.EventStorage;
import com.alienwish.R;
import com.alienwish.Utils;
import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Alien.isInitialized()) {
            Alien.init(getApplicationContext());
        }

        RxView.clicks(findViewById(R.id.activity_main_schedule_all_button)).subscribe(notification -> {
            Alien.getInstance().scheduleAllEvents();
        });

        RxView.clicks(findViewById(R.id.activity_main_cancel_all_button)).subscribe(notification -> {
            Alien.getInstance().cancelAllEvents();
        });

        RxView.clicks(findViewById(R.id.activity_main_sample_db_button)).subscribe(notification -> {
            EventStorage es = Alien.getInstance().getEventStorage();

            es.clean();

            es.addEvent("take the book", Utils.createTomorrow());
            es.addEvent("read the stuff", Utils.createTomorrow());
            es.addEvent("make a gun", Utils.createTomorrow());
            es.addEvent("conquer the globe", Utils.createTomorrow());

            es.addEvent("Hi, am a notifier that you set 15 seconds ago", Utils.createIn15Seconds());
        });

        RxView.clicks(findViewById(R.id.activity_main_goto_events_button)).subscribe(notification -> {
            startActivity(new Intent(getApplicationContext(), EventListActivity.class));
        });
    }
}
