package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

import java.util.List;

public interface IMusicLibraryService {

    void setMusicPlaylist(List<Integer> playlist);
    void setMusicList(List<TrackArtistAlbumEntity> songs);
    void setArtistList(List<ArtistEntity> artists);
    void setAlbumList(List<AlbumArtistEntity> albums);
    void setPlaylists(List<PlaylistEntity> playlists);
    void addPlaylistsTracks(List<PlaylistTrackEntity> playlistsTracks);
    void addSong(TrackArtistAlbumEntity song);
    void addArtist(ArtistEntity artist);
    void addAlbum(AlbumArtistEntity album);
    void addPlaylist(PlaylistEntity playlist);
    void setCurrentQueuePos(int pos);
    void addSongToQueue(int songId);
    void addSongsToQueue(List<Integer> songIds);
    void setAsShuffled(boolean shuffle);
    void addPlaylistTrack(PlaylistTrackEntity playlistTrack);

    List<Integer> getPlaylist();
    List<TrackArtistAlbumEntity> getSongs();
    List<TrackArtistAlbumEntity> getCurrentPlaylistSongs();
    List<TrackArtistAlbumEntity> getArtistSongs(int artistId);
    List<TrackArtistAlbumEntity> getAlbumSongs(int albumId);
    List<TrackArtistAlbumEntity> getPlaylistTracks(int playlistId);
    List<PlaylistTrackEntity> getPlaylistTrackEntityList(int playlistId);
    List<ArtistEntity> getArtists();
    List<AlbumArtistEntity> getAlbums();
    List<AlbumArtistEntity> getArtistAlbums(int artistId);
    List<PlaylistEntity> getPlaylists();

    TrackArtistAlbumEntity getSongById(int id);
    ArtistEntity getArtistById(int id);
    AlbumArtistEntity getAlbumById(int id);
    PlaylistEntity getPlaylistById(int id);
    PlaylistTrackEntity getPlaylistTrackByIds(int playlistId, int playlistTrackId);

    boolean removeSong(TrackArtistAlbumEntity song);
    boolean removeAlbum(AlbumArtistEntity album);
    boolean removeArtist(ArtistEntity artist);
    boolean removePlaylistTrack(PlaylistTrackEntity playlistTrack);
    boolean removePlaylist(PlaylistEntity playlist);
    boolean containsSong(int songId);
    boolean containsPlaylistTrack(int playlistId, PlaylistTrackEntity playlistTrackEntity);
}
