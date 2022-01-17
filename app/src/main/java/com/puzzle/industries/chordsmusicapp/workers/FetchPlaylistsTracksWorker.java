package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;

public class FetchPlaylistsTracksWorker extends Worker {

    public FetchPlaylistsTracksWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        loadPlaylistTracks();
        return Result.success();
    }

    private void loadPlaylistTracks(){
        final List<PlaylistTrackEntity> playlistTracks = Chords.getDatabase().playlistTrackDao().getAll();
        MusicLibraryService.getInstance().addPlaylistsTracks(playlistTracks);
    }
}
