package com.example.restaurantapp;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.Inspection;
import com.example.restaurantapp.SingletonSupport.Restaurant;
import com.example.restaurantapp.SingletonSupport.RestaurantManager;
import com.example.restaurantapp.SingletonSupport.Violations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//https://material.io/resources/icons/?icon=straighten&style=baseline for all the icons

/*
    For the inspection activity, the information about the inspection is displayed at the
    top of the screen. A list of all of the violations is then created and displayed.
    Each violation also shows an icon, colour coded hazard ratings, and the information
    about the violation.
 */

public class InspectionActivity extends AppCompatActivity {

    private RestaurantManager manager;
    private Restaurant restaurant;
    private Inspection inspection;
    private List<Violations> violationList = new ArrayList<>();
    private List<Inspection> inspectionList = new ArrayList<>();
    private long restaurantIndex;
    private long inspectionIndex;
    private SQLDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        db = new SQLDatabase(this);
        db.open();

        Intent intent = getIntent();
        restaurantIndex = intent.getLongExtra("restIndex", 0);
        inspectionIndex = intent.getLongExtra("inspectIndex", 0);

        setText(inspectionIndex);
        Cursor inspectionCursor = db.getInspectionRow(inspectionIndex);
        Log.i("violations:", inspectionCursor.getString(db.COL_VIOLLUMP));
        if(inspectionCursor.getCount() != 0 && !(inspectionCursor.getString(db.COL_VIOLLUMP).equals("No inspections"))){
            createViolationList(inspectionCursor);
            registerClickCallBack();
            populateListView();
        }
        else{
            TextView showText = (TextView) findViewById(R.id.show_text);
            showText.setVisibility(View.VISIBLE);
            showText.setText(R.string.no_violations);
        }

    }


    private void populateListView(){
        ArrayAdapter<Violations> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.inspection_listView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Violations>{
        public MyListAdapter(){
            super(InspectionActivity.this, R.layout.inspection_list_item, violationList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.inspection_list_item, parent, false);
            }

            // find the item

            Violations currentViolation = violationList.get(position);
            ImageView image = (ImageView) itemView.findViewById(R.id.inspection_icon);
            image.setImageResource(iconSelector(currentViolation.getCode()));

            TextView descriptionText = (TextView) itemView.findViewById(R.id.inspection_description);
            descriptionText.setText(descriptionSelector(currentViolation.getCode()));

            TextView criticalText = (TextView) itemView.findViewById(R.id.inspection_critical);


            //Sets violation colour based on hazard level
            if(currentViolation.getCritical().equals("Critical")){
                String Critical=getResources().getString(R.string.Critical);
                criticalText.setText(Critical);
                criticalText.setTextColor(Color.RED);
            }
            else{
                String Non_Critical=getResources().getString(R.string.Non_Critical);
                criticalText.setText(Non_Critical);
                criticalText.setTextColor(Color.GREEN);
            }

            // fill the view

            return itemView;
        }
    }


    private void createViolationList(Cursor iCursor){

        String violationText = iCursor.getString(db.COL_VIOLLUMP);
        String[] parsedText = violationText.split("[@,]");
        Log.i("printing", violationText);
        for(int i = 0; i < parsedText.length; i+=4){
            violationList.add(new Violations(parsedText[i], parsedText[i + 1], parsedText[i + 2], parsedText[i + 3]));
        }
    }

    private void registerClickCallBack(){
        ListView list = (ListView) findViewById(R.id.inspection_listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violations clickedViolation = violationList.get(position);
                displayDialogue(clickedViolation);

            }
        });
    }

    //Sets text for each individual inspection
    private void setText(long index){

        Cursor c = db.getInspectionRow(index);
        if(c.getCount() != 0) {
            TextView dateText = (TextView) findViewById(R.id.inspection_date_text);
            TextView typeText = (TextView) findViewById(R.id.inspection_type_text);
            TextView critText = (TextView) findViewById(R.id.inspection_num_critical_text);
            TextView nonCritText = (TextView) findViewById(R.id.inspection_num_noncritical_text);
            TextView hazardText = (TextView) findViewById(R.id.inspection_hazard_text);
            ImageView hazardIcon = (ImageView) findViewById(R.id.hazard_icon);

            String date = c.getString(db.COL_INSPECTIONDATE);
            String type = c.getString(db.COL_INSPTYPE);
            String numCrit = c.getString(db.COL_NUMCRITICAL);
            String numNonCrit = c.getString(db.COL_NUMNONCRITICAL);
            String hazard = c.getString(db.COL_HAZARDRATING);

            dateText.setText(getString(R.string.inspection_date_string) + " " + date);
            if(type.equals("Follow-Up"))
                type=getResources().getString(R.string.Follow_Up);
            typeText.setText(getString(R.string.inspection_type_string) + " " + type);
            critText.setText(getString(R.string.inspection_num_critical_string) + " " + numCrit);
            nonCritText.setText(getString(R.string.inspection_num_non_critical_string) + " " + numNonCrit);
          //Sets hazard level
            //Sets inspection hazard color and icon
            if (hazard.equals("Low")) {
                hazardIcon.setBackground(getDrawable(R.drawable.ic_baseline_check_circle_outline_24));
                String s = getResources().getString(R.string.Low);
                hazardText.setText(getString(R.string.inspection_hazard_string) + " " +s);
            } else if (hazard.equals("Moderate")) {
                hazardIcon.setBackground(getDrawable(R.drawable.ic_baseline_error_outline_24));
                String s = getResources().getString(R.string.Moderate);
                hazardText.setText(getString(R.string.inspection_hazard_string) + " " +s);
            } else {
                hazardIcon.setBackground(getDrawable(R.drawable.ic_baseline_error_24));
                String s = getResources().getString(R.string.High);
                hazardText.setText(getString(R.string.inspection_hazard_string) + " " +s);
            }
        }

    }
    //Chooses icon for each violation based on the violation
    private int iconSelector(String code){


        int intCode = Integer.parseInt(code);


        switch(intCode){
            case 101:
            case 311:
                return R.drawable.ic_handyman_24px;
            case 102:
            case 103:
            case 104:
            case 212:
            case 314:
            case 501:
            case 502:
                return R.drawable.ic_receipt_long_24px;
            case 201:
            case 202:
                return R.drawable.ic_sick_24px;
            case 203:
            case 205:
                return R.drawable.ic_twotone_ac_unit_24;
            case 204:
            case 206:
            case 211:
                return R.drawable.ic_local_fire_department_24px;
            case 208:
                return R.drawable.ic_sentiment_dissatisfied_24px;
            case 209:
                return R.drawable.ic_coronavirus_24px;
            case 210:
                return R.drawable.ic_microwave_24px;
            case 301:
            case 302:
            case 303:
                return R.drawable.ic_countertops_24px;
            case 304:
            case 305:
                return R.drawable.ic_pest_control_24px;
            case 306:
                return R.drawable.ic_cleaning_services_24px;

            case 307:
            case 308:
                return R.drawable.ic_build_24px;
            case 309:
                return R.drawable.ic_science_24px;
            case 310:
                return R.drawable.ic_baseline_restaurant_24;
            case 312:
                return R.drawable.ic_delete_24px;

            case 313:
                return R.drawable.ic_pets_24px;
            case 315:
                return R.drawable.ic_straighten_24px;

            case 401:
            case 402:
            case 403:
                return R.drawable.ic_wash_24px;

            case 404:
                return R.drawable.ic_smoking_rooms_24px;

            default:
                return R.drawable.ic_new_releases_24px;
        }
    }


    private String descriptionSelector(String code) {


        String description="";
        int number = Integer.parseInt(code);
        if (number == 101){
            description=getResources().getString(R.string.violation101);
        }else if(number == 102){
            description=getResources().getString(R.string.violation102);
        }else if(number == 103){
            description=getResources().getString(R.string.violation103);
        }else if(number == 104){
            description=getResources().getString(R.string.violation104);
        }else if(number == 201){
            description=getResources().getString(R.string.violation201);
        }else if(number == 202){
            description=getResources().getString(R.string.violation202);
        }else if(number == 203){
            description=getResources().getString(R.string.violation203);
        }else if(number == 204){
            description=getResources().getString(R.string.violation204);
        }else if(number == 205){
            description=getResources().getString(R.string.violation205);
        }else if(number == 206){
            description=getResources().getString(R.string.violation206);
        }else if(number == 208){
            description=getResources().getString(R.string.violation208);
        }else if(number == 209){
            description=getResources().getString(R.string.violation209);
        }else if(number == 210){
            description=getResources().getString(R.string.violation210);
        }else if(number == 211){
            description=getResources().getString(R.string.violation211);
        }else if(number == 212){
            description=getResources().getString(R.string.violation212);
        }else if(number == 301){
            description=getResources().getString(R.string.violation301);
        }else if(number == 302){
            description=getResources().getString(R.string.violation302);
        }else if(number == 303) {
            description=getResources().getString(R.string.violation303);
        }else if(number == 304) {
            description=getResources().getString(R.string.violation304);
        }else if(number == 305) {
            description=getResources().getString(R.string.violation305);
        }else if(number == 306) {
            description=getResources().getString(R.string.violation306);
        }else if(number == 307) {
            description=getResources().getString(R.string.violation307);
        }else if(number == 308) {
            description=getResources().getString(R.string.violation308);
        }else if(number == 309) {
            description=getResources().getString(R.string.violation309);
        }else if(number == 310) {
            description=getResources().getString(R.string.violation310);
        }else if(number == 311) {
            description=getResources().getString(R.string.violation311);
        }else if(number == 312) {
            description=getResources().getString(R.string.violation312);
        }else if(number == 313) {
            description=getResources().getString(R.string.violation313);
        }else if(number == 314) {
            description=getResources().getString(R.string.violation314);
        }else if(number == 315) {
            description=getResources().getString(R.string.violation315);
        }else if(number == 401) {
            description=getResources().getString(R.string.violation401);
        }else if(number == 402) {
            description=getResources().getString(R.string.violation402);
        }else if(number == 403) {
            description=getResources().getString(R.string.violation403);
        }else if(number == 404) {
            description=getResources().getString(R.string.violation404);
        }else if(number == 501) {
            description=getResources().getString(R.string.violation501);
        }else if(number == 502) {
            description=getResources().getString(R.string.violation502);
        }
        return description;
    }

    //Sets inspection violation information
    private void displayDialogue(Violations violation){
        // Created a new Dialog
        Dialog dialog = new Dialog(InspectionActivity.this);

        dialog.setContentView(R.layout.detailed_violation);

        TextView descriptionText = (TextView) (dialog.findViewById(R.id.violation_description));
        descriptionText.setText(getString(R.string.violation_description) + "\n" +  descriptionSelector(violation.getCode()));

        TextView codeText = (TextView)(dialog.findViewById(R.id.violation_code));
        codeText.setText(getString(R.string.violation_code) + "\n" + violation.getCode());

        TextView repetitionText = (TextView)(dialog.findViewById(R.id.violation_repetition));
        if(violation.getRepetition().equals("Repeat")) {
            repetitionText.setText(getString(R.string.violation_repetition) + "\n" + getString(R.string.Repeat));
        }else{
            repetitionText.setText(getString(R.string.violation_repetition) + "\n" + getString(R.string.Not_Repeat));
        }

        TextView criticalText = (TextView)(dialog.findViewById(R.id.violation_critical));

        String getCritical;
        if (violation.getCritical().equals("Critical"))
            getCritical=getResources().getString(R.string.Critical);
        else
            getCritical=getResources().getString(R.string.Non_Critical);

        criticalText.setText(getString(R.string.violation_critical) + "\n" + getCritical);

        dialog.show();


    }

}