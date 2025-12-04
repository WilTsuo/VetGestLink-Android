package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.content.Context;
import android.content.SharedPreferences;
public class Singleton {

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_MAIN_URL = "main_url";

    private static Singleton instance = new Singleton();
    private String mainUrl;
    public static Singleton getInstance() {
        return instance;
    }

    // Call this once (e.g. in LoginActivity.onCreate)
    public void init(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mainUrl = prefs.getString(KEY_MAIN_URL, "http://172.22.21.220/backend/web/api/"); // default
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(Context context, String url) {
        this.mainUrl = url;
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MAIN_URL, url).apply();
    }

}