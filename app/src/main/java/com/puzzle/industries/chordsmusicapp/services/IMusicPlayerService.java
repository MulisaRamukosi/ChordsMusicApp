package com.puzzle.industries.chordsmusicapp.services;

import android.widget.ImageView;
import android.widget.SeekBar;

import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.events.PlaySongEvent;
import com.puzzle.industries.chordsmusicapp.utils.RepeatMode;

import java.util.List;

public interface IMusicPlayerService {

    void pause();

    void requestToPlay();

    void playSong(int id, List<Integer> playList);

    void playNextSong();

    void playPreviousSong();

    boolean isShuffleModeEnabled();

    void setShuffleModeEnabled(boolean enabled);

    RepeatMode getRepeatMode();

    void setRepeatMode(RepeatMode repeatMode);

    TrackArtistAlbumEntity getCurrentSong();

    void seekTo(int pos);

    void play(PlaySongEvent event);

    void initMiniPlayer(ImageView playPause, SeekBar songProgress);

    void initPlayer(ImageView playPause, SeekBar songProgress, ImageView nextSong, ImageView prevSong);

    boolean isPlaying();
}
