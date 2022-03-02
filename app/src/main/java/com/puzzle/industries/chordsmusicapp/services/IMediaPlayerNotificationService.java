package com.puzzle.industries.chordsmusicapp.services;

import android.app.Notification;
import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;

import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;

public interface IMediaPlayerNotificationService {

    void createNotificationChannel();

    Notification createNotification();

    void showMediaNotification(MediaPlayer player, MediaSessionCompat mMediaSessionCompat, SongInfoProgressEvent song);

    void closeMediaPlayerNotification();

}
