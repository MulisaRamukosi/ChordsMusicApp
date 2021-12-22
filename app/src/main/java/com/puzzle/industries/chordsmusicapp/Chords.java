package com.puzzle.industries.chordsmusicapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;

import androidx.room.Room;

import com.puzzle.industries.chordsmusicapp.database.ChordsMusicDB;
import com.puzzle.industries.chordsmusicapp.database.Constants;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaPlayerNotificationService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;

public class Chords extends Application {

    private static Chords instance;
    private static ChordsMusicDB db;
    public static volatile Handler applicationHandler;

    public static ChordsMusicDB getDatabase(){
        return db;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static Resources getAppResources(){
        return instance.getApplicationContext().getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        db = Room.databaseBuilder(this, ChordsMusicDB.class, Constants.DB_NAME).build();
        applicationHandler = new Handler(this.getApplicationContext().getMainLooper());
        MediaPlayerNotificationService.getInstance().createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(new Intent(this, MusicLibraryService.class));
            startForegroundService(new Intent(this, MusicPlayerService.class));
        }
        else{
            startService(new Intent(this, MusicLibraryService.class));
            startService(new Intent(this, MusicPlayerService.class));
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(new Intent(this, MusicPlayerService.class));
        stopService(new Intent(this, MusicLibraryService.class));
    }
}
