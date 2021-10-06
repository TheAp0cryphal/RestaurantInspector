package com.example.restaurantapp;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.os.Bundle;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.RecyclerView;

        import android.service.autofill.SaveInfo;
        import android.util.Log;
        import android.view.Menu;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.restaurantapp.SQL.SQLDatabase;
        import com.example.restaurantapp.SingletonSupport.Inspection;
        import com.example.restaurantapp.SingletonSupport.Item;
        import com.example.restaurantapp.SingletonSupport.ItemAdapter;
        import com.example.restaurantapp.SingletonSupport.MapsActivity;
        import com.example.restaurantapp.SingletonSupport.Restaurant;
        import com.example.restaurantapp.SingletonSupport.RestaurantManager;
        import com.example.restaurantapp.SingletonSupport.SQLSingleton;
        import com.example.restaurantapp.SingletonSupport.TabbedActivity;
        import com.example.restaurantapp.SingletonSupport.Violations;

        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.time.LocalDate;
        import java.util.ArrayList;
        import java.util.List;
        import static com.example.restaurantapp.Utils.CommonUtils.*;
        import static com.example.utils.CommonUtils.*;

public class RestaurantActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;
    private Inspection inspectManager;
    private Activity activity;
    Restaurant restaurant;
    private TextView mTextView;
    private ListView scrollView;
    private RecyclerView.LayoutManager scrollLayoutManager;
    private ItemAdapter scrollAdapter;
    private List<Inspection> inspectList = new ArrayList<>();
    private List<Item> infoList;
    private int listIndex;
    private String hazardRate;
    private int inspectionIndex;
    private long restaurantIndex;
    SQLDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        infoList = SQLSingleton.getInstance(this).getList();
        db = new SQLDatabase(this);
        db.open();

        Intent intent = getIntent();
        long s = intent.getLongExtra(MainActivity.Extra_Num, 0);
        listIndex = intent.getIntExtra("Extra_index", 0);
        restaurantIndex = s;

        Cursor restaurantCursor = db.getRestaurantRow(restaurantIndex);
        if(restaurantCursor.getCount() != 0) {
            createInspectionList(restaurantCursor);
            registerClickCallBack();
        }
        restaurantCursor.close();

        populateListView();
        setTextFromDatabase(restaurantIndex);
        setClickableCoords();
        db.close();
    }


    private void setTextFromDatabase(long index){

        Cursor c = db.getRestaurantRow(index);
        if(c.getCount() != 0) {
            TextView textView = findViewById(R.id.RestNameText);
            TextView textView2 = findViewById(R.id.AddressText);
            TextView GPSText = (TextView) findViewById(R.id.CoordsText);
            String name;
            String address;
            String latitude;
            String longitude;

            name = c.getString(db.COL_NAME);
            address = c.getString(db.COL_PHYSICALADDRESS);
            latitude = c.getString(db.COL_LATITUDE);
            longitude = c.getString(db.COL_LONGITUDE);

            textView.setText(name);
            textView2.setText(address);
            // TODO this will need to be changed to string.xml for Bokun
            GPSText.setText("Latitude: " + latitude + " \nLongitude: " + longitude);
        }
        setClickableFavourite((int)index);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("Back", "YEs");
        Intent intent = new Intent (this, TabbedActivity.class);
        intent.putExtra("Res",1);
        startActivity(intent);
    }

    //Sets text portion of the restaurant activity (ie. name, address, latitude and longitude
    private void setRestaurantText(final int s) {
        restaurant = restaurantManager.getRestaurant(s);
        TextView textView = findViewById(R.id.RestNameText);
        textView.setText(restaurant.getName());
        TextView textView2 = findViewById(R.id.AddressText);

        String Located_at=getResources().getString(R.string.Located_at);
        textView2.setText(Located_at + restaurant.getAddress());
        TextView GPSText = (TextView) findViewById(R.id.CoordsText);
        GPSText.setText("Latitude: " +restaurant.getLatitude() + " Longitude: " + restaurant.getLongitude());
    }


    // infoArray[0] =  idNumber infoArray[1] = date infoArray[2] = type
    // infoArray[3] = numCritical infoArray[4] = numNonCritical infoArray[5] = HazardRating infoArray[6] = Violations
    private void createInspectionList(Cursor rCursor){
        Cursor inspectionCursor = db.getAllInspections(rCursor.getString(db.COL_TRACKINGNUMBER));
        if(inspectionCursor.moveToFirst() && inspectionCursor.getCount() != 0) {
            do {

                String id = inspectionCursor.getString(db.COL_INSPECTION_TRACKINGNUMBER);
                String date = inspectionCursor.getString(db.COL_INSPECTIONDATE);
                String type = inspectionCursor.getString(db.COL_INSPTYPE);
                String numCritical = inspectionCursor.getString(db.COL_NUMCRITICAL);
                String numNonCritical = inspectionCursor.getString(db.COL_NUMNONCRITICAL);
                String violations = inspectionCursor.getString(db.COL_VIOLLUMP);
                String hazard = inspectionCursor.getString(db.COL_HAZARDRATING);
                String[] info = {id, date, type, numCritical, numNonCritical, violations, hazard};

                Inspection inspection = new Inspection(info, true, inspectionCursor.getLong(db.COL_INSPECTION_ROWID));
                inspectList.add(inspection);
            } while (inspectionCursor.moveToNext());
        }
        inspectionCursor.close();
    }

        private void setClickableFavourite(final long s) {
            ImageView imageView = (ImageView) findViewById(R.id.fav);
            //restaurant = restaurantManager.getRestaurant(s);
            SQLDatabase db = new SQLDatabase(this);
            db.open();
            Log.i("print debug info", "" + s);
            Cursor restaurantCursor = db.getRestaurantRow(s);

            boolean favourite;
            if(restaurantCursor.getInt(db.COL_FAVOURITE) == 1){
                imageView.setImageResource(R.drawable.favicon);
                favourite = true;
            }
            else{
                imageView.setImageResource(R.drawable.favicondisabled);
                favourite = false;
            }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(favourite) {
                            imageView.setImageResource(R.drawable.favicondisabled);
                            db.updateFavourite(s);
                            infoList.get(listIndex).updateFavourite();
                            Toast.makeText(getApplicationContext(), "Removed from Favourites :(", Toast.LENGTH_SHORT).show();

                            String Removed_from_Favourites=getResources().getString(R.string.Removed_from_Favourites);
                            Toast.makeText(getApplicationContext(), Removed_from_Favourites, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            imageView.setImageResource(R.drawable.favicon);
                            db.updateFavourite(s);
                            infoList.get(listIndex).updateFavourite();
                            Toast.makeText(getApplicationContext(), "Added to Favourites :)", Toast.LENGTH_SHORT).show();
                            String Added_to_Favourites=getResources().getString(R.string.Added_to_Favourites);
                            Toast.makeText(getApplicationContext(), Added_to_Favourites, Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private void setClickableCoords(){
        TextView coordText = (TextView) findViewById(R.id.CoordsText);
        coordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "!!!");
                Intent intent = new Intent(RestaurantActivity.this, TabbedActivity.class);
                intent.putExtra("index", restaurantIndex);
                startActivity(intent);
            }
        });
    }

    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new RestaurantActivity.MyListAdapter();
        ListView list = (ListView) findViewById(R.id.inspectListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection>{
        public MyListAdapter(){
            super(RestaurantActivity.this, R.layout.restaurant_list_item, inspectList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_list_item, parent, false);
            }

            Inspection currentInspection = inspectList.get(position);
            TextView descriptionText = (TextView) itemView.findViewById(R.id.inspection_date);
            descriptionText.setText(calDateDif(currentInspection.getDate()));
            String haz = currentInspection.getHazardRating();
            //Sets hazard level and logo
            if (haz.equals("High")) {

                ImageView iv = itemView.findViewById(R.id.imageView4);
                iv.setImageResource(R.drawable.ic_baseline_error_24);
            } else if (haz.equals("Moderate")) {

                ImageView iv = itemView.findViewById(R.id.imageView4);
                iv.setImageResource(R.drawable.ic_baseline_error_outline_24);
            } else if (haz.equals("Low")) {

                ImageView iv = itemView.findViewById(R.id.imageView4);
                iv.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
            }
            TextView criticalText = (TextView) itemView.findViewById(R.id.critical_count);
            String Critical=getResources().getString(R.string.Critical);
            criticalText.setText(Critical+ " " +currentInspection.getNumCritical());


            TextView nonCritText = (TextView) itemView.findViewById(R.id.non_critical_count);
            String Non_Critical=getResources().getString(R.string.Non_Critical);
            nonCritText.setText("\n       "+Non_Critical+ " " +currentInspection.getNumNonCritical());

            return itemView;
        }

    }

    //Calculates inspection date displayed based on current date unless inspections empty
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

    private void registerClickCallBack(){
        ListView list = (ListView) findViewById(R.id.inspectListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inspection currentInspection = inspectList.get(position);
                Intent intent = new Intent(RestaurantActivity.this, InspectionActivity.class);
                intent.putExtra("inspectIndex", currentInspection.getDatabaseIndex());
                startActivity(intent);
            }
        });
    }

}