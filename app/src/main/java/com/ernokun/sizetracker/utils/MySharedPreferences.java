package com.ernokun.sizetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class MySharedPreferences {

    private final static String SHARED_PREFS = "shared_prefs";
    private final static String SHARED_PREF_WEIGHT_UNIT = "weight_unit";


    public static void saveWeight_unit(Context context, String weight_unit) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SHARED_PREF_WEIGHT_UNIT, weight_unit);

        editor.commit();
    }


    public static String getWeight_unit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        String weight_unit = sharedPreferences.getString(SHARED_PREF_WEIGHT_UNIT, null);

        return weight_unit;
    }
}
