package com.puzzle.industries.chordsmusicapp.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkInfo;

import com.google.common.util.concurrent.ListenableFuture;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

import java.util.List;
import java.util.UUID;

public interface IDownloadManagerService {

    void downloadSong(SongDataStruct song, String url);
    boolean isDownloading(SongDataStruct song);
    List<SongDataStruct> getDownloadsQueue();

}
