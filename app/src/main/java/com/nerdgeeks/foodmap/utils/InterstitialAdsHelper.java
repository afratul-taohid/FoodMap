package com.nerdgeeks.foodmap.utils;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nerdgeeks.foodmap.R;

/**
 * Created by TAOHID on 1/28/2018.
 */

public class InterstitialAdsHelper {

    private InterstitialAd mInterstitialAd;

    public InterstitialAdsHelper(Activity activity) {

        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getString(R.string.Interstitial_test));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void showAds(){

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}
