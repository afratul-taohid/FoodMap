
package com.nerdgeeks.foodmap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Leg {

    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("end_address")
    @Expose
    private String endAddress;
    @SerializedName("end_location")
    @Expose
    private EndLocation endLocation;
    @SerializedName("start_address")
    @Expose
    private String startAddress;
    @SerializedName("start_location")
    @Expose
    private StartLocation startLocation;
    @SerializedName("steps")
    @Expose
    private ArrayList<Step> steps = null;
    @SerializedName("traffic_speed_entry")
    @Expose
    private ArrayList<Object> trafficSpeedEntry = null;
    @SerializedName("via_waypoint")
    @Expose
    private ArrayList<Object> viaWaypoint = null;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public EndLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(EndLocation endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public StartLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocation startLocation) {
        this.startLocation = startLocation;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public ArrayList<Object> getTrafficSpeedEntry() {
        return trafficSpeedEntry;
    }

    public void setTrafficSpeedEntry(ArrayList<Object> trafficSpeedEntry) {
        this.trafficSpeedEntry = trafficSpeedEntry;
    }

    public ArrayList<Object> getViaWaypoint() {
        return viaWaypoint;
    }

    public void setViaWaypoint(ArrayList<Object> viaWaypoint) {
        this.viaWaypoint = viaWaypoint;
    }

}
