package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.base.BaseMediaDeleteWorker;

public class DeleteAlbumWorker extends BaseMediaDeleteWorker {

    public DeleteAlbumWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return deleteAlbum();
    }

    private Result deleteAlbum() {
        return MEDIA_DELETE_SERVICE.deleteAlbum(mediaId) ? Result.success() : Result.failure();
    }
}
