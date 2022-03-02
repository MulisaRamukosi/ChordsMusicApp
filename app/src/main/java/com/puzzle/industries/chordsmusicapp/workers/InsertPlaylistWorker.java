package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistDao;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class InsertPlaylistWorker extends Worker {

    private final String playlistName;
    private final PlaylistDao PLAYLIST_DAO = Chords.getDatabase().playlistDao();
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    public InsertPlaylistWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        playlistName = workerParams.getInputData().getString(Constants.KEY_PLAYLIST_NAME);
    }

    @NonNull
    @Override
    public Result doWork() {
        final PlaylistEntity playlist = addPlaylist();
        final Data data = new Data.Builder().putInt(Constants.KEY_PLAYLIST_ID, playlist.getId()).build();
        return Result.success(data);
    }

    private PlaylistEntity addPlaylist() {
        final PlaylistEntity playlist = new PlaylistEntity(0, playlistName);
        final long playlistId = PLAYLIST_DAO.insert(playlist);
        playlist.setId((int) playlistId);
        MUSIC_LIBRARY.addPlaylist(playlist);
        return playlist;
    }

}
