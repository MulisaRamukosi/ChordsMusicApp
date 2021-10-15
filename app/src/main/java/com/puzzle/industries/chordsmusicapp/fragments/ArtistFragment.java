package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentArtistBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.ArtistRVAdapter;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

public class ArtistFragment extends BaseFragment {

    private FragmentArtistBinding mBinding;
    private ArtistRVAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentArtistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ArtistRVAdapter(MusicLibraryService.getInstance().getArtists());
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.CENTER);
        mBinding.rvArtist.setAdapter(mAdapter);
        mBinding.rvArtist.setLayoutManager(layoutManager);
    }
}
