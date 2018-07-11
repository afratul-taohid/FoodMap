package com.nerdgeeks.foodmap.activities;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.utils.InterstitialAdsHelper;

public class WebActivity extends AppCompatActivity {

    private WebView browser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        InterstitialAdsHelper interstitialAdsHelper = new InterstitialAdsHelper(this);

        new Handler().postDelayed(interstitialAdsHelper::showAds,5000);

        //get String by Data Passing
        String URL = getIntent().getStringExtra("url");

        //Initializing WebView
        browser = findViewById(R.id.mWebView);
        browser.getSettings().setJavaScriptEnabled(true);

        //Load URL on WebView
        startWebView(URL);

        //Adding Fab Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            new Handler().postDelayed(() -> {
                progressDialog = new ProgressDialog(WebActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                browser.reload();
            }, 700);
            progressDialog.dismiss();
        });

    }

    private void startWebView(String url) {
        browser.setWebViewClient(new WebViewClient() {
            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(WebActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });
        browser.loadUrl(url);
    }

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(browser.canGoBack()) {
            browser.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
            finish();
        }
    }
}
