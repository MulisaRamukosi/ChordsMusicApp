package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

import java.util.List;

public interface IMusicLibraryService {

    void setMusicList(List<TrackArtistAlbumEntity> songs);
    void setArtistList(List<ArtistEntity> artists);
    void setAlbumList(List<AlbumArtistEntity> albums);
    ArtistEntity getArtistById(int id);
    AlbumArtistEntity getAlbumById(int id);
    void addSong(TrackArtistAlbumEntity song);
    void addArtist(ArtistEntity artist);
    void addAlbum(AlbumArtistEntity album);
    boolean removeSong(TrackArtistAlbumEntity song);
    boolean removeAlbum(AlbumArtistEntity album);

}
