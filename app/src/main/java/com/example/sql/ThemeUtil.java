// ThemeUtil.java
package com.example.sql;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtil {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_NIGHT_MODE = "night_mode";

    public static void saveThemePreference(Context context, boolean isNightMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_NIGHT_MODE, isNightMode).apply();

        // THIS IS WHAT WAS MISSING:
        AppCompatDelegate.setDefaultNightMode(
                isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean isNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_NIGHT_MODE, false);
    }

    public static void applyThemeFromPrefs(Context context) {
        boolean isNight = isNightMode(context);
        AppCompatDelegate.setDefaultNightMode(
                isNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static void applyBackground(Activity activity, int layoutId) {
        View layout = activity.findViewById(layoutId);
        if (layout != null) {
            boolean isNight = isNightMode(activity);
            Log.d("ThemeUtil", "Applying " + (isNight ? "NIGHT" : "DAY") + " background");
            layout.setBackgroundResource(isNight ? R.drawable.ic_bbackgroundnight : R.drawable.ic_bbackground);
        } else {
            Log.e("ThemeUtil", "Layout not found for background change");
        }
    }

}
