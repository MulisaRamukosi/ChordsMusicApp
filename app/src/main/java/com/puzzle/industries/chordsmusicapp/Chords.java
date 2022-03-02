package com.puzzle.industries.chordsmusicapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import androidx.room.Room;

import com.puzzle.industries.chordsmusicapp.database.ChordsMusicDB;
import com.puzzle.industries.chordsmusicapp.database.Constants;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaPlayerNotificationService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaServiceManager;

public class Chords extends Application {

    public static volatile Handler applicationHandler;
    private static Chords instance;
    private static ChordsMusicDB db;

    public static ChordsMusicDB getDatabase() {
        return db;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static Resources getAppResources() {
        return instance.getApplicationContext().getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        db = Room.databaseBuilder(this, ChordsMusicDB.class, Constants.DB_NAME).build();
        applicationHandler = new Handler(this.getApplicationContext().getMainLooper());
        MediaPlayerNotificationService.getInstance().createNotificationChannel();

        MediaServiceManager.getInstance().startServices();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MediaServiceManager.getInstance().stopServices();
    }
}
