package de.lanian.audiobookmobileclient;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import de.lanian.audiobookmobileclient.utils.Preferences;

public class App {

    private static final String PREF_SET = "AudioBookMobileClient";
    private static App APP;

    private Context appContext;

    private App(Context context) {
        this.appContext = context;
    }

    public static void createInstance(Context context) {
        APP = new App(context);
    }

    public static App getApp() {
        return APP;
    }

    public Context getAppContext() {
        return this.appContext;
    }

    public void setAppPreference(@NonNull Preferences preferenc, @NonNull String value) {
        SharedPreferences.Editor editor = appContext.getSharedPreferences(PREF_SET,0).edit();
        editor.putString(preferenc.toString(), value);
        editor.commit();
    }

    public String getAppPreference(@NonNull Preferences preference) {
        return this.appContext.getSharedPreferences(PREF_SET,0).getString(preference.toString(), null);
    }
}
