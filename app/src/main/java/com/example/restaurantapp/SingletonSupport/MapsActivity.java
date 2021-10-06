package com.example.restaurantapp.SingletonSupport;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.restaurantapp.R;
import com.example.restaurantapp.RestaurantActivity;
import com.example.restaurantapp.SQL.SQLDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/*
    Maps activity is where the user will interact with all the restaurants on the map
    the user can click on a marker and be brought to the restaurant Activity

 */

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    public static final String Extra_Num = "com.example.restaurantapp.SingletonSupport.Extra_Num";
    private GoogleMap mMap;
    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    FusedLocationProviderClient client;
    private ClusterManager<ClusterMarker> clusterManager;
    private List<Inspection> inspectList = new ArrayList<>();
    public static ArrayList<Item> list = new ArrayList<>();
    Inspection currentInspection;
    private static final String TAG = "MapsActivityTab";
    Restaurant restaurant;
    private boolean permissionDenied = false;
    PersistableBundle bundle;
    RestaurantManager manager;
    private int restaurantIndex;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_maps, container, false);
        MapsInit();
        TabbedActivity activity = (TabbedActivity) getActivity();

        SQLSingleton singleton = SQLSingleton.getInstance(getContext());
        if(singleton.getSearchList().size() != 0){
            list = singleton.getSearchList();
            Log.i("fancy", "list");
        }
        else{
            list = TabbedActivity.list;
            Log.i("generic", "list");
        }
        restaurantIndex = activity.getIndex();
        if (restaurantIndex != -1) {
            // do something here;
        }
        client = LocationServices.getFusedLocationProviderClient(getContext());

        return view;
    }

    private void showSelectedMarker(int restaurantIndex) {

        double lat = Double.parseDouble(manager.getList().get(restaurantIndex).getLatitude());
        double lng = Double.parseDouble(manager.getList().get(restaurantIndex).getLongitude());
        LatLng restLoc = new LatLng(lat, lng);
        LatLng mountainView = new LatLng(37.4, -122.1);

// Move the camera instantly to Sydney with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restLoc, 15));

// Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

// Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(restLoc)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    public void MapsInit() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        requestLocationPermission();

        mMap = googleMap;


        clusterManager = new ClusterManager<>(getContext(), mMap);
        clusterManager.setRenderer(new ClusterRenderer(getContext(), googleMap, clusterManager));
        Collection<Marker> collection;


        for (int i = 0; i < list.size(); i++) {
            String infoString;
            float colour = 0;
            int imgsrc = 0;

            //Sets each restaurant's longitude/latitude on the map
            Double lat = Double.parseDouble(list.get(i).getLatitude());
            Double lon = Double.parseDouble(list.get(i).getLongitude());

            LatLng location = new LatLng(lat, lon);
            //Sets each restaurant's map icon and info
            String haz = list.get(i).getHazardLevel();
            if (haz != null) {

                infoString = "Address: " + list.get(i).getAddress() + "\n" + "Recent Hazard: " + haz;

                if (haz.equals("High")) {
                    imgsrc = R.drawable.high;

                } else if (haz.equals("Moderate")) {
                    imgsrc = R.drawable.moderate;


                } else if (haz.equals("Low")) {
                    imgsrc = R.drawable.low;

                }
            } else {
                infoString = "Address: " + list.get(i).getAddress() + "\n" + "Recent Hazard: No Recent Inspections";
                imgsrc = R.drawable.res;

            }
            // mMap.addMarker(new MarkerOptions().title(restaurant.getName()).snippet(infoString).position(location).icon(BitmapDescriptorFactory.defaultMarker(colour)));
            ClusterMarker marker = new ClusterMarker(location, list.get(i).getName(), infoString, i, imgsrc);

            clusterManager.addItem(marker);

        }
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);
        }
        userLocation();

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnInfoWindowClickListener(clusterManager);


        clusterManager.getMarkerCollection()
                .setInfoWindowAdapter(new mapInfoWindow(getContext()));

        //Opens restaurant activity for a specific restaurant when restaurant's map icon is clicked

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarker item) {
                int index = item.getTag();
                long databaseIndex = list.get(index).getIndex();
                Intent i = new Intent(getContext(), RestaurantActivity.class);
                Log.i("printing index", "" + databaseIndex);
                i.putExtra(Extra_Num, databaseIndex);
                i.putExtra("Extra_index", index);
                startActivity(i);
            }
        });


        clusterManager.cluster();


        if (restaurantIndex != -1) {
            showSelectedMarker(restaurantIndex);
        }



        /*
        requestLocationPermission();
        SQLDatabase db = new SQLDatabase(getContext());
        db.open();
        mMap = googleMap;
        int counter = 0;

        clusterManager = new ClusterManager<>(getContext(), mMap);
        clusterManager.setRenderer(new ClusterRenderer(getContext(), googleMap, clusterManager));
        Collection<Marker> collection;


        Cursor restaurantCursor = db.getAllRestaurantRows();
        if(restaurantCursor.getCount() != 0){
            do{
                String trackingNumber = restaurantCursor.getString(db.COL_TRACKINGNUMBER);
                String name = restaurantCursor.getString(db.COL_NAME);
                String address = restaurantCursor.getString(db.COL_PHYSICALADDRESS);

                Double latitude = Double.parseDouble(restaurantCursor.getString(db.COL_LATITUDE));
                Double longitude = Double.parseDouble(restaurantCursor.getString(db.COL_LONGITUDE));
                String infoString;

                float colour = 0;
                int imgsrc = 0;
                Cursor inspectionCursor = db.getMostRecentInspection(trackingNumber);
                LatLng location = new LatLng(latitude, longitude);

                if(inspectionCursor.getCount() != 0){
                    String haz = inspectionCursor.getString(db.COL_HAZARDRATING);

                    String Address=getResources().getString(R.string.Address);
                    String Recent_Hazard=getResources().getString(R.string.Recent_Hazard);

                    if (haz.equals("High")) {
                        haz=getResources().getString(R.string.High);
                        imgsrc = R.drawable.high;
                    } else if (haz.equals("Moderate")) {
                        haz=getResources().getString(R.string.Moderate);
                        imgsrc = R.drawable.moderate;
                    } else if (haz.equals("Low")) {
                        haz=getResources().getString(R.string.Low);
                        imgsrc = R.drawable.low;
                    }
                    infoString = Address + address + "\n" + Recent_Hazard + haz;
                }
                else {
                    String Address=getResources().getString(R.string.Address);
                    String No_Recent_Inspections=getResources().getString(R.string.No_Recent_Inspections);
                    infoString = Address + address + "\n" +No_Recent_Inspections;
                    imgsrc = R.drawable.res;
                }
                // maybe change counter to the physical row in the db
                ClusterMarker marker = new ClusterMarker(location, name, infoString, counter, imgsrc);
                clusterManager.addItem(marker);
                counter++;

                inspectionCursor.close();

            } while(restaurantCursor.moveToNext());
            restaurantCursor.close();

        }
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);
        }
        userLocation();

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnInfoWindowClickListener(clusterManager);
        clusterManager.getMarkerCollection().setInfoWindowAdapter(new mapInfoWindow(getContext()));
        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarker item) {
                int index = item.getTag();
                Intent i = new Intent(getContext(), RestaurantActivity.class);
                i.putExtra(Extra_Num, index);
                startActivity(i);

            }
        });
        clusterManager.cluster();
        if (restaurantIndex != -1) {
            showSelectedMarker(restaurantIndex);
        }

        db.close();

         */
}

    @AfterPermissionGranted(123)
    private void requestLocationPermission() {

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            String Location_permissions_granted=getResources().getString(R.string.Location_permissions_granted);
            Toast.makeText(getContext(), Location_permissions_granted, Toast.LENGTH_SHORT).show();
            onPermissionsGranted(123, perms);

        } else {
            String The_app_needs_location_permissions_to_function_optimally=getResources().getString(R.string.The_app_needs_location_permissions_to_function_optimally);
            EasyPermissions.requestPermissions(this, The_app_needs_location_permissions_to_function_optimally,
                    123, perms);

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String OnRequestPermissionResult=getResources().getString(R.string.OnRequestPermissionResult);
        Toast.makeText(getContext(), OnRequestPermissionResult, Toast.LENGTH_SHORT).show();
        // Forward results to EasyPermissions

        for (int i = 0; i < grantResults.length; i++) {

            Toast.makeText(getContext(), "GrantResult", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), Integer.toString(grantResults[i]), Toast.LENGTH_SHORT).show();
            if (grantResults[i] == -1) {
                onPermissionsDenied(123, perms);
            } else {
                onPermissionsGranted(123, perms);
                userLocation();
            }
        }

        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private void onPermissionsGranted(int requestCode, @NonNull String[] perms) {
        String OnPermissionGranted=getResources().getString(R.string.OnPermissionGranted);
        Toast.makeText(getContext(), OnPermissionGranted, Toast.LENGTH_SHORT).show();

    }

    private void onPermissionsDenied(int requestCode, @NonNull String[] perms) {
        String OnPermissionDenied=getResources().getString(R.string.OnPermissionDenied);
        Toast.makeText(getContext(), OnPermissionDenied, Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) getContext(), Arrays.asList(perms))) {
            String Location_Permission_Required=getResources().getString(R.string.Location_Permission_Required);
            String The_app_needs_location_permissions_to_function_optimally=getResources().getString(R.string.The_app_needs_location_permissions_to_function_optimally);
            String Setting=getResources().getString(R.string.Settings);
            String Use_Anyway=getResources().getString(R.string.Use_Anyway);



            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(Location_Permission_Required)
                    // set dialog message

                    .setMessage(The_app_needs_location_permissions_to_function_optimally)
                    .setCancelable(false)
                    .setPositiveButton(Setting, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getActivity().getPackageName()));

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            startActivityForResult(intent, 16061);
                        }
                    })
                    .setNegativeButton(Use_Anyway, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            userLocation();
        }
    }

    private void userLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                if (restaurantIndex == -1)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 20));
            }
        });

    }


}



