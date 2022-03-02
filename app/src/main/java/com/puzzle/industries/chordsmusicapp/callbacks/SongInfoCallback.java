package com.puzzle.industries.chordsmusicapp.callbacks;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;

public interface SongInfoCallback {

    void songInfoChanged(SongInfoStruct mSongInfo);
    void songInfoChangeFailed();

}
