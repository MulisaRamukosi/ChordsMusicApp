package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

public class FetchArtistsWorker extends Worker {

    public FetchArtistsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        loadArtists();
        return Result.success();
    }

    private void loadArtists(){
        MusicLibraryService.getInstance().setArtistList(Chords.getDatabase().artistDao().getAllArtists());
    }
}
