package com.ultimatesoftil.trackeru;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;


public class PrivacyPolicyActivity extends AppCompatActivity {
   private WebView policy;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
            return true;
        }return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        policy = (WebView) findViewById(R.id.textpolicy);
        policy.getSettings().setJavaScriptEnabled(true);
        policy.loadUrl("file:///android_asset/page.html");


    }
}
