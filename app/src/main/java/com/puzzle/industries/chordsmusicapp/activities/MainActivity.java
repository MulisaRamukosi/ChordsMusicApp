package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaActivity;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityMainBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseMediaActivity {

    private ActivityMainBinding mBinding;
    private NavHostFragment mNavHost;
    private BroadcastReceiver mMusicUpdatedReceiver;
    private SongInfoProgressEvent mSongInfo;
    private boolean mCurrentPlayingState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();

    }

    private void init(){
        initNavigation();
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            if (getMusicPlayerService() != null){
                initPlayer();
                executorService.shutdown();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void initPlayer() {
        mBinding.llPlayer.setOnClickListener(v -> {
            final Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra(Constants.KEY_SONG, mSongInfo);

            final Pair<View, String> songName = Pair.create(mBinding.tvSongName, getString(R.string.trans_song_name));
            final Pair<View, String> albumPic = Pair.create(mBinding.ivAlbum, getString(R.string.trans_album_pic));
            final Pair<View, String> artistName = Pair.create(mBinding.tvSongArtist, getString(R.string.trans_artist_name));
            final Pair<View, String> songProgress = Pair.create(mBinding.sbSongProgress, getString(R.string.trans_seekbar));
            final Pair<View, String> playPause = Pair.create(mBinding.ivPlayPause, getString(R.string.trans_play_pause));

            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, songName, albumPic, artistName, songProgress, playPause
            );

            startActivity(i, optionsCompat.toBundle());
        });

       getMusicPlayerService().initMiniPlayer(mBinding.ivPlayPause, mBinding.sbSongProgress);
    }

    private void initNavigation(){
        mNavHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(mBinding.navHostFragment.getId());
        if (mNavHost != null) {
            NavigationUI.setupWithNavController(mBinding.bottomNavigation, mNavHost.getNavController());
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
                    if (songInfo == null){
                        hideMediaPlayer();
                        return;
                    }
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

    private void hideMediaPlayer(){
        mSongInfo = null;
        mBinding.llPlayer.setVisibility(View.GONE);
    }

    private void initMusicState(SongInfoProgressEvent songInfo) {
        final TrackArtistAlbumEntity track = songInfo.getCurrentSong();
        final AlbumArtistEntity album = songInfo.getCurrentAlbum();
        mSongInfo = songInfo;

        Glide.with(this).load(album.getCover_url()).into(mBinding.ivAlbum);
        mBinding.tvSongName.setText(track.getTitle());
        mBinding.tvSongArtist.setText(track.getName());
        mBinding.sbSongProgress.setMax(songInfo.getSongDurationInMilis());
        mBinding.llPlayer.setVisibility(View.VISIBLE);
        setPlayPauseButtonState(mSongInfo.isPlaying());
    }

    private void setPlayPauseButtonState(boolean isPlaying){
        mCurrentPlayingState = isPlaying;
        mBinding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this,
                isPlaying ? R.drawable.ic_round_pause_24 : R.drawable.ic_round_play_arrow_24
        ));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mMusicUpdatedReceiver);
        mMusicUpdatedReceiver = null;
    }

    private void updateState(SongInfoProgressEvent songInfo){
        mSongInfo.setCurrProgressInMilis(songInfo.getCurrProgressInMilis());
        if (mCurrentPlayingState != songInfo.isPlaying()) {
            setPlayPauseButtonState(songInfo.isPlaying());
            mSongInfo.setPlaying(songInfo.isPlaying());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.sbSongProgress.setProgress(songInfo.getCurrProgressInMilis(), true);
        }
        else{
            mBinding.sbSongProgress.setProgress(songInfo.getCurrProgressInMilis());
        }
    }
}
