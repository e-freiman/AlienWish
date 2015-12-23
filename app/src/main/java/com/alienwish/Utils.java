package com.alienwish;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.alienwish.impl.EventImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Freyman on 23.12.2015.
 */
public final class Utils {

    private Utils() {}

    public static Date createDate(int year, int month, int day) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(year - 1900, month, day);
        return new Date(cal.getTimeInMillis());
    }
}
