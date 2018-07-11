
package com.nerdgeeks.foodmap.model;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteCall {

    @SerializedName("geocoded_waypoints")
    @Expose
    private ArrayList<GeocodedWaypoint> geocodedWaypoints = null;
    @SerializedName("routes")
    @Expose
    private ArrayList<Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(ArrayList<GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
