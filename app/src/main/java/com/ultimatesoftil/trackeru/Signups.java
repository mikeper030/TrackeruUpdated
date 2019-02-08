package com.ultimatesoftil.trackeru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ultimatesoftil.utilities.Utils;

import java.util.Locale;


public class Signups extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public ProgressDialog mProgressDialog;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private String idToken;
    private LoginButton loginButton;
    private final Context mContext = this;
    private Button emailb,policy;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseApp app;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private String name=null, email;
    private String photo;
    private Uri photoUri;
    private SignInButton mSignInButton;
    private CheckBox confirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_signups);
        loginButton = (LoginButton) findViewById(R.id.facebook_button);
        callbackManager = CallbackManager.Factory.create();
        policy=(Button)findViewById(R.id.policyb);
        confirm=(CheckBox)findViewById(R.id.checkB);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                startActivity(new Intent(Signups.this,PrivacyPolicyActivity.class));
            }
        });
        confirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                  loginButton.setEnabled(true);

                }else{
                    loginButton.setEnabled(false);
                }
            }
        });
        loginButton.setEnabled(false);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(!confirm.isChecked()){
                    Toast.makeText(Signups.this, R.string.agree2,Toast.LENGTH_SHORT).show();
                    return;
                }
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

               Toast.makeText(Signups.this, R.string.error_sign_in,Toast.LENGTH_LONG).show();
            }
        });
        emailb=(Button)findViewById(R.id.button2);
        emailb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!confirm.isChecked()){
                    Toast.makeText(Signups.this, R.string.agree2,Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils u = new Utils(getBaseContext());
              if(u.isNetworkAvailable()) {
                  Intent signinwithemail = new Intent(Signups.this, signup.class);
                  startActivity(signinwithemail);
                  finish();
              }else {
                  Toast.makeText(Signups.this, R.string.no_network,Toast.LENGTH_SHORT).show();
              }
            }
        });
        mSignInButton = (SignInButton) findViewById(R.id.google);
        configureSignIn();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!confirm.isChecked()){
                    Toast.makeText(Signups.this, R.string.agree2,Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils u = new Utils(getBaseContext());
               if( u.isNetworkAvailable()){
                   signIn();
               }else{
                   Toast.makeText(Signups.this, R.string.no_network, Toast.LENGTH_SHORT).show();
               }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    try {
                        mFirebaseDatabase = FirebaseDatabase.getInstance();
                        myRef = mFirebaseDatabase.getReference();
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser user2 = auth.getCurrentUser();
                        userID = user2.getUid();
                        myRef.child("users").child(userID).child("full_name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               if(mProgressDialog!=null){
                                   mProgressDialog.dismiss();
                               }
                                name= dataSnapshot.getValue(String.class);
                                Log.d("profile found","name is"+name);
                                if (name!=null) {
                                    Log.d("does have profile","");
                                    Intent i = new Intent(Signups.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else {
                                    RequestQueue queue= Volley.newRequestQueue(Signups.this);
                                    String local =null;
                                    if(Locale.getDefault().getLanguage().contentEquals("iw"))
                                        local ="HE";
                                    else
                                        local="EN";
                                    String baseurl="http://ultimatesoft-il.com/wp-json/apis/v2/mailer?app=trackeru&Locale="+local+"&Email="+FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    Log.d("baseurl",baseurl);

                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, baseurl,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    // Display the first 500 characters of the response string.

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                                    queue.add(stringRequest);
                                    Log.d("does not","have profile");
                                    Intent i = new Intent(Signups.this, createprofile.class);
                                    startActivity(i);
                                   finish();
                                }
                                Toast.makeText(Signups.this, R.string.sign_in_success,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                        // User is signed in

                    Log.d("login", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d("login", "onAuthStateChanged:signed_out");
                }
            }
        };




    }
   // }

    private void handleFacebookAccessToken(AccessToken acs){
     AuthCredential credential= FacebookAuthProvider.getCredential(acs.getToken());
     mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
          if(!task.isSuccessful()){

              Toast.makeText(Signups.this, R.string.auth_failed2,Toast.LENGTH_SHORT).show();
          }
          if (task.isSuccessful()){
              Toast.makeText(Signups.this, R.string.sign_in_success,Toast.LENGTH_SHORT).show();

          }
         }
     });
    }
    private void validateUser() {

        //Since Firebase does not allow "." in the key name, we'll have to encode and change the "." to ","
        // using the encodeEmail method in class Utils


        //create an object of Firebase database and pass the the Firebase URL
        if (!UserHasProfile()) {
            Log.d("does not","have profile");
            Intent i = new Intent(Signups.this, createprofile.class);
            startActivity(i);
            finish();
        }
        if (UserHasProfile()){
            Log.d("does have profile","");
            Intent i = new Intent(Signups.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(…);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("Login", "login unsuccesful");
                Log.e(TAG, "Google sign in failed"+e);
                try {
                    mProgressDialog.dismiss();
                }catch (Exception e1){
                    e1.printStackTrace();
                }


                Toast.makeText(this, R.string.auth_failed2, Toast.LENGTH_SHORT)
                        .show();
            }



        }
     else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void configureSignIn() {
// Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.webapid))
                .requestEmail()
                .build();
// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }
    private void signIn() {
        mProgressDialog = new ProgressDialog(Signups.this);
        mProgressDialog.setMessage("Loading..."); // Setting Message
        mProgressDialog.setTitle(R.string.log);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        mProgressDialog.show(); // Display Progress Dialog
        mProgressDialog.setCancelable(false);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private boolean UserHasProfile() {
        try {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            myRef.child("users").child(userID).child("full_name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name= dataSnapshot.getValue(String.class);
                   Log.d("profile found","name is"+name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } catch (Exception e) {
           e.printStackTrace();


        }
        if(name==null) {
            return false;



        }
        return true;
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        mProgressDialog.setTitle(R.string.acc);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signed in", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.


                        if (!task.isSuccessful()) {
                            Log.w("error", "signInWithCredential", task.getException());
                            Toast.makeText(Signups.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();

        }
       try {
           mAuth.addAuthStateListener(mAuthListener);
       }catch (Exception e){

       }
        }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}