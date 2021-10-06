package com.example.restaurantapp.SingletonSupport;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/*
    This is an inspection object. From here you can access the general information about
    a specific inspection, or get access to a list of Violations.

 */

public class Inspection{

    private String[] infoArray;
    private static final int INFO_SIZE_DEFAULT = 6;
    private static final int INFO_SIZE_DOWNLOADED = 5;
    private List<Violations> violationList;
    private long databaseIndex;

    // infoArray[0] =  idNumber infoArray[1] = date infoArray[2] = type
    // infoArray[3] = numCritical infoArray[4] = numNonCritical infoArray[5] = HazardRating infoArray[6] = Violations

    public Inspection(String[] inputArray, boolean downloaded, long index){
        this.infoArray = new String[INFO_SIZE_DEFAULT];
        databaseIndex = index;

        if(!downloaded) {
            for (int i = 0; i < INFO_SIZE_DEFAULT; i++) {
                infoArray[i] = inputArray[i].replace("\"", "");
                Log.i("value: ", infoArray[i]);
            }
            //Log.i("size", " " + infoArray.length);
            if (inputArray.length > 7) {
                violationList = new ArrayList<>();
                populateViolations(inputArray);
            }
        }
        else{
            for(int i = 0; i < INFO_SIZE_DOWNLOADED; i++){
                infoArray[i] = inputArray[i].replace("\"", "").replace("@", " ");

            }
            infoArray[5] = inputArray[inputArray.length - 1];

            if (inputArray.length > 7) {
                violationList = new ArrayList<>();
                populateViolationsDownloaded(inputArray);
            }
        }

    }

    public int getDate(){
        return Integer.parseInt(infoArray[1]);
    }

    public String getType(){
        return infoArray[2];
    }

    public String getNumCritical(){
        return infoArray[3];
    }

    public String getNumNonCritical(){
        return infoArray[4];
    }

    public String getHazardRating(){
        return infoArray[5];
    }

    public List<Violations> getViolationList(){
        return violationList;
    }

    private void populateViolations(String[] inputArray){
        for(int i = INFO_SIZE_DEFAULT; i < inputArray.length; i+=4){
            violationList.add(new Violations(inputArray[i], inputArray[i + 1], inputArray[i + 2], inputArray[i + 3]));
        }
    }

    private void populateViolationsDownloaded(String[] inputArray){
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

            violationList.add(new Violations(violationCode, hazard, violationString, repetition));
        }
    }

    public String getFormattedDate(){
        String currentDate = infoArray[1];
        String returnDate = "";
        String year = currentDate.substring(0, 4);
        String month = currentDate.substring(4, 6);
        String day = currentDate.substring(6, 8);

        int monthInt = Integer.parseInt(month);
        switch (monthInt){
            case(1):
                month = "Jan";
                break;
            case(2):
                month = "Feb";
                break;
            case(3):
                month = "Mar";
                break;
            case(4):
                month = "Apr";
                break;
            case(5):
                month = "May";
                break;
            case(6):
                month = "June";
                break;
            case(7):
                month = "July";
                break;
            case(8):
                month = "Aug";
                break;
            case(9):
                month = "Sept";
                break;
            case(10):
                month = "Oct";
                break;
            case(11):
                month = "Nov";
                break;
            case(12):
                month = "Dec";
                break;
        }
        returnDate = month + " " + day + ", " + year;
        return returnDate;
    }

    public int getSumErrors(){
        return Integer.parseInt(getNumCritical())+Integer.parseInt(getNumNonCritical());
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

    public long getDatabaseIndex(){
        return databaseIndex;
    }
}
