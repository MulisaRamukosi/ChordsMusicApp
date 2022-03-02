package com.puzzle.industries.chordsmusicapp.callbacks;

import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;

public interface PlaylistCallback {

    void playlistCreated(PlaylistEntity playlistEntity);

    void songsAddedToPlaylist();

    void playlistUpdated();

    void operationFailed();

}
