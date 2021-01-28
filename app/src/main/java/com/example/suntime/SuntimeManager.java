package com.example.suntime;

import android.location.Location;
import android.util.Base64;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.luckycatlabs.sunrisesunset.Zenith;
import com.luckycatlabs.sunrisesunset.calculator.SolarEventCalculator;


public class SuntimeManager {

    public static SuntimeItem getSuntime(Location loc) {
        if (loc == null) {
            return null;
        }
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        int timezone = getTimezone(longitude);

        com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(String.valueOf(latitude), String.valueOf(longitude));
        SuntimeCalculator calculator = new SuntimeCalculator(location, "UTC");

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
                if (sunrise != null && sunset != null) {
                    int sunriseHour = adjustHour(sunrise.get(Calendar.HOUR_OF_DAY), timezone);
                    int sunsetHour = adjustHour(sunset.get(Calendar.HOUR_OF_DAY), timezone);
                    items.add(new Integer(sunriseHour));
                    items.add(new Integer(sunrise.get(Calendar.MINUTE)));
                    items.add(new Integer(sunsetHour));
                    items.add(new Integer(sunset.get(Calendar.MINUTE)));
                }
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
        if (loc == null) {
            return null;
        }
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        int timezone = getTimezone(longitude);

        com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(String.valueOf(latitude), String.valueOf(longitude));
        SuntimeCalculator calculator = new SuntimeCalculator(location, "UTC");

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
                if (sunrise != null && sunset != null) {
                    int sunriseHour = adjustHour(sunrise.get(Calendar.HOUR_OF_DAY), timezone);
                    int sunsetHour = adjustHour(sunset.get(Calendar.HOUR_OF_DAY), timezone);
                    String sunriseStr = String.format("%02d", sunriseHour) + ":" + String.format("%02d", sunrise.get(Calendar.MINUTE));
                    String sunsetStr = String.format("%02d", sunsetHour) + ":" + String.format("%02d", sunset.get(Calendar.MINUTE));
                    value += sdf.format(day.getTime()) + " sunrise" +" " + sunriseStr + " sunset" + " " + sunsetStr + "\n";
                }
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



class SuntimeCalculator {

    private com.luckycatlabs.sunrisesunset.dto.Location location;
    private SuntimeEventCalculator calculator;

    public SuntimeCalculator(com.luckycatlabs.sunrisesunset.dto.Location location, String timeZoneIdentifier) {
        this.location = location;
        this.calculator = new SuntimeEventCalculator(location, timeZoneIdentifier);
    }

    public Calendar getOfficialSunriseCalendarForDate(Calendar date) {
        return calculator.computeSunriseCalendar(Zenith.OFFICIAL, date);
    }

    public Calendar getOfficialSunsetCalendarForDate(Calendar date) {
        return calculator.computeSunsetCalendar(Zenith.OFFICIAL, date);
    }
}


class SuntimeEventCalculator extends SolarEventCalculator {
    public SuntimeEventCalculator(com.luckycatlabs.sunrisesunset.dto.Location location, String timeZoneIdentifier) {
        super(location, timeZoneIdentifier);
    }

    @Override
    protected Calendar getLocalTimeAsCalendar(BigDecimal localTimeParam, Calendar date) {
        if (localTimeParam == null) {
            return null;
        }

        // Create a clone of the input calendar so we get locale/timezone information.
        Calendar resultTime = (Calendar) date.clone();

        BigDecimal localTime = localTimeParam;
        if (localTime.compareTo(BigDecimal.ZERO) == -1) {
            localTime = localTime.add(BigDecimal.valueOf(24.0D));
            resultTime.add(Calendar.HOUR_OF_DAY, -24);
        }
        String[] timeComponents = localTime.toPlainString().split("\\.");
        int hour = Integer.parseInt(timeComponents[0]);

        BigDecimal minutes = new BigDecimal("0." + timeComponents[1]);
        // 使用RoundingMode.DOWN向下取整
        minutes = minutes.multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.DOWN);
        if (minutes.intValue() == 60) {
            minutes = BigDecimal.ZERO;
            hour += 1;
        }
        if (hour == 24) {
            hour = 0;
        }

        // Set the local time
        resultTime.set(Calendar.HOUR_OF_DAY, hour);
        resultTime.set(Calendar.MINUTE, minutes.intValue());
        resultTime.set(Calendar.SECOND, 0);
        resultTime.set(Calendar.MILLISECOND, 0);
        resultTime.setTimeZone(date.getTimeZone());

        return resultTime;
    }
}