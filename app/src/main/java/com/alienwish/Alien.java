package com.alienwish;

/**
 * Created by Freyman on 22.12.2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.alienwish.impl.EventStorageImpl;

/**
 * A core data storage. Represents the application itself or "The alien"
 * Instance of the class exists as long as apps is running and
 * doesn't depend on orientation, current top activity and etc.
 *
 * The class should be initialized by {@link Alien#init(Context)} method before any usage,
 * otherwise an {@link IllegalStateException} will be thrown
 * The instance accessible everywhere in the project through {@link Alien#getInstance()} method
 */
public final class Alien {

    private static Alien sInstance;
    private EventStorage mEventStorage;
    private Context mContext;

    public static void init(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Alien instance was already initialized");
        }
        sInstance = new Alien(context);
    }

    public static boolean isInitialized() {
        return sInstance != null;
    }

    private Alien(Context context) {
        mContext = context;
        mEventStorage = new EventStorageImpl(context);
    }

    public static Alien getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Alien instance wasn't initialized");
        }
        return sInstance;
    }

    public EventStorage getEventStorage() {
        return mEventStorage;
    }

    public boolean scheduleEvent(Event e) {

        if (e.getAlertDate().getTime() < System.currentTimeMillis()) {
            return false;
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                e.getAlertDate().getTime(),
                AlarmReceiver.eventToPendingIntent(mContext, e));

        return true;
    }

    public void cancelEvent(Event e) {
        PendingIntent pi = AlarmReceiver.eventToPendingIntent(mContext, e);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    /**
     * Schedule all events that are stored in the database which will happen in the future.
     */
    public void scheduleAllEvents() {
        mEventStorage.getEvents().subscribe(events -> {
            for (Event e : events) {
                scheduleEvent(e);
            }
        });
    }

    /**
     * Cancel all scheduled events that are stored in the database.
     */
    public void cancelAllEvents() {
        mEventStorage.getEvents().subscribe(events -> {
            for (Event e : events) {
                cancelEvent(e);
            }
        });
    }
}
