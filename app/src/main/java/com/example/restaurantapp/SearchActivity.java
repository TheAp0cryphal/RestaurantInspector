package com.example.restaurantapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.restaurantapp.RefactoredFiles.AsyncSearch;
import com.example.restaurantapp.SingletonSupport.Item;
import com.example.restaurantapp.SingletonSupport.SQLSingleton;
import com.example.restaurantapp.SingletonSupport.TabbedActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.example.restaurantapp.SingletonSupport.MapsActivity;
import com.example.restaurantapp.SingletonSupport.TabbedActivity;

//Lee Yuen Seafood Restaurant

public class SearchActivity extends Fragment {

    Activity active;
    private TextView mTextView;
    private ArrayList<Item> searchList = new ArrayList<>();
    boolean fav = false;
    boolean greater = false;
    String lessThan = "";
    String greaterThan = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_search, container, false);

        Button mapButton = (Button) view.findViewById(R.id.search_map);
        Button listButton = (Button) view.findViewById(R.id.search_list);
        Button clearButton = (Button) view.findViewById(R.id.clear_button);

        CompoundButton lessGreater = (CompoundButton) view.findViewById(R.id.LessThan_GreatThanSwitch);
        CompoundButton favToggle = (CompoundButton) view.findViewById(R.id.favourite_switch);

        lessGreater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // Switch is on
                    greater = true;

                } else {
                    // Switch is off
                    greater = false;
                }
            }
        });

        favToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // Switch is on
                    fav = true;
                } else {
                    // Switch is off
                    fav = false;
                }
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            public void onClick(View v) {
                String enterName = "";
                String enterHazard = "";
                String favourite;
                String lessThan = "";
                String greaterThan = "";

                EditText enterNumViolation = (EditText) view.findViewById(R.id.name_enter_critical_text);

                EditText enterRestNameText = (EditText) view.findViewById(R.id.name_enter_name_text);
                if(enterRestNameText != null) {
                    enterName = enterRestNameText.getText().toString();
                }
                EditText enterHazLevelText = (EditText) view.findViewById(R.id.name_enter_hazard_text);
                if(enterHazLevelText != null) {
                    enterHazard = enterHazLevelText.getText().toString();
                }
                if(fav){
                    favourite = "1";
                }
                else{
                    favourite = "0";
                }
                if(greater){
                    greaterThan = enterNumViolation.getText().toString();
                }
                else{
                    lessThan = enterNumViolation.getText().toString();
                }


                Log.i("why does this not work", enterName + " " + favourite + " " + enterHazard + " " + lessThan + " " + greaterThan);
                AsyncSearch search = new AsyncSearch(getContext(), enterName, favourite, enterHazard, lessThan, greaterThan);
                try {
                    search.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent newIntent = new Intent(getContext(), TabbedActivity.class);
                newIntent.putExtra("Search", 0);
                startActivity(newIntent);
            }
        });

        listButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceType")
            public void onClick(View v) {
                String enterName = "";
                String enterHazard = "";
                String favourite;
                String lessThan = "";
                String greaterThan = "";
                EditText enterRestNameText = (EditText) view.findViewById(R.id.name_enter_name_text);
                if(enterRestNameText != null) {
                    enterName = enterRestNameText.getText().toString();
                }
                EditText enterHazLevelText = (EditText) view.findViewById(R.id.name_enter_hazard_text);
                EditText enterNumViolation = (EditText) view.findViewById(R.id.name_enter_critical_text);
                if(enterHazLevelText != null) {
                    enterHazard = enterHazLevelText.getText().toString();
                }
                if(fav){
                    favourite = "1";
                }
                else{
                    favourite = "0";
                }
                if(greater){
                    greaterThan = enterNumViolation.getText().toString();
                }
                else{
                    lessThan = enterNumViolation.getText().toString();
                }


                //int numViolation = Integer.parseInt(enterNumViolation.getText().toString());

                Log.i("why does this not work", enterName + " " + favourite + " " + enterHazard + " " + lessThan + " " + greaterThan);
                AsyncSearch search = new AsyncSearch(getContext(), enterName, favourite, enterHazard, "", "");
                try {
                    search.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent newIntent = new Intent(getContext(), TabbedActivity.class);
                newIntent.putExtra("Search", 1);
                startActivity(newIntent);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            public void onClick(View v) {
                SQLSingleton singleton = SQLSingleton.getInstance(getContext());
                singleton.clearSearch();
                Intent newIntent = new Intent(getContext(), TabbedActivity.class);
                newIntent.putExtra("Search", 1);
                startActivity(newIntent);

            }
        });






        // Enables Always-on
        return view;
    }
}