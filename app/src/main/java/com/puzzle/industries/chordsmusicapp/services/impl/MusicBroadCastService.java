package com.puzzle.industries.chordsmusicapp.services.impl;

import android.content.Intent;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.services.IMusicBroadCastService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class MusicBroadCastService implements IMusicBroadCastService {

    private static MusicBroadCastService instance;

    public static MusicBroadCastService getInstance(){
        if (instance == null){
            synchronized (MusicBroadCastService.class){
                if (instance == null){
                    instance = new MusicBroadCastService();
                }
            }
        }
        return instance;
    }

    @Override
    public void musicQueueChanged() {
        final Intent i = new Intent(Constants.ACTION_MUSIC_ADDED_TO_QUEUE);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void artistQueueChanged() {
        final Intent i = new Intent(Constants.ACTION_AQ);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void albumQueueChanged() {
        final Intent i = new Intent(Constants.ACTION_ABQ);
        Chords.getAppContext().sendBroadcast(i);
    }
}
