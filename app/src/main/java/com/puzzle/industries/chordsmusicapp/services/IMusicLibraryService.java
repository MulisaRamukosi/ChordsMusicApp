package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

import java.util.List;

public interface IMusicLibraryService {

    void setMusicPlaylist(List<Integer> playlist);
    void setMusicList(List<TrackArtistAlbumEntity> songs);
    void setArtistList(List<ArtistEntity> artists);
    void setAlbumList(List<AlbumArtistEntity> albums);
    void addSong(TrackArtistAlbumEntity song);
    void addArtist(ArtistEntity artist);
    void addAlbum(AlbumArtistEntity album);
    void setCurrentQueuePos(int pos);
    void addSongToQueue(int songId);
    void addSongsToQueue(List<Integer> songIds);
    void setAsShuffled(boolean shuffle);

    List<Integer> getPlaylist();
    List<TrackArtistAlbumEntity> getSongs();
    List<TrackArtistAlbumEntity> getArtistSongs(int artistId);
    List<TrackArtistAlbumEntity> getAlbumSongs(int albumId);
    List<ArtistEntity> getArtists();
    List<AlbumArtistEntity> getAlbums();
    List<AlbumArtistEntity> getArtistAlbums(int artistId);

    TrackArtistAlbumEntity getSongById(int id);
    ArtistEntity getArtistById(int id);
    AlbumArtistEntity getAlbumById(int id);

    boolean removeSong(TrackArtistAlbumEntity song);
    boolean removeAlbum(AlbumArtistEntity album);
    boolean removeArtist(ArtistEntity artist);
    boolean containsSong(int songId);
}
