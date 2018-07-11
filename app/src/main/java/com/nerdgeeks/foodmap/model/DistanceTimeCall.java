package com.nerdgeeks.foodmap.model;

/**
 * Created by TAOHID on 2/9/2018.
 */

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistanceTimeCall {

    @SerializedName("destination_addresses")
    @Expose
    private ArrayList<String> destinationAddresses = null;
    @SerializedName("origin_addresses")
    @Expose
    private ArrayList<String> originAddresses = null;
    @SerializedName("rows")
    @Expose
    private ArrayList<DistanceTime> rows = null;
    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(ArrayList<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public ArrayList<String> getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(ArrayList<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    public ArrayList<DistanceTime> getRows() {
        return rows;
    }

    public void setRows(ArrayList<DistanceTime> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
