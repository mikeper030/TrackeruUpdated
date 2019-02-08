package com.ultimatesoftil.models;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ultimatesoftil.trackeru.MainActivity;
import com.ultimatesoftil.trackeru.R;

/**
 * Created by Mike on 23/09/2018.
 */

public class GPSservice extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient mFusedLocationClient;
    private static final String LOGSERVICE = "#######";
    private static final String MyOnClick = "myOnClickTag";
    private String userID;
    private DatabaseReference myRef;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private Intent ints;
    private  LocationCallback mLocationCallback;



    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        initLocationRequest();
        Log.i(LOGSERVICE, "onCreate");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ints = intent;
        if (intent != null && intent.getAction() != null && intent.getAction().equals("STOP")) {
            Log.d("stopped", "ss");
            stopSelf();
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            try {
                myRef.child("public").child("UsersLocation").child(ints.getStringExtra("username")).child("online").setValue(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return START_NOT_STICKY;
        }
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, "01")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.service))
                    .setAutoCancel(false)
                    .setOngoing(true);

            Notification notification = builder.build();
            startForeground(2, notification);

//
       } else {
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText(getResources().getString(R.string.service))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setOngoing(true)
//                    .setAutoCancel(false);
//
//
//            Notification notification = builder.build();
//
//            startForeground(2, notification);
       }

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        Intent notificationIntent = new Intent(getApplicationContext(), getClass());

//**add this line**
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setAction("STOP");
        try {
            notificationIntent.putExtra("username", ints.getStringExtra("username"));
        } catch (Exception e) {
            e.printStackTrace();
        }

//**edit this line to put requestID as requestCode**
        PendingIntent contentIntent = PendingIntent.getService(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        contentView.setOnClickPendingIntent(R.id.kill, contentIntent);

        Notification.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent app = new Intent(this, MainActivity.class);

        NotificationCompat.Builder mBuilder=(NotificationCompat.Builder) new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT <= 18) {
            NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getActivity(this, 0, app, 0);

            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher2)
                    .setCustomBigContentView(contentView)
                    .setTicker(getResources().getString(R.string.lo))
                    .setContentIntent(snoozePendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setOngoing(true)
                    ;


            Notification notification=mBuilder.build();

            startForeground(1,notification);

            return START_NOT_STICKY;

    }else{
            PendingIntent snoozePendingIntent =
                    PendingIntent.getActivity(this, 0, app, 0);

            mBuilder = new NotificationCompat.Builder(this,"1")
                    .setSmallIcon(R.mipmap.ic_launcher2)
                    .setAutoCancel(true)
                    .setTicker(getResources().getString(R.string.lo))
                    .setChannelId("2")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setContentIntent(snoozePendingIntent)
                    //.setCustomBigContentView(contentView)
                   .setContentTitle(getResources().getString(R.string.app_name))
                   .setContentText(getResources().getString(R.string.service))
                   .addAction(android.R.drawable.ic_media_pause,getResources().getString(R.string.stop),contentIntent)
            ;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "fdD";
            String description ="dfadD";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
           // NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


//// notificationId is a unique int for each notification that you must define

        }
        Notification notification=mBuilder.build();
        notification.flags= Notification.FLAG_ONGOING_EVENT;;
        startForeground(2, mBuilder.build());
        return START_NOT_STICKY;

    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    //Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    LocationModel lm = new LocationModel();
                    lm.setLat(location.getLatitude());
                    lm.setLongt(location.getLongitude());
                    lm.setOnline(true);
                    String url=null;
                    String username=null;
                    username=ints.getStringExtra("username");
                    url=ints.getStringExtra("url");
                    if(url!=null){
                        lm.setUrl(url);
                    }
                    try {
                       myRef.child("public").child("UsersLocation").child(username).setValue(lm);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Place current location marker
                    final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("location service",String.valueOf(location.getLatitude()+" "+ location.getLongitude()));
                }
            }
        };
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOGSERVICE, "onConnectionSuspended " + i);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
      try {
          stopForeground(true);
          NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
          notificationManager.cancel(1);
          Log.i(LOGSERVICE, "onDestroy - Estou sendo destruido ");
          mFusedLocationClient.removeLocationUpdates(mLocationCallback);
      }catch (Exception e){
          e.printStackTrace();
      }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOGSERVICE, "onConnectionFailed ");

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }


}
