package com.ultimatesoftil.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.ultimatesoftil.trackeru.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Mike on 01/10/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("noti","on");
        RemoteViews contentView = null;
        PendingIntent contentIntent=null;
        if (Build.VERSION.SDK_INT <= 18) {
        contentView=    new RemoteViews(context.getPackageName(), R.layout.custom_notification2);
        }else {
         contentView=   new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        }
        boolean start=intent.getExtras().getBoolean("start",false);
        if(start){
            // continue sharing
            Log.d("start",String.valueOf(start));
            contentView.setImageViewResource(R.id.kill,android.R.drawable.ic_media_pause);
            contentView.setTextViewText(R.id.nfEventAddress,context.getResources().getString(R.string.service));
            contentView.setTextViewText(R.id.nfDecline,context.getResources().getString(R.string.stop));
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("share", "on").apply();

            Intent notificationIntent = new Intent(context, NotificationReceiver.class);

            notificationIntent.putExtra("start",false);
            contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            contentView.setOnClickPendingIntent(R.id.kill,contentIntent);


        }else {
            //stop sharing
            contentView.setTextViewText(R.id.nfDecline,context.getResources().getString(R.string.contin));
            contentView.setImageViewResource(R.id.kill,android.R.drawable.ic_media_play);
            contentView.setTextViewText(R.id.nfEventAddress,context.getResources().getString(R.string.stopped));


            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("share", "off").apply();
            Log.d("start",String.valueOf(start));
            Intent notificationIntent = new Intent(context, NotificationReceiver.class);

            notificationIntent.putExtra("start",true);

             contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            contentView.setOnClickPendingIntent(R.id.kill,contentIntent);

        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder=(NotificationCompat.Builder) new NotificationCompat.Builder(context);
        if (Build.VERSION.SDK_INT <= 18) {
            RemoteViews contentVie = new RemoteViews(context.getPackageName(), R.layout.custom_notification2);

            Notification notification = new Notification(R.mipmap.ic_launcher2,context. getResources().getString(R.string.lo), 1);

            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

            contentVie.setOnClickPendingIntent(R.id.kill,contentIntent);
            notification.contentView = contentVie;
            mNotificationManager.notify(1, notification);


        }else{


            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher2)

                    .setTicker(context.getResources().getString(R.string.lo))
                    .setChannelId("1")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setOngoing(true)

                    .setCustomBigContentView(contentView)

            ;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "fd";
            String description ="dfad";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            // NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


//// notificationId is a unique int for each notification that you must define

        }
        notificationManager.notify(1, mBuilder.build());
    }
}
