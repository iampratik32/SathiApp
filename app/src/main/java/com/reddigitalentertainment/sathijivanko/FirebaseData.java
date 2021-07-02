package com.reddigitalentertainment.sathijivanko;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseData extends Application {
    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}
