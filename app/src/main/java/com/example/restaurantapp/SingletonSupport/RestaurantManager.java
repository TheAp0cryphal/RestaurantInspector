package com.example.restaurantapp.SingletonSupport;
import android.content.Context;
import android.util.Log;


import com.example.restaurantapp.InspectionActivity;
import com.example.restaurantapp.MainActivity;
import com.example.restaurantapp.R;
import com.example.restaurantapp.RestaurantActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


/*
    REFERENCES:
    for reading csv file: https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
 */

/*
    This is the Restaurant manager class. This is how you access specific restaurants.
    This class reads the Restaurant CSV file and uses that information to generate all of
    the Restaurant objects.

    HOW TO USE:
    1. in your class write RestaurantManager manager = RestaurantManager.getInstance();
    2. To access a specific restaurant do Restaurant restaurant = manager.getRestaurant(index);
    3. From there you can do int totalErrors = restaurant.getSumErrors() to get the total errors;
    4. there is other information you can access in the Restaurant and Inspection classes as well.
 */

public class RestaurantManager{

    private static RestaurantManager instance;
    private static ArrayList<Restaurant> restaurantList;
    private static final String SPLIT = ",";
    private InputStream restaurantInputStream;
    private Map<String, ArrayList<Inspection>> inspectionMap;
    private boolean isDownloaded;
    Context context;

    private RestaurantManager(Context current) throws IOException {
        context = current;
        // WE NEED TO CHECK IF THE CSV FILES HAVE BEEN MADE. IF THEY HAVE THEN USE THOSE
        //new FileInputStream(this.getFileStreamPath("restaurants.csv").getAbsolutePath()))

        File file = new File(context.getFileStreamPath("restaurants.csv").getAbsolutePath());
        if(file.length() == 0) {
            restaurantInputStream = context.getResources().openRawResource(R.raw.restaurants_itr1);
            isDownloaded = false;
        }
        else{
            restaurantInputStream = new FileInputStream(context.getFileStreamPath("restaurants.csv").getAbsolutePath());
            isDownloaded = true;
        }
        CsvReader inspectionReader = new CsvReader(isDownloaded, current);
        inspectionMap = inspectionReader.getInspectionMap();
        restaurantList = ReadRestaurantCsv();
        sortList();
    }

    public static RestaurantManager getInstance(MainActivity current) throws IOException {
        if(instance == null){
            instance = new RestaurantManager(current.getContext());
        }
        return instance;
    }
    public static RestaurantManager getInstance(MapsActivity current) throws IOException {
        if(instance == null){
            instance = new RestaurantManager(current.getContext());
        }
        return instance;
    }
    public static RestaurantManager getInstance(InspectionActivity current) throws IOException {
        if(instance == null){
            instance = new RestaurantManager(current);
        }
        return instance;
    }
    public static RestaurantManager getInstance(RestaurantActivity current) throws IOException {
        if(instance == null){
            instance = new RestaurantManager(current);
        }
        return instance;
    }



    private ArrayList<Restaurant> ReadRestaurantCsv(){
        ArrayList<Restaurant> returnList = new ArrayList<>();
        String[] currentParsedLine;
        String line = "";

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(restaurantInputStream))){
            line = reader.readLine(); // Read an extra line to skip the top information

            while((line = reader.readLine()) != null && line.length() != 0){

                currentParsedLine = line.split(SPLIT);

                if(currentParsedLine.length != 0){
                    ArrayList<Inspection> tempList = inspectionMap.get(currentParsedLine[0]);
                    Restaurant currentRestaurant = new Restaurant(currentParsedLine, context, tempList);
                    returnList.add(currentRestaurant);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return returnList;
    }

    public Restaurant getRestaurant(int index){
        return restaurantList.get(index);

    }


    private void sortList(){
        Collections.sort(restaurantList, new sortByName());
    }

    public ArrayList<Restaurant> getList() { return restaurantList; }

    public int getManagerSize() {
        return restaurantList.size();
    }

}

class sortByName implements Comparator<Restaurant> {

    public int compare(Restaurant restaurant1, Restaurant restaurant2)
    {
        return restaurant1.getName().compareTo(restaurant2.getName());
    }
}
