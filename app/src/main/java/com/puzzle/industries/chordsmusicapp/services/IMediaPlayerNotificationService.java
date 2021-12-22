package com.puzzle.industries.chordsmusicapp.services;

import android.app.Notification;

import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;

public interface IMediaPlayerNotificationService {

    void createNotificationChannel();
    Notification createNotification();
    void showMediaNotification(SongInfoProgressEvent song);
    void closeMediaPlayerNotification();

}
