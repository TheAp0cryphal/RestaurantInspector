package com.example.restaurantapp.RefactoredFiles;

import android.content.Context;
import android.database.Cursor;

import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.UpdateItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DownloadDriver {

    Context currentContext;
    SQLDatabase db;
    private String restaurantUrl;
    private String inspectionUrl;

    public DownloadDriver(Context context, String restURL, String inspURL){

        currentContext = context;
        restaurantUrl = restURL;
        inspectionUrl = inspURL;
    }

    public void runDownloader(){
        HashMap<String, UpdateItem> updateList = getList();
        currentContext.deleteDatabase("RestaurantDB");
        RestaurantCSVdownloader restaurantDownload = new RestaurantCSVdownloader(currentContext);
        InspectionCSVdownloader inspectionDownload = new InspectionCSVdownloader(currentContext, updateList);

        try {
            restaurantDownload.execute(restaurantUrl).get();
            inspectionDownload.execute(inspectionUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private HashMap<String, UpdateItem> getList(){
        HashMap<String, UpdateItem>  returnList = new HashMap<>();
        SQLDatabase db = new SQLDatabase(currentContext);
        db.open();
        Cursor c = db.getFavourites();
        String name;
        String hazard;
        String date;
        if(c.getCount() != 0){
            do{
                String trackingNumber = c.getString(db.COL_TRACKINGNUMBER);
                Cursor inspection = db.getMostRecentInspection(trackingNumber);
                name = c.getString(db.COL_NAME);
                hazard = inspection.getString(db.COL_HAZARDRATING);
                date = inspection.getString(db.COL_INSPECTIONDATE);
                returnList.put(trackingNumber, new UpdateItem(name, date, hazard, trackingNumber));
            }while(c.moveToNext());
        }

        return returnList;
    }
}
