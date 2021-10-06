package com.example.restaurantapp.SingletonSupport;

import java.time.LocalDate;
/*
    This is the class for a single item of the recyclerview
    from here you can set attributes or get information about
    a single item
 */
public class Item {
    private int imageResource;
    private String name;
    private String dateDif;
    private boolean fav;
    private String hazardLevel;
    private String latitude;
    private String longitude;
    private String address;
    private int sumErrors;
    private long index;

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateDif() {
        return dateDif;
    }

    public void setDateDif(String dateDif) {
        this.dateDif = dateDif;
    }

    public long getIndex(){
        return index;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public int getSumErrors() {
        return sumErrors;
    }

    public void setSumErrors(int sumErrors) {
        this.sumErrors = sumErrors;
    }

    public boolean getFav() {return fav;}

    public String getLatitude(){return latitude;}

    public String getLongitude(){return longitude;}

    public String getAddress(){return address;}

    public void updateFavourite(){
        if(fav){
            fav = false;
        }
        else{
            fav = true;
        }
    }

    private String img;
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public Item(int imageResource, String name, String dateDif, String hazardLevel, int sumErrors, boolean fav, long dataBaseIndex, String latitude, String longitude, String address) {
        this.imageResource = imageResource;
        this.name = name;
        this.dateDif = dateDif;
        this.hazardLevel = hazardLevel;
        this.sumErrors = sumErrors;
        this.index = dataBaseIndex;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;

        this.fav = fav;
    }



    public int getImageResource()
    {
        return imageResource;
    }


}
