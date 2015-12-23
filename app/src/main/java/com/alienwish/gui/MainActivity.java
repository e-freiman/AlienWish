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

        RxView.clicks(findViewById(R.id.rxtest_button)).subscribe(notification -> {
            Observable.just("Hello, world!")
                    .map(s -> s + " -ABC")
                    .subscribe(s -> Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show());
        });

        RxView.clicks(findViewById(R.id.dbtest_button)).subscribe(notification -> {
            EventStorage es = Alien.getInstance().getEventStorage();

            es.clean();

            es.addEvent("take the book", Utils.createDate(2015, 12, 22));
            es.addEvent("read the stuff", Utils.createDate(2015, 12, 23));
            es.addEvent("make a gun", Utils.createDate(2015, 12, 24));
            es.addEvent("conquer the globe", Utils.createDate(2015, 12, 25));
        });

        RxView.clicks(findViewById(R.id.goto_events_button)).subscribe(notification -> {
            startActivity(new Intent(getApplicationContext(), EventListActivity.class));
        });
    }
}
