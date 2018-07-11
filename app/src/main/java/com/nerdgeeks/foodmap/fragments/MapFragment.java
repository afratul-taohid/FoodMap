package com.nerdgeeks.foodmap.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nerdgeeks.foodmap.api.ApiClient;
import com.nerdgeeks.foodmap.api.ApiInterface;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.app.AppData;
import com.nerdgeeks.foodmap.model.DistanceTime;
import com.nerdgeeks.foodmap.model.PlaceDetails;
import com.nerdgeeks.foodmap.model.Route;
import com.nerdgeeks.foodmap.model.RouteCall;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.nerdgeeks.foodmap.app.AppConfig.TAG;

/**
 * Created by TAOHID on 3/19/2018.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap gMap;
    private Typeface ThemeFont;
    private Bitmap smallMarker;

    private PlaceDetails placeDetails;
    private ArrayList<DistanceTime> distanceTime;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ThemeFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaNeue.ttf");

        if (gMap == null) {
            getActivity().runOnUiThread(() -> {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapFragment.this);
            });
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setInfoWindowAdapter(this);

        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth()/2)+20,(bitmap.getHeight()/2)+20, false);

        placeDetails = AppData.placeDetails;
        distanceTime = AppData.distanceTimes;

        RestaurantMap();
    }


    private void RestaurantMap(){

        double srcLat = AppData.currentLattitude;
        double srcLng = AppData.currentLongitude;

        String name = placeDetails.getName();
        double destLat = placeDetails.getGeometry().getLocation().getLat();
        double destLng = placeDetails.getGeometry().getLocation().getLng();

        gMap.addMarker(new MarkerOptions().position(new LatLng(srcLat,srcLng))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        Marker desMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(destLat,destLng)).title(name));
        desMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        desMarker.showInfoWindow();

        gMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        getRoutes(new LatLng(srcLat,srcLng), new LatLng(destLat,destLng));
    }

    private void getRoutes(LatLng origin, LatLng dest){

        String origing = origin.latitude+","+origin.longitude;
        String destination = dest.latitude+","+dest.longitude;

        ApiClient.getApiInterface().getRoute(origing,destination).enqueue(new Callback<RouteCall>() {
            @Override
            public void onResponse(@NonNull Call<RouteCall> call, @NonNull Response<RouteCall> response) {

                ArrayList<Route> distanceRoute = response.body().getRoutes();
                AppData.routeArrayList = distanceRoute;

                LatLngBounds bounds = null;

                for (Route route: distanceRoute){

                    LatLng northeast = new LatLng(route.getBounds().getNortheast().getLat(),
                            route.getBounds().getNortheast().getLng());
                    LatLng southeast = new LatLng(route.getBounds().getSouthwest().getLat(),
                            route.getBounds().getSouthwest().getLng());

                    if (northeast.latitude < southeast.latitude){
                        bounds = new LatLngBounds(northeast, southeast);
                    } else {
                        bounds = new LatLngBounds(southeast, northeast);
                    }
                    
                    List<LatLng> routeLatLng = decodePoly(route.getOverviewPolyline().getPoints());
                    gMap.addPolyline(new PolylineOptions().geodesic(true).addAll(routeLatLng).width(10).color(Color.RED).geodesic(true));
                }

                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.20); // offset from edges of the map 20% of screen

                LatLngBounds latLngBounds = adjustBoundsForMaxZoomLevel(bounds);
                if (latLngBounds!= null)
                    gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,padding));
            }

            @Override
            public void onFailure(@NonNull Call<RouteCall> call, @NonNull Throwable t) {
                Log.d(TAG, "Json Api get failed");
            }
        });

    }

    private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
        try{
            LatLng sw = bounds.southwest;
            LatLng ne = bounds.northeast;
            double deltaLat = Math.abs(sw.latitude - ne.latitude);
            double deltaLon = Math.abs(sw.longitude - ne.longitude);

            final double zoomN = 0.005; // minimum zoom coefficient
            if (deltaLat < zoomN) {
                sw = new LatLng(sw.latitude - (zoomN - deltaLat / 2), sw.longitude);
                ne = new LatLng(ne.latitude + (zoomN - deltaLat / 2), ne.longitude);
                bounds = new LatLngBounds(sw, ne);
            }
            else if (deltaLon < zoomN) {
                sw = new LatLng(sw.latitude, sw.longitude - (zoomN - deltaLon / 2));
                ne = new LatLng(ne.latitude, ne.longitude + (zoomN - deltaLon / 2));
                bounds = new LatLngBounds(sw, ne);
            }

            return bounds;
        } catch (Exception ex){
            return null;
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View info = View.inflate(getContext(),R.layout.item_info_window, null);

        TextView title = info.findViewById(R.id.mtitle);
        title.setText(marker.getTitle());

        TextView roundTile = info.findViewById(R.id.msg_thumb);
        try {
            roundTile.setText(String.valueOf(marker.getTitle().charAt(0)));
        } catch (StringIndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
        roundTile.setTypeface(ThemeFont);

        return info;
    }
}
