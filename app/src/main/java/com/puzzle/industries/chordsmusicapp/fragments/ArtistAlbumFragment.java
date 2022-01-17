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
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentArtistAlbumBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.AlbumRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class ArtistAlbumFragment extends BaseMediaFragment<AlbumArtistEntity> {

    private FragmentArtistAlbumBinding mBinding;
    private MediaVM mMediaViewModel;
    private AlbumRVAdapter mAdapter;

    private int mArtistId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentArtistAlbumBinding.inflate(inflater, container, false);
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

    private void initAdapter(){
        mAdapter = new AlbumRVAdapter(MUSIC_LIBRARY.getArtistAlbums(mArtistId));
        mAdapter.setItemLongClickCallback(this);
        mBinding.rvAlbum.setAdapter(mAdapter);
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
        if (album.getArtist_id() == mArtistId) {
            mAdapter.itemAdded(album);
        }
    }

    @Override
    public void onReceivedItemRemovedBroadCast(Intent intent) {
        final AlbumArtistEntity album = intent.getParcelableExtra(Constants.KEY_ALBUM);
        if (album.getArtist_id() == mArtistId) {
            mAdapter.itemRemoved(album);
        }
    }
}
