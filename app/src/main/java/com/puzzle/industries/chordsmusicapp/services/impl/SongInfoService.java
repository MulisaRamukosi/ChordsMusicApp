package com.puzzle.industries.chordsmusicapp.services.impl;

import android.util.Log;

import com.puzzle.industries.chordsmusicapp.activities.PlayerActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.remote.genius.api.GeniusApiCall;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.services.ISongInfoService;

import java.util.HashMap;
import java.util.Map;

public class SongInfoService implements ISongInfoService {

    private static SongInfoService instance;
    private final Map<Integer, SongInfoStruct> SONG_INFOS = new HashMap<>();

    public static SongInfoService getInstance() {
        if (instance == null) {
            synchronized (SongInfoService.class) {
                if (instance == null) {
                    instance = new SongInfoService();
                }
            }
        }
        return instance;
    }

    @Override
    public void getSongInfo(TrackArtistAlbumEntity track, ApiCallBack<SongInfoStruct> callback) {
        if (SONG_INFOS.containsKey(track.getId())) {
            callback.onSuccess(SONG_INFOS.get(track.getId()));
        } else {
            GeniusApiCall.getInstance().getSongInfo(track, new ApiCallBack<SongInfoStruct>() {
                @Override
                public void onSuccess(SongInfoStruct songInfoStruct) {
                    SONG_INFOS.put(track.getId(), songInfoStruct);
                    callback.onSuccess(songInfoStruct);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    callback.onFailure(t);
                }
            });
        }
    }
}
