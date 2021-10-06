package com.example.restaurantapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.Item;
import com.example.restaurantapp.SingletonSupport.ItemAdapter;
import com.example.restaurantapp.SingletonSupport.Restaurant;
import com.example.restaurantapp.SingletonSupport.Inspection;
import com.example.restaurantapp.SingletonSupport.RestaurantManager;
import com.example.restaurantapp.SingletonSupport.SQLSingleton;
import com.example.restaurantapp.SingletonSupport.TabbedActivity;
import com.example.restaurantapp.SingletonSupport.UpdateItem;
import com.example.restaurantapp.SingletonSupport.Update_Singleton;
import com.example.restaurantapp.Utils.DownloadCsv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Fragment {
    private String restaurantUrl = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private String inspectionUrl = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraser_health_restaurant_inspection_reports.csv";

    public static final String Extra_Num = "com.example.restaurantapp.SingletonSupport.Extra_Num";

    private Timer myTimer;
    private int TIMERINTERVAL = 3000;
    private RestaurantManager manager;
    private RecyclerView mView;
    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Item> list = new ArrayList<>();
    private static final String TAG = "MainActivityTab";
    private SQLDatabase db;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.notifyDataSetChanged();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_main, container, false);
        //Log.i("size:" , ""+db.getDatabaseSize());

        SQLSingleton singleton = SQLSingleton.getInstance(getContext());
        Update_Singleton uSingle = Update_Singleton.getInstance(getContext());

        if((uSingle.getUpdateItems().size() != 0)) {
            displayDialogue();
        }

        if(singleton.getSearchList().size() != 0){
            list = singleton.getSearchList();
            Log.i("fancy", "list");
        }
        else{
            list = TabbedActivity.list;
            Log.i("generic", "list" + list.size());
        }
        populateRecyclerView(view);

        return view;
    }


    private void populateRecyclerView(View v) {
        Log.i("populate", "start");
        mView = v.findViewById(R.id.recyclerView);
        mView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        mView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ItemAdapter(getContext(),list);

        mView.setLayoutManager(mLayoutManager);
        mView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new ItemAdapter.OnItemClickListener(){

            //Calls restaurant activity when icon clicked
            @Override
            public void OnClick(int position) {
                long databaseIndex = list.get(position).getIndex();
                Intent i = new Intent(getContext(), RestaurantActivity.class);
                i.putExtra(Extra_Num, databaseIndex);
                i.putExtra("Extra_index", position);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    //Calculates most recent inspection date displayed based on current date unless inspections empty
    public String calDateDif(int mostRecentDate){
        String No_inspections = getResources().getString(R.string.No_inspections);
        if(mostRecentDate==0) return No_inspections;
        String s=(""+mostRecentDate);
        int myear=Integer.parseInt(s.substring(0,4));
        int mmonth=Integer.parseInt(s.substring(4,6));
        int mday=Integer.parseInt(s.substring(6,8));

        LocalDate from = LocalDate.of(myear, mmonth, mday);
        long day = LocalDate.now().toEpochDay() - from.toEpochDay();

        String[] Month = {"0","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String month=" ";
        switch (Month[mmonth]) {
            case "January":
                month = getResources().getString(R.string.January);
                break;
            case "February":
                month = getResources().getString(R.string.February);
                break;
            case "March":
                month = getResources().getString(R.string.March);
                break;
            case "April":
                month = getResources().getString(R.string.April);
                break;
            case "May":
                month = getResources().getString(R.string.May);
                break;
            case "June":
                month = getResources().getString(R.string.June);
                break;
            case "July":
                month = getResources().getString(R.string.July);
                break;
            case "August":
                month = getResources().getString(R.string.August);
                break;
            case "September":
                month = getResources().getString(R.string.September);
                break;
            case "October":
                month = getResources().getString(R.string.October);
                break;
            case "November":
                month = getResources().getString(R.string.November);
                break;
            case "December":
                month =getResources().getString(R.string.December);
                break;
            default:
                break;
        }
        if(day<=30){
            return (""+day+" days");
        }
        else if(day<=365) {

            return (""+month+" "+mday);
        }
        else {
            return (""+month+" "+myear);
        }
    }

    private void displayDialogue(){

        Update_Singleton singleton = Update_Singleton.getInstance(getContext());
        ArrayList<UpdateItem> list = singleton.getUpdateItems();
        ArrayList<String> stringList = new ArrayList<>();
        CharSequence[] cs;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.New_inspections);
        // name date hazard.
        for(UpdateItem items : list){
            String name = items.getName();
            String date = items.getDate();
            String hazard = items.getHazard();
            stringList.add(name + "\n" + date + "\t" + hazard);
        }

        cs = stringList.toArray(new CharSequence[stringList.size()]);


        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // RESTAURANT.getInspection and hazard???

                }
                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage("Last Inspection" + "li\n" + "Recent Hazard");
                builderInner.setTitle("Res name?");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}




