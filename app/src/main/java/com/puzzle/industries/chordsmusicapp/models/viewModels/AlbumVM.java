package com.puzzle.industries.chordsmusicapp.models.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.remote.deezer.api.DeezerApiCall;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

import java.util.List;

public class AlbumVM extends ViewModel {

    private final DeezerApiCall DEEZER_API = DeezerApiCall.getInstance();

    private MutableLiveData<DeezerAlbumDataModel> mAlbum;
    private MutableLiveData<List<AlbumArtistEntity>> mLocalAlbums;

    public LiveData<DeezerAlbumDataModel> getAlbumResultObservable(){
        if (mAlbum == null){
            mAlbum = new MutableLiveData<>();
        }

        return mAlbum;
    }

    public void searchAlbumByName(String name){
        DEEZER_API.searchAlbums(name, new ApiCallBack<DeezerAlbumDataModel>() {
            @Override
            public void onSuccess(DeezerAlbumDataModel deezerAlbumDataModel) {
                mAlbum.postValue(deezerAlbumDataModel);
            }

            @Override
            public void onFailure(Throwable t) {
                mAlbum.postValue(null);
            }
        });

    }

}
