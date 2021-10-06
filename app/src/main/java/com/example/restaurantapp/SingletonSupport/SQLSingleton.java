package com.example.restaurantapp.SingletonSupport;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.restaurantapp.MainActivity;
import com.example.restaurantapp.R;
import com.example.restaurantapp.SQL.SQLDatabase;
import com.google.android.gms.common.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



// This is the SQL singleton
// this is for quick access to large amounts of the singleton data

public class SQLSingleton {
    private static SQLSingleton instance;
    private ArrayList<Item> list = new ArrayList<>();
    private ArrayList<Item> searchList = new ArrayList<>();

    public SQLSingleton(Context context){
        SQLDatabase db = new SQLDatabase(context);
        db.open();
        Cursor cursor =  db.getAllRestaurantRows();
        db.close();
        populateList(context, cursor, list);
    }

    public static SQLSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new SQLSingleton(context);
        }
        return instance;
    }

    public ArrayList<Item> getList(){
        return list;
    }
    public ArrayList<Item> getSearchList(){
        return searchList;
    }

    public void clearSearch(){
        searchList.clear();
    }


    private void populateList(Context context, Cursor cursor, ArrayList<Item> inputList){
        // TODO FIX BUG WHERE THE FIRST ITEM IN THE DATABASE HAS COUNT 0
        SQLDatabase db = new SQLDatabase(context);
        db.open();

        //Log.i("SizeofCursor", "" + cursor.getCount());
        int restaurantLogo;
        int sumErrors;
        String name;
        String date;
        String hazard;
        String critical;
        String nonCritical;
        String latitude;
        String longitude;
        String address;
        long index;
        boolean favourite;
        if(cursor.moveToFirst()){
            do{
                name = cursor.getString(db.COL_NAME);
                index = cursor.getLong(db.COL_RESTAURANT_ROWID);
                latitude = cursor.getString(db.COL_LATITUDE);
                longitude = cursor.getString(db.COL_LONGITUDE);
                address = cursor.getString(db.COL_PHYSICALADDRESS);
                if(cursor.getInt(db.COL_FAVOURITE) == 1){
                    favourite = true;
                }
                else{
                    favourite = false;
                }
                Cursor inspectionCursor = db.getMostRecentInspection(cursor.getString(db.COL_TRACKINGNUMBER));
                if(inspectionCursor.getCount() != 0) {
                    //Log.i("name is, ", name + " " + cursor.getString(db.COL_TRACKINGNUMBER));
                    //Log.i("size of cursor", "" + inspectionCursor.getCount());
                    date = calDateDif(Integer.parseInt(inspectionCursor.getString(db.COL_INSPECTIONDATE)));
                    hazard = inspectionCursor.getString(db.COL_HAZARDRATING);
                    critical = inspectionCursor.getString(db.COL_NUMCRITICAL);
                    nonCritical = inspectionCursor.getString(db.COL_NUMNONCRITICAL);


                    if (!critical.equals("") && !nonCritical.equals("")) {
                        //Log.i("crit non crit", critical + nonCritical);
                        sumErrors = Integer.parseInt(critical) + Integer.parseInt(nonCritical);
                    } else {
                        sumErrors = 0;
                    }
                    restaurantLogo = getLogo(name);
                    inputList.add(new Item(restaurantLogo, name, date, hazard, sumErrors, favourite ,index, latitude, longitude, address));

                }
                inspectionCursor.close();

            }while(cursor.moveToNext());
            //Log.i("printing list size: ", "" + inputList.size());
        }
        cursor.close();
        db.close();
    }

    // https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
    // for is numeric
    public boolean runSearch(Context c, String name, String favourite, String hazard, String lessThan, String greaterThan){
        Log.i("running Search now", "lets test this");
        clearSearch();
        ArrayList<Item> listIntersection = new ArrayList<>();
        HashMap<String, Item> referenceMap = new HashMap<>();
        SQLDatabase db = new SQLDatabase(c);
        db.open();
        Log.i("fav", favourite);

        if(!(name.equals(""))){
            ArrayList<Item> tempList = new ArrayList<>();
            Cursor cursor = db.searchRestaurantName(name);
            populateList(c, cursor, tempList);

            for(Item items: tempList){
                referenceMap.put(items.getName(), items);
            }

            cursor.close();
        }
        if(favourite.equals("1")){
            ArrayList<Item> tempList = new ArrayList<>();
            HashMap<String, Item> tempMap = new HashMap<>();


            Cursor cursor = db.getFavourites();
            populateList(c, cursor, tempList);
            Log.i("size of tempList", "" + tempList.size());
            if(referenceMap.size() == 0){
                for(Item items: tempList){
                    referenceMap.put(items.getName(), items);
                }
            }else{
                int counter = 0;
                while(counter < tempList.size() && tempMap.size() < referenceMap.size()){
                    Item currentItem = tempList.get(counter);
                    String currentName = currentItem.getName();
                    if(referenceMap.get(currentName) != null){
                        tempMap.put(currentName, currentItem);
                    }
                    counter++;
                }
                referenceMap = tempMap;
            }
            cursor.close();
        }
        if(!(hazard.equals(""))){
            ArrayList<Item> tempList = new ArrayList<>();
            HashMap<String, Item> tempMap = new HashMap<>();
            Cursor cursor = db.getHazard(hazard);
            populateList(c, cursor, tempList);

            if(referenceMap.size() == 0){
                for(Item items: tempList){
                    referenceMap.put(items.getName(), items);
                }
            }else{
                int counter = 0;
                while(counter < tempList.size() && tempMap.size() < referenceMap.size()){
                    Item currentItem = tempList.get(counter);
                    String currentName = currentItem.getName();
                    if(referenceMap.get(currentName) != null){
                        tempMap.put(currentName, currentItem);
                    }
                    counter++;
                }
                referenceMap = tempMap;
            }
            cursor.close();
        }
        if(!(lessThan.equals("")) && lessThan.chars().allMatch( Character::isDigit )){
            int inputLess = Integer.parseInt(lessThan);
            ArrayList<Item> tempList = new ArrayList<>();
            HashMap<String, Item> tempMap = new HashMap<>();
            Cursor cursor = db.getCriticalLessOrEqual(inputLess);
            populateList(c, cursor, tempList);

            if(referenceMap.size() == 0){
                for(Item items: tempList){
                    referenceMap.put(items.getName(), items);
                }
            }else{
                int counter = 0;
                while(counter < tempList.size() && tempMap.size() < referenceMap.size()){
                    Item currentItem = tempList.get(counter);
                    String currentName = currentItem.getName();
                    if(referenceMap.get(currentName) != null){
                        tempMap.put(currentName, currentItem);
                    }
                    counter++;
                }
                referenceMap = tempMap;
            }
            cursor.close();

        }
        if(!(greaterThan.equals("")) && greaterThan.chars().allMatch( Character::isDigit)){
            int inputGreater = Integer.parseInt(greaterThan);
            ArrayList<Item> tempList = new ArrayList<>();
            HashMap<String, Item> tempMap = new HashMap<>();
            Cursor cursor = db.getCriticalGreaterOrEqual(inputGreater);
            populateList(c, cursor, tempList);

            if(referenceMap.size() == 0){
                for(Item items: tempList){
                    referenceMap.put(items.getName(), items);
                }
            }else{
                int counter = 0;
                while(counter < tempList.size() && tempMap.size() < referenceMap.size()){
                    Item currentItem = tempList.get(counter);
                    String currentName = currentItem.getName();
                    if(referenceMap.get(currentName) != null){
                        tempMap.put(currentName, currentItem);
                    }
                    counter++;
                }
                referenceMap = tempMap;
            }
            cursor.close();
        }
        db.close();
        for(String strings: referenceMap.keySet()){
            searchList.add(referenceMap.get(strings));
        }
        Log.i("Done", "returning size: " + searchList.size());
        return true;
    }
    /*

     ArrayList<User> usersArrayList = new ArrayList<User>();

    Collection<User> userCollection = new HashSet<User>(usersArrayList);
    Convert Collection to ArrayList

    Collection<User> userCollection = new HashSet<User>(usersArrayList);

    List<User> userList = new ArrayList<User>(userCollection );
     */


    // found at stackOverFlow: https://stackoverflow.com/questions/5283047/intersection-and-union-of-arraylists-in-java
    public ArrayList<Item> intersection(ArrayList<Item> l1, ArrayList<Item> l2){
        ArrayList<Item> returnList = new ArrayList<>();
        for(Item items: l1){
            String currentName = items.getName();
            if(l2.contains(items)){
                returnList.add(items);
            }
        }
        return returnList;
    }


    //Calculates most recent inspection date displayed based on current date unless inspections empty
    public String calDateDif(int mostRecentDate){
        if(mostRecentDate==0) return "No inspections";
        String s=(""+mostRecentDate);
        int myear=Integer.parseInt(s.substring(0,4));
        int mmonth=Integer.parseInt(s.substring(4,6));
        int mday=Integer.parseInt(s.substring(6,8));

        LocalDate from = LocalDate.of(myear, mmonth, mday);
        long day = LocalDate.now().toEpochDay() - from.toEpochDay();

        String[] Month = {"0","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        if(day<=30){
            return (""+day+" days");
        }
        else if(day<=365) {
            return (""+Month[mmonth]+" "+mday);
        }
        else {
            return (""+Month[mmonth]+" "+myear);
        }
    }

    public int getLogo(String name){
        //Sets logo based upon data collected from restaurant name
        //(IE. Big Fast Food Chains, Specific cuisine types, etc...
        if(name.contains("A&W") || name.contains("A & W")){
            return R.drawable.ic_aw_logo;
        } else if (name.contains("Brown's")){
            return R.drawable.ic_browns_logo;
        } else if (name.contains("Booster Juice")){
            return R.drawable.ic_booster_juice_logo;
        } else if (name.contains("Bubble World") || name.contains("Bubble Tea")) {
            return R.drawable.ic_bubble_tea_logo;
        } else if (name.contains("Burger King")) {
            return R.drawable.ic_burger_king_logo;
        } else if (name.contains("Dairy Queen")){
            return R.drawable.ic_dairy_queen_logo;
        } else if (name.contains("De Dutch")) {
            return R.drawable.ic_de_dutch_logo;
        } else if (name.contains("Denny's")) {
            return R.drawable.ic_dennys_logo;
        } else if (name.contains("Earl's")) {
            return R.drawable.ic_earls_logo;
        } else if (name.contains("Fatburger")){
            return R.drawable.ic_fatburger_logo;
        } else if (name.contains("Five Guy's")) {
            return R.drawable.ic_five_guys_logo;
        } else if (name.contains("Freshslice")) {
            return R.drawable.ic_freshslice_logo;
        } else if (name.contains("Freshii")) {
            return R.drawable.ic_freshii_logo;
        } else if (name.contains("IHOP")){
            return R.drawable.ic_ihop_logo;
        } else if (name.contains("KFC")) {
            return R.drawable.ic_kfc_logo;
        } else if (name.contains("Little Caesars")) {
            return R.drawable.ic_caesars_logo;
        } else if (name.contains("McDonald's")) {
            return R.drawable.ic_mcdonalds_logo;
        } else if (name.contains("Milestone's")) {
            return R.drawable.ic_milestones_logo;
        } else if (name.contains("Papa John's")){
            return R.drawable.ic_papa_johns_logo;
        } else if (name.contains("Panago")){
            return R.drawable.ic_panago_logo;
        } else if (name.contains("Pizza Hut")){
            return R.drawable.ic_pizza_hut_logo;
        } else if (name.contains("Popeye's")) {
            return R.drawable.ic_popeyes_logo;
        } else if (name.contains("Quiznos")){
            return R.drawable.ic_quiznos_logo;
        } else if (name.contains("Starbucks")){
            return R.drawable.ic_starbucks_logo;
        } else if (name.contains("Subway")) {
            return R.drawable.ic_subway_logo;
        } else if (name.contains("Tim Hortons")){
            return R.drawable.ic_tim_hortons_logo;
        } else if (name.contains("Wendy's")){
            return R.drawable.ic_wendys_logo;
        } else if (name.contains("White Spot")) {
            return R.drawable.ic_white_spot_logo;
        } else if (name.contains("7-Eleven")){
            return R.drawable.ic_7_eleven_logo;
        } else if (name.contains("Health")){
            return R.drawable.ic_health_logo;
        } else if (name.contains("Taco") || name.contains("Burrito") || name.contains("Mexican")){
            return R.drawable.ic_taco_logo;
        } else if (name.contains("Chicken")) {
            return R.drawable.ic_chicken_logo;
        } else if (name.contains("Grill")) {
            return R.drawable.ic_grill_logo;
        } else if (name.contains("Juice")) {
            return R.drawable.ic_juice_logo;
        } else if (name.contains("Bar") || name.contains("Brewery") || name.contains("Brewing") || name.contains("Pub")
                || name.contains("Public House") || name.contains("Taphouse")) {
            return R.drawable.ic_bar_logo;
        } else if (name.contains("Bakery") || name.contains("Biscotti") || name.contains("Bread") || name.contains("Cake")
                || name.contains("Pastry") || name.contains("Pastries")) {
            return R.drawable.ic_bakery_logo;
        } else if (name.contains("BBQ")){
            return R.drawable.ic_bbq_logo;
        } else if (name.contains("Burger")){
            return R.drawable.ic_burger_logo;
        } else if (name.contains("Sweet") || name.contains("Sweets") || name.contains("Chocolate") || name.contains("Delights")){
            return R.drawable.ic_sweets_logo;
        } else if (name.contains("Cafe") || name.contains("Coffee") || name.contains("Espresso") || name.contains("Java")
                || name.contains("Tea")){
            return R.drawable.ic_cafe_logo;
        } else if (name.contains("Chili") || name.contains("Indian") || name.contains("Spice")){
            return R.drawable.ic_indian_logo;
        } else if (name.contains("Asian") || name.contains("Chinese") || name.contains("Szechuan")
                || name.contains("Thai") || name.contains("Lee Yuen")) {
            return R.drawable.ic_chinese_food_logo;
        } else if (name.contains("Cinema") || name.contains("Concession")) {
            return R.drawable.ic_concession_logo;
        } else if (name.contains("Club") || name.contains("Lounge") || name.contains("Lodge")){
            return R.drawable.ic_club_logo;
        } else if (name.contains("Deli")) {
            return R.drawable.ic_deli_logo;
        } else if (name.contains("Donair")){
            return R.drawable.ic_donair_logo;
        } else if (name.contains("Fish & Chips") || name.contains("Fish and Chips")) {
            return R.drawable.ic_fish_chips_logo;
        } else if (name.contains("Fries")) {
            return R.drawable.ic_fries_logo;
        } else if (name.contains("Ice Cream") || name.contains("Sundae") || name.contains("Frozen Yogurt")){
            return R.drawable.ic_ice_cream_logo;
        } else if (name.contains("Pho")){
            return R.drawable.ic_pho_logo;
        } else if (name.contains("Pizza") || name.contains("Pizzaria")){
            return R.drawable.ic_pizza_logo;
        } else if (name.contains("Sandwich") || name.contains("Sub")) {
            return R.drawable.ic_sandwich_logo;
        } else if (name.contains("Seafood")) {
            return R.drawable.ic_seafood_logo;
        } else if (name.contains("Steak")) {
            return R.drawable.ic_steak_logo;
        } else if (name.contains("Sushi")){
            return R.drawable.ic_sushi_logo;
        } else {
            return R.drawable.res;
        }
    }

}
