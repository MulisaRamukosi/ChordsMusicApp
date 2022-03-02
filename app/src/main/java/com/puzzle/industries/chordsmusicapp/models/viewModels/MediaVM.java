package com.puzzle.industries.chordsmusicapp.models.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

public class MediaVM extends ViewModel {


    private MutableLiveData<ArtistEntity> mArtist;
    private MutableLiveData<AlbumArtistEntity> mAlbum;
    private MutableLiveData<TrackArtistAlbumEntity> mTrack;

    public LiveData<ArtistEntity> getObservableArtist() {
        if (mArtist == null) {
            mArtist = new MutableLiveData<>();
        }
        return mArtist;
    }

    public LiveData<TrackArtistAlbumEntity> getObservableSong() {
        if (mTrack == null) {
            mTrack = new MutableLiveData<>();
        }
        return mTrack;
    }

    public LiveData<AlbumArtistEntity> getObservableAlbum() {
        if (mAlbum == null) {
            mAlbum = new MutableLiveData<>();
        }

        return mAlbum;
    }

    public void updateSong(TrackArtistAlbumEntity track) {
        if (mTrack == null) mTrack = new MutableLiveData<>();
        if (mTrack.getValue() == null || mTrack.getValue().getId() != track.getId()) {
            mTrack.setValue(track);
        }
    }

    public void updateArtist(ArtistEntity artistEntity) {
        if (mArtist == null) mArtist = new MutableLiveData<>();
        if (mArtist.getValue() == null || mArtist.getValue().getId() != artistEntity.getId()) {
            mArtist.setValue(artistEntity);
        }
    }

    public void updateAlbum(AlbumArtistEntity album) {
        if (mAlbum == null) mAlbum = new MutableLiveData<>();
        if (mAlbum.getValue() == null || mAlbum.getValue().getId() != album.getId()) {
            mAlbum.setValue(album);
        }
    }
}
