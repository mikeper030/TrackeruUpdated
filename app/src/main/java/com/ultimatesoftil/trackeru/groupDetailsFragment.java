package com.ultimatesoftil.trackeru;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.ultimatesoftil.adapters.UsersListAdapter;
import com.ultimatesoftil.models.User;


/**
 * Created by mike on 9 Dec 2017.
 */

public class groupDetailsFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;

    private String userID;
    private DatabaseReference myRef;
    private ImageView menuu;
    private FirebaseAuth auth;
    private String key2;
    private FloatingActionMenu menuRed;
    ListView listView;
    String key = "empty";
    UsersListAdapter usersListAdapter;
    int deleteposition = -1;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private TextView statics;
    private AdView mAdView;
    private int counter=0;
    boolean init=true;
    ArrayList<User> circlesinfo = new ArrayList<User>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle =getArguments();
        if(bundle!=null){
            key2=bundle.getString("key");
            statics.setVisibility(View.INVISIBLE);
         Log.d("bundle",key2);
        //key value is passed from circlesfragment onclick

            myRef.child("public").child("circles").child(key2).child("users").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    getupdates(dataSnapshot);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    User user=dataSnapshot.getValue(User.class);
                    for(int i=0;i<circlesinfo.size();i++){
                        if(user.getUsername().equals(circlesinfo.get(i).getUsername())){
                            circlesinfo.set(i,user);
                            usersListAdapter.notifyDataSetChanged();

                        }
                    }
                    Collections.sort(circlesinfo, new Comparator<User>() {
                        @Override
                        public int compare(User s1, User s2) {
                            return s1.getUsername().compareToIgnoreCase(s2.getUsername());
                        }
                    });
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    for(int i=0;i<circlesinfo.size();i++){
                        if(user.getUsername().equals(circlesinfo.get(i).getUsername())){
                            circlesinfo.remove(i);
                            usersListAdapter.notifyDataSetChanged();

                        }
                    }
                    Collections.sort(circlesinfo, new Comparator<User>() {
                        @Override
                        public int compare(User s1, User s2) {
                            return s1.getUsername().compareToIgnoreCase(s2.getUsername());
                        }
                    });

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }else {
           // Toast.makeText(getActivity(),"Error please try again later",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), login.class));

        } else {
            //fetch list of groups from database
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            listView = getView().findViewById(R.id.ListView2);
            statics = (TextView)getView().findViewById(R.id.stat);

            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            boolean showads = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
            if(showads) {
                mAdView = getView().findViewById(R.id.group_banner);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }

            listView.setAdapter((ListAdapter) usersListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                }
            });


            //====================================== database real time listeners======================================================

        }
    }




    //=========================================================================
    // }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view=null;
        boolean showads = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
        if(showads) {
            view = inflater.inflate(R.layout.groupdetails_fragment_ads, container, false);
        }else {
            view = inflater.inflate(R.layout.groupdetails_fragment, container, false);
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();

       // handle back click
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("df", "keyCode: " + keyCode);
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i("df", "onKey Back listener is working!!!");
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_manu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Intent i = new Intent(getActivity(), settings.class);
                startActivity(i);
                // Not implemented here
                return true;


            default:
                break;
        }

        return true;
    }


    //==============loading users data  from firebase public directory===============================


    public void getupdates(DataSnapshot dataSnapshot) {
      Log.d("added","new child");
        User user=dataSnapshot.getValue(User.class);
            for(int i=0;i<circlesinfo.size();i++){
                if(user.getUsername().equals(circlesinfo.get(i).getUsername())){
                   circlesinfo.remove(i);
                    usersListAdapter = new UsersListAdapter(getActivity(), circlesinfo,key2);
                    listView.setAdapter((ListAdapter) usersListAdapter);
                   return;
                }

            }
        circlesinfo.add(user);




        if (circlesinfo.size() > 0) {
            Collections.sort(circlesinfo, new Comparator<User>() {
                @Override
                public int compare(User s1, User s2) {
                    return s1.getUsername().compareToIgnoreCase(s2.getUsername());
                }
            });
            usersListAdapter = new UsersListAdapter(getActivity(), circlesinfo,key2);
            listView.setAdapter((ListAdapter) usersListAdapter);
           // listView.invalidateViews();
        } else {
            Toast.makeText(getActivity(), R.string.no_data, Toast.LENGTH_SHORT).show();
        }


    }


}
