package com.puzzle.industries.chordsmusicapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.airbnb.lottie.LottieDrawable;
import com.google.common.util.concurrent.ListenableFuture;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivitySplashBinding;
import com.puzzle.industries.chordsmusicapp.workers.FetchAlbumsWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchArtistsWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchPlaylistsTracksWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchPlaylistsWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchSongsWorker;

import java.util.Arrays;
import java.util.concurrent.Executors;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setupLoadingAnimation();
    }

    private boolean isInDarkMode() {
        return (Resources.getSystem().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    private void setupLoadingAnimation() {
        mBinding.animation.setAnimation(isInDarkMode() ? R.raw.anim_music_dark : R.raw.anim_music_light);
        mBinding.animation.setRepeatCount(LottieDrawable.INFINITE);
        mBinding.animation.playAnimation();
    }

    private void initMusicLibrary() {
        final WorkRequest songsRequest = new OneTimeWorkRequest.Builder(FetchSongsWorker.class).build();
        final WorkRequest artistsRequest = new OneTimeWorkRequest.Builder(FetchArtistsWorker.class).build();
        final WorkRequest albumRequest = new OneTimeWorkRequest.Builder(FetchAlbumsWorker.class).build();
        final WorkRequest playlistRequest = new OneTimeWorkRequest.Builder(FetchPlaylistsWorker.class).build();
        final WorkRequest playlistTracksRequest = new OneTimeWorkRequest.Builder(FetchPlaylistsTracksWorker.class).build();

        final ListenableFuture<Operation.State.SUCCESS> result = WorkManager.getInstance(this)
                .enqueue(Arrays.asList(songsRequest, artistsRequest, albumRequest, playlistRequest, playlistTracksRequest))
                .getResult();

        result.addListener(() -> {
            if (result.isDone()) {
                Chords.applicationHandler.postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 3000);
            }
        }, Executors.newSingleThreadExecutor());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPermissionsGranted(this)) {
            initMusicLibrary();
        } else {
            requestPermissions(this);
        }
    }

}