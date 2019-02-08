package com.ultimatesoftil.trackeru;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ultimatesoftil.trackeru.MapFragment.username;


/**
 * Created by Mike on 26/09/2018.
 */

public class ConnectionReceiver extends BroadcastReceiver {
    private ConnectivityReceiverListener mConnectivityReceiverListener;
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    ConnectionReceiver(ConnectivityReceiverListener listener, FirebaseDatabase database) {
        mConnectivityReceiverListener = listener;
       this.db=database;
     try {
         myRef=database.getReference();
     }catch (Exception e){
         e.printStackTrace();
     }

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (mConnectivityReceiverListener != null) {
            mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context));
        } else {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            try {
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                db.goOnline();
            } else {


                myRef.child("public").child("UsersLocation").child(username).child("online").onDisconnect().setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("service", "disconnected from server");
                    }
                });

                db.goOffline();
            }
                } catch (Exception e) {
                    e.printStackTrace();
                }


        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        try {
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
           db.goOnline();
        }else {


            myRef.child("public").child("UsersLocation").child(username).child("online").onDisconnect().setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("service", "disconnected from server");
                }
            });

            db.goOffline();
        }
          }catch (Exception e){
              e.printStackTrace();
          }

        Log.i("status","connected"+String.valueOf(activeNetwork != null && activeNetwork.isConnectedOrConnecting()));
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
