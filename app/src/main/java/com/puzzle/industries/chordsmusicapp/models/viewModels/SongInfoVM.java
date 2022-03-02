package com.puzzle.industries.chordsmusicapp.models.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackInfoEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.services.impl.ExecutorServiceManager;
import com.puzzle.industries.chordsmusicapp.services.impl.SongInfoService;

import java.util.concurrent.Executors;

public class SongInfoVM extends ViewModel {

    private MutableLiveData<SongInfoStruct> songInfo;

    public LiveData<SongInfoStruct> getSongInfo(TrackArtistAlbumEntity track){
        if (songInfo == null) songInfo = new MutableLiveData<>();
        fetchSongInfo(track);
        return songInfo;
    }

    public void setSongInfo(SongInfoStruct songInfo){
        this.songInfo.postValue(songInfo);
    }

    private void fetchSongInfo(TrackArtistAlbumEntity track) {
        ExecutorServiceManager.getInstance().executeRunnableOnSingeThread(
                () -> {
                    final TrackInfoEntity trackInfo = Chords.getDatabase().trackInfoDao().getTrackInfoById(track.getId());
                    if (trackInfo != null){
                        final SongInfoStruct songInfoStruct = new Gson().fromJson(trackInfo.getInfo(), SongInfoStruct.class);
                        songInfo.postValue(songInfoStruct);
                    }
                    else{
                        downloadTrackInfo(track);
                    }
                }
        );
    }

    private void downloadTrackInfo(TrackArtistAlbumEntity track) {
        SongInfoService.getInstance().getSongInfo(track, new ApiCallBack<SongInfoStruct>() {
            @Override
            public void onSuccess(SongInfoStruct songInfoStruct) {
                songInfo.postValue(songInfoStruct);
                saveSongInfo(track, songInfoStruct);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                songInfo.postValue(null);
            }
        });
    }

    private void saveSongInfo(TrackArtistAlbumEntity track, SongInfoStruct songInfoStruct){
        ExecutorServiceManager.getInstance()
                .executeRunnableOnSingeThread(
                        () -> {
                            final String songInfo = new Gson().toJson(songInfoStruct);
                            Chords.getDatabase().trackInfoDao().insertTrackInfo(new TrackInfoEntity(track.getId(), songInfo));
                        }
                );
    }

}
