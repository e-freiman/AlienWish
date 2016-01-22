package com.alienwish.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventDetailsFragment extends Fragment {

    public static final String EVENT_ID_EXTRA = "event_id";
    public static final long CREATE_NEW_EVENT_ID = -1;

    public static final int SHOW_DETAILS_REQUEST_CODE = 2;

    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    private static final int RESULT_OK = 0;

    private static final String DESCRIPTION_TEXT_EXTRA = "description_text";
    private static final String DATE_TEXT_EXTRA = "date_text";
    private static final String TIME_TEXT_EXTRA = "time_text";

    private static final String DATE_EXTRA = "date";
    private static final String TIME_EXTRA = "time";

    private static final int NULL = 0;

    private static final String DATE_PICKER_TAG = "datePicker";
    private static final String TIME_PICKER_TAG = "timePicker";

    private static final String UTC_TIMEZONE = "UTC";

    private EditText mDateEditText, mTimeEditText, mDescriptionEditText;
    private DateFormat mDateFormat, mTimeFormat;
    private Date mPickedDate, mPickedTime;
    private Event mEvent;

    private Calendar mCalendarUTC = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));

    public static void show(Activity activity, long id) {

        // Check to see if we have a frame in which to embed the details fragment directly in the containing UI
        View detailsFrame = activity.findViewById(R.id.activity_main_event_details);
        boolean dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        int frameId = dualPane ? R.id.activity_main_event_details : R.id.activity_main_event_list;

        // Creating and showing EventDetailsFragment if necessary
        Object someFragment = activity.getFragmentManager().findFragmentById(frameId);
        EventDetailsFragment eventDetailsFragment = null;

        if (someFragment instanceof EventDetailsFragment) {
            eventDetailsFragment = (EventDetailsFragment) someFragment;
        }

        if (eventDetailsFragment == null ||
                eventDetailsFragment.getIdExtra() != id ||
                eventDetailsFragment.getIdExtra() == CREATE_NEW_EVENT_ID) {

            eventDetailsFragment = new EventDetailsFragment();
            Bundle args = new Bundle();
            args.putLong(EVENT_ID_EXTRA, id);
            eventDetailsFragment.setArguments(args);

            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.replace(frameId, eventDetailsFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

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

    public long getIdExtra() {

        if (!getArguments().containsKey(EVENT_ID_EXTRA)) {
            throw new IllegalStateException("Fragment doesn't have an argument");
        }

        return getArguments().getLong(EVENT_ID_EXTRA, 0);
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

        if (mPickedDate == null) {
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

        eventRecord.alertAt = new Date(mPickedDate.getTime() + mPickedTime.getTime());

        return eventRecord;
    }

    private void finish(int resultCode) {
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

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

        if (getIdExtra() == CREATE_NEW_EVENT_ID) {
            mEvent = null;
            mPickedDate = null;
            mPickedTime = null;

            if (savedInstanceState == null) {
                mDescriptionEditText.setHint(R.string.fragment_event_details_enter_description_here);
                mDateEditText.setHint(R.string.fragment_event_details_enter_date_here);
                mTimeEditText.setHint(R.string.fragment_event_details_enter_time_here);
            } else {
                mPickedDate = longToDate(savedInstanceState.getLong(DATE_EXTRA, NULL));
                mPickedTime = longToDate(savedInstanceState.getLong(TIME_EXTRA, NULL));

                String str = savedInstanceState.getString(DESCRIPTION_TEXT_EXTRA);

                mDescriptionEditText.setText(str);
                mDateEditText.setText(savedInstanceState.getString(DATE_TEXT_EXTRA));
                mTimeEditText.setText(savedInstanceState.getString(TIME_TEXT_EXTRA));
            }
        } else {
            Alien.getInstance().getEventStorage().getEventById(getIdExtra()).subscribe(event -> {
                mEvent = event;
                mDescriptionEditText.setText(event.getText());
                mDateEditText.setText(mDateFormat.format(event.getAlertDate()));
                mTimeEditText.setText(mTimeFormat.format(event.getAlertDate()));
                mPickedDate = getDateOnly(event.getAlertDate());
                mPickedTime = getTimeOnly(event.getAlertDate());
            });
        }

        Button addButton = (Button)view.findViewById(R.id.fragment_event_details_add_button);
        Button deleteButton = (Button)view.findViewById(R.id.fragment_event_details_delete_button);

        final Alien alien = Alien.getInstance();
        final EventStorage eventStorage = alien.getEventStorage();

        if (getIdExtra() == CREATE_NEW_EVENT_ID) {
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
        if (mPickedDate != null) {
            Bundle args = new Bundle();
            args.putLong(DATE_EXTRA, mPickedDate.getTime());
            dialogFragment.setArguments(args);
        }
        dialogFragment.setTargetFragment(this, DATE_REQUEST_CODE);
        dialogFragment.show(getFragmentManager(), DATE_PICKER_TAG);
    }

    private void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        if (mPickedTime != null) {
            Bundle args = new Bundle();
            args.putLong(TIME_EXTRA, mPickedTime.getTime());
            dialogFragment.setArguments(args);
        }
        dialogFragment.setTargetFragment(this, TIME_REQUEST_CODE);
        dialogFragment.show(getFragmentManager(), TIME_PICKER_TAG);
    }

    private long dateToLong(Date date) {
        if (date == null) {
            return NULL;
        } else {
            return date.getTime();
        }
    }

    private Date longToDate(long millis) {
        if (millis == NULL) {
            return null;
        } else {
            return new Date(millis);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DESCRIPTION_TEXT_EXTRA, mDescriptionEditText.getText().toString());
        outState.putString(DATE_TEXT_EXTRA, mDateEditText.getText().toString());
        outState.putString(TIME_TEXT_EXTRA, mDateEditText.getText().toString());
        outState.putLong(DATE_EXTRA, dateToLong(mPickedDate));
        outState.putLong(TIME_EXTRA, dateToLong(mPickedTime));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DATE_REQUEST_CODE:
                    mDateEditText.setText(mDateFormat.format(data.getLongExtra(DATE_EXTRA, 0)));
                    mPickedDate = getDateOnly(new Date(data.getLongExtra(DATE_EXTRA, 0)));
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
            if (getArguments() != null && getArguments().containsKey(DATE_EXTRA)) {
                c.setTime(new Date(getArguments().getLong(DATE_EXTRA)));
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
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
            if (getArguments() != null && getArguments().containsKey(TIME_EXTRA)) {
                c.setTime(new Date(getArguments().getLong(TIME_EXTRA)));
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar cal = Calendar.getInstance();
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