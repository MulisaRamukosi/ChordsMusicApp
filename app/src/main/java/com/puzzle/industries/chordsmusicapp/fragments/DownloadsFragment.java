package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentDownloadsBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.DownloadsRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.dataModels.DownloadItemDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

import java.util.Map;

public class DownloadsFragment extends BaseFragment {

    private FragmentDownloadsBinding mBinding;
    private DownloadsRVAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDownloadsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new DownloadsRVAdapter(DownloadManagerService.getInstance().getDownloadsQueue());
        mBinding.rv.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Map<Integer, MutableLiveData<DownloadItemDataStruct>> downloadQueue = DownloadManagerService.getInstance().getDownloadsQueue();
        if (downloadQueue.size() != mAdapter.getItemCount()){
            mAdapter.updateDownloadItems(downloadQueue);
        }

    }
}
