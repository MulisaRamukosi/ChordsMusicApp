package com.puzzle.industries.chordsmusicapp.remote.deezer.api;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumSongsDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerArtistsDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerSongResultDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeezerApi {

    @GET("search/album")
    Call<DeezerAlbumDataModel> getAlbum(@Query("q") String album);

    @GET("search/artist")
    Call<DeezerArtistsDataModel> getArtist(@Query("q") String artist);

    @GET("search/track")
    Call<DeezerSongResultDataModel> getTrack(@Query("q") String track, @Query("output") String output);

    @GET("track/{trackId}")
    Call<DeezerTrackDataModel> getTrackInfoById(@Path("trackId") int trackId);

    @GET("album/{album_id}")
    Call<DeezerAlbumSongsDataModel> getAlbumSongs(@Path("album_id") int album_id);

}
