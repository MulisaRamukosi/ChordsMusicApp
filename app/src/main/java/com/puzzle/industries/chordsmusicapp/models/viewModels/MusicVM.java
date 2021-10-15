package com.puzzle.industries.chordsmusicapp.models.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.api.DeezerApiCall;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

import java.util.List;

public class MusicVM extends ViewModel {

    private final DeezerApiCall DEEZER_API = DeezerApiCall.getInstance();

    private MutableLiveData<DeezerTrackDataModel> mMusic;
    private MutableLiveData<List<TrackArtistAlbumEntity>> mLocalMusic;

    public LiveData<DeezerTrackDataModel> getMusicResultObservable(){
        if (mMusic == null){
            mMusic = new MutableLiveData<>();
        }

        return mMusic;
    }

    public void searchMusicByName(String name){
        DEEZER_API.searchTracks(name, new ApiCallBack<DeezerTrackDataModel>() {
            @Override
            public void onSuccess(DeezerTrackDataModel deezerTrackDataModel) {
                mMusic.postValue(deezerTrackDataModel);
            }

            @Override
            public void onFailure(Throwable t) {
                mMusic.postValue(null);
            }
        });
    }

}
