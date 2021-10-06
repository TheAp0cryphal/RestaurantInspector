package com.example.restaurantapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.RefactoredFiles.DownloadDriver;
import com.example.restaurantapp.RefactoredFiles.InspectionCSVdownloader;
import com.example.restaurantapp.RefactoredFiles.RestaurantCSVdownloader;
import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.Item;

import java.util.concurrent.ExecutionException;

public class TestingActivity extends AppCompatActivity {

    private TextView mTextView;

    private String restaurantUrl = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private String inspectionUrl = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraser_health_restaurant_inspection_reports.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mTextView = (TextView) findViewById(R.id.text);
        TextView text2 = (TextView) findViewById(R.id.testingText);
        Log.i("started...", "");

        DownloadDriver driver = new DownloadDriver(this, restaurantUrl, inspectionUrl);
        driver.runDownloader();
        //pizza pit


        SQLDatabase database = new SQLDatabase(this);
        database.open();
        //                         database.insertRestaurantRow(parsedLine[COL_TRACKINGNUMBER].replace("\"", ""),
        //                         parsedLine[COL_NAME].replace("\"", ""),
        //                         parsedLine[COL_PHYSICALADDRESS].replace("\"", ""),
        //                         parsedLine[COL_PHYSICALCITY].replace("\"", ""),
        //                         parsedLine[COL_FACTYPE].replace("\"", ""),
        //                         parsedLine[COL_LATITUDE].replace("\"", ""),
        //                         parsedLine[COL_LONGITUDE].replace("\"", ""));
        Cursor c = database.getCriticalGreaterOrEqual(30);
        //Cursor c = database.getAllInspections(trackingNumber);
        Log.i("size of cursor", "" + c.getCount());
        int counter = 0;

        String name;
        String address;
        String factype;
        String latitude;
        String longitude;
        int favourite;
        long index;
        if (c.moveToFirst()) {
            Log.i("found", "we made it here");
            //c.moveToNext();
            do {
                /*
                name = c.getString(database.COL_NAME);
                index = c.getLong(database.COL_RESTAURANT_ROWID);
                address = c.getString(database.COL_PHYSICALADDRESS);
                factype = c.getString(database.COL_FACTYPE);
                latitude = c.getString(database.COL_LATITUDE);
                longitude = c.getString(database.COL_LONGITUDE);
                 */
                //favourite = c.getInt(database.COL_FAVOURITE);


                String critical = c.getString(database.COL_NAME);
                Log.i("printing hazard", critical);
                //Log.i("", name + " " + index + " " + address + " " + factype + " " + latitude + " " + longitude + " " + latitude + " " + " " + counter);
                counter++;
            } while (c.moveToNext());

        }

        else{
            Log.i("result", "no items found");
        }


            //Log.i("size is: ", ""+database.getDatabaseSize());

            long id = 10;
            Cursor cursor = database.getRestaurantRow(id);
            //Log.i("counter", ""+cursor.getCount());
            //Log.i("tables", "" + database.countTables());

        /*
            String trackingNumber = cursor.getString(database.COL_TRACKINGNUMBER);
            trackingNumber.replace("\"", "");
            mTextView.setText(trackingNumber);


            Cursor cursor1 = database.getMostRecentInspection(trackingNumber);
            Log.i("counter", "" + cursor1.getCount());
            text2.setText(cursor1.getString(database.COL_INSPECTIONDATE));
*/

            database.close();
           // cursor1.close();
            cursor.close();
            c.close();
            //f.getParentFile().mkdirs()

            // ** IMPORTANT. put the restaurant url first, and the inspection url second. IMPORTANT **

            // context.getFileStreamPath(name).getAbsolutePath()


        /*
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8));) {

            String line;

            while ((line = br.readLine()) != null) {

                System.out.println(line);
            }
        }
         */


            // Enables Always-on
    }
}
