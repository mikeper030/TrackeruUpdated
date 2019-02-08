package com.ultimatesoftil.adapters;

/**
 * Created by mike on 17 Dec 2017.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ultimatesoftil.trackeru.MainActivity;
import com.ultimatesoftil.trackeru.R;

import java.util.ArrayList;

import com.ultimatesoftil.models.User;
import com.ultimatesoftil.utilities.PicassoClient;

/**
 * Created by mike on 14 Dec 2017.
 */

public class UsersListAdapter extends BaseAdapter {
    Context c;
    ArrayList<User> circlesinfo;
    LayoutInflater inflater;
    private FirebaseDatabase mFirebaseDatabase;

    private String userID;
    private DatabaseReference myRef;
    private ImageView menuu;
    private FirebaseAuth auth;
    private String key;
    public UsersListAdapter(Context c, ArrayList<User> circlesinfo,String circleKey) {
        this.c = c;
        this.circlesinfo = circlesinfo;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        this.key=circleKey;

        }





    @Override
    public int getCount() {
        return circlesinfo.size();
    }

    @Override
    public Object getItem(int i) {
        return circlesinfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertview, ViewGroup viewGroup) {

        if (inflater== null)
        {
            inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } if(convertview==null)
        {
            convertview= inflater.inflate(R.layout.list_group,viewGroup,false);

        }




        final MyHolder2 holder= new MyHolder2(convertview);
        holder.status.setText(R.string.offline);
        holder.username.setText(circlesinfo.get(i).getUsername());
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu menu = new PopupMenu(view.getContext(), view);
                menu.inflate(R.menu.group_item_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.del_user:
                                // open friends fragment
                                final AlertDialog.Builder adb = new AlertDialog.Builder(
                                        c);
                                adb.setTitle(R.string.warning);

                                // Setting Dialog Message
                                adb.setMessage(R.string.change);

                                // Setting Icon to Dialog
                                // alertDialog.setIcon(R.drawable.);

                                // Setting OK Button
                                adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    //remove group from deleter's list
                                      MainActivity.dbr.child("users").child(MainActivity.userid).child("circles").child("listdata").child(key).child("users").child(circlesinfo.get(i).getUsername()).removeValue();

                                      //remove from public group
                                       MainActivity.dbr.child("public").child("circles").child(key).child("users").child(circlesinfo.get(i).getUsername()).removeValue();

                                       Query query= MainActivity.dbr.child("users").orderByChild("username").equalTo(circlesinfo.get(i).getUsername());
                                      query.addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(DataSnapshot dataSnapshot) {
                                      for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                          Log.d("datasna",ds.getKey());
                                          myRef.child("users").child(ds.getKey()).child("circles").child("listdata").child(key).removeValue();

                                      }




                                  }

                                  @Override
                                  public void onCancelled(DatabaseError databaseError) {

                                  }
                              });
// Write your code here to execute after dialog closed
                                        Toast.makeText(c, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                // Showing Alert Message
                                adb.show();


                                return true;
                        }


                        return false;
                    }
                });
                menu.show();

            }
        });


//        Log.d("pic",circlesinfo.get(i).getPicLink());

        PicassoClient.DownloadGroupImage(c, circlesinfo.get(i).getPicLink(), holder.img2,circlesinfo.get(i).getRotation());

            myRef.child("public").child("UsersLocation").child(circlesinfo.get(i).getUsername()).child("online").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                  try {
                      boolean online = dataSnapshot.getValue(boolean.class);
                      if(online){
                          holder.status.setText(R.string.online);
                          holder.status_img.setImageResource(R.drawable.online);
                      }else {
                          holder.status.setText(R.string.offline);
                          holder.status_img.setImageResource(R.drawable.offline);
                      }

                  }catch (Exception e){
                      e.printStackTrace();
                  }
                  }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });





        return convertview;
    }
}


