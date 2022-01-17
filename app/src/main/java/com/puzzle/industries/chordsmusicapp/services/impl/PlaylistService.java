package com.puzzle.industries.chordsmusicapp.services.impl;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.callbacks.PlaylistCallback;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistTrackDao;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.IPlaylistService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.workers.InsertPlaylistWorker;
import com.puzzle.industries.chordsmusicapp.workers.InsertSongToPlaylistWorker;
import com.puzzle.industries.chordsmusicapp.workers.UpdatePlaylistNameWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Setter;

public class PlaylistService implements IPlaylistService {

    @Setter private PlaylistCallback playlistCallback;
    @Setter private LifecycleOwner lifecycleOwner;
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    private static PlaylistService instance;
    public static PlaylistService getInstance(LifecycleOwner lifecycleOwner, PlaylistCallback playlistCallback) {
        if (instance == null){
            synchronized (PlaylistService.class){
                if (instance == null){
                    instance = new PlaylistService(lifecycleOwner, playlistCallback);
                }
            }
        }
        else{
            instance.setLifecycleOwner(lifecycleOwner);
            instance.setPlaylistCallback(playlistCallback);
        }
        return instance;
    }



    private PlaylistService(LifecycleOwner lifecycleOwner, PlaylistCallback playlistCallback){
        this.playlistCallback = playlistCallback;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void addPlaylist(PlaylistEntity playlist) {
        final Context ctx = Chords.getAppContext();
        final WorkRequest createPlaylistRequest = new OneTimeWorkRequest.Builder(InsertPlaylistWorker.class)
                .setInputData(new Data.Builder().putString(Constants.KEY_PLAYLIST_NAME, playlist.getName()).build())
                .build();

        WorkManager.getInstance(ctx).enqueue(createPlaylistRequest);

        WorkManager.getInstance(ctx).getWorkInfoByIdLiveData(createPlaylistRequest.getId())
               .observe(this.lifecycleOwner, workInfo -> {
                   if (workInfo.getState() == WorkInfo.State.SUCCEEDED){
                       int playlistId = workInfo.getOutputData().getInt(Constants.KEY_PLAYLIST_ID, -1);
                       playlist.setId(playlistId);
                       this.playlistCallback.playlistCreated(playlist);
                   }
               });
    }

    @Override
    public void addSongsToPlaylist(int playlistId, List<PlaylistTrackEntity> playlistTracks) {
        enqueueSongsAdditionToPlaylist(playlistId, playlistTracks.stream()
                .mapToInt(PlaylistTrackEntity::getTrackId).boxed().collect(Collectors.toList()));
    }

    @Override
    public void addSongToPlaylist(int playlistId, int songId) {
        enqueueSongsAdditionToPlaylist(playlistId, new ArrayList<>(Collections.singletonList(songId)));
    }

    @Override
    public void updatePlaylistName(int playlistId, String playlistNewName) {
        final Context ctx = Chords.getAppContext();
        final WorkRequest updatePlaylistRequest = new OneTimeWorkRequest.Builder(UpdatePlaylistNameWorker.class)
                .setInputData(
                        new Data.Builder()
                        .putInt(Constants.KEY_PLAYLIST_ID, playlistId)
                        .putString(Constants.KEY_PLAYLIST_NAME, playlistNewName)
                        .build()
                ).build();

        WorkManager.getInstance(ctx).enqueue(updatePlaylistRequest);
        WorkManager.getInstance(ctx).getWorkInfoByIdLiveData(updatePlaylistRequest.getId())
                .observe(this.lifecycleOwner, workInfo -> {
                    if (workInfo.getState() == WorkInfo.State.SUCCEEDED){
                        playlistCallback.playlistUpdated();
                    }
                });
    }

    @Override
    public void removeSongsFromPlaylist(int playlistId, Set<PlaylistTrackEntity> mRemovedSongs) {
        mRemovedSongs = mRemovedSongs.stream().filter(playlistTrackEntity ->
                MUSIC_LIBRARY.containsPlaylistTrack(playlistId, playlistTrackEntity))
                .collect(Collectors.toSet());
        if (!MediaDeleteService.getInstance().deletePlaylistTracks(new ArrayList<>(mRemovedSongs))){
            this.playlistCallback.operationFailed();
        }
    }

    private void enqueueSongsAdditionToPlaylist(int playlistId, List<Integer> songIds){
        if (songIds.isEmpty()){
            this.playlistCallback.songsAddedToPlaylist();
            return;
        }
        final List<WorkRequest> addSongsRequest = new ArrayList<>();
        for (final Integer songId : songIds){
            addSongsRequest.add(
                    new OneTimeWorkRequest.Builder(InsertSongToPlaylistWorker.class)
                            .setInputData(new Data.Builder()
                                    .putInt(Constants.KEY_PLAYLIST_ID, playlistId)
                                    .putInt(Constants.KEY_MEDIA_ID, songId)
                                    .build()
                            ).build()
            );
        }

        final ListenableFuture<Operation.State.SUCCESS> request = WorkManager
                .getInstance(Chords.getAppContext())
                .enqueue(addSongsRequest).getResult();

        request.addListener(() -> {
            if (request.isDone()){
                this.playlistCallback.songsAddedToPlaylist();
            }
        }, Executors.newSingleThreadExecutor());
    }


}
