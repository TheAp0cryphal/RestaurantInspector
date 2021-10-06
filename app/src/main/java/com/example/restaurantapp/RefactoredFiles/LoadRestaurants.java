package com.example.restaurantapp.RefactoredFiles;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.SQLSingleton;

public class LoadRestaurants extends AsyncTask<Void, Void, Void> {

    Context context;

    public LoadRestaurants(Context inputContext){
        context = inputContext;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        SQLSingleton restaurantSingleton = SQLSingleton.getInstance(context);
        Log.i("ASYNC LOADER FINISHED", "YES");
        return null;
    }

}
