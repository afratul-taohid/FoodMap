package com.nerdgeeks.foodmap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.helper.ConnectivityReceiver;
import com.nerdgeeks.foodmap.utils.UpdateChecker;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeue.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_splash);

        View mSplashImage = findViewById(R.id.splash);
        TextView mSplashText = findViewById(R.id.splashText);
        Animation splashAnimImage = AnimationUtils.loadAnimation(this, R.anim.splash_anim_img);
        Animation splashAnimText = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        mSplashText.startAnimation(splashAnimText);
        mSplashImage.startAnimation(splashAnimImage);

        if (ConnectivityReceiver.isConnected()){
            UpdateChecker.with(this, materialDialog -> {
                if (materialDialog != null){
                    materialDialog.dismiss();
                }
                gotoMainActivity();
            }).execute();
        } else {
            new Handler().postDelayed(this::gotoMainActivity, 1000);
        }
    }

    private void gotoMainActivity(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
