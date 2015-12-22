package com.alienwish;

import android.database.Cursor;

import java.util.Date;
import java.util.List;

import rx.Observable;

/**
 * Created by Freyman on 15.12.2015.
 */
public interface EventStorage {
    void clean();
    long addEvent(String text, Date alertAt);
    boolean removeEvent(long id);
    Observable<List<Event>> getObservableEvents();
    Cursor getCursorEvents();
}
