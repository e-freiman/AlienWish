package com.alienwish.gui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.alienwish.Alien;
import com.alienwish.Event;
import com.alienwish.EventStorage;
import com.alienwish.R;
import com.jakewharton.rxbinding.view.RxView;

import java.text.ParseException;

public class EventListActivity extends AppCompatActivity {

    private static final String TAG = EventListActivity.class.getSimpleName();
    private static final int ADD_EVENT_REQUEST_CODE = 1;

    EventCursorAdapter mAdapter;
    EventStorage mEventStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        mEventStorage = Alien.getInstance().getEventStorage();
        mAdapter = new EventCursorAdapter(this, R.layout.event_list_row, mEventStorage.getCursorEvents(), 0);

        ListView eventList = (ListView) findViewById(R.id.activity_event_list_list);
        eventList.setAdapter(mAdapter);

        View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.event_list_footer, null, false);
        eventList.addFooterView(footerView);

        RxView.clicks(footerView.findViewById(R.id.buttonAdd)).subscribe(notification -> {
            startActivityForResult(new Intent(this, AddEventActivity.class), ADD_EVENT_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_EVENT_REQUEST_CODE:

                break;
            default:
                throw new IllegalStateException("Unknown requestCode");
        }
    }

    class EventCursorAdapter extends ResourceCursorAdapter {

        public EventCursorAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            try {
                Event event = mEventStorage.cursorToEvent(cursor);

                TextView text = (TextView) view.findViewById(R.id.event_list_row_text);
                text.setText(event.getText());

                TextView alertAt = (TextView) view.findViewById(R.id.event_list_row_alertat);
                alertAt.setText(event.getAlertDate().toString());

            } catch (ParseException e) {
                Log.e(TAG, String.format("Can't parse database record with id = %d into event",
                        cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))));
            }
        }
    }
}
