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
    private BroadcastReceiver mDownloadProgressReceiver;
    private BroadcastReceiver mDownloadItemStateChangeReceiver;

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
        initReceivers();
    }

    private void initReceivers(){
        mDownloadProgressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final SongDataStruct song = intent.getParcelableExtra(Constants.KEY_SONG);
                final int currentProgress = intent.getIntExtra(Constants.KEY_DOWNLOAD_PROGRESS, 0);
                mAdapter.updateProgress(song, currentProgress);
            }
        };

        mDownloadItemStateChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final SongDataStruct song = intent.getParcelableExtra(Constants.KEY_SONG);
                final DownloadState downloadState = (DownloadState) intent.getSerializableExtra(Constants.KEY_DOWNLOAD_STATE);
                mAdapter.updateState(song, downloadState);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        final Map<Integer, DownloadItemDataStruct> downloadQueue = DownloadManagerService.getInstance().getDownloadsQueue();
        if (downloadQueue.size() != mAdapter.getItemCount()){
            mAdapter.updateDownloadItems(downloadQueue);
        }
        requireActivity().registerReceiver(mDownloadProgressReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_PROGRESS));
        requireActivity().registerReceiver(mDownloadItemStateChangeReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_STATE));
    }

    public void onPause(){
        super.onPause();
        requireActivity().unregisterReceiver(mDownloadProgressReceiver);
        requireActivity().unregisterReceiver(mDownloadItemStateChangeReceiver);
    }

}
