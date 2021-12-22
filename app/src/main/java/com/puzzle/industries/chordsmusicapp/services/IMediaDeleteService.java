package com.puzzle.industries.chordsmusicapp.services;

public interface IMediaDeleteService {

    boolean deleteSong(int songId);
    boolean deleteAlbum(int albumId);
    boolean deleteArtist(int artistId);
}
