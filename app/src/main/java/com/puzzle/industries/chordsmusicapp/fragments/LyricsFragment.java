package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLyricsBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.LyricsRvAdapter;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.models.viewModels.LyricsVM;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.HitModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.LyricModel;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class LyricsFragment extends BaseFragment {

    private FragmentLyricsBinding mBinding;
    private SongInfoStruct mSongInfo;
    private TrackArtistAlbumEntity mSong;
    private LyricsVM mViewModel;
    private BroadcastReceiver songInfoChangedReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLyricsBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(LyricsVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        if (initExtras()) {
            initLyricsObserver();
            fetchSongLyrics();
            initLyricsFetchErrorImpl();
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
                        if (getArguments() != null){
                            getArguments().putParcelable(Constants.KEY_SONG_INFO, songInfo);
                            mSongInfo = songInfo;
                            initLyricsObserver();
                        }
                    }
                }
            };
        }

        requireContext().registerReceiver(songInfoChangedReceiver, new IntentFilter(Constants.ACTION_SONG_INFO_STRUCT_CHANGED));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireContext().unregisterReceiver(songInfoChangedReceiver);
    }

    private void initLyricsFetchErrorImpl() {
        mBinding.btnTryAgain.setOnClickListener(v -> fetchSongLyrics());
    }

    private boolean initExtras(){
        final Bundle args = getArguments();
        if (args != null) {
            mSongInfo = getArguments().getParcelable(Constants.KEY_SONG_INFO);
            mSong = getArguments().getParcelable(Constants.KEY_SONG);
        }

        return mSongInfo != null && mSong != null;
    }

    private void fetchSongLyrics(){
        if (mSongInfo != null){
            setAsLoading(true);
            final HitModel hitModel = mSongInfo.getSearchResult().getResponse().getHits().get(0).getResult();
            mViewModel.downloadLyrics(mSong, hitModel.getLyricsUrl());
        }
    }

    private void initLyricsObserver(){
        mViewModel.getSongLyricsObservable().observe(getViewLifecycleOwner(), lyricModel -> {
            setAsLoading(false);
            if (lyricModel != null) initAdapter(lyricModel);
            else mBinding.llError.setVisibility(View.VISIBLE);
        });
    }

    private void initAdapter(LyricModel lyricModel) {
        mBinding.rv.setAdapter(new LyricsRvAdapter(lyricModel.getLyricVerses()));
    }

    private void setAsLoading(boolean isLoading){
        requireActivity().runOnUiThread(() -> {
            mBinding.ll.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            mBinding.rv.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            mBinding.llError.setVisibility(View.GONE);
        });
    }


}
