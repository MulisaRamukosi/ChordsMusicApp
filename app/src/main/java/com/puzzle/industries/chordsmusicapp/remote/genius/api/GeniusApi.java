package com.puzzle.industries.chordsmusicapp.remote.genius.api;

import com.puzzle.industries.chordsmusicapp.remote.genius.models.AnnotationResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.ArtistResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SearchResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongResultModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeniusApi {

    @GET("search")
    Call<SearchResultModel> getSearchResults(@Query("q") String word);

    @GET("songs/{id}?text_format=plain")
    Call<SongResultModel> getSongInfo(@Path("id") String songId);

    @GET("annotations/{id}?text_format=plain")
    Call<AnnotationResultModel> getVerseExplanation(@Path("id") String annotationId);

    @GET("artists/{id}?text_format=plain")
    Call<ArtistResultModel> getArtistInfo(@Path("id") int artistId);
}
