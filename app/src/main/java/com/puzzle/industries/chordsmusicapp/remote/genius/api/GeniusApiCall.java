package com.puzzle.industries.chordsmusicapp.remote.genius.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.puzzle.industries.chordsmusicapp.activities.PlayerActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.remote.BaseApiCallback;
import com.puzzle.industries.chordsmusicapp.remote.genius.GeniusRemote;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.AnnotationResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.ArtistResponseModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.ArtistResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.HitModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SearchResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongResultModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeniusApiCall {

    private static GeniusApiCall instance;
    private final GeniusApi geniusApi;

    private GeniusApiCall() {
        geniusApi = GeniusRemote.getClient(GeniusApi.class);
    }

    public static GeniusApiCall getInstance() {
        if (instance == null) {
            synchronized (GeniusApiCall.class) {
                if (instance == null) {
                    instance = new GeniusApiCall();
                }
            }
        }
        return instance;
    }

    public AnnotationResultModel getVerseExplanation(String annotationId){
        try {
            return geniusApi.getVerseExplanation(annotationId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getArtistInfo(int artistId, ApiCallBack<ArtistResultModel> callBack){
        geniusApi.getArtistInfo(artistId).enqueue(new BaseApiCallback<>(callBack));
    }

    public void getSongInfoById(String songId, ApiCallBack<SongResultModel> callBack){
        geniusApi.getSongInfo(songId).enqueue(new BaseApiCallback<>(callBack));
    }

    public void getSongInfo(TrackArtistAlbumEntity track, ApiCallBack<SongInfoStruct> callBack) {
        final ArtistEntity artist = MusicLibraryService.getInstance().getArtistById(track.getArtist_id());
        final String word = String.format("%s %s", artist.getName(), track.getTitle());
        geniusApi.getSearchResults(word).enqueue(new Callback<SearchResultModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchResultModel> call, @NonNull Response<SearchResultModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final SearchResultModel searchResult = response.body();
                    getSongInfo(searchResult, callBack);
                } else {
                    callBack.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResultModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                callBack.onFailure(t);
            }
        });
    }

    private void getSongInfo(SearchResultModel searchResult, ApiCallBack<SongInfoStruct> callBack) {
        if (!searchResult.getResponse().getHits().isEmpty()) {
            final HitModel hitModel = searchResult.getResponse().getHits().get(0).getResult();
            geniusApi.getSongInfo(hitModel.getSongId()).enqueue(new Callback<SongResultModel>() {
                @Override
                public void onResponse(@NonNull Call<SongResultModel> call, @NonNull Response<SongResultModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        final SongInfoStruct songInfo = new SongInfoStruct(response.body(), searchResult);
                        callBack.onSuccess(songInfo);
                    } else {
                        callBack.onFailure(new Throwable("Failed to get results"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SongResultModel> call, @NonNull Throwable t) {
                    callBack.onFailure(new Throwable("Failed to get results"));
                }
            });
        } else {
            callBack.onFailure(new Throwable("Failed to get results"));
        }
    }
}
