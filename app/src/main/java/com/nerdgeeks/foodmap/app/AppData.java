package com.nerdgeeks.foodmap.app;

import com.nerdgeeks.foodmap.model.DistanceTime;
import com.nerdgeeks.foodmap.model.PlaceDetails;
import com.nerdgeeks.foodmap.model.PlaceModel;
import com.nerdgeeks.foodmap.model.Route;
import java.util.ArrayList;

/**
 * Created by TAOHID on 2/7/2018.
 */

public class AppData {

    public static ArrayList<PlaceModel> placeModels = new ArrayList<>();
    public static Double latitude;
    public static Double longitude;
    public static Double currentLattitude;
    public static Double currentLongitude;

    public static PlaceDetails placeDetails;
    public static ArrayList<DistanceTime> distanceTimes;
    public static ArrayList<Route> routeArrayList;

}
