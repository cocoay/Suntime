package com.example.suntime;

public class SuntimeItem {
    public final int timezone;
    public final String timeBase64;

    public SuntimeItem(int timezone, String timeBase64) {
        this.timezone = timezone;
        this.timeBase64 = timeBase64;
    }
}
