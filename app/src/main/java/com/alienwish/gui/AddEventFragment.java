package com.alienwish.gui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.alienwish.R;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Calendar;

public class AddEventFragment extends Fragment {

    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        RxView.clicks(view.findViewById(R.id.fragment_add_event_add_button)).subscribe(notification -> {
            getActivity().finish();
        });

        RxView.clicks(view.findViewById(R.id.fragment_add_event_date)).subscribe(notification -> {
            //startActivityForResult(DatetimePickerActivity.createPickDataIntent(this), DATETIME_REQUEST_CODE);
        });

        RxView.clicks(view.findViewById(R.id.fragment_add_event_time)).subscribe(notification -> {
            DialogFragment dialogFragment = new TimePickerFragment();
            //dialogFragment.setTargetFragment(this, TIME_REQUEST_CODE);
            dialogFragment.show(getFragmentManager(), "timePicker");
        });

        return view;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }
}