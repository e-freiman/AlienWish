package com.alienwish;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.alienwish.gui.EventDetailsFragment;
import com.alienwish.gui.EventListFragment;
import com.alienwish.gui.MainActivity;

/**
 * Created by Freyman on 06.01.16.
 */
public class AlarmReceiver  extends BroadcastReceiver {

    private static final String SCHEME = "alien_wish";
    private static final String ID = "event_id";
    private static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String param = intent.getData().getQueryParameter(ID);
        long id = Long.parseLong(param);

        if(!Alien.isInitialized()) {
            Alien.init(context);
        }

        Alien.getInstance().getEventStorage().getEventById(id).subscribe(event -> {
            if (event != null) {

                Intent i = new Intent(context, MainActivity.class);

                App.getInstance().setState(GuiStates.Details);
                App.getInstance().setCurrentId(event.getId());

                PendingIntent pi = PendingIntent.getActivity(context,
                        MainActivity.NOTIFIER_CLICKED_REQUEST_CODE,
                        i,
                        0,
                        null);

                Notification.Builder builder =
                        new Notification.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(context.getResources().getString(R.string.alarm_receiver_notification_title))
                                .setContentText(event.getText().toString())
                                .setContentIntent(pi);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int)event.getId(), builder.build());
            }
        });
    }

    public static PendingIntent eventToPendingIntent(Context context, Event event) {

        Uri.Builder uri = new Uri.Builder();
        uri.scheme(SCHEME).appendQueryParameter(ID, Long.toString(event.getId()));

        Intent i = new Intent(context, AlarmReceiver.class);
        i.setData(uri.build());
        return PendingIntent.getBroadcast(context, REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
