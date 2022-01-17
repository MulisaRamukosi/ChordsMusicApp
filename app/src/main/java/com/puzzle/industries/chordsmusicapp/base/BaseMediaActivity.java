package com.puzzle.industries.chordsmusicapp.base;

import android.content.Intent;

import com.puzzle.industries.chordsmusicapp.activities.PlayerActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.events.PlaySongEvent;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class BaseMediaActivity extends BaseActivity{

    protected final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayPauseEvent(PlaySongEvent event){
        if (getMusicPlayerService() != null){

            if (getMusicPlayerService().getCurrentSong() != null && getMusicPlayerService().getCurrentSong().getId() == event.getId()){
                final Intent i = new Intent(this, PlayerActivity.class);
                startActivity(i);
            }
            else{
                getMusicPlayerService().play(event);
            }
        }
    }

}
