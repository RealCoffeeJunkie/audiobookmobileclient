package de.lanian.audiobookmobileclient;

import android.content.Context;

public class App {
    private static App APP;

    private Context appContext;

    private String serverIp;

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

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerIp() {
        return this.serverIp;
    }
}
