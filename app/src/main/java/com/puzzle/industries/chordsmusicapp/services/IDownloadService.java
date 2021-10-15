package com.puzzle.industries.chordsmusicapp.services;

import com.puzzle.industries.chordsmusicapp.callbacks.DownloadProgressCallback;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

public interface IDownloadService {

    void downloadSong(SongDataStruct song, String fileUrl, DownloadProgressCallback callback);

}
