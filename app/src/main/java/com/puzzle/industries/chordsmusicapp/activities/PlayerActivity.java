package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaActivity;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.CurrentPlaylistBottomSheet;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityPlayerBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.helpers.ArtHelper;
import com.puzzle.industries.chordsmusicapp.helpers.DurationHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.models.viewModels.SongInfoVM;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.impl.SongInfoService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.RepeatMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends BaseMediaActivity {

    private ActivityPlayerBinding mBinding;
    private SongInfoVM mSongInfoVM;
    private BroadcastReceiver mMusicUpdatedReceiver;
    private SongInfoProgressEvent mSongInfo;
    private BroadcastReceiver songInfoChangedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        mSongInfoVM = new ViewModelProvider(this).get(SongInfoVM.class);
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        initSongDetails();
        initPlayerControls();
        registerReceivers();
    }

    private void registerReceivers() {
        if (songInfoChangedReceiver == null){
            songInfoChangedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final SongInfoStruct songInfo = intent.getParcelableExtra(Constants.KEY_SONG_INFO);
                    if (songInfo != null){
                        mSongInfoVM.setSongInfo(songInfo);
                    }
                }
            };
        }

        registerReceiver(songInfoChangedReceiver, new IntentFilter(Constants.ACTION_SONG_INFO_STRUCT_CHANGED));
    }

    private void initPlayerControls(){
        //wait for music service to connect
        final ScheduledExecutorService stp = Executors.newScheduledThreadPool(1);
        stp.scheduleAtFixedRate(() -> runOnUiThread(() -> {
            if (getMusicPlayerService() != null) {
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

    private void initSongDetails() {
        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            postponeEnterTransition();
            final SongInfoProgressEvent event = bundle.getParcelable(Constants.KEY_SONG);
            final TrackArtistAlbumEntity track = event.getCurrentSong();

            mBinding.tvSongName.setText(track.getTitle());
            mBinding.tvSongArtist.setText(track.getName());
            mBinding.sbSongProgress.setMax(event.getSongDurationInMilis());
            mBinding.sbSongProgress.setProgress(event.getCurrProgressInMilis());
            setPlayPauseButtonState(event.isPlaying());
            ArtHelper.displayAlbumArtFromUrl(event.getCurrentAlbum().getCover_url(), mBinding.ivAlbum);
            scheduleStartPostponedTransition(mBinding.ivAlbum);
        }
    }

    private void initSongInfo(TrackArtistAlbumEntity track) {
        setSongInfoAsLoading(true);
        mSongInfoVM.getSongInfo(track).observe(this, songInfo -> {
            setSongInfoAsLoading(false);
            if (songInfo != null){
                mBinding.ibSongInfo.setOnClickListener(v -> attemptToStartSongInfoActivity(songInfo));
            }
            else{
                mBinding.ibSongInfo.setVisibility(View.GONE);
            }
        });
    }

    private void attemptToStartSongInfoActivity(SongInfoStruct songInfoStruct){
        setSongInfoAsLoading(true);
        Glide.with(PlayerActivity.this).load(songInfoStruct.getSearchResult()
                .getResponse()
                .getHits()
                .get(0)
                .getResult()
                .getSong_art_image_url())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startSongInfoActivity(songInfoStruct);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startSongInfoActivity(songInfoStruct);
                        return false;
                    }
                })
                .submit();
    }


    private void startSongInfoActivity(SongInfoStruct songInfoStruct){
        runOnUiThread(() -> {
            setSongInfoAsLoading(false);
            final Intent intent = new Intent(PlayerActivity.this, SongInfoActivity.class);
            intent.putExtra(Constants.KEY_SONG_INFO, songInfoStruct);
            intent.putExtra(Constants.KEY_SONG, mSongInfo.getCurrentSong());
            startActivity(intent);
        });
    }

    private void setSongInfoAsLoading(boolean isLoading){
        runOnUiThread(() -> {
            mBinding.cpi.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            mBinding.ibSongInfo.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

    }

    private void initPlayer() {
        getMusicPlayerService().initPlayer(mBinding.ivPlayPause,
                mBinding.sbSongProgress,
                mBinding.ivNext,
                mBinding.ivPrev);
        initRepeatButton();
        initShuffleButton();

    }

    private void initShuffleButton() {
        setShuffleButtonState();
        mBinding.ivShuffle.setOnClickListener(v -> {
            final IMusicPlayerService playerService = getMusicPlayerService();
            playerService.setShuffleModeEnabled(!playerService.isShuffleModeEnabled());
            setShuffleButtonState();
        });
    }

    private void setShuffleButtonState() {
        if (getMusicPlayerService().isShuffleModeEnabled()) {
            mBinding.ivShuffle.setColorFilter(ContextCompat.getColor(this, R.color.secondaryDarkColor),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            mBinding.ivShuffle.clearColorFilter();
        }
    }

    private void initRepeatButton() {
        setRepeatButtonState();
        mBinding.ivRepeat.setOnClickListener(v -> {
            final RepeatMode repeatMode = getMusicPlayerService().getRepeatMode();
            if (repeatMode == RepeatMode.REPEAT_OFF)
                getMusicPlayerService().setRepeatMode(RepeatMode.REPEAT_LIST);
            else if (repeatMode == RepeatMode.REPEAT_LIST)
                getMusicPlayerService().setRepeatMode(RepeatMode.REPEAT_SONG);
            else getMusicPlayerService().setRepeatMode(RepeatMode.REPEAT_OFF);
            setRepeatButtonState();
        });
    }

    private void setRepeatButtonState() {
        final RepeatMode repeatMode = getMusicPlayerService().getRepeatMode();
        final Drawable icon = ContextCompat.getDrawable(this,
                repeatMode == RepeatMode.REPEAT_SONG
                        ? R.drawable.ic_round_repeat_one_24
                        : R.drawable.ic_round_repeat_24);
        mBinding.ivRepeat.setImageDrawable(icon);
        final boolean isRepeatModeOn = repeatMode != RepeatMode.REPEAT_OFF;

        if (isRepeatModeOn) {
            mBinding.ivRepeat.setColorFilter(ContextCompat.getColor(this, R.color.secondaryDarkColor),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            mBinding.ivRepeat.clearColorFilter();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMusicUpdatedReceiver == null) {
            mMusicUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final SongInfoProgressEvent songInfo = intent.getParcelableExtra(Constants.KEY_MUSIC_PROGRESS);
                    if (songInfo == null) return;
                    if (mSongInfo == null || mSongInfo.getCurrentSong().getId() != songInfo.getCurrentSong().getId()) {
                        initMusicState(songInfo);
                    } else {
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

        displayImageFromLink(album.getCover_url(), mBinding.ivAlbum, R.drawable.bg_album, true);
        mBinding.tvSongName.setText(track.getTitle());
        mBinding.tvSongArtist.setText(track.getName());
        mBinding.sbSongProgress.setMax(songInfo.getSongDurationInMilis());
        mBinding.tvSongDuration.setText(DurationHelper.minutesSecondsToString(mSongInfo.getSongDurationInMilis()));
        mBinding.tvCurrProgress.setText(DurationHelper.minutesSecondsToString(mSongInfo.getCurrProgressInMilis()));

        setPlayPauseButtonState(mSongInfo.isPlaying());
        initSongInfo(track);
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

    private void updateState(SongInfoProgressEvent songInfo) {
        if (mSongInfo.isPlaying() != songInfo.isPlaying()) {
            setPlayPauseButtonState(songInfo.isPlaying());
        }
        mSongInfo.setPlaying(songInfo.isPlaying());
        mBinding.tvCurrProgress.setText(DurationHelper.minutesSecondsToString(songInfo.getCurrProgressInMilis()));
        setSongProgress(songInfo.getCurrProgressInMilis());
    }

    private void setSongProgress(int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.sbSongProgress.setProgress(progress, true);
        } else {
            mBinding.sbSongProgress.setProgress(progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.songInfoChangedReceiver);
    }
}
