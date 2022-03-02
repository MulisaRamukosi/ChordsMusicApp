package com.puzzle.industries.chordsmusicapp.services.impl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MusicLibraryService extends Service implements IMusicLibraryService {

    private static MusicLibraryService instance;
    private final Map<Integer, TrackArtistAlbumEntity> SONGS = new HashMap<>();
    private final Map<Integer, ArtistEntity> ARTISTS = new HashMap<>();
    private final Map<Integer, AlbumArtistEntity> ALBUMS = new HashMap<>();
    private final Map<Integer, PlaylistEntity> PLAYLISTS = new HashMap<>();
    private final Map<Integer, Set<PlaylistTrackEntity>> PLAYLISTS_TRACKS = new HashMap<>();
    private List<Integer> playlist;
    private int currentPosOfQueue;

    public static MusicLibraryService getInstance() {
        if (instance == null) {
            synchronized (MusicLibraryService.class) {
                if (instance == null) {
                    instance = new MusicLibraryService();
                }
            }
        }
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
        return START_NOT_STICKY;
    }

    @Override
    public void setMusicPlaylist(List<Integer> playlist) {
        this.playlist = playlist;
    }

    @Override
    public void setMusicList(List<TrackArtistAlbumEntity> songs) {
        for (TrackArtistAlbumEntity track : songs) {
            this.SONGS.put(track.getId(), track);
        }
    }

    @Override
    public void setArtistList(List<ArtistEntity> artists) {
        for (ArtistEntity artist : artists) {
            this.ARTISTS.put(artist.getId(), artist);
        }
    }

    @Override
    public void setAlbumList(List<AlbumArtistEntity> albums) {
        for (AlbumArtistEntity album : albums) {
            this.ALBUMS.put(album.getId(), album);
        }
    }

    @Override
    public void addPlaylistsTracks(List<PlaylistTrackEntity> playlistsTracks) {
        for (PlaylistTrackEntity playlistTrack : playlistsTracks) {
            this.PLAYLISTS_TRACKS.compute(playlistTrack.getPlaylistId(), (integer, playlistTrackEntities) -> {
                if (playlistTrackEntities == null) {
                    playlistTrackEntities = new HashSet<>();
                }
                playlistTrackEntities.add(playlistTrack);
                return playlistTrackEntities;
            });
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
    public List<TrackArtistAlbumEntity> getCurrentPlaylistSongs() {
        final List<TrackArtistAlbumEntity> currentSongs = new ArrayList<>();
        for (int songId : playlist) {
            final TrackArtistAlbumEntity track = SONGS.get(songId);
            if (track != null) {
                currentSongs.add(track);
            }
        }
        return currentSongs;
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
    public List<PlaylistEntity> getPlaylists() {
        return new ArrayList<>(PLAYLISTS.values());
    }

    @Override
    public void setPlaylists(List<PlaylistEntity> playlists) {
        for (PlaylistEntity playlist : playlists) {
            this.PLAYLISTS.put(playlist.getId(), playlist);
        }
    }

    @Override
    public List<TrackArtistAlbumEntity> getAlbumSongs(int albumId) {
        return new ArrayList<>(SONGS.values()).stream()
                .filter(trackArtistAlbumEntity -> trackArtistAlbumEntity.getAlbum_id() == albumId)
                .sorted(Comparator.comparingInt(TrackArtistAlbumEntity::getTrack_number))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrackArtistAlbumEntity> getPlaylistTracks(int playlistId) {
        final Set<PlaylistTrackEntity> playlistTracksIds = PLAYLISTS_TRACKS.get(playlistId);
        final List<TrackArtistAlbumEntity> playlistTracks = new ArrayList<>();
        if (playlistTracksIds != null) {
            for (PlaylistTrackEntity playlistTrack : playlistTracksIds) {
                playlistTracks.add(SONGS.get(playlistTrack.getTrackId()));
            }
        }
        Collections.sort(playlistTracks, Comparator.comparing(TrackArtistAlbumEntity::getTitle));
        return playlistTracks;
    }

    @Override
    public List<PlaylistTrackEntity> getPlaylistTrackEntityList(int playlistId) {
        final Set<PlaylistTrackEntity> playlistTrackEntitySet = PLAYLISTS_TRACKS.get(playlistId);
        if (playlistTrackEntitySet == null) return new ArrayList<>();
        return new ArrayList<>(playlistTrackEntitySet);
    }

    @Override
    public List<ArtistEntity> getArtists() {
        return ARTISTS.values().stream().sorted(Comparator.comparing(ArtistEntity::getName)).collect(Collectors.toList());
    }

    @Override
    public List<AlbumArtistEntity> getAlbums() {
        return ALBUMS.values().stream().sorted(Comparator.comparing(AlbumArtistEntity::getTitle)).collect(Collectors.toList());
    }

    @Override
    public TrackArtistAlbumEntity getSongById(int id) {
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
    public PlaylistEntity getPlaylistById(int id) {
        return this.PLAYLISTS.get(id);
    }

    @Override
    public PlaylistTrackEntity getPlaylistTrackByIds(int playlistId, int playlistTrackId) {
        final Set<PlaylistTrackEntity> playlistTracks = PLAYLISTS_TRACKS.get(playlistId);
        if (playlistTracks == null) {
            return null;
        }
        return playlistTracks.stream().filter(playlistTrackEntity -> playlistTrackEntity.getId() == playlistTrackId).findFirst().get();
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
    public void addPlaylist(PlaylistEntity playlist) {
        this.PLAYLISTS.put(playlist.getId(), playlist);
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
        if (shuffle) {
            Collections.shuffle(playlist);
        } else {
            Collections.sort(playlist, (id, otherId) -> {
                final TrackArtistAlbumEntity track = SONGS.get(id);
                final TrackArtistAlbumEntity otherTrack = SONGS.get(otherId);
                return track != null && otherTrack != null ? track.getTitle().compareTo(otherTrack.getTitle()) : 0;
            });
        }
    }

    @Override
    public void addPlaylistTrack(PlaylistTrackEntity playlistTrack) {
        PLAYLISTS_TRACKS.compute(playlistTrack.getPlaylistId(), (integer, playlistTrackEntities) -> {
            if (playlistTrackEntities == null) {
                playlistTrackEntities = new HashSet<>();
            }
            playlistTrackEntities.add(playlistTrack);
            return playlistTrackEntities;
        });
    }

    @Override
    public boolean removePlaylistTrack(PlaylistTrackEntity playlistTrack) {
        AtomicBoolean removed = new AtomicBoolean(false);
        PLAYLISTS_TRACKS.compute(playlistTrack.getPlaylistId(), (integer, playlistTrackEntities) -> {
            if (playlistTrackEntities != null) {
                removed.set(playlistTrackEntities.remove(playlistTrack));
            }
            return playlistTrackEntities;
        });
        return removed.get();
    }

    @Override
    public boolean removePlaylist(PlaylistEntity playlist) {
        return this.PLAYLISTS.remove(playlist.getId(), playlist);
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

    @Override
    public boolean containsPlaylistTrack(int playlistId, PlaylistTrackEntity playlistTrackEntity) {
        final Set<PlaylistTrackEntity> playlistTracks = this.PLAYLISTS_TRACKS.get(playlistId);
        if (playlistTracks != null) {
            return playlistTracks.contains(playlistTrackEntity);
        }
        return false;
    }

}
