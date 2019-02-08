package com.ultimatesoftil.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.ultimatesoftil.models.User;
import com.ultimatesoftil.models.UserCircleModel;
import com.ultimatesoftil.trackeru.MainActivity;
import com.ultimatesoftil.trackeru.MapFragment;
import com.ultimatesoftil.trackeru.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ultimatesoftil.models.LocationModel;

import static com.ultimatesoftil.trackeru.MapFragment.markers;

/**
 * Created by mike on 24 Dec 2017.
 */

public class DrawerAdapter extends BaseAdapter{
    private String tag ="adapter";
    Context c;
    public static int position = -1;
    public static String key=null;
    ArrayList<UserCircleModel> circlesnames;
    LayoutInflater inflater;
    String username;
    ArrayList<String> users= new ArrayList<>();
    public static LinkedHashMap <String,ChildEventListener>  groupsUsersListener= new LinkedHashMap<>();
    private static LinkedHashMap<String,LinkedHashMap<String,ValueEventListener>> userLocationListeners=new LinkedHashMap<>();
                                                      //group//user//locatrion  listener
    public DrawerAdapter(Context c, ArrayList<UserCircleModel>  circlesnames) {
        this.c = c;
        username = PreferenceManager.getDefaultSharedPreferences(c).getString("username", null);

        this.circlesnames = circlesnames;


    }


    @Override
    public int getCount() {
        return circlesnames.size();
    }

    @Override
    public Object getItem(int i) {
        return circlesnames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup viewGroup) {

        if (inflater == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.drawer_item, viewGroup, false);

        }
        final int[] dbcount = new int[1];
        MainActivity.dbr.child("public").child("circles").child(circlesnames.get(position).getKey()).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    dbcount[0]++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final MyHolder3 holder = new MyHolder3(convertview);
        holder.groupname.setText(circlesnames.get(position).getName());

        // will get  each  user info from database per clicked circle
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] counter = {1};

                //get users of group
                if(holder.check.isChecked()){
                    // fetch list of circles than
                   // each row of circles will set listener on users real time location

                  final ChildEventListener grplistener= new ChildEventListener() {
                      @Override
                      public void onChildAdded(final DataSnapshot snapshot, String s) {
                          Log.d("counter", String.valueOf(dbcount[0] + " " + counter[0]));
                          if (!snapshot.getKey().equals(username)) {
                              //will get list of relevant users of cycle
                              //new user just add to the group
                              Toast.makeText(c,"User "+snapshot.getKey()+" Has joined the group",Toast.LENGTH_SHORT).show();
                              Collections.sort(users, new Comparator<String>() {
                                  @Override
                                  public int compare(String s1, String s2) {
                                      return s1.compareToIgnoreCase(s2);
                                  }
                              });
                              users.add(snapshot.getKey().toString());
                              Collections.sort(users, new Comparator<String>() {
                                  @Override
                                  public int compare(String s1, String s2) {
                                      return s1.compareToIgnoreCase(s2);
                                  }
                              });


                          } else {
                              users.add(username);
                              Collections.sort(users, new Comparator<String>() {
                                  @Override
                                  public int compare(String s1, String s2) {
                                      return s1.compareToIgnoreCase(s2);
                                  }
                              });

                          }

                          //finished loading group users now add locations listeners
                          Log.d(tag, users.toString());



                          final LocationModel lm = new LocationModel();
                          //adding real time location listener for each user in arraylist

                          ValueEventListener locations = new ValueEventListener() {
                              @Override
                              public void onDataChange(DataSnapshot dataSnapshot) {
                                  try {
                                      lm.setGroupid(circlesnames.get(position).getKey());
                                      lm.setUsername(snapshot.getKey());
                                      lm.setLat(dataSnapshot.child("lat").getValue(double.class));
                                      lm.setLongt(dataSnapshot.child("longt").getValue(double.class));
                                      String userpic = dataSnapshot.child("url").getValue(String.class);
                                      lm.setUrl(userpic);
                                      //each time a change is triggered create marker function is called with new location object
                                      MapFragment.createMarker(lm, c);
                                  } catch (Exception e) {
                                      e.printStackTrace();
                                  }


                                  Log.d("loc", String.valueOf(lm.getLat() + " " + lm.getLongt() + lm.getUsername()) + lm.getUrl());
                              }

                              @Override
                              public void onCancelled(DatabaseError databaseError) {

                              }
                          };

                          if (userLocationListeners.size() == 0) {
                              LinkedHashMap<String, ValueEventListener> first = new LinkedHashMap<>();
                              first.put(snapshot.getKey(), locations);
                              userLocationListeners.put(circlesnames.get(position).getKey(), first);
                          } else {
                              LinkedHashMap<String, ValueEventListener> updated = userLocationListeners.get(circlesnames.get(position).getKey());
                              updated.put(snapshot.getKey(), locations);
                              userLocationListeners.put(circlesnames.get(position).getKey(), updated);
                          }

                          MainActivity.dbr.child("public").child("UsersLocation").child(snapshot.getKey()).addValueEventListener(locations);

                      }





                      @Override
                      public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                      }

                      @Override
                      public void onChildRemoved(DataSnapshot snapshot) {
                          for(int i=0;i<users.size();i++) {
                              if (users.get(i).equals(snapshot.getKey())) {
                                  // user left the group remove him from map
                                  //disarm group listener

                                  Toast.makeText(c, "User " + snapshot.getKey() + " Has left the group", Toast.LENGTH_LONG).show();



                                  Log.w("listener removed from", "public/userslocation/" + snapshot.getKey());


                                  Log.d("drw", users.get(i) + " " + snapshot.getKey() + " " + i);
                                  ChildEventListener listener = groupsUsersListener.get(circlesnames.get(position).getKey());
                                  MainActivity.dbr.child("public").child("circles").child(circlesnames.get(position).getKey()).child("users").removeEventListener(listener);
                                  LinkedHashMap<String, ValueEventListener> usersLocations = userLocationListeners.get(circlesnames.get(position).getKey());
                                  ValueEventListener userLocationListener = usersLocations.get(snapshot.getKey());

                                  MainActivity.dbr.child("public").child("UsersLocation").child(snapshot.getKey()).removeEventListener(userLocationListener);

                                  MainActivity.dbr.child("public").child("circles").child(circlesnames.get(position).getKey()).child("users").addChildEventListener(listener);

                                  try {

                                      for (int j = 0; j < markers.size(); j++) {
                                          //move markers from map
                                          String tag = (String) markers.get(j).getTag();
                                          String[] splited = tag.split("\\s+");
                                          Log.w("stinrgs", markers.get(j).getTag().toString());
                                          Log.w("DLL", "|" + splited[1] + "|" + snapshot.getKey() + "|");
                                          if (splited[1].equals(snapshot.getKey())) {
                                              Log.w("removed", "markers" + markers.get(j).getTag().toString());

                                              markers.get(j).remove();
                                              return;
                                          }

                                      }
                                  }catch (Exception e) {
                                  e.printStackTrace();
                                  }



                              }
                          }
                      }

                      @Override
                      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {

                      }
                  };

                    groupsUsersListener.put(circlesnames.get(position).getKey(),grplistener);
                    MainActivity.dbr.child("public").child("circles").child(circlesnames.get(position).getKey()).child("users").addChildEventListener(grplistener);
                   //store each users location in array list of location model
               }else{

                    MainActivity.dbr.child("public").child("circles").child(circlesnames.get(position).getKey()).child("users").removeEventListener(groupsUsersListener.get(circlesnames.get(position).getKey()));
                    //remove locations listeners

                   LinkedHashMap<String,ValueEventListener> userslocationslisteners=userLocationListeners.get(circlesnames.get(position).getKey());
                    for (Map.Entry<String,ValueEventListener> listeners : userslocationslisteners.entrySet()) {

                        MainActivity.dbr.child("public").child("UsersLocation").child(listeners.getKey()).removeEventListener(listeners.getValue());
                        Log.d("removed","listener "+listeners.getKey());
                    }

                    //remove markers of current group

                    try {
                        for(int j = 0; j< markers.size(); j++){
                            //move markers from map
                            String tag=(String)markers.get(j).getTag();
                            String[] splited = tag.split("\\s+");
                            Log.d("stinrgs",markers.get(j).getTag()+" "+circlesnames.get(position).getKey());
                            if(splited[0].equals(circlesnames.get(position).getKey())){
                                Log.d("removed","markers");
                                markers.get(j).remove();

                            }


                       }
                   }catch (Exception e){
                        e.printStackTrace();
                   }

                   //remove listeners from db
                    //first group listener
                    users.clear();

               }
        }
        });





        //   Log.d("url",circlesinfo.get(i).getImgUrl().toString());




        return convertview;

    }
    public Object getElementByIndex(LinkedHashMap map,int index){
        return map.get( (map.keySet().toArray())[ index ] );
    }
}
