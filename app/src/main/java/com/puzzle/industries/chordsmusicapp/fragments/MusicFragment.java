package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaFragment;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryBinding;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryTabBinding;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentMusicBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class MusicFragment extends BaseMediaFragment<TrackArtistAlbumEntity> {

    private MusicRVAdapter mAdapter;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        initAdapter();
        initObservables();
    }

    private void initObservables() {
        mMediaViewModel.getObservableSong().observe(getViewLifecycleOwner(), song -> mAdapter.itemChanged(song));
    }

    private void initAdapter() {
        mAdapter = new MusicRVAdapter(MusicLibraryService.getInstance().getSongs());
        mBinding.getRoot().setAdapter(mAdapter);
        mAdapter.setItemLongClickCallback(this);
    }

    private List<TrackArtistAlbumEntity> getPlaylist(){
        return mDisplayCurrentPlaylist
                ? MUSIC_LIBRARY.getCurrentPlaylistSongs()
                : mTracks == null
                ? MUSIC_LIBRARY.getSongs()
                : mTracks;
    }

    @Override
    public void initReceiverIntentFilters() {
        setItemAddedIntentFilterAction(Constants.ACTION_MUSIC_ADDED_TO_LIST);
        setItemRemovedIntentFilterAction(Constants.ACTION_MUSIC_DELETED);
        setItemUpdatedIntentFilterAction(Constants.ACTION_MUSIC_PROGRESS_UPDATE);
    }

    @Override
    public void onReceivedItemAddedBroadCast(Intent intent) {
        final TrackArtistAlbumEntity song = intent.getParcelableExtra(Constants.KEY_SONG);
        mAdapter.itemAdded(song);
    }

    @Override
    public void onReceivedItemRemovedBroadCast(Intent intent) {
        final TrackArtistAlbumEntity song = intent.getParcelableExtra(Constants.KEY_SONG);
        mAdapter.itemRemoved(song);
    }

}
