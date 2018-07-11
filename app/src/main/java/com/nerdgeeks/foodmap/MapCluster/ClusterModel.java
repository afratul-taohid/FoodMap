package com.nerdgeeks.foodmap.MapCluster;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by TAOHID on 3/19/2018.
 */

public class ClusterModel implements ClusterItem{

    private LatLng mPosition;
    private String mTitle, mSnippet, mPlaceId;
    private BitmapDescriptor icon;

    public ClusterModel(String placeId, MarkerOptions markerOptions){
        this.mPosition = markerOptions.getPosition();
        this.mTitle = markerOptions.getTitle();
        this.mSnippet = markerOptions.getSnippet();
        this.icon = markerOptions.getIcon();
        this.mPlaceId = placeId;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}

