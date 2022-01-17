package com.puzzle.industries.chordsmusicapp.remote.deezer.models;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class DeezerSongResultDataModel {

    private ArrayList<SongDataStruct> data;
    private int total;
    private String next;

}
