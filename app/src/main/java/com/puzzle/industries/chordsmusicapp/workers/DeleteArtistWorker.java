package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.base.BaseMediaDeleteWorker;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

import java.util.List;

public class DeleteArtistWorker extends BaseMediaDeleteWorker {

    public DeleteArtistWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return deleteArtist();
    }

    private Result deleteArtist() {
        return MEDIA_DELETE_SERVICE.deleteArtist(mediaId) ? Result.success() : Result.failure();
    }


}
