package com.alienwish;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by Freyman on 06.02.16.
 */

public final class App extends Application {

    public static final long CREATE_NEW_EVENT_ID = -1;

    private static App sInstance;

    private GuiStates mState = GuiStates.Events;
    private int mCurrentIndex;
    private long mCurrentId = CREATE_NEW_EVENT_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static App getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Application instance wasn't created yet");
        }
        return sInstance;
    }

    public GuiStates getState() {
        return mState;
    }

    public void setState(GuiStates state) {
        mState = state;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
    }

    public long getCurrentId() {
        return mCurrentId;
    }

    public void setCurrentId(long currentId) {
        mCurrentId = currentId;
    }
}
