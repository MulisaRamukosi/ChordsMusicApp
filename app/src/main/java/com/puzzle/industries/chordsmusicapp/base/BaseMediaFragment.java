package com.puzzle.industries.chordsmusicapp.base;

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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.puzzle.industries.chordsmusicapp.callbacks.MediaItemLongClickCallback;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryTabBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.services.IMediaOptionsService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaOptionsService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;

import lombok.Setter;

public abstract class BaseMediaFragment<T> extends BaseFragment implements MediaItemLongClickCallback<T> {

    private BroadcastReceiver itemAddedReceiver;
    private BroadcastReceiver itemRemovedReceiver;
    private BroadcastReceiver itemUpdatedReceiver;

    @Setter private String itemAddedIntentFilterAction;
    @Setter private String itemRemovedIntentFilterAction;
    @Setter private String itemUpdatedIntentFilterAction;

    protected MediaVM mMediaViewModel;
    protected FragmentLibraryTabBinding mBinding;
    protected IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    private IMediaOptionsService<T> mMediaOptionsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaViewModel = new ViewModelProvider(requireActivity()).get(MediaVM.class);
        mMediaOptionsService = new MediaOptionsService<>(getChildFragmentManager());
        initReceivers();
        initReceiverIntentFilters();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLibraryTabBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    @Override
    public void mediaItemLongClicked(T t, List<Integer> songIds){
        mMediaOptionsService.showMediaOptionBottomSheet(t, songIds);
    }

    private void initReceivers(){
        itemAddedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceivedItemAddedBroadCast(intent);
            }
        };

        itemRemovedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceivedItemRemovedBroadCast(intent);
            }
        };

        itemUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceivedItemChangedBroadCast(intent);
            }
        };
    }

    private void registerReceivers(){
        final FragmentActivity activity = getActivity();
        if (activity != null){
            activity.registerReceiver(itemAddedReceiver, new IntentFilter(itemAddedIntentFilterAction));
            activity.registerReceiver(itemRemovedReceiver, new IntentFilter(itemRemovedIntentFilterAction));
            activity.registerReceiver(itemUpdatedReceiver, new IntentFilter(itemUpdatedIntentFilterAction));
        }
    }

    private void unregisterReceivers(){
        final FragmentActivity activity = getActivity();
        if (activity != null){
            activity.unregisterReceiver(itemAddedReceiver);
            activity.unregisterReceiver(itemRemovedReceiver);
            activity.unregisterReceiver(itemUpdatedReceiver);
        }

    }

    public abstract void initReceiverIntentFilters();
    public abstract void onReceivedItemAddedBroadCast(Intent intent);
    public abstract void onReceivedItemRemovedBroadCast(Intent intent);


    private void onReceivedItemChangedBroadCast(Intent intent) {
        final SongInfoProgressEvent songInfo = intent.getParcelableExtra(Constants.KEY_MUSIC_PROGRESS);
        if (songInfo == null) return;
        mMediaViewModel.updateAlbum(songInfo.getCurrentAlbum());
        mMediaViewModel.updateSong(songInfo.getCurrentSong());
        mMediaViewModel.updateArtist(songInfo.getCurrentArtist());
    }
}
