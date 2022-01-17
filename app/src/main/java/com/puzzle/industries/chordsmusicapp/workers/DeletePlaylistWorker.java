package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.base.BaseMediaDeleteWorker;

public class DeletePlaylistWorker extends BaseMediaDeleteWorker {

    public DeletePlaylistWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return deletePlaylist();
    }

    private Result deletePlaylist() {
        return MEDIA_DELETE_SERVICE.deletePlaylist(mediaId) ? Result.success() : Result.failure();
    }
}
