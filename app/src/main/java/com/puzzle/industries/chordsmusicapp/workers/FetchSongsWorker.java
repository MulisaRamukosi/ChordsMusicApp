package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

public class FetchSongsWorker extends Worker {

    public FetchSongsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        loadSongs();
        return Result.success();
    }

    private void loadSongs(){
        MusicLibraryService.getInstance().setMusicList(Chords.getDatabase().trackDao().getAllTracks());
    }
}
