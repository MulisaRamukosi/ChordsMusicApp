package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaFragment;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryTabBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.adapters.ArtistRVAdapter;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class ArtistFragment extends BaseMediaFragment<ArtistEntity> {

    private ArtistRVAdapter mAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ArtistRVAdapter(MUSIC_LIBRARY.getArtists());
        mAdapter.setItemLongClickCallback(this);
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.CENTER);
        mBinding.getRoot().setAdapter(mAdapter);
        mBinding.getRoot().setLayoutManager(layoutManager);

        mMediaViewModel.getObservableArtist().observe(getViewLifecycleOwner(), artist -> mAdapter.itemChanged(artist));
    }

    @Override
    public void initReceiverIntentFilters() {
        setItemAddedIntentFilterAction(Constants.ACTION_ARTIST_ADDED_TO_LIST);
        setItemRemovedIntentFilterAction(Constants.ACTION_ARTIST_DELETED);
        setItemUpdatedIntentFilterAction(Constants.ACTION_MUSIC_PROGRESS_UPDATE);
    }

    @Override
    public void onReceivedItemAddedBroadCast(Intent intent) {
        final ArtistEntity artistEntity = intent.getParcelableExtra(Constants.KEY_ARTIST);
        mAdapter.itemAdded(artistEntity);
    }

    @Override
    public void onReceivedItemRemovedBroadCast(Intent intent) {
        final ArtistEntity artistEntity = intent.getParcelableExtra(Constants.KEY_ARTIST);
        mAdapter.itemRemoved(artistEntity);
    }

}
