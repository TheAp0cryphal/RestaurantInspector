package com.example.restaurantapp.SingletonSupport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/*
    This is the code for a single cluster item
 */

public class ClusterMarker implements ClusterItem {


    private LatLng position;
    private String title;
    private String snippet;
    private final int mTag;
    private int bitmap;


    public ClusterMarker(LatLng position, String title, String snippet, int mTag, int bitmap) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.mTag = mTag;
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }


    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getTag() {
        return mTag;
    }
    public int getBitmap()
    {
        return bitmap;
    }
}


