package com.puzzle.industries.chordsmusicapp.services.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.services.IMediaServiceManager;

import lombok.Getter;

public class MediaServiceManager implements IMediaServiceManager {

    @Getter private static final MediaServiceManager instance = new MediaServiceManager();

    private final Class<?>[] services = new Class<?>[]{
            MusicLibraryService.class,
            MusicPlayerService.class
    };

    @Override
    public void startServices() {
        final Context ctx = Chords.getAppContext();
        for (Class<?> service : services) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(new Intent(ctx, service));
            } else {
                ctx.startService(new Intent(ctx, service));
            }
        }
    }

    @Override
    public void stopServices() {
        final Context ctx = Chords.getAppContext();
        for (Class<?> service : services) {
            ctx.stopService(new Intent(ctx, service));

        }
        MediaPlayerNotificationService.getInstance().closeMediaPlayerNotification();
        final ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(ctx.getPackageName());
    }
}
