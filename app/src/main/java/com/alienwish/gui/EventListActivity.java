package com.alienwish.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

    public static final int ADD_EVENT_REQUEST_CODE = 1;
    public static final int EVENT_ADDED_RESULT_CODE = 2;
    private static final String TAG = EventListActivity.class.getSimpleName();

    EventCursorAdapter mAdapter;
    EventStorage mEventStorage;
    ListView mEventList;

    private void updateListView() {
        mAdapter.changeCursor(mEventStorage.getCursorEvents());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        mEventStorage = Alien.getInstance().getEventStorage();
        mEventList = (ListView) findViewById(R.id.activity_event_list_list);
        mAdapter = new EventCursorAdapter(this, R.layout.event_list_row, mEventStorage.getCursorEvents(), 0);
        mEventList.setAdapter(mAdapter);

        View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.event_list_footer, null, false);
        mEventList.addFooterView(footerView);
        mEventList.setOnItemClickListener((parent, view, position, id) -> {

            if (!(view.getTag() instanceof Long)) {
                throw new IllegalStateException("The item view does not have a tag");
            }

            final long record_id = (Long) view.getTag();

            mEventStorage.getEventById(record_id).subscribe(event -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(String.format(getResources().getString(R.string.activity_event_list_delete_pattern), event.getText()))
                        .setPositiveButton(getResources().getString(R.string.activity_event_list_delete_yes), (dialog, which) -> {
                            mEventStorage.removeEvent(id).subscribe(result -> {
                                if (result) {
                                    updateListView();
                                }
                            });
                        })
                        .setNegativeButton(getResources().getString(R.string.activity_event_list_delete_cancel), (dialog, which) -> {})
                        .show();
            });
        });

        RxView.clicks(footerView.findViewById(R.id.buttonAdd)).subscribe(notification -> {
            startActivityForResult(new Intent(this, AddEventActivity.class), ADD_EVENT_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_EVENT_REQUEST_CODE:
                if (resultCode == EVENT_ADDED_RESULT_CODE) {
                    updateListView();
                }
                break;
            default:
                throw new IllegalStateException("Unknown requestCode");
        }
    }

    static class EventCursorAdapter extends ResourceCursorAdapter {

        public EventCursorAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            try {
                Event event = Alien.getInstance().getEventStorage().cursorToEvent(cursor);

                TextView text = (TextView) view.findViewById(R.id.event_list_row_text);
                text.setText(event.getText());

                TextView alertAt = (TextView) view.findViewById(R.id.event_list_row_alertat);
                alertAt.setText(event.getAlertDate().toString());

                view.setTag(new Long(event.getId()));

            } catch (ParseException e) {
                Log.e(TAG, String.format("Can't parse database record with id = %d into event",
                        cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))));
            }
        }
    }
}
