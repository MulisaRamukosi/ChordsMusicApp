package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

public interface IMediaBroadCastService {

    void songAdded(TrackArtistAlbumEntity song);
    void artistAdded(ArtistEntity artist);
    void albumAdded(AlbumArtistEntity album);
    void songRemoved(TrackArtistAlbumEntity song);
    void artistRemoved(ArtistEntity artist);
    void albumRemoved(AlbumArtistEntity album);
    void mediaDownloadStateChanged(SongDataStruct song, DownloadState downloadState);
}
