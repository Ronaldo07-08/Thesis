package com.example.vmeste;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSkills {
    private static final String PREFS_NAME = "UserSkillsPrefs";
    private static final String SKILL_LEVEL_KEY = "skillLevel";

    public static int getSkillLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(SKILL_LEVEL_KEY, 0); // 0 - тест не пройден
    }

    public static void setSkillLevel(Context context, int level) {
        if (level >= 1 && level <= 5) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(SKILL_LEVEL_KEY, level).apply();
        }
    }
}
