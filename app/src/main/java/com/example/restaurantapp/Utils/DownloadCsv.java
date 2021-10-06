package com.example.restaurantapp.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadCsv extends AsyncTask<String, Void, Void> {
    private String restaurantUrl = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private String inspectionUrl = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraser_health_restaurant_inspection_reports.csv";
    private Context currentContext;
    private ArrayList<String> saveStrings = new ArrayList<>();

    public DownloadCsv(Context context){
        currentContext = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            for(String urls : strings) {
                // open the url

                URL inputUrl = new URL(urls);
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputUrl.openStream()));

                StringBuilder buildString = new StringBuilder();
                String currentLine = "";

                // Read the csv data
                currentLine = inputReader.readLine();
                int counter = 0;
                while (currentLine != null) {
                    buildString.append(currentLine);
                    buildString.append("\n");
                    currentLine = inputReader.readLine();
                    counter++;
                }

                Log.i("numLines", ""+counter);


                //buildString.replace(buildString.length() - 1, buildString.length(), "\0");


                inputReader.close();
                String saveInfo = buildString.toString();
                // saveStrings now has both the csv files
                saveStrings.add(saveInfo);
            }

            Log.i("csv: ", "done");
            /*
                File dir = getFilesDir();
                  File file = new File(dir, "my_filename");
                boolean deleted = file.delete();\


             File file = new File(context.getFileStreamPath("inspection.csv").getAbsolutePath());
            if(file.length() == 0) {
            inspectionInputStream = context.getResources().openRawResource(R.raw.inspectionsreports_itr1);
            Log.i("Default Restaurant: ", "true");
        }
             */

            File file = new File(currentContext.getFileStreamPath("inspection.csv").getAbsolutePath());
            File file1 = new File(currentContext.getFileStreamPath("restaurant.csv").getAbsolutePath());

            if(file.length() == 0 && file1.length() == 0) {

                FileOutputStream restaurantOutput = currentContext.openFileOutput("restaurants.csv", Context.MODE_PRIVATE);
                FileOutputStream inspectionOutput = currentContext.openFileOutput("inspection.csv", Context.MODE_PRIVATE);

                restaurantOutput.write(saveStrings.get(0).getBytes());
                inspectionOutput.write(saveStrings.get(1).getBytes());

                restaurantOutput.close();
                inspectionOutput.close();

                FileInputStream is = currentContext.openFileInput("inspection.csv");
                int available = is.available();
                is.close();

                Log.i("isRead: ", "" + available);
            }
            else{
                file.delete();
                file1.delete();

                FileOutputStream restaurantOutput = currentContext.openFileOutput("restaurants.csv", Context.MODE_PRIVATE);
                FileOutputStream inspectionOutput = currentContext.openFileOutput("inspection.csv", Context.MODE_PRIVATE);

                restaurantOutput.write(saveStrings.get(0).getBytes());
                inspectionOutput.write(saveStrings.get(1).getBytes());

                restaurantOutput.close();
                inspectionOutput.close();

                FileInputStream is = currentContext.openFileInput("inspection.csv");
                int available = is.available();
                is.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
