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

import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentDownloadsBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.DownloadsRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;

public class DownloadsFragment extends BaseFragment {

    private FragmentDownloadsBinding mBinding;
    private DownloadsRVAdapter mAdapter;
    private List<SongDataStruct> mSongsDownloadQueue;
    private IntentFilter mDownloadFilter;
    private BroadcastReceiver mDownloadReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDownloadsBinding.inflate(inflater, container, false);
        mDownloadFilter = new IntentFilter();
        mDownloadFilter.addAction(Constants.ACTION_IF);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSongsDownloadQueue = DownloadManagerService.getInstance().getDownloadsQueue();
        mAdapter = new DownloadsRVAdapter(mSongsDownloadQueue);
        mBinding.rv.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mDownloadReceiver == null){
            mDownloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    final SongDataStruct song = intent.getParcelableExtra(Constants.KEY_SONG);
                    final int currentProgress = intent.getIntExtra(Constants.KEY_DOWNLOAD_PROGRESS, 0);

                    mAdapter.updateProgress(song, currentProgress);
                }
            };
        }

        requireActivity().registerReceiver(mDownloadReceiver, mDownloadFilter);

        mAdapter.updateDownloadQueue(DownloadManagerService.getInstance().getDownloadsQueue());
    }

    public void onPause(){
        super.onPause();
        requireActivity().unregisterReceiver(mDownloadReceiver);
    }

}
