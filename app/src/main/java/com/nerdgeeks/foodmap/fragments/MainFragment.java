package com.nerdgeeks.foodmap.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.activities.MainActivity;
import com.nerdgeeks.foodmap.adapter.PlaceAutocompleteAdapter;
import com.nerdgeeks.foodmap.app.AppData;
import com.nerdgeeks.foodmap.utils.LocationHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by TAOHID on 3/19/2018.
 */

public class MainFragment extends Fragment implements LocationHelper.LocationCallBack {

    private String NearbyFragmentTag = "MAP";
    private String ResultFragmentTag = "RESULT";
    private FragmentTransaction mTransaction;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    public static PlaceAutocompleteAdapter placesAutoCompleteAdapter;

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context mContext;
    int bottomNavigationState = 1;
    public static ProgressDialog pDialog;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        AdView mAdView = rootView.findViewById(R.id.adView);
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

        BottomNavigationView navigation = rootView.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Loading ");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        pDialog.show();
        if (AppData.longitude == null && AppData.latitude == null){
            LocationHelper.with(mContext).updateLocation(this);
        } else {
            loadFragment();
        }

        return rootView;
    }

    @Override
    public void onLocationCallBack(Location location) {

        AppData.latitude = AppData.currentLattitude = location.getLatitude();
        AppData.longitude = AppData.currentLongitude =  location.getLongitude();

        // Default show nearby map fragment
        loadFragment();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment nearbyFragment = getChildFragmentManager().findFragmentByTag(NearbyFragmentTag);
            Fragment resultFragment = getChildFragmentManager().findFragmentByTag(ResultFragmentTag);
            mTransaction = getChildFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.map:

                    if (bottomNavigationState == 0){
                        mTransaction.hide(resultFragment);
                        mTransaction.show(nearbyFragment);
                        mTransaction.commit();
                    }

                    bottomNavigationState = 1;
                    return true;

                case R.id.list:

                    if (bottomNavigationState == 1){
                        Fragment mFragment = ResultFragment.newInstance(mParam1);
                        mTransaction.hide(nearbyFragment);
                        if (resultFragment != null){
                            mTransaction.show(resultFragment);
                        } else {
                            mTransaction.add(R.id.frame_container, mFragment,ResultFragmentTag);
                            mTransaction.addToBackStack(null);
                        }
                        mTransaction.commit();
                    }

                    bottomNavigationState = 0;
                    return true;
            }
            return false;
        }
    };

    private void loadFragment() {
        setHasOptionsMenu(true);
        Fragment mFragment = NearbyFragment.newInstance(mParam1);
        mTransaction = getChildFragmentManager().beginTransaction();
        mTransaction.add(R.id.frame_container, mFragment,NearbyFragmentTag);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchView.SearchAutoComplete searchAutoComplete = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.BLACK);
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry(getCountryCode())
                .build();
        placesAutoCompleteAdapter = new PlaceAutocompleteAdapter(mContext, LocationHelper.with(mContext).getGoogleApiClient(),
                LAT_LNG_BOUNDS, autocompleteFilter);
        searchAutoComplete.setAdapter(placesAutoCompleteAdapter);

        searchAutoComplete.setOnItemClickListener((adapterView, view, itemIndex, id) -> {
            try {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
            }
            final AutocompletePrediction place = placesAutoCompleteAdapter.getItem(itemIndex);
            assert place != null;
            final String placeName = String.valueOf(place.getFullText(STYLE_BOLD));
            searchAutoComplete.setText("" + placeName);

            MainActivity.drawer.updateName(7, new StringHolder(placeName));
            MainActivity.drawer.updateName(8,new StringHolder("Remove Location"));

            geoLocate(place.getPlaceId());
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final AutocompletePrediction place = placesAutoCompleteAdapter.getItem(0);
                if (place != null) {
                    final String placeName = String.valueOf(place.getFullText(STYLE_BOLD));
                    searchAutoComplete.setText("" + placeName);

                    MainActivity.drawer.updateName(7, new StringHolder(placeName));
                    MainActivity.drawer.updateName(8,new StringHolder("Remove Location"));

                    geoLocate(place.getPlaceId());
                } else {
                    Toast.makeText(mContext, "Google can't find this place", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private String getCountryCode(){
        String countryCode;
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(mContext, Locale.getDefault());
            addresses = geocoder.getFromLocation(AppData.latitude, AppData.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            countryCode = addresses.get(0).getCountryCode();
        } catch (IOException e) {
            e.printStackTrace();
            countryCode = "";
        }
        return countryCode;
    }

    private void geoLocate(String placeId){

        Places.GeoDataApi.getPlaceById(LocationHelper.with(mContext).getGoogleApiClient(), placeId)
                .setResultCallback(places -> {
                    if (places.getStatus().isSuccess()) {
                        final Place myPlace = places.get(0);
                        LatLng queriedLocation = myPlace.getLatLng();
                        AppData.latitude = queriedLocation.latitude;
                        AppData.longitude = queriedLocation.longitude;

                        pDialog.show();
                        refreshData();
                    }
                    places.release();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case R.id.refresh:
                refreshData();
                return true;
            case R.id.cached:
                deleteCache(mContext);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshData(){
        // clear the fragment manager stack
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        bottomNavigationState = 1;
        AppData.placeModels.clear();
        loadFragment();
    }

    public void deleteCache(Context context) {

        try {
            File cacheDirectory = context.getCacheDir();
            File applicationDirectory = new File(cacheDirectory.getParent());
            if (applicationDirectory.exists()) {
                String[] fileNames = applicationDirectory.list();
                for (String fileName : fileNames) {
                    if (!fileName.equals("lib")) {
                        deleteFile(new File(applicationDirectory, fileName));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    deletedAll = deleteFile(new File(file, aChildren)) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("createacall", 0);
        sharedPreferences.edit().clear().apply();
        return deletedAll;
    }
}
