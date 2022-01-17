package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;

public class FetchPlaylistsWorker extends Worker {

    public FetchPlaylistsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        loadPlaylists();
        return Result.success();
    }

    private void loadPlaylists(){
        final List<PlaylistEntity> playlists = Chords.getDatabase().playlistDao().getAll();
        MusicLibraryService.getInstance().setPlaylists(playlists);
    }
}
