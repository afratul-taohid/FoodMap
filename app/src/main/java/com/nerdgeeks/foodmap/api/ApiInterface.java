package com.nerdgeeks.foodmap.api;

import com.nerdgeeks.foodmap.app.AppConfig;
import com.nerdgeeks.foodmap.model.DistanceTimeCall;
import com.nerdgeeks.foodmap.model.PlaceDetailsCall;
import com.nerdgeeks.foodmap.model.PlaceModelCall;
import com.nerdgeeks.foodmap.model.RouteCall;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TAOHID on 1/21/2018.
 */

public interface ApiInterface {

    String key = AppConfig.GOOGLE_MAP_API_KEY;

    @GET("api/place/nearbysearch/json?sensor=false&key="+key)
    Call<PlaceModelCall> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

    @GET("api/place/nearbysearch/json?key="+key)
    Call<PlaceModelCall> getNextNearbyPlaces(@Query("pagetoken") String pageToken);

    @GET("api/place/details/json?sensor=false&key="+key)
    Call<PlaceDetailsCall> getPlaceDetails(@Query("placeid") String placeId);

    @GET("api/distancematrix/json?sensor=false&mode=walking")
    Call<DistanceTimeCall> getDistanceTime(@Query("origins") String origins, @Query("destinations") String destination);

    @GET("api/directions/json?sensor=false&mode=walking&units=metric")
    Call<RouteCall> getRoute(@Query("origin") String origins, @Query("destination") String destination);


}