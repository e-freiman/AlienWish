package com.alienwish.gui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alienwish.Event;
import com.alienwish.EventStorage;
import com.alienwish.R;
import com.alienwish.Alien;
import com.jakewharton.rxbinding.view.RxView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventDetailsFragment extends Fragment {

    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    private static final int RESULT_OK = 0;

    private static final String DATE_EXTRA = "date";
    private static final String TIME_EXTRA = "time";

    private static final String DATE_PICKER_TAG = "datePicker";
    private static final String TIME_PICKER_TAG = "timePicker";

    private static final String UTC_TIMEZONE = "UTC";

    private EditText mDateEditText, mTimeEditText, mDescriptionEditText;
    private DateFormat mDateFormat, mTimeFormat;
    private Date mPickedData, mPickedTime;
    private Event mEvent;

    private Calendar mCalendarUTC = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));

    private Date getDateOnly(Date utcDatetime) {
        mCalendarUTC.setTime(utcDatetime);
        mCalendarUTC.set(Calendar.HOUR_OF_DAY, 0);
        mCalendarUTC.set(Calendar.MINUTE, 0);
        mCalendarUTC.set(Calendar.SECOND, 0);
        mCalendarUTC.set(Calendar.MILLISECOND, 0);
        return new Date(mCalendarUTC.getTimeInMillis());
    }

    private Date getTimeOnly(Date utcDatetime) {
        mCalendarUTC.setTime(utcDatetime);
        mCalendarUTC.set(Calendar.YEAR, 1970);
        mCalendarUTC.set(Calendar.DAY_OF_YEAR, 1);
        return new Date(mCalendarUTC.getTimeInMillis());
    }

    public static EventDetailsFragment newInstance(long eventId) {
        EventDetailsFragment f = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(EventListFragment.EVENT_ID_EXTRA, eventId);
        f.setArguments(args);
        return f;
    }

    public long getIdExtra() {

        if (!getArguments().containsKey(EventListFragment.EVENT_ID_EXTRA)) {
            throw new IllegalStateException("Fragment doesn't have an argument");
        }

        return getArguments().getLong(EventListFragment.EVENT_ID_EXTRA, 0);
    }

    private EventRecord parseInput() {
        EventRecord eventRecord = new EventRecord();

        if (mDescriptionEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(),
                    getResources().getText(R.string.fragment_event_details_empty_event),
                    Toast.LENGTH_SHORT).show();

            return null;
        }

        eventRecord.text = mDescriptionEditText.getText().toString();

        if (mPickedData == null) {
            Toast.makeText(getActivity(),
                    getResources().getText(R.string.fragment_event_details_wrong_date),
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        if (mPickedTime == null) {
            Toast.makeText(getActivity(),
                    getResources().getText(R.string.fragment_event_details_wrong_time),
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        eventRecord.alertAt = new Date(mPickedData.getTime() + mPickedTime.getTime());

        return eventRecord;
    }

    private void finish(int resultCode) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, null);
        } else {
            getActivity().setResult(resultCode);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return  null;
        }

        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        mDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        mDescriptionEditText = (EditText)view.findViewById(R.id.fragment_event_details_text);
        mDateEditText = (EditText)view.findViewById(R.id.fragment_event_details_date);
        mTimeEditText = (EditText)view.findViewById(R.id.fragment_event_details_time);


        if (getIdExtra() != EventListFragment.CREATE_NEW_EVENT_ID) {
            Alien.getInstance().getEventStorage().getEventById(getIdExtra()).subscribe(event -> {
                mEvent = event;
                mDescriptionEditText.setText(event.getText());
                mDateEditText.setText(mDateFormat.format(event.getAlertDate()));
                mTimeEditText.setText(mTimeFormat.format(event.getAlertDate()));
                mPickedData = getDateOnly(event.getAlertDate());
                mPickedTime = getTimeOnly(event.getAlertDate());
            });
        } else {
            mEvent = null;
            mPickedData = null;
            mPickedTime = null;
            mDescriptionEditText.setHint(R.string.fragment_event_details_enter_description_here);
            mDateEditText.setHint(R.string.fragment_event_details_enter_date_here);
            mTimeEditText.setHint(R.string.fragment_event_details_enter_time_here);
        }

        Button addButton = (Button)view.findViewById(R.id.fragment_event_details_add_button);
        Button deleteButton = (Button)view.findViewById(R.id.fragment_event_details_delete_button);

        final Alien alien = Alien.getInstance();
        final EventStorage eventStorage = alien.getEventStorage();

        if (getIdExtra() == EventListFragment.CREATE_NEW_EVENT_ID) {
            RxView.clicks(addButton).subscribe(notification -> {
                //Add an event
                EventRecord record = parseInput();
                if (record != null) {
                    eventStorage.addEvent(record.text, record.alertAt).subscribe(event -> {
                        alien.scheduleEvent(event);
                        finish(EventListFragment.EVENT_ADDED_RESULT_CODE);
                    });
                }
            });

            RxView.clicks(deleteButton).subscribe(notification -> {
                //Exit without saving changes
                finish(EventListFragment.EVENT_CANCEL_RESULT_CODE);
            });

        } else {
            addButton.setText(getResources().getText(R.string.fragment_event_details_save_button));
            RxView.clicks(addButton).subscribe(notification -> {
                //Save changes in existing event and exit
                EventRecord record = parseInput();
                eventStorage.updateEventById(mEvent.getId(), record.text, record.alertAt).subscribe(event -> {
                    alien.scheduleEvent(event);
                    finish(EventListFragment.EVENT_UPDATED_RESULT_CODE);
                });
            });

            deleteButton.setText(getResources().getText(R.string.fragment_event_details_delete_button));
            RxView.clicks(deleteButton).subscribe(notification -> {
                //Delete an event
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.activity_event_list_delete_question))
                        .setPositiveButton(getResources().getString(R.string.activity_event_list_delete_yes), (dialog, which) -> {
                            eventStorage.removeEventById(getIdExtra()).subscribe(deletedEvent -> {
                                if (deletedEvent != null) {
                                    alien.cancelEvent(deletedEvent);
                                    finish(EventListFragment.EVENT_DELETED_RESULT_CODE);
                                } else {
                                    Toast.makeText(getActivity(),
                                            getResources().getText(R.string.fragment_event_details_deleting_failed),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton(getResources().getString(R.string.activity_event_list_delete_cancel), (dialog, which) -> {
                        })
                        .show();
            });
        }

        RxView.clicks(mDateEditText).subscribe(notifier -> {
            showDatePickerDialog();
        });

        RxView.clicks(mTimeEditText).subscribe(notifier -> {
            showTimePickerDialog();
        });
        return view;
    }

    private void showDatePickerDialog() {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.setTargetFragment(this, DATE_REQUEST_CODE);
        dialogFragment.show(getFragmentManager(), DATE_PICKER_TAG);
    }

    private void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.setTargetFragment(this, TIME_REQUEST_CODE);
        dialogFragment.show(getFragmentManager(), TIME_PICKER_TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DATE_REQUEST_CODE:
                    mDateEditText.setText(mDateFormat.format(data.getLongExtra(DATE_EXTRA, 0)));
                    mPickedData = getDateOnly(new Date(data.getLongExtra(DATE_EXTRA, 0)));
                    break;
                case TIME_REQUEST_CODE:
                    mTimeEditText.setText(mTimeFormat.format(data.getLongExtra(TIME_EXTRA, 0)));
                    mPickedTime = getTimeOnly(new Date(data.getLongExtra(TIME_EXTRA, 0)));
                    break;
                default:
                    throw new IllegalStateException("Unknown request code");
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

    private static final class EventRecord {
        String text;
        Date alertAt;
    }
}