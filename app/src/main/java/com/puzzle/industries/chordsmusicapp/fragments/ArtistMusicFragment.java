package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.puzzle.industries.chordsmusicapp.base.BaseMediaFragment;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentArtistMusicBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class ArtistMusicFragment extends BaseMediaFragment<TrackArtistAlbumEntity> {

    private FragmentArtistMusicBinding mBinding;
    private MusicRVAdapter mAdapter;
    private MediaVM mMediaViewModel;

    private int mArtistId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentArtistMusicBinding.inflate(inflater, container, false);
        mMediaViewModel = new ViewModelProvider(requireActivity()).get(MediaVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        mArtistId = getArtistId();
        if (mArtistId != Constants.DEFAULT_ARTIST_ID){
            initAdapter();
            initObservables();
        }
    }

    private int getArtistId(){
        final Bundle bundle = getArguments();
        if (bundle != null){
            return bundle.getInt(Constants.KEY_ARTIST_ID, Constants.DEFAULT_ARTIST_ID);
        }
        return Constants.DEFAULT_ARTIST_ID;
    }

    private void initObservables() {
        mMediaViewModel.getObservableSong().observe(getViewLifecycleOwner(), song ->
                mAdapter.itemChanged(song));
    }

    private void initAdapter() {
        mAdapter = new MusicRVAdapter(MusicLibraryService.getInstance().getArtistSongs(mArtistId));
        mAdapter.setItemLongClickCallback(this);
        mBinding.rvMusic.setAdapter(mAdapter);
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
        if (song.getArtist_id() == mArtistId) {
            mAdapter.itemAdded(song);
        }
    }

    @Override
    public void onReceivedItemRemovedBroadCast(Intent intent) {
        final TrackArtistAlbumEntity song = intent.getParcelableExtra(Constants.KEY_SONG);
        if (song.getArtist_id() == mArtistId) {
            mAdapter.itemRemoved(song);
        }
    }

}
