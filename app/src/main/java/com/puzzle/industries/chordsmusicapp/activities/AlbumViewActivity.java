package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaActivity;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.MediaOptionsBottomSheet;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityAlbumViewBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.services.IMediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;

public class AlbumViewActivity extends BaseMediaActivity{

    private ActivityAlbumViewBinding mBinding;
    private MusicRVAdapter mAdapter;
    private MediaVM mMediaViewModel;
    private BroadcastReceiver mMusicAddedReceiver;
    private BroadcastReceiver mMusicRemovedReceiver;
    private BroadcastReceiver mMusicUpdatedReceiver;
    private List<TrackArtistAlbumEntity> mAlbumSongs;
    private AlbumArtistEntity mAlbum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAlbumViewBinding.inflate(getLayoutInflater());
        mMediaViewModel = new ViewModelProvider(this).get(MediaVM.class);
        setContentView(mBinding.getRoot());
        postponeEnterTransition();
        init();
        initReceivers();
    }

    private void init() {
        iniAlbumDetails();
        initObservables();
    }

    private void iniAlbumDetails() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAlbum = bundle.getParcelable(Constants.KEY_ALBUM);
            displayImageFromLink(mAlbum.getCover_url(), mBinding.ivAlbum, true);
            if (mAlbum.getCover_url() != null){
                displayImageFromLink(mAlbum.getCover_url(), mBinding.ivAlbum, true);
            }
            else{
                displayImageFromDrawable(R.drawable.bg_album, mBinding.ivAlbum, true);
            }
            mBinding.tvAlbumName.setText(mAlbum.getTitle());
            mBinding.tvAlbumArtist.setText(mAlbum.getName());

            initSongList(mAlbum.getId());
        }

        mBinding.lpi.setIndeterminate(false);
        mBinding.lpi.setVisibility(View.GONE);
    }

    private void initSongList(int albumId) {
        mAlbumSongs = MusicLibraryService.getInstance().getAlbumSongs(albumId);
        mAdapter = new MusicRVAdapter(mAlbumSongs);

        mAdapter.setItemLongClickCallback((t, songIds) -> {
            final Vibrator vibrator = (Vibrator) Chords.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(30);
            final MediaOptionsBottomSheet<TrackArtistAlbumEntity> mediaOptions = new MediaOptionsBottomSheet<>(t, songIds);
            mediaOptions.show(getSupportFragmentManager(), mediaOptions.getTag());
        });
        mBinding.rvSongList.setAdapter(mAdapter);
    }

    private void initObservables() {
        mMediaViewModel.getObservableSong().observe(this, song -> {
            if (mAdapter != null) {
                mAdapter.itemChanged(song);
            }
        });
    }

    private void initReceivers(){
        mMusicAddedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final TrackArtistAlbumEntity song = intent.getParcelableExtra(Constants.KEY_SONG);
                if (song.getAlbum_id() == mAlbum.getId()) {
                    mAdapter.itemAdded(song);
                }
            }
        };

        mMusicUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final SongInfoProgressEvent songInfo = intent.getParcelableExtra(Constants.KEY_MUSIC_PROGRESS);
                if (songInfo == null) return;
                mMediaViewModel.updateSong(songInfo.getCurrentSong());
            }
        };

        mMusicRemovedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final TrackArtistAlbumEntity song = intent.getParcelableExtra(Constants.KEY_SONG);
                if (song.getAlbum_id() == mAlbum.getId()) {
                    mAdapter.itemRemoved(song);
                    if (mAdapter.getItemCount() == 0){
                        MediaBroadCastService.getInstance().albumRemoved(mAlbum);
                        finish();
                    }
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mMusicAddedReceiver, new IntentFilter(Constants.ACTION_MUSIC_ADDED_TO_LIST));
        registerReceiver(mMusicRemovedReceiver, new IntentFilter(Constants.ACTION_MUSIC_DELETED));
        registerReceiver(mMusicUpdatedReceiver, new IntentFilter(Constants.ACTION_MUSIC_PROGRESS_UPDATE));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mMusicAddedReceiver);
        unregisterReceiver(mMusicUpdatedReceiver);
        unregisterReceiver(mMusicRemovedReceiver);
    }


}
