package com.example.restaurantapp.SingletonSupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.restaurantapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/*
    This class is for the popup info window that is displayed when the user clicks on a marker

 */
public class mapInfoWindow implements GoogleMap.InfoWindowAdapter {
    private Context currentContext;
    private LayoutInflater inflater;
    private View infoWindow;
    private ClusterMarker cmarker;

    mapInfoWindow(Context context){
        currentContext = context;
        inflater = (LayoutInflater) currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getInfoWindow(Marker marker) {


        infoWindow = inflater.inflate(R.layout.info_window, null);
        TextView title = infoWindow.findViewById(R.id.title);


        title.setText(marker.getTitle() + "\n" + marker.getSnippet());

        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {


        return null;
    }

}