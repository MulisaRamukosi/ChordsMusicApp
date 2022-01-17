package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityPlayerBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.helpers.DurationHelper;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.RepeatMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends BaseActivity {

    private ActivityPlayerBinding mBinding;
    private BroadcastReceiver mMusicUpdatedReceiver;
    private SongInfoProgressEvent mSongInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init(){
        initSongDetails();

        //wait for music service to connect
        final ScheduledExecutorService stp = Executors.newScheduledThreadPool(1);
        stp.scheduleAtFixedRate(() -> runOnUiThread(() -> {
            if (getMusicPlayerService() != null){
                initPlayer();
                stp.shutdown();
            }
        }), 0, 100, TimeUnit.MILLISECONDS);

        mBinding.tvNotCorrect.setOnClickListener(view -> {
            final Intent i = new Intent(PlayerActivity.this, OverrideSongActivity.class);
            i.putExtra(Constants.KEY_SONG, mSongInfo.getCurrentSong());
            startActivity(i);
        });

        mBinding.ibCurrentPlaylist.setOnClickListener(view -> {
            final CurrentPlaylistBottomSheet currentPlaylistBottomSheet = new CurrentPlaylistBottomSheet();
            currentPlaylistBottomSheet.show(getSupportFragmentManager(), "");
        });
    }

    private void initSongDetails(){
        final Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            postponeEnterTransition();
            final SongInfoProgressEvent event = bundle.getParcelable(Constants.KEY_SONG);

            mBinding.tvSongName.setText(event.getCurrentSong().getTitle());
            mBinding.tvSongArtist.setText(event.getCurrentSong().getName());
            mBinding.sbSongProgress.setMax(event.getSongDurationInMilis());
            mBinding.sbSongProgress.setProgress(event.getCurrProgressInMilis());
            setPlayPauseButtonState(event.isPlaying());
            Glide.with(this).load(event.getCurrentAlbum().getCover_url()).into(mBinding.ivAlbum);
            scheduleStartPostponedTransition(mBinding.ivAlbum);
        }
    }

    private void initPlayer(){
        getMusicPlayerService().initPlayer(mBinding.ivPlayPause,
                mBinding.sbSongProgress,
                mBinding.ivNext,
                mBinding.ivPrev);
        initRepeatButton();
        initShuffleButton();

    }

    private void initShuffleButton(){
        setShuffleButtonState();
        mBinding.ivShuffle.setOnClickListener(v -> {
            final IMusicPlayerService playerService = getMusicPlayerService();
            playerService.setShuffleModeEnabled(!playerService.isShuffleModeEnabled());
            setShuffleButtonState();
        });
    }

    private void setShuffleButtonState(){
        if (getMusicPlayerService().isShuffleModeEnabled()){
            mBinding.ivShuffle.setColorFilter(ContextCompat.getColor(this, R.color.secondaryDarkColor),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else{
            mBinding.ivShuffle.clearColorFilter();
        }
    }

    private void initRepeatButton() {
        setRepeatButtonState();
        mBinding.ivRepeat.setOnClickListener(v -> {
            final RepeatMode repeatMode = getMusicPlayerService().getRepeatMode();
            if (repeatMode == RepeatMode.REPEAT_OFF) getMusicPlayerService().setRepeatMode(RepeatMode.REPEAT_LIST);
            else if (repeatMode == RepeatMode.REPEAT_LIST) getMusicPlayerService().setRepeatMode(RepeatMode.REPEAT_SONG);
            else getMusicPlayerService().setRepeatMode(RepeatMode.REPEAT_OFF);
            setRepeatButtonState();
        });
    }

    private void setRepeatButtonState(){
        final RepeatMode repeatMode = getMusicPlayerService().getRepeatMode();
        final Drawable icon = ContextCompat.getDrawable(this,
                repeatMode == RepeatMode.REPEAT_SONG
                        ? R.drawable.ic_round_repeat_one_24
                        : R.drawable.ic_round_repeat_24);
        mBinding.ivRepeat.setImageDrawable(icon);
        final boolean isRepeatModeOn = repeatMode != RepeatMode.REPEAT_OFF;

        if (isRepeatModeOn){
            mBinding.ivRepeat.setColorFilter(ContextCompat.getColor(this, R.color.secondaryDarkColor),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else{
            mBinding.ivRepeat.clearColorFilter();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMusicUpdatedReceiver == null){
            mMusicUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final SongInfoProgressEvent songInfo = intent.getParcelableExtra(Constants.KEY_MUSIC_PROGRESS);
                    if (songInfo == null) return;
                    if (mSongInfo == null || mSongInfo.getCurrentSong().getId() != songInfo.getCurrentSong().getId()){
                        initMusicState(songInfo);
                    }
                    else {
                        updateState(songInfo);
                    }
                }
            };
        }
        registerReceiver(mMusicUpdatedReceiver, new IntentFilter(Constants.ACTION_MUSIC_PROGRESS_UPDATE));
    }

    private void initMusicState(SongInfoProgressEvent songInfo) {
        final TrackArtistAlbumEntity track = songInfo.getCurrentSong();
        final AlbumArtistEntity album = songInfo.getCurrentAlbum();
        mSongInfo = songInfo;

        displayImageFromLink(album.getCover_url(), mBinding.ivAlbum, true);
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
        mMusicUpdatedReceiver = null;
    }

    private void setPlayPauseButtonState(boolean isPlaying) {
        mBinding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this,
                isPlaying ? R.drawable.ic_round_pause_24 : R.drawable.ic_round_play_arrow_24
        ));
    }

    private void updateState(SongInfoProgressEvent songInfo){
        if (mSongInfo.isPlaying() != songInfo.isPlaying()){
            setPlayPauseButtonState(songInfo.isPlaying());
        }
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
