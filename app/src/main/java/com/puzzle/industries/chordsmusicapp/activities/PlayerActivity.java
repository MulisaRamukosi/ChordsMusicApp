package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.BaseActivity;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityPlayerBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.helpers.DurationHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class PlayerActivity extends BaseActivity {

    private ActivityPlayerBinding mBinding;
    private BroadcastReceiver mMusicUpdatedReceiver;
    private SongInfoProgressEvent mSongInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initPlayer();
    }

    private void initPlayer(){
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            final SongInfoProgressEvent event = bundle.getParcelable(Constants.KEY_SONG);

            mBinding.tvSongName.setText(event.getCurrentSong().getTitle());
            mBinding.tvSongArtist.setText(event.getCurrentSong().getName());
            mBinding.sbSongProgress.setMax(event.getSongDurationInMilis());
            mBinding.sbSongProgress.setProgress(event.getCurrProgressInMilis());
            setPlayPauseButtonState(event.isPlaying());
            Glide.with(this).load(event.getCurrentAlbum().getCover_url()).into(mBinding.ivAlbum);
        }

        mBinding.ivPlayPause.setOnClickListener(v -> {
            boolean isPlaying = mSongInfo != null && mSongInfo.isPlaying();
            setPlayPauseButtonState(!isPlaying);
            mMusicPlayerService.playOrPause(mSongInfo.getCurrentSong().getId());
        });
    }



    @Override
    public void onResume() {
        super.onResume();

        if (mMusicUpdatedReceiver == null){
            mMusicUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final SongInfoProgressEvent songInfo = intent.getParcelableExtra(Constants.KEY_MUSIC_PROGRESS);
                    if (mSongInfo == null || mSongInfo.getCurrentSong().getId() != songInfo.getCurrentSong().getId()){
                        initMusicState(songInfo);
                    }
                    else {
                        updateState(songInfo);
                    }
                }
            };
        }
        registerReceiver(mMusicUpdatedReceiver, new IntentFilter(Constants.KEY_MUSIC_UPDATE));
    }

    private void initMusicState(SongInfoProgressEvent songInfo) {
        final TrackArtistAlbumEntity track = songInfo.getCurrentSong();
        final AlbumArtistEntity album = songInfo.getCurrentAlbum();
        mSongInfo = songInfo;

        Glide.with(this).load(album.getCover_url()).into(mBinding.ivAlbum);
        mBinding.tvSongName.setText(track.getTitle());
        mBinding.tvSongArtist.setText(track.getName());
        mBinding.sbSongProgress.setMax(songInfo.getSongDurationInMilis());
        mBinding.tvSongDuration.setText(DurationHelper.minutesSecondsToString(mSongInfo.getSongDurationInMilis()));
        mBinding.tvCurrProgress.setText(DurationHelper.minutesSecondsToString(mSongInfo.getCurrProgressInMilis()));

        setPlayPauseButtonState(mSongInfo.isPlaying());
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mMusicUpdatedReceiver);
    }

    private void setPlayPauseButtonState(boolean isPlaying) {
        mBinding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this,
                isPlaying ? R.drawable.ic_round_pause_24 : R.drawable.ic_round_play_arrow_24
        ));
    }

    private void updateState(SongInfoProgressEvent songInfo){
        mSongInfo.setPlaying(songInfo.isPlaying());
        mBinding.tvCurrProgress.setText(DurationHelper.minutesSecondsToString(songInfo.getCurrProgressInMilis()));
        setSongProgress(songInfo.getCurrProgressInMilis());
    }

    private void setSongProgress(int progress){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.sbSongProgress.setProgress(progress, true);
        }
        else{
            mBinding.sbSongProgress.setProgress(progress);
        }
    }
}
