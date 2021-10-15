package com.puzzle.industries.chordsmusicapp.remote.deezer.models;


import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class DeezerAlbumDataModel {

    private ArrayList<AlbumDataStruct> data;
    private int total;
    private String next;

}
