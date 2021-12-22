package com.puzzle.industries.chordsmusicapp.services.impl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MusicLibraryService extends Service implements IMusicLibraryService {

    private static final MusicLibraryService instance = new MusicLibraryService();

    private List<Integer> playlist;
    private int currentPosOfQueue;

    private final Map<Integer, TrackArtistAlbumEntity> SONGS = new HashMap<>();
    private final Map<Integer, ArtistEntity> ARTISTS = new HashMap<>();
    private final Map<Integer, AlbumArtistEntity> ALBUMS = new HashMap<>();

    public static MusicLibraryService getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(MediaPlayerNotificationService.NOTIFICATION_ID, MediaPlayerNotificationService.getInstance().createNotification());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void setMusicPlaylist(List<Integer> playlist) {
        this.playlist = playlist;
    }

    @Override
    public void setMusicList(List<TrackArtistAlbumEntity> songs) {
        for (TrackArtistAlbumEntity track : songs){
            this.SONGS.put(track.getId(), track);
        }
    }

    @Override
    public void setArtistList(List<ArtistEntity> artists) {
        for (ArtistEntity artist : artists){
            this.ARTISTS.put(artist.getId(), artist);
        }
    }

    @Override
    public void setAlbumList(List<AlbumArtistEntity> albums) {
        for (AlbumArtistEntity album : albums){
            this.ALBUMS.put(album.getId(), album);
        }
    }

    @Override
    public List<Integer> getPlaylist() {
        return this.playlist;
    }

    @Override
    public List<TrackArtistAlbumEntity> getSongs() {
        return SONGS.values().stream().sorted(Comparator.comparing(TrackArtistAlbumEntity::getTitle)).collect(Collectors.toList());
    }

    @Override
    public List<TrackArtistAlbumEntity> getArtistSongs(int artistId) {
        return new ArrayList<>(SONGS.values()).stream()
                .filter(trackArtistAlbumEntity -> trackArtistAlbumEntity.getArtist_id() == artistId)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlbumArtistEntity> getArtistAlbums(int artistId) {
        return new ArrayList<>(ALBUMS.values()).stream()
                .filter(albumArtistEntity -> albumArtistEntity.getArtist_id() == artistId)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrackArtistAlbumEntity> getAlbumSongs(int albumId) {
        return new ArrayList<>(SONGS.values()).stream()
                .filter(trackArtistAlbumEntity -> trackArtistAlbumEntity.getAlbum_id() == albumId)
                .sorted(Comparator.comparingInt(TrackArtistAlbumEntity::getDisk_number))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistEntity> getArtists() {
        return new ArrayList<>(ARTISTS.values());
    }

    @Override
    public List<AlbumArtistEntity> getAlbums() {
        return new ArrayList<>(ALBUMS.values());
    }

    @Override
    public TrackArtistAlbumEntity getSongById(int id){
        return this.SONGS.get(id);
    }

    @Override
    public ArtistEntity getArtistById(int id) {
        return this.ARTISTS.get(id);
    }

    @Override
    public AlbumArtistEntity getAlbumById(int id) {
        return this.ALBUMS.get(id);
    }

    @Override
    public void addSong(TrackArtistAlbumEntity song) {
        this.SONGS.put(song.getId(), song);
    }

    @Override
    public void addArtist(ArtistEntity artist) {
        this.ARTISTS.put(artist.getId(), artist);
    }

    @Override
    public void addAlbum(AlbumArtistEntity album) {
        this.ALBUMS.put(album.getId(), album);
    }

    @Override
    public void setCurrentQueuePos(int pos) {
        this.currentPosOfQueue = pos;
    }


    @Override
    public void addSongToQueue(int songId) {
        this.playlist.add(currentPosOfQueue + 1, songId);
    }

    @Override
    public void addSongsToQueue(List<Integer> songIds) {
        this.playlist.addAll(currentPosOfQueue + 1, songIds);
    }

    @Override
    public void setAsShuffled(boolean shuffle) {
        if (shuffle){
            Collections.shuffle(playlist);
        }
        else{
            Collections.sort(playlist, (id, otherId) -> {
                final TrackArtistAlbumEntity track = SONGS.get(id);
                final TrackArtistAlbumEntity otherTrack = SONGS.get(otherId);
                return track != null && otherTrack != null ? track.getTitle().compareTo(otherTrack.getTitle()) : 0;
            });
        }
    }

    @Override
    public boolean removeSong(TrackArtistAlbumEntity song) {
        this.playlist.remove(Integer.valueOf(song.getId()));
        return this.SONGS.remove(song.getId(), song);
    }

    @Override
    public boolean removeAlbum(AlbumArtistEntity album) {
        return this.ALBUMS.remove(album.getId(), album);
    }

    @Override
    public boolean removeArtist(ArtistEntity artist) {
        return this.ARTISTS.remove(artist.getId(), artist);
    }

    @Override
    public boolean containsSong(int songId) {
        return this.SONGS.containsKey(songId);
    }


}
