package com.nerdgeeks.foodmap.MapCluster;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by TAOHID on 3/19/2018.
 */

public class ClusterRenderer extends DefaultClusterRenderer<ClusterModel> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterModel> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterModel markerItem, MarkerOptions markerOptions) {
        if (markerItem.getIcon() != null) {
            markerOptions.icon(markerItem.getIcon());
        }
    }
}