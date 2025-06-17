package com.example.vmeste;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_FIRST_RUN = "isFirstRun";

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstRun() {
        return prefs.getBoolean(KEY_FIRST_RUN, true);
    }

    public void setFirstRun(boolean isFirst) {
        prefs.edit().putBoolean(KEY_FIRST_RUN, isFirst).apply();
    }
}