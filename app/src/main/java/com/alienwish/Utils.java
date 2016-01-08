package com.alienwish;

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
        cal.set(year, month, day);
        return new Date(cal.getTimeInMillis());
    }

    public static Date createTomorrow() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return new Date(cal.getTimeInMillis());
    }

    public static Date createIn15Seconds() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.SECOND, 15);
        return new Date(cal.getTimeInMillis());
    }
}
