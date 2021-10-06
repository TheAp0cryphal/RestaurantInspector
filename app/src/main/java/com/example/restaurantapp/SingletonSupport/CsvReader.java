package com.example.restaurantapp.SingletonSupport;

import android.content.Context;
import android.util.Log;

import com.example.restaurantapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    This is a file for reading the CSV files
    it will store all the information locally
 */

public class CsvReader {

    private InputStream inspectionInputStream;
    private boolean isDownloaded;
    private Map<String, ArrayList<Inspection>> inspectionMap = new HashMap<>();
    private static final String SPLIT = "[,|]";

    public CsvReader(boolean downloaded, Context context) throws IOException {
        File file = new File(context.getFileStreamPath("inspection.csv").getAbsolutePath());
        if(file.length() == 0) {
            inspectionInputStream = context.getResources().openRawResource(R.raw.inspectionsreports_itr1);
            Log.i("Default Restaurant: ", "true");
        }
        else{
            //inspectionInputStream = context.getResources().openRawResource(R.raw.inspectionsreports_itr1);
            inspectionInputStream = new FileInputStream(context.getFileStreamPath("inspection.csv").getAbsolutePath());
            isDownloaded = true;
        }
        isDownloaded = downloaded;
        ReadInspectionCsv();
    }


    private void ReadInspectionCsv() throws IOException {
        String[] currentParsedLine;
        String line = "";
        int counter = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inspectionInputStream))) {

            line = reader.readLine(); // Read an extra line to skip the top information

            while ((line = reader.readLine()) != null && line.length() != 0) {

                currentParsedLine = line.split(SPLIT);
                if (currentParsedLine.length != 0) {
                    /*
                    for(int i = 0; i < currentParsedLine.length; i++){
                        Log.i("info: ", currentParsedLine[i]);
                    }

                     */
                    Inspection current = new Inspection(currentParsedLine, isDownloaded, counter);
                    if (inspectionMap.get(currentParsedLine[0]) == null) {
                        ArrayList<Inspection> tempList = new ArrayList<>();
                        tempList.add(current);
                        inspectionMap.put(currentParsedLine[0], tempList);
                    } else {
                        ArrayList<Inspection> currentList = inspectionMap.get(currentParsedLine[0]);
                        currentList.add(current);
                        inspectionMap.put(currentParsedLine[0], currentList);
                    }
                    counter++;
                }
            }
        }
    }
    public Map<String, ArrayList<Inspection>> getInspectionMap(){
        return inspectionMap;
    }
}
