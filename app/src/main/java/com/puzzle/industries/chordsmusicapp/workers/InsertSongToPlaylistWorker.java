package com.puzzle.industries.chordsmusicapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistTrackDao;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class InsertSongToPlaylistWorker extends Worker {

    private final int songId;
    private final int playlistId;
    private final PlaylistTrackDao PLAYLIST_TRACK_DAO = Chords.getDatabase().playlistTrackDao();
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    public InsertSongToPlaylistWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        songId = workerParams.getInputData().getInt(Constants.KEY_MEDIA_ID, -1);
        playlistId = workerParams.getInputData().getInt(Constants.KEY_PLAYLIST_ID, -1);
    }

    @NonNull
    @Override
    public Result doWork() {
        final PlaylistTrackEntity playlistTrack = addSongToPlaylist();
        final Data data = new Data.Builder().putInt(Constants.KEY_PLAYLIST_TRACK_ID, playlistTrack.getId()).build();
        return Result.success(data);
    }

    private PlaylistTrackEntity addSongToPlaylist() {
        final PlaylistTrackEntity playlistTrack = new PlaylistTrackEntity(0, playlistId, songId);
        final long playlistTrackId = PLAYLIST_TRACK_DAO.insert(playlistTrack);
        playlistTrack.setId((int) playlistTrackId);
        MUSIC_LIBRARY.addPlaylistTrack(playlistTrack);
        return playlistTrack;
    }
}
