package com.alienwish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Freyman on 18.01.2016.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Alien.isInitialized()) {
            Alien.init(context);
        }
        Alien.getInstance().scheduleAllEvents();
    }
}
