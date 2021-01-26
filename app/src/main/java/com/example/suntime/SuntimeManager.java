package com.example.suntime;

import android.location.Location;
import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

public class SuntimeManager {

    public static SuntimeItem getSuntime(Location loc) {
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        int timezone = getTimezone(longitude);

        com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(String.valueOf(latitude), String.valueOf(longitude));
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "UTC");

        ArrayList<Integer> items = new ArrayList();
        for (int i = 0; i < 12; i++) {
            Calendar month = Calendar.getInstance();
            month.clear();
            month.set(2020, i, 1);
            int days = month.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int j = 1; j <= days; j++) {
                Calendar day = Calendar.getInstance();
                month.clear();
                day.set(2020, i, j);

                Calendar sunrise = calculator.getOfficialSunriseCalendarForDate(day);
                Calendar sunset = calculator.getOfficialSunsetCalendarForDate(day);
                int sunriseHour = adjustHour(sunrise.get(Calendar.HOUR_OF_DAY), timezone);
                int sunsetHour = adjustHour(sunset.get(Calendar.HOUR_OF_DAY), timezone);
                items.add(new Integer(sunriseHour));
                items.add(new Integer(sunrise.get(Calendar.MINUTE)));
                items.add(new Integer(sunsetHour));
                items.add(new Integer(sunset.get(Calendar.MINUTE)));
            }
        }
        int length = items.size();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = int2byte(items.get(i));
        }
        String timeBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return new SuntimeItem(timezone, timeBase64);
    }

    public static String getSuntimeTest(Location loc) {
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        int timezone = getTimezone(longitude);

        com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(String.valueOf(latitude), String.valueOf(longitude));
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "UTC");

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");

        String value = "latitude:" + latitude + "\nlongitude:" + longitude + "\ntimezone:" + timezone + "\n";
        for (int i = 0; i < 12; i++) {
            Calendar month = Calendar.getInstance();
            month.clear();
            month.set(2020, i, 1);
            int days = month.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int j = 1; j <= days; j++) {
                Calendar day = Calendar.getInstance();
                month.clear();
                day.set(2020, i, j);

                Calendar sunrise = calculator.getOfficialSunriseCalendarForDate(day);
                Calendar sunset = calculator.getOfficialSunsetCalendarForDate(day);
                int sunriseHour = adjustHour(sunrise.get(Calendar.HOUR_OF_DAY), timezone);
                int sunsetHour = adjustHour(sunset.get(Calendar.HOUR_OF_DAY), timezone);
                String sunriseStr = String.format("%02d", sunriseHour) + ":" + String.format("%02d", sunrise.get(Calendar.MINUTE));
                String sunsetStr = String.format("%02d", sunsetHour) + ":" + String.format("%02d", sunset.get(Calendar.MINUTE));
                value += sdf.format(day.getTime()) + " sunrise" +" " + sunriseStr + " sunset" + " " + sunsetStr + "\n";
            }
        }
        return value;
    }

    private static int getTimezone(double longitude) {
        boolean isWest = (longitude < 0);
        double lng = Math.abs(longitude);
        int quotient = (int)(lng / 15);
        double remainder = lng % 15;
        if (remainder > 7.5) {
            quotient += 1;
        }
        if (isWest) {
            return -quotient;
        } else {
            return quotient;
        }
    }

    private static int adjustHour(int hour, int timezone) {
        hour += timezone;
        if (hour < 0) {
            hour += 24;
        } else if (hour == 24) {
            hour = 0;
        } else if (hour > 24) {
            hour -= 24;
        }
        return hour;
    }

    private static byte int2byte(int i) {
        return new Integer(i).byteValue();
    }
}
