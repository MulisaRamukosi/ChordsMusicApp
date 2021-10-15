package com.puzzle.industries.chordsmusicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;
import com.puzzle.industries.chordsmusicapp.BaseActivity;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.databinding.ActivitySplashBinding;
import com.puzzle.industries.chordsmusicapp.workers.FetchAlbumsWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchArtistsWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchSongsWorker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

    }

    private void initMusicLibrary(){
        final WorkRequest songsRequest = new OneTimeWorkRequest.Builder(FetchSongsWorker.class).build();
        final WorkRequest artistsRequest = new OneTimeWorkRequest.Builder(FetchArtistsWorker.class).build();
        final WorkRequest albumRequest = new OneTimeWorkRequest.Builder(FetchAlbumsWorker.class).build();

        final ListenableFuture<Operation.State.SUCCESS> result = WorkManager.getInstance(this)
                .enqueue(Arrays.asList(songsRequest, artistsRequest, albumRequest)).getResult();

        result.addListener(() -> {
            if (result.isDone()){
                new Handler(getMainLooper()).postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 3000);
            }
        }, Executors.newSingleThreadExecutor());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPermissionsGranted(this)){
            initMusicLibrary();
        }
        else{
            requestPermissions(this);
        }
    }

}