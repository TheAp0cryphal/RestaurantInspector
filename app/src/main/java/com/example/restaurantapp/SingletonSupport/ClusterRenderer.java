package com.example.restaurantapp.SingletonSupport;
//https://codinginfinite.com/android-google-map-custom-marker-clustering/
import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.restaurantapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
/*
    This is the class for making sure the cluster are generated in an efficient manner
    The clusters will merge into numbers if there are many in one area
 */

public class ClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private static final int MARKER_DIMENSION = 120;
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);
    }


    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 4;
    }



    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

        markerImageView.setImageResource(item.getBitmap());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }
}