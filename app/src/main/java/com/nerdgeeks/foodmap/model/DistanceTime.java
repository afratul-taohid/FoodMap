package com.nerdgeeks.foodmap.model;

/**
 * Created by TAOHID on 2/9/2018.
 */

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistanceTime {

    @SerializedName("elements")
    @Expose
    private ArrayList<Element> elements = null;

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

}