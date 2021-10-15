package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentSearchBinding;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

}
