package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;

import java.util.List;

public interface IMediaDeleteService {

    boolean deleteSong(int songId);
    boolean deleteAlbum(int albumId);
    boolean deleteArtist(int artistId);
    boolean deletePlaylist(int playlistId);
    boolean deletePlaylistTracks(List<PlaylistTrackEntity> playlistTracks);
}
