package com.puzzle.industries.chordsmusicapp.callbacks;

public interface OverrideDownloadProgressCallback {

    void updateProgress(int currentProgress);

    void downloadComplete();

    void downloadFailed();

}
