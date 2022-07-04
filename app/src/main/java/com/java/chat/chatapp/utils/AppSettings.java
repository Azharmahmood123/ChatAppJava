package com.java.chat.chatapp.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.java.chat.chatapp.App;

public class AppSettings {

    public static String PREF_USER_ID = "pref_user_id";

    public static void setUserID(String userID) {
        setString(PREF_USER_ID, userID);
    }

    public static String getUserId() {
        return getString(PREF_USER_ID);
    }

    private static void setString(String key, String value) {
        SharedPreferences userPref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getString(String key) {
        SharedPreferences usePref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        return usePref.getString(key, "");
    }

    private static void setBool(String key, boolean value) {
        SharedPreferences userPref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getBool(String key) {
        SharedPreferences usePref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        return usePref.getBoolean(key, false);
    }

    private static void setInt(String key, int value) {
        SharedPreferences userPref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        SharedPreferences usePref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        return usePref.getInt(key, 0);
    }

    public static void removeAll() {
        SharedPreferences userPref = App.self.getSharedPreferences(App.packageID, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.clear();
        editor.apply();
    }

}
