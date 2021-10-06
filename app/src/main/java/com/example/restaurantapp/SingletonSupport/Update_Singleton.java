package com.example.restaurantapp.SingletonSupport;

import android.content.Context;
import android.database.Cursor;

import com.example.restaurantapp.SQL.SQLDatabase;

import java.util.ArrayList;

public class Update_Singleton {
    ArrayList<UpdateItem> updateItems = new ArrayList<>();
    private static Update_Singleton instance;

    public Update_Singleton(Context context){

    }

    public static Update_Singleton getInstance(Context context) {
        if (instance == null) {
            instance = new Update_Singleton(context);
        }
        return instance;
    }

    public void addToList(UpdateItem item){
        updateItems.add(item);
    }

    public ArrayList<UpdateItem> getUpdateItems(){
        return updateItems;
    }

}
