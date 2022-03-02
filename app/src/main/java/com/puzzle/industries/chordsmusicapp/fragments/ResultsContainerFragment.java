package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaFragment;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaRVAdapter;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentSearchResultsContainerBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.AlbumRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.adapters.ArtistRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.models.viewModels.SearchVM;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.MediaType;

import java.util.ArrayList;

public class ResultsContainerFragment<T> extends BaseMediaFragment<T> {

    private FragmentSearchResultsContainerBinding mBinding;
    private BaseMediaRVAdapter<? extends ViewBinding, T> mAdapter;
    private MediaVM mMediaViewModel;
    private SearchVM mSearchViewModel;
    private MediaType mType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        initMediaType();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSearchResultsContainerBinding.inflate(inflater, container, false);
        mMediaViewModel = new ViewModelProvider(requireActivity()).get(MediaVM.class);
        mSearchViewModel = new ViewModelProvider(requireActivity()).get(SearchVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initAdapter();
        initObservables();
    }

    private void initMediaType() {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            mType = (MediaType) bundle.getSerializable(Constants.KEY_MEDIA_TYPE);
        }
    }

    private void initAdapter() {
        switch (mType) {
            case SONG:
                mAdapter = (BaseMediaRVAdapter<? extends ViewBinding, T>) new MusicRVAdapter(new ArrayList<>());
                break;

            case ALBUM:
                mAdapter = (BaseMediaRVAdapter<? extends ViewBinding, T>) new AlbumRVAdapter(new ArrayList<>());
                break;

            case ARTIST:
                mAdapter = (BaseMediaRVAdapter<? extends ViewBinding, T>) new ArtistRVAdapter(new ArrayList<>());
                final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
                layoutManager.setFlexDirection(FlexDirection.ROW);
                layoutManager.setAlignItems(AlignItems.CENTER);
                mBinding.rv.setLayoutManager(layoutManager);
                break;
        }

        mAdapter.setItemLongClickCallback(this);
        mBinding.rv.setAdapter(mAdapter);
    }

    private void initObservables() {
        mSearchViewModel.getObservableSearchWord().observe(getViewLifecycleOwner(), word -> mAdapter.showSearchResults(word));
        switch (mType) {
            case SONG:
                mMediaViewModel.getObservableSong().observe(getViewLifecycleOwner(), song -> mAdapter.itemChanged((T) song));
                break;
            case ARTIST:
                mMediaViewModel.getObservableArtist().observe(getViewLifecycleOwner(), artist -> mAdapter.itemChanged((T) artist));
                break;
            case ALBUM:
                mMediaViewModel.getObservableAlbum().observe(getViewLifecycleOwner(), album -> mAdapter.itemChanged((T) album));
                break;
        }
    }

    @Override
    public void initReceiverIntentFilters() {
        switch (mType) {
            case SONG:
                setItemAddedIntentFilterAction(Constants.ACTION_MUSIC_ADDED_TO_LIST);
                setItemRemovedIntentFilterAction(Constants.ACTION_MUSIC_DELETED);
                break;

            case ALBUM:
                setItemAddedIntentFilterAction(Constants.ACTION_ALBUM_ADDED_TO_LIST);
                setItemRemovedIntentFilterAction(Constants.ACTION_ALBUM_DELETED);
                break;

            case ARTIST:
                setItemAddedIntentFilterAction(Constants.ACTION_ARTIST_ADDED_TO_LIST);
                setItemRemovedIntentFilterAction(Constants.ACTION_ARTIST_DELETED);
                break;
        }

        setItemUpdatedIntentFilterAction(Constants.ACTION_MUSIC_PROGRESS_UPDATE);
    }

    @Override
    public void onReceivedItemAddedBroadCast(Intent intent) {
        final T item = intent.getParcelableExtra(getKey());
        mAdapter.itemAdded(item);
    }

    @Override
    public void onReceivedItemRemovedBroadCast(Intent intent) {
        final T item = intent.getParcelableExtra(getKey());
        mAdapter.itemRemoved(item);
    }

    private String getKey() {
        switch (mType) {
            case SONG:
                return Constants.KEY_SONG;
            case ALBUM:
                return Constants.KEY_ALBUM;
            case ARTIST:
                return Constants.KEY_ARTIST;
        }
        return "";
    }
}
