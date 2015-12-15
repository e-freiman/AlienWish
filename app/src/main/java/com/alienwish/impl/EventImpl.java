package com.alienwish.impl;

import com.alienwish.Event;

import java.util.Date;

/**
 * Created by Freyman on 15.12.2015.
 */
public final class EventImpl implements Event {

    private String mText;
    private Date mCreatedAt, mAlertAt;
    private long mId;

    EventImpl(long id, String text, Date createdAt, Date alertAt) {
        mAlertAt = alertAt;
        mCreatedAt = createdAt;
        mText = text;
        mId = id;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public Date getCreationDate() {
        return mCreatedAt;
    }

    @Override
    public Date getAlertDate() {
        return mAlertAt;
    }

    @Override
    public long getId() {
        return mId;
    }
}
