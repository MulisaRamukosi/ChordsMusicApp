package com.puzzle.industries.chordsmusicapp.services.impl;

import android.content.Intent;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IMediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

public class MediaBroadCastService implements IMediaBroadCastService {

    private static MediaBroadCastService instance;

    public static MediaBroadCastService getInstance(){
        if (instance == null){
            synchronized (MediaBroadCastService.class){
                if (instance == null){
                    instance = new MediaBroadCastService();
                }
            }
        }
        return instance;
    }

    @Override
    public void songAdded(TrackArtistAlbumEntity song) {
        final Intent i = new Intent(Constants.ACTION_MUSIC_ADDED_TO_LIST);
        i.putExtra(Constants.KEY_SONG, song);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void artistAdded(ArtistEntity artist) {
        final Intent i = new Intent(Constants.ACTION_ARTIST_ADDED_TO_LIST);
        i.putExtra(Constants.KEY_ARTIST, artist);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void albumAdded(AlbumArtistEntity album) {
        final Intent i = new Intent(Constants.ACTION_ALBUM_ADDED_TO_LIST);
        i.putExtra(Constants.KEY_ALBUM, album);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void songRemoved(TrackArtistAlbumEntity song) {
        final Intent i = new Intent(Constants.ACTION_MUSIC_DELETED);
        i.putExtra(Constants.KEY_SONG, song);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void artistRemoved(ArtistEntity artist) {
        final Intent i = new Intent(Constants.ACTION_ARTIST_DELETED);
        i.putExtra(Constants.KEY_ARTIST, artist);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void albumRemoved(AlbumArtistEntity album) {
        final Intent i = new Intent(Constants.ACTION_ALBUM_DELETED);
        i.putExtra(Constants.KEY_ALBUM, album);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void playlistRemoved(PlaylistEntity playlist) {
        final Intent i = new Intent(Constants.ACTION_PLAYLIST_DELETED);
        i.putExtra(Constants.KEY_PLAYLIST, playlist);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void playlistTrackRemoved(PlaylistTrackEntity playlistTrack) {
        final Intent i = new Intent(Constants.ACTION_PLAYLIST_TRACK_DELETED);
        i.putExtra(Constants.KEY_PLAYLIST_TRACK, playlistTrack);
        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void playlistTracksRemoved() {
        Chords.getAppContext().sendBroadcast(new Intent(Constants.ACTION_PLAYLIST_TRACKS_DELETED));
    }

}
