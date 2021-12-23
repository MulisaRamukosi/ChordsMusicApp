package com.puzzle.industries.chordsmusicapp.callbacks;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

public interface DownloadProgressCallback {

    void updateProgress(int currentProgress);
    void downloadComplete(SongDataStruct song);
    void downloadFailed();

}
