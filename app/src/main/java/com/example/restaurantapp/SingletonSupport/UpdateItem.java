package com.example.restaurantapp.SingletonSupport;

public class UpdateItem {
    String Name;
    String InspectionDate;
    String HazardLevel;
    String tracking;

    public UpdateItem(String name, String inspectionDate, String hazardLevel, String trackingNumber){
        Name = name;
        InspectionDate = inspectionDate;
        HazardLevel = hazardLevel;
        tracking = trackingNumber;
    }

    public String getDate(){
        return InspectionDate;
    }

    public String getName(){
        return Name;
    }

    public String getHazard(){
        return HazardLevel;
    }
}
