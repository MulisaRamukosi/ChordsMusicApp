package com.puzzle.industries.chordsmusicapp.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.services.IMediaDeleteService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaDeleteService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public abstract class BaseMediaDeleteWorker extends Worker {

    protected final IMediaDeleteService MEDIA_DELETE_SERVICE = MediaDeleteService.getInstance();
    protected final int mediaId;

    public BaseMediaDeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mediaId = workerParams.getInputData().getInt(Constants.KEY_MEDIA_ID, -1);
    }
}
