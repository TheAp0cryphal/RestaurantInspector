package com.example.restaurantapp.SingletonSupport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.example.restaurantapp.MainActivity;
import com.example.restaurantapp.R;
import com.example.restaurantapp.RefactoredFiles.AsyncSearch;
import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SearchActivity;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TabbedActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private int restaurantIndex = -1;
    private int activity = -1;
    public static ArrayList<Item> list = new ArrayList<>();
    public static ArrayList<Item> searchList = new ArrayList<>();
    private SQLSingleton restaurantSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        viewPager = findViewById(R.id.view_pager);

        restaurantSingleton = SQLSingleton.getInstance(this);
        list = restaurantSingleton.getList();
        searchList = restaurantSingleton.getSearchList();
        Log.i("GOT LIST", "LIST IS GOTTEN");
        setup(viewPager);
        TabLayout tabLayout = (findViewById(R.id.tabs));
        tabLayout.setupWithViewPager(viewPager);

        Intent restaurantIntent = getIntent();
        restaurantIndex = restaurantIntent.getIntExtra("index", -1);
        activity = restaurantIntent.getIntExtra("Res", -1);
        if(activity == 1) viewPager.setCurrentItem(1);

        int activity = restaurantIntent.getIntExtra("Search", -1);
        switch(activity){
            case 0: viewPager.setCurrentItem(0); break;
            case 1: viewPager.setCurrentItem(1); break;
        }


        /*
        MainActivity MainFragment = new MainActivity();
        Bundle args = new Bundle();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment).commit();

        MapsActivity MapFragment = new MapsActivity();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, MapFragment).commit();
        */

    }


    private void setup(ViewPager vp)
    {
        String Map=getResources().getString(R.string.Map);
        String Search=getResources().getString(R.string.Search);


        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new MapsActivity(), Map);
        pageAdapter.addFragment(new MainActivity(), "Restaurants");
        pageAdapter.addFragment(new SearchActivity(), Search);
        viewPager.setAdapter(pageAdapter); //Miracle Line


    }


    public int getIndex(){
        return restaurantIndex;
    }






}