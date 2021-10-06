package com.example.restaurantapp.RefactoredFiles;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantapp.SQL.SQLDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantCSVdownloader extends AsyncTask<String, Void, Void> {

    private String restaurantUrl = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private String inspectionUrl = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraser_health_restaurant_inspection_reports.csv";
    private Context currentContext;
    private ArrayList<String> saveStrings = new ArrayList<>();
    private static final String SPLIT = "[,|]";
    private boolean isExpectedLength = true;
    private static final int COL_TRACKINGNUMBER = 0;
    private static final int COL_NAME = 1;
    private static final int COL_PHYSICALADDRESS = 2;
    private static final int COL_PHYSICALCITY = 3;
    private static final int COL_FACTYPE = 4;
    private static final int COL_LATITUDE = 5;
    private static final int COL_LONGITUDE = 6;

    private static final int EXPECTED_LENGTH = 7;


    public RestaurantCSVdownloader(Context context) {
        currentContext = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            for (String urls : strings) {
                // open the url

                URL inputUrl = new URL(urls);
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputUrl.openStream()));
                SQLDatabase database = new SQLDatabase(currentContext).open();
                database.StartTransaction();
                Log.i("here", "made it here but for restaurants");
                String currentLine = "";
                currentLine = inputReader.readLine();
                while (currentLine != null && currentLine.length() != 0) {
                    String[] parsedLine;
                    currentLine = inputReader.readLine();
                    if (currentLine != null) {
                        parsedLine = currentLine.split(SPLIT);
                        if(parsedLine.length != EXPECTED_LENGTH){
                            parsedLine = reformatRestaurantStrings(parsedLine);
                        }
                        database.insertRestaurantRow(parsedLine[COL_TRACKINGNUMBER].replace("\"", ""), parsedLine[COL_NAME].replace("\"", ""), parsedLine[COL_PHYSICALADDRESS].replace("\"", ""), parsedLine[COL_PHYSICALCITY].replace("\"", ""), parsedLine[COL_FACTYPE].replace("\"", ""), parsedLine[COL_LATITUDE].replace("\"", ""), parsedLine[COL_LONGITUDE].replace("\"", ""));
                    }
                }
                Log.i("compare size: ", "" + database.getDatabaseSize());
                database.SetAndEnd();
                database.close();
                inputReader.close();
            }

            Log.i("csv: ", "done");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String[] reformatRestaurantStrings(String[] inputArray) {
        String[] outputArray = new String[EXPECTED_LENGTH];
        // Reformats the information when the csv file has weird comma splicing
        int counter = 0;
        int numValsToRead = inputArray.length - EXPECTED_LENGTH;
        Log.i("#", "" + numValsToRead);
        Log.i("nameInfo: ", inputArray[0] + " " + inputArray[1] + " " + inputArray[2]);
        String reformattedString = "";
        while (counter <= numValsToRead) {
            reformattedString += inputArray[counter + 1];
            counter++;
        }
        outputArray[0] = inputArray[0];
        outputArray[1] = reformattedString;
        for (int i = 2; i < outputArray.length; i++) {
            // i == 1 means this is the restaurant index, concatenate spliced restaurant text

            outputArray[i] = inputArray[i + numValsToRead];
            Log.i("info: ", outputArray[i]);
        }


        return outputArray;
    }
}