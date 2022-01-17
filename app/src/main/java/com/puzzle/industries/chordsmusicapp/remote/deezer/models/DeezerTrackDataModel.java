package com.puzzle.industries.chordsmusicapp.remote.deezer.models;

import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;

import lombok.Getter;

@Getter
public class DeezerTrackDataModel {

    private String id;
    private String title;
    private int track_position;
    private String release_date;
    private ArtistDataStruct artist;
    private AlbumDataStruct album;
}
