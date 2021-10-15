package com.puzzle.industries.chordsmusicapp.services.impl;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.workers.DownloadSongWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import lombok.Getter;


public class DownloadManagerService implements IDownloadManagerService {

    private static DownloadManagerService instance;
    private final WorkManager mWorkManager;

    @Getter private final List<SongDataStruct> mDownloadQueue;

    public static DownloadManagerService getInstance() {
        if (instance == null){
            synchronized (DownloadManagerService.class){
                if (instance == null){
                    instance = new DownloadManagerService();
                }
            }
        }
        return instance;
    }


    private DownloadManagerService() {
        this.mWorkManager = WorkManager.getInstance(Chords.getAppContext());
        mDownloadQueue = new ArrayList<>();
    }

    @Override
    public void downloadSong(SongDataStruct song, String url) {
        final String sSong = new Gson().toJson(song);

        final OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownloadSongWorker.class)
                .addTag(String.valueOf(song.getId()))
                .setInputData(new Data.Builder()
                        .putString(Constants.KEY_SONG, sSong)
                        .putString(Constants.URL_SONG, url)
                        .build()
                ).build();

        mWorkManager.enqueue(request);
        mDownloadQueue.add(song);
    }

    @Override
    public List<SongDataStruct> getDownloadsQueue() {
        return mDownloadQueue;
    }

    @Override
    public boolean isDownloading(SongDataStruct song){
        return mDownloadQueue.contains(song);
    }


}
