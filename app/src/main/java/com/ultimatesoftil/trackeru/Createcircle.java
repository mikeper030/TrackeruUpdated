package com.ultimatesoftil.trackeru;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

import com.ultimatesoftil.adapters.PagerAdapter;
import com.ultimatesoftil.models.User;
import com.ultimatesoftil.models.UserCircleModel;
import com.ultimatesoftil.utilities.PicassoClient;
import com.ultimatesoftil.utilities.Utils;

public class Createcircle extends AppCompatActivity {
    private EditText circlename;
    private TextView add;
    private ProgressBar pr;
    private String key;
    private FirebaseDatabase mFirebaseDatabase;
    private ImageView img,groupimage,toolbarf;
    private StorageReference storageRef;
    int RESULT_LOAD_IMG=1;
    private FirebaseStorage storage;
    private FirebaseApp app;
    private Context context;
    private String userID,imageurl=null;
    private DatabaseReference myRef;
    private PagerAdapter mAdapter;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private CircularProgressButton savecircle;
    private String picpath;
    private ImageButton rotate;
    public static int deg=0;
    private ProgressDialog progress;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

                // save the circle
//                if(!circlename.getText().toString().isEmpty()&&groupimage.getTag().equals("default")){
//
//                    Uri otherPath = Uri.parse("android.resource:///drawable/profile.png");
//                    String path = otherPath.toString();
//                    Utils.savegroup(this,new usersinCircleModel(circlename.getText().toString(),path));
//
//                }if((!circlename.getText().toString().isEmpty())&&(groupimage.getTag()!="default")){
//
//                    Utils.savegroup(this,new usersinCircleModel(circlename.getText().toString(),picpath));
//
//                }else{
//                    Log.d("tag","no");
//            }



            case R.id.settings:

                Intent i = new Intent(getBaseContext(),settings.class);
                startActivity(i);


        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cretecircle_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcircle);

        final Button share=(Button)findViewById(R.id.buttonshare);

        share.setVisibility(View.INVISIBLE);
                auth = FirebaseAuth.getInstance();
       progress = new ProgressDialog(this);
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, login.class));
            finish();
        } else {
            toolbar = (Toolbar) findViewById(R.id.toolbar2);
            setSupportActionBar(toolbar);
            try {
                if (getIntent().getStringExtra("title") != null) {
                    getSupportActionBar().setTitle(getIntent().getStringExtra("title").toString());
                    key = getIntent().getStringExtra("title");
                } else {

                    getSupportActionBar().setTitle(R.string.new_circle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            app = FirebaseApp.getInstance();
            storage = FirebaseStorage.getInstance(app);
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            final TextView tx = (TextView) findViewById(R.id.pname2);
            final FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            img = (ImageView) findViewById(R.id.profile2);
            pr = (ProgressBar) findViewById(R.id.progressBar2);
            savecircle = (CircularProgressButton) findViewById(R.id.save1);
            add = (TextView) findViewById(R.id.textadd);
            rotate = (ImageButton) findViewById(R.id.rotate);

            final TextView title = (TextView) findViewById(R.id.textView2);
            try {
                Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Trackeru/profile.jpg");
                myRef.child("users").child(userID).child("full_name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        tx.setText(name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getBaseContext(), MyAccount.class);
                        startActivity(i);
                    }
                });
                img.setImageBitmap(bmp);

            } catch (Exception e) {
                e.printStackTrace();
            }
            circlename = (TextInputEditText) findViewById(R.id.edittextcirclename);

            if (getIntent().getStringExtra("name") != null && getIntent().getStringExtra("pic") == null) {
                savecircle.setVisibility(View.INVISIBLE);
                circlename.setText(getIntent().getStringExtra("name"));
                ((TextInputLayout) findViewById(R.id.textInputLayout)).setHint(null);

                //circlename.setEnabled(false);

                add.setVisibility(View.VISIBLE);

                share.setVisibility(View.VISIBLE);
            }

            if (getIntent().getStringExtra("name") != null && getIntent().getStringExtra("pic") != null) {
               //add friend screen
                share.setVisibility(View.VISIBLE);

                savecircle.setVisibility(View.INVISIBLE);
                title.setText(R.string.change_group_pic);
                circlename.setText(getIntent().getStringExtra("name"));
                ((TextInputLayout) findViewById(R.id.textInputLayout)).setHint(null);
                //circlename.setEnabled(false);
                circlename.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                       savecircle.setVisibility(View.VISIBLE);
                       savecircle.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if(!TextUtils.isEmpty(circlename.getText().toString())) {
                                // myRef.child("public").child("circles").child(getIntent().getStringExtra("title").toString()).child("name").setValue(circlename.getText().toString());
                                   Query query=myRef.child("users").orderByChild("full_name");
                                   query.addChildEventListener(new ChildEventListener() {
                                       @Override
                                       public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                          try {
                                              myRef.child("public").child("circles").child(getIntent().getStringExtra("title").toString()).child("name").setValue(circlename.getText().toString() );

                                              String name= dataSnapshot.child("circles").child("listdata").child(getIntent().getStringExtra("title").toString()).child("key").getValue(String.class);
                                              Log.d("snapshot",name);
                                              if(name!=null){
                                                  myRef.child("users").child(dataSnapshot.getKey()).child("circles").child("listdata").child(getIntent().getStringExtra("title").toString()).child("name").setValue(circlename.getText().toString());
                                              }
                                          }catch (Exception E){
                                              E.printStackTrace();
                                          }

                                       }

                                       @Override
                                       public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                       }

                                       @Override
                                       public void onChildRemoved(DataSnapshot dataSnapshot) {

                                       }

                                       @Override
                                       public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                       }
                                   });


                               }else {
                                   Toast.makeText(Createcircle.this, R.string.empty_circle,Toast.LENGTH_SHORT).show();
                               }
                           Toast.makeText(Createcircle.this, R.string.done,Toast.LENGTH_SHORT).show();
                           }
                       });
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        //update the circle name for all users

                    }
                });
                myRef.child("public").child("circles").child(getIntent().getStringExtra("title")).child("imgUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String img = dataSnapshot.getValue(String.class);

                        myRef.child("public").child("circles").child(getIntent().getStringExtra("title")).child("imgRotation").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    int degree = dataSnapshot.getValue(int.class);
                                    PicassoClient.DownloadGroupImage(Createcircle.this, img, groupimage, degree);

                                } catch (Exception e) {
                                    PicassoClient.DownloadGroupImage(Createcircle.this, img, groupimage, 0);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            savecircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!circlename.getText().toString().isEmpty()) {
                        final User user1 = new User();
                        simulateSuccessProgress(savecircle);

                        //generate circle key


                        //upload circle string and key to firebase db
                        // myRef.child("users").child(userID).child("circlesinfo").child(circlename.getText().toString()).child("User name").setValue("default");


                        //edit image ref
                        if (getIntent().getStringExtra("name") != null && getIntent().getStringExtra("pic") != null) {
                            Log.d("dfs", imageurl);
                            myRef.child("users").child(userID).child("circles").child("listdata").child(getIntent().getStringExtra("title")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    UserCircleModel updatedmodel = new UserCircleModel();
                                    updatedmodel.setKey(dataSnapshot.getValue(UserCircleModel.class).getKey());
                                    updatedmodel.setImgUrl(imageurl);
                                    updatedmodel.setImgRotation(deg);
                                    updatedmodel.setName(dataSnapshot.getValue(UserCircleModel.class).getName());

                                    //updating private
                                    myRef.child("users").child(userID).child("circles").child("listdata").child(getIntent().getStringExtra("title")).setValue(updatedmodel);
                                    //updating public
                                    myRef.child("public").child("circles").child(getIntent().getStringExtra("title")).setValue(updatedmodel);
                                    getIntent().removeExtra("title");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            //create circle ref
                        } else {
                            key = Utils.createKey();
                            UserCircleModel usm = new UserCircleModel();
                            usm.setName(circlename.getText().toString());
                            usm.setKey(key);
                            usm.setImgUrl(imageurl);
                            myRef.child("users").child(userID).child("circles").child("listdata").child(key).setValue(usm);

                            //upload data to public folder in database
                            myRef.child("public").child("circles").child(key).setValue(usm);
                            //add creator to circle
                            final User user = new User();
                            //email
                            myRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                        try {
                                            user.setUsername(dataSnapshot.child("username").getValue().toString());
                                            user.setEmail(dataSnapshot.child("email").getValue().toString());
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Toast.makeText(Createcircle.this,"Error. please make sure your profile is complete",Toast.LENGTH_LONG).show();
                                            Intent in = new Intent(Createcircle.this, createprofile.class);
                                            startActivity(in);
                                        }



                                        user.setPicLink(dataSnapshot.child("picLink").getValue().toString());

                                    user.setPhone(dataSnapshot.child("phone").getValue().toString());
                                    user.setFull_name(dataSnapshot.child("full_name").getValue().toString());

                                    myRef.child("public").child("circles").child(key).child("users").child(user.getUsername()).setValue(user);
                                    myRef.child("users").child(userID).child("circles").child("listdata").child(key).child("users").child(user.getUsername()).setValue(user);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Log.d("user", user.toString());
                        }


                        //make share elements visible
                        add.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getBaseContext(), R.string.empty_name, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            groupimage = (ImageView) findViewById(R.id.profile3);
            groupimage.setTag("default");
            groupimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (getIntent().getStringExtra("title") == null) {
                        final String key2 = key;


                        final AlertDialog.Builder alert = new AlertDialog.Builder(Createcircle.this);
                        alert.setTitle(key2);
                        alert.setMessage(R.string.share_code);
                        LayoutInflater inflater = (LayoutInflater) Createcircle.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                        alert.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {


                                String shareBody = getResources().getString(R.string.invite) + System.getProperty("line.separator") + "https://goo.gl/wKcH7n" + System.getProperty("line.separator") +  "["+key2+"]";
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.tra);
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                            }
                        });

                        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();





            }
                  if (getIntent().getStringExtra("title") != null) {
                      final String key2 = (getIntent().getStringExtra("title").toString());



                      final AlertDialog.Builder alert = new AlertDialog.Builder(Createcircle.this);
                      alert.setTitle(key2);
                      alert.setMessage(R.string.share_code);
                      LayoutInflater inflater = (LayoutInflater)Createcircle.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                      alert.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int whichButton) {


                              String shareBody = getResources().getString(R.string.invite) + System.getProperty("line.separator") + "https://goo.gl/wKcH7n" + System.getProperty("line.separator") +  "["+key2+"]";
                              Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                              sharingIntent.setType("text/plain");
                              sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.tra);
                              sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                              startActivity(Intent.createChooser(sharingIntent,getResources().getString(R.string.share_using)));
                          }
                      });

                      alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                          }
                      });
                      alert.show();





                  }
              }
              });

        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get the contact details via activityresult
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {


       try {

           final Uri selectedImage = data.getData();
           String[] filePathColumn = {MediaStore.Images.Media.DATA};
           Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
           cursor.moveToFirst();
           int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picpath = cursor.getString(columnIndex);
           cursor.close();
           ImageView imageView = (ImageView) findViewById(R.id.profile3);
           imageView.setImageBitmap(BitmapFactory.decodeFile(picpath));
          img.setTag("1");

           new AsyncTask<Void, Void, Void>() {
               @Override
               protected void onPreExecute() {
                   super.onPreExecute();
                   progress.setMessage("uploading picture please wait..");
                   progress.show();
                   pr.setVisibility(View.VISIBLE);
                   savecircle.setVisibility(View.INVISIBLE);
                   rotate.setVisibility(View.VISIBLE);
               }

               @Override
               protected void onPostExecute(Void aVoid) {

                   super.onPostExecute(aVoid);
                   rotate.setOnClickListener(new View.OnClickListener() {

                       @Override
                       public void onClick(View view) {
                           progress.setMessage("uploading picture please wait..");
                           progress.show();
                          pr.setVisibility(View.VISIBLE);
                           groupimage.setRotation(deg+90);

                           deg+=90;
                           Random rand = new Random();
                           double rand_dub2 = rand.nextDouble();
                        final   StorageReference ref = storage.getReference("photos").child(userID).child(String.valueOf(rand_dub2));

                           ref.putFile(selectedImage)
                                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                           Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                           imageurl = downloadUrl.toString();
                                           Log.d("url",downloadUrl.toString());
                                           savecircle.setVisibility(View.VISIBLE);
                                           pr.setVisibility(View.INVISIBLE);
                                           progress.dismiss();


                                       }
                                   })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception exception) {
                                           Toast.makeText(getBaseContext(), R.string.retry,Toast.LENGTH_LONG).show();
                                       }
                                   })
                                   .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {



                                       }
                                   });
                       }
                   });
                   rotate.setVisibility(View.VISIBLE);

               }

               @Override
               protected Void doInBackground(Void... voids) {
                   Random rand = new Random();
                   double rand_dub2 = rand.nextDouble();
                   StorageReference ref = storage.getReference("photos").child(userID).child(String.valueOf(rand_dub2));

                   ref.putFile(selectedImage)
                           .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                  imageurl = downloadUrl.toString();
                                   Log.d("url",downloadUrl.toString());
                                   savecircle.setVisibility(View.VISIBLE);
                                   pr.setVisibility(View.INVISIBLE);
                                   progress.dismiss();

                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {
                               Toast.makeText(getBaseContext(), R.string.retry,Toast.LENGTH_LONG).show();
                               }
                           })
                           .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {



                               }
                           });

                   return null;
               }
           }.execute();
       }catch (Exception e){
           e.printStackTrace();
       }

       }

    }




    private void simulateSuccessProgress(final CircularProgressButton btnsetup) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1500);

        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                btnsetup.setProgress(value);
                if(value==100){
                   Log.d("df",String.valueOf(value));
                    savecircle.setEnabled(false);
                }
            }
        });
        widthAnimation.start();

    }

}