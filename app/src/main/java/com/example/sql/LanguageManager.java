package com.example.sql;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import java.io.InputStream;
public class LanguageManager {
    private static JSONObject languageData;

    public static void loadLanguage(Context context, String langCode) {
        try {
            InputStream is = context.getAssets().open(langCode + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            languageData = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        if (languageData == null) return key;
        return languageData.optString(key, key);
    }
}
