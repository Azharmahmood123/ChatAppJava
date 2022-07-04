package com.java.chat.chatapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    public static App self;
    public static String packageID;

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        try {
            packageID = self.getPackageManager().getPackageInfo(self.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageID = "com.java.chat.chatapp";
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }

    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }
}
