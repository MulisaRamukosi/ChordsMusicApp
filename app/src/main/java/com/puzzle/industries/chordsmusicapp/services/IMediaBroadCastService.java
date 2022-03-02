package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;

public interface IMediaBroadCastService {

    void songAdded(TrackArtistAlbumEntity song);

    void artistAdded(ArtistEntity artist);

    void albumAdded(AlbumArtistEntity album);

    void songRemoved(TrackArtistAlbumEntity song);

    void artistRemoved(ArtistEntity artist);

    void albumRemoved(AlbumArtistEntity album);

    void playlistRemoved(PlaylistEntity playlist);

    void playlistTrackRemoved(PlaylistTrackEntity playlistTrack);

    void playlistTracksRemoved();

    void songInfoStructChanged(SongInfoStruct songInfoStruct);

}
