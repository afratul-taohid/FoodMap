package com.nerdgeeks.foodmap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAOHID on 2/9/2018.
 */

public class PlaceDetailsCall {

    @SerializedName("html_attributions")
    @Expose
    private ArrayList<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private PlaceDetails result;
    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(ArrayList<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceDetails getResult() {
        return result;
    }

    public void setResult(PlaceDetails result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
