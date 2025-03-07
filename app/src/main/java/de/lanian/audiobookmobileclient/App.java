package de.lanian.audiobookmobileclient;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.List;

import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.utils.Preferences;

public class App {

    private static final String PREF_SET = "AudioBookMobileClient";
    private static App APP;

    /** Attributes **/
    private final Context appContext;
    private List<AudioBook> audioBookList;

    /** Singleton */
    private App(Context context) {
        this.appContext = context;
    }

    public static void createInstance(Context context) {
        APP = new App(context);
    }

    public static App getApp() {
        return APP;
    }

    /** Getter & Setter */
    public Context getAppContext() {
        return this.appContext;
    }

    public void setAudioBookList(List<AudioBook> books) {
        this.audioBookList = books;
    }

    public List<AudioBook> getAudioBookList() {
        return this.audioBookList;
    }

    public void setAppPreference(@NonNull Preferences preferences, @NonNull String value) {
        SharedPreferences.Editor editor = appContext.getSharedPreferences(PREF_SET,0).edit();
        editor.putString(preferences.toString(), value);
        editor.apply();
    }

    public String getAppPreference(@NonNull Preferences preference) {
        return this.appContext.getSharedPreferences(PREF_SET,0).getString(preference.toString(), null);
    }
}
