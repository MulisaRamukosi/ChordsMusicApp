package com.puzzle.industries.chordsmusicapp.remote.deezer.api;

import com.puzzle.industries.chordsmusicapp.remote.BaseApiCallback;
import com.puzzle.industries.chordsmusicapp.remote.deezer.DeezerRemote;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumSongsDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerSongResultDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

public class DeezerApiCall {

    private static DeezerApiCall instance;
    private final DeezerApi deezerApi;

    private DeezerApiCall() {
        deezerApi = DeezerRemote.getClient(DeezerApi.class);
    }

    public static DeezerApiCall getInstance() {
        if (instance == null) {
            synchronized (DeezerApiCall.class) {
                if (instance == null) {
                    instance = new DeezerApiCall();
                }
            }
        }
        return instance;
    }

    public void searchTracks(String name, ApiCallBack<DeezerSongResultDataModel> callback) {
        deezerApi.getTrack(name, "json").enqueue(new BaseApiCallback<>(callback));
    }

    public void getTrackInfoById(int trackId, ApiCallBack<DeezerTrackDataModel> callBack) {
        deezerApi.getTrackInfoById(trackId).enqueue(new BaseApiCallback<>(callBack));
    }

    public void searchAlbums(String name, ApiCallBack<DeezerAlbumDataModel> callback) {
        deezerApi.getAlbum(name).enqueue(new BaseApiCallback<>(callback));
    }

    public void getAlbumSongsById(int albumId, ApiCallBack<DeezerAlbumSongsDataModel> callback) {
        deezerApi.getAlbumSongs(albumId).enqueue(new BaseApiCallback<>(callback));
    }
}
