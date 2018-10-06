package com.example.lucete.somchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyUtils {

    public static String convertTime(long timestamp) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date(timestamp);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}
