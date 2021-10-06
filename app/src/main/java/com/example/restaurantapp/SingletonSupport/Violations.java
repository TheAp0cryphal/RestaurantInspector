package com.example.restaurantapp.SingletonSupport;

/*
    This is the Violations class. Here you can access all the specific information about
    one specific Violation.
 */

public class Violations {
    private String code;
    private String critical;
    private String description;
    private String repetition;

    public Violations(String Code, String Critical, String Description, String Repetition){
        code = Code.replace("\"", "");
        critical = Critical.replace("\"", "");;
        description = Description.replace("\"", "");;
        repetition = Repetition.replace("\"", "");;
    }

    public String getCode(){
        return code;
    }

    public String getCritical(){
        return critical;
    }

    public String getDescription(){
        return description;
    }

    public String getShortDescription(){return description.substring(0, 30) + " [...]";}

    public String getRepetition(){
        return repetition;
    }


}
