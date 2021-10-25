package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

import java.util.List;

public interface IMusicPlayerService {

    void pause();
    void resume();
    void playSong(int id);
    void setPlayList(List<TrackArtistAlbumEntity> songs);
    void playNextSong();
    void playPreviousSong();
    TrackArtistAlbumEntity getCurrentSong();
    int getTotalDuration();
    void seekTo(int pos);
    void playOrPause(int id);

}
