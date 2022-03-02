package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistDao;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class UpdatePlaylistNameWorker extends Worker {

    private final int mPlaylistId;
    private final String mPlaylistName;
    private final PlaylistDao PLAYLIST_DAO = Chords.getDatabase().playlistDao();
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    public UpdatePlaylistNameWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        mPlaylistId = workerParams.getInputData().getInt(Constants.KEY_PLAYLIST_ID, -1);
        mPlaylistName = workerParams.getInputData().getString(Constants.KEY_PLAYLIST_NAME);
    }

    @NonNull
    @Override
    public Result doWork() {
        updatePlaylist();
        MUSIC_LIBRARY.addPlaylist(new PlaylistEntity(mPlaylistId, mPlaylistName));
        return Result.success();
    }

    private void updatePlaylist() {
        PLAYLIST_DAO.updateName(mPlaylistId, mPlaylistName);
    }
}
