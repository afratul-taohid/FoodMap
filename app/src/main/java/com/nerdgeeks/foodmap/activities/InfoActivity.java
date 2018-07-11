package com.nerdgeeks.foodmap.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.nerdgeeks.foodmap.api.ApiClient;
import com.nerdgeeks.foodmap.api.ApiInterface;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.adapter.TabsAdapter;
import com.nerdgeeks.foodmap.app.AppData;
import com.nerdgeeks.foodmap.fragments.MapFragment;
import com.nerdgeeks.foodmap.fragments.ReviewsFragment;
import com.nerdgeeks.foodmap.model.DistanceTime;
import com.nerdgeeks.foodmap.model.DistanceTimeCall;
import com.nerdgeeks.foodmap.model.PlaceDetails;
import com.nerdgeeks.foodmap.model.PlaceDetailsCall;
import com.nerdgeeks.foodmap.model.TabsItem;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nerdgeeks.foodmap.app.AppConfig.TAG;

public class InfoActivity extends AppCompatActivity implements MaterialTabListener {

    private static final int REQUEST_PHONE_CALL = 1;
    private ProgressDialog progressDialog;
    private List<TabsItem> mTabs = new ArrayList<>();
    private TextView mPhone, mWeb, mName, mVicnity, mOpen, mRate, mTime, mDistance;
    private String web;
    private String phone;
    private String mapUrl;
    private String iconUrl;
    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private ImageView mThumbs;
    private RatingBar ratingBar;
    double latitude, longitude;
    private int[] icon = {R.drawable.ic_map, R.drawable.ic_feedback};
    private TabsAdapter tabAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeue.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_info);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i)
            {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded()
            {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        latitude = AppData.latitude;
        longitude = AppData.longitude;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        mThumbs = findViewById(R.id.imgThumb);
        mName = findViewById(R.id.nName);
        mVicnity = findViewById(R.id.nVicnity);
        mOpen = findViewById(R.id.nOpen);
        mRate = findViewById(R.id.nRate);
        mPhone = findViewById(R.id.nPhone);
        mWeb = findViewById(R.id.nWeb);
        mTime = findViewById(R.id.nTime);
        mDistance = findViewById(R.id.nDistance);
        ratingBar = findViewById(R.id.rateBar);
        viewPager = findViewById(R.id.viewPager);
        tabHost = findViewById(R.id.materialTabHost);
        View llBottomSheet = findViewById(R.id.card);

        String placeId = getIntent().getExtras().getString("placeId");
        getDataFromServer(placeId);

        mTabs.add(new TabsItem(MapFragment.newInstance()));
        mTabs.add(new TabsItem(ReviewsFragment.newInstance()));

        tabAdapter = new TabsAdapter(getSupportFragmentManager(), mTabs);
        viewPager.setOffscreenPageLimit(mTabs.size());
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < tabAdapter.getCount(); i++) {
            tabHost.addTab(tabHost.newTab().setIcon(getResources().getDrawable(icon[i])).setTabListener(InfoActivity.this));
        }

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> new Handler().postDelayed(() -> {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        }, 700));

        // init the bottom sheet behavior
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                // this part hides the button immediately and waits bottom sheet
                // to collapse to show
                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                viewPager.animate().translationY(1 - slideOffset).setDuration(0).start();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        finish();
        return true;
    }

    private void getDataFromServer(String placeId){

        progressDialog.show();

        ApiClient.getApiInterface().getPlaceDetails(placeId).enqueue(new Callback<PlaceDetailsCall>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetailsCall> call, @NonNull Response<PlaceDetailsCall> response) {

                PlaceDetails placeDetails = response.body().getResult();

                AppData.placeDetails = placeDetails;

                if (response.body().getStatus().equalsIgnoreCase("OK")) {

                    try {
                        mVicnity.setText(placeDetails.getFormattedAddress());
                    } catch (Exception e){
                        mVicnity.setText("N/A");
                    }

                    try {
                        phone = "N/A";
                        if (!placeDetails.getInternationalPhoneNumber().isEmpty()){
                            phone = placeDetails.getInternationalPhoneNumber();
                        }
                        mPhone.setText("Phone : " + phone);
                        mPhone.setOnClickListener(view -> startCall());
                    } catch (Exception e){
                        mPhone.setText("Phone : N/A");
                    }

                    if (!placeDetails.getIcon().isEmpty()) {
                        iconUrl = placeDetails.getIcon();
                        Picasso.get().load(iconUrl).into(mThumbs);
                    }

                    if (!placeDetails.getName().isEmpty()){
                        mName.setText(placeDetails.getName());
                    }

                    try {
                        if (placeDetails.getOpeningHours().getOpenNow()) {
                            mOpen.setText("Opened Now");
                        } else {
                            mOpen.setText("Closed Now");
                        }
                    } catch (Exception e){
                        mOpen.setText("N/A");
                    }


                    try{
                        web = "N/A";
                        if (!placeDetails.getWebsite().isEmpty()){
                            web = placeDetails.getWebsite();
                        }
                        mWeb.setText("Website : " + web);
                        mWeb.setOnClickListener(view -> {
                            Intent urlIntent = new Intent(InfoActivity.this, WebActivity.class);
                            urlIntent.putExtra("url", web);
                            startActivity(urlIntent);
                        });
                    } catch (Exception e){
                        mWeb.setText("Website : N/A");
                    }

                    if (!placeDetails.getUrl().isEmpty()) {
                        mapUrl = placeDetails.getUrl();
                    }

                    String ratings = ""+placeDetails.getRating().toString();

                    if (ratings.equals("0.0")) {
                        ratingBar.setRating(0);
                        mRate.setText("N/A");
                    } else {
                        ratingBar.setRating(Float.parseFloat(ratings));
                        mRate.setText(ratings);
                    }

                    double lat = placeDetails.getGeometry().getLocation().getLat();
                    double lng = placeDetails.getGeometry().getLocation().getLng();
                    loadDistanceTime(new LatLng(lat,lng));

                } else {
                    Toast.makeText(InfoActivity.this, "No Information found!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetailsCall> call, @NonNull Throwable t) {
                Log.d(TAG, "Json Api get failed");
                // stopping swipe refresh
                progressDialog.dismiss();
                Toast.makeText(InfoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDistanceTime(LatLng dest) {

        String origing = AppData.currentLattitude+","+AppData.currentLongitude;
        String destination = dest.latitude+","+dest.longitude;

        ApiClient.getApiInterface().getDistanceTime(origing,destination).enqueue(new Callback<DistanceTimeCall>() {
            @Override
            public void onResponse(@NonNull Call<DistanceTimeCall> call, @NonNull Response<DistanceTimeCall> response) {

                ArrayList<DistanceTime> distanceTimes = response.body().getRows();
                AppData.distanceTimes = distanceTimes;

                for (DistanceTime distanceTime: distanceTimes){

                    String distance = distanceTime.getElements().get(0).getDistance().getText();
                    mDistance.setText(distance);

                    String duration = distanceTime.getElements().get(0).getDuration().getText();
                    mTime.setText(duration);
                }

                viewPager.setAdapter(tabAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<DistanceTimeCall> call, @NonNull Throwable t) {
                Log.d(TAG, "Json Api get failed");
                // stopping swipe refresh
                progressDialog.dismiss();
                Toast.makeText(InfoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_option, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.phone:
                startCall();
                return true;
            case R.id.web:
                startWeb(web);
                return true;
            case R.id.explore:
                exploreMap(mapUrl);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void exploreMap(String url){
        assert url != null;
        if(!url.equals("N/A")){
            Intent mapUrlIntent = new Intent(InfoActivity.this, WebActivity.class);
            mapUrlIntent.putExtra("url", url);
            startActivity(mapUrlIntent);
        }else {
            new AlertDialog.Builder(InfoActivity.this)
                    .setIcon(R.drawable.ic_map_grey)
                    .setTitle("Unavailable")
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    public void startCall() {
        if (phone != null) {
            new AlertDialog.Builder(InfoActivity.this)
                    .setIcon(R.drawable.ic_phone_grey)
                    .setTitle("Call this number?")
                    .setMessage(phone)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            (dialog, which) -> {
                                try {
                                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                    phoneIntent.setData(Uri.parse("tel:" + phone));
                                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                        }
                                    } else {
                                        startActivity(phoneIntent);
                                    }
                                } catch (SecurityException e) {
                                    Toast.makeText(InfoActivity.this,
                                            "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .show();
        } else {
            new AlertDialog.Builder(InfoActivity.this)
                    .setIcon(R.drawable.ic_phone_grey)
                    .setTitle("Phone Number Unavailable")
                    .setMessage(phone)
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:" + phone));
                        startActivity(phoneIntent);

                    } catch (android.content.ActivityNotFoundException | SecurityException ex) {
                        Toast.makeText(this,
                                "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
                return;
            }
        }
    }

    public void startWeb(String webUrl) {
        if (webUrl != null) {
            Intent urlIntent = new Intent(InfoActivity.this, WebActivity.class);
            urlIntent.putExtra("url", webUrl);
            startActivity(urlIntent);
        } else {
            new AlertDialog.Builder(InfoActivity.this)
                    .setIcon(R.drawable.ic_web_grey)
                    .setTitle("Website Unavailable")
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }
}
