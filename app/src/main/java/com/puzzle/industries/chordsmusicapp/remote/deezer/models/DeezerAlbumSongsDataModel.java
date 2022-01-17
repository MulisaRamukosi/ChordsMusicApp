package com.puzzle.industries.chordsmusicapp.remote.deezer.models;


import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;

import java.util.Date;

import lombok.Getter;

@Getter
public class DeezerAlbumSongsDataModel {

    private int id;
    private String title;
    private String cover_big;
    private ArtistDataStruct artist;
    private Date release_date;
    private DeezerSongResultDataModel tracks;

}
