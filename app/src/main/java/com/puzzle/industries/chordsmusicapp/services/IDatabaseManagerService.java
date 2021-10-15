package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

public interface IDatabaseManagerService {

    void saveSongToDb(SongDataStruct song);
}
