package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

public class FetchAlbumsWorker extends Worker {

    public FetchAlbumsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        loadAlbums();
        return Result.success();
    }

    private void loadAlbums() {
        MusicLibraryService.getInstance().setAlbumList(Chords.getDatabase().albumDao().getAllAlbums());
    }
}
