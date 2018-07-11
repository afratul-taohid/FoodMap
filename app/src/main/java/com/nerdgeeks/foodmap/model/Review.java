package com.nerdgeeks.foodmap.model;

/**
 * Created by TAOHID on 2/9/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author_name")
    @Expose
    private String authorName;
    @SerializedName("author_url")
    @Expose
    private String authorUrl;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("profile_photo_url")
    @Expose
    private String profilePhotoUrl;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("relative_time_description")
    @Expose
    private String relativeTimeDescription;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("time")
    @Expose
    private Double time;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public Double getRating() {
        if (rating == null){
            rating=0.0;
        }
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getRelativeTimeDescription() {
        return relativeTimeDescription;
    }

    public void setRelativeTimeDescription(String relativeTimeDescription) {
        this.relativeTimeDescription = relativeTimeDescription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

}