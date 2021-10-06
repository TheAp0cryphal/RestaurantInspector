package com.example.restaurantapp.RefactoredFiles;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.SQLSingleton;

public class AsyncSearch extends AsyncTask<Void, Void, String> {

    Context context;
    String name;
    String favourite;
    String hazard;
    String lessThan;
    String greaterThan;


    public AsyncSearch(Context c, String Name, String Favourite, String Hazard, String LessThan, String GreaterThan){
        context = c;
        name = Name;
        favourite = Favourite;
        hazard = Hazard;
        lessThan = LessThan;
        greaterThan = GreaterThan;
    }
    @Override
    protected String doInBackground(Void... voids) {
        SQLSingleton restaurantSingleton = SQLSingleton.getInstance(context);
        restaurantSingleton.runSearch(context, name, favourite, hazard, lessThan, greaterThan);
        Log.i("search Ran", "finished");
        Log.i("printing size", ""+ restaurantSingleton.getSearchList().size());
        return null;
    }

}
