package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;

import java.util.List;
import java.util.Set;

public interface IPlaylistService {

    void addPlaylist(PlaylistEntity playlist);
    void addSongsToPlaylist(int playlistId, List<PlaylistTrackEntity> playlistTracks);
    void addSongToPlaylist(int playlistId, int songId);
    void updatePlaylistName(int playlistId, String playlistNewName);
    void removeSongsFromPlaylist(int playlistId, Set<PlaylistTrackEntity> mRemovedSongs);

}
