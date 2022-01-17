package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.AlbumViewActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaFragment;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryTabBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.adapters.AlbumRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class AlbumFragment extends BaseMediaFragment<AlbumArtistEntity> {

    private AlbumRVAdapter mAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        initAdapters();
        initObservables();
    }

    private void initAdapters(){
        mAdapter = new AlbumRVAdapter(mAlbums == null ? MUSIC_LIBRARY.getAlbums() : mAlbums);
        mBinding.getRoot().setAdapter(mAdapter);
        mAdapter.setItemLongClickCallback(this);
    }

    private void initObservables(){
        mMediaViewModel.getObservableAlbum().observe(getViewLifecycleOwner(), album -> mAdapter.itemChanged(album));
    }

    @Override
    public void initReceiverIntentFilters() {
        setItemAddedIntentFilterAction(Constants.ACTION_ALBUM_ADDED_TO_LIST);
        setItemRemovedIntentFilterAction(Constants.ACTION_ALBUM_DELETED);
        setItemUpdatedIntentFilterAction(Constants.ACTION_MUSIC_PROGRESS_UPDATE);
    }

    @Override
    public void onReceivedItemAddedBroadCast(Intent intent) {
        final AlbumArtistEntity album = intent.getParcelableExtra(Constants.KEY_ALBUM);
        mAdapter.itemAdded(album);
    }

    @Override
    public void onReceivedItemRemovedBroadCast(Intent intent) {
        final AlbumArtistEntity album = intent.getParcelableExtra(Constants.KEY_ALBUM);
        mAdapter.itemRemoved(album);
    }

}
