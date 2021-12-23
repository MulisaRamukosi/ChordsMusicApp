package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;
import java.util.stream.Collectors;

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
        final List<TrackArtistAlbumEntity> tracks = Chords.getDatabase().trackDao().getAllTracks();
        MusicLibraryService.getInstance().setMusicPlaylist(tracks.stream().mapToInt(TrackArtistAlbumEntity::getId).boxed().collect(Collectors.toList()));
        MusicLibraryService.getInstance().setMusicList(tracks);
    }
}
