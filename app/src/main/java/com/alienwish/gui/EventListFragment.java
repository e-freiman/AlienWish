package com.alienwish.gui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.alienwish.Alien;
import com.alienwish.Event;
import com.alienwish.EventStorage;
import com.alienwish.R;
import com.jakewharton.rxbinding.view.RxView;

import java.text.ParseException;
import java.util.Objects;

public class EventListFragment extends ListFragment {
    public static final int EVENT_ADDED_RESULT_CODE = 2;
    public static final int EVENT_DELETED_RESULT_CODE = 3;
    public static final int EVENT_CANCEL_RESULT_CODE = 4;
    public static final int EVENT_UPDATED_RESULT_CODE = 5;


    private static final String TAG = EventListFragment.class.getSimpleName();
    private static final String CURRENT_INDEX = "current_index";
    private static final String CURRENT_ID = "current_id";

    private int mCurrentIndex;
    private long mCurrentId;

    private EventCursorAdapter mAdapter;
    private EventStorage mEventStorage;

    {
        mCurrentId = EventDetailsFragment.CREATE_NEW_EVENT_ID;
    }

    private void updateListView() {
        mAdapter.changeCursor(mEventStorage.getCursorEvents());
    }

    public static EventListFragment newInstance() {
        EventListFragment f = new EventListFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurrentIndex = savedInstanceState.getInt(CURRENT_INDEX, 0);
            mCurrentId = savedInstanceState.getLong(CURRENT_ID, 0);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventStorage = Alien.getInstance().getEventStorage();
        mAdapter = new EventCursorAdapter(getActivity(), R.layout.event_list_row, mEventStorage.getCursorEvents(), 0);
        setListAdapter(mAdapter);

        View footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.event_list_footer, null, false);
        getListView().addFooterView(footerView);

        RxView.clicks(footerView.findViewById(R.id.buttonAdd)).subscribe(notification -> {
            EventDetailsFragment.show(getActivity(), this, EventDetailsFragment.CREATE_NEW_EVENT_ID);
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            EventDetailsFragment.show(getActivity(), this, mCurrentId);
        }
    }

    public static void show(Activity activity) {
        EventListFragment eventListFragment = EventListFragment.newInstance();
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_main_event_list, eventListFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_INDEX, mCurrentIndex);
        outState.putLong(CURRENT_ID, mCurrentId);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (!(v.getTag() instanceof Long)) {
            throw new IllegalStateException("The item view does not have a tag");
        }

        mCurrentId = (Long) v.getTag();
        mCurrentIndex = position;

        EventDetailsFragment.show(getActivity(), this, mCurrentId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EventDetailsFragment.SHOW_DETAILS_REQUEST_CODE) {
            switch (resultCode) {
                case EVENT_ADDED_RESULT_CODE:
                case EVENT_DELETED_RESULT_CODE:
                case EVENT_UPDATED_RESULT_CODE:
                    updateListView();
                    break;
                case EVENT_CANCEL_RESULT_CODE:
                    break;
                default:
            }
        } else {
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
