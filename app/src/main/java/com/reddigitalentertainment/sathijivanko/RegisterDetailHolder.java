package com.reddigitalentertainment.sathijivanko;

import java.util.LinkedList;

public class RegisterDetailHolder {

    private static LinkedList allDetails = new LinkedList();

    public static LinkedList getAllDetails() {
        return allDetails;
    }

    public static void setAllDetails(LinkedList allDetails) {
        RegisterDetailHolder.allDetails = allDetails;
    }
    public static void keepDetails(String detail){
        allDetails.add(detail);
    }

}
