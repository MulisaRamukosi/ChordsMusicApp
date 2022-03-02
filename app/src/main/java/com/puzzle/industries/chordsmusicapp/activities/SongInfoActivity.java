package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseVPAdapter;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivitySongInfoBinding;
import com.puzzle.industries.chordsmusicapp.fragments.LyricsFragment;
import com.puzzle.industries.chordsmusicapp.fragments.SongInfoFragment;
import com.puzzle.industries.chordsmusicapp.helpers.ArtHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.models.viewModels.SongInfoVM;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongInfoActivity extends BaseActivity {

    private SongInfoStruct mSongInfo;
    private TrackArtistAlbumEntity mSong;
    private ActivitySongInfoBinding mBinding;
    private BroadcastReceiver songInfoChangedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySongInfoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        init();
    }

    private void init() {
        if (fetchSongInfo()){
            initSongArt();
            initTabs();
            registerReceivers();
        }
    }

    private void registerReceivers() {
        if (songInfoChangedReceiver == null){
            songInfoChangedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final SongInfoStruct songInfo = intent.getParcelableExtra(Constants.KEY_SONG_INFO);
                    if (songInfo != null){
                        mSongInfo = songInfo;
                    }
                }
            };
        }

        registerReceiver(songInfoChangedReceiver, new IntentFilter(Constants.ACTION_SONG_INFO_STRUCT_CHANGED));
    }

    private boolean fetchSongInfo(){
        final Bundle extras = getIntent().getExtras();
        if (extras != null){
            this.mSongInfo = extras.getParcelable(Constants.KEY_SONG_INFO);
            this.mSong = extras.getParcelable(Constants.KEY_SONG);
        }
        return this.mSongInfo != null && this.mSong != null;
    }

    private void initSongArt() {
        ArtHelper.displayArtistArtFromUrl(
                mSongInfo.getSearchResult()
                        .getResponse()
                        .getHits()
                        .get(0)
                        .getResult()
                        .getSong_art_image_url(),
                mBinding.ivSongArt);
    }

    private void initTabs() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_SONG_INFO, mSongInfo);
        bundle.putParcelable(Constants.KEY_SONG, mSong);


        final SongInfoFragment songInfoFragment = new SongInfoFragment();
        final LyricsFragment lyricsFragment = new LyricsFragment();
        songInfoFragment.setArguments(bundle);
        lyricsFragment.setArguments(bundle);


        final List<String> fragmentTitles = new ArrayList<>(Arrays.asList(
                getString(R.string.info),
                getString(R.string.lyrics)
        ));

        final List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                songInfoFragment,
                lyricsFragment
        ));

        mBinding.vp.setAdapter(new BaseVPAdapter(this, fragmentList));
        new TabLayoutMediator(mBinding.tl, mBinding.vp, (tab, position) -> tab.setText(fragmentTitles.get(position)))
                .attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.songInfoChangedReceiver);
    }
}
