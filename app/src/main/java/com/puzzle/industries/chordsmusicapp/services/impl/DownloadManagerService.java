package com.puzzle.industries.chordsmusicapp.services.impl;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.models.dataModels.DownloadItemDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;
import com.puzzle.industries.chordsmusicapp.services.IDownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;
import com.puzzle.industries.chordsmusicapp.workers.DownloadSongWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;


public class DownloadManagerService implements IDownloadManagerService {

    private static DownloadManagerService instance;
    private final WorkManager mWorkManager;

    private volatile Map<Integer, DownloadItemDataStruct> mDownloadQueue;
    private final List<Integer> mQueueTrack;
    private final int MAX_ALLOWED_DOWNLOADS = 5;

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
        mDownloadQueue = new HashMap<>();
        mQueueTrack = new ArrayList<>();
    }

    @Override
    public void downloadSong(SongDataStruct song) {
        if (mDownloadQueue.containsKey(song.getId())){
            Chords.applicationHandler.post(() ->
                    Toast.makeText(Chords.getAppContext(), String.format("%s is already in the download queue", song.getSongName()),
                            Toast.LENGTH_SHORT).show());
        }
        else if (MusicLibraryService.getInstance().containsSong(song.getId())){
            Chords.applicationHandler.post(() ->
                    Toast.makeText(Chords.getAppContext(), String.format("%s is already exists", song.getSongName()),
                            Toast.LENGTH_SHORT).show());
        }
        else{
            mDownloadQueue.put(song.getId(), new DownloadItemDataStruct(song.getId(), song, DownloadState.IN_QUEUE));

            Chords.applicationHandler.post(() ->
                    Toast.makeText(Chords.getAppContext(), String.format("%s added to download queue", song.getSongName()),
                            Toast.LENGTH_SHORT).show());
            if (maxDownloadNotExceeded()){
                attemptToDownloadSong(song);
            }
            else{
                mQueueTrack.add(song.getId());
            }
        }

    }

    private synchronized boolean maxDownloadNotExceeded(){
        return mDownloadQueue.values().stream()
                .filter(downloadItemDataStruct ->
                        downloadItemDataStruct.getDownloadState() == DownloadState.PENDING
                || downloadItemDataStruct.getDownloadState() == DownloadState.DOWNLOADING)
                .count() < MAX_ALLOWED_DOWNLOADS;
    }

    private void attemptToDownloadSong(SongDataStruct song){
        updateSongState(song, DownloadState.PENDING);
        Chords.applicationHandler.post(() -> new MusicFinderApi(song, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String songUrl) {
                updateSongState(song, DownloadState.DOWNLOADING);

                final String sSong = new Gson().toJson(song);

                final OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownloadSongWorker.class)
                        .addTag(String.valueOf(song.getId()))
                        .setInputData(new Data.Builder()
                                .putString(Constants.KEY_SONG, sSong)
                                .putString(Constants.URL_SONG, songUrl)
                                .build()
                        ).build();

                mWorkManager.enqueue(request);
                final ListenableFuture<WorkInfo> result = mWorkManager.getWorkInfoById(request.getId());
                result.addListener(() -> {
                    if (result.isDone()){
                        downloadNextInQueueIfAvailable();
                    }
                }, Executors.newSingleThreadExecutor());
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Chords.getAppContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                updateSongState(song, DownloadState.FAILED);
            }
        }));
    }

    private void downloadNextInQueueIfAvailable(){
        if (!mQueueTrack.isEmpty()){
            final int id = mQueueTrack.remove(0);
            final DownloadItemDataStruct downloadItem = mDownloadQueue.get(id);
            if (downloadItem != null){
                updateSongState(downloadItem.getSong(), DownloadState.PENDING);
                attemptToDownloadSong(downloadItem.getSong());
            }
        }
    }

    @Override
    public Map<Integer, DownloadItemDataStruct> getDownloadsQueue() {
        return mDownloadQueue;
    }

    @Override
    public void retryDownload(SongDataStruct song){
        MediaBroadCastService.getInstance().mediaDownloadStateChanged(song, DownloadState.PENDING);
        attemptToDownloadSong(song);
    }

    @Override
    public void updateSongState(SongDataStruct song, DownloadState state) {
        mDownloadQueue.computeIfPresent(song.getId(), (integer, downloadItemDataStruct) -> {
            downloadItemDataStruct.setDownloadState(state);
            return downloadItemDataStruct;
        });
        MediaBroadCastService.getInstance().mediaDownloadStateChanged(song, state);
    }

}
