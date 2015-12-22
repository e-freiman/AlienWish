package com.alienwish;

/**
 * Created by Freyman on 22.12.2015.
 */

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

    public static void init(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Alien instance was already initialized");
        }
        sInstance = new Alien(context);
    }

    private Alien(Context context) {
        mEventStorage = new EventStorageImpl(context);
    }

    public  static Alien getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Alien instance wasn't initialized");
        }
        return sInstance;
    }

    public EventStorage getEventStorage() {
        return mEventStorage;
    }
}
