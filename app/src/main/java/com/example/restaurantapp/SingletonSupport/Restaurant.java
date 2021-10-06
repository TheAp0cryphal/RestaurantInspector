package com.example.restaurantapp.SingletonSupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.restaurantapp.R;

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
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/*
    This is the Restaurant class. From here you can access all the information about
    the Restaurant, and get the list of inspections. This class also reads the inspection CSV
    file and generates all of the inspections.
 */


public class Restaurant{

    private String[] infoArray;
    private ArrayList<Inspection> inspectionList;
    Context context;
    private static final int EXPECTED_LENGTH = 7;
    private boolean bool;



    // infoArray[0] =  idNumber infoArray[1] = name infoArray[2] = address
    // infoArray[3] = city infoArray[4] = type infoArray[5] = latitude infoArray[6] = longitude

    public Restaurant(String[] inputArray, Context current, ArrayList<Inspection> inputList){
        context = current;
        infoArray = new String[EXPECTED_LENGTH];
        boolean isExpectedLength = (inputArray.length == EXPECTED_LENGTH);
        bool = false;

        if(isExpectedLength) {
            // typical line, ie no splicing issues
            for (int i = 0; i < inputArray.length; i++) {
                infoArray[i] = inputArray[i];
            }
        }
        else{
            // Reformats the information when the csv file has weird comma splicing
            int counter = 0;
            int numValsToRead = inputArray.length - EXPECTED_LENGTH;
            Log.i("#", ""+numValsToRead);
            Log.i("nameInfo: ", inputArray[0] + " " + inputArray[1] + " " + inputArray[2]);
            String reformattedString = "";
            while(counter <= numValsToRead){
                reformattedString += inputArray[counter + 1];
                counter++;
            }
            infoArray[0] = inputArray[0];
            infoArray[1] = reformattedString;
            for(int i = 2; i < infoArray.length; i++){
                // i == 1 means this is the restaurant index, concatenate spliced restaurant text

                infoArray[i] = inputArray[i + numValsToRead];
                Log.i("info: ", infoArray[i]);
            }

        }
        inspectionList = inputList;
        if(inspectionList != null) {
            listSort();
        }
    }



    public int getListSize(){
        if(inspectionList != null) {
            return inspectionList.size();
        }
        else{
            return 0;
        }
    }

    public String[] getAllInfo(){
        return infoArray;
    }

    public String getName(){
        return infoArray[1].replace("\"", "");
    }

    public String getAddress(){
        return infoArray[2].replace("\"","");
    }

    public String getCity(){
        return infoArray[3];
    }

    public String getType(){
        return infoArray[4];
    }

    public String getLatitude(){
        return infoArray[5];
    }

    public String getLongitude(){
        return infoArray[6];
    }

    public Inspection getInspection(int index){
        return inspectionList.get(index);
    }

    public ArrayList<Inspection> getInspectionList(){return inspectionList;}

    public boolean getFavourite(){return bool;}

    public void setFavourite(boolean bool)
    {
        this.bool = bool;
    }

    private void listSort(){
        Collections.sort(inspectionList, new sortByDate());
    }

}

class sortByDate implements Comparator<Inspection>{

    public int compare(Inspection inspection1, Inspection inspection2)
    {
        return inspection2.getDate() - inspection1.getDate();
    }
}
