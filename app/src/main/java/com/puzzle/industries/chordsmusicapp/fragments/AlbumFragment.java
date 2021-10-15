package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentAlbumBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.AlbumRVAdapter;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

public class AlbumFragment extends BaseFragment {

    private FragmentAlbumBinding mBinding;
    private AlbumRVAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentAlbumBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new AlbumRVAdapter(MusicLibraryService.getInstance().getAlbums());
        mBinding.rvAlbum.setAdapter(mAdapter);
    }
}
