package com.nerdgeeks.foodmap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by TAOHID on 2/7/2018.
 */

public class PlaceModelCall {

        @SerializedName("html_attributions")
        @Expose
        private ArrayList<Object> htmlAttributions = null;
        @SerializedName("next_page_token")
        @Expose
        private String nextPageToken;
        @SerializedName("results")
        @Expose
        private ArrayList<PlaceModel> results = null;
        @SerializedName("status")
        @Expose
        private String status;

        public ArrayList<Object> getHtmlAttributions() {
            return htmlAttributions;
        }

        public void setHtmlAttributions(ArrayList<Object> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
        }

        public String getNextPageToken() {
            return nextPageToken;
        }

        public void setNextPageToken(String nextPageToken) {
            this.nextPageToken = nextPageToken;
        }

        public ArrayList<PlaceModel> getResults() {
            return results;
        }

        public void setResults(ArrayList<PlaceModel> results) {
            this.results = results;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }