package com.puzzle.industries.chordsmusicapp.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkInfo;

import com.google.common.util.concurrent.ListenableFuture;
import com.puzzle.industries.chordsmusicapp.callbacks.SongAddedToDownloadQueueCallback;
import com.puzzle.industries.chordsmusicapp.models.dataModels.DownloadItemDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

public interface IDownloadManagerService {

    void downloadSong(SongDataStruct song, SongAddedToDownloadQueueCallback callback);
    void retryDownload(SongDataStruct song);
    void updateSongState(SongDataStruct song, DownloadState state);
    MutableLiveData<DownloadItemDataStruct> getDownloadProgressObservable(int songId);
    Map<Integer, MutableLiveData<DownloadItemDataStruct>> getDownloadsQueue();
    boolean containsSong(int songId);

}
