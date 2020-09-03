package com.ernokun.sizetracker.utils;

import android.content.res.Resources;

import com.ernokun.sizetracker.R;

public class MyResources {
    // Reference to resources in MainActivity
    private Resources resources;


    // Array of months.
    private String[] months;


    // Array of weight units (kg, lbs)
    private String[] weight_units;


    // Colors
    private int white;
    private int dark;
    private int purple;
    private int blue;
    private int black;


    public MyResources(Resources resources) {
        this.resources = resources;

        // Gets values from resources.
        setupColorsFromResources();
        setupStringsFromResources();
    }


    private void setupStringsFromResources() {
        months = resources.getStringArray(R.array.months);
        weight_units = resources.getStringArray(R.array.weight_units);
    }


    private void setupColorsFromResources() {
        white = resources.getColor(R.color.white, null);
        dark = resources.getColor(R.color.dark, null);
        purple = resources.getColor(R.color.purple, null);
        blue = resources.getColor(R.color.blue, null);
        black = resources.getColor(R.color.black, null);
    }


    public String[] getMonths() {
        return months;
    }


    public String[] getWeight_units() {
        return weight_units;
    }


    public int getWhite() {
        return white;
    }


    public int getDark() {
        return dark;
    }


    public int getPurple() {
        return purple;
    }


    public int getBlue() {
        return blue;
    }


    public int getBlack() {
        return black;
    }
}

