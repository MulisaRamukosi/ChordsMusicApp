package com.puzzle.industries.chordsmusicapp.remote.deezer.models;

import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class DeezerArtistsDataModel {

    private ArrayList<ArtistDataStruct> data;
    private int total;
    private String next;

}
