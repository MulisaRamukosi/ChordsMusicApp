package com.puzzle.industries.chordsmusicapp.services.impl;

import android.content.Intent;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
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
    public void mediaDownloadStateChanged(SongDataStruct song, DownloadState downloadState) {
        final Intent i = new Intent(Constants.ACTION_DOWNLOAD_STATE);
        i.putExtra(Constants.KEY_SONG, song);
        i.putExtra(Constants.KEY_DOWNLOAD_STATE, downloadState);
        Chords.getAppContext().sendBroadcast(i);
    }
}
