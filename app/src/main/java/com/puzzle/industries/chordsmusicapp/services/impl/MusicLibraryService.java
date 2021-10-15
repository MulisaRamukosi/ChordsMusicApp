package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class MusicLibraryService implements IMusicLibraryService {

    private static final MusicLibraryService instance = new MusicLibraryService();
    private List<TrackArtistAlbumEntity> mSongs;
    private List<ArtistEntity> artists;
    private List<AlbumArtistEntity> albums;

    public static MusicLibraryService getInstance(){
        return instance;
    }

    @Override
    public void setMusicList(List<TrackArtistAlbumEntity> songs) {
        this.mSongs = songs;
    }

    @Override
    public void setArtistList(List<ArtistEntity> artists) {
        this.artists = artists;
    }

    @Override
    public void setAlbumList(List<AlbumArtistEntity> albums) {
        this.albums = albums;
    }

    @Override
    public ArtistEntity getArtistById(int id) {
        final List<ArtistEntity> artist = artists.stream().filter(artistEntity -> artistEntity.getId() == id).collect(Collectors.toList());
        return artist.size() == 0 ? null : artist.get(0);
    }

    @Override
    public AlbumArtistEntity getAlbumById(int id) {
        final List<AlbumArtistEntity> album = albums.stream().filter(albumArtistEntity -> albumArtistEntity.getId() == id).collect(Collectors.toList());
        return album.size() == 0 ? null : album.get(0);
    }

    @Override
    public void addSong(TrackArtistAlbumEntity song) {
        mSongs.add(song);
    }

    @Override
    public void addArtist(ArtistEntity artist) {
        artists.add(artist);
    }

    @Override
    public void addAlbum(AlbumArtistEntity album) {
        albums.add(album);
    }

    @Override
    public boolean removeSong(TrackArtistAlbumEntity song) {
        return mSongs.remove(song);
    }

    @Override
    public boolean removeAlbum(AlbumArtistEntity album) {
        return albums.remove(album);
    }

}
