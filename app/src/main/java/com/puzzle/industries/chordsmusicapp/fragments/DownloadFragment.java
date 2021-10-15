package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentDownloadBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.BaseVPAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadFragment extends BaseFragment {

    private FragmentDownloadBinding mBinding;
    private BaseVPAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDownloadBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final List<String> fragmentTitles = new ArrayList<>(Arrays.asList(
                getString(R.string.downloads),
                getString(R.string.Music),
                getString(R.string.albums)
        ));

        final List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                new DownloadsFragment(),
                new MusicDownloadFragment(),
                new AlbumDownloadFragment()
        ));

        mAdapter = new BaseVPAdapter(requireActivity(), fragmentList);
        mBinding.vp.setAdapter(mAdapter);

        new TabLayoutMediator(mBinding.tl, mBinding.vp, (tab, position) -> tab.setText(fragmentTitles.get(position)))
                .attach();

    }
}
