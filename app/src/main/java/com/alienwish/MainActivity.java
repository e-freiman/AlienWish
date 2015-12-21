package com.alienwish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alienwish.impl.EventStorageImpl;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import rx.Observable;

public class MainActivity extends AppCompatActivity {

    private Date createDate(int year, int month, int day) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(year - 1900, month, day);
        return new Date(cal.getTimeInMillis());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxView.clicks(findViewById(R.id.rxtest_button)).subscribe(notification -> {
            Observable.just("Hello, world!")
                    .map(s -> s + " -ABC")
                    .subscribe(s -> Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show());
        });

        RxView.clicks(findViewById(R.id.dbtest_button)).subscribe(
            notification -> {
                EventStorage es = new EventStorageImpl(getApplicationContext());
                es.clean();

                es.addEvent("take the book", createDate(2015, 12, 22));
                es.addEvent("read the stuff", createDate(2015, 12, 23));
                es.addEvent("make a gun", createDate(2015, 12, 24));
                es.addEvent("conquer the globe", createDate(2015, 12, 25));
            }
        );
    }
}
