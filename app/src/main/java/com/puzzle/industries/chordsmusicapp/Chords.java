package com.puzzle.industries.chordsmusicapp;

import static java.lang.System.out;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import androidx.room.Room;

import com.puzzle.industries.chordsmusicapp.database.ChordsMusicDB;
import com.puzzle.industries.chordsmusicapp.database.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        db = Room.databaseBuilder(this, ChordsMusicDB.class, Constants.DB_NAME).build();
        applicationHandler = new Handler(this.getApplicationContext().getMainLooper());

        /*File[] f = getExternalMediaDirs();
        File test = new File(f[0], "test.txt");
        if (!test.exists()) {
            try {
                test.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
