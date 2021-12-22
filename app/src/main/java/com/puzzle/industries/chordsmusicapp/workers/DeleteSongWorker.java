package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.base.BaseMediaDeleteWorker;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

public class DeleteSongWorker extends BaseMediaDeleteWorker {

    public DeleteSongWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {
        return deleteSong();
    }

    private Result deleteSong() {
        return MEDIA_DELETE_SERVICE.deleteSong(mediaId) ? Result.success() : Result.failure();
    }
}
