package com.puzzle.industries.chordsmusicapp.helpers;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;

public class MapperHelper {

    public static SongDataStruct mapTrackToSongDataStruct(DeezerTrackDataModel track) {
        return new SongDataStruct(Integer.parseInt(track.getId()), track.getTitle(),
                track.getArtist(), track.getAlbum(), track.getTrack_position(), track.getRelease_date());
    }

}
