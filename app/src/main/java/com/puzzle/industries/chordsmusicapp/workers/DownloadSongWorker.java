package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.callbacks.DownloadProgressCallback;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDownloadService;
import com.puzzle.industries.chordsmusicapp.services.impl.DatabaseManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

public class DownloadSongWorker extends Worker implements DownloadProgressCallback {

    private IDownloadService mDownloadService;
    private SongDataStruct song;

    public DownloadSongWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data data = getInputData();
        final String sSong = data.getString(Constants.KEY_SONG);
        final String songUrl = data.getString(Constants.URL_SONG);
        song = new Gson().fromJson(sSong, SongDataStruct.class);

        mDownloadService = new DownloadService();
        mDownloadService.downloadSong(song, songUrl, this);
        return Result.success();
    }

    @Override
    public void updateProgress(int currentProgress) {
        final Intent i = new Intent(Constants.ACTION_DOWNLOAD_PROGRESS);
        i.putExtra(Constants.KEY_SONG, song);
        i.putExtra(Constants.KEY_DOWNLOAD_PROGRESS, currentProgress);

        Chords.getAppContext().sendBroadcast(i);
    }

    @Override
    public void downloadComplete(SongDataStruct song) {
        DownloadManagerService.getInstance().updateSongState(song, DownloadState.COMPLETE);
        DatabaseManagerService.getInstance().saveSongToDb(song);
    }

    @Override
    public void downloadFailed() {
        DownloadManagerService.getInstance().updateSongState(song, DownloadState.FAILED);
    }

}
