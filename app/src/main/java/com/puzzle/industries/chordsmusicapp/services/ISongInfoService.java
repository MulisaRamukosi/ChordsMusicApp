package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

public interface ISongInfoService {

    void getSongInfo(TrackArtistAlbumEntity track, ApiCallBack<SongInfoStruct> callback);

}
