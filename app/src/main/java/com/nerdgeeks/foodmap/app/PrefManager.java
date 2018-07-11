package com.nerdgeeks.foodmap.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.nerdgeeks.foodmap.model.PlaceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by TAOHID on 3/19/2018.
 */

public class PrefManager {
    public static final String TAG = PrefManager.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = AppConfig.PREF_NAME;

    //private static final String MAP_ARRAY = "maplist";

    public PrefManager(Context mContext) {
        int PRIVATE_MODE = 0;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void storeData(ArrayList<PlaceModel> arrayList, String type){

        Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        if(sharedPreferences.contains(type)){
            editor.remove(type);
        }

        editor.putString(type,gson.toJson(arrayList));
        editor.apply();
        Log.e(TAG,"Inserted");
    }

    public ArrayList<PlaceModel> readData(String type){
        List<PlaceModel> modelArrayList;

        if(sharedPreferences.contains(type)){
            Gson gson = new Gson();
            String json = sharedPreferences.getString(type,null);
            PlaceModel [] placeModels = gson.fromJson(json,PlaceModel[].class);

            modelArrayList = Arrays.asList(placeModels);
            modelArrayList = new ArrayList<>(modelArrayList);
        } else {
            return null;
        }
        return (ArrayList<PlaceModel>) modelArrayList;
    }

    public boolean isPrefAvailable(String type){
        return sharedPreferences.contains(type);
    }
}
