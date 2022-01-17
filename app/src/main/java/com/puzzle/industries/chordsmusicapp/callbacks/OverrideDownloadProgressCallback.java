package com.puzzle.industries.chordsmusicapp.callbacks;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

public interface OverrideDownloadProgressCallback {

    void updateProgress(int currentProgress);
    void downloadComplete();
    void downloadFailed();

}
