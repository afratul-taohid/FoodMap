package com.nerdgeeks.foodmap.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.nerdgeeks.foodmap.BuildConfig;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.app.AppData;
import com.nerdgeeks.foodmap.fragments.MainFragment;
import com.nerdgeeks.foodmap.utils.InterstitialAdsHelper;
import com.nerdgeeks.foodmap.utils.LocationHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private int selectedPosition = 1;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;
    private int resumeCount;
    private InterstitialAdsHelper interstitialAdsHelper;
    public static Drawer drawer;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedPosition", selectedPosition);
     }

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
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            selectedPosition = savedInstanceState.getInt("selectedPosition");
        }

        interstitialAdsHelper = new InterstitialAdsHelper(this);

        //Google Analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        Tracker tracker = analytics.newTracker("UA-72883943-9");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationView();
    }

    private void initNavigationView() {

        //adding navigation header
        View view = View.inflate(this,R.layout.nav_header,null);
        TextView navText = view.findViewById(R.id.nav_text);

        //Adding navigation drawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                //.withTranslucentStatusBar(true)
                .withHeader(view)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Restaurant")
                                .withIcon(R.drawable.ic_restaurant)
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withName("Bar")
                                .withIcon(R.drawable.ic_local_bar)
                                .withIdentifier(2),
                        new PrimaryDrawerItem()
                                .withName("Cafe")
                                .withIcon(R.drawable.ic_local_cafe)
                                .withIdentifier(3),
                        new PrimaryDrawerItem()
                                .withName("Grocery Store")
                                .withIcon(R.drawable.ic_local_grocery_store)
                                .withIdentifier(4),
                        new SectionDrawerItem()
                                .withName("MORE"),
                        new PrimaryDrawerItem()
                                .withName("Rate Me")
                                .withIcon(R.drawable.ic_rate_review)
                                .withIdentifier(5),
                        new PrimaryDrawerItem()
                                .withName("About")
                                .withIcon(R.drawable.ic_info)
                                .withIdentifier(6),
                        new SectionDrawerItem()
                                .withName("Current Location")
                                .withIdentifier(7),
                        new PrimaryDrawerItem()
                                .withName("")
                                .withIdentifier(8)

                )
                .withOnDrawerItemClickListener((view1, position, drawerItem) -> {
                    if (drawerItem != null) {

                        if (selectedPosition != drawerItem.getIdentifier() && !AppData.placeModels.isEmpty()){
                            AppData.placeModels.clear();
                        }

                        selectedPosition = ((int) drawerItem.getIdentifier());

                        if (drawerItem.getIdentifier() == 1) {
                            toolbar.setSubtitle("Restaurants");
                            fragment = MainFragment
                                    .newInstance("restaurant");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                        } else if (drawerItem.getIdentifier() == 2) {
                            toolbar.setSubtitle("Bars");
                            fragment = MainFragment
                                    .newInstance("bar");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                        } else if (drawerItem.getIdentifier() == 3) {
                            toolbar.setSubtitle("Cafe");
                            fragment = MainFragment
                                    .newInstance("cafe");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                        } else if (drawerItem.getIdentifier() == 4) {
                            toolbar.setSubtitle("Stores");
                            fragment = MainFragment.newInstance("grocery_or_supermarket");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                        } else if (drawerItem.getIdentifier() == 5) {
                            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                            }
                        } else if (drawerItem.getIdentifier() == 6) {
                            aboutMyApp();
                        } else if (drawerItem.getIdentifier() == 8) {
                            AppData.latitude = AppData.longitude = null;
                            drawer.updateName(7,new StringHolder("Current Location"));
                            drawer.updateName(8,new StringHolder(""));
                            drawer.setSelection(1);
                        }
                    }
                    return false;
                })
                .build();
        drawer.setSelection(selectedPosition,true);

    }

    private void aboutMyApp() {

        MaterialDialog.Builder bulder = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .customView(R.layout.about, true)
                .backgroundColor(getResources().getColor(R.color.snippetBackground))
                .titleColorRes(android.R.color.white)
                .positiveText("MORE APPS")
                .positiveColor(getResources().getColor(android.R.color.white))
                .icon(getResources().getDrawable(R.drawable.ic_splash))
                .limitIconToDefaultSize()
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("market://search?q=pub:" + "NerdGeeks");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/search?q=pub:" + "NerdGeeks")));
                    }
                });

        MaterialDialog materialDialog = bulder.build();

        TextView versionCode = (TextView) materialDialog.findViewById(R.id.version_code);
        TextView versionName = (TextView) materialDialog.findViewById(R.id.version_name);
        versionCode.setText(String.valueOf("Version Code : " + BuildConfig.VERSION_CODE));
        versionName.setText(String.valueOf("Version Name : " + BuildConfig.VERSION_NAME));

        materialDialog.show();
    }

    private void showQuitPopup(){

        MaterialDialog.Builder builder =  new MaterialDialog.Builder(MainActivity.this)
                .canceledOnTouchOutside(false)
                .title("Are you sure you want to exit?")
                .customView(R.layout.exit_dialog,false)
                .negativeText("BACK")
                .onNegative((dialog, which) -> {})
                .positiveText("EXIT")
                .onPositive((dialog, which) -> {
                    finish();
                    System.exit(1);
                });

        MaterialDialog dialog = builder.build();
        LinearLayout view = (LinearLayout) dialog.findViewById(R.id.adViewLayout);
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showQuitPopup();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (drawer.isDrawerOpen()){
            drawer.closeDrawer();
        }
        resumeCount++;
        if (resumeCount % 4 == 0){
            showRandomInterstitialAds();
        }
    }

    private void showRandomInterstitialAds(){
        interstitialAdsHelper.showAds();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationHelper.with(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LocationHelper.with(this).onActivityResult(requestCode,resultCode,data);
    }

}