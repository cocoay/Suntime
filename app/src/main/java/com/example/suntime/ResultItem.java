package com.example.suntime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ResultItem {

    HashMap results;

    public String status;

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Calendar day = Calendar.getInstance();

        String temp = "status: " + status + "\ntime: " + sdf.format(day.getTime()) + "\nsunrise: " + results.get("sunrise") + "\nsunset: " + results.get("sunset");
        return temp;
    }
}
