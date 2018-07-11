package com.nerdgeeks.foodmap.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TAOHID on 1/21/2018.
 */

public class ApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiInterface getApiInterface(){
        return getClient().create(ApiInterface.class);
    }
}
