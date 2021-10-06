package com.example.restaurantapp.RefactoredFiles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.UpdateItem;
import com.example.restaurantapp.SingletonSupport.Update_Singleton;
import com.example.restaurantapp.SingletonSupport.Violations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class InspectionCSVdownloader extends AsyncTask<String, Void, Void> {
        private String restaurantUrl = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
        private String inspectionUrl = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraser_health_restaurant_inspection_reports.csv";
        private Context currentContext;
        private ArrayList<String> saveStrings = new ArrayList<>();
        private HashMap<String, UpdateItem> tempList;
        private static final String SPLIT = "[,|]";
        private boolean isExpectedLength = true;

        private Update_Singleton singleton;
        private static final int INFO_SIZE_DOWNLOADED = 5;

        private static final int COL_TRACKINGNUMBER = 0;
        private static final int COL_INSPECTIONDATE = 1;
        private static final int COL_INSPTYPE = 2;
        private static final int COL_NUMCRITICAL = 3;
        private static final int COL_NUMNONCRITICAL = 4;



    public InspectionCSVdownloader(Context context, HashMap<String, UpdateItem> inputArray){
            currentContext = context;
            tempList = inputArray;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                for (String urls : strings) {
                    // open the url
                    singleton = Update_Singleton.getInstance(currentContext);
                    URL inputUrl = new URL(urls);
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputUrl.openStream()));

                    SQLDatabase database = new SQLDatabase(currentContext);
                    database = database.open();
                    database.StartTransaction();
                    String currentLine = "";
                    Log.i("here", "made it here");

                    // Read the csv data
                    currentLine = inputReader.readLine();
                    while (currentLine != null && currentLine.length() != 0) {
                        String[] parsedLine;
                        // TODO change this string to be in the strings.xml
                        String violationString = "No inspections";
                        currentLine = inputReader.readLine();
                        if(currentLine != null) {
                            parsedLine = currentLine.split(SPLIT);
                            if (parsedLine.length != 0) {
                                //Log.i("LENGTH", "" + parsedLine.length);

                                if (parsedLine.length > 7) {
                                    violationString = createViolationString(parsedLine);
                                }

                                if(tempList.get(parsedLine[COL_TRACKINGNUMBER]) != null){
                                    UpdateItem item = tempList.get(parsedLine[COL_TRACKINGNUMBER]);
                                    String date = item.getDate();
                                    if(Integer.parseInt(date) < Integer.parseInt(parsedLine[COL_INSPECTIONDATE])){
                                        singleton.addToList(new UpdateItem(item.getName(), parsedLine[COL_INSPECTIONDATE], parsedLine[parsedLine.length - 1], parsedLine[COL_TRACKINGNUMBER]));
                                    }
                                }
                                //Log.i("info", "" + parsedLine[COL_TRACKINGNUMBER] + " " + parsedLine[COL_INSPECTIONDATE] + " " + parsedLine[COL_INSPTYPE] + " " + parsedLine[COL_NUMCRITICAL] + " " + parsedLine[COL_NUMNONCRITICAL] + " " + violationString + " " + parsedLine[parsedLine.length - 1]);
                                //Log.i("printing the violation", violationString);
                                database.insertInspectionRow(parsedLine[COL_TRACKINGNUMBER].replace("\"", ""),
                                        parsedLine[COL_INSPECTIONDATE].replace("\"", ""),
                                        parsedLine[COL_INSPTYPE].replace("\"", ""),
                                        parsedLine[COL_NUMCRITICAL].replace("\"", ""),
                                        parsedLine[COL_NUMNONCRITICAL].replace("\"", ""),
                                        violationString, parsedLine[parsedLine.length - 1].replace("\"", ""));

                            }
                        }
                        //Log.i("size is", ""+database.getDatabaseSize());
                    }
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


    private String createViolationString(String[] inputArray){
        String returnString = "";
        String violationCode;
        String hazard;
        String violationString;
        String repetition;

        for(int i = INFO_SIZE_DOWNLOADED; i < inputArray.length - 1; i+=4){

            violationCode = inputArray[i].replace("\"", "");
            hazard = inputArray[i + 1].replace("\"", "");;

            Pair<String, Integer> checkFormatting = reformatString(inputArray, i + 2, violationCode);
            if(checkFormatting != null){
                violationString = checkFormatting.first;
                i += checkFormatting.second;
            }
            else{
                violationString = inputArray[i + 2].replace("\"", "");;
            }

            repetition = inputArray[i + 3].replace("\"", "");
            returnString += violationCode + "@" + hazard + "@" + violationString + "@" + repetition + ",";
        }

        return returnString;

    }

    private Pair<String, Integer> reformatString(String[] array, int index, String code){
        String retString = "";
        Pair<String, Integer> returnPair;
        switch(code){

            case "305":
            case "502":
            case "403":
            case "313":
                retString = array[index].replace("\"", "") + array[index + 1].replace("\"", "");
                returnPair = new Pair<>(retString, 1);
                break;


            case "309":
                retString = array[index].replace("\"", "") + array[index + 1].replace("\"", "") + array[index + 2].replace("\"", "");
                returnPair = new Pair<>(retString, 2);
                break;


            default:
                returnPair = null;

        }
        return returnPair;
    }

}
