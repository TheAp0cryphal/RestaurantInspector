package com.example.restaurantapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.restaurantapp.Base.BaseActivity;
import com.example.restaurantapp.Entity.InspectionBean;
import com.example.restaurantapp.Entity.RestaurantsBean;
import com.example.restaurantapp.RefactoredFiles.DownloadDriver;
import com.example.restaurantapp.RefactoredFiles.LoadRestaurants;
import com.example.restaurantapp.SQL.SQLDatabase;
import com.example.restaurantapp.SingletonSupport.Restaurant;
import com.example.restaurantapp.SingletonSupport.RestaurantManager;
import com.example.restaurantapp.SingletonSupport.SQLSingleton;
import com.example.restaurantapp.SingletonSupport.TabbedActivity;
import com.example.restaurantapp.Utils.AppNetConfig;
import com.example.restaurantapp.Utils.DownloadCsv;
import com.google.gson.Gson;
import com.kongzue.dialog.v2.WaitDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.restaurantapp.Utils.CommonUtils.*;
import static com.example.utils.CommonUtils.*;

/**
 * APP Introduction Page
 */
public class StartActivity extends BaseActivity {
    private Activity activity;
    private String restaurantUrl = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private String inspectionUrl = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraser_health_restaurant_inspection_reports.csv";
    DownloadCsv csvDownloader;
    boolean newData =false;
    String last_modified_restaurant;
    String last_modified_inspection;

    /**
     *load layout
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_start;
    }

    /**
     * Initialize
     */
    @Override
    protected void init() {
        activity = this;
        HideBarAll(this);
        String fisrt = SharedInfo(activity,"First","first");
        //If its the first time loaing up the app it will download all the data
        if (fisrt == null){
            /*long before = System.currentTimeMillis() ;//current time
            SaveLong(activity,"Before","before",before);*/
            GetCurrentAction();//load data first time
            UpdateDate();

        }
        else {
            //Else it will check for new data and call updatedata function if it has been more than 20 hours since the last update
            long current = System.currentTimeMillis();//current time
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long beforeinfo = SharedLong(activity, "Before", "before");
            if (beforeinfo != 0) {
                long pass = (current - beforeinfo) / 60 / 1000;
                //1200 min = 20 hours
                if (pass >= 1200) {
                    UpdateDate();
                    System.out.println("more than 20 hours");

                } else {
                    //Do not update the data, go directly to the homepage
                    System.out.println("no more than 20 hours");
                    IntentToPage(activity, TabbedActivity.class);
                    activity.finishAffinity();
                }
                System.out.println("difference value：" + pass);
                System.out.println("current time：" + dateFormat.format(beforeinfo));
            }
        }

    }



    /**
     * Get current data
     */
    private void GetCurrentAction(){

        OKPostNovalue(AppNetConfig.InspectionUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WaitDialog.dismiss();
                        String error=getResources().getString(R.string.Server_error);

                        DialogShow(activity,error);
                    }
                });
            }
            //Saves inspection data to local device
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                System.out.println(json);
                InspectionBean databean = new Gson().fromJson(json, InspectionBean.class);
                SaveInfo(activity,"GetDate2","date2",json);// save locally

                inspectionUrl = databean.getResult().getResources().get(0).getUrl();
                String last_modified_inspection=databean.getResult().getResources().get(0).getLast_modified();
                SaveInfo(activity,"LastModifiedInspection","lastModifiedInspection",last_modified_inspection);


                int oldsize2 = databean.getResult().getResources().size();
                SaveInfo(activity,"OLD2","oldsize2",String.valueOf(oldsize2));
            }
        });


        OKPostNovalue(AppNetConfig.RestaurantUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WaitDialog.dismiss();
                        String error=getResources().getString(R.string.Server_error);

                        DialogShow(activity,error);
                    }
                });
            }

            //Saves restaurant data to local device
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                System.out.println(json);
                InspectionBean databean = new Gson().fromJson(json, InspectionBean.class);

                restaurantUrl = databean.getResult().getResources().get(0).getUrl();
                String last_modified_restaurant=databean.getResult().getResources().get(0).getLast_modified();
                SaveInfo(activity,"LastModifiedRestaurant","lastModifiedRestaurant",last_modified_restaurant);



                int oldsize = databean.getResult().getResources().size();
                SaveInfo(activity,"OLD","oldsize",String.valueOf(oldsize));
            }
        });
    }


    /**
     * Update Data
     */
    private void UpdateDate(){

        String loadMessage=getResources().getString(R.string.items_loading);
        WaitDialog.show(activity,loadMessage);
        OkGet(AppNetConfig.RestaurantUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WaitDialog.dismiss();
                        String error=getResources().getString(R.string.Server_error);

                        DialogShow(activity,error);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //RestaurantManager manager = RestaurantManager.getInstance(activity);
                String json = response.body().string();
                System.out.println(json);

                RestaurantsBean databeanR = new Gson().fromJson(json, RestaurantsBean.class);
                InspectionBean databeanI = new Gson().fromJson(json, InspectionBean.class);
                last_modified_restaurant = databeanR.getResult().getResources().get(0).getLast_modified();
                last_modified_inspection = databeanI.getResult().getResources().get(0).getLast_modified();



                // check if last_modified changes
                if ( SharedInfo(activity,"First","first")!=null) {
                    String previous_modified_restaurant=SharedInfo(activity, "LastModifiedRestaurant", "lastModifiedRestaurant");
                    String previous_modified_inspection= SharedInfo(activity,"LastModifiedInspection","lastModifiedInspection");
                    //last_modified changes
                    if (!last_modified_restaurant.equals(previous_modified_restaurant) || !last_modified_inspection.equals(previous_modified_inspection) ) {

                        newData= true;
                    }
                }



                /*int currentsize = databean.getResult().getResources().size();
                SaveInfo(activity,"GetDate","date",json);//save locally
                SaveInfo(activity,"CURRENT","currentsize",String.valueOf(currentsize));*/
                //Popup dialog for updating database
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String updateMessage=getResources().getString(R.string.Data_is_updating);
                        WaitDialog.show(activity,updateMessage);

                        //Get string from xml
                        String Update_data=getResources().getString(R.string.Update_Data);
                        String NO=getResources().getString(R.string.NO);
                        String YES=getResources().getString(R.string.YES);
                        String updated=getResources().getString(R.string.updated);
                        String Cancel_update=getResources().getString(R.string.Cancel_update);
                        String OK=getResources().getString(R.string.OK);
                        String Hint=getResources().getString(R.string.Hint);



                        //Update data
                        if(SharedInfo(activity,"First","first")==null || (SharedInfo(activity,"First","first")!=null&&newData)){
                            new XPopup.Builder(activity).asConfirm(Hint, Update_data,NO,YES, new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    WaitDialog.dismiss();
                                                    new XPopup.Builder(activity).asConfirm(Hint, updated, Cancel_update, OK, new OnConfirmListener() {
                                                        @Override
                                                        public void onConfirm() {//confirm
                                                            // TODO Note: swap this out for the SQL downloader

                                                            DownloadDriver driver = new DownloadDriver(activity, restaurantUrl, inspectionUrl);
                                                            driver.runDownloader();
                                                            LoadRestaurants loader = new LoadRestaurants(activity);
                                                            loader.execute();

                                                            SaveInfo(activity, "LastModifiedRestaurant", "lastModifiedRestaurant", last_modified_restaurant);
                                                            SaveInfo(activity, "LastModifiedInspection", "lastModifiedInspection", last_modified_inspection);

                                                            if (SharedInfo(activity, "First", "first") == null)
                                                                SaveInfo(activity, "First", "first", "1");//Is it first time to update

                                                            long before = System.currentTimeMillis();//current time
                                                            SaveLong(activity, "Before", "before", before);
                                                            //Delay3s(activity,activity,MainActivity.class);
                                                            activity.startActivity(new Intent(activity, TabbedActivity.class));
                                                            activity.finishAffinity();
                                                        }
                                                    }, new OnCancelListener() {
                                                        @Override
                                                        public void onCancel() {//cancel update
                                                            activity.startActivity(new Intent(activity, TabbedActivity.class));
                                                            activity.finishAffinity();
                                                        }
                                                    }, false).show();
                                                }

                                            }, 3000);
                                        }

                                    }, new OnCancelListener() {
                                        @Override
                                        public void onCancel() {
                                            activity.startActivity(new Intent(activity, TabbedActivity.class));
                                            activity.finishAffinity();
                                        }
                                    },false).show();
                        }else {
                            Log.i("else case", "else");
                            LoadRestaurants loader = new LoadRestaurants(activity);
                            try {
                                loader.execute().get();
                                Log.i("finished in start", "have i been testing wrong");
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            activity.startActivity(new Intent(activity, TabbedActivity.class));
                            activity.finishAffinity();
                        }

                    }
                });
            }
        });
    }

}
