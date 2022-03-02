package com.puzzle.industries.chordsmusicapp.services.impl;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.callbacks.SongAddedToDownloadQueueCallback;
import com.puzzle.industries.chordsmusicapp.helpers.MapperHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.DownloadItemDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.api.DeezerApiCall;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;
import com.puzzle.industries.chordsmusicapp.services.IDownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;
import com.puzzle.industries.chordsmusicapp.workers.DownloadSongWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class DownloadManagerService implements IDownloadManagerService {

    private static DownloadManagerService instance;
    private final WorkManager mWorkManager;

    private final Map<Integer, MutableLiveData<DownloadItemDataStruct>> mDownloadQueue;
    private final List<Integer> mQueueTrack;

    private final int MAX_ALLOWED_DOWNLOADS = 5;
    private final AtomicInteger currDownloads = new AtomicInteger(0);

    private DownloadManagerService() {
        this.mWorkManager = WorkManager.getInstance(Chords.getAppContext());
        mDownloadQueue = new HashMap<>();
        mQueueTrack = new ArrayList<>();
    }

    public static DownloadManagerService getInstance() {
        if (instance == null) {
            synchronized (DownloadManagerService.class) {
                if (instance == null) {
                    instance = new DownloadManagerService();
                }
            }
        }
        return instance;
    }

    @Override
    public void downloadSong(SongDataStruct song, @Nullable SongAddedToDownloadQueueCallback callback) {
        if (mDownloadQueue.containsKey(song.getId())) {
            showToastNotification(String.format("%s is already in the download queue", song.getSongName()));
        } else if (MusicLibraryService.getInstance().containsSong(song.getId())) {
            showToastNotification(String.format("%s already exists", song.getSongName()));
        } else {
            retrieveSongInfoAndAttemptDownload(song, callback);
        }

    }

    private void retrieveSongInfoAndAttemptDownload(SongDataStruct song, @Nullable SongAddedToDownloadQueueCallback callback) {

        final int songId = song.getId();

        getTrackInfo(song.getId(), new ApiCallBack<DeezerTrackDataModel>() {
            @Override
            public void onSuccess(DeezerTrackDataModel track) {
                incrementDownloads();
                if (callback != null) callback.success();
                final SongDataStruct song = MapperHelper.mapTrackToSongDataStruct(track);
                final DownloadItemDataStruct downloadItemDataStruct = new DownloadItemDataStruct(songId, song, DownloadState.IN_QUEUE, true);
                final MutableLiveData<DownloadItemDataStruct> liveData = new MutableLiveData<>();
                liveData.setValue(downloadItemDataStruct);

                mDownloadQueue.put(songId, liveData);
                showToastNotification(String.format("%s added to download queue", song.getSongName()));

                boolean maxDownloadNotExceeded = maxDownloadNotExceeded();
                if (maxDownloadNotExceeded) {
                    attemptToDownloadSong(song);
                } else {
                    mQueueTrack.add(songId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (callback != null) callback.failed();
                decreaseDownloads();
                showToastNotification(String.format("Failed to get info of Track %s", song.getSongName()));

                final MutableLiveData<DownloadItemDataStruct> downloadItem = mDownloadQueue.get(songId);
                if (downloadItem != null) {
                    downloadItem.postValue(new DownloadItemDataStruct(songId, song, DownloadState.FAILED, false));
                }
                mDownloadQueue.put(songId, downloadItem);
            }
        });
    }

    private void getTrackInfo(int trackId, ApiCallBack<DeezerTrackDataModel> callBack) {
        DeezerApiCall.getInstance().getTrackInfoById(trackId, callBack);
    }

    private synchronized boolean maxDownloadNotExceeded() {
        return /*mDownloadQueue.values().stream()
                .filter(downloadItemDataStruct ->
                        Objects.requireNonNull(downloadItemDataStruct.getValue()).getDownloadState() == DownloadState.PENDING
                                || downloadItemDataStruct.getValue().getDownloadState() == DownloadState.IN_QUEUE
                                || downloadItemDataStruct.getValue().getDownloadState() == DownloadState.DOWNLOADING)
                .count() < MAX_ALLOWED_DOWNLOADS ||*/ currDownloads.get() < MAX_ALLOWED_DOWNLOADS;
    }

    private void attemptToDownloadSong(SongDataStruct song) {
        updateSongState(song, DownloadState.PENDING);

        Chords.applicationHandler.post(() -> new MusicFinderApi.MusicFinderApiBuilder()
                .setSongDataStruct(song)
                .setApiCallBack(new ApiCallBack<String>() {
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

                        initDownloadProgressObserver(song.getId(), request.getId());
                        initDownloadWorkerStateListener(request.getId());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        decreaseDownloads();
                        Toast.makeText(Chords.getAppContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        updateSongState(song, DownloadState.FAILED);
                    }
                }).build());
    }

    private void initDownloadWorkerStateListener(UUID workerId) {
        mWorkManager.getWorkInfoByIdLiveData(workerId).observeForever(workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                decreaseDownloads();
                downloadNextInQueueIfAvailable();
            }
        });

    }

    private void initDownloadProgressObserver(int songId, UUID downloadId) {
        final LiveData<WorkInfo> workObserver = mWorkManager.getWorkInfoByIdLiveData(downloadId);
        workObserver.observeForever(workInfo -> {
            final int progress = workInfo.getProgress().getInt(Constants.KEY_DOWNLOAD_PROGRESS, 0);
            final MutableLiveData<DownloadItemDataStruct> downloadItemObserver = mDownloadQueue.get(songId);
            if (downloadItemObserver != null) {
                final DownloadItemDataStruct downloadItem = downloadItemObserver.getValue();
                assert downloadItem != null;
                downloadItem.setDownloadProgress(progress);
                downloadItemObserver.postValue(downloadItem);
                mDownloadQueue.put(songId, downloadItemObserver);
            }
        });
    }

    private synchronized void downloadNextInQueueIfAvailable() {
        if (!mQueueTrack.isEmpty()) {
            final int id = mQueueTrack.remove(0);
            final MutableLiveData<DownloadItemDataStruct> observableDownload = mDownloadQueue.get(id);
            if (observableDownload != null) {
                final DownloadItemDataStruct downloadItem = observableDownload.getValue();
                if (downloadItem != null) {
                    attemptToDownloadSong(downloadItem.getSong());
                }
            }
        }
    }

    @Override
    public Map<Integer, MutableLiveData<DownloadItemDataStruct>> getDownloadsQueue() {
        return mDownloadQueue;
    }

    @Override
    public boolean containsSong(int songId) {
        return mDownloadQueue.containsKey(songId);
    }

    @Override
    public void retryDownload(SongDataStruct song) {
        final MutableLiveData<DownloadItemDataStruct> downloadItem = mDownloadQueue.get(song.getId());
        if (downloadItem != null) {
            final DownloadItemDataStruct downloadInfo = downloadItem.getValue();
            assert downloadInfo != null;
            if (!downloadInfo.isInfoSuccessfullyRetrieved()) {
                retrieveSongInfoAndAttemptDownload(song, null);
            } else {
                attemptToDownloadSong(downloadInfo.getSong());
            }
        }
    }

    @Override
    public void updateSongState(SongDataStruct song, DownloadState state) {
        final MutableLiveData<DownloadItemDataStruct> downloadItem = mDownloadQueue.get(song.getId());
        if (downloadItem != null) {
            final DownloadItemDataStruct item = downloadItem.getValue();
            if (item != null) {
                item.setDownloadState(state);
                downloadItem.postValue(item);
            }
        }
    }

    @Override
    public MutableLiveData<DownloadItemDataStruct> getDownloadProgressObservable(int songId) {
        return this.mDownloadQueue.get(songId);
    }

    private void showToastNotification(String message) {
        Chords.applicationHandler.post(() -> Toast.makeText(Chords.getAppContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void incrementDownloads() {
        if (currDownloads.get() < MAX_ALLOWED_DOWNLOADS) {
            currDownloads.incrementAndGet();
        }
    }

    private void decreaseDownloads() {
        if (currDownloads.get() > 0) currDownloads.decrementAndGet();
    }
}
