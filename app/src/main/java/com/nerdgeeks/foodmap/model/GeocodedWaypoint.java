
package com.nerdgeeks.foodmap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GeocodedWaypoint {

    @SerializedName("geocoder_status")
    @Expose
    private String geocoderStatus;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("types")
    @Expose
    private ArrayList<String> types = null;

    public String getGeocoderStatus() {
        return geocoderStatus;
    }

    public void setGeocoderStatus(String geocoderStatus) {
        this.geocoderStatus = geocoderStatus;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

}
