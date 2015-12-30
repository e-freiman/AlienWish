package com.alienwish.gui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alienwish.R;
import com.jakewharton.rxbinding.view.RxView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddEventFragment extends Fragment {

    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    private static final int RESULT_OK = 0;

    private static final String DATE_EXTRA = "date";
    private static final String TIME_EXTRA = "time";

    private static final String DATE_PICKER_TAG = "datePicker";
    private static final String TIME_PICKER_TAG = "timePicker";

    private EditText mDateEditText, mTimeEditText;
    private DateFormat mDateFormat, mTimeFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        mDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        mDateEditText = (EditText)view.findViewById(R.id.fragment_add_event_date);
        mTimeEditText = (EditText)view.findViewById(R.id.fragment_add_event_time);

        final EditText textEditText = (EditText)view.findViewById(R.id.fragment_add_event_text);

        RxView.clicks(view.findViewById(R.id.fragment_add_event_add_button)).subscribe(notification -> {
            if (!textEditText.getText().toString().isEmpty()) {
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.fragment_add_event_empty_event),
                        Toast.LENGTH_SHORT).show();
            }
        });

        RxView.clicks(mDateEditText).subscribe(notification -> {
            DialogFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setTargetFragment(this, DATE_REQUEST_CODE);
            dialogFragment.show(getFragmentManager(), DATE_PICKER_TAG);
        });

        RxView.clicks(mTimeEditText).subscribe(notification -> {
            DialogFragment dialogFragment = new TimePickerFragment();
            dialogFragment.setTargetFragment(this, TIME_REQUEST_CODE);
            dialogFragment.show(getFragmentManager(), TIME_PICKER_TAG);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DATE_REQUEST_CODE:
                    mDateEditText.setText(mDateFormat.format(data.getLongExtra(DATE_EXTRA, 0)));
                    break;
                case TIME_REQUEST_CODE:
                    mTimeEditText.setText(mTimeFormat.format(data.getLongExtra(TIME_EXTRA, 0)));
                    break;
                default:
                    throw new IllegalStateException("Unknown request cde");
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());
            cal.set(year, month, day);

            Intent intent = new Intent();
            intent.putExtra(DATE_EXTRA, cal.getTimeInMillis());
            getTargetFragment().onActivityResult(getTargetRequestCode() , RESULT_OK, intent);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            Intent intent = new Intent();
            intent.putExtra(TIME_EXTRA, cal.getTimeInMillis());
            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        }
    }
}