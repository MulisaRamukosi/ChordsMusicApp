package com.puzzle.industries.chordsmusicapp.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.callbacks.OverrideDownloadProgressCallback;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityOverrideSongBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.helpers.SongFileNameHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaFileManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OverrideSongActivity extends BaseActivity implements OverrideDownloadProgressCallback{

    private ActivityOverrideSongBinding mBinding;
    private boolean mIsActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityOverrideSongBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init(){

        final Bundle extras = getIntent().getExtras();
        if (extras != null){
            final TrackArtistAlbumEntity track = extras.getParcelable(Constants.KEY_SONG);

            new MusicFinderApi.MusicFinderApiBuilder()
                    .setAsOverride(true)
                    .setWebView(mBinding.wv)
                    .setApiCallBack(new ApiCallBack<String>() {
                        @Override
                        public void onSuccess(String url) {
                            downloadSong(track, url);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showAlert(getString(R.string.error_url), true, getString(R.string.okay), null);
                        }
                    }).build();
        }
    }

    private void downloadSong(TrackArtistAlbumEntity track, String url) {
        final ArtistEntity artist = MusicLibraryService.getInstance().getArtistById(track.getArtist_id());
        Toast.makeText(OverrideSongActivity.this, "Downloading, please wait", Toast.LENGTH_SHORT).show();
        mBinding.tvOverride.setText(String.format("Overriding %s by %s, please wait", track.getTitle(), artist.getName()));
        mBinding.wv.setVisibility(View.GONE);
        final String fileName = track.getFileName();
        if(MediaFileManagerService.getInstance().deleteFile(fileName) || !MediaFileManagerService.getInstance().fileExists(fileName)){
            Executors.newSingleThreadExecutor().execute(() -> new DownloadService().downloadSong(fileName, url, this));
        }
    }

    @Override
    public void updateProgress(int currentProgress) {
        runOnUiThread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding.lpi.setProgress(currentProgress, true);
            }
            else{
                mBinding.lpi.setProgress(currentProgress);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActive = false;
    }

    @Override
    public void downloadComplete() {
        if (!mIsActive) return;
        runOnUiThread(() -> {
            showAlert("Successfully downloaded song", false, getString(R.string.okay), view -> finish());
        });
    }

    @Override
    public void downloadFailed() {
        if (!mIsActive) return;
        runOnUiThread(() -> {
            mBinding.wv.setVisibility(View.VISIBLE);
            showAlert("Failed to download song, the web view will reappear, please try to download the song",
                    true, getString(R.string.okay), null);
        });

    }
}
