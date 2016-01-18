package com.alienwish.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.SyncStateContract;

import com.alienwish.Event;
import com.alienwish.EventStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * Created by Freyman on 15.12.2015.
 */
public class EventStorageImpl extends SQLiteOpenHelper implements EventStorage, BaseColumns {

    private static final String DATABASE_NAME = "alienwish.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EVENTS = "table_events";
    private static final String TABLE_EVENTS_TEXT = "text";
    private static final String TABLE_EVENTS_CREATED_AT = "created_at";
    private static final String TABLE_EVENTS_ALERT_AT = "alert_at";

    private static final String DROP_TABLE_EVENTS_SCRIPT = "DROP TABLE IF EXISTS " + TABLE_EVENTS;
    private static final String CREATE_TABLE_EVENTS_SCRIPT = "CREATE TABLE " + TABLE_EVENTS + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TABLE_EVENTS_TEXT + " text not null, "
            + TABLE_EVENTS_CREATED_AT + " text not null, "
            + TABLE_EVENTS_ALERT_AT + " text not null"
            + ");";

    private static final String ISO8601_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String ISO8601_TIMEZONE = "UTC";

    private DateFormat createISO8601DateFormat() {
        DateFormat df = new SimpleDateFormat(ISO8601_DATETIME_PATTERN, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone(ISO8601_TIMEZONE));
        return df;
    }

    public EventStorageImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(
                (subscriber) -> {
                    try {
                        subscriber.onNext(func.call());
                        subscriber.onCompleted();
                    } catch(Exception ex) {
                        subscriber.onError(ex);
                    }
                });
    }

    private void clean() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(DROP_TABLE_EVENTS_SCRIPT);
        onCreate(db);
    }

    private ContentValues parseInput(String text, Date alertAt) {
        DateFormat df = createISO8601DateFormat();
        String isoCreatedAt = df.format(new Date());
        String isoAlertAt = df.format(alertAt);

        ContentValues values = new ContentValues();
        values.put(TABLE_EVENTS_TEXT, text);
        values.put(TABLE_EVENTS_CREATED_AT, isoCreatedAt);
        values.put(TABLE_EVENTS_ALERT_AT, isoAlertAt);

        return values;
    }

    @Override
    public Observable<Event> updateEventById(long id, String text, Date alertAt) {
        return makeObservable(updateEventCallable(id, text, alertAt));
    }

    private Callable<Event> updateEventCallable(long id, String text, Date alertAt) {
        return () -> {
            SQLiteDatabase db = getReadableDatabase();
            int res = db.update(TABLE_EVENTS, parseInput(text, alertAt), _ID + "=?", new String[]{Long.toString(id)});

            if (res > 1) {
                throw new IllegalStateException("More than one record with id = " + Long.toString(id) + " were updated in " + TABLE_EVENTS);
            }

            return getCallableEventById(id).call();
        };
    }

    @Override
    public Observable<Event> addEvent(String text, Date alertAt) {
        return makeObservable(addEventCallable(text, alertAt));
    }

    private  Callable<Event> addEventCallable(String text, Date alertAt) {
        return () -> {
            long id = getReadableDatabase().insert(TABLE_EVENTS, null, parseInput(text, alertAt));
            if (id < 0) {
                throw new SQLException("An event with text '" + text + "' wasn't added into " + TABLE_EVENTS);
            }
            return getCallableEventById(id).call();
        };
    }

    private Callable<Event> removeEventByIdCallable(long id) {
        return () -> {

            Event deletedEvent = getCallableEventById(id).call();

            SQLiteDatabase db = getReadableDatabase();
            int res = db.delete(TABLE_EVENTS, BaseColumns._ID + "=?", new String[]{Long.toString(id)});

            if (res > 1) {
                throw new IllegalStateException("More than one record with id = " + Long.toString(id) + " were deleted from " + TABLE_EVENTS);
            }

            return deletedEvent;
        };
    }

    @Override
    public Observable<Event> removeEventById(long id) {
        return makeObservable(removeEventByIdCallable(id));
    }

    private Callable<List<Event>> getCallableEvents() {
        return () -> {
            Cursor cursor = getCursorEvents();
            List<Event> events = new LinkedList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                events.add(cursorToEvent(cursor));
                cursor.moveToNext();
            }
            return events;
        };
    }

    private Callable<Event> getCallableEventById(long id) {
        return () -> {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLE_EVENTS,
                    new String[]{BaseColumns._ID, TABLE_EVENTS_CREATED_AT, TABLE_EVENTS_ALERT_AT, TABLE_EVENTS_TEXT},
                    BaseColumns._ID + "=" + Long.toString(id),
                    null,
                    null,
                    null,
                    TABLE_EVENTS_ALERT_AT,
                    null);

            if (cursor.getCount() > 1) {
                throw new IllegalStateException("More than one record with id = " + Long.toString(id) + " in " + TABLE_EVENTS);
            }

            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursorToEvent(cursor);
        };
    }

    @Override
    public Event cursorToEvent(Cursor cursor) throws ParseException {
        DateFormat df = createISO8601DateFormat();
        long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        Date createdAt = df.parse(cursor.getString(cursor.getColumnIndex(TABLE_EVENTS_CREATED_AT)));
        Date alertAt = df.parse(cursor.getString(cursor.getColumnIndex(TABLE_EVENTS_ALERT_AT)));
        String text = cursor.getString(cursor.getColumnIndex(TABLE_EVENTS_TEXT));

        EventImpl event = new EventImpl(id, text, createdAt, alertAt);
        return event;
    }

    @Override
    public Observable<List<Event>> getEvents() {
        return makeObservable(getCallableEvents());
    }

    @Override
    public Cursor getCursorEvents() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS,
                new String[]{BaseColumns._ID, TABLE_EVENTS_CREATED_AT, TABLE_EVENTS_ALERT_AT, TABLE_EVENTS_TEXT},
                null,
                null,
                null,
                null,
                TABLE_EVENTS_ALERT_AT,
                null);
        return cursor;
    }

    @Override
    public Observable<Event> getEventById(long id) {
        return makeObservable(getCallableEventById(id));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENTS_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clean();
    }
}
